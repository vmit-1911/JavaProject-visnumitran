package moviebooking.dao;

import moviebooking.model.Booking;
import moviebooking.model.Seat;
import moviebooking.model.Show;
import moviebooking.model.User;
import moviebooking.util.DBConnection;
import moviebooking.util.MockDatabaseStore;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Bookings.
 * Features ACID Database Transactions with duplicate seat locking.
 */
public class BookingDAO {

    /**
     * Executes atomic booking inside a database transaction.
     * Checks if any of the requested seats are already booked before confirmation.
     */
    public Booking createBookingTransaction(User user, Show show, List<Seat> selectedSeats,
                                            String paymentMethod, String txnId) throws Exception {
        if (DBConnection.isFallbackActive()) {
            return MockDatabaseStore.getInstance().createBookingTransaction(user, show, selectedSeats, paymentMethod, txnId);
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return MockDatabaseStore.getInstance().createBookingTransaction(user, show, selectedSeats, paymentMethod, txnId);
        }

        boolean originalAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false); // Begin ACID Transaction

            // 1. Double-booking check: Check if any requested seat is already booked for this show
            String checkSeatSql = "SELECT bs.seat_id, s.row_char, s.seat_number " +
                                  "FROM booking_seats bs " +
                                  "JOIN seats s ON bs.seat_id = s.seat_id " +
                                  "WHERE bs.show_id = ? AND bs.seat_id = ? AND bs.status = 'BOOKED' FOR UPDATE";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSeatSql)) {
                for (Seat seat : selectedSeats) {
                    checkStmt.setInt(1, show.getShowId());
                    checkStmt.setInt(2, seat.getSeatId());
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next()) {
                            conn.rollback();
                            throw new IllegalStateException("Seat " + seat.getSeatLabel() + " is already booked! Please select different seats.");
                        }
                    }
                }
            }

            // 2. Calculate Pricing
            double baseAmount = selectedSeats.stream().mapToDouble(Seat::getBasePrice).sum();
            double convenienceFee = selectedSeats.size() * 30.00;
            double gst = (baseAmount + convenienceFee) * 0.18;
            double grandTotal = baseAmount + convenienceFee + gst;
            String bookingRef = "BMS-" + (2026) + "-" + (10000 + (int)(Math.random() * 90000));

            // 3. Insert into bookings
            int bookingId;
            String insertBookingSql = "INSERT INTO bookings (booking_reference, user_id, show_id, total_seats, base_amount, convenience_fee, gst_amount, total_amount, booking_status) " +
                                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'CONFIRMED')";
            try (PreparedStatement pstmt = conn.prepareStatement(insertBookingSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, bookingRef);
                pstmt.setInt(2, user.getUserId());
                pstmt.setInt(3, show.getShowId());
                pstmt.setInt(4, selectedSeats.size());
                pstmt.setDouble(5, baseAmount);
                pstmt.setDouble(6, convenienceFee);
                pstmt.setDouble(7, gst);
                pstmt.setDouble(8, grandTotal);
                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        bookingId = rs.getInt(1);
                    } else {
                        throw new SQLException("Failed to obtain generated booking ID.");
                    }
                }
            }

            // 4. Insert into booking_seats
            String insertSeatsSql = "INSERT INTO booking_seats (booking_id, show_id, seat_id, seat_label, category, price, status) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, 'BOOKED')";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSeatsSql)) {
                for (Seat seat : selectedSeats) {
                    pstmt.setInt(1, bookingId);
                    pstmt.setInt(2, show.getShowId());
                    pstmt.setInt(3, seat.getSeatId());
                    pstmt.setString(4, seat.getSeatLabel());
                    pstmt.setString(5, seat.getCategory().name());
                    pstmt.setDouble(6, seat.getBasePrice());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            // 5. Update available seats on the show
            String updateShowSql = "UPDATE shows SET available_seats = GREATEST(0, available_seats - ?) WHERE show_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateShowSql)) {
                pstmt.setInt(1, selectedSeats.size());
                pstmt.setInt(2, show.getShowId());
                pstmt.executeUpdate();
            }

            // 6. Insert into payments
            String insertPaymentSql = "INSERT INTO payments (booking_id, transaction_id, payment_method, amount, payment_status) VALUES (?, ?, ?, ?, 'SUCCESS')";
            try (PreparedStatement pstmt = conn.prepareStatement(insertPaymentSql)) {
                pstmt.setInt(1, bookingId);
                pstmt.setString(2, txnId);
                pstmt.setString(3, paymentMethod);
                pstmt.setDouble(4, grandTotal);
                pstmt.executeUpdate();
            }

            conn.commit(); // Commit transaction

            // Assemble booking object
            Booking b = new Booking();
            b.setBookingId(bookingId);
            b.setBookingReference(bookingRef);
            b.setUserId(user.getUserId());
            b.setShowId(show.getShowId());
            b.setTotalSeats(selectedSeats.size());
            b.setBaseAmount(baseAmount);
            b.setConvenienceFee(convenienceFee);
            b.setGstAmount(gst);
            b.setTotalAmount(grandTotal);
            b.setBookingStatus(Booking.Status.CONFIRMED);
            b.setBookingDate(new Timestamp(System.currentTimeMillis()));

            b.setCustomerName(user.getName());
            b.setCustomerEmail(user.getEmail());
            b.setCustomerPhone(user.getPhone());
            b.setMovieTitle(show.getMovieTitle());
            b.setTheatreName(show.getTheatreName());
            b.setScreenName(show.getScreenName());
            b.setShowDate(show.getFormattedDate());
            b.setShowTime(show.getFormattedTime());
            List<String> labels = new ArrayList<>();
            for (Seat s : selectedSeats) labels.add(s.getSeatLabel());
            b.setSeatLabels(labels);
            b.setTransactionId(txnId);
            b.setPaymentMethod(paymentMethod);

            return b;
        } catch (Exception ex) {
            conn.rollback(); // Rollback transaction on failure
            System.err.println("[BookingDAO] Booking transaction rolled back: " + ex.getMessage());
            throw ex;
        } finally {
            conn.setAutoCommit(originalAutoCommit);
        }
    }

    public boolean cancelBooking(int bookingId) {
        if (DBConnection.isFallbackActive()) {
            return MockDatabaseStore.getInstance().cancelBooking(bookingId);
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return MockDatabaseStore.getInstance().cancelBooking(bookingId);
        }

        try {
            conn.setAutoCommit(false);

            // Fetch show_id and total_seats
            int showId = 0;
            int seatsCount = 0;
            String getBookingSql = "SELECT show_id, total_seats, booking_status FROM bookings WHERE booking_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(getBookingSql)) {
                pstmt.setInt(1, bookingId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        if ("CANCELLED".equalsIgnoreCase(rs.getString("booking_status"))) {
                            conn.rollback();
                            return false;
                        }
                        showId = rs.getInt("show_id");
                        seatsCount = rs.getInt("total_seats");
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            // 1. Update booking status
            String updateBookingSql = "UPDATE bookings SET booking_status = 'CANCELLED' WHERE booking_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateBookingSql)) {
                pstmt.setInt(1, bookingId);
                pstmt.executeUpdate();
            }

            // 2. Mark booking_seats as CANCELLED so seats are released
            String updateSeatsSql = "UPDATE booking_seats SET status = 'CANCELLED' WHERE booking_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSeatsSql)) {
                pstmt.setInt(1, bookingId);
                pstmt.executeUpdate();
            }

            // 3. Return available seat count to show
            String updateShowSql = "UPDATE shows SET available_seats = LEAST(64, available_seats + ?) WHERE show_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateShowSql)) {
                pstmt.setInt(1, seatsCount);
                pstmt.setInt(2, showId);
                pstmt.executeUpdate();
            }

            // 4. Update payment status to REFUNDED
            String updatePaySql = "UPDATE payments SET payment_status = 'REFUNDED' WHERE booking_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updatePaySql)) {
                pstmt.setInt(1, bookingId);
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (Exception ignored) {}
            System.err.println("[BookingDAO] cancelBooking error: " + e.getMessage());
            return MockDatabaseStore.getInstance().cancelBooking(bookingId);
        }
    }

    public List<Booking> getBookingsByUser(int userId) {
        if (DBConnection.isFallbackActive()) {
            return MockDatabaseStore.getInstance().getBookingsForUser(userId);
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return MockDatabaseStore.getInstance().getBookingsForUser(userId);
        }

        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, u.name as customer_name, u.email as customer_email, u.phone as customer_phone, " +
                     "m.title as movie_title, m.poster_path, t.name as theatre_name, sc.screen_name, " +
                     "s.show_date, s.start_time, p.transaction_id, p.payment_method " +
                     "FROM bookings b " +
                     "JOIN users u ON b.user_id = u.user_id " +
                     "JOIN shows s ON b.show_id = s.show_id " +
                     "JOIN movies m ON s.movie_id = m.movie_id " +
                     "JOIN screens sc ON s.screen_id = sc.screen_id " +
                     "JOIN theatres t ON sc.theatre_id = t.theatre_id " +
                     "LEFT JOIN payments p ON b.booking_id = p.booking_id " +
                     "WHERE b.user_id = ? " +
                     "ORDER BY b.booking_id DESC";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Booking b = mapBooking(rs);
                    b.setSeatLabels(getSeatLabelsForBooking(conn, b.getBookingId()));
                    list.add(b);
                }
            }
        } catch (SQLException e) {
            System.err.println("[BookingDAO] getBookingsByUser error: " + e.getMessage());
            return MockDatabaseStore.getInstance().getBookingsForUser(userId);
        }
        return list;
    }

    public List<Booking> getAllBookings() {
        if (DBConnection.isFallbackActive()) {
            return MockDatabaseStore.getInstance().getAllBookings();
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return MockDatabaseStore.getInstance().getAllBookings();
        }

        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, u.name as customer_name, u.email as customer_email, u.phone as customer_phone, " +
                     "m.title as movie_title, m.poster_path, t.name as theatre_name, sc.screen_name, " +
                     "s.show_date, s.start_time, p.transaction_id, p.payment_method " +
                     "FROM bookings b " +
                     "JOIN users u ON b.user_id = u.user_id " +
                     "JOIN shows s ON b.show_id = s.show_id " +
                     "JOIN movies m ON s.movie_id = m.movie_id " +
                     "JOIN screens sc ON s.screen_id = sc.screen_id " +
                     "JOIN theatres t ON sc.theatre_id = t.theatre_id " +
                     "LEFT JOIN payments p ON b.booking_id = p.booking_id " +
                     "ORDER BY b.booking_id DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Booking b = mapBooking(rs);
                b.setSeatLabels(getSeatLabelsForBooking(conn, b.getBookingId()));
                list.add(b);
            }
        } catch (SQLException e) {
            System.err.println("[BookingDAO] getAllBookings error: " + e.getMessage());
            return MockDatabaseStore.getInstance().getAllBookings();
        }
        return list;
    }

    private List<String> getSeatLabelsForBooking(Connection conn, int bookingId) {
        List<String> seats = new ArrayList<>();
        String sql = "SELECT seat_label FROM booking_seats WHERE booking_id = ? ORDER BY seat_label ASC";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    seats.add(rs.getString("seat_label"));
                }
            }
        } catch (SQLException ignored) {}
        return seats;
    }

    private Booking mapBooking(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setBookingId(rs.getInt("booking_id"));
        b.setBookingReference(rs.getString("booking_reference"));
        b.setUserId(rs.getInt("user_id"));
        b.setShowId(rs.getInt("show_id"));
        b.setTotalSeats(rs.getInt("total_seats"));
        b.setBaseAmount(rs.getDouble("base_amount"));
        b.setConvenienceFee(rs.getDouble("convenience_fee"));
        b.setGstAmount(rs.getDouble("gst_amount"));
        b.setTotalAmount(rs.getDouble("total_amount"));
        b.setBookingStatus(Booking.Status.valueOf(rs.getString("booking_status")));
        b.setBookingDate(rs.getTimestamp("booking_date"));

        b.setCustomerName(rs.getString("customer_name"));
        b.setCustomerEmail(rs.getString("customer_email"));
        b.setCustomerPhone(rs.getString("customer_phone"));
        b.setMovieTitle(rs.getString("movie_title"));
        b.setMoviePoster(rs.getString("poster_path"));
        b.setTheatreName(rs.getString("theatre_name"));
        b.setScreenName(rs.getString("screen_name"));
        Date d = rs.getDate("show_date");
        b.setShowDate(d != null ? d.toString() : "");
        Time t = rs.getTime("start_time");
        b.setShowTime(t != null ? t.toString() : "");
        b.setTransactionId(rs.getString("transaction_id"));
        b.setPaymentMethod(rs.getString("payment_method"));
        return b;
    }
}

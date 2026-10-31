package moviebooking.dao;

import moviebooking.model.Screen;
import moviebooking.model.Seat;
import moviebooking.model.Theatre;
import moviebooking.util.DBConnection;
import moviebooking.util.MockDatabaseStore;

import java.sql.*;
import java.util.*;

/**
 * Data Access Object for Theatres, Screens, and Seats.
 */
public class TheatreDAO {

    public List<Theatre> getAllTheatres() {
        if (DBConnection.isFallbackActive()) {
            return MockDatabaseStore.getInstance().getAllTheatres();
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return MockDatabaseStore.getInstance().getAllTheatres();
        }

        List<Theatre> list = new ArrayList<>();
        String sql = "SELECT * FROM theatres ORDER BY theatre_id ASC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapTheatre(rs));
            }
        } catch (SQLException e) {
            System.err.println("[TheatreDAO] getAllTheatres error: " + e.getMessage());
            return MockDatabaseStore.getInstance().getAllTheatres();
        }
        return list;
    }

    public Theatre getTheatreById(int theatreId) {
        if (DBConnection.isFallbackActive()) {
            return MockDatabaseStore.getInstance().getTheatreById(theatreId);
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return MockDatabaseStore.getInstance().getTheatreById(theatreId);
        }

        String sql = "SELECT * FROM theatres WHERE theatre_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, theatreId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapTheatre(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[TheatreDAO] getTheatreById error: " + e.getMessage());
            return MockDatabaseStore.getInstance().getTheatreById(theatreId);
        }
        return null;
    }

    public boolean addTheatre(Theatre theatre) {
        if (DBConnection.isFallbackActive()) {
            return MockDatabaseStore.getInstance().addTheatre(theatre) != null;
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return MockDatabaseStore.getInstance().addTheatre(theatre) != null;
        }

        String sql = "INSERT INTO theatres (name, city, address, total_screens) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, theatre.getName());
            pstmt.setString(2, theatre.getCity());
            pstmt.setString(3, theatre.getAddress());
            pstmt.setInt(4, theatre.getTotalScreens());

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) theatre.setTheatreId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[TheatreDAO] addTheatre error: " + e.getMessage());
        }
        return false;
    }

    public boolean updateTheatre(Theatre theatre) {
        if (DBConnection.isFallbackActive()) {
            return MockDatabaseStore.getInstance().updateTheatre(theatre);
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return MockDatabaseStore.getInstance().updateTheatre(theatre);
        }

        String sql = "UPDATE theatres SET name=?, city=?, address=?, total_screens=? WHERE theatre_id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, theatre.getName());
            pstmt.setString(2, theatre.getCity());
            pstmt.setString(3, theatre.getAddress());
            pstmt.setInt(4, theatre.getTotalScreens());
            pstmt.setInt(5, theatre.getTheatreId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[TheatreDAO] updateTheatre error: " + e.getMessage());
            return MockDatabaseStore.getInstance().updateTheatre(theatre);
        }
    }

    public boolean deleteTheatre(int theatreId) {
        if (DBConnection.isFallbackActive()) {
            return MockDatabaseStore.getInstance().deleteTheatre(theatreId);
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return MockDatabaseStore.getInstance().deleteTheatre(theatreId);
        }

        String sql = "DELETE FROM theatres WHERE theatre_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, theatreId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[TheatreDAO] deleteTheatre error: " + e.getMessage());
            return MockDatabaseStore.getInstance().deleteTheatre(theatreId);
        }
    }

    public List<Screen> getScreensByTheatre(int theatreId) {
        if (DBConnection.isFallbackActive()) {
            return MockDatabaseStore.getInstance().getScreensByTheatre(theatreId);
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return MockDatabaseStore.getInstance().getScreensByTheatre(theatreId);
        }

        List<Screen> list = new ArrayList<>();
        String sql = "SELECT s.*, t.name as theatre_name FROM screens s JOIN theatres t ON s.theatre_id = t.theatre_id WHERE s.theatre_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, theatreId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Screen sc = new Screen();
                    sc.setScreenId(rs.getInt("screen_id"));
                    sc.setTheatreId(rs.getInt("theatre_id"));
                    sc.setScreenNumber(rs.getInt("screen_number"));
                    sc.setScreenName(rs.getString("screen_name"));
                    sc.setTotalSeats(rs.getInt("total_seats"));
                    sc.setTheatreName(rs.getString("theatre_name"));
                    list.add(sc);
                }
            }
        } catch (SQLException e) {
            System.err.println("[TheatreDAO] getScreensByTheatre error: " + e.getMessage());
            return MockDatabaseStore.getInstance().getScreensByTheatre(theatreId);
        }
        return list;
    }

    public List<Seat> getSeatsForScreen(int screenId) {
        if (DBConnection.isFallbackActive()) {
            return MockDatabaseStore.getInstance().getSeatsForScreen(screenId);
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return MockDatabaseStore.getInstance().getSeatsForScreen(screenId);
        }

        List<Seat> list = new ArrayList<>();
        String sql = "SELECT * FROM seats WHERE screen_id = ? ORDER BY row_char ASC, seat_number ASC";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, screenId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Seat seat = new Seat();
                    seat.setSeatId(rs.getInt("seat_id"));
                    seat.setScreenId(rs.getInt("screen_id"));
                    seat.setRowChar(rs.getString("row_char"));
                    seat.setSeatNumber(rs.getInt("seat_number"));
                    seat.setCategory(Seat.Category.valueOf(rs.getString("category")));
                    seat.setBasePrice(rs.getDouble("base_price"));
                    list.add(seat);
                }
            }
        } catch (SQLException e) {
            System.err.println("[TheatreDAO] getSeatsForScreen error: " + e.getMessage());
            return MockDatabaseStore.getInstance().getSeatsForScreen(screenId);
        }

        if (list.isEmpty()) {
            return MockDatabaseStore.getInstance().getSeatsForScreen(screenId);
        }
        return list;
    }

    public Set<Integer> getBookedSeatIdsForShow(int showId) {
        if (DBConnection.isFallbackActive()) {
            return MockDatabaseStore.getInstance().getBookedSeatIdsForShow(showId);
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return MockDatabaseStore.getInstance().getBookedSeatIdsForShow(showId);
        }

        Set<Integer> booked = new HashSet<>();
        String sql = "SELECT seat_id FROM booking_seats WHERE show_id = ? AND status = 'BOOKED'";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, showId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    booked.add(rs.getInt("seat_id"));
                }
            }
        } catch (SQLException e) {
            System.err.println("[TheatreDAO] getBookedSeatIdsForShow error: " + e.getMessage());
            return MockDatabaseStore.getInstance().getBookedSeatIdsForShow(showId);
        }
        return booked;
    }

    private Theatre mapTheatre(ResultSet rs) throws SQLException {
        Theatre t = new Theatre();
        t.setTheatreId(rs.getInt("theatre_id"));
        t.setName(rs.getString("name"));
        t.setCity(rs.getString("city"));
        t.setAddress(rs.getString("address"));
        t.setTotalScreens(rs.getInt("total_screens"));
        t.setCreatedAt(rs.getTimestamp("created_at"));
        return t;
    }
}

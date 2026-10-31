package moviebooking.service;

import moviebooking.dao.BookingDAO;
import moviebooking.model.Booking;
import moviebooking.model.Seat;
import moviebooking.model.Show;
import moviebooking.model.User;

import java.util.List;

/**
 * Service orchestrating customer bookings and cancellations.
 */
public class BookingService {

    private static final BookingService INSTANCE = new BookingService();
    private final BookingDAO bookingDAO = new BookingDAO();

    public static BookingService getInstance() {
        return INSTANCE;
    }

    private BookingService() {}

    public Booking processBooking(User user, Show show, List<Seat> selectedSeats, String paymentMethod, String txnId) throws Exception {
        if (user == null) {
            throw new IllegalArgumentException("User session expired. Please log in.");
        }
        if (show == null) {
            throw new IllegalArgumentException("Invalid show selected.");
        }
        if (selectedSeats == null || selectedSeats.isEmpty()) {
            throw new IllegalArgumentException("Please select at least one seat.");
        }

        return bookingDAO.createBookingTransaction(user, show, selectedSeats, paymentMethod, txnId);
    }

    public boolean cancelBooking(int bookingId) {
        return bookingDAO.cancelBooking(bookingId);
    }

    public List<Booking> getUserBookings(int userId) {
        return bookingDAO.getBookingsByUser(userId);
    }

    public List<Booking> getAllBookings() {
        return bookingDAO.getAllBookings();
    }
}

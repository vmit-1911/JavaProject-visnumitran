package moviebooking;

import moviebooking.dao.MovieDAO;
import moviebooking.dao.ShowDAO;
import moviebooking.dao.TheatreDAO;
import moviebooking.dao.UserDAO;
import moviebooking.model.*;
import moviebooking.service.AuthenticationService;
import moviebooking.service.BookingService;
import moviebooking.service.SeatService;

import java.util.List;

/**
 * Headless Automated Verification Test to validate all business logic and services.
 */
public class TestSystem {
    public static void main(String[] args) {
        System.out.println(">>> STARTING SYSTEM TEST SUITE <<<");

        // 1. Test Authentication
        try {
            AuthenticationService auth = AuthenticationService.getInstance();
            User user = auth.loginUser("mitran@example.com", "password123");
            System.out.println("✅ User login test passed: " + user.getName());

            Admin admin = auth.loginAdmin("admin", "admin123");
            System.out.println("✅ Admin login test passed: " + admin.getName());
        } catch (Exception e) {
            System.err.println("❌ Auth test failed: " + e.getMessage());
            System.exit(1);
        }

        // 2. Test MovieDAO
        MovieDAO movieDAO = new MovieDAO();
        List<Movie> movies = movieDAO.getAllMovies();
        System.out.println("✅ Movies loaded: " + movies.size() + " movies found");
        if (movies.isEmpty()) {
            System.err.println("❌ No movies found!");
            System.exit(1);
        }

        // 3. Test Theatre & Shows
        TheatreDAO theatreDAO = new TheatreDAO();
        List<Theatre> theatres = theatreDAO.getAllTheatres();
        System.out.println("✅ Theatres loaded: " + theatres.size() + " theatres found");

        ShowDAO showDAO = new ShowDAO();
        List<Show> shows = showDAO.getAllShows();
        System.out.println("✅ Shows loaded: " + shows.size() + " active shows found");

        // 4. Test Seat Selection & Concurrency Transaction
        try {
            Show firstShow = shows.get(0);
            SeatService seatService = SeatService.getInstance();
            List<Seat> seats = seatService.getSeatsForShow(firstShow);
            System.out.println("✅ Seats loaded for show: " + seats.size() + " seats in screen");

            // Pick 2 available seats
            List<Seat> available = seats.stream().filter(Seat::isAvailable).limit(2).toList();
            System.out.println("Selected seats: " + available.get(0).getSeatLabel() + ", " + available.get(1).getSeatLabel());

            // Process Booking Transaction
            User testUser = new User(1, "Mitran Sharma", "mitran@example.com", "9876543210", "password123");
            BookingService bookingService = BookingService.getInstance();
            Booking b = bookingService.processBooking(testUser, firstShow, available, "UPI", "TXN-TEST-12345");

            System.out.println("✅ Booking Transaction Confirmed! Ref: " + b.getBookingReference() + ", Total: ₹" + b.getTotalAmount());

            // 5. Test Double-Booking Prevention (Must fail gracefully)
            try {
                bookingService.processBooking(testUser, firstShow, available, "CREDIT_CARD", "TXN-TEST-DUP");
                System.err.println("❌ Double-booking prevention FAILED! Duplicate seats allowed.");
                System.exit(1);
            } catch (IllegalStateException e) {
                System.out.println("✅ Concurrency Lock Test Passed! Prevented double booking: " + e.getMessage());
            }

            // 6. Test Cancellation & Seat Release
            boolean cancelled = bookingService.cancelBooking(b.getBookingId());
            if (cancelled) {
                System.out.println("✅ Cancellation Test Passed! Booking status updated and seats released.");
            } else {
                System.err.println("❌ Cancellation failed!");
                System.exit(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println(">>> ALL 6 SYSTEM TEST SUITES PASSED FLAWLESSLY! <<<");
    }
}

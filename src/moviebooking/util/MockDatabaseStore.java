package moviebooking.util;

import moviebooking.model.*;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * In-Memory Database Store with full ACID-like synchronization.
 * Serves as a fallback if the local MySQL server is stopped or unconfigured.
 */
public class MockDatabaseStore {

    private static final MockDatabaseStore INSTANCE = new MockDatabaseStore();

    public static MockDatabaseStore getInstance() {
        return INSTANCE;
    }

    private final Map<Integer, User> users = new ConcurrentHashMap<>();
    private final Map<Integer, Admin> admins = new ConcurrentHashMap<>();
    private final Map<Integer, Movie> movies = new ConcurrentHashMap<>();
    private final Map<Integer, Theatre> theatres = new ConcurrentHashMap<>();
    private final Map<Integer, Screen> screens = new ConcurrentHashMap<>();
    private final Map<Integer, Seat> seats = new ConcurrentHashMap<>();
    private final Map<Integer, Show> shows = new ConcurrentHashMap<>();
    private final Map<Integer, Booking> bookings = new ConcurrentHashMap<>();
    private final Map<Integer, Payment> payments = new ConcurrentHashMap<>();

    // Show ID -> Set of booked seat IDs
    private final Map<Integer, Set<Integer>> showBookedSeats = new ConcurrentHashMap<>();

    private final AtomicInteger userIdGen = new AtomicInteger(10);
    private final AtomicInteger movieIdGen = new AtomicInteger(10);
    private final AtomicInteger theatreIdGen = new AtomicInteger(10);
    private final AtomicInteger showIdGen = new AtomicInteger(20);
    private final AtomicInteger bookingIdGen = new AtomicInteger(10);
    private final AtomicInteger paymentIdGen = new AtomicInteger(10);

    private MockDatabaseStore() {
        seedInitialData();
    }

    private void seedInitialData() {
        // Admins
        admins.put(1, new Admin(1, "admin", "System Administrator", "admin@cinema.com", "admin123", "SUPER_ADMIN"));
        admins.put(2, new Admin(2, "manager", "Theatre Manager", "manager@cinema.com", "manager123", "THEATRE_MANAGER"));

        // Users
        users.put(1, new User(1, "Mitran Sharma", "mitran@example.com", "9876543210", "password123"));
        users.put(2, new User(2, "Rahul Verma", "rahul@example.com", "9812345678", "rahul123"));
        users.put(3, new User(3, "Priya Patel", "priya@example.com", "9123456780", "priya123"));

        // Movies
        long now = System.currentTimeMillis();
        movies.put(1, new Movie(1, "Interstellar", "Sci-Fi / Adventure / Drama", "English", 169,
                new Date(now), "U/A",
                "When Earth becomes uninhabitable, a pilot travels through a wormhole in search of a new home for mankind.",
                9.2, "poster_interstellar.png", "banner_interstellar.png", "NOW_SHOWING"));

        movies.put(2, new Movie(2, "Dune: Part Two", "Sci-Fi / Action / Epic", "English", 166,
                new Date(now), "U/A",
                "Paul Atreides unites with Chani and the Fremen while seeking revenge against those who destroyed his family.",
                8.8, "poster_dune.png", "banner_dune.png", "NOW_SHOWING"));

        movies.put(3, new Movie(3, "Oppenheimer", "Biography / Drama / History", "English", 180,
                new Date(now), "A",
                "The story of American scientist J. Robert Oppenheimer and his role in the creation of the atomic bomb.",
                8.9, "poster_oppenheimer.png", "banner_oppenheimer.png", "NOW_SHOWING"));

        movies.put(4, new Movie(4, "The Dark Knight", "Action / Crime / Drama", "English", 152,
                new Date(now), "U/A",
                "Batman accepts one of his greatest psychological and physical tests battling the criminal mastermind Joker.",
                9.0, "poster_darkknight.png", "banner_darkknight.png", "NOW_SHOWING"));

        movies.put(5, new Movie(5, "Inception", "Action / Sci-Fi / Thriller", "English", 148,
                new Date(now), "U/A",
                "A thief who steals secrets through dream-sharing is tasked to plant an idea into a CEO's mind.",
                8.8, "poster_inception.png", "banner_inception.png", "NOW_SHOWING"));

        movies.put(6, new Movie(6, "Avatar: The Way of Water", "Action / Sci-Fi / Adventure", "English", 192,
                new Date(now + 86400000L * 30), "U/A",
                "Jake Sully and Neytiri protect their family as an ancient threat returns to Pandora.",
                8.4, "poster_avatar.png", "banner_avatar.png", "UPCOMING"));

        movies.put(7, new Movie(7, "Spider-Man: Beyond the Spider-Verse", "Animation / Action / Adventure", "English", 140,
                new Date(now + 86400000L * 60), "U",
                "Miles Morales journeys across the Multiverse to unite spider-heroes against an omnipresent foe.",
                9.1, "poster_spiderman.png", "banner_spiderman.png", "UPCOMING"));

        movies.put(8, new Movie(8, "Gladiator II", "Action / Adventure / Drama", "English", 150,
                new Date(now + 86400000L * 90), "A",
                "Years after the fall of Maximus, Lucius enters the Colosseum to liberate Rome from tyrannical emperors.",
                8.6, "poster_gladiator.png", "banner_gladiator.png", "UPCOMING"));

        // Theatres
        theatres.put(1, new Theatre(1, "PVR: ICON Gold Class", "Mumbai", "Phoenix Palladium, Lower Parel", 3));
        theatres.put(2, new Theatre(2, "INOX: Megaplex IMAX", "Mumbai", "Inorbit Mall, Malad West", 3));
        theatres.put(3, new Theatre(3, "Cinepolis: VIP Luxe", "Delhi NCR", "DLF Avenue, Saket", 3));
        theatres.put(4, new Theatre(4, "PVR: Superplex", "Bangalore", "Forum Mall, Koramangala", 3));
        theatres.put(5, new Theatre(5, "SPI Cinemas: Sathyam", "Chennai", "Royapettah Main Road", 3));

        // Screens
        screens.put(1, new Screen(1, 1, 1, "Audi 1 - IMAX Laser", 64));
        screens.put(2, new Screen(2, 1, 2, "Audi 2 - Dolby Atmos 4K", 64));
        screens.put(3, new Screen(3, 1, 3, "Audi 3 - Gold VIP Luxe", 64));
        screens.put(4, new Screen(4, 2, 1, "Screen 1 - Onyx LED", 64));

        // Populate 64 Seats for screen 1: Rows A-H, 1-8
        int seatCounter = 1;
        String[] rows = {"A", "B", "C", "D", "E", "F", "G", "H"};
        for (int screenId = 1; screenId <= 4; screenId++) {
            for (String r : rows) {
                Seat.Category cat;
                double price;
                if (r.equals("A") || r.equals("B")) {
                    cat = Seat.Category.PREMIUM;
                    price = 350.0;
                } else if (r.equals("C") || r.equals("D") || r.equals("E")) {
                    cat = Seat.Category.GOLD;
                    price = 250.0;
                } else {
                    cat = Seat.Category.SILVER;
                    price = 150.0;
                }
                for (int num = 1; num <= 8; num++) {
                    int sid = seatCounter++;
                    Seat seat = new Seat(sid, screenId, r, num, cat, price);
                    seats.put(sid, seat);
                }
            }
        }

        // Shows
        Date today = new Date(now);
        Date tomorrow = new Date(now + 86400000L);

        shows.put(1, createShow(1, 1, 1, today, "10:30:00", 350.0, 62));
        shows.put(2, createShow(2, 1, 1, today, "14:15:00", 350.0, 64));
        shows.put(3, createShow(3, 1, 1, today, "18:45:00", 380.0, 61));
        shows.put(4, createShow(4, 1, 1, today, "22:30:00", 350.0, 64));
        shows.put(5, createShow(5, 2, 2, today, "11:00:00", 320.0, 64));
        shows.put(6, createShow(6, 2, 2, today, "15:30:00", 320.0, 64));
        shows.put(7, createShow(7, 3, 3, today, "12:00:00", 300.0, 64));
        shows.put(8, createShow(8, 4, 1, tomorrow, "14:00:00", 300.0, 64));
        shows.put(9, createShow(9, 5, 2, tomorrow, "18:00:00", 300.0, 64));

        // Pre-book seats for Show 1 (Row A Seats 4, 5)
        Set<Integer> show1Booked = new HashSet<>(Arrays.asList(4, 5));
        showBookedSeats.put(1, show1Booked);

        // Pre-book seats for Show 3 (Row C Seats 3, 4, 5 -> IDs 19, 20, 21)
        Set<Integer> show3Booked = new HashSet<>(Arrays.asList(19, 20, 21));
        showBookedSeats.put(3, show3Booked);

        // Pre-populate sample booking
        Booking sampleBooking = new Booking();
        sampleBooking.setBookingId(1);
        sampleBooking.setBookingReference("BMS-2026-94812");
        sampleBooking.setUserId(1);
        sampleBooking.setShowId(1);
        sampleBooking.setTotalSeats(2);
        sampleBooking.setBaseAmount(700.0);
        sampleBooking.setConvenienceFee(60.0);
        sampleBooking.setGstAmount(136.80);
        sampleBooking.setTotalAmount(896.80);
        sampleBooking.setBookingStatus(Booking.Status.CONFIRMED);
        sampleBooking.setBookingDate(new Timestamp(now - 3600000L));
        sampleBooking.setCustomerName("Mitran Sharma");
        sampleBooking.setCustomerEmail("mitran@example.com");
        sampleBooking.setCustomerPhone("9876543210");
        sampleBooking.setMovieTitle("Interstellar");
        sampleBooking.setTheatreName("PVR: ICON Gold Class");
        sampleBooking.setScreenName("Audi 1 - IMAX Laser");
        sampleBooking.setShowDate("Today");
        sampleBooking.setShowTime("10:30 AM");
        sampleBooking.setSeatLabels(Arrays.asList("A4", "A5"));
        sampleBooking.setTransactionId("TXN-UPI-98421045");
        sampleBooking.setPaymentMethod("UPI");
        bookings.put(1, sampleBooking);

        payments.put(1, new Payment(1, 1, "TXN-UPI-98421045", Payment.Method.UPI, 896.80, Payment.Status.SUCCESS));
    }

    private Show createShow(int id, int movieId, int screenId, Date date, String timeStr, double price, int available) {
        Show s = new Show(id, movieId, screenId, date, Time.valueOf(timeStr), price, available);
        Movie m = movies.get(movieId);
        Screen sc = screens.get(screenId);
        if (m != null) s.setMovieTitle(m.getTitle());
        if (sc != null) {
            s.setScreenName(sc.getScreenName());
            Theatre th = theatres.get(sc.getTheatreId());
            if (th != null) {
                s.setTheatreName(th.getName());
                s.setCity(th.getCity());
            }
        }
        return s;
    }

    // --- User Operations ---
    public User findUserByEmail(String email) {
        return users.values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email.trim()))
                .findFirst().orElse(null);
    }

    public User findUserById(int id) {
        return users.get(id);
    }

    public synchronized User registerUser(String name, String email, String phone, String password) {
        if (findUserByEmail(email) != null) return null;
        int id = userIdGen.incrementAndGet();
        User u = new User(id, name, email, phone, password);
        u.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        users.put(id, u);
        return u;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    // --- Admin Operations ---
    public Admin findAdminByUsername(String username) {
        return admins.values().stream()
                .filter(a -> a.getUsername().equalsIgnoreCase(username.trim()))
                .findFirst().orElse(null);
    }

    public List<Admin> getAllAdmins() {
        return new ArrayList<>(admins.values());
    }

    // --- Movie Operations ---
    public List<Movie> getAllMovies() {
        return new ArrayList<>(movies.values());
    }

    public List<Movie> getMoviesByStatus(String status) {
        return movies.values().stream()
                .filter(m -> m.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    public Movie getMovieById(int id) {
        return movies.get(id);
    }

    public synchronized Movie addMovie(Movie movie) {
        int id = movieIdGen.incrementAndGet();
        movie.setMovieId(id);
        movie.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        movies.put(id, movie);
        return movie;
    }

    public synchronized boolean updateMovie(Movie movie) {
        if (!movies.containsKey(movie.getMovieId())) return false;
        movies.put(movie.getMovieId(), movie);
        return true;
    }

    public synchronized boolean deleteMovie(int id) {
        return movies.remove(id) != null;
    }

    // --- Theatre & Screen Operations ---
    public List<Theatre> getAllTheatres() {
        return new ArrayList<>(theatres.values());
    }

    public Theatre getTheatreById(int id) {
        return theatres.get(id);
    }

    public synchronized Theatre addTheatre(Theatre theatre) {
        int id = theatreIdGen.incrementAndGet();
        theatre.setTheatreId(id);
        theatres.put(id, theatre);
        return theatre;
    }

    public synchronized boolean updateTheatre(Theatre theatre) {
        if (!theatres.containsKey(theatre.getTheatreId())) return false;
        theatres.put(theatre.getTheatreId(), theatre);
        return true;
    }

    public synchronized boolean deleteTheatre(int id) {
        return theatres.remove(id) != null;
    }

    public List<Screen> getScreensByTheatre(int theatreId) {
        return screens.values().stream()
                .filter(sc -> sc.getTheatreId() == theatreId)
                .collect(Collectors.toList());
    }

    public Screen getScreenById(int id) {
        return screens.get(id);
    }

    // --- Seats Operations ---
    public List<Seat> getSeatsForScreen(int screenId) {
        return seats.values().stream()
                .filter(s -> s.getScreenId() == screenId)
                .sorted(Comparator.comparing(Seat::getRowChar).thenComparingInt(Seat::getSeatNumber))
                .collect(Collectors.toList());
    }

    public Set<Integer> getBookedSeatIdsForShow(int showId) {
        return showBookedSeats.getOrDefault(showId, Collections.emptySet());
    }

    // --- Shows Operations ---
    public List<Show> getAllShows() {
        return new ArrayList<>(shows.values());
    }

    public List<Show> getShowsForMovie(int movieId) {
        return shows.values().stream()
                .filter(s -> s.getMovieId() == movieId)
                .collect(Collectors.toList());
    }

    public Show getShowById(int id) {
        return shows.get(id);
    }

    public synchronized Show addShow(Show show) {
        int id = showIdGen.incrementAndGet();
        show.setShowId(id);
        Movie m = movies.get(show.getMovieId());
        Screen sc = screens.get(show.getScreenId());
        if (m != null) show.setMovieTitle(m.getTitle());
        if (sc != null) {
            show.setScreenName(sc.getScreenName());
            Theatre th = theatres.get(sc.getTheatreId());
            if (th != null) {
                show.setTheatreName(th.getName());
                show.setCity(th.getCity());
            }
        }
        shows.put(id, show);
        return show;
    }

    public synchronized boolean updateShow(Show show) {
        if (!shows.containsKey(show.getShowId())) return false;
        shows.put(show.getShowId(), show);
        return true;
    }

    public synchronized boolean deleteShow(int id) {
        return shows.remove(id) != null;
    }

    // --- Booking & Transaction Lock ---
    public synchronized Booking createBookingTransaction(User user, Show show, List<Seat> selectedSeats,
                                                          String paymentMethod, String txnId) throws Exception {
        Set<Integer> booked = showBookedSeats.computeIfAbsent(show.getShowId(), k -> new HashSet<>());

        // Concurrency double-booking verification
        for (Seat s : selectedSeats) {
            if (booked.contains(s.getSeatId())) {
                throw new IllegalStateException("Seat " + s.getSeatLabel() + " was just booked by another user! Please select different seats.");
            }
        }

        // Lock seats
        for (Seat s : selectedSeats) {
            booked.add(s.getSeatId());
        }

        // Update show available seats
        show.setAvailableSeats(Math.max(0, show.getAvailableSeats() - selectedSeats.size()));

        double baseAmount = selectedSeats.stream().mapToDouble(Seat::getBasePrice).sum();
        double convenienceFee = selectedSeats.size() * 30.0;
        double gst = (baseAmount + convenienceFee) * 0.18;
        double total = baseAmount + convenienceFee + gst;

        int bId = bookingIdGen.incrementAndGet();
        String ref = "BMS-" + (2026) + "-" + (10000 + (int)(Math.random() * 90000));

        Booking b = new Booking();
        b.setBookingId(bId);
        b.setBookingReference(ref);
        b.setUserId(user.getUserId());
        b.setShowId(show.getShowId());
        b.setTotalSeats(selectedSeats.size());
        b.setBaseAmount(baseAmount);
        b.setConvenienceFee(convenienceFee);
        b.setGstAmount(gst);
        b.setTotalAmount(total);
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
        b.setSeatLabels(selectedSeats.stream().map(Seat::getSeatLabel).collect(Collectors.toList()));
        b.setTransactionId(txnId);
        b.setPaymentMethod(paymentMethod);

        bookings.put(bId, b);

        // Record payment
        int pId = paymentIdGen.incrementAndGet();
        Payment p = new Payment(pId, bId, txnId, Payment.Method.valueOf(paymentMethod), total, Payment.Status.SUCCESS);
        p.setPaymentDate(new Timestamp(System.currentTimeMillis()));
        payments.put(pId, p);

        return b;
    }

    public synchronized boolean cancelBooking(int bookingId) {
        Booking b = bookings.get(bookingId);
        if (b == null || b.getBookingStatus() == Booking.Status.CANCELLED) return false;

        b.setBookingStatus(Booking.Status.CANCELLED);

        // Release seats
        Set<Integer> booked = showBookedSeats.get(b.getShowId());
        if (booked != null) {
            Show show = shows.get(b.getShowId());
            if (show != null) {
                show.setAvailableSeats(Math.min(64, show.getAvailableSeats() + b.getTotalSeats()));
                List<Seat> screenSeats = getSeatsForScreen(show.getScreenId());
                for (Seat seat : screenSeats) {
                    if (b.getSeatLabels().contains(seat.getSeatLabel())) {
                        booked.remove(seat.getSeatId());
                    }
                }
            }
        }
        return true;
    }

    public List<Booking> getBookingsForUser(int userId) {
        return bookings.values().stream()
                .filter(b -> b.getUserId() == userId)
                .sorted((b1, b2) -> b2.getBookingDate().compareTo(b1.getBookingDate()))
                .collect(Collectors.toList());
    }

    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings.values());
    }

    public List<Payment> getAllPayments() {
        return new ArrayList<>(payments.values());
    }
}

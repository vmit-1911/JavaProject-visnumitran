package moviebooking.ui;

import moviebooking.model.Admin;
import moviebooking.model.Booking;
import moviebooking.model.Movie;
import moviebooking.model.Seat;
import moviebooking.model.Show;
import moviebooking.model.User;
import moviebooking.service.AuthenticationService;
import moviebooking.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Main Application Frame.
 * Maximized window hosting the BookMyShow navbar and CardLayout view switcher.
 */
public class HomeFrame extends JFrame {

    public static final String CARD_MOVIES = "MOVIES";
    public static final String CARD_DETAILS = "DETAILS";
    public static final String CARD_THEATRES = "THEATRES";
    public static final String CARD_SEATS = "SEATS";
    public static final String CARD_SUMMARY = "SUMMARY";
    public static final String CARD_PAYMENT = "PAYMENT";
    public static final String CARD_TICKET = "TICKET";
    public static final String CARD_MY_BOOKINGS = "MY_BOOKINGS";
    public static final String CARD_ADMIN = "ADMIN";

    private final CardLayout cardLayout;
    private final JPanel contentCards;

    private final MoviePanel moviePanel;
    private final MovieDetailsPanel movieDetailsPanel;
    private final TheatreSelectionPanel theatreSelectionPanel;
    private final SeatSelectionPanel seatSelectionPanel;
    private final BookingSummaryPanel bookingSummaryPanel;
    private final PaymentPanel paymentPanel;
    private final TicketPanel ticketPanel;
    private final MyBookingsPanel myBookingsPanel;
    private final AdminDashboard adminDashboard;

    private JLabel lblUserProfile;
    private JButton btnAdminNav;

    public HomeFrame() {
        setTitle("BookMyShow Cinema - Movie Ticket Booking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1024, 700));
        getContentPane().setBackground(UITheme.BG_DARK);

        // Top Navigation Bar
        JPanel navBar = createNavBar();
        getContentPane().add(navBar, BorderLayout.NORTH);

        // CardLayout Panel
        cardLayout = new CardLayout();
        contentCards = new JPanel(cardLayout);
        contentCards.setBackground(UITheme.BG_DARK);

        // Instantiate Views
        moviePanel = new MoviePanel(this);
        movieDetailsPanel = new MovieDetailsPanel(this);
        theatreSelectionPanel = new TheatreSelectionPanel(this);
        seatSelectionPanel = new SeatSelectionPanel(this);
        bookingSummaryPanel = new BookingSummaryPanel(this);
        paymentPanel = new PaymentPanel(this);
        ticketPanel = new TicketPanel(this);
        myBookingsPanel = new MyBookingsPanel(this);
        adminDashboard = new AdminDashboard(this);

        contentCards.add(moviePanel, CARD_MOVIES);
        contentCards.add(movieDetailsPanel, CARD_DETAILS);
        contentCards.add(theatreSelectionPanel, CARD_THEATRES);
        contentCards.add(seatSelectionPanel, CARD_SEATS);
        contentCards.add(bookingSummaryPanel, CARD_SUMMARY);
        contentCards.add(paymentPanel, CARD_PAYMENT);
        contentCards.add(ticketPanel, CARD_TICKET);
        contentCards.add(myBookingsPanel, CARD_MY_BOOKINGS);
        contentCards.add(adminDashboard, CARD_ADMIN);

        getContentPane().add(contentCards, BorderLayout.CENTER);

        updateUserStatus();
    }

    private JPanel createNavBar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(UITheme.BG_NAV);
        nav.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
                new EmptyBorder(12, 28, 12, 28)
        ));

        // Left Branding
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        brandPanel.setOpaque(false);

        JLabel lblLogo = new JLabel("🎬 BookMyShow");
        lblLogo.setFont(UITheme.FONT_LOGO);
        lblLogo.setForeground(UITheme.PRIMARY_RED);
        lblLogo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLogo.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showMovieBrowser();
            }
        });

        brandPanel.add(lblLogo);

        // Center Nav Tabs
        JPanel centerNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        centerNav.setOpaque(false);

        JButton btnMovies = createNavButton("🎥 Movies");
        btnMovies.addActionListener(e -> showMovieBrowser());

        JButton btnMyBookings = createNavButton("🎟️ My Bookings");
        btnMyBookings.addActionListener(e -> showMyBookings());

        btnAdminNav = createNavButton("🛠️ Admin Console");
        btnAdminNav.addActionListener(e -> showAdminDashboard());

        centerNav.add(btnMovies);
        centerNav.add(btnMyBookings);
        centerNav.add(btnAdminNav);

        // Right Profile & Logout
        JPanel rightNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        rightNav.setOpaque(false);

        lblUserProfile = new JLabel("👤 Guest");
        lblUserProfile.setFont(UITheme.FONT_BODY_BOLD);
        lblUserProfile.setForeground(UITheme.TEXT_PRIMARY);

        JButton btnLogout = UITheme.createSecondaryButton("🚪 Logout");
        btnLogout.addActionListener(e -> {
            AuthenticationService.getInstance().logout();
            dispose();
            new LoginFrame().setVisible(true);
        });

        rightNav.add(lblUserProfile);
        rightNav.add(btnLogout);

        nav.add(brandPanel, BorderLayout.WEST);
        nav.add(centerNav, BorderLayout.CENTER);
        nav.add(rightNav, BorderLayout.EAST);

        return nav;
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_SECTION);
        btn.setForeground(UITheme.TEXT_SECONDARY);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setForeground(Color.WHITE);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setForeground(UITheme.TEXT_SECONDARY);
            }
        });
        return btn;
    }

    public void updateUserStatus() {
        User user = AuthenticationService.getInstance().getCurrentUser();
        Admin admin = AuthenticationService.getInstance().getCurrentAdmin();

        if (admin != null) {
            lblUserProfile.setText("👑 Admin: " + admin.getName());
            btnAdminNav.setVisible(true);
        } else if (user != null) {
            lblUserProfile.setText("👤 " + user.getName());
            btnAdminNav.setVisible(true); // Allow easy inspection
        } else {
            lblUserProfile.setText("👤 Guest");
        }
    }

    // --- Navigation Flow Triggers ---
    public void showMovieBrowser() {
        moviePanel.reloadMovies();
        cardLayout.show(contentCards, CARD_MOVIES);
    }

    public void showMovieDetails(Movie movie) {
        movieDetailsPanel.setMovie(movie);
        cardLayout.show(contentCards, CARD_DETAILS);
    }

    public void showTheatreSelection(Movie movie) {
        theatreSelectionPanel.setMovie(movie);
        cardLayout.show(contentCards, CARD_THEATRES);
    }

    public void showSeatSelection(Show show, Movie movie) {
        seatSelectionPanel.setShowAndMovie(show, movie);
        cardLayout.show(contentCards, CARD_SEATS);
    }

    public void showBookingSummary(Show show, Movie movie, List<Seat> selectedSeats) {
        bookingSummaryPanel.setDetails(show, movie, selectedSeats);
        cardLayout.show(contentCards, CARD_SUMMARY);
    }

    public void showPayment(Show show, Movie movie, List<Seat> selectedSeats, double grandTotal) {
        paymentPanel.setPaymentDetails(show, movie, selectedSeats, grandTotal);
        cardLayout.show(contentCards, CARD_PAYMENT);
    }

    public void showTicket(Booking booking) {
        ticketPanel.setBooking(booking);
        cardLayout.show(contentCards, CARD_TICKET);
    }

    public void showMyBookings() {
        myBookingsPanel.reloadBookings();
        cardLayout.show(contentCards, CARD_MY_BOOKINGS);
    }

    public void showAdminDashboard() {
        adminDashboard.reloadAllData();
        cardLayout.show(contentCards, CARD_ADMIN);
    }

    public void showLogin() {
        dispose();
        new LoginFrame().setVisible(true);
    }
}

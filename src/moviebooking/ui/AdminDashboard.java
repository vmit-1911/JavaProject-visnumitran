package moviebooking.ui;

import moviebooking.dao.ShowDAO;
import moviebooking.dao.TheatreDAO;
import moviebooking.dao.UserDAO;
import moviebooking.model.Booking;
import moviebooking.model.Movie;
import moviebooking.model.Show;
import moviebooking.model.Theatre;
import moviebooking.service.BookingService;
import moviebooking.service.MovieService;
import moviebooking.util.UITheme;
import moviebooking.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

/**
 * Modern Administrator Dashboard.
 * Includes KPI metrics cards, JTable movie management, theatre/show management,
 * customer audit logs, and revenue reports.
 */
public class AdminDashboard extends JPanel {

    private final HomeFrame parentFrame;
    private final MovieService movieService = MovieService.getInstance();
    private final TheatreDAO theatreDAO = new TheatreDAO();
    private final ShowDAO showDAO = new ShowDAO();
    private final BookingService bookingService = BookingService.getInstance();
    private final UserDAO userDAO = new UserDAO();

    // Metric Labels
    private JLabel lblTotalMovies;
    private JLabel lblTotalTheatres;
    private JLabel lblTotalShows;
    private JLabel lblTotalBookings;
    private JLabel lblTotalRevenue;

    // Tables
    private JTable tblMovies;
    private DefaultTableModel moviesModel;
    private List<Movie> cachedMovies;

    private JTable tblTheatres;
    private DefaultTableModel theatresModel;

    private JTable tblShows;
    private DefaultTableModel showsModel;

    private JTable tblBookings;
    private DefaultTableModel bookingsModel;

    public AdminDashboard(HomeFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_DARK);

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel lblTitle = new JLabel("🛠️ BookMyShow - Cinema Administration Console");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);

        JButton btnRefresh = UITheme.createSecondaryButton("🔄 Refresh Dashboard");
        btnRefresh.addActionListener(e -> reloadAllData());

        header.add(lblTitle, BorderLayout.WEST);
        header.add(btnRefresh, BorderLayout.EAST);

        main.add(header);
        main.add(Box.createVerticalStrut(18));

        // Top Metrics Cards Row
        JPanel metricsPanel = createMetricsPanel();
        main.add(metricsPanel);
        main.add(Box.createVerticalStrut(20));

        // Management Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(UITheme.BG_NAV);
        tabbedPane.setForeground(UITheme.TEXT_PRIMARY);
        tabbedPane.setFont(UITheme.FONT_SECTION);

        tabbedPane.addTab("🎬 Movie Management", createMoviesTab());
        tabbedPane.addTab("🏛️ Theatres & Screens", createTheatresTab());
        tabbedPane.addTab("🕒 Shows & Timings", createShowsTab());
        tabbedPane.addTab("📑 All Bookings Audit", createBookingsTab());
        tabbedPane.addTab("📊 Sales & Revenue", createReportsTab());

        main.add(tabbedPane);

        add(main, BorderLayout.CENTER);
        reloadAllData();
    }

    private JPanel createMetricsPanel() {
        JPanel p = new JPanel(new GridLayout(1, 5, 16, 0));
        p.setOpaque(false);

        lblTotalMovies = new JLabel("0");
        lblTotalTheatres = new JLabel("0");
        lblTotalShows = new JLabel("0");
        lblTotalBookings = new JLabel("0");
        lblTotalRevenue = new JLabel("₹0");

        p.add(createMetricCard("TOTAL MOVIES", lblTotalMovies, new Color(59, 130, 246)));
        p.add(createMetricCard("THEATRES", lblTotalTheatres, new Color(139, 92, 246)));
        p.add(createMetricCard("ACTIVE SHOWS", lblTotalShows, new Color(16, 185, 129)));
        p.add(createMetricCard("TOTAL BOOKINGS", lblTotalBookings, new Color(245, 158, 11)));
        p.add(createMetricCard("TOTAL REVENUE", lblTotalRevenue, UITheme.PRIMARY_RED));

        return p;
    }

    private JPanel createMetricCard(String title, JLabel valLabel, Color accent) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(UITheme.BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

                // Accent Top Border
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, getWidth(), 4, 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 16, 12, 16));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblT.setForeground(UITheme.TEXT_MUTED);

        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valLabel.setForeground(Color.WHITE);

        card.add(lblT);
        card.add(Box.createVerticalStrut(4));
        card.add(valLabel);

        return card;
    }

    // --- Tab 1: Movies Management ---
    private JPanel createMoviesTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        String[] cols = {"ID", "Title", "Genre", "Language", "Duration", "Cert", "Rating", "Status"};
        moviesModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblMovies = new JTable(moviesModel);
        UITheme.styleTable(tblMovies);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnBar.setOpaque(false);

        JButton btnAdd = UITheme.createPrimaryButton("+ Add New Movie");
        btnAdd.addActionListener(e -> showAddMovieDialog());

        JButton btnEdit = UITheme.createSecondaryButton("✏️ Edit Selected");
        btnEdit.addActionListener(e -> showEditMovieDialog());

        JButton btnDelete = UITheme.createSecondaryButton("🗑️ Delete Movie");
        btnDelete.addActionListener(e -> deleteSelectedMovie());

        btnBar.add(btnAdd);
        btnBar.add(btnEdit);
        btnBar.add(btnDelete);

        panel.add(btnBar, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblMovies), BorderLayout.CENTER);
        return panel;
    }

    // --- Tab 2: Theatres Management ---
    private JPanel createTheatresTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        String[] cols = {"Theatre ID", "Name", "City", "Address", "Total Screens"};
        theatresModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblTheatres = new JTable(theatresModel);
        UITheme.styleTable(tblTheatres);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnBar.setOpaque(false);
        JButton btnAddTheatre = UITheme.createPrimaryButton("+ Add Theatre");
        btnAddTheatre.addActionListener(e -> showAddTheatreDialog());

        btnBar.add(btnAddTheatre);

        panel.add(btnBar, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblTheatres), BorderLayout.CENTER);
        return panel;
    }

    // --- Tab 3: Shows Management ---
    private JPanel createShowsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        String[] cols = {"Show ID", "Movie", "Theatre", "Screen", "Date", "Time", "Ticket Price", "Available Seats"};
        showsModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblShows = new JTable(showsModel);
        UITheme.styleTable(tblShows);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnBar.setOpaque(false);
        JButton btnAddShow = UITheme.createPrimaryButton("+ Schedule New Show");
        btnAddShow.addActionListener(e -> showAddShowDialog());

        JButton btnDeleteShow = UITheme.createSecondaryButton("Delete Show");
        btnDeleteShow.addActionListener(e -> deleteSelectedShow());

        btnBar.add(btnAddShow);
        btnBar.add(btnDeleteShow);

        panel.add(btnBar, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblShows), BorderLayout.CENTER);
        return panel;
    }

    // --- Tab 4: All Bookings Audit ---
    private JPanel createBookingsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        String[] cols = {"Ref", "Customer", "Phone", "Movie", "Theatre", "Seats", "Total Amount", "Status"};
        bookingsModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblBookings = new JTable(bookingsModel);
        UITheme.styleTable(tblBookings);

        panel.add(new JScrollPane(tblBookings), BorderLayout.CENTER);
        return panel;
    }

    // --- Tab 5: Sales & Revenue Reports ---
    private JPanel createReportsTab() {
        JPanel panel = UITheme.createCardPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lblRepTitle = new JLabel("📊 Financial & Sales Report");
        lblRepTitle.setFont(UITheme.FONT_TITLE);
        lblRepTitle.setForeground(Color.WHITE);

        JTextArea txtReport = new JTextArea();
        txtReport.setOpaque(false);
        txtReport.setForeground(new Color(226, 232, 240));
        txtReport.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtReport.setEditable(false);

        List<Booking> all = bookingService.getAllBookings();
        double totalRev = all.stream().filter(b -> b.getBookingStatus() == Booking.Status.CONFIRMED).mapToDouble(Booking::getTotalAmount).sum();
        long confirmedCount = all.stream().filter(b -> b.getBookingStatus() == Booking.Status.CONFIRMED).count();
        long cancelledCount = all.stream().filter(b -> b.getBookingStatus() == Booking.Status.CANCELLED).count();

        StringBuilder sb = new StringBuilder();
        sb.append("=================================================================\n");
        sb.append("                 BOOKMYSHOW REVENUE & SALES AUDIT                \n");
        sb.append("=================================================================\n");
        sb.append("Gross Revenue Collected  : ").append(ValidationUtil.formatCurrency(totalRev)).append("\n");
        sb.append("Confirmed Bookings Count : ").append(confirmedCount).append(" orders\n");
        sb.append("Cancelled Bookings Count : ").append(cancelledCount).append(" orders\n");
        sb.append("Convenience Fee Income   : ").append(ValidationUtil.formatCurrency(confirmedCount * 30.0)).append("\n");
        sb.append("-----------------------------------------------------------------\n");
        sb.append("Breakdown by Active Movies:\n");

        all.stream().filter(b -> b.getBookingStatus() == Booking.Status.CONFIRMED)
                .collect(java.util.stream.Collectors.groupingBy(Booking::getMovieTitle, java.util.stream.Collectors.summingDouble(Booking::getTotalAmount)))
                .forEach((title, rev) -> sb.append(String.format("  - %-32s : %s\n", title, ValidationUtil.formatCurrency(rev))));

        sb.append("=================================================================\n");
        txtReport.setText(sb.toString());

        panel.add(lblRepTitle);
        panel.add(Box.createVerticalStrut(14));
        panel.add(new JScrollPane(txtReport));

        return panel;
    }

    public void reloadAllData() {
        // Movies
        cachedMovies = movieService.getAllMovies();
        moviesModel.setRowCount(0);
        for (Movie m : cachedMovies) {
            moviesModel.addRow(new Object[]{
                    m.getMovieId(), m.getTitle(), m.getGenre(), m.getLanguage(),
                    m.getFormattedDuration(), m.getCertification(), m.getRating(), m.getStatus()
            });
        }
        lblTotalMovies.setText(String.valueOf(cachedMovies.size()));

        // Theatres
        List<Theatre> theatres = theatreDAO.getAllTheatres();
        theatresModel.setRowCount(0);
        for (Theatre t : theatres) {
            theatresModel.addRow(new Object[]{
                    t.getTheatreId(), t.getName(), t.getCity(), t.getAddress(), t.getTotalScreens()
            });
        }
        lblTotalTheatres.setText(String.valueOf(theatres.size()));

        // Shows
        List<Show> shows = showDAO.getAllShows();
        showsModel.setRowCount(0);
        for (Show s : shows) {
            showsModel.addRow(new Object[]{
                    s.getShowId(), s.getMovieTitle(), s.getTheatreName(), s.getScreenName(),
                    s.getFormattedDate(), s.getFormattedTime(), ValidationUtil.formatCurrency(s.getTicketPrice()), s.getAvailableSeats()
            });
        }
        lblTotalShows.setText(String.valueOf(shows.size()));

        // Bookings & Revenue
        List<Booking> bookings = bookingService.getAllBookings();
        bookingsModel.setRowCount(0);
        double rev = 0;
        for (Booking b : bookings) {
            if (b.getBookingStatus() == Booking.Status.CONFIRMED) {
                rev += b.getTotalAmount();
            }
            bookingsModel.addRow(new Object[]{
                    b.getBookingReference(), b.getCustomerName(), b.getCustomerPhone(),
                    b.getMovieTitle(), b.getTheatreName(), b.getSeatLabelsFormatted(),
                    ValidationUtil.formatCurrency(b.getTotalAmount()), b.getBookingStatus()
            });
        }
        lblTotalBookings.setText(String.valueOf(bookings.size()));
        lblTotalRevenue.setText(ValidationUtil.formatCurrency(rev));
    }

    // --- Dialogs for Add/Edit/Delete ---
    private void showAddMovieDialog() {
        JTextField txtTitle = new JTextField();
        JTextField txtGenre = new JTextField("Action / Sci-Fi");
        JTextField txtLang = new JTextField("English");
        JTextField txtDuration = new JTextField("150");
        JTextField txtRating = new JTextField("8.5");
        JComboBox<String> cbCert = new JComboBox<>(new String[]{"U", "U/A", "A"});
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"NOW_SHOWING", "UPCOMING"});
        JTextArea txtDesc = new JTextArea("Compelling blockbuster storyline...");

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Movie Title:")); form.add(txtTitle);
        form.add(new JLabel("Genre:")); form.add(txtGenre);
        form.add(new JLabel("Language:")); form.add(txtLang);
        form.add(new JLabel("Duration (mins):")); form.add(txtDuration);
        form.add(new JLabel("Rating (0-10):")); form.add(txtRating);
        form.add(new JLabel("Certification:")); form.add(cbCert);
        form.add(new JLabel("Status:")); form.add(cbStatus);
        form.add(new JLabel("Synopsis:")); form.add(new JScrollPane(txtDesc));

        int res = JOptionPane.showConfirmDialog(this, form, "Add New Movie", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION && !txtTitle.getText().trim().isEmpty()) {
            Movie m = new Movie();
            m.setTitle(txtTitle.getText().trim());
            m.setGenre(txtGenre.getText().trim());
            m.setLanguage(txtLang.getText().trim());
            m.setDurationMinutes(Integer.parseInt(txtDuration.getText().trim()));
            m.setRating(Double.parseDouble(txtRating.getText().trim()));
            m.setCertification((String) cbCert.getSelectedItem());
            m.setStatus((String) cbStatus.getSelectedItem());
            m.setDescription(txtDesc.getText().trim());
            m.setReleaseDate(new Date(System.currentTimeMillis()));

            movieService.addMovie(m);
            reloadAllData();
            JOptionPane.showMessageDialog(this, "Movie '" + m.getTitle() + "' added successfully!");
        }
    }

    private void showEditMovieDialog() {
        int row = tblMovies.getSelectedRow();
        if (row < 0 || cachedMovies == null || row >= cachedMovies.size()) {
            JOptionPane.showMessageDialog(this, "Please select a movie to edit.");
            return;
        }

        Movie m = cachedMovies.get(row);
        JTextField txtTitle = new JTextField(m.getTitle());
        JTextField txtGenre = new JTextField(m.getGenre());
        JTextField txtRating = new JTextField(String.valueOf(m.getRating()));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Title:")); form.add(txtTitle);
        form.add(new JLabel("Genre:")); form.add(txtGenre);
        form.add(new JLabel("Rating:")); form.add(txtRating);

        int res = JOptionPane.showConfirmDialog(this, form, "Edit Movie", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            m.setTitle(txtTitle.getText().trim());
            m.setGenre(txtGenre.getText().trim());
            m.setRating(Double.parseDouble(txtRating.getText().trim()));
            movieService.updateMovie(m);
            reloadAllData();
        }
    }

    private void deleteSelectedMovie() {
        int row = tblMovies.getSelectedRow();
        if (row < 0 || cachedMovies == null || row >= cachedMovies.size()) {
            JOptionPane.showMessageDialog(this, "Please select a movie to delete.");
            return;
        }

        Movie m = cachedMovies.get(row);
        int conf = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete '" + m.getTitle() + "'?", "Delete Movie", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            movieService.deleteMovie(m.getMovieId());
            reloadAllData();
        }
    }

    private void showAddTheatreDialog() {
        JTextField txtName = new JTextField();
        JTextField txtCity = new JTextField("Mumbai");
        JTextField txtAddress = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Theatre Name:")); form.add(txtName);
        form.add(new JLabel("City:")); form.add(txtCity);
        form.add(new JLabel("Address:")); form.add(txtAddress);

        int res = JOptionPane.showConfirmDialog(this, form, "Add Theatre", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION && !txtName.getText().trim().isEmpty()) {
            Theatre t = new Theatre(0, txtName.getText().trim(), txtCity.getText().trim(), txtAddress.getText().trim(), 3);
            theatreDAO.addTheatre(t);
            reloadAllData();
        }
    }

    private void showAddShowDialog() {
        if (cachedMovies == null || cachedMovies.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please create movies first.");
            return;
        }

        JComboBox<Movie> cbM = new JComboBox<>(cachedMovies.toArray(new Movie[0]));
        JTextField txtTime = new JTextField("19:00:00");
        JTextField txtPrice = new JTextField("350.00");

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Select Movie:")); form.add(cbM);
        form.add(new JLabel("Start Time (HH:MM:SS):")); form.add(txtTime);
        form.add(new JLabel("Ticket Price (₹):")); form.add(txtPrice);

        int res = JOptionPane.showConfirmDialog(this, form, "Schedule Show", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            Movie selectedM = (Movie) cbM.getSelectedItem();
            Show s = new Show();
            s.setMovieId(selectedM.getMovieId());
            s.setScreenId(1); // Screen 1 default
            s.setShowDate(new Date(System.currentTimeMillis()));
            s.setStartTime(Time.valueOf(txtTime.getText().trim()));
            s.setTicketPrice(Double.parseDouble(txtPrice.getText().trim()));
            s.setAvailableSeats(64);

            showDAO.addShow(s);
            reloadAllData();
            JOptionPane.showMessageDialog(this, "Show scheduled successfully!");
        }
    }

    private void deleteSelectedShow() {
        int row = tblShows.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a show to delete.");
            return;
        }
        int showId = (int) tblShows.getValueAt(row, 0);
        showDAO.deleteShow(showId);
        reloadAllData();
    }
}

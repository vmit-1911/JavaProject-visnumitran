package moviebooking.ui;

import moviebooking.dao.ShowDAO;
import moviebooking.dao.TheatreDAO;
import moviebooking.model.Movie;
import moviebooking.model.Show;
import moviebooking.model.Theatre;
import moviebooking.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Screen for selecting City, Theatre, Date, and Show Timings.
 */
public class TheatreSelectionPanel extends JPanel {

    private final HomeFrame parentFrame;
    private final ShowDAO showDAO = new ShowDAO();
    private final TheatreDAO theatreDAO = new TheatreDAO();

    private Movie selectedMovie;
    private JComboBox<String> cbCity;
    private JComboBox<String> cbDate;
    private JPanel showsContainer;

    public TheatreSelectionPanel(HomeFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_DARK);
    }

    public void setMovie(Movie movie) {
        this.selectedMovie = movie;
        removeAll();

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(24, 36, 24, 36));

        // Top Navigation & Movie Header
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setOpaque(false);

        JButton btnBack = UITheme.createSecondaryButton("← Back to Movie Details");
        btnBack.addActionListener(e -> parentFrame.showMovieDetails(selectedMovie));

        JLabel lblMovieHeader = new JLabel("Book Tickets: " + selectedMovie.getTitle() + " (" + selectedMovie.getLanguage() + ")");
        lblMovieHeader.setFont(UITheme.FONT_TITLE);
        lblMovieHeader.setForeground(Color.WHITE);

        topHeader.add(btnBack, BorderLayout.WEST);
        topHeader.add(lblMovieHeader, BorderLayout.CENTER);

        // Filter Bar (City & Date)
        JPanel filterCard = UITheme.createCardPanel();
        filterCard.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 4));

        JLabel lblCity = new JLabel("Select City:");
        lblCity.setFont(UITheme.FONT_SECTION);
        lblCity.setForeground(UITheme.TEXT_SECONDARY);

        cbCity = new JComboBox<>(new String[]{"All Cities", "Mumbai", "Delhi NCR", "Bangalore", "Chennai"});
        cbCity.setBackground(UITheme.BG_INPUT);
        cbCity.setForeground(UITheme.TEXT_PRIMARY);
        cbCity.setFont(UITheme.FONT_BODY);
        cbCity.addActionListener(e -> renderTheatresAndShows());

        JLabel lblDate = new JLabel("Date:");
        lblDate.setFont(UITheme.FONT_SECTION);
        lblDate.setForeground(UITheme.TEXT_SECONDARY);

        long now = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM");
        String todayStr = "Today (" + sdf.format(new Date(now)) + ")";
        String tomorrowStr = "Tomorrow (" + sdf.format(new Date(now + 86400000L)) + ")";
        String dayAfterStr = sdf.format(new Date(now + 86400000L * 2));

        cbDate = new JComboBox<>(new String[]{todayStr, tomorrowStr, dayAfterStr});
        cbDate.setBackground(UITheme.BG_INPUT);
        cbDate.setForeground(UITheme.TEXT_PRIMARY);
        cbDate.setFont(UITheme.FONT_BODY);
        cbDate.addActionListener(e -> renderTheatresAndShows());

        filterCard.add(lblCity);
        filterCard.add(cbCity);
        filterCard.add(new JSeparator(SwingConstants.VERTICAL));
        filterCard.add(lblDate);
        filterCard.add(cbDate);

        main.add(topHeader);
        main.add(Box.createVerticalStrut(16));
        main.add(filterCard);
        main.add(Box.createVerticalStrut(20));

        // Shows Container
        showsContainer = new JPanel();
        showsContainer.setLayout(new BoxLayout(showsContainer, BoxLayout.Y_AXIS));
        showsContainer.setOpaque(false);

        JScrollPane scroll = new JScrollPane(showsContainer);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        main.add(scroll);

        add(main, BorderLayout.CENTER);
        renderTheatresAndShows();
        revalidate();
        repaint();
    }

    private void renderTheatresAndShows() {
        showsContainer.removeAll();

        String selectedCity = (String) cbCity.getSelectedItem();
        List<Show> movieShows = showDAO.getShowsForMovie(selectedMovie.getMovieId());

        if (selectedCity != null && !selectedCity.equalsIgnoreCase("All Cities")) {
            movieShows = movieShows.stream()
                    .filter(s -> s.getCity() != null && s.getCity().equalsIgnoreCase(selectedCity))
                    .collect(Collectors.toList());
        }

        if (movieShows.isEmpty()) {
            JPanel empty = UITheme.createCardPanel();
            empty.setLayout(new FlowLayout(FlowLayout.CENTER));
            JLabel lblNone = new JLabel("No shows currently scheduled for this selection. Try selecting 'All Cities'.");
            lblNone.setFont(UITheme.FONT_BODY);
            lblNone.setForeground(UITheme.TEXT_MUTED);
            empty.add(lblNone);
            showsContainer.add(empty);
        } else {
            // Group shows by Theatre
            java.util.Map<String, List<Show>> grouped = movieShows.stream()
                    .collect(Collectors.groupingBy(s -> s.getTheatreName() != null ? s.getTheatreName() : "Cinema Hall"));

            for (java.util.Map.Entry<String, List<Show>> entry : grouped.entrySet()) {
                showsContainer.add(createTheatreCard(entry.getKey(), entry.getValue()));
                showsContainer.add(Box.createVerticalStrut(14));
            }
        }

        showsContainer.revalidate();
        showsContainer.repaint();
    }

    private JPanel createTheatreCard(String theatreName, List<Show> shows) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(16, 12));

        // Header info
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel lblName = new JLabel("🏛️ " + theatreName);
        lblName.setFont(UITheme.FONT_SUBTITLE);
        lblName.setForeground(Color.WHITE);

        String info = (shows.get(0).getCity() != null ? shows.get(0).getCity() : "") + " • Cancellation Available • M-Ticket";
        JLabel lblInfo = new JLabel(info);
        lblInfo.setFont(UITheme.FONT_SMALL);
        lblInfo.setForeground(UITheme.TEXT_SECONDARY);

        headerPanel.add(lblName);
        headerPanel.add(Box.createVerticalStrut(3));
        headerPanel.add(lblInfo);

        // Show Times Flow Panel
        JPanel timesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        timesPanel.setOpaque(false);

        for (Show show : shows) {
            String timeText = show.getFormattedTime();
            String priceText = "₹" + (int) show.getTicketPrice();
            String screenText = show.getScreenName() != null ? show.getScreenName() : "Standard";

            JButton btnTime = new JButton("<html><center><b>" + timeText + "</b><br><font size='2' color='#94a3b8'>" + screenText + " • " + priceText + "</font></center></html>");
            btnTime.setFont(UITheme.FONT_BODY);
            btnTime.setBackground(new Color(30, 41, 59));
            btnTime.setForeground(UITheme.SEAT_AVAILABLE);
            btnTime.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(51, 65, 85), 1, true),
                    BorderFactory.createEmptyBorder(6, 14, 6, 14)
            ));
            btnTime.setFocusPainted(false);
            btnTime.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btnTime.addActionListener(e -> parentFrame.showSeatSelection(show, selectedMovie));
            timesPanel.add(btnTime);
        }

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(timesPanel, BorderLayout.CENTER);
        return card;
    }
}

package moviebooking.ui;

import moviebooking.model.Movie;
import moviebooking.service.MovieService;
import moviebooking.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Main Movie Browsing panel featuring search, filters, and modern card grid.
 */
public class MoviePanel extends JPanel {

    private final HomeFrame parentFrame;
    private final JTextField txtSearch;
    private final JComboBox<String> cbGenre;
    private final JComboBox<String> cbLanguage;
    private final JComboBox<String> cbSort;
    private final JToggleButton btnNowShowing;
    private final JToggleButton btnUpcoming;
    private final JPanel moviesGrid;
    private final JLabel lblCount;

    public MoviePanel(HomeFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_DARK);

        // --- Top Hero Banner & Controls Bar ---
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);
        topContainer.setBorder(new EmptyBorder(16, 24, 16, 24));

        // Hero Promotional Banner
        JPanel heroBanner = createHeroBanner();
        topContainer.add(heroBanner);
        topContainer.add(Box.createVerticalStrut(16));

        // Filter Controls Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        filterBar.setOpaque(false);

        // Status Toggle (Now Showing vs Upcoming)
        btnNowShowing = new JToggleButton("Now Showing", true);
        btnUpcoming = new JToggleButton("Upcoming Releases", false);
        ButtonGroup statusGroup = new ButtonGroup();
        statusGroup.add(btnNowShowing);
        statusGroup.add(btnUpcoming);

        styleToggleButton(btnNowShowing);
        styleToggleButton(btnUpcoming);

        btnNowShowing.addActionListener(e -> reloadMovies());
        btnUpcoming.addActionListener(e -> reloadMovies());

        // Search Input
        txtSearch = UITheme.createTextField(14);
        txtSearch.setToolTipText("Search movie title or cast");
        JButton btnSearch = UITheme.createSecondaryButton("🔍 Search");
        btnSearch.addActionListener(e -> reloadMovies());

        // Genre Filter
        cbGenre = new JComboBox<>(new String[]{
                "All Genres", "Sci-Fi", "Action", "Drama", "Thriller", "Adventure", "Animation", "Biography"
        });
        styleComboBox(cbGenre);
        cbGenre.addActionListener(e -> reloadMovies());

        // Language Filter
        cbLanguage = new JComboBox<>(new String[]{
                "All Languages", "English", "Hindi", "Tamil", "Telugu"
        });
        styleComboBox(cbLanguage);
        cbLanguage.addActionListener(e -> reloadMovies());

        // Sort By
        cbSort = new JComboBox<>(new String[]{
                "Sort: Rating (High to Low)", "Sort: Release Date"
        });
        styleComboBox(cbSort);
        cbSort.addActionListener(e -> reloadMovies());

        filterBar.add(btnNowShowing);
        filterBar.add(btnUpcoming);
        filterBar.add(new JSeparator(SwingConstants.VERTICAL));
        filterBar.add(new JLabel("Search: ") {{ setForeground(UITheme.TEXT_SECONDARY); }});
        filterBar.add(txtSearch);
        filterBar.add(btnSearch);
        filterBar.add(cbGenre);
        filterBar.add(cbLanguage);
        filterBar.add(cbSort);

        topContainer.add(filterBar);

        // Header Count Display
        lblCount = new JLabel("Showing Movies");
        lblCount.setFont(UITheme.FONT_SUBTITLE);
        lblCount.setForeground(UITheme.TEXT_PRIMARY);
        lblCount.setBorder(new EmptyBorder(8, 6, 0, 0));
        topContainer.add(lblCount);

        add(topContainer, BorderLayout.NORTH);

        // --- Movies Grid Area ---
        moviesGrid = new JPanel(new GridLayout(0, 4, 20, 24));
        moviesGrid.setOpaque(false);
        moviesGrid.setBorder(new EmptyBorder(10, 24, 30, 24));

        JScrollPane scrollPane = new JScrollPane(moviesGrid);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);

        add(scrollPane, BorderLayout.CENTER);

        // Initial Load
        reloadMovies();
    }

    private JPanel createHeroBanner() {
        JPanel hero = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(136, 19, 55), getWidth(), 0, new Color(15, 23, 42));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(225, 29, 72, 80));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        hero.setOpaque(false);
        hero.setPreferredSize(new Dimension(800, 110));
        hero.setBorder(new EmptyBorder(16, 24, 16, 24));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel lblTag = new JLabel("FEATURED BLOCKBUSTER");
        lblTag.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTag.setForeground(UITheme.ACCENT_GOLD);

        JLabel lblTitle = new JLabel("Experience Nolan's Masterpieces in IMAX 70mm");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblDesc = new JLabel("Grab your seats early with exclusive Gold & Premium recliners. Dolby Atmos 3D audio enabled.");
        lblDesc.setFont(UITheme.FONT_BODY);
        lblDesc.setForeground(new Color(226, 232, 240));

        textPanel.add(lblTag);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(lblTitle);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(lblDesc);

        JButton btnExplore = UITheme.createPrimaryButton("Book IMAX Now");
        btnExplore.addActionListener(e -> {
            Movie interstellar = MovieService.getInstance().getMovieById(1);
            if (interstellar != null) {
                parentFrame.showMovieDetails(interstellar);
            }
        });

        hero.add(textPanel, BorderLayout.CENTER);
        hero.add(btnExplore, BorderLayout.EAST);
        return hero;
    }

    public void reloadMovies() {
        moviesGrid.removeAll();

        String status = btnNowShowing.isSelected() ? "NOW_SHOWING" : "UPCOMING";
        String query = txtSearch.getText().trim();
        String genre = (String) cbGenre.getSelectedItem();
        String language = (String) cbLanguage.getSelectedItem();
        String sortOption = (String) cbSort.getSelectedItem();
        String sortBy = (sortOption != null && sortOption.contains("Rating")) ? "rating" : "release";

        List<Movie> movies = MovieService.getInstance().searchMovies(query, genre, language, sortBy);
        // Filter by status tab
        movies.removeIf(m -> !m.getStatus().equalsIgnoreCase(status));

        lblCount.setText(status.equals("NOW_SHOWING") ? "Currently Showing (" + movies.size() + ")" : "Upcoming Releases (" + movies.size() + ")");

        if (movies.isEmpty()) {
            JPanel emptyPanel = new JPanel(new GridBagLayout());
            emptyPanel.setOpaque(false);
            JLabel lblEmpty = new JLabel("No movies match your criteria. Try adjusting your filters.");
            lblEmpty.setFont(UITheme.FONT_SUBTITLE);
            lblEmpty.setForeground(UITheme.TEXT_SECONDARY);
            emptyPanel.add(lblEmpty);
            moviesGrid.setLayout(new BorderLayout());
            moviesGrid.add(emptyPanel, BorderLayout.CENTER);
        } else {
            moviesGrid.setLayout(new GridLayout(0, 4, 20, 24));
            for (Movie movie : movies) {
                moviesGrid.add(createMovieCard(movie));
            }
        }

        moviesGrid.revalidate();
        moviesGrid.repaint();
    }

    private JPanel createMovieCard(Movie movie) {
        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(UITheme.BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(10, 10, 12, 10));

        // Poster image
        ImageIcon poster = UITheme.generatePosterImage(movie.getTitle(), movie.getGenre(), movie.getRating(), 200, 250);
        JLabel lblPoster = new JLabel(poster);
        lblPoster.setHorizontalAlignment(SwingConstants.CENTER);
        lblPoster.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblPoster.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parentFrame.showMovieDetails(movie);
            }
        });

        // Details Block
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(movie.getTitle());
        lblTitle.setFont(UITheme.FONT_SUBTITLE);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel lblSub = new JLabel(movie.getLanguage() + " • " + movie.getCertification() + " • " + movie.getFormattedDuration());
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.TEXT_SECONDARY);

        JLabel lblRating = new JLabel("⭐ " + movie.getRating() + "/10 • " + movie.getGenre());
        lblRating.setFont(UITheme.FONT_SMALL);
        lblRating.setForeground(UITheme.ACCENT_GOLD);

        JButton btnBook = UITheme.createPrimaryButton(movie.getStatus().equals("NOW_SHOWING") ? "Book Tickets" : "View Details");
        btnBook.addActionListener(e -> parentFrame.showMovieDetails(movie));

        detailsPanel.add(lblTitle);
        detailsPanel.add(Box.createVerticalStrut(3));
        detailsPanel.add(lblSub);
        detailsPanel.add(Box.createVerticalStrut(3));
        detailsPanel.add(lblRating);
        detailsPanel.add(Box.createVerticalStrut(8));
        detailsPanel.add(btnBook);

        card.add(lblPoster, BorderLayout.CENTER);
        card.add(detailsPanel, BorderLayout.SOUTH);

        return card;
    }

    private void styleToggleButton(JToggleButton btn) {
        btn.setFont(UITheme.FONT_SECTION);
        btn.setForeground(UITheme.TEXT_PRIMARY);
        btn.setBackground(UITheme.BG_CARD);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleComboBox(JComboBox<String> cb) {
        cb.setBackground(UITheme.BG_INPUT);
        cb.setForeground(UITheme.TEXT_PRIMARY);
        cb.setFont(UITheme.FONT_BODY);
        cb.setFocusable(false);
    }
}

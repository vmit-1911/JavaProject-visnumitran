package moviebooking.ui;

import moviebooking.model.Movie;
import moviebooking.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Detailed view of a selected movie with synopsis, metadata, and booking trigger.
 */
public class MovieDetailsPanel extends JPanel {

    private final HomeFrame parentFrame;
    private Movie currentMovie;

    public MovieDetailsPanel(HomeFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_DARK);
    }

    public void setMovie(Movie movie) {
        this.currentMovie = movie;
        removeAll();

        JPanel container = new JPanel(new BorderLayout(30, 20));
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(30, 40, 40, 40));

        // Top Navigation Bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topBar.setOpaque(false);
        JButton btnBack = UITheme.createSecondaryButton("← Back to Movies");
        btnBack.addActionListener(e -> parentFrame.showMovieBrowser());
        topBar.add(btnBack);

        // Left Poster Panel
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        ImageIcon poster = UITheme.generatePosterImage(movie.getTitle(), movie.getGenre(), movie.getRating(), 280, 390);
        JLabel lblPoster = new JLabel(poster);
        leftPanel.add(lblPoster, BorderLayout.NORTH);

        // Right Info Card
        JPanel rightCard = UITheme.createCardPanel();
        rightCard.setLayout(new BoxLayout(rightCard, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel(movie.getTitle());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblMeta = new JLabel(movie.getLanguage() + "  |  " + movie.getCertification() + "  |  " + movie.getFormattedDuration() + "  |  Released: " + movie.getReleaseDate());
        lblMeta.setFont(UITheme.FONT_SECTION);
        lblMeta.setForeground(UITheme.TEXT_SECONDARY);

        JLabel lblRating = new JLabel("⭐ " + movie.getRating() + " / 10  (Audience Score)");
        lblRating.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblRating.setForeground(UITheme.ACCENT_GOLD);

        JLabel lblGenre = new JLabel("Genre: " + movie.getGenre());
        lblGenre.setFont(UITheme.FONT_BODY_BOLD);
        lblGenre.setForeground(UITheme.TEXT_PRIMARY);

        JLabel lblAbout = new JLabel("About the Movie");
        lblAbout.setFont(UITheme.FONT_SUBTITLE);
        lblAbout.setForeground(UITheme.PRIMARY_HOVER);

        JTextArea txtDesc = new JTextArea(movie.getDescription());
        txtDesc.setFont(UITheme.FONT_BODY);
        txtDesc.setForeground(new Color(203, 213, 225));
        txtDesc.setOpaque(false);
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setEditable(false);
        txtDesc.setBorder(null);

        // Action Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        actionPanel.setOpaque(false);

        JButton btnBook = UITheme.createPrimaryButton("🎟️ Select Theatre & Shows");
        btnBook.setPreferredSize(new Dimension(240, 44));
        btnBook.addActionListener(e -> parentFrame.showTheatreSelection(currentMovie));

        actionPanel.add(btnBook);

        rightCard.add(lblTitle);
        rightCard.add(Box.createVerticalStrut(8));
        rightCard.add(lblMeta);
        rightCard.add(Box.createVerticalStrut(12));
        rightCard.add(lblRating);
        rightCard.add(Box.createVerticalStrut(14));
        rightCard.add(lblGenre);
        rightCard.add(Box.createVerticalStrut(20));
        rightCard.add(lblAbout);
        rightCard.add(Box.createVerticalStrut(8));
        rightCard.add(txtDesc);
        rightCard.add(Box.createVerticalGlue());
        rightCard.add(actionPanel);

        container.add(topBar, BorderLayout.NORTH);
        container.add(leftPanel, BorderLayout.WEST);
        container.add(rightCard, BorderLayout.CENTER);

        add(container, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}

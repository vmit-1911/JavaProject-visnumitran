package moviebooking.ui;

import moviebooking.model.Movie;
import moviebooking.model.Seat;
import moviebooking.model.Show;
import moviebooking.service.SeatService;
import moviebooking.util.UITheme;
import moviebooking.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Interactive Cinema Seating Layout.
 * Features Curved Cinema Screen indicator, Silver/Gold/Premium tiers,
 * Green (Available), Blue (Selected), Red (Booked) states,
 * and live dynamic price calculation.
 */
public class SeatSelectionPanel extends JPanel {

    private final HomeFrame parentFrame;
    private final SeatService seatService = SeatService.getInstance();

    private Show currentShow;
    private Movie currentMovie;

    private List<Seat> allSeats = new ArrayList<>();
    private final List<Seat> selectedSeats = new ArrayList<>();

    private JLabel lblSelectedSeats;
    private JLabel lblTotalPrice;
    private JButton btnProceed;

    public SeatSelectionPanel(HomeFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_DARK);
    }

    public void setShowAndMovie(Show show, Movie movie) {
        this.currentShow = show;
        this.currentMovie = movie;
        selectedSeats.clear();
        removeAll();

        // Load seats from service
        allSeats = seatService.getSeatsForShow(show);

        JPanel main = new JPanel(new BorderLayout(0, 16));
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(16, 28, 16, 28));

        // --- Top Header ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setOpaque(false);

        JButton btnBack = UITheme.createSecondaryButton("← Change Show Timing");
        btnBack.addActionListener(e -> parentFrame.showTheatreSelection(currentMovie));

        JLabel lblTitle = new JLabel(movie.getTitle() + " | " + show.getTheatreName() + " (" + show.getScreenName() + ")");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblTiming = new JLabel("📅 " + show.getFormattedDate() + " @ " + show.getFormattedTime());
        lblTiming.setFont(UITheme.FONT_SECTION);
        lblTiming.setForeground(UITheme.TEXT_SECONDARY);

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);
        titleBlock.add(lblTitle);
        titleBlock.add(lblTiming);

        navBar.add(btnBack, BorderLayout.WEST);
        navBar.add(titleBlock, BorderLayout.CENTER);

        topPanel.add(navBar);
        topPanel.add(Box.createVerticalStrut(14));

        // Cinema Screen Arc Indicator
        topPanel.add(createCinemaScreenIndicator());
        topPanel.add(Box.createVerticalStrut(10));

        // Legend Bar (Available, Selected, Booked, Tiers)
        topPanel.add(createLegendBar());

        main.add(topPanel, BorderLayout.NORTH);

        // --- Center Interactive Seat Grid ---
        JPanel gridScrollContainer = new JPanel();
        gridScrollContainer.setLayout(new BoxLayout(gridScrollContainer, BoxLayout.Y_AXIS));
        gridScrollContainer.setOpaque(false);

        // Tier 1: PREMIUM (Rows A, B)
        gridScrollContainer.add(createTierSection("PREMIUM - ₹350", new String[]{"A", "B"}, Seat.Category.PREMIUM));
        gridScrollContainer.add(Box.createVerticalStrut(16));

        // Tier 2: GOLD (Rows C, D, E)
        gridScrollContainer.add(createTierSection("GOLD - ₹250", new String[]{"C", "D", "E"}, Seat.Category.GOLD));
        gridScrollContainer.add(Box.createVerticalStrut(16));

        // Tier 3: SILVER (Rows F, G, H)
        gridScrollContainer.add(createTierSection("SILVER - ₹150", new String[]{"F", "G", "H"}, Seat.Category.SILVER));

        JScrollPane scrollPane = new JScrollPane(gridScrollContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        main.add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Summary & Booking Action Bar ---
        JPanel bottomBar = createBottomActionBar();
        main.add(bottomBar, BorderLayout.SOUTH);

        add(main, BorderLayout.CENTER);
        updateSelectionSummary();
        revalidate();
        repaint();
    }

    private JPanel createCinemaScreenIndicator() {
        JPanel screen = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Draw curved screen arc
                g2.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setPaint(new GradientPaint(w * 0.2f, 0, new Color(56, 189, 248), w * 0.8f, 0, new Color(225, 29, 72)));

                QuadCurve2D curve = new QuadCurve2D.Float(w * 0.15f, h * 0.75f, w * 0.5f, 5, w * 0.85f, h * 0.75f);
                g2.draw(curve);

                // Screen glow
                g2.setPaint(new GradientPaint(0, 0, new Color(56, 189, 248, 40), 0, h, new Color(0, 0, 0, 0)));
                g2.fillRect((int)(w * 0.2f), 10, (int)(w * 0.6f), h - 10);

                // Label
                g2.setColor(UITheme.TEXT_SECONDARY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                String text = "SCREEN THIS WAY  •  ALL EYES TOWARDS THE SCREEN";
                FontMetrics fm = g2.getFontMetrics();
                int textX = (w - fm.stringWidth(text)) / 2;
                g2.drawString(text, textX, h - 8);

                g2.dispose();
            }
        };
        screen.setOpaque(false);
        screen.setPreferredSize(new Dimension(800, 48));
        return screen;
    }

    private JPanel createLegendBar() {
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.CENTER, 28, 4));
        legend.setOpaque(false);

        legend.add(createLegendItem(UITheme.SEAT_AVAILABLE, "Available (Click to Select)"));
        legend.add(createLegendItem(UITheme.SEAT_SELECTED, "Selected"));
        legend.add(createLegendItem(UITheme.SEAT_BOOKED, "Booked / Occupied"));

        return legend;
    }

    private JPanel createLegendItem(Color color, String label) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        p.setOpaque(false);

        JPanel box = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, 16, 16, 4, 4);
                g2.dispose();
            }
        };
        box.setPreferredSize(new Dimension(16, 16));
        box.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_SMALL);
        lbl.setForeground(UITheme.TEXT_SECONDARY);

        p.add(box);
        p.add(lbl);
        return p;
    }

    private JPanel createTierSection(String title, String[] rows, Seat.Category category) {
        JPanel tier = UITheme.createCardPanel();
        tier.setLayout(new BoxLayout(tier, BoxLayout.Y_AXIS));

        JLabel lblTier = new JLabel(title);
        lblTier.setFont(UITheme.FONT_SECTION);
        lblTier.setForeground(category == Seat.Category.PREMIUM ? UITheme.ACCENT_GOLD : (category == Seat.Category.GOLD ? new Color(251, 191, 36) : UITheme.TEXT_SECONDARY));
        lblTier.setAlignmentX(Component.CENTER_ALIGNMENT);

        tier.add(lblTier);
        tier.add(Box.createVerticalStrut(10));

        for (String rowLetter : rows) {
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
            rowPanel.setOpaque(false);

            // Left Row Label
            JLabel lblRowLeft = new JLabel(rowLetter);
            lblRowLeft.setFont(UITheme.FONT_BODY_BOLD);
            lblRowLeft.setForeground(UITheme.TEXT_MUTED);
            lblRowLeft.setPreferredSize(new Dimension(24, 32));
            rowPanel.add(lblRowLeft);

            // 8 Seats in row
            for (int seatNum = 1; seatNum <= 8; seatNum++) {
                final int sn = seatNum;
                final String rl = rowLetter;

                // Find seat in model
                Seat seat = allSeats.stream()
                        .filter(s -> s.getRowChar().equalsIgnoreCase(rl) && s.getSeatNumber() == sn)
                        .findFirst()
                        .orElseGet(() -> new Seat(0, currentShow.getScreenId(), rl, sn, category, category.getDefaultPrice()));

                JButton seatBtn = createSeatButton(seat);
                rowPanel.add(seatBtn);

                // Add aisle gap in the middle (between seat 4 and 5)
                if (seatNum == 4) {
                    rowPanel.add(Box.createHorizontalStrut(28));
                }
            }

            // Right Row Label
            JLabel lblRowRight = new JLabel(rowLetter);
            lblRowRight.setFont(UITheme.FONT_BODY_BOLD);
            lblRowRight.setForeground(UITheme.TEXT_MUTED);
            lblRowRight.setPreferredSize(new Dimension(24, 32));
            rowPanel.add(lblRowRight);

            tier.add(rowPanel);
        }

        return tier;
    }

    private JButton createSeatButton(Seat seat) {
        JButton btn = new JButton(String.valueOf(seat.getSeatNumber())) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bg;
                if (seat.isBooked()) {
                    bg = UITheme.SEAT_BOOKED;
                } else if (seat.isSelected()) {
                    bg = UITheme.SEAT_SELECTED;
                } else {
                    bg = UITheme.SEAT_AVAILABLE;
                }

                // Hover shade
                if (getModel().isRollover() && !seat.isBooked()) {
                    bg = bg.brighter();
                }

                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);

                // Seat outline
                g2.setColor(new Color(255, 255, 255, 40));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setPreferredSize(new Dimension(36, 34));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);

        if (seat.isBooked()) {
            btn.setEnabled(false);
            btn.setToolTipText(seat.getSeatLabel() + " is already BOOKED");
            btn.setCursor(Cursor.getDefaultCursor());
        } else {
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setToolTipText(seat.getSeatLabel() + " (" + seat.getCategory() + " - ₹" + (int) seat.getBasePrice() + ")");
            btn.addActionListener(e -> {
                if (seat.isSelected()) {
                    // Deselect
                    seat.setStatus(Seat.Status.AVAILABLE);
                    selectedSeats.remove(seat);
                } else {
                    // Select
                    seat.setStatus(Seat.Status.SELECTED);
                    selectedSeats.add(seat);
                }
                btn.repaint();
                updateSelectionSummary();
            });
        }

        return btn;
    }

    private JPanel createBottomActionBar() {
        JPanel bar = UITheme.createCardPanel();
        bar.setLayout(new BorderLayout(20, 0));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        lblSelectedSeats = new JLabel("No seats selected");
        lblSelectedSeats.setFont(UITheme.FONT_SUBTITLE);
        lblSelectedSeats.setForeground(Color.WHITE);

        lblTotalPrice = new JLabel("Total: ₹0.00 (select seats above)");
        lblTotalPrice.setFont(UITheme.FONT_BODY);
        lblTotalPrice.setForeground(UITheme.TEXT_SECONDARY);

        infoPanel.add(lblSelectedSeats);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(lblTotalPrice);

        btnProceed = UITheme.createPrimaryButton("Proceed to Booking Summary →");
        btnProceed.setEnabled(false);
        btnProceed.setPreferredSize(new Dimension(280, 44));
        btnProceed.addActionListener(e -> {
            if (!selectedSeats.isEmpty()) {
                parentFrame.showBookingSummary(currentShow, currentMovie, new ArrayList<>(selectedSeats));
            }
        });

        bar.add(infoPanel, BorderLayout.CENTER);
        bar.add(btnProceed, BorderLayout.EAST);
        return bar;
    }

    private void updateSelectionSummary() {
        if (selectedSeats.isEmpty()) {
            lblSelectedSeats.setText("Selected Seats: None");
            lblTotalPrice.setText("Select seats to view dynamic pricing");
            btnProceed.setEnabled(false);
        } else {
            String seatList = selectedSeats.stream()
                    .map(Seat::getSeatLabel)
                    .collect(Collectors.joining(", "));

            double base = seatService.calculateBaseAmount(selectedSeats);
            double total = seatService.calculateGrandTotal(selectedSeats);

            lblSelectedSeats.setText("Selected Seats (" + selectedSeats.size() + "): " + seatList);
            lblTotalPrice.setText("Base: " + ValidationUtil.formatCurrency(base) + "  |  Grand Total (incl. GST): " + ValidationUtil.formatCurrency(total));
            btnProceed.setEnabled(true);
        }
    }
}

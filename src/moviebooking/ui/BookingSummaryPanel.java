package moviebooking.ui;

import moviebooking.model.Movie;
import moviebooking.model.Seat;
import moviebooking.model.Show;
import moviebooking.model.User;
import moviebooking.service.AuthenticationService;
import moviebooking.service.SeatService;
import moviebooking.util.UITheme;
import moviebooking.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Booking Summary Screen detailing movie, show, allocated seats, and transparent price breakdown.
 */
public class BookingSummaryPanel extends JPanel {

    private final HomeFrame parentFrame;
    private final SeatService seatService = SeatService.getInstance();

    private Show show;
    private Movie movie;
    private List<Seat> selectedSeats;

    public BookingSummaryPanel(HomeFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_DARK);
    }

    public void setDetails(Show show, Movie movie, List<Seat> selectedSeats) {
        this.show = show;
        this.movie = movie;
        this.selectedSeats = selectedSeats;
        removeAll();

        User currentUser = AuthenticationService.getInstance().getCurrentUser();
        String customerName = currentUser != null ? currentUser.getName() : "Guest User";

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(24, 40, 24, 40));

        // Top Navigation
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topBar.setOpaque(false);
        JButton btnBack = UITheme.createSecondaryButton("← Modify Seat Selection");
        btnBack.addActionListener(e -> parentFrame.showSeatSelection(show, movie));
        topBar.add(btnBack);

        JLabel lblHeading = new JLabel("Booking & Order Summary");
        lblHeading.setFont(UITheme.FONT_TITLE);
        lblHeading.setForeground(Color.WHITE);
        lblHeading.setBorder(new EmptyBorder(12, 0, 16, 0));

        // Two Column Container (Left: Movie & Booking Details, Right: Price Breakdown Card)
        JPanel contentGrid = new JPanel(new GridLayout(1, 2, 28, 0));
        contentGrid.setOpaque(false);

        // Left Card: Show and Customer Details
        JPanel leftCard = UITheme.createCardPanel();
        leftCard.setLayout(new BoxLayout(leftCard, BoxLayout.Y_AXIS));

        leftCard.add(createDetailRow("Customer Name", customerName));
        leftCard.add(Box.createVerticalStrut(10));
        leftCard.add(createDetailRow("Movie Title", movie.getTitle() + " (" + movie.getLanguage() + " " + movie.getCertification() + ")"));
        leftCard.add(Box.createVerticalStrut(10));
        leftCard.add(createDetailRow("Theatre", show.getTheatreName() + ", " + (show.getCity() != null ? show.getCity() : "")));
        leftCard.add(Box.createVerticalStrut(10));
        leftCard.add(createDetailRow("Auditorium", show.getScreenName()));
        leftCard.add(Box.createVerticalStrut(10));
        leftCard.add(createDetailRow("Show Date", show.getFormattedDate()));
        leftCard.add(Box.createVerticalStrut(10));
        leftCard.add(createDetailRow("Show Time", show.getFormattedTime()));
        leftCard.add(Box.createVerticalStrut(14));

        String seatLabels = selectedSeats.stream().map(Seat::getSeatLabel).collect(Collectors.joining(", "));
        leftCard.add(createDetailRow("Selected Seats", seatLabels + " (" + selectedSeats.size() + " Tickets)"));

        // Right Card: Price Calculation Breakdown
        double baseAmount = seatService.calculateBaseAmount(selectedSeats);
        double convenienceFee = seatService.calculateConvenienceFee(selectedSeats);
        double gst = seatService.calculateGst(baseAmount + convenienceFee);
        double total = baseAmount + convenienceFee + gst;

        JPanel rightCard = UITheme.createCardPanel();
        rightCard.setLayout(new BoxLayout(rightCard, BoxLayout.Y_AXIS));

        JLabel lblPriceHead = new JLabel("Payment Summary");
        lblPriceHead.setFont(UITheme.FONT_SUBTITLE);
        lblPriceHead.setForeground(Color.WHITE);
        rightCard.add(lblPriceHead);
        rightCard.add(Box.createVerticalStrut(16));

        rightCard.add(createPriceRow("Base Ticket Price (" + selectedSeats.size() + " seats)", ValidationUtil.formatCurrency(baseAmount)));
        rightCard.add(Box.createVerticalStrut(10));
        rightCard.add(createPriceRow("Convenience Fee (₹30 / seat)", ValidationUtil.formatCurrency(convenienceFee)));
        rightCard.add(Box.createVerticalStrut(10));
        rightCard.add(createPriceRow("Integrated GST (18%)", ValidationUtil.formatCurrency(gst)));
        rightCard.add(Box.createVerticalStrut(16));

        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.BORDER_COLOR);
        rightCard.add(sep);
        rightCard.add(Box.createVerticalStrut(14));

        // Grand Total Row
        JPanel grandRow = new JPanel(new BorderLayout());
        grandRow.setOpaque(false);
        JLabel lblTotalLabel = new JLabel("Grand Total Amount:");
        lblTotalLabel.setFont(UITheme.FONT_TITLE);
        lblTotalLabel.setForeground(Color.WHITE);

        JLabel lblTotalVal = new JLabel(ValidationUtil.formatCurrency(total));
        lblTotalVal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTotalVal.setForeground(UITheme.SEAT_AVAILABLE);

        grandRow.add(lblTotalLabel, BorderLayout.WEST);
        grandRow.add(lblTotalVal, BorderLayout.EAST);
        rightCard.add(grandRow);
        rightCard.add(Box.createVerticalGlue());

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        btnPanel.setOpaque(false);

        JButton btnCancel = UITheme.createSecondaryButton("Cancel Booking");
        btnCancel.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel and return to movie selection?", "Cancel Booking", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                parentFrame.showMovieBrowser();
            }
        });

        JButton btnProceedPay = UITheme.createPrimaryButton("Proceed to Payment →");
        btnProceedPay.addActionListener(e -> parentFrame.showPayment(show, movie, selectedSeats, total));

        btnPanel.add(btnCancel);
        btnPanel.add(btnProceedPay);
        rightCard.add(btnPanel);

        contentGrid.add(leftCard);
        contentGrid.add(rightCard);

        main.add(topBar);
        main.add(lblHeading);
        main.add(contentGrid);

        add(main, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel createDetailRow(String label, String value) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_BODY);
        lbl.setForeground(UITheme.TEXT_SECONDARY);

        JLabel val = new JLabel(value);
        val.setFont(UITheme.FONT_BODY_BOLD);
        val.setForeground(Color.WHITE);

        p.add(lbl, BorderLayout.WEST);
        p.add(val, BorderLayout.EAST);
        return p;
    }

    private JPanel createPriceRow(String label, String value) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_BODY);
        lbl.setForeground(UITheme.TEXT_SECONDARY);

        JLabel val = new JLabel(value);
        val.setFont(UITheme.FONT_BODY);
        val.setForeground(Color.WHITE);

        p.add(lbl, BorderLayout.WEST);
        p.add(val, BorderLayout.EAST);
        return p;
    }
}

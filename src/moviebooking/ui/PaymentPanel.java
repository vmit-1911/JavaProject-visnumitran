package moviebooking.ui;

import moviebooking.model.Booking;
import moviebooking.model.Movie;
import moviebooking.model.Seat;
import moviebooking.model.Show;
import moviebooking.model.User;
import moviebooking.service.AuthenticationService;
import moviebooking.service.BookingService;
import moviebooking.service.PaymentService;
import moviebooking.util.UITheme;
import moviebooking.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Simulated Payment Interface supporting UPI, Cards, Net Banking, and Box Office Cash.
 */
public class PaymentPanel extends JPanel {

    private final HomeFrame parentFrame;
    private final BookingService bookingService = BookingService.getInstance();
    private final PaymentService paymentService = PaymentService.getInstance();

    private Show show;
    private Movie movie;
    private List<Seat> selectedSeats;
    private double grandTotal;

    private JComboBox<String> cbMethod;
    private JTextField txtIdentifier;
    private JLabel lblPrompt;
    private JLabel lblAmountDisplay;

    public PaymentPanel(HomeFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_DARK);
    }

    public void setPaymentDetails(Show show, Movie movie, List<Seat> selectedSeats, double total) {
        this.show = show;
        this.movie = movie;
        this.selectedSeats = selectedSeats;
        this.grandTotal = total;
        removeAll();

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(30, 80, 30, 80));

        // Top Navigation
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topBar.setOpaque(false);
        JButton btnBack = UITheme.createSecondaryButton("← Back to Summary");
        btnBack.addActionListener(e -> parentFrame.showBookingSummary(show, movie, selectedSeats));
        topBar.add(btnBack);

        // Payment Card
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setMaximumSize(new Dimension(600, 520));

        JLabel lblTitle = new JLabel("💳 Secure Payment Gateway (Simulation)");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Complete your transaction to generate your official cinema ticket.");
        lblSub.setFont(UITheme.FONT_BODY);
        lblSub.setForeground(UITheme.TEXT_SECONDARY);

        lblAmountDisplay = new JLabel("Amount Payable: " + ValidationUtil.formatCurrency(grandTotal));
        lblAmountDisplay.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblAmountDisplay.setForeground(UITheme.SEAT_AVAILABLE);

        // Payment Method Dropdown
        JLabel lblMethodSelect = new JLabel("Select Payment Method:");
        lblMethodSelect.setFont(UITheme.FONT_SECTION);
        lblMethodSelect.setForeground(Color.WHITE);

        cbMethod = new JComboBox<>(new String[]{
                "UPI (GPay / PhonePe / Paytm / BHIM)",
                "Credit Card",
                "Debit Card",
                "Net Banking (HDFC, SBI, ICICI, Axis)",
                "Cash at Cinema Counter"
        });
        cbMethod.setBackground(UITheme.BG_INPUT);
        cbMethod.setForeground(UITheme.TEXT_PRIMARY);
        cbMethod.setFont(UITheme.FONT_BODY);

        lblPrompt = new JLabel("Enter UPI ID (e.g. mobile@upi):");
        lblPrompt.setFont(UITheme.FONT_SECTION);
        lblPrompt.setForeground(UITheme.TEXT_SECONDARY);

        txtIdentifier = UITheme.createTextField(24);
        txtIdentifier.setText("mitran@okhdfcbank");

        cbMethod.addActionListener(e -> {
            int idx = cbMethod.getSelectedIndex();
            if (idx == 0) {
                lblPrompt.setText("Enter UPI ID (e.g. mobile@upi):");
                txtIdentifier.setText("mitran@okhdfcbank");
                txtIdentifier.setVisible(true);
            } else if (idx == 1 || idx == 2) {
                lblPrompt.setText("Simulated Card Number (e.g. 4111-XXXX-XXXX-1111):");
                txtIdentifier.setText("4111 2222 3333 4444");
                txtIdentifier.setVisible(true);
            } else if (idx == 3) {
                lblPrompt.setText("Select Bank Name:");
                txtIdentifier.setText("State Bank of India - Net Banking");
                txtIdentifier.setVisible(true);
            } else {
                lblPrompt.setText("Pay directly at Box Office 30 minutes before showtime.");
                txtIdentifier.setText("BOX_OFFICE_CASH_HOLD");
                txtIdentifier.setVisible(false);
            }
        });

        JLabel lblNotice = new JLabel("🔒 Test Mode: No real funds will be deducted. Never share personal CVVs.");
        lblNotice.setFont(UITheme.FONT_SMALL);
        lblNotice.setForeground(UITheme.ACCENT_GOLD);

        JButton btnPay = UITheme.createPrimaryButton("Pay " + ValidationUtil.formatCurrency(grandTotal) + " & Confirm Booking");
        btnPay.setPreferredSize(new Dimension(380, 46));
        btnPay.addActionListener(e -> processPayment());

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(6));
        card.add(lblSub);
        card.add(Box.createVerticalStrut(18));
        card.add(lblAmountDisplay);
        card.add(Box.createVerticalStrut(20));
        card.add(lblMethodSelect);
        card.add(Box.createVerticalStrut(8));
        card.add(cbMethod);
        card.add(Box.createVerticalStrut(16));
        card.add(lblPrompt);
        card.add(Box.createVerticalStrut(8));
        card.add(txtIdentifier);
        card.add(Box.createVerticalStrut(16));
        card.add(lblNotice);
        card.add(Box.createVerticalStrut(24));
        card.add(btnPay);

        main.add(topBar);
        main.add(Box.createVerticalStrut(16));
        main.add(card);

        add(main, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void processPayment() {
        String methodChoice = (String) cbMethod.getSelectedItem();
        String internalMethod = "UPI";
        if (methodChoice != null) {
            if (methodChoice.contains("Credit")) internalMethod = "CREDIT_CARD";
            else if (methodChoice.contains("Debit")) internalMethod = "DEBIT_CARD";
            else if (methodChoice.contains("Net")) internalMethod = "NET_BANKING";
            else if (methodChoice.contains("Cash")) internalMethod = "CASH_AT_COUNTER";
        }

        String txnId = paymentService.generateTransactionId(internalMethod);
        User user = AuthenticationService.getInstance().getCurrentUser();
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Please sign in to complete your booking.", "Session Expired", JOptionPane.WARNING_MESSAGE);
            parentFrame.showLogin();
            return;
        }

        try {
            // Process transactional seat booking
            Booking confirmedBooking = bookingService.processBooking(user, show, selectedSeats, internalMethod, txnId);

            JOptionPane.showMessageDialog(this,
                    "🎉 Payment Successful!\nTransaction ID: " + txnId + "\nBooking Reference: " + confirmedBooking.getBookingReference(),
                    "Booking Confirmed",
                    JOptionPane.INFORMATION_MESSAGE);

            // Navigate to Ticket Generation screen
            parentFrame.showTicket(confirmedBooking);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Booking Failed: " + ex.getMessage(),
                    "Transaction Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

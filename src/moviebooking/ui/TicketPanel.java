package moviebooking.ui;

import moviebooking.model.Booking;
import moviebooking.util.TicketGenerator;
import moviebooking.util.UITheme;
import moviebooking.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

/**
 * Screen displaying the official Cinema Ticket with visual ticket card,
 * printing, and export options.
 */
public class TicketPanel extends JPanel {

    private final HomeFrame parentFrame;
    private Booking currentBooking;

    public TicketPanel(HomeFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_DARK);
    }

    public void setBooking(Booking booking) {
        this.currentBooking = booking;
        removeAll();

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(24, 60, 30, 60));

        // Top Success Banner
        JLabel lblSuccess = new JLabel("✅ Booking Confirmed Successfully!");
        lblSuccess.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblSuccess.setForeground(UITheme.SEAT_AVAILABLE);
        lblSuccess.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Your official movie e-ticket is ready below.");
        lblSub.setFont(UITheme.FONT_BODY);
        lblSub.setForeground(UITheme.TEXT_SECONDARY);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        main.add(lblSuccess);
        main.add(Box.createVerticalStrut(4));
        main.add(lblSub);
        main.add(Box.createVerticalStrut(20));

        // Center Cinema Ticket Card
        JPanel ticketCard = createVisualTicketCard(booking);
        ticketCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        main.add(ticketCard);
        main.add(Box.createVerticalStrut(24));

        // Action Buttons Row
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        actionRow.setOpaque(false);

        JButton btnPrint = UITheme.createPrimaryButton("🖨️ Print Ticket / Save to PDF");
        btnPrint.addActionListener(e -> TicketGenerator.printTicket(currentBooking, this));

        JButton btnDownload = UITheme.createSecondaryButton("💾 Download / Save Ticket File");
        btnDownload.addActionListener(e -> saveTicketToFile());

        JButton btnMyBookings = UITheme.createSecondaryButton("🎟️ My Bookings History");
        btnMyBookings.addActionListener(e -> parentFrame.showMyBookings());

        JButton btnHome = UITheme.createSecondaryButton("🏠 Home Dashboard");
        btnHome.addActionListener(e -> parentFrame.showMovieBrowser());

        actionRow.add(btnPrint);
        actionRow.add(btnDownload);
        actionRow.add(btnMyBookings);
        actionRow.add(btnHome);

        main.add(actionRow);

        JScrollPane scroll = new JScrollPane(main);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);

        add(scroll, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel createVisualTicketCard(Booking b) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Ticket Background
                g2.setColor(new Color(24, 32, 52));
                g2.fillRoundRect(0, 0, w, h, 18, 18);

                // Perforated notches on left and right side
                g2.setColor(UITheme.BG_DARK);
                int notchY = (int) (h * 0.72);
                g2.fillOval(-14, notchY - 14, 28, 28);
                g2.fillOval(w - 14, notchY - 14, 28, 28);

                // Dashed Perforation Line
                Stroke dashed = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{6, 6}, 0);
                g2.setStroke(dashed);
                g2.setColor(new Color(51, 65, 85));
                g2.drawLine(20, notchY, w - 20, notchY);

                // Top Red Header Band
                g2.setColor(UITheme.PRIMARY_RED);
                g2.fillRoundRect(0, 0, w, 48, 18, 18);
                g2.fillRect(0, 30, w, 18);

                // Border
                g2.setColor(new Color(51, 65, 85));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 18, 18);

                g2.dispose();
            }
        };

        card.setPreferredSize(new Dimension(540, 480));
        card.setMaximumSize(new Dimension(540, 480));
        card.setOpaque(false);
        card.setLayout(new BorderLayout());

        // Header band content
        JPanel headPanel = new JPanel(new BorderLayout());
        headPanel.setOpaque(false);
        headPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel lblBrand = new JLabel("🎬 BOOKMYSHOW OFFICIAL TICKET");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBrand.setForeground(Color.WHITE);

        JLabel lblRef = new JLabel(b.getBookingReference());
        lblRef.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblRef.setForeground(new Color(254, 243, 199));

        headPanel.add(lblBrand, BorderLayout.WEST);
        headPanel.add(lblRef, BorderLayout.EAST);
        card.add(headPanel, BorderLayout.NORTH);

        // Body Info
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel lblMovie = new JLabel(b.getMovieTitle());
        lblMovie.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblMovie.setForeground(Color.WHITE);

        JLabel lblTheatre = new JLabel(b.getTheatreName() + " | " + b.getScreenName());
        lblTheatre.setFont(UITheme.FONT_SECTION);
        lblTheatre.setForeground(UITheme.TEXT_SECONDARY);

        body.add(lblMovie);
        body.add(Box.createVerticalStrut(4));
        body.add(lblTheatre);
        body.add(Box.createVerticalStrut(16));

        // 2x2 Grid for Date, Time, Customer, Seats
        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 10));
        grid.setOpaque(false);

        grid.add(createTicketInfoBlock("DATE", b.getShowDate()));
        grid.add(createTicketInfoBlock("SHOW TIME", b.getShowTime()));
        grid.add(createTicketInfoBlock("CUSTOMER", b.getCustomerName()));
        grid.add(createTicketInfoBlock("SEATS (" + b.getTotalSeats() + ")", b.getSeatLabelsFormatted()));

        body.add(grid);
        body.add(Box.createVerticalStrut(38)); // Space for perforation gap

        // Bottom Perforated Stub (Total & Barcode simulation)
        JPanel stub = new JPanel(new BorderLayout());
        stub.setOpaque(false);
        stub.setBorder(new EmptyBorder(12, 0, 0, 0));

        JPanel leftStub = new JPanel();
        leftStub.setLayout(new BoxLayout(leftStub, BoxLayout.Y_AXIS));
        leftStub.setOpaque(false);

        JLabel lblTotal = new JLabel("Total Paid: " + ValidationUtil.formatCurrency(b.getTotalAmount()));
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotal.setForeground(UITheme.SEAT_AVAILABLE);

        JLabel lblTxn = new JLabel("Txn: " + (b.getTransactionId() != null ? b.getTransactionId() : "N/A"));
        lblTxn.setFont(UITheme.FONT_SMALL);
        lblTxn.setForeground(UITheme.TEXT_MUTED);

        leftStub.add(lblTotal);
        leftStub.add(Box.createVerticalStrut(2));
        leftStub.add(lblTxn);

        // QR Code Simulated Block
        JPanel qrMock = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(Color.BLACK);
                // Draw pseudo QR grid pattern
                int size = 6;
                int hash = Math.abs(b.getBookingReference().hashCode());
                for (int r = 0; r < 7; r++) {
                    for (int c = 0; c < 7; c++) {
                        if (((hash >> (r * 3 + c)) & 1) == 1 || r == 0 || c == 0 || r == 6 || c == 6) {
                            g2.fillRect(r * size + 4, c * size + 4, size - 1, size - 1);
                        }
                    }
                }
                g2.dispose();
            }
        };
        qrMock.setPreferredSize(new Dimension(50, 50));

        stub.add(leftStub, BorderLayout.WEST);
        stub.add(qrMock, BorderLayout.EAST);

        body.add(stub);
        card.add(body, BorderLayout.CENTER);

        return card;
    }

    private JPanel createTicketInfoBlock(String label, String value) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(UITheme.TEXT_MUTED);

        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 14));
        v.setForeground(Color.WHITE);

        p.add(l);
        p.add(Box.createVerticalStrut(2));
        p.add(v);
        return p;
    }

    private void saveTicketToFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("Ticket_" + currentBooking.getBookingReference() + ".html"));
        int res = chooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            boolean ok = TicketGenerator.saveTicketAsFile(currentBooking, file);
            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Ticket saved successfully to:\n" + file.getAbsolutePath(),
                        "Ticket Saved",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save ticket file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

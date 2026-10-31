package moviebooking.ui;

import moviebooking.model.Booking;
import moviebooking.model.User;
import moviebooking.service.AuthenticationService;
import moviebooking.service.BookingService;
import moviebooking.util.UITheme;
import moviebooking.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Customer Booking History screen.
 * Allows viewing tickets, searching history, and cancelling bookings to release seats.
 */
public class MyBookingsPanel extends JPanel {

    private final HomeFrame parentFrame;
    private final BookingService bookingService = BookingService.getInstance();

    private final JTextField txtSearch;
    private final JTable tblBookings;
    private final DefaultTableModel tableModel;
    private List<Booking> currentList;

    public MyBookingsPanel(HomeFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_DARK);

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(24, 36, 24, 36));

        // Header and Search bar
        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setOpaque(false);

        JLabel lblTitle = new JLabel("🎟️ My Booking History");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);
        txtSearch = UITheme.createTextField(14);
        txtSearch.setToolTipText("Search by Movie or Booking ID");

        JButton btnSearch = UITheme.createSecondaryButton("Search");
        btnSearch.addActionListener(e -> filterBookings());

        JButton btnRefresh = UITheme.createSecondaryButton("🔄 Refresh");
        btnRefresh.addActionListener(e -> reloadBookings());

        searchPanel.add(new JLabel("Search: ") {{ setForeground(UITheme.TEXT_SECONDARY); }});
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);

        headerBar.add(lblTitle, BorderLayout.WEST);
        headerBar.add(searchPanel, BorderLayout.EAST);

        main.add(headerBar);
        main.add(Box.createVerticalStrut(20));

        // Bookings Table
        String[] columns = {"Booking Ref", "Movie", "Theatre & Screen", "Date & Time", "Seats", "Total Paid", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblBookings = new JTable(tableModel);
        UITheme.styleTable(tblBookings);
        tblBookings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(tblBookings);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true));

        main.add(scroll);
        main.add(Box.createVerticalStrut(18));

        // Action Buttons Row (View Ticket, Cancel Booking)
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        actionRow.setOpaque(false);

        JButton btnViewTicket = UITheme.createPrimaryButton("🎟️ View & Print Ticket");
        btnViewTicket.addActionListener(e -> {
            Booking selected = getSelectedBooking();
            if (selected != null) {
                parentFrame.showTicket(selected);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a booking from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton btnCancel = UITheme.createSecondaryButton("❌ Cancel Selected Booking");
        btnCancel.addActionListener(e -> cancelSelectedBooking());

        actionRow.add(btnCancel);
        actionRow.add(btnViewTicket);

        main.add(actionRow);

        add(main, BorderLayout.CENTER);
    }

    public void reloadBookings() {
        User user = AuthenticationService.getInstance().getCurrentUser();
        if (user == null) {
            tableModel.setRowCount(0);
            return;
        }

        currentList = bookingService.getUserBookings(user.getUserId());
        populateTable(currentList);
    }

    private void filterBookings() {
        if (currentList == null) return;
        String query = txtSearch.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            populateTable(currentList);
            return;
        }

        List<Booking> filtered = currentList.stream()
                .filter(b -> b.getBookingReference().toLowerCase().contains(query) ||
                             b.getMovieTitle().toLowerCase().contains(query) ||
                             b.getTheatreName().toLowerCase().contains(query))
                .collect(Collectors.toList());

        populateTable(filtered);
    }

    private void populateTable(List<Booking> list) {
        tableModel.setRowCount(0);
        if (list == null) return;

        for (Booking b : list) {
            tableModel.addRow(new Object[]{
                    b.getBookingReference(),
                    b.getMovieTitle(),
                    b.getTheatreName() + " (" + b.getScreenName() + ")",
                    b.getShowDate() + " @ " + b.getShowTime(),
                    b.getSeatLabelsFormatted(),
                    ValidationUtil.formatCurrency(b.getTotalAmount()),
                    b.getBookingStatus()
            });
        }
    }

    private Booking getSelectedBooking() {
        int row = tblBookings.getSelectedRow();
        if (row < 0 || currentList == null || row >= currentList.size()) return null;
        return currentList.get(row);
    }

    private void cancelSelectedBooking() {
        Booking selected = getSelectedBooking();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a booking to cancel.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selected.getBookingStatus() == Booking.Status.CANCELLED) {
            JOptionPane.showMessageDialog(this, "This booking has already been cancelled.", "Already Cancelled", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int conf = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel booking " + selected.getBookingReference() + "?\n" +
                "Movie: " + selected.getMovieTitle() + "\n" +
                "Seats: " + selected.getSeatLabelsFormatted() + "\n\n" +
                "The allocated seats will be released and marked available for other patrons.",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (conf == JOptionPane.YES_OPTION) {
            boolean ok = bookingService.cancelBooking(selected.getBookingId());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Booking cancelled successfully. Seats released and refund processed.", "Cancellation Completed", JOptionPane.INFORMATION_MESSAGE);
                reloadBookings();
            } else {
                JOptionPane.showMessageDialog(this, "Unable to cancel booking.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

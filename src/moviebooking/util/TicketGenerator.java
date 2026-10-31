package moviebooking.util;

import moviebooking.model.Booking;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Utility for rendering, saving, exporting, and printing cinema tickets.
 */
public class TicketGenerator {

    /**
     * Generates a modern HTML movie ticket.
     */
    public static String generateTicketHTML(Booking booking) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Movie Ticket - ").append(booking.getBookingReference()).append("</title>")
                .append("<style>")
                .append("body { font-family: 'Segoe UI', Arial, sans-serif; background-color: #0b0f19; color: #f8fafc; padding: 20px; }")
                .append(".ticket { max-width: 500px; margin: 0 auto; background: #182034; border: 1px solid #2a3652; border-radius: 12px; overflow: hidden; box-shadow: 0 10px 25px rgba(0,0,0,0.5); }")
                .append(".header { background: #e11d48; padding: 18px 24px; color: #ffffff; display: flex; justify-content: space-between; align-items: center; }")
                .append(".cinema-title { font-size: 20px; font-weight: bold; letter-spacing: 1px; }")
                .append(".ref-badge { font-size: 13px; font-weight: bold; background: rgba(0,0,0,0.25); padding: 4px 10px; border-radius: 6px; }")
                .append(".body { padding: 24px; }")
                .append(".movie-title { font-size: 22px; font-weight: bold; color: #ffffff; margin-bottom: 8px; }")
                .append(".theatre-info { font-size: 14px; color: #94a3b8; margin-bottom: 20px; }")
                .append(".meta-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 14px; margin-bottom: 20px; }")
                .append(".meta-item .label { font-size: 11px; color: #64748b; text-transform: uppercase; font-weight: bold; }")
                .append(".meta-item .value { font-size: 15px; color: #f8fafc; font-weight: 600; margin-top: 3px; }")
                .append(".seats-box { background: #0f172a; border: 1px dashed #e11d48; border-radius: 8px; padding: 12px 16px; margin-bottom: 20px; display: flex; justify-content: space-between; align-items: center; }")
                .append(".seats-list { font-size: 18px; font-weight: bold; color: #38bdf8; }")
                .append(".total-box { display: flex; justify-content: space-between; border-top: 1px solid #2a3652; padding-top: 14px; margin-top: 14px; font-size: 16px; }")
                .append(".total-val { font-size: 20px; font-weight: bold; color: #10b981; }")
                .append(".footer { background: #0f172a; padding: 16px 24px; text-align: center; border-top: 2px dashed #2a3652; font-size: 12px; color: #64748b; }")
                .append("</style></head><body>")
                .append("<div class='ticket'>")
                .append("<div class='header'><span class='cinema-title'>🎟️ BOOKMYSHOW CINEMA</span><span class='ref-badge'>").append(booking.getBookingReference()).append("</span></div>")
                .append("<div class='body'>")
                .append("<div class='movie-title'>").append(booking.getMovieTitle()).append("</div>")
                .append("<div class='theatre-info'>").append(booking.getTheatreName()).append(" | ").append(booking.getScreenName()).append("</div>")
                .append("<div class='meta-grid'>")
                .append("<div class='meta-item'><div class='label'>Date</div><div class='value'>").append(booking.getShowDate()).append("</div></div>")
                .append("<div class='meta-item'><div class='label'>Show Time</div><div class='value'>").append(booking.getShowTime()).append("</div></div>")
                .append("<div class='meta-item'><div class='label'>Customer</div><div class='value'>").append(booking.getCustomerName()).append("</div></div>")
                .append("<div class='meta-item'><div class='label'>Tickets</div><div class='value'>").append(booking.getTotalSeats()).append(" Tickets</div></div>")
                .append("</div>")
                .append("<div class='seats-box'><div><span style='color:#94a3b8; font-size:12px;'>CONFIRMED SEATS</span><div class='seats-list'>").append(booking.getSeatLabelsFormatted()).append("</div></div>")
                .append("<div style='text-align:right;'><span style='background:#10b981; color:#fff; font-size:11px; padding:3px 8px; border-radius:4px; font-weight:bold;'>PAID</span></div></div>")
                .append("<div class='total-box'><span>Grand Total (incl. GST):</span><span class='total-val'>").append(ValidationUtil.formatCurrency(booking.getTotalAmount())).append("</span></div>")
                .append("</div>")
                .append("<div class='footer'>Booking ID: ").append(booking.getBookingReference()).append(" &bull; Txn: ").append(booking.getTransactionId() != null ? booking.getTransactionId() : "N/A")
                .append("<br>Enjoy your show! Please arrive 15 minutes before showtime.</div>")
                .append("</div></body></html>");
        return sb.toString();
    }

    /**
     * Saves the ticket to a local file (.html or .txt).
     */
    public static boolean saveTicketAsFile(Booking booking, File targetFile) {
        try (FileWriter writer = new FileWriter(targetFile)) {
            if (targetFile.getName().toLowerCase().endsWith(".html")) {
                writer.write(generateTicketHTML(booking));
            } else {
                writer.write(generatePlainTextTicket(booking));
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Generates a clean text receipt of the ticket.
     */
    public static String generatePlainTextTicket(Booking booking) {
        StringBuilder sb = new StringBuilder();
        sb.append("===================================================\n");
        sb.append("            BOOKMYSHOW CINEMA TICKET               \n");
        sb.append("===================================================\n");
        sb.append("Booking Reference : ").append(booking.getBookingReference()).append("\n");
        sb.append("Booking Date      : ").append(booking.getFormattedBookingDate()).append("\n");
        sb.append("Customer Name     : ").append(booking.getCustomerName()).append("\n");
        sb.append("Customer Email    : ").append(booking.getCustomerEmail()).append("\n");
        sb.append("Customer Phone    : ").append(booking.getCustomerPhone()).append("\n");
        sb.append("---------------------------------------------------\n");
        sb.append("Movie Title       : ").append(booking.getMovieTitle()).append("\n");
        sb.append("Theatre           : ").append(booking.getTheatreName()).append("\n");
        sb.append("Screen            : ").append(booking.getScreenName()).append("\n");
        sb.append("Show Date & Time  : ").append(booking.getShowDate()).append(" @ ").append(booking.getShowTime()).append("\n");
        sb.append("Allocated Seats   : ").append(booking.getSeatLabelsFormatted()).append("\n");
        sb.append("Number of Tickets : ").append(booking.getTotalSeats()).append("\n");
        sb.append("---------------------------------------------------\n");
        sb.append("Base Ticket Price : ").append(ValidationUtil.formatCurrency(booking.getBaseAmount())).append("\n");
        sb.append("Convenience Fee   : ").append(ValidationUtil.formatCurrency(booking.getConvenienceFee())).append("\n");
        sb.append("GST (18%)         : ").append(ValidationUtil.formatCurrency(booking.getGstAmount())).append("\n");
        sb.append("Total Amount Paid : ").append(ValidationUtil.formatCurrency(booking.getTotalAmount())).append("\n");
        sb.append("Payment Method    : ").append(booking.getPaymentMethod()).append("\n");
        sb.append("Transaction ID    : ").append(booking.getTransactionId() != null ? booking.getTransactionId() : "N/A").append("\n");
        sb.append("Booking Status    : ").append(booking.getBookingStatus()).append("\n");
        sb.append("===================================================\n");
        sb.append("         Thank you for booking with us!            \n");
        sb.append("  Please show this ticket at the cinema entrance.  \n");
        sb.append("===================================================\n");
        return sb.toString();
    }

    /**
     * Prints the ticket directly via Java Print API / Windows Print Dialog.
     */
    public static void printTicket(Booking booking, Component parent) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Movie Ticket - " + booking.getBookingReference());

        job.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex > 0) return NO_SUCH_PAGE;

                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                int y = 40;
                g2d.setFont(new Font("Monospaced", Font.BOLD, 14));
                g2d.drawString("BOOKMYSHOW - CINEMA TICKET", 50, y); y += 22;
                g2d.drawLine(50, y, 450, y); y += 18;

                g2d.setFont(new Font("Monospaced", Font.PLAIN, 10));
                String[] lines = generatePlainTextTicket(booking).split("\n");
                for (String line : lines) {
                    g2d.drawString(line, 50, y);
                    y += 14;
                }
                return PAGE_EXISTS;
            }
        });

        if (job.printDialog()) {
            try {
                job.print();
                JOptionPane.showMessageDialog(parent, "Ticket sent to printer successfully!", "Print", JOptionPane.INFORMATION_MESSAGE);
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(parent, "Printing failed: " + e.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

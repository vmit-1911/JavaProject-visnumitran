package moviebooking.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Model representing a customer booking.
 */
public class Booking {
    public enum Status {
        CONFIRMED,
        CANCELLED
    }

    private int bookingId;
    private String bookingReference;
    private int userId;
    private int showId;
    private int totalSeats;
    private double baseAmount;
    private double convenienceFee;
    private double gstAmount;
    private double totalAmount;
    private Status bookingStatus = Status.CONFIRMED;
    private Timestamp bookingDate;

    // Display fields
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String movieTitle;
    private String moviePoster;
    private String theatreName;
    private String screenName;
    private String showDate;
    private String showTime;
    private List<String> seatLabels = new ArrayList<>();
    private String transactionId;
    private String paymentMethod;

    public Booking() {}

    public Booking(int bookingId, String bookingReference, int userId, int showId, int totalSeats,
                   double baseAmount, double convenienceFee, double gstAmount, double totalAmount,
                   Status bookingStatus, Timestamp bookingDate) {
        this.bookingId = bookingId;
        this.bookingReference = bookingReference;
        this.userId = userId;
        this.showId = showId;
        this.totalSeats = totalSeats;
        this.baseAmount = baseAmount;
        this.convenienceFee = convenienceFee;
        this.gstAmount = gstAmount;
        this.totalAmount = totalAmount;
        this.bookingStatus = bookingStatus;
        this.bookingDate = bookingDate;
    }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getShowId() { return showId; }
    public void setShowId(int showId) { this.showId = showId; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public double getBaseAmount() { return baseAmount; }
    public void setBaseAmount(double baseAmount) { this.baseAmount = baseAmount; }

    public double getConvenienceFee() { return convenienceFee; }
    public void setConvenienceFee(double convenienceFee) { this.convenienceFee = convenienceFee; }

    public double getGstAmount() { return gstAmount; }
    public void setGstAmount(double gstAmount) { this.gstAmount = gstAmount; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public Status getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(Status bookingStatus) { this.bookingStatus = bookingStatus; }

    public Timestamp getBookingDate() { return bookingDate; }
    public void setBookingDate(Timestamp bookingDate) { this.bookingDate = bookingDate; }

    public String getFormattedBookingDate() {
        if (bookingDate == null) return "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
            return sdf.format(bookingDate);
        } catch (Exception e) {
            return bookingDate.toString();
        }
    }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getMoviePoster() { return moviePoster; }
    public void setMoviePoster(String moviePoster) { this.moviePoster = moviePoster; }

    public String getTheatreName() { return theatreName; }
    public void setTheatreName(String theatreName) { this.theatreName = theatreName; }

    public String getScreenName() { return screenName; }
    public void setScreenName(String screenName) { this.screenName = screenName; }

    public String getShowDate() { return showDate; }
    public void setShowDate(String showDate) { this.showDate = showDate; }

    public String getShowTime() { return showTime; }
    public void setShowTime(String showTime) { this.showTime = showTime; }

    public List<String> getSeatLabels() { return seatLabels; }
    public void setSeatLabels(List<String> seatLabels) { this.seatLabels = seatLabels; }

    public String getSeatLabelsFormatted() {
        if (seatLabels == null || seatLabels.isEmpty()) return "None";
        return String.join(", ", seatLabels);
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}

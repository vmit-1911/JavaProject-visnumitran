package moviebooking.model;

import java.sql.Timestamp;

/**
 * Model representing a payment transaction.
 */
public class Payment {
    public enum Method {
        UPI("UPI (GPay / PhonePe / Paytm)"),
        CREDIT_CARD("Credit Card"),
        DEBIT_CARD("Debit Card"),
        NET_BANKING("Net Banking"),
        CASH_AT_COUNTER("Cash at Box Office");

        private final String displayName;
        Method(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    public enum Status {
        SUCCESS,
        FAILED,
        REFUNDED
    }

    private int paymentId;
    private int bookingId;
    private String transactionId;
    private Method paymentMethod;
    private double amount;
    private Status paymentStatus = Status.SUCCESS;
    private Timestamp paymentDate;

    public Payment() {}

    public Payment(int paymentId, int bookingId, String transactionId, Method paymentMethod, double amount, Status paymentStatus) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.transactionId = transactionId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
    }

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public Method getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(Method paymentMethod) { this.paymentMethod = paymentMethod; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public Status getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(Status paymentStatus) { this.paymentStatus = paymentStatus; }

    public Timestamp getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Timestamp paymentDate) { this.paymentDate = paymentDate; }
}

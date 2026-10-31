package moviebooking.model;

import java.util.Objects;

/**
 * Model representing an individual cinema seat.
 */
public class Seat {
    public enum Category {
        SILVER(150.0),
        GOLD(250.0),
        PREMIUM(350.0);

        private final double defaultPrice;
        Category(double defaultPrice) { this.defaultPrice = defaultPrice; }
        public double getDefaultPrice() { return defaultPrice; }
    }

    public enum Status {
        AVAILABLE,
        SELECTED,
        BOOKED
    }

    private int seatId;
    private int screenId;
    private String rowChar;
    private int seatNumber;
    private Category category;
    private double basePrice;
    private Status status = Status.AVAILABLE;

    public Seat() {}

    public Seat(int seatId, int screenId, String rowChar, int seatNumber, Category category, double basePrice) {
        this.seatId = seatId;
        this.screenId = screenId;
        this.rowChar = rowChar;
        this.seatNumber = seatNumber;
        this.category = category;
        this.basePrice = basePrice;
    }

    public int getSeatId() { return seatId; }
    public void setSeatId(int seatId) { this.seatId = seatId; }

    public int getScreenId() { return screenId; }
    public void setScreenId(int screenId) { this.screenId = screenId; }

    public String getRowChar() { return rowChar; }
    public void setRowChar(String rowChar) { this.rowChar = rowChar; }

    public int getSeatNumber() { return seatNumber; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }

    public String getSeatLabel() {
        return rowChar + seatNumber;
    }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public boolean isBooked() {
        return status == Status.BOOKED;
    }

    public boolean isSelected() {
        return status == Status.SELECTED;
    }

    public boolean isAvailable() {
        return status == Status.AVAILABLE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Seat)) return false;
        Seat seat = (Seat) o;
        if (seatId > 0 && seat.seatId > 0) return seatId == seat.seatId;
        return screenId == seat.screenId && getSeatLabel().equalsIgnoreCase(seat.getSeatLabel());
    }

    @Override
    public int hashCode() {
        return seatId > 0 ? Integer.hashCode(seatId) : Objects.hash(screenId, getSeatLabel());
    }

    @Override
    public String toString() {
        return getSeatLabel() + " (" + category + " - ₹" + (int)basePrice + ")";
    }
}

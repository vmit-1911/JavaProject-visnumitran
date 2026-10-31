package moviebooking.model;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;

/**
 * Model representing a Movie Show at a specific Screen, Date, and Time.
 */
public class Show {
    private int showId;
    private int movieId;
    private int screenId;
    private Date showDate;
    private Time startTime;
    private double ticketPrice;
    private int availableSeats;

    // Associated details for rich display
    private String movieTitle;
    private String theatreName;
    private String screenName;
    private String city;

    public Show() {}

    public Show(int showId, int movieId, int screenId, Date showDate, Time startTime, double ticketPrice, int availableSeats) {
        this.showId = showId;
        this.movieId = movieId;
        this.screenId = screenId;
        this.showDate = showDate;
        this.startTime = startTime;
        this.ticketPrice = ticketPrice;
        this.availableSeats = availableSeats;
    }

    public int getShowId() { return showId; }
    public void setShowId(int showId) { this.showId = showId; }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public int getScreenId() { return screenId; }
    public void setScreenId(int screenId) { this.screenId = screenId; }

    public Date getShowDate() { return showDate; }
    public void setShowDate(Date showDate) { this.showDate = showDate; }

    public Time getStartTime() { return startTime; }
    public void setStartTime(Time startTime) { this.startTime = startTime; }

    public String getFormattedTime() {
        if (startTime == null) return "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            return sdf.format(startTime);
        } catch (Exception e) {
            return startTime.toString();
        }
    }

    public String getFormattedDate() {
        if (showDate == null) return "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy");
            return sdf.format(showDate);
        } catch (Exception e) {
            return showDate.toString();
        }
    }

    public double getTicketPrice() { return ticketPrice; }
    public void setTicketPrice(double ticketPrice) { this.ticketPrice = ticketPrice; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getTheatreName() { return theatreName; }
    public void setTheatreName(String theatreName) { this.theatreName = theatreName; }

    public String getScreenName() { return screenName; }
    public void setScreenName(String screenName) { this.screenName = screenName; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    @Override
    public String toString() {
        return (theatreName != null ? theatreName + " | " : "") +
                getFormattedDate() + " at " + getFormattedTime() + " (₹" + (int)ticketPrice + ")";
    }
}

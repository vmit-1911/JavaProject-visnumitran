package moviebooking.model;

/**
 * Model representing an auditorium / screen within a theatre.
 */
public class Screen {
    private int screenId;
    private int theatreId;
    private int screenNumber;
    private String screenName;
    private int totalSeats;
    private String theatreName; // helper for display

    public Screen() {}

    public Screen(int screenId, int theatreId, int screenNumber, String screenName, int totalSeats) {
        this.screenId = screenId;
        this.theatreId = theatreId;
        this.screenNumber = screenNumber;
        this.screenName = screenName;
        this.totalSeats = totalSeats;
    }

    public int getScreenId() { return screenId; }
    public void setScreenId(int screenId) { this.screenId = screenId; }

    public int getTheatreId() { return theatreId; }
    public void setTheatreId(int theatreId) { this.theatreId = theatreId; }

    public int getScreenNumber() { return screenNumber; }
    public void setScreenNumber(int screenNumber) { this.screenNumber = screenNumber; }

    public String getScreenName() { return screenName; }
    public void setScreenName(String screenName) { this.screenName = screenName; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public String getTheatreName() { return theatreName; }
    public void setTheatreName(String theatreName) { this.theatreName = theatreName; }

    @Override
    public String toString() {
        return screenName != null && !screenName.isEmpty() ? screenName : ("Screen " + screenNumber);
    }
}

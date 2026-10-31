package moviebooking.model;

import java.sql.Timestamp;

/**
 * Model representing a Cinema Theatre.
 */
public class Theatre {
    private int theatreId;
    private String name;
    private String city;
    private String address;
    private int totalScreens;
    private Timestamp createdAt;

    public Theatre() {}

    public Theatre(int theatreId, String name, String city, String address, int totalScreens) {
        this.theatreId = theatreId;
        this.name = name;
        this.city = city;
        this.address = address;
        this.totalScreens = totalScreens;
    }

    public int getTheatreId() { return theatreId; }
    public void setTheatreId(int theatreId) { this.theatreId = theatreId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getTotalScreens() { return totalScreens; }
    public void setTotalScreens(int totalScreens) { this.totalScreens = totalScreens; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return name + " - " + city;
    }
}

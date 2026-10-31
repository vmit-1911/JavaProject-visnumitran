package moviebooking.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Model representing a Movie with details, genre, language, rating, and status.
 */
public class Movie {
    private int movieId;
    private String title;
    private String genre;
    private String language;
    private int durationMinutes;
    private Date releaseDate;
    private String certification;
    private String description;
    private double rating;
    private String posterPath;
    private String bannerPath;
    private String status; // NOW_SHOWING, UPCOMING
    private Timestamp createdAt;

    public Movie() {}

    public Movie(int movieId, String title, String genre, String language, int durationMinutes,
                 Date releaseDate, String certification, String description, double rating,
                 String posterPath, String bannerPath, String status) {
        this.movieId = movieId;
        this.title = title;
        this.genre = genre;
        this.language = language;
        this.durationMinutes = durationMinutes;
        this.releaseDate = releaseDate;
        this.certification = certification;
        this.description = description;
        this.rating = rating;
        this.posterPath = posterPath;
        this.bannerPath = bannerPath;
        this.status = status;
    }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getFormattedDuration() {
        int hours = durationMinutes / 60;
        int mins = durationMinutes % 60;
        return hours > 0 ? (hours + "h " + mins + "m") : (mins + "m");
    }

    public Date getReleaseDate() { return releaseDate; }
    public void setReleaseDate(Date releaseDate) { this.releaseDate = releaseDate; }

    public String getCertification() { return certification; }
    public void setCertification(String certification) { this.certification = certification; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getPosterPath() { return posterPath; }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }

    public String getBannerPath() { return bannerPath; }
    public void setBannerPath(String bannerPath) { this.bannerPath = bannerPath; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return title + " (" + language + ", " + certification + ")";
    }
}

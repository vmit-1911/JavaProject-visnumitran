package moviebooking.dao;

import moviebooking.model.Show;
import moviebooking.util.DBConnection;
import moviebooking.util.MockDatabaseStore;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Show management.
 */
public class ShowDAO {

    public List<Show> getAllShows() {
        if (DBConnection.isFallbackActive()) {
            return MockDatabaseStore.getInstance().getAllShows();
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return MockDatabaseStore.getInstance().getAllShows();
        }

        List<Show> list = new ArrayList<>();
        String sql = "SELECT s.*, m.title as movie_title, sc.screen_name, t.name as theatre_name, t.city " +
                     "FROM shows s " +
                     "JOIN movies m ON s.movie_id = m.movie_id " +
                     "JOIN screens sc ON s.screen_id = sc.screen_id " +
                     "JOIN theatres t ON sc.theatre_id = t.theatre_id " +
                     "ORDER BY s.show_date ASC, s.start_time ASC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapShow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ShowDAO] getAllShows error: " + e.getMessage());
            return MockDatabaseStore.getInstance().getAllShows();
        }
        return list;
    }

    public List<Show> getShowsForMovie(int movieId) {
        if (DBConnection.isFallbackActive()) {
            return MockDatabaseStore.getInstance().getShowsForMovie(movieId);
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return MockDatabaseStore.getInstance().getShowsForMovie(movieId);
        }

        List<Show> list = new ArrayList<>();
        String sql = "SELECT s.*, m.title as movie_title, sc.screen_name, t.name as theatre_name, t.city " +
                     "FROM shows s " +
                     "JOIN movies m ON s.movie_id = m.movie_id " +
                     "JOIN screens sc ON s.screen_id = sc.screen_id " +
                     "JOIN theatres t ON sc.theatre_id = t.theatre_id " +
                     "WHERE s.movie_id = ? " +
                     "ORDER BY s.show_date ASC, s.start_time ASC";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapShow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ShowDAO] getShowsForMovie error: " + e.getMessage());
            return MockDatabaseStore.getInstance().getShowsForMovie(movieId);
        }
        return list;
    }

    public Show getShowById(int showId) {
        if (DBConnection.isFallbackActive()) {
            return MockDatabaseStore.getInstance().getShowById(showId);
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return MockDatabaseStore.getInstance().getShowById(showId);
        }

        String sql = "SELECT s.*, m.title as movie_title, sc.screen_name, t.name as theatre_name, t.city " +
                     "FROM shows s " +
                     "JOIN movies m ON s.movie_id = m.movie_id " +
                     "JOIN screens sc ON s.screen_id = sc.screen_id " +
                     "JOIN theatres t ON sc.theatre_id = t.theatre_id " +
                     "WHERE s.show_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, showId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapShow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ShowDAO] getShowById error: " + e.getMessage());
            return MockDatabaseStore.getInstance().getShowById(showId);
        }
        return null;
    }

    public boolean addShow(Show show) {
        if (DBConnection.isFallbackActive()) {
            return MockDatabaseStore.getInstance().addShow(show) != null;
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return MockDatabaseStore.getInstance().addShow(show) != null;
        }

        String sql = "INSERT INTO shows (movie_id, screen_id, show_date, start_time, ticket_price, available_seats) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, show.getMovieId());
            pstmt.setInt(2, show.getScreenId());
            pstmt.setDate(3, show.getShowDate());
            pstmt.setTime(4, show.getStartTime());
            pstmt.setDouble(5, show.getTicketPrice());
            pstmt.setInt(6, show.getAvailableSeats());

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) show.setShowId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[ShowDAO] addShow error: " + e.getMessage());
        }
        return false;
    }

    public boolean updateShow(Show show) {
        if (DBConnection.isFallbackActive()) {
            return MockDatabaseStore.getInstance().updateShow(show);
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return MockDatabaseStore.getInstance().updateShow(show);
        }

        String sql = "UPDATE shows SET movie_id=?, screen_id=?, show_date=?, start_time=?, ticket_price=?, available_seats=? WHERE show_id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, show.getMovieId());
            pstmt.setInt(2, show.getScreenId());
            pstmt.setDate(3, show.getShowDate());
            pstmt.setTime(4, show.getStartTime());
            pstmt.setDouble(5, show.getTicketPrice());
            pstmt.setInt(6, show.getAvailableSeats());
            pstmt.setInt(7, show.getShowId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ShowDAO] updateShow error: " + e.getMessage());
            return MockDatabaseStore.getInstance().updateShow(show);
        }
    }

    public boolean deleteShow(int showId) {
        if (DBConnection.isFallbackActive()) {
            return MockDatabaseStore.getInstance().deleteShow(showId);
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return MockDatabaseStore.getInstance().deleteShow(showId);
        }

        String sql = "DELETE FROM shows WHERE show_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, showId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ShowDAO] deleteShow error: " + e.getMessage());
            return MockDatabaseStore.getInstance().deleteShow(showId);
        }
    }

    private Show mapShow(ResultSet rs) throws SQLException {
        Show s = new Show();
        s.setShowId(rs.getInt("show_id"));
        s.setMovieId(rs.getInt("movie_id"));
        s.setScreenId(rs.getInt("screen_id"));
        s.setShowDate(rs.getDate("show_date"));
        s.setStartTime(rs.getTime("start_time"));
        s.setTicketPrice(rs.getDouble("ticket_price"));
        s.setAvailableSeats(rs.getInt("available_seats"));
        s.setMovieTitle(rs.getString("movie_title"));
        s.setScreenName(rs.getString("screen_name"));
        s.setTheatreName(rs.getString("theatre_name"));
        s.setCity(rs.getString("city"));
        return s;
    }
}

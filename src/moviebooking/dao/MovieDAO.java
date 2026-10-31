package moviebooking.dao;

import moviebooking.model.Movie;
import moviebooking.util.DBConnection;
import moviebooking.util.MockDatabaseStore;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Access Object for Movie entity.
 */
public class MovieDAO {

    public List<Movie> getAllMovies() {
        if (DBConnection.isFallbackActive()) {
            return MockDatabaseStore.getInstance().getAllMovies();
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return MockDatabaseStore.getInstance().getAllMovies();
        }

        List<Movie> list = new ArrayList<>();
        String sql = "SELECT * FROM movies ORDER BY movie_id ASC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapMovie(rs));
            }
        } catch (SQLException e) {
            System.err.println("[MovieDAO] getAllMovies error: " + e.getMessage());
            return MockDatabaseStore.getInstance().getAllMovies();
        }
        return list;
    }

    public List<Movie> getMoviesByStatus(String status) {
        if (DBConnection.isFallbackActive()) {
            return MockDatabaseStore.getInstance().getMoviesByStatus(status);
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return MockDatabaseStore.getInstance().getMoviesByStatus(status);
        }

        List<Movie> list = new ArrayList<>();
        String sql = "SELECT * FROM movies WHERE status = ? ORDER BY rating DESC";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapMovie(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[MovieDAO] getMoviesByStatus error: " + e.getMessage());
            return MockDatabaseStore.getInstance().getMoviesByStatus(status);
        }
        return list;
    }

    public Movie getMovieById(int movieId) {
        if (DBConnection.isFallbackActive()) {
            return MockDatabaseStore.getInstance().getMovieById(movieId);
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return MockDatabaseStore.getInstance().getMovieById(movieId);
        }

        String sql = "SELECT * FROM movies WHERE movie_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapMovie(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[MovieDAO] getMovieById error: " + e.getMessage());
            return MockDatabaseStore.getInstance().getMovieById(movieId);
        }
        return null;
    }

    public boolean addMovie(Movie movie) {
        if (DBConnection.isFallbackActive()) {
            Movie added = MockDatabaseStore.getInstance().addMovie(movie);
            return added != null;
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            Movie added = MockDatabaseStore.getInstance().addMovie(movie);
            return added != null;
        }

        String sql = "INSERT INTO movies (title, genre, language, duration_minutes, release_date, certification, description, rating, poster_path, banner_path, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, movie.getTitle());
            pstmt.setString(2, movie.getGenre());
            pstmt.setString(3, movie.getLanguage());
            pstmt.setInt(4, movie.getDurationMinutes());
            pstmt.setDate(5, movie.getReleaseDate());
            pstmt.setString(6, movie.getCertification());
            pstmt.setString(7, movie.getDescription());
            pstmt.setDouble(8, movie.getRating());
            pstmt.setString(9, movie.getPosterPath());
            pstmt.setString(10, movie.getBannerPath());
            pstmt.setString(11, movie.getStatus());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        movie.setMovieId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[MovieDAO] addMovie error: " + e.getMessage());
        }
        return false;
    }

    public boolean updateMovie(Movie movie) {
        if (DBConnection.isFallbackActive()) {
            return MockDatabaseStore.getInstance().updateMovie(movie);
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return MockDatabaseStore.getInstance().updateMovie(movie);
        }

        String sql = "UPDATE movies SET title=?, genre=?, language=?, duration_minutes=?, release_date=?, certification=?, description=?, rating=?, poster_path=?, banner_path=?, status=? WHERE movie_id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, movie.getTitle());
            pstmt.setString(2, movie.getGenre());
            pstmt.setString(3, movie.getLanguage());
            pstmt.setInt(4, movie.getDurationMinutes());
            pstmt.setDate(5, movie.getReleaseDate());
            pstmt.setString(6, movie.getCertification());
            pstmt.setString(7, movie.getDescription());
            pstmt.setDouble(8, movie.getRating());
            pstmt.setString(9, movie.getPosterPath());
            pstmt.setString(10, movie.getBannerPath());
            pstmt.setString(11, movie.getStatus());
            pstmt.setInt(12, movie.getMovieId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MovieDAO] updateMovie error: " + e.getMessage());
            return MockDatabaseStore.getInstance().updateMovie(movie);
        }
    }

    public boolean deleteMovie(int movieId) {
        if (DBConnection.isFallbackActive()) {
            return MockDatabaseStore.getInstance().deleteMovie(movieId);
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return MockDatabaseStore.getInstance().deleteMovie(movieId);
        }

        String sql = "DELETE FROM movies WHERE movie_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MovieDAO] deleteMovie error: " + e.getMessage());
            return MockDatabaseStore.getInstance().deleteMovie(movieId);
        }
    }

    public List<Movie> searchMovies(String query, String genre, String language, String sortBy) {
        List<Movie> all = getAllMovies();
        return all.stream()
                .filter(m -> {
                    if (query != null && !query.trim().isEmpty()) {
                        String q = query.trim().toLowerCase();
                        boolean match = m.getTitle().toLowerCase().contains(q) ||
                                        m.getGenre().toLowerCase().contains(q) ||
                                        m.getLanguage().toLowerCase().contains(q);
                        if (!match) return false;
                    }
                    if (genre != null && !genre.equalsIgnoreCase("All Genres") && !genre.isEmpty()) {
                        if (!m.getGenre().toLowerCase().contains(genre.toLowerCase())) return false;
                    }
                    if (language != null && !language.equalsIgnoreCase("All Languages") && !language.isEmpty()) {
                        if (!m.getLanguage().equalsIgnoreCase(language)) return false;
                    }
                    return true;
                })
                .sorted((m1, m2) -> {
                    if ("rating".equalsIgnoreCase(sortBy)) {
                        return Double.compare(m2.getRating(), m1.getRating());
                    } else if ("release".equalsIgnoreCase(sortBy)) {
                        if (m1.getReleaseDate() != null && m2.getReleaseDate() != null) {
                            return m2.getReleaseDate().compareTo(m1.getReleaseDate());
                        }
                    }
                    return Integer.compare(m1.getMovieId(), m2.getMovieId());
                })
                .collect(Collectors.toList());
    }

    private Movie mapMovie(ResultSet rs) throws SQLException {
        Movie m = new Movie();
        m.setMovieId(rs.getInt("movie_id"));
        m.setTitle(rs.getString("title"));
        m.setGenre(rs.getString("genre"));
        m.setLanguage(rs.getString("language"));
        m.setDurationMinutes(rs.getInt("duration_minutes"));
        m.setReleaseDate(rs.getDate("release_date"));
        m.setCertification(rs.getString("certification"));
        m.setDescription(rs.getString("description"));
        m.setRating(rs.getDouble("rating"));
        m.setPosterPath(rs.getString("poster_path"));
        m.setBannerPath(rs.getString("banner_path"));
        m.setStatus(rs.getString("status"));
        m.setCreatedAt(rs.getTimestamp("created_at"));
        return m;
    }
}

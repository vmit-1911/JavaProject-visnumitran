package moviebooking.service;

import moviebooking.dao.MovieDAO;
import moviebooking.model.Movie;

import java.util.List;

/**
 * Service managing movie catalogue, search, filters, and admin operations.
 */
public class MovieService {

    private static final MovieService INSTANCE = new MovieService();
    private final MovieDAO movieDAO = new MovieDAO();

    public static MovieService getInstance() {
        return INSTANCE;
    }

    private MovieService() {}

    public List<Movie> getAllMovies() {
        return movieDAO.getAllMovies();
    }

    public List<Movie> getNowShowingMovies() {
        return movieDAO.getMoviesByStatus("NOW_SHOWING");
    }

    public List<Movie> getUpcomingMovies() {
        return movieDAO.getMoviesByStatus("UPCOMING");
    }

    public Movie getMovieById(int id) {
        return movieDAO.getMovieById(id);
    }

    public boolean addMovie(Movie movie) {
        return movieDAO.addMovie(movie);
    }

    public boolean updateMovie(Movie movie) {
        return movieDAO.updateMovie(movie);
    }

    public boolean deleteMovie(int movieId) {
        return movieDAO.deleteMovie(movieId);
    }

    public List<Movie> searchMovies(String query, String genre, String language, String sortBy) {
        return movieDAO.searchMovies(query, genre, language, sortBy);
    }
}

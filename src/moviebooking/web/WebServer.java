package moviebooking.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import moviebooking.dao.MovieDAO;
import moviebooking.dao.ShowDAO;
import moviebooking.dao.TheatreDAO;
import moviebooking.dao.UserDAO;
import moviebooking.model.*;
import moviebooking.service.BookingService;
import moviebooking.service.SeatService;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Built-in Lightweight HTTP Server providing browser access at http://localhost:8080.
 * Directly interacts with the MySQL database (movie_booking_db).
 */
public class WebServer {

    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        System.out.println("=================================================");
        System.out.println("🎬 BookMyShow Cinema Web Server Running!");
        System.out.println("🌐 Open in browser: http://localhost:" + PORT);
        System.out.println("=================================================");

        server.createContext("/", new StaticFileHandler());
        server.createContext("/api/movies", new MoviesApiHandler());
        server.createContext("/api/shows", new ShowsApiHandler());
        server.createContext("/api/seats", new SeatsApiHandler());
        server.createContext("/api/book", new BookingApiHandler());
        server.createContext("/api/bookings", new BookingsListApiHandler());

        server.setExecutor(null); // default executor
        server.start();

        // Automatically open default browser
        try {
            if (java.awt.Desktop.isDesktopSupported() && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                java.awt.Desktop.getDesktop().browse(new java.net.URI("http://localhost:" + PORT));
            }
        } catch (Exception ignored) {}
    }

    // Handler serving Web UI HTML/CSS/JS
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            File webFile = new File("web/index.html");
            if (!webFile.exists()) {
                String notFound = "<h1>Web UI not found.</h1>";
                sendResponse(exchange, 404, notFound, "text/html");
                return;
            }
            byte[] bytes = java.nio.file.Files.readAllBytes(webFile.toPath());
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    // Handler for Movies API
    static class MoviesApiHandler implements HttpHandler {
        private final MovieDAO movieDAO = new MovieDAO();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            List<Movie> list = movieDAO.getAllMovies();
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                Movie m = list.get(i);
                if (i > 0) json.append(",");
                json.append(String.format(Locale.US,
                        "{\"movieId\":%d,\"title\":\"%s\",\"genre\":\"%s\",\"language\":\"%s\",\"durationMinutes\":%d,\"certification\":\"%s\",\"rating\":%.1f,\"status\":\"%s\"}",
                        m.getMovieId(), escape(m.getTitle()), escape(m.getGenre()), escape(m.getLanguage()),
                        m.getDurationMinutes(), escape(m.getCertification()), m.getRating(), m.getStatus()));
            }
            json.append("]");
            sendResponse(exchange, 200, json.toString(), "application/json");
        }
    }

    // Handler for Shows API
    static class ShowsApiHandler implements HttpHandler {
        private final ShowDAO showDAO = new ShowDAO();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            int movieId = 1;
            if (query != null && query.contains("movieId=")) {
                try {
                    movieId = Integer.parseInt(query.split("movieId=")[1].split("&")[0]);
                } catch (Exception ignored) {}
            }
            List<Show> list = showDAO.getShowsForMovie(movieId);
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                Show s = list.get(i);
                if (i > 0) json.append(",");
                json.append(String.format(Locale.US,
                        "{\"showId\":%d,\"movieId\":%d,\"screenName\":\"%s\",\"theatreName\":\"%s\",\"city\":\"%s\",\"date\":\"%s\",\"time\":\"%s\",\"price\":%.2f,\"availableSeats\":%d}",
                        s.getShowId(), s.getMovieId(), escape(s.getScreenName()), escape(s.getTheatreName()),
                        escape(s.getCity()), s.getFormattedDate(), s.getFormattedTime(), s.getTicketPrice(), s.getAvailableSeats()));
            }
            json.append("]");
            sendResponse(exchange, 200, json.toString(), "application/json");
        }
    }

    // Handler for Seats API
    static class SeatsApiHandler implements HttpHandler {
        private final ShowDAO showDAO = new ShowDAO();
        private final SeatService seatService = SeatService.getInstance();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            int showId = 1;
            if (query != null && query.contains("showId=")) {
                try {
                    showId = Integer.parseInt(query.split("showId=")[1].split("&")[0]);
                } catch (Exception ignored) {}
            }
            Show show = showDAO.getShowById(showId);
            if (show == null) {
                sendResponse(exchange, 404, "[]", "application/json");
                return;
            }
            List<Seat> seats = seatService.getSeatsForShow(show);
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < seats.size(); i++) {
                Seat s = seats.get(i);
                if (i > 0) json.append(",");
                json.append(String.format(Locale.US,
                        "{\"seatId\":%d,\"row\":\"%s\",\"number\":%d,\"category\":\"%s\",\"price\":%.2f,\"status\":\"%s\"}",
                        s.getSeatId(), s.getRowChar(), s.getSeatNumber(), s.getCategory().name(), s.getBasePrice(), s.getStatus().name()));
            }
            json.append("]");
            sendResponse(exchange, 200, json.toString(), "application/json");
        }
    }

    // Handler for Booking API (POST)
    static class BookingApiHandler implements HttpHandler {
        private final ShowDAO showDAO = new ShowDAO();
        private final TheatreDAO theatreDAO = new TheatreDAO();
        private final BookingService bookingService = BookingService.getInstance();
        private final UserDAO userDAO = new UserDAO();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Method not allowed", "text/plain");
                return;
            }

            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) body.append(line);

            // Parse simple params showId=1&seatIds=1,2&method=UPI
            Map<String, String> params = parseFormData(body.toString());
            try {
                int showId = Integer.parseInt(params.getOrDefault("showId", "1"));
                String seatIdsStr = params.getOrDefault("seatIds", "");
                String paymentMethod = params.getOrDefault("method", "UPI");

                Show show = showDAO.getShowById(showId);
                List<Seat> screenSeats = theatreDAO.getSeatsForScreen(show.getScreenId());

                List<Integer> targetSeatIds = Arrays.stream(seatIdsStr.split(","))
                        .map(String::trim).map(Integer::parseInt).collect(Collectors.toList());

                List<Seat> chosen = screenSeats.stream()
                        .filter(s -> targetSeatIds.contains(s.getSeatId()))
                        .collect(Collectors.toList());

                User user = userDAO.findById(1); // Default user for web demo
                if (user == null) user = new User(1, "Mitran Sharma", "mitran@example.com", "9876543210", "password123");

                String txnId = "TXN-WEB-" + System.currentTimeMillis() % 1000000;
                Booking b = bookingService.processBooking(user, show, chosen, paymentMethod, txnId);

                String json = String.format(Locale.US,
                        "{\"success\":true,\"bookingReference\":\"%s\",\"totalAmount\":%.2f,\"seats\":\"%s\"}",
                        b.getBookingReference(), b.getTotalAmount(), b.getSeatLabelsFormatted());
                sendResponse(exchange, 200, json, "application/json");
            } catch (Exception e) {
                String err = "{\"success\":false,\"error\":\"" + escape(e.getMessage()) + "\"}";
                sendResponse(exchange, 400, err, "application/json");
            }
        }
    }

    // Handler for All Bookings List
    static class BookingsListApiHandler implements HttpHandler {
        private final BookingService bookingService = BookingService.getInstance();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            List<Booking> list = bookingService.getAllBookings();
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                Booking b = list.get(i);
                if (i > 0) json.append(",");
                json.append(String.format(Locale.US,
                        "{\"bookingReference\":\"%s\",\"movieTitle\":\"%s\",\"theatreName\":\"%s\",\"screenName\":\"%s\",\"date\":\"%s\",\"time\":\"%s\",\"seats\":\"%s\",\"total\":%.2f,\"status\":\"%s\"}",
                        b.getBookingReference(), escape(b.getMovieTitle()), escape(b.getTheatreName()),
                        escape(b.getScreenName()), b.getShowDate(), b.getShowTime(), b.getSeatLabelsFormatted(), b.getTotalAmount(), b.getBookingStatus().name()));
            }
            json.append("]");
            sendResponse(exchange, 200, json.toString(), "application/json");
        }
    }

    private static Map<String, String> parseFormData(String formData) {
        Map<String, String> map = new HashMap<>();
        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                try {
                    map.put(URLDecoder.decode(kv[0], "UTF-8"), URLDecoder.decode(kv[1], "UTF-8"));
                } catch (Exception ignored) {}
            }
        }
        return map;
    }

    private static void sendResponse(HttpExchange exchange, int code, String response, String contentType) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }
}

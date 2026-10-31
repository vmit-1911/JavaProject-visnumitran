package moviebooking.service;

import moviebooking.dao.TheatreDAO;
import moviebooking.model.Seat;
import moviebooking.model.Show;

import java.util.List;
import java.util.Set;

/**
 * Service managing seat inventory, real-time show status, and dynamic pricing calculations.
 */
public class SeatService {

    private static final SeatService INSTANCE = new SeatService();
    private final TheatreDAO theatreDAO = new TheatreDAO();

    public static SeatService getInstance() {
        return INSTANCE;
    }

    private SeatService() {}

    /**
     * Loads all seats for a show's screen and populates status (BOOKED vs AVAILABLE).
     */
    public List<Seat> getSeatsForShow(Show show) {
        List<Seat> seats = theatreDAO.getSeatsForScreen(show.getScreenId());
        Set<Integer> bookedIds = theatreDAO.getBookedSeatIdsForShow(show.getShowId());

        for (Seat s : seats) {
            if (bookedIds.contains(s.getSeatId())) {
                s.setStatus(Seat.Status.BOOKED);
            } else {
                s.setStatus(Seat.Status.AVAILABLE);
            }
        }
        return seats;
    }

    public double calculateBaseAmount(List<Seat> selectedSeats) {
        if (selectedSeats == null || selectedSeats.isEmpty()) return 0.0;
        return selectedSeats.stream().mapToDouble(Seat::getBasePrice).sum();
    }

    public double calculateConvenienceFee(List<Seat> selectedSeats) {
        if (selectedSeats == null || selectedSeats.isEmpty()) return 0.0;
        return selectedSeats.size() * 30.00; // ₹30 per ticket
    }

    public double calculateGst(double subtotalWithFee) {
        return subtotalWithFee * 0.18; // 18% GST standard
    }

    public double calculateGrandTotal(List<Seat> selectedSeats) {
        double base = calculateBaseAmount(selectedSeats);
        double fee = calculateConvenienceFee(selectedSeats);
        double gst = calculateGst(base + fee);
        return base + fee + gst;
    }
}

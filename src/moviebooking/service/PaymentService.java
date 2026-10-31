package moviebooking.service;

import moviebooking.dao.PaymentDAO;
import moviebooking.model.Payment;

import java.util.List;

/**
 * Service managing simulated payment processing without contacting external gateways.
 * Never stores raw card numbers or sensitive CVVs.
 */
public class PaymentService {

    private static final PaymentService INSTANCE = new PaymentService();
    private final PaymentDAO paymentDAO = new PaymentDAO();

    public static PaymentService getInstance() {
        return INSTANCE;
    }

    private PaymentService() {}

    public String generateTransactionId(String method) {
        String prefix = "TXN-";
        if (method != null && method.contains("UPI")) prefix += "UPI-";
        else if (method != null && method.contains("CREDIT")) prefix += "CC-";
        else if (method != null && method.contains("DEBIT")) prefix += "DC-";
        else if (method != null && method.contains("NET")) prefix += "NB-";
        else prefix += "CASH-";

        return prefix + System.currentTimeMillis() % 100000000;
    }

    /**
     * Simulates payment gateway processing.
     */
    public boolean processPayment(String method, double amount, String inputIdentifier) {
        if (amount <= 0) return false;
        // In simulation mode, accept any non-empty input or default counter
        return true;
    }

    public List<Payment> getAllPayments() {
        return paymentDAO.getAllPayments();
    }
}

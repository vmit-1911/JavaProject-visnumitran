package moviebooking.dao;

import moviebooking.model.Payment;
import moviebooking.util.DBConnection;
import moviebooking.util.MockDatabaseStore;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Payment logs.
 */
public class PaymentDAO {

    public List<Payment> getAllPayments() {
        if (DBConnection.isFallbackActive()) {
            return MockDatabaseStore.getInstance().getAllPayments();
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return MockDatabaseStore.getInstance().getAllPayments();
        }

        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payments ORDER BY payment_id DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Payment p = new Payment();
                p.setPaymentId(rs.getInt("payment_id"));
                p.setBookingId(rs.getInt("booking_id"));
                p.setTransactionId(rs.getString("transaction_id"));
                p.setPaymentMethod(Payment.Method.valueOf(rs.getString("payment_method")));
                p.setAmount(rs.getDouble("amount"));
                p.setPaymentStatus(Payment.Status.valueOf(rs.getString("payment_status")));
                p.setPaymentDate(rs.getTimestamp("payment_date"));
                list.add(p);
            }
        } catch (SQLException e) {
            System.err.println("[PaymentDAO] getAllPayments error: " + e.getMessage());
            return MockDatabaseStore.getInstance().getAllPayments();
        }
        return list;
    }
}

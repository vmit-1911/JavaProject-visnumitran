package moviebooking.dao;

import moviebooking.model.Admin;
import moviebooking.model.User;
import moviebooking.util.DBConnection;
import moviebooking.util.MockDatabaseStore;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User and Admin entities.
 * Supports MySQL JDBC PreparedStatement with seamless in-memory fallback.
 */
public class UserDAO {

    public User findByEmail(String email) {
        if (DBConnection.isFallbackActive()) {
            return MockDatabaseStore.getInstance().findUserByEmail(email);
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return MockDatabaseStore.getInstance().findUserByEmail(email);
        }

        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] findByEmail error: " + e.getMessage());
            return MockDatabaseStore.getInstance().findUserByEmail(email);
        }
        return null;
    }

    public User findById(int userId) {
        if (DBConnection.isFallbackActive()) {
            return MockDatabaseStore.getInstance().findUserById(userId);
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return MockDatabaseStore.getInstance().findUserById(userId);
        }

        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] findById error: " + e.getMessage());
            return MockDatabaseStore.getInstance().findUserById(userId);
        }
        return null;
    }

    public boolean registerUser(User user) {
        if (DBConnection.isFallbackActive()) {
            User registered = MockDatabaseStore.getInstance().registerUser(
                    user.getName(), user.getEmail(), user.getPhone(), user.getPassword()
            );
            if (registered != null) {
                user.setUserId(registered.getUserId());
                return true;
            }
            return false;
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            User registered = MockDatabaseStore.getInstance().registerUser(
                    user.getName(), user.getEmail(), user.getPhone(), user.getPassword()
            );
            return registered != null;
        }

        String sql = "INSERT INTO users (name, email, phone, password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail().trim());
            pstmt.setString(3, user.getPhone().trim());
            pstmt.setString(4, user.getPassword());

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setUserId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] registerUser error: " + e.getMessage());
        }
        return false;
    }

    public Admin findAdminByUsername(String username) {
        if (DBConnection.isFallbackActive()) {
            return MockDatabaseStore.getInstance().findAdminByUsername(username);
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return MockDatabaseStore.getInstance().findAdminByUsername(username);
        }

        String sql = "SELECT * FROM admins WHERE username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Admin admin = new Admin();
                    admin.setAdminId(rs.getInt("admin_id"));
                    admin.setUsername(rs.getString("username"));
                    admin.setName(rs.getString("name"));
                    admin.setEmail(rs.getString("email"));
                    admin.setPassword(rs.getString("password"));
                    admin.setRole(rs.getString("role"));
                    admin.setCreatedAt(rs.getTimestamp("created_at"));
                    return admin;
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] findAdmin error: " + e.getMessage());
            return MockDatabaseStore.getInstance().findAdminByUsername(username);
        }
        return null;
    }

    public List<User> getAllUsers() {
        if (DBConnection.isFallbackActive()) {
            return MockDatabaseStore.getInstance().getAllUsers();
        }

        List<User> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return MockDatabaseStore.getInstance().getAllUsers();
        }

        String sql = "SELECT * FROM users ORDER BY user_id DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] getAllUsers error: " + e.getMessage());
            return MockDatabaseStore.getInstance().getAllUsers();
        }
        return list;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setPassword(rs.getString("password"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        return u;
    }
}

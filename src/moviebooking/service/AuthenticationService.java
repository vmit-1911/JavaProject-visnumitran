package moviebooking.service;

import moviebooking.dao.UserDAO;
import moviebooking.model.Admin;
import moviebooking.model.User;
import moviebooking.util.ValidationUtil;

/**
 * Service handling customer and administrator authentication and session management.
 */
public class AuthenticationService {

    private static final AuthenticationService INSTANCE = new AuthenticationService();
    private final UserDAO userDAO = new UserDAO();

    private User currentUser;
    private Admin currentAdmin;

    public static AuthenticationService getInstance() {
        return INSTANCE;
    }

    private AuthenticationService() {}

    public User loginUser(String email, String password) throws Exception {
        if (!ValidationUtil.isNotEmpty(email) || !ValidationUtil.isNotEmpty(password)) {
            throw new IllegalArgumentException("Email and password cannot be empty.");
        }

        User user = userDAO.findByEmail(email.trim());
        if (user == null || !user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        this.currentUser = user;
        this.currentAdmin = null;
        return user;
    }

    public User registerUser(String name, String email, String phone, String password, String confirmPassword) throws Exception {
        if (!ValidationUtil.isNotEmpty(name)) {
            throw new IllegalArgumentException("Full name is required.");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Please enter a valid email address (e.g., user@example.com).");
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            throw new IllegalArgumentException("Please enter a valid 10-digit mobile number.");
        }
        if (!ValidationUtil.isValidPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        if (userDAO.findByEmail(email) != null) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        User newUser = new User(name.trim(), email.trim(), phone.trim(), password);
        boolean success = userDAO.registerUser(newUser);
        if (!success) {
            throw new RuntimeException("Registration failed. Please check database connection.");
        }

        this.currentUser = newUser;
        return newUser;
    }

    public Admin loginAdmin(String username, String password) throws Exception {
        if (!ValidationUtil.isNotEmpty(username) || !ValidationUtil.isNotEmpty(password)) {
            throw new IllegalArgumentException("Username and password cannot be empty.");
        }

        Admin admin = userDAO.findAdminByUsername(username.trim());
        if (admin == null || !admin.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid admin credentials.");
        }

        this.currentAdmin = admin;
        this.currentUser = null;
        return admin;
    }

    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User user) { this.currentUser = user; }

    public Admin getCurrentAdmin() { return currentAdmin; }
    public void setCurrentAdmin(Admin admin) { this.currentAdmin = admin; }

    public boolean isLoggedIn() {
        return currentUser != null || currentAdmin != null;
    }

    public boolean isAdmin() {
        return currentAdmin != null;
    }

    public void logout() {
        this.currentUser = null;
        this.currentAdmin = null;
    }
}

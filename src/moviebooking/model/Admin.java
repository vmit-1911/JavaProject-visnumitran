package moviebooking.model;

import java.sql.Timestamp;

/**
 * Model representing an administrative user in the Movie Ticket Booking System.
 */
public class Admin {
    private int adminId;
    private String username;
    private String name;
    private String email;
    private String password;
    private String role;
    private Timestamp createdAt;

    public Admin() {}

    public Admin(int adminId, String username, String name, String email, String password, String role) {
        this.adminId = adminId;
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public int getAdminId() { return adminId; }
    public void setAdminId(int adminId) { this.adminId = adminId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Admin{" +
                "adminId=" + adminId +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}

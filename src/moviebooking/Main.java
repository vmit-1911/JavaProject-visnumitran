package moviebooking;

import moviebooking.ui.LoginFrame;
import moviebooking.util.DBConnection;

import javax.swing.*;

/**
 * Application Entry Point.
 * Sets modern desktop look-and-feel, initializes database, and launches LoginFrame.
 */
public class Main {

    public static void main(String[] args) {
        // Enable anti-aliased font rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Initialize Database asynchronously / check connectivity
        new Thread(() -> {
            try {
                System.out.println("[Main] Connecting and verifying database tables...");
                DBConnection.initializeDatabase();
            } catch (Exception e) {
                System.err.println("[Main] Database check notice: " + e.getMessage());
            }
        }).start();

        // Launch UI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}

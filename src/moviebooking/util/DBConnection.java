package moviebooking.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Database Connection Manager.
 * Connects to MySQL using JDBC, initializes schema/seed data,
 * and provides safe fallback mode if MySQL is not currently configured.
 */
public class DBConnection {

    private static String url = "jdbc:mysql://localhost:3306/movie_booking_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String user = "root";
    private static String password = "root";
    private static boolean enableFallback = true;
    private static boolean fallbackActive = false;
    private static Connection connection = null;

    static {
        loadProperties();
    }

    private static void loadProperties() {
        File propFile = new File("db.properties");
        if (propFile.exists()) {
            try (FileInputStream fis = new FileInputStream(propFile)) {
                Properties props = new Properties();
                props.load(fis);
                if (props.getProperty("db.url") != null) url = props.getProperty("db.url").trim();
                if (props.getProperty("db.user") != null) user = props.getProperty("db.user").trim();
                if (props.getProperty("db.password") != null) password = props.getProperty("db.password").trim();
                if (props.getProperty("db.enableFallback") != null) {
                    enableFallback = Boolean.parseBoolean(props.getProperty("db.enableFallback").trim());
                }
            } catch (Exception e) {
                System.err.println("[DBConnection] Warning: Could not read db.properties: " + e.getMessage());
            }
        }
    }

    /**
     * Obtains a live JDBC Connection to MySQL.
     * If MySQL is unreachable and fallback is enabled, sets fallbackActive to true.
     */
    public static synchronized Connection getConnection() {
        if (fallbackActive) {
            return null;
        }
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
            fallbackActive = false;
            System.out.println("[DBConnection] Successfully connected to MySQL database!");
            return connection;
        } catch (Exception e) {
            System.err.println("[DBConnection] MySQL Connection notice: " + e.getMessage());
            if (enableFallback) {
                System.out.println("[DBConnection] Fallback in-memory database activated for seamless operation.");
                fallbackActive = true;
            }
            return null;
        }
    }

    public static boolean isFallbackActive() {
        if (connection == null) {
            getConnection();
        }
        return fallbackActive;
    }

    public static void setFallbackActive(boolean active) {
        fallbackActive = active;
    }

    public static String getUrl() { return url; }
    public static String getUser() { return user; }

    /**
     * Initializes MySQL database tables and seed data from SQL files.
     */
    public static boolean initializeDatabase() {
        Connection conn = getConnection();
        if (conn == null) return false;

        try {
            // Check if users table exists
            Statement checkStmt = conn.createStatement();
            ResultSet rs = checkStmt.executeQuery("SHOW TABLES LIKE 'movies'");
            boolean tablesExist = rs.next();
            rs.close();
            checkStmt.close();

            if (!tablesExist) {
                System.out.println("[DBConnection] Tables not found. Initializing schema.sql...");
                executeSqlScript(conn, "src/database/schema.sql");
                System.out.println("[DBConnection] Seeding sample data...");
                executeSqlScript(conn, "src/database/sample_data.sql");
            }
            return true;
        } catch (SQLException e) {
            System.err.println("[DBConnection] Database init error: " + e.getMessage());
            return false;
        }
    }

    private static void executeSqlScript(Connection conn, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("[DBConnection] SQL file not found: " + filePath);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
             Statement stmt = conn.createStatement()) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--") || line.startsWith("/*")) {
                    continue;
                }
                sb.append(line).append(" ");
                if (line.endsWith(";")) {
                    String sql = sb.toString().trim();
                    sql = sql.substring(0, sql.length() - 1); // remove semicolon
                    try {
                        stmt.execute(sql);
                    } catch (SQLException ex) {
                        // ignore drop if not exists
                    }
                    sb.setLength(0);
                }
            }
        } catch (Exception e) {
            System.err.println("[DBConnection] Error executing " + filePath + ": " + e.getMessage());
        }
    }

    /**
     * Closes the active connection cleanly.
     */
    public static synchronized void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

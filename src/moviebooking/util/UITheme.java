package moviebooking.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * Modern Cinema Design System inspired by BookMyShow.
 * Features dark aesthetic, curated typography, crimson accents, and custom components.
 */
public class UITheme {

    // Color Palette
    public static final Color BG_DARK = new Color(11, 15, 25);         // Deep space dark
    public static final Color BG_CARD = new Color(24, 32, 52);         // Card container
    public static final Color BG_CARD_HOVER = new Color(34, 45, 74);   // Hover card
    public static final Color BG_INPUT = new Color(18, 24, 40);        // Input fields
    public static final Color BG_NAV = new Color(15, 23, 42);          // Navigation bar
    public static final Color BORDER_COLOR = new Color(42, 54, 82);    // Border subtle

    public static final Color PRIMARY_RED = new Color(225, 29, 72);    // BookMyShow Crimson Red
    public static final Color PRIMARY_HOVER = new Color(244, 63, 94);  // Lighter Crimson
    public static final Color ACCENT_GOLD = new Color(245, 158, 11);   // Gold class / Ratings

    // Seat Colors
    public static final Color SEAT_AVAILABLE = new Color(16, 185, 129); // Emerald Green
    public static final Color SEAT_SELECTED = new Color(59, 130, 246);  // Dodger Blue
    public static final Color SEAT_BOOKED = new Color(239, 68, 68);     // Rose Red
    public static final Color SEAT_PREMIUM = new Color(217, 119, 6);   // Amber

    // Text Colors
    public static final Color TEXT_PRIMARY = new Color(248, 250, 252); // White
    public static final Color TEXT_SECONDARY = new Color(148, 163, 184);// Slate Muted
    public static final Color TEXT_MUTED = new Color(100, 116, 139);   // Dim

    // Typography
    public static final Font FONT_LOGO = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    /**
     * Creates a rounded styled primary crimson button.
     */
    public static JButton createPrimaryButton(String text) {
        return createButton(text, PRIMARY_RED, PRIMARY_HOVER, Color.WHITE);
    }

    /**
     * Creates a rounded secondary/outline button.
     */
    public static JButton createSecondaryButton(String text) {
        return createButton(text, BG_CARD, BG_CARD_HOVER, TEXT_PRIMARY);
    }

    /**
     * Creates a generic styled rounded button with hover effects.
     */
    public static JButton createButton(String text, Color normalBg, Color hoverBg, Color textColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(hoverBg.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(hoverBg);
                } else {
                    g2.setColor(normalBg);
                }
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BODY_BOLD);
        btn.setForeground(textColor);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBorder(new EmptyBorder(9, 18, 9, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Creates a rounded modern text input field.
     */
    public static JTextField createTextField(int columns) {
        JTextField tf = new JTextField(columns) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_INPUT);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(BORDER_COLOR);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tf.setOpaque(false);
        tf.setBackground(BG_INPUT);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(TEXT_PRIMARY);
        tf.setFont(FONT_BODY);
        tf.setBorder(new EmptyBorder(8, 12, 8, 12));
        return tf;
    }

    /**
     * Creates a rounded modern password input field.
     */
    public static JPasswordField createPasswordField(int columns) {
        JPasswordField pf = new JPasswordField(columns) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_INPUT);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(BORDER_COLOR);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pf.setOpaque(false);
        pf.setBackground(BG_INPUT);
        pf.setForeground(TEXT_PRIMARY);
        pf.setCaretColor(TEXT_PRIMARY);
        pf.setFont(FONT_BODY);
        pf.setBorder(new EmptyBorder(8, 12, 8, 12));
        return pf;
    }

    /**
     * Creates a rounded card panel with soft border.
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(BORDER_COLOR);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        return panel;
    }

    /**
     * Applies modern dark styling to a JTable.
     */
    public static void styleTable(JTable table) {
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_PRIMARY);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(PRIMARY_RED);
        table.setSelectionForeground(Color.WHITE);
        table.setRowHeight(36);
        table.setFont(FONT_BODY);
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setBackground(BG_NAV);
        header.setForeground(TEXT_SECONDARY);
        header.setFont(FONT_SECTION);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBackground(BG_CARD);
        centerRenderer.setForeground(TEXT_PRIMARY);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    /**
     * Generates a high-resolution, cinema-grade movie poster graphic in memory.
     * This avoids broken image paths and gives a clean, commercial visual quality.
     */
    public static ImageIcon generatePosterImage(String title, String genre, double rating, int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        // Gradient Background based on title hash for distinct visual themes
        int hash = Math.abs(title.hashCode());
        Color topColor = new Color((hash % 80) + 10, ((hash / 10) % 60) + 15, ((hash / 100) % 90) + 40);
        Color bottomColor = new Color(8, 12, 22);

        GradientPaint gp = new GradientPaint(0, 0, topColor, 0, height, bottomColor);
        g2.setPaint(gp);
        g2.fillRect(0, 0, width, height);

        // Cinema Decorative geometric art
        g2.setColor(new Color(255, 255, 255, 12));
        g2.fillOval(-width / 4, -height / 4, width, height / 2);
        g2.setColor(new Color(225, 29, 72, 35));
        g2.fillOval(width / 3, height / 3, width, height);

        // Top Accent Film Tag
        g2.setColor(PRIMARY_RED);
        g2.fillRoundRect(12, 12, 42, 22, 6, 6);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g2.drawString("CINEMA", 16, 27);

        // Rating Star Badge
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(width - 64, 12, 52, 22, 6, 6);
        g2.setColor(ACCENT_GOLD);
        g2.drawString("★ " + String.format("%.1f", rating), width - 58, 27);

        // Center Movie Icon Graphic
        g2.setColor(new Color(255, 255, 255, 40));
        g2.drawRoundRect(width / 2 - 35, height / 2 - 55, 70, 70, 14, 14);
        g2.setColor(PRIMARY_RED);
        int[] xPoints = {width / 2 - 12, width / 2 - 12, width / 2 + 16};
        int[] yPoints = {height / 2 - 35, height / 2 - 5, height / 2 - 20};
        g2.fillPolygon(xPoints, yPoints, 3);

        // Title and Genre at bottom with darkened scrim
        GradientPaint scrim = new GradientPaint(0, height - 90, new Color(0, 0, 0, 0), 0, height, new Color(5, 8, 15, 240));
        g2.setPaint(scrim);
        g2.fillRect(0, height - 90, width, 90);

        // Movie Title
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, Math.max(12, width / 14)));
        FontMetrics fm = g2.getFontMetrics();
        String displayTitle = title;
        if (fm.stringWidth(displayTitle) > width - 20) {
            displayTitle = title.substring(0, Math.min(title.length(), 16)) + "...";
        }
        g2.drawString(displayTitle, 12, height - 38);

        // Genre
        g2.setColor(TEXT_SECONDARY);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        String displayGenre = genre.length() > 24 ? genre.substring(0, 22) + "..." : genre;
        g2.drawString(displayGenre, 12, height - 18);

        // Outer Border
        g2.setColor(BORDER_COLOR);
        g2.drawRoundRect(0, 0, width - 1, height - 1, 10, 10);

        g2.dispose();
        return new ImageIcon(img);
    }
}

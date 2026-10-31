package moviebooking.ui;

import moviebooking.model.Admin;
import moviebooking.model.User;
import moviebooking.service.AuthenticationService;
import moviebooking.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Authentication screen featuring modern BookMyShow dark theme,
 * User login, Admin login toggle, and validation.
 */
public class LoginFrame extends JFrame {

    private final JTextField txtUsernameOrEmail;
    private final JPasswordField txtPassword;
    private final JCheckBox chkShowPassword;
    private final JRadioButton rbUser;
    private final JRadioButton rbAdmin;
    private final JLabel lblStatus;

    public LoginFrame() {
        setTitle("BookMyShow Cinema - Login");
        setSize(460, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_DARK);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(36, 40, 36, 40));

        // Header Logo & Branding
        JLabel lblLogo = new JLabel("🎬 BookMyShow", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblLogo.setForeground(UITheme.PRIMARY_RED);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTagline = new JLabel("Cinematic Experiences Await You", SwingConstants.CENTER);
        lblTagline.setFont(UITheme.FONT_BODY);
        lblTagline.setForeground(UITheme.TEXT_SECONDARY);
        lblTagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Role Selector Radio Buttons
        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        rolePanel.setOpaque(false);
        rbUser = new JRadioButton("User Login", true);
        rbAdmin = new JRadioButton("Admin Login", false);
        ButtonGroup bgRole = new ButtonGroup();
        bgRole.add(rbUser);
        bgRole.add(rbAdmin);

        for (JRadioButton rb : new JRadioButton[]{rbUser, rbAdmin}) {
            rb.setFont(UITheme.FONT_SECTION);
            rb.setForeground(UITheme.TEXT_PRIMARY);
            rb.setOpaque(false);
            rb.setFocusPainted(false);
            rolePanel.add(rb);
        }

        // Card Panel for Form
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new GridLayout(0, 1, 0, 10));

        JLabel lblUser = new JLabel("Email / Username");
        lblUser.setFont(UITheme.FONT_SECTION);
        lblUser.setForeground(UITheme.TEXT_PRIMARY);

        txtUsernameOrEmail = UITheme.createTextField(20);
        txtUsernameOrEmail.setText("mitran@example.com"); // friendly demo pre-fill

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(UITheme.FONT_SECTION);
        lblPass.setForeground(UITheme.TEXT_PRIMARY);

        txtPassword = UITheme.createPasswordField(20);
        txtPassword.setText("password123");

        // Show/Hide Password
        chkShowPassword = new JCheckBox("Show Password");
        chkShowPassword.setFont(UITheme.FONT_SMALL);
        chkShowPassword.setForeground(UITheme.TEXT_MUTED);
        chkShowPassword.setOpaque(false);
        chkShowPassword.setFocusPainted(false);
        chkShowPassword.addActionListener(e -> {
            if (chkShowPassword.isSelected()) {
                txtPassword.setEchoChar((char) 0);
            } else {
                txtPassword.setEchoChar('•');
            }
        });

        // Quick Admin toggle helper
        rbAdmin.addActionListener(e -> {
            lblUser.setText("Admin Username");
            txtUsernameOrEmail.setText("admin");
            txtPassword.setText("admin123");
        });
        rbUser.addActionListener(e -> {
            lblUser.setText("Email Address");
            txtUsernameOrEmail.setText("mitran@example.com");
            txtPassword.setText("password123");
        });

        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(UITheme.FONT_BODY);
        lblStatus.setForeground(UITheme.PRIMARY_HOVER);

        JButton btnLogin = UITheme.createPrimaryButton("Sign In to Cinema");
        btnLogin.addActionListener(e -> handleLogin());

        // Press Enter to submit
        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        };
        txtUsernameOrEmail.addKeyListener(enterKey);
        txtPassword.addKeyListener(enterKey);

        card.add(lblUser);
        card.add(txtUsernameOrEmail);
        card.add(lblPass);
        card.add(txtPassword);
        card.add(chkShowPassword);
        card.add(lblStatus);
        card.add(btnLogin);

        // Bottom registration bar
        JPanel registerBar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        registerBar.setOpaque(false);
        JLabel lblNoAcc = new JLabel("New to BookMyShow? ");
        lblNoAcc.setFont(UITheme.FONT_BODY);
        lblNoAcc.setForeground(UITheme.TEXT_SECONDARY);

        JButton btnRegister = new JButton("Create Account");
        btnRegister.setFont(UITheme.FONT_BODY_BOLD);
        btnRegister.setForeground(UITheme.PRIMARY_HOVER);
        btnRegister.setContentAreaFilled(false);
        btnRegister.setBorderPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.addActionListener(e -> {
            dispose();
            new RegisterFrame().setVisible(true);
        });

        registerBar.add(lblNoAcc);
        registerBar.add(btnRegister);

        // Assemble Main Frame
        mainPanel.add(lblLogo);
        mainPanel.add(Box.createVerticalStrut(6));
        mainPanel.add(lblTagline);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(rolePanel);
        mainPanel.add(Box.createVerticalStrut(16));
        mainPanel.add(card);
        mainPanel.add(Box.createVerticalStrut(14));
        mainPanel.add(registerBar);

        add(mainPanel);
    }

    private void handleLogin() {
        lblStatus.setText(" ");
        String identifier = txtUsernameOrEmail.getText().trim();
        String password = new String(txtPassword.getPassword());

        try {
            if (rbAdmin.isSelected()) {
                Admin admin = AuthenticationService.getInstance().loginAdmin(identifier, password);
                dispose();
                HomeFrame home = new HomeFrame();
                home.showAdminDashboard();
                home.setVisible(true);
            } else {
                User user = AuthenticationService.getInstance().loginUser(identifier, password);
                dispose();
                HomeFrame home = new HomeFrame();
                home.setVisible(true);
            }
        } catch (Exception ex) {
            lblStatus.setText("⚠️ " + ex.getMessage());
        }
    }
}

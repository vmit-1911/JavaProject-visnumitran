package moviebooking.ui;

import moviebooking.model.User;
import moviebooking.service.AuthenticationService;
import moviebooking.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Registration screen for new moviegoers.
 */
public class RegisterFrame extends JFrame {

    private final JTextField txtName;
    private final JTextField txtEmail;
    private final JTextField txtPhone;
    private final JPasswordField txtPassword;
    private final JPasswordField txtConfirmPassword;
    private final JCheckBox chkShowPassword;
    private final JLabel lblStatus;

    public RegisterFrame() {
        setTitle("BookMyShow Cinema - Register Account");
        setSize(480, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_DARK);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Header Logo
        JLabel lblLogo = new JLabel("🎬 BookMyShow", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblLogo.setForeground(UITheme.PRIMARY_RED);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTagline = new JLabel("Create Your Cinema Account", SwingConstants.CENTER);
        lblTagline.setFont(UITheme.FONT_BODY);
        lblTagline.setForeground(UITheme.TEXT_SECONDARY);
        lblTagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Form Card
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new GridLayout(0, 1, 0, 8));

        txtName = UITheme.createTextField(20);
        txtEmail = UITheme.createTextField(20);
        txtPhone = UITheme.createTextField(20);
        txtPassword = UITheme.createPasswordField(20);
        txtConfirmPassword = UITheme.createPasswordField(20);

        chkShowPassword = new JCheckBox("Show Passwords");
        chkShowPassword.setFont(UITheme.FONT_SMALL);
        chkShowPassword.setForeground(UITheme.TEXT_MUTED);
        chkShowPassword.setOpaque(false);
        chkShowPassword.setFocusPainted(false);
        chkShowPassword.addActionListener(e -> {
            char echo = chkShowPassword.isSelected() ? (char) 0 : '•';
            txtPassword.setEchoChar(echo);
            txtConfirmPassword.setEchoChar(echo);
        });

        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(UITheme.FONT_BODY);
        lblStatus.setForeground(UITheme.PRIMARY_HOVER);

        JButton btnRegister = UITheme.createPrimaryButton("Register & Start Booking");
        btnRegister.addActionListener(e -> handleRegistration());

        card.add(createFieldLabel("Full Name"));
        card.add(txtName);
        card.add(createFieldLabel("Email Address"));
        card.add(txtEmail);
        card.add(createFieldLabel("Phone Number (10 digits)"));
        card.add(txtPhone);
        card.add(createFieldLabel("Password (min 6 characters)"));
        card.add(txtPassword);
        card.add(createFieldLabel("Confirm Password"));
        card.add(txtConfirmPassword);
        card.add(chkShowPassword);
        card.add(lblStatus);
        card.add(btnRegister);

        // Bottom login link
        JPanel loginBar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loginBar.setOpaque(false);
        JLabel lblHaveAcc = new JLabel("Already registered? ");
        lblHaveAcc.setFont(UITheme.FONT_BODY);
        lblHaveAcc.setForeground(UITheme.TEXT_SECONDARY);

        JButton btnLogin = new JButton("Sign In Here");
        btnLogin.setFont(UITheme.FONT_BODY_BOLD);
        btnLogin.setForeground(UITheme.PRIMARY_HOVER);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        loginBar.add(lblHaveAcc);
        loginBar.add(btnLogin);

        mainPanel.add(lblLogo);
        mainPanel.add(Box.createVerticalStrut(4));
        mainPanel.add(lblTagline);
        mainPanel.add(Box.createVerticalStrut(18));
        mainPanel.add(card);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(loginBar);

        add(mainPanel);
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_SECTION);
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        return lbl;
    }

    private void handleRegistration() {
        lblStatus.setText(" ");
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirm = new String(txtConfirmPassword.getPassword());

        try {
            User user = AuthenticationService.getInstance().registerUser(name, email, phone, password, confirm);
            JOptionPane.showMessageDialog(this,
                    "Registration successful!\nWelcome to BookMyShow, " + user.getName() + "!",
                    "Account Created",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
            HomeFrame home = new HomeFrame();
            home.setVisible(true);
        } catch (Exception ex) {
            lblStatus.setText("⚠️ " + ex.getMessage());
        }
    }
}

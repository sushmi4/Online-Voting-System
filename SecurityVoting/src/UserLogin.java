import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class UserLogin extends JPanel {

    public UserLogin(CardLayout cl, JPanel contentPanel) {
        contentPanel.add(new ResetPassword(cl, contentPanel), "ResetPassword");

        setOpaque(false);
        setLayout(new GridBagLayout());

        UITheme.Card loginBox = new UITheme.Card(new GridBagLayout());
        loginBox.setBorder(new EmptyBorder(36, 46, 30, 46));
        loginBox.setPreferredSize(new Dimension(440, 500));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        int row = 0;

        // Top filler keeps the card content vertically balanced
        gbc.gridy = row++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JPanel topFill = new JPanel();
        topFill.setOpaque(false);
        loginBox.add(topFill, gbc);
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel icon = new JLabel(UITheme.logoIcon(56));
        gbc.gridy = row++;
        loginBox.add(icon, gbc);

        JLabel title = new JLabel("Voter Login");
        title.setFont(UITheme.font(Font.BOLD, 28));
        title.setForeground(UITheme.PRIMARY_DARK);
        gbc.gridy = row++;
        loginBox.add(title, gbc);

        JLabel subtitle = new JLabel("Sign in to cast your vote");
        subtitle.setFont(UITheme.font(Font.PLAIN, 13));
        subtitle.setForeground(UITheme.TEXT_MUTED);
        gbc.gridy = row++;
        loginBox.add(subtitle, gbc);

        // Email label (flush left, above the field)
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(18, 0, 6, 0);
        JLabel lblEmail = new JLabel("Email");
        lblEmail.setFont(UITheme.font(Font.BOLD, 12));
        lblEmail.setForeground(UITheme.TEXT_MUTED);
        gbc.gridy = row++;
        loginBox.add(lblEmail, gbc);

        JTextField emailField = new UITheme.RoundedTextField(18);
        emailField.setPreferredSize(new Dimension(0, 44));
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.gridy = row++;
        loginBox.add(emailField, gbc);

        // Password label
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(16, 0, 6, 0);
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(UITheme.font(Font.BOLD, 12));
        lblPass.setForeground(UITheme.TEXT_MUTED);
        gbc.gridy = row++;
        loginBox.add(lblPass, gbc);

        // Password field with built-in eye toggle (inside the box)
        JPasswordField passField = new UITheme.RoundedPasswordField(15, true);
        passField.setPreferredSize(new Dimension(0, 44));
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.gridy = row++;
        loginBox.add(passField, gbc);

        JButton loginBtn = UITheme.button("SIGN IN");
        loginBtn.setPreferredSize(new Dimension(240, 44));
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(22, 0, 0, 0);
        gbc.gridy = row++;
        loginBox.add(loginBtn, gbc);

        JLabel lblRegister = new JLabel("New user?  Create an account");
        lblRegister.setFont(UITheme.font(Font.BOLD, 13));
        lblRegister.setForeground(UITheme.ACCENT);
        lblRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.insets = new Insets(14, 0, 0, 0);
        gbc.gridy = row++;
        loginBox.add(lblRegister, gbc);

        JLabel lblForgotPassword = new JLabel("Forgot your password?  Reset it");
        lblForgotPassword.setFont(UITheme.font(Font.PLAIN, 12));
        lblForgotPassword.setForeground(UITheme.TEXT_MUTED);
        lblForgotPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.insets = new Insets(6, 0, 0, 0);
        gbc.gridy = row++;
        loginBox.add(lblForgotPassword, gbc);

        // Bottom filler
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JPanel bottomFill = new JPanel();
        bottomFill.setOpaque(false);
        gbc.gridy = row++;
        loginBox.add(bottomFill, gbc);

        add(loginBox);

        // --- LISTENERS ---
        loginBtn.addActionListener(e -> {
            String emailText = emailField.getText().trim();
            String passwordText = new String(passField.getPassword());

            if (emailText.isEmpty() || passwordText.isEmpty()) {
                UITheme.showMessage(this, "Login Required",
                        "Please enter email and password.",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }

            String result = DBConnection.validateLogin(emailText, passwordText);
            switch (result) {
                case "LOCKED":
                    UITheme.showMessage(this, "Account Locked",
                            "Account temporarily locked after multiple failed attempts.\n"
                                    + "Please try again later.",
                            javax.swing.JOptionPane.WARNING_MESSAGE);
                    break;
                case "OK":
                    if (!"Approved".equalsIgnoreCase(UserSession.getStatus())) {
                        UITheme.showMessage(this, "Pending Approval",
                                "Access Denied: Your account status is " + UserSession.getStatus(),
                                javax.swing.JOptionPane.WARNING_MESSAGE);
                        UserSession.logout();
                        break;
                    }
                    passField.setText("");
                    emailField.setText("");
                    hideMainNavbar();
                    contentPanel.add(new Userdrashboard(cl, contentPanel), "VoterPage");
                    cl.show(contentPanel, "VoterPage");
                    break;
                default:
                    UITheme.showMessage(this, "Login Failed",
                            "Invalid email or password.",
                            javax.swing.JOptionPane.ERROR_MESSAGE);
                    break;
            }
        });

        lblRegister.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cl.show(contentPanel, "UserRegister");
            }
        });

        lblForgotPassword.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cl.show(contentPanel, "ResetPassword");
            }
        });
    }

    private void hideMainNavbar() {
        Window win = SwingUtilities.getWindowAncestor(this);
        if (win instanceof MainApp) {
            ((MainApp) win).setMainNavbarVisible(false);
        }
    }
}

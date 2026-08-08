import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class AdminLogin extends JPanel {

    private JTextField adminUserField;
    private JPasswordField adminPassField;

    public AdminLogin(CardLayout cl, JPanel contentPanel) {
        setOpaque(false);
        setLayout(new GridBagLayout());

        UITheme.Card loginBox = new UITheme.Card(new GridBagLayout());
        loginBox.setBorder(new EmptyBorder(42, 46, 34, 46));
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

        JLabel title = new JLabel("Admin Control Center");
        title.setFont(UITheme.font(Font.BOLD, 26));
        title.setForeground(UITheme.PRIMARY_DARK);
        gbc.gridy = row++;
        loginBox.add(title, gbc);

        JLabel subtitle = new JLabel("Authorized personnel only");
        subtitle.setFont(UITheme.font(Font.PLAIN, 13));
        subtitle.setForeground(UITheme.TEXT_MUTED);
        gbc.gridy = row++;
        loginBox.add(subtitle, gbc);

        // Username label (flush left, above the field)
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(22, 0, 6, 0);
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(UITheme.font(Font.BOLD, 12));
        lblUser.setForeground(UITheme.TEXT_MUTED);
        gbc.gridy = row++;
        loginBox.add(lblUser, gbc);

        adminUserField = new UITheme.RoundedTextField(18);
        adminUserField.setPreferredSize(new Dimension(0, 44));
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.gridy = row++;
        loginBox.add(adminUserField, gbc);

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
        adminPassField = new UITheme.RoundedPasswordField(15, true);
        adminPassField.setPreferredSize(new Dimension(0, 44));
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.gridy = row++;
        loginBox.add(adminPassField, gbc);

        JButton btnLogin = UITheme.button("ACCESS DASHBOARD");
        btnLogin.setPreferredSize(new Dimension(250, 44));
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(24, 0, 0, 0);
        gbc.gridy = row++;
        loginBox.add(btnLogin, gbc);

        JLabel lblBack = new JLabel("Go back to Home");
        lblBack.setFont(UITheme.font(Font.BOLD, 13));
        lblBack.setForeground(UITheme.ACCENT);
        lblBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.insets = new Insets(14, 0, 0, 0);
        gbc.gridy = row++;
        loginBox.add(lblBack, gbc);

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
        btnLogin.addActionListener(e -> authenticate(cl, contentPanel));

        lblBack.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cl.show(contentPanel, "Home");
            }
        });
    }

    private void authenticate(CardLayout cl, JPanel contentPanel) {
        String username = adminUserField.getText().trim();
        String password = new String(adminPassField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            UITheme.showMessage(this, "Login Required",
                    "Please enter username and password.",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (isValidAdmin(username, password)) {
            adminUserField.setText("");
            adminPassField.setText("");

            UserSession.openSession("Administrator", "", "", "", "Approved",
                    "", "", "", UserSession.Role.ADMIN);

            MainApp parent = (MainApp) SwingUtilities.getWindowAncestor(this);
            parent.setMainNavbarVisible(false);

            cl.show(contentPanel, "AdminDashboard");
        } else {
            UITheme.showMessage(this, "Error",
                    "Invalid Credentials", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isValidAdmin(String username, String password) {
        String sql = "SELECT password FROM admins WHERE username=?";
        try (Connection conn = Database.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return PasswordHasher.verifyPassword(password, rs.getString("password"));
                }
            }
        } catch (Exception e) {
            System.out.println("Admin auth error: " + e.getMessage());
        }
        return false;
    }
}

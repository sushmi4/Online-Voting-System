import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class AdminLogin extends JPanel {

    private JTextField adminUserField;
    private JPasswordField adminPassField;
    private boolean showPassword = false;

    public AdminLogin(CardLayout cl, JPanel contentPanel) {

        setOpaque(false);
        setLayout(new GridBagLayout());

        JPanel loginBox = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(255, 255, 255, 190));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);

                g2.dispose();
            }
        };

        loginBox.setOpaque(false);
        loginBox.setLayout(new BoxLayout(loginBox, BoxLayout.Y_AXIS));
        loginBox.setBorder(new EmptyBorder(40, 50, 40, 50));

        JLabel title = new JLabel("Admin Control Center");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(20, 33, 61));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginBox.add(title);
        loginBox.add(Box.createRigidArea(new Dimension(0, 25)));

        JLabel userLabel = new JLabel("Admin Username:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(new Color(50, 50, 50));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginBox.add(userLabel);

        adminUserField = new JTextField();
        adminUserField.setPreferredSize(new Dimension(250, 35));
        adminUserField.setMaximumSize(new Dimension(250, 35));
        adminUserField.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginBox.add(adminUserField);
        loginBox.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passLabel.setForeground(new Color(50, 50, 50));
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginBox.add(passLabel);

        // Password field panel (for eye icon inside)
        JPanel passPanel = new JPanel(new BorderLayout());
        passPanel.setMaximumSize(new Dimension(250, 35));

        adminPassField = new JPasswordField();
        adminPassField.setBorder(new EmptyBorder(5, 5, 5, 5));

        JButton eyeButton = new JButton("👁");
        eyeButton.setBorder(null);
        eyeButton.setFocusPainted(false);
        eyeButton.setContentAreaFilled(false);
        eyeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        eyeButton.addActionListener(e -> {

            if (showPassword) {
                adminPassField.setEchoChar('•');
                eyeButton.setText("👁");
                showPassword = false;
            } else {
                adminPassField.setEchoChar((char) 0);
                eyeButton.setText("🔒");
                showPassword = true;
            }

        });

        passPanel.add(adminPassField, BorderLayout.CENTER);
        passPanel.add(eyeButton, BorderLayout.EAST);

        loginBox.add(passPanel);

        loginBox.add(Box.createRigidArea(new Dimension(0, 25)));

        JButton btnLogin = new JButton("ACCESS DASHBOARD");
        btnLogin.setBackground(new Color(20, 33, 61));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setPreferredSize(new Dimension(250, 40));
        btnLogin.setMaximumSize(new Dimension(250, 40));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setFocusPainted(false);

        btnLogin.addActionListener(e -> {

            String user = adminUserField.getText();
            String pass = new String(adminPassField.getPassword());

            if (user.equals("admin") && pass.equals("admin123")) {

                adminUserField.setText("");
                adminPassField.setText("");

                MainApp parent = (MainApp) SwingUtilities.getWindowAncestor(this);
                parent.setMainNavbarVisible(false);

                cl.show(contentPanel, "AdminDashboard");

            } else {

                JOptionPane.showMessageDialog(this,
                        "Invalid Credentials",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        });

        loginBox.add(btnLogin);

        loginBox.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel lblBack = new JLabel("Go back to Home");
        lblBack.setForeground(new Color(100, 100, 100));
        lblBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblBack.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                cl.show(contentPanel, "Home");
            }
        });

        loginBox.add(lblBack);

        add(loginBox);
    }
}
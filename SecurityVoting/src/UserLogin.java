import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class UserLogin extends JPanel {

    private Image backgroundImage;
    private boolean isPasswordVisible = false; // Flag to track visibility

    public UserLogin(CardLayout cl, JPanel contentPanel) {

        // Load Background Image
        try {
            backgroundImage = new ImageIcon("C:/Gmailsecurity/SecurityVoting/vote.png").getImage();
        } catch (Exception e) {
            System.out.println("Image not found!");
        }

        contentPanel.add(new ResetPassword(cl, contentPanel), "ResetPassword");

        setLayout(new GridBagLayout());

        // Glass Rounded Panel
        JPanel loginBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                g2.dispose();
            }
        };

        loginBox.setOpaque(false);
        loginBox.setPreferredSize(new Dimension(420, 400)); // Slightly increased height
        loginBox.setLayout(new BoxLayout(loginBox, BoxLayout.Y_AXIS));
        loginBox.setBorder(new EmptyBorder(40, 50, 40, 50));

        // Title
        JLabel title = new JLabel("VOTER LOGIN");
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(new Color(20, 40, 80));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Fields
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        fieldsPanel.add(lblEmail, gbc);

        JTextField emailField = new JTextField(18);
        gbc.gridx = 1;
        fieldsPanel.add(emailField, gbc);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        fieldsPanel.add(lblPass, gbc);

        // --- PASSWORD PANEL WITH TOGGLE ---
        JPanel passContainer = new JPanel(new BorderLayout());
        passContainer.setBackground(Color.WHITE);
        passContainer.setBorder(new MatteBorder(1, 1, 1, 1, Color.GRAY)); // Matches standard field look

        JPasswordField passField = new JPasswordField(15);
        passField.setBorder(null); // Remove inner border

        JLabel eyeIcon = new JLabel("👁"); // You can replace with new ImageIcon("eye.png")
        eyeIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        eyeIcon.setBorder(new EmptyBorder(0, 5, 0, 5));

        passContainer.add(passField, BorderLayout.CENTER);
        passContainer.add(eyeIcon, BorderLayout.EAST);

        gbc.gridx = 1;
        fieldsPanel.add(passContainer, gbc);

        // Toggle logic
        eyeIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isPasswordVisible) {
                    passField.setEchoChar('•'); // Hide password
                    eyeIcon.setText("👁");
                } else {
                    passField.setEchoChar((char) 0); // Show password
                    eyeIcon.setText("🔒");
                }
                isPasswordVisible = !isPasswordVisible;
            }
        });

        // Action Panel
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        actionPanel.setOpaque(false);

        JPanel loginRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        loginRow.setOpaque(false);

        JButton loginBtn = new JButton("LOGIN");
        loginBtn.setPreferredSize(new Dimension(120, 40));
        loginBtn.setBackground(new Color(20, 40, 80));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 13));
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginRow.add(loginBtn);

        JLabel lblRegister = new JLabel("New User? Register");
        lblRegister.setForeground(new Color(0, 102, 204));
        lblRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JPanel registerRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        registerRow.setOpaque(false);
        registerRow.add(lblRegister);

        JLabel lblForgotPassword = new JLabel("ResetPassword?");
        lblForgotPassword.setForeground(new Color(0, 102, 204));
        lblForgotPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JPanel forgotRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        forgotRow.setOpaque(false);
        forgotRow.add(lblForgotPassword);

        actionPanel.add(loginRow);
        actionPanel.add(registerRow);
        actionPanel.add(forgotRow);

        loginBox.add(title);
        loginBox.add(Box.createRigidArea(new Dimension(0, 30)));
        loginBox.add(fieldsPanel);
        loginBox.add(Box.createRigidArea(new Dimension(0, 25)));
        loginBox.add(actionPanel);

        add(loginBox);

        // --- LISTENERS ---
        loginBtn.addActionListener(e -> {
            String emailText = emailField.getText();
            String passwordText = new String(passField.getPassword());
            if (DBConnection.validateLogin(emailText, passwordText)) {
                if (UserSession.getStatus().equalsIgnoreCase("Approved")) {
                    contentPanel.add(new Userdrashboard(cl, contentPanel), "VoterPage");
                    cl.show(contentPanel, "VoterPage");
                } else {
                    JOptionPane.showMessageDialog(this, "Access Denied: " + UserSession.getStatus());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.");
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
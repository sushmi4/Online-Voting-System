import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
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

import javax.swing.BorderFactory;
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

public class ResetPassword extends JPanel {

    private Image backgroundImage;
    // Track visibility state
    private boolean isPassVisible = false;
    private boolean isConfirmVisible = false;

    public ResetPassword(CardLayout cl, JPanel contentPanel) {
        try {
            backgroundImage = new ImageIcon("C:/Gmailsecurity/SecurityVoting/vote.png").getImage();
        } catch (Exception e) {
            System.out.println("Background image not found!");
        }

        setLayout(new GridBagLayout());

        JPanel resetBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 210));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                g2.dispose();
            }
        };

        resetBox.setOpaque(false);
        resetBox.setPreferredSize(new Dimension(450, 520));
        resetBox.setLayout(new BoxLayout(resetBox, BoxLayout.Y_AXIS));
        resetBox.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("RESET PASSWORD");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(20, 40, 80));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;

        // --- EMAIL ---
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Enter Registered Email:"), gbc);
        JTextField emailField = new JTextField(20);
        gbc.gridy = 1;
        inputPanel.add(emailField, gbc);

        // --- NEW PASSWORD WITH TOGGLE ---
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Enter New Password:"), gbc);

        JPanel p1Container = createPasswordContainer();
        JPasswordField newPassField = (JPasswordField) p1Container.getComponent(0);
        JLabel eye1 = (JLabel) p1Container.getComponent(1);
        gbc.gridy = 3;
        inputPanel.add(p1Container, gbc);

        // --- CONFIRM PASSWORD WITH TOGGLE ---
        gbc.gridy = 4;
        inputPanel.add(new JLabel("Confirm New Password:"), gbc);

        JPanel p2Container = createPasswordContainer();
        JPasswordField confirmPassField = (JPasswordField) p2Container.getComponent(0);
        JLabel eye2 = (JLabel) p2Container.getComponent(1);
        gbc.gridy = 5;
        inputPanel.add(p2Container, gbc);

        // Logic for Eye 1
        eye1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                isPassVisible = !isPassVisible;
                newPassField.setEchoChar(isPassVisible ? (char) 0 : '•');
                eye1.setText(isPassVisible ? "👁" : "🔒"); // You can use ImageIcons here
            }
        });

        // Logic for Eye 2
        eye2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                isConfirmVisible = !isConfirmVisible;
                confirmPassField.setEchoChar(isConfirmVisible ? (char) 0 : '•');
                eye2.setText(isConfirmVisible ? "👁" : "🔒");
            }
        });

        // --- BUTTONS ---
        JButton updateBtn = new JButton("UPDATE PASSWORD");
        updateBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateBtn.setPreferredSize(new Dimension(220, 40));
        updateBtn.setMaximumSize(new Dimension(220, 40));
        updateBtn.setBackground(new Color(63, 137, 187));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        updateBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblBack = new JLabel("Back to Login");
        lblBack.setForeground(new Color(0, 102, 204));
        lblBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblBack.setAlignmentX(Component.CENTER_ALIGNMENT);

        resetBox.add(title);
        resetBox.add(Box.createRigidArea(new Dimension(0, 20)));
        resetBox.add(inputPanel);
        resetBox.add(Box.createRigidArea(new Dimension(0, 20)));
        resetBox.add(updateBtn);
        resetBox.add(Box.createRigidArea(new Dimension(0, 15)));
        resetBox.add(lblBack);
        add(resetBox);

        // Action Listeners
        updateBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String newPass = new String(newPassField.getPassword());
            if (DBConnection.updatePassword(email, newPass)) {
                JOptionPane.showMessageDialog(this, "Success!");
                cl.show(contentPanel, "UserLogin");
            }
        });

        lblBack.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cl.show(contentPanel, "UserLogin");
            }
        });
    }

    // Helper method to create the password field + Eye icon look
    private JPanel createPasswordContainer() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY),
                new EmptyBorder(2, 5, 2, 5)));

        JPasswordField field = new JPasswordField();
        field.setBorder(null); // Remove default border

        JLabel eyeLabel = new JLabel("🔒"); // Use an ImageIcon here for a better look
        eyeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panel.add(field, BorderLayout.CENTER);
        panel.add(eyeLabel, BorderLayout.EAST);
        return panel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null)
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
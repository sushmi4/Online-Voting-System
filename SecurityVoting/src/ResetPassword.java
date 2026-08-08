import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ResetPassword extends JPanel {

    private CardLayout steps = new CardLayout();
    private JPanel stepPanel;

    private final CardLayout parentCl;
    private final JPanel parentContent;

    private JTextField emailField;
    private JTextField otpField;
    private JPasswordField newPassField;
    private JPasswordField confirmPassField;

    private String resetEmail;

    private static final Pattern PASSWORD_RULE =
            Pattern.compile("(?=.*[A-Za-z])(?=.*\\d).{8,}");

    public ResetPassword(CardLayout cl, JPanel contentPanel) {
        this.parentCl = cl;
        this.parentContent = contentPanel;

        setOpaque(false);
        setLayout(new GridBagLayout());

        UITheme.Card resetBox = new UITheme.Card();
        resetBox.setLayout(new BoxLayout(resetBox, BoxLayout.Y_AXIS));
        resetBox.setBorder(new EmptyBorder(32, 44, 26, 44));
        resetBox.setPreferredSize(new Dimension(480, 460));

        JLabel icon = new JLabel(UITheme.Icons.lock(UITheme.ACCENT));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetBox.add(icon);
        resetBox.add(Box.createRigidArea(new Dimension(0, 6)));

        JLabel title = new JLabel("Reset Password");
        title.setFont(UITheme.font(Font.BOLD, 26));
        title.setForeground(UITheme.PRIMARY_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetBox.add(title);
        resetBox.add(Box.createRigidArea(new Dimension(0, 20)));

        stepPanel = new JPanel(steps);
        stepPanel.setOpaque(false);

        JPanel step1 = buildEmailStep();
        JPanel step2 = buildOtpStep();
        JPanel step3 = buildPasswordStep();

        stepPanel.add(step1, "step1");
        stepPanel.add(step2, "step2");
        stepPanel.add(step3, "step3");

        resetBox.add(stepPanel);
        resetBox.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblBack = new JLabel("Back to Login");
        lblBack.setFont(UITheme.font(Font.BOLD, 13));
        lblBack.setForeground(UITheme.ACCENT);
        lblBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblBack.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cl.show(contentPanel, "UserLogin");
            }
        });
        resetBox.add(lblBack);

        add(resetBox);

        steps.show(stepPanel, "step1");
    }

    // ================= STEP 1: EMAIL =================
    private JPanel buildEmailStep() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        p.add(centerLabel("Enter your registered email address:", 13, UITheme.TEXT_MUTED));
        p.add(Box.createRigidArea(new Dimension(0, 14)));

        emailField = new UITheme.RoundedTextField(22);
        emailField.setMaximumSize(new Dimension(320, 44));
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(emailField);
        p.add(Box.createRigidArea(new Dimension(0, 24)));

        JButton next = UITheme.button("SEND OTP");
        next.setPreferredSize(new Dimension(220, 42));
        next.setMaximumSize(new Dimension(220, 42));
        next.setAlignmentX(Component.CENTER_ALIGNMENT);
        next.addActionListener(e -> handleSendOtp());
        p.add(next);

        return p;
    }

    private void handleSendOtp() {
        String email = emailField.getText().trim();
        if (email.isEmpty() || !email.contains("@")) {
            UITheme.showMessage(this, "Invalid Email",
                    "Please enter a valid email address.",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!DBConnection.emailExists(email)) {
            UITheme.showMessage(this, "Email Not Found",
                    "No account is registered with this email address.",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        String otp = OtpManager.issue(email);
        if (otp == null) {
            UITheme.showMessage(this, "Too Many Requests",
                    "Please wait a moment before requesting another code.",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean sent = EmailService.sendOtpEmail(email, otp);
        if (!sent) {
            if (!EmailService.isConfigured()) {
                UITheme.showMessage(this, "Email Not Configured",
                        "Email service is not configured on this system.\n"
                                + "Add SMTP settings to config.properties to enable OTP delivery.",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            } else {
                UITheme.showMessage(this, "Send Failed",
                        "Failed to send the verification email. Please try again.",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }
            OtpManager.invalidate(email);
            return;
        }

        resetEmail = email;
        otpField.setText("");
        steps.show(stepPanel, "step2");
    }

    // ================= STEP 2: OTP =================
    private JPanel buildOtpStep() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        p.add(centerLabel("Enter the 6-digit code sent to your email:", 13, UITheme.TEXT_MUTED));
        p.add(Box.createRigidArea(new Dimension(0, 14)));

        otpField = new UITheme.RoundedTextField(6);
        otpField.setHorizontalAlignment(JTextField.CENTER);
        otpField.setMaximumSize(new Dimension(180, 44));
        otpField.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(otpField);
        p.add(Box.createRigidArea(new Dimension(0, 24)));

        JButton verify = UITheme.button("VERIFY CODE");
        verify.setPreferredSize(new Dimension(220, 42));
        verify.setMaximumSize(new Dimension(220, 42));
        verify.setAlignmentX(Component.CENTER_ALIGNMENT);
        verify.addActionListener(e -> {
            String code = otpField.getText().trim();
            if (OtpManager.verify(resetEmail, code)) {
                steps.show(stepPanel, "step3");
            } else {
                UITheme.showMessage(this, "Verification Failed",
                        "Invalid or expired code.",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });
        p.add(verify);
        p.add(Box.createRigidArea(new Dimension(0, 12)));

        JButton resend = UITheme.outlineButton("RESEND CODE", UITheme.TEXT_MUTED);
        resend.setPreferredSize(new Dimension(220, 38));
        resend.setMaximumSize(new Dimension(220, 38));
        resend.setAlignmentX(Component.CENTER_ALIGNMENT);
        resend.addActionListener(e -> {
            String otp = OtpManager.issue(resetEmail);
            if (otp == null) {
                UITheme.showMessage(this, "Too Many Requests",
                        "Please wait a moment before requesting another code.",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!EmailService.sendOtpEmail(resetEmail, otp)) {
                OtpManager.invalidate(resetEmail);
                UITheme.showMessage(this, "Send Failed",
                        "Failed to resend the email. Please try again.",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });
        p.add(resend);

        return p;
    }

    // ================= STEP 3: NEW PASSWORD =================
    private JPanel buildPasswordStep() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        gbc.gridx = 0;
        gbc.gridy = 0;
        p.add(centerLabel("Choose a new password:", 13, UITheme.TEXT_MUTED), gbc);

        gbc.gridy = 1;
        newPassField = new UITheme.RoundedPasswordField(18);
        p.add(newPassField, gbc);

        gbc.gridy = 2;
        p.add(centerLabel("Confirm new password:", 13, UITheme.TEXT_MUTED), gbc);

        gbc.gridy = 3;
        confirmPassField = new UITheme.RoundedPasswordField(18);
        p.add(confirmPassField, gbc);

        gbc.gridy = 4;
        p.add(centerLabel("Minimum 8 characters with at least one letter and one number.",
                11, UITheme.TEXT_MUTED), gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(16, 5, 0, 5);
        JButton update = UITheme.button("UPDATE PASSWORD");
        p.add(update, gbc);

        update.addActionListener(e -> handleUpdatePassword());

        return p;
    }

    private void handleUpdatePassword() {
        String newPass = new String(newPassField.getPassword());
        String confirm = new String(confirmPassField.getPassword());

        if (!PASSWORD_RULE.matcher(newPass).matches()) {
            UITheme.showMessage(this, "Weak Password",
                    "Password must be at least 8 characters with letters and numbers.",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!newPass.equals(confirm)) {
            UITheme.showMessage(this, "Mismatch",
                    "Passwords do not match.",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (DBConnection.updatePassword(resetEmail, newPass)) {
            OtpManager.invalidate(resetEmail);
            UITheme.showMessage(this, "Success", "Password updated successfully!",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
            newPassField.setText("");
            confirmPassField.setText("");
            emailField.setText("");
            otpField.setText("");
            steps.show(stepPanel, "step1");
            parentCl.show(parentContent, "UserLogin");
        } else {
            UITheme.showMessage(this, "Error",
                    "Password update failed. Please try again.",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel centerLabel(String text, int size, Color color) {
        JLabel l = new JLabel(text, JLabel.CENTER);
        l.setFont(UITheme.font(Font.PLAIN, size));
        l.setForeground(color);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }
}

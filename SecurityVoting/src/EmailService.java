import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Sends emails via the SMTP settings in config.properties.
 * Returns false (without throwing) when SMTP is not configured or fails,
 * so the UI can show a friendly message.
 */
public class EmailService {

    public static boolean isConfigured() {
        return !Config.smtpHost().isEmpty()
                && !Config.smtpFrom().isEmpty();
    }

    public static boolean sendOtpEmail(String toEmail, String otp) {
        if (!isConfigured()) {
            System.out.println("SMTP not configured; OTP email skipped for " + toEmail);
            return false;
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", Config.smtpHost());
        props.put("mail.smtp.port", String.valueOf(Config.smtpPort()));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Config.smtpFrom(), Config.smtpPassword());
            }
        });

        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(Config.smtpFrom(), Config.smtpFromName(), "UTF-8"));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            msg.setSubject("Online Voting System - Password Reset OTP");
            msg.setText("Hello,\n\n"
                    + "Your password reset verification code is: " + otp + "\n\n"
                    + "This code is valid for 5 minutes. Do not share it with anyone.\n\n"
                    + "If you did not request this, please ignore this email.\n\n"
                    + "Regards,\nOnline Voting System");
            Transport.send(msg);
            return true;
        } catch (Exception e) {
            System.out.println("Email send failed: " + e.getMessage());
            return false;
        }
    }
}

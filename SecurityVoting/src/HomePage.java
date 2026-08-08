import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Landing page shown on the home screen with an intro, call-to-action buttons
 * and a row of feature highlights.
 */
public class HomePage extends JPanel {

    private final CardLayout cl;
    private final JPanel cardPanel;

    public HomePage(CardLayout cl, JPanel cardPanel) {
        this.cl = cl;
        this.cardPanel = cardPanel;

        setOpaque(false);
        setLayout(new BorderLayout());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new javax.swing.border.EmptyBorder(34, 46, 34, 46));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // ---- Hero ----
        JLabel logo = new JLabel(UITheme.logoIcon(72));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(logo);
        content.add(Box.createRigidArea(new Dimension(0, 18)));

        JLabel title = new JLabel("Secure Online Voting System");
        title.setFont(UITheme.font(Font.BOLD, 34));
        title.setForeground(UITheme.PRIMARY_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(title);
        content.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel tagline = new JLabel("Cast your vote securely, transparently and from anywhere.");
        tagline.setFont(UITheme.font(Font.PLAIN, 17));
        tagline.setForeground(UITheme.TEXT_MUTED);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(tagline);
        content.add(Box.createRigidArea(new Dimension(0, 8)));

        JLabel description = centered("<html><div style='text-align:center;width:640px;'>"
                + "An e-voting platform built for election authorities and voters. "
                + "Register once, get approved, then cast a single secure vote. "
                + "Watch live results update in real time without standing in a single queue."
                + "</div></html>", 14, UITheme.TEXT_MUTED);
        content.add(description);
        content.add(Box.createRigidArea(new Dimension(0, 26)));

        // ---- Call to action ----
        JPanel cta = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        cta.setOpaque(false);
        cta.add(actionButton("Voter Login", "UserLogin"));
        cta.add(actionButton("Register to Vote", "UserRegister"));
        cta.add(outlineButton("Admin Login", "AdminLogin"));
        cta.add(outlineButton("View Results", "Result"));
        content.add(cta);
        content.add(Box.createRigidArea(new Dimension(0, 34)));

        // ---- Feature highlights ----
        JLabel featuresTitle = new JLabel("Why choose this system?");
        featuresTitle.setFont(UITheme.font(Font.BOLD, 18));
        featuresTitle.setForeground(UITheme.PRIMARY_DARK);
        featuresTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(featuresTitle);
        content.add(Box.createRigidArea(new Dimension(0, 16)));

        JPanel features = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        features.setOpaque(false);
        features.add(featureCard(UITheme.Icons.lock(UITheme.ACCENT),
                "Secure Login", "Password-protected accounts with OTP-based password recovery."));
        features.add(featureCard(UITheme.Icons.ballot(UITheme.GREEN),
                "One Voter, One Vote", "Voters can only vote once per election - no duplicates allowed."));
        features.add(featureCard(UITheme.Icons.chart(UITheme.WARNING),
                "Live Results", "Results are calculated and updated automatically as votes arrive."));
        content.add(features);
        content.add(Box.createRigidArea(new Dimension(0, 26)));

        JLabel footer = new JLabel("Need help? Contact your election administrator.");
        footer.setFont(UITheme.font(Font.PLAIN, 12));
        footer.setForeground(UITheme.TEXT_MUTED);
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(footer);

        wrapper.add(content, BorderLayout.NORTH);
        add(wrapper, BorderLayout.CENTER);
    }

    private JLabel centered(String text, int size, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.font(Font.PLAIN, size));
        l.setForeground(color);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    private javax.swing.JButton actionButton(String text, String cardName) {
        javax.swing.JButton b = UITheme.button(text);
        b.setPreferredSize(new Dimension(180, 44));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> cl.show(cardPanel, cardName));
        return b;
    }

    private javax.swing.JButton outlineButton(String text, String cardName) {
        javax.swing.JButton b = UITheme.outlineButton(text, UITheme.ACCENT);
        b.setPreferredSize(new Dimension(180, 44));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> cl.show(cardPanel, cardName));
        return b;
    }

    private JPanel featureCard(javax.swing.Icon icon, String heading, String detail) {
        UITheme.Card card = new UITheme.Card(new BorderLayout(0, 10));
        card.setPreferredSize(new Dimension(250, 150));
        card.setBorder(new javax.swing.border.EmptyBorder(20, 20, 20, 20));

        JPanel head = new JPanel(new BorderLayout(8, 0));
        head.setOpaque(false);
        head.add(new JLabel(icon), BorderLayout.WEST);
        JLabel h = new JLabel(heading);
        h.setFont(UITheme.font(Font.BOLD, 15));
        h.setForeground(UITheme.PRIMARY_DARK);
        head.add(h, BorderLayout.CENTER);
        card.add(head, BorderLayout.NORTH);

        JLabel d = new JLabel("<html><div style='text-align:left;width:200px;'>" + detail + "</div></html>");
        d.setFont(UITheme.font(Font.PLAIN, 12));
        d.setForeground(UITheme.TEXT_MUTED);
        card.add(d, BorderLayout.CENTER);

        return card;
    }
}

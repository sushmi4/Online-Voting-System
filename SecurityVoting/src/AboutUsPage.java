import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class AboutUsPage extends JPanel {

    private Image backgroundImage;

    public AboutUsPage() {

        // ✅ Load Background Image
        try {
            backgroundImage = new ImageIcon("C:/Gmailsecurity/SecurityVoting/vote.png").getImage();
        } catch (Exception e) {
            System.out.println("Image not found!");
        }

        setLayout(new BorderLayout());

        // =========================
        // PURPLE HEADER
        // =========================
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(20, 120, 200));
        header.setPreferredSize(new Dimension(0, 60));

        JLabel title = new JLabel("  ABOUT OUR SYSTEM");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // =========================
        // GLASS CONTENT PANEL
        // =========================
        JPanel glassPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                // White transparent glass effect
                g2.setColor(new Color(255, 255, 255, 220));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);

                g2.dispose();
            }
        };

        glassPanel.setOpaque(false);
        glassPanel.setLayout(new BorderLayout());
        glassPanel.setBorder(new EmptyBorder(40, 60, 40, 60));
        glassPanel.setPreferredSize(new Dimension(800, 450));

        // =========================
        // TEXT CONTENT
        // =========================
        String aboutUsText = "Online Voting System is a web-based application that helps in making the voting process more efficient, "
                + "as it provides features that allow voters to cast their votes anytime and from anywhere.\n\n"
                + "This system aims to make the voting experience more secure, transparent, and convenient for voters, "
                + "while also making it easier for election authorities to manage the entire election process.\n\n"
                + "Voters can view candidate details and cast their votes based on eligibility and availability without standing "
                + "in long queues or facing manual verification delays.\n\n"
                + "Features such as voter registration, vote casting, real-time result calculation, and secure authentication "
                + "reduce human effort, minimize errors, and increase trust and satisfaction among voters.";

        JTextArea textArea = new JTextArea(aboutUsText);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setOpaque(false);
        textArea.setForeground(new Color(50, 50, 50));

        glassPanel.add(textArea, BorderLayout.CENTER);

        // Center the glass panel
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(glassPanel);

        JScrollPane scrollPane = new JScrollPane(centerWrapper);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);

        add(scrollPane, BorderLayout.CENTER);
    }

    // =========================
    // DRAW BACKGROUND IMAGE
    // =========================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0,
                    getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(240, 240, 240));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
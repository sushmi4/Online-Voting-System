import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MainApp extends JFrame {

    private CardLayout cl;
    private JPanel cardPanel;
    private JPanel headerPanel;

    public MainApp() {
        setTitle("Online Voting System");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 1. Setup Background Content Pane
        String bgPath = "C:\\Gmailsecurity\\SecurityVoting\\vote.png";
        setContentPane(new BackgroundPanel(bgPath));
        setLayout(new BorderLayout());

        // 2. Setup CardLayout and Pages
        cl = new CardLayout();
        cardPanel = new JPanel(cl);
        cardPanel.setOpaque(false);

        // Landing/Home Page (Transparent to show background)
        JPanel homePage = new JPanel();
        homePage.setOpaque(false);
        cardPanel.add(homePage, "Home");

        // JPanel welcomeScreen = new JPanel(); // fixed
        // welcomeScreen.setBackground(Color.WHITE);
        // welcomeScreen.add(new JLabel(
        // "<html><center><h1 style='color:#14213d;'>Welcome to Admin Control
        // Panel</h1>"
        // + "<p>Select an option from the menu above to begin.</p></center></html>"));

        // cardPanel.add(welcomeScreen, "Welcome"); // Use cardPanel, not bodyPanel

        try {
            // Registering all functional pages
            cardPanel.add(new AboutUsPage(), "AboutUs");
            cardPanel.add(new UserLogin(cl, cardPanel), "UserLogin");
            cardPanel.add(new UserRegister(cl, cardPanel), "UserRegister");
            // cardPanel.add(new ResetPassword(cl, cardPanel), "ResetPassword");

            cardPanel.add(new AdminLogin(cl, cardPanel), "AdminLogin");

            // Registering Result and Dashboard
            cardPanel.add(new UserResult(cl, cardPanel), "Result");
            cardPanel.add(new AdminDashboard(cl, cardPanel), "AdminDashboard");

        } catch (Exception e) {
            System.err.println("Note: Component error: " + e.getMessage());
        }

        // 3. Setup Navbar
        headerPanel = createNavbar();

        add(headerPanel, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);

        cl.show(cardPanel, "Home");
    }

    // public void setHeaderVisible(boolean visible) {
    // if (headerPanel != null) {
    // headerPanel.setVisible(visible);
    // revalidate();
    // repaint();
    // }
    // }

    private JPanel createNavbar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(new Color(0, 33, 61));
        nav.setPreferredSize(new Dimension(0, 70));
        nav.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));

        JLabel title = new JLabel("VOTING SYSTEM");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        nav.add(title, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        btnPanel.setOpaque(false);

        // Navigation Items
        btnPanel.add(createNavLink("HOME", "Home"));
        btnPanel.add(createNavLink("ABOUT US", "AboutUs"));
        btnPanel.add(createNavLink("USER", "UserLogin"));
        btnPanel.add(createNavLink("ADMIN", "AdminLogin"));
        btnPanel.add(createNavLink("RESULT", "Result"));

        nav.add(btnPanel, BorderLayout.EAST);
        return nav;
    }

    // --- FULL REFRESH LOGIC INTEGRATED HERE ---
    private JButton createNavLink(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(new Color(0, 255, 127));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            // Logic to refresh data when the RESULT button is clicked
            if (cardName.equals("Result")) {
                for (Component comp : cardPanel.getComponents()) {
                    if (comp instanceof UserResult) {
                        ((UserResult) comp).refreshResults();
                    }
                }
            }
            cl.show(cardPanel, cardName);
        });

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(Color.WHITE);
            }

            public void mouseExited(MouseEvent e) {
                btn.setForeground(new Color(0, 255, 127));
            }
        });

        return btn;
    }

    public void setMainNavbarVisible(boolean visible) {
        if (headerPanel != null) {
            headerPanel.setVisible(visible);
            revalidate();
            repaint();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp().setVisible(true));
    }

    class BackgroundPanel extends JPanel {
        private Image img;

        public BackgroundPanel(String path) {
            this.img = new ImageIcon(path).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) {
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MainApp extends JFrame {

    private CardLayout cl;
    private JPanel cardPanel;
    private JPanel headerPanel;
    private final Map<String, UITheme.TextButton> navLinks = new HashMap<>();

    public MainApp() {
        setTitle("Secure Online Voting System");
        setSize(1200, 750);
        setMinimumSize(new Dimension(1000, 640));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        UITheme.tuneLookAndFeel();

        // 1. Setup Background Content Pane
        setContentPane(new UITheme.GradientBackground());
        setLayout(new BorderLayout());

        // 2. Setup CardLayout and Pages
        cl = new NavCardLayout(this::setActiveNav);
        cardPanel = new JPanel(cl);
        cardPanel.setOpaque(false);

        cardPanel.add(new HomePage(cl, cardPanel), "Home");

        try {
            cardPanel.add(new AboutUsPage(), "AboutUs");
            cardPanel.add(new UserLogin(cl, cardPanel), "UserLogin");
            cardPanel.add(new UserRegister(cl, cardPanel), "UserRegister");
            cardPanel.add(new AdminLogin(cl, cardPanel), "AdminLogin");
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

        // Clear any lingering session when the application closes
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                UserSession.logout();
            }
        });
    }

    private JPanel createNavbar() {
        JPanel nav = new JPanel(new GridBagLayout());
        nav.setBackground(UITheme.SURFACE);
        nav.setPreferredSize(new Dimension(0, 68));
        nav.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
                BorderFactory.createEmptyBorder(0, 24, 0, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;

        // Brand
        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brand.setOpaque(false);
        JLabel logo = new JLabel(UITheme.logoIcon(34));
        JLabel title = new JLabel("SecureVote");
        title.setFont(UITheme.font(Font.BOLD, 19));
        title.setForeground(UITheme.PRIMARY_DARK);
        brand.add(logo);
        brand.add(title);
        gbc.gridx = 0;
        gbc.weightx = 0;
        nav.add(brand, gbc);

        // Spacer keeps links pinned to the right edge
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        nav.add(spacer, gbc);

        // Nav links
        JPanel links = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        links.setOpaque(false);
        addNavLink(links, "Home", "Home");
        addNavLink(links, "About", "AboutUs");
        addNavLink(links, "Voter Login", "UserLogin");
        addNavLink(links, "Admin Login", "AdminLogin");
        addNavLink(links, "Results", "Result");
        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        nav.add(links, gbc);

        return nav;
    }

    private void addNavLink(JPanel panel, String text, String cardName) {
        UITheme.TextButton btn = new UITheme.TextButton(text);
        navLinks.put(cardName, btn);
        btn.addActionListener(e -> {
            // Auto-expire session if it has been idle too long
            if (UserSession.isLoggedIn() && UserSession.isExpired()) {
                UserSession.logout();
            }

            if (cardName.equals("Result")) {
                for (Component comp : cardPanel.getComponents()) {
                    if (comp instanceof UserResult) {
                        ((UserResult) comp).refreshResults();
                    }
                }
            }
            cl.show(cardPanel, cardName);
        });
        panel.add(btn);
    }

    private void setActiveNav(String cardName) {
        for (Map.Entry<String, UITheme.TextButton> entry : navLinks.entrySet()) {
            entry.getValue().setActive(entry.getKey().equals(cardName));
        }
    }

    /** CardLayout that reports which card is shown so the navbar can highlight it. */
    private static class NavCardLayout extends CardLayout {
        private final Consumer<String> onShow;

        NavCardLayout(Consumer<String> onShow) {
            this.onShow = onShow;
        }

        @Override
        public void show(Container parent, String name) {
            super.show(parent, name);
            onShow.accept(name);
        }
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
}

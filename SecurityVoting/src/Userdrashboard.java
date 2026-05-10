import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class Userdrashboard extends JPanel {
    private CardLayout cl; // Main CardLayout from parent (MainApp)
    private JPanel contentPanel; // Main content panel from parent
    private CardLayout innerCL = new CardLayout();
    private JPanel innerBody = new JPanel(innerCL);
    private JPanel sidebar; // Made global to toggle visibility

    public Userdrashboard(CardLayout cl, JPanel contentPanel) {
        this.cl = cl;
        this.contentPanel = contentPanel;

        setLayout(new BorderLayout());

        // --- 1. HEADER ---
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setBackground(new Color(63, 137, 187));
        topHeader.setPreferredSize(new Dimension(0, 60));
        topHeader.setBorder(new EmptyBorder(0, 15, 0, 20));

        // LEFT SIDE: Hamburger Menu + Title
        JPanel leftHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        leftHeaderPanel.setOpaque(false);

        // Hamburger Icon (Three bars)
        JLabel menuToggle = new JLabel("\u2630"); // Unicode for hamburger icon
        menuToggle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        menuToggle.setForeground(Color.WHITE);
        menuToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Toggle Logic
        menuToggle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                sidebar.setVisible(!sidebar.isVisible());
                revalidate();
                repaint();
            }
        });

        JLabel headerTxt = new JLabel("User Dashboard");
        headerTxt.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerTxt.setForeground(Color.white);

        leftHeaderPanel.add(menuToggle);
        leftHeaderPanel.add(headerTxt);
        topHeader.add(leftHeaderPanel, BorderLayout.WEST);

        // RIGHT SIDE: Profile Dropdown
        JLabel profileDropdown = new JLabel("\uD83D\uDC64  \u25BC");
        profileDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        profileDropdown.setForeground(new Color(220, 220, 220));
        profileDropdown.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Logout Popup
        JPopupMenu logoutMenu = new JPopupMenu();
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logoutItem.addActionListener(e -> {
            UserSession.clearSession();
            Window win = SwingUtilities.getWindowAncestor(this);
            if (win instanceof MainApp) {
                ((MainApp) win).setMainNavbarVisible(true);
            }
            innerCL.show(innerBody, "HOME");
            cl.show(contentPanel, "Home");
        });
        logoutMenu.add(logoutItem);

        profileDropdown.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                logoutMenu.show(profileDropdown, 0, profileDropdown.getHeight());
            }
        });
        topHeader.add(profileDropdown, BorderLayout.EAST);

        // --- 2. SIDEBAR ---
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(40, 60, 70));
        sidebar.setPreferredSize(new Dimension(180, 0));
        sidebar.setBorder(new EmptyBorder(30, 20, 0, 0));

        // sidebar.add(createNavItem("Dashboard", "HOME"));
        // sidebar.add(Box.createRigidArea(new Dimension(0, 40)));
        sidebar.add(createNavItem("Profile", "PROFILE"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));
        sidebar.add(createNavItem("Voting Results", "RESULT"));

        // --- 3. INNER PAGES ---
        // innerBody.add(createViewPanel("Welcome User Dashboard"), "HOME");

        try {
            innerBody.add(new VoterDetailsPage(cl, contentPanel), "PROFILE");
            innerBody.add(new VotingResultsPage(), "RESULT");
        } catch (Exception ex) {
            innerBody.add(createViewPanel("Profile Page"), "PROFILE");
            innerBody.add(createViewPanel("Results Page"), "RESULT");
        }

        // --- 4. ASSEMBLE ---
        add(topHeader, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(innerBody, BorderLayout.CENTER);
    }

    private JLabel createNavItem(String text, String cardName) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setForeground(new Color(220, 220, 220));
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                innerCL.show(innerBody, cardName);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                label.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setForeground(new Color(220, 220, 220));
            }
        });
        return label;
    }

    private JPanel createViewPanel(String text) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(new Color(245, 247, 250));
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        p.add(lbl);
        return p;
    }
}
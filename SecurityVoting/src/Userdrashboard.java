import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class Userdrashboard extends JPanel {
    private CardLayout cl;
    private JPanel contentPanel;
    private CardLayout innerCL = new CardLayout();
    private JPanel innerBody = new JPanel(innerCL);
    private JPanel sidebar;
    private UITheme.SidebarItem profileItem, resultsItem;

    public Userdrashboard(CardLayout cl, JPanel contentPanel) {
        this.cl = cl;
        this.contentPanel = contentPanel;

        setLayout(new BorderLayout());

        // --- 1. HEADER ---
        JPanel topHeader = new UITheme.HeaderBar();
        topHeader.setLayout(new BorderLayout());
        topHeader.setPreferredSize(new Dimension(0, 62));
        topHeader.setBorder(new EmptyBorder(0, 18, 0, 20));

        // LEFT: Hamburger Menu + Title
        JPanel leftHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 14));
        leftHeaderPanel.setOpaque(false);

        JLabel menuToggle = new JLabel(UITheme.Icons.menu(UITheme.WHITE));
        menuToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));

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
        headerTxt.setForeground(UITheme.WHITE);

        leftHeaderPanel.add(menuToggle);
        leftHeaderPanel.add(headerTxt);
        topHeader.add(leftHeaderPanel, BorderLayout.WEST);

        // RIGHT: Profile name + visible Logout button
        JPanel profileWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        profileWrap.setOpaque(false);

        JLabel profileName = new JLabel(UserSession.getFullName());
        profileName.setFont(UITheme.font(Font.BOLD, 13));
        profileName.setForeground(UITheme.WHITE);

        JButton logoutBtn = new UITheme.RoundedButton("Logout", UITheme.RED, true);
        logoutBtn.setIcon(UITheme.Icons.logout(UITheme.WHITE));
        logoutBtn.setFont(UITheme.font(Font.BOLD, 12));
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        logoutBtn.addActionListener(e -> {
            UserSession.logout();
            Window win = SwingUtilities.getWindowAncestor(this);
            if (win instanceof MainApp) {
                ((MainApp) win).setMainNavbarVisible(true);
            }
            innerCL.show(innerBody, "PROFILE");
            cl.show(contentPanel, "Home");
        });

        profileWrap.add(profileName);
        profileWrap.add(logoutBtn);
        topHeader.add(profileWrap, BorderLayout.EAST);

        // --- 2. SIDEBAR ---
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UITheme.SIDEBAR);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(new EmptyBorder(22, 16, 0, 16));

        JLabel brand = new JLabel("MY DASHBOARD");
        brand.setFont(UITheme.font(Font.BOLD, 13));
        brand.setForeground(UITheme.TEXT_MUTED);
        brand.setBorder(new EmptyBorder(0, 12, 18, 0));
        sidebar.add(brand);

        profileItem = new UITheme.SidebarItem("Profile", UITheme.Icons.person(UITheme.SIDEBAR_ITEM));
        resultsItem = new UITheme.SidebarItem("Voting Results", UITheme.Icons.chart(UITheme.SIDEBAR_ITEM));

        profileItem.setActive(true);
        profileItem.addMouseListener(navClick("PROFILE"));
        resultsItem.addMouseListener(navClick("RESULT"));

        sidebar.add(profileItem);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(resultsItem);

        // --- 3. INNER PAGES ---
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

    private MouseAdapter navClick(String cardName) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!UserSession.isUsable()) {
                    UITheme.showMessage(Userdrashboard.this, "Session Expired",
                            "Your session has expired. Please log in again.",
                            JOptionPane.WARNING_MESSAGE);
                    cl.show(contentPanel, "Home");
                    return;
                }
                innerCL.show(innerBody, cardName);
                profileItem.setActive(cardName.equals("PROFILE"));
                resultsItem.setActive(cardName.equals("RESULT"));
            }
        };
    }

    private JPanel createViewPanel(String text) {
        JPanel p = new JPanel(new java.awt.GridBagLayout());
        p.setBackground(UITheme.SURFACE_MUTED);
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        p.add(lbl);
        return p;
    }
}

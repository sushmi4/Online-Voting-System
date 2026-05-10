import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class AdminDashboard extends JPanel {
    private CardLayout innerCL = new CardLayout();
    private JPanel bodyPanel = new JPanel(innerCL);
    private JPanel sidebar; // Global so toggle can access it
    private boolean isSidebarVisible = true;

    public AdminDashboard(CardLayout cl, JPanel contentPanel) {
        setLayout(new BorderLayout());

        // --- 1. Top Header Bar (Blue Bar) ---
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setBackground(new Color(63, 137, 187)); // Modern Blue
        topHeader.setPreferredSize(new Dimension(100, 60));
        topHeader.setBorder(new EmptyBorder(0, 15, 0, 20));

        // LEFT: Hamburger Menu & Title
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        leftHeader.setOpaque(false);

        JLabel lblMenuToggle = new JLabel("\u2630"); // Hamburger Icon
        lblMenuToggle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblMenuToggle.setForeground(Color.WHITE);
        lblMenuToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblSystem = new JLabel("Online Voting System");
        lblSystem.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblSystem.setForeground(Color.WHITE);

        leftHeader.add(lblMenuToggle);
        leftHeader.add(lblSystem);
        topHeader.add(leftHeader, BorderLayout.WEST);

        // RIGHT: Profile Dropdown
        JLabel profileDropdown = new JLabel("\uD83D\uDC64  \u25BC"); // User icon + Arrow
        profileDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        profileDropdown.setForeground(Color.WHITE);
        profileDropdown.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPopupMenu logoutMenu = new JPopupMenu();
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logoutItem.addActionListener(e -> {
            Window win = SwingUtilities.getWindowAncestor(this);
            if (win instanceof MainApp) {
                ((MainApp) win).setMainNavbarVisible(true);
            }
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

        // --- 2. Sidebar Navigation (Dark Navy) ---
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(40, 60, 70));
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
        sidebar.setBorder(new EmptyBorder(30, 20, 20, 20));

        // Sidebar Toggle Logic
        lblMenuToggle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                isSidebarVisible = !isSidebarVisible;
                sidebar.setVisible(isSidebarVisible);
                revalidate();
                repaint();
            }
        });

        // Initialize External Pages
        AdminViewVoter voterTablePage = new AdminViewVoter(cl, contentPanel);
        ViewResultPage resultPage = new ViewResultPage(cl, contentPanel);
        GroupApp groupPage = new GroupApp();

        // Menu Items
        String[] menuItems = { "HOME", "ADD GROUP", "VIEW RESULTS", "VIEW VOTERS" };
        for (String item : menuItems) {
            JLabel navItem = new JLabel(item);
            navItem.setFont(new Font("Segoe UI", Font.BOLD, 13));
            navItem.setForeground(Color.WHITE);
            navItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
            navItem.setAlignmentX(Component.LEFT_ALIGNMENT);
            navItem.setBorder(new EmptyBorder(15, 0, 15, 0));

            navItem.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    switch (item) {
                        case "HOME":
                            innerCL.show(bodyPanel, "Welcome");
                            break;
                        case "ADD GROUP":
                            innerCL.show(bodyPanel, "AddGroup");
                            break;
                        case "VIEW RESULTS":
                            resultPage.refreshResults();
                            innerCL.show(bodyPanel, "ViewResultPage");
                            break;
                        case "VIEW VOTERS":
                            voterTablePage.refreshData();
                            innerCL.show(bodyPanel, "VoterTable");
                            break;
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    navItem.setForeground(new Color(52, 152, 219));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    navItem.setForeground(Color.WHITE);
                }
            });
            sidebar.add(navItem);
        }

        // --- 3. Main Content Area ---
        bodyPanel.setBackground(Color.WHITE);
        JPanel welcomeScreen = new JPanel(new GridBagLayout());
        welcomeScreen.setBackground(Color.white);
        JLabel welcomeLbl = new JLabel(
                "<html><center><h1 style='color:#2c3e50;'>Admin Control Panel</h1>"
                        + "<p>Click the menu icon to hide the sidebar.</p></center></html>");
        welcomeScreen.add(welcomeLbl);

        bodyPanel.add(welcomeScreen, "Welcome");
        bodyPanel.add(voterTablePage, "VoterTable");
        bodyPanel.add(resultPage, "ViewResultPage");
        bodyPanel.add(groupPage, "AddGroup");

        // --- 4. Assembly ---
        add(topHeader, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(bodyPanel, BorderLayout.CENTER);
    }
}
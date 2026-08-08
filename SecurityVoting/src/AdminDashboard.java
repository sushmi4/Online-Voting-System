import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class AdminDashboard extends JPanel {
    private CardLayout innerCL = new CardLayout();
    private JPanel bodyPanel = new JPanel(innerCL);
    private JPanel sidebar;
    private boolean isSidebarVisible = true;
    private JLabel lblTotalVoters, lblApproved, lblGroups, lblVotes;

    public AdminDashboard(CardLayout cl, JPanel contentPanel) {
        setLayout(new BorderLayout());

        // --- 1. Top Header Bar ---
        JPanel topHeader = new UITheme.HeaderBar();
        topHeader.setLayout(new BorderLayout());
        topHeader.setPreferredSize(new Dimension(100, 62));
        topHeader.setBorder(new EmptyBorder(0, 18, 0, 20));

        // LEFT: Hamburger Menu & Title
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 14));
        leftHeader.setOpaque(false);

        JLabel lblMenuToggle = new JLabel(UITheme.Icons.menu(UITheme.WHITE));
        lblMenuToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblSystem = new JLabel("Online Voting System");
        lblSystem.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblSystem.setForeground(UITheme.WHITE);

        leftHeader.add(lblMenuToggle);
        leftHeader.add(lblSystem);
        topHeader.add(leftHeader, BorderLayout.WEST);

        // RIGHT: Profile name + visible Logout button
        JPanel profileWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        profileWrap.setOpaque(false);

        JLabel profileName = new JLabel("Administrator");
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
            cl.show(contentPanel, "Home");
        });

        profileWrap.add(profileName);
        profileWrap.add(logoutBtn);
        topHeader.add(profileWrap, BorderLayout.EAST);

        // --- 2. Sidebar Navigation ---
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UITheme.SIDEBAR);
        sidebar.setPreferredSize(new Dimension(240, getHeight()));
        sidebar.setBorder(new EmptyBorder(22, 16, 20, 16));

        JLabel brand = new JLabel("ADMIN PANEL");
        brand.setFont(UITheme.font(Font.BOLD, 13));
        brand.setForeground(UITheme.TEXT_MUTED);
        brand.setBorder(new EmptyBorder(0, 12, 18, 0));
        sidebar.add(brand);

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
        UITheme.SidebarItem home = new UITheme.SidebarItem("Home", UITheme.Icons.home(UITheme.SIDEBAR_ITEM));
        UITheme.SidebarItem groups = new UITheme.SidebarItem("Add Group", UITheme.Icons.list(UITheme.SIDEBAR_ITEM));
        UITheme.SidebarItem results = new UITheme.SidebarItem("View Results", UITheme.Icons.chart(UITheme.SIDEBAR_ITEM));
        UITheme.SidebarItem voters = new UITheme.SidebarItem("View Voters", UITheme.Icons.users(UITheme.SIDEBAR_ITEM));

        UITheme.SidebarItem[] items = { home, groups, results, voters };
        home.setActive(true);

        home.addMouseListener(menuClick("HOME", cl, contentPanel, resultPage, voterTablePage, items));
        groups.addMouseListener(menuClick("ADD GROUP", cl, contentPanel, resultPage, voterTablePage, items));
        results.addMouseListener(menuClick("VIEW RESULTS", cl, contentPanel, resultPage, voterTablePage, items));
        voters.addMouseListener(menuClick("VIEW VOTERS", cl, contentPanel, resultPage, voterTablePage, items));

        sidebar.add(home);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(groups);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(results);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(voters);

        sidebar.add(Box.createVerticalGlue());

        // --- 3. Main Content Area ---
        bodyPanel.setBackground(UITheme.SURFACE_MUTED);

        JPanel welcomeScreen = new JPanel(new BorderLayout());
        welcomeScreen.setBackground(UITheme.SURFACE_MUTED);
        welcomeScreen.setBorder(new EmptyBorder(36, 40, 36, 40));

        JPanel welcomeTop = new JPanel(new BorderLayout(0, 6));
        welcomeTop.setOpaque(false);
        JLabel welcomeLbl = new JLabel("Welcome back, Administrator");
        welcomeLbl.setFont(UITheme.font(Font.BOLD, 26));
        welcomeLbl.setForeground(UITheme.PRIMARY_DARK);
        JLabel welcomeSub = new JLabel("Here is an overview of your election");
        welcomeSub.setFont(UITheme.font(Font.PLAIN, 14));
        welcomeSub.setForeground(UITheme.TEXT_MUTED);
        welcomeTop.add(welcomeLbl, BorderLayout.NORTH);
        welcomeTop.add(welcomeSub, BorderLayout.SOUTH);

        welcomeScreen.add(welcomeTop, BorderLayout.NORTH);

        JPanel statsGrid = new JPanel(new GridLayout(2, 2, 18, 18));
        statsGrid.setOpaque(false);
        statsGrid.setBorder(new EmptyBorder(24, 0, 0, 0));

        lblTotalVoters = new JLabel("0");
        lblApproved = new JLabel("0");
        lblGroups = new JLabel("0");
        lblVotes = new JLabel("0");

        statsGrid.add(statCard(UITheme.Icons.users(UITheme.ACCENT), lblTotalVoters, "Registered Voters"));
        statsGrid.add(statCard(UITheme.Icons.check(UITheme.GREEN), lblApproved, "Approved Voters"));
        statsGrid.add(statCard(UITheme.Icons.list(UITheme.WARNING), lblGroups, "Voting Groups"));
        statsGrid.add(statCard(UITheme.Icons.chart(UITheme.RED), lblVotes, "Votes Cast"));

        welcomeScreen.add(statsGrid, BorderLayout.CENTER);

        bodyPanel.add(welcomeScreen, "Welcome");
        bodyPanel.add(voterTablePage, "VoterTable");
        bodyPanel.add(resultPage, "ViewResultPage");
        bodyPanel.add(groupPage, "AddGroup");

        // --- 4. Assembly ---
        add(topHeader, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(bodyPanel, BorderLayout.CENTER);

        refreshStats();
    }

    private JPanel statCard(Icon icon, JLabel value, String labelText) {
        UITheme.Card card = new UITheme.Card(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(new JLabel(icon), BorderLayout.WEST);

        value.setFont(UITheme.font(Font.BOLD, 28));
        value.setForeground(UITheme.PRIMARY_DARK);
        top.add(value, BorderLayout.EAST);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UITheme.font(Font.BOLD, 13));
        lbl.setForeground(UITheme.TEXT_MUTED);

        card.add(top, BorderLayout.NORTH);
        card.add(lbl, BorderLayout.SOUTH);
        return card;
    }

    private MouseAdapter menuClick(String action, CardLayout cl, JPanel contentPanel,
            ViewResultPage resultPage, AdminViewVoter voterTablePage, UITheme.SidebarItem[] items) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!UserSession.isUsable()) {
                    UITheme.showMessage(AdminDashboard.this, "Session Expired",
                            "Your session has expired. Please log in again.",
                            JOptionPane.WARNING_MESSAGE);
                    cl.show(contentPanel, "Home");
                    return;
                }
                switch (action) {
                    case "HOME":
                        refreshStats();
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
                for (UITheme.SidebarItem it : items) {
                    it.setActive(it.getText().equalsIgnoreCase(action.replace("_", " ")));
                }
            }
        };
    }

    private void refreshStats() {
        try (Connection conn = Database.getConnection();
                Statement st = conn.createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "SELECT COUNT(*) FROM voters")) {
                if (rs.next()) {
                    lblTotalVoters.setText(String.valueOf(rs.getInt(1)));
                }
            }
            try (ResultSet rs = st.executeQuery(
                    "SELECT COUNT(*) FROM voters WHERE status='Approved'")) {
                if (rs.next()) {
                    lblApproved.setText(String.valueOf(rs.getInt(1)));
                }
            }
            try (ResultSet rs = st.executeQuery(
                    "SELECT COUNT(*) FROM user_groups")) {
                if (rs.next()) {
                    lblGroups.setText(String.valueOf(rs.getInt(1)));
                }
            }
            try (ResultSet rs = st.executeQuery(
                    "SELECT COUNT(*) FROM votes WHERE vote_year=" + Config.electionYear())) {
                if (rs.next()) {
                    lblVotes.setText(String.valueOf(rs.getInt(1)));
                }
            }
        } catch (Exception ex) {
            System.err.println("Stats error: " + ex.getMessage());
        }
    }
}

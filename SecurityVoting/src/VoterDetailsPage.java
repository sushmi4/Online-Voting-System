import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class VoterDetailsPage extends JPanel {

    private JLabel valStatus, lblName, lblEmail, lblDob, lblVoterId, lblMobile, imgLabel;
    private DefaultTableModel model;
    private JTable table;
    private CardLayout cl;
    private JPanel contentPanel;

    public VoterDetailsPage(CardLayout cl, JPanel contentPanel) {

        this.cl = cl;
        this.contentPanel = contentPanel;

        setLayout(new BorderLayout());
        setBackground(UITheme.SURFACE_MUTED);

        JPanel wrapper = new JPanel(new BorderLayout(20, 0));
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(22, 22, 22, 22));

        // ================= PROFILE CARD =================
        UITheme.Card sidebar = new UITheme.Card();
        sidebar.setPreferredSize(new Dimension(320, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(24, 22, 24, 22));

        JLabel title = new JLabel("Voter Profile", JLabel.CENTER);
        title.setFont(UITheme.font(Font.BOLD, 22));
        title.setForeground(UITheme.PRIMARY_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(title);
        sidebar.add(Box.createRigidArea(new Dimension(0, 18)));

        imgLabel = new JLabel();
        imgLabel.setPreferredSize(new Dimension(130, 130));
        imgLabel.setMaximumSize(new Dimension(130, 130));
        imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imgLabel.setOpaque(true);
        imgLabel.setBackground(UITheme.SURFACE_MUTED);
        imgLabel.setBorder(new UITheme.RoundBorder(UITheme.BORDER, 20));
        updateProfileImage();

        sidebar.add(imgLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 24)));

        JPanel infoGrid = new JPanel(new GridLayout(0, 2, 5, 14));
        infoGrid.setOpaque(false);

        lblName = new JLabel(UserSession.getFullName());
        lblEmail = new JLabel(UserSession.getEmail());
        lblDob = new JLabel(UserSession.getDob());
        lblVoterId = new JLabel(UserSession.getVoterId());
        lblMobile = new JLabel(UserSession.getMobile());
        valStatus = new JLabel(UserSession.getStatus());

        addDetailRow(infoGrid, "Name:", lblName);
        addDetailRow(infoGrid, "Email:", lblEmail);
        addDetailRow(infoGrid, "DOB:", lblDob);
        addDetailRow(infoGrid, "Voter ID:", lblVoterId);
        addDetailRow(infoGrid, "Mobile:", lblMobile);

        JLabel statusLbl = new JLabel("Status:");
        statusLbl.setFont(UITheme.font(Font.BOLD, 14));
        statusLbl.setForeground(UITheme.TEXT_MUTED);
        infoGrid.add(statusLbl);
        infoGrid.add(UITheme.pillForStatus(UserSession.getStatus()));

        sidebar.add(infoGrid);
        sidebar.add(Box.createRigidArea(new Dimension(0, 22)));

        JButton btnEdit = UITheme.outlineButton("Edit Profile", UITheme.GREEN);
        btnEdit.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnEdit.addActionListener(e -> handleEditProfile());
        sidebar.add(btnEdit);

        wrapper.add(sidebar, BorderLayout.WEST);

        // ================= TABLE SECTION =================
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setOpaque(false);

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        tableHeader.setBorder(new EmptyBorder(0, 4, 14, 4));

        JLabel tableTitle = new JLabel("Available Groups for e-Voting");
        tableTitle.setFont(UITheme.font(Font.BOLD, 22));
        tableTitle.setForeground(UITheme.PRIMARY_DARK);
        tableHeader.add(tableTitle, BorderLayout.WEST);

        UITheme.Pill hint = new UITheme.Pill("One vote per voter", UITheme.TEXT_MUTED, UITheme.SURFACE_MUTED);
        tableHeader.add(hint, BorderLayout.EAST);
        mainContent.add(tableHeader, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[] { "S.No", "Group Image", "Group Name", "Action" }, 0) {

            public Class<?> getColumnClass(int col) {
                if (col == 1) {
                    return ImageIcon.class;
                }
                return Object.class;
            }

            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(64);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(0).setMaxWidth(70);
        table.getColumnModel().getColumn(1).setMaxWidth(100);
        table.getColumnModel().getColumn(3).setMaxWidth(140);
        table.getColumnModel().getColumn(3).setCellRenderer(new ActionRenderer());

        fetchGroupsFromDatabase();

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == 3) {
                    handleVoteAction(row);
                }
            }
        });

        JScrollPane sp = new JScrollPane(table);
        UITheme.styleScrollPane(sp);
        mainContent.add(sp, BorderLayout.CENTER);

        wrapper.add(mainContent, BorderLayout.CENTER);

        add(wrapper, BorderLayout.CENTER);
    }

    // ================= PROFILE IMAGE =================
    private void updateProfileImage() {
        String path = UserSession.getImagePath();
        if (path != null && new File(path).exists()) {
            Image img = new ImageIcon(path).getImage()
                    .getScaledInstance(130, 130, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(img));
            imgLabel.setText("");
        } else {
            imgLabel.setIcon(null);
            imgLabel.setText("No Photo");
            imgLabel.setHorizontalTextPosition(JLabel.CENTER);
            imgLabel.setVerticalTextPosition(JLabel.CENTER);
            imgLabel.setFont(UITheme.font(Font.PLAIN, 12));
        }
    }

    // ================= ADD ROW =================
    private void addDetailRow(JPanel panel, String label, JLabel value) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.font(Font.BOLD, 14));
        lbl.setForeground(UITheme.TEXT_MUTED);
        value.setFont(UITheme.font(Font.BOLD, 14));
        value.setForeground(UITheme.PRIMARY_DARK);
        panel.add(lbl);
        panel.add(value);
    }

    // ================= LOAD GROUPS =================
    private void fetchGroupsFromDatabase() {
        model.setRowCount(0);
        boolean alreadyVoted = VoteDAO.hasUserAlreadyVoted(UserSession.getVoterId());

        String query = "SELECT sn, group_name, group_image_path FROM user_groups ORDER BY sn";
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String sn = rs.getString("sn");
                String name = rs.getString("group_name");
                String imgPath = rs.getString("group_image_path");

                ImageIcon icon = null;
                if (imgPath != null && new File(imgPath).exists()) {
                    ImageIcon temp = new ImageIcon(imgPath);
                    Image img = temp.getImage().getScaledInstance(56, 56, Image.SCALE_SMOOTH);
                    icon = new ImageIcon(img);
                }

                String action = alreadyVoted ? "Voted" : "Vote Now";
                model.addRow(new Object[] { sn, icon, name, action });
            }
        } catch (Exception e) {
            UITheme.showMessage(this, "Error", e.getMessage(),
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= VOTE ACTION =================
    private void handleVoteAction(int row) {
        if (!UserSession.isUsable()) {
            UITheme.showMessage(this, "Session Expired",
                    "Your session has expired. Please log in again.",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            cl.show(contentPanel, "Home");
            return;
        }

        if (VoteDAO.hasUserAlreadyVoted(UserSession.getVoterId())) {
            UITheme.showMessage(this, "Voting Error",
                    "You have already voted in this election!",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        String groupName = model.getValueAt(row, 2).toString();

        boolean confirm = UITheme.confirm(this, "Confirm Vote",
                "Confirm vote for " + groupName + "?\n"
                        + "This action is final and cannot be undone.");

        if (!confirm) {
            return;
        }

        VoteDAO.VoteResult result = VoteDAO.castVote(UserSession.getVoterId(), groupName);
        switch (result) {
            case SUCCESS:
                UserSession.setStatus("Voted");
                fetchGroupsFromDatabase();
                UITheme.showMessage(this, "Success", "Vote Successful!",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
                break;
            case ALREADY_VOTED:
                fetchGroupsFromDatabase();
                UITheme.showMessage(this, "Voting Error",
                        "You have already voted in this election!",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                break;
            default:
                UITheme.showMessage(this, "Error",
                        "Voting failed. Please try again.",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                break;
        }
    }

    // ================= EDIT PROFILE =================
    private void handleEditProfile() {
        if (!UserSession.isUsable()) {
            UITheme.showMessage(this, "Session Expired",
                    "Your session has expired. Please log in again.",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            cl.show(contentPanel, "Home");
            return;
        }

        JTextField txtName = new JTextField(UserSession.getFullName());
        JTextField txtEmail = new JTextField(UserSession.getEmail());
        JTextField txtMobile = new JTextField(UserSession.getMobile());

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 10));
        panel.add(new JLabel("Name"));
        panel.add(txtName);
        panel.add(new JLabel("Email"));
        panel.add(txtEmail);
        panel.add(new JLabel("Mobile"));
        panel.add(txtMobile);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Edit Profile", JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String mobile = txtMobile.getText().trim();

        if (name.isEmpty() || email.isEmpty() || mobile.isEmpty()) {
            UITheme.showMessage(this, "Incomplete", "All fields are required.",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!mobile.matches("\\d{10}")) {
            UITheme.showMessage(this, "Mobile Error",
                    "Mobile number must be exactly 10 digits.",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (DBConnection.emailExistsExcept(email, UserSession.getEmail())) {
            UITheme.showMessage(this, "Email In Use",
                    "This email is already registered to another account.",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "UPDATE voters SET full_name=?, email=?, mobile=? WHERE voter_id=?";
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, mobile);
            ps.setString(4, UserSession.getVoterId());
            if (ps.executeUpdate() > 0) {
                UserSession.setFullName(name);
                UserSession.setEmail(email);
                UserSession.setMobile(mobile);
                lblName.setText(name);
                lblEmail.setText(email);
                lblMobile.setText(mobile);
                UITheme.showMessage(this, "Success", "Profile Updated",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            UITheme.showMessage(this, "Error",
                    "Update failed: " + e.getMessage(),
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= ACTION RENDER =================
    class ActionRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {

            setHorizontalAlignment(JLabel.CENTER);
            setBorder(new EmptyBorder(0, 0, 0, 0));

            if (value != null && value.toString().equals("Voted")) {
                UITheme.Pill pill = new UITheme.Pill("Voted", UITheme.GREEN, UITheme.GREEN_BG);
                return pill;
            }
            UITheme.Pill pill = new UITheme.Pill("Vote Now", UITheme.ACCENT, UITheme.ACCENT_LIGHT);
            pill.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            return pill;
        }
    }
}

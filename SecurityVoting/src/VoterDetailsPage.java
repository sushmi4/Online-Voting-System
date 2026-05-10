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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

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
        setBackground(Color.WHITE);

        JPanel wrapper = new JPanel(new BorderLayout(20, 0));
        wrapper.setBorder(new EmptyBorder(20, 20, 20, 20));

        // ================= SIDEBAR =================

        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(320, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(245, 245, 245));

        JLabel title = new JLabel("Voter Profile");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        // wrapper.add(sidebar, BorderLayout.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(title);

        sidebar.add(title);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        imgLabel = new JLabel();
        imgLabel.setPreferredSize(new Dimension(150, 150));
        imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateProfileImage();

        sidebar.add(imgLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 25)));

        JPanel infoGrid = new JPanel(new GridLayout(0, 2, 5, 15));
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
        addDetailRow(infoGrid, "Voting Status:", valStatus);

        sidebar.add(infoGrid);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton btnEdit = new JButton("Edit Profile");
        btnEdit.setBackground(new Color(40, 167, 69));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        btnEdit.addActionListener(e -> handleEditProfile());

        sidebar.add(btnEdit);

        wrapper.add(sidebar, BorderLayout.WEST);

        // ================= TABLE SECTION =================

        JPanel mainContent = new JPanel(new BorderLayout());

        JLabel tableTitle = new JLabel("Available Groups for e-Voting", JLabel.CENTER);
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));

        mainContent.add(tableTitle, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[] { "S.No", "Group Image", "Group Name", "Action" }, 0) {

            public Class<?> getColumnClass(int col) {
                if (col == 1)
                    return ImageIcon.class;
                return Object.class;
            }

            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(80);

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

        mainContent.add(new JScrollPane(table), BorderLayout.CENTER);

        wrapper.add(mainContent, BorderLayout.CENTER);

        add(wrapper, BorderLayout.CENTER);
    }

    // ================= PROFILE IMAGE =================

    private void updateProfileImage() {

        String path = UserSession.getImagePath();

        if (path != null && new File(path).exists()) {

            Image img = new ImageIcon(path).getImage()
                    .getScaledInstance(150, 150, Image.SCALE_SMOOTH);

            imgLabel.setIcon(new ImageIcon(img));

        } else {

            imgLabel.setText("No Photo");
        }
    }

    // ================= ADD ROW =================

    private void addDetailRow(JPanel panel, String label, JLabel value) {

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));

        panel.add(lbl);
        panel.add(value);
    }

    // ================= LOAD GROUPS =================

    private void fetchGroupsFromDatabase() {

        model.setRowCount(0);

        boolean alreadyVoted = VoteDAO.hasUserAlreadyVoted(UserSession.getVoterId());

        try {

            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/onlinevoting_db",
                    "root",
                    "");

            String query = "SELECT sn,group_name,group_image_path FROM user_groups";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {

                String sn = rs.getString("sn");
                String name = rs.getString("group_name");
                String imgPath = rs.getString("group_image_path");

                ImageIcon icon = null;

                if (imgPath != null) {

                    File file = new File(imgPath);

                    if (file.exists()) {

                        ImageIcon temp = new ImageIcon(imgPath);

                        Image img = temp.getImage()
                                .getScaledInstance(70, 70, Image.SCALE_SMOOTH);

                        icon = new ImageIcon(img);
                    }
                }

                String action = alreadyVoted ? "Voted" : "Vote Now";

                model.addRow(new Object[] { sn, icon, name, action });
            }

            conn.close();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ================= VOTE ACTION =================

    private void handleVoteAction(int row) {

        if (VoteDAO.hasUserAlreadyVoted(UserSession.getVoterId())) {

            JOptionPane.showMessageDialog(this,
                    "You have already voted!",
                    "Voting Error",
                    JOptionPane.ERROR_MESSAGE);

            return;
        }

        String groupName = model.getValueAt(row, 2).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Confirm vote for " + groupName + "?",
                "Confirm Vote",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {

            boolean success = VoteDAO.castVote(UserSession.getVoterId(), groupName);

            if (success) {

                UserSession.setStatus("Voted");

                valStatus.setText("Voted");
                valStatus.setForeground(new Color(0, 150, 0));

                fetchGroupsFromDatabase();

                JOptionPane.showMessageDialog(this, "Vote Successful!");
            }
        }
    }

    // ================= EDIT PROFILE =================

    private void handleEditProfile() {

        JTextField txtName = new JTextField(UserSession.getFullName());
        JTextField txtEmail = new JTextField(UserSession.getEmail());
        JTextField txtDob = new JTextField(UserSession.getDob());
        JTextField txtMobile = new JTextField(UserSession.getMobile());

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 10));

        panel.add(new JLabel("Name"));
        panel.add(txtName);

        panel.add(new JLabel("Email"));
        panel.add(txtEmail);

        panel.add(new JLabel("DOB"));
        panel.add(txtDob);

        panel.add(new JLabel("Mobile"));
        panel.add(txtMobile);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Edit Profile",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {

            try {

                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/onlinevoting_db",
                        "root",
                        "");

                String sql = "UPDATE voters SET full_name=?,email=?,dob=?,mobile=? WHERE voter_id=?";

                PreparedStatement ps = conn.prepareStatement(sql);

                ps.setString(1, txtName.getText());
                ps.setString(2, txtEmail.getText());
                ps.setString(3, txtDob.getText());
                ps.setString(4, txtMobile.getText());
                ps.setString(5, UserSession.getVoterId());

                ps.executeUpdate();

                UserSession.setFullName(txtName.getText());
                UserSession.setEmail(txtEmail.getText());
                UserSession.setDob(txtDob.getText());
                UserSession.setMobile(txtMobile.getText());

                lblName.setText(txtName.getText());
                lblEmail.setText(txtEmail.getText());
                lblDob.setText(txtDob.getText());
                lblMobile.setText(txtMobile.getText());

                JOptionPane.showMessageDialog(this, "Profile Updated");

                conn.close();

            } catch (Exception e) {

                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    // ================= ACTION RENDER =================

    class ActionRenderer extends DefaultTableCellRenderer {

        public Component getTableCellRendererComponent(
                JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {

            super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            setHorizontalAlignment(JLabel.CENTER);

            if (value != null && value.toString().equals("Voted")) {

                setForeground(new Color(0, 128, 0));
                setFont(new Font("Segoe UI", Font.BOLD, 13));

            } else {

                setForeground(Color.BLUE);
                setText("<html><u>Vote Now</u></html>");
            }

            return this;
        }
    }
}
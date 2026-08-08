import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class GroupApp extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private int snCounter = 1;

    public GroupApp() {
        setLayout(new BorderLayout());
        setBackground(UITheme.SURFACE_MUTED);

        // --- Header ---
        JPanel headerBar = new UITheme.HeaderBar();
        headerBar.setLayout(new BorderLayout());
        headerBar.setPreferredSize(new Dimension(0, 62));
        JLabel lblTitle = new JLabel("MANAGE VOTING GROUPS", JLabel.CENTER);
        lblTitle.setForeground(UITheme.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerBar.add(lblTitle, BorderLayout.CENTER);
        add(headerBar, BorderLayout.NORTH);

        // --- Table ---
        String[] columns = { "S.No", "Group Name", "Group Image" };
        model = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return (column == 2) ? Icon.class : Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        UITheme.styleTable(table);
        table.setRowHeight(70);
        table.getColumnModel().getColumn(0).setMaxWidth(70);
        table.getColumnModel().getColumn(2).setMaxWidth(110);

        JScrollPane scrollPane = new JScrollPane(table);
        UITheme.styleScrollPane(scrollPane);
        scrollPane.setBorder(new EmptyBorder(24, 36, 12, 36));

        // --- Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 18, 0));
        JButton btnAdd = UITheme.button("ADD NEW GROUP");
        JButton btnDelete = UITheme.outlineButton("DELETE SELECTED", UITheme.RED);
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);

        // --- Button Actions ---
        btnAdd.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Enter Group Name:");
            if (name != null && !name.trim().isEmpty()) {
                name = name.trim();
                if (groupExists(name)) {
                    UITheme.showMessage(this, "Duplicate Name",
                            "A group with this name already exists.",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Select Group Image (PNG/JPG, Max 1MB)");
                chooser.setFileFilter(new FileNameExtensionFilter("Image Files", "png", "jpg", "jpeg", "gif"));
                if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    if (!isAllowedImage(file)) {
                        UITheme.showMessage(this, "Invalid Image",
                                "Please select a PNG/JPG image smaller than 1MB.",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    saveData(name, file.getAbsolutePath());
                    loadData();
                }
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String name = table.getValueAt(row, 1).toString();
                if (groupHasVotes(name)) {
                    UITheme.showMessage(this, "Group In Use",
                            "Cannot delete a group that already received votes.",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                boolean ok = UITheme.confirm(this, "Delete Group",
                        "Delete " + name + "?");
                if (ok) {
                    deleteData(name);
                    loadData();
                }
            }
        });

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadData();
    }

    private boolean isAllowedImage(File file) {
        String lower = file.getName().toLowerCase();
        boolean extOk = lower.endsWith(".png") || lower.endsWith(".jpg")
                || lower.endsWith(".jpeg") || lower.endsWith(".gif");
        return extOk && file.length() <= 1024 * 1024;
    }

    private boolean groupExists(String name) {
        String sql = "SELECT COUNT(*) FROM user_groups WHERE group_name=?";
        try (Connection conn = Database.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, name);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private boolean groupHasVotes(String name) {
        String sql = "SELECT COUNT(*) FROM votes WHERE group_name=?";
        try (Connection conn = Database.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, name);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // --- Save Data ---
    private void saveData(String name, String path) {
        try (Connection conn = Database.getConnection();
                PreparedStatement pst = conn.prepareStatement(
                        "INSERT INTO user_groups (group_name, group_image_path) VALUES (?, ?)")) {
            pst.setString(1, name);
            pst.setString(2, path);
            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // --- Delete Data ---
    private void deleteData(String name) {
        try (Connection conn = Database.getConnection();
                PreparedStatement pst = conn.prepareStatement(
                        "DELETE FROM user_groups WHERE group_name = ?")) {
            pst.setString(1, name);
            pst.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    // --- Load Data ---
    private void loadData() {
        model.setRowCount(0);
        snCounter = 1;
        try (Connection conn = Database.getConnection();
                ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM user_groups ORDER BY sn")) {
            while (rs.next()) {
                String path = rs.getString("group_image_path");
                ImageIcon icon = null;
                if (path != null && new File(path).exists()) {
                    icon = new ImageIcon(new ImageIcon(path)
                            .getImage().getScaledInstance(46, 46, Image.SCALE_SMOOTH));
                }
                model.addRow(new Object[] { snCounter++, rs.getString("group_name"), icon });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

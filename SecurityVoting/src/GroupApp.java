import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class GroupApp extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private int snCounter = 1;

    private final Color BRAND_BLUE = new Color(0, 102, 204);
    private final String URL = "jdbc:mysql://localhost:3306/onlinevoting_db";
    private final String USER = "root";
    private final String PASS = "";

    public GroupApp() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // --- Header ---
        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setBackground(BRAND_BLUE);
        headerBar.setPreferredSize(new Dimension(0, 50));
        JLabel lblTitle = new JLabel("MANAGE VOTING GROUPS", JLabel.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerBar.add(lblTitle, BorderLayout.CENTER);

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
        table.setRowHeight(80);
        table.setGridColor(new Color(240, 240, 240));
        table.setShowVerticalLines(false);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(230, 240, 255));

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setBackground(Color.WHITE);
        tableHeader.setForeground(new Color(50, 50, 50));
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(20, 40, 20, 40));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // --- Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        JButton btnAdd = createStyledButton("ADD NEW GROUP", BRAND_BLUE);
        JButton btnDelete = createStyledButton("DELETE SELECTED", new Color(220, 53, 69));
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);

        // --- Button Actions ---
        btnAdd.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Enter Group Name:");
            if (name != null && !name.trim().isEmpty()) {
                JFileChooser chooser = new JFileChooser();
                if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    saveData(name, chooser.getSelectedFile().getAbsolutePath());
                    loadData();
                }
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String name = table.getValueAt(row, 1).toString();
                if (JOptionPane.showConfirmDialog(this, "Delete " + name + "?") == JOptionPane.YES_OPTION) {
                    deleteData(name);
                    loadData();
                }
            }
        });

        add(headerBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadData();
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return btn;
    }

    // --- Save Data ---
    private void saveData(String name, String path) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            // Changed 'image_path' to 'group_image_path'
            PreparedStatement pst = conn.prepareStatement(
                    "INSERT INTO user_groups (group_name, group_image_path) VALUES (?, ?)");
            pst.setString(1, name);
            pst.setString(2, path);
            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // --- Delete Data ---
    private void deleteData(String name) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            PreparedStatement pst = conn.prepareStatement(
                    "DELETE FROM user_groups WHERE group_name = ?");
            pst.setString(1, name);
            pst.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    // --- Load Data ---
    // Update Load Data method
    private void loadData() {
        model.setRowCount(0);
        snCounter = 1;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM user_groups");
            while (rs.next()) {
                // Changed 'image_path' to 'group_image_path'
                String path = rs.getString("group_image_path");
                ImageIcon icon = null;
                if (path != null && new File(path).exists()) {
                    icon = new ImageIcon(new ImageIcon(path)
                            .getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
                }
                model.addRow(new Object[] { snCounter++, rs.getString("group_name"), icon });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // --- Main method to test ---
    public static void main(String[] args) {
        JFrame frame = new JFrame("Group Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setContentPane(new GroupApp());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

public class AdminViewVoter extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;

    // Database Connection
    private final String DB_URL = "jdbc:mysql://localhost:3306/onlinevoting_db";
    private final String DB_USER = "root";
    private final String DB_PASS = "";

    // Brand Colors
    private final Color NAVBAR_BLUE = new Color(0, 102, 204);

    public AdminViewVoter(CardLayout cl, JPanel contentPanel) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // --- 1. TOP PANEL: Header + Search ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(NAVBAR_BLUE);

        JLabel header = new JLabel("VOTER REGISTRATION DATA", JLabel.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search Name: ");
        searchLabel.setForeground(Color.WHITE);
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        searchField = new JTextField(20);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                searchTable();
            }

            public void removeUpdate(DocumentEvent e) {
                searchTable();
            }

            public void changedUpdate(DocumentEvent e) {
                searchTable();
            }
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        topPanel.add(header, BorderLayout.CENTER);
        topPanel.add(searchPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // --- 2. TABLE SETUP ---
        String[] columns = { "User ID", "Photo", "Voter ID No", "Name", "Email", "DOB", "Address", "Phone No", "Status",
                "Action" };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 9;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return (column == 1) ? Icon.class : Object.class;
            }
        };

        table = new JTable(model);
        table.setRowHeight(100);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Header Styling
        table.getTableHeader().setBackground(NAVBAR_BLUE);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setReorderingAllowed(false);

        // Set Custom Renderer and Editor for the Action Column
        table.getColumnModel().getColumn(9).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(9).setCellEditor(new ButtonEditor(new JCheckBox()));

        add(new JScrollPane(table), BorderLayout.CENTER);
        refreshData();
    }

    private void searchTable() {
        String text = searchField.getText();
        if (text.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            // Index 3 is the "Name" column
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 3));
        }
    }

    public void refreshData() {
        model.setRowCount(0);
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM voters")) {

            while (rs.next()) {
                String path = rs.getString("image_path");
                ImageIcon icon = null;
                if (path != null && new File(path).exists()) {
                    ImageIcon temp = new ImageIcon(path);
                    Image img = temp.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
                    icon = new ImageIcon(img);
                }

                model.addRow(new Object[] {
                        rs.getInt("id"), icon, rs.getString("voter_id"),
                        rs.getString("full_name"), rs.getString("email"),
                        rs.getString("dob"), rs.getString("address"),
                        rs.getString("mobile"), rs.getString("status"), "Actions"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- INNER CLASS: ButtonRenderer ---
    class ButtonRenderer extends JPanel implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 15));
            String[] labels = { "APPROVE", "REJECT" };
            Color[] colors = { new Color(0, 128, 64), Color.RED };

            for (int i = 0; i < 2; i++) {
                JButton b = new JButton(labels[i]);
                b.setBackground(colors[i]);
                b.setForeground(Color.WHITE);
                b.setFont(new Font("Segoe UI", Font.BOLD, 9));
                add(b);
            }
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
            setBackground(isS ? t.getSelectionBackground() : t.getBackground());
            return this;
        }
    }

    // --- INNER CLASS: ButtonEditor ---
    class ButtonEditor extends DefaultCellEditor {
        protected JPanel panel;
        protected JButton btnApp, btnRej;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 15));

            btnApp = createBtn("APPROVE", new Color(0, 128, 64));
            btnRej = createBtn("REJECT", Color.RED);

            btnApp.addActionListener(
                    e -> performAction("UPDATE voters SET status = 'Approved' WHERE voter_id = ?", "Approved"));
            btnRej.addActionListener(
                    e -> performAction("UPDATE voters SET status = 'Rejected' WHERE voter_id = ?", "Rejected"));

            panel.add(btnApp);
            panel.add(btnRej);
        }

        private JButton createBtn(String txt, Color bg) {
            JButton b = new JButton(txt);
            b.setBackground(bg);
            b.setForeground(Color.WHITE);
            b.setFont(new Font("Segoe UI", Font.BOLD, 9));
            b.setFocusPainted(false);
            return b;
        }

        private void performAction(String sql, String actionName) {
            int row = table.getSelectedRow();
            if (row == -1)
                return;

            // Getting Voter ID from column index 2
            String voterId = table.getValueAt(row, 2).toString();

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                    PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, voterId);
                pst.executeUpdate();
                fireEditingStopped();
                refreshData();
                JOptionPane.showMessageDialog(null, "Voter " + actionName);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public Component getTableCellEditorComponent(JTable t, Object v, boolean isS, int r, int c) {
            panel.setBackground(t.getSelectionBackground());
            return panel;
        }
    }
}
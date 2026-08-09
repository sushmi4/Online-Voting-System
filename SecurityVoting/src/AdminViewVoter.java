import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import javax.swing.DefaultCellEditor;
import javax.swing.BorderFactory;
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

    public AdminViewVoter(CardLayout cl, JPanel contentPanel) {
        setLayout(new BorderLayout());
        setBackground(UITheme.SURFACE_MUTED);

        // --- 1. TOP PANEL: Header + Search ---
        JPanel topPanel = new UITheme.HeaderBar();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBorder(new javax.swing.border.EmptyBorder(18, 26, 18, 26));

        JLabel header = new JLabel("VOTER REGISTRATION DATA");
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.setForeground(UITheme.WHITE);
        topPanel.add(header, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel(UITheme.Icons.person(UITheme.WHITE));
        searchPanel.add(searchLabel);

        searchField = new UITheme.RoundedTextField(18);
        searchField.setToolTipText("Search by name");
        searchField.setPreferredSize(new Dimension(230, 38));
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

        searchPanel.add(searchField);
        topPanel.add(searchPanel, BorderLayout.EAST);
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
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        UITheme.styleTable(table);

        table.getColumnModel().getColumn(0).setMaxWidth(70);
        table.getColumnModel().getColumn(1).setMaxWidth(110);
        table.getColumnModel().getColumn(8).setMinWidth(100);
        table.getColumnModel().getColumn(8).setPreferredWidth(120);
        table.getColumnModel().getColumn(8).setMaxWidth(130);
        table.getColumnModel().getColumn(9).setMinWidth(190);
        table.getColumnModel().getColumn(9).setMaxWidth(220);

        table.getColumnModel().getColumn(8).setCellRenderer(new StatusBadgeRenderer());
        table.getColumnModel().getColumn(9).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(9).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane sp = new JScrollPane(table);
        UITheme.styleScrollPane(sp);
        add(sp, BorderLayout.CENTER);
        refreshData();
    }

    private void searchTable() {
        String text = searchField.getText();
        if (text.trim().isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }
        try {
            // Escape regex metacharacters to prevent regex injection / errors
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 3));
        } catch (java.util.regex.PatternSyntaxException ex) {
            sorter.setRowFilter(null);
        }
    }

    public void refreshData() {
        model.setRowCount(0);
        String query = "SELECT id, voter_id, full_name, email, dob, address, mobile, image_path, status"
                + " FROM voters ORDER BY id DESC";
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String path = rs.getString("image_path");
                ImageIcon icon = null;
                if (path != null && new File(path).exists()) {
                    ImageIcon temp = new ImageIcon(path);
                    Image img = temp.getImage().getScaledInstance(46, 46, Image.SCALE_SMOOTH);
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

    // --- INNER CLASS: Status Badge Renderer ---
    class StatusBadgeRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object value, boolean isS, boolean hasF, int r, int c) {
            super.getTableCellRendererComponent(t, value, isS, hasF, r, c);
            String status = value == null ? "" : value.toString();
            setText(status);
            setHorizontalAlignment(JLabel.CENTER);
            setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
            if (!isS) {
                setBackground(r % 2 == 0 ? UITheme.SURFACE : UITheme.TABLE_STRIPE);
            }
            setForeground(isS ? t.getSelectionForeground() : statusColor(status));
            return this;
        }

        private Color statusColor(String status) {
            if ("Approved".equalsIgnoreCase(status)) return UITheme.GREEN;
            if ("Rejected".equalsIgnoreCase(status)) return UITheme.RED;
            if ("Voted".equalsIgnoreCase(status)) return UITheme.ACCENT;
            return UITheme.WARNING;
        }
    }

    // --- INNER CLASS: ButtonRenderer ---
    class ButtonRenderer extends JPanel implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 10));
            JButton app = UITheme.button("Approve", UITheme.GREEN);
            app.setFont(UITheme.font(Font.BOLD, 11));
            JButton rej = UITheme.button("Reject", UITheme.RED);
            rej.setFont(UITheme.font(Font.BOLD, 11));
            add(app);
            add(rej);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
            setBackground(isS ? t.getSelectionBackground() : UITheme.SURFACE);
            return this;
        }
    }

    // --- INNER CLASS: ButtonEditor ---
    class ButtonEditor extends DefaultCellEditor {
        protected JPanel panel;
        protected JButton btnApp, btnRej;
        private int editingRow = -1;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 10));
            panel.setBackground(UITheme.SURFACE);

            btnApp = UITheme.button("Approve", UITheme.GREEN);
            btnApp.setFont(UITheme.font(Font.BOLD, 11));
            btnRej = UITheme.button("Reject", UITheme.RED);
            btnRej.setFont(UITheme.font(Font.BOLD, 11));

            btnApp.addActionListener(e -> performAction("UPDATE voters SET status = 'Approved' WHERE voter_id = ?", "Approved"));
            btnRej.addActionListener(e -> performAction("UPDATE voters SET status = 'Rejected' WHERE voter_id = ?", "Rejected"));

            panel.add(btnApp);
            panel.add(btnRej);
        }

        private void performAction(String sql, String actionName) {
            int row = editingRow;
            if (row == -1 || row >= table.getRowCount()) {
                return;
            }

            // Column 2 is the Voter ID (view index == model index, no row sorter mapping needed for value fetch)
            String voterId = table.getValueAt(row, 2).toString();

            try (Connection conn = Database.getConnection();
                    PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, voterId);
                pst.executeUpdate();
                fireEditingStopped();
                refreshData();
                UITheme.showMessage(AdminViewVoter.this, "Voter " + actionName,
                        "Voter " + actionName, JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                UITheme.showMessage(AdminViewVoter.this, "Error",
                        "Action failed: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            }
        }

        @Override
        public Component getTableCellEditorComponent(JTable t, Object v, boolean isS, int r, int c) {
            editingRow = r;
            panel.setBackground(t.getSelectionBackground());
            return panel;
        }
    }
}

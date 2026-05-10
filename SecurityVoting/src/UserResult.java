import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class UserResult extends JPanel {
    private JLabel lblTotalVotes, lblWinner;
    private DefaultTableModel tableModel;

    public UserResult(CardLayout cl, JPanel cardPanel) {
        // 1. Make the panel transparent to show the MainApp background
        setOpaque(false);
        setLayout(new BorderLayout());

        // 2. Wrap everything in a semi-transparent "Light White" container
        JPanel mainContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Pure White with 210 Alpha for transparency
                g2.setColor(new Color(255, 255, 255, 180));
                g2.fillRoundRect(20, 20, getWidth() - 40, getHeight() - 40, 40, 40);
                g2.dispose();
            }
        };
        mainContainer.setOpaque(false);
        mainContainer.setLayout(new BorderLayout());
        mainContainer.setBorder(new EmptyBorder(50, 60, 50, 60));

        // Header Section
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        headerPanel.setOpaque(false);

        lblTotalVotes = new JLabel("Total Votes Cast = 0");
        lblTotalVotes.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTotalVotes.setForeground(new Color(0, 123, 200));

        lblWinner = new JLabel("Highest Vote Cast to - None");
        lblWinner.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblWinner.setForeground(new Color(40, 167, 69)); // Green for winner info

        headerPanel.add(lblTotalVotes);
        headerPanel.add(lblWinner);
        mainContainer.add(headerPanel, BorderLayout.NORTH);

        // Table Section
        String[] columns = { "S.No", "Group Name", "Total Votes" };
        tableModel = new DefaultTableModel(columns, 0);
        JTable resultTable = new JTable(tableModel);
        resultTable.setRowHeight(35);
        resultTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        mainContainer.add(scrollPane, BorderLayout.CENTER);

        add(mainContainer, BorderLayout.CENTER);

        // --- FIX: Trigger the database fetch immediately when panel loads ---
        // refreshResults();
    }

    public void refreshResults() {
        tableModel.setRowCount(0); // Clear old data
        int total = 0;
        String winner = "None";
        int max = -1;

        // DB Connection
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/onlinevoting_db", "root", "")) {
            // Query to count votes per group
            String query = "SELECT group_name, COUNT(*) as count FROM votes GROUP BY group_name ORDER BY count DESC";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);

            int i = 1;
            while (rs.next()) {
                String group = rs.getString("group_name");
                int count = rs.getInt("count");

                tableModel.addRow(new Object[] { i++, group, count }); // Update table

                total += count;
                if (count > max) {
                    max = count;
                    winner = group;
                }
            }

            // Update the UI labels
            lblTotalVotes.setText("Total Votes Cast = " + total);
            lblWinner.setText("Highest Vote Cast to - " + winner + " (" + (max == -1 ? 0 : max) + ")");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Result Refresh Error: " + e.getMessage());
        }
    }
}
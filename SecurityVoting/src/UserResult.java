import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
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
        setOpaque(false);
        setLayout(new GridBagLayout());

        UITheme.Card mainContainer = new UITheme.Card(new BorderLayout());
        mainContainer.setBorder(new EmptyBorder(36, 44, 36, 44));
        mainContainer.setPreferredSize(new Dimension(760, 520));

        // Header Section
        JPanel headerPanel = new JPanel(new BorderLayout(0, 10));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 18, 0));

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        JLabel title = new JLabel("LIVE ELECTION RESULTS");
        title.setFont(UITheme.font(Font.BOLD, 24));
        title.setForeground(UITheme.PRIMARY_DARK);
        titleRow.add(title, BorderLayout.WEST);
        headerPanel.add(titleRow, BorderLayout.NORTH);

        JPanel statRow = new JPanel(new BorderLayout(0, 8));
        statRow.setOpaque(false);
        lblTotalVotes = new JLabel("Total Votes Cast = 0");
        lblTotalVotes.setFont(UITheme.font(Font.BOLD, 16));
        lblTotalVotes.setForeground(UITheme.ACCENT);

        lblWinner = new JLabel("Highest Vote Cast to - None");
        lblWinner.setFont(UITheme.font(Font.BOLD, 15));
        lblWinner.setForeground(UITheme.GREEN);

        statRow.add(lblTotalVotes, BorderLayout.NORTH);
        statRow.add(lblWinner, BorderLayout.SOUTH);
        headerPanel.add(statRow, BorderLayout.CENTER);

        mainContainer.add(headerPanel, BorderLayout.NORTH);

        // Table Section
        String[] columns = { "S.No", "Group Name", "Total Votes" };
        tableModel = new DefaultTableModel(columns, 0);
        JTable resultTable = new JTable(tableModel);
        UITheme.styleTable(resultTable);
        resultTable.setRowHeight(40);
        resultTable.getColumnModel().getColumn(0).setMaxWidth(80);
        resultTable.getColumnModel().getColumn(2).setMaxWidth(140);

        JScrollPane scrollPane = new JScrollPane(resultTable);
        UITheme.styleScrollPane(scrollPane);
        mainContainer.add(scrollPane, BorderLayout.CENTER);

        add(mainContainer);

        refreshResults();
    }

    public void refreshResults() {
        tableModel.setRowCount(0);
        int total = 0;
        String winner = "None";
        int max = -1;

        Map<String, Integer> results = VoteDAO.getResults();
        int i = 1;
        for (Map.Entry<String, Integer> entry : results.entrySet()) {
            int count = entry.getValue();
            tableModel.addRow(new Object[] { i++, entry.getKey(), count });
            total += count;
            if (count > max) {
                max = count;
                winner = entry.getKey();
            }
        }

        lblTotalVotes.setText("Total Votes Cast = " + total);
        lblWinner.setText("Highest Vote Cast to - " + winner + " (" + (max == -1 ? 0 : max) + ")");
    }
}

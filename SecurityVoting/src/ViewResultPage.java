import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

public class ViewResultPage extends JPanel {
    private JPanel listPanel;
    private CardLayout cl;
    private JPanel contentPanel;

    public ViewResultPage(CardLayout cl, JPanel contentPanel) {
        this.cl = cl;
        this.contentPanel = contentPanel;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header
        JLabel header = new JLabel("LIVE ELECTION RESULTS", JLabel.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        header.setBackground(new Color(0, 102, 204));
        header.setPreferredSize(new Dimension(0, 60));
        add(header, BorderLayout.NORTH);

        // Results Area
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        listPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshResults() {
        listPanel.removeAll();
        Map<String, Integer> data = VoteDAO.getResults();

        int totalVotesCast = 0;
        String highestGroup = "N/A";
        int maxVotes = -1;

        // Calculate totals and find winner
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            totalVotesCast += entry.getValue();
            if (entry.getValue() > maxVotes) {
                maxVotes = entry.getValue();
                highestGroup = entry.getKey();
            }
        }

        // --- SUMMARY SECTION ---
        JLabel lblTotal = new JLabel("Total Votes Cast: " + totalVotesCast);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotal.setBorder(new EmptyBorder(0, 0, 5, 0));
        listPanel.add(lblTotal);

        JLabel lblHighest = new JLabel("Current Leader: " + highestGroup + " (" + maxVotes + " votes)");
        lblHighest.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHighest.setForeground(new Color(0, 153, 76)); // Dark Green
        lblHighest.setBorder(new EmptyBorder(0, 0, 30, 0));
        listPanel.add(lblHighest);

        // --- TABLE HEADER ---
        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setMaximumSize(new Dimension(1000, 40));
        tableHeader.setBackground(new Color(245, 245, 245));
        tableHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));

        JLabel hSno = new JLabel(" S.No", JLabel.LEFT);
        hSno.setPreferredSize(new Dimension(60, 20));
        hSno.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel hGroup = new JLabel("Group Performance");
        hGroup.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel hVotes = new JLabel("Votes ", JLabel.RIGHT);
        hVotes.setPreferredSize(new Dimension(100, 20));
        hVotes.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tableHeader.add(hSno, BorderLayout.WEST);
        tableHeader.add(hGroup, BorderLayout.CENTER);
        tableHeader.add(hVotes, BorderLayout.EAST);
        listPanel.add(tableHeader);

        // --- DATA ROWS ---
        int sno = 1;
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            JPanel row = new JPanel(new BorderLayout(15, 0));
            row.setMaximumSize(new Dimension(1000, 60));
            row.setBackground(Color.WHITE);
            row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
            row.setOpaque(true);

            // S.No
            JLabel lblSno = new JLabel("  " + sno++);
            lblSno.setPreferredSize(new Dimension(60, 20));
            row.add(lblSno, BorderLayout.WEST);

            // Center Panel: Name + Bar
            JPanel centerPanel = new JPanel(new GridLayout(2, 1));
            centerPanel.setBackground(Color.WHITE);

            JLabel lblName = new JLabel(entry.getKey());
            lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));

            // The "Bar Line" (Progress Bar)
            JProgressBar bar = new JProgressBar(0, totalVotesCast == 0 ? 100 : totalVotesCast);
            bar.setValue(entry.getValue());
            bar.setStringPainted(true); // Shows %
            bar.setForeground(new Color(51, 153, 255)); // Blue bar
            bar.setBackground(new Color(230, 230, 230));

            centerPanel.add(lblName);
            centerPanel.add(bar);
            row.add(centerPanel, BorderLayout.CENTER);

            // Total Count
            JLabel lblCount = new JLabel(String.valueOf(entry.getValue()) + "  ", JLabel.RIGHT);
            lblCount.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lblCount.setPreferredSize(new Dimension(100, 20));
            row.add(lblCount, BorderLayout.EAST);

            listPanel.add(row);
        }

        listPanel.revalidate();
        listPanel.repaint();
    }
}
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

public class ViewResultPage extends JPanel {
    private JPanel listPanel;
    private JLabel lblTotalVotes, lblLeader, lblLeaderVotes;

    public ViewResultPage(CardLayout cl, JPanel contentPanel) {
        setLayout(new BorderLayout());
        setBackground(UITheme.SURFACE_MUTED);

        // Header
        JPanel header = new UITheme.HeaderBar();
        header.setLayout(new BorderLayout());
        header.setPreferredSize(new Dimension(0, 62));
        JLabel headerText = new JLabel("LIVE ELECTION RESULTS", JLabel.CENTER);
        headerText.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerText.setForeground(UITheme.WHITE);
        header.add(headerText, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // Summary cards
        JPanel summary = new JPanel(new GridLayout(1, 2, 18, 0));
        summary.setOpaque(false);
        summary.setBorder(new EmptyBorder(24, 30, 4, 30));

        UITheme.Card totalCard = new UITheme.Card(new BorderLayout(0, 6));
        totalCard.setBorder(new EmptyBorder(18, 20, 18, 20));
        JLabel totalTitle = new JLabel("TOTAL VOTES CAST");
        totalTitle.setFont(UITheme.font(Font.BOLD, 12));
        totalTitle.setForeground(UITheme.TEXT_MUTED);
        lblTotalVotes = new JLabel("0");
        lblTotalVotes.setFont(UITheme.font(Font.BOLD, 28));
        lblTotalVotes.setForeground(UITheme.ACCENT);
        totalCard.add(totalTitle, BorderLayout.NORTH);
        totalCard.add(lblTotalVotes, BorderLayout.CENTER);

        UITheme.Card leaderCard = new UITheme.Card(new BorderLayout(0, 6));
        leaderCard.setBorder(new EmptyBorder(18, 20, 18, 20));
        JLabel leaderTitle = new JLabel("CURRENT LEADER");
        leaderTitle.setFont(UITheme.font(Font.BOLD, 12));
        leaderTitle.setForeground(UITheme.TEXT_MUTED);
        JPanel leaderRow = new JPanel(new BorderLayout(10, 0));
        leaderRow.setOpaque(false);
        lblLeader = new JLabel("N/A");
        lblLeader.setFont(UITheme.font(Font.BOLD, 22));
        lblLeader.setForeground(UITheme.GREEN);
        lblLeaderVotes = new JLabel("0 votes");
        lblLeaderVotes.setFont(UITheme.font(Font.BOLD, 14));
        lblLeaderVotes.setForeground(UITheme.TEXT_MUTED);
        leaderRow.add(lblLeader, BorderLayout.WEST);
        leaderRow.add(lblLeaderVotes, BorderLayout.EAST);
        leaderCard.add(leaderTitle, BorderLayout.NORTH);
        leaderCard.add(leaderRow, BorderLayout.CENTER);

        summary.add(totalCard);
        summary.add(leaderCard);
        add(summary, BorderLayout.NORTH);

        // Results area
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        listPanel.setBorder(new EmptyBorder(16, 40, 20, 40));

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        UITheme.styleScrollPane(scrollPane);
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

        lblTotalVotes.setText(String.valueOf(totalVotesCast));
        lblLeader.setText(highestGroup);
        lblLeaderVotes.setText((maxVotes < 0 ? 0 : maxVotes) + " votes");

        if (data.isEmpty()) {
            JLabel empty = new JLabel("No results available yet.", JLabel.CENTER);
            empty.setFont(UITheme.font(Font.PLAIN, 15));
            empty.setForeground(UITheme.TEXT_MUTED);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(Box.createRigidArea(new Dimension(0, 40)));
            listPanel.add(empty);
            listPanel.revalidate();
            listPanel.repaint();
            return;
        }

        // --- COLUMN HEADER ---
        JPanel colHeader = new JPanel(new BorderLayout());
        colHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        colHeader.setOpaque(false);
        colHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER));

        JLabel hSno = new JLabel("  #");
        hSno.setPreferredSize(new Dimension(50, 24));
        hSno.setFont(UITheme.font(Font.BOLD, 12));
        hSno.setForeground(UITheme.TEXT_MUTED);

        JLabel hGroup = new JLabel("Group Performance");
        hGroup.setFont(UITheme.font(Font.BOLD, 12));
        hGroup.setForeground(UITheme.TEXT_MUTED);

        JLabel hVotes = new JLabel("Votes ", JLabel.RIGHT);
        hVotes.setPreferredSize(new Dimension(90, 24));
        hVotes.setFont(UITheme.font(Font.BOLD, 12));
        hVotes.setForeground(UITheme.TEXT_MUTED);

        colHeader.add(hSno, BorderLayout.WEST);
        colHeader.add(hGroup, BorderLayout.CENTER);
        colHeader.add(hVotes, BorderLayout.EAST);
        listPanel.add(colHeader);
        listPanel.add(Box.createRigidArea(new Dimension(0, 6)));

        // --- DATA ROWS ---
        int sno = 1;
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            boolean isLeader = entry.getValue() == maxVotes && maxVotes > 0;

            JPanel row = new JPanel(new BorderLayout(14, 0));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
            row.setOpaque(true);
            row.setBackground(isLeader ? UITheme.ACCENT_LIGHT : UITheme.SURFACE);
            row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
                    new EmptyBorder(10, 8, 10, 8)));

            JLabel lblSno = new JLabel("  " + sno++);
            lblSno.setPreferredSize(new Dimension(50, 24));
            lblSno.setFont(UITheme.font(Font.BOLD, 13));
            lblSno.setForeground(UITheme.TEXT_MUTED);
            row.add(lblSno, BorderLayout.WEST);

            JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 4));
            centerPanel.setOpaque(false);

            JPanel nameRow = new JPanel(new BorderLayout());
            nameRow.setOpaque(false);
            JLabel lblName = new JLabel(entry.getKey());
            lblName.setFont(UITheme.font(isLeader ? Font.BOLD : Font.BOLD, 14));
            lblName.setForeground(isLeader ? UITheme.ACCENT : UITheme.TEXT_DARK);
            nameRow.add(lblName, BorderLayout.WEST);
            if (isLeader) {
                UITheme.Pill leader = new UITheme.Pill("LEADER", UITheme.ACCENT, UITheme.WHITE);
                nameRow.add(leader, BorderLayout.EAST);
            }
            centerPanel.add(nameRow);

            UITheme.RoundedProgressBar bar = new UITheme.RoundedProgressBar(0, totalVotesCast == 0 ? 100 : totalVotesCast);
            bar.setValue(entry.getValue());
            bar.setString(entry.getValue() + " / " + totalVotesCast);
            if (isLeader) {
                bar.setForeground(UITheme.GREEN);
            }
            centerPanel.add(bar);

            row.add(centerPanel, BorderLayout.CENTER);

            JLabel lblCount = new JLabel(String.valueOf(entry.getValue()) + "  ", JLabel.RIGHT);
            lblCount.setFont(UITheme.font(Font.BOLD, 16));
            lblCount.setForeground(isLeader ? UITheme.GREEN : UITheme.TEXT_DARK);
            lblCount.setPreferredSize(new Dimension(90, 24));
            row.add(lblCount, BorderLayout.EAST);

            listPanel.add(row);
        }

        listPanel.revalidate();
        listPanel.repaint();
    }
}

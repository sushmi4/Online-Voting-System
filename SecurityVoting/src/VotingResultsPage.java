import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class VotingResultsPage extends JPanel {
    private JLabel lblTotalVotersLimit, lblCountVotesData, lblWinnerName, lblWinnerVotes;
    private JPanel groupsContainer;
    private JScrollPane scrollPane;

    public VotingResultsPage() {
        setLayout(new BorderLayout());
        setBackground(UITheme.SURFACE_MUTED);

        // --- Top Panel (Statistics) ---
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 28, 26));
        statsPanel.setOpaque(false);

        JPanel boxTotal = createStatBox("Voting Statistics", UITheme.ACCENT);
        JPanel totalContent = new JPanel(new GridLayout(2, 1));
        totalContent.setOpaque(false);
        lblTotalVotersLimit = new JLabel("Total Voters = " + Config.voterLimit(), JLabel.CENTER);
        lblTotalVotersLimit.setFont(UITheme.font(Font.BOLD, 14));
        lblTotalVotersLimit.setForeground(UITheme.TEXT_MUTED);
        lblCountVotesData = new JLabel("Count Votes = 0", JLabel.CENTER);
        lblCountVotesData.setFont(UITheme.font(Font.BOLD, 20));
        lblCountVotesData.setForeground(UITheme.ACCENT);
        totalContent.add(lblTotalVotersLimit);
        totalContent.add(lblCountVotesData);
        boxTotal.add(totalContent, BorderLayout.CENTER);

        JPanel boxWinner = createStatBox("Wins in Group", UITheme.GREEN);
        JPanel winnerContent = new JPanel(new GridLayout(2, 1));
        winnerContent.setOpaque(false);
        lblWinnerName = new JLabel("None", JLabel.CENTER);
        lblWinnerVotes = new JLabel("0", JLabel.CENTER);
        lblWinnerName.setFont(UITheme.font(Font.PLAIN, 18));
        lblWinnerName.setForeground(UITheme.TEXT_DARK);
        lblWinnerVotes.setFont(UITheme.font(Font.BOLD, 26));
        lblWinnerVotes.setForeground(UITheme.GREEN);
        winnerContent.add(lblWinnerName);
        winnerContent.add(lblWinnerVotes);
        boxWinner.add(winnerContent, BorderLayout.CENTER);

        statsPanel.add(boxTotal);
        statsPanel.add(boxWinner);
        add(statsPanel, BorderLayout.NORTH);

        // --- Center Panel (Dynamic Group Cards) ---
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);

        JPanel groupHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
        groupHeader.setOpaque(false);
        JLabel gh = new JLabel("Live Group Counts");
        gh.setFont(UITheme.font(Font.BOLD, 16));
        gh.setForeground(UITheme.PRIMARY_DARK);
        groupHeader.add(gh);
        center.add(groupHeader, BorderLayout.NORTH);

        groupsContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 22, 22));
        groupsContainer.setOpaque(false);
        groupsContainer.setBorder(new EmptyBorder(0, 0, 20, 0));

        scrollPane = new JScrollPane(groupsContainer);
        UITheme.styleScrollPane(scrollPane);
        center.add(scrollPane, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        refreshData();

        // Auto-refresh every 5 seconds, but skip work while hidden
        Timer timer = new Timer(5000, e -> {
            if (isShowing()) {
                refreshData();
            }
        });
        timer.start();
    }

    private JPanel createStatBox(String titleText, Color accent) {
        UITheme.Card box = new UITheme.Card(new BorderLayout());
        box.setPreferredSize(new Dimension(320, 140));
        box.setBorder(new EmptyBorder(16, 18, 16, 18));
        JLabel title = new JLabel(titleText, JLabel.CENTER);
        title.setFont(UITheme.font(Font.BOLD, 13));
        title.setForeground(accent);
        box.add(title, BorderLayout.NORTH);
        return box;
    }

    private JPanel createGroupCard(String name, String path, String count, boolean isWinner) {
        UITheme.Card card = new UITheme.Card(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(16, 18, 16, 18));
        card.setPreferredSize(new Dimension(150, 190));

        JLabel imageLabel = new JLabel("", JLabel.CENTER);
        imageLabel.setOpaque(true);
        imageLabel.setBackground(UITheme.SURFACE_MUTED);
        imageLabel.setPreferredSize(new Dimension(116, 116));

        if (path != null && !path.isEmpty()) {
            File imgFile = new File(path);
            if (imgFile.exists()) {
                ImageIcon icon = new ImageIcon(path);
                Image img = icon.getImage().getScaledInstance(116, 116, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(img));
                imageLabel.setText("");
            } else {
                imageLabel.setText("Not Found");
            }
        }

        JLabel nameLabel = new JLabel(name, JLabel.CENTER);
        nameLabel.setFont(UITheme.font(Font.BOLD, 13));
        nameLabel.setForeground(UITheme.PRIMARY_DARK);

        JLabel countLabel = new JLabel(count, JLabel.CENTER);
        countLabel.setFont(UITheme.font(Font.BOLD, 24));
        countLabel.setForeground(isWinner ? UITheme.GREEN : UITheme.ACCENT);

        card.add(imageLabel, BorderLayout.CENTER);
        card.add(nameLabel, BorderLayout.NORTH);
        card.add(countLabel, BorderLayout.SOUTH);
        return card;
    }

    public void refreshData() {
        int year = Config.electionYear();
        try (Connection conn = Database.getConnection()) {

            // 1. Overall Vote Count
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM votes WHERE vote_year = ?")) {
                ps.setInt(1, year);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int count = rs.getInt(1);
                        lblCountVotesData.setText("Count Votes = " + count);
                        lblCountVotesData.setForeground(
                                count >= Config.voterLimit() ? UITheme.RED : UITheme.ACCENT);
                    }
                }
            }

            // 2. Current Winner
            String winner = "None";
            int winnerCount = 0;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT group_name, COUNT(*) as total FROM votes"
                            + " WHERE vote_year = ? GROUP BY group_name ORDER BY total DESC LIMIT 1")) {
                ps.setInt(1, year);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        winner = rs.getString("group_name");
                        winnerCount = rs.getInt("total");
                    }
                }
            }
            lblWinnerName.setText(winner);
            lblWinnerVotes.setText(String.valueOf(winnerCount));

            // 3. Icon Row (JOIN user_groups with votes)
            groupsContainer.removeAll();
            int maxCount = -1;
            String query = "SELECT ug.group_image_path, ug.group_name, COUNT(v.id) as vote_count "
                    + "FROM user_groups ug "
                    + "LEFT JOIN votes v ON ug.group_name = v.group_name AND v.vote_year = ? "
                    + "GROUP BY ug.group_name, ug.group_image_path";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, year);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int voteCount = rs.getInt("vote_count");
                        if (voteCount > maxCount) {
                            maxCount = voteCount;
                        }
                    }
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, year);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String path = rs.getString("group_image_path");
                        String name = rs.getString("group_name");
                        int voteCount = rs.getInt("vote_count");
                        boolean isWinner = maxCount > 0 && voteCount == maxCount;
                        groupsContainer.add(createGroupCard(name, path,
                                String.valueOf(voteCount), isWinner));
                    }
                }
            }

            groupsContainer.revalidate();
            groupsContainer.repaint();

        } catch (SQLException e) {
            System.err.println("DB Error: " + e.getMessage());
        }
    }
}

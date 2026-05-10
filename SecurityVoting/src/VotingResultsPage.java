import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

public class VotingResultsPage extends JPanel {
    private JLabel lblTotalVotersLimit, lblCountVotesData, lblWinnerName, lblWinnerVotes;
    private JPanel groupsContainer;
    private JScrollPane scrollPane;

    private final String URL = "jdbc:mysql://localhost:3306/onlinevoting_db";
    private final String USER = "root";
    private final String PASS = "";
    private final int VOTER_LIMIT = 2000;

    public VotingResultsPage() {
        setLayout(new BorderLayout());
        setBackground(new Color(211, 228, 250));

        // --- Top Panel (Statistics) ---
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 40));
        statsPanel.setOpaque(false);

        JPanel boxTotal = createStatBox("Voting Statistics");
        JPanel totalContent = new JPanel(new GridLayout(2, 1));
        totalContent.setOpaque(false);
        lblTotalVotersLimit = new JLabel("Total Voters = " + VOTER_LIMIT, JLabel.CENTER);
        lblTotalVotersLimit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblCountVotesData = new JLabel("Count Votes = 0", JLabel.CENTER);
        lblCountVotesData.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblCountVotesData.setForeground(new Color(0, 51, 153));
        totalContent.add(lblTotalVotersLimit);
        totalContent.add(lblCountVotesData);
        boxTotal.add(totalContent, BorderLayout.CENTER);

        JPanel boxWinner = createStatBox("Wins in Group");
        JPanel winnerContent = new JPanel(new GridLayout(2, 1));
        winnerContent.setOpaque(false);
        lblWinnerName = new JLabel("None", JLabel.CENTER);
        lblWinnerVotes = new JLabel("0", JLabel.CENTER);
        lblWinnerName.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblWinnerVotes.setFont(new Font("Segoe UI", Font.BOLD, 24));
        winnerContent.add(lblWinnerName);
        winnerContent.add(lblWinnerVotes);
        boxWinner.add(winnerContent, BorderLayout.CENTER);

        statsPanel.add(boxTotal);
        statsPanel.add(boxWinner);
        add(statsPanel, BorderLayout.NORTH);

        // --- Center Panel (Dynamic Group Icons) ---
        groupsContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 35, 20));
        groupsContainer.setOpaque(false);

        scrollPane = new JScrollPane(groupsContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        refreshData();

        // Auto-refresh every 5 seconds
        Timer timer = new Timer(5000, e -> refreshData());
        timer.start();
    }

    private JPanel createStatBox(String titleText) {
        JPanel box = new JPanel(new BorderLayout());
        box.setPreferredSize(new Dimension(300, 140));
        box.setBackground(new Color(190, 215, 250));
        box.setBorder(new LineBorder(Color.BLACK, 1));
        JLabel title = new JLabel(titleText, JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        box.add(title, BorderLayout.NORTH);
        return box;
    }

    // Helper method to create an Icon Card using a File Path
    private JPanel createGroupCard(String path, String count) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(130, 160));

        JLabel imageLabel = new JLabel("", JLabel.CENTER);

        if (path != null && !path.isEmpty()) {
            File imgFile = new File(path);
            if (imgFile.exists()) {
                ImageIcon icon = new ImageIcon(path);
                Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(img));
            } else {
                imageLabel.setText("Not Found");
            }
        }

        JLabel countLabel = new JLabel(count, JLabel.CENTER);
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));

        card.add(imageLabel, BorderLayout.CENTER);
        card.add(countLabel, BorderLayout.SOUTH);
        return card;
    }

    public void refreshData() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            int year = 2000;

            // 1. Update Overall Vote Count
            Statement st1 = conn.createStatement();
            ResultSet rs1 = st1.executeQuery("SELECT COUNT(*) FROM votes WHERE vote_year = " + year);
            if (rs1.next()) {
                int count = rs1.getInt(1);
                lblCountVotesData.setText("Count Votes = " + count);
                lblCountVotesData.setForeground(count >= VOTER_LIMIT ? Color.RED : new Color(0, 51, 153));
            }

            // 2. Update Current Winner
            Statement st2 = conn.createStatement();
            ResultSet rs2 = st2.executeQuery("SELECT group_name, COUNT(*) as total FROM votes WHERE vote_year = " + year
                    + " GROUP BY group_name ORDER BY total DESC LIMIT 1");
            if (rs2.next()) {
                lblWinnerName.setText(rs2.getString("group_name"));
                lblWinnerVotes.setText(rs2.getString("total"));
            }

            // 3. Update Icon Row (JOIN user_groups with votes)
            groupsContainer.removeAll();

            String query = "SELECT ug.group_image_path, COUNT(v.id) as vote_count " +
                    "FROM user_groups ug " +
                    "LEFT JOIN votes v ON ug.group_name = v.group_name AND v.vote_year = " + year + " " +
                    "GROUP BY ug.group_name, ug.group_image_path";

            Statement st3 = conn.createStatement();
            ResultSet rs3 = st3.executeQuery(query);

            while (rs3.next()) {
                String path = rs3.getString("group_image_path");
                String count = String.valueOf(rs3.getInt("vote_count"));
                groupsContainer.add(createGroupCard(path, count));
            }

            groupsContainer.revalidate();
            groupsContainer.repaint();

        } catch (SQLException e) {
            System.err.println("DB Error: " + e.getMessage());
        }
    }
}
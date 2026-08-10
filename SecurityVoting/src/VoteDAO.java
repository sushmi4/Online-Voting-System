import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class VoteDAO {

    public enum VoteResult {
        SUCCESS, ALREADY_VOTED, INVALID_VOTER, ERROR
    }

    private VoteDAO() {
    }

    // --- Did this voter already vote in the current election? ---
    public static boolean hasUserAlreadyVoted(String voterId) {
        String query = "SELECT voter_id FROM votes WHERE voter_id=? AND vote_year=?";
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, voterId);
            ps.setInt(2, Config.electionYear());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Which group did this voter vote for in the current election? (null if none) ---
    public static String getVotedGroup(String voterId) {
        String query = "SELECT group_name FROM votes WHERE voter_id=? AND vote_year=?";
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, voterId);
            ps.setInt(2, Config.electionYear());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Casts a vote atomically. The UNIQUE(voter_id, vote_year) constraint is the
     * final authority, so concurrent double-voting cannot succeed.
     */
    public static VoteResult castVote(String voterId, String groupName) {
        if (voterId == null || voterId.isEmpty() || groupName == null || groupName.isEmpty()) {
            return VoteResult.INVALID_VOTER;
        }

        String insert = "INSERT INTO votes (voter_id, group_name, vote_year, voted_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                ps.setString(1, voterId);
                ps.setString(2, groupName);
                ps.setInt(3, Config.electionYear());
                ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                int affected = ps.executeUpdate();
                return affected > 0 ? VoteResult.SUCCESS : VoteResult.ERROR;
            } catch (SQLException dup) {
                if (isDuplicateKey(dup)) {
                    return VoteResult.ALREADY_VOTED;
                }
                dup.printStackTrace();
                return VoteResult.ERROR;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return VoteResult.ERROR;
        }
    }

    private static boolean isDuplicateKey(SQLException e) {
        // SQLState 23000 covers duplicate-key / constraint violations in MySQL
        return "23000".equals(e.getSQLState())
                || (e.getMessage() != null && e.getMessage().toLowerCase().contains("duplicate"));
    }

    // --- Election results for a given year (groups ordered by votes desc) ---
    public static Map<String, Integer> getResults() {
        return getResults(Config.electionYear());
    }

    public static Map<String, Integer> getResults(int year) {
        Map<String, Integer> results = new LinkedHashMap<>();
        String query = "SELECT group_name, COUNT(*) as total FROM votes"
                + " WHERE vote_year=? GROUP BY group_name ORDER BY total DESC, group_name ASC";

        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.put(rs.getString("group_name"), rs.getInt("total"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public static int totalVotesCast() {
        return totalVotesCast(Config.electionYear());
    }

    public static int totalVotesCast(int year) {
        String query = "SELECT COUNT(*) FROM votes WHERE vote_year=?";
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class VoteDAO {

    // --- MySQL connection settings ---
    private static final String DB_URL = "jdbc:mysql://localhost:3306/onlinevoting_db";
    private static final String USER = "root";
    private static final String PASS = ""; // Change if your DB has password

    // --- Load MySQL Driver ---
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found!");
        }
    }

    // --- Check if voter has already voted for a group ---
    public static boolean hasVoted(String voterId, String groupName) {
        String query = "SELECT voter_id FROM votes WHERE voter_id = ? AND group_name = ?";
        try (Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
                PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, voterId);
            ps.setString(2, groupName);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // true if a row exists
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Cast a vote (used by VoterDetailsPage) ---
    public static boolean castVote(String voterId, String groupName) {
        // First, ensure voter hasn't already voted
        if (hasVoted(voterId, groupName)) {
            return false; // Already voted
        }

        String query = "INSERT INTO votes (voter_id, group_name) VALUES (?, ?)";
        try (Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
                PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, voterId);
            ps.setString(2, groupName);

            int result = ps.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean hasUserAlreadyVoted(String voterId) {

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/onlinevoting_db", "root", "")) {

            String sql = "SELECT * FROM votes WHERE voter_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, voterId);

            ResultSet rs = ps.executeQuery();

            return rs.next(); // if record exists → already voted

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // --- Get election results ---
    public static Map<String, Integer> getResults() {
        Map<String, Integer> results = new LinkedHashMap<>();
        String query = "SELECT group_name, COUNT(*) as total FROM votes GROUP BY group_name ORDER BY total DESC";

        try (Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                results.put(rs.getString("group_name"), rs.getInt("total"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }
}
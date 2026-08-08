import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class DBConnection {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_MINUTES = 5;

    private DBConnection() {
    }

    // ================= LOGIN (with brute-force lockout) =================
    /**
     * Returns:
     *  - "OK"                when credentials are valid (session is set)
     *  - "LOCKED"            when the account is temporarily locked
     *  - "INVALID"           on wrong credentials / unknown account
     */
    public static String validateLogin(String email, String password) {
        String sql = "SELECT * FROM voters WHERE email=?";
        try (Connection conn = Database.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, email);
            try (ResultSet rs = pst.executeQuery()) {

                if (!rs.next()) {
                    // Never reveal whether an account exists
                    return "INVALID";
                }

                if (isLocked(rs)) {
                    return "LOCKED";
                }

                String storedHash = rs.getString("password");
                if (!PasswordHasher.verifyPassword(password, storedHash)) {
                    registerFailedAttempt(conn, email);
                    return "INVALID";
                }

                // Success: reset failure counters and open the session
                resetFailedAttempts(conn, email);
                UserSession.openSession(
                        rs.getString("full_name"),
                        rs.getString("voter_id"),
                        rs.getString("email"),
                        rs.getString("mobile"),
                        rs.getString("status"),
                        rs.getString("dob"),
                        rs.getString("image_path"),
                        rs.getString("address"),
                        UserSession.Role.VOTER);

                // Refresh locked-until / attempt state in the session
                UserSession.touch();
                return "OK";
            }
        } catch (Exception e) {
            System.out.println("Login Error: " + e.getMessage());
            return "INVALID";
        }
    }

    private static boolean isLocked(ResultSet rs) throws SQLException {
        Timestamp lockedUntil = rs.getTimestamp("locked_until");
        return lockedUntil != null && lockedUntil.toLocalDateTime().isAfter(LocalDateTime.now());
    }

    private static void registerFailedAttempt(Connection conn, String email) throws SQLException {
        int attempts;
        try (PreparedStatement sel = conn.prepareStatement(
                "SELECT failed_attempts FROM voters WHERE email=?")) {
            sel.setString(1, email);
            try (ResultSet rs = sel.executeQuery()) {
                attempts = rs.next() ? rs.getInt(1) + 1 : 1;
            }
        }
        LocalDateTime lockUntil = attempts >= MAX_ATTEMPTS ? LocalDateTime.now().plusMinutes(LOCK_MINUTES) : null;
        try (PreparedStatement upd = conn.prepareStatement(
                "UPDATE voters SET failed_attempts=?, locked_until=? WHERE email=?")) {
            upd.setInt(1, attempts);
            upd.setTimestamp(2, lockUntil == null ? null : Timestamp.valueOf(lockUntil));
            upd.setString(3, email);
            upd.executeUpdate();
        }
    }

    private static void resetFailedAttempts(Connection conn, String email) throws SQLException {
        try (PreparedStatement upd = conn.prepareStatement(
                "UPDATE voters SET failed_attempts=0, locked_until=NULL WHERE email=?")) {
            upd.setString(1, email);
            upd.executeUpdate();
        }
    }

    // ================= RESET / UPDATE PASSWORD =================
    public static boolean updatePassword(String email, String newPassword) {
        String sql = "UPDATE voters SET password=?, failed_attempts=0, locked_until=NULL WHERE email=?";
        try (Connection conn = Database.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, PasswordHasher.hashPassword(newPassword));
            pst.setString(2, email);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Update Password Error: " + e.getMessage());
            return false;
        }
    }

    public static boolean emailExists(String email) {
        return countBy("SELECT COUNT(*) FROM voters WHERE email=?", email) > 0;
    }

    // ================= REGISTER =================
    public static boolean registerVoter(String fullName, String voterId, String email,
            String password, String dob, String mobile,
            String imagePath, String address) {

        String sql = "INSERT INTO voters (full_name,voter_id,email,password,dob,mobile,image_path,address,status)"
                + " VALUES (?,?,?,?,?,?,?,?,'Pending')";
        try (Connection conn = Database.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, fullName);
            pst.setString(2, voterId);
            pst.setString(3, email);
            pst.setString(4, PasswordHasher.hashPassword(password));
            pst.setString(5, dob);
            pst.setString(6, mobile);
            pst.setString(7, imagePath);
            pst.setString(8, address);

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Registration Error: " + e.getMessage());
            return false;
        }
    }

    // ================= UPDATE PROFILE =================
    public static boolean updateVoterContactInfo(String email, String newVoterId, String newMobile) {
        String sql = "UPDATE voters SET voter_id=?, mobile=? WHERE email=?";
        try (Connection conn = Database.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, newVoterId);
            pst.setString(2, newMobile);
            pst.setString(3, email);

            if (pst.executeUpdate() > 0) {
                UserSession.setVoterId(newVoterId);
                UserSession.setMobile(newMobile);
                return true;
            }
        } catch (Exception e) {
            System.out.println("Update Error: " + e.getMessage());
        }
        return false;
    }

    // ================= CHECK CONSTRAINTS =================
    public static boolean voterIdExists(String voterId) {
        return countBy("SELECT COUNT(*) FROM voters WHERE voter_id=?", voterId) > 0;
    }

    public static boolean mobileExists(String mobile) {
        return countBy("SELECT COUNT(*) FROM voters WHERE mobile=?", mobile) > 0;
    }

    public static boolean emailExistsExcept(String email, String currentEmail) {
        return countBy("SELECT COUNT(*) FROM voters WHERE email=? AND email<>?", email, currentEmail) > 0;
    }

    private static int countBy(String sql, String... params) {
        try (Connection conn = Database.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pst.setString(i + 1, params[i]);
            }
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}

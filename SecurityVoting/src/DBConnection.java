import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/onlinevoting_db";
    private static final String USER = "root";
    private static final String PASS = "";

    // ================= DATABASE CONNECTION =================
    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // ================= LOGIN =================
    public static boolean validateLogin(String email, String password) {
        String sql = "SELECT * FROM voters WHERE email=? AND password=?";
        try (Connection conn = getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, email);
            pst.setString(2, PasswordHasher.hashPassword(password));

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                System.out.println("Login success for voter: " + rs.getString("voter_id"));

                UserSession.setSession(
                        rs.getString("full_name"),
                        rs.getString("voter_id"),
                        rs.getString("email"),
                        rs.getString("mobile"),
                        rs.getString("status"),
                        rs.getString("dob"),
                        rs.getString("image_path"),
                        "", "",
                        rs.getString("address"));
                return true;
            }
        } catch (Exception e) {
            System.out.println("Login Error: " + e.getMessage());
        }
        return false;
    }

    // ================= RESET / UPDATE PASSWORD =================
    public static boolean updatePassword(String email, String newPassword) {
        boolean isUpdated = false;
        // We use the same PasswordHasher used in registration and login
        String sql = "UPDATE voters SET password = ? WHERE email = ?";

        try (Connection conn = getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, PasswordHasher.hashPassword(newPassword));
            pst.setString(2, email);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                isUpdated = true;
                System.out.println("Password updated successfully for: " + email);
            }
        } catch (Exception e) {
            System.out.println("Update Password Error: " + e.getMessage());
        }
        return isUpdated;
    }

    // ================= REGISTER =================
    public static boolean registerVoter(String fullName, String voterId, String email,
            String password, String dob, String mobile,
            String imagePath, String address) {

        String sql = "INSERT INTO voters (full_name,voter_id,email,password,dob,mobile,image_path,address,status) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection conn = getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, fullName);
            pst.setString(2, voterId);
            pst.setString(3, email);
            pst.setString(4, PasswordHasher.hashPassword(password));
            pst.setString(5, dob);
            pst.setString(6, mobile);
            pst.setString(7, imagePath);
            pst.setString(8, address);
            pst.setString(9, "Pending");

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Registration Error: " + e.getMessage());
            return false;
        }
    }

    // ================= UPDATE PROFILE =================
    public static boolean updateVoterContactInfo(String email, String newVoterId, String newMobile) {
        String sql = "UPDATE voters SET voter_id=?, mobile=? WHERE email=?";
        try (Connection conn = getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, newVoterId);
            pst.setString(2, newMobile);
            pst.setString(3, email);

            if (pst.executeUpdate() > 0) {
                UserSession.setSession(
                        UserSession.getFullName(),
                        newVoterId,
                        UserSession.getEmail(),
                        newMobile,
                        UserSession.getStatus(),
                        UserSession.getDob(),
                        UserSession.getImagePath(),
                        "", "",
                        UserSession.getAddress());
                return true;
            }
        } catch (Exception e) {
            System.out.println("Update Error: " + e.getMessage());
        }
        return false;
    }

    // ================= CHECK CONSTRAINTS =================
    public static boolean voterIdExists(String voterId) {
        String sql = "SELECT COUNT(*) FROM voters WHERE voter_id=?";
        try (Connection conn = getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, voterId);
            ResultSet rs = pst.executeQuery();
            if (rs.next())
                return rs.getInt(1) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean mobileExists(String mobile) {
        String sql = "SELECT COUNT(*) FROM voters WHERE mobile=?";
        try (Connection conn = getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, mobile);
            ResultSet rs = pst.executeQuery();
            if (rs.next())
                return rs.getInt(1) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM voters WHERE email=?";
        try (Connection conn = getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();
            if (rs.next())
                return rs.getInt(1) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
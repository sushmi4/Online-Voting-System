public class UserSession {

    private static String fullName;
    private static String voterId;
    private static String email;
    private static String mobile;
    private static String dob;
    private static String status;
    private static String imagePath;
    private static String address;
    private static String votedGroupName;

    // ================= SET SESSION =================
    public static void setSession(String name, String id, String mail, String mob,
            String stat, String d, String path,
            String party, String elect, String addr) {

        fullName = name;
        voterId = id;
        email = mail;
        mobile = mob;
        status = stat;
        dob = d;
        imagePath = path;
        address = addr;
        votedGroupName = party; // Assuming this is intended
    }

    // ================= GETTERS =================
    public static String getFullName() {
        return fullName;
    }

    public static String getVoterId() {
        return voterId;
    }

    public static String getEmail() {
        return email;
    }

    public static String getMobile() {
        return mobile;
    }

    public static String getStatus() {
        return status;
    }

    public static String getDob() {
        return dob;
    }

    public static String getImagePath() {
        return imagePath;
    }

    public static String getAddress() {
        return address;
    }

    public static String getVotedGroupName() {
        return votedGroupName;
    }

    // ================= SETTERS =================
    public static void setFullName(String name) {
        fullName = name;
    }

    public static void setVoterId(String id) {
        voterId = id;
    }

    public static void setEmail(String mail) {
        email = mail;
    }

    public static void setMobile(String mob) {
        mobile = mob;
    }

    public static void setDob(String d) {
        dob = d;
    }

    public static void setStatus(String stat) {
        status = stat;
    }

    public static void setImagePath(String path) {
        imagePath = path;
    }

    public static void setAddress(String addr) {
        address = addr;
    }

    public static void setVotedGroupName(String groupName) {
        votedGroupName = groupName;
    }

    // ================= CLEAR SESSION =================
    public static void clearSession() {
        fullName = null;
        voterId = null;
        email = null;
        mobile = null;
        status = null;
        dob = null;
        imagePath = null;
        address = null;
        votedGroupName = null;
    }
}
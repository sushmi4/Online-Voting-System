import java.util.Timer;
import java.util.TimerTask;

public class UserSession {

    public enum Role {
        VOTER, ADMIN
    }

    private static String fullName;
    private static String voterId;
    private static String email;
    private static String mobile;
    private static String dob;
    private static String status;
    private static String imagePath;
    private static String address;
    private static String votedGroupName;

    private static Role role = null;
    private static long loginTime = 0L;
    private static long lastActivity = 0L;
    private static final int TIMEOUT_MS = Config.sessionTimeoutMinutes() * 60_000;
    private static Timer idleTimer;

    private UserSession() {
    }

    // ================= OPEN / CLOSE =================
    public static void openSession(String name, String id, String mail, String mob,
            String stat, String d, String path, String addr, Role r) {
        fullName = name;
        voterId = id;
        email = mail;
        mobile = mob;
        status = stat;
        dob = d;
        imagePath = path;
        address = addr;
        votedGroupName = null;
        role = r;
        loginTime = System.currentTimeMillis();
        lastActivity = loginTime;
        startIdleWatchdog();
    }

    public static void logout() {
        stopIdleWatchdog();
        fullName = null;
        voterId = null;
        email = null;
        mobile = null;
        status = null;
        dob = null;
        imagePath = null;
        address = null;
        votedGroupName = null;
        role = null;
        loginTime = 0L;
        lastActivity = 0L;
    }

    public static void clearSession() {
        logout();
    }

    // ================= STATE CHECKS =================
    public static boolean isLoggedIn() {
        return role != null;
    }

    public static boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public static boolean isVoter() {
        return role == Role.VOTER;
    }

    public static boolean isExpired() {
        return role != null
                && (System.currentTimeMillis() - lastActivity) > TIMEOUT_MS;
    }

    public static boolean isUsable() {
        if (role == null) {
            return false;
        }
        if (isExpired()) {
            logout();
            return false;
        }
        touch();
        return true;
    }

    // Refresh the "last activity" timestamp whenever the app is used
    public static void touch() {
        lastActivity = System.currentTimeMillis();
    }

    private static void startIdleWatchdog() {
        stopIdleWatchdog();
        idleTimer = new Timer("session-idle-watchdog", true);
        idleTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (role != null && isExpired()) {
                    logout();
                }
            }
        }, 60_000, 60_000);
    }

    private static void stopIdleWatchdog() {
        if (idleTimer != null) {
            idleTimer.cancel();
            idleTimer = null;
        }
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

    public static Role getRole() {
        return role;
    }

    // ================= SETTERS =================
    public static void setFullName(String name) {
        fullName = name;
        touch();
    }

    public static void setVoterId(String id) {
        voterId = id;
        touch();
    }

    public static void setEmail(String mail) {
        email = mail;
        touch();
    }

    public static void setMobile(String mob) {
        mobile = mob;
        touch();
    }

    public static void setDob(String d) {
        dob = d;
        touch();
    }

    public static void setStatus(String stat) {
        status = stat;
        touch();
    }

    public static void setImagePath(String path) {
        imagePath = path;
        touch();
    }

    public static void setAddress(String addr) {
        address = addr;
        touch();
    }

    public static void setVotedGroupName(String groupName) {
        votedGroupName = groupName;
        touch();
    }

    // Kept for backward compatibility with older call sites
    public static void setSession(String name, String id, String mail, String mob,
            String stat, String d, String path,
            String party, String elect, String addr) {
        openSession(name, id, mail, mob, stat, d, path, addr, Role.VOTER);
    }
}

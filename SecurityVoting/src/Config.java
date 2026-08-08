import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Properties;

public class Config {

    private static final Properties props = new Properties();

    static {
        // 1. Packaged resource (if compiled into the classpath)
        try (InputStream in = Config.class.getResourceAsStream("/config.properties")) {
            if (in != null) props.load(in);
        } catch (Exception ignored) {}

        // 2. File next to the working directory (overrides packaged values)
        try (InputStream in = new FileInputStream("config.properties")) {
            props.load(in);
        } catch (Exception ignored) {}

        // 3. JVM system properties (highest priority)
        for (String key : new String[]{
                "db.url", "db.user", "db.password",
                "election.year", "voter.limit", "session.timeout.minutes",
                "smtp.host", "smtp.port", "smtp.from", "smtp.password", "smtp.from.name"}) {
            String override = System.getProperty(key);
            if (override != null) props.setProperty(key, override);
        }
    }

    private Config() {
    }

    public static String get(String key, String def) {
        String v = props.getProperty(key);
        return v == null || v.trim().isEmpty() ? def : v.trim();
    }

    public static String dbUrl() {
        return get("db.url", "jdbc:mysql://localhost:3306/onlinevoting_db");
    }

    public static String dbUser() {
        return get("db.user", "voting_app");
    }

    public static String dbPassword() {
        return get("db.password", "");
    }

    public static int electionYear() {
        try {
            return Integer.parseInt(get("election.year", String.valueOf(LocalDate.now().getYear())));
        } catch (NumberFormatException e) {
            return LocalDate.now().getYear();
        }
    }

    public static int voterLimit() {
        try {
            return Integer.parseInt(get("voter.limit", "2000"));
        } catch (NumberFormatException e) {
            return 2000;
        }
    }

    public static int sessionTimeoutMinutes() {
        try {
            return Integer.parseInt(get("session.timeout.minutes", "30"));
        } catch (NumberFormatException e) {
            return 30;
        }
    }

    public static String smtpHost() {
        return get("smtp.host", "");
    }

    public static int smtpPort() {
        try {
            return Integer.parseInt(get("smtp.port", "587"));
        } catch (NumberFormatException e) {
            return 587;
        }
    }

    public static String smtpFrom() {
        return get("smtp.from", "");
    }

    public static String smtpPassword() {
        return get("smtp.password", "");
    }

    public static String smtpFromName() {
        return get("smtp.from.name", "Online Voting System");
    }
}

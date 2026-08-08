import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * In-memory OTP store: 6-digit codes with 5-minute expiry, a max number of
 * verification attempts and a resend cooldown.
 */
public class OtpManager {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int TTL_MS = 5 * 60 * 1000;          // 5 minutes
    private static final int RESEND_COOLDOWN_MS = 60 * 1000;   // 1 minute
    private static final int MAX_ATTEMPTS = 3;

    private static final Map<String, OtpEntry> STORE = new HashMap<>();

    private OtpManager() {
    }

    private static final class OtpEntry {
        String code;
        long expiresAt;
        long lastSentAt;
        int attempts;

        OtpEntry(String code, long expiresAt, long lastSentAt) {
            this.code = code;
            this.expiresAt = expiresAt;
            this.lastSentAt = lastSentAt;
        }
    }

    /** Generates a fresh OTP for the email, respecting the resend cooldown. */
    public static String issue(String email) {
        synchronized (STORE) {
            OtpEntry existing = STORE.get(email);
            if (existing != null
                    && System.currentTimeMillis() - existing.lastSentAt < RESEND_COOLDOWN_MS) {
                return null; // too soon to resend
            }
            String code = String.format("%06d", RANDOM.nextInt(1_000_000));
            STORE.put(email, new OtpEntry(code,
                    System.currentTimeMillis() + TTL_MS,
                    System.currentTimeMillis()));
            return code;
        }
    }

    /**
     * Verifies a code. Consumes an attempt on failure and removes the entry
     * once the code is correct, expired or attempts run out.
     */
    public static boolean verify(String email, String code) {
        if (email == null || code == null) {
            return false;
        }
        synchronized (STORE) {
            OtpEntry entry = STORE.get(email);
            if (entry == null) {
                return false;
            }
            if (System.currentTimeMillis() > entry.expiresAt) {
                STORE.remove(email);
                return false;
            }
            if (entry.code.equals(code.trim())) {
                STORE.remove(email);
                return true;
            }
            entry.attempts++;
            if (entry.attempts >= MAX_ATTEMPTS) {
                STORE.remove(email);
            }
            return false;
        }
    }

    public static void invalidate(String email) {
        synchronized (STORE) {
            STORE.remove(email);
        }
    }
}

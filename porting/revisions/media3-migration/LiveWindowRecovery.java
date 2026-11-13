package e.e.a;

/** Limits how often a live player jumps back to the default position after a fatal error. */
final class LiveWindowRecovery {
    private static final long RETRY_INTERVAL_MS = 30_000L;
    private long lastAttemptMs = -1;

    boolean claimAttempt(long elapsedRealtimeMs) {
        if (lastAttemptMs >= 0 && elapsedRealtimeMs - lastAttemptMs < RETRY_INTERVAL_MS) {
            return false;
        }
        lastAttemptMs = elapsedRealtimeMs;
        return true;
    }

    void reset() {
        lastAttemptMs = -1;
    }
}


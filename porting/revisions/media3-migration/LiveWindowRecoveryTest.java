package e.e.a;

public final class LiveWindowRecoveryTest {
    public static void main(String[] args) {
        LiveWindowRecovery recovery = new LiveWindowRecovery();
        check(recovery.claimAttempt(0), "first attempt");
        check(!recovery.claimAttempt(0), "duplicate callback");
        check(!recovery.claimAttempt(29_999), "cooldown");
        check(recovery.claimAttempt(30_000), "attempt after cooldown");
        check(!recovery.claimAttempt(30_001), "cooldown restarted");
        recovery.reset();
        check(recovery.claimAttempt(30_001), "new watch session");
        System.out.println("PASS: live-window recovery retry limit and session reset");
    }

    private static void check(boolean condition, String scenario) {
        if (!condition) {
            throw new AssertionError(scenario);
        }
    }
}


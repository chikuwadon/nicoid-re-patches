package e.e.a;

/** Select stable identifiers from watch-page metadata for comment writes. */
final class PostingRules {
    private PostingRules() { }

    static String videoId(String clientWatchId, String requestedId, String videoId) {
        if (valid(clientWatchId)) return clientWatchId;
        if (requestedId != null && requestedId.matches("(?:sm|nm|so|ss)[0-9]+")) return requestedId;
        if (valid(videoId)) return videoId;
        throw new IllegalArgumentException("Missing canonical watch ID");
    }

    private static boolean valid(String value) {
        return value != null && value.matches("(?:sm|nm|so|ss)[0-9]+");
    }
}

package e.e.a;

/** Select stable identifiers from watch-page metadata for comment writes. */
final class PostingRules {
    private PostingRules() { }

    static String videoId(String clientWatchId, String requestedId, String videoId) {
        if (clientWatchId!=null&&clientWatchId.matches("(?:sm|nm|so|ss)?[0-9]+")) return clientWatchId;
        if (requestedId != null && requestedId.matches("(?:sm|nm|so|ss)[0-9]+")) return requestedId;
        if (valid(videoId)) return videoId;
        throw new IllegalArgumentException("Missing canonical watch ID");
    }

    static org.json.JSONObject thread(org.json.JSONObject comment,org.json.JSONArray targets)throws org.json.JSONException {
        org.json.JSONArray threads=comment.optJSONArray("threads");
        if(threads!=null)for(int i=0;i<threads.length();i++){org.json.JSONObject t=threads.optJSONObject(i);if(t!=null&&t.optBoolean("isDefaultPostTarget")&&t.optBoolean("isActive",true))return t;}
        for(int i=0;i<targets.length();i++){org.json.JSONObject t=targets.getJSONObject(i);if("main".equals(t.optString("fork")))return t;}
        throw new org.json.JSONException("No posting thread");
    }

    private static boolean valid(String value) {
        return value != null && value.matches("(?:sm|nm|so|ss)[0-9]+");
    }
}

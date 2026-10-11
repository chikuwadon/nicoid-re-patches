package e.e.a;

import org.json.JSONObject;

/** Reconstructs the identity data required for nicoru actions from cached comments. */
public final class CachedCommentMetadata {
    public static final class Owner { public final JSONObject modernWatch; Owner(JSONObject watch){modernWatch=watch;} }
    private CachedCommentMetadata() {}

    private static Object field(Object owner,String name) throws Exception { for(Class<?> type=owner.getClass();type!=null;type=type.getSuperclass())try{java.lang.reflect.Field f=type.getDeclaredField(name);f.setAccessible(true);return f.get(owner);}catch(NoSuchFieldException ignored){}return null;}

    public static void begin(JSONObject response, Object model) {
        try {
            Object watch = field(model, "modernWatch");
            if (watch instanceof JSONObject) {
                Class.forName("e.e.a.CommentListExtras").getMethod("begin", JSONObject.class, Object.class).invoke(null, response, model);
                return;
            }
            String id = String.valueOf(field(model, "d"));
            JSONObject cachedWatch = new JSONObject()
                .put("video", new JSONObject().put("id", id))
                .put("comment", new JSONObject().put("nvComment",
                    new JSONObject().put("server", "https://nvcomment.nicovideo.jp")));
            Class.forName("e.e.a.CommentListExtras").getMethod("begin", JSONObject.class, Object.class)
                .invoke(null, response, new Owner(cachedWatch));
        } catch (Exception error) {
            android.util.Log.w("nicoid-comments", "Cached comment metadata unavailable", error);
        }
    }
}

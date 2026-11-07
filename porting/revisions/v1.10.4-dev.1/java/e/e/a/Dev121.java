package e.e.a;

import android.app.Activity;
import android.app.Service;
import android.os.SystemClock;
import android.view.View;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.WeakHashMap;
import org.json.JSONArray;
import org.json.JSONObject;

/** Development controls timeout and bounded incremental comment hydration. */
public final class Dev121 {
    private static final long CONTROL_HIDE_MS = 2000L;
    private static final Map<Object, Boolean> TUNED = new WeakHashMap<>();
    private static final Map<Object, Snapshot> SNAPSHOTS = new WeakHashMap<>();
    private Dev121() {}

    private static Field field(Class<?> type, String name) throws Exception {
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            try {
                Field f = c.getDeclaredField(name);
                f.setAccessible(true);
                return f;
            } catch (NoSuchFieldException ignored) { }
        }
        throw new NoSuchFieldException(name);
    }

    /** Let ExoMedia manage the timer, pause/seek interaction and animation itself. */
    public static void controls(Object owner) {
        if (owner == null) return;
        try {
            Object video;
            if (owner instanceof Activity) {
                Object fragment = field(owner.getClass(), "v").get(owner);
                if (fragment == null) return;
                video = field(fragment.getClass(), "a0").get(fragment);
            } else if (owner instanceof Service) {
                video = field(owner.getClass(), "e").get(owner);
            } else if (owner instanceof View) {
                video = owner;
            } else {
                return;
            }
            if (video == null) return;
            Object controls = video.getClass().getMethod("getVideoControls").invoke(video);
            if (controls == null) return;
            synchronized (TUNED) {
                if (TUNED.containsKey(controls)) return;
                controls.getClass().getMethod("setHideDelay", long.class).invoke(controls, CONTROL_HIDE_MS);
                TUNED.put(controls, Boolean.TRUE);
            }
        } catch (Exception ignored) {
            // Unknown legacy player variant: preserve its original controls.
        }
    }

    private static final class Snapshot {
        final String video;
        final Map<String, Integer> offsets = new HashMap<>();
        final HashSet<String> seen = new HashSet<>();
        final ArrayList<Object> items = new ArrayList<>();
        int rendered;
        long renderedAt;
        Snapshot(String video) { this.video = video; }
    }

    private static String video(Object owner) {
        try { return String.valueOf(field(owner.getClass(), "d").get(owner)); }
        catch (Exception ignored) { return ""; }
    }

    private static Method commentCommand(Object owner) throws Exception {
        for (Class<?> c = owner.getClass(); c != null; c = c.getSuperclass()) {
            try {
                Method m = c.getDeclaredMethod("a", String.class, Integer[].class, String[].class);
                m.setAccessible(true);
                return m;
            } catch (NoSuchMethodException ignored) { }
        }
        throw new NoSuchMethodException("Comment command decoder");
    }

    /** Parse each JSON comment once; never reconvert comments already displayed. */
    private static void hydrate(Object owner, JSONObject root, Snapshot state) throws Exception {
        Class<?> type = Class.forName("e.e.a.q");
        Constructor<?> ctor = type.getDeclaredConstructor(String.class, String.class, Integer[].class);
        ctor.setAccessible(true);
        Field user = field(type, "c"), secondary = field(type, "h");
        Method decoder = commentCommand(owner);
        Class<?> extra = Class.forName("e.e.a.CommentListExtras");
        Method capture = extra.getDeclaredMethod("capture", Object.class, JSONObject.class);
        capture.setAccessible(true);
        JSONArray threads = root.getJSONObject("data").getJSONArray("threads");
        for (int t = 0; t < threads.length(); t++) {
            JSONObject thread = threads.getJSONObject(t);
            String threadKey = thread.optString("id") + ":" + thread.optString("fork");
            JSONArray comments = thread.optJSONArray("comments");
            if (comments == null) continue;
            int from = state.offsets.containsKey(threadKey) ? state.offsets.get(threadKey) : 0;
            if (from > comments.length()) from = 0;
            for (int i = from; i < comments.length(); i++) {
                JSONObject comment = comments.getJSONObject(i);
                String id = comment.optString("id", comment.optString("no"));
                if (!state.seen.add(threadKey + ":" + id)) continue;
                String body = comment.optString("body");
                JSONArray commands = comment.optJSONArray("commands");
                StringBuilder flags = new StringBuilder();
                if (commands != null) {
                    for (int n = 0; n < commands.length(); n++) {
                        if (n > 0) flags.append(' ');
                        flags.append(commands.optString(n));
                    }
                }
                String command = flags.toString();
                Integer[] attributes = new Integer[8];
                attributes[0] = comment.optInt("vposMs") / 10;
                attributes[1] = comment.optInt("no");
                Integer[] parsed = new Integer[3];
                String[] labels = new String[3];
                decoder.invoke(owner, command, parsed, labels);
                attributes[2] = parsed[2];
                attributes[3] = parsed[0];
                attributes[4] = parsed[1];
                Object row = ctor.newInstance(body, command, attributes);
                secondary.set(row, labels[2]);
                user.set(row, comment.optString("userId"));
                capture.invoke(null, row, comment);
                state.items.add(row);
            }
            state.offsets.put(threadKey, comments.length());
        }
    }

    /** Called after completed network pages, not from the main/UI thread. */
    public static void progress(Object owner, JSONObject accumulated, int added) {
        if (owner == null || accumulated == null || added <= 0) return;
        if (!Dev114.memoryAvailable()) return;
        long now = SystemClock.elapsedRealtime();
        Snapshot state;
        synchronized (SNAPSHOTS) {
            state = SNAPSHOTS.get(owner);
            String id = video(owner);
            if (state == null || !state.video.equals(id)) {
                state = new Snapshot(id);
                SNAPSHOTS.put(owner, state);
            }
        }
        synchronized (state) {
            int pending = added - state.rendered;
            long elapsed = state.renderedAt == 0 ? 0 : Math.max(0, now - state.renderedAt);
            if (!CommentBatchPolicy121.ready(pending, elapsed, state.renderedAt == 0)) return;
            try {
                hydrate(owner, accumulated, state);
                if (!Dev114.memoryAvailable()) return;
                Class<?> ui = Class.forName("e.e.a.CommentUi");
                Method apply = ui.getDeclaredMethod("apply", Object.class, ArrayList.class);
                apply.setAccessible(true);
                apply.invoke(null, owner, new ArrayList<>(state.items));
                state.rendered = added;
                state.renderedAt = now;
            } catch (Exception ignored) {
                // Never interrupt playback or block the final, original full load.
            }
        }
    }

    public static void done(Object owner) {
        if (owner == null) return;
        synchronized (SNAPSHOTS) { SNAPSHOTS.remove(owner); }
    }
}

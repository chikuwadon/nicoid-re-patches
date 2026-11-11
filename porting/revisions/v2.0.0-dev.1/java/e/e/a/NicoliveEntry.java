package e.e.a;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class NicoliveEntry {
    private static final String POPUP_SERVICE = "com.sauzask.nicoid.NicoidPopupViewService";

    public static void beforeVideo() {
        NicoliveService live = NicoliveService.active;
        if (live == null || live.stopped) return;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            live.stopPlayback();
        } else {
            new Handler(Looper.getMainLooper()).post(live::stopPlayback);
        }
    }

    static void beforeLive(Context context) {
        stopRetainedVideo();
        context.stopService(new Intent().setClassName(context, POPUP_SERVICE));
        try {
            Field field = PlaybackSession.class.getDeclaredField("SESSIONS");
            field.setAccessible(true);
            Object sessions = field.get(null);
            if (sessions instanceof Map) {
                for (Object owner : new ArrayList<>(((Map<?, ?>) sessions).keySet())) {
                    if (owner == null) continue;
                    String videoField = owner.getClass().getName().contains("Popup") ? "e" : "a0";
                    try {
                        Object video = readField(owner, videoField);
                        if (video != null) video.getClass().getMethod("pause").invoke(video);
                    } catch (ReflectiveOperationException failure) {
                        Log.w("Nicolive", "Could not pause the previous video", failure);
                    }
                }
            }
        } catch (ReflectiveOperationException failure) {
            Log.w("Nicolive", "Could not find previous video sessions", failure);
        }
    }

    private static void stopRetainedVideo() {
        // v1.11.1 retains the original decoder independently of the notification service.
        try {
            Class<?> retained = Class.forName("e.e.a.RetainedPlayback137");
            synchronized (retained) {
                Field current = retained.getDeclaredField("current");
                current.setAccessible(true);
                Object session = current.get(null);
                if (session == null) return;
                try {
                    Object player = readField(session, "player");
                    if (player != null) player.getClass().getMethod("pause").invoke(player);
                } finally {
                    Method finish = retained.getDeclaredMethod("finish", current.getType(), boolean.class);
                    finish.setAccessible(true);
                    finish.invoke(null, session, true);
                }
            }
        } catch (ReflectiveOperationException failure) {
            Log.w("Nicolive", "Could not close the retained video session", failure);
        }
    }

    private static Object readField(Object owner, String name) throws ReflectiveOperationException {
        for (Class<?> type = owner.getClass(); type != null; type = type.getSuperclass()) {
            try {
                Field field = type.getDeclaredField(name);
                field.setAccessible(true);
                return field.get(owner);
            } catch (NoSuchFieldException missing) {
                if (type.getSuperclass() == null) throw missing;
            }
        }
        throw new NoSuchFieldException(name);
    }

    public static void addMenu(Context context, ArrayList<Map<String, Object>> items) {
        if (Build.VERSION.SDK_INT < 28) return;
        for (Object item : items) {
            if (item instanceof Map && "nicoid_live".equals(((Map<?, ?>) item).get("id"))) return;
        }
        boolean japanese = NicoliveUi.ja(context);
        Map<String, Object> item = new HashMap<>();
        item.put("title", japanese ? "生放送" : "Live streams");
        item.put("subtitle", japanese ? "放送中の番組・フォロー・視聴履歴" : "On air, following and viewing history");
        item.put("islabel", false);
        item.put("type", 0);
        item.put("id", "nicoid_live");
        item.put("intent", new Intent(context, NicoliveActivity.class));
        items.add(Math.min(3, items.size()), item);
    }
}

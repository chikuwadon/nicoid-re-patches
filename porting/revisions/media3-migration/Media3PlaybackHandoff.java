package e.e.a;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/** Moves the running engine to the Service's native video view. */
public final class Media3PlaybackHandoff {
    private Media3PlaybackHandoff() {}

    public static boolean adopt(Service service, Thread loader) {
        Media3VideoPlayer source;
        Media3VideoPlayer target;
        Object sourceDelegate;
        Object targetDelegate;
        Field engine;
        View targetView;
        try {
            Class<?> transfers = Class.forName("e.e.a.PlaybackTransfer221");
            Object transfer = Dev139.field(transfers, "pending").get(null);
            if (transfer == null || Dev139.get(transfer, "target") != service
                    || !Boolean.TRUE.equals(Dev139.get(transfer, "toPopup"))
                    || !HistoryRules.same((String) Dev139.get(transfer, "id"), Dev139.get(service, "f"))) {
                return false;
            }
            Object fragment = Dev139.get(transfer, "source");
            Activity activity = (Activity) Dev139.get(fragment, "A1");
            View sourceView = (View) Dev139.get(transfer, "sourcePlayer");
            if (activity == null || activity.isFinishing() || activity.isDestroyed()
                    || sourceView != Dev139.get(fragment, "a0")
                    || !HistoryRules.same((String) Dev139.get(fragment, "b0"), Dev139.get(service, "f"))) {
                return false;
            }
            targetView = (View) Dev139.get(service, "e");
            sourceDelegate = Dev139.delegate(sourceView);
            targetDelegate = Dev139.delegate(targetView);
            engine = Dev139.field(sourceDelegate.getClass(), "media3Player");
            source = (Media3VideoPlayer) engine.get(sourceDelegate);
            target = (Media3VideoPlayer) engine.get(targetDelegate);
            if (source == null || !source.canMoveTo(target)) {
                ModernDebug.record("Media3 popup handoff unavailable: playback or destination not prepared");
                return false;
            }
            Uri uri = source.currentUri();
            // Loopback media may belong to the source screen's cache server.
            if (uri == null || "127.0.0.1".equals(uri.getHost()) || "localhost".equals(uri.getHost())
                    || "::1".equals(uri.getHost()) || "[::1]".equals(uri.getHost())) {
                ModernDebug.record("Media3 popup handoff unavailable: screen-owned cache");
                return false;
            }
            Object previousLoader = Dev139.get(fragment, "h1");
            Bundle metadata = FeedbackMedia.bundle(fragment);
            Bundle info = metadata == null ? new Bundle() : new Bundle(metadata);
            if (info.isEmpty()) {
                info.putString("videoTitle", (String) Dev139.get(fragment, "b0"));
            }
            Dev139.field(loader.getClass(), "m").set(loader, info);
            if (previousLoader != null) {
                Dev139.field(loader.getClass(), "I").set(loader, Dev139.get(previousLoader, "I"));
            }
            Object listener = Dev139.get(loader, "k");
            try {
                listener.getClass().getMethod("a", Bundle.class).invoke(listener, info);
            } catch (ReflectiveOperationException | RuntimeException failure) {
                Log.w("nicoid-transfer", "Cannot refresh popup metadata", failure);
            }

            Dev139.field(targetView.getClass(), "videoUri").set(targetView, uri);
            Dev139.field(service.getClass(), "t0").setInt(null, 0);
            engine.set(sourceDelegate, target);
            engine.set(targetDelegate, source);
        } catch (ReflectiveOperationException | RuntimeException failure) {
            Log.w("nicoid-transfer", "Cannot adopt prepared playback", failure);
            return false;
        }
        source.moveTo(target);
        targetView.setKeepScreenOn(source.isPlaying());
        RetainedPlayback137.popupAdopted(service);
        return true;
    }

    private static Media3VideoPlayer player(View video) {
        try {
            return video == null ? null : Dev139.player(video);
        } catch (ReflectiveOperationException | RuntimeException failure) {
            return null;
        }
    }

    public static boolean isBinding(Object video) {
        Media3VideoPlayer player = video instanceof View ? player((View) video) : null;
        return player != null && player.isBindingRetainedView();
    }

    static boolean owns(Service service) {
        try {
            return player((View) Dev139.get(service, "e")) != null
                && Dev139.get(service, "f") != null;
        } catch (ReflectiveOperationException failure) {
            return false;
        }
    }

    static void requestPopup(Object fragment) {
        try {
            Class.forName("e.e.a.PlaybackTransfer221").getMethod("popup", Object.class, boolean.class)
                .invoke(null, fragment, false);
        } catch (ReflectiveOperationException failure) {
            Log.w("nicoid-transfer", "Cannot request popup from retained playback", failure);
        }
    }

    public static boolean returnToNormal(Service service) {
        try {
            View video = (View) Dev139.get(service, "e");
            Media3VideoPlayer player = player(video);
            Object loader = Dev139.get(service, "z");
            if (player == null || !player.hasPlaybackSession() || loader == null) {
                return false;
            }
            Field metadata = Dev139.field(loader.getClass(), "m");
            Bundle info = (Bundle) metadata.get(loader);
            if (info == null || info.isEmpty()) {
                info = (Bundle) Dev139.field(service.getClass(), "s0").get(null);
                if (info == null || info.isEmpty()) {
                    info = new Bundle();
                    info.putString("videoTitle", (String) Dev139.get(service, "M"));
                }
                metadata.set(loader, new Bundle(info));
            }
            player.endScrubbing();
            return Dev115.seamlessReturn(service);
        } catch (ReflectiveOperationException failure) {
            Log.w("nicoid-transfer", "Cannot prepare the active popup for return", failure);
            return false;
        }
    }

    public static boolean tryRestorePopup(Object owner) {
        if (!(owner instanceof Service)) {
            return false;
        }
        Service service = (Service) owner;
        if (owns(service)) {
            return returnToNormal(service);
        }
        return PopupReturn244.restore(owner);
    }

    public static void startPopup(View video) {
        Media3VideoPlayer player = player(video);
        if (player == null || !player.skipPopupAutoStart()) {
            try {
                video.getClass().getMethod("start").invoke(video);
            } catch (ReflectiveOperationException failure) {
                Log.w("nicoid-transfer", "Cannot start popup playback", failure);
            }
        }
    }

    public static void addPopupWindow(Service service, WindowManager windows, View root,
            WindowManager.LayoutParams params) {
        Media3VideoPlayer player = retainedPopupPlayer(service);
        if (player == null) {
            windows.addView(root, params);
            return;
        }
        float alpha = params.alpha;
        params.alpha = 0f;
        try {
            windows.addView(root, params);
        } finally {
            params.alpha = alpha;
        }
        Runnable reveal = () -> {
            if (root.isAttachedToWindow()) {
                windows.updateViewLayout(root, params);
            }
        };
        root.postDelayed(reveal, 750);
        player.awaitPopupOutput(true, () -> {
            root.removeCallbacks(reveal);
            reveal.run();
            player.notifyRetainedView();
        });
    }

    public static void notifyRetained(Service service) {
        Media3VideoPlayer player = retainedPopupPlayer(service);
        if (player == null) {
            return;
        }
        try {
            ModernDebug.record("Media3 transferred the active media item to the popup service");
            View loading = (View) Dev139.get(service, "H");
            if (loading != null) {
                loading.setVisibility(View.GONE);
            }
            if (Dev139.field(service.getClass(), "p0").getBoolean(null)) {
                player.awaitPopupOutput(false, player::notifyRetainedView);
            }
        } catch (ReflectiveOperationException failure) {
            Log.w("nicoid-transfer", "Cannot notify retained playback", failure);
        }
    }

    private static Media3VideoPlayer retainedPopupPlayer(Service service) {
        try {
            Media3VideoPlayer player = player((View) Dev139.get(service, "e"));
            return player != null && player.isBindingRetainedView() ? player : null;
        } catch (ReflectiveOperationException failure) {
            return null;
        }
    }

    public static Object bindReturnedView(Object pending, Method callback, Object listener, Object[] arguments)
            throws ReflectiveOperationException {
        Media3VideoPlayer player = player((View) Dev139.get(pending, "video"));
        return player == null ? callback.invoke(listener, arguments)
            : player.bindReturnedView(callback, listener, arguments);
    }

    public static boolean showPopup(Service service, Intent request) {
        if (request == null || request.hasExtra("MediaButtonCode")
                || request.getBooleanExtra("isBackgroundPlay", false) || !Settings.canDrawOverlays(service)) {
            return false;
        }
        try {
            Field background = Dev139.field(service.getClass(), "p0");
            String current = (String) Dev139.get(service, "f");
            String requested = request.getStringExtra("url");
            if (!background.getBoolean(null) || current == null || !HistoryRules.same(current, requested)) {
                return false;
            }
            View video = (View) Dev139.get(service, "e");
            View root = (View) Dev139.get(service, "a");
            WindowManager windows = (WindowManager) Dev139.get(service, "b");
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) Dev139.get(service, "U");
            Media3VideoPlayer player = player(video);
            if (root == null || root.isAttachedToWindow() || windows == null || params == null
                    || player == null || !player.hasPlaybackSession()) {
                return false;
            }
            Object mini = Dev139.field(Class.forName("e.e.a.DirectBackground235"), "active").get(null);
            Method closeMini = null;
            if (mini != null && Dev139.get(mini, "service") == service) {
                closeMini = mini.getClass().getDeclaredMethod("close", boolean.class);
                closeMini.setAccessible(true);
            }
            Method resize = service.getClass().getMethod("b", int.class);
            Method notification = service.getClass().getMethod("b", boolean.class);
            params.x = Dev139.field(service.getClass(), "u0").getInt(null);
            params.y = Dev139.field(service.getClass(), "v0").getInt(null);
            windows.addView(root, params);
            background.setBoolean(null, false);
            try {
                if (closeMini != null) {
                    closeMini.invoke(mini, false);
                }
                resize.invoke(service, 0);
                notification.invoke(service, false);
                ModernDebug.record("Media3 switched the active background media item to popup");
            } catch (ReflectiveOperationException | RuntimeException failure) {
                Log.w("nicoid-transfer", "Cannot update the retained popup layout", failure);
            }
            return true;
        } catch (ReflectiveOperationException | RuntimeException failure) {
            Log.w("nicoid-transfer", "Cannot show the active background video as a popup", failure);
            return false;
        }
    }

    public static void addPopupAction(Notification.Builder notification, Object owner) {
        if (!(owner instanceof Service)) {
            return;
        }
        Service service = (Service) owner;
        try {
            if (!Dev139.field(service.getClass(), "p0").getBoolean(null)
                    || !Settings.canDrawOverlays(service)) {
                return;
            }
            String id = (String) Dev139.get(service, "f");
            if (id == null || !id.matches("(?:sm|nm|so|ss)?[0-9]+")) {
                return;
            }
            Intent request = new Intent(service, service.getClass())
                .setAction("com.sauzask.nicoid.MEDIA3_SHOW_POPUP")
                .setData(Uri.parse("https://www.nicovideo.jp/watch/" + id))
                .putExtra("url", id);
            PendingIntent action = PendingIntent.getService(service, 3210, request,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            notification.addAction(new Notification.Action.Builder(android.R.drawable.ic_menu_view,
                UiStrings.translate("ポップアップ再生"), action).build());
        } catch (ReflectiveOperationException | RuntimeException failure) {
            Log.w("nicoid-transfer", "Cannot add popup notification action", failure);
        }
    }
}

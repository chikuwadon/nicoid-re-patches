package e.e.a;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/** Gates the confirmed control toggle without consuming playback touch streams. */
public final class TapControls141 {
    static final String KEY = "nicoid_single_tap_control_region";
    private static final Handler MAIN = new Handler(Looper.getMainLooper());
    private static final WeakHashMap<Object, NativeTap> NATIVE = new WeakHashMap<>();
    private static SharedPreferences preferences;
    private static volatile TapRegions141 regions = TapRegions141.preset(TapRegions141.ALL);
    private static final SharedPreferences.OnSharedPreferenceChangeListener LISTENER =
            (prefs, key) -> { if (key == null || KEY.equals(key)) regions = load(prefs); };

    private TapControls141() {}

    private static TapRegions141 load(SharedPreferences prefs) {
        try { return TapRegions141.parse(prefs.getString(KEY, "all")); }
        catch (ClassCastException ignored) { return TapRegions141.preset(TapRegions141.ALL); }
    }

    static TapRegions141 settings(Context context) {
        if (preferences == null) {
            Context app = context.getApplicationContext();
            preferences = PreferenceManager.getDefaultSharedPreferences(app == null ? context : app);
            preferences.registerOnSharedPreferenceChangeListener(LISTENER);
            regions = load(preferences);
        }
        return regions;
    }

    static void save(Context context, TapRegions141 value) {
        settings(context);
        regions = value;
        preferences.edit().putString(KEY, value.encode()).apply();
    }

    private static View render(View view) {
        if (view instanceof TextureView || view instanceof SurfaceView) return view;
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = render(group.getChildAt(i));
                if (child != null && child.isShown() && child.getWidth() > 0) return child;
            }
        }
        return null;
    }

    private static boolean alwaysVisible(Context context, boolean popup) {
        try {
            return Boolean.TRUE.equals(Dev112.staticCall("e.e.a.PlayerVisibility192", "enabled",
                    new Class<?>[]{Context.class, boolean.class}, context, popup));
        } catch (Exception ignored) {
            return false;
        }
    }

    static boolean allows(View video, float x, float y, boolean popup) {
        if (video == null || !video.isAttachedToWindow() || !video.isShown()) return false;
        View display = render(video);
        if (display == null) display = video;
        int[] at = new int[2];
        display.getLocationOnScreen(at);
        float minimum = popup ? 24 * video.getResources().getDisplayMetrics().density : 0;
        Context context = video.getContext();
        return settings(context).contains(x - at[0], y - at[1],
                display.getWidth(), display.getHeight(), minimum)
                && !alwaysVisible(context, popup);
    }

    private static final class NativeTap {
        WeakReference<View> view;
        long down = -1, up = -1;
        int generation;
        boolean replaying;
    }

    /** Observe the real stream before the native GestureDetector receives it. */
    public static void nativeTouch(Object listener, View view, MotionEvent event) {
        int action = event.getActionMasked();
        NativeTap state = NATIVE.get(listener);
        if (action == MotionEvent.ACTION_DOWN) {
            if (state == null) {
                state = new NativeTap();
                NATIVE.put(listener, state);
            }
            state.view = new WeakReference<>(view);
            state.down = event.getDownTime();
            state.up = -1;
            state.generation++;
        } else if (state != null && action == MotionEvent.ACTION_UP && state.down == event.getDownTime()) {
            state.up = event.getEventTime();
        } else if (state != null && (action == MotionEvent.ACTION_CANCEL ||
                action == MotionEvent.ACTION_POINTER_DOWN)) {
            state.generation++;
            state.down = state.up = -1;
        }
    }

    /** A seek consumed upstream of the native listener also cancels its tap. */
    public static void cancelFor(Object player) {
        for (NativeTap state : NATIVE.values()) {
            View view = state.view.get();
            while (view != null) {
                if (view == player) {
                    state.generation++;
                    state.down = state.up = -1;
                    break;
                }
                android.view.ViewParent parent = view.getParent();
                view = parent instanceof View ? (View) parent : null;
            }
        }
    }

    /** The library already confirms single taps. Cover the remaining interval
     * when the app's seek recognizer measures its timeout from finger-up. */
    public static boolean nativeAllowed(Object listener, MotionEvent event) {
        NativeTap state = NATIVE.get(listener);
        if (state == null) return false;
        View view = state.view.get();
        if (view == null || !view.hasWindowFocus() || state.down != event.getDownTime()
                || state.up < 0 || !allows(view, event.getRawX(), event.getRawY(), false)) return false;
        if (state.replaying) return true;
        long due = state.up + ViewConfiguration.getDoubleTapTimeout() + 1L;
        if (SystemClock.uptimeMillis() >= due) return true;
        int generation = state.generation;
        WeakReference<Object> owner = new WeakReference<>(listener);
        MotionEvent saved = MotionEvent.obtain(event);
        if (!MAIN.postAtTime(() -> {
            try {
                Object current = owner.get();
                if (current != null && state.generation == generation) {
                    state.replaying = true;
                    java.lang.reflect.Method callback = current.getClass()
                            .getDeclaredMethod("onSingleTapConfirmed", MotionEvent.class);
                    callback.setAccessible(true);
                    callback.invoke(current, saved);
                }
            } catch (ReflectiveOperationException ignored) {
                // A stale callback cannot toggle a replacement player's controls.
            } finally {
                state.replaying = false;
                saved.recycle();
            }
        }, due)) saved.recycle();
        return false;
    }
}

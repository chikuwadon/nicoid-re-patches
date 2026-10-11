package e.e.a;

import android.os.Handler;
import android.os.Looper;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

/** Applies parsed comments to player adapters on the UI thread. */
public final class CommentUi {
    private static final Handler MAIN = new Handler(Looper.getMainLooper());
    private CommentUi() {}
    private static final java.util.WeakHashMap<Object, ArrayList<?>> PENDING = new java.util.WeakHashMap<>();
    public static void list(Object fragment, ArrayList<?> comments) {
        final ArrayList<?> snapshot = comments == null ? new ArrayList<Object>() : new ArrayList<Object>(comments);
        Runnable action = () -> {
            try {
                android.app.Activity activity = (android.app.Activity) field(fragment, "a0");
                if (field(fragment, "Z") == null || activity == null) { PENDING.put(fragment, snapshot); return; }
                if (activity.isFinishing()) { PENDING.remove(fragment); return; }
                PENDING.remove(fragment);
                invoke(fragment, "bNative", snapshot);
            } catch (Exception error) { android.util.Log.w("nicoid-comments", "Comment list update failed", error); }
        };
        if (Looper.myLooper() == Looper.getMainLooper()) action.run(); else MAIN.post(action);
    }
    public static void ready(Object fragment) {
        ArrayList<?> comments = PENDING.remove(fragment);
        if (comments != null) list(fragment, comments);
    }

    public static void apply(Object model, ArrayList<?> comments) {
        Runnable action = () -> {
            try {
                Object fragment = field(model, "k");
                Object adapter = field(model, "b");
                boolean videoFragment = fragment != null && fragment.getClass().getName().equals("com.sauzask.nicoid.NicoidVideoFragment");
                if (videoFragment) {
                    if (adapter == null) adapter = field(fragment, "g1");
                    if (adapter != null) invoke(adapter, "a", comments);
                    else invoke(fragment, "b", comments);
                } else if (adapter != null) {
                    invoke(adapter, "a", comments);
                    android.util.Log.i("nicoid-comments", "Popup comments applied to overlay");
                    Object view = field(adapter, "a");
                    if (view != null && view.getClass().getName().equals("e.e.a.u")) invoke(view, "e");
                }
                Object listModel = field(model, "c");
                if (listModel != null) invoke(listModel, "a", comments);
            } catch (Exception error) {
                android.util.Log.w("nicoid-comments", "Comment adapter update failed", error);
            }
        };
        if (Looper.myLooper() == Looper.getMainLooper()) action.run(); else MAIN.post(action);
    }

    private static Object field(Object owner, String name) throws Exception {
        if (owner == null) return null;
        for (Class<?> type = owner.getClass(); type != null; type = type.getSuperclass()) {
            try { Field f = type.getDeclaredField(name); f.setAccessible(true); return f.get(owner); }
            catch (NoSuchFieldException ignored) { }
        }
        return null;
    }

    private static void invoke(Object owner, String name, ArrayList<?> comments) throws Exception {
        Method method = owner.getClass().getMethod(name, ArrayList.class);
        method.invoke(owner, comments);
    }
    private static void invoke(Object owner, String name) throws Exception {
        owner.getClass().getMethod(name).invoke(owner);
    }
}

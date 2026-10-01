package e.e.a;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.WeakHashMap;
import org.json.JSONArray;
import org.json.JSONObject;

/** Short feeds reuse nicoid's existing player, comments and playback policies. */
public final class ModernShorts {
    private static final String PLAYER = "com.sauzask.nicoid.NicoidVideoActivity";
    private static final String MODE = "nicoid_re_shorts";
    private static final String SESSION = "nicoid_re_shorts_session";
    private static final Handler MAIN = new Handler(android.os.Looper.getMainLooper());
    private static final LinkedHashMap<String, Feed> FEEDS = new LinkedHashMap<>();
    private static final WeakHashMap<Activity, State> STATES = new WeakHashMap<>();
    private static final WeakHashMap<Activity, Boolean> MENU_STATE = new WeakHashMap<>();
    private static boolean registered;
    private static final class Item {
        final String id, title;
        Item(String id, String title) { this.id = id; this.title = title; }
    }
    private static final class Feed {
        final String key = UUID.randomUUID().toString();
        final ArrayList<Item> items = new ArrayList<>();
    }
    private static final class State {
        Feed feed;
        int index;
        boolean busy, dead, dragging, blocked;
        float x, y;
        long downTime;
        TextView number;
        ProgressBar progress;
        View video;
        ViewTreeObserver.OnGlobalLayoutListener listener;
    }
    private ModernShorts() {}
    private static SharedPreferences prefs(Context c) { return PreferenceManager.getDefaultSharedPreferences(c); }
    private static int dp(Context c, int n) { return Math.round(c.getResources().getDisplayMetrics().density * n); }
    private static int color(Context c, int attr, int fallback) {
        TypedValue v = new TypedValue();
        return c.getTheme().resolveAttribute(attr, v, true) ? v.data : fallback;
    }
    private static void tint(Context c, ProgressBar p) {
        p.getIndeterminateDrawable().mutate().setColorFilter(color(c, 0x7f03005e, 0xff52cca3), PorterDuff.Mode.SRC_IN);
    }
    private static Button button(Context c, String text) {
        Button b = new Button(c); b.setText(text); b.setTextColor(color(c, 0x7f03005e, 0xff52cca3));
        b.setMinWidth(0); b.setMinimumWidth(0); b.setTextSize(14); return b;
    }
    private static Intent player(Context c, String id) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.nicovideo.jp/watch/" + id))
            .setClassName(c.getPackageName(), PLAYER).putExtra("intentselect", true);
    }
    public static void addMenu(Context c, ArrayList<?> rows) {
        register(c);
        if (c instanceof Activity) MENU_STATE.put((Activity)c, prefs(c).getBoolean("show_shorts_menu", true));
        if (!prefs(c).getBoolean("show_shorts_menu", true)) return;
        Intent i = player(c, "ss0").setData(Uri.parse("nicoid-re://shorts"));
        try {
            Class.forName("com.sauzask.nicoid.NicoidTopActivity").getMethod("a", ArrayList.class, boolean.class,
                String.class, String.class, Intent.class, int.class).invoke(null, rows, false, "ショート", "縦型動画をスワイプで切り替え", i, 0);
        } catch (Exception e) { log(e); }
    }
    public static void settings(PreferenceActivity a) {
        if (a.findPreference("show_shorts_menu") != null) return;
        PreferenceGroup group = (PreferenceGroup)a.findPreference("player");
        if (group == null) group = a.getPreferenceScreen();
        if (group == null) return;
        CheckBoxPreference p = new CheckBoxPreference(a); p.setKey("show_shorts_menu");
        p.setTitle("サイドバーにショートを表示"); p.setSummary("ランキングの下にショート動画の入口を表示します");
        p.setDefaultValue(true); group.addPreference(p);
    }
    /** Called after Activity.super.onCreate, before nicoid parses a watch URL. */
    public static boolean bootstrap(Activity a) {
        Uri u = a.getIntent().getData();
        if (u == null || !"nicoid-re".equals(u.getScheme()) || !"shorts".equals(u.getHost())) return false;
        register(a);
        State s = new State(); s.feed = new Feed(); STATES.put(a, s);
        a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        LinearLayout root = new LinearLayout(a); root.setOrientation(LinearLayout.VERTICAL); root.setGravity(Gravity.CENTER);
        root.setBackgroundColor(color(a, android.R.attr.colorBackground, 0xff1b1d22));
        TextView title = new TextView(a); title.setText("ショート"); title.setTextSize(22);
        title.setTextColor(color(a, android.R.attr.textColorPrimary, 0xffffffff)); root.addView(title);
        ProgressBar p = new ProgressBar(a); tint(a, p); root.addView(p); s.progress = p;
        TextView message = new TextView(a); message.setText("ショート動画を読み込んでいます…");
        message.setTextColor(color(a, android.R.attr.textColorPrimary, 0xffffffff)); root.addView(message);
        Button retry = button(a, "再試行"); retry.setVisibility(View.GONE); root.addView(retry);
        Button close = button(a, "戻る"); close.setOnClickListener(v -> a.finish()); root.addView(close);
        a.setContentView(root);
        Runnable load = () -> {
            if (s.busy) return;
            s.busy = true; p.setVisibility(View.VISIBLE); retry.setVisibility(View.GONE); message.setText("ショート動画を読み込んでいます…");
            request(null, (items, error) -> {
                s.busy = false;
                if (s.dead || a.isFinishing()) return;
                p.setVisibility(View.GONE);
                if (error != null || items.isEmpty()) { message.setText("ショート動画を取得できませんでした。通信状態を確認して再試行してください。"); retry.setVisibility(View.VISIBLE); return; }
                append(s.feed, items); remember(s.feed); launch(a, s, 0);
            });
        };
        retry.setOnClickListener(v -> load.run()); load.run(); return true;
    }
    /** Called after the original video fragment transaction has been committed. */
    public static void attach(Activity a) {
        Uri uri = a.getIntent().getData(); String id = uri == null ? null : uri.getLastPathSegment();
        if (!a.getIntent().getBooleanExtra(MODE, false) && (uri == null || !uri.getPath().startsWith("/shorts/"))) return;
        if (!ShortsRules.videoId(id)) return;
        register(a); a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        State s = new State(); s.index = a.getIntent().getIntExtra("nicoid_re_shorts_index", 0);
        s.feed = FEEDS.get(a.getIntent().getStringExtra(SESSION));
        if (s.feed == null) { s.feed = new Feed(); s.feed.items.add(new Item(id, "現在のショート")); s.index = 0; remember(s.feed); }
        if (s.index < 0 || s.index >= s.feed.items.size()) s.index = 0;
        STATES.put(a, s); final String current = id;
        install(a, s, 0);
        if (s.feed.items.size() == 1) extend(a, s, current, false);
    }
    private static void install(Activity a, State s, int attempt) {
        if (s.dead || a.isFinishing()) return;
        View video = find(a, "videoLayout");
        if (video == null) { if (attempt < 40) MAIN.postDelayed(() -> install(a, s, attempt + 1), 50); return; }
        s.video = video;
        View info = find(a, "info"); if (info != null) info.setVisibility(View.GONE);
        for (String id : new String[]{"prevbutton", "nextbutton", "fullscbutton"}) { View v = find(a, id); if (v != null) v.setVisibility(View.GONE); }
        FrameLayout content = (FrameLayout)a.findViewById(android.R.id.content);
        LinearLayout bar = new LinearLayout(a); bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setBackgroundColor(color(a, android.R.attr.colorBackground, 0xff1b1d22));
        Button prev = button(a, "前"); prev.setOnClickListener(v -> step(a, s, -1)); bar.addView(prev, new LinearLayout.LayoutParams(dp(a, 54), -1));
        TextView number = new TextView(a); number.setGravity(Gravity.CENTER); number.setTextColor(color(a, android.R.attr.textColorPrimary, 0xffffffff)); s.number = number;
        bar.addView(number, new LinearLayout.LayoutParams(0, -1, 1));
        ProgressBar p = new ProgressBar(a); tint(a, p); s.progress = p; p.setVisibility(s.busy ? View.VISIBLE : View.GONE);
        bar.addView(p, new LinearLayout.LayoutParams(dp(a, 28), dp(a, 28)));
        Button list = button(a, "一覧"); list.setOnClickListener(v -> showList(a, s)); bar.addView(list, new LinearLayout.LayoutParams(dp(a, 64), -1));
        Button next = button(a, "次"); next.setOnClickListener(v -> step(a, s, 1)); bar.addView(next, new LinearLayout.LayoutParams(dp(a, 54), -1));
        content.addView(bar, new FrameLayout.LayoutParams(-1, dp(a, 56), Gravity.BOTTOM)); update(s);
        s.listener = () -> {
            if (s.dead) return;
            int h = content.getHeight() - dp(a, 56);
            ViewGroup.LayoutParams lp = video.getLayoutParams();
            if (h > 0 && lp.height != h) { lp.height = h; video.setLayoutParams(lp); }
            if (info != null && info.getVisibility() != View.GONE) info.setVisibility(View.GONE);
            View loading = find(a, "videopro"); if (loading instanceof ProgressBar) tint(a, (ProgressBar)loading);
        };
        content.getViewTreeObserver().addOnGlobalLayoutListener(s.listener); s.listener.onGlobalLayout();
        Toast.makeText(a, "上にスワイプで次、下にスワイプで前の動画", Toast.LENGTH_SHORT).show();
    }
    private static View find(Activity a, String name) { return a.findViewById(a.getResources().getIdentifier(name, "id", a.getPackageName())); }
    private static void update(State s) { if (s.number != null) s.number.setText("ショート " + (s.index + 1) + "/" + s.feed.items.size()); }
    private static void showList(Activity a, State s) {
        String[] labels = new String[s.feed.items.size()];
        for (int n = 0; n < labels.length; n++) labels[n] = s.feed.items.get(n).title;
        new AlertDialog.Builder(a).setTitle("ショート動画").setSingleChoiceItems(labels, s.index, (d, n) -> { d.dismiss(); if (n != s.index && !s.busy) launch(a, s, n); })
            .setPositiveButton("一覧を更新", (d, n) -> extend(a, s, s.feed.items.get(s.index).id, false)).setNegativeButton("閉じる", null).show();
    }
    private static void step(Activity a, State s, int direction) {
        if (s.busy || s.dead || a.isFinishing()) return;
        int n = s.index + direction;
        if (n < 0) { Toast.makeText(a, "最初の動画です", 0).show(); return; }
        if (n >= s.feed.items.size()) { extend(a, s, s.feed.items.get(s.index).id, true); return; }
        launch(a, s, n);
    }
    private static void launch(Activity a, State s, int index) {
        if (s.dead || index < 0 || index >= s.feed.items.size()) return;
        s.busy = true;
        Intent i = player(a, s.feed.items.get(index).id).putExtra(MODE, true).putExtra(SESSION, s.feed.key)
            .putExtra("nicoid_re_shorts_index", index).putExtra("title", s.feed.items.get(index).title);
        // finish is set before onPause, so app-switch policies do not open a second player.
        a.startActivity(i); a.finish(); a.overridePendingTransition(0, 0);
    }
    private static void extend(Activity a, State s, String id, boolean next) {
        if (s.busy || s.dead) return;
        s.busy = true; if (s.progress != null) s.progress.setVisibility(View.VISIBLE);
        request(id, (items, error) -> {
            if (s.dead || a.isFinishing()) return;
            s.busy = false; if (s.progress != null) s.progress.setVisibility(View.GONE);
            if (error != null) { Toast.makeText(a, "一覧を取得できませんでした。もう一度お試しください", 0).show(); return; }
            int added = append(s.feed, items); update(s);
            if (next && s.index + 1 < s.feed.items.size()) launch(a, s, s.index + 1);
            else if (added == 0) Toast.makeText(a, "新しいショート動画が見つかりませんでした", 0).show();
        });
    }
    private static int append(Feed feed, ArrayList<Item> items) {
        int before = feed.items.size();
        for (Item i : items) { boolean found = false; for (Item old : feed.items) if (old.id.equals(i.id)) { found = true; break; }
            if (!found && feed.items.size() < 200) feed.items.add(i); }
        return feed.items.size() - before;
    }
    private static void remember(Feed f) { FEEDS.put(f.key, f); if (FEEDS.size() > 4) FEEDS.remove(FEEDS.keySet().iterator().next()); }
    /** Only capture a clear vertical swipe that starts outside interactive controls. */
    public static boolean touch(Activity a, MotionEvent e) {
        State s = STATES.get(a); if (s == null || s.video == null || s.dead) return false;
        int action = e.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            s.x = e.getRawX(); s.y = e.getRawY(); s.downTime = e.getDownTime(); s.dragging = false;
            s.blocked = s.busy || interactive(a.getWindow().getDecorView(), s.x, s.y) || e.getY() < dp(a, 24) || e.getY() > a.getWindow().getDecorView().getHeight() - dp(a, 24);
            return false;
        }
        if (e.getPointerCount() > 1 || action == MotionEvent.ACTION_POINTER_DOWN) { s.blocked = true; return false; }
        if (action == MotionEvent.ACTION_CANCEL) { boolean consumed = s.dragging; s.dragging = false; s.blocked = true; return consumed; }
        if (s.blocked || e.getDownTime() != s.downTime) return false;
        int direction = ShortsRules.direction(e.getRawX() - s.x, e.getRawY() - s.y, a.getResources().getDisplayMetrics().density, false);
        if (action == MotionEvent.ACTION_MOVE && direction != 0 && !s.dragging) {
            s.dragging = true; MotionEvent cancel = MotionEvent.obtain(e); cancel.setAction(MotionEvent.ACTION_CANCEL);
            a.getWindow().getDecorView().dispatchTouchEvent(cancel); cancel.recycle();
        }
        if (action == MotionEvent.ACTION_UP && s.dragging) { s.dragging = false; if (direction != 0) step(a, s, direction); return true; }
        return s.dragging;
    }
    private static boolean interactive(View v, float x, float y) {
        if (v.getVisibility() != View.VISIBLE) return false;
        int[] at = new int[2]; v.getLocationOnScreen(at);
        if (x < at[0] || y < at[1] || x >= at[0] + v.getWidth() || y >= at[1] + v.getHeight()) return false;
        if (v instanceof Button || v instanceof SeekBar || v instanceof EditText) return true;
        if (v instanceof ViewGroup) { ViewGroup g = (ViewGroup)v; for (int n = g.getChildCount() - 1; n >= 0; n--) if (interactive(g.getChildAt(n), x, y)) return true; }
        return false;
    }
    private interface Result { void done(ArrayList<Item> items, Exception error); }
    private static void request(String id, Result result) {
        final String cookie = cookie();
        new Thread(() -> {
            ArrayList<Item> items = new ArrayList<>(); Exception error = null; HttpURLConnection c = null;
            try {
                String url = "https://nvapi.nicovideo.jp/v1/playlist/recipe-id?recipeId=video_short_watch_recommendation&recipeVersion=1&site=nicovideo";
                if (id != null) url += "&videoId=" + Uri.encode(id) + "&currentVideoId=" + Uri.encode(id);
                c = (HttpURLConnection)new URL(url).openConnection(); c.setConnectTimeout(8000); c.setReadTimeout(8000);
                c.setRequestProperty("X-Frontend-Id", "6"); c.setRequestProperty("X-Frontend-Version", "0");
                c.setRequestProperty("Accept", "application/json"); c.setRequestProperty("Origin", "https://www.nicovideo.jp");
                if (!cookie.isEmpty()) c.setRequestProperty("Cookie", cookie);
                StringBuilder text = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(c.getInputStream(), "UTF-8"))) {
                    String line; while ((line = reader.readLine()) != null) { text.append(line); if (text.length() > 2000000) throw new IllegalStateException("Oversized feed"); }
                }
                JSONObject json = new JSONObject(text.toString());
                if (json.getJSONObject("meta").getInt("status") != 200) throw new IllegalStateException("Feed unavailable");
                JSONArray rows = json.getJSONObject("data").getJSONArray("items");
                for (int n = 0; n < rows.length(); n++) {
                    JSONObject row = rows.getJSONObject(n); String watch = row.optString("watchId");
                    if (!ShortsRules.videoId(watch)) continue;
                    JSONObject content = row.optJSONObject("content");
                    items.add(new Item(watch, content == null ? watch : content.optString("title", watch)));
                }
            } catch (Exception e) { error = e; log(e); } finally { if (c != null) c.disconnect(); }
            final Exception failure = error; MAIN.post(() -> result.done(items, failure));
        }, "nicoid-shorts-feed").start();
    }
    private static String cookie() {
        try { Class<?> v = Class.forName("e.e.a.v0"); Object store = v.getField("b").get(null);
            if (store == null) return "";
            return (String)v.getMethod("a", Class.forName("org.apache.http.client.CookieStore")).invoke(null, store);
        } catch (Exception e) { return ""; }
    }
    private static void register(Context c) {
        if (registered) return; registered = true;
        Application app = (Application)c.getApplicationContext();
        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            public void onActivityCreated(Activity a, Bundle b) {}
            public void onActivityStarted(Activity a) {}
            public void onActivityResumed(Activity a) {
                Boolean prior = MENU_STATE.get(a); boolean now = prefs(a).getBoolean("show_shorts_menu", true);
                if (prior != null && prior != now) {
                    View menu = find(a, "menu_listview");
                    if (menu instanceof ListView) try {
                        Class.forName("com.sauzask.nicoid.NicoidTopActivity").getMethod("a", Context.class, ListView.class).invoke(null, a, menu);
                    } catch (Exception e) { log(e); }
                    MENU_STATE.put(a, now);
                }
            }
            public void onActivityPaused(Activity a) {}
            public void onActivityStopped(Activity a) {}
            public void onActivitySaveInstanceState(Activity a, Bundle b) {}
            public void onActivityDestroyed(Activity a) {
                State s = STATES.remove(a); MENU_STATE.remove(a);
                if (s != null) { s.dead = true; if (s.listener != null) {
                    ViewTreeObserver observer = a.findViewById(android.R.id.content).getViewTreeObserver();
                    if (observer.isAlive()) observer.removeOnGlobalLayoutListener(s.listener);
                } }
            }
        });
    }
    private static void log(Exception e) { Log.w("nicoid-shorts", e.getClass().getSimpleName()); }
}

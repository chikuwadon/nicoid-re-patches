package e.e.a;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
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
import java.util.Iterator;
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
    private static final WeakHashMap<View, Boolean> REFRESH_WATCH = new WeakHashMap<>();
    private static final WeakHashMap<Activity, Integer> REFRESH_MONITORS = new WeakHashMap<>();
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
        boolean home;
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
        b.setMinWidth(0); b.setMinimumWidth(0); b.setTextSize(14); b.setAllCaps(false);
        b.setTypeface(android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL));
        b.setPadding(dp(c, 16), 0, dp(c, 16), 0);
        GradientDrawable bg = new GradientDrawable(); bg.setColor(color(c, android.R.attr.colorBackground, 0xff1b1d22));
        bg.setCornerRadius(dp(c, 24)); b.setBackground(bg); return b;
    }
    private static Intent player(Context c, String id) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.nicovideo.jp/watch/" + id))
            .setClassName(c.getPackageName(), PLAYER).putExtra("intentselect", true);
    }
    public static void addMenu(Context c, ArrayList<?> rows) {
        register(c);
        removeMovedMenuRows(rows);
        if (c instanceof Activity) MENU_STATE.put((Activity)c, prefs(c).getBoolean("show_shorts_menu", true));
        if (!prefs(c).getBoolean("show_shorts_menu", true)) return;
        Intent i = player(c, "ss0").setData(Uri.parse("nicoid-re://shorts"));
        try {
            Class.forName("com.sauzask.nicoid.NicoidTopActivity").getMethod("a", ArrayList.class, boolean.class,
                String.class, String.class, Intent.class, int.class).invoke(null, rows, false, "ショート", "縦型動画をスワイプで切り替え", i, 0);
        } catch (Exception e) { log(e); }
    }
    public static void settings(PreferenceActivity a) {
        PreferenceScreen screen = a.getPreferenceScreen(); if (screen == null) return;
        PreferenceGroup group = (PreferenceGroup)a.findPreference("player"); if (group == null) group = screen;
        if (a.findPreference("show_shorts_menu") == null) {
            CheckBoxPreference p = new CheckBoxPreference(a); p.setKey("show_shorts_menu");
            p.setTitle("サイドバーにショートを表示"); p.setSummary("ランキングの下にショート動画の入口を表示します");
            p.setDefaultValue(true); group.addPreference(p);
        }
        Preference version = a.findPreference("nicoid_patch_version");
        if (version != null) version.setSummary(PatchVersion.DISPLAY);
        addSettingsSection(a, screen, "setting_whole", "nicoid_other_category", "その他", "nicoid_restart_app",
            "アプリを再起動", "設定を反映するため、アプリを終了して再度起動します。再生中の動画は停止します。", true);
        addSettingsSection(a, screen, null, "nicoid_debug_category", "デバッグ", "nicoid_share_debug",
            "デバッグログを共有", "再生エラー、通信先、応答コードなどの診断ログを共有します。共有する前に内容と送信先を確認してください。", false);
    }
    private static void addSettingsSection(PreferenceActivity a, PreferenceScreen screen, String afterKey,
                                           String categoryKey, String heading, String rowKey, String title,
                                           String summary, boolean restart) {
        if (a.findPreference(rowKey) != null) return;
        PreferenceCategory category = (PreferenceCategory)a.findPreference(categoryKey);
        if (category == null) {
            category = new PreferenceCategory(a); category.setKey(categoryKey); category.setTitle(heading);
            Preference anchor = afterKey == null ? findCategoryByTitle(screen, "言語") : a.findPreference(afterKey);
            int order = anchor == null ? screen.getPreferenceCount() + 10 : anchor.getOrder();
            if (anchor != null) for (int i = 0; i < screen.getPreferenceCount(); i++) {
                Preference sibling = screen.getPreference(i);
                if (sibling != anchor && sibling.getOrder() > order) sibling.setOrder(sibling.getOrder() + 1);
            }
            category.setOrder(anchor == null ? order : order + 1);
            screen.addPreference(category);
        }
        Preference row = new Preference(a); row.setKey(rowKey); row.setTitle(title); row.setSummary(summary);
        row.setOnPreferenceClickListener(p -> {
            try { Class.forName("e.e.a.ModernDebug").getMethod(restart ? "restart" : "share", Context.class).invoke(null, a); }
            catch (Exception e) { log(e); Toast.makeText(a, "操作を実行できませんでした", Toast.LENGTH_SHORT).show(); }
            return true;
        });
        category.addPreference(row);
    }
    private static Preference findCategoryByTitle(PreferenceGroup group, String title) {
        for (int i = 0; i < group.getPreferenceCount(); i++) {
            Preference p = group.getPreference(i);
            if (p instanceof PreferenceCategory && title.contentEquals(p.getTitle())) return p;
        }
        return null;
    }
    private static void removeMovedMenuRows(ArrayList<?> rows) {
        for (Iterator<?> it = rows.iterator(); it.hasNext();) {
            Object row = it.next(); boolean remove = false;
            for (Class<?> type = row.getClass(); type != null && !remove; type = type.getSuperclass()) {
                for (java.lang.reflect.Field f : type.getDeclaredFields()) {
                    if (f.getType() != String.class) continue;
                    try { f.setAccessible(true); Object value = f.get(row);
                        if ("アプリを再起動".equals(value) || "デバッグログを共有".equals(value)) { remove = true; break; }
                    } catch (Exception ignored) { }
                }
            }
            if (remove) it.remove();
        }
    }
    /** Called after Activity.super.onCreate, before nicoid parses a watch URL. */
    public static boolean bootstrap(Activity a) {
        Uri u = a.getIntent().getData();
        if (u == null || !"nicoid-re".equals(u.getScheme()) || !"shorts".equals(u.getHost())) return false;
        register(a);
        State s = new State(); s.feed = new Feed(); s.home = true; STATES.put(a, s);
        a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Home home = buildHome(a); a.setContentView(home.root);
        Runnable load = () -> {
            if (s.busy) return;
            s.busy = true; home.progress.setVisibility(View.VISIBLE); home.retry.setVisibility(View.GONE);
            home.message.setText("ショート動画を読み込んでいます…");
            request(null, (items, error) -> {
                s.busy = false;
                if (s.dead || a.isFinishing()) return;
                home.progress.setVisibility(View.GONE);
                if (error != null || items.isEmpty()) { home.message.setText("ショート動画を取得できませんでした。通信状態を確認して再試行してください。"); home.retry.setVisibility(View.VISIBLE); return; }
                append(s.feed, items); remember(s.feed); renderHome(a, s, home);
            });
        };
        home.retry.setOnClickListener(v -> load.run()); home.refresh.setOnClickListener(v -> load.run()); load.run(); return true;
    }
    private static final class Home { LinearLayout root, rows; TextView message; ProgressBar progress; Button retry, refresh; }
    private static Home buildHome(Activity a) {
        Home h = new Home(); h.root = new LinearLayout(a); h.root.setOrientation(LinearLayout.VERTICAL);
        h.root.setPadding(dp(a, 20), dp(a, 18), dp(a, 20), dp(a, 12));
        h.root.setBackgroundColor(color(a, android.R.attr.colorBackground, 0xff101116));
        LinearLayout header = new LinearLayout(a); header.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout titles = new LinearLayout(a); titles.setOrientation(LinearLayout.VERTICAL);
        TextView title = new TextView(a); title.setText("ショート"); title.setTextSize(28);
        title.setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD);
        title.setTextColor(color(a, android.R.attr.textColorPrimary, 0xffffffff));
        TextView subtitle = new TextView(a); subtitle.setText("気になる動画を選んで再生"); subtitle.setTextSize(14);
        subtitle.setTextColor(color(a, android.R.attr.textColorSecondary, 0xffb8bbc5));
        titles.addView(title); titles.addView(subtitle); header.addView(titles, new LinearLayout.LayoutParams(0, -2, 1));
        h.refresh = button(a, "更新"); header.addView(h.refresh); h.root.addView(header);
        h.message = new TextView(a); h.message.setText("ショート動画を読み込んでいます…");
        h.message.setTextSize(14); h.message.setTextColor(color(a, android.R.attr.textColorSecondary, 0xffb8bbc5));
        h.message.setPadding(0, dp(a, 16), 0, dp(a, 8)); h.root.addView(h.message);
        h.progress = new ProgressBar(a); tint(a, h.progress); h.root.addView(h.progress);
        ScrollView scroll = new ScrollView(a); h.rows = new LinearLayout(a); h.rows.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(h.rows); h.root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        h.retry = button(a, "再試行"); h.retry.setVisibility(View.GONE); h.root.addView(h.retry);
        Button back = button(a, "アプリに戻る"); back.setOnClickListener(v -> a.finish()); h.root.addView(back);
        return h;
    }
    private static void renderHome(Activity a, State s, Home h) {
        h.rows.removeAllViews(); h.message.setText(s.feed.items.size() + " 本の動画");
        for (int i = 0; i < s.feed.items.size(); i++) {
            final int index = i; Item item = s.feed.items.get(i);
            LinearLayout card = new LinearLayout(a); card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(a, 16), dp(a, 12), dp(a, 16), dp(a, 12));
            GradientDrawable bg = new GradientDrawable(); bg.setColor(color(a, android.R.attr.colorBackground, 0xff1b1d22));
            bg.setCornerRadius(dp(a, 20)); card.setBackground(bg); card.setClickable(true); card.setFocusable(true);
            TextView itemTitle = new TextView(a); itemTitle.setText(item.title); itemTitle.setTextSize(16);
            itemTitle.setMaxLines(2); itemTitle.setTextColor(color(a, android.R.attr.textColorPrimary, 0xffffffff));
            TextView metadata = new TextView(a); metadata.setText("ニコニコ動画  •  " + item.id); metadata.setTextSize(12);
            metadata.setTextColor(color(a, android.R.attr.textColorSecondary, 0xffb8bbc5));
            card.addView(itemTitle); card.addView(metadata); card.setOnClickListener(v -> launch(a, s, index));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2); lp.bottomMargin = dp(a, 10); h.rows.addView(card, lp);
        }
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
        View videoView = find(a, "video_view");
        if (videoView != null) try {
            // ExoMedia normally measures to the encoded dimensions; Shorts should use the portrait viewport.
            videoView.getClass().getMethod("setMeasureBasedOnAspectRatioEnabled", boolean.class).invoke(videoView, false);
        } catch (Exception e) { log(e); }
        View info = find(a, "info"); if (info != null) info.setVisibility(View.GONE);
        for (String id : new String[]{"prevbutton", "nextbutton", "fullscbutton"}) { View v = find(a, id); if (v != null) v.setVisibility(View.GONE); }
        FrameLayout content = (FrameLayout)a.findViewById(android.R.id.content);
        LinearLayout bar = new LinearLayout(a); bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(dp(a, 10), dp(a, 6), dp(a, 10), dp(a, 6));
        GradientDrawable barBg = new GradientDrawable(); barBg.setColor(color(a, android.R.attr.colorBackground, 0xff1b1d22));
        barBg.setCornerRadius(dp(a, 22)); bar.setBackground(barBg);
        Button prev = button(a, "前へ"); prev.setOnClickListener(v -> step(a, s, -1)); bar.addView(prev, new LinearLayout.LayoutParams(-2, -1));
        TextView number = new TextView(a); number.setGravity(Gravity.CENTER); number.setTextColor(color(a, android.R.attr.textColorPrimary, 0xffffffff)); s.number = number;
        bar.addView(number, new LinearLayout.LayoutParams(0, -1, 1));
        ProgressBar p = new ProgressBar(a); tint(a, p); s.progress = p; p.setVisibility(s.busy ? View.VISIBLE : View.GONE);
        bar.addView(p, new LinearLayout.LayoutParams(dp(a, 24), dp(a, 24)));
        Button next = button(a, "次へ"); next.setOnClickListener(v -> step(a, s, 1)); bar.addView(next, new LinearLayout.LayoutParams(-2, -1));
        number.setOnClickListener(v -> showList(a, s)); number.setClickable(true);
        FrameLayout.LayoutParams barLp = new FrameLayout.LayoutParams(-1, dp(a, 64), Gravity.BOTTOM);
        barLp.setMargins(dp(a, 12), 0, dp(a, 12), dp(a, 8)); content.addView(bar, barLp); update(s);
        TextView home = new TextView(a); home.setText("⌂  ショートのホーム"); home.setTextSize(14);
        home.setTextColor(color(a, 0x7f03005e, 0xff52cca3)); home.setGravity(Gravity.CENTER_VERTICAL);
        home.setPadding(dp(a, 16), 0, dp(a, 16), 0); home.setBackground(barBg); home.setOnClickListener(v -> a.finish());
        FrameLayout.LayoutParams homeLp = new FrameLayout.LayoutParams(-2, dp(a, 48), Gravity.TOP | Gravity.START);
        homeLp.setMargins(dp(a, 12), dp(a, 10), 0, 0); content.addView(home, homeLp);
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
        s.busy = true; s.index = index;
        Intent i = player(a, s.feed.items.get(index).id).putExtra(MODE, true).putExtra(SESSION, s.feed.key)
            .putExtra("nicoid_re_shorts_index", index).putExtra("title", s.feed.items.get(index).title);
        // finish is set before onPause, so app-switch policies do not open a second player.
        a.startActivity(i); if (!s.home) a.finish(); a.overridePendingTransition(0, 0);
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
        if (v instanceof Button || v instanceof SeekBar || v instanceof EditText || v.isClickable()) return true;
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
                int generation = REFRESH_MONITORS.containsKey(a) ? REFRESH_MONITORS.get(a) + 1 : 1;
                REFRESH_MONITORS.put(a, generation); monitorRefresh(a, generation);
                Boolean prior = MENU_STATE.get(a); boolean now = prefs(a).getBoolean("show_shorts_menu", true);
                if (prior != null && prior != now) {
                    View menu = find(a, "menu_listview");
                    if (menu instanceof ListView) try {
                        Class.forName("com.sauzask.nicoid.NicoidTopActivity").getMethod("a", Context.class, ListView.class).invoke(null, a, menu);
                    } catch (Exception e) { log(e); }
                    MENU_STATE.put(a, now);
                }
            }
            public void onActivityPaused(Activity a) {
                Integer generation = REFRESH_MONITORS.get(a);
                REFRESH_MONITORS.put(a, generation == null ? 1 : generation + 1);
            }
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
    private static void watchRefresh(View root) {
        if (root.getClass().getName().equals("androidx.swiperefreshlayout.widget.SwipeRefreshLayout")) {
            View swipe = root;
            if (!REFRESH_WATCH.containsKey(swipe)) {
                REFRESH_WATCH.put(swipe, false);
                swipe.addOnLayoutChangeListener((v, l, t, r, b, ol, ot, or, ob) -> {
                    boolean refreshing = isRefreshing(swipe);
                    if (refreshing && !Boolean.TRUE.equals(REFRESH_WATCH.get(swipe))) {
                        REFRESH_WATCH.put(swipe, true);
                        MAIN.postDelayed(() -> {
                            if (isRefreshing(swipe)) {
                                setRefreshing(swipe, false);
                                Toast.makeText(swipe.getContext(), "更新が完了しませんでした。もう一度お試しください。", Toast.LENGTH_SHORT).show();
                            }
                            REFRESH_WATCH.put(swipe, false);
                        }, 10000);
                    } else if (!refreshing) REFRESH_WATCH.put(swipe, false);
                });
            }
            return;
        }
        if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup)root;
            for (int i = 0; i < group.getChildCount(); i++) watchRefresh(group.getChildAt(i));
        }
    }
    private static void monitorRefresh(Activity a, int generation) {
        MAIN.postDelayed(() -> {
            if (!Integer.valueOf(generation).equals(REFRESH_MONITORS.get(a)) || a.isFinishing()) return;
            watchRefresh(a.getWindow().getDecorView());
            monitorRefresh(a, generation);
        }, 1000);
    }
    private static boolean isRefreshing(View v) {
        try { return (Boolean)v.getClass().getMethod("isRefreshing").invoke(v); } catch (Exception e) { return false; }
    }
    private static void setRefreshing(View v, boolean value) {
        try { v.getClass().getMethod("setRefreshing", boolean.class).invoke(v, value); } catch (Exception ignored) { }
    }
    private static void log(Exception e) { Log.w("nicoid-shorts", e.getClass().getSimpleName()); }
}

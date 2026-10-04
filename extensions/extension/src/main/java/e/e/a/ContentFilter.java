package e.e.a;

import android.content.Context;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.content.res.ColorStateList;
import android.util.TypedValue;
import java.util.ArrayList;
import java.lang.reflect.Method;
import org.json.JSONObject;

public final class ContentFilter {
    private ContentFilter() { }
    private static final String KEY = "nicoid_content_keywords";
    private static final String CHANNELS = "nicoid_content_channels";
    private static String t(String text) { return UiStrings.translate(text); }
    public static void settings(PreferenceActivity activity) {
        if (activity.findPreference(KEY) != null) return;
        PreferenceScreen screen = activity.getPreferenceManager().createPreferenceScreen(activity);
        screen.setKey("nicoid_content_filter"); screen.setTitle(t("コンテンツフィルター"));
        entry(activity, screen, KEY, "キーワードフィルタ", "動画タイトルに含まれるキーワードをカンマまたは改行で区切って入力してください。次の一覧読み込みから非表示になります。");
        entry(activity, screen, CHANNELS, "チャンネルフィルタ", "非表示にする投稿者・チャンネル名をカンマまたは改行で区切って入力してください。名前の部分一致で判定します。次の一覧読み込みから反映されます。");
        PreferenceGroup root = activity.getPreferenceScreen();
        // Preserve existing sections while placing this screen directly after comments.
        int after = root.getPreferenceCount();
        ArrayList<Preference> sections = new ArrayList<>();
        for (int n = 0; n < root.getPreferenceCount(); n++) {
            Preference section = root.getPreference(n); sections.add(section);
            if ("comment".equals(section.getKey())) after = n + 1;
        }
        for (int n = 0; n < sections.size(); n++) sections.get(n).setOrder(n * 2);
        screen.setOrder(after * 2 - 1); root.addPreference(screen);
    }
    private static void entry(PreferenceActivity activity, PreferenceScreen screen, String key, String title, String summary) {
        EditTextPreference words = new EditTextPreference(activity);
        words.setKey(key); words.setTitle(t(title)); words.setDialogTitle(t(title));
        words.setSummary(t(summary));
        words.getEditText().setSingleLine(false);
        words.getEditText().setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        TypedValue color = new TypedValue();
        words.getEditText().getContext().getTheme().resolveAttribute(0x7f03005e, color, true);
        int accent = color.resourceId == 0 ? color.data : activity.getResources().getColorStateList(color.resourceId).getDefaultColor();
        words.getEditText().setBackgroundTintList(ColorStateList.valueOf(accent));
        screen.addPreference(words);
    }
    public static boolean blocked(Context context, String title) {
        return blocked(context, title, null);
    }
    public static boolean blocked(Context context, String title, String channel) {
        android.content.SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return ContentFilterRules.blocked(title, ContentFilterRules.keywords(prefs.getString(KEY, ""))) ||
            ContentFilterRules.blocked(channel, ContentFilterRules.keywords(prefs.getString(CHANNELS, "")));
    }
    /** Same owner-name sources used by normal video rows, including channel videos. */
    public static String owner(JSONObject row) {
        if (row == null) return "";
        for (String key : new String[]{"owner", "user", "channel"}) {
            JSONObject source = row.optJSONObject(key);
            if (source == null) continue;
            for (String name : new String[]{"nickname", "name"}) {
                String value = source.optString(name, "");
                if (!value.isEmpty()) return value;
            }
            String nested = owner(source);
            if (!nested.isEmpty()) return nested;
        }
        String value = row.optString("ownerName", "");
        return value.isEmpty() ? row.optString("uploaderName", "") : value;
    }
    public static void rememberHistory(JSONObject record) {
        try {
            JSONObject watch = (JSONObject) Class.forName("e.e.a.ModernPlayback").getField("latestWatch").get(null);
            JSONObject video = watch == null ? null : watch.optJSONObject("video");
            if (video != null && HistoryRules.same(record.optString("videourl"), video.optString("id"))) {
                String name = owner(watch);
                if (!name.isEmpty()) record.put("ownerName", name);
            }
        } catch (ReflectiveOperationException | org.json.JSONException ignored) { }
    }
    public static void restoreHistory(Object row, JSONObject record) {
        try { row.getClass().getField("y").set(row, owner(record)); }
        catch (ReflectiveOperationException error) { throw new IllegalStateException("Unsupported history row", error); }
    }
    /** The adapter and fragment share this list, preserving click and selection indices. */
    public static void filter(Object adapter) {
        try {
            Class<?> type = adapter.getClass();
            Context context = (Context) type.getField("d").get(adapter);
            String[] words = ContentFilterRules.keywords(PreferenceManager.getDefaultSharedPreferences(context).getString(KEY, ""));
            String[] channels = ContentFilterRules.keywords(PreferenceManager.getDefaultSharedPreferences(context).getString(CHANNELS, ""));
            if (words.length == 0 && channels.length == 0) return;
            ArrayList<?> rows = (ArrayList<?>) type.getField("b").get(adapter);
            Method value = Class.forName("e.e.a.x1").getMethod("a", String.class);
            for (int n = rows.size() - 1; n >= 0; n--) {
                Object row = rows.get(n);
                Object title = value.invoke(row, "title");
                Object url = value.invoke(row, "videourl");
                Object channel = row.getClass().getField("y").get(row);
                if (url != null && url.toString().matches(".*?/(watch|shorts)/.*") &&
                    (ContentFilterRules.blocked(title == null ? null : title.toString(), words) ||
                    ContentFilterRules.blocked(channel == null ? null : channel.toString(), channels))) rows.remove(n);
            }
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Unsupported video list", error);
        }
    }
}

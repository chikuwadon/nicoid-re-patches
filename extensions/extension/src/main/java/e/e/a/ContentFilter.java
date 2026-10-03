package e.e.a;

import android.content.Context;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.content.res.ColorStateList;
import android.util.TypedValue;
import java.util.ArrayList;
import java.lang.reflect.Method;

public final class ContentFilter {
    private ContentFilter() { }
    private static final String KEY = "nicoid_content_keywords";
    private static String t(String text) { return UiStrings.translate(text); }
    public static void settings(PreferenceActivity activity) {
        if (activity.findPreference(KEY) != null) return;
        PreferenceScreen screen = activity.getPreferenceManager().createPreferenceScreen(activity);
        screen.setTitle(t("コンテンツフィルター"));
        EditTextPreference words = new EditTextPreference(activity);
        words.setKey(KEY); words.setTitle(t("キーワードでフィルター"));
        words.setDialogTitle(t("キーワードでフィルター"));
        words.setSummary(t("動画タイトルに含まれるキーワードをカンマまたは改行で区切って入力してください。次の一覧読み込みから非表示になります。"));
        words.getEditText().setSingleLine(false);
        words.getEditText().setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        TypedValue color = new TypedValue();
        words.getEditText().getContext().getTheme().resolveAttribute(0x7f03005e, color, true);
        int accent = color.resourceId == 0 ? color.data : activity.getResources().getColorStateList(color.resourceId).getDefaultColor();
        words.getEditText().setBackgroundTintList(ColorStateList.valueOf(accent));
        screen.addPreference(words); activity.getPreferenceScreen().addPreference(screen);
    }
    public static boolean blocked(Context context, String title) {
        return ContentFilterRules.blocked(title, ContentFilterRules.keywords(
            PreferenceManager.getDefaultSharedPreferences(context).getString(KEY, "")));
    }
    /** The adapter and fragment share this list, preserving click and selection indices. */
    public static void filter(Object adapter) {
        try {
            Class<?> type = adapter.getClass();
            Context context = (Context) type.getField("d").get(adapter);
            String[] words = ContentFilterRules.keywords(PreferenceManager.getDefaultSharedPreferences(context).getString(KEY, ""));
            if (words.length == 0) return;
            ArrayList<?> rows = (ArrayList<?>) type.getField("b").get(adapter);
            Method value = Class.forName("e.e.a.x1").getMethod("a", String.class);
            for (int n = rows.size() - 1; n >= 0; n--) {
                Object row = rows.get(n);
                Object title = value.invoke(row, "title");
                Object url = value.invoke(row, "videourl");
                if (url != null && url.toString().matches(".*?/(watch|shorts)/.*") &&
                    ContentFilterRules.blocked(title == null ? null : title.toString(), words)) rows.remove(n);
            }
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Unsupported video list", error);
        }
    }
}

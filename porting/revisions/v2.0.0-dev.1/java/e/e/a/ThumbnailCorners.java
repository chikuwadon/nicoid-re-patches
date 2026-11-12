package e.e.a;

import android.content.Context;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.WeakHashMap;

/** Configurable thumbnail shape shared by normal videos and Niconico Live. */
public final class ThumbnailCorners {
    private static final String ENABLED = "thumbnail_rounded_corners";
    private static final int ROW_THUMBNAIL = 0x7f0800cd;
    private static final int NATIVE_THUMBNAIL = 0x7f0801b2;
    private static final WeakHashMap<View, OriginalOutline> ORIGINALS = new WeakHashMap<>();
    private static final ViewOutlineProvider OUTLINE = new ViewOutlineProvider() {
        @Override public void getOutline(View view, Outline outline) {
            float radius = 4 * view.getResources().getDisplayMetrics().density;
            outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
        }
    };

    private static final class OriginalOutline {
        final ViewOutlineProvider provider;
        final boolean clip;
        OriginalOutline(View view) {
            provider = view.getOutlineProvider();
            clip = view.getClipToOutline();
        }
    }

    public static boolean enabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(ENABLED, true);
    }

    public static void settings(PreferenceActivity activity, PreferenceGroup other) {
        if (activity == null || other == null || activity.findPreference(ENABLED) != null) return;
        Preference anchor = other.findPreference("show_nicolive_menu");
        if (anchor == null) anchor = other.findPreference("show_shorts_menu");
        int order = anchor == null ? other.getPreferenceCount() : anchor.getOrder() + 1;
        ArrayList<Preference> later = new ArrayList<>();
        for (int i = 0; i < other.getPreferenceCount(); i++) {
            Preference row = other.getPreference(i);
            if (row.getOrder() >= order && row.getOrder() < Integer.MAX_VALUE) later.add(row);
        }
        for (Preference row : later) row.setOrder(row.getOrder() + 1);
        CheckBoxPreference rounded = new CheckBoxPreference(activity);
        rounded.setKey(ENABLED);
        rounded.setDefaultValue(true);
        rounded.setOrder(order);
        rounded.setTitle(PanelUi.tr(activity, "サムネイルの角丸表示",
                "Rounded thumbnails", "縮圖圓角顯示"));
        rounded.setSummary(PanelUi.tr(activity,
                "OFFにすると、従来の角丸なしのサムネイルを表示します",
                "Turn off to show thumbnails with square corners, as before.",
                "關閉後，縮圖會恢復為原本的直角樣式"));
        other.addPreference(rounded);
    }

    public static void apply(View view) {
        if (view == null) return;
        if (!enabled(view.getContext())) {
            OriginalOutline original = ORIGINALS.remove(view);
            if (original != null) {
                view.setOutlineProvider(original.provider);
                view.setClipToOutline(original.clip);
            }
            return;
        }
        if (view.getOutlineProvider() == OUTLINE) return;
        ORIGINALS.put(view, new OriginalOutline(view));
        view.setOutlineProvider(OUTLINE);
        view.setClipToOutline(true);
    }

    /** Live thumbnails contain badges; clip the frame and leave badge corners unchanged. */
    public static void applyFrame(View frame) {
        if (frame == null) return;
        apply(frame);
        Drawable background = frame.getBackground();
        if (background instanceof GradientDrawable) {
            ((GradientDrawable) background).setCornerRadius(
                    enabled(frame.getContext()) ? 4 * frame.getResources().getDisplayMetrics().density : 0);
        }
    }

    public static void applyNative(View row) {
        if (row == null) return;
        View thumbnail = row.findViewById(ROW_THUMBNAIL);
        if (thumbnail instanceof ImageView) apply(thumbnail);
        thumbnail = row.findViewById(NATIVE_THUMBNAIL);
        if (thumbnail instanceof ImageView) apply(thumbnail);
    }

    public static void load(String url, ImageView image, TextView placeholder) {
        apply(image);
        ShortImages.load(url, image, placeholder);
    }
}

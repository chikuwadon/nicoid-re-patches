package e.e.a;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

final class Media3ControlStyle {
    private Media3ControlStyle() {}

    static void apply(SeekBar bar, View controls) {
        int accent = accent(bar);
        int height = dp(bar, 4);
        LayerDrawable track = new LayerDrawable(new Drawable[] {
            capsule(0x50ffffff, height),
            new ClipDrawable(capsule(0xbaffffff, height), Gravity.START, ClipDrawable.HORIZONTAL),
            new ClipDrawable(capsule(accent, height), Gravity.START, ClipDrawable.HORIZONTAL)
        });
        int[] ids = {android.R.id.background, android.R.id.secondaryProgress, android.R.id.progress};
        for (int layer = 0; layer < ids.length; layer++) {
            track.setId(layer, ids[layer]);
            track.setLayerHeight(layer, height);
            track.setLayerGravity(layer, Gravity.FILL_HORIZONTAL | Gravity.CENTER_VERTICAL);
        }
        bar.setProgressTintList(null);
        bar.setProgressBackgroundTintList(null);
        bar.setSecondaryProgressTintList(null);
        bar.setThumbTintList(null);
        bar.setProgressDrawable(track);

        StateListDrawable thumb = new StateListDrawable();
        thumb.addState(new int[] {-android.R.attr.state_enabled}, circle(0x00ffffff, dp(bar, 16)));
        thumb.addState(new int[] {android.R.attr.state_pressed}, circle(accent, dp(bar, 16)));
        thumb.addState(new int[] {android.R.attr.state_focused}, circle(accent, dp(bar, 16)));
        thumb.addState(new int[0], circle(accent, dp(bar, 12)));
        bar.setThumb(thumb);
        bar.setThumbOffset(dp(bar, 6));
        bar.setSplitTrack(false);

        styleTime(controls.findViewById(0x7f080085));
        styleTime(controls.findViewById(0x7f08015d));
    }

    private static void styleTime(View currentTime) {
        if (currentTime instanceof TextView) {
            TextView time = (TextView) currentTime;
            time.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
            time.setFontFeatureSettings("tnum");
            time.setShadowLayer(dp(time, 1), 0, dp(time, 1), 0xb0000000);
        }
    }

    private static int accent(View view) {
        TypedValue color = new TypedValue();
        if (!view.getContext().getTheme().resolveAttribute(0x7f03005e, color, true)) {
            return Color.WHITE;
        }
        if (color.resourceId == 0) {
            return color.data;
        }
        ColorStateList colors = view.getContext().getColorStateList(color.resourceId);
        return colors.getColorForState(view.getDrawableState(), colors.getDefaultColor());
    }

    private static GradientDrawable capsule(int color, int width) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(width / 2f);
        drawable.setColor(color);
        return drawable;
    }

    private static GradientDrawable circle(int color, int size) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(color);
        drawable.setSize(size, size);
        return drawable;
    }

    private static int dp(View view, int value) {
        return Math.round(value * view.getResources().getDisplayMetrics().density);
    }
}

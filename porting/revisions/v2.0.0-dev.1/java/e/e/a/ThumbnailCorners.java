package e.e.a;

import android.graphics.Outline;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.TextView;

public final class ThumbnailCorners {
    private static final int ROW_THUMBNAIL = 0x7f0800cd;
    private static final int NATIVE_THUMBNAIL = 0x7f0801b2;
    private static final ViewOutlineProvider OUTLINE = new ViewOutlineProvider() {
        @Override public void getOutline(View view, Outline outline) {
            float radius = 4 * view.getResources().getDisplayMetrics().density;
            outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
        }
    };

    public static void apply(View view) {
        if (view == null || view.getOutlineProvider() == OUTLINE) return;
        view.setOutlineProvider(OUTLINE);
        view.setClipToOutline(true);
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

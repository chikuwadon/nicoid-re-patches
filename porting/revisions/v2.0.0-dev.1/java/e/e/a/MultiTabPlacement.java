package e.e.a;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

final class MultiTabPlacement {
    private static final String KEY = "multiple_video_tabs_at_bottom";

    static void settings(PreferenceGroup group, Preference enabled) {
        Context context = enabled.getContext();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Preference placement = new Preference(context);
        placement.setKey(KEY);
        placement.setPersistent(false);
        placement.setTitle(PanelUi.tr(context, "複数タブの配置位置", "Multiple tab position", "多分頁列位置"));
        String[] positions = {
            PanelUi.tr(context, "上", "Top", "上方"),
            PanelUi.tr(context, "下", "Bottom", "下方")
        };
        String current = PanelUi.tr(context, "現在値：", "Current: ", "目前：");
        String description = PanelUi.tr(context,
                "複数タブのタブバーを画面の上または下に表示します",
                "Show the multiple-tab bar at the top or bottom of the screen",
                "將多分頁列顯示在畫面的上方或下方");
        placement.setSummary(current + positions[preferences.getBoolean(KEY, false) ? 1 : 0] + "\n" + description);
        placement.setOnPreferenceClickListener(row -> {
            UiDialogs.show(UiDialogs.builder(context).setTitle(placement.getTitle())
                    .setSingleChoiceItems(positions, preferences.getBoolean(KEY, false) ? 1 : 0, (dialog, index) -> {
                        preferences.edit().putBoolean(KEY, index == 1).apply();
                        placement.setSummary(current + positions[index] + "\n" + description);
                        dialog.dismiss();
                    }).setNegativeButton(android.R.string.cancel, null).create());
            return true;
        });
        group.addPreference(placement);
        placement.setDependency(enabled.getKey());
    }

    static void insert(LinearLayout parent, View bar, int topIndex, ViewGroup.LayoutParams params) {
        parent.addView(bar, atBottom(bar) ? parent.getChildCount() : topIndex, params);
    }

    static void update(LinearLayout parent, View bar) {
        if (parent == null || bar == null || bar.getParent() != parent) return;
        boolean bottom = atBottom(bar);
        int desiredIndex = bottom ? parent.getChildCount() - 1 : 0;
        if (parent.indexOfChild(bar) == desiredIndex) return;
        ViewGroup.LayoutParams params = bar.getLayoutParams();
        parent.removeView(bar);
        parent.addView(bar, desiredIndex, params);
    }

    private static boolean atBottom(View bar) {
        return PreferenceManager.getDefaultSharedPreferences(bar.getContext()).getBoolean(KEY, false);
    }
}

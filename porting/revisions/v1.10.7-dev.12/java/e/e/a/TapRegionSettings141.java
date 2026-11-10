package e.e.a;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;

public final class TapRegionSettings141 {
    private static final String ANCHOR = "gesture_quiet_controls";

    private TapRegionSettings141() {}

    private static String title(Context c) {
        return Dev112.tr(c, "シングルタップの操作部表示領域", "Single-tap control area", "單點切換控制項的區域");
    }

    private static String description(Context c) {
        return Dev112.tr(c, "シングルタップで再生コントロールを開閉できる領域を設定します",
                "Choose where a single tap toggles playback controls", "設定單點可開關播放控制項的區域");
    }

    private static String[] names(Context c) {
        return new String[]{
            Dev112.tr(c, "画面全体", "Entire video", "整個畫面"),
            Dev112.tr(c, "左右端", "Left and right edges", "左右兩側"),
            Dev112.tr(c, "中央", "Center", "中央"),
            Dev112.tr(c, "カスタム", "Custom", "自訂")
        };
    }

    private static PreferenceGroup parent(PreferenceGroup group, Preference anchor) {
        for (int i = 0; i < group.getPreferenceCount(); i++) {
            Preference child = group.getPreference(i);
            if (child == anchor) return group;
            if (child instanceof PreferenceGroup) {
                PreferenceGroup result = parent((PreferenceGroup) child, anchor);
                if (result != null) return result;
            }
        }
        return null;
    }

    public static void install(PreferenceActivity activity) {
        if (activity.getPreferenceScreen() == null || activity.findPreference(TapControls141.KEY) != null) return;
        Preference anchor = activity.findPreference(ANCHOR);
        if (anchor == null) return;
        PreferenceGroup group = parent(activity.getPreferenceScreen(), anchor);
        if (group == null) return;
        ArrayList<Preference> siblings = new ArrayList<>();
        for (int i = 0; i < group.getPreferenceCount(); i++) siblings.add(group.getPreference(i));
        for (int i = 0; i < siblings.size(); i++) siblings.get(i).setOrder(i * 10);
        Preference item = new Preference(activity);
        item.setPersistent(false);
        item.setKey(TapControls141.KEY);
        item.setOrder(anchor.getOrder() + 1);
        item.setTitle(title(activity));
        updateSummary(item);
        item.setOnPreferenceClickListener(p -> { open(activity, item); return true; });
        group.addPreference(item);
    }

    private static void updateSummary(Preference item) {
        Context c = item.getContext();
        item.setSummary(description(c) + "\n" + names(c)[TapControls141.settings(c).mode]);
    }

    private static void open(PreferenceActivity activity, Preference item) {
        try {
            AlertDialog.Builder builder = (AlertDialog.Builder) Dev112.staticCall("e.e.a.UiDialogs", "builder",
                    new Class<?>[]{Context.class}, activity);
            Editor editor = new Editor(builder.getContext(), TapControls141.settings(activity));
            builder.setTitle(title(activity)).setView(editor.scroll)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        TapControls141.save(activity, editor.value());
                        updateSummary(item);
                    }).show();
        } catch (Exception failure) {
            android.util.Log.w("nicoid-controls", "Cannot open single-tap area editor", failure);
        }
    }

    private static final class Editor {
        final Context context;
        final ScrollView scroll;
        final RadioGroup presets;
        final Preview preview;
        final LinearLayout rows;
        final Button add;
        final int accent;
        final int inactive;
        final int firstId = View.generateViewId();
        final int[] ids = {firstId, View.generateViewId(), View.generateViewId(), View.generateViewId()};
        final ArrayList<int[]> ranges = new ArrayList<>();
        final TextView[][] values = new TextView[TapRegions141.LIMIT][2];
        int mode;
        boolean updating;

        Editor(Context context, TapRegions141 current) throws Exception {
            this.context = context;
            accent = (Integer) Dev112.staticCall("e.e.a.UiDialogs", "accent",
                    new Class<?>[]{Context.class}, context);
            TextView text = new TextView(context);
            inactive = text.getCurrentTextColor();
            LinearLayout content = new LinearLayout(context);
            content.setOrientation(LinearLayout.VERTICAL);
            content.setPadding(dp(20), dp(8), dp(20), dp(8));
            scroll = new ScrollView(context);
            scroll.addView(content);

            presets = new RadioGroup(context);
            String[] labels = names(context);
            for (int i = 0; i < labels.length; i++) {
                RadioButton option = new RadioButton(context);
                option.setId(ids[i]);
                option.setText(labels[i]);
                option.setMinHeight(dp(44));
                option.setButtonTintList(new android.content.res.ColorStateList(
                        new int[][]{new int[]{android.R.attr.state_checked}, new int[0]},
                        new int[]{accent, inactive}));
                presets.addView(option);
            }
            content.addView(presets);
            TextView legend = new TextView(context);
            legend.setText(tr("色付きの範囲で開閉 · 境界をドラッグして調整",
                    "Colored areas toggle controls · Drag a boundary to adjust",
                    "著色區域可開關控制項 · 拖曳邊界調整"));
            content.addView(legend);
            preview = new Preview(this);
            content.addView(preview, new LinearLayout.LayoutParams(-1, dp(100)));
            rows = new LinearLayout(context);
            rows.setOrientation(LinearLayout.VERTICAL);
            content.addView(rows);
            add = button(tr("領域を追加", "Add area", "新增區域"), this::addArea);
            content.addView(add);
            content.addView(button(tr("デフォルトに戻す", "Restore default", "恢復預設"),
                    () -> set(TapRegions141.preset(TapRegions141.ALL))));
            set(current);
            presets.setOnCheckedChangeListener((group, checked) -> {
                if (updating) return;
                for (int i = 0; i < ids.length; i++) {
                    if (ids[i] != checked) continue;
                    if (i == TapRegions141.CUSTOM) {
                        mode = i;
                        rebuild();
                    } else set(TapRegions141.preset(i));
                    break;
                }
            });
        }

        int dp(int value) { return Math.round(value * context.getResources().getDisplayMetrics().density); }
        String tr(String ja, String en, String zh) { return Dev112.tr(context, ja, en, zh); }

        Button button(String text, Runnable action) {
            Button button = new Button(context, null, android.R.attr.borderlessButtonStyle);
            button.setText(text);
            button.setTextColor(accent);
            button.setAllCaps(false);
            button.setMinHeight(dp(44));
            button.setMinimumWidth(dp(44));
            button.setStateListAnimator(null);
            button.setOnClickListener(v -> action.run());
            return button;
        }

        void set(TapRegions141 value) {
            mode = value.mode;
            ranges.clear();
            for (int[] range : value.ranges) ranges.add(range.clone());
            selectMode();
            rebuild();
        }

        void selectMode() {
            updating = true;
            presets.check(ids[mode]);
            updating = false;
        }

        TapRegions141 value() {
            return mode == TapRegions141.CUSTOM
                    ? TapRegions141.custom(ranges.toArray(new int[0][]))
                    : TapRegions141.preset(mode);
        }

        void addArea() {
            if (ranges.size() >= TapRegions141.LIMIT) return;
            int bestStart = 0, bestEnd = 0, start = 0;
            TapRegions141 active = value();
            for (int x = 0; x <= 100; x++) {
                if (x == 100 || active.contains(x, 100, 0)) {
                    if (x - start > bestEnd - bestStart) {
                        bestStart = start;
                        bestEnd = x;
                    }
                    start = x + 1;
                }
            }
            ranges.add(bestEnd > bestStart ? new int[]{bestStart, bestEnd} : new int[]{40, 60});
            mode = TapRegions141.CUSTOM;
            selectMode();
            rebuild();
        }

        void adjust(int index, int edge, int percent) {
            int[] range = ranges.get(index);
            int next = edge == 0 ? Math.max(0, Math.min(range[1] - 1, percent))
                    : Math.min(100, Math.max(range[0] + 1, percent));
            if (range[edge] == next) return;
            range[edge] = next;
            if (mode != TapRegions141.CUSTOM) {
                mode = TapRegions141.CUSTOM;
                selectMode();
                rebuild();
            } else {
                values[index][edge].setText(edgeText(edge, next));
                preview.invalidate();
            }
        }

        private String edgeText(int edge, int value) {
            return (edge == 0 ? tr("開始", "Start", "起點") : tr("終了", "End", "終點")) + " " + value + "%";
        }

        void rebuild() {
            rows.removeAllViews();
            boolean custom = mode == TapRegions141.CUSTOM;
            add.setEnabled(ranges.size() < TapRegions141.LIMIT);
            preview.invalidate();
            if (!custom) return;
            for (int i = 0; i < ranges.size(); i++) {
                final int index = i;
                LinearLayout header = new LinearLayout(context);
                header.setGravity(Gravity.CENTER_VERTICAL);
                TextView label = new TextView(context);
                label.setText(tr("領域", "Area", "區域") + " " + (i + 1));
                header.addView(label, new LinearLayout.LayoutParams(0, -2, 1));
                Button remove = button(tr("削除", "Delete", "刪除"), () -> {
                    if (ranges.size() > 1) {
                        ranges.remove(index);
                        rebuild();
                    }
                });
                remove.setEnabled(ranges.size() > 1);
                header.addView(remove);
                rows.addView(header);
                for (int j = 0; j < 2; j++) {
                    final int edge = j;
                    LinearLayout line = new LinearLayout(context);
                    line.setGravity(Gravity.CENTER_VERTICAL);
                    TextView value = new TextView(context);
                    value.setText(edgeText(edge, ranges.get(index)[edge]));
                    values[index][edge] = value;
                    line.addView(value, new LinearLayout.LayoutParams(0, -2, 1));
                    Button minus = button("−", () -> adjust(index, edge, ranges.get(index)[edge] - 1));
                    Button plus = button("+", () -> adjust(index, edge, ranges.get(index)[edge] + 1));
                    minus.setContentDescription(label.getText() + " " + edgeText(edge, 0).replace(" 0%", "") + " −1%");
                    plus.setContentDescription(label.getText() + " " + edgeText(edge, 0).replace(" 0%", "") + " +1%");
                    line.addView(minus, new LinearLayout.LayoutParams(dp(48), dp(44)));
                    line.addView(plus, new LinearLayout.LayoutParams(dp(48), dp(44)));
                    rows.addView(line);
                }
            }
        }
    }

    /** The main strip shows the union. Separate handle lanes allow overlapping
     * ranges to remain individually editable. */
    private static final class Preview extends View {
        final Editor editor;
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int dragIndex = -1, dragEdge;

        Preview(Editor editor) {
            super(editor.context);
            this.editor = editor;
            setContentDescription(editor.tr("領域プレビュー", "Area preview", "區域預覽"));
        }

        private float pad() { return editor.dp(12); }
        private float span() { return Math.max(1, getWidth() - 2 * pad()); }
        private float x(int percent) { return pad() + span() * percent / 100f; }
        private float lane(int index) { return editor.dp(36 + index * 21); }

        @Override protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float top = editor.dp(4), bottom = editor.dp(21);
            paint.setColor((editor.inactive & 0x00ffffff) | 0x33000000);
            canvas.drawRect(x(0), top, x(100), bottom, paint);
            paint.setColor(editor.accent);
            for (int[] range : editor.ranges) canvas.drawRect(x(range[0]), top, x(range[1]), bottom, paint);
            for (int i = 0; i < editor.ranges.size(); i++) {
                int[] range = editor.ranges.get(i);
                float y = lane(i);
                canvas.drawRect(x(range[0]), y - editor.dp(1), x(range[1]), y + editor.dp(1), paint);
                canvas.drawCircle(x(range[0]), y, editor.dp(6), paint);
                canvas.drawCircle(x(range[1]), y, editor.dp(6), paint);
            }
            paint.setColor(editor.inactive);
            paint.setTextSize(editor.dp(12));
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("0%", x(0), editor.dp(96), paint);
            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText("100%", x(100), editor.dp(96), paint);
        }

        @Override public boolean onTouchEvent(MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    int index = Math.round((event.getY() - lane(0)) / editor.dp(21));
                    if (index < 0 || index >= editor.ranges.size()
                            || Math.abs(event.getY() - lane(index)) > editor.dp(12)) return false;
                    int[] range = editor.ranges.get(index);
                    float left = Math.abs(event.getX() - x(range[0]));
                    float right = Math.abs(event.getX() - x(range[1]));
                    if (Math.min(left, right) > editor.dp(24)) return false;
                    dragIndex = index;
                    dragEdge = left <= right ? 0 : 1;
                    getParent().requestDisallowInterceptTouchEvent(true);
                    return true;
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_UP:
                    if (dragIndex < 0) return false;
                    editor.adjust(dragIndex, dragEdge,
                            Math.round((event.getX() - pad()) * 100 / span()));
                    if (event.getActionMasked() == MotionEvent.ACTION_UP) endDrag();
                    return true;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_POINTER_DOWN:
                    endDrag();
                    return true;
                default:
                    return dragIndex >= 0;
            }
        }

        private void endDrag() {
            dragIndex = -1;
            getParent().requestDisallowInterceptTouchEvent(false);
        }
    }
}

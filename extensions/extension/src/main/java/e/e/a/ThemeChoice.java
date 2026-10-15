package e.e.a;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.view.View;

/** Explicit light/dark selection while retaining the existing Material You path. */
public final class ThemeChoice {
    private static final String KEY = "app_theme";
    private static final String[] VALUES = {"light", "dark", "material", "amoled"};
    private static final String[] LABELS = {"ライトモード", "ダークモード", "Material You", "AMOLEDダークモード"};
    private static final java.util.WeakHashMap<Activity, String> APPLIED = new java.util.WeakHashMap<>();
    private static boolean watching;
    private static SharedPreferences prefs(Context c) { return PreferenceManager.getDefaultSharedPreferences(c); }
    private static String mode(Context c) {
        SharedPreferences p = prefs(c);
        return ThemeRules.mode(p.getString(KEY, null), p.getBoolean("material_you_mode", false));
    }
    private static boolean systemNight(Context c) {
        return (c.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }
    public static boolean isNight(Context c) { return ThemeRules.night(mode(c), systemNight(c)); }
    public static boolean isAmoled(Context c) { return "amoled".equals(prefs(c).getString("app_theme", "")); }
    public static void apply(Activity a) {
        String selected = mode(a);
        SharedPreferences p = prefs(a);
        boolean material = "material".equals(selected);
        if (!p.contains(KEY) || p.getBoolean("material_you_mode", false) != material)
            p.edit().putString(KEY, selected).putBoolean("material_you_mode", material).commit();
        String style = ThemeRules.style(selected, systemNight(a), android.os.Build.VERSION.SDK_INT);
        int id = a.getResources().getIdentifier(style, "style", a.getPackageName());
        if (id != 0) a.setTheme(id);
        legacy("e.e.a.StartupScreen","register",Activity.class,a);
        if(isAmoled(a)){
            a.getWindow().setStatusBarColor(0xff000000);
            a.getWindow().setNavigationBarColor(0xff000000);
        }
        APPLIED.put(a, stamp(a));
        if (!watching) {
            watching = true;
            a.getApplication().registerActivityLifecycleCallbacks(new android.app.Application.ActivityLifecycleCallbacks() {
                public void onActivityCreated(Activity activity, android.os.Bundle state) { activity.getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(()->{ListDividers191.tree(activity.getWindow().getDecorView());PlayerTheme.detailRows(activity.getWindow().getDecorView());FlatUi191.tree(activity.getWindow().getDecorView());}); activity.getWindow().getDecorView().post(()->{controls(activity.getWindow().getDecorView());UiTheme191.activity(activity);ListDividers191.tree(activity.getWindow().getDecorView());}); }
                public void onActivityStarted(Activity activity) { }
                public void onActivityResumed(Activity activity) {
                    activity.getWindow().getDecorView().post(()->{controls(activity.getWindow().getDecorView());UiTheme191.activity(activity);ListDividers191.tree(activity.getWindow().getDecorView());});
                    String old = APPLIED.get(activity), current = stamp(activity);
                    if (old != null && !old.equals(current) && !activity.isFinishing()) {
                        APPLIED.put(activity, current);
                        activity.getWindow().getDecorView().post(activity::recreate);
                    }
                }
                public void onActivityPaused(Activity activity) { }
                public void onActivityStopped(Activity activity) { }
                public void onActivitySaveInstanceState(Activity activity, android.os.Bundle state) { }
                public void onActivityDestroyed(Activity activity) { APPLIED.remove(activity); }
            });
        }
    }
    private static void legacy(String cls,String method,Class<?> arg,Object value){try{Class.forName(cls).getMethod(method,arg).invoke(null,value);}catch(Exception error){android.util.Log.w("nicoid-theme","Theme compatibility hook failed",error);}}
    private static String stamp(Context c) { return mode(c) + ":" + isNight(c); }
    public static void settings(PreferenceActivity a) {
        Preference old = a.findPreference("material_you_mode");
        Preference choice=a.findPreference(KEY);
        PreferenceGroup parent=old==null?null:parent(a.getPreferenceScreen(),old);
        if(parent==null){Preference general=a.findPreference("setting_whole");parent=general instanceof PreferenceGroup?(PreferenceGroup)general:a.getPreferenceScreen();}
        if(choice==null){choice=new Preference(a);choice.setKey(KEY);choice.setOrder(old==null?0:old.getOrder());parent.addPreference(choice);}
        PreferenceGroup currentParent=parent(a.getPreferenceScreen(),choice);if(currentParent!=parent){if(currentParent!=null)currentParent.removePreference(choice);parent.addPreference(choice);}
        choice.setOrder(-1000);
        choice.setTitle(UiStrings.translate("テーマ"));
        int current = index(mode(a));
        choice.setSummary(UiStrings.translate(LABELS[current]));
        choice.setOnPreferenceClickListener(row -> {
            String[] labels = new String[LABELS.length];
            for (int i = 0; i < labels.length; i++) labels[i] = UiStrings.translate(LABELS[i]);
            AlertDialog dialog = UiDialogs.builder(PlaybackSession.dialogContext(a))
                .setTitle(UiStrings.translate("テーマ"))
                .setSingleChoiceItems(labels, index(mode(a)), (d, which) -> {
                    String selected = VALUES[which];
                    prefs(a).edit().putString(KEY, selected).putBoolean("material_you_mode", "material".equals(selected)).commit();
                    d.dismiss(); a.recreate();
                }).setNegativeButton(UiStrings.translate("キャンセル"), null).create();
            PlaybackSession.showDialog(dialog);
            return true;
        });
        if(old!=null){PreferenceGroup previous=parent(a.getPreferenceScreen(),old);if(previous!=null)previous.removePreference(old);}
    }
    private static int index(String value) {
        for (int i = 0; i < VALUES.length; i++) if (VALUES[i].equals(value)) return i;
        return 0;
    }
    private static PreferenceGroup parent(PreferenceGroup group, Preference target) {
        for (int i = 0; i < group.getPreferenceCount(); i++) {
            Preference p = group.getPreference(i);
            if (p == target) return group;
            if (p instanceof PreferenceGroup) {
                PreferenceGroup found = parent((PreferenceGroup)p, target);
                if (found != null) return found;
            }
        }
        return null;
    }
    private static int color(Context c, int attr, int fallback) {
        android.util.TypedValue v = new android.util.TypedValue();
        if (!c.getTheme().resolveAttribute(attr, v, true)) return fallback;
        return v.resourceId == 0 ? v.data : c.getResources().getColor(v.resourceId);
    }
    public static int accent(Context c) { return DialogColors.accent(color(UiDialogs.owner(c),0x7f03005e,isNight(c)?DialogColors.ACCENT_DARK:0xff006b5b),isNight(c)); }
    public static int textColor(View v) { return isNight(v.getContext())?DialogColors.INK_DARK:color(v.getContext(),android.R.attr.textColorPrimary,DialogColors.INK_LIGHT); }
    public static boolean amoled(Context c){return isAmoled(c);}
    public static void tree(View view){
        if(view instanceof android.widget.TextView){android.widget.TextView text=(android.widget.TextView)view;if(!"nicoid_duration".equals(view.getTag()))text.setTextColor(textColor(view));text.setHintTextColor(isNight(view.getContext())?0xffb7bac3:0xff666a73);}
        controlsOne(view);
        if(view instanceof android.view.ViewGroup){android.view.ViewGroup group=(android.view.ViewGroup)view;for(int n=0;n<group.getChildCount();n++)tree(group.getChildAt(n));}
    }
    public static void controls(View view){if(!isNight(view.getContext()))return;controlsOne(view);if(view instanceof android.widget.Spinner)spinner((android.widget.Spinner)view);if(view instanceof android.view.ViewGroup){android.view.ViewGroup g=(android.view.ViewGroup)view;for(int i=0;i<g.getChildCount();i++)controls(g.getChildAt(i));}}
    static boolean purpleStates(android.content.res.ColorStateList colors){if(colors==null)return false;if(DialogColors.purple(colors.getDefaultColor()))return true;for(int state:new int[]{android.R.attr.state_pressed,android.R.attr.state_checked,android.R.attr.state_selected,android.R.attr.state_focused})if(DialogColors.purple(colors.getColorForState(new int[]{android.R.attr.state_enabled,state},colors.getDefaultColor())))return true;return false;}
    static void controlsOne(View v){if(!isNight(v.getContext()))return;android.content.res.ColorStateList tint=android.content.res.ColorStateList.valueOf(accent(v.getContext()));if(v instanceof android.widget.TextView){android.widget.TextView t=(android.widget.TextView)v;if(purpleStates(t.getTextColors()))t.setTextColor(DialogColors.INK_DARK);if(purpleStates(t.getHintTextColors()))t.setHintTextColor(DialogColors.MUTED_DARK);if(purpleStates(t.getLinkTextColors()))t.setLinkTextColor(tint);}if(v instanceof android.widget.ImageView){android.widget.ImageView image=(android.widget.ImageView)v;if(purpleStates(image.getImageTintList()))image.setImageTintList(android.content.res.ColorStateList.valueOf(v.isSelected()||v.isActivated()?accent(v.getContext()):DialogColors.INK_DARK));}if(v instanceof android.widget.EditText){if(!"nicoid-managed-input".equals(v.getTag()))v.setBackgroundTintList(tint);if(android.os.Build.VERSION.SDK_INT>=29){android.widget.EditText t=(android.widget.EditText)v;android.graphics.drawable.Drawable cursor=t.getTextCursorDrawable();if(cursor!=null){cursor=cursor.mutate();cursor.setTintList(tint);t.setTextCursorDrawable(cursor);}for(android.graphics.drawable.Drawable d:new android.graphics.drawable.Drawable[]{t.getTextSelectHandle(),t.getTextSelectHandleLeft(),t.getTextSelectHandleRight()})if(d!=null)d.mutate().setTintList(tint);}}if(v instanceof android.widget.CompoundButton)((android.widget.CompoundButton)v).setButtonTintList(tint);if(v instanceof android.widget.Switch||v.getClass().getName().endsWith("SwitchCompat")){int checked=accent(v.getContext()),muted=DialogColors.MUTED_DARK;android.content.res.ColorStateList thumb=new android.content.res.ColorStateList(new int[][]{new int[]{android.R.attr.state_checked},new int[]{}},new int[]{checked,muted});android.content.res.ColorStateList track=new android.content.res.ColorStateList(new int[][]{new int[]{android.R.attr.state_checked},new int[]{}},new int[]{(checked&0xffffff)|0x66000000,0xff52565e});try{v.getClass().getMethod("setThumbTintList",android.content.res.ColorStateList.class).invoke(v,thumb);v.getClass().getMethod("setTrackTintList",android.content.res.ColorStateList.class).invoke(v,track);}catch(Exception ignored){}}if(v instanceof android.widget.CheckedTextView)((android.widget.CheckedTextView)v).setCheckMarkTintList(tint);if(v instanceof android.widget.ProgressBar){((android.widget.ProgressBar)v).setProgressTintList(tint);((android.widget.ProgressBar)v).setIndeterminateTintList(tint);}if(v instanceof android.widget.SeekBar)((android.widget.SeekBar)v).setThumbTintList(tint);if(v.getBackground() instanceof android.graphics.drawable.ColorDrawable&&DialogColors.purple(((android.graphics.drawable.ColorDrawable)v.getBackground()).getColor()))v.setBackgroundColor(UiDialogs.surface(v.getContext()));android.content.res.ColorStateList background=v.getBackgroundTintList();if(purpleStates(background))v.setBackgroundTintList(tint);}
    public static void spinner(android.widget.Spinner spinner){
        if(isNight(spinner.getContext()))spinner.setPopupBackgroundDrawable(new android.graphics.drawable.ColorDrawable(UiDialogs.surface(spinner.getContext())));
        android.widget.SpinnerAdapter original=spinner.getAdapter();
        if(original==null||original instanceof ThemedSpinner)return;
        int selected=spinner.getSelectedItemPosition();spinner.setAdapter(new ThemedSpinner(original,spinner.getContext()));spinner.setSelection(Math.max(0,selected));
    }
    private static final class ThemedSpinner extends android.widget.BaseAdapter {
        final android.widget.SpinnerAdapter original;final Context context;
        ThemedSpinner(android.widget.SpinnerAdapter original,Context context){this.original=original;this.context=context;original.registerDataSetObserver(new android.database.DataSetObserver(){public void onChanged(){notifyDataSetChanged();}public void onInvalidated(){notifyDataSetInvalidated();}});}
        public int getCount(){return original.getCount();}public Object getItem(int position){return original.getItem(position);}public long getItemId(int position){return original.getItemId(position);}public boolean hasStableIds(){return original.hasStableIds();}
        public View getView(int position,View convert,android.view.ViewGroup parent){View view=original.getView(position,convert,parent);tree(view);return view;}
        public View getDropDownView(int position,View convert,android.view.ViewGroup parent){View view=original.getDropDownView(position,convert,parent);background(view);tree(view);return view;}
    }
    public static void background(View v) { v.setBackgroundColor(isNight(v.getContext())?UiDialogs.surface(v.getContext()):color(v.getContext(),android.R.attr.colorBackground,0xffffffff)); }
    public static void button(android.widget.Button b) {
        Context c = b.getContext(); boolean dark = isNight(c);
        int surfaceAttr = c.getResources().getIdentifier("nicoidSurface", "attr", c.getPackageName());
        int surface = dark?DialogColors.secondary(UiDialogs.surface(c),true):color(c,surfaceAttr,0xffe7e7e7);
        int ink = textColor(b);
        if (!dark && "material".equals(mode(c)) && android.os.Build.VERSION.SDK_INT >= 31) {
            int id = c.getResources().getIdentifier(dark ? "system_accent1_700" : "system_accent1_100", "color", "android");
            if (id != 0) surface = c.getResources().getColor(id);
            id = c.getResources().getIdentifier(dark ? "system_neutral1_50" : "system_neutral1_900", "color", "android");
            if (id != 0) ink = c.getResources().getColor(id);
        }
        b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(surface)); b.setTextColor(ink);
        legacy("e.e.a.Followup3","button",android.widget.Button.class,b);FlatUi191.one(b);
    }
}

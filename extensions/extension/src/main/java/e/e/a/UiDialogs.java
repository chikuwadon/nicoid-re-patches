package e.e.a;

import android.app.AlertDialog;
import android.content.*;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.*;
import android.widget.*;

/** One palette for native dialogs, generated forms and action lists. */
public final class UiDialogs {
    public static AlertDialog.Builder builder(Context c){return new AlertDialog.Builder(UiTheme191.context(c));}
    static Context owner(Context context) {
        for (int n=0; n<12 && context instanceof ContextWrapper && !(context instanceof android.app.Activity); n++) {
            Context base=((ContextWrapper)context).getBaseContext();
            if(base==null || base==context) break;
            context=base;
        }
        return context;
    }
    public static int accent(Context context) { return ThemeChoice.accent(owner(context)); }
    public static int surface(Context context) {
        Context c=owner(context); if(ThemeChoice.isAmoled(c))return 0xff000000;boolean dark=ThemeChoice.isNight(c);if(dark)return DialogColors.SURFACE_DARK;
        if(Build.VERSION.SDK_INT>=31 && android.preference.PreferenceManager.getDefaultSharedPreferences(c).getBoolean("material_you_mode",false)) {
            int id=c.getResources().getIdentifier(dark?"system_neutral1_900":"system_neutral1_50","color","android");
            if(id!=0) return c.getResources().getColor(id,c.getTheme());
        }
        return dark?0xff191b20:0xfffafafa;
    }
    /** Insets for custom dialog content; preserve any larger intentional spacing. */
    public static void insetContent(View content) {
        int horizontal=PanelUi.dp(content.getContext(),20),vertical=PanelUi.dp(content.getContext(),12);
        content.setPadding(Math.max(horizontal,content.getPaddingLeft()),Math.max(vertical,content.getPaddingTop()),Math.max(horizontal,content.getPaddingRight()),Math.max(vertical,content.getPaddingBottom()));
    }
    public static void secondaryButton(Button button) {
        Context c=button.getContext();boolean dark=ThemeChoice.isNight(owner(c));
        int color=DialogColors.secondary(surface(c),dark);
        GradientDrawable background=new GradientDrawable();background.setColor(color);background.setCornerRadius(2*c.getResources().getDisplayMetrics().density);
        button.setTag("nicoid-range-control");
        button.setBackgroundTintList(null);button.setBackground(new android.graphics.drawable.RippleDrawable(ColorStateList.valueOf((accent(c)&0x00ffffff)|0x33000000),new android.graphics.drawable.InsetDrawable(background,Math.round(3*c.getResources().getDisplayMetrics().density),Math.round(4*c.getResources().getDisplayMetrics().density),Math.round(3*c.getResources().getDisplayMetrics().density),Math.round(4*c.getResources().getDisplayMetrics().density)),null));
        button.setElevation(0);button.setAllCaps(false);button.setTextColor(dark?0xffeeeeee:0xff202124);button.setTextSize(14);
        int pad=Math.round(12*c.getResources().getDisplayMetrics().density),vertical=Math.round(8*c.getResources().getDisplayMetrics().density);
        button.setPadding(pad,vertical,pad,vertical);button.setMinWidth(0);button.setMinimumWidth(0);button.setMinHeight(Math.round(48*c.getResources().getDisplayMetrics().density));button.setMinimumHeight(Math.round(48*c.getResources().getDisplayMetrics().density));button.setGravity(Gravity.CENTER);button.setIncludeFontPadding(true);button.setSingleLine(false);
    }
    public static void style(AlertDialog dialog) {
        if(dialog.getWindow()==null) return;
        Context c=owner(dialog.getContext()); int accent=accent(c);
        GradientDrawable background=new GradientDrawable();background.setColor(surface(c));background.setCornerRadius(24*c.getResources().getDisplayMetrics().density);
        dialog.getWindow().setBackgroundDrawable(background);
        for(int which:new int[]{-1,-2,-3}) if(dialog.getButton(which)!=null) dialog.getButton(which).setTextColor(accent);
        boolean dark=ThemeChoice.isNight(owner(c));
        UiTheme191.surface(dialog.getWindow().getDecorView());
        inputs(dialog.getWindow().getDecorView(),accent,dark);ListDividers191.tree(dialog.getWindow().getDecorView());
        for(int which:new int[]{-1,-2,-3})if(dialog.getButton(which)!=null)actionButton(dialog.getButton(which),which==-1);DialogActions194.watch(dialog);FlatUi191.window(dialog);


    }
    /** 44 dp visible pill inside a 48 dp accessible hit target. */
    public static void actionButton(Button b,boolean primary){
        Context c=b.getContext();int pad=PanelUi.dp(c,16);b.setTag("nicoid-dialog-action");b.setAllCaps(false);b.setTextSize(14);b.setSingleLine(true);b.setGravity(Gravity.CENTER);b.setIncludeFontPadding(false);b.setMinWidth(PanelUi.dp(c,80));b.setMinimumWidth(PanelUi.dp(c,80));b.setMinHeight(PanelUi.dp(c,48));b.setMinimumHeight(PanelUi.dp(c,48));b.setPadding(pad,0,pad,0);b.setElevation(0);b.setBackgroundTintList(null);
        android.graphics.drawable.Drawable bg=primary?new android.graphics.drawable.InsetDrawable(PanelUi.round(c,accent(c),22),0,PanelUi.dp(c,2),0,PanelUi.dp(c,2)):new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT);
        b.setBackground(new android.graphics.drawable.RippleDrawable(ColorStateList.valueOf((accent(c)&0xffffff)|0x33000000),bg,null));
        b.setTextColor(new ColorStateList(new int[][]{new int[]{-android.R.attr.state_enabled},new int[]{}},new int[]{0xff75787f,primary?DialogColors.onAccent(accent(c)):accent(c)}));
        ViewGroup.LayoutParams lp=b.getLayoutParams();if(lp!=null){lp.height=PanelUi.dp(c,48);lp.width=primary?Math.max(PanelUi.dp(c,80),(int)b.getPaint().measureText(b.getText().toString())+pad*2):ViewGroup.LayoutParams.WRAP_CONTENT;if(lp instanceof LinearLayout.LayoutParams){LinearLayout.LayoutParams l=(LinearLayout.LayoutParams)lp;l.weight=0;l.gravity=Gravity.CENTER_VERTICAL;l.leftMargin=PanelUi.dp(c,4);}b.setLayoutParams(lp);}
        if(b.getParent() instanceof LinearLayout)((LinearLayout)b.getParent()).setGravity(Gravity.END|Gravity.CENTER_VERTICAL);
    }
    private static void blackPanels(View view){if(view instanceof ViewGroup){view.setBackgroundColor(0xff000000);ViewGroup group=(ViewGroup)view;for(int n=0;n<group.getChildCount();n++)blackPanels(group.getChildAt(n));}}
    private static void inputs(View v,int accent,boolean dark) {
        if(v instanceof AbsListView) {
            AbsListView list=(AbsListView)v;list.setBackgroundColor(surface(owner(v.getContext())));
            AbsListView.OnScrollListener listener=new AbsListView.OnScrollListener(){public void onScrollStateChanged(AbsListView view,int state){}public void onScroll(AbsListView view,int first,int count,int total){for(int n=0;n<view.getChildCount();n++)inputs(view.getChildAt(n),accent,dark);}};
            list.setOnScrollListener(listener);list.post(()->listener.onScroll(list,0,list.getChildCount(),list.getCount()));
        }
        if(v instanceof TextView&&!"nicoid_duration".equals(v.getTag())) ((TextView)v).setTextColor(v instanceof Button&&!(v instanceof CompoundButton)&&!"nicoid-range-control".equals(v.getTag())?accent:dark?0xffeeeeee:0xff202124);
        String name="";try{if(v.getId()!=View.NO_ID)name=v.getResources().getResourceEntryName(v.getId());}catch(Exception ignored){}
        // Stock NG/mylist forms have an unpadded spinner and input inside mainlay.
        if(name.equals("mainlay") && v instanceof LinearLayout) {
            LinearLayout form=(LinearLayout)v;
            if(form.getChildCount()==2 && form.getChildAt(0) instanceof Spinner && form.getChildAt(1) instanceof EditText) {
                insetContent(form);DialogInputs.pad(form);
            }
        }
        if(name.equals("parentPanel")||name.equals("topPanel")||name.equals("contentPanel")||name.equals("buttonPanel")||name.equals("customPanel"))v.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        if(v instanceof Spinner)ThemeChoice.spinner((Spinner)v);
        ThemeChoice.controlsOne(v);
        if(v instanceof EditText) {
            EditText e=(EditText)v;e.setTextColor(dark?0xffeeeeee:0xff202124);e.setHintTextColor(dark?0xffb7bac3:0xff666a73);if(!"nicoid-managed-input".equals(v.getTag()))UiControls194.apply(e);
            if(Build.VERSION.SDK_INT>=29) {
                android.graphics.drawable.Drawable cursor=e.getTextCursorDrawable();if(cursor!=null){cursor=cursor.mutate();cursor.setTint(accent);e.setTextCursorDrawable(cursor);}
            }
        }
        if(v instanceof ViewGroup) {ViewGroup g=(ViewGroup)v;for(int n=0;n<g.getChildCount();n++) inputs(g.getChildAt(n),accent,dark);}
    }
    public static void show(AlertDialog dialog) { dialog.show();PlaybackSession.styleDialog(dialog);style(dialog); }
    public static AlertDialog showBuilder(AlertDialog.Builder builder) {AlertDialog d=builder.create();show(d);return d;}
}

package e.e.a;

import android.content.*;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.view.*;
import android.widget.*;
import java.util.*;

/** Small visual/lifecycle corrections with no layout work in the render loop. */
public final class Revision196 {
    static final String INFO_POSITION="popup_info_position";
    static boolean infoLeft(Context c){return !"right".equals(PreferenceManager.getDefaultSharedPreferences(c).getString(INFO_POSITION,"left"));}
    public static void popupInfoSettings(PreferenceActivity activity,PreferenceGroup group){
        if(activity.findPreference(INFO_POSITION)!=null)return;
        Preference entry=new Preference(activity);entry.setKey(INFO_POSITION);
        entry.setTitle(PanelUi.tr(activity,"ポップアップの動画情報ボタンの位置","Video information button position in popup","浮動播放的影片資訊按鈕位置"));
        String[] labels={PanelUi.tr(activity,"左側","Left","左側"),PanelUi.tr(activity,"右側","Right","右側")};
        entry.setSummary(labels[infoLeft(activity)?0:1]);
        entry.setOnPreferenceClickListener(p->{UiDialogs.show(UiDialogs.builder(PlaybackSession.dialogContext(activity)).setTitle(entry.getTitle()).setSingleChoiceItems(labels,infoLeft(activity)?0:1,(dialog,which)->{PreferenceManager.getDefaultSharedPreferences(activity).edit().putString(INFO_POSITION,which==0?"left":"right").apply();entry.setSummary(labels[which]);dialog.dismiss();}).setNegativeButton(UiStrings.translate("キャンセル"),null).create());return true;});
        group.addPreference(entry);
    }
    public static int followTint(Context c,boolean followed,boolean shortStyle){
        if(shortStyle)return Color.WHITE;
        return followed?ThemeChoice.accent(c):(ThemeChoice.isNight(c)?Color.WHITE:PanelUi.ink(c));
    }
    public static void popupInfo(LinearLayout row,View info){
        if(info==null||!(row.getParent() instanceof RelativeLayout))return;
        ViewGroup menu=(ViewGroup)row.getParent();
        if(!infoLeft(row.getContext())){if(info.getParent() instanceof ViewGroup)((ViewGroup)info.getParent()).removeView(info);row.addView(info);return;}
        if(info.getParent()!=menu){if(info.getParent() instanceof ViewGroup)((ViewGroup)info.getParent()).removeView(info);RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(PanelUi.dp(info.getContext(),40),PanelUi.dp(info.getContext(),40));lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);menu.addView(info,lp);}
    }
    public static void popupInfoSize(LinearLayout row,int slot){
        if(!(row.getParent() instanceof View))return;View info=find((View)row.getParent(),"infobutton");if(info==null)return;
        ViewGroup.LayoutParams lp=info.getLayoutParams();if(lp.width!=slot||lp.height!=slot){lp.width=slot;lp.height=slot;info.setLayoutParams(lp);}
        int padding=infoLeft(row.getContext())?slot:0;if(row.getPaddingLeft()!=padding)row.setPadding(padding,0,0,0);
    }
    public static int popupCount(LinearLayout row){return row.getChildCount()+(infoLeft(row.getContext())?1:0);}
    static View find(View root,String id){return root.findViewById(root.getResources().getIdentifier(id,"id",root.getContext().getPackageName()));}
    public static void summary(Preference p,boolean player){
        p.setSummary(PanelUi.tr(p.getContext(),player?"プレイヤー上部のボタンの表示・非表示と並び順を変更します。":"動画情報欄のボタンの表示・非表示を設定します。",player?"Change the visibility and order of the buttons at the top of the player.":"Show or hide buttons in the video information area.",player?"設定播放器上方按鈕的顯示狀態與順序。":"設定影片資訊區按鈕的顯示狀態。"));
    }
    public static void back(TextView view){
        view.setText("");view.setContentDescription(UiStrings.translate("戻る"));view.setPadding(0,0,0,0);view.setBackground(new Back(PanelUi.ink(view.getContext())));
    }
    static final class Back extends Drawable {
        final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);Back(int ink){p.setColor(ink);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(2);p.setStrokeCap(Paint.Cap.ROUND);p.setStrokeJoin(Paint.Join.ROUND);}
        public void draw(Canvas c){Rect b=getBounds();float side=Math.min(b.width(),b.height())*.5f;int save=c.save();c.translate(b.exactCenterX()-side/2,b.exactCenterY()-side/2);c.scale(side/24,side/24);Path path=new Path();path.moveTo(14,5);path.lineTo(7,12);path.lineTo(14,19);c.drawPath(path,p);c.restoreToCount(save);}
        public void setAlpha(int a){p.setAlpha(a);invalidateSelf();}public void setColorFilter(ColorFilter f){p.setColorFilter(f);invalidateSelf();}public int getOpacity(){return PixelFormat.TRANSLUCENT;}
    }
    public static void centered(TextView view){view.setGravity(Gravity.CENTER);view.setPadding(0,view.getPaddingTop(),0,view.getPaddingBottom());view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);}
    public static float glyph(float size,String name){
        if(name.equals("play")||name.equals("pause"))return size;
        // Corner glyphs cover a larger square than the round symbols at the same size.
        float cap=(name.startsWith("fullscreen")||name.equals("exitfullscreen"))?21:24;
        return Math.min(size,cap*android.content.res.Resources.getSystem().getDisplayMetrics().density);
    }
    public static void popupCenter(View root){
        View menu=find(root,"topmenulay"),center=find(root,"viewctrllay");if(menu==null||center==null)return;
        inherit(menu,center);
    }
    public static void inherit(View menu,View center){
        if(menu==null||center==null)return;
        // The center was moved outside menu: explicitly inherit its native auto-hide state.
        if(center.getVisibility()!=menu.getVisibility())center.setVisibility(menu.getVisibility());
        if(center.getAlpha()!=menu.getAlpha())center.setAlpha(menu.getAlpha());
        android.view.animation.Animation animation=menu.getAnimation();
        if(animation!=null){android.view.animation.Transformation transform=new android.view.animation.Transformation();animation.getTransformation(android.view.animation.AnimationUtils.currentAnimationTimeMillis(),transform);float a=menu.getAlpha()*transform.getAlpha();if(center.getAlpha()!=a)center.setAlpha(a);}
    }
    public static void queue(Object fragment){
        try{
            Object activity=Review181.field(fragment,"o0"),player=null;try{player=Review181.field(activity,"v");}catch(Exception ignored){}
            Object source=null;int position=((Number)Review181.field(fragment,"w0")).intValue();
            if(player!=null){try{source=Review181.field(player,"C1");position=((Number)Review181.field(player,"D1")).intValue();}catch(Exception ignored){}}
            if(!(source instanceof List))try{source=Class.forName("e.e.a.v0").getMethod("c").invoke(null);}catch(Exception ignored){}
            java.lang.reflect.Field field=fragment.getClass().getField("a0");ArrayList<Object> rows=(ArrayList<Object>)field.get(fragment);
            if(rows==null){rows=new ArrayList<>();field.set(fragment,rows);}
            ArrayList<?> snapshot=source instanceof List?new ArrayList<>((List<?>)source):new ArrayList<>(rows);
            rows.clear();rows.addAll(snapshot);position=rows.isEmpty()?0:Math.max(0,Math.min(position,rows.size()-1));
            for(int n=0;n<rows.size();n++)rows.get(n).getClass().getField("i").set(rows.get(n),n==position);
            fragment.getClass().getField("w0").setInt(fragment,position);
            // An empty restored queue must remain loadable when the player reconnects.
            fragment.getClass().getField("l0").setBoolean(fragment,!rows.isEmpty());
            if(Review181.field(fragment,"Z")!=null)fragment.getClass().getMethod("c").invoke(fragment);
        }catch(Exception e){android.util.Log.w("nicoid-player","Unable to restore queue view",e);}
    }
}

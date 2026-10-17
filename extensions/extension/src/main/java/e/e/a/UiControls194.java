package e.e.a;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.*;
import android.os.Build;
import android.view.View;
import android.widget.*;
/** Common control colors preserve disabled, checked and selected states. */
public final class UiControls194 {
 private UiControls194(){}
 private static int alpha(int color,int a){return (color&0xffffff)|(a<<24);}
 private static ColorStateList states(int active,int normal,int disabled){return new ColorStateList(new int[][]{new int[]{-android.R.attr.state_enabled},new int[]{android.R.attr.state_checked},new int[]{android.R.attr.state_selected},new int[]{android.R.attr.state_activated},new int[]{}},new int[]{disabled,active,active,active,normal});}
 public static void apply(View v){Context c=v.getContext();boolean dark=ThemeChoice.isNight(c);int accent=ThemeChoice.accent(c),ink=DialogColors.ink(dark),muted=DialogColors.muted(dark);ColorStateList active=states(accent,accent,alpha(muted,97));
  if(v instanceof TextView){TextView t=(TextView)v;if(ThemeChoice.purpleStates(t.getTextColors()))t.setTextColor(states(ink,ink,alpha(muted,97)));if(ThemeChoice.purpleStates(t.getHintTextColors()))t.setHintTextColor(muted);if(ThemeChoice.purpleStates(t.getLinkTextColors()))t.setLinkTextColor(active);}
  if(v instanceof ImageView){ImageView i=(ImageView)v;if(ThemeChoice.purpleStates(i.getImageTintList()))i.setImageTintList(states(accent,ink,alpha(muted,97)));}
  if(v instanceof EditText){EditText e=(EditText)v;if(!"nicoid-managed-input".equals(v.getTag()))v.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{-android.R.attr.state_enabled},new int[]{android.R.attr.state_focused},new int[]{}},new int[]{alpha(muted,97),accent,muted}));if(Build.VERSION.SDK_INT>=29){Drawable cursor=e.getTextCursorDrawable();if(cursor!=null){cursor=cursor.mutate();cursor.setTintList(active);e.setTextCursorDrawable(cursor);}for(Drawable d:new Drawable[]{e.getTextSelectHandle(),e.getTextSelectHandleLeft(),e.getTextSelectHandleRight()})if(d!=null)d.mutate().setTintList(active);}}
  if(v instanceof CompoundButton)((CompoundButton)v).setButtonTintList(states(accent,muted,alpha(muted,97)));
  if(v instanceof Switch||v.getClass().getName().endsWith("SwitchCompat"))try{v.getClass().getMethod("setThumbTintList",ColorStateList.class).invoke(v,states(accent,muted,alpha(muted,97)));v.getClass().getMethod("setTrackTintList",ColorStateList.class).invoke(v,states(alpha(accent,102),alpha(muted,77),alpha(muted,38)));}catch(ReflectiveOperationException ignored){}
  if(v instanceof CheckedTextView)((CheckedTextView)v).setCheckMarkTintList(states(accent,muted,alpha(muted,97)));
  if(v instanceof ProgressBar){ProgressBar p=(ProgressBar)v;p.setProgressTintList(active);p.setIndeterminateTintList(active);if(ThemeChoice.purpleStates(p.getProgressBackgroundTintList()))p.setProgressBackgroundTintList(ColorStateList.valueOf(alpha(muted,77)));}if(v instanceof SeekBar)((SeekBar)v).setThumbTintList(active);
  Drawable bg=v.getBackground();if(bg instanceof ColorDrawable&&DialogColors.purple(((ColorDrawable)bg).getColor())&&((ColorDrawable)bg).getColor()!=accent)v.setBackgroundColor(UiDialogs.surface(c));
  if(ThemeChoice.purpleStates(v.getBackgroundTintList())){if(v instanceof Button&&!(v instanceof CompoundButton)){int fill=DialogColors.secondary(UiDialogs.surface(c),dark);v.setBackgroundTintList(states(fill,fill,alpha(fill,97)));}else v.setBackgroundTintList(active);}
 }
}

package e.e.a;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.Build;
import android.view.*;
import android.widget.*;

/** Decorative depth is removed without changing sibling Z order or media pixels. */
public final class FlatUi191 {
 private FlatUi191(){}
 static boolean media(View v){String n=v.getClass().getName();return v instanceof SurfaceView||v instanceof TextureView||v instanceof android.webkit.WebView||n.startsWith("com.devbrackets.")||n.contains("PlayerView")||"nicoid-media-overlay".equals(v.getTag());}
 static final class FlatOutline extends ViewOutlineProvider {final ViewOutlineProvider source;FlatOutline(ViewOutlineProvider source){this.source=source;}public void getOutline(View v,Outline out){source.getOutline(v,out);out.setAlpha(0);}}
 static void noShadow(View v){
  if(v.getStateListAnimator()!=null&&(v instanceof Button||v.getClass().getName().contains("CardView")))v.setStateListAnimator(null);
  // Linear-layout controls have no overlapping siblings. Other views retain Z for ordering.
  if(v.getParent() instanceof LinearLayout&&!v.hasTransientState()){if(v.getElevation()!=0)v.setElevation(0);if(v.getTranslationZ()!=0)v.setTranslationZ(0);}
  ViewOutlineProvider outline=v.getOutlineProvider();if(outline!=null&&!(outline instanceof FlatOutline))v.setOutlineProvider(new FlatOutline(outline));
  if(Build.VERSION.SDK_INT>=28){if(v.getOutlineAmbientShadowColor()!=Color.TRANSPARENT)v.setOutlineAmbientShadowColor(Color.TRANSPARENT);if(v.getOutlineSpotShadowColor()!=Color.TRANSPARENT)v.setOutlineSpotShadowColor(Color.TRANSPARENT);}
 }
 static Drawable flatten(Drawable d){if(d==null)return null;if(d.getClass().getName().endsWith("MaterialShapeDrawable"))try{d.getClass().getMethod("setShadowCompatibilityMode",int.class).invoke(d,1);}catch(ReflectiveOperationException ignored){}if(Build.VERSION.SDK_INT>=24&&d instanceof GradientDrawable){GradientDrawable shape=(GradientDrawable)d;int[] colors=shape.getColors();if(colors!=null&&colors.length>1)((GradientDrawable)d.mutate()).setColor(colors[0]);}if(d instanceof LayerDrawable){LayerDrawable layers=(LayerDrawable)d;for(int n=0;n<layers.getNumberOfLayers();n++)flatten(layers.getDrawable(n));}else if(d instanceof InsetDrawable)flatten(((InsetDrawable)d).getDrawable());return d;}
 public static void one(View v){if(v==null||media(v))return;noShadow(v);if(v instanceof TextView&&!"nicoid_duration".equals(v.getTag())){TextView text=(TextView)v;if(text.getShadowRadius()!=0)text.setShadowLayer(0,0,0,0);}if(!(v instanceof ImageView))flatten(v.getBackground());if(v instanceof Button&&!(v instanceof CompoundButton))legacyButton((Button)v);}
 public static void tree(View root){if(root==null||media(root))return;one(root);if(root instanceof ViewGroup){ViewGroup group=(ViewGroup)root;for(int n=0;n<group.getChildCount();n++)tree(group.getChildAt(n));}}
 static void legacyButton(Button b){Drawable bg=b.getBackground();Drawable current=bg==null?null:bg.getCurrent();if(!(current instanceof NinePatchDrawable))return;int color=ThemeChoice.isNight(b.getContext())?DialogColors.secondary(UiDialogs.surface(b.getContext()),true):0xffe7e7e7;ColorStateList tint=b.getBackgroundTintList();if(tint!=null)color=tint.getDefaultColor();GradientDrawable shape=PanelUi.round(b.getContext(),color,2);replaceBackground(b,new RippleDrawable(ColorStateList.valueOf((UiDialogs.accent(b.getContext())&0xffffff)|0x33000000),shape,null));}
 static void replaceBackground(View view,Drawable bg){int l=view.getPaddingLeft(),t=view.getPaddingTop(),r=view.getPaddingRight(),b=view.getPaddingBottom(),w=view.getMinimumWidth(),h=view.getMinimumHeight();view.setBackgroundTintList(null);view.setBackground(bg);view.setPadding(l,t,r,b);view.setMinimumWidth(w);view.setMinimumHeight(h);}
 public static void window(Dialog dialog){if(dialog.getWindow()!=null){dialog.getWindow().setElevation(0);tree(dialog.getWindow().getDecorView());}}
 static void tagDepth(View v){Context c=v.getContext();int style=c.getResources().getIdentifier("NicoidFlatTag","style",c.getPackageName());android.content.res.TypedArray a=c.obtainStyledAttributes(style,new int[]{android.R.attr.elevation,android.R.attr.translationZ});try{v.setElevation(a.getDimension(0,0));v.setTranslationZ(a.getDimension(1,0));}finally{a.recycle();}v.setStateListAnimator(null);noShadow(v);}
}

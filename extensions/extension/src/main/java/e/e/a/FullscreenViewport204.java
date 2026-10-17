package e.e.a;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Rect;
import android.view.*;
import android.widget.*;
import java.util.*;

/** Keep the picture, comment renderer and timeline in the same fullscreen coordinate space. */
public final class FullscreenViewport204 {
 private static final Map<View,Session> sessions=new WeakHashMap<View,Session>();
 private static View find(View root,String name){return root.findViewById(root.getResources().getIdentifier(name,"id",root.getContext().getPackageName()));}
 private static Activity owner(Context c){for(int i=0;i<16;i++){if(c instanceof Activity)return (Activity)c;if(!(c instanceof ContextWrapper))break;Context next=((ContextWrapper)c).getBaseContext();if(next==c)break;c=next;}return null;}
 public static void update(View root,ViewGroup decor,View bar){
  if(root==null)return;
  Session s=sessions.get(root);if(s==null){s=new Session();sessions.put(root,s);s.activity=owner(root.getContext());s.decor=decor;s.flags=decor.getSystemUiVisibility();if(s.activity!=null)s.windowFlags=s.activity.getWindow().getAttributes().flags;}
  if(s.activity!=null){Window w=s.activity.getWindow();if((w.getAttributes().flags&WindowManager.LayoutParams.FLAG_FULLSCREEN)==0)w.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);int flags=s.flags|0x1706;if(decor.getSystemUiVisibility()!=flags)decor.setSystemUiVisibility(flags);
   if(android.os.Build.VERSION.SDK_INT>=28)try{WindowManager.LayoutParams p=w.getAttributes();java.lang.reflect.Field f=p.getClass().getField("layoutInDisplayCutoutMode");if(s.cutout<0)s.cutout=f.getInt(p);int mode=android.os.Build.VERSION.SDK_INT>=30?3:1;if(f.getInt(p)!=mode){f.setInt(p,mode);w.setAttributes(p);}}catch(Exception ignored){}
  }
  View frame=find(root,"videoLayout"),controller=find(root,"controller"),comments=find(root,"commentlay"),video=find(root,"video");
  // Previous portrait/docked dimensions must not survive entry into fullscreen.
  s.fit(root,-1,-1,0,0,false);if(frame!=root)s.fit(frame,-1,-1,0,0,false);
  s.fit(controller,-1,controller==null?-2:controller.getLayoutParams().height,0,0,true);
  if(controller!=null)s.timeline(controller);
  String mode=android.preference.PreferenceManager.getDefaultSharedPreferences(root.getContext()).getString("player_controls_visibility_mode","off");
  boolean held="normal".equals(mode)||"both".equals(mode);
  int upper=held&&bar!=null?bar.getLayoutParams().height:0;
  int lower=held&&controller!=null?controller.getHeight():0;
  s.fit(video,-1,-1,Math.max(0,upper),Math.max(0,lower),false);
  s.fit(comments,-1,-1,Math.max(0,upper),Math.max(0,lower),false);
  s.commentBounds(comments);s.fit(find(root,"video_view"),-1,-1,0,0,false);
 }
 public static void restore(View root){Session s=sessions.remove(root);if(s==null)return;for(Map.Entry<View,Saved> e:s.saved.entrySet())e.getValue().restore(e.getKey());if(s.decor!=null)s.decor.setSystemUiVisibility(s.flags);if(s.activity!=null){Window w=s.activity.getWindow();w.setFlags(s.windowFlags,WindowManager.LayoutParams.FLAG_FULLSCREEN);if(s.cutout>=0)try{WindowManager.LayoutParams p=w.getAttributes();p.getClass().getField("layoutInDisplayCutoutMode").setInt(p,s.cutout);w.setAttributes(p);}catch(Exception ignored){}}}
 private static final class Saved {
  final ViewGroup.LayoutParams params;final int[] padding;final Rect clip;
  Saved(View v){ViewGroup.LayoutParams p=v.getLayoutParams();params=copy(p);padding=new int[]{v.getPaddingLeft(),v.getPaddingTop(),v.getPaddingRight(),v.getPaddingBottom()};clip=v.getClipBounds();}
  void restore(View v){v.setLayoutParams(params);v.setPadding(padding[0],padding[1],padding[2],padding[3]);v.setClipBounds(clip);}
 }
 private static ViewGroup.LayoutParams copy(ViewGroup.LayoutParams p){if(p instanceof RelativeLayout.LayoutParams)return new RelativeLayout.LayoutParams((RelativeLayout.LayoutParams)p);if(p instanceof LinearLayout.LayoutParams)return new LinearLayout.LayoutParams((LinearLayout.LayoutParams)p);if(p instanceof FrameLayout.LayoutParams)return new FrameLayout.LayoutParams((FrameLayout.LayoutParams)p);if(p instanceof ViewGroup.MarginLayoutParams)return new ViewGroup.MarginLayoutParams((ViewGroup.MarginLayoutParams)p);return new ViewGroup.LayoutParams(p);}
 private static final class Session {
  final Map<View,Saved> saved=new LinkedHashMap<View,Saved>();final Map<View,Long> sizes=new WeakHashMap<View,Long>();Activity activity;View decor;int flags,windowFlags,cutout=-1;
  void save(View v){if(v!=null&&!saved.containsKey(v))saved.put(v,new Saved(v));}
  void fit(View v,int width,int height,int top,int bottom,boolean bottomAligned){
   if(v==null||v.getLayoutParams()==null)return;save(v);ViewGroup.LayoutParams old=v.getLayoutParams(),p=copy(old);p.width=width;p.height=height;
   boolean changed=old.width!=width||old.height!=height;
   if(p instanceof ViewGroup.MarginLayoutParams){ViewGroup.MarginLayoutParams m=(ViewGroup.MarginLayoutParams)p;changed|=m.leftMargin!=0||m.rightMargin!=0||m.topMargin!=top||m.bottomMargin!=bottom;m.setMargins(0,top,0,bottom);if(android.os.Build.VERSION.SDK_INT>=17){changed|=m.getMarginStart()!=0||m.getMarginEnd()!=0;m.setMarginStart(0);m.setMarginEnd(0);}}
   if(p instanceof RelativeLayout.LayoutParams){RelativeLayout.LayoutParams r=(RelativeLayout.LayoutParams)p;int[] before=r.getRules().clone();for(int rule:new int[]{RelativeLayout.BELOW,RelativeLayout.ABOVE,RelativeLayout.RIGHT_OF,RelativeLayout.LEFT_OF,RelativeLayout.ALIGN_TOP,RelativeLayout.ALIGN_BOTTOM,RelativeLayout.ALIGN_LEFT,RelativeLayout.ALIGN_RIGHT,RelativeLayout.CENTER_IN_PARENT,RelativeLayout.CENTER_VERTICAL,RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.ALIGN_PARENT_TOP,RelativeLayout.ALIGN_PARENT_BOTTOM})r.removeRule(rule);r.addRule(bottomAligned?RelativeLayout.ALIGN_PARENT_BOTTOM:RelativeLayout.ALIGN_PARENT_TOP);r.addRule(RelativeLayout.ALIGN_PARENT_LEFT);r.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);changed|=!Arrays.equals(before,r.getRules());}
   if(changed)v.setLayoutParams(p);
   if(v.getPaddingLeft()!=0||v.getPaddingTop()!=0||v.getPaddingRight()!=0||v.getPaddingBottom()!=0)v.setPadding(0,0,0,0);
  }
  void timeline(View v){if(v instanceof SeekBar){View child=v;ViewParent parent=child.getParent();while(parent instanceof ViewGroup){View group=(View)parent;if(group instanceof LinearLayout&&((LinearLayout)group).getOrientation()==LinearLayout.HORIZONTAL){save(child);ViewGroup.LayoutParams p=copy(child.getLayoutParams());if(p instanceof LinearLayout.LayoutParams){LinearLayout.LayoutParams l=(LinearLayout.LayoutParams)p;if(l.width!=0||l.weight!=1){l.width=0;l.weight=1;child.setLayoutParams(l);}}break;}if(group.getId()==v.getResources().getIdentifier("controller","id",v.getContext().getPackageName()))break;fit(group,-1,group.getLayoutParams().height,0,0,false);child=group;parent=group.getParent();}return;}if(v instanceof ViewGroup){ViewGroup g=(ViewGroup)v;for(int i=0;i<g.getChildCount();i++)timeline(g.getChildAt(i));}}
  void commentBounds(View v){if(v==null)return;String n=v.getClass().getName();if("e.e.a.t".equals(n)||"e.e.a.u".equals(n)){save(v);if(v.getClipBounds()!=null)v.setClipBounds(null);fit(v,-1,-1,0,0,false);int width=v.getWidth(),height=v.getHeight();long size=((long)width<<32)|(height&0xffffffffL);Long previous=sizes.get(v);if(width>0&&height>0&&(previous==null||previous.longValue()!=size)){try{v.getClass().getMethod("a",float.class,float.class).invoke(v,(float)width,(float)height);sizes.put(v,size);}catch(Exception e){android.util.Log.w("nicoid-layout","Cannot update comment viewport",e);}}}if(v instanceof ViewGroup){ViewGroup g=(ViewGroup)v;for(int i=0;i<g.getChildCount();i++)commentBounds(g.getChildAt(i));}}
 }
}

package e.e.a;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Rect;
import android.view.*;
import android.widget.*;
import java.util.*;

/** Keep the picture, comment renderer and timeline in the same fullscreen coordinate space. */
public final class FullscreenViewport206 {
 private static final Map<View,Session> sessions=new WeakHashMap<View,Session>();
 private static final Map<View,Session> sideSessions=new WeakHashMap<View,Session>();
 private static View find(View root,String name){return root.findViewById(root.getResources().getIdentifier(name,"id",root.getContext().getPackageName()));}
 private static Activity owner(Context c){for(int i=0;i<16;i++){if(c instanceof Activity)return (Activity)c;if(!(c instanceof ContextWrapper))break;Context next=((ContextWrapper)c).getBaseContext();if(next==c)break;c=next;}return null;}
 public static void update(View root,ViewGroup decor,View bar){
  if(root==null)return;
  Session s=sessions.get(root);if(s==null){s=new Session();sessions.put(root,s);s.activity=owner(root.getContext());s.decor=decor;s.flags=decor.getSystemUiVisibility();if(s.activity!=null)s.windowFlags=s.activity.getWindow().getAttributes().flags;}
  if(s.activity!=null){Window w=s.activity.getWindow();if((w.getAttributes().flags&WindowManager.LayoutParams.FLAG_FULLSCREEN)==0)w.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);int flags=s.flags|0x1706;if(decor.getSystemUiVisibility()!=flags)decor.setSystemUiVisibility(flags);
   if(android.os.Build.VERSION.SDK_INT>=28)try{WindowManager.LayoutParams p=w.getAttributes();java.lang.reflect.Field f=p.getClass().getField("layoutInDisplayCutoutMode");if(s.cutout<0)s.cutout=f.getInt(p);int mode=android.os.Build.VERSION.SDK_INT>=30?3:1;if(f.getInt(p)!=mode){f.setInt(p,mode);w.setAttributes(p);}}catch(Exception ignored){}
  }
  View frame=find(root,"videoLayout"),comments=find(root,"commentlay"),video=find(root,"video");
  if(s.controller==null)s.controller=find(root,"controller");
  if(s.bottom==null)s.bottom=find(root,"bottommenulay");
  View controller=s.controller;
  ViewGroup layer=bar!=null&&bar.getParent() instanceof FrameLayout?(ViewGroup)bar.getParent():decor;
  s.capture(controller);s.capture(s.bottom);s.bottomIcons();
  s.overlay(controller,layer,0);
  s.overlay(s.bottom,layer,controller==null?0:controller.getHeight());
  // Previous portrait/docked dimensions must not survive entry into fullscreen.
  s.fit(root,-1,-1,0,0,false);if(frame!=root)s.fit(frame,-1,-1,0,0,false);

  if(controller!=null)s.timeline(controller);
  String mode=android.preference.PreferenceManager.getDefaultSharedPreferences(root.getContext()).getString("player_controls_visibility_mode","off");
  boolean held="normal".equals(mode)||"both".equals(mode);
  int upper=held&&bar!=null?bar.getLayoutParams().height:0;
  int lower=held&&controller!=null?controller.getHeight():0;
  s.fit(video,-1,-1,Math.max(0,upper),Math.max(0,lower),false);
  s.fit(comments,-1,-1,Math.max(0,upper),Math.max(0,lower),false);
  s.commentBounds(comments);s.fit(find(root,"video_view"),-1,-1,0,0,false);
 }
 public static void restore(View root){restoreSession(sessions.remove(root));restoreSession(sideSessions.remove(root));}
 private static void restoreSession(Session s){if(s==null)return;if(s.attachRoot!=null)s.attachRoot.removeOnAttachStateChangeListener(s.detach);s.removeListeners();s.restoreParents();for(Map.Entry<View,Saved> e:s.saved.entrySet())e.getValue().restore(e.getKey());if(s.decor!=null)s.decor.setSystemUiVisibility(s.flags);if(s.activity!=null){Window w=s.activity.getWindow();w.setFlags(s.windowFlags,WindowManager.LayoutParams.FLAG_FULLSCREEN);if(s.cutout>=0)try{WindowManager.LayoutParams p=w.getAttributes();p.getClass().getField("layoutInDisplayCutoutMode").setInt(p,s.cutout);w.setAttributes(p);}catch(Exception ignored){}}}
 /** Keep the native information pane's reserved width; controls stay inside the picture column. */
 public static boolean side(View root){
  if(root==null)return false;View frame=find(root,"videoLayout"),info=find(root,"info");
  if(frame==null||info==null||info.getVisibility()!=View.VISIBLE||!(info.getLayoutParams() instanceof RelativeLayout.LayoutParams))return false;
  RelativeLayout.LayoutParams ip=(RelativeLayout.LayoutParams)info.getLayoutParams();
  if(ip.getRules()[RelativeLayout.RIGHT_OF]!=frame.getId()&&ip.getRules()[RelativeLayout.END_OF]!=frame.getId())return false;
  Session state=sideSessions.get(root);if(state==null){state=new Session();state.bottom=find(root,"bottommenulay");state.attachRoot=root;final View target=root;state.detach=new View.OnAttachStateChangeListener(){public void onViewAttachedToWindow(View v){}public void onViewDetachedFromWindow(View v){restore(target);}};root.addOnAttachStateChangeListener(state.detach);sideSessions.put(root,state);}
  if(state.bottom!=null){state.save(state.bottom);state.bottomIcons();}
  for(String name:new String[]{"titlebar","controller","bottommenulay"}){
   View v=find(root,name);if(v==null||v.getLayoutParams()==null)continue;state.save(v);ViewGroup.LayoutParams p=copy(v.getLayoutParams());boolean changed=p.width!=-1;p.width=-1;
   if(p instanceof ViewGroup.MarginLayoutParams){ViewGroup.MarginLayoutParams m=(ViewGroup.MarginLayoutParams)p;changed|=m.leftMargin!=0||m.rightMargin!=0||m.getMarginStart()!=0||m.getMarginEnd()!=0;m.leftMargin=0;m.rightMargin=0;m.setMarginStart(0);m.setMarginEnd(0);}
   if(changed)v.setLayoutParams(p);
  }
  return true;
 }
 public static void top(View root,View bar,View host,View tools,View title){
  // One continuous band, while text alone has an internal reading inset.
  bar.setBackgroundColor(0x77000000);if(host!=null)host.setBackgroundColor(0);
  if(title!=null){int inset=Math.round(12*root.getResources().getDisplayMetrics().density);if(title.getPaddingLeft()!=inset||title.getPaddingTop()!=0||title.getPaddingRight()!=inset||title.getPaddingBottom()!=0)title.setPadding(inset,0,inset,0);}
 }
 private static android.graphics.drawable.Drawable flatIcon(View button,android.graphics.drawable.Drawable previous){
  try{
   Class<?> type=Class.forName("e.e.a.PlayerIcons$Icon");java.lang.reflect.Constructor<?> ctor=type.getDeclaredConstructor(String.class,boolean.class);ctor.setAccessible(true);
   boolean info=button.getId()==button.getResources().getIdentifier("infobutton","id",button.getContext().getPackageName());
   android.graphics.drawable.Drawable symbol=(android.graphics.drawable.Drawable)ctor.newInstance(info?"info":"commentpost",false);
   android.content.res.ColorStateList color;
   {android.util.TypedValue value=new android.util.TypedValue();if(!button.getContext().getTheme().resolveAttribute(0x7f03005e,value,true))button.getContext().getTheme().resolveAttribute(android.R.attr.colorAccent,value,true);int accent=value.resourceId==0?value.data:button.getResources().getColor(value.resourceId);color=android.content.res.ColorStateList.valueOf((accent&0xffffff)|0x55000000);}
   return new android.graphics.drawable.RippleDrawable(color,symbol,new android.graphics.drawable.ColorDrawable(0xffffffff));
  }catch(Exception e){android.util.Log.w("nicoid-layout","Cannot flatten player action icon",e);return previous;}
 }
 /** Paint one rectangular strip only behind visible right-aligned action buttons. */
 public static final class Band extends android.graphics.drawable.Drawable{
  private final ViewGroup row;private final android.graphics.Paint paint=new android.graphics.Paint();
  public Band(ViewGroup row){this.row=row;paint.setColor(0x77000000);}
  @Override public void draw(android.graphics.Canvas c){Rect b=getBounds();int width=0;for(int i=0;i<row.getChildCount();i++){View child=row.getChildAt(i);if(child.getVisibility()!=View.GONE)width+=Math.max(0,child.getLayoutParams().width);}c.drawRect(Math.max(b.left,b.right-width),b.top,b.right,b.bottom,paint);}
  @Override public void setAlpha(int alpha){paint.setAlpha(alpha);invalidateSelf();}
  @Override public void setColorFilter(android.graphics.ColorFilter filter){paint.setColorFilter(filter);invalidateSelf();}
  @Override public int getOpacity(){return android.graphics.PixelFormat.TRANSLUCENT;}
 }
 /** Same drawing transform as the upper fullscreen toolbar, independent of touch slot width. */
 public static final class Glyph extends android.graphics.drawable.Drawable implements android.graphics.drawable.Drawable.Callback {
  private final android.graphics.drawable.Drawable original;
  public Glyph(android.graphics.drawable.Drawable d){original=d;d.setCallback(this);}
  @Override public void draw(android.graphics.Canvas canvas){Rect bounds=getBounds();original.setBounds(bounds);int save=canvas.save();canvas.scale(1.344f,1.344f,bounds.exactCenterX(),bounds.exactCenterY());original.draw(canvas);canvas.restoreToCount(save);}
  @Override public void setAlpha(int alpha){original.setAlpha(alpha);}
  @Override public void setColorFilter(android.graphics.ColorFilter filter){original.setColorFilter(filter);}
  @Override public int getOpacity(){return original.getOpacity();}
  @Override public boolean isStateful(){return original.isStateful();}
  @Override protected boolean onStateChange(int[] state){return original.setState(state);}
  @Override public void invalidateDrawable(android.graphics.drawable.Drawable who){invalidateSelf();}
  @Override public void scheduleDrawable(android.graphics.drawable.Drawable who,Runnable what,long when){scheduleSelf(what,when);}
  @Override public void unscheduleDrawable(android.graphics.drawable.Drawable who,Runnable what){unscheduleSelf(what);}
 }
 private static final class Moved{
  final View view;final ViewGroup parent;final int index;
  Moved(View v){view=v;parent=(ViewGroup)v.getParent();index=parent.indexOfChild(v);}
 }
 private static final class Saved {
  final ViewGroup.LayoutParams params;final int[] padding;final Rect clip;final android.graphics.drawable.Drawable background;final int minWidth,minHeight,textMinWidth,textMinHeight;
  Saved(View v){ViewGroup.LayoutParams p=v.getLayoutParams();params=copy(p);padding=new int[]{v.getPaddingLeft(),v.getPaddingTop(),v.getPaddingRight(),v.getPaddingBottom()};clip=v.getClipBounds();background=v.getBackground();minWidth=v.getMinimumWidth();minHeight=v.getMinimumHeight();textMinWidth=v instanceof TextView?((TextView)v).getMinWidth():0;textMinHeight=v instanceof TextView?((TextView)v).getMinHeight():0;}
  void restore(View v){v.setLayoutParams(params);v.setPadding(padding[0],padding[1],padding[2],padding[3]);v.setClipBounds(clip);v.setBackground(background);v.setMinimumWidth(minWidth);v.setMinimumHeight(minHeight);if(v instanceof TextView){((TextView)v).setMinWidth(textMinWidth);((TextView)v).setMinHeight(textMinHeight);}}
 }
 private static ViewGroup.LayoutParams copy(ViewGroup.LayoutParams p){if(p instanceof RelativeLayout.LayoutParams)return new RelativeLayout.LayoutParams((RelativeLayout.LayoutParams)p);if(p instanceof LinearLayout.LayoutParams)return new LinearLayout.LayoutParams((LinearLayout.LayoutParams)p);if(p instanceof FrameLayout.LayoutParams)return new FrameLayout.LayoutParams((FrameLayout.LayoutParams)p);if(p instanceof ViewGroup.MarginLayoutParams)return new ViewGroup.MarginLayoutParams((ViewGroup.MarginLayoutParams)p);return new ViewGroup.LayoutParams(p);}
 private static final class Session {
  final Map<View,Saved> saved=new LinkedHashMap<View,Saved>();final Map<View,Long> sizes=new WeakHashMap<View,Long>();final Map<View,View.OnLayoutChangeListener> listeners=new HashMap<View,View.OnLayoutChangeListener>();final List<Moved> moved=new ArrayList<Moved>();Activity activity;View decor,controller,bottom,attachRoot;View.OnAttachStateChangeListener detach;int flags,windowFlags,cutout=-1;
  void save(View v){if(v!=null&&!saved.containsKey(v))saved.put(v,new Saved(v));}
  void fit(View v,int width,int height,int top,int bottom,boolean bottomAligned){
   if(v==null||v.getLayoutParams()==null)return;save(v);ViewGroup.LayoutParams old=v.getLayoutParams(),p=copy(old);p.width=width;p.height=height;
   boolean changed=old.width!=width||old.height!=height;
   if(p instanceof ViewGroup.MarginLayoutParams){ViewGroup.MarginLayoutParams m=(ViewGroup.MarginLayoutParams)p;changed|=m.leftMargin!=0||m.rightMargin!=0||m.topMargin!=top||m.bottomMargin!=bottom;m.setMargins(0,top,0,bottom);if(android.os.Build.VERSION.SDK_INT>=17){changed|=m.getMarginStart()!=0||m.getMarginEnd()!=0;m.setMarginStart(0);m.setMarginEnd(0);}}
   if(p instanceof RelativeLayout.LayoutParams){RelativeLayout.LayoutParams r=(RelativeLayout.LayoutParams)p;int[] before=r.getRules().clone();for(int rule:new int[]{RelativeLayout.BELOW,RelativeLayout.ABOVE,RelativeLayout.RIGHT_OF,RelativeLayout.LEFT_OF,RelativeLayout.ALIGN_TOP,RelativeLayout.ALIGN_BOTTOM,RelativeLayout.ALIGN_LEFT,RelativeLayout.ALIGN_RIGHT,RelativeLayout.CENTER_IN_PARENT,RelativeLayout.CENTER_VERTICAL,RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.ALIGN_PARENT_TOP,RelativeLayout.ALIGN_PARENT_BOTTOM})r.removeRule(rule);r.addRule(bottomAligned?RelativeLayout.ALIGN_PARENT_BOTTOM:RelativeLayout.ALIGN_PARENT_TOP);r.addRule(RelativeLayout.ALIGN_PARENT_LEFT);r.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);changed|=!Arrays.equals(before,r.getRules());}
   if(changed)v.setLayoutParams(p);
   if(v.getPaddingLeft()!=0||v.getPaddingTop()!=0||v.getPaddingRight()!=0||v.getPaddingBottom()!=0)v.setPadding(0,0,0,0);
  }
  void bottomIcons(){
   if(!(bottom instanceof ViewGroup))return;ViewGroup row=(ViewGroup)bottom;save(row);if(!(row.getBackground() instanceof Band))row.setBackground(new Band(row));
   int height=Math.round(40*bottom.getResources().getDisplayMetrics().density),width=Math.round(44*bottom.getResources().getDisplayMetrics().density);
   for(int i=0;i<row.getChildCount();i++){
    View button=row.getChildAt(i);save(button);ViewGroup.LayoutParams old=button.getLayoutParams(),p=copy(old);boolean changed=p.width!=width||p.height!=height;p.width=width;p.height=height;
    if(p instanceof LinearLayout.LayoutParams){LinearLayout.LayoutParams lp=(LinearLayout.LayoutParams)p;changed|=lp.weight!=0||lp.leftMargin!=0||lp.topMargin!=0||lp.rightMargin!=0||lp.bottomMargin!=0;lp.weight=0;lp.setMargins(0,0,0,0);}
    if(changed)button.setLayoutParams(p);
    if(button.getPaddingLeft()!=0||button.getPaddingTop()!=0||button.getPaddingRight()!=0||button.getPaddingBottom()!=0)button.setPadding(0,0,0,0);
    button.setMinimumWidth(0);button.setMinimumHeight(0);if(button instanceof TextView){((TextView)button).setMinWidth(0);((TextView)button).setMinHeight(0);}
    android.graphics.drawable.Drawable d=button.getBackground();if(d!=null&&!(d instanceof Glyph))button.setBackground(new Glyph(flatIcon(button,d)));
   }
  }
  void capture(View v){if(v==null)return;for(Moved m:moved)if(m.view==v)return;if(!(v.getParent() instanceof ViewGroup))return;save(v);moved.add(new Moved(v));}
  void overlay(View v,ViewGroup layer,int bottomMargin){
   if(v==null||layer==null)return;
   if(v.getParent()!=layer){((ViewGroup)v.getParent()).removeView(v);layer.addView(v,new FrameLayout.LayoutParams(-1,-2,Gravity.BOTTOM|Gravity.LEFT));}
   FrameLayout.LayoutParams p=(FrameLayout.LayoutParams)v.getLayoutParams();
   if(p.width!=-1||p.height!=-2||p.gravity!=(Gravity.BOTTOM|Gravity.LEFT)||p.leftMargin!=0||p.topMargin!=0||p.rightMargin!=0||p.bottomMargin!=bottomMargin){p.width=-1;p.height=-2;p.gravity=Gravity.BOTTOM|Gravity.LEFT;p.setMargins(0,0,0,bottomMargin);p.setMarginStart(0);p.setMarginEnd(0);v.setLayoutParams(p);}
   if(v.getPaddingLeft()!=0||v.getPaddingTop()!=0||v.getPaddingRight()!=0||v.getPaddingBottom()!=0)v.setPadding(0,0,0,0);
  }
  void restoreParents(){
   Collections.sort(moved,new Comparator<Moved>(){public int compare(Moved a,Moved b){return a.index-b.index;}});
   for(Moved m:moved){if(m.view.getParent() instanceof ViewGroup)((ViewGroup)m.view.getParent()).removeView(m.view);m.parent.addView(m.view,Math.min(m.index,m.parent.getChildCount()),copy(saved.get(m.view).params));}
  }
  void timeline(View v){if(v instanceof SeekBar){save(v);int inset=Math.round(12*v.getResources().getDisplayMetrics().density);if(v.getPaddingLeft()!=inset||v.getPaddingRight()!=inset)v.setPadding(inset,v.getPaddingTop(),inset,v.getPaddingBottom());View child=v;ViewParent parent=child.getParent();while(parent instanceof ViewGroup){View group=(View)parent;if(group instanceof LinearLayout&&((LinearLayout)group).getOrientation()==LinearLayout.HORIZONTAL){save(child);ViewGroup.LayoutParams p=copy(child.getLayoutParams());if(p instanceof LinearLayout.LayoutParams){LinearLayout.LayoutParams l=(LinearLayout.LayoutParams)p;if(l.width!=0||l.weight!=1){l.width=0;l.weight=1;child.setLayoutParams(l);}}break;}if(group.getId()==v.getResources().getIdentifier("controller","id",v.getContext().getPackageName()))break;fit(group,-1,group.getLayoutParams().height,0,0,false);child=group;parent=group.getParent();}return;}if(v instanceof ViewGroup){ViewGroup g=(ViewGroup)v;for(int i=0;i<g.getChildCount();i++)timeline(g.getChildAt(i));}}
  void sync(View v){int width=v.getWidth(),height=v.getHeight();long size=((long)width<<32)|(height&0xffffffffL);Long previous=sizes.get(v);if(width>0&&height>0&&(previous==null||previous.longValue()!=size)){try{v.getClass().getMethod("a",float.class,float.class).invoke(v,(float)width,(float)height);sizes.put(v,size);}catch(Exception e){android.util.Log.w("nicoid-layout","Cannot update comment viewport",e);}}}
  void removeListeners(){for(Map.Entry<View,View.OnLayoutChangeListener> e:listeners.entrySet())e.getKey().removeOnLayoutChangeListener(e.getValue());}
  void commentBounds(View v){
   if(v==null)return;String n=v.getClass().getName();
   if("e.e.a.t".equals(n)||"e.e.a.u".equals(n)){
    save(v);if(v.getClipBounds()!=null)v.setClipBounds(null);fit(v,-1,-1,0,0,false);
    if(!listeners.containsKey(v)){View.OnLayoutChangeListener listener=new View.OnLayoutChangeListener(){public void onLayoutChange(View view,int l,int t,int r,int b,int ol,int ot,int or,int ob){sync(view);}};listeners.put(v,listener);v.addOnLayoutChangeListener(listener);}
    sync(v);
   }
   if(v instanceof ViewGroup){ViewGroup g=(ViewGroup)v;for(int i=0;i<g.getChildCount();i++)commentBounds(g.getChildAt(i));}
  }
 }
}

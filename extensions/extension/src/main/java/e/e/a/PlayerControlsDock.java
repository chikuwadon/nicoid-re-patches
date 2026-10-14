package e.e.a;

import android.content.Context;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.view.*;
import android.widget.*;

/** Reserve a picture viewport while preserving native player actions and state updates. */
public final class PlayerControlsDock {
 public static final String KEY="player_controls_outside_video";
 public static boolean enabled(Context c){return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(KEY,false);}
 private static View find(View root,String name){return root.findViewById(root.getResources().getIdentifier(name,"id",root.getContext().getPackageName()));}
 public static void attach(View root){
  if(!enabled(root.getContext()))return;
  View video=find(root,"videoLayout");if(!(video instanceof RelativeLayout)||video.findViewWithTag(KEY)!=null)return;
  RelativeLayout frame=(RelativeLayout)video;View marker=new View(root.getContext());marker.setTag(KEY);frame.addView(marker,new RelativeLayout.LayoutParams(0,0));
  Dock state=new Dock(root,frame);frame.getViewTreeObserver().addOnPreDrawListener(state);
  frame.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener(){public void onViewAttachedToWindow(View v){if(v.getViewTreeObserver().isAlive()){v.getViewTreeObserver().removeOnPreDrawListener(state);v.getViewTreeObserver().addOnPreDrawListener(state);}}public void onViewDetachedFromWindow(View v){if(v.getViewTreeObserver().isAlive())v.getViewTreeObserver().removeOnPreDrawListener(state);}});
 }
 private static final class Dock implements ViewTreeObserver.OnPreDrawListener {
  final RelativeLayout frame;final int pictureHeight;final View bar,top,bottom,controller,video,comments,videoView,central,play,post,info;
  final View[] order;
  final int[] origin=new int[2],location=new int[2];final Rect bounds=new Rect(),previous=new Rect();
  Dock(View root,RelativeLayout f){frame=f;pictureHeight=f.getLayoutParams().height>0?f.getLayoutParams().height:PanelUi.dp(f.getContext(),180);bar=find(root,"titlebar");top=find(root,"topmenulay");bottom=find(root,"bottommenulay");controller=find(root,"controller");video=find(root,"video");comments=find(root,"commentlay");videoView=find(root,"video_view");central=find(root,"viewctrllay");play=find(root,"viewbutton");post=find(root,"commentpostbutton");info=find(root,"infobutton");order=new View[]{info,post,play,root.findViewWithTag("modern-speed"),root.findViewWithTag("modern-quality"),find(root,"popupbutton"),find(root,"repeatbutton"),find(root,"commentbutton"),find(root,"fullscbutton")};}
  boolean show(View v){if(v==null)return false;boolean change=v.getVisibility()!=View.VISIBLE||v.getAlpha()!=1||v.getAnimation()!=null;if(change){v.clearAnimation();v.animate().cancel();v.setVisibility(View.VISIBLE);v.setAlpha(1);}return change;}
  boolean dock(View v){if(v==null||v.getParent()==frame)return false;ViewGroup old=(ViewGroup)v.getParent();if(old==null)return false;ViewGroup.LayoutParams original=v.getLayoutParams();old.removeView(v);frame.addView(v,original instanceof RelativeLayout.LayoutParams?new RelativeLayout.LayoutParams((RelativeLayout.LayoutParams)original):new RelativeLayout.LayoutParams(original));v.setBackgroundColor(android.graphics.Color.BLACK);return true;}
  boolean upper(View v){if(v==null||!(top instanceof LinearLayout)||v.getParent()==top)return false;ViewGroup parent=(ViewGroup)v.getParent();if(parent==null)return false;parent.removeView(v);int size=PanelUi.dp(frame.getContext(),36);((LinearLayout)top).addView(v,new LinearLayout.LayoutParams(size,size));v.setPadding(0,0,0,0);v.setMinimumWidth(0);v.setMinimumHeight(0);show(v);return true;}
  void arrange(){if(!(top instanceof LinearLayout))return;LinearLayout row=(LinearLayout)top;int index=0;for(View v:order){if(v==null||v.getParent()!=row)continue;if(row.indexOfChild(v)!=index){ViewGroup.LayoutParams lp=v.getLayoutParams();row.removeView(v);row.addView(v,index,lp);}index++;}}
  boolean fit(View v){if(v==null)return false;ViewGroup.LayoutParams p=v.getLayoutParams();if(p.width==-1&&p.height==-1)return false;p.width=-1;p.height=-1;v.setLayoutParams(p);return true;}
  boolean inset(View v,int top,int bottom){if(v==null||!(v.getLayoutParams() instanceof RelativeLayout.LayoutParams))return false;RelativeLayout.LayoutParams p=(RelativeLayout.LayoutParams)v.getLayoutParams();if(p.topMargin==top&&p.bottomMargin==bottom&&p.height==-1)return false;p.topMargin=top;p.bottomMargin=bottom;p.height=-1;v.setLayoutParams(p);return true;}
  void clip(View v,int upper,int lower){if(v==null)return;String name=v.getClass().getName();if(name.equals("e.e.a.t")||name.equals("e.e.a.u")){v.getLocationInWindow(location);int left=Math.max(0,origin[0]-location[0]),right=Math.min(v.getWidth(),origin[0]+frame.getWidth()-location[0]);int y=Math.max(0,origin[1]+upper-location[1]),end=Math.min(v.getHeight(),origin[1]+frame.getHeight()-lower-location[1]);bounds.set(left,y,Math.max(left,right),Math.max(y,end));if(!v.getClipBounds(previous)||!bounds.equals(previous)){v.setClipBounds(bounds);try{v.getClass().getMethod("a",float.class,float.class).invoke(v,(float)bounds.width(),(float)bounds.height());}catch(Exception ignored){}}}if(v instanceof ViewGroup){ViewGroup group=(ViewGroup)v;group.setClipChildren(true);for(int n=0;n<group.getChildCount();n++)clip(group.getChildAt(n),upper,lower);}}
  public boolean onPreDraw(){
   if(bar==null||controller==null)return true;
   boolean changed=dock(bar)|dock(controller);if(top!=null&&top.getParent()!=bar)changed|=dock(top);
   changed|=upper(play)|upper(post)|upper(info);arrange();
   if(central!=null&&central.getVisibility()!=View.GONE){central.setVisibility(View.GONE);changed=true;}
   if(bottom!=null&&bottom.getVisibility()!=View.GONE){bottom.setVisibility(View.GONE);changed=true;}
   changed|=show(bar)|show(top)|show(controller)|show(play)|show(post)|show(info);
   View title=bar.findViewById(bar.getResources().getIdentifier("titlebartext","id",bar.getContext().getPackageName()));if(title!=null&&title.getVisibility()!=View.GONE){title.setVisibility(View.GONE);changed=true;}
   if(top instanceof LinearLayout){LinearLayout tools=(LinearLayout)top;int count=tools.getChildCount();int available=frame.getWidth()-bar.getPaddingLeft()-bar.getPaddingRight();if(count>0&&available>0){int slot=Math.min(PanelUi.dp(frame.getContext(),36),available/count);for(int n=0;n<count;n++){View child=tools.getChildAt(n);ViewGroup.LayoutParams cp=child.getLayoutParams();if(cp.width!=slot||cp.height!=slot){cp.width=slot;cp.height=slot;child.setLayoutParams(cp);changed=true;}if(child instanceof TextView){TextView label=(TextView)child;label.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX,slot*.31f);label.setSingleLine(true);label.setMinWidth(0);label.setMinimumWidth(0);}}ViewGroup.LayoutParams tp=tools.getLayoutParams();if(tp.width!=available){tp.width=available;tools.setLayoutParams(tp);changed=true;}}}
   int topEdge=bar.getBottom();if(top!=null&&top.getParent()!=bar)topEdge=Math.max(topEdge,top.getBottom());
   int lower=Math.max(0,frame.getHeight()-controller.getTop()),upper=Math.max(0,topEdge);
   changed|=inset(video,upper,lower)|inset(comments,upper,lower)|fit(videoView);
   frame.getLocationInWindow(origin);clip(comments,upper,lower);
   ViewGroup.LayoutParams p=frame.getLayoutParams();if(p.height>0){int wanted=pictureHeight+upper+lower;if(p.height!=wanted){p.height=wanted;frame.setLayoutParams(p);changed=true;}}
   // Never cancel a draw: completion callbacks may change visibility every frame.
   return true;
  }
 }
}

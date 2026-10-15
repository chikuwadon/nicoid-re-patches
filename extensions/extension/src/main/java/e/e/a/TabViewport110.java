package e.e.a;
import android.content.Context;
import android.view.*;
import android.widget.*;
/** A full content viewport owns blank-area gestures as well as gestures over its children. */
abstract class TabViewport110 extends FrameLayout {
 private float startX,startY;private boolean horizontal,vertical;private VelocityTracker velocity;
 private final int slop;
 TabViewport110(Context c){super(c);slop=ViewConfiguration.get(c).getScaledTouchSlop();setClipChildren(true);setLayoutParams(new ViewGroup.LayoutParams(-1,-1));}
 abstract boolean switching();abstract void swipe(int direction);
 private void begin(MotionEvent e){startX=e.getX();startY=e.getY();horizontal=false;vertical=startX<PanelUi.dp(getContext(),24)||switching();recycle();velocity=VelocityTracker.obtain();velocity.addMovement(e);}
 private void classify(MotionEvent e){if(e.getPointerCount()>1){vertical=true;horizontal=false;return;}if(vertical||horizontal)return;float dx=e.getX()-startX,dy=e.getY()-startY;if(Math.abs(dy)>slop&&Math.abs(dy)>=Math.abs(dx))vertical=true;else if(Following190.Gestures.isHorizontal(dx,dy,slop)){if(scrollable(this,startX,startY,dx<0?1:-1))vertical=true;else horizontal=true;}}
 private static boolean scrollable(View view,float x,float y,int direction){if(view instanceof ViewGroup){ViewGroup group=(ViewGroup)view;for(int n=group.getChildCount()-1;n>=0;n--){View child=group.getChildAt(n);if(child.getVisibility()!=VISIBLE)continue;float localX=x+group.getScrollX()-child.getLeft()-child.getTranslationX(),localY=y+group.getScrollY()-child.getTop()-child.getTranslationY();if(localX>=0&&localY>=0&&localX<child.getWidth()&&localY<child.getHeight()&&scrollable(child,localX,localY,direction))return true;}}return !(view instanceof TabViewport110)&&view.canScrollHorizontally(direction);}
 public boolean onInterceptTouchEvent(MotionEvent e){if(e.getActionMasked()==MotionEvent.ACTION_DOWN)begin(e);else if(e.getActionMasked()==MotionEvent.ACTION_MOVE){classify(e);if(horizontal){if(getParent()!=null)getParent().requestDisallowInterceptTouchEvent(true);return true;}}else if(e.getActionMasked()==MotionEvent.ACTION_CANCEL||e.getActionMasked()==MotionEvent.ACTION_UP)recycle();return horizontal;}
 public boolean onTouchEvent(MotionEvent e){int action=e.getActionMasked();if(action==MotionEvent.ACTION_DOWN)begin(e);if(velocity!=null)velocity.addMovement(e);if(action==MotionEvent.ACTION_MOVE)classify(e);if(action==MotionEvent.ACTION_UP||action==MotionEvent.ACTION_CANCEL){float speed=0;if(velocity!=null){velocity.computeCurrentVelocity(1000);speed=velocity.getXVelocity();}float dx=e.getX()-startX;boolean change=action==MotionEvent.ACTION_UP&&horizontal&&!vertical&&Following190.Gestures.shouldSwitch(dx,speed,getWidth(),PanelUi.dp(getContext(),600));horizontal=false;recycle();if(getParent()!=null)getParent().requestDisallowInterceptTouchEvent(false);if(change)swipe(dx<0?1:-1);}return true;}
 private void recycle(){if(velocity!=null){velocity.recycle();velocity=null;}}
 protected void onDetachedFromWindow(){horizontal=false;vertical=false;recycle();super.onDetachedFromWindow();}
 static void fill(View root){ViewGroup.LayoutParams p=root.getLayoutParams();if(p==null)p=new ViewGroup.LayoutParams(-1,-1);p.width=-1;if(p instanceof LinearLayout.LayoutParams&&((LinearLayout.LayoutParams)p).weight>0)p.height=0;else p.height=-1;root.setLayoutParams(p);if(root instanceof ScrollView)((ScrollView)root).setFillViewport(true);}
}

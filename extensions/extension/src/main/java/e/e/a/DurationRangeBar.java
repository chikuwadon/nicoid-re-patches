package e.e.a;
import android.content.Context;import android.graphics.*;import android.view.*;
final class DurationRangeBar extends View {
 int low,high,max;boolean unlimited;int handle;java.util.function.BiConsumer<Integer,Integer> changed;final Paint paint=new Paint(3);
 DurationRangeBar(Context c,int max){super(c);this.max=max;setContentDescription("動画時間の下限・上限。入力欄から秒単位でも指定できます");}
 float x(int value){return pad()+(getWidth()-2*pad())*value/max;}float pad(){return PanelUi.dp(getContext(),20);}
 protected void onDraw(Canvas c){float y=getHeight()/2f;paint.setStrokeWidth(PanelUi.dp(getContext(),4));paint.setColor(UiDialogs.accent(getContext()));paint.setAlpha(70);c.drawLine(pad(),y,getWidth()-pad(),y,paint);paint.setAlpha(255);c.drawLine(x(low),y,unlimited?getWidth()-pad():x(high),y,paint);c.drawCircle(x(low),y,PanelUi.dp(getContext(),10),paint);if(!unlimited)c.drawCircle(x(high),y,PanelUi.dp(getContext(),10),paint);}
 public boolean onTouchEvent(MotionEvent e){if(e.getActionMasked()==MotionEvent.ACTION_DOWN){if(!isEnabled())return false;handle=unlimited||Math.abs(e.getX()-x(low))<Math.abs(e.getX()-x(high))?0:1;getParent().requestDisallowInterceptTouchEvent(true);}if(e.getActionMasked()==MotionEvent.ACTION_DOWN||e.getActionMasked()==MotionEvent.ACTION_MOVE){int n=Math.max(0,Math.min(max,Math.round((e.getX()-pad())/Math.max(1,getWidth()-2*pad())*max/15f)*15));if(handle==0){low=unlimited?n:Math.min(n,high);if(unlimited)high=Math.max(high,low);}else high=Math.max(n,low);if(changed!=null)changed.accept(low,high);invalidate();return true;}if(e.getActionMasked()==MotionEvent.ACTION_UP||e.getActionMasked()==MotionEvent.ACTION_CANCEL){if(e.getActionMasked()==MotionEvent.ACTION_UP)performClick();getParent().requestDisallowInterceptTouchEvent(false);return true;}return true;}
 public boolean performClick(){super.performClick();return true;}
}

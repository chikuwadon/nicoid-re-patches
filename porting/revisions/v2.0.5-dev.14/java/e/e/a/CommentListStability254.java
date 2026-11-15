package e.e.a;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.util.Log;
import java.lang.ref.WeakReference;

/** Compact the existing sort header and hide one-frame comment-list resets. */
public final class CommentListStability254 {
 private CommentListStability254(){}
 public static void attach(ListView list,View controls){
  if(list==null||!(controls instanceof LinearLayout))return;
  LinearLayout row=(LinearLayout)controls;
  CheckBox toggle=null;
  Spinner sort=null;
  for(int i=0;i<row.getChildCount();i++){
   View child=row.getChildAt(i);
   if(child instanceof CheckBox)toggle=(CheckBox)child;
   else if(child instanceof Spinner)sort=(Spinner)child;
  }
  if(toggle==null||sort==null)return;
  // The original UI made both children 50% wide, leaving wasted space
  // between the auto-follow checkbox and the sort selection.
  toggle.setLayoutParams(new LinearLayout.LayoutParams(-2,-2,0f));
  sort.setLayoutParams(new LinearLayout.LayoutParams(0,-2,1f));
  int inset=Math.round(6*row.getResources().getDisplayMetrics().density);
  sort.setPadding(inset,sort.getPaddingTop(),sort.getPaddingRight(),sort.getPaddingBottom());
  new Anchor(list,toggle);
 }
 private static final class Anchor implements ViewTreeObserver.OnPreDrawListener,
      View.OnAttachStateChangeListener {
  final WeakReference<ListView> list;
  final WeakReference<CheckBox> toggle;
  int lastFirst,lastTop,repairs;
  Anchor(ListView list,CheckBox toggle){
   this.list=new WeakReference<>(list);this.toggle=new WeakReference<>(toggle);
   list.addOnAttachStateChangeListener(this);
   list.getViewTreeObserver().addOnPreDrawListener(this);
  }
  public boolean onPreDraw(){
   ListView l=list.get();CheckBox checked=toggle.get();
   if(l==null||checked==null)return true;
   if(!checked.isChecked()||l.getCount()<8){lastFirst=0;repairs=0;return true;}
   int first=l.getFirstVisiblePosition();
   if(first==0&&lastFirst>4&&lastFirst<l.getCount()-1&&repairs<3){
    repairs++;
    l.setSelectionFromTop(lastFirst,lastTop);
    if(repairs==1)Log.i("nicoid-comment","Suppressed transient comment-list top reset at "+lastFirst);
    return false;
   }
   if(first>0){
    lastFirst=first;
    View top=l.getChildAt(0);
    lastTop=top==null?0:top.getTop();
    repairs=0;
   }
   return true;
  }
  public void onViewAttachedToWindow(View v){
   ListView l=list.get();if(l!=null)l.getViewTreeObserver().addOnPreDrawListener(this);
  }
  public void onViewDetachedFromWindow(View v){
   ListView l=list.get();
   if(l!=null&&l.getViewTreeObserver().isAlive())
    l.getViewTreeObserver().removeOnPreDrawListener(this);
   if(l!=null)l.removeOnAttachStateChangeListener(this);
  }
 }
}

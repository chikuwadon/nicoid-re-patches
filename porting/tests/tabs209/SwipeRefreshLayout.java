package androidx.swiperefreshlayout.widget;
import android.content.Context;import android.util.AttributeSet;import android.widget.FrameLayout;
/** Contract stand-in only: native icon rendering/animation stays in the installed application. */
public class SwipeRefreshLayout extends FrameLayout {
 public interface h {void a();}public h listener;public boolean refreshing;public int[] colors;public int background,distance;
 public SwipeRefreshLayout(Context c,AttributeSet a){super(c,a);}
 public void setOnRefreshListener(h value){listener=value;}public void setRefreshing(boolean value){refreshing=value;}
 public void setColorSchemeColors(int... value){colors=value;}public void setProgressBackgroundColorSchemeColor(int value){background=value;}public void setDistanceToTriggerSync(int value){distance=value;}
 public void trigger(){refreshing=true;listener.a();}
}

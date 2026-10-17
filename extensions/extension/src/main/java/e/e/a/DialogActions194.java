package e.e.a;
import android.app.AlertDialog;
import android.view.*;
import android.widget.*;
import java.util.*;
/** Stack dialog actions only when translated labels do not fit horizontally. */
public final class DialogActions194 {
 private static final Set<View> WATCHED=Collections.newSetFromMap(new WeakHashMap<View,Boolean>());
 private static final View.OnLayoutChangeListener LAYOUT=new View.OnLayoutChangeListener(){public void onLayoutChange(View v,int l,int t,int r,int b,int ol,int ot,int or,int ob){if(v instanceof LinearLayout)fit((LinearLayout)v);}};
 private DialogActions194(){}
 public static void watch(AlertDialog d){Button b=d.getButton(-1);if(b==null)b=d.getButton(-2);if(b==null)b=d.getButton(-3);if(b==null||!(b.getParent() instanceof LinearLayout))return;LinearLayout row=(LinearLayout)b.getParent();if(WATCHED.add(row))row.addOnLayoutChangeListener(LAYOUT);fit(row);}
 private static void fit(LinearLayout row){if(row.getWidth()<=0)return;int needed=row.getPaddingLeft()+row.getPaddingRight(),count=0;for(int i=0;i<row.getChildCount();i++){View v=row.getChildAt(i);if(!(v instanceof Button)||v.getVisibility()==View.GONE)continue;Button b=(Button)v;count++;needed+=Math.max(b.getMinimumWidth(),(int)Math.ceil(b.getPaint().measureText(b.getText().toString()))+b.getPaddingLeft()+b.getPaddingRight());if(b.getLayoutParams() instanceof ViewGroup.MarginLayoutParams){ViewGroup.MarginLayoutParams p=(ViewGroup.MarginLayoutParams)b.getLayoutParams();needed+=p.leftMargin+p.rightMargin;}}int direction=count>1&&needed>row.getWidth()?LinearLayout.VERTICAL:LinearLayout.HORIZONTAL;if(row.getOrientation()!=direction){row.setOrientation(direction);row.setGravity(Gravity.END|Gravity.CENTER_VERTICAL);ViewGroup.LayoutParams p=row.getLayoutParams();if(p!=null&&p.height!=-2){p.height=-2;row.setLayoutParams(p);}}}
}

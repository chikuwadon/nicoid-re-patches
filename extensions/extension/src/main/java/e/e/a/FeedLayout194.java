package e.e.a;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
/** Preserve phone spacing and keep the same feed cards readable on tablets. */
public final class FeedLayout194 extends LinearLayout {
 public FeedLayout194(Context c){super(c);}
 @Override protected void onMeasure(int w,int h){float density=getResources().getDisplayMetrics().density;int width=View.MeasureSpec.getMode(w)==View.MeasureSpec.UNSPECIFIED?0:View.MeasureSpec.getSize(w);int inset=Math.round(16*density)+Math.max(0,(width-Math.round(720*density))/2);if(getPaddingLeft()!=inset||getPaddingRight()!=inset)setPadding(inset,getPaddingTop(),inset,getPaddingBottom());super.onMeasure(w,h);}
}

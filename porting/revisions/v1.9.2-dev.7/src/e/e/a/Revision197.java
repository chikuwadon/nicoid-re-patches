package e.e.a;

import android.view.*;
import android.widget.*;

/** Keep the preview controls on one row with a shared column grid. */
public final class Revision197 {
    public static LinearLayout previewRow(LinearLayout row){
        if(row.getChildCount()!=6||!(row.getChildAt(0) instanceof CheckBox)||!(row.getChildAt(3) instanceof Spinner)||!(row.getChildAt(5) instanceof Spinner))return row;
        View[] views=new View[6];for(int n=0;n<6;n++)views[n]=row.getChildAt(n);row.removeAllViews();row=new PreviewRow(row.getContext());
        row.setPadding(0,0,0,0);row.setWeightSum(3);row.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout[] groups=new LinearLayout[3];
        for(int n=0;n<3;n++){groups[n]=new LinearLayout(row.getContext());groups[n].setGravity(Gravity.CENTER_VERTICAL);row.addView(groups[n],new LinearLayout.LayoutParams(0,PanelUi.dp(row.getContext(),48),1));}
        CheckBox flow=(CheckBox)views[0];flow.setPadding(0,0,0,0);flow.setSingleLine(true);flow.setMinWidth(0);flow.setMinimumWidth(0);
        groups[0].addView(flow,new LinearLayout.LayoutParams(0,-1,1));
        TextView info=(TextView)views[1];info.setPadding(0,0,0,0);info.setGravity(Gravity.CENTER);info.setMinWidth(0);info.setMinimumWidth(0);
        groups[0].addView(info,new LinearLayout.LayoutParams(PanelUi.dp(row.getContext(),32),-1));
        for(int n=1;n<3;n++){
            TextView label=(TextView)views[n*2];label.setPadding(0,0,0,0);label.setGravity(Gravity.CENTER);label.setSingleLine(true);
            groups[n].addView(label,new LinearLayout.LayoutParams(PanelUi.dp(row.getContext(),32),-1));
            View spinner=views[n*2+1];spinner.setPadding(0,0,0,0);spinner.setMinimumWidth(0);
            groups[n].addView(spinner,new LinearLayout.LayoutParams(0,-1,1));
        }
        return row;
    }
    static final class PreviewRow extends LinearLayout {
        PreviewRow(android.content.Context context){super(context);setOrientation(HORIZONTAL);}
        protected void onMeasure(int widthSpec,int heightSpec){
            if(getChildCount()==3&&View.MeasureSpec.getMode(widthSpec)!=View.MeasureSpec.UNSPECIFIED){
                int available=View.MeasureSpec.getSize(widthSpec)-getPaddingLeft()-getPaddingRight();
                CheckBox flow=(CheckBox)((LinearLayout)getChildAt(0)).getChildAt(0);
                int minimum=(int)Math.ceil(flow.getPaint().measureText(flow.getText().toString()))+flow.getCompoundPaddingLeft()+flow.getCompoundPaddingRight()+PanelUi.dp(getContext(),32);
                int first=Math.min(available,Math.max(available/3,minimum)),other=Math.max(0,(available-first)/2);
                for(int n=0;n<3;n++){LinearLayout.LayoutParams p=(LinearLayout.LayoutParams)getChildAt(n).getLayoutParams();p.width=n==0?first:n==1?other:available-first-other;p.weight=0;}
            }
            super.onMeasure(widthSpec,heightSpec);
        }
    }
}

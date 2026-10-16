package e.e.a;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import android.content.Context;
import android.view.*;
import android.widget.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE,sdk=28)
public class Revision197Test {
 LinearLayout make(){
  Context c=RuntimeEnvironment.getApplication();LinearLayout row=new LinearLayout(c);
  CheckBox flow=new CheckBox(c);flow.setText("コメントを流す");flow.setTextSize(11);flow.setChecked(true);row.addView(flow);TextView info=new TextView(c);info.setText("ⓘ");row.addView(info);
  for(String label:new String[]{"密度","背景"}){TextView text=new TextView(c);text.setText(label);row.addView(text);Spinner s=new Spinner(c);s.setAdapter(new ArrayAdapter<String>(c,android.R.layout.simple_spinner_item,new String[]{"通常","密集"}));s.setSelection(1);row.addView(s);}
  return row;
 }
 void layout(LinearLayout row,int width){int exact=View.MeasureSpec.makeMeasureSpec(width,View.MeasureSpec.EXACTLY),height=View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);row.measure(exact,height);row.layout(0,0,width,row.getMeasuredHeight());row.measure(exact,height);row.layout(0,0,width,row.getMeasuredHeight());}
 @Test public void equalGroupsAndMatchingSelectorColumns(){LinearLayout row=make();row=Revision197.previewRow(row);layout(row,600);assertEquals(3,row.getChildCount());assertEquals(row.getChildAt(0).getWidth(),row.getChildAt(1).getWidth());assertEquals(row.getChildAt(1).getWidth(),row.getChildAt(2).getWidth());LinearLayout a=(LinearLayout)row.getChildAt(1),b=(LinearLayout)row.getChildAt(2);assertEquals(a.getChildAt(0).getWidth(),b.getChildAt(0).getWidth());assertEquals(a.getChildAt(1).getWidth(),b.getChildAt(1).getWidth());}
 @Test public void narrowWidthRetainsTextAndEqualSelectors(){LinearLayout row=make();row=Revision197.previewRow(row);layout(row,320);LinearLayout flow=(LinearLayout)row.getChildAt(0);CheckBox check=(CheckBox)flow.getChildAt(0);assertTrue(check.getWidth()>=check.getPaint().measureText(check.getText().toString())+check.getCompoundPaddingLeft()+check.getCompoundPaddingRight());assertTrue(Math.abs(row.getChildAt(1).getWidth()-row.getChildAt(2).getWidth())<=1);assertEquals(320,row.getChildAt(2).getRight());}
 @Test public void listenersAndSelectedValuesSurviveRegrouping(){LinearLayout row=make();CheckBox check=(CheckBox)row.getChildAt(0);TextView info=(TextView)row.getChildAt(1);Spinner density=(Spinner)row.getChildAt(3),background=(Spinner)row.getChildAt(5);int[] clicks={0};info.setOnClickListener(v->clicks[0]++);row=Revision197.previewRow(row);layout(row,400);assertTrue(check.isChecked());assertEquals(1,density.getSelectedItemPosition());assertEquals(1,background.getSelectedItemPosition());info.performClick();assertEquals(1,clicks[0]);row=Revision197.previewRow(row);assertEquals(3,row.getChildCount());}
 @Test public void orientationChangeRecomputesWidths(){LinearLayout row=make();row=Revision197.previewRow(row);layout(row,600);layout(row,320);layout(row,900);assertEquals(300,row.getChildAt(0).getWidth());assertEquals(300,row.getChildAt(1).getWidth());assertEquals(300,row.getChildAt(2).getWidth());}
}

package e.e.a;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.preference.PreferenceManager;
import android.view.*;
import android.widget.*;
import java.io.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

/** Real framework rendering of shared components; not a complete app-screen capture. */
@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(manifest=Config.NONE,sdk={28,34},qualifiers="mdpi")
public class UiMatrix194Test {
 static final String[] MODES={"light","dark","amoled"},LANGS={"0","1","2"};
 static final int[][] SIZES={{320,640},{640,360},{1280,800}};
 static String tr(String key){return UiStrings.translate(key);}
 static void layout(View view,int width,int height){view.measure(View.MeasureSpec.makeMeasureSpec(width,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(height,View.MeasureSpec.EXACTLY));view.layout(0,0,width,height);}
 @Test public void sharedDialogMatrix()throws Exception{
  File output=new File(System.getProperty("nicoid.test.output"),"ui-matrix-api-"+android.os.Build.VERSION.SDK_INT);output.mkdirs();
  try(PrintWriter index=new PrintWriter(new File(output,"results.csv"),"UTF-8")){
   index.println("theme,language,width_dp,height_dp,actions_fit,checked_state,disabled_state,image");
   for(String mode:MODES)for(String lang:LANGS)for(int[] size:SIZES){
    String language="0".equals(lang)?"ja":"1".equals(lang)?"en":"zh-rTW";RuntimeEnvironment.setQualifiers(language+"-sw"+Math.min(size[0],size[1])+"dp-w"+size[0]+"dp-h"+size[1]+"dp-"+(size[0]>size[1]?"land":"port")+"-mdpi");
    org.robolectric.android.controller.ActivityController<Activity> lifecycle=Robolectric.buildActivity(Activity.class).setup().visible();Activity a=lifecycle.get();
    PreferenceManager.getDefaultSharedPreferences(a).edit().clear().putString("app_theme",mode).putString("app_lang",lang).commit();UiStrings.selectLanguage(lang);
    Context c=UiTheme191.context(a);LinearLayout form=new LinearLayout(c);form.setOrientation(LinearLayout.VERTICAL);UiDialogs.insetContent(form);
    TextView summary=new TextView(c);summary.setText(tr("プレビューを見ながら文字と影、表示時間、最大行数を調整します"));summary.setTextSize(14);form.addView(summary,new LinearLayout.LayoutParams(-1,-2));
    EditText input=new EditText(c);input.setHint(tr("コメント"));input.setSingleLine(false);form.addView(input,new LinearLayout.LayoutParams(-1,-2));
    CheckBox checked=new CheckBox(c);checked.setText(tr("コメントを太字にする"));checked.setChecked(true);form.addView(checked,new LinearLayout.LayoutParams(-1,-2));
    CheckBox disabled=new CheckBox(c);disabled.setText(tr("コメント表示"));disabled.setEnabled(false);form.addView(disabled,new LinearLayout.LayoutParams(-1,-2));
    TextView value=new TextView(c);value.setText(tr("コメントの表示時間")+" · "+tr("1秒"));form.addView(value,new LinearLayout.LayoutParams(-1,-2));SeekBar seek=new SeekBar(c);seek.setProgress(40);form.addView(seek,new LinearLayout.LayoutParams(-1,48));
    ScrollView scroll=new ScrollView(c);scroll.addView(form);
    AlertDialog dialog=UiDialogs.builder(a).setTitle(tr("コメントの最大行数")).setView(scroll).setPositiveButton(tr("保存"),null).setNegativeButton(tr("キャンセル"),null).setNeutralButton(tr("デフォルトに戻す"),null).create();PlaybackSession.showDialog(dialog);
    View decor=dialog.getWindow().getDecorView();for(int n=0;n<3;n++)layout(decor,size[0],size[1]);
    org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(java.time.Duration.ofMillis(300));for(int n=0;n<3;n++)layout(decor,size[0],size[1]);decor.jumpDrawablesToCurrentState();
    String tag=mode+"-"+lang+"-"+size[0]+"x"+size[1];
    for(int which:new int[]{-1,-2,-3}){Button b=dialog.getButton(which);assertFalse(tag+": missing action label",b.getText().toString().isEmpty());assertTrue(tag+": action clipped: "+b.getText(),b.getWidth()-b.getPaddingLeft()-b.getPaddingRight()+1>=b.getPaint().measureText(b.getText().toString()));assertTrue(tag+": action hit height",b.getHeight()>=48);Rect visible=new Rect();assertTrue(tag+": hidden action",b.getGlobalVisibleRect(visible));assertTrue(tag+": clipped action height",visible.height()>=48);}
    int accent=UiDialogs.accent(a);assertEquals(tag,accent,checked.getButtonTintList().getColorForState(new int[]{android.R.attr.state_enabled,android.R.attr.state_checked},0));assertNotEquals(tag,accent,disabled.getButtonTintList().getColorForState(new int[]{},0));
    double x=Compat194Test.luminance(dialog.getButton(-1).getCurrentTextColor()),y=Compat194Test.luminance(accent);assertTrue(tag+": primary contrast",(Math.max(x,y)+.05)/(Math.min(x,y)+.05)>=4.5);
    if(size[0]==320&&"1".equals(lang)){for(int which:new int[]{-1,-2,-3}){Button b=dialog.getButton(which);System.out.println("Action geometry "+mode+" "+which+" label="+b.getText()+" rect="+b.getLeft()+","+b.getTop()+","+b.getRight()+","+b.getBottom()+" baseline="+b.getBaseline()+" layout="+b.getLayout()+" visible="+b.getVisibility()+" parent="+((View)b.getParent()).getWidth()+"x"+((View)b.getParent()).getHeight()+" clip="+b.getClipBounds()+" ink="+Integer.toHexString(b.getCurrentTextColor()));}}
    if(size[0]==320&&"1".equals(lang)&&"light".equals(mode)){Button b=dialog.getButton(-1);Bitmap isolated=Bitmap.createBitmap(b.getWidth(),b.getHeight(),Bitmap.Config.ARGB_8888);b.draw(new Canvas(isolated));try(FileOutputStream stream=new FileOutputStream(new File(output,"isolated-button.png"))){isolated.compress(Bitmap.CompressFormat.PNG,100,stream);}isolated.recycle();}
    Bitmap bitmap=Bitmap.createBitmap(size[0],size[1],Bitmap.Config.ARGB_8888);decor.draw(new Canvas(bitmap));try(FileOutputStream stream=new FileOutputStream(new File(output,tag+".png"))){assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG,100,stream));}bitmap.recycle();
    index.println(mode+","+lang+","+size[0]+","+size[1]+",true,true,true,"+tag+".png");index.flush();dialog.dismiss();lifecycle.pause().stop().destroy();
   }
  }
  System.out.println("Shared UI matrix: 3 languages x 3 themes x 3 sizes = 27 rendered cases");
 }
}

package e.e.a;
import android.app.*;import android.content.*;import android.view.*;import android.widget.*;import java.lang.reflect.*;import org.junit.*;import org.junit.runner.RunWith;import org.robolectric.*;import org.robolectric.annotation.Config;import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(manifest=Config.NONE,sdk={28,34})
public class NativePopup195Test {
 Activity a;Object service;Class<?> type;View root;WindowManager.LayoutParams window;Method touch;int updates;
 void field(String n,Object v)throws Exception{type.getField(n).set(service,v);}
 void move(float rawX,float rawY)throws Exception {MotionEvent e=MotionEvent.obtain(0,100,MotionEvent.ACTION_MOVE,rawX,rawY,0);touch.invoke(service,root,e);e.recycle();}
 @Before public void init()throws Exception {
  a=Robolectric.buildActivity(Activity.class).setup().visible().get();type=Class.forName("com.sauzask.nicoid.NicoidPopupViewService");service=NativePlayback195Test.make(type);root=new FrameLayout(a);root.layout(0,0,400,225);window=new WindowManager.LayoutParams();window.width=-2;window.height=-2;window.x=200;window.y=200;
  field("a",root);field("U",window);field("o",1000);field("p",800);field("l",0);field("f0",0f);field("n",100f);field("m",100f);
  field("Y",new GestureDetector(a,new GestureDetector.SimpleOnGestureListener()));field("R",a.getSharedPreferences("popup",0).edit());
  field("b",Proxy.newProxyInstance(WindowManager.class.getClassLoader(),new Class<?>[]{WindowManager.class},(p,m,args)->{if(m.getName().equals("updateViewLayout"))updates++;return null;}));
  type.getField("o0").setBoolean(null,true);touch=type.getMethod("onTouch",View.class,MotionEvent.class);
 }
 @Test public void movementUsesMeasuredWidthForWrapContentWindow()throws Exception {move(700,400);assertEquals(600,window.x);assertEquals(300,window.y);assertTrue(updates>0);}
 @Test public void leftEdgeReleasesImmediatelyWhenFingerReverses()throws Exception {move(-100,400);assertEquals(0,window.x);move(-80,400);assertEquals(20,window.x);}
 @Test public void rightEdgeReleasesImmediatelyWhenFingerReverses()throws Exception {move(1200,400);assertEquals(600,window.x);move(1180,400);assertEquals(580,window.x);}
 @Test public void oversizedWindowNeverProducesNegativeOrigin()throws Exception {field("o",300);field("p",200);move(1000,1000);assertEquals(0,window.x);assertEquals(0,window.y);}
}

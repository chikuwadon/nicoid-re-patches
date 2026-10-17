package e.e.a;

import android.app.Activity;
import android.content.*;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import java.lang.reflect.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

/** Execute the shipped native callback and fullscreen reparenting, without native stubs. */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE,sdk={28,34})
public class NativeVideoTitle194Test {
 static final int TITLE=0x7f0801b8;
 Activity host; Object fragment; Class<?> type; FrameLayout root; LinearLayout bar,tools; TextView title;
 static Object uninitialized(Class<?> c)throws Exception {
  Class<?> u=Class.forName("sun.misc.Unsafe");Field f=u.getDeclaredField("theUnsafe");f.setAccessible(true);
  return u.getMethod("allocateInstance",Class.class).invoke(f.get(null),c);
 }
 void field(String key,Object value)throws Exception {Field f=type.getField(key);f.set(fragment,value);}
 @Before public void setup()throws Exception {
  host=Robolectric.buildActivity(Activity.class).setup().visible().get();
  final Resources base=host.getResources();final Resources res=new Resources(base.getAssets(),base.getDisplayMetrics(),base.getConfiguration()){
   @Override public int getIdentifier(String n,String t,String p){return n.equals("titlebartext")?TITLE:super.getIdentifier(n,t,p);}
  };
  Context c=new ContextWrapper(host){@Override public Resources getResources(){return res;}};
  root=new FrameLayout(c);bar=new LinearLayout(c);tools=new LinearLayout(c);title=new TextView(c);title.setId(TITLE);title.setText("old");
  bar.addView(title,new LinearLayout.LayoutParams(0,48,1));root.addView(bar,new FrameLayout.LayoutParams(-1,48));root.addView(tools,new FrameLayout.LayoutParams(-1,48));host.setContentView(root);
  type=Class.forName("com.sauzask.nicoid.NicoidVideoFragment");fragment=type.getConstructor().newInstance();
  field("x1",root);field("A1",uninitialized(Class.forName("com.sauzask.nicoid.NicoidVideoActivity")));field("s0",true);
 }
 Bundle run()throws Exception {
  Bundle data=new Bundle();Class<?> callback=Class.forName(type.getName()+"$c0");
  ((Runnable)callback.getConstructor(type,String.class,Bundle.class).newInstance(fragment,"new title",data)).run();return data;
 }
 @Test public void normalTitleAndMetadataStillUpdate()throws Exception {assertTrue(run().getBoolean("isSide"));assertEquals("new title",title.getText().toString());}
 @Test public void fullscreenReparentedTitleStillUpdates()throws Exception {
  Method install=Class.forName("e.e.a.FullscreenScreen203").getDeclaredMethod("install",View.class,ViewGroup.class,LinearLayout.class,LinearLayout.class);install.setAccessible(true);
  try {install.invoke(null,root,(ViewGroup)host.getWindow().getDecorView(),bar,tools);
   assertNull("Native fullscreen must move the title outside the fragment root",root.findViewById(TITLE));
   assertSame(title,host.getWindow().getDecorView().findViewById(TITLE));
   assertTrue(run().getBoolean("isSide"));assertEquals("new title",title.getText().toString());
  } finally {Class.forName("e.e.a.FullscreenScreen203").getMethod("restore",View.class).invoke(null,root);}
 }
 @Test public void absentTitleDoesNotDiscardMetadata()throws Exception {bar.removeView(title);assertTrue(run().getBoolean("isSide"));}
 @Test public void absentRootIgnoresLateCallback()throws Exception {field("x1",null);assertFalse(run().containsKey("isSide"));}
 @Test public void absentActivityIgnoresLateCallback()throws Exception {field("A1",null);assertFalse(run().containsKey("isSide"));assertEquals("old",title.getText().toString());}
}

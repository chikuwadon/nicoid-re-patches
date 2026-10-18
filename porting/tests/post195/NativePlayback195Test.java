package e.e.a;
import android.app.*;import android.content.*;import android.os.*;import android.view.*;import android.widget.*;import java.lang.reflect.*;import org.junit.*;import org.junit.runner.RunWith;import org.robolectric.*;import org.robolectric.annotation.Config;import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(manifest=Config.NONE,sdk={28,34})
public class NativePlayback195Test {
 static Object make(Class<?> c)throws Exception {Class<?> u=Class.forName("sun.misc.Unsafe");Field f=u.getDeclaredField("theUnsafe");f.setAccessible(true);return u.getMethod("allocateInstance",Class.class).invoke(f.get(null),c);}
 static void set(Object o,String n,Object v)throws Exception{o.getClass().getField(n).set(o,v);}
 public static class Video extends com.devbrackets.android.exomedia.ui.widget.VideoView {
  public android.net.Uri uri;public long position;public int starts;
  public Video(Context c){super(c);}
  @Override public void setVideoURI(android.net.Uri value){uri=value;}
  @Override public void seekTo(long value){position=value;}
  @Override public void start(){starts++;}
 }
 Object fragment;Activity a;Video video;
 @Before public void init()throws Exception{a=Robolectric.buildActivity(Activity.class).setup().visible().get();fragment=Class.forName("com.sauzask.nicoid.NicoidVideoFragment").getConstructor().newInstance();set(fragment,"b0","sm123");video=(Video)make(Video.class);set(fragment,"a0",video);set(fragment,"x1",new FrameLayout(a));}
 Runnable apply(long position)throws Exception {Class<?> task=Class.forName("e.e.a.ModernControls$Switch");Object t=task.getConstructors()[0].newInstance(fragment,null,position);set(t,"url","https://example.test/video.m3u8");return (Runnable)Class.forName("e.e.a.ModernControls$Apply").getConstructors()[0].newInstance(t);}
 @Test public void qualitySwitchRestartsAtExistingPosition()throws Exception{Runnable task=apply(12345);task.run();assertNotNull(video.uri);assertEquals(12345,video.position);assertEquals(1,video.starts);}
 @Test public void obsoleteQualityChangeCannotReplaceAnotherVideo()throws Exception {Runnable task=apply(200);set(fragment,"b0","sm456");task.run();assertNull(video.uri);}
 @Test public void obsoleteQualityChangeCannotUseRecreatedPlayer()throws Exception {Runnable task=apply(200);Video next=(Video)make(Video.class);set(fragment,"a0",next);task.run();assertNull(next.uri);}
 @Test public void pendingMetadataAfterDetachDoesNotCrash()throws Exception {set(fragment,"x1",null);fragment.getClass().getMethod("f",int.class).invoke(fragment,90000);assertEquals(90000,fragment.getClass().getField("h0").getLong(fragment));}
 @Test public void durationOutsideFragmentRootIsUpdated()throws Exception {
  for(String name:new String[]{"k1","m1","l1","o1","s1"})set(fragment,name,new Button(a));set(fragment,"t1",new ProgressBar(a));set(fragment,"Z",new SeekBar(a));
  FrameLayout window=new FrameLayout(a),root=new FrameLayout(a);TextView duration=new TextView(a);duration.setId(0x7f080085);window.addView(root);window.addView(duration);a.setContentView(window);set(fragment,"x1",root);
  fragment.getClass().getMethod("f",int.class).invoke(fragment,90000);assertTrue(duration.getText().length()>0);
 }
}

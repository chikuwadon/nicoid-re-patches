package e.e.a;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
/** Link the actual patched native entry points, with no replacement native classes. */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE,sdk={28,34})
public class NativeLinkTest {
 @Test public void nativePlaybackAndCastEntryPointsLink()throws Exception {
  for(String name:new String[]{"e.e.a.n1","e.e.a.t","e.e.a.u","e.e.a.d0","e.e.a.p","e.e.a.o2","com.sauzask.nicoid.NicoidVideoActivity","com.sauzask.nicoid.NicoidChormecastSenderService"}){
   Class<?> c=Class.forName(name,false,getClass().getClassLoader());assertTrue(name,c.getDeclaredMethods().length>0);
  }
 }
}

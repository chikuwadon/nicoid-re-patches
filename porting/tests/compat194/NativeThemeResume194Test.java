package e.e.a;
import android.app.*;
import android.os.Looper;
import android.preference.PreferenceManager;
import java.lang.reflect.*;
import java.util.Map;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

/** Execute the callback from the applied APK; do not compile replacement ThemeChoice classes. */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE,sdk={28,34})
public class NativeThemeResume194Test {
 public static class Host extends Activity {int recreations;@Override public void recreate(){recreations++;}}
 Host host;Application.ActivityLifecycleCallbacks callback;Map<Activity,String> applied;
 @Before public void setup()throws Exception {
  host=Robolectric.buildActivity(Host.class).setup().visible().get();
  PreferenceManager.getDefaultSharedPreferences(host).edit().putString("app_theme","light").putString("app_lang","0").commit();
  Class<?> theme=Class.forName("e.e.a.ThemeChoice");Field map=theme.getDeclaredField("APPLIED");map.setAccessible(true);applied=(Map<Activity,String>)map.get(null);applied.clear();
  Constructor<?> ctor=Class.forName("e.e.a.ThemeChoice$1").getDeclaredConstructor();ctor.setAccessible(true);callback=(Application.ActivityLifecycleCallbacks)ctor.newInstance();
 }
 void idle(){Shadows.shadowOf(Looper.getMainLooper()).idle();}
 @Test public void returningFromThemeSettingsRecreatesOnce(){
  applied.put(host,"light:false:0");int count=0;
  for(String mode:new String[]{"dark","amoled","material","light"}){
   PreferenceManager.getDefaultSharedPreferences(host).edit().putString("app_theme",mode).commit();
   callback.onActivityResumed(host);idle();assertEquals(++count,host.recreations);
   callback.onActivityResumed(host);idle();assertEquals(count,host.recreations);
  }
 }
 @Test public void returningFromLanguageSettingsUsesSameSafePath(){applied.put(host,"light:false:1");callback.onActivityResumed(host);idle();assertEquals(1,host.recreations);}
 @Test public void finishingActivityIsNotRecreated(){applied.put(host,"dark:true:0");host.finish();callback.onActivityResumed(host);idle();assertEquals(0,host.recreations);}
 @Test public void finishingAfterResumeCancelsQueuedRecreation(){applied.put(host,"dark:true:0");callback.onActivityResumed(host);host.finish();idle();assertEquals(0,host.recreations);}
}

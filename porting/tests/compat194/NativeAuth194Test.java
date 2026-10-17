package e.e.a;
import android.app.Activity;
import android.preference.PreferenceManager;
import java.lang.reflect.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import static org.junit.Assert.*;

/** Use the original native cookie parser/serializer and real Apache CookieStore. */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE,sdk={28,34})
public class NativeAuth194Test {
 public static class Host extends Activity {public CookieStore E;}
 Host host;
 @Before public void setup()throws Exception{host=Robolectric.buildActivity(Host.class).setup().get();PreferenceManager.getDefaultSharedPreferences(host).edit().clear().putString("save_cookie","user_session=fixture; ").commit();Class.forName("e.e.a.v0").getField("b").set(null,null);}
 String cookie()throws Exception{Method method=Class.forName("e.e.a.FollowFeed").getDeclaredMethod("cookie",Activity.class);method.setAccessible(true);return (String)method.invoke(null,host);}
 @Test public void nativeCookieParserBootstrapsFirstFeed()throws Exception{String value=cookie();assertTrue(value,value.contains("user_session=fixture;"));assertNotNull(Class.forName("e.e.a.v0").getField("b").get(null));assertEquals(value,cookie());}
 @Test public void emptyNativeStoreDoesNotMaskSavedSession()throws Exception{Class.forName("e.e.a.v0").getField("b").set(null,new BasicCookieStore());assertTrue(cookie().contains("user_session=fixture;"));}
 @Test public void nativeActiveSessionWinsAndLogoutDoesNotRestoreIt()throws Exception{BasicCookieStore store=new BasicCookieStore();store.addCookie(new BasicClientCookie("user_session","active"));host.E=store;assertTrue(cookie().contains("user_session=active;"));PreferenceManager.getDefaultSharedPreferences(host).edit().putBoolean("nologin",true).commit();assertEquals("",cookie());}
}

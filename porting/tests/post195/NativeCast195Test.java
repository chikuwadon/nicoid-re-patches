package e.e.a;
import e.d.b.b.e.k.g;import e.d.b.b.e.k.k;import com.google.android.gms.common.api.Status;import java.lang.reflect.*;import org.junit.*;import org.junit.runner.RunWith;import org.robolectric.RobolectricTestRunner;import org.robolectric.annotation.Config;import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(manifest=Config.NONE,sdk={28,34})
public class NativeCast195Test {
 public static class Pending extends g<Status> {public k<? super Status> listener;@Override public void a(k<? super Status> value){listener=value;}}
 StringBuilder entries()throws Exception{Field field=Class.forName("e.e.a.ModernDebug").getDeclaredField("entries");field.setAccessible(true);return (StringBuilder)field.get(null);}
 @Test public void sendResultIsObservedUsingActualNativeSdkInterfaces()throws Exception {entries().setLength(0);Pending p=new Pending();CastDiagnostics.sent(p);assertNotNull(p.listener);p.listener.a(new Status(0));assertTrue(entries().toString(),entries().toString().contains("load message result:"));assertFalse(entries().toString().contains("unavailable"));}
 @Test public void unavailableSenderIsDiagnosticOnly()throws Exception {entries().setLength(0);CastDiagnostics.sent(null);assertTrue(entries().toString().contains("no send result"));}
}

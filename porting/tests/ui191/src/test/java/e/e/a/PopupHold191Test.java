package e.e.a;
import android.app.*;import android.os.*;import android.view.*;import org.junit.*;import org.junit.runner.RunWith;import org.robolectric.*;import org.robolectric.annotation.*;import java.time.Duration;import static org.junit.Assert.*;
@GraphicsMode(GraphicsMode.Mode.NATIVE) @RunWith(RobolectricTestRunner.class) @Config(manifest=Config.NONE,sdk=28) @LooperMode(LooperMode.Mode.PAUSED)
public class PopupHold191Test {
 static final class Probe extends ListActions.Hold {int opened;Probe(){super(null,null,"https://www.nicovideo.jp/watch/sm1","Video");} @Override void open(View view){longPressed=true;opened++;}}
 View view;Probe hold;
 @Before public void start(){Activity a=Robolectric.buildActivity(Activity.class).setup().get();view=new View(a);hold=new Probe();}
 void event(int action,float x,float y){MotionEvent e=MotionEvent.obtain(0,SystemClock.uptimeMillis(),action,x,y,0);assertFalse(hold.onTouch(view,e));e.recycle();}
 void waitMs(long ms){Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(ms));}
 @Test public void earlyFrameworkCallbackWaitsUntil500msAndFiresOnce(){event(0,20,20);waitMs(300);hold.onLongClick(view);assertEquals(0,hold.opened);waitMs(199);assertEquals(0,hold.opened);waitMs(1);assertEquals(1,hold.opened);hold.onLongClick(view);assertEquals(1,hold.opened);event(1,20,20);waitMs(1000);assertEquals(1,hold.opened);}
 @Test public void releasingDuringDelayCancelsPopup(){event(0,20,20);waitMs(300);hold.onLongClick(view);event(1,20,20);waitMs(1000);assertEquals(0,hold.opened);}
 @Test public void scrollingOrCancellationPreventsPopup(){for(int action:new int[]{2,3,5}){hold=new Probe();event(0,20,20);waitMs(300);hold.onLongClick(view);event(action,200,200);waitMs(1000);hold.onLongClick(view);assertEquals(0,hold.opened);}}
 @Test public void nextPressCanOpenAfterCancelledPress(){event(0,20,20);waitMs(300);hold.onLongClick(view);event(3,20,20);event(0,20,20);waitMs(500);hold.onLongClick(view);assertEquals(1,hold.opened);}
 @Test public void frameworkWithLongerTimeoutDoesNotGainExtraDelay(){event(0,20,20);waitMs(1500);hold.onLongClick(view);assertEquals(1,hold.opened);}
 @Test public void accessibilityLongClickWithoutTouchOpensImmediately(){hold.onLongClick(view);assertEquals(1,hold.opened);}
}

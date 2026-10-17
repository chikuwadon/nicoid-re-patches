package e.e.a;
import android.app.*;import android.os.*;import android.view.*;import android.widget.*;import java.lang.reflect.*;import java.util.*;import org.json.*;import org.junit.*;import org.junit.runner.RunWith;import org.robolectric.*;import org.robolectric.annotation.*;import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(manifest=Config.NONE,sdk={28,34})
public class TabSession208Test {
 public static class Page extends Activity {public ListView A;public ListAdapter C;public boolean H,I;public int J;public ArrayList<String> z=new ArrayList<>();public View y,Q;public Object E,O,L;}
 static Page activity(){Page a=Robolectric.buildActivity(Page.class).setup().visible().get();a.A=new ListView(a);a.C=new ArrayAdapter<String>(a,android.R.layout.simple_list_item_1,new String[]{"user"});a.A.setAdapter(a.C);FrameLayout root=new FrameLayout(a);root.addView(a.A);a.setContentView(root);return a;}
 static Object field(Object o,String n)throws Exception{Field f=o.getClass().getDeclaredField(n);f.setAccessible(true);return f.get(o);}
 static Object session(Page a)throws Exception{Field f=FollowFeed.class.getDeclaredField("STATES");f.setAccessible(true);return ((Map)f.get(null)).get(a);}
 static void select(Page a,Object session,int n)throws Exception{Method m=FollowFeed.class.getDeclaredMethod("select",Activity.class,session.getClass(),int.class);m.setAccessible(true);m.invoke(null,a,session,n);}
 static void sized(View v){v.measure(View.MeasureSpec.makeMeasureSpec(400,1073741824),View.MeasureSpec.makeMeasureSpec(600,1073741824));v.layout(0,0,400,600);}
 static void drag(TabRefresh208 refresh,int dx,int dy,boolean cancel){sized(refresh);refresh.dispatchTouchEvent(MotionEvent.obtain(0,0,0,100,10,0));refresh.dispatchTouchEvent(MotionEvent.obtain(0,20,2,100+dx/2,10+dy/2,0));refresh.dispatchTouchEvent(MotionEvent.obtain(0,40,2,100+dx,10+dy,0));refresh.dispatchTouchEvent(MotionEvent.obtain(0,60,cancel?3:1,100+dx,10+dy,0));}
 @Test public void pullRefreshActuallyInvokesFeedRequestButTabReturnDoesNot()throws Exception{
  PlaybackSession.cookies=0;Page a=activity();FollowFeed.load(a);Object s=session(a);TabRefresh208 pull=(TabRefresh208)((ViewGroup)a.y).getChildAt(3);assertEquals(1,PlaybackSession.cookies);
  // Layout root is authors, tabs, status and refresh viewport.
  drag(pull,0,110,false);assertEquals(2,PlaybackSession.cookies);select(a,s,1);assertEquals(3,PlaybackSession.cookies);select(a,s,0);assertEquals(3,PlaybackSession.cookies);drag(pull,0,110,false);assertEquals(4,PlaybackSession.cookies);
 }
 @Test public void pullRefreshReloadsOnlyVisibleFollowingTab()throws Exception{
  CreatorPage.channels=CreatorPage.mylists=0;Page a=activity();Following190.attach(a);Following190.Pager pager=(Following190.Pager)a.A.getParent();TabRefresh208 pull=(TabRefresh208)pager.getParent();
  pager.select(1);NetworkTask old=CreatorPage.channelTask;drag(pull,0,110,false);assertEquals(2,CreatorPage.channels);assertTrue(old.cancelled());assertEquals(0,CreatorPage.mylists);
  pager.animating=false;pager.select(2);org.robolectric.Shadows.shadowOf(Looper.getMainLooper()).idleFor(java.time.Duration.ofMillis(250));pager.animating=false;
  drag(pull,0,110,false);assertEquals(2,CreatorPage.mylists);assertEquals(2,CreatorPage.channels);
  pager.animating=false;pager.select(0);org.robolectric.Shadows.shadowOf(Looper.getMainLooper()).idleFor(java.time.Duration.ofMillis(250));pager.animating=false;
  y.done=new java.util.concurrent.CountDownLatch(1);a.z.add("stale");drag(pull,0,110,false);assertTrue(y.done.await(2,java.util.concurrent.TimeUnit.SECONDS));assertEquals(java.util.Arrays.asList("fresh"),a.z);assertEquals(0,a.J);assertFalse(a.H);
 }
 @Test public void pullRequiresTopThresholdAndVerticalDirection(){
  Page a=activity();final int[] count={0};ScrollView scroll=new ScrollView(a);View tall=new View(a);tall.setMinimumHeight(2000);scroll.addView(tall);TabRefresh208 pull=new TabRefresh208(a,scroll,()->count[0]++);a.setContentView(pull);sized(pull);
  drag(pull,0,30,false);drag(pull,160,40,false);drag(pull,0,110,true);assertEquals(0,count[0]);scroll.scrollTo(0,120);drag(pull,0,110,false);assertEquals(0,count[0]);ScrollView top=new ScrollView(a);View child=new View(a);child.setMinimumHeight(2000);top.addView(child);TabRefresh208 atTop=new TabRefresh208(a,top,()->count[0]++);a.setContentView(atTop);drag(atTop,0,110,false);assertEquals(1,count[0]);
 }
 @Test public void feedLoadsEachTabOnceAndRefreshesOnlySelectedTab()throws Exception{
  PlaybackSession.cookies=0;Page a=activity();FollowFeed.load(a);assertNotNull(a.y);a.setContentView(a.y);Object s=session(a);assertNotNull(s);assertEquals(1,PlaybackSession.cookies);
  for(int n=1;n<4;n++)select(a,s,n);assertEquals(4,PlaybackSession.cookies);Object[] pages=(Object[])field(s,"pages");
  for(int cycle=0;cycle<3;cycle++)for(int n=0;n<4;n++){select(a,s,n);assertSame(pages[n],field(s,"current"));assertFalse((Boolean)field(pages[n],"autoLoad"));}assertEquals("Returning to a tab must not start another request",4,PlaybackSession.cookies);
  FollowFeed.load(a);assertEquals("Explicit refresh still requests data",5,PlaybackSession.cookies);assertSame(pages[3],field(s,"current"));
  Page b=activity();FollowFeed.load(b);assertEquals("A newly opened page gets a fresh session",6,PlaybackSession.cookies);assertNotSame(s,session(b));
 }
 @Test public void feedRestoresRowsCursorAndActualListPosition()throws Exception{
  Page a=activity();FollowFeed.load(a);Object s=session(a);a.setContentView(a.y);List items=(List)field(s,"items");
  for(int i=0;i<35;i++)items.add(new FollowFeedData.Item(new JSONObject().put("id","activity"+i),new JSONObject().put("id","sm"+(100+i)).put("title","video "+i)));
  Field auto=s.getClass().getDeclaredField("autoLoad");auto.setAccessible(true);auto.set(s,false);
  Method rebuild=FollowFeed.class.getDeclaredMethod("rebuild",Activity.class,s.getClass());rebuild.setAccessible(true);rebuild.invoke(null,a,s);
  ListView list=(ListView)field(s,"list");list.measure(View.MeasureSpec.makeMeasureSpec(400,1073741824),View.MeasureSpec.makeMeasureSpec(600,1073741824));list.layout(0,0,400,600);list.setSelectionFromTop(10,-17);list.forceLayout();list.measure(View.MeasureSpec.makeMeasureSpec(400,1073741824),View.MeasureSpec.makeMeasureSpec(600,1073741824));list.layout(0,0,400,600);int first=list.getFirstVisiblePosition(),top=list.getChildAt(0).getTop();assertTrue(first>0);
  int before=PlaybackSession.cookies;select(a,s,1);select(a,s,0);list.forceLayout();list.measure(View.MeasureSpec.makeMeasureSpec(400,1073741824),View.MeasureSpec.makeMeasureSpec(600,1073741824));list.layout(0,0,400,600);assertEquals(first,list.getFirstVisiblePosition());assertEquals(top,list.getChildAt(0).getTop());assertEquals(35,items.size());assertEquals(before+1,PlaybackSession.cookies);
 }
 @Test public void backgroundTabCannotOverwriteActiveStatusAndKeepsTask()throws Exception{
  Page a=activity();FollowFeed.load(a);Object s=session(a);select(a,s,1);Object selected=field(s,"current");NetworkTask pending=new NetworkTask();Field task=s.getClass().getDeclaredField("task");task.setAccessible(true);task.set(s,pending);
  TextView status=(TextView)field(s,"status");status.setText("active tab");Method update=FollowFeed.class.getDeclaredMethod("status",s.getClass(),CharSequence.class);update.setAccessible(true);update.invoke(null,s,"background finished");assertEquals("active tab",status.getText().toString());assertSame(pending,task.get(s));assertFalse(pending.cancelled());
  Field cursor=s.getClass().getDeclaredField("cursor");cursor.setAccessible(true);cursor.set(s,"older-cursor");select(a,s,0);assertSame(s,field(s,"current"));assertEquals("older-cursor",cursor.get(s));assertSame(pending,task.get(s));assertFalse(pending.cancelled());
 }
 @Test public void followingReusesRowsScrollAndPendingRequestsAcrossTabAndSwipe()throws Exception{
  CreatorPage.channels=CreatorPage.mylists=0;Page a=activity();Following190.attach(a);Following190.Pager pager=(Following190.Pager)a.A.getParent();assertNotNull(pager);
  // Width zero completes selection synchronously; both entry points call the same select.
  pager.select(1);View channel=pager.getChildAt(1);ScrollView scroll=(ScrollView)channel;scroll.getChildAt(0).setMinimumHeight(2000);scroll.measure(View.MeasureSpec.makeMeasureSpec(400,1073741824),View.MeasureSpec.makeMeasureSpec(500,1073741824));scroll.layout(0,0,400,500);scroll.scrollTo(0,120);NetworkTask task=CreatorPage.channelTask;assertEquals(1,CreatorPage.channels);
  pager.swipe(1);assertEquals(1,CreatorPage.mylists);View mylists=pager.getChildAt(2);NetworkTask listsTask=(NetworkTask)((ScrollView)mylists).getChildAt(0).getTag();
  for(int n=0;n<3;n++){pager.select(0);pager.select(1);assertSame(channel,pager.getChildAt(1));assertFalse(task.cancelled());pager.swipe(1);assertSame(mylists,pager.getChildAt(2));assertFalse(listsTask.cancelled());}
  assertEquals(1,CreatorPage.channels);assertEquals(1,CreatorPage.mylists);assertEquals(120,scroll.getScrollY());
 }
}

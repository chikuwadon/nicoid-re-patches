package e.e.a;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import android.view.View;
import android.widget.TextView;
import android.preference.Preference;
import android.graphics.*;
import java.util.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE,sdk=28)
public class Revision196Test {
 static final String A="e9uj2uks",B="4eet3ca4";
 static class Data implements RankingPager196.Source {
  int pages,tags,fail=-1;boolean sparse;boolean duplicates;
  public JSONObject page(String genre,String term,int page,NetworkTask task)throws Exception {
   pages++;if(page==fail)throw new java.io.IOException("offline");JSONArray items=new JSONArray();
   for(int n=0;n<100;n++){int id=(page-1)*100+n+1+(genre.equals(B)&&!duplicates?2000:0);items.put(new JSONObject().put("id","sm"+id).put("title","v"+id));}
   return new JSONObject().put("items",items).put("hasNext",page<10);
  }
  public synchronized JSONArray tags(JSONObject video,NetworkTask task)throws Exception {tags++;int id=Integer.parseInt(video.getString("id").substring(2));return new JSONArray().put(new JSONObject().put("name",!sparse||id%3==0?"match":"other"));}
 }
 JSONObject preset(boolean rule,String...genres)throws Exception {
  JSONArray ids=new JSONArray();for(String g:genres)ids.put(g);JSONArray rules=new JSONArray();if(rule)rules.put(new JSONObject().put("tag","match").put("mode","all").put("source","both"));
  return new JSONObject().put("name","test").put("genres",ids).put("rules",rules);
 }
 @Test public void firstPageStopsAt100AndContinuationDoesNotRefetch()throws Exception {
  Data d=new Data();RankingPager196.Session s=new RankingPager196.Session(preset(true,A),"24h",d);
  assertEquals(100,s.page(0,new NetworkTask()).size());assertEquals(1,d.pages);assertEquals(100,d.tags);assertTrue(s.more());
  ArrayList<JSONObject> next=s.page(100,new NetworkTask());assertEquals(100,next.size());assertEquals("sm101",next.get(0).getString("id"));assertEquals(2,d.pages);assertEquals(200,d.tags);
  assertEquals("sm101",s.page(100,new NetworkTask()).get(0).getString("id"));assertEquals(200,d.tags);
 }
 @Test public void sparseResultsWaitFor100AndKeepOrder()throws Exception {
  Data d=new Data();d.sparse=true;RankingPager196.Session s=new RankingPager196.Session(preset(true,A),"24h",d);
  ArrayList<JSONObject> page=s.page(0,new NetworkTask());assertEquals(100,page.size());assertEquals(3,d.pages);assertEquals(300,d.tags);assertEquals("sm3",page.get(0).getString("id"));assertEquals("sm300",page.get(99).getString("id"));
  assertEquals("sm303",s.page(100,new NetworkTask()).get(0).getString("id"));
 }
 @Test public void allGenresRetainRoundRobinRankOrder()throws Exception {
  Data d=new Data();RankingPager196.Session s=new RankingPager196.Session(preset(false,A,B),"24h",d);ArrayList<JSONObject> p=s.page(0,new NetworkTask());
  assertEquals("sm1",p.get(0).getString("id"));assertEquals("sm2001",p.get(1).getString("id"));assertEquals("sm50",p.get(98).getString("id"));assertEquals(2,d.pages);assertEquals(0,d.tags);
 }
 @Test public void genresDeduplicateBeforeTagLookup()throws Exception {
  Data d=new Data();d.duplicates=true;RankingPager196.Session s=new RankingPager196.Session(preset(true,A,B),"24h",d);ArrayList<JSONObject> p=s.page(0,new NetworkTask());
  assertEquals(100,p.size());assertEquals("sm100",p.get(99).getString("id"));assertEquals(100,d.tags);assertEquals(2,d.pages);
 }
 @Test public void failedContinuationRetriesWithoutSkippingCandidates()throws Exception {
  Data d=new Data();RankingPager196.Session s=new RankingPager196.Session(preset(true,A),"24h",d);s.page(0,new NetworkTask());d.fail=2;
  try{s.page(100,new NetworkTask());fail();}catch(java.io.IOException expected){}
  assertEquals(100,s.matches.size());assertEquals(100,s.rank);d.fail=-1;assertEquals("sm101",s.page(100,new NetworkTask()).get(0).getString("id"));
 }
 @Test public void cancellationRollsBackAndKeepsExistingPage()throws Exception {
  Data d=new Data();RankingPager196.Session s=new RankingPager196.Session(preset(true,A),"24h",d);s.page(0,new NetworkTask());NetworkTask task=new NetworkTask();task.cancel();
  try{s.page(100,task);fail();}catch(java.io.InterruptedIOException expected){}assertEquals(100,s.matches.size());assertEquals(100,s.rank);
 }
 @Test public void fewerThan100AndEmptyResultsFinish()throws Exception {
  Data d=new Data(){public JSONArray tags(JSONObject v,NetworkTask t)throws Exception {return new JSONArray().put(new JSONObject().put("name",Integer.parseInt(v.getString("id").substring(2))<13?"match":"other"));}};
  RankingPager196.Session s=new RankingPager196.Session(preset(true,A),"24h",d);assertEquals(12,s.page(0,new NetworkTask()).size());assertFalse(s.more());assertEquals(10,d.pages);
 }
 @Test public void boundedTo1000UniqueCandidates()throws Exception {
  Data d=new Data();RankingPager196.Session s=new RankingPager196.Session(preset(false,A,B),"24h",d);for(int n=0;n<10;n++)assertEquals(100,s.page(n*100,new NetworkTask()).size());assertTrue(s.page(1000,new NetworkTask()).isEmpty());assertFalse(s.more());assertEquals(1000,s.unique.size());
 }
 @Test public void popupCenterFollowsHiddenAndVisibleStates(){
  View menu=new View(RuntimeEnvironment.getApplication()),center=new View(menu.getContext());menu.setVisibility(View.GONE);Revision196.inherit(menu,center);assertEquals(View.GONE,center.getVisibility());menu.setVisibility(View.VISIBLE);menu.setAlpha(.4f);Revision196.inherit(menu,center);assertEquals(View.VISIBLE,center.getVisibility());assertEquals(.4f,center.getAlpha(),.001f);
 }
 @Test public void spinnerTextIsCenteredWithBalancedPadding(){TextView view=new TextView(RuntimeEnvironment.getApplication());view.setPadding(9,2,0,3);Revision196.centered(view);assertEquals(android.view.Gravity.CENTER,view.getGravity());assertEquals(view.getPaddingLeft(),view.getPaddingRight());}
 @Test public void backArrowHasVisiblePixelsAndKeepsClickTarget(){TextView v=new TextView(RuntimeEnvironment.getApplication());Revision196.back(v);assertEquals("",v.getText().toString());Bitmap image=Bitmap.createBitmap(48,48,Bitmap.Config.ARGB_8888);v.getBackground().setBounds(0,0,48,48);v.getBackground().draw(new Canvas(image));assertNotNull(v.getContentDescription());}
 @Test public void summariesDescribeVisibility(){Preference p=new Preference(RuntimeEnvironment.getApplication());Revision196.summary(p,true);assertFalse(p.getSummary().toString().isEmpty());Revision196.summary(p,false);assertFalse(p.getSummary().toString().isEmpty());}
 public static class Row {public Boolean i;}
 public static class Player {public ArrayList<Row> C1=new ArrayList<>();public int D1=99;}
 public static class Host {public Player v=new Player();}
 public static class Fragment {public Host o0=new Host();public ArrayList<Row> a0;public int w0;public boolean l0;public View Z;}
 @Test public void queueRestoresNullListAndClampsPosition(){Fragment f=new Fragment();f.o0.v.C1.add(new Row());f.o0.v.C1.add(new Row());Revision196.queue(f);assertEquals(2,f.a0.size());assertEquals(1,f.w0);assertFalse(f.a0.get(0).i);assertTrue(f.a0.get(1).i);assertTrue(f.l0);assertNotSame(f.a0,f.o0.v.C1);}
 @Test public void emptyRestoredQueueCanLoadLater(){Fragment f=new Fragment();Revision196.queue(f);assertNotNull(f.a0);assertTrue(f.a0.isEmpty());assertFalse(f.l0);f.o0.v.C1.add(new Row());Revision196.queue(f);assertEquals(1,f.a0.size());assertTrue(f.l0);}
 @Test public void popupInformationPositionDefaultsLeftAndCanMoveRight(){
  android.content.Context c=RuntimeEnvironment.getApplication();android.preference.PreferenceManager.getDefaultSharedPreferences(c).edit().remove(Revision196.INFO_POSITION).commit();
  android.widget.RelativeLayout menu=new android.widget.RelativeLayout(c);android.widget.LinearLayout row=new android.widget.LinearLayout(c);menu.addView(row);View info=new View(c);
  Revision196.popupInfo(row,info);assertSame(menu,info.getParent());assertEquals(1,Revision196.popupCount(row));
  android.preference.PreferenceManager.getDefaultSharedPreferences(c).edit().putString(Revision196.INFO_POSITION,"right").commit();Revision196.popupInfo(row,info);assertSame(row,info.getParent());assertEquals(1,Revision196.popupCount(row));
  View close=new View(c);row.addView(close);assertSame(info,row.getChildAt(0));assertSame(close,row.getChildAt(1));
  android.preference.PreferenceManager.getDefaultSharedPreferences(c).edit().putString(Revision196.INFO_POSITION,"left").commit();Revision196.popupInfo(row,info);assertSame(menu,info.getParent());assertSame(close,row.getChildAt(0));
 }
 @Test public void destroyedRankingScreenCancelsAndRollsBack()throws Exception {
  Data d=new Data();RankingPager196.Session s=new RankingPager196.Session(preset(true,A),"24h",d);s.page(0,new NetworkTask());
  try{s.page(100,new NetworkTask(),()->false);fail();}catch(java.io.InterruptedIOException expected){}assertEquals(100,s.matches.size());assertEquals(100,s.rank);assertEquals(1,d.pages);
 }
}

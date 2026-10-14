package e.e.a;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.preference.PreferenceManager;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import java.util.*;
import java.util.concurrent.*;
import org.json.*;

/** Build a series-only native queue and respect the configured tap playback mode. */
final class SeriesPlayback {
    private static final ExecutorService WORK=Executors.newSingleThreadExecutor();
    private static final Handler MAIN=new Handler(Looper.getMainLooper());
    private static final Set<Activity> BUSY=Collections.newSetFromMap(new WeakHashMap<Activity,Boolean>());
    private static final Map<Activity,Object> CHANGES=new WeakHashMap<>();
    static boolean shuffled(Context c,String series){return PreferenceManager.getDefaultSharedPreferences(c).getBoolean("series_shuffle_"+series,false);}
    static boolean enabled(Context c,String series){return PreferenceManager.getDefaultSharedPreferences(c).getBoolean("series_continuous_"+series,false);}
    static Activity activity(Context c){for(int n=0;n<12&&c instanceof ContextWrapper&&!(c instanceof Activity);n++){Context next=((ContextWrapper)c).getBaseContext();if(next==null||next==c)break;c=next;}return c instanceof Activity?(Activity)c:null;}
    static void toggle(Context context,String series,boolean enabled){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("series_continuous_"+series,enabled).apply();
        Activity a=activity(context);if(a==null)return;
        Object fragment;String current;
        try{fragment=Review181.field(a,"v");current=HistoryRules.id(String.valueOf(Review181.field(fragment,"b0")));fragment.getClass().getField("C1");}catch(Exception unavailable){return;}
        Object token=new Object();synchronized(CHANGES){CHANGES.put(a,token);}
        if(!enabled){bind(fragment,null,0);return;}
        NetworkTask task=new NetworkTask();task.start(WORK,()->{try{
            ArrayList<JSONObject> videos=fetch(a,series,task);if(shuffled(context,series))Collections.shuffle(videos);ArrayList<Object> rows=rows(videos);int index=-1;
            for(int n=0;n<videos.size();n++)if(current.equals(HistoryRules.id(DetailData.target(videos.get(n))))){index=n;break;}
            if(index<0)throw new java.io.IOException("Current video is not in series");int selected=index;
            MAIN.post(()->{synchronized(CHANGES){if(CHANGES.get(a)!=token)return;}try{if(!a.isFinishing()&&current.equals(HistoryRules.id(String.valueOf(Review181.field(fragment,"b0")))))bind(fragment,rows,selected);}catch(Exception ignored){}});
        }catch(Exception error){MAIN.post(()->{synchronized(CHANGES){if(CHANGES.get(a)!=token)return;}if(!a.isFinishing())Toast.makeText(a,PanelUi.tr(a,"シリーズを取得できませんでした","Unable to load series","無法載入系列"),Toast.LENGTH_SHORT).show();});}});
    }
    private static void bind(Object fragment,ArrayList<?> rows,int index){try{
        fragment.getClass().getField("U0").setBoolean(fragment,false);
        fragment.getClass().getField("C1").set(fragment,rows);
        fragment.getClass().getField("D1").setInt(fragment,index);
        boolean enabled=rows!=null&&!rows.isEmpty();fragment.getClass().getField("U0").setBoolean(fragment,enabled);
        try{Object player=Review181.field(fragment,"a0");if(player!=null){boolean repeat=fragment.getClass().getField("F0").getBoolean(fragment);player.getClass().getMethod("setRepeatMode",int.class).invoke(player,!enabled&&repeat?1:0);}}catch(Exception ignored){}
    }catch(Exception error){android.util.Log.w("nicoid-series","Unable to update playback queue",error);}}
    private static ArrayList<JSONObject> fetch(Activity a,String series,NetworkTask task)throws Exception{
        ArrayList<JSONObject> videos=new ArrayList<>();Set<String> seen=new HashSet<>();
        for(int page=1;;page++){
            if(a.isFinishing())throw new java.io.InterruptedIOException();
            JSONObject data=VideoDetails.request("https://nvapi.nicovideo.jp/v1/playlist/series/"+series+"?pageSize=100&page="+page,"GET",false,task).getJSONObject("data");
            JSONArray items=data.optJSONArray("items");if(items==null)throw new java.io.IOException("Missing series items");int added=0;
            for(int n=0;n<items.length();n++){JSONObject item=CreatorData.video(items.optJSONObject(n));if(item==null)continue;String url=DetailData.target(item);if(!url.contains("/watch/")||!seen.add(url))continue;videos.add(item);added++;}
            if(items.length()<100||added==0||!data.optBoolean("hasNext",videos.size()<data.optInt("totalCount",Integer.MAX_VALUE)))break;
        }return videos;
    }
    private static ArrayList<Object> rows(ArrayList<JSONObject> videos)throws Exception{
        Class<?> type=Class.forName("e.e.a.x1");java.lang.reflect.Method put=type.getMethod("a",String.class,Object.class);ArrayList<Object> rows=new ArrayList<>();
        for(JSONObject item:videos){String url=DetailData.target(item);Object row=type.getConstructor().newInstance();put.invoke(row,"videourl",url);put.invoke(row,"title",item.optString("title"));put.invoke(row,"thumbnail",DetailData.thumbnail(item));type.getField("b").set(row,HistoryRules.id(url));rows.add(row);}return rows;
    }
    static void open(Context context,String series,JSONObject selected,boolean continuous) {
        Context c=context;while(c instanceof ContextWrapper&&!(c instanceof Activity)){Context next=((ContextWrapper)c).getBaseContext();if(next==c)break;c=next;}
        if(!(c instanceof Activity))return;Activity activity=(Activity)c;
        if(!continuous){PlaylistPlayback.launchTap(activity,DetailData.target(selected),selected.optString("title"),new ArrayList<>(),0,false,false);return;}
        synchronized(BUSY){if(!BUSY.add(activity))return;}
        NetworkTask task=new NetworkTask();task.start(WORK,()->{
            try{
                ArrayList<JSONObject> videos=fetch(activity,series,task);if(shuffled(context,series))Collections.shuffle(videos);ArrayList<Object> rows=rows(videos);int selectedIndex=-1;
                for(int n=0;n<videos.size();n++)if(DetailData.target(videos.get(n)).equals(DetailData.target(selected)))selectedIndex=n;
                if(selectedIndex<0)throw new java.io.IOException("Selected series video is unavailable");int index=selectedIndex;
                MAIN.post(()->{if(!activity.isFinishing())PlaylistPlayback.launchTap(activity,DetailData.target(selected),selected.optString("title"),rows,index,true,false);});
            }catch(Exception error){MAIN.post(()->{if(!activity.isFinishing())Toast.makeText(activity,PanelUi.tr(activity,"シリーズを取得できませんでした","Unable to load series","無法載入系列"),Toast.LENGTH_SHORT).show();});}
            finally{synchronized(BUSY){BUSY.remove(activity);}}
        });
    }
}

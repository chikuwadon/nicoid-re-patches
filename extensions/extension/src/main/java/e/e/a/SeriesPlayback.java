package e.e.a;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
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
    static void open(Context context,String series,JSONObject selected,boolean continuous) {
        Context c=context;while(c instanceof ContextWrapper&&!(c instanceof Activity)){Context next=((ContextWrapper)c).getBaseContext();if(next==c)break;c=next;}
        if(!(c instanceof Activity))return;Activity activity=(Activity)c;
        if(!continuous){PlaylistPlayback.launchTap(activity,DetailData.target(selected),selected.optString("title"),new ArrayList<>(),0,false,false);return;}
        synchronized(BUSY){if(!BUSY.add(activity))return;}
        NetworkTask task=new NetworkTask();task.start(WORK,()->{
            try{
                ArrayList<JSONObject> videos=new ArrayList<>();Set<String> seen=new HashSet<>();
                for(int page=1;;page++){
                    if(activity.isFinishing())return;
                    JSONObject data=VideoDetails.request("https://nvapi.nicovideo.jp/v1/playlist/series/"+series+"?pageSize=100&page="+page,"GET",false,task).getJSONObject("data");
                    JSONArray items=data.optJSONArray("items");if(items==null)throw new java.io.IOException("Missing series items");int added=0;
                    for(int n=0;n<items.length();n++){JSONObject row=items.optJSONObject(n),item=row==null?null:row.optJSONObject("content");if(item==null)continue;String url=DetailData.target(item);if(!url.contains("/watch/")||!seen.add(url))continue;videos.add(item);added++;}
                    if(items.length()<100||added==0||!data.optBoolean("hasNext",videos.size()<data.optInt("totalCount",Integer.MAX_VALUE)))break;
                }
                Class<?> type=Class.forName("e.e.a.x1");java.lang.reflect.Method put=type.getMethod("a",String.class,Object.class);ArrayList<Object> rows=new ArrayList<>();int selectedIndex=-1;
                for(JSONObject item:videos){String url=DetailData.target(item);if(url.equals(DetailData.target(selected)))selectedIndex=rows.size();Object row=type.getConstructor().newInstance();put.invoke(row,"videourl",url);put.invoke(row,"title",item.optString("title"));put.invoke(row,"thumbnail",DetailData.thumbnail(item));type.getField("b").set(row,HistoryRules.id(url));rows.add(row);}
                if(selectedIndex<0)throw new java.io.IOException("Selected series video is unavailable");int index=selectedIndex;
                MAIN.post(()->{if(!activity.isFinishing())PlaylistPlayback.launchTap(activity,DetailData.target(selected),selected.optString("title"),rows,index,true,false);});
            }catch(Exception error){MAIN.post(()->{if(!activity.isFinishing())Toast.makeText(activity,PanelUi.tr(activity,"シリーズを取得できませんでした","Unable to load series","無法載入系列"),Toast.LENGTH_SHORT).show();});}
            finally{synchronized(BUSY){BUSY.remove(activity);}}
        });
    }
}

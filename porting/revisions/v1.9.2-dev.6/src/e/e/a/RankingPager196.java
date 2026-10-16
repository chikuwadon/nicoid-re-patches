package e.e.a;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;
import org.json.*;
import java.util.*;
import java.util.concurrent.*;

/** Incremental exact-tag filtering, preserving the official interleaved rank order. */
public final class RankingPager196 {
    static final Map<Object, Session> SESSIONS = Collections.synchronizedMap(new WeakHashMap<Object, Session>());
    static final ExecutorService TAGS = Executors.newFixedThreadPool(4, r -> {Thread t=new Thread(r,"nicoid-ranking-tags");t.setDaemon(true);return t;});
    interface Source {
        JSONObject page(String genre,String term,int page,NetworkTask task)throws Exception;
        JSONArray tags(JSONObject video,NetworkTask task)throws Exception;
    }
    interface Scope {boolean active();}
    static final Source NETWORK = new Source() {
        public JSONObject page(String genre,String term,int page,NetworkTask task)throws Exception {
            return VideoDetails.request("https://nvapi.nicovideo.jp/v1/ranking/teiban/"+Uri.encode(genre)+"?term="+Uri.encode(term)+"&pageSize=100&page="+page,"GET",false,task).getJSONObject("data");
        }
        public JSONArray tags(JSONObject video,NetworkTask task)throws Exception {
            // Reuse the current parser, cookie handling and tag cache, including nested tags.
            java.lang.reflect.Method m=Class.forName("e.e.a.RankingFetch190").getDeclaredMethod("embedded",JSONObject.class);
            m.setAccessible(true);JSONArray embedded=(JSONArray)m.invoke(null,video);
            if(embedded!=null)return embedded;
            m=Class.forName("e.e.a.RankingFetch190").getDeclaredMethod("tags",String.class,NetworkTask.class);
            m.setAccessible(true);
            try{return (JSONArray)m.invoke(null,video.getString("id"),task);}
            catch(java.lang.reflect.InvocationTargetException e){throw (Exception)e.getCause();}
        }
    };
    static final class Genre {
        final String id; int page=-1; JSONArray items=new JSONArray(); boolean ended;
        final Set<String> seen=new HashSet<>();
        Genre(String id){this.id=id;}
    }
    static final class Session {
        final String signature,term; final JSONArray rules; final Genre[] genres; final Source source;
        final ArrayList<JSONObject> matches=new ArrayList<>(); final Set<String> unique=new HashSet<>();
        int rank,genreIndex,warnings; boolean ended;Scope scope;
        void checkScope(NetworkTask task)throws java.io.InterruptedIOException {if(scope!=null&&!scope.active())task.cancel();check(task);}
        Session(JSONObject preset,String term,Source source)throws Exception {
            RankingRules190.validate(preset);signature=preset.toString()+"/"+term;this.term=term;this.source=source;rules=preset.getJSONArray("rules");
            JSONArray ids=preset.getJSONArray("genres");genres=new Genre[ids.length()];for(int i=0;i<genres.length;i++)genres[i]=new Genre(ids.getString(i));
        }
        boolean more(){return !ended;}
        JSONObject candidate(NetworkTask task)throws Exception {
            while(!ended){
                checkScope(task);if(unique.size()>=1000||rank>=1000){ended=true;return null;}
                boolean all=true;for(Genre g:genres)all&=g.ended&&rank>=g.page*100+g.items.length();
                if(all){ended=true;return null;}
                Genre g=genres[genreIndex];int page=rank/100,index=rank%100;
                if(!g.ended&&g.page!=page){
                    JSONObject data=source.page(g.id,term,page+1,task);checkScope(task);
                    JSONArray items=data.getJSONArray("items");
                    Set<String> ids=new HashSet<>();
                    for(int n=0;n<items.length();n++){JSONObject item=items.optJSONObject(n);if(item!=null){JSONObject v=item.optJSONObject("content");if(v==null)v=item;String id=v.optString("id",item.optString("watchId"));if(!id.isEmpty())ids.add(id);}}
                    if(!ids.isEmpty()&&g.seen.containsAll(ids))throw new java.io.IOException("Repeated ranking page");
                    g.seen.addAll(ids);g.page=page;g.items=items;g.ended=items.length()==0||!data.optBoolean("hasNext",false)||page>=9;
                }
                JSONObject item=g.page==page?g.items.optJSONObject(index):null;
                genreIndex++;if(genreIndex==genres.length){genreIndex=0;rank++;}
                if(item==null)continue;JSONObject video=item.optJSONObject("content");if(video==null)video=item;
                String id=video.optString("id",item.optString("watchId",""));if(!id.matches("(?:sm|nm|so|ss)[0-9]+")||!unique.add(id))continue;
                video.put("id",id);return video;
            }return null;
        }
        synchronized ArrayList<JSONObject> page(int start,NetworkTask task)throws Exception {return page(start,task,null);}
        synchronized ArrayList<JSONObject> page(int start,NetworkTask task,Scope scope)throws Exception {
            this.scope=scope;
            int oldRank=rank,oldGenre=genreIndex,oldWarnings=warnings,oldSize=matches.size();boolean oldEnded=ended;
            Set<String> oldUnique=new HashSet<>(unique);int[] pages=new int[genres.length];boolean[] ends=new boolean[genres.length];JSONArray[] items=new JSONArray[genres.length];ArrayList<Set<String>> seen=new ArrayList<>();
            for(int n=0;n<genres.length;n++){pages[n]=genres[n].page;ends[n]=genres[n].ended;items[n]=genres[n].items;seen.add(new HashSet<>(genres[n].seen));}
            try{return readPage(start,task);}catch(Exception e){
                rank=oldRank;genreIndex=oldGenre;warnings=oldWarnings;ended=oldEnded;while(matches.size()>oldSize)matches.remove(matches.size()-1);unique.clear();unique.addAll(oldUnique);
                for(int n=0;n<genres.length;n++){genres[n].page=pages[n];genres[n].ended=ends[n];genres[n].items=items[n];genres[n].seen.clear();genres[n].seen.addAll(seen.get(n));}throw e;
            }finally{this.scope=null;}
        }
        ArrayList<JSONObject> readPage(int start,NetworkTask task)throws Exception {
            int target=Math.min(1000,start+100);
            while(matches.size()<target&&!ended){
                // At most four in-flight requests; never submit all 1000 tag lookups.
                ArrayList<JSONObject> videos=new ArrayList<>();ArrayList<NetworkTask> children=new ArrayList<>();ArrayList<Future<JSONArray>> pending=new ArrayList<>();
                try {
                    int batch=rules.length()==0?1:Math.min(4,target-matches.size());
                    for(int n=0;n<batch;n++){JSONObject v=candidate(task);if(v==null)break;videos.add(v);if(rules.length()>0){NetworkTask child=new NetworkTask();children.add(child);pending.add(TAGS.submit(()->source.tags(v,child)));}}
                    for(int n=0;n<videos.size();n++){
                        boolean hit=rules.length()==0;
                        if(!hit)try{JSONArray tags;for(;;){checkScope(task);try{tags=pending.get(n).get(100,TimeUnit.MILLISECONDS);break;}catch(TimeoutException waiting){}}hit=RankingRules190.matches(tags,rules);}catch(ExecutionException e){warnings++;}
                        if(hit)matches.add(videos.get(n));
                    }
                }finally{for(int n=0;n<children.size();n++){children.get(n).cancel();pending.get(n).cancel(true);}}
            }
            if(unique.size()>=1000)ended=true;
            checkScope(task);return new ArrayList<>(matches.subList(Math.min(start,matches.size()),Math.min(target,matches.size())));
        }
    }
    static void check(NetworkTask task)throws java.io.InterruptedIOException {if(task.cancelled())throw new java.io.InterruptedIOException();}
    static void set(Object o,String field,Object value)throws Exception {o.getClass().getField(field).set(o,value);}
    public static boolean custom(Object loader){
        try{
            Uri uri=Uri.parse(String.valueOf(Review181.field(loader,"a")));String key=uri.getLastPathSegment();if(key==null||!key.startsWith("custom-"))return false;
            Context c=(Context)Review181.field(loader,"p");VideoDetails.initializeCookies(c);
            JSONArray presets=Ranking190.stored(c,"ranking_presets_v190");JSONObject preset=null;
            for(int n=0;n<presets.length();n++){JSONObject p=presets.optJSONObject(n);if(p!=null&&key.equals("custom-"+p.optString("id")))preset=p;}
            if(preset==null)throw new IllegalStateException("Missing ranking preset");
            int start=RankingPages190.offset(loader);String term=uri.getQueryParameter("term");if(term==null)term="24h";
            String signature=preset.toString()+"/"+term;Session s=SESSIONS.get(loader);
            // First-page refresh is transactional: leave the prior session usable on failure.
            boolean fresh=start==0||s==null||!s.signature.equals(signature);if(fresh)s=new Session(preset,term,NETWORK);
            NetworkTask task=new NetworkTask();ArrayList<JSONObject> videos;
            int warnings=s.warnings;
            try{videos=s.page(start,task,()->!(c instanceof Activity)||!((Activity)c).isFinishing()&&!((Activity)c).isDestroyed());}finally{task.cancel();}
            if(fresh)SESSIONS.put(loader,s);
            ArrayList<Object> rows=new ArrayList<>();Class<?> model=Class.forName("e.e.a.x1"),format=Class.forName("e.e.a.ModernRanking");
            for(int n=0;n<videos.size();n++){
                JSONObject v=videos.get(n),count=v.optJSONObject("count");if(count==null)count=new JSONObject();Object row=model.newInstance();
                set(row,"a","https://www.nicovideo.jp/watch/"+v.getString("id"));set(row,"b",v.getString("id"));set(row,"h",v.optString("title"));set(row,"y",ContentFilter.owner(v));set(row,"c",DetailData.thumbnail(v));set(row,"g",Integer.toString(start+n+1));
                set(row,"p",count.optLong("view"));set(row,"q",count.optLong("comment"));set(row,"m",count.optInt("mylist"));int seconds=v.optInt("duration");set(row,"d",android.text.Html.fromHtml(String.format(Locale.ROOT,"%d:%02d",seconds/60,seconds%60)));
                set(row,"f",format.getMethod("formatJst",String.class).invoke(null,v.optString("registeredAt")));set(row,"e",format.getMethod("countInfo",JSONObject.class).invoke(null,count));rows.add(row);
            }
            RankingPages190.loaded(loader,rows);loader.getClass().getField("i").setInt(loader,s.more()&&start<900?Math.max(1,rows.size()):0);
            Handler handler=(Handler)Review181.field(loader,"l");
            if(s.warnings>warnings)handler.post(()->Toast.makeText(c,UiStrings.translate("一部の動画のタグを取得できませんでした"),Toast.LENGTH_LONG).show());
            Runnable result=(Runnable)Class.forName("e.e.a.e0$i").getConstructor(loader.getClass(),ArrayList.class,int.class).newInstance(loader,rows,0);handler.post(result);return true;
        }catch(Exception e){
            try{((Handler)Review181.field(loader,"l")).post((Runnable)Class.forName("e.e.a.e0$c").getConstructor(loader.getClass()).newInstance(loader));}catch(Exception ignored){}
            android.util.Log.w("nicoid-ranking","Unable to load ranking page",e);return true;
        }
    }
}

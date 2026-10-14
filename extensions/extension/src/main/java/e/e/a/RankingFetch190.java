package e.e.a;

import org.json.*;import java.util.*;

/** Merge official genre rankings in rank order, then apply exact tag conditions. */
final class RankingFetch190 {
 static final ThreadLocal<JSONObject> PAGE=new ThreadLocal<>();
 private static final java.util.concurrent.ExecutorService TAG_WORK=java.util.concurrent.Executors.newFixedThreadPool(4);
 private static final LinkedHashMap<String,TagEntry> TAGS=new LinkedHashMap<String,TagEntry>(64,.75f,true){protected boolean removeEldestEntry(Map.Entry<String,TagEntry> e){return size()>128;}};
 static final class TagEntry {long time;JSONArray tags;TagEntry(JSONArray tags){this.tags=tags;time=System.currentTimeMillis();}}
 static ArrayList<JSONObject> load(JSONObject preset,String term,NetworkTask task)throws Exception{RankingRules190.validate(preset);JSONArray genres=preset.getJSONArray("genres"),rules=preset.getJSONArray("rules");LinkedHashMap<String,JSONObject> unique=new LinkedHashMap<>();HashMap<String,Integer> ranks=new HashMap<>();int genreOrder=0;
  for(int g=0;g<genres.length();g++){String key=genres.getString(g);Set<String> pageIds=new HashSet<>();int offset=0;for(;;){if(task.cancelled())throw new java.io.InterruptedIOException();JSONObject data=VideoDetails.request("https://nvapi.nicovideo.jp/v1/ranking/teiban/"+key+"?term="+term+"&pageSize=100&page="+(offset/100+1),"GET",false,task).getJSONObject("data");JSONArray items=data.getJSONArray("items");int added=0;for(int i=0;i<items.length();i++){JSONObject item=items.optJSONObject(i);if(item==null)continue;JSONObject video=item.optJSONObject("content");if(video==null)video=item;String id=video.optString("id",item.optString("watchId",""));if(!id.matches("(?:sm|nm|so|ss)[0-9]+"))continue;video.put("id",id);if(pageIds.add(id))added++;int rank=(offset+i)*genres.length()+genreOrder;if(!unique.containsKey(id)||rank<ranks.get(id)){unique.put(id,video);ranks.put(id,rank);}}
   offset+=100;if(items.length()==0||offset>=1000||!data.optBoolean("hasNext",false))break;if(added==0)throw new java.io.IOException("Repeated ranking page");}
   genreOrder++;
  }
  ArrayList<JSONObject> sorted=new ArrayList<>(unique.values());Collections.sort(sorted,(x,y)->Integer.compare(ranks.get(x.optString("id")),ranks.get(y.optString("id"))));if(sorted.size()>1000)sorted=new ArrayList<>(sorted.subList(0,1000));if(rules.length()==0)return sorted;ArrayList<JSONObject> filtered=new ArrayList<>();ArrayList<NetworkTask> children=new ArrayList<>();ArrayList<java.util.concurrent.Future<JSONArray>> pending=new ArrayList<>();try{for(JSONObject video:sorted){if(task.cancelled())throw new java.io.InterruptedIOException();NetworkTask child=new NetworkTask();children.add(child);String id=video.getString("id");pending.add(TAG_WORK.submit(()->tags(id,child)));}for(int i=0;i<sorted.size();i++){JSONArray tags;for(;;){if(task.cancelled())throw new java.io.InterruptedIOException();try{tags=pending.get(i).get(100,java.util.concurrent.TimeUnit.MILLISECONDS);break;}catch(java.util.concurrent.TimeoutException waiting){}}if(RankingRules190.matches(tags,rules))filtered.add(sorted.get(i));}return filtered;}finally{for(int i=0;i<children.size();i++){children.get(i).cancel();pending.get(i).cancel(true);}}
 }
 public static JSONArray parseOfficial(String source)throws JSONException{JSONObject data=new JSONObject(source).getJSONObject("data");PAGE.set(data);JSONArray items=data.getJSONArray("items"),out=new JSONArray();for(int i=0;i<items.length();i++){JSONObject item=items.optJSONObject(i);if(item!=null)out.put(item.optJSONObject("content")==null?item:item.getJSONObject("content"));}return out;}
 static JSONArray tags(String id,NetworkTask task)throws Exception{ synchronized(TAGS){TagEntry e=TAGS.get(id);if(e!=null&&System.currentTimeMillis()-e.time<5*60*1000)return e.tags;}
  JSONObject response=VideoDetails.request("https://www.nicovideo.jp/watch/"+id+"?responseType=json","GET",true,task);JSONObject watch=VideoDetails.watchData(response),tag=watch.optJSONObject("tag");if(tag==null||tag.optJSONArray("items")==null)throw new java.io.IOException("Missing ranking tags");JSONArray tags=tag.getJSONArray("items");synchronized(TAGS){TAGS.put(id,new TagEntry(tags));}return tags;
 }
}

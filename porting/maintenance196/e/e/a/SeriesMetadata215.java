package e.e.a;
import android.net.Uri;import android.text.Html;import org.json.*;import java.io.*;import java.net.*;import java.util.*;import java.util.regex.*;
/** Series-only V4 metadata. Never changes playback data or access-right keys. */
public final class SeriesMetadata215 {
 static final Pattern META=Pattern.compile("<meta\\b[^>]*\\bname=[\"']server-response[\"'][^>]*\\bcontent=\"([^\"]*)\"",Pattern.CASE_INSENSITIVE);
 static final Map<String,Entry> CACHE=new LinkedHashMap<String,Entry>();
 static final class Entry {final JSONObject data;final long at=android.os.SystemClock.elapsedRealtime();Entry(JSONObject d){data=d;}}
 static final class Expired extends IOException {}
 static JSONObject cached(String id){synchronized(CACHE){Entry e=CACHE.get(id);return e!=null&&android.os.SystemClock.elapsedRealtime()-e.at<60000?e.data:null;}}
 static String transfer(String url,String body,NetworkTask task)throws Exception {
  HttpURLConnection c=(HttpURLConnection)new URL(url).openConnection();if(!task.bind(c))throw new InterruptedIOException();
  try{c.setRequestMethod(body==null?"GET":"POST");c.setConnectTimeout(10000);c.setReadTimeout(10000);c.setInstanceFollowRedirects(false);WatchRequestHeaders.apply(c,url);c.setRequestProperty("User-Agent","Mozilla/5.0");
   c.setRequestProperty("X-Frontend-Id","6");c.setRequestProperty("X-Frontend-Version","0");c.setRequestProperty("X-Request-With","https://www.nicovideo.jp");c.setRequestProperty("Origin","https://www.nicovideo.jp");c.setRequestProperty("Cookie",VideoDetails.cookie());
   if(body!=null){byte[] b=body.getBytes("UTF-8");c.setDoOutput(true);c.setRequestProperty("Content-Type","application/json");c.setFixedLengthStreamingMode(b.length);try(OutputStream out=c.getOutputStream()){out.write(b);}}
   int status=c.getResponseCode();InputStream stream=status>=200&&status<300?c.getInputStream():c.getErrorStream();ByteArrayOutputStream bytes=new ByteArrayOutputStream();
   if(stream!=null)try(InputStream in=stream){byte[] buffer=new byte[8192];int n;while((n=in.read(buffer))!=-1){if(task.cancelled()||bytes.size()+n>2*1024*1024)throw new IOException("Request cancelled or oversized");bytes.write(buffer,0,n);}}
   String text=bytes.toString("UTF-8");if(status<200||status>=300){try{if("KEY_EXPIRED".equals(new JSONObject(text).getJSONObject("meta").optString("errorCode")))throw new Expired();}catch(JSONException ignored){}throw new IOException("Series metadata HTTP "+status);}return text;
  }finally{task.release(c);c.disconnect();}
 }
 static JSONObject bootstrap(String id,NetworkTask task)throws Exception {
  String page=transfer("https://www.nicovideo.jp/watch/"+Uri.encode(id),null,task);Matcher m=META.matcher(page);if(!m.find())throw new IOException("Missing V4 metadata");
  JSONObject data=new JSONObject(Html.fromHtml(m.group(1)).toString()).getJSONObject("data").getJSONObject("response").getJSONObject("$watchV4").getJSONObject("data");
  if(!id.equals(data.getJSONObject("video").optString("id")))throw new IOException("Mismatched V4 video");return data;
 }
 public static JSONObject watch(String id,NetworkTask task)throws Exception {
  if(id==null||!id.matches("(?:sm|nm|so|ss)?[0-9]+"))throw new IOException("Invalid video id");JSONObject saved=cached(id);if(saved!=null)return saved;
  for(int attempt=0;attempt<2;attempt++)try{
   JSONObject base=bootstrap(id,task);String token=base.getJSONObject("lazy").getString("authKey"),track=base.getJSONObject("client").getString("watchTrackId");if(token.isEmpty())throw new IOException("Missing V4 key");
   String url="https://nvapi.nicovideo.jp/v4/watch/lazy/"+Uri.encode(id)+"?actionTrackId="+Uri.encode(track);
   JSONObject response=new JSONObject(transfer(url,new JSONObject().put("keyToken",token).toString(),task));JSONObject meta=response.optJSONObject("meta");if(meta==null||meta.optInt("status",500)!=200)throw new IOException("V4 request rejected");
   JSONObject data=response.getJSONObject("data");if(!data.has("series"))throw new IOException("Missing V4 series field");Object series=data.get("series");
   if(series!=JSONObject.NULL&&(!(series instanceof JSONObject)||!((JSONObject)series).optString("id").matches("[0-9]+")))throw new IOException("Invalid V4 series");
   JSONObject result=new JSONObject().put("video",new JSONObject().put("id",id)).put("series",series);synchronized(CACHE){CACHE.put(id,new Entry(result));while(CACHE.size()>16)CACHE.remove(CACHE.keySet().iterator().next());}
   ModernDebug.record("Series V4 received: "+(series==JSONObject.NULL?"none":"present"));return result;
  }catch(Expired e){if(attempt==1)throw new IOException("Series V4 key expired");}
  throw new IOException("Unable to load V4 series");
 }
}

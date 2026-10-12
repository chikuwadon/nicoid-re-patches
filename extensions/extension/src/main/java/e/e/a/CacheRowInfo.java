package e.e.a;
import org.json.*;import java.text.*;import java.util.*;
/** Cached metadata is presented with the same statistics and Japanese date format as other lists. */
public final class CacheRowInfo {
 public final String counts,owner,posted,transfer;
 CacheRowInfo(String c,String o,String p,String t){counts=c;owner=o;posted=p;transfer=t;}
 public static CacheRowInfo from(JSONObject data,long bytes,boolean complete){
  String counts="再生数:"+data.optLong("viewCount")+" コメント:"+data.optLong("commentCount");if(data.has("likeCount"))counts+=" いいね:"+data.optLong("likeCount");counts+=" マイリスト:"+data.optLong("mylistCount");
  String owner=data.optString("ownerName",data.optString("uploaderName",data.optString("userNickname","")));
  for(String key:new String[]{"owner","channel","details_owner","details_channel"}){if(!owner.isEmpty())break;JSONObject o=data.optJSONObject(key);if(o==null){try{o=new JSONObject(data.optString(key,"{}"));}catch(Exception ignored){}}if(o!=null)owner=o.optString("nickname",o.optString("name",""));}
  String posted=data.optString("postedAt",data.optString("first_retrieve",""));for(String pattern:new String[]{"yyyy-MM-dd'T'HH:mm:ssXXX","yyyy/MM/dd HH:mm:ss","yyyy-MM-dd HH:mm:ss"})try{SimpleDateFormat f=new SimpleDateFormat(pattern,Locale.ROOT);f.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));Date date=f.parse(posted);if(date!=null){SimpleDateFormat out=new SimpleDateFormat("yyyy年MM月dd日 HH時mm分",Locale.JAPAN);out.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));posted=out.format(date)+" 投稿";break;}}catch(Exception ignored){}
  return new CacheRowInfo(counts,owner,posted,String.format(Locale.ROOT,"%.1f MB",bytes/1048576f)+(complete?" (100%)":" · 未完了"));
 }
}

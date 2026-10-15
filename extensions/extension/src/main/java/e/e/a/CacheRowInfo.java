package e.e.a;
import org.json.*;import java.text.*;import java.util.*;
/** Cached metadata is presented with the same statistics and date format as other lists. */
public final class CacheRowInfo {
 public final String counts,owner,posted,transfer;
 public final long[] statistics;
 CacheRowInfo(String c,String o,String p,String t,long[] s){counts=c;owner=o;posted=p;transfer=t;statistics=s;}
 public static CacheRowInfo from(JSONObject data,long bytes,boolean complete){
  long[] statistics=VideoInfoCounts.read(key->data.isNull(key)?null:data.optString(key,null));String[] labels={"再生数","コメント","いいね","マイリスト"};String counts="";for(int i=0;i<4;i++)if(statistics[i]>=0)counts+=(counts.isEmpty()?"":" ")+labels[i]+":"+statistics[i];
  String owner=data.optString("ownerName",data.optString("uploaderName",data.optString("userNickname","")));
  for(String key:new String[]{"owner","channel","details_owner","details_channel"}){if(!owner.isEmpty())break;JSONObject o=data.optJSONObject(key);if(o==null){try{o=new JSONObject(data.optString(key,"{}"));}catch(Exception ignored){}}if(o!=null)owner=o.optString("nickname",o.optString("name",""));}
  String posted=VideoRows191.date(data.optString("postedAt",data.optString("first_retrieve","")),"投稿");
  return new CacheRowInfo(counts,owner,posted,String.format(Locale.ROOT,"%.1f MB",bytes/1048576f)+(complete?" (100%)":" · 未完了"),statistics);
 }
}

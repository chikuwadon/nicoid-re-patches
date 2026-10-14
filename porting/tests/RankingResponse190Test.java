package e.e.a;
import org.json.*;
public final class RankingResponse190Test {
 public static void main(String[] args)throws Exception{
  JSONObject video=new JSONObject().put("type","essential").put("id","sm9").put("title","test").put("duration",22).put("count",new JSONObject().put("view",10)).put("owner",new JSONObject().put("name","owner"));
  JSONArray input=new JSONArray().put(new JSONObject().put("watchId","sm9").put("content",video));
  JSONArray parsed=RankingFetch190.parseOfficial(new JSONObject().put("data",new JSONObject().put("items",input)).toString());
  if(parsed.length()!=1||!parsed.getJSONObject(0).getString("id").equals("sm9")||parsed.getJSONObject(0).getJSONObject("count").getInt("view")!=10||!parsed.getJSONObject(0).getJSONObject("owner").getString("name").equals("owner"))throw new AssertionError("Official playlist wrapper lost video metadata");
  input=new JSONArray().put(video);parsed=RankingFetch190.parseOfficial(new JSONObject().put("data",new JSONObject().put("items",input)).toString());
  if(parsed.getJSONObject(0).getInt("duration")!=22)throw new AssertionError("Flat response lost duration");
  try{RankingFetch190.parseOfficial("{\"meta\":{\"status\":500}}");throw new AssertionError("Invalid response accepted as empty ranking");}catch(JSONException expected){}
  System.out.println("Official ranking response conversion verified");
 }
}

package e.e.a;
import org.json.*;
public final class RankingRules190Test {
 static void check(boolean value,String label){if(!value)throw new AssertionError(label);}
 static JSONObject condition(String tag,String mode,String source)throws Exception{return new JSONObject().put("tag",tag).put("mode",mode).put("source",source);}
 public static void main(String[] args)throws Exception{
  JSONArray tags=new JSONArray().put(new JSONObject().put("name","VOICEROID実況プレイ").put("isLocked",true)).put(new JSONObject().put("name","ゲーム").put("isLocked",false));
  check(RankingRules190.matches(tags,new JSONArray()),"Empty conditions retain videos");
  check(RankingRules190.matches(tags,new JSONArray().put(condition("voiceroid実況プレイ","all","locked")).put(condition("ゲーム","all","user"))),"AND, source and case");
  check(!RankingRules190.matches(tags,new JSONArray().put(condition("ゲーム","all","locked"))),"Source mismatch");
  check(RankingRules190.matches(tags,new JSONArray().put(condition("料理","any","both")).put(condition("ゲーム","any","both"))),"OR");
  check(!RankingRules190.matches(tags,new JSONArray().put(condition("料理","any","both"))),"No OR match");
  check(!RankingRules190.matches(tags,new JSONArray().put(condition("ゲーム","all","both")).put(condition("VOICEROID実況プレイ","exclude","both"))),"Exclude veto");
  check(!RankingRules190.contains(tags,"実況","both"),"Exact tags only");
  check(RankingRules190.normalized(" ＡＢＣ ").equals("abc"),"Width normalization");
  JSONArray restored=RankingRules190.categories(new JSONArray().put(new JSONObject().put("id","4eet3ca4").put("visible",false)).put(new JSONObject().put("id","obsolete")).put(new JSONObject().put("id","4eet3ca4")));
  check(restored.length()==RankingRules190.GENRES.length,"Missing genres restored, duplicates removed");
  check(restored.getJSONObject(0).getString("id").equals("4eet3ca4")&&!restored.getJSONObject(0).getBoolean("visible"),"Ordering and visibility preserved");
  JSONObject valid=new JSONObject().put("name","test").put("genres",new JSONArray().put("e9uj2uks")).put("rules",new JSONArray());RankingRules190.validate(valid);
  try{RankingRules190.validate(new JSONObject(valid.toString()).put("genres",new JSONArray()));throw new AssertionError("Empty genres accepted");}catch(JSONException expected){}
  try{RankingRules190.validate(new JSONObject(valid.toString()).put("rules",new JSONArray().put(condition("x","invalid","both"))));throw new AssertionError("Unknown mode accepted");}catch(JSONException expected){}
  System.out.println("Ranking conditions and persisted genre settings verified");
 }
}

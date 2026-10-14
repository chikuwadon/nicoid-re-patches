package e.e.a;

import java.util.*;import org.json.*;

/** Device-local category order and exact tag conditions. */
public final class RankingRules190 {
 static final String[][] GENRES={
  {"e9uj2uks","総合"},{"4eet3ca4","ゲーム"},{"zc49b03a","アニメ"},{"dshv5do5","ボカロ"},{"wnm2mhv0","音声合成実況・解説・劇場"},{"8kjl94d9","エンタメ"},{"wq76qdin","音楽"},{"1ya6bnqd","歌ってみた"},{"6yuf530c","踊ってみた"},{"6r5jr8nd","演奏してみた"},{"v6wdx6p5","解説・講座"},{"lq8d5918","料理"},{"k1libcse","旅行・アウトドア"},{"24aa8fkw","自然"},{"3d8zlls9","乗り物"},{"n46kcz9u","技術・工作"},{"lzicx0y6","社会・政治・時事"},{"p1acxuoz","MMD"},{"6mkdo4xd","VTuber"},{"oxzi6bje","ラジオ"},{"4w3p65pf","スポーツ"},{"ne72lua2","動物"},{"ramuboyn","その他"}};
 private RankingRules190(){}
 static boolean known(String key){for(String[] g:GENRES)if(g[0].equals(key))return true;return false;}
 static String name(String id){for(String[] g:GENRES)if(g[0].equals(id))return g[1];return id;}
 static JSONArray categories(JSONArray saved)throws JSONException{JSONArray out=new JSONArray();Set<String> seen=new HashSet<>();for(int i=0;i<saved.length();i++){JSONObject g=saved.optJSONObject(i);if(g!=null&&known(g.optString("id"))&&seen.add(g.optString("id")))out.put(new JSONObject().put("id",g.getString("id")).put("visible",g.optBoolean("visible",true)));}for(String[] g:GENRES)if(seen.add(g[0]))out.put(new JSONObject().put("id",g[0]).put("visible",true));return out;}
 static String normalized(String tag){return java.text.Normalizer.normalize(tag.trim(),java.text.Normalizer.Form.NFKC).toLowerCase(Locale.ROOT);}
 static boolean contains(JSONArray tags,String word,String source){for(int i=0;i<tags.length();i++){JSONObject t=tags.optJSONObject(i);if(t==null)continue;boolean locked=t.optBoolean("isLocked",false);if(source.equals("locked")&&!locked||source.equals("user")&&locked)continue;if(normalized(t.optString("name")).equals(normalized(word)))return true;}return false;}
 static boolean matches(JSONArray tags,JSONArray conditions){boolean any=false,hasAny=false;for(int i=0;i<conditions.length();i++){JSONObject c=conditions.optJSONObject(i);if(c==null)return false;boolean hit=contains(tags,c.optString("tag"),c.optString("source","both"));String mode=c.optString("mode","all");if(mode.equals("all")&&!hit||mode.equals("exclude")&&hit)return false;if(mode.equals("any")){hasAny=true;any|=hit;}}return !hasAny||any;}
 static void validate(JSONObject preset)throws JSONException{
  if(preset.optString("name").trim().isEmpty())throw new JSONException("名前を入力してください");JSONArray genres=preset.optJSONArray("genres"),rules=preset.optJSONArray("rules");if(genres==null||genres.length()==0)throw new JSONException("ジャンルを選択してください");for(int i=0;i<genres.length();i++)if(!known(genres.optString(i)))throw new JSONException("ジャンルを確認してください");if(rules==null)throw new JSONException("タグ条件を確認してください");for(int i=0;i<rules.length();i++){JSONObject c=rules.optJSONObject(i);if(c==null||c.optString("tag").trim().isEmpty()||!Arrays.asList("all","any","exclude").contains(c.optString("mode"))||!Arrays.asList("locked","user","both").contains(c.optString("source")))throw new JSONException("タグ条件を確認してください");}
 }
}

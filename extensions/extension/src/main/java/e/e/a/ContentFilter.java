package e.e.a;
import android.app.*;import android.content.*;import android.preference.*;import android.view.*;import android.widget.*;import java.util.*;import java.lang.reflect.Method;import org.json.*;
public final class ContentFilter {
 private static final String KEY="nicoid_content_keywords",CHANNELS="nicoid_content_channels",STORE="nicoid_content_rules_v2";
 private static String tr(Context c,String j,String e,String z){return PanelUi.tr(c,j,e,z);}
 public static void settings(PreferenceActivity a){PreferenceGroup root=a.getPreferenceScreen();PreferenceCategory section=(PreferenceCategory)a.findPreference("nicoid_content_filter");if(section==null){section=new PreferenceCategory(a);section.setKey("nicoid_content_filter");int after=root.getPreferenceCount();for(int i=0;i<root.getPreferenceCount();i++){Preference p=root.getPreference(i);p.setOrder(i*2);if("comment".equals(p.getKey()))after=i+1;}section.setOrder(after*2-1);root.addPreference(section);}section.setTitle(tr(a,"その他","Other","其他"));if(a.findPreference("nicoid_filter_list")==null){Preference p=new Preference(a);p.setKey("nicoid_filter_list");p.setTitle(tr(a,"コンテンツフィルター","Content filter","內容篩選"));p.setSummary(stored(a).length()+tr(a,"件：登録した条件に一致する動画を非表示"," rules: hide videos matching registered conditions","項：隱藏符合已登錄條件的影片"));p.setOnPreferenceClickListener(v->{list(a);return true;});section.addPreference(p);}}
 static JSONArray stored(Context c){SharedPreferences p=PreferenceManager.getDefaultSharedPreferences(c);if(p.contains(STORE)){try{return new JSONArray(p.getString(STORE,"[]"));}catch(Exception e){return new JSONArray();}}JSONArray a=new JSONArray();try{for(int cat=0;cat<2;cat++)for(String s:ContentFilterRules.keywords(p.getString(cat==0?KEY:CHANNELS,"")))a.put(new JSONObject().put("category",cat).put("value",s).put("mode","partial").put("enabled",true).put("date",0));}catch(Exception e){}save(c,a);return a;}
 static void save(Context c,JSONArray a){PreferenceManager.getDefaultSharedPreferences(c).edit().putString(STORE,a.toString()).apply();compiled=null;}
 public static final class Rules {final String snapshot;final ArrayList<ContentFilterRules.Rule> words=new ArrayList<>(),names=new ArrayList<>();Rules(String s){snapshot=s;try{JSONArray a=new JSONArray(s);for(int i=0;i<a.length();i++){JSONObject o=a.optJSONObject(i);if(o==null||o.optString("value").isEmpty())continue;try{(o.optInt("category")==0?words:names).add(new ContentFilterRules.Rule(o.optString("value"),o.optString("mode","partial"),o.optBoolean("enabled",true)));}catch(java.util.regex.PatternSyntaxException ignored){}}}catch(Exception ignored){}}Rules(String a,String b){this(legacy(a,b));}static String legacy(String a,String b){JSONArray rows=new JSONArray();try{for(int cat=0;cat<2;cat++)for(String s:ContentFilterRules.keywords(cat==0?a:b))rows.put(new JSONObject().put("category",cat).put("value",s));}catch(Exception ignored){}return rows.toString();}public boolean blocked(String title,String channel){for(ContentFilterRules.Rule r:words)if(r.matches(title))return true;for(ContentFilterRules.Rule r:names)if(r.matches(channel))return true;return false;}boolean empty(){return words.isEmpty()&&names.isEmpty();}}
 public static void confirmOwner(Context host,String owner){if(owner==null||owner.trim().isEmpty()){Toast.makeText(host,tr(host,"投稿者名を取得できませんでした","Uploader name unavailable","無法取得投稿者名稱"),0).show();return;}Context context=UiDialogs.owner(host);if(!(context instanceof Activity))return;JSONArray rows=stored(host);JSONObject target=null;for(int n=0;n<rows.length();n++){JSONObject item=rows.optJSONObject(n);if(item!=null&&item.optInt("category")==1&&owner.equals(item.optString("value"))&&"exact".equals(item.optString("mode"))){target=item;break;}}try{if(target==null){target=new JSONObject().put("category",1).put("value",owner).put("mode","exact").put("enabled",true).put("date",System.currentTimeMillis());rows.put(target);}FilterUi110.edit((Activity)context,rows,target,1,()->Toast.makeText(host,tr(host,"NGリストに保存しました","Saved to NG list","已儲存至 NG 清單"),0).show());}catch(JSONException error){android.util.Log.w("nicoid-filter","Unable to prepare rule editor",error);}}
 private static volatile Rules compiled;
 public static Rules rules(Context c){SharedPreferences p=PreferenceManager.getDefaultSharedPreferences(c);String s=p.contains(STORE)?p.getString(STORE,"[]"):stored(c).toString();Rules r=compiled;if(r!=null&&r.snapshot.equals(s))return r;return compiled=new Rules(s);}
 public static boolean blocked(Context c,String title){return blocked(c,title,null);}public static boolean blocked(Context c,String title,String channel){return rules(c).blocked(title,channel);}
 private static String category(Context c,int n){return n==0?tr(c,"動画タイトル","Video titles","影片標題"):tr(c,"投稿者・チャンネル名","Uploaders / channels","投稿者／頻道名稱");}
 private static String mode(Context c,String s){return s.equals("regex")?tr(c,"正規表現","Regular expression","正規表示式"):s.equals("exact")?tr(c,"完全一致","Exact match","完全相符"):tr(c,"部分一致","Partial match","部分相符");}
 private static void list(PreferenceActivity a){FilterUi110.show(a);}
    private static volatile Method rowValue;
    private static volatile java.lang.reflect.Field rowOwner;
    /** Same owner-name sources used by normal video rows, including channel videos. */
    public static String owner(JSONObject row) {
        if (row == null) return "";
        PaidVideos.remember(row);
        for (String key : new String[]{"owner", "user", "channel"}) {
            JSONObject source = row.optJSONObject(key);
            if (source == null) continue;
            for (String name : new String[]{"nickname", "name"}) {
                String value = source.optString(name, "");
                if (!value.isEmpty()) return value;
            }
            String nested = owner(source);
            if (!nested.isEmpty()) return nested;
        }
        String value = row.optString("ownerName", "");
        return value.isEmpty() ? row.optString("uploaderName", "") : value;
    }
    public static void rememberHistory(JSONObject record) {
        HistorySupport.enrichCached(record);
        try {
            JSONObject watch = (JSONObject) Class.forName("e.e.a.ModernPlayback").getField("latestWatch").get(null);
            JSONObject video = watch == null ? null : watch.optJSONObject("video");
            if (video != null && HistoryRules.same(record.optString("videourl"), video.optString("id"))) {
                record.put("isPaymentRequired", PaidVideos.watchRequired(watch));
                PaidVideos.remember(record);
                String name = owner(watch);
                if (!name.isEmpty()) record.put("ownerName", name);
                if(video.has("registeredAt"))record.put("postedAt",video.optString("registeredAt"));
                JSONObject counts=video.optJSONObject("count");if(counts!=null)record.put("videoCounts",counts);
            }
        } catch (ReflectiveOperationException | org.json.JSONException ignored) { }
    }
    public static void restoreHistory(Object row, JSONObject record) {
        PaidVideos.remember(record);
        HistorySupport.captureLocal(row,record);
        try { row.getClass().getField("y").set(row, owner(record)); }
        catch (ReflectiveOperationException error) { throw new IllegalStateException("Unsupported history row", error); }
    }
    /** The adapter and fragment share this list, preserving click and selection indices. */
    public static void filter(Object adapter) {
        try {
            Class<?> type = adapter.getClass();
            Context context = (Context) type.getField("d").get(adapter);
            Rules rules = rules(context);
            if (rules.empty()) return;
            ArrayList<?> rows = (ArrayList<?>) type.getField("b").get(adapter);
            Method value = rowValue; java.lang.reflect.Field owner = rowOwner;
            if (value == null || owner == null) {
                Class<?> rowType = Class.forName("e.e.a.x1");
                value = rowType.getMethod("a", String.class); owner = rowType.getField("y");
                rowValue = value; rowOwner = owner;
            }
            for (int n = rows.size() - 1; n >= 0; n--) {
                Object row = rows.get(n);
                Object title = value.invoke(row, "title");
                Object url = value.invoke(row, "videourl");
                Object channel = owner.get(row);
                if (url != null && (url.toString().contains("/watch/") || url.toString().contains("/shorts/")) &&
                    rules.blocked(title == null ? null : title.toString(), channel == null ? null : channel.toString())) rows.remove(n);
            }
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Unsupported video list", error);
        }
    }
}

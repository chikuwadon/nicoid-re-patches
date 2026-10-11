package e.e.a;
import org.json.JSONObject;
/** Video payloads differ between uploaded-video and playlist endpoints. */
final class CreatorData {
 static JSONObject video(JSONObject item){if(item==null)return null;for(String key:new String[]{"essential","content","video"}){JSONObject video=item.optJSONObject(key);if(video!=null)return video;}return item;}
}

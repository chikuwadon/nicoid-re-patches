package e.e.a;
import org.json.JSONObject;
public class CreatorDataTest {
 public static void main(String[] args)throws Exception{
 JSONObject upload=new JSONObject("{\"series\":{\"id\":12},\"essential\":{\"id\":\"ss123\",\"title\":\"Short\",\"registeredAt\":\"2026-10-10T12:00:00+09:00\",\"count\":{\"view\":12,\"comment\":2},\"thumbnail\":{\"url\":\"https://example.invalid/short.jpg\"}}}");
 JSONObject video=CreatorData.video(upload);if(!video.getString("id").equals("ss123")||video.getJSONObject("count").getInt("view")!=12||!video.has("registeredAt")||!video.has("thumbnail"))throw new AssertionError("Uploaded Shorts must retain metadata");
 JSONObject playlist=new JSONObject("{\"content\":{\"id\":\"sm456\",\"count\":{\"like\":3}}}");if(!CreatorData.video(playlist).getString("id").equals("sm456"))throw new AssertionError("Playlist wrapper");
 JSONObject direct=new JSONObject("{\"id\":\"ss789\"}");if(CreatorData.video(direct)!=direct||CreatorData.video(null)!=null)throw new AssertionError("Direct and absent rows");
 System.out.println("Creator payload fixtures passed");
 }
}

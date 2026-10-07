package e.e.a;
import org.json.JSONObject;
import java.util.ArrayList;

public final class FollowFeedDataTest {
    private static void check(boolean value,String message){if(!value)throw new AssertionError(message);}
    public static void main(String[] args)throws Exception{
        JSONObject page=new JSONObject("{\"code\":\"ok\",\"activities\":["+
            "{\"createdAt\":\"2026-10-07T10:00:00+09:00\",\"actor\":{\"name\":\"投稿者\"},\"thumbnailUrl\":\"https://example.com/image\",\"content\":{\"type\":\"video\",\"id\":\"sm9\",\"title\":\"動画\"}},"+
            "{\"content\":{\"type\":\"live\",\"id\":\"lv123\"}},"+
            "{\"content\":{\"type\":\"video\",\"id\":\"https://example.com\"}},null,"+
            "{\"content\":{\"type\":\"video\",\"id\":\"so123\"}}]}");
        ArrayList<FollowFeedData.Item> items=FollowFeedData.parse(page);
        check(items.size()==2,"ignore unrelated and malformed entries");
        check(items.get(0).title.equals("動画")&&items.get(0).actor.equals("投稿者"),"retain Unicode labels");
        check(items.get(0).thumbnail.equals("https://example.com/image"),"retain thumbnail URL");
        check(items.get(1).title.equals("so123")&&items.get(1).actor.isEmpty(),"missing optional fields");
        check(FollowFeedData.parse(new JSONObject("{\"activities\":[]}")).isEmpty(),"empty timeline");
        check(FollowFeedData.parse(new JSONObject()).isEmpty(),"missing array");
        System.out.println("Follow feed parsing checks passed");
    }
}

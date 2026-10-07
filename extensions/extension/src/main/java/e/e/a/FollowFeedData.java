package e.e.a;
import java.util.ArrayList;
import org.json.*;

/** Parses only video upload entries; malformed and unrelated events are ignored. */
final class FollowFeedData {
    static final class Item {
        final String id,title,thumbnail,actor,date;
        Item(String id,String title,String thumbnail,String actor,String date) {
            this.id=id;this.title=title;this.thumbnail=thumbnail;this.actor=actor;this.date=date;
        }
    }
    static ArrayList<Item> parse(JSONObject response) {
        ArrayList<Item> result=new ArrayList<>();JSONArray activities=response.optJSONArray("activities");if(activities==null)return result;
        for(int i=0;i<activities.length();i++){
            JSONObject activity=activities.optJSONObject(i);if(activity==null)continue;
            JSONObject content=activity.optJSONObject("content");if(content==null||!"video".equals(content.optString("type")))continue;
            String id=content.optString("id","");if(!id.matches("(?:sm|nm|so|ss)?[0-9]+"))continue;
            JSONObject actor=activity.optJSONObject("actor");
            result.add(new Item(id,content.optString("title",id),activity.optString("thumbnailUrl",""),actor==null?"":actor.optString("name",""),activity.optString("createdAt","")));
        }
        return result;
    }
}

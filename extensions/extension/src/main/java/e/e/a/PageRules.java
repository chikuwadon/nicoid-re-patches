package e.e.a;
import org.json.JSONObject;
public final class PageRules {
    private PageRules() {}
    public static boolean more(JSONObject data,int count,int page,int size) {
        if(count==0)return false;
        if(data.has("hasNext"))return data.optBoolean("hasNext");
        long total=data.optLong("totalCount",data.optLong("total",-1));
        if(total>=0)return (long)page*size<total;
        return count==size;
    }
}

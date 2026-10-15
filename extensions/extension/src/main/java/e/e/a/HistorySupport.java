package e.e.a;

import android.content.Context;
import org.json.JSONArray;

public final class HistorySupport {
    private static final java.util.Map<Object,String> OWNERS=java.util.Collections.synchronizedMap(new java.util.WeakHashMap<Object,String>());
    private static final java.util.Map<Object,Record> RECORDS=java.util.Collections.synchronizedMap(new java.util.WeakHashMap<Object,Record>());
    static final class Record {long watched,posted,views;String postedText="";long[] counts;}
    private HistorySupport() { }
    private static final java.util.LinkedHashMap<String,org.json.JSONObject> CACHED=new java.util.LinkedHashMap<>();
    static void rememberCached(android.os.Bundle data){String id=data.getString("videoId");if(id==null)return;org.json.JSONObject record=new org.json.JSONObject();try{String posted=data.getString("postedAt");if(posted==null)posted=data.getString("first_retrieve");if(posted!=null)record.put("postedAt",posted);for(String key:new String[]{"ownerName","uploaderName","userNickname"}){String owner=data.getString(key);if(owner!=null&&!owner.isEmpty()){record.put("ownerName",owner);break;}}synchronized(CACHED){CACHED.put(id,record);while(CACHED.size()>128)CACHED.remove(CACHED.keySet().iterator().next());}}catch(org.json.JSONException ignored){}}
    static void enrichCached(org.json.JSONObject record){String id=HistoryRules.id(record.optString("videourl"));synchronized(CACHED){org.json.JSONObject cached=CACHED.get(id);if(cached==null)return;for(String key:new String[]{"postedAt","ownerName"})if(!record.has(key)&&cached.has(key))try{record.put(key,cached.optString(key));}catch(org.json.JSONException ignored){}}}

    public static void capture(Object row,org.json.JSONObject video){String name="";for(String key:new String[]{"owner","user","channel"}){org.json.JSONObject o=video.optJSONObject(key);if(o!=null){name=o.optString("nickname",o.optString("name", ""));if(name.isEmpty()&&o.optJSONObject("user")!=null)name=o.optJSONObject("user").optString("nickname");if(!name.isEmpty())break;}}if(name.isEmpty())name=video.optString("ownerName",video.optString("uploaderName", ""));OWNERS.put(row,name);Record record=RECORDS.get(row);if(record==null){record=new Record();RECORDS.put(row,record);}record.postedText=video.optString("registeredAt",video.optString("postedAt",video.optString("first_retrieve","")));record.posted=VideoRows191.millis(record.postedText);org.json.JSONObject count=video.optJSONObject("count");if(count!=null)record.counts=statistics(count);}

    public static void capture(Object row,org.json.JSONObject video,org.json.JSONObject record){capture(row,video);String name=OWNERS.get(row);if((name==null||name.isEmpty())&&record!=null)OWNERS.put(row,ContentFilter.owner(record));Record data=RECORDS.get(row);if(record!=null)data.watched=HistorySortRules.watched(record.optString("viewedAt"));}
    public static void captureLocal(Object row,org.json.JSONObject source){capture(row,source);Record data=RECORDS.get(row);data.watched=source.optLong("posttime");data.views=source.optLong("viewCount");org.json.JSONObject count=source.optJSONObject("videoCounts");if(count!=null)data.counts=statistics(count);}
    static long[] statistics(org.json.JSONObject count){return VideoInfoCounts.read(key->{String stat=key.equals("viewCount")?"view":key.equals("commentCount")?"comment":key.equals("likeCount")?"like":"mylist";return count.isNull(stat)?null:count.optString(stat,null);});}
    static String owner(Object row){return OWNERS.get(row);}
    static long[] counts(Object row){Record data=RECORDS.get(row);return data==null?null:data.counts;}
    static long watched(Object row){Record data=RECORDS.get(row);return data!=null&&data.watched>0?data.watched:HistorySortRules.watched(ListActions.value(row,"posttime"));}
    static long posted(Object row){Record data=RECORDS.get(row);return data!=null?data.posted:HistorySortRules.watched(ListActions.value(row,"postedAt"));}
    static long views(Object row){Record data=RECORDS.get(row);return data==null?0:data.views;}

    public static String format(String ignored, Object[] values) {
        return String.format(UiStrings.translate("<b>%s</b> <font color='red'>%s回視聴</font>"), values);
    }
    public static void bindAccount(android.view.View root, Object adapter, Object row) throws ReflectiveOperationException {
        int type=adapter.getClass().getField("e").getInt(adapter);if(type!=4&&type!=8){android.view.View prior=root.findViewWithTag("nicoid_history_posted");if(prior!=null)prior.setVisibility(android.view.View.GONE);return;}
        java.lang.reflect.Method get=row.getClass().getMethod("a",String.class);
        Object watched=get.invoke(row,"posttime"),info=get.invoke(row,"videoinfo");
        android.widget.TextView date=(android.widget.TextView)root.findViewById(0x7f08015e),statistics=(android.widget.TextView)root.findViewById(0x7f0801d3),ownerView=(android.widget.TextView)root.findViewById(0x7f0801e2);
        if(date!=null&&watched!=null){String text=watched.toString();if(type==4)text=HistoryRules.accountViewedAt(text,UiStrings.translate(root.getContext().getString(0x7f0f0215)));date.setText(text);date.setContentDescription(null);}
        String owner=OWNERS.get(row);if(owner==null||owner.isEmpty())try{owner=String.valueOf(row.getClass().getField("y").get(row));if(owner.equals("null"))owner="";}catch(Exception ignored){owner="";}
        if(ownerView!=null){ownerView.setText(owner);ownerView.setVisibility(owner.isEmpty()?android.view.View.GONE:android.view.View.VISIBLE);}
        android.view.View postedView=root.findViewWithTag("nicoid_history_posted");android.widget.TextView posted;
        if(postedView instanceof android.widget.TextView)posted=(android.widget.TextView)postedView;
        else{posted=new android.widget.TextView(root.getContext());posted.setTag("nicoid_history_posted");posted.setTextSize(12);if(statistics!=null&&statistics.getParent() instanceof android.view.ViewGroup){android.view.ViewGroup parent=(android.view.ViewGroup)statistics.getParent();parent.addView(posted,parent.indexOfChild(statistics)+1,new android.view.ViewGroup.LayoutParams(-1,-2));}}
        if(statistics==null||info==null){posted.setVisibility(android.view.View.GONE);return;}
        String source=info.toString();int newline=source.indexOf('\n');String counts=newline<0?source:source.substring(0,newline);VideoCounts.setText(statistics,counts);
        Record data=RECORDS.get(row);String text=data==null?"":data.postedText;
        if(text.isEmpty())text=ListActions.value(row,"postedAt");
        if(text.isEmpty()&&type==4&&newline>=0)text=source.substring(newline+1).trim();
        long millis=HistorySortRules.watched(text);if(millis>0){java.text.SimpleDateFormat format=new java.text.SimpleDateFormat(UiStrings.translate(root.getContext().getString(0x7f0f004d)),java.util.Locale.getDefault());format.setTimeZone(java.util.TimeZone.getTimeZone("Asia/Tokyo"));text=format.format(new java.util.Date(millis))+" "+UiStrings.translate("投稿");}
        posted.setText(text);posted.setTextColor(statistics.getCurrentTextColor());posted.setVisibility(text.isEmpty()?android.view.View.GONE:android.view.View.VISIBLE);
    }
    public static int write(int type, JSONArray data, Context context) throws Exception {
        int result = (Integer) Class.forName("e.e.a.v0").getMethod("a", int.class, JSONArray.class, Context.class)
            .invoke(null, type, data, context);
        if (result != 1) {
            android.widget.Toast.makeText(context, UiStrings.translate("履歴を削除できませんでした。再試行してください。"), android.widget.Toast.LENGTH_LONG).show();
            throw new java.io.IOException("History could not be saved");
        }
        return result;
    }
}

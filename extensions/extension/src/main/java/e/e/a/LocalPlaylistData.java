package e.e.a;
import org.json.*;import java.util.*;
/** Account-independent folders with stable identities, persisted in one atomic snapshot. */
public final class LocalPlaylistData {
 public final JSONObject data;public final JSONArray folders,videos,groups;
 public LocalPlaylistData(String json)throws JSONException{
  data=new JSONObject(json);folders=array(data,"folders");videos=array(data,"videos");groups=array(data,"groups");
  Set<String> groupIds=new HashSet<>();for(int n=0;n<groups.length();n++){JSONObject g=object(groups,n);required(g,"name");if(!groupIds.add(required(g,"id")))throw new JSONException("Duplicate group");}
  Set<String> folderIds=new HashSet<>(),videoIds=new HashSet<>();
  for(int n=0;n<folders.length();n++){JSONObject f=object(folders,n);String id=required(f,"id");required(f,"name");String group=f.optString("group","");if(!group.isEmpty()&&!groupIds.contains(group))throw new JSONException("Missing group");if(!folderIds.add(id))throw new JSONException("Duplicate folder identity");}
  for(int n=0;n<videos.length();n++){JSONObject v=object(videos,n);String id=required(v,"id");String videoId=required(v,"videoId"),url=required(v,"url");if(!videoId.matches("(?:sm|nm|so|ss)?[0-9]+")||!videoId.equals(HistoryRules.id(url)))throw new JSONException("Invalid playlist video");if(!videoIds.add(id))throw new JSONException("Duplicate video identity");String folder=v.optString("folder","");if(!folder.isEmpty()&&!folderIds.contains(folder))throw new JSONException("Missing folder identity");for(String key:new String[]{"folder","title","owner","thumbnail","length","videoinfo","posttime"})if(v.has(key)&&!(v.get(key) instanceof String))throw new JSONException("Invalid video field");}
  data.put("folders",folders).put("videos",videos).put("groups",groups);
 }
 private static JSONArray array(JSONObject data,String key)throws JSONException{if(!data.has(key))return new JSONArray();Object value=data.get(key);if(!(value instanceof JSONArray))throw new JSONException("Invalid playlist array");return (JSONArray)value;}
 private static JSONObject object(JSONArray array,int n)throws JSONException{Object value=array.get(n);if(!(value instanceof JSONObject))throw new JSONException("Invalid playlist entry");return (JSONObject)value;}
 private static String required(JSONObject data,String key)throws JSONException{Object value=data.get(key);if(!(value instanceof String)||((String)value).isEmpty())throw new JSONException("Invalid playlist identity");return (String)value;}

 public String folder(String name)throws JSONException{String id=UUID.randomUUID().toString();folders.put(new JSONObject().put("id",id).put("name",name));return id;}
 public void removeFolder(String id){if(id==null||id.isEmpty())return;for(int n=folders.length()-1;n>=0;n--)if(id.equals(folders.optJSONObject(n).optString("id")))folders.remove(n);for(int n=0;n<videos.length();n++){JSONObject video=videos.optJSONObject(n);if(video!=null&&id.equals(video.optString("folder")))video.remove("folder");}}
 public boolean hasFolder(String id){if(id==null)return false;if(id.isEmpty())return true;for(int n=0;n<folders.length();n++)if(id.equals(folders.optJSONObject(n).optString("id")))return true;return false;}
 public void add(String folder,String url,String title,String owner,String image)throws JSONException{if(!hasFolder(folder))throw new JSONException("Missing folder");String id=HistoryRules.id(url);if(id==null||!id.matches("(?:sm|nm|so|ss)?[0-9]+"))throw new JSONException("Invalid video");for(int n=0;n<videos.length();n++){JSONObject v=videos.getJSONObject(n);if(folder.equals(v.optString("folder"))&&id.equals(v.optString("videoId")))return;}videos.put(new JSONObject().put("id",UUID.randomUUID().toString()).put("folder",folder).put("videoId",id).put("url","https://www.nicovideo.jp/watch/"+id).put("title",title).put("owner",owner).put("thumbnail",image).put("date",System.currentTimeMillis()));}
 public void delete(Set<String> ids){for(int n=videos.length()-1;n>=0;n--)if(ids.contains(videos.optJSONObject(n).optString("id")))videos.remove(n);}
 public void move(Set<String> ids,String folder)throws JSONException{if(!hasFolder(folder))throw new JSONException("Missing folder");Set<String> existing=new HashSet<>();for(int n=0;n<videos.length();n++){JSONObject v=videos.getJSONObject(n);if(folder.equals(v.optString("folder"))&&!ids.contains(v.optString("id")))existing.add(v.optString("videoId"));}for(int n=videos.length()-1;n>=0;n--){JSONObject v=videos.getJSONObject(n);if(ids.contains(v.optString("id"))){if(!existing.add(v.optString("videoId")))videos.remove(n);else v.put("folder",folder);}}}
 public boolean hasGroup(String id){if(id==null)return false;if(id.isEmpty())return true;for(int n=0;n<groups.length();n++)if(id.equals(groups.optJSONObject(n).optString("id")))return true;return false;}
 public String group(String name)throws JSONException{String id=UUID.randomUUID().toString();groups.put(new JSONObject().put("id",id).put("name",name));return id;}
 public void movePlaylist(String id,String group)throws JSONException{if(!hasGroup(group))throw new JSONException("Missing group");for(int n=0;n<folders.length();n++)if(id.equals(folders.getJSONObject(n).optString("id")))folders.getJSONObject(n).put("group",group);}
 public void removeGroup(String id)throws JSONException{for(int n=groups.length()-1;n>=0;n--)if(id.equals(groups.getJSONObject(n).optString("id")))groups.remove(n);for(int n=0;n<folders.length();n++)if(id.equals(folders.getJSONObject(n).optString("group")))folders.getJSONObject(n).remove("group");}
 public String toString(){return data.toString();}
}

package e.e.a;
import java.util.*;
/** Legacy snapshots survive grouping, moves and folder removal without losing videos. */
public final class PlaylistGroups110Test {
 static int checks;static void check(boolean ok,String why){checks++;if(!ok)throw new AssertionError(why);}
 public static void main(String[] args)throws Exception{
  LocalPlaylistData old=new LocalPlaylistData("{}");String playlist=old.folder("Music");old.add(playlist,"sm123","Title","Owner","");old.add("","sm456","Loose","","");old.data.remove("groups");String legacy=old.toString();LocalPlaylistData data=new LocalPlaylistData(legacy);
  check(data.groups.length()==0&&data.folders.length()==1&&data.videos.length()==2,"legacy data retained");String group=data.group("Favorites");data.movePlaylist(playlist,group);LocalPlaylistData restored=new LocalPlaylistData(data.toString());check(restored.groups.length()==1,"folder persisted");check(group.equals(restored.folders.getJSONObject(0).getString("group")),"playlist membership persisted");check(restored.videos.getJSONObject(0).getString("folder").equals(playlist),"grouping leaves video ownership unchanged");
  String before=restored.toString();try{restored.movePlaylist(playlist,"missing");throw new AssertionError();}catch(org.json.JSONException expected){}check(before.equals(restored.toString()),"invalid move is non-destructive");restored.removeGroup(group);check(restored.groups.length()==0&&restored.folders.length()==1&&restored.videos.length()==2,"folder removal preserves playlists and videos");check(restored.folders.getJSONObject(0).optString("group").isEmpty(),"removed folder returns playlist to root");restored.removeFolder(playlist);check(restored.videos.length()==2&&restored.videos.getJSONObject(0).optString("folder").isEmpty(),"playlist removal retains videos in unfiled");new LocalPlaylistData(restored.toString());
  for(String invalid:new String[]{"{\"groups\":false}","{\"groups\":[null]}","{\"groups\":[{\"id\":\"a\",\"name\":\"A\"},{\"id\":\"a\",\"name\":\"B\"}]}","{\"folders\":[{\"id\":\"p\",\"name\":\"P\",\"group\":\"absent\"}]}"}){try{new LocalPlaylistData(invalid);throw new AssertionError("bad group accepted");}catch(org.json.JSONException expected){checks++;}}
  System.out.println("Playlist grouping: "+checks+" checks passed");
 }
}

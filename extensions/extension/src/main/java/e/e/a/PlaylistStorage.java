package e.e.a;
import java.io.IOException;
/** Serial read-modify-write transactions preserve independent queued edits. */
final class PlaylistStorage {
 interface Persistence {String read();boolean write(String snapshot);void restore(String snapshot);}
 interface Edit {void apply(LocalPlaylistData data)throws Exception;}
 static synchronized boolean update(Persistence storage,Edit edit)throws Exception{
  String before=storage.read();LocalPlaylistData data=new LocalPlaylistData(before);edit.apply(data);
  // Validate the new snapshot too, before writing any persisted value.
  String after=new LocalPlaylistData(data.toString()).toString();
  if(before.equals(after))return true;
  boolean ok=false;try{ok=storage.write(after);return ok;}finally{if(!ok)storage.restore(before);}
 }
}

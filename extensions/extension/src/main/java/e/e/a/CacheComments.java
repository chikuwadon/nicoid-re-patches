package e.e.a;
import java.io.*;import java.util.UUID;import java.util.zip.*;
/** Only video cache owns persistent comments. Ordinary online viewing leaves no files. */
public final class CacheComments {
 static final int LIMIT=2*1024*1024;
 private static final Object[] LOCKS=new Object[32];
 static {for(int n=0;n<LOCKS.length;n++)LOCKS[n]=new Object();}
 private static Object lock(String folder,String id){return LOCKS[((folder+"/"+id).hashCode()&0x7fffffff)%LOCKS.length];}
 static boolean video(String folder,String id){return new CacheFile(folder,id+".mp4").isFile()||new CacheFile(folder,id+".ncache").isFile()||new CacheFile(folder,id+".m3u8").isFile();}
 public static void save(String folder,String id,String json){
  if(json==null||!json.contains("\"threads\""))return;
  synchronized(lock(folder,id)){
   if(!video(folder,id))return;
   File temp=new CacheFile(folder,id+".comments."+UUID.randomUUID()+".partial"),dest=new CacheFile(folder,id+".comments.gz"),backup=new CacheFile(folder,id+".comments.backup");boolean backed=false;
   try{
    byte[] bytes=json.getBytes("UTF-8");if(bytes.length>LIMIT)return;
    try(OutputStream out=new GZIPOutputStream(new CacheOutputStream(temp))){out.write(bytes);}
    // Recover an interrupted swap before starting another one. Keep a readable backup on failure.
    if(backup.exists()){
     if(readFile(dest,true)==null){if(dest.exists()&&!dest.delete())throw new IOException("Cannot remove incomplete comments");if(!backup.renameTo(dest))throw new IOException("Cannot restore comments");}
     else if(!backup.delete())throw new IOException("Cannot remove previous comment backup");
    }
    if(dest.exists()){if(!dest.renameTo(backup))throw new IOException("Cannot back up comments");backed=true;}
    if(!temp.renameTo(dest)){if(backed)backup.renameTo(dest);throw new IOException("Cannot finalize comments");}
    backup.delete();new CacheFile(folder,id+".comments.json").delete();
   }catch(IOException|RuntimeException e){android.util.Log.w("nicoid-cache","Cannot save comments",e);}
   finally{temp.delete();}
  }
 }
 public static String read(String folder,String id){synchronized(lock(folder,id)){
  String json=readFile(new CacheFile(folder,id+".comments.gz"),true);
  if(json==null)json=readFile(new CacheFile(folder,id+".comments.backup"),true);
  return json!=null?json:readFile(new CacheFile(folder,id+".comments.json"),false);
 }}
 static String readFile(File file,boolean compressed){if(!file.isFile())return null;try(InputStream raw=new CacheInputStream(file);InputStream in=compressed?new GZIPInputStream(raw):raw;ByteArrayOutputStream out=new ByteArrayOutputStream()){byte[] b=new byte[4096];int n;while((n=in.read(b))!=-1){if(out.size()+n>LIMIT)return null;out.write(b,0,n);}return out.toString("UTF-8");}catch(IOException|RuntimeException e){return null;}}
 static boolean copyable(File source){String name=source.getName();if(name.contains(".comments.")){String id=CacheFolders.videoId(name);return id!=null&&(name.endsWith(".comments.json")||name.endsWith(".comments.gz"))&&video(source.getParent(),id);}return !name.contains(".copy-")&&!name.endsWith(".partial");}
 static void clean(String folder,File[] files){for(File file:files){String name=file.getName();if(!name.contains(".comments."))continue;String id=CacheFolders.videoId(name);if(id==null)continue;synchronized(lock(folder,id)){
  if(!video(folder,id)){file.delete();continue;}
  if(name.endsWith(".partial")){if(System.currentTimeMillis()-file.lastModified()>24L*60*60*1000)file.delete();continue;}
  if(name.endsWith(".comments.backup")){File dest=new CacheFile(folder,id+".comments.gz");if(readFile(dest,true)!=null)file.delete();else if(readFile(file,true)!=null){if(!dest.exists()||dest.delete())file.renameTo(dest);}continue;}
  if(name.endsWith(".comments.json")){File zipped=new CacheFile(folder,id+".comments.gz");if(readFile(zipped,true)!=null)file.delete();else{String json=readFile(file,false);if(json!=null)save(folder,id,json);}}
 }}}
}

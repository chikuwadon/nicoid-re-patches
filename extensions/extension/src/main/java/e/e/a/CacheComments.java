package e.e.a;
import java.io.*;import java.util.zip.*;
/** Only video cache owns persistent comments. Ordinary online viewing leaves no files. */
public final class CacheComments {
 static final int LIMIT=2*1024*1024;
 static boolean video(String folder,String id){return new CacheFile(folder,id+".mp4").isFile()||new CacheFile(folder,id+".ncache").isFile()||new CacheFile(folder,id+".m3u8").isFile();}
 public static void save(String folder,String id,String json){if(json==null||!json.contains("\"threads\"")||!video(folder,id))return;File temp=new CacheFile(folder,id+".comments.partial"),dest=new CacheFile(folder,id+".comments.gz");try{byte[] bytes=json.getBytes("UTF-8");if(bytes.length>LIMIT)return;try(OutputStream out=new GZIPOutputStream(new CacheOutputStream(temp))){out.write(bytes);}if(dest.exists()&&!dest.delete())throw new IOException("Cannot replace comments");if(!temp.renameTo(dest))throw new IOException("Cannot finalize comments");new CacheFile(folder,id+".comments.json").delete();}catch(IOException e){android.util.Log.w("nicoid-cache","Cannot save comments",e);}finally{temp.delete();}}
 public static String read(String folder,String id){
  String json=readFile(new CacheFile(folder,id+".comments.gz"),true);
  return json!=null?json:readFile(new CacheFile(folder,id+".comments.json"),false);
 }
 static String readFile(File file,boolean compressed){if(!file.isFile())return null;try(InputStream raw=new CacheInputStream(file);InputStream in=compressed?new GZIPInputStream(raw):raw;ByteArrayOutputStream out=new ByteArrayOutputStream()){byte[] b=new byte[4096];int n;while((n=in.read(b))!=-1){if(out.size()+n>LIMIT)return null;out.write(b,0,n);}return out.toString("UTF-8");}catch(IOException e){return null;}}
 static boolean copyable(File source){String name=source.getName();if(name.endsWith(".comments.json")||name.endsWith(".comments.gz")||name.endsWith(".comments.partial")){String id=CacheFolders.videoId(name);return id!=null&&!name.endsWith(".partial")&&video(source.getParent(),id);}return !name.contains(".copy-")&&!name.endsWith(".partial");}
 static void clean(String folder,File[] files){for(File file:files){String name=file.getName();if(!(name.endsWith(".comments.json")||name.endsWith(".comments.gz")))continue;String id=CacheFolders.videoId(name);if(id==null)continue;if(!video(folder,id))file.delete();else if(name.endsWith(".comments.json")){File zipped=new CacheFile(folder,id+".comments.gz");if(readFile(zipped,true)!=null)file.delete();else{String json=readFile(file,false);if(json!=null)save(folder,id,json);}}}}
}

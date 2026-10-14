package e.e.a;
import android.content.*;import android.os.*;import java.io.*;import java.lang.reflect.*;import java.util.*;import org.json.*;
/** Local metadata and media are restored together without a watch-page request. */
public final class CachedPlayback {
 private static final Handler MAIN=new Handler(Looper.getMainLooper());private static final Set<Object> WAITING=Collections.newSetFromMap(new WeakHashMap<Object,Boolean>());
 private CachedPlayback(){}
 public static String path(Context context,String value){String id=HistoryRules.id(value);if(!id.matches("(?:sm|nm|so|ss)[0-9]+"))return null;String root=CacheFolders.root(context)+"/nicoid/nicoid_cache/";if(completedBytes(root,id)<=0||!new CacheFile(root,id+".json").isFile())return null;File playlist=new CacheFile(root,id+".m3u8");return CachePlayback.url(new CacheFile(root,id+(playlist.isFile()?".m3u8":".mp4")).getAbsolutePath());}
 public static long completedBytes(String folder,String id){try{
  File marker=new CacheFile(folder,id+".mp4"),playlist=new CacheFile(folder,id+".m3u8"),packed=new CacheFile(folder,id+".ncache");
  if(packed.isFile()&&packed.length()>0&&CachePack.entry(playlist)!=null)return packed.length();
  if(!marker.isFile())return -1;
  if(playlist.isFile()){long bytes=0;File[] files=new CacheFile(folder).listFiles();if(files!=null)for(File f:files)if(f.getName().startsWith(id+"_asset_"))bytes+=f.length();return bytes>0?bytes:-1;}
  if(marker.length()>0&&marker.length()<1024){byte[] header=new byte[16];try(InputStream in=new CacheInputStream(marker)){int n=in.read(header);if(n>0&&new String(header,0,n,"UTF-8").startsWith("HLS cache:"))return -1;}}
  return marker.length()>0?marker.length():-1;
 }catch(Exception e){return -1;}}

 private static final java.util.concurrent.ExecutorService ACQUIRE=java.util.concurrent.Executors.newSingleThreadExecutor();
 private static final Set<String> REQUESTED=new HashSet<>();
 /** Called only after a network stream is ready; cache completion never calls back into this player. */
 public static void cacheWhilePlaying(Object model,String value){try{
  if(Boolean.TRUE.equals(field(model,"r").get(model))||field(model,"a").getBoolean(model))return;
  Context c=(Context)field(model,"K").get(model);if(!CachePlaybackPolicy.acquire(android.preference.PreferenceManager.getDefaultSharedPreferences(c).getString("play_mode","0")))return;
  String id=HistoryRules.id(value);if(id==null||!id.matches("(?:sm|nm|so|ss)[0-9]+")||path(c,id)!=null)return;
  synchronized(REQUESTED){if(REQUESTED.size()>=32||!REQUESTED.add(id))return;}
  ACQUIRE.execute(()->{boolean posted=false;try{Class<?> service=Class.forName("com.sauzask.nicoid.NicoidDownloadCache");long began=SystemClock.elapsedRealtime();while(service.getField("v").getBoolean(null)){if(id.equals(String.valueOf(service.getField("t").get(null)))||field(model,"a").getBoolean(model)||Thread.currentThread().isInterrupted())return;if(SystemClock.elapsedRealtime()-began>30*60*1000)throw new IOException("Cache queue timed out");Thread.sleep(250);}
   if(field(model,"a").getBoolean(model)||path(c,id)!=null)return;
   posted=MAIN.post(()->{try{if(field(model,"a").getBoolean(model))return;if(service.getField("v").getBoolean(null))return;CacheFolders.startForegroundService(c,acquisitionIntent(c,id));}catch(Exception error){android.util.Log.w("nicoid-cache","Unable to start background acquisition",error);}finally{synchronized(REQUESTED){REQUESTED.remove(id);}}});
  }catch(Exception error){android.util.Log.w("nicoid-cache","Unable to queue background acquisition",error);}finally{if(!posted)synchronized(REQUESTED){REQUESTED.remove(id);}}});
 }catch(Exception error){android.util.Log.w("nicoid-cache","Unable to prepare background acquisition",error);}}
 /** A title marks the native service intent as fully prepared (URL, metadata and comment bytes).
  * ID-only requests must enter its acquisition worker instead. */
 public static Intent acquisitionIntent(Context c,String id){return new Intent().setClassName(c,"com.sauzask.nicoid.NicoidDownloadCache").putExtra("id",id);}
 static Bundle metadata(Context c,String id)throws Exception{File f=new CacheFile(CacheFolders.root(c)+"/nicoid/nicoid_cache/",id+".json");ByteArrayOutputStream bytes=new ByteArrayOutputStream();try(InputStream in=new CacheInputStream(f)){byte[] b=new byte[4096];int n;while((n=in.read(b))!=-1){if(bytes.size()+n>2*1024*1024)throw new IOException();bytes.write(b,0,n);}}JSONObject data=new JSONObject(bytes.toString("UTF-8"));Bundle result=new Bundle();Iterator<String> keys=data.keys();while(keys.hasNext()){String k=keys.next();if(!data.isNull(k))result.putString(k,data.optString(k));}result.putString("videoId",id);result.putInt("nicovideo_version",data.optInt("nicovideo_version",2));return result;}
 public static String stream(Object model,String value){try{if(Boolean.TRUE.equals(field(model,"r").get(model)))return null;Context c=(Context)field(model,"K").get(model);String id=HistoryRules.id(value),path=path(c,id);if(path==null)return null;Bundle data=metadata(c,id);HistorySupport.rememberCached(data);data.putString("flvUrl",path);field(model,"m").set(model,data);field(model,"l").set(model,data);field(model,"G").set(model,path);field(model,"modernWatch").set(model,null);Object renderer=field(model,"b").get(model);if(renderer!=null)field(renderer,"d").set(renderer,path);initializeMetadata(model,data);Object listener=field(model,"k").get(model);// Native metadata listeners enqueue their own UI work. Dispatch here so their work precedes the media-start callback.
if(listener!=null&&!field(model,"a").getBoolean(model))Class.forName("e.e.a.l3.j").getMethod("a",Bundle.class).invoke(listener,data);if(!field(model,"modernCommentsRequested").getBoolean(model)){field(model,"modernCommentsRequested").setBoolean(model,true);new Thread(()->{try{Class.forName("e.e.a.ModernComments").getMethod("load",model.getClass()).invoke(null,model);refresh(model,id,path);}catch(Exception e){android.util.Log.w("nicoid-cache","Unable to read cached comments",e);}},"nicoid-cached-comments").start();}return path;}catch(Exception e){android.util.Log.w("nicoid-cache","Unable to restore cached video",e);return null;}}
 /** Refresh only metadata/comments; parse on an isolated model because the native parser changes media fields. */
 static void refresh(Object model,String id,String local){try{
  if(field(model,"a").getBoolean(model))return;Context c=(Context)field(model,"K").get(model);if(!refreshAllowed(c))return;
  Object probe=refreshModel(model,c);field(probe,"r").set(probe,Boolean.TRUE);
  for(String name:new String[]{"n","I"})field(probe,name).set(probe,field(model,name).get(model));
  Class<?> playback=Class.forName("e.e.a.ModernPlayback");Method watch=playback.getDeclaredMethod("watch",model.getClass(),String.class);watch.setAccessible(true);
  JSONObject fresh=(JSONObject)watch.invoke(null,probe,id);if(fresh==null||!current(model,id,local))return;
  Bundle data=(Bundle)model.getClass().getMethod("b",String.class).invoke(probe,fresh.toString());
  Method description=playback.getDeclaredMethod("fillDescription",JSONObject.class,Bundle.class);description.setAccessible(true);description.invoke(null,fresh,data);
  data.putInt("nicovideo_version",2);data.putString("videoId",id);data.putString("flvUrl",local);
  if(!current(model,id,local))return;field(model,"modernWatch").set(model,fresh);field(model,"m").set(model,data);field(model,"l").set(model,data);
  HistorySupport.rememberCached(data);Object listener=field(model,"k").get(model);if(listener!=null)Class.forName("e.e.a.l3.j").getMethod("a",Bundle.class).invoke(listener,data);
  try{playback.getField("latestWatch").set(null,fresh);}catch(NoSuchFieldException ignored){}
  try{saveMetadata(c,id,data);}catch(Exception error){android.util.Log.w("nicoid-cache","Unable to save refreshed metadata",error);}
  if(current(model,id,local)&&refreshAllowed(c))Class.forName("e.e.a.ModernComments").getMethod("load",model.getClass()).invoke(null,model);
 }catch(Exception error){android.util.Log.w("nicoid-cache","Metadata refresh unavailable; saved information retained ("+error.getClass().getSimpleName()+")");}}
 static boolean refreshAllowed(Context c){String mode=android.preference.PreferenceManager.getDefaultSharedPreferences(c).getString("cache_offline","0");if("2".equals(mode))return false;if(!"1".equals(mode))return true;try{return Boolean.TRUE.equals(Class.forName("e.e.a.v0").getMethod("g",Context.class).invoke(null,c));}catch(Exception error){return false;}}
 static Object refreshModel(Object model,Context c)throws Exception{
  java.util.concurrent.FutureTask<Object> task=new java.util.concurrent.FutureTask<>(()->model.getClass().getConstructor(Context.class).newInstance(c));
  if(Looper.myLooper()==Looper.getMainLooper())task.run();else if(!MAIN.post(task))throw new IOException("UI unavailable");
  try{return task.get(10,java.util.concurrent.TimeUnit.SECONDS);}finally{if(!task.isDone())task.cancel(false);}
 }
 static boolean current(Object model,String id,String local)throws Exception{return !field(model,"a").getBoolean(model)&&id.equals(HistoryRules.id((String)field(model,"d").get(model)))&&local.equals(field(model,"G").get(model));}
 /** Media files are immutable during metadata/comment refresh; retry installs new media. */
 static long acquiredAt(String root,String id){for(String extension:new String[]{".ncache",".mp4",".m3u8"}){File media=new CacheFile(root,id+extension);if(media.isFile()&&media.lastModified()>0)return media.lastModified();}return new CacheFile(root,id+".json").lastModified();}
 static synchronized void saveMetadata(Context c,String id,Bundle data)throws Exception{
  Bundle safe=new Bundle(data);safe.remove("cookie");safe.remove("sessionRequest");safe.remove("flvUrl");
  String root=CacheFolders.root(c)+"/nicoid/nicoid_cache/";safe.putString("cacheAcquiredAt",Long.toString(acquiredAt(root,id)));safe.putString("informationUpdatedAt",Long.toString(System.currentTimeMillis()));
  String json=(String)Class.forName("e.e.a.v0").getMethod("a",Bundle.class).invoke(null,safe);
  File dest=new CacheFile(root,id+".json"),temp=new CacheFile(root,id+".json.part"),backup=new CacheFile(root,id+".json.backup");boolean backed=false,installed=false;
  try{if(backup.exists())throw new IOException("Previous metadata backup retained");try(OutputStream out=new CacheOutputStream(temp,false)){out.write(json.getBytes("UTF-8"));}
   if(dest.exists()){if(!dest.renameTo(backup))throw new IOException("Cannot back up metadata");backed=true;}
   if(!temp.renameTo(dest))throw new IOException("Cannot install metadata");installed=true;if(backed)backup.delete();
  }finally{if(!installed&&backed)backup.renameTo(dest);if(temp.exists())temp.delete();}
 }
 public static void initializeMetadata(Object model,Bundle data)throws Exception{if(!Boolean.TRUE.equals(field(model,"r").get(model))){model.getClass().getMethod("a",Bundle.class).invoke(model,data);return;}String[] values=(String[])field(model,"L").get(model);values[0]=String.valueOf(field(model,"d").get(model));values[1]=data.getString("videoTitle",values[0]);values[2]=data.getString("thumbImage",data.getString("thumbnailUrl",""));String length=data.getString("length","0");try{long seconds=Long.parseLong(length);length=String.format(Locale.ROOT,"%d:%02d",seconds/60,seconds%60);}catch(NumberFormatException ignored){}values[3]=length;values[4]="再生:"+data.getString("viewCount","0")+" コメ:"+data.getString("commentCount","0")+" マイ:"+data.getString("mylistCount","0");}
 static void play(Object model,String path)throws Exception{Object cookie=field(model,"I").get(model);model.getClass().getMethod("a",String.class,Class.forName("org.apache.http.client.CookieStore")).invoke(model,path,cookie);}
 public static boolean offline(Object model){try{
  if(Boolean.TRUE.equals(field(model,"r").get(model)))return false;String id=HistoryRules.id((String)field(model,"d").get(model));String ready=stream(model,id);if(ready!=null){play(model,ready);return true;}
  Context c=(Context)field(model,"K").get(model);if(!CachePlaybackPolicy.acquire(android.preference.PreferenceManager.getDefaultSharedPreferences(c).getString("play_mode","0")))return false;
  synchronized(WAITING){if(!WAITING.add(model))return true;}new Thread(()->{try{if(field(model,"a").getBoolean(model))return;String network=(String)model.getClass().getMethod("g",String.class).invoke(model,id);if(network==null)throw new IOException("No playable stream");if(!field(model,"a").getBoolean(model))play(model,network);}catch(Exception error){android.util.Log.w("nicoid-cache","Unable to start streaming cache playback",error);status(model,UiStrings.translate("動画を再生できませんでした"));}finally{synchronized(WAITING){WAITING.remove(model);}}},"nicoid-cache-stream").start();return true;
 }catch(Exception error){android.util.Log.w("nicoid-cache","Unable to prepare cached playback",error);return false;}}
 static void progress(Object model,String message){MAIN.post(()->{try{if(field(model,"a").getBoolean(model))return;Object listener=field(model,"k").get(model);if(listener==null)return;Object value=field(listener,"x1").get(listener);if(!(value instanceof android.view.View))return;android.view.View root=(android.view.View)value;int id=root.getResources().getIdentifier("loadstatus","id",root.getContext().getPackageName());android.view.View view=id==0?null:root.findViewById(id);if(view instanceof android.widget.TextView){android.widget.TextView text=(android.widget.TextView)view;if(!message.contentEquals(text.getText()))text.setText(message);text.setTextColor(ThemeChoice.textColor(text));text.setVisibility(android.view.View.VISIBLE);}}catch(Exception ignored){}});}
 static void status(Object model,String message){MAIN.post(()->{try{if(!field(model,"a").getBoolean(model)){Object listener=field(model,"k").get(model);Class.forName("e.e.a.l3.j").getMethod("a",String.class).invoke(listener,message);}}catch(Exception ignored){}});}
 private static Field field(Object o,String name)throws Exception{Field f=o.getClass().getField(name);f.setAccessible(true);return f;}
}

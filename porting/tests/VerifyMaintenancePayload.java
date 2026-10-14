import com.android.tools.smali.dexlib2.Opcodes;
import com.android.tools.smali.dexlib2.dexbacked.DexBackedDexFile;
import com.android.tools.smali.dexlib2.iface.*;
import com.android.tools.smali.dexlib2.iface.instruction.*;
import com.android.tools.smali.dexlib2.iface.reference.*;
import java.io.*;import java.util.*;
/** Guard the implementation actually delivered in the merged helper payload. */
public final class VerifyMaintenancePayload {
 static Map<String,ClassDef> classes=new HashMap<>();static int checks;
 static void check(boolean value,String reason){checks++;if(!value)throw new AssertionError(reason);}
 static Method method(String family,String name){ClassDef c=classes.get("Le/e/a/"+family+";");if(c!=null)for(Method m:c.getMethods())if(m.getName().equals(name))return m;throw new AssertionError("Missing maintained method: "+family+"."+name);}
 static boolean calls(Method m,String owner,String name){if(m.getImplementation()==null)return false;for(Instruction i:m.getImplementation().getInstructions())if(i instanceof ReferenceInstruction){Reference ref=((ReferenceInstruction)i).getReference();if(ref instanceof MethodReference){MethodReference r=(MethodReference)ref;if(r.getDefiningClass().equals(owner)&&r.getName().equals(name))return true;}}return false;}
 static boolean familyCalls(String family,String owner,String name){ClassDef c=classes.get("Le/e/a/"+family+";");for(Method m:c.getMethods())if(calls(m,owner,name))return true;return false;}
 static boolean contains(String family,String value){for(Method m:classes.get("Le/e/a/"+family+";").getMethods())if(m.getImplementation()!=null)for(Instruction i:m.getImplementation().getInstructions())if(i instanceof ReferenceInstruction){Reference r=((ReferenceInstruction)i).getReference();if(r instanceof StringReference&&((StringReference)r).getString().contains(value))return true;}return false;}
 public static void main(String[] args)throws Exception{
  try(InputStream in=new BufferedInputStream(new FileInputStream(args[0]))){for(ClassDef c:DexBackedDexFile.fromInputStream(Opcodes.getDefault(),in).getClasses())classes.put(c.getType(),c);}
  check(contains("CacheComments",".comments.backup"),"comment recovery must be shipped");
  check(!contains("CachedPlayback","z0"),"local metadata restoration must not enable Cast state");
  check(familyCalls("ListActions","Le/e/a/ListActions;","cacheList"),"cache retry is gated to cache list");
  check(familyCalls("ListActions","Landroid/widget/ListView;","addHeaderView"),"history sorting is installed in list header");
  check(contains("SettingsIntegrity","comment_opacity_percent")&&contains("SettingsIntegrity","comment_rows"),"actual comment controls have explicit ordering");
  check(contains("CacheTransferPlan","Transfer size overflow"),"checked transfer accounting");method("CacheTransferPlan","parseRange");checks++;
  check(calls(method("CacheFolders","document"),"Le/e/a/DirectoryIndex;","document"),"scoped provider lookup");
  check(!calls(method("CacheFolders","document"),"Le/e/a/CacheFolders;","cacheChildren"),"single-file lookup must not scan all videos");
  check(calls(method("CacheOutputStream","close"),"Le/e/a/CacheFolders;","changed"),"written metadata invalidated");
  check(calls(method("LocalPlaylistData","<init>"),"Le/e/a/LocalPlaylistData;","object"),"playlist schema validation shipped");
  check(familyCalls("LocalPlaylists","Le/e/a/PlaylistStorage;","update"),"transactional background writes shipped");
  Method toggle=method("LocalPlaylists$Page","toggle");check(calls(toggle,"Le/e/a/LocalPlaylists$Page;","selectionChanged")&&!calls(toggle,"Le/e/a/LocalPlaylists$Page;","render"),"selection must retain the list");
  Method selection=method("LocalPlaylists$Page","selectionChanged");check(!calls(selection,"Le/e/a/LocalPlaylists$Page;","render")&&!calls(selection,"Le/e/a/LocalPlaylists;","read"),"selection must not rebuild or reparse");
  check(calls(method("ImageDiskCache","put"),"Le/e/a/ImageDiskCache;","reconcile"),"incremental disk accounting shipped");
  check(!calls(method("ImageDiskCache","trim"),"Ljava/io/File;","listFiles"),"each thumbnail write must not rescan files");
  check(calls(method("SearchRange","durations"),"Landroid/widget/EditText;","addTextChangedListener")||calls(method("SearchRange","durations"),"Landroid/widget/TextView;","addTextChangedListener"),"typed duration changes must be observed");
  check(classes.containsKey("Le/e/a/DurationRangeModel;"),"shared duration model shipped");
  check(calls(method("Review181","settings"),"Le/e/a/SettingsIntegrity;","ensure"),"settings restoration is wired");
  check(calls(method("SettingsIntegrity","ensure"),"Le/e/a/ThemeChoice;","settings"),"theme restoration is wired");
  for(String key:new String[]{"play_mode","comment_fetch_limit","google_cast_connect","google_cast_disconnect","remove_ad_option","labs"})check(contains("SettingsIntegrity",key),"settings regression guard: "+key);
  check(calls(method("CachedPlayback","offline"),"Le/e/a/CachePlaybackPolicy;","acquire"),"native cache mode contract is wired");
  check(calls(method("VideoDetails","bind"),"Le/e/a/PlayerTheme;","information"),"information theme is wired");
  boolean seriesToggle=false;for(ClassDef c:classes.values())if(c.getType().startsWith("Le/e/a/VideoDetails$SeriesPage"))for(Method m:c.getMethods())seriesToggle|=calls(m,"Le/e/a/SeriesPlayback;","toggle");check(seriesToggle,"series checkbox changes current queue");
  check(calls(method("SeriesPlayback","fetch"),"Le/e/a/CreatorData;","video"),"series response variants handled");
  check(!calls(method("ModernShorts","normalPlayer"),"Landroid/app/Activity;","finish"),"normal transition retains source screen");
  check(calls(method("ModernPlayback","stream"),"Le/e/a/CachedPlayback;","cacheWhilePlaying"),"streaming cache acquisition must be scheduled after media setup");
  check(calls(method("CachedPlayback","stream"),"Le/e/a/CachedPlayback;","initializeMetadata"),"cached playback initializes the native metadata contract");
  check(!familyCalls("CachedPlayback","Le/e/a/CachedPlayback;","pending"),"cache completion must not gate playback");
  check(calls(method("CachedPlayback","completedBytes"),"Le/e/a/CachePack;","entry"),"packed cache can complete without the old marker");
  method("ListActions","adjustHistoryMenu");method("ListActions","handleHistoryShuffle");checks+=2;
  check(calls(method("ListActions","themeHeader"),"Le/e/a/UiDialogs;","surface"),"public mylist description is themed");
  method("ModernShorts","completed");checks++;
  check(calls(method("FullscreenControls","update"),"Le/e/a/ModernShorts;","restoreLayout"),"native layout updates retain Shorts");
  check(contains("VideoDetails","nicoid_duration"),"thumbnail duration labels shipped");
  System.out.println("Maintained payload integration verified: "+checks+" checks");
 }
}

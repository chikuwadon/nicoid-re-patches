package e.e.a;

import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.*;
import android.preference.*;
import android.provider.DocumentsContract;
import android.widget.Toast;
import java.io.*;
import java.util.*;

/** Video cache requires an explicitly granted folder; auxiliary app data remains private. */
public final class CacheFolders {
    static volatile Context context;
    private static final String KEY="cache_tree_uri", PREFIX="/@nicoid-cache/", SUFFIX="/nicoid/nicoid_cache";
    private static final Handler MAIN=new Handler(Looper.getMainLooper());
    static SharedPreferences prefs(){return PreferenceManager.getDefaultSharedPreferences(context);}
    static void init(Context c){context=c.getApplicationContext();}
    static String privatePath(String path){if(path!=null&&context!=null&&path.startsWith(PREFIX)){int start=path.indexOf('/',PREFIX.length());if(start>=0){String relative=path.substring(start);if(relative.startsWith("/nicoid/")&&!relative.startsWith(SUFFIX))return privateRoot(context)+relative;}}return path;}
    public static void started(Context c){init(c);UiStrings.selectLanguage(prefs().getString("app_lang","0"));CacheProgress.start(c);MAIN.post(()->Toast.makeText(c,UiStrings.translate("キャッシュ取得を開始しました"),Toast.LENGTH_SHORT).show());}
    public static String privateRoot(Context c){init(c);File base=c.getExternalFilesDir(null);return (base==null?c.getFilesDir():base).getAbsolutePath();}
    public static String root(Context c) {
        init(c);String tree=prefs().getString(KEY,"");
        if(tree.isEmpty())return PREFIX+"unselected";
        String key=key(tree),bookmark="cache_tree_"+key;
        if(!tree.equals(prefs().getString(bookmark,"")))prefs().edit().putString(bookmark,tree).apply();return PREFIX+key;
    }
    private static String key(String tree) {
        try{byte[] bytes=java.security.MessageDigest.getInstance("SHA-256").digest(tree.getBytes("UTF-8"));StringBuilder s=new StringBuilder();for(byte b:bytes)s.append(String.format(java.util.Locale.ROOT,"%02x",b&255));return s.toString();}catch(Exception e){throw new IllegalStateException(e);}
    }
    static boolean virtual(File f){return f.getAbsolutePath().startsWith(PREFIX);}
    static Uri tree(File f) throws FileNotFoundException {
        String path=f.getAbsolutePath();int end=path.indexOf('/',PREFIX.length());String key=end<0?path.substring(PREFIX.length()):path.substring(PREFIX.length(),end);
        String tree=prefs().getString("cache_tree_"+key,"");if(tree.isEmpty())throw new FileNotFoundException("Cache folder not selected");return Uri.parse(tree);
    }
    static String relative(File f) throws FileNotFoundException {
        String path=f.getAbsolutePath();int start=path.indexOf(SUFFIX,PREFIX.length());if(start<0)throw new FileNotFoundException("Not a video cache path");
        String name=path.substring(start+SUFFIX.length());if(name.startsWith("/"))name=name.substring(1);
        if(name.contains("/")||name.equals("..")||name.equals("."))throw new FileNotFoundException("Invalid cache path");return name;
    }
    private static final Map<String,DirectoryIndex<Uri>> indexes=new LinkedHashMap<String,DirectoryIndex<Uri>>(8,.75f,true){protected boolean removeEldestEntry(Map.Entry<String,DirectoryIndex<Uri>> e){return size()>8;}};
    private static DirectoryIndex<Uri> index(Uri tree){synchronized(indexes){DirectoryIndex<Uri> index=indexes.get(tree.toString());if(index==null){Uri root=DocumentsContract.buildDocumentUriUsingTree(tree,DocumentsContract.getTreeDocumentId(tree));index=new DirectoryIndex<>(root,new DirectoryIndex.Provider<Uri>(){
        public List<DirectoryIndex.Item<Uri>> children(Uri parent)throws IOException{List<DirectoryIndex.Item<Uri>> out=new ArrayList<>();for(Entry e:CacheFolders.children(parent,tree))out.add(item(e));return out;}
        public Uri create(Uri parent,String name,boolean directory)throws IOException{try{return DocumentsContract.createDocument(context.getContentResolver(),parent,directory?DocumentsContract.Document.MIME_TYPE_DIR:mime(name),name);}catch(RuntimeException e){throw new IOException("Cannot create cache document",e);}}
        public DirectoryIndex.Item<Uri> stat(Uri uri,String name)throws IOException{return rawStat(uri,name);}
        public long now(){return SystemClock.elapsedRealtime();}
    });indexes.put(tree.toString(),index);}return index;}}
    private static DirectoryIndex.Item<Uri> item(Entry e){return new DirectoryIndex.Item<>(e.name,e.uri,DocumentsContract.Document.MIME_TYPE_DIR.equals(e.mime),e.size,e.time);}
    static void forget(File f){try{index(tree(f)).forget(relative(f));}catch(IOException ignored){}}
    static void remember(File f,Uri uri){try{index(tree(f)).remember(relative(f),uri);}catch(IOException ignored){}}
    static void changed(File f){if(!virtual(f))return;try{index(tree(f)).changed(relative(f));}catch(IOException ignored){}}
    static Uri document(File f,boolean create)throws IOException{return index(tree(f)).document(relative(f),create);}
    static void cleanupVideoFolder(File f)throws IOException{
        String id=videoId(relative(f));if(id==null)return;Uri tree=tree(f);DirectoryIndex<Uri> index=index(tree);Uri directory=index.directory(id);if(directory==null)return;
        for(Entry child:children(directory,tree))if(!".nomedia".equals(child.name))return;
        if(DocumentsContract.deleteDocument(context.getContentResolver(),directory))index.removeDirectory(id);
    }
    private static final java.util.regex.Pattern VIDEO_ID=java.util.regex.Pattern.compile("^((?:sm|so|nm|ss)[0-9]+)(?:[._].*)?$");
    static String videoId(String name){java.util.regex.Matcher m=VIDEO_ID.matcher(name);return m.matches()?m.group(1):null;}
    static String physicalName(String name){return videoId(name)!=null&&name.endsWith(".mp4")?name.substring(0,name.length()-4)+".complete":name;}
    static List<Entry> cacheChildren(Uri parent,Uri tree)throws IOException{List<Entry> out=new ArrayList<>();for(DirectoryIndex.Item<Uri> item:index(tree).all())out.add(entry(item));return out;}
    private static Entry entry(DirectoryIndex.Item<Uri> item){Entry e=new Entry();e.uri=item.ref;e.name=DirectoryIndex.logical(item.name);e.mime=item.directory?DocumentsContract.Document.MIME_TYPE_DIR:mime(e.name);e.size=item.size;e.time=item.time;return e;}
    private static final Map<String,String> comments=new LinkedHashMap<String,String>(4,.75f,true){protected boolean removeEldestEntry(Map.Entry<String,String> e){return size()>4;}};
    public static boolean comments(String folder,String id,String json){if(json!=null&&json.length()<2000000)synchronized(comments){comments.put(id,json);}return saveComments(folder,id);}
    static void exportComments(String folder,String id){String json;synchronized(comments){json=comments.get(id);}if(json==null)return;CacheComments.save(folder,id,json);}
    public static boolean saveComments(String folder,String id){return CacheComments.video(folder,id);}
    static String mime(String n){return n.endsWith(".m3u8")?"application/vnd.apple.mpegurl":n.endsWith(".json")?"application/json":n.endsWith(".mp4")?"video/mp4":"application/octet-stream";}
    static final class Entry {Uri uri;String name,mime;long size,time;}
    static List<Entry> children(Uri parent,Uri tree) throws IOException {
        ArrayList<Entry> result=new ArrayList<>();Uri uri=DocumentsContract.buildChildDocumentsUriUsingTree(tree,DocumentsContract.getDocumentId(parent));
        String[] columns={"document_id","_display_name","mime_type","_size","last_modified"};
        try(Cursor cursor=context.getContentResolver().query(uri,columns,null,null,null)) {
            if(cursor==null)throw new IOException("Cache folder unavailable");
            while(cursor.moveToNext()){Entry e=new Entry();e.uri=DocumentsContract.buildDocumentUriUsingTree(tree,cursor.getString(0));e.name=cursor.getString(1);e.mime=cursor.getString(2);e.size=cursor.getLong(3);e.time=cursor.getLong(4);result.add(e);}
        }catch(RuntimeException e){throw new IOException("Cache folder unavailable",e);}return result;
    }
    private static DirectoryIndex.Item<Uri> rawStat(Uri u,String name)throws IOException{
        try(Cursor c=context.getContentResolver().query(u,new String[]{"mime_type","_size","last_modified"},null,null,null)){
            if(c==null||!c.moveToFirst())return null;return new DirectoryIndex.Item<>(name,u,DocumentsContract.Document.MIME_TYPE_DIR.equals(c.getString(0)),c.getLong(1),c.getLong(2));
        }catch(RuntimeException x){throw new IOException(x);}
    }
    static Entry stat(File f)throws IOException{DirectoryIndex.Item<Uri> item=index(tree(f)).stat(relative(f));if(item!=null)return entry(item);CachePackIndex.Entry packed=CachePack.entry(f);if(packed==null)return null;Entry e=new Entry();e.name=f.getName();e.mime=mime(e.name);e.size=packed.size;e.time=packed.time;return e;}
    static ParcelFileDescriptor open(File f,boolean write,boolean append) throws FileNotFoundException {
        try {
            if(!virtual(f)){
                if(write){File parent=f.getParentFile();if(parent!=null&&!parent.isDirectory()&&!parent.mkdirs())throw new IOException("Cannot create cache directory");}
                return ParcelFileDescriptor.open(f,write?ParcelFileDescriptor.MODE_WRITE_ONLY|ParcelFileDescriptor.MODE_CREATE|(append?ParcelFileDescriptor.MODE_APPEND:ParcelFileDescriptor.MODE_TRUNCATE):ParcelFileDescriptor.MODE_READ_ONLY);
            }
            Uri uri=document(f,write);if(write)changed(f);if(uri==null)throw new IOException("Missing cached file");ParcelFileDescriptor fd=context.getContentResolver().openFileDescriptor(uri,write?(append?"wa":"wt"):"r");if(fd==null)throw new IOException("Cannot open cached file");return fd;
        }catch(IOException|RuntimeException e){FileNotFoundException failure=new FileNotFoundException(e.getMessage());failure.initCause(e);throw failure;}
    }
    public static void settings(PreferenceActivity a) {
        init(a);Preference old=a.findPreference("cache_dir");
        if(old instanceof ListPreference){PreferenceGroup parent=parent(a.getPreferenceScreen(),old);if(parent!=null){Preference p=new Preference(a);p.setKey("cache_dir");p.setTitle(UiStrings.translate("キャッシュ保存先"));p.setOrder(old.getOrder());parent.removePreference(old);parent.addPreference(p);old=p;}}
        if(old!=null){old.setOnPreferenceClickListener(p->{location(a,null);return true;});summary(a);}
    }
    private static PreferenceGroup parent(PreferenceGroup group,Preference p){for(int i=0;i<group.getPreferenceCount();i++){Preference child=group.getPreference(i);if(child==p)return group;if(child instanceof PreferenceGroup){PreferenceGroup found=parent((PreferenceGroup)child,p);if(found!=null)return found;}}return null;}
    public static void summary(PreferenceActivity a) {
        init(a);Preference p=a.findPreference("cache_dir");if(p==null)return;String tree=prefs().getString(KEY,"");
        String label=tree.isEmpty()?UiStrings.translate("未指定（フォルダーを選択してください）"):DocumentsContract.getTreeDocumentId(Uri.parse(tree));p.setSummary(UiStrings.translate("保存先: ")+label);
    }
    private static Activity activity(Context c){while(c instanceof ContextWrapper){if(c instanceof Activity)return (Activity)c;Context next=((ContextWrapper)c).getBaseContext();if(next==c)break;c=next;}return c instanceof Activity?(Activity)c:null;}
    private static boolean ready(){try{String s=prefs().getString(KEY,"");if(s.isEmpty())return false;Uri tree=Uri.parse(s);Uri root=DocumentsContract.buildDocumentUriUsingTree(tree,DocumentsContract.getTreeDocumentId(tree));children(root,tree);return true;}catch(Exception e){return false;}}
    public static ComponentName startService(Context c,Intent intent) {return service(c,intent,false);}
    public static ComponentName startForegroundService(Context c,Intent intent) {return service(c,intent,true);}
    private static ComponentName service(Context c,Intent intent,boolean foreground) {
        init(c);ComponentName target=intent.getComponent();
        if(target!=null&&target.getClassName().equals("com.sauzask.nicoid.NicoidDownloadCache")&&!ready()){
            Activity a=activity(c);if(a!=null)location(a,new Intent(intent));else Toast.makeText(c,UiStrings.translate("設定でキャッシュ保存先を選択してください"),Toast.LENGTH_LONG).show();return null;
        }
        return foreground&&Build.VERSION.SDK_INT>=26?c.startForegroundService(intent):c.startService(intent);
    }
    static void location(Activity a,Intent pending) {init(a);choose(a,pending);}
    public static boolean workerReady(Context c){init(c);return ready();}
    public static void startActivity(Context c,Intent intent){
        init(c);if(pageRequest(intent)&&!ready()){Activity a=activity(c);if(a!=null)choose(a,new Intent(intent));else Toast.makeText(c,UiStrings.translate("設定でキャッシュ保存先を選択してください"),Toast.LENGTH_LONG).show();return;}
        c.startActivity(intent);
    }
    private static boolean replaceHost(Intent pending){return pending!=null&&pending.getBooleanExtra("nicoid_cache_replace_picker_host",false);}
    public static boolean page(Activity a){
        init(a);if(ready()){
            String previous=a.getIntent().getStringExtra("nicoid_cache_copy_from");
            if(previous!=null){a.getIntent().removeExtra("nicoid_cache_copy_from");MAIN.post(()->{if(!a.isFinishing())offerCopy(a,previous);});}
            return true;
        }
        choose(a,new Intent().setClassName(a,"com.sauzask.nicoid.NicoidCacheManagerActivity").putExtra("nicoid_cache_replace_picker_host",true));return false;
    }
    private static boolean pageRequest(Intent pending){ComponentName c=pending==null?null:pending.getComponent();return c!=null&&c.getClassName().equals("com.sauzask.nicoid.NicoidCacheManagerActivity");}
    static void choose(Activity a,Intent pending) {
        // Defer until the current click/fragment callback has finished.
        MAIN.post(()->{
            if(a.isFinishing()||a.isDestroyed())return;
            android.app.FragmentManager manager=a.getFragmentManager();
            if(Build.VERSION.SDK_INT>=26&&manager.isStateSaved()){
                Toast.makeText(a,UiStrings.translate("設定でキャッシュ保存先を選択してください"),Toast.LENGTH_LONG).show();return;
            }
            try{
                android.app.Fragment existing=manager.findFragmentByTag("cache-folder-picker");
                Picker p=existing instanceof Picker?(Picker)existing:new Picker();p.pending=pending;
                if(existing==null)manager.beginTransaction().add(p,"cache-folder-picker").commit();
                manager.executePendingTransactions();p.pick();
            }catch(IllegalStateException e){
                Toast.makeText(a,UiStrings.translate("設定でキャッシュ保存先を選択してください"),Toast.LENGTH_LONG).show();
            }
        });
    }
    public static final class Picker extends android.app.Fragment {
        Intent pending;boolean picking;
        @Override public void onCreate(Bundle state){super.onCreate(state);if(state!=null){pending=state.getParcelable("pending");picking=state.getBoolean("picking");}}
        @Override public void onSaveInstanceState(Bundle state){super.onSaveInstanceState(state);state.putParcelable("pending",pending);state.putBoolean("picking",picking);}
        void pick(){if(picking)return;picking=true;Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION|Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION|Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
            if(Build.VERSION.SDK_INT>=26)i.putExtra(DocumentsContract.EXTRA_INITIAL_URI,Uri.parse("content://com.android.externalstorage.documents/document/primary%3AMovies"));
            try{startActivityForResult(i,41);}catch(ActivityNotFoundException e){picking=false;Toast.makeText(getActivity(),UiStrings.translate("フォルダー選択画面を開けませんでした"),1).show();}}
        @Override public void onActivityResult(int request,int result,Intent data){
            super.onActivityResult(request,result,data);if(request!=41)return;picking=false;Activity a=getActivity();if(a==null||result!=Activity.RESULT_OK||data==null||data.getData()==null){boolean finish=replaceHost(pending);pending=null;if(finish&&a!=null)a.finish();return;}
            init(a);try{
                Uri tree=data.getData();int flags=data.getFlags()&(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                if(flags!=(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION))throw new IOException("Read/write grant required");
                a.getContentResolver().takePersistableUriPermission(tree,flags);
                Uri root=DocumentsContract.buildDocumentUriUsingTree(tree,DocumentsContract.getTreeDocumentId(tree));
                Uri probe=DocumentsContract.createDocument(a.getContentResolver(),root,"application/octet-stream",".nicoid-test-"+UUID.randomUUID());
                if(probe==null)throw new IOException("Folder is not writable");if(!DocumentsContract.deleteDocument(a.getContentResolver(),probe))throw new IOException("Folder is not writable");
                String before=prefs().getString(KEY,"");if(!prefs().edit().putString(KEY,tree.toString()).putString("cache_tree_"+key(tree.toString()),tree.toString()).commit())throw new IOException("Cannot save folder grant");
                if(a instanceof PreferenceActivity)summary((PreferenceActivity)a);
                Intent next=pending;pending=null;if(pageRequest(next)){if(!tree.toString().equals(before))next.putExtra("nicoid_cache_copy_from",before);boolean finish=replaceHost(next);next.removeExtra("nicoid_cache_replace_picker_host");a.startActivity(next);if(finish)a.finish();}else{if(next!=null)service(a,next,Build.VERSION.SDK_INT>=26);if(!tree.toString().equals(before))offerCopy(a,before);}
            }catch(Exception e){Toast.makeText(a,UiStrings.translate("保存先を使用できません。別のフォルダーを選択してください"),1).show();if(pageRequest(pending)){boolean finish=replaceHost(pending);pending=null;if(finish)a.finish();}}
        }
    }
    static void invalidateSummary(Context c){new File(privateRoot(c),"nicoid/cachelist.json").delete();}
    static void offerCopy(Activity a,String previous) {
        File old=previous.isEmpty()?new File(privateRoot(a),"nicoid/nicoid_cache"):new CacheFile(PREFIX+key(previous)+SUFFIX);File[] existing=old.listFiles();boolean found=false;if(existing!=null)for(File f:existing)if(videoId(f.getName())!=null&&f.isFile()&&CacheComments.copyable(f)){found=true;break;}if(!found)return;
        AlertDialog dialog=new AlertDialog.Builder(PlaybackSession.dialogContext(a)).setTitle(UiStrings.translate("既存キャッシュをコピー"))
            .setMessage(UiStrings.translate("以前の保存先からコピーします。元のファイルは削除しません"))
            .setPositiveButton(UiStrings.translate("コピー"),(d,w)->copy(a,old))
            .setNegativeButton(UiStrings.translate("後で"),null).create();UiDialogs.show(dialog);
    }
    private static int copyOrder(String name){return name.endsWith(".json")?2:name.endsWith(".mp4")?1:0;}
    private static void copy(Activity a,File old) {
        String destination=root(a)+SUFFIX;Context dialogContext=PlaybackSession.dialogContext(a);android.widget.LinearLayout content=new android.widget.LinearLayout(dialogContext);content.setGravity(android.view.Gravity.CENTER_VERTICAL);int pad=PanelUi.dp(dialogContext,24);content.setPadding(pad,pad,pad,pad);content.setBackgroundColor(UiDialogs.surface(dialogContext));android.widget.ProgressBar spinner=new android.widget.ProgressBar(dialogContext);content.addView(spinner,new android.widget.LinearLayout.LayoutParams(PanelUi.dp(dialogContext,40),PanelUi.dp(dialogContext,40)));android.widget.TextView message=PanelUi.text(dialogContext,UiStrings.translate("キャッシュをコピー中"),16);message.setPadding(pad,0,0,0);content.addView(message,new android.widget.LinearLayout.LayoutParams(0,-2,1));AlertDialog progress=new AlertDialog.Builder(dialogContext).setView(content).setCancelable(false).create();UiDialogs.show(progress);
        android.widget.ProgressBar bar=spinner;if(bar!=null){android.util.TypedValue color=new android.util.TypedValue();bar.getContext().getTheme().resolveAttribute(0x7f03005e,color,true);int tint=color.resourceId==0?color.data:bar.getResources().getColor(color.resourceId);bar.setIndeterminateTintList(android.content.res.ColorStateList.valueOf(tint));}
        new Thread(()->{boolean ok=false;try{File[] files=old.listFiles();if(files==null)throw new IOException("Cannot list old cache");Arrays.sort(files,(x,y)->Integer.compare(copyOrder(x.getName()),copyOrder(y.getName())));for(File f:files){if(!f.isFile()||!CacheComments.copyable(f))continue;File dest=new CacheFile(destination,f.getName());if(dest.exists())continue;File temp=new CacheFile(destination,f.getName()+".copy-"+UUID.randomUUID());
            boolean complete=false;try(InputStream in=virtual(f)?new CacheInputStream(f):new FileInputStream(f);OutputStream out=new CacheOutputStream(temp)){byte[] b=new byte[65536];int n;while((n=in.read(b))!=-1)out.write(b,0,n);complete=true;}finally{if(!complete)temp.delete();}if(!temp.renameTo(dest)){temp.delete();throw new IOException("Cannot finalize cache copy");}}
            CacheComments.clean(destination,new CacheFile(destination).listFiles());ok=true;}catch(Exception e){android.util.Log.w("nicoid-cache","Cache copy failed",e);}boolean success=ok;MAIN.post(()->{if(!a.isFinishing()){progress.dismiss();if(success&&a.getClass().getName().equals("com.sauzask.nicoid.NicoidCacheManagerActivity"))a.recreate();Toast.makeText(a,UiStrings.translate(success?"キャッシュのコピーが完了しました":"コピーに失敗しました。元のファイルは保持されています"),1).show();}});},"nicoid-cache-copy").start();
    }
}

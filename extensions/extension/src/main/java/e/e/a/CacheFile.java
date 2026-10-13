package e.e.a;

import java.io.*;
import java.util.*;
import android.net.Uri;
import android.provider.DocumentsContract;

/** Preserve the legacy File API while resolving selected cache files through SAF. */
public class CacheFile extends File {
    public CacheFile(String path){super(CacheFolders.privatePath(path));}
    public CacheFile(String parent,String child){super(CacheFolders.privatePath(new File(parent,child).getPath()));}
    public CacheFile(File parent,String child){super(CacheFolders.privatePath(new File(parent,child).getPath()));}
    public CacheFile(java.net.URI uri){super(CacheFolders.privatePath(new File(uri).getPath()));}
    private boolean saf(){return CacheFolders.virtual(this);}
    @Override public File getParentFile(){String p=getParent();return p==null?null:new CacheFile(p);}
    @Override public File getAbsoluteFile(){return new CacheFile(getAbsolutePath());}
    @Override public boolean exists(){if(!saf()){if(super.exists())return true;try{return CachePack.entry(this)!=null;}catch(IOException|RuntimeException e){return false;}}try{return CacheFolders.stat(this)!=null;}catch(IOException|RuntimeException e){return false;}}
    @Override public boolean isDirectory(){if(!saf())return super.isDirectory();try{CacheFolders.Entry e=CacheFolders.stat(this);return e!=null&&DocumentsContract.Document.MIME_TYPE_DIR.equals(e.mime);}catch(IOException|RuntimeException e){return false;}}
    @Override public boolean isFile(){if(!saf())return exists()&&!super.isDirectory();try{CacheFolders.Entry e=CacheFolders.stat(this);return e!=null&&!DocumentsContract.Document.MIME_TYPE_DIR.equals(e.mime);}catch(IOException|RuntimeException e){return false;}}
    @Override public long length(){if(!saf()){if(super.exists())return super.length();try{CachePackIndex.Entry e=CachePack.entry(this);return e==null?0:e.size;}catch(IOException|RuntimeException e){return 0;}}try{CacheFolders.Entry e=CacheFolders.stat(this);return e==null?0:e.size;}catch(IOException|RuntimeException e){return 0;}}
    @Override public long lastModified(){if(!saf()){if(super.exists())return super.lastModified();try{CachePackIndex.Entry e=CachePack.entry(this);return e==null?0:e.time;}catch(IOException|RuntimeException e){return 0;}}try{CacheFolders.Entry e=CacheFolders.stat(this);return e==null?0:e.time;}catch(IOException|RuntimeException e){return 0;}}
    @Override public boolean mkdir(){return saf()?isDirectory():super.mkdir();}
    @Override public boolean mkdirs(){return saf()?isDirectory():super.mkdirs();}
    @Override public boolean createNewFile()throws IOException{if(!saf())return super.createNewFile();if(exists())return false;CacheFolders.document(this,true);return true;}
    @Override public boolean delete(){if(!saf())return super.delete();try{if(CacheFolders.relative(this).isEmpty())return false;Uri uri=CacheFolders.document(this,false);boolean removed=uri!=null&&DocumentsContract.deleteDocument(CacheFolders.context.getContentResolver(),uri);if(removed){CacheFolders.forget(this);try{CacheFolders.cleanupVideoFolder(this);}catch(IOException e){android.util.Log.w("nicoid-cache","Cache folder cleanup failed",e);}}return removed;}catch(Exception e){return false;}}
    @Override public boolean renameTo(File dest){if(!saf())return super.renameTo(dest);try{if(!CacheFolders.virtual(dest)||!CacheFolders.tree(this).equals(CacheFolders.tree(dest))||dest.exists())return false;Uri renamed=DocumentsContract.renameDocument(CacheFolders.context.getContentResolver(),CacheFolders.document(this,false),CacheFolders.physicalName(dest.getName()));if(renamed!=null){CacheFolders.forget(this);CacheFolders.remember(dest,renamed);}return renamed!=null;}catch(Exception e){return false;}}
    @Override public File[] listFiles(){if(!saf()){File[] files=super.listFiles();if(files==null)return new File[0];File[] result=new File[files.length];for(int i=0;i<files.length;i++)result[i]=new CacheFile(files[i].getAbsolutePath());return result;}try{Uri tree=CacheFolders.tree(this),parent=CacheFolders.document(this,false);if(parent==null)return new File[0];List<CacheFolders.Entry> es=CacheFolders.cacheChildren(parent,tree);File[] files=new File[es.size()];for(int i=0;i<files.length;i++)files[i]=new CacheFile(this,es.get(i).name);return files;}catch(IOException|RuntimeException e){return new File[0];}}
    @Override public File[] listFiles(FilenameFilter filter){File[] fs=listFiles();if(fs==null||filter==null)return fs;ArrayList<File> out=new ArrayList<>();for(File f:fs)if(filter.accept(this,f.getName()))out.add(f);return out.toArray(new File[0]);}
    @Override public File[] listFiles(FileFilter filter){File[] fs=listFiles();if(fs==null||filter==null)return fs;ArrayList<File> out=new ArrayList<>();for(File f:fs)if(filter.accept(f))out.add(f);return out.toArray(new File[0]);}
    @Override public String[] list(){File[] fs=listFiles();if(fs==null)return null;String[] names=new String[fs.length];for(int i=0;i<names.length;i++)names[i]=fs[i].getName();return names;}
    @Override public String[] list(FilenameFilter filter){File[] fs=listFiles(filter);if(fs==null)return null;String[] names=new String[fs.length];for(int i=0;i<names.length;i++)names[i]=fs[i].getName();return names;}
}

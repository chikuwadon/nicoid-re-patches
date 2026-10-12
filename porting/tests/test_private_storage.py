#!/usr/bin/env python3
"""Run the production File/stream adapters against a private-history directory."""
import argparse,pathlib,subprocess,tempfile
parser=argparse.ArgumentParser(description=__doc__)
parser.add_argument('helper_classes',type=pathlib.Path)
parser.add_argument('android_jar',type=pathlib.Path)
parser.add_argument('ecj_jar',type=pathlib.Path)
args=parser.parse_args();classes=args.helper_classes.resolve();android=args.android_jar.resolve()
with tempfile.TemporaryDirectory() as d:
 p=pathlib.Path(d);sources={
 'android/content/Context.java':'''package android.content;import java.io.File;public class Context{File base;public Context(File f){base=f;}public Context getApplicationContext(){return this;}public File getExternalFilesDir(String ignored){return base;}public File getFilesDir(){return base;}}''',
 'android/os/Looper.java':'package android.os;public class Looper{public static Looper getMainLooper(){return new Looper();}}',
 'android/os/Handler.java':'package android.os;public class Handler{public Handler(Looper l){}}',
 'android/os/ParcelFileDescriptor.java':'''package android.os;import java.io.*;public class ParcelFileDescriptor implements Closeable{public static final int MODE_WRITE_ONLY=1,MODE_CREATE=2,MODE_APPEND=4,MODE_TRUNCATE=8,MODE_READ_ONLY=0;RandomAccessFile f;private ParcelFileDescriptor(File file,int mode)throws IOException{f=new RandomAccessFile(file,mode==0?"r":"rw");if((mode&MODE_TRUNCATE)!=0)f.setLength(0);if((mode&MODE_APPEND)!=0)f.seek(f.length());}public static ParcelFileDescriptor open(File f,int mode)throws FileNotFoundException{try{return new ParcelFileDescriptor(f,mode);}catch(IOException e){throw new FileNotFoundException(e.toString());}}public FileDescriptor getFileDescriptor(){try{return f.getFD();}catch(IOException e){throw new RuntimeException(e);}}public void close()throws IOException{f.close();}}''',
 'e/e/a/PrivateStorageTest.java':'''package e.e.a;import java.io.*;import android.content.*;public class PrivateStorageTest{static void check(boolean v,String m){if(!v)throw new AssertionError(m);}public static void main(String[] args)throws Exception{File base=new File(args[0]);CacheFolders.privateRoot(new Context(base));String virtual="/@nicoid-cache/abc/nicoid/";File history=new CacheFile(virtual,"history.json");check(history.equals(new File(base,"nicoid/history.json")),"history is private");check(history.getParentFile().mkdirs(),"history directory created");try(OutputStream out=new CacheOutputStream(history)){out.write("[{\\\"id\\\":\\\"sm1\\\"}]".getBytes("UTF-8"));}try(InputStream in=new CacheInputStream(history)){check(new String(in.readAllBytes(),"UTF-8").contains("sm1"),"history survives adapter read");}File same=new CacheFile("/@nicoid-cache/def/nicoid/history.json");check(same.equals(history)&&same.exists(),"changing selected tree preserves history");check(new CacheFile(virtual,"bookmark.json").getParentFile().equals(history.getParentFile()),"bookmarks remain private");check(new CacheFile("/@nicoid-cache/def/nicoid/nicoid_cache/sm1.json").getPath().startsWith("/@nicoid-cache/"),"video cache stays SAF");check(same.delete()&&!new CacheFile(virtual,"history.json").exists(),"history deletion persists");System.out.println("Private history/bookmark routing: 7 checks passed");}}'''
 }
 for name,source in sources.items():f=p/name;f.parent.mkdir(parents=True,exist_ok=True);f.write_text(source)
 subprocess.run(['java','-jar',str(args.ecj_jar),'-17','-proc:none','-nowarn','-cp',f'{classes}:{android}','-d',str(p),*map(str,p.rglob('*.java'))],check=True)
 disk=p/'private';disk.mkdir();subprocess.run(['java','-cp',f'{p}:{classes}:{android}','e.e.a.PrivateStorageTest',str(disk)],check=True)

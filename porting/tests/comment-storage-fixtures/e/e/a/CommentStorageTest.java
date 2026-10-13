package e.e.a;
import java.io.*;import java.nio.file.*;import java.util.*;import java.util.concurrent.*;
public class CommentStorageTest {
 static void check(boolean b,String s){if(!b)throw new AssertionError(s);}
 public static void main(String[] args)throws Exception{File root=Files.createTempDirectory("nicoid-comments-").toFile();String folder=root.toString(),old="{\"threads\":[{\"id\":1}]}";try{
  Files.write(new File(root,"sm1.mp4").toPath(),new byte[]{1});CacheComments.save(folder,"sm1",old);check(old.equals(CacheComments.read(folder,"sm1")),"initial save");
  CacheFile.failInstall=true;CacheComments.save(folder,"sm1","{\"threads\":[]}");check(old.equals(CacheComments.read(folder,"sm1")),"installation failure restores old comments");
  CacheFile.failRestore=true;CacheComments.save(folder,"sm1","{\"threads\":[]}");check(old.equals(CacheComments.read(folder,"sm1")),"failed rollback keeps readable backup");check(new File(root,"sm1.comments.backup").isFile(),"backup retained after double rename failure");
  CacheFile.failInstall=false;CacheFile.failRestore=false;String next="{\"threads\":[{\"id\":2}]}";CacheComments.save(folder,"sm1",next);check(next.equals(CacheComments.read(folder,"sm1")),"next save recovers interrupted swap");
  CacheFile.failOpen=true;CacheComments.save(folder,"sm1",old);check(next.equals(CacheComments.read(folder,"sm1")),"write failure preserves current comments");CacheFile.failOpen=false;
  File dest=new File(root,"sm1.comments.gz"),backup=new File(root,"sm1.comments.backup");check(dest.renameTo(backup),"simulate process death after backup");CacheComments.clean(folder,root.listFiles());check(next.equals(CacheComments.read(folder,"sm1"))&&dest.exists()&&!backup.exists(),"cleanup recovers interrupted swap");
  ExecutorService pool=Executors.newFixedThreadPool(8);try{List<Future<?>> tasks=new ArrayList<>();for(int n=0;n<100;n++){final int value=n;tasks.add(pool.submit(()->{CacheComments.save(folder,"sm1","{\"threads\":[{\"id\":"+value+"}]}");String json=CacheComments.read(folder,"sm1");check(json!=null&&json.startsWith("{\"threads\":"),"concurrent save/read stays readable");}));}for(Future<?> f:tasks)f.get();}finally{pool.shutdownNow();}
  for(File f:root.listFiles())check(!f.getName().endsWith(".partial")&&!f.getName().endsWith(".backup"),"successful saves leave no temporary files");
  File partial=new File(root,"sm1.comments.abandoned.partial");Files.write(partial.toPath(),new byte[]{0});partial.setLastModified(System.currentTimeMillis()-2L*24*60*60*1000);CacheComments.clean(folder,root.listFiles());check(!partial.exists(),"abandoned partial is cleaned");
  check(!CacheComments.copyable(new File(root,"sm1.comments.backup")),"backup not copied as permanent comments");new File(root,"sm1.mp4").delete();CacheComments.clean(folder,root.listFiles());check(!dest.exists(),"deleted video owns no retained comments");
  System.out.println("Comment storage: failed install/write/rollback, interrupted recovery, 100 concurrent saves and abandoned-file cleanup passed");
 }finally{for(File f:root.listFiles())f.delete();root.delete();}}
}

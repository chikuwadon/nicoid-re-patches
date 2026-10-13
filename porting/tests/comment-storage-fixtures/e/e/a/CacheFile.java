package e.e.a;
import java.io.*;
public class CacheFile extends File {
 static volatile boolean failInstall,failRestore,failOpen;
 public CacheFile(String p,String n){super(p,n);}
 public boolean renameTo(File target){if(failInstall&&getName().endsWith(".partial"))return false;if(failRestore&&getName().endsWith(".backup"))return false;return super.renameTo(target);}
}

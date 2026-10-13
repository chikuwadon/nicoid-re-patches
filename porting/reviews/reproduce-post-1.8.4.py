from pathlib import Path
import subprocess, argparse, tempfile
parser=argparse.ArgumentParser(description='Reproduce three post-1.8.4 audit findings in isolated JVM fixtures.')
parser.add_argument('--ecj', required=True)
parser.add_argument('--json', required=True)
parser.add_argument('--java', default='java')
args=parser.parse_args()
r=Path(__file__).resolve().parents[2]
temporary=tempfile.TemporaryDirectory(prefix='nicoid-audit-')
w=Path(temporary.name)
sources={
'e/e/a/CacheFile.java':'''package e.e.a; import java.io.*; public class CacheFile extends File { static boolean fail; public CacheFile(String p){super(p);} public CacheFile(String p,String n){super(p,n);} public boolean renameTo(File f){return !fail && super.renameTo(f);} }''',
'e/e/a/CacheFolders.java':'''package e.e.a; class CacheFolders {static String videoId(String s){return s.split("\\\\.")[0];}}''',
'e/e/a/CacheInputStream.java':'''package e.e.a; import java.io.*; class CacheInputStream extends FileInputStream {CacheInputStream(File f)throws IOException{super(f);}}''',
'e/e/a/CacheOutputStream.java':'''package e.e.a; import java.io.*; class CacheOutputStream extends FileOutputStream {CacheOutputStream(File f)throws IOException{super(f);}}''',
'android/util/Log.java':'''package android.util; public class Log {public static int w(String a,String b,Throwable t){return 0;}}''',
'e/e/a/AuditReproduction.java':'''package e.e.a; import java.io.*;import java.net.*;import java.nio.file.*;
public class AuditReproduction { public static void main(String[] args)throws Exception {
 File root=Files.createTempDirectory("comment-repro").toFile(); String id="sm1"; Files.write(new File(root,id+".mp4").toPath(),new byte[]{1});
 String old="{\\"threads\\":[{\\"comments\\":[]}]}";CacheComments.save(root.toString(),id,old);if(!old.equals(CacheComments.read(root.toString(),id)))throw new AssertionError("fixture setup");
 CacheFile.fail=true; CacheComments.save(root.toString(),id,"{\\"threads\\":[]}"); boolean lost=CacheComments.read(root.toString(),id)==null;
 if(!lost)throw new AssertionError("expected current defect");System.out.println("REPRODUCED: rename failure removes previous comment cache");
 long actual=CacheTransferPlan.bytes(new URL("https://example.test/v.m3u8"),u->"#EXTM3U\\n#EXT-X-BYTERANGE:100@0\\nv.mp4\\n#EXT-X-BYTERANGE:100\\nv.mp4\\n#EXT-X-BYTERANGE:100\\nv.mp4\\n#EXT-X-ENDLIST\\n");
 if(actual!=200)throw new AssertionError(actual);System.out.println("REPRODUCED: implicit byte ranges total 300 but plan reports "+actual);
 boolean crash=false;try{new LocalPlaylistData("{\\"folders\\":[null]}").hasFolder("test");}catch(NullPointerException e){crash=true;}
 if(!crash)throw new AssertionError();System.out.println("REPRODUCED: malformed playlist row causes NullPointerException");
 for(File f:root.listFiles())f.delete();root.delete();
 }}'''}
for name,body in sources.items():
 p=w/'src'/name;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(body)
java=args.java; ecj=str(Path(args.ecj).resolve()); json=str(Path(args.json).resolve())
baseline=w/'baseline/e/e/a';baseline.mkdir(parents=True)
for name in ['CacheComments','CacheTransferPlan','LocalPlaylistData','HistoryRules']:
    source=subprocess.check_output(['git','show','v1.8.4:extensions/extension/src/main/java/e/e/a/'+name+'.java'],cwd=r,text=True)
    (baseline/(name+'.java')).write_text(source)
files=list((w/'src').rglob('*.java'))+list(baseline.glob('*.java'))
subprocess.run([java,'-jar',ecj,'-1.8','-warn:none','-cp',json,'-d',str(w/'classes'),*map(str,files)],check=True)
result=subprocess.run([java,'-cp',str(w/'classes')+':'+json,'e.e.a.AuditReproduction'],capture_output=True,text=True,check=True)
print(result.stdout, end='')
temporary.cleanup()

"""Link checks use the applied APK translated to JVM bytecode; no native stubs."""
from pathlib import Path
from zipfile import ZipFile
import argparse,subprocess,glob
p=argparse.ArgumentParser();p.add_argument('--classpath',type=Path,required=True);p.add_argument('--sdk-dir',type=Path,required=True);p.add_argument('--ecj',type=Path,required=True);p.add_argument('--work',type=Path,required=True);p.add_argument('--apk-jar',type=Path,required=True);p.add_argument('--extra',default='');a=p.parse_args();source=Path(__file__).parent;w=a.work.resolve();w.mkdir(parents=True,exist_ok=True);cp=a.classpath.read_text().strip()+':'+str(a.sdk_dir.resolve()/'android-all-14-robolectric-10818077.jar')+':'+str(a.apk_jar.resolve())+(':'+':'.join(f for item in a.extra.split(':') for f in sorted(glob.glob(item))) if a.extra else '')
entries=[]
for item in cp.split(':'):
 path=Path(item)
 if path.suffix=='.aar':
  target=w/(path.stem+'-classes.jar');target.write_bytes(ZipFile(path).read('classes.jar'));entries.append(str(target))
 else:entries.append(item)
cp=':'.join(entries)

subprocess.run(['java','-jar',str(a.ecj),'-1.8','-warn:none','-cp',cp,'-d',str(w/'classes'),str(source/'NativeLinkTest.java'),str(source/'CastNetwork194Test.java'),str(source/'NativeAuth194Test.java'),str(source/'NativeVideoTitle194Test.java'),str(source/'NativePreference194Test.java'),str(source/'NativeThemeResume194Test.java')],check=True)
subprocess.run(['java','-Drobolectric.offline=true','-Drobolectric.usePreinstrumentedJars=false','-Drobolectric.dependency.dir='+str(a.sdk_dir.resolve()),'-cp',str(w/'classes')+':'+cp,'org.junit.runner.JUnitCore','e.e.a.NativeLinkTest','e.e.a.CastNetwork194Test','e.e.a.NativeAuth194Test','e.e.a.NativeVideoTitle194Test','e.e.a.NativePreference194Test','e.e.a.NativeThemeResume194Test'],check=True)

from pathlib import Path
from zipfile import ZipFile
import argparse,subprocess
p=argparse.ArgumentParser();p.add_argument('--jar',required=True);p.add_argument('--work',type=Path,required=True);p.add_argument('--runtime',type=Path,required=True);a=p.parse_args();a.work.mkdir(parents=True,exist_ok=True);entries=[]
for item in (a.runtime/'classpath.txt').read_text().strip().split(':'):
 f=Path(item)
 if f.suffix=='.aar':
  target=a.work/(f.stem+'-classes.jar');target.write_bytes(ZipFile(f).read('classes.jar'));entries.append(str(target))
 else:entries.append(item)
entries += [str(f) for pattern in ['httpclient-*.jar','httpcore-*.jar','commons-logging-*.jar','commons-codec-*.jar'] for f in (a.runtime.parent/'tools/apache-maven-3.9.9/lib').glob(pattern)]
cp=':'.join(entries)+':'+str(a.runtime.parent/'tools/sdk/android-all-14-robolectric-10818077.jar')+':'+a.jar
subprocess.run(['java','-jar',str(a.runtime.parent/'tools/ecj.jar'),'-1.8','-warn:none','-cp',cp,'-d',str(a.work/'classes'),*map(str,Path(__file__).parent.glob('*Test.java'))],check=True)
subprocess.run(['java','-Drobolectric.offline=true','-Drobolectric.usePreinstrumentedJars=false','-Drobolectric.dependency.dir='+str(a.runtime.parent/'tools/sdk'),'-cp',str(a.work/'classes')+':'+cp,'org.junit.runner.JUnitCore','e.e.a.NativePlayback195Test','e.e.a.NativePopup195Test','e.e.a.NativeCast195Test'],check=True)

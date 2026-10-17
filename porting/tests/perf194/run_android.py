"""Run isolated framework thumbnail lifecycle checks with no external traffic."""
from pathlib import Path
from zipfile import ZipFile
import argparse,subprocess
p=argparse.ArgumentParser();p.add_argument('--classpath',type=Path,required=True);p.add_argument('--sdk-dir',type=Path,required=True);p.add_argument('--ecj',required=True);p.add_argument('--work',type=Path,required=True);p.add_argument('--compiled-sources',type=Path);p.add_argument('--source-dir',type=Path);a=p.parse_args()
source=Path(__file__).parent;repo=source.parents[2];w=a.work.resolve();w.mkdir(parents=True,exist_ok=True);jars=[]
for item in a.classpath.read_text().strip().split(':'):
 f=Path(item)
 if f.suffix=='.aar':
  out=w/(f.stem+'-classes.jar');out.write_bytes(ZipFile(f).read('classes.jar'));jars.append(str(out))
 else:jars.append(str(f.resolve()))
jars.append(str(a.sdk_dir.resolve()/'android-all-14-robolectric-10818077.jar'));cp=':'.join(jars)
java_source=a.source_dir.resolve() if a.source_dir else repo/'extensions/extension/src/main/java/e/e/a'
files=[java_source/(n+'.java') for n in ['ShortImages','ImageDiskCache','NetworkTask']]+[source/'ThumbnailLifecycleTest.java']
tests=['e.e.a.ThumbnailLifecycleTest']
if a.compiled_sources:
 cp+=':'+str(a.compiled_sources.resolve());files += [source/'ExistingFrameworkTest.java',repo/'porting/tests/RankingPaging190Test.java'];tests.append('e.e.a.ExistingFrameworkTest')
subprocess.run(['java','-jar',a.ecj,'-1.8','-warn:none','-encoding','UTF-8','-cp',cp,'-d',str(w/'classes'),*map(str,files)],check=True)
subprocess.run(['java','-Drobolectric.offline=true','-Drobolectric.usePreinstrumentedJars=false','-Drobolectric.dependency.dir='+str(a.sdk_dir.resolve()),'-cp',str(w/'classes')+':'+cp,'org.junit.runner.JUnitCore',*tests],check=True)

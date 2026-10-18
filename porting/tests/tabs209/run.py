"""Run framework layout tests using a Maven-resolved Robolectric classpath.

Resolve dependencies using the adjacent pom.xml. Pass the generated classpath
file, ECJ jar and a directory containing Android-all 9 and 14 SDK jars.
"""
from pathlib import Path
from zipfile import ZipFile
import argparse,subprocess,shutil
p=argparse.ArgumentParser();p.add_argument('--classpath',type=Path,required=True);p.add_argument('--sdk-dir',type=Path,required=True);p.add_argument('--ecj',type=Path,required=True);p.add_argument('--helpers',type=Path,required=True);p.add_argument('--work',type=Path,required=True);a=p.parse_args()
source=Path(__file__).resolve().parent;repo=source.parents[2];w=a.work.resolve();w.mkdir(parents=True,exist_ok=True)
classpath=[]
for item in a.classpath.read_text().strip().split(':'):
 f=Path(item)
 if f.suffix=='.aar':
  dest=w/(f.stem+'-classes.jar');dest.write_bytes(ZipFile(f).read('classes.jar'));classpath.append(str(dest))
 else:classpath.append(str(f.resolve()))
classpath.append(str(a.sdk_dir.resolve()/'android-all-14-robolectric-10818077.jar'));cp=':'.join(classpath)
classes=w/'classes'
if classes.exists():shutil.rmtree(classes)
sources=[repo/'extensions/extension/src/main/java/e/e/a'/name for name in ['FollowFeed.java','FeedRefresh209.java','Following190.java','TabViewport110.java','FollowFeedData.java','NetworkTask.java','Ui110.java','PanelUi.java','SessionCookies194.java','FeedLayout194.java']] + list(source.glob('*.java'))
cp+=':'+str(a.helpers.resolve())
subprocess.run(['java','-jar',str(a.ecj),'-1.8','-warn:none','-cp',cp,'-d',str(classes),*map(str,sources)],check=True)
subprocess.run(['java','-Drobolectric.offline=true','-Dnicoid.test.image='+str(w/'strip.png'),'-Drobolectric.usePreinstrumentedJars=false','-Drobolectric.dependency.dir='+str(a.sdk_dir.resolve()),'-cp',str(classes)+':'+cp.rsplit(':',1)[0],'org.junit.runner.JUnitCore','e.e.a.TabSession209Test'],check=True)

"""Run framework layout tests using a Maven-resolved Robolectric classpath.

Resolve dependencies using the adjacent pom.xml. Pass the generated classpath
file, ECJ jar and a directory containing Android-all 9 and 14 SDK jars.
"""
from pathlib import Path
from zipfile import ZipFile
import argparse,subprocess,shutil
p=argparse.ArgumentParser();p.add_argument('--classpath',type=Path,required=True);p.add_argument('--sdk-dir',type=Path,required=True);p.add_argument('--ecj',type=Path,required=True);p.add_argument('--work',type=Path,required=True);a=p.parse_args()
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
icons=(repo/'extensions/extension/src/main/java/e/e/a/PlayerIcons.java').read_text();start=icons.index(' static final class Icon extends Drawable {');icon_source=w/'PlayerIcons.java';icon_source.write_text('package e.e.a;import android.graphics.*;import android.graphics.drawable.Drawable;public final class PlayerIcons {\n'+icons[start:])
sources=[repo/'extensions/extension/src/main/java/e/e/a/FullscreenViewport204.java',repo/'extensions/extension/src/main/java/e/e/a/FullscreenViewport205.java',repo/'extensions/extension/src/main/java/e/e/a/FullscreenViewport207.java',repo/'extensions/extension/src/main/java/e/e/a/FullscreenViewport208.java',*source.glob('*.java'),icon_source]
subprocess.run(['java','-jar',str(a.ecj),'-1.8','-warn:none','-cp',cp,'-d',str(classes),*map(str,sources)],check=True)
subprocess.run(['java','-Drobolectric.offline=true','-Dnicoid.test.image='+str(w/'strip.png'),'-Drobolectric.usePreinstrumentedJars=false','-Drobolectric.dependency.dir='+str(a.sdk_dir.resolve()),'-cp',str(classes)+':'+cp,'org.junit.runner.JUnitCore','e.e.a.FullscreenLayout208Test'],check=True)

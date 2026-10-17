"""Run Android framework regressions against the exact emitted helper DEX jar."""
from pathlib import Path
import argparse,subprocess
p=argparse.ArgumentParser();p.add_argument('--classpath',type=Path,required=True);p.add_argument('--sdk-dir',type=Path,required=True);p.add_argument('--ecj',type=Path,required=True);p.add_argument('--work',type=Path,required=True);p.add_argument('--runtime-helpers',type=Path,required=True);a=p.parse_args();tests=Path(__file__).resolve().parent;w=a.work.resolve();w.mkdir(parents=True,exist_ok=True)
cp=a.classpath.read_text().strip()+':'+str(a.sdk_dir.resolve()/'android-all-14-robolectric-10818077.jar')+':'+str(a.runtime_helpers.resolve())
files=[f for f in tests.glob('*Test.java') if f.name not in ['NativeLinkTest.java','I18nAudit194Test.java','CastNetwork194Test.java']]+list((tests/'fixtures').rglob('*.java'))
subprocess.run(['java','-jar',str(a.ecj),'-1.8','-warn:none','-encoding','UTF-8','-cp',cp,'-d',str(w/'classes'),*map(str,files)],check=True)
subprocess.run(['java','-Drobolectric.offline=true','-Drobolectric.usePreinstrumentedJars=false','-Drobolectric.dependency.dir='+str(a.sdk_dir.resolve()),'-Dnicoid.test.output='+str(w),'-cp',str(w/'classes')+':'+cp,'org.junit.runner.JUnitCore',*['e.e.a.'+f.stem for f in tests.glob('*Test.java') if f.name not in ['NativeLinkTest.java','I18nAudit194Test.java','CastNetwork194Test.java']]],check=True)

"""Build the reviewed native fixes and canonical Java helper families over v1.9.4.
Payload build; retains the v1.9.4 patch engine. Updates tracked payloads/snapshots.
"""
from pathlib import Path
from zipfile import ZipFile,ZIP_DEFLATED
import subprocess,shutil,re,hashlib,json,time
import argparse
parser=argparse.ArgumentParser();parser.add_argument('--base',type=Path,required=True);parser.add_argument('--tools',type=Path,required=True);parser.add_argument('--work',type=Path,required=True);parser.add_argument('--output',type=Path,required=True);args=parser.parse_args()
repo=Path(__file__).resolve().parents[3];w=args.work.resolve();w.mkdir(parents=True,exist_ok=True);tools=args.tools.resolve();build=w/'build';build.mkdir(exist_ok=True)
assert ZipFile(args.base).read('META-INF/MANIFEST.MF').find(b'Version: 1.9.4\r\n')>=0, 'Use the official v1.9.4 MPP'
run_java_sources=sorted((repo/'extensions/extension/src/main/java').rglob('*.java'))
subprocess.run(['java','-jar',str(tools/'ecj.jar'),'-1.8','-warn:none','-encoding','UTF-8','-cp',str(tools/'sdk/android-all-14-robolectric-10818077.jar'),'-d',str(w/'classes'),*map(str,run_java_sources)],check=True)
cp=':'.join(str(tools/(n+'.jar')) for n in ['baksmali','smali','dexlib2','guava','jcommander','util','antlr','antlr-runtime','stringtemplate'])
def run(*a):subprocess.run(list(map(str,a)),check=True)
source=repo/'extensions/extension/src/main/java/e/e/a';families={'FollowFeed','FullscreenViewport209','PopupDrag195','CastDiagnostics'}
# Keep source, canonical snapshots and emitted helpers in agreement.
snapshot=repo/'porting/helpers-v1.9.3.zip'
with ZipFile(snapshot) as z:entries={n:z.read(n) for n in z.namelist() if Path(n).stem.split('$')[0] not in families}
for n in ['ModernControls$Apply','ModernControls$Switch']:entries['e/e/a/'+n+'.smali']=(repo/'porting/helpers-smali/e/e/a'/(n+'.smali')).read_bytes()
with ZipFile(snapshot,'w',ZIP_DEFLATED) as z:
 for n,b in sorted(entries.items()):z.writestr(n,b)
manifest=repo/'porting/helper-ownership.json';owners=json.loads(manifest.read_text())
for n in families:owners[n]={'owner':'java'}
manifest.write_text(json.dumps(dict(sorted(owners.items())),indent=2)+'\n')
for f in (repo/'porting/helpers-smali/e/e/a').glob('*.smali'):
 if f.stem.split('$')[0] in families:f.unlink()
# Compile selected complete families together; other supplied classes are compile dependencies only.
classes=build/'classes';classes.mkdir(exist_ok=True)
run('java','-jar',tools/'ecj.jar','-1.8','-warn:none','-encoding','UTF-8','-cp',str(tools/'sdk/android-all-14-robolectric-10818077.jar')+':'+str(w/'classes'),'-d',classes,*[source/(n+'.java') for n in sorted(families)])
with ZipFile(build/'classes.jar','w') as z:
 for f in classes.rglob('*.class'):z.write(f,str(f.relative_to(classes)))
dex=build/'dex';dex.mkdir(exist_ok=True)
run('java','-cp',tools/'r8.jar','com.android.tools.r8.D8','--min-api','21','--lib',tools/'sdk/android-all-14-robolectric-10818077.jar','--lib',Path(shutil.which('java')).resolve().parents[1],'--output',dex,build/'classes.jar')
with ZipFile(args.base) as z:
 for name in ['helpers.mpe','method-delta.dex']:(build/name).write_bytes(z.read('nicoid/'+name))
for name in ['helpers.mpe','method-delta.dex']:
 target=build/name.replace('.','-')
 if target.exists():shutil.rmtree(target)
 run('java','-cp',cp,'org.jf.baksmali.Main','d',build/name,'-o',target)
gen=build/'generated';shutil.rmtree(gen,ignore_errors=True);run('java','-cp',cp,'org.jf.baksmali.Main','d',dex/'classes.dex','-o',gen)
helpers=build/'helpers-mpe'
for f in (helpers/'e/e/a').glob('*.smali'):
 if f.stem.split('$')[0] in families:f.unlink()
for f in (gen/'e/e/a').glob('*.smali'):shutil.copy2(f,helpers/'e/e/a'/f.name)
for n in ['ModernControls$Apply','ModernControls$Switch']:shutil.copy2(repo/'porting/helpers-smali/e/e/a'/(n+'.smali'),helpers/'e/e/a'/(n+'.smali'))
# Version string only; preserve all other ModernShorts behavior.
for f in (helpers/'e/e/a').glob('ModernShorts*.smali'):f.write_text(f.read_text().replace('v1.9.4 @chikuwadon','v1.9.5-dev.1 @chikuwadon'))
# Replace reviewed native methods while retaining all other patched methods.
delta=build/'method-delta-dex'
for f in (repo/'porting/method-overrides').rglob('*.smali'):
 target=delta/f.relative_to(repo/'porting/method-overrides');new=f.read_text()
 if target.exists():
  text=target.read_text()
  for m in re.finditer(r'^\.method[^\n]*\n.*?^\.end method',new,re.M|re.S):
   sig=m[0].splitlines()[0];pattern=r'^'+re.escape(sig)+r'\n.*?^\.end method'
   if re.search(pattern,text,re.M|re.S):text=re.sub(pattern,lambda _:m[0],text,flags=re.M|re.S)
   else:text+='\n'+m[0]+'\n'
 else:text=new
 target.parent.mkdir(parents=True,exist_ok=True);target.write_text(text)
for folder,out in [(helpers,'helpers.mpe'),(delta,'method-delta.dex')]:
 run('java','-cp',cp,'org.jf.smali.Main','a',folder,'-o',build/('final-'+out));shutil.copy2(build/('final-'+out),repo/'patches/src/main/resources/nicoid'/out)
out=args.output.resolve();out.parent.mkdir(parents=True,exist_ok=True)
with ZipFile(args.base) as z,ZipFile(out,'w',ZIP_DEFLATED) as final:
 for info in z.infolist():
  data=z.read(info.filename)
  if info.filename in ['nicoid/helpers.mpe','nicoid/method-delta.dex']:data=(build/('final-'+info.filename.split('/')[-1])).read_bytes()
  elif info.filename=='META-INF/MANIFEST.MF':data=data.replace(b'Version: 1.9.4',b'Version: 1.9.5-dev.1');data=re.sub(rb'Timestamp: \d+',('Timestamp: '+str(int(time.time()*1000))).encode(),data)
  final.writestr(info,data)
print('Output',out,hashlib.sha256(out.read_bytes()).hexdigest())

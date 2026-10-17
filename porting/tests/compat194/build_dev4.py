"""Build the theme lifecycle fix over the verified dev.3 MPP, retaining its engine."""
from pathlib import Path
from zipfile import ZipFile,ZIP_DEFLATED
import argparse,hashlib,subprocess,shutil,re,time,json
p=argparse.ArgumentParser();p.add_argument('--base',type=Path,required=True);p.add_argument('--tools',type=Path,required=True);p.add_argument('--generated-smali',type=Path,required=True);p.add_argument('--work',type=Path,required=True);p.add_argument('--output',type=Path,required=True);p.add_argument('--update-payload',action='store_true');a=p.parse_args()
assert hashlib.sha256(a.base.read_bytes()).hexdigest()=='765ac3eac6c6ca6030a5bff1e807df6e633494acfbd805bf76e5bf99c14283bd'
repo=Path(__file__).resolve().parents[3];w=a.work.resolve();w.mkdir(parents=True,exist_ok=True);tools=a.tools.resolve();cp=':'.join(str(tools/(n+'.jar')) for n in ['baksmali','smali','dexlib2','guava','jcommander','util','antlr','antlr-runtime','stringtemplate'])
with ZipFile(a.base) as z:(w/'before.dex').write_bytes(z.read('nicoid/helpers.mpe'))
helpers=w/'helpers'
if helpers.exists():shutil.rmtree(helpers)
subprocess.run(['java','-cp',cp,'org.jf.baksmali.Main','d',str(w/'before.dex'),'-o',str(helpers)],check=True)
folder=helpers/'e/e/a'
for f in folder.glob('ThemeChoice*.smali'):
 if f.stem.split('$',1)[0]=='ThemeChoice':f.unlink()
changed=[]
for f in a.generated_smali.resolve().glob('ThemeChoice*.smali'):
 if f.stem.split('$',1)[0]=='ThemeChoice':shutil.copy2(f,folder/f.name);changed.append(f.name)
assert 'ThemeChoice$Refresh.smali' in changed
for f in folder.glob('ModernShorts*.smali'):f.write_text(f.read_text().replace('v1.9.4-dev.3 @chikuwadon','v1.9.4-dev.4 @chikuwadon'))
subprocess.run(['java','-cp',cp,'org.jf.smali.Main','a',str(helpers),'-o',str(w/'helpers.dex')],check=True)
if a.update_payload:
 # The canonical Java-owned family is replaced as a unit. Other families retain
 # the repository's current methods and hashes rather than this package's layout.
 target=w/'repository-helpers';subprocess.run(['java','-cp',cp,'org.jf.baksmali.Main','d',str(repo/'patches/src/main/resources/nicoid/helpers.mpe'),'-o',str(target)],check=True)
 for f in (target/'e/e/a').glob('ThemeChoice*.smali'):
  if f.stem.split('$',1)[0]=='ThemeChoice':f.unlink()
 for name in changed:shutil.copy2(folder/name,target/'e/e/a'/name)
 subprocess.run(['java','-cp',cp,'org.jf.smali.Main','a',str(target),'-o',str(repo/'patches/src/main/resources/nicoid/helpers.mpe')],check=True)
a.output.parent.mkdir(parents=True,exist_ok=True)
with ZipFile(a.base) as src,ZipFile(a.output,'w',ZIP_DEFLATED) as out:
 for info in src.infolist():
  data=src.read(info.filename)
  if info.filename=='nicoid/helpers.mpe':data=(w/'helpers.dex').read_bytes()
  elif info.filename=='META-INF/MANIFEST.MF':data=data.replace(b'Version: 1.9.4-dev.3',b'Version: 1.9.4-dev.4');data=re.sub(rb'Timestamp: \d+',('Timestamp: '+str(int(time.time()*1000))).encode(),data)
  out.writestr(info,data)
with ZipFile(a.base) as before,ZipFile(a.output) as after:
 assert after.testzip() is None
 assert {n for n in before.namelist() if before.read(n)!=after.read(n)}=={'nicoid/helpers.mpe','META-INF/MANIFEST.MF'}
print(json.dumps({'build':'payload-only; engine and native fixes retained','sha256':hashlib.sha256(a.output.read_bytes()).hexdigest(),'bytes':a.output.stat().st_size,'theme_family':sorted(changed)},indent=2))

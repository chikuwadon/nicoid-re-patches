"""Rebuild from the exact distributed dev.7, preserving all unrelated payloads."""
from pathlib import Path
from zipfile import ZipFile,ZIP_DEFLATED
import argparse,hashlib,json,shutil,subprocess,sys
p=argparse.ArgumentParser();p.add_argument('--base',type=Path,required=True);p.add_argument('--tools',type=Path,required=True);p.add_argument('--work',type=Path,required=True);p.add_argument('--output',type=Path,required=True);a=p.parse_args()
revision=Path(__file__).resolve().parent;repo=revision.parents[2];t=a.tools.resolve();w=a.work.resolve();w.mkdir(parents=True,exist_ok=True)
meta=json.loads((revision/'manifest.json').read_text());assert hashlib.sha256(a.base.read_bytes()).hexdigest()==meta['base_sha256']
def run(*cmd):subprocess.run(list(map(str,cmd)),check=True)
with ZipFile(a.base) as z:(w/'base.dex').write_bytes(z.read('nicoid/helpers.mpe'))
if (w/'helpers').exists():shutil.rmtree(w/'helpers')
run('java','-cp',str(t/'*'),'org.jf.baksmali.Main','d',w/'base.dex','-o',w/'helpers')
run(sys.executable,revision/'patch.py',w/'helpers')
classes=w/'classes';dex=w/'dex'
for directory in [classes,dex,w/'viewport']:
 if directory.exists():shutil.rmtree(directory)
 directory.mkdir()
run(sys.executable,repo/'porting/tools/helper_compile_stubs.py',w/'helpers',w/'compile-helpers.jar')
run('java','-jar',t/'ecj.jar','-1.8','-warn:none','-encoding','UTF-8','-cp',str(t/'android.jar')+':'+str(w/'compile-helpers.jar'),'-d',classes,*[repo/'extensions/extension/src/main/java/e/e/a'/name for name in ['FullscreenViewport208.java','TabRefresh208.java','FollowFeed.java','Following190.java']])
with ZipFile(w/'viewport.jar','w') as z:
 for f in classes.rglob('*.class'):z.write(f,f.relative_to(classes))
run('java','-cp',t/'r8.jar','com.android.tools.r8.D8','--lib',t/'android.jar','--lib',Path(shutil.which('java')).resolve().parents[1],'--classpath',w/'compile-helpers.jar','--min-api','23','--output',dex,w/'viewport.jar')
run('java','-cp',str(t/'*'),'org.jf.baksmali.Main','d',dex/'classes.dex','-o',w/'viewport')
for family in ['FollowFeed','Following190']:
 for old in (w/'helpers/e/e/a').glob(family+'*.smali'):
  if old.name==family+'.smali' or old.name.startswith(family+'$'):old.unlink()
shutil.copytree(w/'viewport',w/'helpers',dirs_exist_ok=True)
run('java','-cp',str(t/'*'),'org.jf.smali.Main','a',w/'helpers','-o',w/'helpers.dex')
with ZipFile(a.base) as src,ZipFile(a.output,'w',ZIP_DEFLATED) as out:
 for info in src.infolist():
  data=src.read(info.filename)
  if info.filename=='nicoid/helpers.mpe':data=(w/'helpers.dex').read_bytes()
  if info.filename=='META-INF/MANIFEST.MF':data=data.replace(b'1.9.3-dev.7',meta['version'].encode())
  out.writestr(info,data)
with ZipFile(a.output) as z:assert z.testzip() is None
print(a.output,hashlib.sha256(a.output.read_bytes()).hexdigest())

"""Rebuild this local verification revision without modifying the release project."""
from pathlib import Path
from zipfile import ZipFile, ZIP_DEFLATED
import argparse,hashlib,json,shutil,subprocess

p=argparse.ArgumentParser()
p.add_argument('--base',type=Path,required=True)
p.add_argument('--tools',type=Path,required=True,help='Directory containing ecj.jar, r8.jar and android.jar')
p.add_argument('--smali-classpath',required=True)
p.add_argument('--compile-helpers',type=Path,required=True,help='Existing Java helper classes used only for compilation')
p.add_argument('--work',type=Path,required=True)
p.add_argument('--output',type=Path,required=True)
a=p.parse_args();source=Path(__file__).resolve().parent;meta=json.loads((source/'manifest.json').read_text())
assert hashlib.sha256(a.base.read_bytes()).hexdigest()==meta['base_sha256'],'Unexpected baseline MPP'
w=a.work.resolve();w.mkdir(parents=True,exist_ok=True)
def run(*cmd):subprocess.run(list(map(str,cmd)),check=True)
with ZipFile(a.base) as z:
 for entry,name in [('nicoid/helpers.mpe','helpers'),('nicoid/method-delta.dex','delta')]:
  (w/(name+'.dex')).write_bytes(z.read(entry))
  run('java','-cp',a.smali_classpath,'org.jf.baksmali.Main','d',w/(name+'.dex'),'-o',w/name)
shutil.copytree(source/'overrides',w/'helpers',dirs_exist_ok=True)
shutil.copytree(source/'native',w/'delta',dirs_exist_ok=True)
for f in (w/'helpers').rglob('*.smali'):
 s=f.read_text();f.write_text(s.replace('1.9.2-dev.5',meta['version']))
(w/'classes').mkdir(exist_ok=True);(w/'d8').mkdir(exist_ok=True)
run('java','-jar',a.tools/'ecj.jar','-1.8','-warn:none','-encoding','UTF-8','-cp',str(a.tools/'android.jar')+':'+str(a.compile_helpers),'-d',w/'classes',*sorted((source/'src').rglob('*.java')))
with ZipFile(w/'revision.jar','w',ZIP_DEFLATED) as z:
 for f in (w/'classes').rglob('*.class'):z.write(f,f.relative_to(w/'classes'))
run('java','-cp',a.tools/'r8.jar','com.android.tools.r8.D8','--min-api','21','--lib',a.tools/'android.jar','--output',w/'d8',w/'revision.jar')
run('java','-cp',a.smali_classpath,'org.jf.baksmali.Main','d',w/'d8/classes.dex','-o',w/'compiled-smali')
shutil.copytree(w/'compiled-smali',w/'helpers',dirs_exist_ok=True)
for name in ('helpers','delta'):run('java','-cp',a.smali_classpath,'org.jf.smali.Main','a',w/name,'-o',w/(name+'-final.dex'))
changes={'nicoid/helpers.mpe':(w/'helpers-final.dex').read_bytes(),'nicoid/method-delta.dex':(w/'delta-final.dex').read_bytes()}
with ZipFile(a.base) as src,ZipFile(a.output,'w',ZIP_DEFLATED) as out:
 for entry in src.infolist():
  data=changes.get(entry.filename,src.read(entry.filename))
  if entry.filename.endswith('.class') or entry.filename=='META-INF/MANIFEST.MF':data=data.replace(b'1.9.2-dev.5',meta['version'].encode())
  out.writestr(entry,data)
with ZipFile(a.output) as z:assert z.testzip() is None
print(a.output,hashlib.sha256(a.output.read_bytes()).hexdigest())

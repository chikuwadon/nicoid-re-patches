from pathlib import Path
from zipfile import ZipFile, ZIP_DEFLATED
import argparse,hashlib,json,shutil,subprocess
p=argparse.ArgumentParser();p.add_argument('--base',type=Path,required=True);p.add_argument('--smali-classpath',required=True);p.add_argument('--work',type=Path,required=True);p.add_argument('--output',type=Path,required=True);a=p.parse_args()
source=Path(__file__).resolve().parent;meta=json.loads((source/'manifest.json').read_text());assert hashlib.sha256(a.base.read_bytes()).hexdigest()==meta['base_sha256'];w=a.work.resolve();w.mkdir(parents=True,exist_ok=True)
with ZipFile(a.base) as z:(w/'helpers.dex').write_bytes(z.read('nicoid/helpers.mpe'))
subprocess.run(['java','-cp',a.smali_classpath,'org.jf.baksmali.Main','d',str(w/'helpers.dex'),'-o',str(w/'helpers')],check=True)
shutil.copytree(source/'overrides',w/'helpers',dirs_exist_ok=True)
subprocess.run(['java','-cp',a.smali_classpath,'org.jf.smali.Main','a',str(w/'helpers'),'-o',str(w/'updated.dex')],check=True)
with ZipFile(a.base) as src,ZipFile(a.output,'w',ZIP_DEFLATED) as out:
 for entry in src.infolist():
  data=(w/'updated.dex').read_bytes() if entry.filename=='nicoid/helpers.mpe' else src.read(entry.filename)
  if entry.filename.endswith('.class') or entry.filename=='META-INF/MANIFEST.MF':data=data.replace(b'1.9.2-dev.7',meta['version'].encode())
  out.writestr(entry,data)
with ZipFile(a.output) as z:assert z.testzip() is None
print(a.output,hashlib.sha256(a.output.read_bytes()).hexdigest())

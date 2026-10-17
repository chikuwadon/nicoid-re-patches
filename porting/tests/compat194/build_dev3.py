"""Assemble reviewed native-method and settings fixes over the delivered dev.2 MPP.

The patch-engine classes are retained byte-for-byte. This is a payload build,
not a Gradle build. Final validation must apply the output using Morphe.
"""
from pathlib import Path
from zipfile import ZipFile,ZIP_DEFLATED
import argparse,hashlib,subprocess,shutil,re,time,json
p=argparse.ArgumentParser();p.add_argument('--base',type=Path,required=True);p.add_argument('--tools',type=Path,required=True);p.add_argument('--work',type=Path,required=True);p.add_argument('--output',type=Path,required=True);a=p.parse_args()
assert hashlib.sha256(a.base.read_bytes()).hexdigest()=='64573bb2ecc8ec75b842600000767ed01ecf29989e2cf2459bfe68530630b425'
repo=Path(__file__).resolve().parents[3];w=a.work.resolve();w.mkdir(parents=True,exist_ok=True);tools=a.tools.resolve()
cp=':'.join(str(tools/(n+'.jar')) for n in ['baksmali','smali','dexlib2','guava','jcommander','util','antlr','antlr-runtime','stringtemplate'])
def run(*args):subprocess.run(list(map(str,args)),check=True)
with ZipFile(a.base) as z:
 for name in ['method-delta.dex','helpers.mpe']:(w/name).write_bytes(z.read('nicoid/'+name))
for stem,name in [('delta','method-delta.dex'),('helpers','helpers.mpe')]:
 if (w/stem).exists():shutil.rmtree(w/stem)
 run('java','-cp',cp,'org.jf.baksmali.Main','d',w/name,'-o',w/stem)
# Native metadata callback: fullscreen title is outside x1, but in the same window.
native=w/'delta/com/sauzask/nicoid/NicoidVideoFragment$c0.smali';s=native.read_text()
needle='    invoke-virtual {v0, v1}, Landroid/view/View;->findViewById(I)Landroid/view/View;';assert s.count(needle)==1
s=s.replace(needle,'''    if-nez v0, :title_window_ready
    return-void
    :title_window_ready
    invoke-virtual {v0}, Landroid/view/View;->getRootView()Landroid/view/View;
    move-result-object v0
'''+needle)
s=s.replace('    check-cast v0, Landroid/widget/TextView;','    if-eqz v0, :title_metadata\n\n    check-cast v0, Landroid/widget/TextView;',1)
s=s.replace('    iget-object v0, p0, Lcom/sauzask/nicoid/NicoidVideoFragment$c0;->b:Landroid/os/Bundle;','    :title_metadata\n    iget-object v0, p0, Lcom/sauzask/nicoid/NicoidVideoFragment$c0;->b:Landroid/os/Bundle;',1)
native.write_text(s)
# Only the two audited canonical helper methods change; preserve all other
# delivered families, including snapshot-owned behavior and Java-owned code.
with ZipFile(repo/'porting/helpers-v1.9.3.zip') as z:
 for name,sig in [('UiText','preferences(Landroid/preference/Preference;)V'),('Followup173','translatePreferences(Landroid/preference/PreferenceGroup;)V')]:
  path='e/e/a/'+name+'.smali';target=w/'helpers'/path;before=target.read_text();source=z.read(path).decode();pat=r'^\.method[^\n]* '+re.escape(sig)+r'\n.*?^\.end method'
  old=list(re.finditer(pat,before,re.M|re.S));new=list(re.finditer(pat,source,re.M|re.S));assert len(old)==len(new)==1
  target.write_text(before[:old[0].start()]+new[0].group()+before[old[0].end():])
for f in (w/'helpers/e/e/a').glob('ModernShorts*.smali'):f.write_text(f.read_text().replace('v1.9.4-dev.2 @chikuwadon','v1.9.4-dev.3 @chikuwadon'))
run('java','-cp',cp,'org.jf.smali.Main','a',w/'delta','-o',w/'updated-delta.dex')
run('java','-cp',cp,'org.jf.smali.Main','a',w/'helpers','-o',w/'updated-helpers.dex')
a.output.parent.mkdir(parents=True,exist_ok=True)
with ZipFile(a.base) as src,ZipFile(a.output,'w',ZIP_DEFLATED) as out:
 for info in src.infolist():
  data=src.read(info.filename)
  if info.filename=='nicoid/method-delta.dex':data=(w/'updated-delta.dex').read_bytes()
  elif info.filename=='nicoid/helpers.mpe':data=(w/'updated-helpers.dex').read_bytes()
  elif info.filename=='META-INF/MANIFEST.MF':data=data.replace(b'Version: 1.9.4-dev.2',b'Version: 1.9.4-dev.3');data=re.sub(rb'Timestamp: \d+',('Timestamp: '+str(int(time.time()*1000))).encode(),data)
  out.writestr(info,data)
with ZipFile(a.base) as before,ZipFile(a.output) as after:
 assert after.testzip() is None
 changed=[n for n in before.namelist() if before.read(n)!=after.read(n)]
 assert set(changed)=={'nicoid/method-delta.dex','nicoid/helpers.mpe','META-INF/MANIFEST.MF'},changed
print(json.dumps({'build':'payload-only; patch-engine unchanged','sha256':hashlib.sha256(a.output.read_bytes()).hexdigest(),'bytes':a.output.stat().st_size,'changed_entries':changed},indent=2))

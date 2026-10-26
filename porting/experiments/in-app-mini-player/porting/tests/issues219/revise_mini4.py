"""Incremental v1.9.9-dev.4 build over the exact tested dev.3 payload. Local only."""
from pathlib import Path
import argparse,subprocess,zipfile,shutil,re,hashlib,json,time
p=argparse.ArgumentParser()
for name in ['base','work','ecj','android','r8','comment_factory']:p.add_argument('--'+name.replace('_','-'),type=Path,required=True)
p.add_argument('--smali-classpath',required=True);a=p.parse_args();R=Path(__file__).resolve().parents[3];W=a.work.resolve();W.mkdir(parents=True,exist_ok=True)
def run(*args):subprocess.run([str(x) for x in args],check=True)
with zipfile.ZipFile(a.base) as z:
 assert b'Version: 1.9.9-dev.3' in z.read('META-INF/MANIFEST.MF')
 for key,name in [('nicoid/helpers.mpe','helpers'),('nicoid/method-delta.dex','native')]: (W/(name+'.dex')).write_bytes(z.read(key))
for name in ['helpers','native']:
 shutil.rmtree(W/(name+'-smali'),ignore_errors=True)
 run('java','-cp',a.smali_classpath,'org.jf.baksmali.Main','disassemble',W/(name+'.dex'),'-o',W/(name+'-smali'))
run('python3',R/'porting/tools/helper_compile_stubs.py',W/'helpers-smali',W/'stubs.jar')
for name in ['classes','d8','new-smali']:shutil.rmtree(W/name,ignore_errors=True);(W/name).mkdir()
S=R/'porting/maintenance199/e/e/a'
run('java','-jar',a.ecj,'-8','-proc:none','-nowarn','-cp',str(W/'stubs.jar')+':'+str(a.android),'-d',W/'classes',S/'MiniPlayer219.java',S/'MiniSurface220.java',S/'PlayerGestures219.java')
with zipfile.ZipFile(W/'changes.jar','w') as z:
 for f in (W/'classes').rglob('*.class'):z.write(f,f.relative_to(W/'classes').as_posix())
run('java','-cp',a.r8,'com.android.tools.r8.D8','--release','--min-api','21','--lib',a.android,'--lib',Path(shutil.which('java')).resolve().parent.parent,'--classpath',W/'stubs.jar','--output',W/'d8',W/'changes.jar')
run('java','-cp',a.smali_classpath,'org.jf.baksmali.Main','disassemble',W/'d8/classes.dex','-o',W/'new-smali')
for family in ['MiniPlayer219','MiniSurface220','PlayerGestures219']:
 for f in (W/'helpers-smali/e/e/a').glob(family+'*.smali'):f.unlink()
for f in (W/'new-smali').rglob('*.smali'):
 target=W/'helpers-smali'/f.relative_to(W/'new-smali');target.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(f,target)
native=W/'native-smali/e/e/a/v.smali'
text=native.read_text()
original=a.comment_factory.read_text()
for method in re.findall(r'^\.method public constructor <init>[^\n]*\n.*?^\.end method',original,re.M|re.S):
 signature=method.splitlines()[0]
 pattern=r'^'+re.escape(signature)+r'\n.*?^\.end method'
 existing=re.search(pattern,text,re.M|re.S)
 current=existing[0] if existing else method
 needle='    move-result v0\n'
 assert current.count('Le/e/a/v0;->f()I')==1
 current=current.replace('    invoke-direct {v0, p1, p0}, Le/e/a/u;-><init>(Landroid/content/Context;Le/e/a/v;)V','    const/high16 v1, 0x3f800000\n    invoke-static {v1}, Ljava/lang/Float;->valueOf(F)Ljava/lang/Float;\n    move-result-object v1\n    invoke-direct {v0, p1, v1, p0}, Le/e/a/u;-><init>(Landroid/content/Context;Ljava/lang/Float;Le/e/a/v;)V')
 pos=current.index(needle,current.index('Le/e/a/v0;->f()I'))+len(needle)
 current=current[:pos]+'\n    invoke-static {p1, v0}, Le/e/a/MiniSurface220;->commentApi(Landroid/content/Context;I)I\n    move-result v0\n'+current[pos:]
 if existing:text=text[:existing.start()]+current+text[existing.end():]
 else:text+='\n'+current+'\n'
native.write_text(text)
f=W/'helpers-smali/e/e/a/ModernShorts.smali';s=f.read_text();assert 'v1.9.9-dev.3 @chikuwadon' in s;f.write_text(s.replace('v1.9.9-dev.3 @chikuwadon','v1.9.9-dev.4 @chikuwadon'))
for root,out in [('helpers-smali','candidate-helpers.dex'),('native-smali','candidate-native.dex')]:run('java','-cp',a.smali_classpath,'org.jf.smali.Main','assemble','--api','21',W/root,'-o',W/out)
out=W/'nicoid-re-1.9.9-dev.4.mpp'
with zipfile.ZipFile(a.base) as z,zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as result:
 for info in z.infolist():
  data=z.read(info.filename)
  if info.filename=='nicoid/helpers.mpe':data=(W/'candidate-helpers.dex').read_bytes()
  if info.filename=='nicoid/method-delta.dex':data=(W/'candidate-native.dex').read_bytes()
  if info.filename=='META-INF/MANIFEST.MF':data=data.replace(b'Version: 1.9.9-dev.3',b'Version: 1.9.9-dev.4');data=re.sub(rb'Timestamp: \d+',('Timestamp: '+str(int(time.time()*1000))).encode(),data)
  result.writestr(info,data)
print(json.dumps({'version':'1.9.9-dev.4','sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'path':str(out)}))

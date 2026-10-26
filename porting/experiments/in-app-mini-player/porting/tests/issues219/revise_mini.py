"""Incremental v1.9.9-dev.3 build over the exact tested dev.2 payload. Local only."""
from pathlib import Path
import argparse,subprocess,zipfile,shutil,re,hashlib,json,time
p=argparse.ArgumentParser()
for name in ['base','work','ecj','android','r8','native_video']:p.add_argument('--'+name.replace('_','-'),type=Path,required=True)
p.add_argument('--smali-classpath',required=True);a=p.parse_args();R=Path(__file__).resolve().parents[3];W=a.work.resolve();W.mkdir(parents=True,exist_ok=True)
def run(*args):subprocess.run([str(x) for x in args],check=True)
with zipfile.ZipFile(a.base) as z:
 assert b'Version: 1.9.9-dev.2' in z.read('META-INF/MANIFEST.MF')
 for key,name in [('nicoid/helpers.mpe','helpers'),('nicoid/method-delta.dex','native')]: (W/(name+'.dex')).write_bytes(z.read(key))
for name in ['helpers','native']:
 shutil.rmtree(W/(name+'-smali'),ignore_errors=True)
 run('java','-cp',a.smali_classpath,'org.jf.baksmali.Main','disassemble',W/(name+'.dex'),'-o',W/(name+'-smali'))
run('python3',R/'porting/tools/helper_compile_stubs.py',W/'helpers-smali',W/'stubs.jar')
for name in ['classes','d8','new-smali']:shutil.rmtree(W/name,ignore_errors=True);(W/name).mkdir()
S=R/'porting/maintenance199/e/e/a'
run('java','-jar',a.ecj,'-8','-proc:none','-nowarn','-cp',str(W/'stubs.jar')+':'+str(a.android),'-d',W/'classes',S/'MiniPlayer219.java',S/'MiniSurface220.java')
with zipfile.ZipFile(W/'changes.jar','w') as z:
 for f in (W/'classes').rglob('*.class'):z.write(f,f.relative_to(W/'classes').as_posix())
run('java','-cp',a.r8,'com.android.tools.r8.D8','--release','--min-api','21','--lib',a.android,'--lib',Path(shutil.which('java')).resolve().parent.parent,'--classpath',W/'stubs.jar','--output',W/'d8',W/'changes.jar')
run('java','-cp',a.smali_classpath,'org.jf.baksmali.Main','disassemble',W/'d8/classes.dex','-o',W/'new-smali')
for f in (W/'helpers-smali/e/e/a').glob('MiniPlayer219*.smali'):f.unlink()
for f in (W/'new-smali').rglob('*.smali'):
 target=W/'helpers-smali'/f.relative_to(W/'new-smali');target.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(f,target)
native=W/'native-smali/com/devbrackets/android/exomedia/ui/widget/VideoView.smali';native.parent.mkdir(parents=True,exist_ok=True)
signature='getVideoViewApiImplementation(Landroid/content/Context;Lcom/devbrackets/android/exomedia/ui/widget/VideoView$AttributeContainer;)I'
pattern=r'^\.method[^\n]* '+re.escape(signature)+r'\n.*?^\.end method'
original=a.native_video.read_text();m=re.search(pattern,original,re.M|re.S);assert m
method=m[0];assert method.count('    return p1')==1
method=method.replace('    return p1','    invoke-virtual {p0}, Landroid/view/View;->getContext()Landroid/content/Context;\n    move-result-object v0\n    invoke-static {v0, p2, p1}, Le/e/a/MiniSurface220;->layout(Landroid/content/Context;Ljava/lang/Object;I)I\n    move-result p1\n    return p1')
if native.exists():
 text=native.read_text();text,n=re.subn(pattern,lambda m:method,text,flags=re.M|re.S)
 if n==0:text+='\n'+method+'\n'
else:text=original[:re.search(r'^\.method',original,re.M).start()]+'\n'+method+'\n'
native.write_text(text)
f=W/'helpers-smali/e/e/a/ModernShorts.smali';s=f.read_text();assert 'v1.9.9-dev.2 @chikuwadon' in s;f.write_text(s.replace('v1.9.9-dev.2 @chikuwadon','v1.9.9-dev.3 @chikuwadon'))
for root,out in [('helpers-smali','candidate-helpers.dex'),('native-smali','candidate-native.dex')]:run('java','-cp',a.smali_classpath,'org.jf.smali.Main','assemble','--api','21',W/root,'-o',W/out)
out=W/'nicoid-re-1.9.9-dev.3.mpp'
with zipfile.ZipFile(a.base) as z,zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as result:
 for info in z.infolist():
  data=z.read(info.filename)
  if info.filename=='nicoid/helpers.mpe':data=(W/'candidate-helpers.dex').read_bytes()
  if info.filename=='nicoid/method-delta.dex':data=(W/'candidate-native.dex').read_bytes()
  if info.filename=='META-INF/MANIFEST.MF':data=data.replace(b'Version: 1.9.9-dev.2',b'Version: 1.9.9-dev.3');data=re.sub(rb'Timestamp: \d+',('Timestamp: '+str(int(time.time()*1000))).encode(),data)
  result.writestr(info,data)
print(json.dumps({'version':'1.9.9-dev.3','sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'path':str(out)}))

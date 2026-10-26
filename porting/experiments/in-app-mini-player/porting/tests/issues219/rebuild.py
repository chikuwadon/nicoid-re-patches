"""Reconstruct the v1.9.9 verification MPP over the reviewed v1.9.8 release."""
from pathlib import Path
import argparse,subprocess,zipfile,shutil,re,hashlib,json,time,os
p=argparse.ArgumentParser();p.add_argument('--base',type=Path,required=True);p.add_argument('--work',type=Path,required=True);p.add_argument('--java',default='java');p.add_argument('--ecj',type=Path,required=True);p.add_argument('--r8',type=Path,required=True);p.add_argument('--android',type=Path,required=True);p.add_argument('--smali-classpath',required=True);a=p.parse_args()
R=Path(__file__).resolve().parents[3];W=a.work.resolve();W.mkdir(parents=True,exist_ok=True);JAVA=Path(shutil.which(a.java) or a.java).resolve();ANDROID=a.android.resolve();CP=a.smali_classpath
assert hashlib.sha256(a.base.read_bytes()).hexdigest()=='c0a003760fc08396eca63f4976543558bc0d8a15ffa8e412118ebe6aaa0dcc9b','Use the published v1.9.8 MPP'
shutil.copyfile(a.base,W/'base.mpp')
with zipfile.ZipFile(a.base) as z:(W/'base-helpers.dex').write_bytes(z.read('nicoid/helpers.mpe'))
subprocess.run([str(JAVA),'-cp',CP,'org.jf.baksmali.Main','disassemble',str(W/'base-helpers.dex'),'-o',str(W/'base-smali')],check=True)
subprocess.run(['python3',str(R/'porting/tools/helper_compile_stubs.py'),str(W/'base-smali'),str(W/'helper-stubs.jar')],check=True)
def run(*args):subprocess.run([str(x) for x in args],check=True)
for p in ['classes','d8','new-smali','candidate-smali']:
 shutil.rmtree(W/p,ignore_errors=True);(W/p).mkdir()
stub=W/'base-smali/e/e/a/Review181.smali';original=stub.read_text()
try:
 stub.write_text(original.replace('.method public static seekTouch(','.method public static seekTouchLegacy('))
 run('python3',R/'porting/tools/helper_compile_stubs.py',W/'base-smali',W/'helper-stubs.jar')
finally:stub.write_text(original)
run(JAVA,'-jar',a.ecj,'-8','-proc:none','-nowarn','-cp',str(W/'helper-stubs.jar')+':'+str(ANDROID),'-d',W/'classes',*sorted((R/'porting/maintenance199').rglob('*.java')))
with zipfile.ZipFile(W/'new.jar','w') as z:
 for p in (W/'classes').rglob('*.class'):z.write(p,p.relative_to(W/'classes'))
run(JAVA,'-cp',a.r8,'com.android.tools.r8.D8','--release','--min-api','21','--lib',ANDROID,'--lib',JAVA.parent.parent,'--classpath',W/'helper-stubs.jar','--output',W/'d8',W/'new.jar')
run(JAVA,'-cp',CP,'org.jf.baksmali.Main','disassemble',W/'d8/classes.dex','-o',W/'new-smali')
shutil.copytree(W/'base-smali',W/'candidate-smali',dirs_exist_ok=True)
for p in (W/'new-smali').rglob('*.smali'):
 target=W/'candidate-smali'/p.relative_to(W/'new-smali');target.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(p,target)
def patch(name,old,new):
 p=W/'candidate-smali/e/e/a'/name;s=p.read_text();assert s.count(old)==1,(name,s.count(old));p.write_text(s.replace(old,new))
patch('Review181.smali','.method public static seekTouch(Landroid/app/Activity;Landroid/view/MotionEvent;)Z','.method public static seekTouch(Landroid/app/Activity;Landroid/view/MotionEvent;)Z\n    .registers 2\n    invoke-static {p0, p1}, Le/e/a/PlayerGestures219;->touch(Landroid/app/Activity;Landroid/view/MotionEvent;)Z\n    move-result p0\n    return p0\n.end method\n\n.method public static seekTouchLegacy(Landroid/app/Activity;Landroid/view/MotionEvent;)Z')
for name in ['Review181.smali','PopupGestures.smali']:
 p=W/'candidate-smali/e/e/a'/name;s=p.read_text();assert s.count('Le/e/a/SeekRules;->target')==1;s=s.replace('Le/e/a/SeekRules;->target','Le/e/a/SeekFeedback219;->target');assert s.count('Landroid/widget/Toast;->makeText')==1;s=s.replace('Landroid/widget/Toast;->makeText','Le/e/a/SeekFeedback219;->toast');p.write_text(s)
for name,signature,call in [
 ('DynamicTheme.smali','apply(Landroid/app/Activity;)V','Le/e/a/MiniPlayer219;->track(Landroid/app/Activity;)V'),
 ('PlayerGestures.smali','destroy(Landroid/app/Activity;)V','Le/e/a/PlayerGestures219;->destroy(Landroid/app/Activity;)V'),
 ('Review181.smali','settings(Landroid/preference/PreferenceActivity;)V','Le/e/a/PlayerGestures219;->settings(Landroid/preference/PreferenceActivity;)V')]:
 p=W/'candidate-smali/e/e/a'/name;s=p.read_text();pattern=r'(\.method public static '+re.escape(signature)+r'\n    \.registers \d+\n)';s,n=re.subn(pattern,lambda m:m.group(1)+'\n    invoke-static/range {p0 .. p0}, '+call+'\n',s);assert n==1,(name,signature);p.write_text(s)
patch('FullscreenControls.smali', '.method public static update(Ljava/lang/Object;)V\n    .registers 19', '.method public static update(Ljava/lang/Object;)V\n    .registers 19\n    invoke-static/range {p0 .. p0}, Le/e/a/MiniPlayer219;->layout(Ljava/lang/Object;)Z\n    move-result v0\n    if-eqz v0, :normal_layout_219\n    return-void\n    :normal_layout_219')
patch('HoldSpeed.smali', '    iput-boolean v4, p0, Le/e/a/HoldSpeed$State;->holding:Z', '    iput-boolean v4, p0, Le/e/a/HoldSpeed$State;->holding:Z\n\n    invoke-static {p0}, Le/e/a/PlayerGestures219;->startedHold(Ljava/lang/Object;)V')
patch('HoldSpeed.smali', '    iput-boolean v0, p0, Le/e/a/HoldSpeed$State;->holding:Z', '    iput-boolean v0, p0, Le/e/a/HoldSpeed$State;->holding:Z\n\n    invoke-static {p0}, Le/e/a/PlayerGestures219;->stoppedHold(Ljava/lang/Object;)V')
p=W/'candidate-smali/e/e/a/ModernShorts.smali';s=p.read_text();assert 'v1.9.8 @chikuwadon' in s;p.write_text(s.replace('v1.9.8 @chikuwadon','v1.9.9 @chikuwadon'))
run(JAVA,'-cp',CP,'org.jf.smali.Main','assemble','--api','21',W/'candidate-smali','-o',W/'candidate-helpers.dex')
with zipfile.ZipFile(W/'base.mpp') as base,zipfile.ZipFile(W/'nicoid-re-1.9.9.mpp','w',zipfile.ZIP_DEFLATED) as out:
 for item in base.infolist():
  data=base.read(item.filename)
  if item.filename=='nicoid/helpers.mpe':data=(W/'candidate-helpers.dex').read_bytes()
  if item.filename=='META-INF/MANIFEST.MF':data=data.replace(b'Version: 1.9.8',b'Version: 1.9.9');data=re.sub(rb'Timestamp: \d+',('Timestamp: '+str(int(time.time()*1000))).encode(),data)
  out.writestr(item,data)
print(json.dumps({'file':'nicoid-re-1.9.9.mpp','sha256':hashlib.sha256((W/'nicoid-re-1.9.9.mpp').read_bytes()).hexdigest()}))

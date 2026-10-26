"""Rebuild the local gesture/HUD/lifecycle revision over the v1.9.9 test MPP.
Requires Java, ECJ, D8, Android API 28+ and smali/baksmali. No network or publishing.
"""
from pathlib import Path
import argparse, subprocess, zipfile, shutil, re, hashlib, time, json
p=argparse.ArgumentParser()
for key in ['base','work','ecj','r8','android']:p.add_argument('--'+key,type=Path,required=True)
p.add_argument('--smali-classpath',required=True);p.add_argument('--version',default='1.9.9-dev.2');a=p.parse_args()
R=Path(__file__).resolve().parents[3];W=a.work.resolve();W.mkdir(parents=True,exist_ok=True)
def run(*args):subprocess.run([str(x) for x in args],check=True)
with zipfile.ZipFile(a.base) as z:
 for key,file in [('nicoid/helpers.mpe','base.dex'),('nicoid/method-delta.dex','native.dex')]: (W/file).write_bytes(z.read(key))
for stem in ['base','native']:
 shutil.rmtree(W/(stem+'-smali'),ignore_errors=True)
 run('java','-cp',a.smali_classpath,'org.jf.baksmali.Main','disassemble',W/(stem+'.dex'),'-o',W/(stem+'-smali'))
run('python3',R/'porting/tools/helper_compile_stubs.py',W/'base-smali',W/'stubs.jar')
for stem in ['classes','d8','new-smali','candidate-smali']:
 shutil.rmtree(W/stem,ignore_errors=True);(W/stem).mkdir()
run('java','-jar',a.ecj,'-8','-proc:none','-nowarn','-cp',str(W/'stubs.jar')+':'+str(a.android),'-d',W/'classes',*sorted((R/'porting/maintenance199').rglob('*.java')))
with zipfile.ZipFile(W/'new.jar','w') as z:
 for f in (W/'classes').rglob('*.class'):z.write(f,f.relative_to(W/'classes').as_posix())
run('java','-cp',a.r8,'com.android.tools.r8.D8','--release','--min-api','21','--lib',a.android,'--lib',Path(shutil.which('java')).resolve().parent.parent,'--classpath',W/'stubs.jar','--output',W/'d8',W/'new.jar')
run('java','-cp',a.smali_classpath,'org.jf.baksmali.Main','disassemble',W/'d8/classes.dex','-o',W/'new-smali')
shutil.copytree(W/'base-smali',W/'candidate-smali',dirs_exist_ok=True)
for f in (W/'new-smali').rglob('*.smali'):
 target=W/'candidate-smali'/f.relative_to(W/'new-smali');target.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(f,target)
H=W/'candidate-smali/e/e/a'
def replace_method(path,signature,body):
 s=path.read_text();pattern=r'^\.method[^\n]* '+re.escape(signature)+r'\n.*?^\.end method'
 s,n=re.subn(pattern,lambda m:body,s,flags=re.M|re.S);assert n==1,(path,signature,n);path.write_text(s)
def prepend(path,signature,code):
 s=path.read_text();pattern=r'(^\.method[^\n]* '+re.escape(signature)+r'\n\s*\.registers \d+\n)'
 s,n=re.subn(pattern,lambda m:m[1]+code+'\n',s,flags=re.M);assert n==1,(path,signature,n);path.write_text(s)
replace_method(H/'HoldSpeed.smali','indicator(Landroid/content/Context;F)Landroid/widget/TextView;', '.method static indicator(Landroid/content/Context;F)Landroid/widget/TextView;\n .registers 2\n invoke-static {p0, p1}, Le/e/a/FeedbackHud219;->indicator(Landroid/content/Context;F)Landroid/widget/TextView;\n move-result-object p0\n return-object p0\n.end method')
replace_method(H/'HoldSpeed.smali','position(Landroid/view/ViewGroup;Landroid/view/View;Landroid/widget/TextView;)V', '.method static position(Landroid/view/ViewGroup;Landroid/view/View;Landroid/widget/TextView;)V\n .registers 3\n invoke-static {p0, p1, p2}, Le/e/a/FeedbackHud219;->position(Landroid/view/ViewGroup;Landroid/view/View;Landroid/widget/TextView;)V\n return-void\n.end method')
replace_method(H/'PopupHoldSpeed.smali','position(Le/e/a/PopupHoldSpeed$State;)V','.method private static position(Le/e/a/PopupHoldSpeed$State;)V\n .registers 1\n invoke-static {p0}, Le/e/a/FeedbackHud219;->popupPosition(Ljava/lang/Object;)V\n return-void\n.end method')
# Normalize translated display strings without changing translation keys or saved values.
f=H/'UiStrings.smali';s=f.read_text()
for signature in ['translate(Ljava/lang/String;)Ljava/lang/String;','translate(Ljava/lang/String;Ljava/util/Locale;)Ljava/lang/String;']:
 pattern=r'^\.method[^\n]* '+re.escape(signature)+r'\n.*?^\.end method'
 match=re.search(pattern,s,re.M|re.S);assert match,signature
 method=re.sub(r'(?m)^(\s*)return-object ([vp]\d+)',lambda m:m[1]+'invoke-static {'+m[2]+'}, Le/e/a/CastSettings219;->text(Ljava/lang/String;)Ljava/lang/String;\n'+m[1]+'move-result-object '+m[2]+'\n'+m[1]+'return-object '+m[2],match[0]);s=s[:match.start()]+method+s[match.end():]
f.write_text(s)
f=H/'SettingsIntegrity.smali';s=f.read_text();pattern=r'^\.method public static ensure\(Landroid/preference/PreferenceActivity;\)V\n.*?^\.end method';m=re.search(pattern,s,re.M|re.S);assert m
body=m[0].replace('    return-void','    invoke-static {p0}, Le/e/a/CastSettings219;->apply(Landroid/preference/PreferenceActivity;)V\n    return-void');s=s[:m.start()]+body+s[m.end():];f.write_text(s)
# Volume and brightness HUDs share the black translucent surface.
f=H/'PlayerGestures.smali';s=f.read_text();old='    invoke-virtual {v3, v2}, Landroid/graphics/drawable/GradientDrawable;->setColor(I)V';assert s.count(old)==1;s=s.replace(old,'    const v2, -0x4d000000\n'+old);f.write_text(s)
for task in ['Stall','Retry','RetryUi']:
 prepend(H/('ModernPlayback$'+task+'.smali'),'run()V',' invoke-static {p0}, Le/e/a/PlaybackLifecycle219;->retryTask(Ljava/lang/Object;)Z\n move-result v0\n if-nez v0, :current_lifecycle_219\n return-void\n :current_lifecycle_219')
prepend(H/'PlaybackSession.smali','destroy(Ljava/lang/Object;Z)V',' invoke-static {p0}, Le/e/a/PlaybackLifecycle219;->destroy(Ljava/lang/Object;)V')
N=W/'native-smali'
def signature(m):return m.splitlines()[0].split()[-1]
# Original method snippets are retained to make previously unpatched methods explicit.
for f in (R/'porting/maintenance199/native').glob('*.smali'):
 s=f.read_text();name=re.search(r'^\.class[^\n]* (L[^;]+);',s,re.M)[1][1:];target=N/(name+'.smali');target.parent.mkdir(parents=True,exist_ok=True)
 if not target.exists():target.write_text(s[:re.search(r'^\.method',s,re.M).start()])
 for method in re.findall(r'^\.method[^\n]*\n.*?^\.end method',s,re.M|re.S):
  sig=signature(method)
  if re.search(r'^\.method[^\n]* '+re.escape(sig)+r'$',target.read_text(),re.M):continue
  with target.open('a') as out:out.write('\n'+method+'\n')
fragment=N/'com/sauzask/nicoid/NicoidVideoFragment.smali'
prepend(fragment,'a0()V',' invoke-static {p0}, Le/e/a/PlaybackLifecycle219;->retain(Ljava/lang/Object;)Z\n move-result v0\n if-eqz v0, :native_stop_219\n return-void\n :native_stop_219')
prepend(fragment,'Q()V',' invoke-static {p0}, Le/e/a/PlaybackLifecycle219;->resume(Ljava/lang/Object;)V')
# Route existing app feedback, including the shared native notification utility.
redirects=0
for root in [W/'candidate-smali',N]:
 for f in root.rglob('*.smali'):
  if f.stem.startswith('FeedbackHud219'):continue
  s=f.read_text();redirects+=s.count('Landroid/widget/Toast;->makeText');s=s.replace('Landroid/widget/Toast;->makeText','Le/e/a/FeedbackHud219;->makeText');f.write_text(s)
f=H/'ModernShorts.smali';s=f.read_text();assert 'v1.9.9 @chikuwadon' in s;f.write_text(s.replace('v1.9.9 @chikuwadon','v'+a.version+' @chikuwadon'))
for root,out in [(W/'candidate-smali',W/'helpers.dex'),(N,W/'methods.dex')]:run('java','-cp',a.smali_classpath,'org.jf.smali.Main','assemble','--api','21',root,'-o',out)
output=W/('nicoid-re-'+a.version+'.mpp')
with zipfile.ZipFile(a.base) as z,zipfile.ZipFile(output,'w',zipfile.ZIP_DEFLATED) as out:
 for entry in z.infolist():
  data=z.read(entry.filename)
  if entry.filename=='nicoid/helpers.mpe':data=(W/'helpers.dex').read_bytes()
  if entry.filename=='nicoid/method-delta.dex':data=(W/'methods.dex').read_bytes()
  if entry.filename=='META-INF/MANIFEST.MF':data=data.replace(b'Version: 1.9.9',('Version: '+a.version).encode());data=re.sub(rb'Timestamp: \d+',('Timestamp: '+str(int(time.time()*1000))).encode(),data)
  out.writestr(entry,data)
result={'version':a.version,'file':str(output),'sha256':hashlib.sha256(output.read_bytes()).hexdigest(),'redirected_feedback_calls':redirects}
(W/'build-result.json').write_text(json.dumps(result,indent=2)+'\n');print(json.dumps(result))

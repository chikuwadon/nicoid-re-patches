"""Build reviewed compatibility changes over the exact delivered dev.1 payload."""
from pathlib import Path
from zipfile import ZipFile,ZIP_DEFLATED
import argparse,hashlib,json,re,shutil,subprocess,time
p=argparse.ArgumentParser();p.add_argument('--base',type=Path,required=True);p.add_argument('--tools',type=Path,required=True);p.add_argument('--work',type=Path,required=True);p.add_argument('--output',type=Path,required=True);p.add_argument('--update-payload',action='store_true');a=p.parse_args()
assert hashlib.sha256(a.base.read_bytes()).hexdigest()=='7a3c188b5b567f99cc7dd153f09b66e25aebced4f15f2fce154a83597f43261d'
repo=Path(__file__).resolve().parents[3];source=repo/'extensions/extension/src/main/java/e/e/a';w=a.work.resolve();w.mkdir(parents=True,exist_ok=True);tools=a.tools.resolve();android=tools/'sdk/android-all-14-robolectric-10818077.jar'
smalicp=':'.join(str(tools/(n+'.jar')) for n in ['baksmali','smali','dexlib2','guava','jcommander','util','antlr','antlr-runtime','stringtemplate'])
def run(*cmd):subprocess.run(list(map(str,cmd)),check=True)
def family(path):return Path(path).stem.split('$',1)[0]
with ZipFile(a.base) as z:(w/'base.dex').write_bytes(z.read('nicoid/helpers.mpe'))
helpers=w/'helpers';generated=w/'generated';classes=w/'classes';dex=w/'dex'
for d in [helpers,generated,classes,dex]:
 if d.exists():shutil.rmtree(d)
 d.mkdir()
run('java','-cp',smalicp,'org.jf.baksmali.Main','d',w/'base.dex','-o',helpers)
run('java','-jar',tools/'ecj.jar','-1.8','-warn:none','-encoding','UTF-8','-cp',android,'-d',classes,*sorted(source.glob('*.java')))
with ZipFile(w/'compiled.jar','w') as z:
 for f in classes.rglob('*.class'):z.write(f,f.relative_to(classes))
run('java','-cp',tools/'r8.jar','com.android.tools.r8.D8','--lib',android,'--lib',Path(shutil.which('java')).resolve().parents[1],'--min-api','23','--output',dex,w/'compiled.jar')
run('java','-cp',smalicp,'org.jf.baksmali.Main','d',dex/'classes.dex','-o',generated)
migrated=['FullscreenViewport209','SessionCookies194','FeedLayout194','FilterRows194','ThemeChoice','UiControls194','DialogActions194','DialogColors','UiDialogs','UiStrings','CastRelay','CastAddress194']
changed=set()
for name in migrated:
 for f in (helpers/'e/e/a').glob('*.smali'):
  if family(f)==name:f.unlink()
 for f in (generated/'e/e/a').glob('*.smali'):
  if family(f)==name:shutil.copy2(f,helpers/'e/e/a'/f.name);changed.add(str(f.relative_to(generated)))
def method(s,sig):
 matches=list(re.finditer(r'^\.method[^\n]* '+re.escape(sig)+r'\n.*?^\.end method',s,re.M|re.S));assert len(matches)==1,sig
 return matches[0]
def replace(name,sig,body=None):
 target=helpers/'e/e/a'/(name+'.smali');s=target.read_text();m=method(s,sig)
 if body is None:body=method((generated/'e/e/a'/(name+'.smali')).read_text(),sig).group()
 target.write_text(s[:m.start()]+body+s[m.end():]);changed.add(str(target.relative_to(helpers)))
replace('FollowFeed','cookie(Landroid/app/Activity;)Ljava/lang/String;')
replace('PlaybackSession','form(Landroid/view/View;Z)V')
replace('Ui110','primary(Landroid/widget/Button;)V')
replace('PanelUi','tr(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;')
target=helpers/'e/e/a/FollowFeed$Rows.smali';s=target.read_text();sig='getView(ILandroid/view/View;Landroid/view/ViewGroup;)Landroid/view/View;';body=method(s,sig).group();body,n=re.subn(r'new-instance (v\d+), Landroid/widget/LinearLayout;',r'new-instance \1, Le/e/a/FeedLayout194;',body,count=1);assert n==1
body,n=re.subn(r'(invoke-direct \{[^}]+\}, )Landroid/widget/LinearLayout;-><init>\(Landroid/content/Context;\)V',r'\1Le/e/a/FeedLayout194;-><init>(Landroid/content/Context;)V',body,count=1);assert n==1;replace('FollowFeed$Rows',sig,body)
# Retain all native title, owner-name and uploader-ID predicates and failure order.
target=helpers/'e/e/a/ContentFilterId193.smali';body=method(target.read_text(),'filter(Ljava/lang/Object;)V').group();assert '.registers 7' in body;body=body.replace('.registers 7','.registers 8\n    const/4 v6, 0x0',1)
needle='    invoke-virtual {p0, v1}, Ljava/util/ArrayList;->remove(I)Ljava/lang/Object;';assert body.count(needle)==1
body=body.replace(needle,'''    if-nez v6, :mark_removed_194
    new-instance v2, Ljava/util/BitSet;
    invoke-direct {v2}, Ljava/util/BitSet;-><init>()V
    move-object v6, v2
    :mark_removed_194
    invoke-virtual {v6, v1}, Ljava/util/BitSet;->set(I)V''')
body=body.replace('move-exception p0','move-exception v2').replace('invoke-static {v0, v1, p0}, Landroid/util/Log;->w','invoke-static {v0, v1, v2}, Landroid/util/Log;->w').replace('    return-void','    invoke-static {p0, v6}, Le/e/a/FilterRows194;->compact(Ljava/lang/Object;Ljava/util/BitSet;)V\n    return-void');replace('ContentFilterId193','filter(Ljava/lang/Object;)V',body)
# A legacy AMOLED form pass must honor the same custom-input contract.
target=helpers/'e/e/a/FeedbackFixes.smali';sig='clearEdit(Landroid/view/View;)V';body=method(target.read_text(),sig).group();body=re.sub(r'(\.registers \d+)',r'''\1
    invoke-virtual {p0}, Landroid/view/View;->getTag()Ljava/lang/Object;
    move-result-object v0
    const-string v1, "nicoid-managed-input"
    invoke-virtual {v1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z
    move-result v0
    if-eqz v0, :native_input_194
    return-void
    :native_input_194''',body,count=1);replace('FeedbackFixes',sig,body)
if a.update_payload:
 snapshot=repo/'porting/helpers-v1.9.3.zip'
 with ZipFile(snapshot) as z:entries={n:z.read(n) for n in z.namelist() if family(n) not in migrated}
 for n in changed:
  if family(n) not in migrated:entries[n]=(helpers/n).read_bytes()
 with ZipFile(snapshot,'w',ZIP_DEFLATED) as z:
  for n,data in sorted(entries.items()):z.writestr(n,data)
 for f in (repo/'porting/helpers-smali/e/e/a').glob('*.smali'):
  if family(f) in migrated:f.unlink()
 manifest=repo/'porting/helper-ownership.json';owners=json.loads(manifest.read_text())
 for n in migrated:owners[n]={'owner':'java'}
 for name in set(map(family,changed)):
  if name in migrated or name not in owners:continue
  owners[name]['java_sha256']=hashlib.sha256((name+'.java').encode()+b'\0'+(source/(name+'.java')).read_bytes()).hexdigest();h=hashlib.sha256()
  for n,data in sorted(entries.items()):
   if family(n)==name:h.update(Path(n).name.encode()+b'\0'+data)
  owners[name]['smali_sha256']=h.hexdigest()
 manifest.write_text(json.dumps(dict(sorted(owners.items())),indent=2)+'\n')
for f in (helpers/'e/e/a').glob('ModernShorts*.smali'):f.write_text(f.read_text().replace('v1.9.4-dev.1 @chikuwadon','v1.9.4-dev.2 @chikuwadon'))
run('java','-cp',smalicp,'org.jf.smali.Main','a',helpers,'-o',w/'helpers.dex')
a.output.parent.mkdir(parents=True,exist_ok=True)
with ZipFile(a.base) as src,ZipFile(a.output,'w',ZIP_DEFLATED) as out:
 for info in src.infolist():
  data=src.read(info.filename)
  if info.filename=='nicoid/helpers.mpe':data=(w/'helpers.dex').read_bytes()
  if info.filename=='nicoid/ui-strings.txt':data=(repo/'patches/src/main/resources/nicoid/ui-strings.txt').read_bytes()
  if info.filename=='META-INF/MANIFEST.MF':data=data.replace(b'Version: 1.9.4-dev.1',b'Version: 1.9.4-dev.2');data=re.sub(rb'Timestamp: \d+',('Timestamp: '+str(int(time.time()*1000))).encode(),data)
  out.writestr(info,data)
if a.update_payload:
 (repo/'patches/src/main/resources/nicoid/helpers.mpe').write_bytes((w/'helpers.dex').read_bytes());p=repo/'gradle.properties';p.write_text(re.sub(r'(?m)^version = .+$','version = 1.9.4-dev.2',p.read_text()))
with ZipFile(a.base) as before,ZipFile(a.output) as after:
 assert after.testzip() is None
 for n in before.namelist():
  if n not in ['nicoid/helpers.mpe','nicoid/ui-strings.txt','META-INF/MANIFEST.MF']:assert before.read(n)==after.read(n),n
print(json.dumps({'bytes':a.output.stat().st_size,'sha256':hashlib.sha256(a.output.read_bytes()).hexdigest(),'changed_helpers':sorted(changed)},indent=2))

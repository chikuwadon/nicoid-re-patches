"""Rebuild the reviewed helper families on top of the exact v1.9.3 release.

The main patch, native method delta, resources and unrelated helpers are retained.
Use --update-payload to persist canonical source ownership for future Gradle builds.
"""
from pathlib import Path
from zipfile import ZipFile,ZIP_DEFLATED
import argparse,hashlib,json,re,shutil,subprocess,time
p=argparse.ArgumentParser();p.add_argument('--base',type=Path,required=True);p.add_argument('--tools',type=Path,required=True);p.add_argument('--work',type=Path,required=True);p.add_argument('--output',type=Path,required=True);p.add_argument('--update-payload',action='store_true');p.add_argument('--version',default='1.9.4-dev.1');a=p.parse_args()
repo=Path(__file__).resolve().parents[3];w=a.work.resolve();w.mkdir(parents=True,exist_ok=True);tools=a.tools.resolve();source=repo/'extensions/extension/src/main/java/e/e/a'
assert hashlib.sha256(a.base.read_bytes()).hexdigest()=='99f622f0b8f82ccc08358cb56aabaec6809a440cc1ffb0c7ab9ace7287442eb9','Use the reviewed v1.9.3 release'
def run(*args):subprocess.run(list(map(str,args)),check=True)
with ZipFile(a.base) as z:(w/'base.dex').write_bytes(z.read('nicoid/helpers.mpe'))
helpers=w/'helpers';generated=w/'generated';classes=w/'classes';dex=w/'dex'
for d in [helpers,generated,classes,dex]:
 if d.exists():shutil.rmtree(d)
 d.mkdir()
run('java','-cp',str(tools/'*'),'org.jf.baksmali.Main','d',w/'base.dex','-o',helpers)
run('python3',repo/'porting/tools/helper_compile_stubs.py',helpers,w/'compile-helpers.jar')
names=['PageCache','NetworkTask','ShortImages','ImageDiskCache','RowDates194','FullscreenViewport209']
run('java','-jar',tools/'ecj.jar','-1.8','-warn:none','-encoding','UTF-8','-cp',str(tools/'android.jar')+':'+str(w/'compile-helpers.jar'),'-d',classes,*[source/(n+'.java') for n in names])
with ZipFile(w/'helpers.jar','w') as z:
 for f in classes.rglob('*.class'):z.write(f,f.relative_to(classes))
run('java','-cp',tools/'r8.jar','com.android.tools.r8.D8','--lib',tools/'android.jar','--lib',Path(shutil.which('java')).resolve().parents[1],'--classpath',w/'compile-helpers.jar','--min-api','23','--output',dex,w/'helpers.jar')
run('java','-cp',str(tools/'*'),'org.jf.baksmali.Main','d',dex/'classes.dex','-o',generated)
migrated=['PageCache','NetworkTask','ShortImages']
for family in migrated+['RowDates194']:
 for f in (helpers/'e/e/a').glob('*.smali'):
  if f.stem.split('$',1)[0]==family:f.unlink()
 for f in (generated/'e/e/a').glob('*.smali'):
  if f.stem.split('$',1)[0]==family:shutil.copy2(f,helpers/'e/e/a'/f.name)
# This nested drawable is self-contained; keep the rest of the tested player intact.
shutil.copy2(generated/'e/e/a/FullscreenViewport209$Band.smali',helpers/'e/e/a/FullscreenViewport209$Band.smali')
row=helpers/'e/e/a/VideoRows191.smali';s=row.read_text()
for signature,body in [
 ('millis(Ljava/lang/String;)J','    .registers 2\n    invoke-static {p0}, Le/e/a/RowDates194;->millis(Ljava/lang/String;)J\n    move-result-wide v0\n    return-wide v0'),
 ('date(JLjava/lang/String;)Ljava/lang/String;','    .registers 3\n    invoke-static {p0, p1, p2}, Le/e/a/RowDates194;->format(JLjava/lang/String;)Ljava/lang/String;\n    move-result-object p0\n    return-object p0')]:
 pattern=r'^\.method[^\n]* '+re.escape(signature)+r'\n.*?^\.end method'
 s,n=re.subn(pattern,lambda m:'.method static '+signature+'\n'+body+'\n.end method',s,flags=re.M|re.S);assert n==1,signature
row.write_text(s)
if a.update_payload:
 # Keep Java-owned families out of both override layers so normal builds use source.
 snapshot=repo/'porting/helpers-v1.9.3.zip'
 with ZipFile(snapshot) as z:entries={n:z.read(n) for n in z.namelist() if Path(n).stem.split('$',1)[0] not in migrated}
 for n in ['e/e/a/VideoRows191.smali','e/e/a/FullscreenViewport209$Band.smali']:entries[n]=(helpers/n).read_bytes()
 with ZipFile(snapshot,'w',ZIP_DEFLATED) as z:
  for n,data in sorted(entries.items()):z.writestr(n,data)
 for f in (repo/'porting/helpers-smali/e/e/a').glob('*.smali'):
  if f.stem.split('$',1)[0] in migrated:f.unlink()
 manifest_path=repo/'porting/helper-ownership.json';ownership=json.loads(manifest_path.read_text())
 for family in migrated+['RowDates194']:ownership[family]={'owner':'java'}
 for family in ['VideoRows191','FullscreenViewport209']:
  raw=(source/(family+'.java')).read_bytes();ownership[family]['java_sha256']=hashlib.sha256((family+'.java').encode()+b'\0'+raw).hexdigest()
  h=hashlib.sha256()
  for n,data in sorted(entries.items()):
   if Path(n).stem.split('$',1)[0]==family:h.update(Path(n).name.encode()+b'\0'+data)
  ownership[family]['smali_sha256']=h.hexdigest()
 manifest_path.write_text(json.dumps(dict(sorted(ownership.items())),indent=2)+'\n')
 # Only the emitted payload receives its version; the canonical snapshot retains placeholders.
for f in (helpers/'e/e/a').glob('ModernShorts*.smali'):
 f.write_text(f.read_text().replace('v1.9.3 @chikuwadon','v'+a.version+' @chikuwadon'))
run('java','-cp',str(tools/'*'),'org.jf.smali.Main','a',helpers,'-o',w/'helpers.dex')
a.output.parent.mkdir(parents=True,exist_ok=True)
with ZipFile(a.base) as src,ZipFile(a.output,'w',ZIP_DEFLATED) as out:
 for info in src.infolist():
  data=src.read(info.filename)
  if info.filename=='nicoid/helpers.mpe':data=(w/'helpers.dex').read_bytes()
  if info.filename=='META-INF/MANIFEST.MF':data=data.replace(b'Version: 1.9.3\r\n',('Version: '+a.version+'\r\n').encode()).replace(b'Timestamp: 1792249893520',('Timestamp: '+str(int(time.time()*1000))).encode())
  out.writestr(info,data)
if a.update_payload:
 (repo/'patches/src/main/resources/nicoid/helpers.mpe').write_bytes((w/'helpers.dex').read_bytes())
 properties=repo/'gradle.properties';properties.write_text(re.sub(r'(?m)^version = .+$','version = '+a.version,properties.read_text()))
with ZipFile(a.base) as base,ZipFile(a.output) as built:
 assert built.testzip() is None
 for n in base.namelist():
  if n not in ['nicoid/helpers.mpe','META-INF/MANIFEST.MF']:assert base.read(n)==built.read(n),n
print(json.dumps({'file':str(a.output),'sha256':hashlib.sha256(a.output.read_bytes()).hexdigest(),'bytes':a.output.stat().st_size}))

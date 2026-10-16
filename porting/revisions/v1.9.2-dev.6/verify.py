from pathlib import Path
from zipfile import ZipFile
import hashlib,re,subprocess
w=Path(__file__).resolve().parent;root=w.parent;cp=(w/'cp.txt').read_text()
def run(*args):subprocess.run(list(map(str,args)),check=True)
with ZipFile(root/'nicoid-re-1.9.2-dev.6.mpp') as z,ZipFile(root/'nicoid-re-1.9.2-dev.5.mpp') as old:
 assert z.namelist()==old.namelist() and z.testzip() is None
 for n in z.namelist():
  if n in ('nicoid/helpers.mpe','nicoid/method-delta.dex'):continue
  before=old.read(n)
  if n.endswith('.class') or n=='META-INF/MANIFEST.MF':before=before.replace(b'1.9.2-dev.5',b'1.9.2-dev.6')
  assert z.read(n)==before,n
 for entry,out in [('nicoid/helpers.mpe','packaged-helpers'),('nicoid/method-delta.dex','packaged-delta')]:
  p=w/(out+'.dex');p.write_bytes(z.read(entry));run('java','-cp',cp,'org.jf.baksmali.Main','d',p,'-o',w/out)
helper=w/'packaged-helpers/e/e/a'
def read(name):return (helper/(name+'.smali')).read_text()
def method(s,name):return re.search(r'^\.method[^\n]* '+re.escape(name)+r'\([^\n]*\n.*?^\.end method',s,re.M|re.S).group(0)
checks=0
for before in (root/'crashfix/helpers/e/e/a').glob('*.smali'):
 after=helper/before.name;assert after.exists(),before.name
 if '$' not in before.name:
  signatures=r'^\.method[^\n]*\bpublic\b[^\n]* ([^\s]+\([^\n]+)'
  assert not set(re.findall(signatures,before.read_text(),re.M))-set(re.findall(signatures,after.read_text(),re.M)),before.name
 checks+=1
print('Previous helper classes and public API preserved:',checks)
assert 'Revision196;->summary' in read('PlayerButtons192') and 'Revision196;->summary' in read('DetailActions195')
assert '動画情報欄のボタン' in read('DetailActions195') or '\\u6b04' in read('DetailActions195')
assert 'RankingPager196;->custom' in method(read('RankingPages190'),'custom')
assert 'Revision196;->popupInfo' in read('PopupControls193')
assert 'Revision196;->popupInfoSize' in read('PopupControls193')
assert 'Revision196;->inherit' in read('PlayerVisibility192$Popup')
assert 'Revision196;->back' in read('CommentSettings194$Editor')
assert read('CommentSettings194$Editor$14').count('Revision196;->centered')==2
assert 'Revision196;->followTint' in read('VideoDetails$FollowIcon')
assert 'const/4 v0, 0x5' in method(read('ShadowSettings194'),'magnitude')
assert '0x3f000000' in read('PlayerIcons$Icon')
assert '1.9.2-dev.6' in read('ModernShorts')
fragment=(w/'packaged-delta/com/sauzask/nicoid/NicoidVideoListFragment.smali').read_text()
create=method(fragment,'a') if False else re.search(r'^\.method public a\(Landroid/view/LayoutInflater;.*?^\.end method',fragment,re.M|re.S).group(0)
assert 'PlaybackQueue192;->initialize' in create and 'ArrayList;->iterator' not in create
assert '->a0:Ljava/util/ArrayList;' in create[:1000]
popup=(w/'packaged-delta/com/sauzask/nicoid/NicoidPopupViewService.smali').read_text()
start=method(popup,'onStartCommand');hook='ModernEnhancements;->attach'
assert start.index(hook)>max(start.rfind('->findViewById(I)'),start.rfind('->addView('),start.rfind('->setLayoutParams('))
for p in (w/'packaged-delta').rglob('*.smali'):
 if p.name=='NicoidVideoListFragment.smali':continue
 def normalized(s):
  s='\n'.join(line for line in s.splitlines() if not line.lstrip().startswith('#'))
  return re.sub(r':(?:cond|goto|pswitch|pswitch_data|try_start|try_end|catchall|catch|array)_[0-9a-f]+',':label',re.sub(r'\s+',' ',s)).strip()
 assert normalized(p.read_text())==normalized((root/'crashfix/delta'/p.relative_to(w/'packaged-delta')).read_text()),p.name
print('Packaged behavior hooks, defaults, popup startup order and native payload continuity: passed')
run('python',root/'nicoid-re-patches/porting/tests/VerifyReleaseContinuity.py',root/'build191dev7/base-helpers-smali',w/'packaged-helpers',root/'build191dev1/native')

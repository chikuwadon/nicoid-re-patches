#!/usr/bin/env python3
"""Apply after cache and player refinements; keep private app data independent of SAF."""
from pathlib import Path
import re,sys
root=Path(sys.argv[1])/'smali'
p=root/'e/e/a/v0.smali';p.write_text(p.read_text().replace('Le/e/a/o;->c(Landroid/content/Context;)Ljava/lang/String;','Le/e/a/CacheFolders;->privateRoot(Landroid/content/Context;)Ljava/lang/String;'))
for base in ['e/e/a','com/sauzask/nicoid']:
 for p in (root/base).rglob('*.smali'):
  s=p.read_text()
  s=re.sub(r'invoke-virtual(/range)? (\{[^}]+\}), Landroid/widget/Button;->setBackgroundResource\(I\)V',lambda m:'invoke-static'+(m[1] or '')+' '+m[2]+', Le/e/a/PlayerIcons;->background(Landroid/view/View;I)V',s)
  form=p.name in ['h1.smali','x2.smali','v0$a.smali']
  method='showForm' if form else 'showDialog'
  s=re.sub(r'invoke-virtual(/range)? (\{[^}]+\}), Landroid/app/AlertDialog;->show\(\)V',lambda m:'invoke-static'+(m[1] or '')+' '+m[2]+', Le/e/a/PlaybackSession;->'+method+'(Landroid/app/AlertDialog;)V',s)
  p.write_text(s)
# Initialize progress inside the guarded start path before the worker can report bytes.
p=root/'com/sauzask/nicoid/NicoidDownloadCache.smali';s=p.read_text()
pat=r'(\.method public final b\(Landroid/content/Intent;\)V\n)(.*?)(\.end method)'
def started(m):
 old='    invoke-virtual {v0}, Ljava/lang/Thread;->start()V'
 assert m[2].count(old)==1
 return m[1]+m[2].replace(old,'    invoke-static {p0}, Le/e/a/CacheFolders;->started(Landroid/content/Context;)V\n    const-string v1, "title"\n    invoke-virtual {p1, v1}, Landroid/content/Intent;->getStringExtra(Ljava/lang/String;)Ljava/lang/String;\n    move-result-object v1\n    invoke-static {v1}, Le/e/a/CacheProgress;->title(Ljava/lang/String;)V\n'+old)+m[3]
s,n=re.subn(pat,started,s,flags=re.S);assert n==1;p.write_text(s)

# Channels belong in the existing following list, without duplicating home/sidebar entries.
p=root/'com/sauzask/nicoid/NicoidFavUserActivity.smali';s=p.read_text()
needle='    invoke-virtual {p1, v0}, Landroid/widget/ListView;->setAdapter(Landroid/widget/ListAdapter;)V'
if 'CreatorPage;->following' not in s:
 assert s.count(needle)==1
 s=s.replace(needle,'    invoke-static {p0}, Le/e/a/CreatorPage;->following(Landroid/app/Activity;)V\n'+needle)
p.write_text(s)

# The non-HLS cache path must report received bytes as well.
p=root/'com/sauzask/nicoid/NicoidDownloadCache.smali';s=p.read_text()
if 'CacheProgress;->transfer' not in s:
 needle='    add-long/2addr v5, v11\n\n    sput-wide v5, Lcom/sauzask/nicoid/NicoidDownloadCache;->r:J'
 assert s.count(needle)==1
 s=s.replace(needle,needle+'\n    invoke-static {v5, v6, v11, v12}, Le/e/a/CacheProgress;->transfer(JJ)V')
 needle='    add-long/2addr v5, v11\n\n    sput-wide v5, Lcom/sauzask/nicoid/NicoidDownloadCache;->s:J'
 assert s.count(needle)==1
 s=s.replace(needle,needle+'\n    invoke-static {v0}, Le/e/a/CacheProgress;->bytes(I)V')
p.write_text(s)

# Framework-owned settings dialogs are not opened by the app's AlertDialog hooks.
p=root/'com/sauzask/nicoid/NicoidSetting.smali';s=p.read_text()
pat=r'(\.method public onCreate\(Landroid/os/Bundle;\)V\n)(.*?)(\.end method)'
def preference_dialogs(m):
 assert m[2].count('    return-void')==1
 return m[1]+m[2].replace('    return-void','    invoke-static {p0}, Le/e/a/PreferenceDialogs;->attach(Landroid/preference/PreferenceActivity;)V\n    return-void')+m[3]
s,n=re.subn(pat,preference_dialogs,s,flags=re.S);assert n==1;p.write_text(s)
# The native prepared callback auto-starts cached streams, but may leave the
# center control in its initial play state because no click initiated playback.
p=root/'com/sauzask/nicoid/NicoidVideoFragment$n.smali';s=p.read_text()
pat=r'(\.method public onPrepared\(\)V\n)(.*?)(\n\.end method)'
def prepared(m):
 body=m[2]
 if 'PlayerIcons;->playing' in body:return m[0]
 marker='    return-void'
 assert body.count(marker)==1
 body=body.replace(marker,'    const/4 v0, 0x1\n    invoke-static {p0, v0}, Le/e/a/PlayerIcons;->playing(Ljava/lang/Object;Z)V\n'+marker)
 return m[1]+body+m[3]
s,n=re.subn(pat,prepared,s,flags=re.S);assert n==1;p.write_text(s)

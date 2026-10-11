#!/usr/bin/env python3
"""Apply after player-cache.py; leave event scheduling on the video clock."""
from pathlib import Path
import sys,re
r=Path(sys.argv[1])/'smali'
for cls in ['t','u']:
 p=r/f'e/e/a/{cls}.smali';s=p.read_text()
 pat=r'(\.method [^\n]* a\(Landroid/graphics/Canvas;Ljava/util/ArrayList;Ljava/util/HashMap;Ljava/util/HashMap;Ljava/util/HashMap;\)V\n)(.*?)(\.end method)'
 def draw(m):
  b=m[2].replace('    move-object/from16 v1, p0','    move-object/from16 v1, p0\n    invoke-static {v1}, Le/e/a/CommentMotion;->frame(Ljava/lang/Object;)V',1)
  field='Q' if cls=='t' else 'P'
  b=b.replace('    :cond_0\n    iget v7, v1, Le/e/a/'+cls+';->'+field+':F','    :cond_0\n    invoke-static {v1, v11, v10}, Le/e/a/CommentMotion;->start(Ljava/lang/Object;Ljava/lang/Object;F)F\n    move-result v10\n    iget v7, v1, Le/e/a/'+cls+';->'+field+':F',1)
  b=re.sub(r'    iget (v\d+), (v\d+), Le/e/a/'+cls+';->'+field+r':F',lambda v:'    invoke-static {'+v[2]+'}, Le/e/a/CommentMotion;->position(Ljava/lang/Object;)F\n    move-result '+v[1],b)
  return m[1]+b+m[3]
 s,n=re.subn(pat,draw,s,flags=re.S);assert n==1
 def run(m):
  b=m[2].replace(f'    iget v0, p0, Le/e/a/{cls};->a:I','    invoke-static {p0}, Le/e/a/CommentMotion;->fps(Ljava/lang/Object;)I\n    move-result v0',1)
  b=b.replace('    invoke-static {}, Ljava/lang/System;->currentTimeMillis()J','    invoke-static {}, Le/e/a/CommentMotion;->nowQ16()J')
  b=re.sub(r'(?m)^    shl-long/2addr (v\d+), v4\n', '', b)
  n=b.count('    const-wide/32 v5, 0x20000')
  b=b.replace('    const-wide/32 v5, 0x20000','    const-wide/16 v5, 0x0')
  assert n==1, f'{cls}: expected one 2 ms minimum-sleep clamp, found {n}'
  b,n=re.subn(r'    shr-long v5, v2, v4\n\n(    :try_start_[^\n]+\n)    invoke-static \{v5, v6\}, Ljava/lang/Thread;->sleep\(J\)V',
              r'\1    invoke-static {v2, v3}, Le/e/a/CommentMotion;->sleepQ16(J)V', b)
  assert n==1, f'{cls}: expected one frame sleep call, found {n}'
  return m[1]+b+m[3]
 s,n=re.subn(r'(\.method public run\(\)V\n)(.*?)(\.end method)',run,s,flags=re.S);assert n==1;p.write_text(s)
p=r/'com/sauzask/nicoid/NicoidVideoFragment.smali';s=p.read_text();s=re.sub(r'(    invoke-static \{([^}]+)\}, Le/e/a/ModernControls;->attach\(Lcom/sauzask/nicoid/NicoidVideoFragment;\)V)',lambda m:m[1]+'\n    invoke-static {'+m[2]+'}, Le/e/a/FullscreenControls;->update(Ljava/lang/Object;)V',s)
s=re.sub(r'(\.method public onConfigurationChanged\(Landroid/content/res/Configuration;\)V\n)(.*?)(\.end method)',lambda m:m[1]+m[2].replace('    return-void','    invoke-static {p0}, Le/e/a/FullscreenControls;->update(Ljava/lang/Object;)V\n    return-void')+m[3],s,flags=re.S);p.write_text(s)

p=r/'com/sauzask/nicoid/NicoidDownloadCache.smali';s=p.read_text().replace('    sput-wide v10, Lcom/sauzask/nicoid/NicoidDownloadCache;->s:J\n    return-void','    sput-wide v10, Lcom/sauzask/nicoid/NicoidDownloadCache;->s:J\n    invoke-static {v8, v9, v10, v11}, Le/e/a/CachePack;->completed(Ljava/lang/String;Ljava/lang/String;J)V\n    invoke-static {}, Le/e/a/CacheProgress;->done()V\n    return-void',1);p.write_text(s)

p=r/'e/e/a/ModernComments.smali';s=p.read_text().replace('    invoke-static {v13, v12, v0}, Le/e/a/CacheHls;->saveComments','    invoke-static {v13, v12, v0}, Le/e/a/CacheFolders;->comments(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z\n    move-result v11\n    if-eqz v11, :decode_comments\n    invoke-static {v13, v12, v0}, Le/e/a/CacheHls;->saveComments');p.write_text(s)

# The modern watch response has no legacy comment URL. Do not report its absence
# as a failed cache-comment request; fetch the modern payload instead.
p=r/'e/e/a/d0.smali';s=p.read_text()
def cache_run(m):
 b=m[2];needle='    iget-object v0, v1, Le/e/a/d0;->E:Ljava/lang/String;'
 target=re.search(r'(:goto_[0-9a-f]+)\n    iget-object v0, v1, Le/e/a/d0;->I:Lorg/apache/http/client/CookieStore;\n\n    invoke-static',b)
 assert target, 'cache comment callback continuation missing'
 b=b.replace(needle,'    iget-object v0, v1, Le/e/a/d0;->modernWatch:Lorg/json/JSONObject;\n    if-eqz v0, :legacy_cache_comments\n    invoke-static {v1}, Le/e/a/ModernComments;->load(Le/e/a/d0;)V\n    move-object v8, v4\n    goto '+target[1]+'\n    :legacy_cache_comments\n'+needle,1)
 return m[1]+b+m[3]
s,n=re.subn(r'(\.method public run\(\)V\n)(.*?)(\.end method)',cache_run,s,flags=re.S);assert n==1;p.write_text(s)

p=r/'com/sauzask/nicoid/NicoidDownloadCache.smali';s=p.read_text()
s,n=re.subn(r'(\.method public onDestroy\(\)V\n.*?)(    invoke-super \{p0\})',lambda m:m[1]+'    invoke-static {}, Le/e/a/CacheProgress;->stop()V\n'+m[2],s,flags=re.S);assert n==1;p.write_text(s)

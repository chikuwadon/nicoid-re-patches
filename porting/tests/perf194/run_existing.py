"""Run existing policy/JSON regressions against the complete compiled Java sources."""
from pathlib import Path
import argparse,subprocess,re,json
p=argparse.ArgumentParser();p.add_argument('--classes',required=True);p.add_argument('--android',required=True);p.add_argument('--json',required=True);p.add_argument('--ecj',required=True);p.add_argument('--work',type=Path,required=True);a=p.parse_args()
repo=Path(__file__).resolve().parents[3];tests=repo/'porting/tests';w=a.work.resolve();w.mkdir(parents=True,exist_ok=True)
names=['VideoCountRulesTest','CacheTransferPlanTest','ContentHistoryRulesTest','FollowFeedDataTest','ManualCookieTest','ThemeRulesTest','PostingRulesTest','SearchBoundsTest','SearchDatePresetTest','SearchRangeTest','ShortsRulesTest','RankingRules190Test','RankingResponse190Test','RankingPaging190Test','CreatorDataTest','DetailDataTest','HistorySortRulesTest','PlayerRefinementsTest','DevelopmentDataTest','StorageImprovementsTest','PlaylistGroups110Test','CommentFollowRulesTest','GestureRulesTest','PageRulesTest','PopupPinchGeometryTest','PlayerRulesTest','PlaybackRoutingTest','PlaybackRulesTest','OverlayRulesTest','CacheRowInfoTest','NgCommentDataTest','UiStringsTest','CommentHistoryTest','VideoInfoCountsTest','WatchRequestHeadersTest']
result=[]
for name in names:
 if name=='RankingPaging190Test':
  result.append({'test':name,'phase':'framework','exit':None,'output':'Run by ExistingFrameworkTest under Robolectric; requires Android Looper.'});continue
 source=tests/(name+'.java');out=w/name;out.mkdir(exist_ok=True);cp=a.classes+':'+a.json+':'+a.android
 compile=subprocess.run(['java','-jar',a.ecj,'-1.8','-warn:none','-encoding','UTF-8','-cp',cp,'-d',str(out),str(source)],capture_output=True,text=True)
 package='e.e.a.' if re.search(r'^package e\.e\.a;',source.read_text()) else ''
 if compile.returncode==0:
  args=[]
  if name=='DetailDataTest':
   watch=w/'watch-fixture.json';watch.write_text(json.dumps({'data':{'response':{'series':{'id':480166}}}}))
   tree=w/'tree-fixture.json';tree.write_text(json.dumps({'data':{'children':{'contents':[{'globalId':'sm3617161','thumbnailURL':'https://example.invalid/r320x180'}]}}}))
   args=[str(watch),str(tree)]
  run=subprocess.run(['java','-Duser.timezone=Asia/Tokyo','-cp',str(out)+':'+cp,package+name,*args],capture_output=True,text=True,timeout=90)
  row={'test':name,'phase':'run','exit':run.returncode,'output':run.stdout+run.stderr}
 else:row={'test':name,'phase':'compile','exit':compile.returncode,'output':compile.stdout+compile.stderr}
 result.append(row);(w/'results.json').write_text(json.dumps(result,indent=2));print(name,'PASS' if row['exit']==0 else 'FAIL',flush=True)
print('Passed',sum(r['exit']==0 for r in result),'host tests; framework tests:',sum(r['phase']=='framework' for r in result))

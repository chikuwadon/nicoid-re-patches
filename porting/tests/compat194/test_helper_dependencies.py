"""Reject compiler-generated dependencies removed by canonical-family overrides."""
from pathlib import Path
import argparse,subprocess,shutil
p=argparse.ArgumentParser();p.add_argument('--source',type=Path,required=True);p.add_argument('--tools',type=Path,required=True);p.add_argument('--work',type=Path,required=True);a=p.parse_args();w=a.work.resolve();w.mkdir(parents=True,exist_ok=True);tools=a.tools.resolve();java=w/'src/NicoidDexMerger.java';java.parent.mkdir(exist_ok=True);shutil.copy2(a.source,java)
subprocess.run(['java','-jar',str(tools/'ecj.jar'),'-1.8','-warn:none','-cp',str(tools/'morphe-desktop.jar'),'-d',str(w/'classes'),str(java)],check=True)
cp=':'.join(str(tools/(n+'.jar')) for n in ['baksmali','smali','dexlib2','guava','jcommander','util','antlr','antlr-runtime','stringtemplate'])
def cls(name,body=''):return '.class public L'+name+';\n.super Ljava/lang/Object;\n'+body
consumer=cls('e/e/a/Consumer','''.method public static make()Ljava/lang/Object;
 .registers 1
 new-instance v0, Le/e/a/Shared$1;
 invoke-direct {v0}, Le/e/a/Shared$1;-><init>()V
 return-object v0
.end method
''')
def dex(name,classes):
 folder=w/name;folder.mkdir(exist_ok=True)
 for n,s in enumerate(classes):(folder/(str(n)+'.smali')).write_text(s)
 out=w/(name+'.dex');subprocess.run(['java','-cp',cp,'org.jf.smali.Main','a',str(folder),'-o',str(out)],check=True);return out
existing=dex('existing',[cls('e/e/a/DynamicTheme'),cls('e/e/a/DynamicTheme$Refresh'),cls('e/e/a/ModernDebug')]);generated=dex('generated',[consumer,cls('e/e/a/Shared'),cls('e/e/a/Shared$1')]);broken=dex('broken',[cls('e/e/a/Shared')]);valid=dex('valid',[cls('e/e/a/Shared'),cls('e/e/a/Shared$1')])
run=lambda over:subprocess.run(['java','-cp',str(w/'classes')+':'+str(tools/'morphe-desktop.jar'),'app.nicoid.patches.NicoidDexMerger',str(generated),str(existing),str(over),str(w/'merged.dex')],capture_output=True,text=True)
r=run(broken);assert r.returncode!=0 and 'helper dependency' in r.stderr.lower(),'Missing compiler dependency was accepted by the merger'
r=run(valid);assert r.returncode==0,r.stderr
print('PASS: omitted compiler dependency rejected; complete canonical dependency accepted')

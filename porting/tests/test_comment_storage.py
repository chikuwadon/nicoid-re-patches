"""Exercise production replacement logic with deterministic file-provider failures."""
from pathlib import Path
import argparse, subprocess, tempfile
p=argparse.ArgumentParser(description=__doc__)
p.add_argument('--ecj',required=True)
p.add_argument('--java',default='java')
a=p.parse_args()
repo=Path(__file__).resolve().parents[2]
with tempfile.TemporaryDirectory() as output:
    sources=sorted((repo/'porting/tests/comment-storage-fixtures').rglob('*.java'))
    sources.append(repo/'extensions/extension/src/main/java/e/e/a/CacheComments.java')
    subprocess.run([a.java,'-jar',a.ecj,'-1.8','-warn:none','-d',output,*map(str,sources)],check=True)
    subprocess.run([a.java,'-cp',output,'e.e.a.CommentStorageTest'],check=True)

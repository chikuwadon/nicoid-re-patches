from pathlib import Path
import sys
root=Path(sys.argv[1])
for name in ['FullscreenScreen203.smali','FullscreenScreen203$State.smali','Revision201.smali']:
 p=root/'e/e/a'/name;s=p.read_text();assert 'FullscreenViewport207' in s;p.write_text(s.replace('FullscreenViewport207','FullscreenViewport208'))
p=root/'e/e/a/ModernShorts.smali';p.write_text(p.read_text().replace('1.9.3-dev.7','1.9.3-dev.8'))

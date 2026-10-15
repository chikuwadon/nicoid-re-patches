"""Legacy bitmap consumers require PNG, not only the same resource name and ID."""
from pathlib import Path
import io,json,struct,sys,zipfile
from VerifyResourceIdentity191 import resource_zip
base=resource_zip(sys.argv[1]);candidate=resource_zip(sys.argv[2]);icons=json.loads((Path(__file__).resolve().parents[1]/'theme-191/flat-icons.json').read_text());count=0
for name in base.namelist():
 if not name.endswith('.png'):continue
 assert name in candidate.namelist(),'Missing or incompatible raster asset: '+name
 before=base.read(name);after=candidate.read(name)
 assert after.startswith(b'\x89PNG\r\n\x1a\n'),name
 assert struct.unpack_from('>II',before,16)==struct.unpack_from('>II',after,16),('Image dimensions changed',name)
 count+=1
print('All',count,'original PNG entries retain format, density path and pixel dimensions')
for name in icons:
 extra={p for p in candidate.namelist() if p.endswith('/'+name+'.xml')}-{p for p in base.namelist() if p.endswith('/'+name+'.xml')}
 assert not extra,('Legacy bitmap resource changed to XML',name,sorted(extra))
print('All',len(icons),'refreshed glyph families remain bitmap-compatible')

"""Render audited flat UI paths to PNG for legacy BitmapFactory consumers.

Keep the PNG names, pixel bounds and density qualifiers. Changing a drawable's
resource ID is not the only compatibility concern: old callers decode/cast bitmaps.
Requires CairoSVG for code-native path rasterization.
"""
from pathlib import Path
import json,struct,sys
from collections import Counter
import cairosvg
from PIL import Image
root=Path(sys.argv[1]);root=root/'res' if (root/'res').is_dir() else root
icons=json.loads((Path(__file__).parent/'flat-icons.json').read_text());report=[]
for folder in root.glob('drawable*'):
 for name,icon in icons.items():
  p=folder/(name+'.png')
  if not p.exists():continue
  width,height=struct.unpack_from('>II',p.read_bytes(),16)
  ink='#eeeeee'
  if name.startswith('ic_'):
   image=Image.open(p).convert('RGBA');colors=Counter((r,g,b) for r,g,b,a in image.getdata() if a>200)
   if colors:ink='#%02x%02x%02x'%colors.most_common(1)[0][0]
  fill=ink if icon['fill'] else 'none';stroke='none' if icon['fill'] else ink
  svg=f'<svg xmlns="http://www.w3.org/2000/svg" width="{width}" height="{height}" viewBox="0 0 24 24" preserveAspectRatio="none"><path d="{icon["path"]}" fill="{fill}" stroke="{stroke}" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round" /></svg>'
  data=cairosvg.svg2png(bytestring=svg.encode(),output_width=width,output_height=height)
  assert struct.unpack_from('>II',data,16)==(width,height)
  p.write_bytes(data);report.append({'resource':name,'folder':folder.name,'old_pixels':[width,height],'new_pixels':[width,height],'format':'PNG'})
print('Refreshed',len(set(x['resource'] for x in report)),'UI glyphs across',len(report),'density variants; bitmap format and dimensions preserved')
if len(sys.argv)>2:Path(sys.argv[2]).write_text(json.dumps(report,indent=2)+'\n')

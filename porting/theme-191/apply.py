"""Apply the shared UI theme overlay to an apktool-decoded resource directory."""
from pathlib import Path
import json,shutil,sys,xml.etree.ElementTree as ET
bundle=Path(__file__).parent;res=Path(sys.argv[1]);res=res/'res' if (res/'res').is_dir() else res
for name in ['nicoid_palette.xml','nicoid_dialogs.xml']:shutil.copyfile(bundle/name,res/'values'/name)
for target,items in json.loads((bundle/'app-theme-overlays.json').read_text()).items():
 folder,name=target.split('/',1);p=res/folder/'styles.xml';tree=ET.parse(p);root=tree.getroot();style=next(x for x in root.findall('style') if x.attrib['name']==name)
 for key,value in items.items():
  item=next((x for x in style if x.attrib.get('name')==key),None)
  if item is None:item=ET.SubElement(style,'item',name=key)
  item.text=value
 ET.indent(tree);tree.write(p,encoding='utf-8',xml_declaration=True)

# Only known list separators, retaining positions, dimensions and margins.
for folder in res.glob('layout*'):
 for name in ['text_list_item.xml','ranking_layout_header.xml']:
  p=folder/name
  if p.exists():
   text=p.read_text().replace('android:background="#665f7593"','android:tag="nicoid-list-divider" android:background="@color/nicoid_list_divider_dark"')
   p.write_text(text)

# Common popup depth policy; leave colors, shapes and geometry untouched.
for folder in res.glob('values*'):
 p=folder/'styles.xml'
 if not p.exists():continue
 tree=ET.parse(p);root=tree.getroot();changed=False
 for style in root.findall('style'):
  if not (style.get('name','').startswith('MyTheme') or style.get('name','').startswith('NicoidDialog')):continue
  for key,value in [('android:popupElevation','0dp')]:
   item=next((x for x in style if x.get('name')==key),None)
   if item is None:item=ET.SubElement(style,'item',name=key)
   item.text=value;changed=True
 if changed:ET.indent(tree);tree.write(p,encoding='utf-8',xml_declaration=True)
# Decorative XML text shadows; no media or comment renderer is rewritten.
for folder in res.glob('layout*'):
 for name in ['player.xml','old_player.xml','video_list.xml','comment_list.xml']:
  p=folder/name
  if p.exists():
   text=p.read_text()
   import re
   text=re.sub(r' android:shadow(?:Color|Radius|Dx|Dy)="[^"]*"','',text)
   p.write_text(text)

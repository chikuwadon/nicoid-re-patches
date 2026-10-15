"""Validate the actual resource payload against an installed-release baseline.

Accepts resources.zip or an MPP; validates manifest identity and every existing
resource ID, allowing appended resource names only.
"""
import io,sys,zipfile
from pathlib import Path
from VerifyManifest import elements,verify
import struct
U16=lambda b,p:struct.unpack_from('<H',b,p)[0]
U32=lambda b,p:struct.unpack_from('<I',b,p)[0]
def chunks(b,start,end):
 while start<end:
  kind,header,size=struct.unpack_from('<HHI',b,start)
  assert size>=header and size>0
  yield kind,start,header,size
  start+=size
 assert start==end

def pool(b,p):
 count,styles,flags,offset=struct.unpack_from('<IIII',b,p+8);utf8=bool(flags&256);out=[]
 def length(q,wide):
  n=U16(b,q) if wide else b[q];q+=2 if wide else 1
  flag=0x8000 if wide else 0x80
  if n&flag:n=((n&(flag-1))<<(16 if wide else 8))+(U16(b,q) if wide else b[q]);q+=2 if wide else 1
  return n,q
 for n in range(count):
  q=p+offset+U32(b,p+U16(b,p+2)+n*4)
  size,q=length(q,not utf8)
  if utf8:size,q=length(q,False)
  out.append(b[q:q+size*(1 if utf8 else 2)].decode('utf8' if utf8 else 'utf-16le'))
 return out

def ids(b):
 out={}
 for kind,p,h,size in chunks(b,12,len(b)):
  if kind!=0x200:continue
  pkg=U32(b,p+8);types=pool(b,p+U32(b,p+268));keys=pool(b,p+U32(b,p+276))
  for typ,q,ch,sz in chunks(b,p+h,p+size):
   if typ!=0x201:continue
   tid,flags=b[q+8],b[q+9];count,start=struct.unpack_from('<II',b,q+12)
   assert flags==0,flags
   for i in range(count):
    off=U32(b,q+ch+i*4)
    if off==0xffffffff:continue
    entry=q+start+off;key=U32(b,entry+4);rid=pkg<<24|tid<<16|i
    value=types[tid-1]+'/'+keys[key]
    assert rid not in out or out[rid]==value
    out[rid]=value
 return out

def resource_zip(path):
 data=Path(path).read_bytes()
 z=zipfile.ZipFile(io.BytesIO(data))
 if 'nicoid/resources.zip' in z.namelist():return zipfile.ZipFile(io.BytesIO(z.read('nicoid/resources.zip')))
 return z

def validate(baseline,candidate):
 for required in ['AndroidManifest.xml','resources.arsc']:
  if required not in candidate.namelist():raise ValueError('Missing mandatory resource payload entry: '+required)
 before=baseline.read('AndroidManifest.xml');after=candidate.read('AndroidManifest.xml')
 verify(after)
 assert list(elements(before))==list(elements(after)), 'Manifest identity, components or theme changed from installed release'
 previous=ids(baseline.read('resources.arsc'));current=ids(candidate.read('resources.arsc'))
 changed=[(hex(key),value,current.get(key)) for key,value in previous.items() if current.get(key)!=value]
 assert not changed, 'Existing resource IDs changed: '+repr(changed[:10])
 print('Manifest identity/components/themes preserved; '+str(len(previous))+' existing resource IDs preserved')

if __name__=='__main__':validate(resource_zip(sys.argv[1]),resource_zip(sys.argv[2]))

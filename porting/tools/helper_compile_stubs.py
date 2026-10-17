"""Create compile-only class stubs from a helper DEX's baksmali output.

The generated jar must never be shipped or used at runtime.
"""
from pathlib import Path
from zipfile import ZipFile
import re,struct,sys
u2=lambda n:struct.pack('>H',n)
u4=lambda n:struct.pack('>I',n)
root,out=map(Path,sys.argv[1:])
sources={}
for p in root.rglob('*.smali'):
 s=p.read_text();m=re.search(r'^\.class .* L([^;]+);',s,re.M)
 if m:sources[m[1]]=s
with ZipFile(out,'w') as jar:
 for name,s in sources.items():
  pool=[];cache={}
  def add(key,entry):
   if key not in cache:cache[key]=len(pool)+1;pool.append(entry)
   return cache[key]
  def utf(v):
   data=v.encode();return add(('u',v),b'\x01'+u2(len(data))+data)
  def cls(v):return add(('c',v),b'\x07'+u2(utf(v)))
  this=cls(name);m=re.search(r'^\.super L([^;]+);',s,re.M);superclass=cls(m[1] if m else 'java/lang/Object')
  interfaces=[cls(x) for x in re.findall(r'^\.implements L([^;]+);',s,re.M)]
  def flags(words,method=False):
   f=1 if 'public' in words else 4 if 'protected' in words else 2 if 'private' in words else 0
   if 'static' in words:f|=8
   if 'interface' in words:f|=0x600
   return f
  fields=[]
  for access,fn,desc in re.findall(r'^\.field (.*?) ([^\s:]+):([^\s=]+)',s,re.M):fields.append(u2(flags(access.split()))+u2(utf(fn))+u2(utf(desc))+u2(0))
  methods=[]
  for access,mn,desc in re.findall(r'^\.method (?:(.*?) )?([^\s(]+)(\([^\n]+)',s,re.M):
   if mn=='<clinit>':continue
   desc=desc.strip();f=flags(access.split());f|=0x80 if 'varargs' in access.split() else 0;ret=desc.split(')')[1];code=b'\xb1' if ret=='V' else b'\x01\xb0' if ret.startswith(('L','[')) else b'\x09\xad' if ret=='J' else b'\x0e\xaf' if ret=='D' else b'\x0b\xae' if ret=='F' else b'\x03\xac'
   # Stubs are read by the compiler only; constructor bodies are not executed.
   body=u2(2)+u2(255)+u4(len(code))+code+u2(0)+u2(0)
   attr=u2(utf('Code'))+u4(len(body))+body
   methods.append(u2(f)+u2(utf(mn))+u2(utf(desc))+u2(1)+attr)
  inners=[]
  for child in sources:
   if '$' in child and (child==name or child.rsplit('$',1)[0]==name):
    outer,inner=child.rsplit('$',1)
    if inner.isidentifier():inners.append(u2(cls(child))+u2(cls(outer))+u2(utf(inner))+u2(9))
  attrs=[]
  if inners:
   body=u2(len(inners))+b''.join(inners);attrs.append(u2(utf('InnerClasses'))+u4(len(body))+body)
  body=u2(flags(s.splitlines()[0].split()))+u2(this)+u2(superclass)+u2(len(interfaces))+b''.join(map(u2,interfaces))+u2(len(fields))+b''.join(fields)+u2(len(methods))+b''.join(methods)+u2(len(attrs))+b''.join(attrs)
  jar.writestr(name+'.class',b'\xca\xfe\xba\xbe'+u2(0)+u2(52)+u2(len(pool)+1)+b''.join(pool)+body)
print('Compile-only helper stubs:',out)

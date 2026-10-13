package e.e.a;
import java.io.*;import java.util.*;import java.util.regex.Pattern;
/** Provider work is scoped to the requested directory, with no global I/O monitor. */
final class DirectoryIndex<T> {
 static final long TTL=2000;
 static final class Item<T> {final String name;final T ref;final boolean directory;final long size,time;Item(String n,T r,boolean d,long s,long t){name=n;ref=r;directory=d;size=s;time=t;}}
 interface Provider<T> {List<Item<T>> children(T parent)throws IOException;T create(T parent,String name,boolean directory)throws IOException;Item<T> stat(T ref,String name)throws IOException;long now();}
 private static final Pattern ID=Pattern.compile("^((?:sm|so|nm|ss)[0-9]+)(?:[._].*)?$");
 private final Provider<T> provider;private final Bucket root;
 private final Map<String,Bucket> videos=new LinkedHashMap<String,Bucket>(16,.75f,true){protected boolean removeEldestEntry(Map.Entry<String,Bucket> e){return size()>128;}};
 private final class Bucket {
  final T parent;long refreshed;boolean loaded;final Map<String,Item<T>> items=new LinkedHashMap<>();final Set<String> dirty=new HashSet<>();
  Bucket(T p){parent=p;}
  synchronized void load()throws IOException{if(loaded&&provider.now()-refreshed<TTL)return;List<Item<T>> list=provider.children(parent);items.clear();dirty.clear();for(Item<T> item:list)items.put(logical(item.name),item);refreshed=provider.now();loaded=true;}
  synchronized Item<T> find(String name,boolean create,boolean directory)throws IOException{load();Item<T> item=items.get(name);if(item==null&&create){T ref=provider.create(parent,physical(name),directory);if(ref==null)throw new IOException("Cannot create cache document");item=new Item<>(name,ref,directory,0,0);items.put(name,item);dirty.add(name);}return item;}
  synchronized Item<T> metadata(String name)throws IOException{Item<T> item=find(name,false,false);if(item!=null&&dirty.contains(name)){Item<T> fresh=provider.stat(item.ref,name);if(fresh==null)items.remove(name);else items.put(name,fresh);dirty.remove(name);item=fresh;}return item;}
  synchronized List<Item<T>> all()throws IOException{load();return new ArrayList<>(items.values());}
  synchronized void forget(String name){items.remove(name);dirty.remove(name);}
  synchronized void remember(String name,T ref){items.put(name,new Item<>(name,ref,false,0,0));dirty.add(name);}
  synchronized void changed(String name){if(items.containsKey(name))dirty.add(name);}
 }
 DirectoryIndex(T root,Provider<T> provider){this.root=new Bucket(root);this.provider=provider;}
 static String group(String name){java.util.regex.Matcher m=ID.matcher(name);return m.matches()?m.group(1):null;}
 static String logical(String name){return name.endsWith(".complete")?name.substring(0,name.length()-9)+".mp4":name;}
 static String physical(String name){return group(name)!=null&&name.endsWith(".mp4")?name.substring(0,name.length()-4)+".complete":name;}
 private Bucket bucket(String name,boolean create)throws IOException{
  String id=group(name);if(id==null)return root;
  Item<T> directory=root.find(id,create,true);if(directory==null||!directory.directory)return root;
  synchronized(videos){Bucket b=videos.get(id);if(b==null||!b.parent.equals(directory.ref)){b=new Bucket(directory.ref);videos.put(id,b);}return b;}
 }
 T document(String name,boolean create)throws IOException{
  if(name.isEmpty())return root.parent;
  // Existing flat caches remain readable while new documents use per-video directories.
  Item<T> loose=root.find(name,false,false);if(loose!=null&&!loose.directory)return loose.ref;
  Bucket b=bucket(name,create);if(create&&b!=root)b.find(".nomedia",true,false);
  Item<T> item=b.find(name,create,false);return item==null?null:item.ref;
 }
 Item<T> stat(String name)throws IOException{if(name.isEmpty())return new Item<>("",root.parent,true,0,0);Item<T> loose=root.metadata(name);if(loose!=null&&!loose.directory)return loose;return bucket(name,false).metadata(name);}
 List<Item<T>> all()throws IOException{LinkedHashMap<String,Item<T>> out=new LinkedHashMap<>();List<Item<T>> roots=root.all();for(Item<T> item:roots)if(!item.directory&&group(item.name)!=null)out.put(logical(item.name),item);for(Item<T> item:roots)if(item.directory&&item.name.equals(group(item.name)))for(Item<T> child:bucket(item.name,false).all())if(!child.name.equals(".nomedia"))out.put(logical(child.name),child);return new ArrayList<>(out.values());}
 void forget(String name)throws IOException{root.forget(name);String id=group(name);synchronized(videos){Bucket b=videos.get(id);if(b!=null)b.forget(name);}}
 void remember(String name,T ref)throws IOException{Bucket b=bucket(name,false);b.remember(name,ref);}
 void changed(String name)throws IOException{root.changed(name);String id=group(name);synchronized(videos){Bucket b=videos.get(id);if(b!=null)b.changed(name);}}
 T directory(String id)throws IOException{Item<T> item=root.find(id,false,true);return item!=null&&item.directory?item.ref:null;}
 void removeDirectory(String id){root.forget(id);synchronized(videos){videos.remove(id);}}
}

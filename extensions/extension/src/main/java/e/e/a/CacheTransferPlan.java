package e.e.a;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

/** Measure the byte ranges declared by the selected HLS playlists before transfer. */
final class CacheTransferPlan {
    private static final Pattern URI=Pattern.compile("URI=\"([^\"]+)\"");
    private static final Pattern RANGE=Pattern.compile("BYTERANGE=\"([0-9]+)(?:@([0-9]+))?\"");
    interface Fetch {
        String text(URL url) throws IOException;
        default void assets(int count) {}
        default long length(URL url) throws IOException {return -1;}
    }
    static long bytes(URL url, Fetch fetch) throws IOException {
        Map<String,Long> assets=new LinkedHashMap<>();walk(url,fetch,assets,new HashSet<>(),0);
        fetch.assets(assets.size());
        long total=0;Map<String,Long> measured=new HashMap<>();
        Set<String> unknown=new HashSet<>();for(Map.Entry<String,Long> asset:assets.entrySet())if(asset.getValue()<=0)unknown.add(asset.getKey().split("\\|",2)[0]);
        // Size feedback must not generate one extra request for every media segment.
        if(unknown.size()>8)return -1;
        for(Map.Entry<String,Long> asset:assets.entrySet()){
            long size=asset.getValue();
            if(size<=0){String target=asset.getKey().split("\\|",2)[0];Long known=measured.get(target);if(known==null){known=fetch.length(new URL(target));measured.put(target,known);}size=known;}
            if(size<=0)return -1;if(size>Long.MAX_VALUE-total)throw new IOException("Transfer size overflow");total+=size;
        }
        return total>0?total:-1;
    }
    private static long[] parseRange(String value,long implicit)throws IOException{
        try{String[] parts=value.split("@",-1);if(parts.length>2)throw new NumberFormatException();long length=Long.parseLong(parts[0]),offset=parts.length==2?Long.parseLong(parts[1]):implicit;if(length<=0||offset<0||offset>Long.MAX_VALUE-length)throw new NumberFormatException();return new long[]{length,offset};}
        catch(NumberFormatException e){throw new IOException("Invalid byte range",e);}
    }
    private static void walk(URL url,Fetch fetch,Map<String,Long> assets,Set<String> seen,int depth)throws IOException {
        if(depth>4||!seen.add(url.toString()))return;
        String text=fetch.text(url);if(!text.startsWith("#EXTM3U"))throw new IOException("Invalid playlist");
        boolean variant=false,chosen=false;String range=null,previousUri=null;long previousEnd=0;boolean previousRange=false;
        for(String line:text.split("\\r?\\n")){
            line=line.trim();if(line.startsWith("#EXT-X-STREAM-INF")){variant=true;continue;}
            if(line.startsWith("#EXT-X-BYTERANGE:")){range=line.substring(17);continue;}
            if(line.startsWith("#")){
                Matcher uri=URI.matcher(line);if(!uri.find())continue;
                URL child=new URL(url,uri.group(1));if(child.getPath().endsWith(".m3u8")){walk(child,fetch,assets,seen,depth+1);continue;}
                Matcher size=RANGE.matcher(line);
                // AES keys are 16 bytes; initialization maps declare their range.
                if(size.find()){long[] parsed=parseRange(size.group(1)+(size.group(2)==null?"":"@"+size.group(2)),0);assets.put(child.toString()+"|"+parsed[0]+"@"+parsed[1],parsed[0]);}
                else assets.put(child.toString()+"|",line.startsWith("#EXT-X-KEY:")?16L:-1L);continue;
            }
            if(line.isEmpty())continue;
            URL child=new URL(url,line);
            if(variant||child.getPath().endsWith(".m3u8")){
                if(!chosen){walk(child,fetch,assets,seen,depth+1);chosen=true;}variant=false;
            }else{
                String target=child.toString();
                if(range==null){assets.put(target+"|",-1L);previousRange=false;}
                else{if(!range.contains("@")&&(!previousRange||!target.equals(previousUri)))throw new IOException("Missing implicit range offset");long[] parsed=parseRange(range,previousEnd);assets.put(target+"|"+parsed[0]+"@"+parsed[1],parsed[0]);previousEnd=parsed[1]+parsed[0];previousRange=true;}
                previousUri=target;range=null;
            }
        }
    }
}

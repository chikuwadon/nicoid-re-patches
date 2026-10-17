package e.e.a;
import java.util.*;
/** Isolate the measured array-movement bottleneck; excludes API, JSON and rule matching. */
public final class FilterBenchmark194 {
 static volatile long checksum;
 static void legacy(ArrayList<Object> rows,BitSet bits){for(int i=rows.size()-1;i>=0;i--)if(bits.get(i))rows.remove(i);}
 static long sample(boolean after,int size,BitSet bits){ArrayList<Object> rows=new ArrayList<>(size);for(int i=0;i<size;i++)rows.add(Integer.valueOf(i));long start=System.nanoTime();if(after)FilterRows194.compact(rows,bits);else legacy(rows,bits);long elapsed=System.nanoTime()-start;for(int i=0;i<rows.size();i++)if(((Integer)rows.get(i)).intValue()!=i*2+1)throw new AssertionError("Order changed");checksum+=rows.size();return elapsed;}
 public static void main(String[] args){int size=100000;BitSet bits=new BitSet(size);for(int i=0;i<size;i+=2)bits.set(i);for(int i=0;i<5;i++){sample(false,size,bits);sample(true,size,bits);}System.out.print("{\"rows\":"+size+",\"warmups\":5,\"before_ns\":[");long[] a=new long[7],b=new long[7];for(int i=0;i<7;i++){if(i%2==0){a[i]=sample(false,size,bits);b[i]=sample(true,size,bits);}else{b[i]=sample(true,size,bits);a[i]=sample(false,size,bits);}}for(int i=0;i<7;i++)System.out.print((i==0?"":",")+a[i]);System.out.print("],\"after_ns\":[");for(int i=0;i<7;i++)System.out.print((i==0?"":",")+b[i]);System.out.println("]}");}
}

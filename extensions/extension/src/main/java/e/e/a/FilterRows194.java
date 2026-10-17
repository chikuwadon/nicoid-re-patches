package e.e.a;
import java.util.ArrayList;
import java.util.BitSet;
/** One stable compaction keeps the native shared list and selection indices. */
public final class FilterRows194 {
 private FilterRows194(){}
 public static void compact(Object value,BitSet removed){if(removed==null)return;ArrayList rows=(ArrayList)value;int first=removed.nextSetBit(0);if(first<0)return;int write=first,size=rows.size();for(int read=first+1;read<size;read++)if(!removed.get(read))rows.set(write++,rows.get(read));rows.subList(write,size).clear();}
}

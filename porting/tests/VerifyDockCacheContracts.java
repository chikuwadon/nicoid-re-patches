import org.jf.dexlib2.*;
import org.jf.dexlib2.iface.*;
import org.jf.dexlib2.iface.instruction.*;
import org.jf.dexlib2.iface.reference.*;
import java.io.File;
import java.util.*;

/** Verify native timer/finish hooks survive the partial-method payload merge. */
public final class VerifyDockCacheContracts {
 static final Map<String,ClassDef> classes=new HashMap<>();
 static Method method(String type,String name){for(Method m:classes.get(type).getMethods())if(m.getName().equals(name))return m;throw new AssertionError(type+"."+name);}
 static boolean calls(Method m,String owner,String name){for(Instruction i:m.getImplementation().getInstructions())if(i instanceof ReferenceInstruction){Reference r=((ReferenceInstruction)i).getReference();if(r instanceof MethodReference){MethodReference call=(MethodReference)r;if(call.getDefiningClass().equals(owner)&&call.getName().equals(name))return true;}}return false;}
 static void require(boolean value,String message){if(!value)throw new AssertionError(message);}
 public static void main(String[] args)throws Exception{
  MultiDexContainer<? extends DexFile> dex=DexFileFactory.loadDexContainer(new File(args[0]),Opcodes.getDefault());
  for(String entry:dex.getDexEntryNames())for(ClassDef c:dex.getEntry(entry).getDexFile().getClasses())classes.put(c.getType(),c);
  Method timer=method("Le/e/a/m1;","run");
  require(calls(timer,"Le/e/a/PlayerControlsDock;","enabled"),"Always-visible mode must keep native position updates active");
  require(calls(timer,"Lcom/devbrackets/android/exomedia/ui/widget/VideoView;","getCurrentPosition"),"Timer must read actual playback position");
  require(calls(method("Le/e/a/m1$a;","run"),"Landroid/widget/SeekBar;","setProgress"),"Native seekbar update must survive");
  require(calls(method("Lcom/sauzask/nicoid/NicoidDownloadCache$f;","run"),"Le/e/a/CacheProgress;","finishResource"),"Completion toast must use transfer result");
  require(!calls(method("Le/e/a/Review181;","seekTouch"),"Landroid/view/View;","postDelayed"),"Video taps must not schedule playback toggles");
  Method stop=method("Le/e/a/CacheProgress;","stop");
  for(Instruction i:stop.getImplementation().getInstructions())if(i.getOpcode()==Opcode.SPUT_BOOLEAN&&i instanceof ReferenceInstruction){FieldReference f=(FieldReference)((ReferenceInstruction)i).getReference();require(!f.getName().equals("completed"),"Service teardown must retain completed state for pending toast");}
  System.out.println("Dock/cache DEX contracts: timer, seekbar, completion state and tap behavior passed");
 }
}

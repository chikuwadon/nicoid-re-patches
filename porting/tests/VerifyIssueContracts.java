import org.jf.dexlib2.*;
import org.jf.dexlib2.iface.*;
import org.jf.dexlib2.iface.instruction.*;
import org.jf.dexlib2.iface.reference.*;
import java.io.File;
import java.util.*;

/** Verify native timer/finish hooks survive the partial-method payload merge. */
public final class VerifyIssueContracts {
 static final Map<String,ClassDef> classes=new HashMap<>();
 static Method method(String type,String name){for(Method m:classes.get(type).getMethods())if(m.getName().equals(name))return m;throw new AssertionError(type+"."+name);}
 static boolean calls(Method m,String owner,String name){for(Instruction i:m.getImplementation().getInstructions())if(i instanceof ReferenceInstruction){Reference r=((ReferenceInstruction)i).getReference();if(r instanceof MethodReference){MethodReference call=(MethodReference)r;if(call.getDefiningClass().equals(owner)&&call.getName().equals(name))return true;}}return false;}
 static void require(boolean value,String message){if(!value)throw new AssertionError(message);}
 public static void main(String[] args)throws Exception{
  MultiDexContainer<? extends DexFile> dex=DexFileFactory.loadDexContainer(new File(args[0]),Opcodes.getDefault());
  for(String entry:dex.getDexEntryNames())for(ClassDef c:dex.getEntry(entry).getDexFile().getClasses())classes.put(c.getType(),c);
  Method list=method("Lcom/sauzask/nicoid/NicoidCommentListFragment;","b");
  require(calls(list,"Le/e/a/CommentUi;","list"),"All native list updates must route to UI snapshot helper");
  Method swap=method("Lcom/sauzask/nicoid/NicoidCommentListFragment;","bNative");
  require(!calls(swap,"Ljava/util/ArrayList;","clear"),"Attached comment list must not be cleared during refresh");
  require(calls(swap,"Landroid/widget/ListView;","setAdapter"),"New comment snapshot must attach its adapter");
  require(calls(method("Le/e/a/FullscreenControls;","update"),"Le/e/a/PlayerTheme;","apply"),"Tablet player theme hook missing");
  boolean fit=false;for(Instruction i:method("Le/e/a/ModernShorts;","install").getImplementation().getInstructions())if(i instanceof ReferenceInstruction){Reference ref=((ReferenceInstruction)i).getReference();if(ref instanceof StringReference&&((StringReference)ref).getString().equals("FIT_CENTER"))fit=true;}
  require(fit,"Shorts must fit the whole video without cropping");
  System.out.println("Issue DEX contracts: comment snapshot routing, adapter attachment, tablet theme and Shorts fit passed");
 }
}

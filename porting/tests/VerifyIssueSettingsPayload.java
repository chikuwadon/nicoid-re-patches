import com.android.tools.smali.dexlib2.*;
import com.android.tools.smali.dexlib2.iface.*;
import com.android.tools.smali.dexlib2.iface.instruction.*;
import com.android.tools.smali.dexlib2.iface.reference.*;
import java.io.*;
/** Check queue reception in the native delta, independently of helper source. */
public final class VerifyIssueSettingsPayload {
 public static void main(String[] args)throws Exception{
  DexFile file=DexFileFactory.loadDexFile(new File(args[0]),Opcodes.getDefault());int queues=0;
  for(ClassDef c:file.getClasses())if(c.getType().equals("Lcom/sauzask/nicoid/NicoidVideoFragment;")||c.getType().equals("Lcom/sauzask/nicoid/NicoidPopupViewService;"))for(Method m:c.getMethods())if(m.getImplementation()!=null){boolean intentQueue=false,globalGate=false;for(Instruction i:m.getImplementation().getInstructions())if(i instanceof ReferenceInstruction){Reference r=((ReferenceInstruction)i).getReference();if(r instanceof StringReference&&((StringReference)r).getString().equals("isContinuousPlay"))intentQueue=true;if(r instanceof FieldReference){FieldReference f=(FieldReference)r;if(f.getDefiningClass().equals("Lcom/sauzask/nicoid/NicoidVideoListActivity;")&&f.getName().equals("I"))globalGate=true;}}if(intentQueue){if(globalGate)throw new AssertionError("Explicit queue still gated by list preference");queues++;}}
  if(queues!=2)throw new AssertionError("Expected both native queue receivers, found "+queues);
  boolean ordered=false,loaded=false;for(ClassDef c:file.getClasses())for(Method m:c.getMethods())if(m.getImplementation()!=null){if(c.getType().equals("Lcom/sauzask/nicoid/NicoidVideoListActivity;")&&m.getName().equals("onCreateOptionsMenu")){boolean parent=false;for(Instruction i:m.getImplementation().getInstructions()){if(i.getOpcode()==Opcode.INVOKE_SUPER)parent=true;if(i instanceof ReferenceInstruction){Reference ref=((ReferenceInstruction)i).getReference();if(ref instanceof MethodReference&&((MethodReference)ref).getName().equals("adjustHistoryMenu")&&parent)ordered=true;}}}if(c.getType().equals("Lcom/sauzask/nicoid/NicoidVideoListFragment;"))for(Instruction i:m.getImplementation().getInstructions())if(i instanceof ReferenceInstruction){Reference ref=((ReferenceInstruction)i).getReference();if(ref instanceof MethodReference&&((MethodReference)ref).getName().equals("historyLoaded"))loaded=true;}}
  if(!ordered||!loaded)throw new AssertionError("History menu/header lifecycle hooks missing");
  System.out.println("Native queues and history menu/header lifecycle verified");
 }
}

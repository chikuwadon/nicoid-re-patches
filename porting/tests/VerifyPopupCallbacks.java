import java.util.*;
import com.android.tools.smali.dexlib2.*;import com.android.tools.smali.dexlib2.iface.*;import com.android.tools.smali.dexlib2.iface.instruction.*;import com.android.tools.smali.dexlib2.iface.reference.*;
public class VerifyPopupCallbacks {
 public static void main(String[] a)throws Exception {
  DexFile dex; try(java.io.InputStream in=new java.io.BufferedInputStream(new java.io.FileInputStream(a[0]))) { dex=com.android.tools.smali.dexlib2.dexbacked.DexBackedDexFile.fromInputStream(Opcodes.forApi(21),in); }
  Set<String> definitions=new HashSet<>();List<MethodReference> refs=new ArrayList<>();
  for(ClassDef c:dex.getClasses())if(c.getType().equals("Le/e/a/PopupPinchLayout;"))for(Method m:c.getMethods())definitions.add(m.toString());
  int checked=0;for(ClassDef c:dex.getClasses())for(Method m:c.getMethods())if(m.getImplementation()!=null)for(Instruction i:m.getImplementation().getInstructions())if(i instanceof ReferenceInstruction){Reference r=((ReferenceInstruction)i).getReference();if(r instanceof MethodReference){MethodReference target=(MethodReference)r;if(target.getDefiningClass().equals("Le/e/a/PopupPinchLayout;")&&target.getName().startsWith("lambda$")){checked++;if(!definitions.contains(target.toString()))throw new AssertionError("Unresolved packaged DEX callback: "+target);}}}
  if(checked<2)throw new AssertionError("Missing callbacks");System.out.println("Resolved exact DEX callback names: "+checked);
 }
}

import java.io.*;
import java.util.*;
import org.jf.dexlib2.*;
import org.jf.dexlib2.iface.*;
import org.jf.dexlib2.iface.instruction.*;
import org.jf.dexlib2.iface.reference.*;
import org.jf.dexlib2.analysis.*;
import org.jf.dexlib2.analysis.reflection.ReflectionClassDef;
/** Analyze control flow, not just the presence of a hook, before checking injected arguments. */
public final class VerifyRevision196 {
 static boolean assignable(ClassPath cp,RegisterType actual,String expected){
  if(actual.category==RegisterType.NULL)return true;
  if(actual.category!=RegisterType.REFERENCE||actual.type==null)return false;
  if(expected.equals("Ljava/lang/Object;"))return true;
  String type=actual.type.getType();if(type.equals(expected))return true;
  Set<String> seen=new HashSet<>();while(type!=null&&seen.add(type)){if(type.equals(expected))return true;ClassDef c=cp.getClassDef(type);if(c.getInterfaces().contains(expected))return true;type=c.getSuperclass();}return false;
 }

 public static void main(String[] args)throws Exception {
  List<ClassProvider> providers=new ArrayList<>();List<ClassDef> classes=new ArrayList<>();
  for(String path:args){MultiDexContainer<? extends DexFile> files=DexFileFactory.loadDexContainer(new File(path),Opcodes.getDefault());for(String name:files.getDexEntryNames()){DexFile dex=files.getEntry(name).getDexFile();providers.add(new DexClassProvider(dex));classes.addAll(dex.getClasses());}}
  providers.add(type->{try{return new ReflectionClassDef(Class.forName(type.substring(1,type.length()-1).replace('/','.'),false,VerifyRevision196.class.getClassLoader()));}catch(Throwable missing){return null;}});
  ClassPath cp=new ClassPath(providers);Set<String> targets=new HashSet<>(Arrays.asList("Le/e/a/Revision196;","Le/e/a/RankingPager196;","Le/e/a/PlaybackQueue192;"));int calls=0,methods=0;
  for(ClassDef c:classes)for(Method m:c.getMethods()){
   if(m.getImplementation()==null)continue;boolean relevant=false;for(Instruction i:m.getImplementation().getInstructions())if(i instanceof ReferenceInstruction){Reference r=((ReferenceInstruction)i).getReference();if(r instanceof MethodReference&&targets.contains(((MethodReference)r).getDefiningClass()))relevant=true;}
   if(!relevant)continue;
   MethodAnalyzer analyzer=new MethodAnalyzer(cp,m,null,false);if(analyzer.getAnalysisException()!=null)throw new AssertionError(c.getType()+"->"+m.getName()+": "+analyzer.getAnalysisException());methods++;
   for(AnalyzedInstruction ai:analyzer.getAnalyzedInstructions()){
    Instruction i=ai.getInstruction();if(!(i instanceof ReferenceInstruction))continue;Reference r=((ReferenceInstruction)i).getReference();if(!(r instanceof MethodReference))continue;MethodReference ref=(MethodReference)r;if(!targets.contains(ref.getDefiningClass()))continue;
    int[] regs;if(i instanceof FiveRegisterInstruction){FiveRegisterInstruction v=(FiveRegisterInstruction)i;regs=new int[]{v.getRegisterC(),v.getRegisterD(),v.getRegisterE(),v.getRegisterF(),v.getRegisterG()};}else if(i instanceof RegisterRangeInstruction){RegisterRangeInstruction v=(RegisterRangeInstruction)i;regs=new int[v.getRegisterCount()];for(int n=0;n<regs.length;n++)regs[n]=v.getStartRegister()+n;}else throw new AssertionError("Unsupported call");
    int n=i.getOpcode()==Opcode.INVOKE_STATIC||i.getOpcode()==Opcode.INVOKE_STATIC_RANGE?0:1;for(CharSequence param:ref.getParameterTypes()){String expected=param.toString();if(expected.startsWith("L")||expected.startsWith("[")){RegisterType actual=ai.getPreInstructionRegisterType(regs[n]);if(!assignable(cp,actual,expected))throw new AssertionError(c.getType()+"->"+m.getName()+" @"+analyzer.getInstructionAddress(ai)+" "+ref.getName()+" argument "+n+": "+actual+" is not "+expected);}n+=expected.equals("J")||expected.equals("D")?2:1;}calls++;
   }
  }
  if(calls<10)throw new AssertionError("Missing revision hooks: "+calls);System.out.println("Revision argument verification: "+calls+" calls across "+methods+" methods passed");
 }
}

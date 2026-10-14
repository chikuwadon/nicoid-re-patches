import java.io.*;
import java.util.*;
import org.jf.dexlib2.*;
import org.jf.dexlib2.iface.*;
import org.jf.dexlib2.iface.instruction.*;
import org.jf.dexlib2.iface.reference.*;
import org.jf.dexlib2.analysis.*;
import org.jf.dexlib2.analysis.reflection.ReflectionClassDef;
/** Analyze control flow, not just the presence of a hook, before checking injected arguments. */
public final class VerifyInjectedArguments {
 static boolean assignable(ClassPath cp,RegisterType actual,String expected){
  if(actual.category==RegisterType.NULL)return true;
  if(actual.category!=RegisterType.REFERENCE||actual.type==null)return false;
  if(expected.equals("Ljava/lang/Object;"))return true;
  String type=actual.type.getType();if(type.equals(expected))return true;
  Set<String> seen=new HashSet<>();while(type!=null&&seen.add(type)){if(type.equals(expected))return true;ClassDef c=cp.getClassDef(type);if(c.getInterfaces().contains(expected))return true;type=c.getSuperclass();}return false;
 }
 public static void main(String[] args)throws Exception {
  MultiDexContainer<? extends DexFile> files=DexFileFactory.loadDexContainer(new File(args[0]),Opcodes.getDefault());List<ClassProvider> providers=new ArrayList<>();List<ClassDef> classes=new ArrayList<>();for(String name:files.getDexEntryNames()){DexFile dex=files.getEntry(name).getDexFile();providers.add(new DexClassProvider(dex));classes.addAll(dex.getClasses());}
  providers.add(type->{try{return new ReflectionClassDef(Class.forName(type.substring(1,type.length()-1).replace('/','.'),false,VerifyInjectedArguments.class.getClassLoader()));}catch(Throwable missing){return null;}});
  ClassPath cp=new ClassPath(providers);Set<String> targets=new HashSet<>(Arrays.asList("Le/e/a/ListActions;","Le/e/a/UiDialogs;","Le/e/a/CreatorPage;","Le/e/a/HistorySupport;","Le/e/a/PopupGestures;","Le/e/a/SearchFilters;","Le/e/a/CacheListing;","Le/e/a/CachedPlayback;","Le/e/a/PlaybackUrls;","Le/e/a/CacheFolders;","Le/e/a/CacheHls;","Le/e/a/CacheProgress;","Le/e/a/Ranking190;","Le/e/a/MylistFollow190;","Le/e/a/RankingPages190;","Le/e/a/DialogColors;","Le/e/a/SearchRange;","Le/e/a/CommentViewport190;"));int calls=0,history=0;
  for(ClassDef c:classes){if(!(c.getType().equals("Lcom/sauzask/nicoid/NicoidFavUserActivity;")||c.getType().equals("Le/e/a/NgCommentSettings$ListDialog;")||c.getType().equals("Lcom/sauzask/nicoid/NicoidListButton;")||c.getType().equals("Le/e/a/t;")||c.getType().equals("Le/e/a/u;")||c.getType().equals("Le/e/a/StartupScreen;")||c.getType().equals("Le/e/a/StartupRouter;")||c.getType().equals("Le/e/a/ModernRanking;")||c.getType().equals("Le/e/a/PanelUi;")||c.getType().equals("Le/e/a/DialogInputs;")||c.getType().equals("Le/e/a/RankingPeriod;")||c.getType().equals("Lcom/sauzask/nicoid/NicoidActivity;")||c.getType().equals("Lcom/sauzask/nicoid/NicoidRankingActivity;")||c.getType().equals("Lcom/sauzask/nicoid/NicoidUserVideoListFragment$b;")||c.getType().equals("Le/e/a/e0;")||c.getType().equals("Le/e/a/v0;")||c.getType().equals("Le/e/a/o;")||c.getType().equals("Le/e/a/d0;")||c.getType().equals("Lcom/sauzask/nicoid/NicoidPopupViewService$k;")||c.getType().equals("Lcom/sauzask/nicoid/NicoidVideoFragment;")||c.getType().equals("Le/e/a/a3;")||c.getType().equals("Lcom/sauzask/nicoid/NicoidPopupViewService;")||c.getType().equals("Lcom/sauzask/nicoid/NicoidCacheManagerActivity;")||c.getType().equals("Lcom/sauzask/nicoid/NicoidDownloadCache;")||c.getType().equals("Lcom/sauzask/nicoid/NicoidTopActivity$b;")||c.getType().equals("Le/e/a/CacheHls;")))continue;
   for(Method m:c.getMethods()){if(m.getImplementation()==null)continue;boolean relevant=false;for(Instruction i:m.getImplementation().getInstructions())if(i instanceof ReferenceInstruction){Reference r=((ReferenceInstruction)i).getReference();if(r instanceof MethodReference&&targets.contains(((MethodReference)r).getDefiningClass()))relevant=true;}if(!relevant)continue;
    MethodAnalyzer analyzer=new MethodAnalyzer(cp,m,null,false);if(analyzer.getAnalysisException()!=null)throw new AssertionError(c.getType()+"->"+m.getName()+": "+analyzer.getAnalysisException());
    for(AnalyzedInstruction ai:analyzer.getAnalyzedInstructions()){Instruction i=ai.getInstruction();if(!(i instanceof ReferenceInstruction))continue;Reference r=((ReferenceInstruction)i).getReference();if(!(r instanceof MethodReference))continue;MethodReference ref=(MethodReference)r;if(!targets.contains(ref.getDefiningClass()))continue;
     int[] regs;if(i instanceof FiveRegisterInstruction){FiveRegisterInstruction v=(FiveRegisterInstruction)i;regs=new int[]{v.getRegisterC(),v.getRegisterD(),v.getRegisterE(),v.getRegisterF(),v.getRegisterG()};}else if(i instanceof RegisterRangeInstruction){RegisterRangeInstruction v=(RegisterRangeInstruction)i;regs=new int[v.getRegisterCount()];for(int n=0;n<regs.length;n++)regs[n]=v.getStartRegister()+n;}else throw new AssertionError("Unsupported call");
     int n=i.getOpcode()==org.jf.dexlib2.Opcode.INVOKE_STATIC||i.getOpcode()==org.jf.dexlib2.Opcode.INVOKE_STATIC_RANGE?0:1;for(CharSequence p:ref.getParameterTypes()){String expected=p.toString();if(expected.startsWith("L")||expected.startsWith("[")){RegisterType actual=ai.getPreInstructionRegisterType(regs[n]);if(!assignable(cp,actual,expected))throw new AssertionError(c.getType()+"->"+m.getName()+" @"+analyzer.getInstructionAddress(ai)+" "+ref.getName()+" argument "+n+": "+actual+" is not "+expected);}n+=expected.equals("J")||expected.equals("D")?2:1;}
     calls++;if(ref.getDefiningClass().equals("Le/e/a/HistorySupport;")&&ref.getName().equals("capture")&&m.getName().equals("a")&&m.getParameterTypes().toString().equals("[I]"))history++;
    }
   }
  }
  if(history!=1)throw new AssertionError("Expected one account-history capture, found "+history);if(calls<6)throw new AssertionError("Missing native hooks");System.out.println("Injected arguments: "+calls+" calls checked across native control flow");
 }
}

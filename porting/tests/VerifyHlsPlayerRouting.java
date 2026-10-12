import java.io.*;import java.util.*;
import org.jf.dexlib2.*;import org.jf.dexlib2.iface.*;import org.jf.dexlib2.iface.instruction.*;import org.jf.dexlib2.iface.reference.*;
/** Execute the native URL-selection branches with both streaming preferences and no cached file. */
public final class VerifyHlsPlayerRouting {
 static List<Instruction> list(Method m){List<Instruction> out=new ArrayList<>();for(Instruction i:m.getImplementation().getInstructions())out.add(i);return out;}
 static boolean truth(Object v){return v!=null&&(!(v instanceof Number)||((Number)v).intValue()!=0)&&!Boolean.FALSE.equals(v);}
 static int[] regs(Instruction i){if(i instanceof FiveRegisterInstruction){FiveRegisterInstruction v=(FiveRegisterInstruction)i;return new int[]{v.getRegisterC(),v.getRegisterD(),v.getRegisterE(),v.getRegisterF(),v.getRegisterG()};}RegisterRangeInstruction v=(RegisterRangeInstruction)i;int[] a=new int[v.getRegisterCount()];for(int n=0;n<a.length;n++)a[n]=v.getStartRegister()+n;return a;}
 static String execute(Method method,String input,boolean legacy,boolean normalize){List<Instruction> code=list(method);Map<Integer,Integer> addresses=new HashMap<>();int address=0;for(int n=0;n<code.size();n++){addresses.put(address,n);address+=code.get(n).getCodeUnits();}Object[] r=new Object[method.getImplementation().getRegisterCount()];r[r.length-2]=new Object();r[r.length-1]=input;Object result=null;int pc=0,at=0;
  for(int steps=0;steps<500;steps++){Instruction i=code.get(pc);Opcode op=i.getOpcode();boolean jump=false;int next=at+i.getCodeUnits();
   switch(op){
    case CONST_4:case CONST_16:case CONST: r[((OneRegisterInstruction)i).getRegisterA()]=((NarrowLiteralInstruction)i).getNarrowLiteral();break;
    case CONST_STRING:case CONST_STRING_JUMBO:r[((OneRegisterInstruction)i).getRegisterA()]=((StringReference)((ReferenceInstruction)i).getReference()).getString();break;
    case IGET_BOOLEAN:case SGET_BOOLEAN:{FieldReference f=(FieldReference)((ReferenceInstruction)i).getReference();r[((OneRegisterInstruction)i).getRegisterA()]=(f.getName().equals("P0")||f.getName().equals("Q0"))?legacy:false;break;}
    case IGET_OBJECT:r[((OneRegisterInstruction)i).getRegisterA()]=new Object();break;
    case IPUT_OBJECT:case IPUT_BOOLEAN:break;
    case NEW_INSTANCE:{String type=((TypeReference)((ReferenceInstruction)i).getReference()).getType();r[((OneRegisterInstruction)i).getRegisterA()]=type.equals("Ljava/lang/StringBuilder;")?new StringBuilder():new Object();break;}
    case MOVE_RESULT:case MOVE_RESULT_OBJECT:r[((OneRegisterInstruction)i).getRegisterA()]=result;break;
    case IF_EQZ:case IF_NEZ:{boolean b=truth(r[((OneRegisterInstruction)i).getRegisterA()]);jump=op==Opcode.IF_NEZ?b:!b;break;}
    case IF_GT:case IF_GE:case IF_LT:case IF_LE:{TwoRegisterInstruction v=(TwoRegisterInstruction)i;int a=((Number)r[v.getRegisterA()]).intValue(),b=((Number)r[v.getRegisterB()]).intValue();jump=op==Opcode.IF_GT?a>b:op==Opcode.IF_GE?a>=b:op==Opcode.IF_LT?a<b:a<=b;break;}
    case GOTO:case GOTO_16:case GOTO_32:jump=true;break;
    case INVOKE_STATIC:case INVOKE_STATIC_RANGE:case INVOKE_VIRTUAL:case INVOKE_VIRTUAL_RANGE:case INVOKE_DIRECT:case INVOKE_DIRECT_RANGE:{MethodReference m=(MethodReference)((ReferenceInstruction)i).getReference();int[] a=regs(i);String c=m.getDefiningClass(),name=m.getName();result=null;
     if(c.equals("Le/e/a/PlaybackUrls;")&&name.equals("isHls"))result=e.e.a.PlaybackUrls.isHls((String)r[a[0]]);
     else if(c.equals("Le/e/a/CachePlayback;")&&name.equals("parse"))result=normalize?e.e.a.PlaybackUrls.directHls((String)r[a[0]]):r[a[0]];
     else if(c.equals("Le/e/a/CachePlayback;")&&name.equals("dataSource"))return normalize?e.e.a.PlaybackUrls.directHls((String)r[a[1]]):(String)r[a[1]];
     else if(c.equals("Ljava/lang/String;")&&name.equals("length"))result=((String)r[a[0]]).length();
     else if(c.equals("Ljava/lang/String;")&&name.equals("contains"))result=((String)r[a[0]]).contains((String)r[a[1]]);
     else if(c.equals("Ljava/lang/StringBuilder;")&&name.equals("append"))result=((StringBuilder)r[a[0]]).append(r[a[1]]);
     else if(c.equals("Ljava/lang/StringBuilder;")&&name.equals("toString"))result=r[a[0]].toString();
     else if(name.equals("setVideoURI"))return (String)r[a[1]];
     else if(name.equals("Z")||name.equals("a")&&m.getReturnType().equals("Ljava/lang/String;"))result=null;
     else if(name.equals("<init>")||name.equals("setHandleAudioFocus")||name.equals("setRepeatMode")){}
     else throw new AssertionError("Unhandled native operation: "+c+"->"+name);
     break;}
    default:throw new AssertionError("Unhandled native opcode "+op+" in "+method.getDefiningClass());
   }
   if(jump)next=at+((OffsetInstruction)i).getCodeOffset();at=next;Integer pos=addresses.get(at);if(pos==null)throw new AssertionError("Invalid branch");pc=pos;
  }throw new AssertionError("No media URL selected");
 }
 public static void main(String[] args)throws Exception{MultiDexContainer<? extends DexFile> zip=DexFileFactory.loadDexContainer(new File(args[0]),Opcodes.getDefault());Map<String,ClassDef> classes=new HashMap<>();for(String name:zip.getDexEntryNames())for(ClassDef c:zip.getEntry(name).getDexFile().getClasses())classes.put(c.getType(),c);boolean normalize=classes.containsKey("Le/e/a/PlaybackUrls;");String[] owners={"Lcom/sauzask/nicoid/NicoidVideoFragment;","Le/e/a/a3;","Lcom/sauzask/nicoid/NicoidPopupViewService;"};int checks=0;for(String owner:owners){String name=owner.contains("Popup")?"c":"b";Method selected=null;for(Method m:classes.get(owner).getMethods())if(m.getName().equals(name)&&m.getParameterTypes().toString().equals("[Ljava/lang/String;]")&&m.getReturnType().equals("V"))selected=m;if(selected==null)throw new AssertionError("Missing player path");for(boolean legacy:new boolean[]{false,true})for(String url:new String[]{"https://delivery.domand.nicovideo.jp/master.m3u8?token=sample","http://127.0.0.1:12345/token/root/sm1.m3u8"}){String result=execute(selected,url,legacy,normalize);if(!url.equals(result))throw new AssertionError(owner+" legacy="+legacy+" wrapped HLS in MP4 proxy");checks++;}}System.out.println("Native HLS selection: "+checks+" normal/shorts/fallback/popup routes passed");}
}

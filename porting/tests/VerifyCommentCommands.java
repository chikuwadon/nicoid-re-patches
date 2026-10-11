import java.io.File;
import java.util.*;
import org.jf.dexlib2.*;
import org.jf.dexlib2.iface.*;
import org.jf.dexlib2.iface.instruction.*;
import org.jf.dexlib2.iface.reference.*;

/** Check the shipped loader routes native command results into renderer fields. */
public final class VerifyCommentCommands {
    static void require(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
    public static void main(String[] args) throws Exception {
        Map<String, ClassDef> classes = new HashMap<>();
        MultiDexContainer<? extends DexFile> dex = DexFileFactory.loadDexContainer(new File(args[0]), Opcodes.getDefault());
        for (String entry : dex.getDexEntryNames())
            for (ClassDef type : dex.getEntry(entry).getDexFile().getClasses()) classes.put(type.getType(), type);
        Method load = null, parser = null;
        for (Method m : classes.get("Le/e/a/ModernComments;").getMethods()) if (m.getName().equals("load")) load = m;
        for (Method m : classes.get("Le/e/a/d0;").getMethods())
            if (m.getName().equals("a") && m.getParameterTypes().toString().equals("[Ljava/lang/String;, [Ljava/lang/Integer;, [Ljava/lang/String;]")) parser = m;
        require(load != null && parser != null, "Loader and native command parser must ship together");
        Set<String> tokens = new HashSet<>();
        boolean hex = false;
        for (Instruction i : parser.getImplementation().getInstructions()) if (i instanceof ReferenceInstruction) {
            Reference r = ((ReferenceInstruction)i).getReference();
            if (r instanceof StringReference) tokens.add(((StringReference)r).getString());
            if (r instanceof MethodReference && ((MethodReference)r).getName().equals("parseColor")) hex = true;
        }
        require(tokens.containsAll(Arrays.asList("red", "blue", "white", "red2", "ue", "shita", "naka", "big", "small", "medium")) && hex,
            "Named/hex colors, placement and size commands must retain native support");
        Map<Integer, Object> registers = new HashMap<>();
        Map<String, Object> arrays = new HashMap<>();
        int parsed = -1;
        boolean constructed = false;
        for (Instruction i : load.getImplementation().getInstructions()) {
            if (i instanceof NarrowLiteralInstruction && i instanceof OneRegisterInstruction)
                registers.put(((OneRegisterInstruction)i).getRegisterA(), ((NarrowLiteralInstruction)i).getNarrowLiteral());
            if (i instanceof ReferenceInstruction && ((ReferenceInstruction)i).getReference() instanceof MethodReference) {
                MethodReference call = (MethodReference)((ReferenceInstruction)i).getReference();
                if (call.getDefiningClass().equals("Le/e/a/d0;") && call.getName().equals("a") && call.getParameterTypes().size() == 3)
                    parsed = ((FiveRegisterInstruction)i).getRegisterE();
                if (call.getDefiningClass().equals("Le/e/a/q;") && call.getName().equals("<init>")) {
                    require(parsed >= 0, "Commands must be parsed before constructing the comment");
                    int metadata = ((FiveRegisterInstruction)i).getRegisterF();
                    require(("parsed:2").equals(arrays.get(metadata + ":2")), "Paint color must use parsed color");
                    require(("parsed:0").equals(arrays.get(metadata + ":3")), "Text size must use parsed size");
                    require(("parsed:1").equals(arrays.get(metadata + ":4")), "Placement must use parsed position");
                    constructed = true;
                }
            }
            if (i instanceof ThreeRegisterInstruction) {
                ThreeRegisterInstruction t = (ThreeRegisterInstruction)i;
                Object index = registers.get(t.getRegisterC());
                if (i.getOpcode() == Opcode.AGET_OBJECT && t.getRegisterB() == parsed)
                    registers.put(t.getRegisterA(), "parsed:" + index);
                if (i.getOpcode() == Opcode.APUT_OBJECT)
                    arrays.put(t.getRegisterB() + ":" + index, registers.get(t.getRegisterA()));
            }
        }
        require(constructed, "A parsed comment must reach the renderer");
        System.out.println("Comment command DEX checks passed: named/hex colors, upper/lower placement and text sizes");
    }
}

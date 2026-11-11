import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.writer.io.FileDataStore;
import org.jf.dexlib2.writer.pool.DexPool;

final class MergeLiveDex {
    private static DexBackedDexFile read(String path) throws IOException {
        return org.jf.dexlib2.DexFileFactory.loadDexFile(new File(path), Opcodes.forApi(28));
    }

    public static void main(String[] args) throws IOException {
        DexBackedDexFile baseline = read(args[0]);
        DexBackedDexFile hooks = read(args[1]);
        DexBackedDexFile live = read(args[2]);
        DexPool pool = new DexPool(baseline.getOpcodes());
        Set<String> replacements = new HashSet<>();
        for (ClassDef cls : hooks.getClasses()) {
            replacements.add(cls.getType());
            pool.internClass(cls);
        }
        Set<String> types = new HashSet<>();
        for (ClassDef cls : baseline.getClasses()) {
            types.add(cls.getType());
            if (!replacements.contains(cls.getType())) pool.internClass(cls);
        }
        if (!types.containsAll(replacements)) throw new IOException("Unknown replacement class");
        for (ClassDef cls : live.getClasses()) {
            String type = cls.getType();
            if (types.add(type)) {
                pool.internClass(cls);
            } else if (!type.startsWith("Lcom/android/tools/r8/annotations/")) {
                throw new IOException("Live dependency collides with stable helper: " + type);
            }
        }
        FileDataStore output = new FileDataStore(new File(args[3]));
        try {
            pool.writeTo(output);
        } finally {
            output.close();
        }
        System.out.println("Verified helper classes: " + types.size());
    }
}

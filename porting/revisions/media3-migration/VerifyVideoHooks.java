import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.Method;

final class VerifyVideoHooks {
    private static final String DELEGATE =
        "Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;";

    private static ClassDef find(String file, String type) throws IOException {
        for (ClassDef cls : DexFileFactory.loadDexFile(new File(file), Opcodes.forApi(28)).getClasses()) {
            if (type.equals(cls.getType())) {
                return cls;
            }
        }
        throw new IllegalArgumentException("Missing " + type);
    }

    private static String signature(Method method) {
        return method.getName() + method.getParameterTypes() + method.getReturnType();
    }

    public static void main(String[] args) throws IOException {
        ClassDef original = find(args[0], DELEGATE);
        ClassDef replacement = find(args[1], DELEGATE);
        Map<String, Integer> methods = new HashMap<>();
        for (Method method : original.getMethods()) {
            methods.put(signature(method), method.getAccessFlags());
        }
        for (Method method : replacement.getMethods()) {
            Integer flags = methods.get(signature(method));
            if (flags == null || flags != method.getAccessFlags()) {
                throw new IllegalStateException("Unexpected method override: " + signature(method));
            }
        }
        System.out.println("PASS: Media3 overrides match the original APK video delegate");
    }
}

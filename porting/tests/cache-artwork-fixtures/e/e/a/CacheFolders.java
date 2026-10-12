package e.e.a;
import android.content.Context;
/** The fixture's files directory represents an explicitly selected cache root. */
public final class CacheFolders {
    public static String root(Context context){return context.getFilesDir().getAbsolutePath();}
}

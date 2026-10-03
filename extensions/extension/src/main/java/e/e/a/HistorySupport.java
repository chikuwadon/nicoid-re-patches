package e.e.a;

import android.content.Context;
import org.json.JSONArray;

public final class HistorySupport {
    private HistorySupport() { }
    public static String format(String ignored, Object[] values) {
        return String.format(UiStrings.translate("<b>%s</b> <font color='red'>%s回視聴</font>"), values);
    }
    public static int write(int type, JSONArray data, Context context) throws Exception {
        int result = (Integer) Class.forName("e.e.a.v0").getMethod("a", int.class, JSONArray.class, Context.class)
            .invoke(null, type, data, context);
        if (result != 1) {
            android.widget.Toast.makeText(context, UiStrings.translate("履歴を削除できませんでした。再試行してください。"), android.widget.Toast.LENGTH_LONG).show();
            throw new java.io.IOException("History could not be saved");
        }
        return result;
    }
}

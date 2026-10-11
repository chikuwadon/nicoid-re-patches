package e.e.a;
import android.view.View;
/** Theme player information surfaces without recoloring the video overlay. */
public final class PlayerTheme {
    private PlayerTheme() {}
    public static void apply(View root) {
        ThemeChoice.background(root);
        android.content.Context c=root.getContext();
        for(String name:new String[]{"info","tabinfo","pager_title_strip","pager","scrollView1"}) {
            int id=c.getResources().getIdentifier(name,"id",c.getPackageName());
            View view=id==0?null:root.findViewById(id);
            if(view!=null) {
                ThemeChoice.background(view);
                if(name.equals("pager_title_strip"))try {
                    view.getClass().getMethod("setTextColor",int.class).invoke(view,ThemeChoice.textColor(view));
                }catch(Exception ignored){}
            }
        }
    }
}

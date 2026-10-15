package e.e.a;
import android.content.*;import android.graphics.drawable.*;import android.view.*;import android.widget.*;
public final class ListDividers191 {
 private ListDividers191(){}
 public static int color(Context c){String name=ThemeChoice.isNight(c)?"nicoid_list_divider_dark":"nicoid_list_divider_light";int id=c.getResources().getIdentifier(name,"color",c.getPackageName());return id==0?(ThemeChoice.isNight(c)?0x22eeeeee:0x22202124):c.getResources().getColor(id);}
 public static void tree(View v){if(v==null||UiTheme191.media(v))return;if("nicoid-list-divider".equals(v.getTag())){int color=color(v.getContext());if(!(v.getBackground() instanceof ColorDrawable)||((ColorDrawable)v.getBackground()).getColor()!=color)v.setBackgroundColor(color);}if(v instanceof ListView){ListView list=(ListView)v;if(list.getDivider()!=null&&(!(list.getDivider() instanceof ColorDrawable)||((ColorDrawable)list.getDivider()).getColor()!=color(v.getContext()))){int height=list.getDividerHeight();list.setDivider(new ColorDrawable(color(v.getContext())));list.setDividerHeight(height);}}if(v instanceof ViewGroup){ViewGroup g=(ViewGroup)v;for(int n=0;n<g.getChildCount();n++)tree(g.getChildAt(n));}}
}

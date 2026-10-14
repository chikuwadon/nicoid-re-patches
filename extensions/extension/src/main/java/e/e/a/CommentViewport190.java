package e.e.a;
import android.graphics.*;import android.view.*;
/** Surface buffers can retain the pre-dock size. Use the visible comment viewport. */
public final class CommentViewport190 {
 public static void begin(Object renderer,Canvas canvas){canvas.save();if(renderer instanceof View){Rect clip=((View)renderer).getClipBounds();if(clip!=null){canvas.clipRect(clip);canvas.translate(clip.left,clip.top);}}}
 public static void end(Canvas canvas){canvas.restore();}
 public static int width(Object renderer,Canvas canvas){return size(renderer,canvas.getWidth(),true);}
 public static int height(Object renderer,Canvas canvas){return size(renderer,canvas.getHeight(),false);}
 static int size(Object renderer,int fallback,boolean horizontal){if(!(renderer instanceof View))return fallback;View view=(View)renderer;int size=horizontal?view.getWidth():view.getHeight();Rect clip=view.getClipBounds();if(clip!=null)size=Math.min(size,horizontal?clip.width():clip.height());return size>0?Math.min(size,fallback):fallback;}
}

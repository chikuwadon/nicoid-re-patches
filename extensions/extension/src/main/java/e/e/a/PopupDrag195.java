package e.e.a;
/** Window origin bounds use the measured popup size, not WRAP_CONTENT (-2). */
public final class PopupDrag195 {
 private PopupDrag195(){}
 public static int clamp(int requested,int screen,int measured){return Math.max(0,Math.min(Math.max(0,screen-Math.max(0,measured)),requested));}
}

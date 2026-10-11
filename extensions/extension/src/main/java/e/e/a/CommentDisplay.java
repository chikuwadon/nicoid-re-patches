package e.e.a;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.view.*;

/** Give Android a refresh-rate hint once when a comment renderer starts. */
public final class CommentDisplay {
    private CommentDisplay() {}
    public static void request(View view,int fps) {
        view.post(()->{
            try {
                if(!view.isAttachedToWindow())return;
                if(Build.VERSION.SDK_INT>=30) {
                    if(view instanceof SurfaceView){Surface surface=((SurfaceView)view).getHolder().getSurface();if(surface!=null&&surface.isValid())surface.setFrameRate(fps,Surface.FRAME_RATE_COMPATIBILITY_DEFAULT);}
                    else if(view instanceof TextureView&&((TextureView)view).isAvailable()){
                        Surface surface=new Surface(((TextureView)view).getSurfaceTexture());try{surface.setFrameRate(fps,Surface.FRAME_RATE_COMPATIBILITY_DEFAULT);}finally{surface.release();}
                    }
                }
                Context c=view.getContext();for(int n=0;n<12&&c instanceof ContextWrapper&&!(c instanceof Activity);n++){Context next=((ContextWrapper)c).getBaseContext();if(next==c)break;c=next;}
                if(!(c instanceof Activity)||Build.VERSION.SDK_INT<23)return;
                Activity activity=(Activity)c;Display display=view.getDisplay();if(display==null)return;
                WindowManager.LayoutParams attributes=activity.getWindow().getAttributes();
                if(fps<=60){attributes.preferredRefreshRate=0;attributes.preferredDisplayModeId=0;activity.getWindow().setAttributes(attributes);return;}
                Display.Mode current=display.getMode(),best=null;
                for(Display.Mode mode:display.getSupportedModes())if(mode.getPhysicalWidth()==current.getPhysicalWidth()&&mode.getPhysicalHeight()==current.getPhysicalHeight()&&mode.getRefreshRate()+.5f>=fps&&(best==null||mode.getRefreshRate()<best.getRefreshRate()))best=mode;
                if(best!=null){attributes.preferredDisplayModeId=best.getModeId();attributes.preferredRefreshRate=best.getRefreshRate();activity.getWindow().setAttributes(attributes);}
            }catch(RuntimeException error){android.util.Log.i("nicoid-comments","Display refresh hint unavailable");}
        });
    }
}

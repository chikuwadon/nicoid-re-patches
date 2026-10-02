package e.e.a;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

/** Self-contained game with original artwork; no network, audio, or extra Activity. */
public final class BikeRun {
    private BikeRun() {}
    public static void open(Activity activity) {
        if (activity.isFinishing()) return;
        Dialog dialog=new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        FrameLayout root=new FrameLayout(activity);
        Track track=new Track(activity);
        root.addView(track, new FrameLayout.LayoutParams(-1,-1));
        TextView close=new TextView(activity); close.setText("×"); close.setTextSize(28);
        close.setGravity(Gravity.CENTER); close.setTextColor(track.accent);
        close.setContentDescription("ミニゲームを閉じる"); close.setOnClickListener(v->dialog.dismiss());
        int size=Math.round(56*activity.getResources().getDisplayMetrics().density);
        root.addView(close,new FrameLayout.LayoutParams(size,size,Gravity.TOP|Gravity.END));
        dialog.setContentView(root); dialog.setOnDismissListener(d->track.active=false);
        dialog.show();
        Window window=dialog.getWindow();
        if(window!=null) { window.setLayout(-1,-1); window.setBackgroundDrawableResource(android.R.color.transparent); window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); }
    }
    private static int color(Context c,int attr,int fallback) {
        TypedValue value=new TypedValue();
        if(!c.getTheme().resolveAttribute(attr,value,true))return fallback;
        if(value.resourceId!=0)try{return c.getResources().getColorStateList(value.resourceId).getDefaultColor();}catch(Exception ignored){}
        return value.data;
    }
    private static final class Track extends View {
        final BikeRunModel game=new BikeRunModel(System.nanoTime());
        final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        final int accent,background,foreground;
        int best;
        long last;
        boolean active=true, recorded;
        Track(Context c) {
            super(c); accent=color(c,0x7f03005e,0xff52cca3);
            background=color(c,android.R.attr.colorBackground,0xff191b20);
            foreground=color(c,android.R.attr.textColorPrimary,0xffeeeeee);
            best=PreferenceManager.getDefaultSharedPreferences(c).getInt("bike_run_best",0);
            setContentDescription("自転車ラン。タップでジャンプ。障害物と穴を避けます。終了後はタップで再挑戦。");
            setFocusable(true);
        }
        void line(Canvas c,float x,float y,float xx,float yy) { c.drawLine(x,y,xx,yy,paint); }
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas); if(!active)return;
            long now=SystemClock.uptimeMillis();
            if(hasWindowFocus()&&last!=0)game.step((now-last)/1000f);
            last=hasWindowFocus()?now:0;
            if(game.over&&!recorded) {
                recorded=true;
                if(game.score()>best) { best=game.score(); PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putInt("bike_run_best",best).apply(); }
            }
            canvas.drawColor(background); canvas.save();
            float scale=getWidth()/720f; if(scale<=0)return;
            canvas.scale(scale,scale); float height=getHeight()/scale,ground=height-130;
            paint.setStyle(Paint.Style.FILL); paint.setColor(foreground); paint.setTextSize(25);
            canvas.drawText("自転車ラン",24,44,paint); paint.setTextSize(18);
            canvas.drawText("距離 "+game.score()+" m   ベスト "+best+" m",24,78,paint);
            paint.setColor(accent); paint.setStrokeWidth(3);
            line(canvas,0,ground,720,ground);
            for(BikeRunModel.Hazard h:game.hazards) {
                if(h.gap) { paint.setColor(background); canvas.drawRect(h.x,ground-3,h.x+h.width,ground+12,paint); paint.setColor(accent);
                    line(canvas,h.x,ground,h.x,ground+24); line(canvas,h.x+h.width,ground,h.x+h.width,ground+24);
                } else { paint.setColor(accent); canvas.drawRoundRect(h.x,ground-h.height,h.x+h.width,ground,5,5,paint); }
            }
            paint.setColor(accent); paint.setStyle(Paint.Style.STROKE); paint.setStrokeWidth(4);
            float x=BikeRunModel.RIDER_X,y=ground+game.y-17;
            canvas.drawCircle(x-24,y,16,paint); canvas.drawCircle(x+24,y,16,paint);
            line(canvas,x-24,y,x-7,y-26); line(canvas,x-7,y-26,x+5,y); line(canvas,x+5,y,x-24,y);
            line(canvas,x+5,y,x+19,y-28); line(canvas,x+19,y-28,x+24,y); line(canvas,x-7,y-26,x+19,y-28);
            line(canvas,x+19,y-28,x+15,y-36); line(canvas,x+15,y-36,x+27,y-36);
            canvas.drawCircle(x-1,y-60,9,paint); line(canvas,x-4,y-50,x-13,y-29); line(canvas,x-13,y-29,x+5,y-16);
            line(canvas,x+5,y-16,x-4,y); line(canvas,x-4,y-50,x+16,y-35);
            paint.setStyle(Paint.Style.FILL); paint.setColor(foreground); paint.setTextAlign(Paint.Align.CENTER); paint.setTextSize(22);
            canvas.drawText("タップでジャンプ",360,height-72,paint);
            if(!game.started||game.over) {
                paint.setTextSize(32); canvas.drawText(game.over?"ゲームオーバー":"障害物と穴をジャンプで避けよう",360,height/2,paint);
                paint.setTextSize(22); canvas.drawText(game.over?"タップで再挑戦":"タップしてスタート",360,height/2+42,paint);
            }
            paint.setTextAlign(Paint.Align.LEFT); canvas.restore();
            if(active&&hasWindowFocus()&&game.started&&!game.over)postInvalidateOnAnimation();
        }
        public boolean onTouchEvent(MotionEvent e) {
            if(e.getActionMasked()==MotionEvent.ACTION_UP) { performClick(); recorded=false; game.tap(); last=0; invalidate(); }
            return true;
        }
        public boolean performClick() { super.performClick(); return true; }
        public void onWindowFocusChanged(boolean focused) { super.onWindowFocusChanged(focused); last=0; if(focused)invalidate(); }
        protected void onDetachedFromWindow() { active=false; super.onDetachedFromWindow(); }
    }
}

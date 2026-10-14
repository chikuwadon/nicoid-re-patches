package e.e.a;
import android.content.Context;import android.graphics.*;import android.graphics.drawable.Drawable;
/** Small vector UI symbols using the common application palette. */
public final class UiGlyph190 extends Drawable {
 final Context context;final String kind;final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
 public UiGlyph190(Context c,String kind){context=c;this.kind=kind;setBounds(0,0,PanelUi.dp(c,32),PanelUi.dp(c,32));}
 public void draw(Canvas canvas){Rect b=getBounds();canvas.save();canvas.translate(b.left,b.top);canvas.scale(b.width()/32f,b.height()/32f);paint.setColor(DialogColors.muted(ThemeChoice.isNight(context)));paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(1.8f);paint.setStrokeCap(Paint.Cap.ROUND);paint.setStrokeJoin(Paint.Join.ROUND);
  if(kind.equals("editor")){paint.setTextSize(7);paint.setStyle(Paint.Style.FILL);for(int i=0;i<3;i++){canvas.drawText(Integer.toString(i+1),1,8+i*9,paint);paint.setColor(i==0?UiDialogs.accent(context):DialogColors.muted(ThemeChoice.isNight(context)));canvas.drawRoundRect(8,3+i*9,25-i*3,6+i*9,1.5f,1.5f,paint);paint.setColor(DialogColors.muted(ThemeChoice.isNight(context)));}paint.setStyle(Paint.Style.STROKE);Path p=new Path();p.moveTo(20,25);p.lineTo(27,18);p.lineTo(30,21);p.lineTo(23,28);p.lineTo(19,29);p.close();canvas.drawPath(p,paint);}
  else if(kind.equals("filter")){Path p=new Path();p.moveTo(5,6);p.lineTo(27,6);p.lineTo(27,10);p.lineTo(19,18);p.lineTo(19,27);p.lineTo(13,24);p.lineTo(13,18);p.lineTo(5,10);p.close();canvas.drawPath(p,paint);}
  else{canvas.drawCircle(16,10,5,paint);canvas.drawArc(7,16,25,32,180,180,false,paint);}canvas.restore();
 }
 public void setAlpha(int alpha){paint.setAlpha(alpha);invalidateSelf();}public void setColorFilter(ColorFilter filter){paint.setColorFilter(filter);invalidateSelf();}public int getOpacity(){return PixelFormat.TRANSLUCENT;}
}

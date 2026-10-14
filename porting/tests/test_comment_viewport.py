"""Exercise docked comment bounds when the Surface buffer is larger than its View."""
from pathlib import Path
import subprocess, sys, tempfile
repo = Path(__file__).resolve().parents[2]
with tempfile.TemporaryDirectory() as folder:
    root = Path(folder)
    sources = {
        'android/graphics/Rect.java': 'package android.graphics;public class Rect{public int left,top,right,bottom;public Rect(int w,int h){right=w;bottom=h;}public int width(){return right-left;}public int height(){return bottom-top;}}',
        'android/graphics/Canvas.java': 'package android.graphics;public class Canvas{public int x,y,saves;public Rect clip;public void save(){saves++;}public void restore(){saves--;x=0;y=0;}public void translate(float a,float b){x+=(int)a;y+=(int)b;}public void clipRect(Rect r){clip=r;}public int getWidth(){return 720;}public int getHeight(){return 540;}}',
        'android/view/View.java': 'package android.view;import android.graphics.Rect;public class View{public Rect clip;public int getWidth(){return 720;}public int getHeight(){return 360;}public Rect getClipBounds(){return clip;}}',
        'e/e/a/ViewportTest.java': '''package e.e.a;import android.graphics.*;import android.view.*;public class ViewportTest{public static void main(String[] args){Canvas canvas=new Canvas();View view=new View();if(CommentViewport190.height(view,canvas)!=360)throw new AssertionError("Old Surface height used");view.clip=new Rect(700,320);if(CommentViewport190.height(view,canvas)!=320||CommentViewport190.width(view,canvas)!=700)throw new AssertionError("Clip bounds ignored");view.clip.left=10;view.clip.top=40;CommentViewport190.begin(view,canvas);if(canvas.x!=10||canvas.y!=40||canvas.saves!=1||CommentViewport190.height(view,canvas)!=280||CommentViewport190.width(view,canvas)!=690)throw new AssertionError("Command drawing origin and viewport dimensions disagree");CommentViewport190.end(canvas);if(canvas.saves!=0||canvas.y!=0)throw new AssertionError("Canvas transform leaked to later drawings");if(CommentViewport190.height(new Object(),canvas)!=540)throw new AssertionError("Non-View fallback changed");System.out.println("Comment viewport / oversized Surface regression passed");}}''',
    }
    for name, source in sources.items():
        file = root / name
        file.parent.mkdir(parents=True, exist_ok=True)
        file.write_text(source)
    subprocess.run(['java','-jar',sys.argv[1],'-1.8','-warn:none','-d',str(root),*map(str,root.rglob('*.java')),str(repo/'extensions/extension/src/main/java/e/e/a/CommentViewport190.java')],check=True)
    subprocess.run(['java','-cp',str(root),'e.e.a.ViewportTest'],check=True)

import java.util.ArrayList;
import e.e.a.CommentUi;
public class CommentUiTest {
 public static class Fragment {
  public Object Z;public android.app.Activity a0;public ArrayList<?> attached=new ArrayList<Object>();public int updates;
  public void bNative(ArrayList<?> values){if(android.os.Looper.worker)throw new AssertionError("Off-main update");attached=new ArrayList<Object>(values);updates++;}
 }
 static void require(boolean x){if(!x)throw new AssertionError();}
 public static void main(String[] args){
  Fragment f=new Fragment();ArrayList<Integer> values=new ArrayList<Integer>();for(int n=0;n<10000;n++)values.add(n);
  CommentUi.list(f,values);require(f.updates==0);f.Z=new Object();f.a0=new android.app.Activity();CommentUi.ready(f);require(f.attached.size()==10000);
  ArrayList<?> visible=f.attached;android.os.Looper.worker=true;ArrayList<Integer> next=new ArrayList<Integer>(values.subList(0,5000));CommentUi.list(f,next);next.clear();
  for(int n=9999;n>=0;n--)require(((Integer)visible.get(n))==n);
  require(f.attached==visible);android.os.Handler.drain();require(f.attached.size()==5000&&visible.size()==10000);
  f.a0.finishing=true;CommentUi.list(f,values);require(f.attached.size()==5000);
  System.out.println("Comments: 10000 to 5000 early-scroll snapshot, queued UI update and lifecycle passed");
 }
}

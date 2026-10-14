package e.e.a;
public final class FollowingGesture190Test {
 static void check(boolean ok,String name){if(!ok)throw new AssertionError(name);}
 public static void main(String[] args){
  check(!Following190.Gestures.isHorizontal(5,1,8),"Touch slop keeps taps and long presses");
  check(!Following190.Gestures.isHorizontal(40,100,8),"Vertical list scroll stays with child");
  check(!Following190.Gestures.isHorizontal(30,28,8),"Diagonal gestures do not steal scroll");
  check(Following190.Gestures.isHorizontal(-80,10,8)&&Following190.Gestures.isHorizontal(80,10,8),"Swipe supports both directions");
  check(Following190.Gestures.shouldSwitch(-100,0,400,600),"Long left swipe");
  check(Following190.Gestures.shouldSwitch(25,900,400,600),"Short intentional right fling");
  check(!Following190.Gestures.shouldSwitch(25,-900,400,600),"Reversed fling does not switch");
  check(!Following190.Gestures.shouldSwitch(5,900,400,600),"Tiny movement does not switch");
  check(!Following190.Gestures.shouldSwitch(60,0,400,600),"Incomplete swipe stays on current tab");
  check(!Following190.Gestures.shouldSwitch(100,0,0,600),"Unmeasured pager cannot switch");
  System.out.println("Following gesture direction, thresholds and scroll arbitration verified");
 }
}

package e.e.a;
/** Validated shared state for typed input, presets and slider gestures. */
final class DurationRangeModel {
 int low,high;boolean unlimited;
 DurationRangeModel(int low,int high,boolean unlimited){this.low=low;this.high=Math.max(low,high);this.unlimited=unlimited;}
 void typed(int low,int high){if(low<0||high<0||!unlimited&&low>high)throw new IllegalArgumentException();this.low=low;this.high=Math.max(low,high);}
 void dragged(int low,int high){this.low=low;this.high=Math.max(low,high);}
 int maximum(){return unlimited?0:high;}
}

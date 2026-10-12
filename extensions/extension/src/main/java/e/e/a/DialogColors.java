package e.e.a;
/** Neutral controls stay close to the dialog surface in both themes. */
public final class DialogColors {private DialogColors(){}public static int secondary(int surface,boolean dark){float amount=dark?.012f:.04f;int target=dark?255:0;int r=Math.round(((surface>>16)&255)*(1-amount)+target*amount),g=Math.round(((surface>>8)&255)*(1-amount)+target*amount),b=Math.round((surface&255)*(1-amount)+target*amount);return 0xff000000|(r<<16)|(g<<8)|b;}}

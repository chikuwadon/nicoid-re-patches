package e.e.a;
public final class DialogColorsTest {
 static void check(boolean b,String m){if(!b)throw new AssertionError(m);}
 static double l(int c){double sum=0;double[] weight={.0722,.7152,.2126};for(int i=0;i<3;i++){double v=((c>>(i*8))&255)/255.0;sum+=weight[i]*(v<=.04045?v/12.92:Math.pow((v+.055)/1.055,2.4));}return sum;}
 public static void main(String[] args){for(int base:new int[]{0xff191b20,0xff000000,0xff1b1b1f}){int control=DialogColors.secondary(base,true);check(Math.abs((control&255)-(base&255))<=4,"control stays close to reference surface");check((control&255)<64&&((control>>8)&255)<64&&((control>>16)&255)<64,"dark controls stay neutral/dim");check((l(0xffeeeeee)+.05)/(l(control)+.05)>7,"dark label contrast");}int light=DialogColors.secondary(0xfffafafa,false);check((l(light)+.05)/(l(0xff202124)+.05)>7,"light label contrast");System.out.println("Neutral dialog controls: dark/AMOLED/Material You/light contrast checks passed");}
}

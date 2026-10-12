package android.os;
public final class Looper {static final Looper MAIN=new Looper();public static boolean worker;public static Looper getMainLooper(){return MAIN;}public static Looper myLooper(){return worker?null:MAIN;}}

package android.os;
public class Handler {public static final java.util.ArrayList<Runnable> queue=new java.util.ArrayList<>();public Handler(Looper l){}public boolean post(Runnable r){queue.add(r);return true;}public static void drain(){Looper.worker=false;for(Runnable r:new java.util.ArrayList<Runnable>(queue))r.run();queue.clear();}}

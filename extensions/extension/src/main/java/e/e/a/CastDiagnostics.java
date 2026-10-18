package e.e.a;

/** Phase markers only: do not log stream URLs, session cookies, or device addresses. */
public final class CastDiagnostics {
    private CastDiagnostics() { }
    static void record(String message) {
        try { Class.forName("e.e.a.ModernDebug").getMethod("record", String.class).invoke(null, message); }
        catch (ReflectiveOperationException ignored) { }
    }
    public static void sent(Object pending) {
        record("Cast: load message submitted");
        if(pending==null){record("Cast: no send result returned");return;}
        try {
            Class<?> callback=Class.forName("e.d.b.b.e.k.k"),result=Class.forName("e.d.b.b.e.k.j");
            Object listener=java.lang.reflect.Proxy.newProxyInstance(callback.getClassLoader(),new Class<?>[]{callback},(proxy,method,args)->{
                if(method.getDeclaringClass()==Object.class){if(method.getName().equals("hashCode"))return System.identityHashCode(proxy);if(method.getName().equals("equals"))return proxy==args[0];return "Cast send observer";}
                if(args!=null&&args.length==1){try{Object status=result.getMethod("j").invoke(args[0]);record("Cast: load message result: "+String.valueOf(status));}catch(ReflectiveOperationException e){record("Cast: send result unavailable");}}
                return null;
            });
            Class.forName("e.d.b.b.e.k.g").getMethod("a",callback).invoke(pending,listener);
        }catch(ReflectiveOperationException e){record("Cast: send observer unavailable: "+e.getClass().getSimpleName());}
    }
    public static void discovery() { record("Cast: discovery started for legacy receiver FAB5A9D8"); }
    public static void connected() { record("Cast: Google API connection established; launching legacy receiver"); }
    public static void receiverResult(Object status) { record("Cast: receiver launch result: " + String.valueOf(status)); }
    public static void stream(String url) {
        if (url == null) { record("Cast: stream acquisition failed"); return; }
        String path;
        try { path = new java.net.URL(url).getPath(); }
        catch (java.net.MalformedURLException ignored) { path = url; }
        record(path.contains(".m3u8") ? "Cast: HLS stream acquired; preparing authenticated relay" : "Cast: stream reached legacy relay");
    }
}

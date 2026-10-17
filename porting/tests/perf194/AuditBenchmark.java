package e.e.a;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/** Host microbenchmarks and deterministic request counts, not device timings. */
public final class AuditBenchmark {
    static volatile long sink;
    static final String[] UNIQUE_DATES=new String[2000];
    static {java.time.format.DateTimeFormatter format=java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");for(int i=0;i<UNIQUE_DATES.length;i++)UNIQUE_DATES[i]=java.time.LocalDateTime.of(2026,10,1,0,0).plusMinutes(i).format(format);}
    static long allocated(){com.sun.management.ThreadMXBean bean=(com.sun.management.ThreadMXBean)ManagementFactory.getThreadMXBean();return bean.getThreadAllocatedBytes(Thread.currentThread().getId());}
    static String measure(Runnable action){long[] time=new long[7],bytes=new long[7];action.run();action.run();for(int i=0;i<7;i++){long b=allocated(),t=System.nanoTime();action.run();time[i]=System.nanoTime()-t;bytes[i]=allocated()-b;}Arrays.sort(time);Arrays.sort(bytes);return "{\"median_ms\":"+time[3]/1e6+",\"allocated_bytes\":"+bytes[3]+",\"samples\":7}";}
    static void date(){for(int i=0;i<10000;i++)sink+=VideoRows191.millis("2026/10/18 02:30")+VideoRows191.date(1792258200000L,"投稿").length();}
    static void uniqueDates(){for(String date:UNIQUE_DATES)sink+=VideoRows191.millis(date);}
    static void filters(){ContentFilterRules.Rule[] rules=new ContentFilterRules.Rule[100];for(int i=0;i<rules.length;i++)rules[i]=new ContentFilterRules.Rule("unmatched-"+i,"partial",true);for(int n=0;n<3000;n++){String s="ＦＵＬＬＷＩＤＴＨ 動画タイトル "+n;for(ContentFilterRules.Rule rule:rules)if(rule.matches(s))sink++;}}
    static class Connection extends HttpURLConnection {
        static final byte[] BODY=("<meta name=\"server-response\" content=\""+String.join("",Collections.nCopies(4096,"日本語 payload "))+"\">").getBytes(StandardCharsets.UTF_8);
        final AtomicInteger count;final CountDownLatch release;boolean delay=true;
        Connection(AtomicInteger count,CountDownLatch release)throws Exception{super(new URL("https://www.nicovideo.jp/search/benchmark"));this.count=count;this.release=release;}
        public InputStream getInputStream()throws IOException{count.incrementAndGet();try{release.await();if(delay)Thread.sleep(30);}catch(InterruptedException ex){Thread.currentThread().interrupt();throw new InterruptedIOException();}return new ByteArrayInputStream(BODY);}
        public void disconnect(){}public void connect(){}public boolean usingProxy(){return false;}
    }
    static void pageMisses(){try{for(int i=0;i<20;i++){PageCache.refresh();Connection c=new Connection(new AtomicInteger(),new CountDownLatch(0));c.delay=false;try(InputStream in=PageCache.input(c)){byte[] b=new byte[8192];for(int n;(n=in.read(b))!=-1;)sink+=n;}}}catch(Exception ex){throw new RuntimeException(ex);}}
    static String requests()throws Exception{PageCache.refresh();AtomicInteger reads=new AtomicInteger();CountDownLatch ready=new CountDownLatch(8),release=new CountDownLatch(1);ExecutorService pool=Executors.newFixedThreadPool(8);try{List<Future<Long>> pending=new ArrayList<>();long start=System.nanoTime();for(int i=0;i<8;i++)pending.add(pool.submit(()->{Connection c=new Connection(reads,release);ready.countDown();ready.await();long n=0;try(InputStream in=PageCache.input(c)){byte[] b=new byte[8192];for(int k;(k=in.read(b))!=-1;)n+=k;}return n;}));ready.await();release.countDown();for(Future<Long> f:pending)f.get();return "{\"requests\":"+reads.get()+",\"response_bytes\":"+(long)reads.get()*Connection.BODY.length+",\"wall_ms\":"+(System.nanoTime()-start)/1e6+",\"consumers\":8,\"fixture_body_bytes\":"+Connection.BODY.length+"}";}finally{release.countDown();pool.shutdownNow();}}
    static String queue()throws Exception{ThreadPoolExecutor pool=new ThreadPoolExecutor(1,1,0,TimeUnit.SECONDS,new LinkedBlockingQueue<>());CountDownLatch entered=new CountDownLatch(1),release=new CountDownLatch(1);try{pool.execute(()->{entered.countDown();try{release.await();}catch(InterruptedException ex){Thread.currentThread().interrupt();}});entered.await();List<NetworkTask> jobs=new ArrayList<>();for(int i=0;i<1000;i++){NetworkTask t=new NetworkTask();t.start(pool,()->{});jobs.add(t);}for(NetworkTask t:jobs)t.cancel();return "{\"submitted\":1000,\"cancelled_queue_entries\":"+pool.getQueue().size()+"}";}finally{release.countDown();pool.shutdownNow();}}
    public static void main(String[] args)throws Exception{System.out.println("{\"java\":\""+System.getProperty("java.version")+"\",\"date_10000_binds\":"+measure(AuditBenchmark::date)+",\"date_2000_unique\":"+measure(AuditBenchmark::uniqueDates)+",\"page_20_misses\":"+measure(AuditBenchmark::pageMisses)+",\"filter_3000x100\":"+measure(AuditBenchmark::filters)+",\"concurrent_pages\":"+requests()+",\"cancellation\":"+queue()+"}");}
}

package e.e.a;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/** Deterministic I/O boundaries: no network account or external API is used. */
public final class AuditRegressionTest {
    static int checks;
    static void check(boolean ok, String message) { checks++; if (!ok) throw new AssertionError(message); }
    static final byte[] PAGE = "<meta name=\"server-response\" content=\"{}\">".getBytes(StandardCharsets.UTF_8);
    static class Connection extends HttpURLConnection {
        final AtomicInteger reads; final CountDownLatch entered, release;
        byte[] body = PAGE; boolean fail; volatile boolean disconnected; long closeDelay;
        Connection(String url, AtomicInteger reads, CountDownLatch entered, CountDownLatch release) throws Exception {
            super(new URL(url)); this.reads=reads; this.entered=entered; this.release=release;
        }
        Connection(String url) throws Exception { this(url,new AtomicInteger(),null,null); }
        public InputStream getInputStream() throws IOException {
            reads.incrementAndGet(); if(entered!=null)entered.countDown();
            if(release!=null)try { if(!release.await(5,TimeUnit.SECONDS))throw new IOException("test timeout"); }
            catch(InterruptedException ex){Thread.currentThread().interrupt();throw new InterruptedIOException();}
            if(fail)throw new IOException("offline"); return new ByteArrayInputStream(body);
        }
        public void connect() { }
        public boolean usingProxy() { return false; }
        public void disconnect() { if(closeDelay>0)try{Thread.sleep(closeDelay);}catch(InterruptedException ex){Thread.currentThread().interrupt();} disconnected=true; }
    }
    static byte[] read(InputStream in) throws Exception {
        try(InputStream input=in;ByteArrayOutputStream out=new ByteArrayOutputStream()) {
            byte[] buffer=new byte[4096];for(int n;(n=input.read(buffer))!=-1;)out.write(buffer,0,n);return out.toByteArray();
        }
    }
    static void pageCache() throws Exception {
        ExecutorService pool=Executors.newFixedThreadPool(8);
        try {
            PageCache.refresh(); AtomicInteger reads=new AtomicInteger();CountDownLatch entered=new CountDownLatch(1),release=new CountDownLatch(1);
            String url="https://www.nicovideo.jp/search/shared";
            Future<byte[]> leader=pool.submit(()->read(PageCache.input(new Connection(url,reads,entered,release))));
            check(entered.await(2,TimeUnit.SECONDS),"leader entered");
            List<Future<byte[]>> followers=new ArrayList<>();CountDownLatch ready=new CountDownLatch(7);
            for(int i=0;i<7;i++)followers.add(pool.submit(()->{ready.countDown();return read(PageCache.input(new Connection(url,reads,null,release)));}));
            check(ready.await(2,TimeUnit.SECONDS),"followers ready");release.countDown();
            check(Arrays.equals(PAGE,leader.get(3,TimeUnit.SECONDS)),"leader body");
            for(Future<byte[]> future:followers)check(Arrays.equals(PAGE,future.get(3,TimeUnit.SECONDS)),"independent follower stream");
            check(reads.get()==1,"one read for eight identical concurrent requests");
            Connection accountA=new Connection(url),accountB=new Connection(url);
            accountA.setRequestProperty("Cookie","user_session=A");accountB.setRequestProperty("Cookie","user_session=B");
            read(PageCache.input(accountA));read(PageCache.input(accountB));read(PageCache.input(accountA));
            check(accountA.reads.get()==1&&accountB.reads.get()==1,"auth scopes separate");
            Connection language=new Connection(url);language.setRequestProperty("Accept-Language","en");read(PageCache.input(language));
            check(language.reads.get()==1,"language scope separate");
            PageCache.refresh();Connection failed=new Connection(url);failed.fail=true;
            try{read(PageCache.input(failed));throw new AssertionError("error expected");}catch(IOException expected){}
            failed.fail=false;read(PageCache.input(failed));check(failed.reads.get()==2,"failure releases flight and allows retry");
            PageCache.refresh();Connection invalid=new Connection(url);invalid.body="maintenance".getBytes(StandardCharsets.UTF_8);
            read(PageCache.input(invalid));read(PageCache.input(invalid));check(invalid.reads.get()==2,"invalid page not cached");
            PageCache.refresh();Connection post=new Connection(url);post.setRequestMethod("POST");read(PageCache.input(post));read(PageCache.input(post));check(post.reads.get()==2,"mutations never cached");
            PageCache.refresh();CountDownLatch oldEntered=new CountDownLatch(1),oldRelease=new CountDownLatch(1);AtomicInteger oldReads=new AtomicInteger();
            Future<byte[]> old=pool.submit(()->read(PageCache.input(new Connection(url,oldReads,oldEntered,oldRelease))));check(oldEntered.await(2,TimeUnit.SECONDS),"old generation started");
            PageCache.refresh();Connection fresh=new Connection(url);read(PageCache.input(fresh));oldRelease.countDown();old.get(3,TimeUnit.SECONDS);
            read(PageCache.input(fresh));check(fresh.reads.get()==1,"refresh starts separate flight and old completion cannot overwrite");
            PageCache.refresh();CountDownLatch block=new CountDownLatch(1),go=new CountDownLatch(1);AtomicInteger activeReads=new AtomicInteger();
            Future<byte[]> active=pool.submit(()->read(PageCache.input(new Connection(url,activeReads,block,go))));check(block.await(2,TimeUnit.SECONDS),"leader for cancellation started");
            CountDownLatch waiting=new CountDownLatch(1);Future<Boolean> interrupted=pool.submit(()->{waiting.countDown();Thread.currentThread().interrupt();try{PageCache.input(new Connection(url));return false;}catch(InterruptedIOException expected){return Thread.currentThread().isInterrupted();}});
            check(waiting.await(2,TimeUnit.SECONDS)&&interrupted.get(2,TimeUnit.SECONDS),"interrupted follower exits without cancelling leader");go.countDown();active.get(3,TimeUnit.SECONDS);check(activeReads.get()==1,"leader survives follower cancellation");
            PageCache.refresh();Connection tooBig=new Connection(url);tooBig.body=new byte[8*1024*1024+1];
            try{read(PageCache.input(tooBig));throw new AssertionError("size limit");}catch(IOException expected){}
            tooBig.body=PAGE;read(PageCache.input(tooBig));check(tooBig.reads.get()==2,"oversize releases flight");
        } finally {pool.shutdownNow();PageCache.refresh();}
    }
    static void cancellation() throws Exception {
        ThreadPoolExecutor executor=new ThreadPoolExecutor(1,1,0,TimeUnit.SECONDS,new LinkedBlockingQueue<>());
        CountDownLatch entered=new CountDownLatch(1),release=new CountDownLatch(1);AtomicInteger ran=new AtomicInteger();
        try {
            executor.execute(()->{entered.countDown();try{release.await();}catch(InterruptedException ex){Thread.currentThread().interrupt();}});
            check(entered.await(2,TimeUnit.SECONDS),"queue blocker started");
            List<NetworkTask> tasks=new ArrayList<>();for(int i=0;i<500;i++){NetworkTask task=new NetworkTask();task.start(executor,ran::incrementAndGet);tasks.add(task);}
            check(executor.getQueue().size()==500,"queued work measured");for(NetworkTask task:tasks)task.cancel();
            check(executor.getQueue().isEmpty(),"cancelled tasks immediately removed");release.countDown();executor.submit(()->{}).get(2,TimeUnit.SECONDS);check(ran.get()==0,"cancelled callbacks never run");
            NetworkTask cancelled=new NetworkTask();cancelled.cancel();Connection late=new Connection("https://example.invalid/");late.closeDelay=300;
            long at=System.nanoTime();check(!cancelled.bind(late),"late bind rejected");check(TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-at)<150,"slow disconnect cannot block caller");
            long deadline=System.nanoTime()+TimeUnit.SECONDS.toNanos(3);while(!late.disconnected&&System.nanoTime()<deadline)Thread.yield();check(late.disconnected,"late connection closed");
        } finally {release.countDown();executor.shutdownNow();}
    }
    static void datesAndRules() throws Exception {
        String[] fixtures={null,"","unknown","1792200000","1792200000000","2026/10/18 02:30","<b>2026年10月18日 02時30分</b> 視聴","2026-10-18T02:30:45.123+09:00","2026-10-18T02:30:45Z","2026-10-18T02:30:45-04:00","2026-02-30 12:00","2026/10/18 02:30:59","2026-10-18 02:30","2026年10月18日 02:30"};
        for(String s:fixtures)check(LegacyDates.millis(s)==RowDates194.millis(s),"date compatibility: "+s);
        for(int n=0;n<2000;n++){
            String s=String.format(Locale.ROOT,"2026/%02d/%02d %02d:%02d",n%12+1,n%28+1,n%24,n%60);
            check(LegacyDates.millis(s)==RowDates194.millis(s),"mixed date compatibility");
        }
        ExecutorService workers=Executors.newFixedThreadPool(4);try{List<Future<Boolean>> results=new ArrayList<>();for(int n=0;n<4;n++)results.add(workers.submit(()->{for(String s:fixtures)if(LegacyDates.millis(s)!=RowDates194.millis(s))return false;return true;}));for(Future<Boolean> result:results)check(result.get(),"date thread isolation");}finally{workers.shutdownNow();}
        ContentFilterRules.Rule exact=new ContentFilterRules.Rule("ＡＢＣ","exact",true),partial=new ContentFilterRules.Rule("実況","partial",true),regex=new ContentFilterRules.Rule("[ＡA]BC$","regex",true),disabled=new ContentFilterRules.Rule("abc","partial",false);
        check(exact.matches("Abc")&&!exact.matches("abc動画"),"NFKC exact unchanged");check(partial.matches("実況動画"),"partial unchanged");check(regex.matches("ABC")&&!regex.matches("abc"),"regex retains original case and text");check(!disabled.matches("abc"),"disabled unchanged");
    }
    public static void main(String[] args) throws Exception {pageCache();cancellation();datesAndRules();System.out.println("Audit regression assertions passed: "+checks);}
}

package e.e.a;

import java.net.HttpURLConnection;
import java.util.concurrent.*;

/** Cancellation belongs to a request, never to a mutable global connection. */
final class NetworkTask {
    private static final ExecutorService CLOSER = Executors.newSingleThreadExecutor(runnable -> { Thread thread = new Thread(runnable, "nicoid-network-close"); thread.setDaemon(true); return thread; });
    private volatile boolean cancelled;
    private HttpURLConnection connection;
    private Future<?> future;
    private ExecutorService executor;
    synchronized void start(ExecutorService executor, Runnable runnable) {
        if (cancelled) return;
        this.executor = executor;
        future = executor.submit(() -> { if (!cancelled) runnable.run(); });
    }
    synchronized boolean bind(HttpURLConnection value) {
        if (cancelled) { CLOSER.execute(value::disconnect); return false; }
        connection = value; return true;
    }
    synchronized void release(HttpURLConnection value) { if (connection == value) connection = null; }
    synchronized void cancel() {
        cancelled = true;
        if (future != null) {
            future.cancel(true);
            // Cancelling a FutureTask does not remove it from an executor queue.
            // Release captured views/data immediately instead of waiting for I/O ahead of it.
            if (executor instanceof ThreadPoolExecutor && future instanceof Runnable)
                ((ThreadPoolExecutor)executor).remove((Runnable)future);
            future = null;
        }
        executor = null;
        HttpURLConnection current = connection; connection = null;
        if (current != null) CLOSER.execute(current::disconnect);
    }
    boolean cancelled() { return cancelled || Thread.currentThread().isInterrupted(); }
}

package e.e.a;

import android.view.MotionEvent;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import android.view.View;
import android.widget.SeekBar;

public final class Media3SeekPreview implements SeekBar.OnSeekBarChangeListener,
                                                View.OnTouchListener, View.OnAttachStateChangeListener {
    private static final WeakHashMap<SeekBar, WeakReference<Media3SeekPreview>> INSTALLED = new WeakHashMap<>();
    private final View video;
    private final SeekBar.OnSeekBarChangeListener delegate;
    private Media3VideoPlayer active;
    private boolean cancelled;
    private boolean touchTracking;
    private long startPosition;

    private Media3SeekPreview(View video, SeekBar.OnSeekBarChangeListener delegate) {
        this.video = video;
        this.delegate = delegate;
    }

    public static void attach(SeekBar bar, SeekBar.OnSeekBarChangeListener delegate, View video) {
        if (video == null) {
            bar.setOnSeekBarChangeListener(delegate);
            return;
        }
        // This path is reinstalled when a new video is prepared.
        WeakReference<Media3SeekPreview> installed = INSTALLED.get(bar);
        Media3SeekPreview previous = installed == null ? null : installed.get();
        if (previous != null) {
            previous.endPreview(true);
            bar.removeOnAttachStateChangeListener(previous);
        }
        Media3SeekPreview preview = new Media3SeekPreview(video, delegate);
        INSTALLED.put(bar, new WeakReference<>(preview));
        bar.setOnSeekBarChangeListener(preview);
        bar.setOnTouchListener(preview);
        bar.addOnAttachStateChangeListener(preview);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            touchTracking = true;
            cancelled = false;
        } else if (event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
            cancelled = true;
            touchTracking = false;
        } else if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            touchTracking = false;
        }
        return false;
    }

    @Override
    public void onStartTrackingTouch(SeekBar bar) {
        delegate.onStartTrackingTouch(bar);
        if (!touchTracking) {
            return;
        }
        try {
            Media3VideoPlayer player = Dev139.player(video);
            long originalPosition = player.getCurrentPosition();
            if (player.startScrubbing()) {
                active = player;
                startPosition = originalPosition;
            }
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            // An alternate native video view still uses its original seek handler.
        }
    }

    @Override
    public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
        delegate.onProgressChanged(bar, progress, fromUser);
        if (fromUser && active != null && !cancelled) {
            active.previewPosition(progress);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar bar) {
        boolean restore = cancelled && active != null && active.isScrubbing();
        try {
            if (restore) {
                // Feed the original listener the restored value so its UI flags reset normally.
                int progress = (int)Math.min(startPosition, Integer.MAX_VALUE);
                bar.setProgress(progress);
                delegate.onProgressChanged(bar, progress, true);
            }
        } finally {
            endPreview(false);
            delegate.onStopTrackingTouch(bar);
        }
    }

    private void endPreview(boolean restorePosition) {
        Media3VideoPlayer player = active;
        active = null;
        if (player != null && player.isScrubbing()) {
            player.endScrubbing();
            if (restorePosition) {
                player.seekTo(startPosition);
            }
        }
    }

    @Override
    public void onViewAttachedToWindow(View view) {}

    @Override
    public void onViewDetachedFromWindow(View view) {
        touchTracking = false;
        cancelled = false;
        endPreview(true);
    }
}

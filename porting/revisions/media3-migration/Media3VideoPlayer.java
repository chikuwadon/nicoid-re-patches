package e.e.a;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.Surface;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.TrackGroup;
import androidx.media3.common.TrackSelectionOverride;
import androidx.media3.common.Tracks;
import androidx.media3.common.VideoSize;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.HttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import com.devbrackets.android.exomedia.core.ListenerMux;
import com.devbrackets.android.exomedia.core.video.ClearableSurface;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Media3VideoPlayer implements Player.Listener {
    private final Context context;
    private final ExoPlayer player;
    private final Handler main = new Handler(Looper.getMainLooper());
    private final boolean ownsSurface;
    private ListenerMux listeners;
    private volatile long sampledPosition;
    private volatile long sampledDuration;
    private volatile boolean sampledPlaying;
    private final Runnable sampleWhilePlaying = new Runnable() {
        @Override
        public void run() {
            refreshClock();
            if (sampledPlaying && player.isPlaying()) {
                main.postDelayed(this, 200);
            }
        }
    };
    private boolean scrubbing;
    private Surface surface;
    private boolean released;
    private boolean pendingSeek;
    private long firstFrameStartedAt = -1;
    private final Runnable updateBuffer = new Runnable() {
        @Override
        public void run() {
            if (released || player.getCurrentMediaItem() == null || listeners == null
                    || player.getPlaybackState() == Player.STATE_IDLE
                    || player.getPlaybackState() == Player.STATE_ENDED) {
                return;
            }
            listeners.onBufferingUpdate(player.getBufferedPercentage());
            main.postDelayed(this, 1000);
        }
    };

    public Media3VideoPlayer(Context context, boolean ownsSurface) {
        this.context = context.getApplicationContext();
        this.ownsSurface = ownsSurface;
        player = new ExoPlayer.Builder(this.context)
            .setLooper(Looper.getMainLooper())
            .setAudioAttributes(new AudioAttributes.Builder().setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE).build(), false)
            .build();
        player.addListener(this);
    }

    public void setListenerMux(ListenerMux listeners) {
        this.listeners = listeners;
    }

    public void setVideoUri(Uri uri) {
        if (released) {
            return;
        }
        pendingSeek = false;
        endScrubbing();
        sampledPosition = 0;
        sampledDuration = 0;
        firstFrameStartedAt = uri == null ? -1 : SystemClock.elapsedRealtime();
        resetListeners();
        if (uri == null) {
            player.stop();
            player.clearMediaItems();
            return;
        }
        VideoHttpSource.Credential credential = new VideoHttpSource.Credential(ModernPlayback.domandCookie);
        DefaultDataSource.Factory data = new DefaultDataSource.Factory(context,
            () -> new VideoHttpSource(credential));
        MediaItem media = MediaItem.fromUri(uri);
        player.setMediaSource(new DefaultMediaSourceFactory(data).createMediaSource(media));
        player.prepare();
    }

    public void start() {
        if (!released) {
            if (listeners != null) {
                listeners.setNotifiedCompleted(false);
            }
            player.play();
        }
    }

    public void pause() {
        if (!released) {
            player.pause();
        }
    }

    public boolean restart() {
        if (released || player.getCurrentMediaItem() == null) {
            return false;
        }
        int state = player.getPlaybackState();
        if (state != Player.STATE_IDLE && state != Player.STATE_ENDED) {
            return false;
        }
        resetListeners();
        firstFrameStartedAt = SystemClock.elapsedRealtime();
        player.seekTo(0);
        player.prepare();
        player.play();
        return true;
    }

    private void resetListeners() {
        if (listeners != null) {
            listeners.setNotifiedPrepared(false);
            listeners.setNotifiedCompleted(false);
        }
    }

    public void stop(ClearableSurface clearable, boolean clear) {
        if (!released) {
            pendingSeek = false;
            firstFrameStartedAt = -1;
            endScrubbing();
            player.pause();
            if (clear && listeners != null && player.getPlaybackState() != Player.STATE_IDLE) {
                listeners.clearSurfaceWhenReady(clearable);
            }
            player.stop();
        }
    }

    public void release() {
        if (!released) {
            released = true;
            main.removeCallbacks(updateBuffer);
            main.removeCallbacks(sampleWhilePlaying);
            sampledPosition = 0;
            sampledDuration = 0;
            sampledPlaying = false;
            player.removeListener(this);
            player.release();
            releaseSurface(surface);
            surface = null;
            listeners = null;
        }
    }

    public Surface getSurface() {
        return surface;
    }

    public void setSurface(Surface output) {
        if (released) {
            releaseSurface(output);
            return;
        }
        Surface previous = surface;
        if (previous != output) {
            player.setVideoSurface(output);
            surface = output;
            releaseSurface(previous);
        }
    }

    private void releaseSurface(Surface output) {
        if (ownsSurface && output != null) {
            output.release();
        }
    }

    boolean isScrubbing() {
        return scrubbing;
    }

    boolean startScrubbing() {
        if (released || !player.isCurrentMediaItemSeekable() || player.getPlaybackState() != Player.STATE_READY) {
            return false;
        }
        scrubbing = true;
        player.setScrubbingModeEnabled(true);
        return true;
    }

    void previewPosition(long position) {
        if (scrubbing && !released) {
            player.seekTo(Math.max(0, position));
        }
    }

    void endScrubbing() {
        if (scrubbing && !released) {
            scrubbing = false;
            player.setScrubbingModeEnabled(false);
        }
    }

    public void seekTo(long position) {
        if (!released && player.getCurrentMediaItem() != null) {
            pendingSeek = true;
            player.seekTo(Math.max(0, position));
            finishSeek();
        }
    }

    @Override
    public void onPositionDiscontinuity(Player.PositionInfo oldPosition,
                                        Player.PositionInfo newPosition, int reason) {
        if (reason == Player.DISCONTINUITY_REASON_SEEK) {
            main.post(this::finishSeek);
        }
    }

    private void finishSeek() {
        if (!released && pendingSeek && player.getPlaybackState() == Player.STATE_READY) {
            pendingSeek = false;
            if (listeners != null) {
                listeners.onSeekComplete();
            }
        }
    }

    // Original video and popup TimerTasks read the clock off the application looper.
    private void refreshClock() {
        boolean ready = !released && listeners != null && listeners.isPrepared();
        sampledPosition = ready ? player.getCurrentPosition() : 0;
        sampledDuration = ready ? Math.max(0, player.getDuration()) : 0;
        sampledPlaying = !released && player.getPlayWhenReady();
    }

    public long getCurrentPosition() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            refreshClock();
        }
        return sampledPosition;
    }

    public long getDuration() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            refreshClock();
        }
        return sampledDuration;
    }

    public int getBufferedPercent() {
        return released ? 0 : player.getBufferedPercentage();
    }

    public boolean isPlaying() {
        return Looper.myLooper() == Looper.getMainLooper()
            ? !released && player.getPlayWhenReady() : sampledPlaying;
    }

    public boolean setPlaybackSpeed(float speed) {
        if (released || !Float.isFinite(speed) || speed <= 0) {
            return false;
        }
        player.setPlaybackSpeed(speed);
        return true;
    }

    public float getPlaybackSpeed() {
        return released ? 1f : player.getPlaybackParameters().speed;
    }

    public boolean setVolume(float volume) {
        if (released) {
            return false;
        }
        player.setVolume(volume);
        return true;
    }

    public float getVolume() {
        return released ? 0f : player.getVolume();
    }

    public void setRepeatMode(int repeatMode) {
        if (!released) {
            player.setRepeatMode(repeatMode);
        }
    }

    private static int trackType(Enum<?> renderer) {
        switch (renderer.name()) {
            case "AUDIO": return C.TRACK_TYPE_AUDIO;
            case "VIDEO": return C.TRACK_TYPE_VIDEO;
            case "CLOSED_CAPTION": return C.TRACK_TYPE_TEXT;
            default: return C.TRACK_TYPE_METADATA;
        }
    }

    public void clearSelectedTracks(Enum<?> renderer) {
        if (!released) {
            player.setTrackSelectionParameters(player.getTrackSelectionParameters().buildUpon()
                .clearOverridesOfType(trackType(renderer)).build());
        }
    }

    public boolean isRendererEnabled(Enum<?> renderer) {
        return !released && !player.getTrackSelectionParameters().disabledTrackTypes.contains(trackType(renderer));
    }

    public void setRendererEnabled(Enum<?> renderer, boolean enabled) {
        if (!released) {
            player.setTrackSelectionParameters(player.getTrackSelectionParameters().buildUpon()
                .setTrackTypeDisabled(trackType(renderer), !enabled).build());
        }
    }

    private Tracks.Group group(Enum<?> renderer, int index) {
        if (released || index < 0) {
            return null;
        }
        int type = trackType(renderer);
        for (Tracks.Group group : player.getCurrentTracks().getGroups()) {
            if (group.getType() == type && index-- == 0) {
                return group;
            }
        }
        return null;
    }

    public Map<Enum<?>, List<TrackGroup>> getAvailableTracks(Enum<?>[] renderers) {
        Map<Enum<?>, List<TrackGroup>> available = new HashMap<>();
        if (!released) {
            for (Enum<?> renderer : renderers) {
                for (Tracks.Group group : player.getCurrentTracks().getGroups()) {
                    if (group.getType() == trackType(renderer)) {
                        available.computeIfAbsent(renderer, key -> new ArrayList<>())
                            .add(group.getMediaTrackGroup());
                    }
                }
            }
        }
        return available;
    }

    public int getSelectedTrackIndex(Enum<?> renderer, int index) {
        Tracks.Group group = group(renderer, index);
        if (group != null) {
            for (int track = 0; track < group.length; track++) {
                if (group.isTrackSelected(track)) {
                    return track;
                }
            }
        }
        return -1;
    }

    public void setTrack(Enum<?> renderer, int index, int track) {
        Tracks.Group group = group(renderer, index);
        if (group != null && track >= 0 && track < group.length) {
            player.setTrackSelectionParameters(player.getTrackSelectionParameters().buildUpon()
                .setOverrideForType(new TrackSelectionOverride(group.getMediaTrackGroup(), track)).build());
        }
    }

    @Override
    public void onEvents(Player source, Player.Events events) {
        if (released) {
            return;
        }
        if (events.containsAny(Player.EVENT_PLAYBACK_STATE_CHANGED, Player.EVENT_PLAY_WHEN_READY_CHANGED)) {
            if (listeners != null) {
                int state = source.getPlaybackState();
                if (state != Player.STATE_ENDED || source.getCurrentMediaItem() != null) {
                    listeners.onStateChanged(source.getPlayWhenReady(), state);
                }
            }
            main.removeCallbacks(updateBuffer);
            main.post(updateBuffer);
        }
        main.removeCallbacks(sampleWhilePlaying);
        sampleWhilePlaying.run();
        finishSeek();
    }

    @Override
    public void onVideoSizeChanged(VideoSize size) {
        if (!released && listeners != null && size.width > 0 && size.height > 0) {
            listeners.onVideoSizeChanged(size.width, size.height, 0, size.pixelWidthHeightRatio);
        }
    }

    @Override
    public void onRenderedFirstFrame() {
        if (firstFrameStartedAt >= 0) {
            long elapsed = SystemClock.elapsedRealtime() - firstFrameStartedAt;
            firstFrameStartedAt = -1;
            ModernDebug.record("Media3 first video frame: " + elapsed + " ms");
        }
    }

    @Override
    public void onPlayerError(PlaybackException error) {
        pendingSeek = false;
        endScrubbing();
        if (!released && listeners != null) {
            listeners.onError(null, error);
        }
    }

    public static String describeError(Exception error) {
        Throwable cause = error;
        for (int depth = 0; cause != null && depth < 8; depth++, cause = cause.getCause()) {
            if (cause instanceof HttpDataSource.InvalidResponseCodeException) {
                HttpDataSource.InvalidResponseCodeException response =
                    (HttpDataSource.InvalidResponseCodeException) cause;
                Uri uri = response.dataSpec.uri;
                // Loopback paths can contain a private cache access token.
                if ("127.0.0.1".equals(uri.getHost()) || "localhost".equals(uri.getHost())) {
                    return "HTTP " + response.responseCode + " @ local cache";
                }
                return "HTTP " + response.responseCode + " @ " + uri.getHost() + "/" + uri.getLastPathSegment()
                    + (!uri.isHierarchical() || uri.getQueryParameter("Signature") == null ? " [no signature]" : " [signed]");
            }
        }
        return (error.getCause() == null ? error : error.getCause()).getClass().getName();
    }
}

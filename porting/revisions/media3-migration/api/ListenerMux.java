package com.devbrackets.android.exomedia.core;

import com.devbrackets.android.exomedia.core.exoplayer.ExoMediaPlayer;
import com.devbrackets.android.exomedia.core.video.ClearableSurface;

public class ListenerMux {
    public native void setNotifiedPrepared(boolean prepared);
    public native void setNotifiedCompleted(boolean completed);
    public native boolean isPrepared();
    public native void onBufferingUpdate(int percentage);
    public native void onStateChanged(boolean playWhenReady, int state);
    public native void onVideoSizeChanged(int width, int height, int rotation, float ratio);
    public native void onSeekComplete();
    public native void onError(ExoMediaPlayer source, Exception error);
    public native void clearSurfaceWhenReady(ClearableSurface surface);
}

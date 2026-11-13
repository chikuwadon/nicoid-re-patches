.class public Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;
.super Ljava/lang/Object;

.field public clearableSurface:Lcom/devbrackets/android/exomedia/core/video/ClearableSurface;
.field public context:Landroid/content/Context;
.field public exoMediaPlayer:Lcom/devbrackets/android/exomedia/core/exoplayer/ExoMediaPlayer;
.field public internalListeners:Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate$InternalListeners;
.field public listenerMux:Lcom/devbrackets/android/exomedia/core/ListenerMux;
.field public playRequested:Z
.field public media3Player:Le/e/a/Media3VideoPlayer;

# The original constructor and setup() initialize the fields above and call initExoPlayer().

.method public initExoPlayer()V
    .locals 3
    new-instance v0, Le/e/a/Media3VideoPlayer;
    iget-object v1, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->context:Landroid/content/Context;
    iget-object v2, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->clearableSurface:Lcom/devbrackets/android/exomedia/core/video/ClearableSurface;
    instance-of v2, v2, Landroid/view/TextureView;
    invoke-direct {v0, v1, v2}, Le/e/a/Media3VideoPlayer;-><init>(Landroid/content/Context;Z)V
    iput-object v0, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->media3Player:Le/e/a/Media3VideoPlayer;
    return-void
.end method

.method public setListenerMux(Lcom/devbrackets/android/exomedia/core/ListenerMux;)V
    .locals 1
    iput-object p1, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->listenerMux:Lcom/devbrackets/android/exomedia/core/ListenerMux;
    iget-object v0, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->media3Player:Le/e/a/Media3VideoPlayer;
    invoke-virtual {v0, p1}, Le/e/a/Media3VideoPlayer;->setListenerMux(Lcom/devbrackets/android/exomedia/core/ListenerMux;)V
    return-void
.end method

.method public setVideoUri(Landroid/net/Uri;Le/d/b/a/m0/u;)V
    .locals 1
    iget-object v0, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->media3Player:Le/e/a/Media3VideoPlayer;
    invoke-virtual {v0, p1}, Le/e/a/Media3VideoPlayer;->setVideoUri(Landroid/net/Uri;)V
    return-void
.end method

.method public onSurfaceDestroyed()V
    .locals 2
    iget-object v0, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->media3Player:Le/e/a/Media3VideoPlayer;
    const/4 v1, 0x0
    invoke-virtual {v0, v1}, Le/e/a/Media3VideoPlayer;->setSurface(Landroid/view/Surface;)V
    return-void
.end method

.method public stopPlayback(Z)V
    .locals 2
    iget-object v0, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->media3Player:Le/e/a/Media3VideoPlayer;
    iget-object v1, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->clearableSurface:Lcom/devbrackets/android/exomedia/core/video/ClearableSurface;
    invoke-virtual {v0, v1, p1}, Le/e/a/Media3VideoPlayer;->stop(Lcom/devbrackets/android/exomedia/core/video/ClearableSurface;Z)V
    return-void
.end method

.method public setTrack(Lcom/devbrackets/android/exomedia/ExoMedia$RendererType;I)V
    .locals 2
    .annotation runtime Ljava/lang/Deprecated;
    .end annotation
    iget-object v0, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->media3Player:Le/e/a/Media3VideoPlayer;
    const/4 v1, 0x0
    invoke-virtual {v0, p1, v1, p2}, Le/e/a/Media3VideoPlayer;->setTrack(Ljava/lang/Enum;II)V
    return-void
.end method

.method public getAvailableTracks()Ljava/util/Map;
    .locals 2
    iget-object v0, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->media3Player:Le/e/a/Media3VideoPlayer;
    invoke-static {}, Lcom/devbrackets/android/exomedia/ExoMedia$RendererType;->values()[Lcom/devbrackets/android/exomedia/ExoMedia$RendererType;
    move-result-object v1
    invoke-virtual {v0, v1}, Le/e/a/Media3VideoPlayer;->getAvailableTracks([Ljava/lang/Enum;)Ljava/util/Map;
    move-result-object v0
    return-object v0
.end method

.method public getWindowInfo()Lcom/devbrackets/android/exomedia/core/exoplayer/WindowInfo;
    .locals 1
    const/4 v0, 0x0
    return-object v0
.end method

.method public setCaptionListener(Lcom/devbrackets/android/exomedia/core/listener/CaptionListener;)V
    .locals 0
    return-void
.end method

.method public setDrmCallback(Le/d/b/a/g0/t;)V
    .locals 0
    return-void
.end method

.method public onSurfaceReady(Landroid/view/Surface;)V
    .locals 1
    iget-object v0, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->media3Player:Le/e/a/Media3VideoPlayer;
    invoke-virtual {v0, p1}, Le/e/a/Media3VideoPlayer;->setSurface(Landroid/view/Surface;)V
    return-void
.end method

.method public release()V
    .locals 1
    iget-object v0, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->media3Player:Le/e/a/Media3VideoPlayer;
    invoke-virtual {v0}, Le/e/a/Media3VideoPlayer;->release()V
    return-void
.end method

.method public suspend()V
    .locals 1
    iget-object v0, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->media3Player:Le/e/a/Media3VideoPlayer;
    invoke-virtual {v0}, Le/e/a/Media3VideoPlayer;->release()V
    return-void
.end method

.method public start()V
    .locals 1
    iget-object v0, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->media3Player:Le/e/a/Media3VideoPlayer;
    invoke-virtual {v0}, Le/e/a/Media3VideoPlayer;->start()V
    return-void
.end method

.method public pause()V
    .locals 1
    iget-object v0, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->media3Player:Le/e/a/Media3VideoPlayer;
    invoke-virtual {v0}, Le/e/a/Media3VideoPlayer;->pause()V
    return-void
.end method

.method public restart()Z
    .locals 1
    iget-object v0, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->media3Player:Le/e/a/Media3VideoPlayer;
    invoke-virtual {v0}, Le/e/a/Media3VideoPlayer;->restart()Z
    move-result v0
    return v0
.end method

.method public seekTo(J)V
    .locals 1
    iget-object v0, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->media3Player:Le/e/a/Media3VideoPlayer;
    invoke-virtual {v0, p1, p2}, Le/e/a/Media3VideoPlayer;->seekTo(J)V
    return-void
.end method

.method public getCurrentPosition()J
    .locals 2
    iget-object v0, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->media3Player:Le/e/a/Media3VideoPlayer;
    invoke-virtual {v0}, Le/e/a/Media3VideoPlayer;->getCurrentPosition()J
    move-result-wide v0
    return-wide v0
.end method

.method public getDuration()J
    .locals 2
    iget-object v0, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->media3Player:Le/e/a/Media3VideoPlayer;
    invoke-virtual {v0}, Le/e/a/Media3VideoPlayer;->getDuration()J
    move-result-wide v0
    return-wide v0
.end method

.method public getBufferedPercent()I
    .locals 1
    iget-object v0, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->media3Player:Le/e/a/Media3VideoPlayer;
    invoke-virtual {v0}, Le/e/a/Media3VideoPlayer;->getBufferedPercent()I
    move-result v0
    return v0
.end method

.method public isPlaying()Z
    .locals 1
    iget-object v0, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->media3Player:Le/e/a/Media3VideoPlayer;
    invoke-virtual {v0}, Le/e/a/Media3VideoPlayer;->isPlaying()Z
    move-result v0
    return v0
.end method

.method public getPlaybackSpeed()F
    .locals 1
    iget-object v0, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->media3Player:Le/e/a/Media3VideoPlayer;
    invoke-virtual {v0}, Le/e/a/Media3VideoPlayer;->getPlaybackSpeed()F
    move-result v0
    return v0
.end method

.method public setPlaybackSpeed(F)Z
    .locals 1
    iget-object v0, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->media3Player:Le/e/a/Media3VideoPlayer;
    invoke-virtual {v0, p1}, Le/e/a/Media3VideoPlayer;->setPlaybackSpeed(F)Z
    move-result v0
    return v0
.end method

.method public getVolume()F
    .locals 1
    iget-object v0, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->media3Player:Le/e/a/Media3VideoPlayer;
    invoke-virtual {v0}, Le/e/a/Media3VideoPlayer;->getVolume()F
    move-result v0
    return v0
.end method

.method public setVolume(F)Z
    .locals 1
    iget-object v0, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->media3Player:Le/e/a/Media3VideoPlayer;
    invoke-virtual {v0, p1}, Le/e/a/Media3VideoPlayer;->setVolume(F)Z
    move-result v0
    return v0
.end method

.method public setRepeatMode(I)V
    .locals 1
    iget-object v0, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->media3Player:Le/e/a/Media3VideoPlayer;
    invoke-virtual {v0, p1}, Le/e/a/Media3VideoPlayer;->setRepeatMode(I)V
    return-void
.end method

.method public clearSelectedTracks(Lcom/devbrackets/android/exomedia/ExoMedia$RendererType;)V
    .locals 1
    iget-object v0, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->media3Player:Le/e/a/Media3VideoPlayer;
    invoke-virtual {v0, p1}, Le/e/a/Media3VideoPlayer;->clearSelectedTracks(Ljava/lang/Enum;)V
    return-void
.end method

.method public isRendererEnabled(Lcom/devbrackets/android/exomedia/ExoMedia$RendererType;)Z
    .locals 1
    iget-object v0, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->media3Player:Le/e/a/Media3VideoPlayer;
    invoke-virtual {v0, p1}, Le/e/a/Media3VideoPlayer;->isRendererEnabled(Ljava/lang/Enum;)Z
    move-result v0
    return v0
.end method

.method public setRendererEnabled(Lcom/devbrackets/android/exomedia/ExoMedia$RendererType;Z)V
    .locals 1
    iget-object v0, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->media3Player:Le/e/a/Media3VideoPlayer;
    invoke-virtual {v0, p1, p2}, Le/e/a/Media3VideoPlayer;->setRendererEnabled(Ljava/lang/Enum;Z)V
    return-void
.end method

.method public getSelectedTrackIndex(Lcom/devbrackets/android/exomedia/ExoMedia$RendererType;I)I
    .locals 1
    iget-object v0, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->media3Player:Le/e/a/Media3VideoPlayer;
    invoke-virtual {v0, p1, p2}, Le/e/a/Media3VideoPlayer;->getSelectedTrackIndex(Ljava/lang/Enum;I)I
    move-result v0
    return v0
.end method

.method public setTrack(Lcom/devbrackets/android/exomedia/ExoMedia$RendererType;II)V
    .locals 1
    iget-object v0, p0, Lcom/devbrackets/android/exomedia/core/video/exo/ExoVideoDelegate;->media3Player:Le/e/a/Media3VideoPlayer;
    invoke-virtual {v0, p1, p2, p3}, Le/e/a/Media3VideoPlayer;->setTrack(Ljava/lang/Enum;II)V
    return-void
.end method

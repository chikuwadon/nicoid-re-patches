(function (message) {
  'use strict';
  if (!message || message.type !== 0 || !/\.m3u8(?:[?#]|$)/i.test(message.loadurl || '')) return message;
  var w = window, state = w.__nicoidReCast216;
  if (state) return message;
  if (!w.video || typeof w.loadcomp !== 'function' || typeof w.allreset !== 'function') return message;
  state = w.__nicoidReCast216 = { generation: 0, player: null, ready: null, active: false };
  var legacyLoad = w.loadcomp, legacyReset = w.allreset;
  function report(phase, code) {
    try {
      if (w.messageBus && w.senderid) w.messageBus.send(w.senderid, JSON.stringify({
        type: 1, videoid: w.videoid || '', mpisplay: !w.video.paused,
        iscomment: !!w.iscomment, mpposition: Math.round((w.video.currentTime || 0) * 1000),
        playerState: phase, errorCode: typeof code === 'number' && isFinite(code) ? code : 0
      }));
    } catch (ignored) {}
  }
  function stop() {
    ++state.generation;
    var player = state.player; state.player = null; state.active = false;
    if (player) try { player.unload(); } catch (ignored) {}
  }
  function fail(code) {
    report('COMPAT_ERROR', code);
    try { w.loopswitch(false); w.loading.style.display = 'none'; w.videomessage.style.display = 'block'; w.setmessage('映像を再生できませんでした（Cast: ' + code + '）'); } catch (ignored) {}
  }
  function api() { return w.cast && w.cast.player && w.cast.player.api; }
  function sdk() {
    if (api() && api().Player && api().CreateHlsStreamingProtocol) return Promise.resolve(api());
    if (state.ready) return state.ready;
    state.ready = new Promise(function (resolve, reject) {
      var script = document.createElement('script'), finished = false;
      function finish(code) {
        if (finished) return; finished = true; clearTimeout(timer);
        script.onload = script.onerror = null;
        if (code) { state.ready = null; if (script.parentNode) script.parentNode.removeChild(script); reject(code); }
        else resolve(api());
      }
      var timer = setTimeout(function () { finish(9101); }, 15000);
      script.src = 'https://www.gstatic.com/cast/sdk/libs/mediaplayer/1.0.0/media_player.js';
      script.async = true;
      script.onload = function () { finish(api() && api().Player && api().CreateHlsStreamingProtocol ? 0 : 9103); };
      script.onerror = function () { finish(9102); };
      document.head.appendChild(script);
    });
    return state.ready;
  }
  w.allreset = function () { stop(); return legacyReset.apply(this, arguments); };
  w.loadcomp = function (text) {
    if (!/\.m3u8(?:[?#]|$)/i.test(w.videosrcurl || '')) { stop(); return legacyLoad.apply(this, arguments); }
    stop(); state.active = true;
    var token = state.generation, url = w.videosrcurl;
    try { var list = JSON.parse(text || '[]'); w.commentlist = Array.isArray(list) ? list : []; }
    catch (ignored) { w.commentlist = []; report('COMPAT_COMMENT_FORMAT', 9104); }
    report('COMPAT_SDK_LOADING', 0);
    sdk().then(function (playerApi) {
      if (token !== state.generation || url !== w.videosrcurl) return;
      var host = new playerApi.Host({ mediaElement: w.video, url: url });
      host.onError = function (code) { if (token === state.generation) fail(typeof code === 'number' ? code : 9199); };
      var protocol = playerApi.CreateHlsStreamingProtocol(host, playerApi.HlsSegmentFormat.FMP4, playerApi.HlsVideoSegmentFormat.FMP4);
      var player = state.player = new playerApi.Player(host);
      w.video.autoplay = true;
      player.load(protocol, 0);
      if (w.iscomhide) { w.iscomment = false; w.iscomctrl = true; w.loopswitch(false); w.textPaint.clearRect(0, 0, w.vw, w.vh); }
      else { w.iscomctrl = false; w.iscomment = true; w.loopswitch(true); }
      report('COMPAT_MANIFEST_LOADING', 0);
    }).catch(function (code) { if (token === state.generation) fail(typeof code === 'number' ? code : 9199); });
  };
  w.video.addEventListener('playing', function () { if (state.active && state.player) report('COMPAT_PLAYING', 0); });
  w.video.addEventListener('error', function () { if (state.active && w.video.error) report('COMPAT_ERROR', 9200 + w.video.error.code); });
  w.addEventListener('unload', stop);
  return message;
})

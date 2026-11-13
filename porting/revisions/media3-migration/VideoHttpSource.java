package e.e.a;

import android.net.Uri;
import androidx.media3.common.C;
import androidx.media3.common.PlaybackException;
import androidx.media3.datasource.BaseDataSource;
import androidx.media3.datasource.DataSourceException;
import androidx.media3.datasource.DataSpec;
import androidx.media3.datasource.HttpDataSource;
import androidx.media3.datasource.HttpUtil;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

final class VideoHttpSource extends BaseDataSource {
    private static final String SITE = "https://www.nicovideo.jp";
    private static final String DOMAND_USER_AGENT =
        "Mozilla/5.0 (Linux; Android) AppleWebKit/537.36 Chrome/120.0 Mobile Safari/537.36";
    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .followSslRedirects(false)
        .addNetworkInterceptor(chain -> {
            Request request = chain.request();
            Request.Builder headers = request.newBuilder().removeHeader("Cookie");
            Credential credential = request.tag(Credential.class);
            HttpUrl destination = request.url();
            // Check every network hop, including redirects.
            if (credential != null && destination.isHttps() && destination.port() == 443
                    && destination.host().endsWith(".domand.nicovideo.jp")) {
                if (credential.cookie != null && !credential.cookie.isEmpty()) {
                    headers.header("Cookie", credential.cookie);
                }
                if ("delivery.domand.nicovideo.jp".equals(destination.host())) {
                    headers.header("User-Agent", DOMAND_USER_AGENT);
                }
            }
            return chain.proceed(headers.build());
        })
        .build();

    static final class Credential {
        final String cookie;

        Credential(String cookie) {
            this.cookie = cookie;
        }
    }

    private final Credential credential;
    private Response response;
    private InputStream stream;
    private long remaining;
    private boolean started;

    VideoHttpSource(Credential credential) {
        super(true);
        this.credential = credential;
    }

    @Override
    public long open(DataSpec spec) throws IOException {
        transferInitializing(spec);
        HttpUrl url = HttpUrl.parse(spec.uri.toString());
        if (url == null) {
            throw new IOException("Unsupported video URL");
        }
        Request.Builder request = new Request.Builder().url(url)
            .tag(Credential.class, credential);
        for (Map.Entry<String, String> header : spec.httpRequestHeaders.entrySet()) {
            request.header(header.getKey(), header.getValue());
        }
        request.header("Referer", SITE + "/").header("Origin", SITE);
        if (!spec.isFlagSet(DataSpec.FLAG_ALLOW_GZIP)) {
            request.header("Accept-Encoding", "identity");
        }
        String range = HttpUtil.buildRangeRequestHeader(spec.position, spec.length);
        if (range != null) {
            request.header("Range", range);
        }
        RequestBody body = spec.httpBody != null || spec.httpMethod == DataSpec.HTTP_METHOD_POST
            ? RequestBody.create(null, spec.httpBody == null ? new byte[0] : spec.httpBody) : null;
        request.method(spec.getHttpMethodString(), body);

        try {
            response = CLIENT.newCall(request.build()).execute();
            int code = response.code();
            if (code == 416 && spec.position == HttpUtil.getDocumentSize(response.header("Content-Range"))) {
                remaining = 0;
                beginTransfer(spec);
                return spec.length == C.LENGTH_UNSET ? 0 : spec.length;
            }
            if (!response.isSuccessful()) {
                IOException cause = code == 416
                    ? new DataSourceException(PlaybackException.ERROR_CODE_IO_READ_POSITION_OUT_OF_RANGE)
                    : null;
                throw new HttpDataSource.InvalidResponseCodeException(code, response.message(), cause,
                    response.headers().toMultimap(), spec, new byte[0]);
            }
            ResponseBody content = response.body();
            if (content == null) {
                throw new EOFException("Empty HTTP response");
            }
            stream = content.byteStream();
            long skip = code == 200 ? spec.position : 0;
            long contentLength = HttpUtil.getContentLength(response.header("Content-Length"),
                response.header("Content-Range"));
            remaining = spec.length != C.LENGTH_UNSET ? spec.length
                : contentLength == C.LENGTH_UNSET ? C.LENGTH_UNSET : contentLength - skip;
            if (remaining < 0 && contentLength != C.LENGTH_UNSET) {
                throw new DataSourceException(PlaybackException.ERROR_CODE_IO_READ_POSITION_OUT_OF_RANGE);
            }
            beginTransfer(spec);
            if (skip > 0) {
                byte[] discarded = new byte[8192];
                while (skip > 0) {
                    int count = stream.read(discarded, 0, (int) Math.min(skip, discarded.length));
                    if (count == -1) {
                        throw new DataSourceException(PlaybackException.ERROR_CODE_IO_READ_POSITION_OUT_OF_RANGE);
                    }
                    skip -= count;
                    bytesTransferred(count);
                }
            }
            return remaining;
        } catch (IOException | RuntimeException failure) {
            close();
            throw failure;
        }
    }

    private void beginTransfer(DataSpec spec) {
        started = true;
        transferStarted(spec);
    }

    @Override
    public int read(byte[] buffer, int offset, int length) throws IOException {
        if (length == 0) {
            return 0;
        }
        if (remaining == 0) {
            return C.RESULT_END_OF_INPUT;
        }
        int count = stream.read(buffer, offset,
            remaining == C.LENGTH_UNSET ? length : (int) Math.min(remaining, length));
        if (count == C.RESULT_END_OF_INPUT && remaining > 0) {
            throw new EOFException("Truncated HTTP response");
        }
        if (count > 0) {
            if (remaining != C.LENGTH_UNSET) {
                remaining -= count;
            }
            bytesTransferred(count);
        }
        return count;
    }

    @Override
    public Uri getUri() {
        return response == null ? null : Uri.parse(response.request().url().toString());
    }

    @Override
    public Map<String, List<String>> getResponseHeaders() {
        return response == null ? Collections.emptyMap() : response.headers().toMultimap();
    }

    @Override
    public void close() {
        Response previous = response;
        response = null;
        stream = null;
        try {
            if (previous != null) {
                previous.close();
            }
        } finally {
            if (started) {
                started = false;
                transferEnded();
            }
        }
    }
}

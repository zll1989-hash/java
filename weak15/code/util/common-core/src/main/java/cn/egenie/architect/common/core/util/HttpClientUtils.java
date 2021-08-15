package cn.egenie.architect.common.core.util;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import cn.egenie.architect.common.function.util.ThrowUtils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 基于OKHttp
 *
 * @author lucien
 * @since 2021/01/15
 */
@Slf4j
public class HttpClientUtils {

    public static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private static final String JSON_CONTENT_TYPE = "application/json";

    /**
     * io 密集型，线程数稍微多点
     */
    private static final ThreadPoolExecutor HTTP_EXECUTOR = new ThreadPoolExecutor(
            16,
            128,
            1,
            TimeUnit.MINUTES,
            new ArrayBlockingQueue<>(200),
            new ThreadFactory() {
                private AtomicInteger atomicInteger = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "thread-http-" + atomicInteger.getAndIncrement());
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy());

    private static final Dispatcher DISPATCHER = new Dispatcher(HTTP_EXECUTOR);

    static {
        DISPATCHER.setMaxRequests(200);
        DISPATCHER.setMaxRequestsPerHost(10);
    }

    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder()
            .dispatcher(DISPATCHER)
            .connectTimeout(30L, TimeUnit.SECONDS)
            .readTimeout(60L, TimeUnit.SECONDS)
            .writeTimeout(30L, TimeUnit.SECONDS)
            .connectionPool(new ConnectionPool(200, 5, TimeUnit.MINUTES))
            .build();

    public static String get(String url) {
        return get(url, Collections.emptyMap());
    }

    public static String get(String url, Map<String, Object> params) {
        url = UrlUtils.buildUrl(url, params);
        Request request = new Request.Builder()
                .addHeader("Content-Type", JSON_CONTENT_TYPE)
                .url(url)
                .build();

        return execute(request, url, null);
    }

    public static String get(String url, Map<String, String> headerMap, Map<String, Object> paramMap) {
        headerMap = headerMap == null ? Collections.emptyMap() : headerMap;
        url = UrlUtils.buildUrl(url, paramMap);

        Request request = new Request.Builder()
                .headers(Headers.of(headerMap))
                .url(url)
                .build();

        return execute(request, url, null);
    }

    public static String post(String url, String bodyJson) {
        RequestBody requestBody = RequestBody.create(bodyJson, JSON_MEDIA_TYPE);

        Request request = new Request.Builder()
                .addHeader("Content-Type", JSON_CONTENT_TYPE)
                .url(url)
                .post(requestBody)
                .build();

        return execute(request, url, bodyJson);
    }

    public static String post(String url, Map<String, String> headerMap, String bodyJson) {
        RequestBody requestBody = RequestBody.create(bodyJson, JSON_MEDIA_TYPE);
        headerMap = headerMap == null ? Collections.emptyMap() : headerMap;

        Request request = new Request.Builder()
                .headers(Headers.of(headerMap))
                .url(url)
                .post(requestBody)
                .build();

        return execute(request, url, bodyJson);
    }

    private static String execute(Request request, String url, String requestBodyJson) {
        Call call = OK_HTTP_CLIENT.newCall(request);
        Response response = ThrowUtils.submit(call::execute);

        if (response.isSuccessful()) {
            ResponseBody body = response.body();
            if (body != null) {
                return ThrowUtils.submit(body::string);
            }
            throw new RuntimeException(Strings.of("Http fail, url={} body={}", url, requestBodyJson));
        }

        throw new RuntimeException(Strings.of("Http fail, url={} body={}", url, requestBodyJson));
    }
}

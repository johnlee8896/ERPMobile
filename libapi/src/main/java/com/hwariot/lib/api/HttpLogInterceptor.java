package com.hwariot.lib.api;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;

/***
 *@date 创建时间 2019-05-07 16:00
 *@author 作者: W.YuLong
 *@description  请求日志的打印
 */
public final class HttpLogInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");


    @Override
    public Response intercept(Chain chain) throws IOException {
        ArrayList<String> logList = new ArrayList<>();

        Request request = chain.request();

        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        String requestStartMessage = String.format("【%s】 【%s】", request.method(), request.url());
        if (hasRequestBody) {
            requestStartMessage += String.format(" ( %d -byte body", requestBody.contentLength());
        }
        logList.add(requestStartMessage);

        if (hasRequestBody) {
            StringBuilder sb = new StringBuilder();
            if (requestBody.contentType() != null) {
                sb.append("【Content-Type】：" + requestBody.contentType());
            }
            if (requestBody.contentLength() != -1) {
                sb.append(" 【Content-Length】：" + requestBody.contentLength());
            }
            logList.add(sb.toString());
        }

        Headers headers = request.headers();

        initHeaders(logList, headers);

        if (hasRequestBody && !bodyEncoded(request.headers())) {
            if (requestBody instanceof MultipartBody) {
                MultipartBody tmpBody = (MultipartBody) requestBody;
                StringBuilder sb = new StringBuilder();
                for (MultipartBody.Part part : tmpBody.parts()) {

                    int requestCount = part.headers().size();
                    if (requestCount > 0) {
                        for (int i = 0; i < requestCount; i++) {
                            String name = part.headers().name(i);
                            sb.append(String.format("【%s = %s】", name, part.headers().value(i)));
                            sb.append("\n");
                        }
                    }

                    if (part.body().contentType() != null && (part.body().contentType().type().equalsIgnoreCase("video") ||
                            part.body().contentType().type().equalsIgnoreCase("image") || part.body().contentType().type().equalsIgnoreCase("file"))) {
                        sb.append("*******************************************************************\n");
                        sb.append("***************这里是文件的数据，省略输出**************************\n");
                        sb.append("*******************************************************************\n\n");
                    } else {
                        sb.append(String.format("【body = %s】", formatBodyToString(part.body())));
                    }
                    sb.append("\n\n");
                }
                logList.add("【Request Body】:" + sb.toString());
            } else {
                logList.add("【Request Body】:" + formatBodyToString(requestBody));
            }
        }
        logList.add(String.format("***************** END 【%s】Request ************************************************\n\n", request.method()));
        logList.add(" ");

        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            logList.add("<-- HTTP FAILED: " + e);
            e.printStackTrace();
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();

        logList.add(String.format("【code = %d】,【url = %s】,【%dms】",
                response.code(), response.request().url(), tookMs));

        Headers responseHeaders = response.headers();
        int responseHeaderSize = responseHeaders.size();
        if (responseHeaderSize > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("ResponseHeaders: ");
            for (int i = 0; i < responseHeaderSize; i++) {
                logList.add(String.format("【%s = %s】,", responseHeaders.name(i), responseHeaders.value(i)));
            }
        }

        if (!HttpHeaders.hasBody(response)) {
            logList.add("<-- END HTTP Without Body");
        } else if (bodyEncoded(response.headers())) {
            logList.add("<-- END HTTP (encoded body omitted)");
        } else {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                try {
                    charset = contentType.charset(UTF8);
                } catch (UnsupportedCharsetException e) {
                    logList.add("Couldn't decode the response body; charset is likely malformed.");
                    logList.add("<-- END HTTP");
                    return response;
                }
            }

            if (!isPlaintext(buffer)) {
                logList.add("<-- END HTTP (binary " + buffer.size() + "-byte body omitted)");
                return response;
            }
            if (contentLength != 0) {
                logList.add("↓↓↓↓↓↓【Response Data】↓↓↓↓↓");
                logList.add(buffer.clone().readString(charset));
            }
            logList.add("<-- END HTTP (" + buffer.size() + " -byte body)");
        }

        APILog.httpLog(logList);
        return response;
    }

    private String formatBodyToString(RequestBody requestBody) {
        String result = "";
        try {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }

            if (isPlaintext(buffer)) {
                result = buffer.readString(charset);
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = "解析错误";
        }

        return result;
    }

    private void initHeaders(ArrayList<String> logList, Headers headers) {
        int requestCount = headers.size();
        if (requestCount > 0) {
            for (int i = 0; i < requestCount; i++) {
                String name = headers.name(i);
                logList.add(String.format("【%s = %s】", name, headers.value(i)));
            }
        }
    }


    private String list2String(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0, len = list.size(); i < len; i++) {
            sb.append("\n");
            sb.append(String.format("[%d]、", i + 1)).append(list.get(i));
        }
        return sb.toString();
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }
}

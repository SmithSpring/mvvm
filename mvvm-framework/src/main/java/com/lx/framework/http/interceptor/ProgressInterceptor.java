package com.lx.framework.http.interceptor;

import java.io.IOException;

import com.lx.framework.http.download.ProgressResponseBody;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by lx on 2017/5/10.
 */

public class ProgressInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder()
                .body(new ProgressResponseBody(originalResponse.body()))
                .build();
    }
}

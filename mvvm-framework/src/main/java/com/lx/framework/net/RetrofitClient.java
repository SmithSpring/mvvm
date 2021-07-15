package com.lx.framework.net;

import com.lx.framework.http.cookie.CookieJarImpl;
import com.lx.framework.http.cookie.store.PersistentCookieStore;
import com.lx.framework.http.interceptor.CacheInterceptor;
import com.lx.framework.utils.Configure;
import com.lx.framework.utils.KLog;
import com.lx.framework.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;

import static java.nio.charset.StandardCharsets.UTF_8;


public class RetrofitClient {
    private static final int DEFAULT_TIMEOUT = 20;
//    private static final int CACHE_TIMEOUT = 10 * 1024 * 1024;

    private static Retrofit retrofit;

    private File httpCacheDirectory;

    private static class SingletonHolder {
        private static final RetrofitClient INSTANCE = new RetrofitClient();
    }
    
    public static RetrofitClient getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private RetrofitClient() {
        this(null);
    }

    private RetrofitClient(Map<String, String> headers) {

        if (httpCacheDirectory == null) {
            httpCacheDirectory = new File(Utils.getContext().getCacheDir(), "lx_cache");
        }


      /*  try {
            Cache cache = null;
            if (cache == null) {
                cache = new Cache(httpCacheDirectory, CACHE_TIMEOUT);
            }
        } catch (Exception e) {
        }*/
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
        //                .cache(cache)
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(new CookieJarImpl(new PersistentCookieStore(Utils.getContext())))
//                .cache(cache)
                .addInterceptor(new MyInterceptor(headers))
                .addInterceptor(new CacheInterceptor(Utils.getContext()))
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .addInterceptor(new Interceptor() {
                    @NotNull
                    @Override
                    public Response intercept(@NotNull Chain chain) throws IOException {
                        Request request = chain.request();

                        long startTime = System.currentTimeMillis();
                        Response response = chain.proceed(chain.request());
                        long endTime = System.currentTimeMillis();
                        long duration = endTime - startTime;
                        MediaType mediaType = response.body().contentType();
                        String content = response.body().string();

                        KLog.e("Interceptor", "请求地址：| " + request.toString());
                        if (!(request.body() instanceof MultipartBody)) {
                            if (request.body() != null) {
                                printParams(request.body());
                            }
                        }
                        KLog.e("Interceptor", "请求体返回：| Response:" + content);
                        KLog.e("Interceptor", "----------请求耗时:" + duration + "毫秒----------");
                        return response.newBuilder().body(okhttp3.ResponseBody.create(mediaType, content)).build();
                    }
                }).addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(8, 15, TimeUnit.SECONDS))
                .proxy(Proxy.NO_PROXY)
                .build();

        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
//                .addConverterFactory(GsonConverterFactory.create())      //默认的解析
                .addConverterFactory(GsonDConverterFactory.create())      //自定义解析
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .baseUrl(Configure.getUrl())
                .build();

    }


    public <T> T create(final Class<T> service) {
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }
        return retrofit.create(service);
    }


    public static <T> T execute(Observable<T> observable, Observer<T> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

        return null;
    }

    private void printParams(RequestBody body) {
        Buffer buffer = new Buffer();
        try {
            body.writeTo(buffer);
            Charset charset = UTF_8;
            MediaType contentType = body.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF_8);
            }
            String params = buffer.readString(charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

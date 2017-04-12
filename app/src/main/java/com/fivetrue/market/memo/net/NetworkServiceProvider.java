package com.fivetrue.market.memo.net;

import android.content.Context;
import android.util.Log;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.net.service.ImageService;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.File;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by kwonojin on 2017. 2. 21..
 */

public class NetworkServiceProvider {

    private static final String TAG = "NetworkServiceProvider";

    private static final int CACHE_SIZE = 20 * 1024 * 1024; // 20 MiB

    private static final CookieManager COOKIE_MANAGER;

    static {
        COOKIE_MANAGER = new CookieManager();
        COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
    }

    private static final String API_MS_SEARCH_IMAGE_HOST = "https://api.cognitive.microsoft.com";

    private Context mContext;
    private Cache mCache;
    private OkHttpClient mForceCacheHttpClient;

    private static NetworkServiceProvider sInstance;


    public static void init(Context context){
        sInstance = new NetworkServiceProvider(context.getApplicationContext());
    }

    public static NetworkServiceProvider getInstance(){
        return sInstance;
    }

    private NetworkServiceProvider(Context context){
        mContext = context;
        mCache = new Cache(new File(mContext.getCacheDir(), "http"), CACHE_SIZE);
        initHttpClient();
    }


    public ImageService getImageService(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_MS_SEARCH_IMAGE_HOST)
                .client(mForceCacheHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(ImageService.class);
    }

    private void initHttpClient(){
        initForceCacheHttpClient();
    }

    private void initForceCacheHttpClient(){
        mForceCacheHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new LongTimeCacheControlInterceptor())
                .connectTimeout(1, TimeUnit.MINUTES)
                .cache(mCache)
                .retryOnConnectionFailure(true)
                .build();
    }

    private static class LongTimeCacheControlInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            if(LL.D) Log.d(TAG, "intercept() called with: chain = [" + chain + "]");
            Request request = chain.request();
            if(LL.D) Log.d(TAG, "intercept: request url" + request.url());
            if(LL.D) Log.d(TAG, "intercept: request body " + request.body());
            if(LL.D) Log.d(TAG, "intercept: request header " + request.headers());
            if(LL.D) Log.d(TAG, "intercept: request cacheControl " + request.cacheControl());
            request = request.newBuilder()
                    .cacheControl(new CacheControl.Builder()
                            .maxAge(365, TimeUnit.DAYS)
                            .minFresh(30, TimeUnit.DAYS)
                            .maxStale(30, TimeUnit.DAYS)
                            .build())
                    .url(request.url())
                    .build();

            Response response = chain.proceed(request);
            if(LL.D) Log.d(TAG, "intercept: response isSuccessful " + response.isSuccessful());
            if(LL.D) Log.d(TAG, "intercept: response body " + response.body());
            if(LL.D) Log.d(TAG, "intercept: response headers " + response.headers());
            if(LL.D) Log.d(TAG, "intercept: response cacheControl " + response.cacheControl());
            return response;
        }
    }
}

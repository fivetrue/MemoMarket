package com.fivetrue.market.memo.net;

import android.content.Context;

import com.fivetrue.market.memo.net.service.ImageService;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.net.CookieManager;
import java.net.CookiePolicy;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by kwonojin on 2017. 2. 21..
 */

public class NetworkServiceProvider {

    private static final String TAG = "NetworkServiceProvider";

    private static final CookieManager COOKIE_MANAGER;

    static {
        COOKIE_MANAGER = new CookieManager();
        COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
    }


    private static final String API_MS_SEARCH_IMAGE_HOST = "https://api.cognitive.microsoft.com";

    private OkHttpClient mHttpClient;

//        mHttpClient = configureClient(new OkHttpClient().newBuilder()) //인증서 무시 .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS) //연결 타임아웃 시간 설정 .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS) //쓰기 타임아웃 시간 설정 .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS) //읽기 타임아웃 시간 설정 .cookieJar(new JavaNetCookieJar(cookieManager)) //쿠키메니져 설정 .addInterceptor(httpLoggingInterceptor) //http 로그 확인 .build();

    public static ImageService getImageService(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_MS_SEARCH_IMAGE_HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(ImageService.class);
    }

}

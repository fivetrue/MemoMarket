package com.fivetrue.market.memo.net.service;

import com.fivetrue.market.memo.model.image.ImageEntry;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by kwonojin on 2017. 2. 21..
 */


public interface ImageService {

    @GET("bing/v5.0/images/search")
    Observable<ImageEntry> getImageList(@Header("Ocp-Apim-Subscription-Key") String apiKey,
                                        @Query("q") String keyword, @Query("mkt") String market);
}
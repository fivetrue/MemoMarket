package com.fivetrue.market.memo.net.service;

import com.fivetrue.market.memo.model.image.GoogleImage;
import com.fivetrue.market.memo.model.image.ImageEntry;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by kwonojin on 2017. 2. 21..
 */


public interface GoogleImageService {

    @GET("search")
    Observable<GoogleImage> getGoogleImage(@QueryMap Map<String, String> query);

}
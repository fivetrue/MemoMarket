package com.fivetrue.market.memo.net.service;

import com.fivetrue.market.memo.model.dto.GeoLocation;
import com.fivetrue.market.memo.model.image.ImageEntry;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by kwonojin on 2017. 2. 21..
 */


public interface GoogleApiService {

    @POST("/geolocation/v1/geolocate")
    Observable<GeoLocation> getGeoLocation(@Query("key") String apiKey);
}
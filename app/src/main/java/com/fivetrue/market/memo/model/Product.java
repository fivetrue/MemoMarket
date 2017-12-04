package com.fivetrue.market.memo.model;

/**
 * Created by kwonojin on 2017. 11. 16..
 */

public interface Product {

    long getId();

    String getName();

    long getPrice();

    String getStoreName();

    String getImageUrl();

    long getCheckInDate();

    void setCheckInDate(long millis);

    long getCheckOutDate();

    void setCheckOutDate(long millis);

}

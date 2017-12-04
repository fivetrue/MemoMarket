package com.fivetrue.market.memo.model.remote;


import com.fivetrue.market.memo.model.Product;

/**
 * Created by kwonojin on 2017. 1. 29..
 */

public class ProductData extends FirebaseData implements Product{

    private String name;
    private String barcode;
    private long price;
    private String storeName;

    public ProductData(){

    }

    public ProductData(Product store){
        name = store.getName();
        price = store.getPrice();
        storeName = store.getStoreName();
    }

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getPrice() {
        return price;
    }

    @Override
    public String getStoreName() {
        return storeName;
    }

    @Override
    public String getImageUrl() {
        return null;
    }

    @Override
    public long getCheckInDate() {
        return 0;
    }

    @Override
    public void setCheckInDate(long millis) {

    }

    @Override
    public long getCheckOutDate() {
        return 0;
    }

    @Override
    public void setCheckOutDate(long millis) {

    }
}

package com.fivetrue.market.memo.model.dto;

import com.fivetrue.market.memo.model.vo.Product;

/**
 * Created by kwonojin on 2017. 1. 29..
 */

public class ProductData extends FirebaseData{

    public String name;
    private String skuId;
    private long price;
    private String storeName;

    public ProductData(){

    }

    public ProductData(Product store){
        name = store.getName();
        skuId = store.getBarcode();
        price = store.getPrice();
        storeName = store.getStoreName();
    }
}

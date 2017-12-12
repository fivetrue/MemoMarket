package com.fivetrue.market.memo.share.kakao;

import android.content.Context;

import com.fivetrue.market.memo.model.Product;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

/**
 * Created by kwonojin on 2017. 12. 12..
 */

public class ProductKakaoLinkItem implements KakaoLinkItem {

    private String title;
    private String description;
    private String imageUrl;
    private String parameters;
    private List<Product> products;

    public ProductKakaoLinkItem(String message, List<Product> products){
        if(products == null || products.isEmpty()){
            throw new IllegalArgumentException("PRODUCT ITEM MUST BE EXISTS");
        }
        this.title = message;
        for(Product p : products){
            if(description == null){
                description = "";
            }
            description += p.getName() + ",";
        }
        description = description.substring(0, title.length() - 1);
        this.products = products;
        this.imageUrl = products.get(0).getImageUrl();
        assignParameter();
    }

    protected void assignParameter(){
        this.parameters = "products=" + new Gson().toJson(Observable.fromIterable(products).map(product -> product.getName()).toList().blockingGet());
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String getParameters() {
        return parameters;
    }

}

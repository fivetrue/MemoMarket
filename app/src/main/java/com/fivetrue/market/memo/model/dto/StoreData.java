package com.fivetrue.market.memo.model.dto;

import com.fivetrue.market.memo.model.vo.Store;

/**
 * Created by kwonojin on 2017. 1. 29..
 */

public class StoreData extends FirebaseData{

    public String name;

    public StoreData(){

    }

    public StoreData(String name){
        this.name = name;
    }
}

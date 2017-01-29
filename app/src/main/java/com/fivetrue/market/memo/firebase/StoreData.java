package com.fivetrue.market.memo.firebase;

import com.fivetrue.market.memo.model.Store;

/**
 * Created by kwonojin on 2017. 1. 29..
 */

public class StoreData extends FirebaseData{

    public String name;

    public StoreData(){

    }

    public StoreData(Store store){
        name = store.getName();
    }
}

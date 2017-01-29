package com.fivetrue.market.memo.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by kwonojin on 2017. 1. 23..
 */

public class Store extends RealmObject{

    @PrimaryKey
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}

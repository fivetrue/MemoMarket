package com.fivetrue.market.memo.model.vo;

import io.realm.RealmObject;

/**
 * Created by kwonojin on 2017. 1. 23..
 */

public class Coast extends RealmObject{

    private long value;
    private String currency;
    private long date;
    private String storeName;

    public long getValue() {
        return value;
    }

    public String getCurrency() {
        return currency;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
}

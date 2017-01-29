package com.fivetrue.market.memo.model;

/**
 * Created by kwonojin on 2017. 1. 26..
 */

public class StoreWrapper {

    public static final int TYPE_ADD_HEADER = 0;
    public static final int TYPE_STORE_ITEM = 1;

    public final int type;
    public final Store store;

    public StoreWrapper(int type, Store store) {
        this.type = type;
        this.store = store;
    }
}

package com.fivetrue.market.memo;

import android.app.Application;

import com.fivetrue.market.memo.utils.DataManager;


/**
 * Created by kwonojin on 2017. 1. 18..
 */

public class MemoApplication extends Application {

    private static final String TAG = "MemoApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        DataManager.getInstance(this);
    }
}

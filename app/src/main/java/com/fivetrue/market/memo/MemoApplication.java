package com.fivetrue.market.memo;

import android.app.Application;

import com.fivetrue.market.memo.database.FirebaseDB;
import com.fivetrue.market.memo.database.RealmDB;

/**
 * Created by kwonojin on 2017. 1. 18..
 */

public class MemoApplication extends Application {

    private static final String TAG = "MemoApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        RealmDB.init(this);
        FirebaseDB.init(this);
    }
}

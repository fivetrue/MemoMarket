package com.fivetrue.market.memo;

import android.arch.persistence.room.RoomDatabase;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.fivetrue.market.memo.persistence.DataRepository;
import com.fivetrue.market.memo.persistence.database.AppExecutors;
import com.fivetrue.market.memo.persistence.database.MemoDatabase;
import com.fivetrue.market.memo.persistence.database.MigrationCallback;
import com.fivetrue.market.memo.persistence.database.RealmDB;
import com.fivetrue.market.memo.net.NetworkServiceProvider;
import com.fivetrue.market.memo.utils.AdUtil;
import com.fivetrue.market.memo.net.NetworkData;
import com.fivetrue.market.memo.utils.TrackingUtil;


/**
 * Created by kwonojin on 2017. 1. 18..
 */

public class MemoApplication extends MultiDexApplication {

    private static final String TAG = "MemoApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        NetworkServiceProvider.init(this);
        RealmDB.init(this);
        NetworkData.getInstance(this);
        TrackingUtil.init(this);
        AdUtil.init(this);

        DataRepository.getInstance(this).findAllProducts().observeForever(productEntities ->
                DataRepository.getInstance(this).updateWidget()
        );
    }


    public MemoDatabase getDatabase(){
        return MemoDatabase.getInstance(this, new AppExecutors(), new MigrationCallback() {
            @Override
            public boolean hasMigrationData(RoomDatabase database) {
                return false;
            }

            @Override
            public void onMigration(RoomDatabase database) {

            }

            @Override
            public void onFinishMigration(RoomDatabase database) {

            }
        });
    }
}

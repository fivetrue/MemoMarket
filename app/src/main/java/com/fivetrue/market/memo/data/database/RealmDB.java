package com.fivetrue.market.memo.data.database;

import android.content.Context;

import com.fivetrue.market.memo.data.database.product.ProductDB;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by kwonojin on 2017. 1. 24..
 */

@Deprecated
public class RealmDB {

    private static final String TAG = "RealmDB";

    public static Realm get(){
        return Realm.getDefaultInstance();
    }

    public static void init(Context context){
        Realm.init(context);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(RealmDBMigration.DB_VERSION) // 스키마가 바뀌면 값을 올려야만 합니다
                .migration(new RealmDBMigration()) // 예외 발생대신에 마이그레이션을 수행하기
                .build();
        Realm.setDefaultConfiguration(config);

        ProductDB.init(context);
    }
}

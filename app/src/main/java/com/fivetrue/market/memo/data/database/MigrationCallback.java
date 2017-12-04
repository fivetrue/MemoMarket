package com.fivetrue.market.memo.data.database;

import android.arch.persistence.room.RoomDatabase;

/**
 * Created by kwonojin on 2017. 11. 20..
 */
public interface MigrationCallback<DB extends RoomDatabase> {

    boolean hasMigrationData(DB database);

    void onMigration(DB database);

    void onFinishMigration(DB database);
}

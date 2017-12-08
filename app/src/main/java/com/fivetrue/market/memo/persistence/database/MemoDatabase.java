package com.fivetrue.market.memo.persistence.database;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.fivetrue.market.memo.persistence.database.converter.DateConverter;
import com.fivetrue.market.memo.persistence.database.dao.ProductDao;
import com.fivetrue.market.memo.model.entity.ProductEntity;


/**
 * Created by kwonojin on 2017. 11. 16..
 */

@Database(entities = {ProductEntity.class}, version = 1)
@TypeConverters(DateConverter.class)
public abstract class MemoDatabase extends RoomDatabase{

    private static final String TAG = "MemoDatabase";

    private static MemoDatabase sInstance;

    private AppExecutors appExecutors;

    @VisibleForTesting
    public static final String DATABASE_NAME = "market-memo-db";

    public abstract ProductDao productDao();

    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    public static MemoDatabase getInstance(final Context context){
        if(sInstance == null){
            return getInstance(context, new AppExecutors(), new RealmMemoMigrationCallback());
        }else{
            return sInstance;
        }
    }

    public static MemoDatabase getInstance(final Context context, final AppExecutors executors, MigrationCallback migrationCallback) {
        if (sInstance == null) {
            synchronized (MemoDatabase.class) {
                if (sInstance == null) {
                    sInstance = buildDatabase(context.getApplicationContext(), executors, migrationCallback);
                    sInstance.appExecutors = executors;
                    sInstance.updateDatabaseCreated(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    /**
     * Build the database. {@link Builder#build()} only sets up the database configuration and
     * creates a new instance of the database.
     * The SQLite database is only created when it's accessed for the first time.
     */
    private static MemoDatabase buildDatabase(final Context appContext,
                                             final AppExecutors executors, final MigrationCallback callback) {
        return Room.databaseBuilder(appContext, MemoDatabase.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        executors.diskIO().execute(() -> {
                            MemoDatabase database = MemoDatabase.getInstance(appContext, executors, callback);
                            if(callback.hasMigrationData(database)){
                                callback.onMigration(database);
                            }
                            callback.onFinishMigration(database);
                            // notify that the database was created and it's ready to be used
                            database.setDatabaseCreated();
                        });
                    }
                }).build();
    }

    /**
     * Check whether the database already exists and expose it via {@link #getDatabaseCreated()}
     */
    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated();
        }
    }

    private void setDatabaseCreated(){
        mIsDatabaseCreated.postValue(true);
    }

    public LiveData<Boolean> getDatabaseCreated() {
        return mIsDatabaseCreated;
    }

    public AppExecutors getAppExecutors(){
        return appExecutors;
    }
}

package com.fivetrue.market.memo.database;

import android.content.Context;
import android.util.Log;

import com.fivetrue.market.memo.LL;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;

/**
 * Created by kwonojin on 2017. 1. 24..
 */

public class RealmDB {

    private static final String TAG = "RealmDB";

    private static final int DB_VERSION = 0;

    private static RealmDB sInstance;

    private Context mContext;

    public static void init(Context context){
        sInstance = new RealmDB(context.getApplicationContext());
    }

    public static RealmDB getInstance(){
        return sInstance;
    }


    private RealmDB(Context context){
        this.mContext = context;
        Realm.init(context);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(DB_VERSION) // 스키마가 바뀌면 값을 올려야만 합니다
                .migration(new ReamDBMigration()) // 예외 발생대신에 마이그레이션을 수행하기
                .build();
    }

    public Realm get(){
        return Realm.getDefaultInstance();
    }


    private static final class ReamDBMigration implements RealmMigration {

        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
            if(LL.D)
                Log.d(TAG, "migrate() called with: realm = [" + realm + "], oldVersion = [" + oldVersion + "], newVersion = [" + newVersion + "]");

            // DynamicRealm는 편집가능한 스키마를 노출합니다
//            RealmSchema schema = realm.getSchema();
//
//            // 버전 1로 마이그레이션: 클래스를 생성합니다
//            if (oldVersion == 0) {
//                schema.create("Person")
//                        .addField("name", String.class)
//                        .addField("age", int.class);
//                oldVersion++;
//            }
//
//            // 버전 2로 마이그레이션: 기본 키를 넣고 객체를 참조합니다
//            if (oldVersion == 1) {
//                schema.get("Person")
//                        .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
//                        .addRealmObjectField("favoriteDog", schema.get("Dog"))
//                        .addRealmListField("dogs", schema.get("Dog"));
//                oldVersion++;
//            }
        }
    }
}

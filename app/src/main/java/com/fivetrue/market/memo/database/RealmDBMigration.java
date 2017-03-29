package com.fivetrue.market.memo.database;

import android.util.Log;

import com.fivetrue.market.memo.LL;

import io.realm.DynamicRealm;

/**
 * Created by kwonojin on 2017. 3. 28..
 */

public class RealmDBMigration implements io.realm.RealmMigration {

    private static final String TAG = "RealmDBMigration";

    public static final int DB_VERSION = 1;

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

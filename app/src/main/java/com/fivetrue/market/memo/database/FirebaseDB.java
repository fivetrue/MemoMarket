package com.fivetrue.market.memo.database;

import android.content.Context;

import com.fivetrue.market.memo.firebase.StoreData;
import com.fivetrue.market.memo.model.Store;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kwonojin on 2017. 1. 27..
 */

public class FirebaseDB {

    private static final String TAG = "FirebaseDB";


    private static final String NODE_MARKET = "market";
    private static final String NODE_STORE = "store";


    private static FirebaseDB sInstance;

    private Context mContext;

    private FirebaseDB(Context context){
        mContext = context;
    }

    public static void init(Context context){
        sInstance = new FirebaseDB(context);
    }

    public static FirebaseDB getInstance(){
        return sInstance;
    }


    public Task<Void> addStore(Store store){
        StoreData data = new StoreData(store);
        return FirebaseDatabase.getInstance().getReference(NODE_MARKET).child(NODE_STORE).push().setValue(data.getValues());
    }

    public Query findStoreContain(String text){
        return FirebaseDatabase.getInstance().getReference(NODE_MARKET)
                .child(NODE_STORE).orderByChild("name").startAt(text).endAt(text +  "\uf8ff");
    }
}

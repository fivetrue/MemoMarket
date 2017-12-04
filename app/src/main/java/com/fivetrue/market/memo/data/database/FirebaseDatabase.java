package com.fivetrue.market.memo.data.database;

import android.content.Context;
import android.util.Log;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.model.remote.ConfigData;
import com.fivetrue.market.memo.model.remote.ProductData;
import com.fivetrue.market.memo.model.Product;
import com.fivetrue.market.memo.preference.DefaultPreferenceUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;


/**
 * Created by kwonojin on 2017. 1. 27..
 */

public class FirebaseDatabase {

    private static final String TAG = "FirebaseDatabase";

    private static final String NODE_CONFIG = "config";
    private static final String NODE_MARKET = "market";
    private static final String NODE_PRODUCT = "product";

    private Context mContext;

    private static FirebaseDatabase sInstance;

    public static FirebaseDatabase getInstance(Context context){
        if(sInstance == null){
            sInstance = new FirebaseDatabase(context.getApplicationContext());
        }
        return sInstance;
    }

    private FirebaseDatabase(Context context){
        mContext = context;
    }


    public Single<ConfigData> getConfig(){
        if(LL.D) Log.d(TAG, "getConfig() called");
        return Single.create(e -> {
            ConfigData data = DefaultPreferenceUtil.getConfigData(mContext);
            if(data != null){
                e.onSuccess(data);
            }else{
                com.google.firebase.database.FirebaseDatabase.getInstance().getReference(NODE_CONFIG).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(LL.D)
                            Log.d(TAG, "onDataChange() called with: dataSnapshot = [" + dataSnapshot + "]");
                        ConfigData configData = dataSnapshot.getValue(ConfigData.class);
                        e.onSuccess(configData);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if(LL.D)
                            Log.d(TAG, "onCancelled() called with: databaseError = [" + databaseError + "]");
                        e.onError(databaseError.toException());
                    }
                });
            }
        });
    }

    public Task<Void> addProduct(Product product){
        ProductData data = new ProductData(product);
        return com.google.firebase.database.FirebaseDatabase.getInstance().getReference(NODE_MARKET).child(NODE_PRODUCT).push().setValue(data.getValues());

    }

    public Single<List<Product>> findProductContain(final String text){
        return Single.create(e -> com.google.firebase.database.FirebaseDatabase.getInstance().getReference(NODE_MARKET)
                .child(NODE_PRODUCT).orderByChild("name").startAt(text).endAt(text +  "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(LL.D) Log.d(TAG, "onDataChange: check count for adding : " + dataSnapshot.getChildrenCount());
                        List<Product> productDataList  = Observable.fromIterable(dataSnapshot.getChildren())
                                .map(data -> (Product) data.getValue(ProductData.class))
                                .toList().blockingGet();
                        e.onSuccess(productDataList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if(LL.D)
                            Log.d(TAG, "onCancelled() called with: databaseError = [" + databaseError + "]");
                        e.onError(databaseError.toException());
                    }
                }));
    }
}

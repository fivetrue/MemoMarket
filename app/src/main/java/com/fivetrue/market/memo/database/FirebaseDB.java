package com.fivetrue.market.memo.database;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.model.dto.ConfigData;
import com.fivetrue.market.memo.model.dto.ProductData;
import com.fivetrue.market.memo.model.vo.Product;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;


/**
 * Created by kwonojin on 2017. 1. 27..
 */

public class FirebaseDB {

    private static final String TAG = "FirebaseDB";

    private static final Map<String, Observable> sObservableMap = new HashMap<>();


    private static final String NODE_CONFIG = "config";
    private static final String NODE_MARKET = "market";
    private static final String NODE_PRODUCT = "product";

    private Context mContext;

    private static FirebaseDB sInstance;

    public static FirebaseDB getInstance(Context context){
        if(sInstance == null){
            sInstance = new FirebaseDB(context.getApplicationContext());
        }
        return sInstance;
    }

    private FirebaseDB(Context context){
        mContext = context;
    }


    public Observable<ConfigData> getConfig(){
        if(LL.D) Log.d(TAG, "getConfig() called");
        Observable<ConfigData> observable = sObservableMap.get(NODE_CONFIG);
        if(observable == null){
            observable = Observable.create(new ObservableOnSubscribe<ConfigData>() {

                private ConfigData configData;

                @Override
                public void subscribe(final ObservableEmitter<ConfigData> e) throws Exception {
                    if(configData == null){
                        FirebaseDatabase.getInstance().getReference(NODE_CONFIG).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(LL.D)
                                    Log.d(TAG, "onDataChange() called with: dataSnapshot = [" + dataSnapshot + "]");
                                configData = dataSnapshot.getValue(ConfigData.class);
                                e.onNext(configData);
                                e.onComplete();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                if(LL.D)
                                    Log.d(TAG, "onCancelled() called with: databaseError = [" + databaseError + "]");
                                e.onError(databaseError.toException());
                                e.onComplete();
                            }
                        });
                    }else{
                        e.onNext(configData);
                        e.onComplete();
                    }
                }
            });
            sObservableMap.put(NODE_CONFIG, observable);
        }
        return observable;
    }

//    public Observable<List<ProductData>> findBarcode(String barcode){
//        return Observable.create(e -> {
//            FirebaseDatabase.getInstance().getReference(NODE_MARKET)
//                    .child(NODE_PRODUCT).orderByChild("barcode").addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if(LL.D)
//                        Log.d(TAG, "onDataChange() called with: dataSnapshot = [" + dataSnapshot + "]");
//                    if(dataSnapshot != null && dataSnapshot.getChildrenCount() > 0){
//                        List<ProductData> dataList = Observable.fromIterable(dataSnapshot.getChildren())
//                                .map(data -> data.getValue(ProductData.class))
//                                .filter(productData -> !TextUtils.isEmpty(productData.barcode) && productData.barcode.equalsIgnoreCase(barcode))
//                                .toList().blockingGet();
//                        if(dataList != null && dataList.size() > 0){
//                            e.onNext(dataList);
//                            e.onComplete();
//                        }else{
//                            e.onError(new Resources.NotFoundException(mContext.getString(R.string.error_empty_product_barcode)));
//                        }
//                    }else{
//                        e.onError(new Resources.NotFoundException(mContext.getString(R.string.error_empty_product_barcode)));
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    if(LL.D)
//                        Log.d(TAG, "onCancelled() called with: databaseError = [" + databaseError + "]");
//                    e.onError(databaseError.toException());
//                }
//            });
//        });
//    }

    public Task<Void> addProduct(Product product){
        ProductData data = new ProductData(product);
        return FirebaseDatabase.getInstance().getReference(NODE_MARKET).child(NODE_PRODUCT).push().setValue(data.getValues());

    }

    public Observable<List<ProductData>> findProductContain(final String text){
        String key = "findProductContain:"+text;
        Observable<List<ProductData>> storeObs = sObservableMap.get(key);
        if(storeObs == null){
            storeObs = Observable.create(new ObservableOnSubscribe<List<ProductData>>() {

                private List<ProductData> storeDataList;

                @Override
                public void subscribe(final ObservableEmitter<List<ProductData>> e) throws Exception {
                    if(storeDataList == null){
                        FirebaseDatabase.getInstance().getReference(NODE_MARKET)
                                .child(NODE_PRODUCT).orderByChild("name").startAt(text).endAt(text +  "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(LL.D) Log.d(TAG, "onDataChange: check count for adding : " + dataSnapshot.getChildrenCount());
                                storeDataList = Observable.fromIterable(dataSnapshot.getChildren())
                                        .map(data -> data.getValue(ProductData.class))
                                        .toList().blockingGet();
                                e.onNext(storeDataList);
                                e.onComplete();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                if(LL.D)
                                    Log.d(TAG, "onCancelled() called with: databaseError = [" + databaseError + "]");
                                e.onError(databaseError.toException());
                            }
                        });
                    }else{
                        e.onNext(storeDataList);
                        e.onComplete();
                    }
                }
            });
            sObservableMap.put(key, storeObs);
        }
        return storeObs;
    }
}

package com.fivetrue.market.memo.database;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.model.dto.ConfigData;
import com.fivetrue.market.memo.model.dto.ProductData;
import com.fivetrue.market.memo.model.dto.StoreData;
import com.fivetrue.market.memo.model.vo.Product;
import com.fivetrue.market.memo.model.vo.Store;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.subjects.PublishSubject;


/**
 * Created by kwonojin on 2017. 1. 27..
 */

public class FirebaseDB {

    private static final String TAG = "FirebaseDB";

    private static final Map<String, Observable> sObservableMap = new HashMap<>();


    private static final String NODE_CONFIG = "config";
    private static final String NODE_MARKET = "market";
    private static final String NODE_STORE = "store";
    private static final String NODE_PRODUCT = "product";

    private PublishSubject<ConfigData> mConfigDataPublishSubject;

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
        mConfigDataPublishSubject = PublishSubject.create();
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

    public Observable<StoreData> addStore(String name){
        final StoreData data = new StoreData(name);
        Observable<StoreData> observable = Observable.create(new ObservableOnSubscribe<StoreData>() {
            @Override
            public void subscribe(final ObservableEmitter<StoreData> ob) throws Exception {
                FirebaseDatabase.getInstance().getReference(NODE_MARKET)
                        .child(NODE_STORE)
                        .push()
                        .setValue(data.getValues())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                ob.onNext(data);
                                ob.onComplete();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ob.onError(e);
                        ob.onComplete();
                    }
                });
            }
        });
        return observable;
    }

    public Task<Void> addProduct(Product product){
        ProductData data = new ProductData(product);
        return FirebaseDatabase.getInstance().getReference(NODE_MARKET).child(NODE_PRODUCT).push().setValue(data.getValues());

    }

    public Observable<List<StoreData>> findStoreContain(final String text){
        String key = "findStoreContain:"+text;
        Observable<List<StoreData>> storeObs = sObservableMap.get(key);
        if(storeObs == null){
            storeObs = Observable.create(new ObservableOnSubscribe<List<StoreData>>() {

                private List<StoreData> storeDataList;

                @Override
                public void subscribe(final ObservableEmitter<List<StoreData>> e) throws Exception {
                    if(storeDataList == null){
                        FirebaseDatabase.getInstance().getReference(NODE_MARKET)
                                .child(NODE_STORE).orderByChild("name").startAt(text).endAt(text +  "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(LL.D) Log.d(TAG, "onDataChange: check count for adding : " + dataSnapshot.getChildrenCount());
                                storeDataList = new ArrayList<StoreData>();
                                if(dataSnapshot != null && dataSnapshot.getChildrenCount() > 0){

                                    for(DataSnapshot d : dataSnapshot.getChildren()){
                                        storeDataList.add(d.getValue(StoreData.class));
                                    }
                                }
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
                                storeDataList = new ArrayList<ProductData>();
                                if(dataSnapshot != null && dataSnapshot.getChildrenCount() > 0){
                                    for(DataSnapshot d : dataSnapshot.getChildren()){
                                        storeDataList.add(d.getValue(ProductData.class));
                                    }
                                }
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

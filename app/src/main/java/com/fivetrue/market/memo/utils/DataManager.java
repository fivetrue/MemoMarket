package com.fivetrue.market.memo.utils;

import android.content.Context;
import android.util.Log;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.database.FirebaseDB;
import com.fivetrue.market.memo.database.RealmDB;
import com.fivetrue.market.memo.model.dto.ConfigData;
import com.fivetrue.market.memo.model.dto.ProductData;
import com.fivetrue.market.memo.model.dto.StoreData;
import com.fivetrue.market.memo.model.image.ImageEntry;
import com.fivetrue.market.memo.model.vo.Product;
import com.fivetrue.market.memo.model.vo.Store;
import com.fivetrue.market.memo.net.NetworkServiceProvider;
import com.fivetrue.market.memo.net.service.ImageService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;

/**
 * Created by kwonojin on 2017. 2. 23..
 */

public class DataManager {

    private static final String TAG = "DataManager";

    private static final Map<String, Observable> sObservableMap = new HashMap<>();

    private FirebaseDB mFirebaseDB;
    private RealmDB mRealDB;

    private ImageService mImageService;

    private Context mContext;

    private static DataManager sInstance;

    public static DataManager getInstance(Context context){
        if(sInstance == null){
            sInstance = new DataManager(context);
        }
        return sInstance;
    }

    private DataManager(Context context){
        mContext = context;
        mFirebaseDB = FirebaseDB.getInstance(context);
    }

    public Observable<ConfigData> getConfig(){
        return mFirebaseDB.getConfig();
    }

    public Observable<List<StoreData>> findStoreName(String name){
        return mFirebaseDB.findStoreContain(name);
    }

    public Observable<List<ProductData>> findProductName(String name){
        return mFirebaseDB.findProductContain(name);
    }

    public Observable<ImageEntry> findImage(ConfigData config, final String q){
        if(LL.D) Log.d(TAG, "findImage() called with: config = [" + config + "], q = [" + q + "]");
        if(mImageService == null){
            mImageService = NetworkServiceProvider.getImageService();
        }
        return mImageService.getImageList(config.msKey, q, "en-us");
    }

    public Observable<Store> addStore(final Store store){
        Observable<Store> observable = Observable.create(new ObservableOnSubscribe<Store>() {
            @Override
            public void subscribe(final ObservableEmitter<Store> e) throws Exception {
                if(RealmDB.get().where(Store.class).equalTo("name", store.getName()).count() == 0){
                    RealmDB.get().beginTransaction();
                    RealmDB.get().insert(store);
                    RealmDB.get().commitTransaction();
                    mFirebaseDB.findStoreContain(store.getName()).subscribe(new Consumer<List<StoreData>>() {
                        @Override
                        public void accept(List<StoreData> storeDatas) throws Exception {
                            if(storeDatas.isEmpty()){
                                mFirebaseDB.addStore(store.getName()).subscribe(new Consumer<StoreData>() {
                                    @Override
                                    public void accept(StoreData storeData) throws Exception {
                                        e.onNext(store);
                                        e.onComplete();
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        Log.e(TAG, "addStore: ", throwable);
                                        e.onError(throwable);
                                        e.onComplete();
                                    }
                                });
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.e(TAG, "addStore: ", throwable);
                            e.onError(throwable);
                            e.onComplete();
                        }
                    });
                }else{
                    e.onError(new Exception("Already has item = " + store.getName()));
                    e.onComplete();
                }
            }
        });
        return observable;
    }
}

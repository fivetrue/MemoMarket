package com.fivetrue.market.memo.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.net.NetworkData;
import com.fivetrue.market.memo.persistence.database.AppExecutors;
import com.fivetrue.market.memo.persistence.database.FirebaseDatabase;
import com.fivetrue.market.memo.persistence.database.MemoDatabase;
import com.fivetrue.market.memo.model.Image;
import com.fivetrue.market.memo.model.Product;
import com.fivetrue.market.memo.model.entity.ProductEntity;
import com.fivetrue.market.memo.model.remote.ConfigData;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by kwonojin on 2017. 11. 21..
 */

public class DataRepository implements Repository {

    private static final String TAG = "DataRepository";

    private static DataRepository sInstance;

    private Context context;

    private MemoDatabase localDatabase;
    private FirebaseDatabase firebaseDatabase;
    private NetworkData networkData;

    private MutableLiveData<ConfigData> configData = new MutableLiveData<>();

    public static DataRepository getInstance(Context context){
        if(sInstance == null){
            sInstance = new DataRepository(context);
        }
        return sInstance;
    }

    private DataRepository(Context context){
        this.context = context;
        this.localDatabase = MemoDatabase.getInstance(context);
        this.firebaseDatabase = FirebaseDatabase.getInstance(context);
        this.networkData = NetworkData.getInstance(context);

    }

    public Single<ConfigData> getConfigData(){
        return firebaseDatabase.getConfig();
    }

    public Single<List<Product>> findProduct(String text){
        return Single.create(e -> 
            localDatabase.getAppExecutors().diskIO().execute(() -> {
                if(LL.D) Log.d(TAG, "findProduct: TRY TO FIND ON LOCAL DB - " + text);
                List<ProductEntity> entities = localDatabase.productDao().findProductByName(text);
                if(entities != null && !entities.isEmpty()){
                    if(LL.D) Log.d(TAG, "findProduct: SUCCESS FOUND ITEMS ON LOCAL DB - " + entities.size());
                    localDatabase.getAppExecutors().mainThread().execute(() ->
                        e.onSuccess(Observable.fromIterable(entities).map(productEntity -> (Product) productEntity).toList().blockingGet()));
                }else{
                    if(LL.D) Log.d(TAG, "findProduct: TRY TO FIND ON REMOTE DB - " + text);
                    firebaseDatabase.findProductContain(text).subscribe((products, throwable) -> {
                        if(products != null){
                            if(LL.D) Log.d(TAG, "findProduct: SUCCESS FOUND ITEMS ON REMOTE DB - " + products.size());
                            localDatabase.getAppExecutors().mainThread().execute(() -> e.onSuccess(products));
                        }else{
                            if(LL.D) Log.d(TAG, "findProduct: CAN NOT FOUNDS - " + text);
                            localDatabase.getAppExecutors().mainThread().execute(() -> e.onSuccess(null));
                        }
                    });
                }
            }));
    }

    public Completable insertProduct(String name, String store, long price){
        return Completable.create(e -> {
            findImage(name, 0).subscribe(images -> {
                String imageUrl = images.get(0).getImageThumbnailUrl();
                ProductEntity productEntity = new ProductEntity();
                productEntity.setName(name);
                productEntity.setStoreName(store);
                productEntity.setPrice(price);
                productEntity.setImageUrl(imageUrl);
                productEntity.setCheckInDate(System.currentTimeMillis());
                localDatabase.getAppExecutors().diskIO().execute(() -> {
                    localDatabase.productDao().insert(productEntity);
                    localDatabase.getAppExecutors().mainThread().execute(() -> e.onComplete());
                });
            }, throwable -> e.onError(throwable));
        });
    }

    public Completable insertProduct(ProductEntity product){
        return Completable.create(e -> {
                ProductEntity productEntity = new ProductEntity();
                productEntity.setName(product.getName());
                productEntity.setStoreName(product.getStoreName());
                productEntity.setPrice(product.getPrice());
                productEntity.setImageUrl(product.getImageUrl());
                productEntity.setCheckInDate(System.currentTimeMillis());
                localDatabase.getAppExecutors().diskIO().execute(() -> {
                    localDatabase.productDao().insert(productEntity);
                    localDatabase.getAppExecutors().mainThread().execute(() -> e.onComplete());
                });
        });
    }

    public Completable insertProduct(List<ProductEntity> products){
        return Completable.create(e -> {
                localDatabase.getAppExecutors().diskIO().execute(() -> {
                    localDatabase.productDao().insertAll(products);
                    localDatabase.getAppExecutors().mainThread().execute(() -> e.onComplete());
                });
        });
    }

    public Completable deleteProduct(ProductEntity product){
        return Completable.create(e ->
            localDatabase.getAppExecutors().diskIO().execute(() -> {
                localDatabase.productDao().deleteProducts(product);
                localDatabase.getAppExecutors().mainThread().execute(() -> e.onComplete());
            }));
    }

    public Completable updateProduct(ProductEntity product){
        return Completable.create(e ->
            localDatabase.getAppExecutors().diskIO().execute(() -> {
                localDatabase.productDao().update(product);
                localDatabase.getAppExecutors().mainThread().execute(() -> e.onComplete());
            }));
    }

    public Completable updateProducts(List<ProductEntity> products){
        return Completable.create(e ->
                localDatabase.getAppExecutors().diskIO().execute(() -> {
                    localDatabase.productDao().updateAll(products);
                    localDatabase.getAppExecutors().mainThread().execute(() -> e.onComplete());
                }));
    }

    public LiveData<List<ProductEntity>> findAllProducts(){
        return localDatabase.productDao().findAllByLiveData();
    }

    public List<ProductEntity> findAllInboxProduct(){
        return localDatabase.productDao().findAllInboxProduct();
    }

    public ProductEntity findProduct(long id){
        return localDatabase.productDao().findItem(id);
    }


    public Observable<List<Image>> findImage(String name, int count){
        return networkData.findImages(name, count);
    }

    public AppExecutors getAppExecutors(){
        return localDatabase.getAppExecutors();
    }

    public void updateWidget(){
        Intent intent = new Intent("android.appwidget.action.APPWIDGET_UPDATE");
        context.sendBroadcast(intent);
    }
}

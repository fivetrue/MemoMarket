package com.fivetrue.market.memo.database.product;

import android.util.Log;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.database.RealmDB;
import com.fivetrue.market.memo.model.vo.Product;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;

/**
 * Created by kwonojin on 2017. 3. 28..
 */

public class ProductDB extends RealmDB implements RealmChangeListener<Realm>{

    private static final String TAG = "ProductDB";

    private static ProductDB sInstance;

    private PublishSubject<List<Product>> mProductPublishSubject;

    public static ProductDB getInstance(){
        if(sInstance == null){
            sInstance = new ProductDB();
        }
        return sInstance;
    }

    private ProductDB(){
        mProductPublishSubject = PublishSubject.create();
        get().addChangeListener(this);
    }


    public void add(Product product){
        get().executeTransaction(realm -> {
            get().insert(product);
            updatePublish();
        });
    }

    public void add(List<Product> products){
        get().executeTransaction(realm -> {
            get().insert(products);
            updatePublish();
        });
    }

    public void delete(Product product){
        get().executeTransaction(realm -> {
            product.deleteFromRealm();
            updatePublish();
        });
    }

    public List<Product> getProducts(){
        return get().where(Product.class).findAllSorted("checkInDate");
    }

    public Product findBarcode(String barcode){
        return get().where(Product.class).equalTo("barcode", barcode).findFirst();
    }

    public Observable<List<Product>> getObservable(){
        return mProductPublishSubject;
    }

    public void updatePublish(){
        if(LL.D) Log.d(TAG, "updatePublish() called");
        mProductPublishSubject.onNext(getProducts());
    }

    @Override
    public void onChange(Realm element) {
        if(LL.D) Log.d(TAG, "onChange() called with: element = [" + element + "]");
        updatePublish();
    }
}

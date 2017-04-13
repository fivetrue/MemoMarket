package com.fivetrue.market.memo.database.product;

import android.content.Context;
import android.content.Intent;
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

    private Context mContext;
    private PublishSubject<List<Product>> mProductPublishSubject;

    public static void init(Context context){
        sInstance = new ProductDB(context.getApplicationContext());
    }

    public static ProductDB getInstance(){
        return sInstance;
    }

    private ProductDB(Context context){
        mContext = context;
        mProductPublishSubject = PublishSubject.create();
        get().addChangeListener(this);
    }


    public void add(Product product){
        get().executeTransaction(realm -> {
            get().insert(product);
        });
    }

    public void add(List<Product> products){
        get().executeTransaction(realm -> {
            get().insert(products);
        });
    }

    public void delete(Product product){
        get().executeTransaction(realm -> {
            product.deleteFromRealm();
        });
    }

    public List<Product> getProducts(){
        return get().where(Product.class).findAllSorted("checkInDate");
    }

    public List<Product> findBarcode(String barcode){
        return get().where(Product.class).equalTo("barcode", barcode).findAll();
    }

    public List<Product> findName(String name){
        return get().where(Product.class).contains("name", name).findAll();
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
        updateIntent();
    }

    private void updateIntent(){
        Intent intent = new Intent("android.appwidget.action.APPWIDGET_UPDATE");
        mContext.sendBroadcast(intent);
    }
}

package com.fivetrue.market.memo.utils;

import android.content.Context;
import android.util.Log;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.database.FirebaseDB;
import com.fivetrue.market.memo.database.product.ProductDB;
import com.fivetrue.market.memo.model.dto.ConfigData;
import com.fivetrue.market.memo.model.dto.ProductData;
import com.fivetrue.market.memo.model.image.ImageEntry;
import com.fivetrue.market.memo.model.vo.Product;
import com.fivetrue.market.memo.net.NetworkServiceProvider;
import com.fivetrue.market.memo.preference.DefaultPreferenceUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

/**
 * Created by kwonojin on 2017. 2. 23..
 */

public class DataManager {

    private static final String TAG = "DataManager";

    private static final Map<String, Observable> sObservableMap = new HashMap<>();

    private FirebaseDB mFirebaseDB;

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
        ConfigData data = DefaultPreferenceUtil.getConfigData(mContext);
        if(data != null){
            return Observable.just(data);
        }
        return mFirebaseDB.getConfig();
    }

    public Observable<List<ProductData>> findProductName(String name){
        List<Product> products = ProductDB.getInstance().findName(name);
        if(products != null && products.size() > 0){
            Log.i(TAG, "findProductName: exist data in Local = " + products.size());
            return Observable.fromIterable(products)
                    .map(product -> new ProductData(product))
                    .buffer(products.size());
        }
        return mFirebaseDB.findProductContain(name);
    }

    public Observable<List<ProductData>> findBarcode(String barcode){
        List<Product> products = ProductDB.getInstance().findBarcode(barcode);
        if(products != null && products.size() > 0){
            Log.i(TAG, "findBarcode: exist data in Local = " + products.size());
            return Observable.fromIterable(products)
                    .map(product -> new ProductData(product))
                    .buffer(products.size());
        }
        return mFirebaseDB.findBarcode(barcode);
    }

    public Observable<ImageEntry> findImage(ConfigData config, final String q){
        Log.i(TAG, "findImage: q = " + q);
        if(LL.D) Log.d(TAG, "findImage() called with: config = [" + config + "], q = [" + q + "]");
        return NetworkServiceProvider.getInstance()
                .getImageService()
                .getImageList(config.msKey, q, "en-us");
    }

}

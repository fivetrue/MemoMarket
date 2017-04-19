package com.fivetrue.market.memo.utils;

import android.content.Context;
import android.util.Log;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.database.FirebaseDB;
import com.fivetrue.market.memo.database.product.ProductDB;
import com.fivetrue.market.memo.model.dto.ConfigData;
import com.fivetrue.market.memo.model.dto.GeoLocation;
import com.fivetrue.market.memo.model.dto.ProductData;
import com.fivetrue.market.memo.model.image.GoogleImage;
import com.fivetrue.market.memo.model.vo.Product;
import com.fivetrue.market.memo.net.NetworkServiceProvider;
import com.fivetrue.market.memo.preference.DefaultPreferenceUtil;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by kwonojin on 2017. 2. 23..
 */

public class DataManager {

    private static final String TAG = "DataManager";

    private static final Map<String, Observable> sObservableMap = new HashMap<>();

    private FirebaseDB mFirebaseDB;

    private Context mContext;

    private static DataManager sInstance;

    public static DataManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DataManager(context);
        }
        return sInstance;
    }

    private DataManager(Context context) {
        mContext = context;
        mFirebaseDB = FirebaseDB.getInstance(context);
    }

    public Observable<ConfigData> getConfig() {
        ConfigData data = DefaultPreferenceUtil.getConfigData(mContext);
        if (data != null) {
            return Observable.just(data);
        }
        return mFirebaseDB.getConfig();
    }

    public Observable<List<ProductData>> findProductName(String name) {
        List<Product> products = ProductDB.getInstance().findName(name);
        if (products != null && products.size() > 0) {
            Log.i(TAG, "findProductName: exist data in Local = " + products.size());
            return Observable.fromIterable(products)
                    .map(product -> new ProductData(product))
                    .toList().toObservable();
        }
        return mFirebaseDB.findProductContain(name);
    }

    public Observable<List<Product>> findStoreName(String name){
        return Observable.fromIterable(ProductDB.getInstance().findStoreName(name)).toList().toObservable();
    }

    public Observable<List<ProductData>> findBarcode(String barcode) {
        List<Product> products = ProductDB.getInstance().findBarcode(barcode);
        if (products != null && products.size() > 0) {
            Log.i(TAG, "findBarcode: exist data in Local = " + products.size());
            return Observable.fromIterable(products)
                    .map(product -> new ProductData(product))
                    .buffer(products.size());
        }
        return mFirebaseDB.findBarcode(barcode);
    }

//    public Observable<ImageEntry> findImage(ConfigData config, final String q) {
//        Log.i(TAG, "findImage: q = " + q);
//        if (LL.D) Log.d(TAG, "findImage() called with: config = [" + config + "], q = [" + q + "]");
//        return NetworkServiceProvider.getInstance()
//                .getImageService()
//                .getImageList(config.msKey, q, "en-us");
//    }

    public Observable<GoogleImage> findGoogleImage(String q){
        if(LL.D) Log.d(TAG, "findGoogleImage() called with: q = [" + q + "]");
        Map<String, String> map = new HashMap<>();
        map.put("tbm","isch");
        map.put("q",q);
        map.put("gws_rd","cr");
        map.put("ei","k0PyWKPiLoOC8wXFr4uoCg");
        return NetworkServiceProvider.getInstance()
                .getGoogleImageService()
                .getGoogleImage(map);
    }

    public Observable<GeoLocation> getGeoLocation() {
        return NetworkServiceProvider.getInstance()
                .getGoogleApiService().getGeoLocation(mContext.getString(R.string.market_google_api_key))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread());
    }

    public Observable<GoogleImage> findImage(String q) {
        return Observable.create((ObservableOnSubscribe<GoogleImage>) e -> {
            if(LL.D) Log.d(TAG, "findImage: q = " + q);
            try {
                String googleUrl = "https://www.google.co.kr/search?tbm=isch&q=" + q + "&gws_rd=cr&ei=k0PyWKPiLoOC8wXFr4uoCg";
                if(LL.D) Log.d(TAG, "findImage: googleUrl = " + googleUrl);
                Document doc = Jsoup.connect(googleUrl).timeout(10 * 1000).get();
                Element data = doc.select("div.rg_meta").first();
                String json = data.childNode(0).toString().trim();
                e.onNext(new Gson().fromJson(json, GoogleImage.class));
            } catch (Exception e1) {
                Log.e(TAG, "findImage: ", e1);
                e.onError(e1);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread());
    }
}

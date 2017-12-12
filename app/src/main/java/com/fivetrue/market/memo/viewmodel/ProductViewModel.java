package com.fivetrue.market.memo.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.model.entity.ProductEntity;
import com.fivetrue.market.memo.persistence.DataRepository;
import com.fivetrue.market.memo.model.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by kwonojin on 2017. 11. 21..
 */

public class ProductViewModel extends AndroidViewModel {

    private static final String TAG = "ProductListViewModel";

    private DataRepository dataRepository;

    public ProductViewModel(@NonNull Application application) {
        super(application);
        this.dataRepository = DataRepository.getInstance(application);
    }

    public Completable insertProduct(String name, String store, long price){
        return dataRepository.insertProduct(name, store, price);
    }

    public Completable insertProduct(String name, String store, String price){
        long p = 0;
        try{
            p = Long.parseLong(price);
        }catch (Exception e){
            p = 0;
        }
        return dataRepository.insertProduct(name, store, p);
    }

    public Single<List<Product>> findProduct(String name){
        return dataRepository.findProduct(name);
    }

    public Completable insertProducts(Intent intent){
        return Completable.create(e -> {
            if(hasProduct(intent)){
                String json = intent.getData().getQueryParameter("products");
                if(LL.D) Log.d(TAG, "insertProducts Json  = [" + json + "]");
                Type listType = new TypeToken<List<String>>() {}.getType();
                List<String> products = new Gson().fromJson(json, listType);
                if(LL.D) Log.d(TAG, "insertProducts  = [" + products + "]");
                dataRepository.getAppExecutors().diskIO().execute(() -> {
                    List<ProductEntity> inbox = dataRepository.findAllInboxProduct();
                    Observable.fromIterable(products)
                            .filter(name -> {
                                for(ProductEntity p : inbox){
                                    if(p.getName().equals(name)){
                                        return false;
                                    }
                                }
                                return true;
                            })
                            .toList()
                            .subscribe(productNames -> {
                                if(productNames != null && !productNames.isEmpty()){
                                    Observable.create(e1 -> {
                                        for(String name : productNames){
                                            dataRepository.insertProduct(name, null, 0).subscribe(() -> e1.onNext(name));
                                        }})
                                            .buffer(productNames.size())
                                            .subscribe(objects -> e.onComplete());
                                }else{
                                    e.onComplete();
                                }
                            });
                });
            }else{
                e.onError(new IllegalArgumentException("Intent has no any products"));
            }
        });
    }

    public boolean hasProduct(Intent intent){
        if(intent.getData() != null){
            String params = intent.getData().getQueryParameter("products");
            if(params != null){
                return true;
            }
        }
        return false;
    }
}

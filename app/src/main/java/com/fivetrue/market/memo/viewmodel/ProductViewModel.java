package com.fivetrue.market.memo.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import android.view.View;

import com.fivetrue.market.memo.data.DataRepository;
import com.fivetrue.market.memo.model.Product;
import com.fivetrue.market.memo.model.entity.ProductEntity;

import java.util.List;

import io.reactivex.Completable;
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

    public Single<List<Product>> findProduct(String name){
        return dataRepository.findProduct(name);
    }
}

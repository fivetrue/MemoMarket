package com.fivetrue.market.memo.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.fivetrue.market.memo.persistence.DataRepository;
import com.fivetrue.market.memo.model.entity.ProductEntity;
import com.fivetrue.market.memo.model.Product;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * Created by kwonojin on 2017. 11. 21..
 */

public class ProductListViewModel extends AndroidViewModel {

    private static final String TAG = "ProductListViewModel";

    private DataRepository dataRepository;

    private MediatorLiveData<List<Product>> productObservable;

    public ProductListViewModel(@NonNull Application application) {
        super(application);
        this.dataRepository = DataRepository.getInstance(application);
        this.productObservable = new MediatorLiveData<>();
        this.productObservable.addSource(dataRepository.findAllProducts(), productEntities
                -> productObservable.setValue(Observable.fromIterable(productEntities)
                .map(productEntity -> (Product) productEntity)
                .toList().blockingGet()));
    }

    public Completable updateProduct(Product product){
        if(product instanceof ProductEntity){
            return dataRepository.updateProduct((ProductEntity) product);
        }
        return null;
    }

    public Completable updateProduct(ProductEntity product){
        return dataRepository.updateProduct(product);
    }

    public Completable updateProducts(List<ProductEntity> products){
        return dataRepository.updateProducts(products);
    }

    public Completable duplicateProduct(Product product){
        if(product instanceof ProductEntity){
            return dataRepository.insertProduct((ProductEntity) product);
        }
        return null;
    }

    public Completable deleteProduct(Product product){
        if(product instanceof ProductEntity){
            return dataRepository.deleteProduct((ProductEntity) product);
        }
        return null;
    }

    public LiveData<List<Product>> getProductList(){
        return productObservable;
    }
}

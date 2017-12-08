package com.fivetrue.market.memo.persistence.database;

import android.util.Log;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.model.entity.ProductEntity;
import com.fivetrue.market.memo.persistence.database.product.ProductDB;
import com.fivetrue.market.memo.model.vo.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kwonojin on 2017. 11. 20..
 */

public class RealmMemoMigrationCallback implements MigrationCallback<MemoDatabase>{

    private static final String TAG = "RealmMemoMigrationCallb";

    @Override
    public boolean hasMigrationData(MemoDatabase database) {
        boolean b = !(database.productDao().getCount() > 0);
        if(LL.D) Log.d(TAG, "hasMigrationData() returned: " + b);
        return b;
    }

    @Override
    public void onMigration(MemoDatabase database) {
        if(LL.D) Log.d(TAG, "onMigration() called with: database = [" + database + "]");
        List<Product> products = ProductDB.getInstance().getProducts();
        List<ProductEntity> productEntities = new ArrayList<>();
        if(products != null){
            for(Product product : products){
                ProductEntity productEntity = new ProductEntity();
                productEntity.setName(product.getName());
                productEntity.setStoreName(product.getStoreName());
                productEntity.setCheckOutDate(product.getCheckOutDate());
                productEntity.setCheckInDate(product.getCheckInDate());
                productEntity.setImageUrl(product.getImageUrl());
                productEntity.setPrice(product.getPrice());
                productEntities.add(productEntity);
            }
        }
        if(!productEntities.isEmpty()){
            database.productDao().insertAll(productEntities);
        }
    }

    @Override
    public void onFinishMigration(MemoDatabase database) {
        if(LL.D) Log.d(TAG, "onFinishMigration() called with: database = [" + database + "]");
    }
}

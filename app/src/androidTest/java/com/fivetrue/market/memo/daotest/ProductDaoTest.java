package com.fivetrue.market.memo.daotest;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.fivetrue.market.memo.data.database.MemoDatabase;
import com.fivetrue.market.memo.data.database.dao.ProductDao;
import com.fivetrue.market.memo.model.entity.ProductEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 * Created by kwonojin on 2017. 11. 16..
 */

@RunWith(AndroidJUnit4.class)
public class ProductDaoTest {

    private static final String TAG = "ProductDaoTest";

    Context context;
    ProductDao mProductDao;


    @Before
    public void createDb() {
        context = InstrumentationRegistry.getTargetContext();
        mProductDao = MemoDatabase.getInstance(context).productDao();
    }

    @Test
    public void insertAndRead() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        mProductDao.deleteAll();
        mProductDao.insert(makeProduct("Apple", "Apple.co.kr"));
        LiveData<List<ProductEntity>> liveProductList = mProductDao.findAllByLiveData();
        liveProductList.observeForever(productEntities -> {
            Log.d(TAG, "IN Observe!!!! CHANGE PRODUCT" + productEntities);
        });

        Log.d(TAG, "ALL PRODUCT" + liveProductList);
        mProductDao.insert(makeProduct("Windows", "Microsoft.co.kr"));
        latch.await(1, TimeUnit.SECONDS);
        mProductDao.insert(makeProduct("Mouse", "Mickey"));
        latch.await(1, TimeUnit.SECONDS);
    }



    @After
    public void closeDb(){
        MemoDatabase.getInstance(context).close();
    }

    public ProductEntity makeProduct(String name, String store){
        Log.d(TAG, "makeProduct() called with: name = [" + name + "], store = [" + store + "]");
        ProductEntity product = new ProductEntity();
        product.setCheckInDate(0);
        product.setCheckOutDate(0);
        product.setName(name);
        product.setStoreName(store);
        return product;
    }
}

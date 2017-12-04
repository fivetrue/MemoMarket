package com.fivetrue.market.memo.data.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.fivetrue.market.memo.model.entity.ProductEntity;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;


/**
 * Created by kwonojin on 2017. 11. 16..
 */

@Dao
public interface ProductDao{

    @Insert(onConflict = IGNORE)
    void insert(ProductEntity data);

    @Insert(onConflict = IGNORE)
    void insertAll(List<ProductEntity> data);


    @Query("SELECT * From ProductEntity")
    LiveData<List<ProductEntity>> findAllByLiveData();


    @Query("SELECT * From ProductEntity")
    List<ProductEntity> findAll();

    @Query("SELECT count(*) From ProductEntity")
    int getCount();

    @Update(onConflict = REPLACE)
    void update(ProductEntity data);

    @Update(onConflict = REPLACE)
    void updateAll(List<ProductEntity> data);


    @Query("DELETE FROM ProductEntity")
    void deleteAll();

    @Delete
    void deleteProducts(ProductEntity... entities);

    @Query("SELECT * From ProductEntity where name LIKE :search")
    List<ProductEntity> findProductByName(String search);

}

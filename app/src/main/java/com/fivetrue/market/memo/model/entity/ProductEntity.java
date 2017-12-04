package com.fivetrue.market.memo.model.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.fivetrue.market.memo.model.Product;

/**
 * Created by kwonojin on 2017. 11. 16..
 */

@Entity(indices = {@Index(value = {"name"})})
public class ProductEntity implements Product{

    @NonNull
    private @PrimaryKey(autoGenerate = true) long id;
    private String name;
    private long price;
    private String storeName;
    private String imageUrl;
    private long checkInDate;
    private long checkOutDate;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    @Override
    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public long getCheckInDate() {
        return checkInDate;
    }

    @Override
    public void setCheckInDate(long checkInDate) {
        this.checkInDate = checkInDate;
    }

    @Override
    public long getCheckOutDate() {
        return checkOutDate;
    }

    @Override
    public void setCheckOutDate(long checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    @Override
    public String toString() {
        return "ProductEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", storeName='" + storeName + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                '}';
    }
}

package com.fivetrue.market.memo.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.database.product.ProductDB;
import com.fivetrue.market.memo.model.vo.Product;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by kwonojin on 2017. 4. 13..
 */

public class HomeRemoteViewsFactory extends AbsRemoteViewsFactory<Product> {

    private static final String TAG = "HomeRemoteViewsFactory";

    public HomeRemoteViewsFactory(Context context, Intent intent) {
        super(context, intent);
    }

    @Override
    protected List<Product> makeListData() {
        if(LL.D) Log.d(TAG, "makeListData() called");
        return Observable.fromIterable(ProductDB.getInstance().getProducts())
                .filter(product -> product.getCheckOutDate() == 0)
                .toList().blockingGet();
    }

    @Override
    protected void onView(RemoteViews remoteViews, Product data) {
        Log.d(TAG, "onView() called with: remoteViews = [" + remoteViews + "], data = [" + data + "]");
        Glide.with(getContext()).load(data.getImageUrl()).asBitmap()
                .dontTransform()
                .dontAnimate().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                remoteViews.setImageViewBitmap(R.id.iv_item_homescreen_widget_list, resource);
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                remoteViews.setImageViewResource(R.id.iv_item_homescreen_widget_list, R.drawable.ic_product_dark_gary_50dp);
            }
        });
        remoteViews.setTextViewText(R.id.tv_item_homescreen_widget_list, data.getName());
    }

    @Override
    protected int getListRowLayoutId() {
        return R.layout.item_homescreen_widget_list;
    }
}

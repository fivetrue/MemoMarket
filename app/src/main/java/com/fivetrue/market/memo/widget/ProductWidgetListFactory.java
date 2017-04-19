package com.fivetrue.market.memo.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.database.product.ProductDB;
import com.fivetrue.market.memo.preference.DefaultPreferenceUtil;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by kwonojin on 2017. 4. 13..
 */

public class ProductWidgetListFactory extends AbsRemoteViewsFactory<ProductDB.ShareableProduct> {

    private static final String TAG = "ProductWidgetListFactor";

    private Context mContext;
    private Map<String, Bitmap> mImageMap = new HashMap<>();

    public ProductWidgetListFactory(Context context, Intent intent) {
        super(context, intent);
        mContext = context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(LL.D) Log.d(TAG, "onCreate() called");
        setData(DefaultPreferenceUtil.getShareableProducts(mContext));
    }

    @Override
    protected void onView(RemoteViews remoteViews, ProductDB.ShareableProduct data, int position) {
        if(LL.D)
            Log.d(TAG, "onView() called with: remoteViews = [" + remoteViews + "], data = [" + data + "]");
        Bitmap bm = mImageMap.get(data.getImageUrl());
        remoteViews.setImageViewResource(R.id.iv_item_homescreen_widget_list, R.drawable.ic_product_gray_50dp);
        if(bm == null){
            new Handler(mContext.getMainLooper()).post(() -> {
                Glide.with(mContext).load(data.getImageUrl()).asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                if(LL.D) Log.d(TAG, "onResourceReady: ");
                                mImageMap.put(data.getImageUrl(), resource);
                                ProductWidgetListFactory.this.onDataSetChanged();
                            }
                        });
            });
        }else{
            if(!bm.isRecycled()){
                remoteViews.setImageViewBitmap(R.id.iv_item_homescreen_widget_list, bm);
            }
        }
        remoteViews.setTextViewText(R.id.tv_item_homescreen_widget_list, data.getName());
        Intent fillInIntent = new Intent();
        fillInIntent.putExtra(HomeScreenWidget.KEY_LIST_POSITION, position);
        remoteViews.setOnClickFillInIntent(R.id.tv_item_homescreen_widget_list, fillInIntent);
    }

    @Override
    protected int getListRowLayoutId() {
        return R.layout.item_homescreen_widget_list;
    }

    @Override
    public void onDataSetChanged() {
        super.onDataSetChanged();
        setData(DefaultPreferenceUtil.getShareableProducts(mContext));
    }
}

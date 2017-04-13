package com.fivetrue.market.memo.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.database.product.ProductDB;


/**
 * Created by kwonojin on 2017. 4. 13..
 */

public class ProductWidgetListFactory extends AbsRemoteViewsFactory<ProductDB.ShareableProduct> {

    private static final String TAG = "ProductWidgetListFactor";

    public ProductWidgetListFactory(Context context, Intent intent) {
        super(context, intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(LL.D) Log.d(TAG, "onCreate() called");
        setData(ProductDB.getInstance().getShareableProducts());
    }

    @Override
    protected void onView(RemoteViews remoteViews, ProductDB.ShareableProduct data, int position) {
        if(LL.D)
            Log.d(TAG, "onView() called with: remoteViews = [" + remoteViews + "], data = [" + data + "]");
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
        setData(ProductDB.getInstance().getShareableProducts());
    }
}

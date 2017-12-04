package com.fivetrue.market.memo.widget;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.data.database.product.ProductDB;
import com.fivetrue.market.memo.preference.DefaultPreferenceUtil;

/**
 * Created by kwonojin on 2017. 4. 13..
 */

public class ProductWidgetListFactory extends AbsRemoteViewsFactory<ProductDB.ShareableProduct> {

    private static final String TAG = "ProductWidgetListFactor";

    private Context mContext;

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

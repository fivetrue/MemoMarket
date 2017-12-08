package com.fivetrue.market.memo.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.model.entity.ProductEntity;
import com.fivetrue.market.memo.persistence.DataRepository;

import java.util.List;


/**
 * Created by kwonojin on 2017. 4. 13..
 */

public class ProductWidgetListFactory extends AbsRemoteViewsFactory<ProductEntity> {

    private static final String TAG = "ProductWidgetListFactor";


    public ProductWidgetListFactory(Context context, Intent intent) {
        super(context, intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        onDataSetChanged();
    }

    @Override
    protected void onView(RemoteViews remoteViews, ProductEntity data, int pos) {
        remoteViews.setTextViewText(R.id.tv_item_homescreen_widget_list, data.getName());
        Intent fillInIntent = new Intent();
        fillInIntent.putExtra(HomeScreenWidget.KEY_ITEM_ID, data.getId());
        fillInIntent.putExtra(HomeScreenWidget.KEY_PRODUCT, data);
        remoteViews.setOnClickFillInIntent(R.id.tv_item_homescreen_widget_list, fillInIntent);
    }


    @Override
    protected int getListRowLayoutId() {
        return R.layout.item_homescreen_widget_list;
    }

    @Override
    public void onDataSetChanged() {
        super.onDataSetChanged();
        DataRepository.getInstance(getContext()).getAppExecutors().diskIO().execute(() ->
                setData(DataRepository.getInstance(getContext()).findAllInboxProduct())
        );
    }
}

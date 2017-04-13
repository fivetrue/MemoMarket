package com.fivetrue.market.memo.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.fivetrue.market.memo.LL;

import java.util.List;

/**
 * Created by kwonojin on 2017. 4. 13..
 */

public abstract class AbsRemoteViewsFactory<T> implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = "AbsRemoteViewsFactory";

    private Context mContext;

    private List<T> mData;
    private int mWidgetId;

    public AbsRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        mData = makeListData();
    }

    abstract protected List<T> makeListData();

    abstract protected void onView(RemoteViews remoteViews, T data);

    protected int getListRowLayoutId(){
        throw new IllegalStateException("Must be implements getListRowLayoutId");
    }

    protected Context getContext(){
        return mContext;
    }

    @Override
    public int getCount() {
        if(LL.D) Log.d(TAG, "getCount() :" + mData.size());
        return mData.size();
    }

    @Override
    public long getItemId(int position) {
        if(LL.D) Log.d(TAG, "getItemId() position = [" + position + "]");
        return position;
    }

    public T getItem(int pos){
        return mData.get(pos);
    }
    /*
    *Similar to getView of Adapter where instead of View
    *we return RemoteViews
    *
    */
    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteView = new RemoteViews(
                mContext.getPackageName(), getListRowLayoutId());
        T item = getItem(position);
        new Handler(mContext.getMainLooper()).post(() -> onView(remoteView, item));
        return remoteView;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

package com.fivetrue.market.memo.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
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

    private Context context;

    private List<T> data;
    private int widgetId;

    public AbsRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
        widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    abstract protected void onView(RemoteViews remoteViews, T data, int pos);

    protected int getListRowLayoutId(){
        throw new IllegalStateException("Must be implements getListRowLayoutId");
    }

    protected Context getContext(){
        return context;
    }

    @Override
    public int getCount() {
        if(data != null){
            if(LL.D) Log.d(TAG, "getCount() :" + data.size());
            return data.size();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        if(LL.D) Log.d(TAG, "getItemId() position = [" + position + "]");
        return position;
    }

    public T getItem(int pos){
        if(data != null){
            return data.get(pos);
        }
        return null;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if(LL.D) Log.d(TAG, "getViewAt() called with: position = [" + position + "]");
        T item = getItem(position);
        RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), getListRowLayoutId());
        onView(remoteView, item, position);
        return remoteView;
    }

    @Override
    public void onCreate() {
        if(LL.D) Log.d(TAG, "onCreate() called");

    }

    @Override
    public void onDataSetChanged(){
        if(LL.D) Log.d(TAG, "onDataSetChanged() called");

    }

    @Override
    public void onDestroy() {
        if(LL.D) Log.d(TAG, "onDestroy() called");

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

    public void setData(List<T> data) {
        if(LL.D) Log.d(TAG, "setData() called with: data = [" + data + "]");
        this.data = data;
    }
}

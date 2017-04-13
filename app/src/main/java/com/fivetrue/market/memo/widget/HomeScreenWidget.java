package com.fivetrue.market.memo.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.ui.ProductAddActivity;
import com.fivetrue.market.memo.ui.SplashActivity;
import com.fivetrue.market.memo.utils.TrackingUtil;


/**
 * Created by kwonojin on 2017. 4. 12..
 */

public class HomeScreenWidget extends AppWidgetProvider {

    private static final String TAG = "HomeScreenWidget";

    private static final String ACTION_APP_START = "com.fivetrue.market.memo.widget.app.start";
    private static final String ACTION_ADD_PRODUCT = "com.fivetrue.market.memo.widget.product.add";
    private static final String ACTION_WIDGET_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        for (int widgetId : appWidgetIds) {
            if(LL.D) Log.d(TAG, "onUpdate: widget id" + widgetId);

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.layout_homescreen_widget);

            Intent addIntent = new Intent(context, HomeScreenWidget.class);
            addIntent.setAction(ACTION_ADD_PRODUCT);
            PendingIntent addPendingIntent = PendingIntent.getBroadcast(context,
                    0, addIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.tv_home_widget_add, addPendingIntent);

            Intent startIntent = new Intent(context, HomeScreenWidget.class);
            startIntent.setAction(ACTION_APP_START);
            PendingIntent startPendingIntent = PendingIntent.getBroadcast(context,
                    0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.tv_home_widget_product, startPendingIntent);

            Intent listIntent = new Intent(context, HomeScreenWidgetService.class);
            listIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            listIntent.setData(Uri.parse(listIntent.toUri(Intent.URI_INTENT_SCHEME)));
            remoteViews.setRemoteAdapter(R.id.lv_home_widget, listIntent);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(LL.D)
            Log.d(TAG, "onReceive() called with: context = [" + context + "], intent = [" + intent + "]");
        if(intent != null){
            String action = intent.getAction();
            if(!TextUtils.isEmpty(action)){
                if(ACTION_ADD_PRODUCT.equalsIgnoreCase(action)){
                    onClickAddProduct(context, intent);
                }else if(ACTION_APP_START.equalsIgnoreCase(action)){
                    onStartApp(context, intent);
                }else{
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    onUpdate(context, appWidgetManager
                            , appWidgetManager
                                    .getAppWidgetIds(new ComponentName(context, HomeScreenWidget.class)));
                }
            }
        }
    }

    private void onClickAddProduct(Context context, Intent intent){
        if(LL.D)
            Log.d(TAG, "onClickAddProduct() called with: context = [" + context + "], intent = [" + intent + "]");
        ProductAddActivity.startProductAdd(context, TAG);
    }

    private void onStartApp(Context context, Intent intent){
        if(LL.D)
            Log.d(TAG, "onStartApp() called with: context = [" + context + "], intent = [" + intent + "]");
        TrackingUtil.getInstance().startProductAdd(TAG);
        Intent appIntent = new Intent(context, SplashActivity.class);
        appIntent.setAction(ACTION_APP_START);
        appIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(appIntent);

    }
}

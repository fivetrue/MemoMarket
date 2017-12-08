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
import com.fivetrue.market.memo.model.entity.ProductEntity;
import com.fivetrue.market.memo.persistence.DataRepository;
import com.fivetrue.market.memo.ui.ProductAddActivity;
import com.fivetrue.market.memo.ui.SplashActivity;
import com.fivetrue.market.memo.utils.TrackingUtil;



/**
 * Created by kwonojin on 2017. 4. 12..
 */

public class HomeScreenWidget extends AppWidgetProvider {

    private static final String TAG = "HomeScreenWidget";

    public static final String ACTION_APP_START = "com.fivetrue.market.memo.widget.app.start";
    public static final String ACTION_ADD_PRODUCT = "com.fivetrue.market.memo.widget.product.add";
    public static final String ACTION_CHECKOUT_PRODUCT = "com.fivetrue.market.memo.widget.product.checkout";
    public static final String ACTION_WIDGET_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE";

    public static final String KEY_ITEM_ID = "item_id";
    public static final String KEY_PRODUCT = "product";

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

            /**
             * List
             */
            Intent listIntent = new Intent(context, HomeScreenWidgetService.class);
            listIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            listIntent.setData(Uri.parse(listIntent.toUri(Intent.URI_INTENT_SCHEME)));
            remoteViews.setRemoteAdapter(R.id.lv_home_widget, listIntent);
            Intent clickIntent = new Intent(context, HomeScreenWidget.class);
            clickIntent.setAction(ACTION_CHECKOUT_PRODUCT);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, 0);
            remoteViews.setPendingIntentTemplate(R.id.lv_home_widget, pendingIntent);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv_home_widget);
            appWidgetManager.updateAppWidget( new ComponentName( context, getClass()),
                    remoteViews);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass())));
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
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass())));
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
                }else if(ACTION_CHECKOUT_PRODUCT.equalsIgnoreCase(action)){
//                    onClickCheckoutProduct(context, intent);
                    onStartApp(context, intent);
                }else if(ACTION_APP_START.equalsIgnoreCase(action)){
                    onStartApp(context, intent);
                }else {
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass())));
                }
            }
        }
    }

    private void onClickAddProduct(Context context, Intent intent){
        if(LL.D)
            Log.d(TAG, "onClickAddProduct() called with: context = [" + context + "], intent = [" + intent + "]");
        Intent activityIntent = ProductAddActivity.makeIntent(context, TAG);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(activityIntent);
    }

    private void onClickCheckoutProduct(Context context, Intent intent){
        if(LL.D)
            Log.d(TAG, "onClickAddProduct() called with: context = [" + context + "], intent = [" + intent + "]");
        long id = intent.getLongExtra(KEY_ITEM_ID, -1);
        ProductEntity product = intent.getParcelableExtra(KEY_PRODUCT);
        if(id > 0){
            if(product != null){
                long mills = System.currentTimeMillis();
                product.setCheckOutDate(mills);
                DataRepository.getInstance(context).updateProduct(product).subscribe();
            }
        }
    }


    private void onStartApp(Context context, Intent intent){
        if(LL.D)
            Log.d(TAG, "onStartApp() called with: context = [" + context + "], intent = [" + intent + "]");
        TrackingUtil.getInstance().startProductAdd(TAG);
        Intent appIntent = new Intent(context, SplashActivity.class);
        appIntent.setAction(ACTION_APP_START);
        appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(appIntent);

    }
}

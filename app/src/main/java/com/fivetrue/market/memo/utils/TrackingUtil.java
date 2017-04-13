package com.fivetrue.market.memo.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;

/**
 * Created by kwonojin on 2017. 4. 12..
 */

public class TrackingUtil {

    private static final String TAG = "TrackingUtil";

    private Context mContext;

    private static TrackingUtil sInstance;

    public static void init(Context context){
        sInstance = new TrackingUtil(context.getApplicationContext());
    }

    public static TrackingUtil getInstance(){
        return sInstance;
    }

    private TrackingUtil(Context context){
        mContext = context;
        FirebaseApp.initializeApp(mContext);
    }

    public void log(String tag, String message, Throwable t){
        FirebaseCrash.log(tag + "::" + message + "::" + t.getLocalizedMessage());
    }

    public void report(Throwable t){
        FirebaseCrash.report(t);
    }

    /**
     * to Analytics
     * @param event
     * @param b
     */
    public void logEvent(String event, Bundle b){
        FirebaseAnalytics.getInstance(mContext).logEvent(event, b);
    }

    /**
     * to Anlytics
     * @param a
     * @param screenName
     * @param screenClsOverride
     */
    public void currentScreen(Activity a, String screenName, String screenClsOverride){
        FirebaseAnalytics.getInstance(mContext).setCurrentScreen(a, screenName, screenClsOverride);
    }

    public void exportEventLog(String fileName, int count){
        Bundle b = new Bundle();
        b.putString("FileName", fileName);
        b.putString("ItemCount", count+"");
        FirebaseAnalytics.getInstance(mContext).logEvent("ExportProducts", b);
    }

    public void resetDataEventLog(int productCount){
        Bundle b = new Bundle();
        b.putString("ItemCount", productCount+"");
        FirebaseAnalytics.getInstance(mContext).logEvent("ResetData", b);
    }

    public void deleteProduct(String name, String where){
        Bundle b = new Bundle();
        b.putString("Name", name);
        b.putString("Where", where);
        FirebaseAnalytics.getInstance(mContext).logEvent("DeleteProduct", b);
    }

    public void startProductAdd(String where){
        Bundle b = new Bundle();
        b.putString("Where", where);
        FirebaseAnalytics.getInstance(mContext).logEvent("StartProductAdd", b);
    }

    public void addProduct(String name, String barcode, long price, String store){
        Bundle b = new Bundle();
        b.putString("Name", name);
        b.putString("Barcode", barcode);
        b.putString("Price", price + "");
        b.putString("Store", store);
        FirebaseAnalytics.getInstance(mContext).logEvent("AddProduct", b);
    }

    public void startProductCheckout(String where){
        Bundle b = new Bundle();
        b.putString("Where", where);
        FirebaseAnalytics.getInstance(mContext).logEvent("StartProductCheckout", b);
    }

    public void checkoutProduct(String name, String barcode, long price, String store){
        Bundle b = new Bundle();
        b.putString("Name", name);
        b.putString("Barcode", barcode);
        b.putString("Price", price + "");
        b.putString("Store", store);
        FirebaseAnalytics.getInstance(mContext).logEvent("CheckoutProduct", b);
    }

    public void scanBarcode(String barcode){
        Bundle b = new Bundle();
        b.putString("barcode", barcode);
        FirebaseAnalytics.getInstance(mContext).logEvent("ScanBarcode", b);
    }

    public void startApp(String where){
        Bundle b = new Bundle();
        b.putString("Where", where);
        FirebaseAnalytics.getInstance(mContext).logEvent("StartAPP", b);
    }
}

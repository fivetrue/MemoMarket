package com.fivetrue.market.memo.preference;

import android.content.Context;
import android.preference.PreferenceManager;

import com.fivetrue.market.memo.persistence.database.product.ProductDB;
import com.fivetrue.market.memo.model.remote.ConfigData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by kwonojin on 2017. 3. 6..
 */

public class DefaultPreferenceUtil {

    private static final String TAG = "DefaultPreferenceUtil";

    private static final String KEY_NEW_PRODUCT_PERIOD = "new_product_period";
    private static final String KEY_CONFIG_DATAE = "config_data";
    private static final String KEY_SHAREABLE_PRODUCTS = "shareable_products";


    private static final long DEFAULT_NEW_PRODUCT_PERIOD = 1000 * 60 * 60 * 24 * 3;

    public static void setNewProductPeriod(Context context, long period){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putLong(KEY_NEW_PRODUCT_PERIOD, period).commit();
    }

    public static long getNewProductPeriod(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(KEY_NEW_PRODUCT_PERIOD, DEFAULT_NEW_PRODUCT_PERIOD);
    }

    public static void saveConfigData(Context context, ConfigData configData){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putString(KEY_CONFIG_DATAE, new Gson().toJson(configData)).commit();
    }

    public static ConfigData getConfigData(Context context){
        String data = PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_CONFIG_DATAE, null);
        if(data != null){
            return new Gson().fromJson(data, ConfigData.class);
        }
        return null;
    }

    public static void saveShareableProducts(Context context, List<ProductDB.ShareableProduct> products){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putString(KEY_SHAREABLE_PRODUCTS, new Gson().toJson(products)).commit();
    }

    public static List<ProductDB.ShareableProduct> getShareableProducts(Context context){
        String data = PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_SHAREABLE_PRODUCTS, null);
        if(data != null){
            return new Gson().fromJson(data, new TypeToken<List<ProductDB.ShareableProduct>>(){}.getType());
        }
        return null;
    }
}

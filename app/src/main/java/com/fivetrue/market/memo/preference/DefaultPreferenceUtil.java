package com.fivetrue.market.memo.preference;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by kwonojin on 2017. 3. 6..
 */

public class DefaultPreferenceUtil {

    private static final String TAG = "DefaultPreferenceUtil";

    private static final String KEY_NEW_PRODUCT_PERIOD = "new_product_period";


    private static final long DEFAULT_NEW_PRODUCT_PERIOD = 1000 * 60 * 60 * 24 * 3;

    public static void setNewProductPeriod(Context context, long period){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putLong(KEY_NEW_PRODUCT_PERIOD, period).commit();
    }

    public static long getNewProductPeriod(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(KEY_NEW_PRODUCT_PERIOD, DEFAULT_NEW_PRODUCT_PERIOD);
    }
}

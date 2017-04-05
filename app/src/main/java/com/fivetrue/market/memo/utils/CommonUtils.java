package com.fivetrue.market.memo.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.text.NumberFormat;

/**
 * Created by kwonojin on 2017. 3. 26..
 */

public class CommonUtils {

    private static final String TAG = "CommonUtils";


    public static Typeface getFont(Context context, String font){
        return Typeface.createFromAsset(context.getAssets(),
                font);
    }

    public static String convertToCurrency(long value){
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        return formatter.format(value);
    }
}

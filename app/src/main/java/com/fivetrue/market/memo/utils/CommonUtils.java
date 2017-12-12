package com.fivetrue.market.memo.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Log;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    public static String getDate(Context context, String pattern, long millis){
        Locale locale = context.getResources().getConfiguration().locale;
        String formatted = DateFormat.getBestDateTimePattern(locale, pattern);
        SimpleDateFormat sdf = new SimpleDateFormat(formatted);
        return sdf.format(new Date(millis));
    }

    public static String getVersionName(Context context){
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(
                    context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getVersionName", e);
        }
        return info != null ? info.versionName : "";
    }

    public static boolean hasAppPackage(Context context, String packageName){
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if(packageInfo.packageName.equals(packageName)) return true;
        }
        return false;
    }

    public static boolean isEnabledAppPackage(Context context, String packageName){
        ApplicationInfo ai = null;
        try {
            ai = context.getPackageManager().getApplicationInfo(packageName,0);
        } catch (PackageManager.NameNotFoundException e) {
        }
        return ai != null && ai.enabled;
    }

    public static void goStore(Context context){
        final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
}

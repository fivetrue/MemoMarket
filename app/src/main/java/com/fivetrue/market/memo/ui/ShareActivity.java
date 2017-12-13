package com.fivetrue.market.memo.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.R;

public class ShareActivity extends BaseActivity {

    private static final String TAG = "ShareActivity";

    private static final int REQUEST_DEFAULT_PERMISSIONS = 0x33;
    private static final int REQUEST_ACCOUNT_CHOOSE = 0x44;
    private static final int REQUEST_GOOGLE_AUTH_LOGIN = 0x55;

    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkPermission();
        }else{
            checkIntent(getIntent());
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission(){
        // Here, thisActivity is the current activity
        boolean dontReadStorage = (ContextCompat.checkSelfPermission(this , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED);
        boolean dontWriteStorage = (ContextCompat.checkSelfPermission(this , Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED);
        if (dontReadStorage || dontWriteStorage) {
            Log.i(TAG, "checkPermission() called with: " + " trying check permission");
            // Should we show an explanation?
            if ((ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    || (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                Log.i(TAG, "checkPermission: has Permission OK");
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                checkIntent(getIntent());

            } else {
                // No explanation needed, we can request the permission.
                Log.i(TAG, "checkPermission: has no Permission = " + PERMISSIONS.toString());
                ActivityCompat.requestPermissions(this,
                        PERMISSIONS,
                        REQUEST_DEFAULT_PERMISSIONS);

                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else{
            Log.i(TAG, "checkPermission() called with: " + " check permission OK");
            checkIntent(getIntent());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult() called with: " + "requestCode = [" + requestCode + "], permissions = [" + permissions + "], grantResults = [" + grantResults + "]");
        switch (requestCode) {
            case REQUEST_DEFAULT_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length == PERMISSIONS.length
                        && (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        && (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    checkIntent(getIntent());
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(android.R.string.dialog_alert_title)
                            .setMessage(R.string.permission_denied_alert_message)
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                                dialog.dismiss();
                                finish();
                            }).setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                        dialog.dismiss();
                        Log.i(TAG, "checkPermission: has no Permission = " + PERMISSIONS.toString());
                        ActivityCompat.requestPermissions(ShareActivity.this,
                                PERMISSIONS,
                                REQUEST_DEFAULT_PERMISSIONS);
                    }).setOnCancelListener(dialog -> {
                        dialog.dismiss();
                        finish();
                    }).setCancelable(false)
                            .show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "onNewIntent: ");
        checkIntent(intent);
    }

    private void checkIntent(Intent intent){
        if(LL.D) Log.d(TAG, "checkIntent() called with: intent = [" + intent + "]");
        checkConfig(intent);
    }

    public void checkConfig(Intent intent){
//        NetworkData.getInstance(this).getConfig()
//                .subscribe(configData -> startApplication(configData), throwable -> {
//            if(LL.D) Log.d(TAG, "call() called with: throwable = [" + throwable + "]");
//            Toast.makeText(SplashActivity.this,throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//
//        });
        startApplication(intent);
    }

    public void startApplication(Intent intent){
        Log.d(TAG, "startApplication() called");
        Intent activity = new Intent(intent);
        activity.setClass(ShareActivity.this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(activity);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ACCOUNT_CHOOSE){
            if(resultCode == RESULT_OK){

            }
        }
        checkIntent(getIntent());
    }
}

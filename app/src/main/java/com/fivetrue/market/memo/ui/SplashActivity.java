package com.fivetrue.market.memo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.model.dto.ConfigData;
import com.fivetrue.market.memo.utils.DataManager;

import io.reactivex.functions.Consumer;


/**
 * Created by kwonojin on 2017. 1. 23..
 */

public class SplashActivity extends BaseActivity {

    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkConfig();
    }

    public void checkConfig(){
        DataManager.getInstance(this).getConfig().subscribe(new Consumer<ConfigData>() {
            @Override
            public void accept(ConfigData configData) throws Exception {
                startApplication(configData);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                if(LL.D) Log.d(TAG, "call() called with: throwable = [" + throwable + "]");
                Toast.makeText(SplashActivity.this,throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    public void startApplication(ConfigData config){
        if(LL.D) Log.d(TAG, "startApplication() called with: config = [" + config + "]");
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }
}

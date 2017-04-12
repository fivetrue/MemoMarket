package com.fivetrue.market.memo.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.fivetrue.market.memo.BuildConfig;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.database.product.ProductDB;
import com.fivetrue.market.memo.utils.CommonUtils;
import com.fivetrue.market.memo.utils.ExportUtil;


import io.reactivex.Observable;

/**
 * Created by kwonojin on 2017. 1. 23..
 */

public class SettingsActivity extends BaseActivity {

    private static final String TAG = "SettingsActivity";

    private TextView mTitle;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initView();
    }

    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitle = (TextView) findViewById(R.id.tv_settings_title);
        mTitle.setTypeface(CommonUtils.getFont(this,  "font/DroidSansMono.ttf"));
        mTitle.setText(R.string.settings);

        mNavigationView = (NavigationView) findViewById(R.id.navi_setting);
        View header = mNavigationView.getHeaderView(0);
        TextView version = (TextView) header.findViewById(R.id.tv_settings_version);
        version.setText(BuildConfig.VERSION_NAME);

        mNavigationView.setNavigationItemSelectedListener(item -> {
            if(item != null){
                switch (item.getItemId()){
                    case R.id.menu_setting_reset_data :{
                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                        builder.setTitle(android.R.string.dialog_alert_title)
                                .setMessage(R.string.reset_data_message)
                                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                                    ProductDB.get().executeTransaction(realm -> {
                                        ProductDB.get().deleteAll();
                                        dialogInterface.dismiss();
                                    });
                                }).setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        }).show();
                    }
                        return true;

                    case R.id.menu_setting_export_data :{
                        ExportUtil.export(SettingsActivity.this
                                , "ALL_" + ExportUtil.getDate(System.currentTimeMillis())
                                ,  Observable.fromIterable(ProductDB.getInstance().getProducts())
                                        .filter(product -> product.getCheckOutDate() > 0)
                                        .toList().blockingGet());
                    }
                        return true;
                }
            }
            return false;
        });
    }



}

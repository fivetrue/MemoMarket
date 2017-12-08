package com.fivetrue.market.memo.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.fivetrue.market.memo.BuildConfig;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.persistence.database.product.ProductDB;
import com.fivetrue.market.memo.utils.CommonUtils;
import com.fivetrue.market.memo.utils.ExportUtil;
import com.fivetrue.market.memo.utils.TrackingUtil;
import com.fivetrue.market.memo.viewmodel.ProductListViewModel;


import io.reactivex.Observable;

/**
 * Created by kwonojin on 2017. 1. 23..
 */

public class SettingsActivity extends BaseActivity {

    private static final String TAG = "SettingsActivity";

    private TextView mTitle;
    private NavigationView mNavigationView;

    private ProductListViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mViewModel = ViewModelProviders.of(this).get(ProductListViewModel.class);
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
                                    TrackingUtil.getInstance().resetDataEventLog(ProductDB.getInstance().getProducts().size());
                                    ProductDB.get().executeTransaction(realm -> {
                                        ProductDB.get().deleteAll();
                                        dialogInterface.dismiss();
                                        ProductDB.getInstance().updatePublish();
                                    });
                                }).setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        }).show();
                    }
                        return true;

                    case R.id.menu_setting_export_data :{
                        mViewModel.getProductList().observe(this, products -> {
                            ExportUtil.export(SettingsActivity.this
                                , "ALL_" + ExportUtil.getDate(System.currentTimeMillis())
                                ,  Observable.fromIterable(products)
                                        .filter(product -> product.getCheckOutDate() > 0)
                                        .toList().blockingGet());
                        });
                    }
                        return true;
                }
            }
            return false;
        });
    }



}

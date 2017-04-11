package com.fivetrue.market.memo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.fivetrue.market.memo.BuildConfig;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.utils.CommonUtils;

/**
 * Created by kwonojin on 2017. 1. 23..
 */

public class SettingsActivity extends BaseActivity {

    private static final String TAG = "SettingsActivity";

    private TextView mTitle;

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

        TextView version = (TextView) findViewById(R.id.tv_settings_version);
        version.setText(BuildConfig.VERSION_NAME);
    }



}

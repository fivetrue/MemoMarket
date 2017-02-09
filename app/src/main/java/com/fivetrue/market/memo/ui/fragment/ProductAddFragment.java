package com.fivetrue.market.memo.ui.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.model.Store;
import com.fivetrue.market.memo.view.PagerTabContent;

/**
 * Created by kwonojin on 2017. 1. 24..
 */

public class ProductAddFragment extends BaseFragment{

    private static final String KEY_STORE_NAME = "store_name";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_store_product, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public String getTitle(Context context) {
        if(getArguments() != null && getArguments().getString(KEY_STORE_NAME) != null){
            return getArguments().getString(KEY_STORE_NAME);
        }
        return "";
    }

    @Override
    public int getImageResource() {
        return 0;
    }

    public static Bundle makeArgument(Store store){
        Bundle b = new Bundle();
        b.putString(KEY_STORE_NAME, store.getName());
        return b;
    }
}

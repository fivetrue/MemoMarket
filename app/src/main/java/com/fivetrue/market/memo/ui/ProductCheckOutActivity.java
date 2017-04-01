package com.fivetrue.market.memo.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.database.RealmDB;
import com.fivetrue.market.memo.database.product.ProductDB;
import com.fivetrue.market.memo.model.dto.ConfigData;
import com.fivetrue.market.memo.model.dto.ProductData;
import com.fivetrue.market.memo.model.image.ImageEntry;
import com.fivetrue.market.memo.model.vo.Product;
import com.fivetrue.market.memo.ui.adapter.product.ProductCheckOutListAdapter;
import com.fivetrue.market.memo.ui.adapter.product.ProductNameListAdapter;
import com.fivetrue.market.memo.utils.CommonUtils;
import com.fivetrue.market.memo.utils.DataManager;
import com.fivetrue.market.memo.utils.SimpleViewUtils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

/**
 * Created by kwonojin on 2017. 3. 5..
 */

public class ProductCheckOutActivity extends BaseActivity{

    private static final String TAG = "ProductCheckOutActivity";

    public static final String KEY_CHECKOUT_DATE = "checkout_date";

    private RecyclerView mRecyclerView;
    private ProductCheckOutListAdapter mAdapter;

    private LinearLayoutManager mLayoutManager;

    private long mCheckOutMillis;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_checkout);
        initData();
        initView();
    }

    private void initData(){
        mCheckOutMillis = getIntent().getLongExtra(KEY_CHECKOUT_DATE, 0);
        mAdapter = new ProductCheckOutListAdapter(Observable.fromIterable(ProductDB.getInstance().getProducts())
                .filter(product -> product.isCheckOut() && product.getCheckOutDate() == mCheckOutMillis)
                .toList().blockingGet());
    }

    private void initView(){
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_checkout_list);
        mRecyclerView.setEnabled(false);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }


    public static Intent makeIntent(Context context, List<Product> products, long ms){
        Intent intent = new Intent(context, ProductCheckOutActivity.class);
        intent.putExtra(KEY_CHECKOUT_DATE, ms);
        return intent;
    }
}

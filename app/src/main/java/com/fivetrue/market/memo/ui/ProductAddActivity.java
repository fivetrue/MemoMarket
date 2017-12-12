package com.fivetrue.market.memo.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.ListPopupWindow;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.databinding.ActivityProductAddBinding;
import com.fivetrue.market.memo.model.Product;
import com.fivetrue.market.memo.ui.adapter.list.ProductNameListAdapter;
import com.fivetrue.market.memo.utils.AdUtil;
import com.fivetrue.market.memo.utils.TrackingUtil;
import com.fivetrue.market.memo.viewmodel.ProductViewModel;

/**
 * Created by kwonojin on 2017. 3. 5..
 */

public class ProductAddActivity extends BaseActivity{

    private static final String TAG = "ProductAddActivity";

    private static final int MESSAGE_FIND_PRODUCT = 0x33;

    private ProductNameListAdapter mProductNameListAdapter;
    private InputMethodManager mImm;

    private ActivityProductAddBinding mBinding;
    private ProductViewModel mProductViewModel;

    private ListPopupWindow mPopup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_product_add);
        mProductViewModel = ViewModelProviders.of(this).get(ProductViewModel.class);
        mImm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        mPopup = new ListPopupWindow(ProductAddActivity.this);

        mBinding.name.setOnFocusChangeListener(onInputFocusChangeListener);
        mBinding.store.setOnFocusChangeListener(onInputFocusChangeListener);
        mBinding.price.setOnFocusChangeListener(onInputFocusChangeListener);
        mBinding.fabAdd.setOnClickListener(view -> {
            if(TextUtils.isEmpty(mBinding.name.getText())){
                Snackbar.make(mBinding.layoutInput, R.string.error_input_product_name_message, Snackbar.LENGTH_SHORT).show();
                return;
            }
            mImm.hideSoftInputFromInputMethod(mBinding.name.getWindowToken(), 0);
            mBinding.setShowAddProgress(true);

            mProductViewModel.insertProduct(mBinding.name.getText().toString()
                    , mBinding.store.getText().toString()
                    , mBinding.price.getText().toString()).subscribe(() -> finish(), throwable -> mBinding.setShowAddProgress(false));
        });

        mBinding.name.addTextChangedListener(new TextFindWatcher(){
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                mHandler.removeMessages(MESSAGE_FIND_PRODUCT);
                if(editable.length() > 0){
                    sendFindProductMessage(editable.toString());
                }
            }
        });
        mBinding.store.addTextChangedListener(new TextFindWatcher());
        mBinding.price.addTextChangedListener(new TextFindWatcher());
    }

    @Override
    protected void onStart() {
        super.onStart();
        AdUtil.getInstance().addAdView(mBinding.layoutAd, AdUtil.AD_PRODUCT_ADD, false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        AdUtil.getInstance().detachAdView(AdUtil.AD_PRODUCT_ADD);
    }


    private void sendFindProductMessage(String name){
        Message message = Message.obtain();
        message.what = MESSAGE_FIND_PRODUCT;
        message.obj = name;
        mHandler.sendMessageDelayed(message, 1000L);
    }


    public static Intent makeIntent(Context context, String where){
        Log.i(TAG, "startProductAdd: where : " + where);
        TrackingUtil.getInstance().startProductAdd(where);
        Intent intent = new Intent(context, ProductAddActivity.class);
        return intent;
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg != null){
                switch (msg.what){
                    case MESSAGE_FIND_PRODUCT :{
                        mBinding.setShowFindProgress(true);
                        mProductViewModel.findProduct((String) msg.obj)
                                .subscribe(products -> {
                                    mBinding.setShowFindProgress(false);
                                    if(LL.D) Log.d(TAG, "FIND PRODUCTS: " + products.size());
                                    if(products != null && !products.isEmpty()){
                                        for(Product p : products){
                                            if(p.getName().equals(mBinding.name.getText().toString())
                                                    && mBinding.name.hasFocus()){
                                                return;
                                            }
                                        }

                                        mPopup.setAnchorView(mBinding.name);
                                        mPopup.setOnItemClickListener((adapterView, view, i3, l) -> {
                                            mBinding.setProduct(mProductNameListAdapter.getItem(i3));
                                            mPopup.dismiss();

                                        });
                                        mProductNameListAdapter = new ProductNameListAdapter(ProductAddActivity.this, products);
                                        mPopup.setAdapter(mProductNameListAdapter);
                                        mProductNameListAdapter.setData(products);
                                        mPopup.show();
                                    }
                                });
                    }
                }
            }
        }
    };

    private View.OnFocusChangeListener onInputFocusChangeListener = (view, b) -> {
        if(b){
            ((EditText)view).selectAll();
        }
        if(mPopup != null && mPopup.isShowing()){
            mPopup.dismiss();
        }
    };

    private class TextFindWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(mPopup != null && mPopup.isShowing()){
                mPopup.dismiss();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}

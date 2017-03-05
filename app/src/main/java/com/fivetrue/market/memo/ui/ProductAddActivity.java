package com.fivetrue.market.memo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.ListPopupWindow;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.database.RealmDB;
import com.fivetrue.market.memo.model.dto.ConfigData;
import com.fivetrue.market.memo.model.dto.ProductData;
import com.fivetrue.market.memo.model.image.ImageEntry;
import com.fivetrue.market.memo.model.vo.Product;
import com.fivetrue.market.memo.ui.adapter.product.ProductNameListAdapter;
import com.fivetrue.market.memo.utils.DataManager;
import com.fivetrue.market.memo.utils.SimpleViewUtils;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

/**
 * Created by kwonojin on 2017. 3. 5..
 */

public class ProductAddActivity extends BaseActivity implements AdapterView.OnItemClickListener{

    private static final String TAG = "ProductAddActivity";

    private View mContainer;

    private View mLayoutInput;
    private ProgressBar mProgressRetrieving;
    private EditText mInput;
    private View mInputOk;

    private FloatingActionButton mFabOk;
    private ProgressBar mProgressDone;

    private ProductNameListAdapter mProductNameListAdapter;
    private ListPopupWindow mPopup;

    private FirebaseDataFinder mDataFinder;
    private InputMethodManager mImm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_add);
        initData();
        initView();
    }

    private void initData(){
        mDataFinder = new FirebaseDataFinder();
        mImm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }

    private void initView(){
        mContainer = findViewById(R.id.layout_product_add);

        mLayoutInput = findViewById(R.id.layout_product_add_input);
        mProgressRetrieving = (ProgressBar) findViewById(R.id.pb_product_add_retrieving);
        mInput = (EditText) findViewById(R.id.et_product_add);
        mInputOk = findViewById(R.id.iv_product_input_ok);

        mProgressDone = (ProgressBar) findViewById(R.id.pb_product_add_done);
        mFabOk = (FloatingActionButton) findViewById(R.id.fab_product_add);

        mInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    if(LL.D)
                        Log.d(TAG, "onEditorAction: done");
                    setInputText(mInput.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });
        mInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInput.selectAll();
            }
        });

        mInputOk.setEnabled(false);
        mInputOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInputText(mInput.getText().toString().trim());
            }
        });

        mInput.addTextChangedListener(mDataFinder);

        mFabOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findImage();
            }
        });
    }

    private void setInputText(String text){
        if(LL.D) Log.d(TAG, "setInputText() called with: text = [" + text + "]");
        if(mPopup != null){
            mPopup.dismiss();
        }
        if(mDataFinder != null && mImm != null){
            mImm.hideSoftInputFromWindow(mInput.getWindowToken(), 0);
            mDataFinder.selectedName = text;
        }
    }

    private void findImage(){
        if(mInput != null && mInput.getText() != null){
            final String text = mInput.getText().toString().trim();
            if(LL.D) Log.d(TAG, "findImage: text = " + text);
            SimpleViewUtils.showView(mProgressDone, View.VISIBLE);
            SimpleViewUtils.hideView(mFabOk, View.GONE);
            DataManager.getInstance(this).getConfig().subscribe(new Consumer<ConfigData>() {
                @Override
                public void accept(ConfigData configData) throws Exception {
                    DataManager.getInstance(ProductAddActivity.this).findImage(configData, text)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.newThread())
                            .subscribe(new Consumer<ImageEntry>() {
                        @Override
                        public void accept(ImageEntry imageEntry) throws Exception {
                            saveProduct(text, imageEntry);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.e(TAG, "accept: ", throwable);
                            saveProduct(text, null);
                        }
                    });
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    saveProduct(text, null);
                }
            });
        }
    }

    private void saveProduct(final String text, final ImageEntry imageEntry){
        if(LL.D)
            Log.d(TAG, "saveProduct() called with: text = [" + text + "], imageEntry = [" + imageEntry + "]");
        if(!TextUtils.isEmpty(text)){
            RealmDB.get().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    String imageUrl = null;
                    if(imageEntry != null
                            && imageEntry.getValue() != null && imageEntry.getValue().size() > 0){
                        imageUrl = imageEntry.getValue().get(0).thumbnailUrl;
                    }
                    Product product = new Product();
                    product.setName(text);
                    product.setCheckInDate(System.currentTimeMillis());
                    product.setImageUrl(imageUrl);
                    realm.insertOrUpdate(product);
                    finish();
                }
            });
        }else{
            SimpleViewUtils.showView(mProgressDone, View.INVISIBLE);
            SimpleViewUtils.hideView(mFabOk, View.VISIBLE);
            Snackbar.make(mLayoutInput, R.string.error_empty_product_name, Snackbar.LENGTH_SHORT)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mInput.findFocus();
                        }
                    }).show();
        }
    }

    private void setRetrievedProductList(List<ProductData> data){
        mProgressRetrieving.setVisibility(View.INVISIBLE);
        if(data != null && data.size() > 0){
            if(mProductNameListAdapter == null){
                mProductNameListAdapter = new ProductNameListAdapter(this, data);
            }else{
                mProductNameListAdapter.setData(data);
            }

            if(mPopup == null){
                mPopup = new ListPopupWindow(this);
                mPopup.setAdapter(mProductNameListAdapter);
                mPopup.setOnItemClickListener(this);
                mPopup.setAnchorView(mLayoutInput);
            }

            if(!mPopup.isShowing()){
                mPopup.show();
            }
        }else{
            if(mPopup != null && mPopup.isShowing()){
                mPopup.dismiss();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(mProductNameListAdapter != null && mProductNameListAdapter.getItemCount() > i){
            ProductData product = mProductNameListAdapter.getItem(i);
            if(LL.D) Log.d(TAG, "onItemClick: product = " + product.name);
            setInputText(product.name);
            mInput.setText(product.name);
        }
    }


    private class FirebaseDataFinder implements TextWatcher {

        public String selectedName;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (LL.D)
                Log.d(TAG, "onTextChanged: text = " + charSequence);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(LL.D) Log.d(TAG, "afterTextChanged() called with: editable = [" + editable + "]");
            if(mInputOk != null){
                final String text = editable.toString().trim();
                if(TextUtils.isEmpty(text)){
                    SimpleViewUtils.showView(mFabOk, View.INVISIBLE);
                    mInputOk.setEnabled(false);
                }else{
                    SimpleViewUtils.showView(mFabOk, View.VISIBLE);
                    mInputOk.setEnabled(true);
                }
                if(!TextUtils.isEmpty(text) && !(selectedName != null && selectedName.equals(text))){
                    mProgressRetrieving.setVisibility(View.VISIBLE);
                    DataManager.getInstance(ProductAddActivity.this).findProductName(text).subscribe(new Consumer<List<ProductData>>() {
                        @Override
                        public void accept(List<ProductData> storeDatas) throws Exception {
                            setRetrievedProductList(storeDatas);
                        }
                    });
                }else{
                    selectedName = null;
                }
            }
        }
    }
}

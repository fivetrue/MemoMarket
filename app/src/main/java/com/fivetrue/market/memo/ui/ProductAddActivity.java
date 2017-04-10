package com.fivetrue.market.memo.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ListPopupWindow;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.database.FirebaseDB;
import com.fivetrue.market.memo.database.RealmDB;
import com.fivetrue.market.memo.model.dto.ProductData;
import com.fivetrue.market.memo.model.image.ImageEntry;
import com.fivetrue.market.memo.model.vo.Product;
import com.fivetrue.market.memo.ui.adapter.product.ProductNameListAdapter;
import com.fivetrue.market.memo.utils.CommonUtils;
import com.fivetrue.market.memo.utils.DataManager;
import com.fivetrue.market.memo.utils.SimpleViewUtils;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by kwonojin on 2017. 3. 5..
 */

public class ProductAddActivity extends BaseActivity{

    private static final String TAG = "ProductAddActivity";

    private View mLayoutInput;
    private ProgressBar mProgressRetrieving;
    private EditText mInput;

    private TextView mBarcode;

    private FloatingActionButton mFabScan;
    private FloatingActionButton mFabOk;
    private ProgressBar mProgressDone;

    private ProductNameListAdapter mProductNameListAdapter;
    private ListPopupWindow mPopup;

    private FirebaseDataFinder mDataFinder;
    private InputMethodManager mImm;

    private ProductData mSelectedProductData;
    private String mScanBarcode;

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
        mLayoutInput = findViewById(R.id.layout_product_add_input);
        mProgressRetrieving = (ProgressBar) findViewById(R.id.pb_product_add_retrieving);
        mInput = (EditText) findViewById(R.id.et_product_add);

        mBarcode = (TextView) findViewById(R.id.tv_product_input_barcode);

        mProgressDone = (ProgressBar) findViewById(R.id.pb_product_add_done);
        mFabScan = (FloatingActionButton) findViewById(R.id.fab_product_scan);
        mFabOk = (FloatingActionButton) findViewById(R.id.fab_product_add);

        mInput.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == EditorInfo.IME_ACTION_DONE){
                if(LL.D)
                    Log.d(TAG, "onEditorAction: done");
                setInputText(mInput.getText().toString().trim());
                return true;
            }
            return false;
        });
        mInput.setOnClickListener(view -> mInput.selectAll());
        mInput.addTextChangedListener(mDataFinder);

        mFabOk.setOnClickListener(view ->{
            if(mInput.getText().length() > 0){
                setInputText(mInput.getText().toString().trim());
                findImage();
            }else{
                Snackbar.make(mLayoutInput, R.string.error_input_product_name_message, Snackbar.LENGTH_SHORT).show();
            }
        });
        mFabScan.setOnClickListener(view -> scan());

        ((TextView)findViewById(R.id.tv_fragment_product_add)).setTypeface(CommonUtils.getFont(this, "font/Magra.ttf"));
    }

    /**
     * Setting ProductData after When retrieving data from firebase.
     * @param data
     */
    private void setProductData(ProductData data){
        mSelectedProductData = data;
        mInput.setText(data.name);
        mBarcode.setText(data.barcode);
        setInputText(data.name);
    }

    /**
     * Setting barcode sku after getting sku from Barcode scanner.
     * @param barcode
     */
    private void setBarcode(String barcode){
        mScanBarcode = barcode;
        mBarcode.setText(barcode);
        mFabScan.setVisibility(View.GONE);
        FirebaseDB.getInstance(this).findBarcode(mScanBarcode)
                .subscribe(productData -> {
                    setProductData(productData);
                    findImage();
                }, throwable -> {
                    Snackbar.make(mLayoutInput, throwable.getMessage(), Snackbar.LENGTH_LONG).show();
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

    private void scan(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.scan_barcode)
                .setMessage(R.string.scan_barcode_message)
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                    IntentIntegrator integrator = new IntentIntegrator(this);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES);
                    integrator.setPrompt(getString(R.string.scan_barcode));
                    integrator.setCameraId(0);  // Use a specific camera of the device
                    integrator.setBeepEnabled(false);
                    integrator.setBarcodeImageEnabled(true);
                    integrator.initiateScan();
                }).setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
                dialogInterface.dismiss();
        }).show();
    }

    private void findImage(){
        if(mInput != null && mInput.getText() != null){
            final String text = mInput.getText().toString().trim();
            if(LL.D) Log.d(TAG, "findImage: text = " + text);
            SimpleViewUtils.showView(mProgressDone, View.VISIBLE);
            SimpleViewUtils.hideView(mFabOk, View.GONE);
            DataManager.getInstance(this).getConfig().subscribe(configData ->
                    DataManager.getInstance(ProductAddActivity.this).findImage(configData, text)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(imageEntry ->
                            saveProduct(text, imageEntry),
                            throwable -> {
                                Log.e(TAG, "accept: ", throwable);
                                saveProduct(text, null);
                            }), throwable -> saveProduct(text, null));
        }
    }

    private void saveProduct(final String text, final ImageEntry imageEntry){
        if(LL.D)
            Log.d(TAG, "saveProduct() called with: text = [" + text + "], imageEntry = [" + imageEntry + "]");
        if(!TextUtils.isEmpty(text)){
            RealmDB.get().executeTransaction(realm -> {
                String imageUrl = null;
                if(imageEntry != null
                        && imageEntry.getValue() != null && imageEntry.getValue().size() > 0){
                    imageUrl = imageEntry.getValue().get(0).thumbnailUrl;
                }
                Product product = new Product();
                product.setName(text);
                product.setCheckInDate(System.currentTimeMillis());
                product.setImageUrl(imageUrl);
                product.setBarcode(mScanBarcode);
                if(mSelectedProductData != null && mSelectedProductData.name.equalsIgnoreCase(text)){
                    product.setBarcode(mSelectedProductData.barcode);
                    product.setStoreName(mSelectedProductData.storeName);
                    product.setPrice(mSelectedProductData.price);
                }
                realm.insertOrUpdate(product);
                finish();
            });
        }else{
            SimpleViewUtils.showView(mProgressDone, View.INVISIBLE);
            SimpleViewUtils.hideView(mFabOk, View.VISIBLE);
            Snackbar.make(mLayoutInput, R.string.error_input_product_name_message, Snackbar.LENGTH_SHORT)
                    .setAction(android.R.string.ok, view -> mInput.findFocus()).show();
        }
    }

    private void setRetrievedProductList(List<ProductData> data){
        mProgressRetrieving.setVisibility(View.INVISIBLE);
        if(data != null && data.size() > 0 && !isFinishing()){
            if(mPopup == null){
                mPopup = new ListPopupWindow(this);
                mPopup.setAnchorView(mLayoutInput);
            }

            if(mProductNameListAdapter == null){
                mProductNameListAdapter = new ProductNameListAdapter(this, data);
                mPopup.setAdapter(mProductNameListAdapter);
            }else{
                mProductNameListAdapter.setData(data);
            }

            mPopup.setOnItemClickListener((adapterView, view, i, l) -> {
                if(mProductNameListAdapter != null && mProductNameListAdapter.getItemCount() > i){
                    ProductData product = mProductNameListAdapter.getItem(i);
                    if(LL.D) Log.d(TAG, "onItemClick: product = " + product.name);
                    setProductData(product);
                }
            });

            if(!mPopup.isShowing()){
                mPopup.show();
            }
        }else{
            if(mPopup != null && mPopup.isShowing()){
                mPopup.dismiss();
            }
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
            final String text = editable.toString().trim();
            if(TextUtils.isEmpty(text)){
                mFabOk.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryRed)));
            }else{
                mFabOk.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryGreen)));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, R.string.scan_barcode_canceled, Toast.LENGTH_LONG).show();
            } else {
                setBarcode(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

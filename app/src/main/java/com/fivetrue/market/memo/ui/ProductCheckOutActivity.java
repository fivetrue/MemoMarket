package com.fivetrue.market.memo.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.text.TextUtils;
import android.widget.Toast;

import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.database.FirebaseDB;
import com.fivetrue.market.memo.database.product.ProductDB;
import com.fivetrue.market.memo.model.vo.Product;
import com.fivetrue.market.memo.ui.adapter.product.CheckOutListAdapter;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

import io.reactivex.Observable;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;

/**
 * Created by kwonojin on 2017. 3. 5..
 */

public class ProductCheckOutActivity extends BaseActivity{

    private static final String TAG = "ProductCheckOutActivity";

    public static final String KEY_CHECKOUT_DATE = "checkout_date";

    private RecyclerView mRecyclerView;
    private CheckOutListAdapter mAdapter;

    private LinearLayoutManager mLayoutManager;

    private long mCheckOutMillis;

    private Product mProductForBarcode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_checkout);
        initData();
        initView();
    }

    private void initData(){
        mCheckOutMillis = getIntent().getLongExtra(KEY_CHECKOUT_DATE, 0);
        mAdapter = new CheckOutListAdapter(Observable.fromIterable(ProductDB.getInstance().getProducts())
                .filter(product -> product.isCheckOut() && product.getCheckOutDate() == mCheckOutMillis)
                .toList().blockingGet(), new CheckOutListAdapter.OnClickCheckoutProductListener() {
            @Override
            public void onAcceptProduct(CheckOutListAdapter.CheckOutViewHolder holder, Product product) {
                if(holder != null){
                    String price = holder.priceInput.getText().toString();
                    String store = holder.storeInput.getText().toString();
                    String barcode = holder.barcode.getText().toString();
                    if(!TextUtils.isEmpty(price)){
                        try{
                            long value = Long.parseLong(price.trim());
                            ProductDB.get().executeTransaction(realm -> {
                                product.setPrice(value);
                                product.setStoreName(store);
                                product.setBarcode(barcode);
                                FirebaseDB.getInstance(ProductCheckOutActivity.this)
                                        .addProduct(product).addOnCompleteListener(task -> {
                                    mAdapter.getData().remove(holder.getAdapterPosition());
                                    mAdapter.notifyItemRemoved(holder.getAdapterPosition());
                                    if(mAdapter.getItemCount() == 0){
                                        finish();
                                    }
                                });
                            });
                        }catch (Exception e){

                        }
                    }else{
                        Toast.makeText(ProductCheckOutActivity.this, R.string.product_has_no_price, Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onDeleteProduct(CheckOutListAdapter.CheckOutViewHolder holder, Product product) {
                ProductDB.get().executeTransaction(realm -> {
                    product.setCheckOutDate(0);
                    mAdapter.getData().remove(holder.getAdapterPosition());
                    mAdapter.notifyItemRemoved(holder.getAdapterPosition());
                    if(mAdapter.getItemCount() == 0){
                        finish();
                    }
                });
            }

            @Override
            public void onScanBarcode(CheckOutListAdapter.CheckOutViewHolder holder, Product product) {
                mProductForBarcode = product;
                IntentIntegrator integrator = new IntentIntegrator(ProductCheckOutActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES);
                integrator.setPrompt(getString(R.string.scan_barcode));
                integrator.setCameraId(0);  // Use a specific camera of the device
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();
            }
        });
    }

    private void initView(){
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_checkout_list);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new FadeInAnimator());
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
    }


    public static Intent makeIntent(Context context, List<Product> products, long ms){
        Intent intent = new Intent(context, ProductCheckOutActivity.class);
        intent.putExtra(KEY_CHECKOUT_DATE, ms);
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                if(mAdapter != null && mProductForBarcode != null){
                    ProductDB.get().executeTransaction(realm -> {
                        mProductForBarcode.setBarcode(result.getContents());
                        mAdapter.notifyDataSetChanged();
                        mProductForBarcode = null;
                    });
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

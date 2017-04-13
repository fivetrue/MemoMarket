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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.database.FirebaseDB;
import com.fivetrue.market.memo.database.product.ProductDB;
import com.fivetrue.market.memo.model.vo.Product;
import com.fivetrue.market.memo.ui.adapter.list.CheckOutListAdapter;
import com.fivetrue.market.memo.utils.SimpleViewUtils;
import com.fivetrue.market.memo.utils.TrackingUtil;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;

/**
 * Created by kwonojin on 2017. 3. 5..
 */

public class ProductCheckOutActivity extends BaseActivity{

    private static final String TAG = "ProductCheckOutActivity";

    private static final String KEY_CHECK_OUT_ITEMS = "checkout_items";

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
        long[] targetMillis = getIntent().getLongArrayExtra(KEY_CHECK_OUT_ITEMS);
        List<Product> productList = Observable.fromIterable(ProductDB.getInstance().getProducts())
                .filter(product -> {
                    for(long l : targetMillis){
                        if(l == product.getCheckInDate()){
                            return true;
                        }
                    }
                    return false;
                }).toList().blockingGet();

        mCheckOutMillis = System.currentTimeMillis();
        mAdapter = new CheckOutListAdapter(productList, new CheckOutListAdapter.OnClickCheckoutProductListener() {
            @Override
            public void onAcceptProduct(CheckOutListAdapter.CheckOutViewHolder holder, Product product) {
                if(holder != null){
                    String price = holder.priceInput.getText().toString();
                    String store = holder.storeInput.getText().toString();
                    String barcode = holder.barcode.getText().toString();
                    if(!TextUtils.isEmpty(price) && !TextUtils.isEmpty(store)){
                        try{
                            long value = Long.parseLong(price.trim());
                            ProductDB.get().executeTransaction(realm -> {
                                product.setPrice(value);
                                product.setStoreName(store);
                                product.setBarcode(barcode);
                                product.setCheckOutDate(mCheckOutMillis);

                                SimpleViewUtils.hideView(holder.accept, View.GONE);
                                SimpleViewUtils.showView(holder.progressBar, View.VISIBLE);
                                TrackingUtil.getInstance().checkoutProduct(product.getName()
                                        , product.getBarcode()
                                        , product.getPrice()
                                        , product.getStoreName());

                                if(!TextUtils.isEmpty(product.getBarcode())){
                                    FirebaseDB.getInstance(ProductCheckOutActivity.this)
                                            .addProduct(product).addOnCompleteListener(task -> {
                                        arrangeProduct(holder);
                                    });
                                }else{
                                    arrangeProduct(holder);
                                }

                            });
                        }catch (Exception e){
                            Log.w(TAG, "onAcceptProduct: ", e);
                        }
                    }else{
                        Toast.makeText(ProductCheckOutActivity.this, R.string.product_has_no_price_message, Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onDeleteProduct(CheckOutListAdapter.CheckOutViewHolder holder, Product product) {
                ProductDB.get().executeTransaction(realm -> {
                    product.setCheckOutDate(0);
                    arrangeProduct(holder);
                });
            }
        });
    }

    private void arrangeProduct(CheckOutListAdapter.CheckOutViewHolder holder){
        if(mAdapter != null && mAdapter.getData().size() > holder.getAdapterPosition()){
            mAdapter.getData().remove(holder.getAdapterPosition());
            mAdapter.notifyItemRemoved(holder.getAdapterPosition());
        }
        if(mAdapter.getItemCount() == 0){
            finish();
        }
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

    public static Intent makeIntent(Context context, String where, Product product){
        ArrayList<Product> array = new ArrayList<>();
        array.add(product);
        return makeIntent(context, where, array);
    }

    public static Intent makeIntent(Context context, String where, long... array){
        TrackingUtil.getInstance().startProductCheckout(where);
        Intent intent = new Intent(context, ProductCheckOutActivity.class);
        intent.putExtra(KEY_CHECK_OUT_ITEMS, array);
        return intent;
    }

    public static Intent makeIntent(Context context, String where, List<Product> products){
        List<Long> list = Observable.fromIterable(products)
                .map(product -> product.getCheckInDate())
                .toList().blockingGet();

        long[] array = new long[list.size()];
        for(int i = 0 ; i < list.size() ; i ++){
            array[i] = list.get(i);
        }
        return makeIntent(context, where, array);
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

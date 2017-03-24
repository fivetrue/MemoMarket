package com.fivetrue.market.memo.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.database.RealmDB;
import com.fivetrue.market.memo.model.vo.Product;
import com.fivetrue.market.memo.ui.adapter.product.ProductListAdapter;
import com.fivetrue.market.memo.utils.SimpleViewUtils;

import java.util.List;

import io.realm.Realm;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;

/**
 * Created by kwonojin on 2017. 2. 7..
 */

public class CheckOutProductFragment extends BaseFragment{

    private static final String TAG = "CheckOutProductFragment";

    private NestedScrollView mScrollView;
    private RecyclerView mRecyclerProduct;
    private ProductListAdapter mProductListAdapter;

    private TextView mTextMessage;

    private FloatingActionButton mFabBuyProduct;
    private FloatingActionButton mFabReturnProduct;
    private FloatingActionButton mFabCancel;

    private RecyclerView.LayoutManager mLayoutManager;
    private int mScrollPos = 0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setProductList(loadProducts(), true);
    }

    public List<Product> loadProducts(){
        return RealmDB.get().where(Product.class)
                .equalTo("checkOut", true).equalTo("checkOutDate", 0).findAll().sort("checkInDate");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mScrollView != null){
            mScrollPos = mScrollView.getScrollY();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkout_product_list, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mScrollView = (NestedScrollView) view.findViewById(R.id.sv_fragment_checkout_product_list);
        mRecyclerProduct = (RecyclerView) view.findViewById(R.id.rv_fragment_checkout_product_list);
        mTextMessage = (TextView) view.findViewById(R.id.tv_fragment_checkout_product_list);

        mFabBuyProduct = (FloatingActionButton) view.findViewById(R.id.fab_fragment_checkout_product_list_buy);
        mFabReturnProduct = (FloatingActionButton) view.findViewById(R.id.fab_fragment_checkout_product_list_return);
        mFabCancel = (FloatingActionButton) view.findViewById(R.id.fab_fragment_checkout_product_list_cancel);

        mLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        mRecyclerProduct.setLayoutManager(mLayoutManager);
        mRecyclerProduct.setItemAnimator(new ProductItemAnimator());
        mRecyclerProduct.setAdapter(mProductListAdapter);

        mFabBuyProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Product> products = mProductListAdapter.getSelections();


            }
        });

        mFabReturnProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity() != null){
                    RealmDB.get().executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            final List<Product> products = mProductListAdapter.getSelections();
                            for(Product p : products){
                                p.setCheckOut(false);
                            }
                            mProductListAdapter.clearSelection();
                            setProductList(loadProducts(), true);
                            updateButtons();
                            Snackbar.make(mScrollView, R.string.product_returned_complete_message
                                    , Snackbar.LENGTH_LONG)
                                    .setAction(R.string.revert, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            RealmDB.get().executeTransaction(new Realm.Transaction() {
                                                @Override
                                                public void execute(Realm realm) {
                                                    for(Product p : products){
                                                        p.setCheckOut(true);
                                                    }
                                                    setProductList(loadProducts(), true);
                                                }
                                            });
                                        }
                                    }).show();
                        }
                    });
                }
            }
        });

        mFabCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProductListAdapter.clearSelection();
                updateButtons();
            }
        });

        view.findViewById(R.id.layout_fragment_checkout_product_list).addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right
                    , int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(view != null){
                    view.removeOnLayoutChangeListener(this);
                    ViewAnimationUtils.createCircularReveal(view, left, top
                            , 0, Math.max(right, bottom)).setDuration(250L).start();
                }
            }
        });

        if(LL.D) Log.d(TAG, "onViewCreated: mScrollPos = " + mScrollPos);
        mTextMessage.setVisibility(mProductListAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);

        mScrollView.setScrollY(mScrollPos);
    }

    private void setProductList(List<Product> productList, boolean notify){
        if(LL.D) Log.d(TAG, "setProductList() called with: storeList = [" + productList + "]");
        if(productList != null){
            if(mProductListAdapter == null){
                mProductListAdapter = new ProductListAdapter(productList, new ProductListAdapter.OnProductItemListener(){

                    @Override
                    public void onClickItem(ProductListAdapter.ProductHolder holder, Product item) {
                        mProductListAdapter.toggle(holder.getAdapterPosition());
                        updateButtons();
                    }

                    @Override
                    public boolean onLongCLickItem(ProductListAdapter.ProductHolder holder, Product item) {
                        return false;
                    }
                });
            }else{
                mProductListAdapter.setData(productList, notify);
            }

            if(mTextMessage != null){
                mTextMessage.setVisibility(mProductListAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
            }
        }
    }

    private void updateButtons(){
        if(mProductListAdapter != null){
            if(mFabBuyProduct != null && mFabReturnProduct != null){
                if(mProductListAdapter.getSelections().size() > 0){
                    SimpleViewUtils.showView(mFabBuyProduct, View.VISIBLE);
                    SimpleViewUtils.showView(mFabReturnProduct, View.VISIBLE);
                    SimpleViewUtils.showView(mFabCancel, View.VISIBLE);

                }else{
                    SimpleViewUtils.hideView(mFabBuyProduct, View.GONE);
                    SimpleViewUtils.hideView(mFabReturnProduct, View.GONE);
                    SimpleViewUtils.hideView(mFabCancel, View.GONE);
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.cart);
    }

    @Override
    public int getImageResource() {
        return 0;
    }

    private static class ProductItemAnimator extends FadeInAnimator{

        @Override
        public boolean getSupportsChangeAnimations() {
            return false;
        }

        @Override
        public long getChangeDuration() {
            return 0;
        }
    }
}

package com.fivetrue.market.memo.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
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
import com.fivetrue.market.memo.ui.ProductAddActivity;
import com.fivetrue.market.memo.ui.adapter.product.ProductListAdapter;
import com.fivetrue.market.memo.utils.SimpleViewUtils;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;

/**
 * Created by kwonojin on 2017. 2. 7..
 */

public class ProductListFragment extends BaseFragment{

    private static final String TAG = "ProductListFragment";

    private NestedScrollView mScrollView;
    private View mContainer;
    private RecyclerView mRecyclerProduct;
    private ProductListAdapter mProductListAdapter;

    private TextView mTextMessage;

    private FloatingActionButton mFabAddProduct;
    private FloatingActionButton mFabLoadProduct;
    private FloatingActionButton mFabDeleteProduct;
    private FloatingActionButton mFabCancel;

    private RecyclerView.LayoutManager mLayoutManager;
    private int mScrollPos = 0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setProductList(RealmDB.get().where(Product.class)
                .equalTo("checkOut", false).findAll().sort("checkInDate"));
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
        return inflater.inflate(R.layout.fragment_product_list, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mScrollView = (NestedScrollView) view.findViewById(R.id.sv_fragment_product_list);
        mContainer = view.findViewById(R.id.layout_fragment_product_list);
        mRecyclerProduct = (RecyclerView) view.findViewById(R.id.rv_fragment_product_list);
        mTextMessage = (TextView) view.findViewById(R.id.tv_fragment_product_list);

        mFabAddProduct = (FloatingActionButton) view.findViewById(R.id.fab_fragment_product_list_add);
        mFabLoadProduct = (FloatingActionButton) view.findViewById(R.id.fab_fragment_product_list_checkout);
        mFabDeleteProduct = (FloatingActionButton) view.findViewById(R.id.fab_fragment_product_list_delete);
        mFabCancel = (FloatingActionButton) view.findViewById(R.id.fab_fragment_product_list_cancel);

        mLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        mRecyclerProduct.setLayoutManager(mLayoutManager);
        mRecyclerProduct.setItemAnimator(new ProductItemAnimator());
        mRecyclerProduct.setAdapter(mProductListAdapter);
        mFabAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RealmDB.get().addChangeListener(new RealmChangeListener<Realm>() {
                    @Override
                    public void onChange(Realm element) {
                        element.removeChangeListener(this);
                        setProductList(RealmDB.get().where(Product.class).equalTo("checkOut", false)
                                .findAll().sort("checkInDate"));
                    }
                });
                if(getActivity() != null){
                    startActivity(new Intent(getActivity(), ProductAddActivity.class));
                }
            }
        });

        mFabLoadProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mFabDeleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RealmDB.get().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        List<Product> products = mProductListAdapter.getSelections();
                        for(Product p : products){
                            int index = mProductListAdapter.getData().indexOf(p);
                            mProductListAdapter.toggle(index);
                            mProductListAdapter.notifyItemRemoved(index);
                        }
                        for(Product p : products){
                            p.deleteFromRealm();
                        }
                        updateButtons();
                    }
                });
            }
        });

        mFabCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProductListAdapter.clearSelection();
                updateButtons();
            }
        });

        mContainer.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
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

    private void setProductList(List<Product> productList){
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
                mProductListAdapter.setData(productList);
            }
        }
    }

    private void updateButtons(){
        if(mProductListAdapter != null){
            if(mFabLoadProduct != null && mFabDeleteProduct != null){
                if(mProductListAdapter.getSelections().size() > 0){
                    SimpleViewUtils.showView(mFabLoadProduct, View.VISIBLE);
                    SimpleViewUtils.showView(mFabDeleteProduct, View.VISIBLE);
                    SimpleViewUtils.showView(mFabCancel, View.VISIBLE);

                }else{
                    SimpleViewUtils.hideView(mFabLoadProduct, View.GONE);
                    SimpleViewUtils.hideView(mFabDeleteProduct, View.GONE);
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
        return null;
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

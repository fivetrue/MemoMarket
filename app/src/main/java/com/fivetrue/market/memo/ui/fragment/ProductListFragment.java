package com.fivetrue.market.memo.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.database.RealmDB;
import com.fivetrue.market.memo.model.vo.Product;
import com.fivetrue.market.memo.ui.BaseActivity;
import com.fivetrue.market.memo.ui.ProductAddActivity;
import com.fivetrue.market.memo.ui.adapter.product.ProductListAdapter;
import com.fivetrue.market.memo.utils.SimpleViewUtils;
import com.fivetrue.market.memo.view.PagerTabContent;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;

/**
 * Created by kwonojin on 2017. 2. 7..
 */

public class ProductListFragment extends BaseFragment implements PagerTabContent, RealmChangeListener<Realm> {

    private static final String TAG = "ProductListFragment";

    private RecyclerView mRecyclerProduct;
    private ProductListAdapter mProductListAdapter;

    private TextView mTextMessage;

    private GridLayoutManager mLayoutManager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setProductList(loadProducts(), true);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_list, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerProduct = (RecyclerView) view.findViewById(R.id.rv_fragment_product_list);
        mTextMessage = (TextView) view.findViewById(R.id.tv_fragment_product_list);

        mLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mProductListAdapter.getItemViewType(position)){
                    case ProductListAdapter.FOOTER :
                        return 2;

                    case ProductListAdapter.PRODUCT:
                        return 1;

                    default:
                        return -1;
                }
            }
        });
        mRecyclerProduct.setLayoutManager(mLayoutManager);
        mRecyclerProduct.setItemAnimator(new ProductItemAnimator());
        mRecyclerProduct.setAdapter(mProductListAdapter);

//        mFabCheckOutProduct.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                RealmDB.get().executeTransaction(new Realm.Transaction() {
//                    @Override
//                    public void execute(Realm realm) {
//                        final List<Product> products = mProductListAdapter.getSelections();
//                        for(Product p : products){
//                            p.setCheckOut(true);
//                        }
//                        mProductListAdapter.clearSelection();
//                        setProductList(loadProducts(), true);
//                        updateButtons();
//
//                        Snackbar.make(mRecyclerProduct, R.string.product_moved_completed_message
//                                , Snackbar.LENGTH_LONG)
//                                .setAction(R.string.revert, new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        RealmDB.get().executeTransaction(new Realm.Transaction() {
//                                            @Override
//                                            public void execute(Realm realm) {
//                                                for(Product p : products){
//                                                    p.setCheckOut(false);
//                                                }
//                                                setProductList(loadProducts(), true);
//                                            }
//                                        });
//                                    }
//                                }).show();
//                    }
//                });
//            }
//        });
//
//        mFabDeleteProduct.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(getActivity() != null){
//
//                    new AlertDialog.Builder(getActivity())
//                            .setTitle(android.R.string.dialog_alert_title)
//                            .setMessage(R.string.delete_product_message)
//                            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    dialogInterface.dismiss();
//                                }
//                            }).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            RealmDB.get().executeTransaction(new Realm.Transaction() {
//                                @Override
//                                public void execute(Realm realm) {
//                                    final List<Product> products = mProductListAdapter.getSelections();
//                                    for(Product p : products){
//                                        int index = mProductListAdapter.getData().indexOf(p);
//                                        mProductListAdapter.toggle(index);
//                                        mProductListAdapter.notifyItemRemoved(index);
//                                    }
//                                    for(Product p : products){
//                                        p.deleteFromRealm();
//                                    }
//                                    updateButtons();
//                                }
//                            });
//                            dialogInterface.dismiss();
//                        }
//                    }).show();
//                }
//            }
//        });

//        mFabCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mProductListAdapter.clearSelection();
//                updateButtons();
//            }
//        });

        view.findViewById(R.id.layout_fragment_product_list).addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
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

        mTextMessage.setVisibility(mProductListAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
    }

    private List<Product> loadProducts(){
        return RealmDB.get().where(Product.class)
                .equalTo("checkOut", false).findAll().sort("checkInDate");
    }

    private void setProductList(List<Product> productList, boolean notify){
        if(LL.D) Log.d(TAG, "setProductList() called with: storeList = [" + productList + "]");
        if(productList != null){
            if(mProductListAdapter == null){
                mProductListAdapter = new ProductListAdapter(productList, new ProductListAdapter.OnProductItemListener(){

                    @Override
                    public void onClickItem(ProductListAdapter.ProductHolder holder, Product item) {
                        mProductListAdapter.toggle(holder.getAdapterPosition());
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

    private void moveToCart(){
        if(getActivity() != null && getActivity() instanceof BaseActivity){
            mProductListAdapter.clearSelection();
            ((BaseActivity) getActivity()).addFragment(CheckOutProductFragment.class, null
                    , ((BaseActivity) getActivity()).getDefaultFragmentAnchor(), true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_cart :
                moveToCart();
            break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        RealmDB.get().addChangeListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        RealmDB.get().removeChangeListener(this);
    }

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.product);
    }

    @Override
    public int getImageResource() {
        return R.drawable.selector_product;
    }

    @Override
    public String getTabTitle(Context context) {
        return getTitle(context);
    }

    @Override
    public Drawable getTabDrawable(Context context) {
        return context.getDrawable(getImageResource());
    }

    @Override
    public boolean isShowingIcon() {
        return true;
    }

    @Override
    public void onChange(Realm element) {
        setProductList(loadProducts(), true);
    }

    @Override
    public boolean onBackPressed() {
        if(mProductListAdapter.getSelections().size() > 0){
            mProductListAdapter.clearSelection();
            return true;
        }
        return super.onBackPressed();
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

    public static Bundle makeArgument(Context context){
        Bundle b = new Bundle();
        return b;
    }

}

package com.fivetrue.market.memo.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
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
import com.fivetrue.market.memo.database.product.ProductDB;
import com.fivetrue.market.memo.model.vo.Product;
import com.fivetrue.market.memo.ui.ProductCheckOutActivity;
import com.fivetrue.market.memo.ui.adapter.product.CheckOutProductListAdapter;
import com.fivetrue.market.memo.ui.adapter.product.ProductListAdapter;
import com.fivetrue.market.memo.utils.SimpleViewUtils;
import com.fivetrue.market.memo.view.PagerTabContent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;

/**
 * Created by kwonojin on 2017. 2. 7..
 */

public class CheckOutProductFragment extends ProductListFragment{

    private static final String TAG = "CheckOutProductFragment";

    @Override
    protected boolean makeFilter(Product p) {
        boolean b = false;
        if(p != null){
            b = p.isCheckOut() && p.getCheckOutDate() == 0;
        }
        return b;
    }

    @Override
    public void doProducts(View snackbarAnchor) {
        if(getAdapter() != null && getActivity() != null){
            ProductDB.get().executeTransaction(realm -> {
                long currentMs = System.currentTimeMillis();
                List<Product> products = getAdapter().getSelections();
                for(Product p : products){
                    p.setCheckOutDate(currentMs);
                }
                Intent intent = ProductCheckOutActivity.makeIntent(getActivity(), products, currentMs);
                getActivity().startActivity(intent);
            });
        }
    }

    @Override
    protected ProductListAdapter makeAdapter(List<Product> productList) {
        return new CheckOutProductListAdapter(productList, new ProductListAdapter.OnProductItemListener() {
            @Override
            public void onClickItem(ProductListAdapter.ProductHolder holder, Product item) {
                getAdapter().toggle(holder.getAdapterPosition());
                publish();
            }

            @Override
            public boolean onLongClickItem(ProductListAdapter.ProductHolder holder, Product item) {
                return false;
            }
        });
    }

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.cart);
    }

    @Override
    public int getImageResource() {
        return R.drawable.selector_cart_loaded;
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
    public TabIcon getIconState() {
        return TabIcon.TextWithIcon;
    }
}

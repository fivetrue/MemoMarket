package com.fivetrue.market.memo.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.model.vo.Product;
import com.fivetrue.market.memo.ui.MainActivity;
import com.fivetrue.market.memo.ui.ProductCheckOutActivity;
import com.fivetrue.market.memo.ui.adapter.list.RecentlyPurchaseListAdapter;
import com.fivetrue.market.memo.ui.adapter.list.ProductListAdapter;

import java.util.List;

/**
 * Created by kwonojin on 2017. 2. 7..
 */

public class CheckOutProductFragment extends ProductListFragment{

    private static final String TAG = "CheckOutProductFragment";

    @Override
    protected boolean makeFilter(Product p) {
        boolean b = false;
        if(p != null){
            b = p.getCheckOutDate() == 0;
        }
        return b;
    }

    @Override
    public void onClickActionButton() {
        if(getAdapter() != null && getActivity() != null){
            Intent intent = ProductCheckOutActivity.makeIntent(getActivity(), TAG, getAdapter().getSelections());
            getActivity().startActivity(intent);
            getAdapter().clearSelection();
            updateFab();
            if(getActivity() != null && getActivity() instanceof MainActivity){
                ((MainActivity) getActivity()).movePageToRight();
            }
        }
    }

    @Override
    protected ProductListAdapter makeAdapter(List<Product> productList) {
        ProductListAdapter adapter =  new RecentlyPurchaseListAdapter(productList, new ProductListAdapter.OnProductItemListener() {
            @Override
            public void onClickItem(ProductListAdapter.ProductHolder holder, Product item) {
                getAdapter().toggle(holder.getAdapterPosition());
                updateFab();
            }

            @Override
            public boolean onLongClickItem(ProductListAdapter.ProductHolder holder, Product item) {
                return false;
            }
        });
        adapter.showAddButton(false);
        return adapter;
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

    @Override
    protected int getFabIconResource() {
        return R.drawable.ic_cart_checkout_white_50dp;
    }

    @Override
    protected int getFabTintColor() {
        return R.color.colorPrimaryDark;
    }
}

package com.fivetrue.market.memo.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.model.vo.Product;
import com.fivetrue.market.memo.ui.MainActivity;
import com.fivetrue.market.memo.ui.ProductCheckOutActivity;
import com.fivetrue.market.memo.ui.adapter.list.ProductCheckoutListAdapter;
import com.fivetrue.market.memo.ui.adapter.list.RecentlyPurchaseListAdapter;
import com.fivetrue.market.memo.ui.adapter.list.ProductListAdapter;
import com.fivetrue.market.memo.utils.AdUtil;
import com.fivetrue.market.memo.utils.CommonUtils;

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
            b = p.getCheckOutDate() > 0 &&
                    CommonUtils.getDate(getActivity()
                            , "MM dd yyyy", p.getCheckOutDate())
                            .equals(CommonUtils.getDate(getActivity()
                                    , "MM dd yyyy",System.currentTimeMillis()));
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
        ProductListAdapter adapter =  new ProductCheckoutListAdapter(productList, new ProductListAdapter.OnProductItemListener() {
            @Override
            public void onClickItem(ProductListAdapter.ProductHolder holder, Product item) {

            }

            @Override
            public boolean onLongClickItem(ProductListAdapter.ProductHolder holder, Product item) {
                if(item != null){
                    if(item != null && getActivity() != null){
                        Toast.makeText(getActivity()
                                , String.format(getString(R.string.product_purchased_date)
                                        , CommonUtils.getDate(getActivity(), "MM dd yyyy HH"
                                                , item.getCheckInDate())), Toast.LENGTH_LONG).show();
                        return true;
                    }
                }
                return false;
            }
        });
        adapter.showAddButton(false);
        return adapter;
    }

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.purchase_product);
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
    protected String getAdType() {
        return AdUtil.AD_LIST_BOTTOM_2;
    }
}

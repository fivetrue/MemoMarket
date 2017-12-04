package com.fivetrue.market.memo.ui.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.model.Product;
import com.fivetrue.market.memo.ui.adapter.holder.HolderClickEvent;
import com.fivetrue.market.memo.ui.adapter.list.ProductCheckoutListAdapter;
import com.fivetrue.market.memo.ui.adapter.list.ProductListAdapter;
import com.fivetrue.market.memo.utils.CommonUtils;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by kwonojin on 2017. 2. 7..
 */

public class CheckOutProductFragment extends ProductListFragment{

    private static final String TAG = "CheckOutProductFragment";

    @Override
    protected List<Product> arrangeProducts(List<Product> products) {
        return Observable.fromIterable(products)
                .filter(product -> product.getCheckOutDate() > 0 &&
                        CommonUtils.getDate(getActivity()
                                , "MM dd yyyy", product.getCheckOutDate())
                                .equals(CommonUtils.getDate(getActivity()
                                        , "MM dd yyyy",System.currentTimeMillis()))).toList().blockingGet();
    }

    @Override
    protected ProductListAdapter makeAdapter(List<Product> productList) {
        ProductListAdapter adapter  = new ProductCheckoutListAdapter(productList);
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
    protected void onItemClickEvent(HolderClickEvent.ClickEvent event) {
        // Nothing
    }
}

package com.fivetrue.market.memo.ui.adapter.list;

import android.content.Context;
import android.support.v7.widget.ListPopupWindow;
import android.util.TypedValue;
import android.widget.ArrayAdapter;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.model.Product;
import com.fivetrue.market.memo.ui.adapter.holder.ProductHolder;
import com.fivetrue.market.memo.utils.CommonUtils;

import java.util.List;


/**
 * Created by kwonojin on 2017. 1. 26..
 */

public class ProductCheckoutListAdapter extends ProductListAdapter {

    private static final String TAG = "ProductCheckoutListAdap";

    public ProductCheckoutListAdapter(List<Product> data) {
        super(data);
    }

    @Override
    protected void onBindProductHolder(ProductHolder holder, int position) {
        final Product item = getItem(position);
        if(holder != null && item != null){
            holder.mBinding.name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
            holder.mBinding.name.setMaxLines(2);
            holder.mBinding.name.setText(item.getName() + "\n" + CommonUtils.convertToCurrency(item.getPrice()));
            holder.setProduct(item, isSelect(position));
            holder.mBinding.more.setOnClickListener(view -> {
                showPopup(view.getContext(), view, item, position);
            });
        }
    }

    protected ListPopupWindow makePopup(Context context, Product item, int position){
        final ListPopupWindow popupWindow = new ListPopupWindow(context);
        String [] listItems = {
                context.getString(R.string.revert)
                , context.getString(R.string.delete)};

        popupWindow.setAdapter(new ArrayAdapter(context,  android.R.layout.simple_list_item_1, listItems));
        popupWindow.setOnItemClickListener((adapterView, view1, i, l) -> {
            popupWindow.dismiss();
            switch (i){
                case 0 :
                    publishMoreEvent(new MoreEvent(MoreType.Revert, position, item));
                    break;
                case 2 :
                    publishMoreEvent(new MoreEvent(MoreType.Delete, position, item));
                    break;
            }
            clearSelection();
        });
        return popupWindow;
    }
}

package com.fivetrue.market.memo.ui.adapter.list;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.database.RealmDB;
import com.fivetrue.market.memo.database.product.ProductDB;
import com.fivetrue.market.memo.model.vo.Product;
import com.fivetrue.market.memo.preference.DefaultPreferenceUtil;
import com.fivetrue.market.memo.ui.ProductAddActivity;
import com.fivetrue.market.memo.ui.ProductCheckOutActivity;
import com.fivetrue.market.memo.ui.adapter.BaseAdapterImpl;
import com.fivetrue.market.memo.utils.CommonUtils;
import com.fivetrue.market.memo.utils.TrackingUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by kwonojin on 2017. 1. 26..
 */

public class ProductCheckoutListAdapter extends ProductListAdapter {

    private static final String TAG = "ProductCheckoutListAdap";

    public ProductCheckoutListAdapter(List<Product> data, ProductListAdapter.OnProductItemListener ll) {
        super(data, ll);
    }

    @Override
    protected void onBindProductHolder(ProductListAdapter.ProductHolder holder, int position) {
        super.onBindProductHolder(holder, position);
        Product p = getItem(position);
        if(p != null){
            holder.name.setText(p.getName() + "\n" + CommonUtils.convertToCurrency(p.getPrice()));
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
                    ProductDB.get().executeTransaction(realm -> {
                        item.setCheckOutDate(0);
                        Toast.makeText(view1.getContext()
                                , String.format("%s \"%s\"", listItems[i], item.getName())
                                , Toast.LENGTH_SHORT).show();
                    });
                    break;
                case 1 :
                    new AlertDialog.Builder(context)
                            .setTitle(R.string.delete)
                            .setMessage(R.string.delete_product_message)
                            .setPositiveButton(android.R.string.ok, (dialogInterface, i1) -> {
                                dialogInterface.dismiss();
                                RealmDB.get().executeTransaction(realm -> {
                                    TrackingUtil.getInstance().deleteProduct(item.getName(), TAG);
                                    item.deleteFromRealm();
                                    notifyItemRemoved(position);
                                });
                            }).setNegativeButton(android.R.string.cancel, (dialogInterface, i1) -> dialogInterface.dismiss())
                            .show();

                    break;
            }
            clearSelection();
        });
        return popupWindow;
    }
}

package com.fivetrue.market.memo.ui.adapter.list;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ListPopupWindow;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.database.RealmDB;
import com.fivetrue.market.memo.database.product.ProductDB;
import com.fivetrue.market.memo.model.vo.Product;
import com.fivetrue.market.memo.utils.TrackingUtil;

import java.util.List;

/**
 * Created by kwonojin on 2017. 3. 29..
 */

public class RecentlyPurchaseListAdapter extends ProductListAdapter {

    private static final String TAG = "RecentlyPurchaseListAda";

    public RecentlyPurchaseListAdapter(List<Product> data, OnProductItemListener ll) {
        super(data, ll);
    }

    @Override
    protected ListPopupWindow makePopup(Context context, Product item, int position) {
        final ListPopupWindow popupWindow = new ListPopupWindow(context);
        String [] listItems = {context.getString(R.string.add_item),
                context.getString(R.string.delete)};

        popupWindow.setAdapter(new ArrayAdapter(context,  android.R.layout.simple_list_item_1, listItems));
        popupWindow.setOnItemClickListener((adapterView, view1, i, l) -> {
            popupWindow.dismiss();
            Toast.makeText(view1.getContext()
                    , String.format("%s \"%s\"", listItems[i], item.getName())
                    , Toast.LENGTH_SHORT).show();
            switch (i){
                case 0 :
                    Product product = new Product();
                    product.setName(item.getName());
                    product.setStoreName(item.getStoreName());
                    product.setBarcode(item.getBarcode());
                    product.setImageUrl(item.getImageUrl());
                    product.setPrice(item.getPrice());
                    product.setCheckInDate(System.currentTimeMillis());
                    ProductDB.getInstance().add(product);
                    break;

                case 1 :
                    new AlertDialog.Builder(context)
                            .setTitle(R.string.delete)
                            .setMessage(R.string.delete_purchase_product_message)
                            .setPositiveButton(android.R.string.ok, (dialogInterface, i1) -> {
                                dialogInterface.dismiss();
                                ProductDB.get().executeTransaction(realm -> {
                                    TrackingUtil.getInstance().deleteProduct(item.getName(), TAG);
                                    item.deleteFromRealm();
                                });
                            }).setNegativeButton(android.R.string.cancel, (dialogInterface, i1) -> dialogInterface.dismiss())
                            .show();



            }
        });
        return popupWindow;
    }
}

package com.fivetrue.market.memo.ui.adapter.product;

import android.content.Context;
import android.support.v7.widget.ListPopupWindow;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.database.RealmDB;
import com.fivetrue.market.memo.model.vo.Product;

import java.util.List;

/**
 * Created by kwonojin on 2017. 3. 29..
 */

public class CheckOutProductListAdapter extends ProductListAdapter {

    public CheckOutProductListAdapter(List<Product> data, OnProductItemListener ll) {
        super(data, ll);
    }

    @Override
    protected ListPopupWindow makePopup(Context context, Product item, int position) {
        final ListPopupWindow popupWindow = new ListPopupWindow(context);
        String [] listItems = {context.getString(R.string.revert)};
        popupWindow.setAdapter(new ArrayAdapter(context,  android.R.layout.simple_list_item_1, listItems));
        popupWindow.setOnItemClickListener((adapterView, view1, i, l) -> {
            popupWindow.dismiss();
            switch (i){
                case 0 :
                    RealmDB.get().executeTransaction(realm -> {
                        notifyItemRemoved(position);
                        clearSelection();
                    });
                    break;
            }
            Toast.makeText(view1.getContext()
                    , String.format("%s \"%s\"", listItems[i], item.getName())
                    , Toast.LENGTH_SHORT).show();
        });
        return popupWindow;
    }
}

package com.fivetrue.market.memo.ui.adapter.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.ui.ProductAddActivity;


/**
 * Created by kwonojin on 2017. 11. 14..
 */

public class ProductAddHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "ProductAddHolder";

    public ProductAddHolder(View itemView) {
        super(itemView);
        itemView.findViewById(R.id.layout_item_product_list_add).setOnClickListener(view -> {
            view.getContext().startActivity(ProductAddActivity.makeIntent(view.getContext(),TAG));
        });
    }

    public static RecyclerView.ViewHolder makeHolder(Context context){
        return new ProductAddHolder(LayoutInflater.from(context).inflate(R.layout.item_product_list_add, null));
    }
}

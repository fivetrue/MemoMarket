package com.fivetrue.market.memo.ui.adapter.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.fivetrue.market.memo.R;

/**
 * Created by kwonojin on 2017. 11. 14..
 */

public class ProductFooterHolder extends RecyclerView.ViewHolder {

    public final FrameLayout layout;

    public ProductFooterHolder(View itemView) {
        super(itemView);
        layout = itemView.findViewById(R.id.layout_item_product_list_footer);
    }

    public static RecyclerView.ViewHolder makeHolder(Context context){
        return new ProductFooterHolder(LayoutInflater.from(context).inflate(R.layout.item_product_list_footer, null));
    }
}

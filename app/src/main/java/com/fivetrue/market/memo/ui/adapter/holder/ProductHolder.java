package com.fivetrue.market.memo.ui.adapter.holder;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.databinding.ItemProductListItemBinding;
import com.fivetrue.market.memo.model.Product;
import com.fivetrue.market.memo.preference.DefaultPreferenceUtil;


/**
 * Created by kwonojin on 2017. 11. 14..
 */

public class ProductHolder extends RecyclerView.ViewHolder{

    public ItemProductListItemBinding mBinding;

    public ProductHolder(View itemView) {
        super(itemView);
        mBinding = DataBindingUtil.bind(itemView);
    }

    public void setProduct(Product product, boolean b){
        mBinding.setProduct(product);
        mBinding.setCheck(b);
        mBinding.setShowBadge(System.currentTimeMillis() - product.getCheckInDate()
                < DefaultPreferenceUtil.getNewProductPeriod(mBinding.image.getContext()));

        Glide.with(mBinding.image.getContext())
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_product_gray_50dp)
                .into(mBinding.image);

        mBinding.layout.animate().scaleX(b ? 0.9f : 1f)
                .scaleY(b ? 0.9f : 1f)
                .setDuration(200L)
                .start();
    }

    public static RecyclerView.ViewHolder makeHolder(Context context){
        return new ProductHolder(LayoutInflater.from(context).inflate(R.layout.item_product_list_item, null));
    }
}

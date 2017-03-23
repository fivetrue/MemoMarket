package com.fivetrue.market.memo.ui.adapter.product;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.model.vo.Product;
import com.fivetrue.market.memo.preference.DefaultPreferenceUtil;
import com.fivetrue.market.memo.ui.adapter.BaseAdapterImpl;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by kwonojin on 2017. 1. 26..
 */

public class ProductListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements BaseAdapterImpl<Product> {

    private static final String TAG = "StoreListAdapter";

    public interface OnProductItemListener {
        void onClickItem(ProductHolder holder, Product item);
        boolean onLongCLickItem(ProductHolder holder, Product item);
    }

    private SparseBooleanArray mSelectedItems;
    private List<Product> mData;
    private OnProductItemListener mProductItemListener;

    public ProductListAdapter(List<Product> data, OnProductItemListener ll){
        this.mData = data;
        mProductItemListener = ll;
        mSelectedItems = new SparseBooleanArray();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
         View view = inflater.inflate(R.layout.item_product_list_item, null);
        RecyclerView.ViewHolder holder = new ProductHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Product item = getItem(position);
        final ProductHolder productHolder = (ProductHolder) holder;
        if(holder != null && item != null){
            productHolder.setProduct(item, isSelect(position));
            productHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mProductItemListener != null){
                        mProductItemListener.onClickItem(productHolder, item);
                    }
                }
            });

            productHolder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(mProductItemListener != null){
                        return mProductItemListener.onLongCLickItem(productHolder, item);
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public Product getItem(int pos) {
        return mData.get(pos);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public List<Product> getData() {
        return mData;
    }

    @Override
    public void setData(List<Product> data) {
        mData = data;
    }

    public void setData(List<Product> data, boolean notify){
        setData(data);
        if(notify){
            notifyDataSetChanged();
        }
    }

    @Override
    public void toggle(int pos) {
        boolean b = !mSelectedItems.get(pos);
        mSelectedItems.put(pos, b);
        notifyItemChanged(pos);
    }

    @Override
    public boolean isSelect(int pos) {
        return mSelectedItems.get(pos);
    }

    @Override
    public void selection(int pos, boolean b) {
        mSelectedItems.put(pos, b);
        notifyItemChanged(pos);
    }

    @Override
    public void clearSelection() {
        mSelectedItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public List<Product> getSelections() {
        ArrayList<Product> list = new ArrayList<>();
        for(int i = 0 ; i < getItemCount() ; i++){
            if(mSelectedItems.get(i)){
                list.add(getItem(i));
            }
        }
        return list;
    }


    public static final class ProductHolder extends RecyclerView.ViewHolder{

        public final View layout;
        public final ImageView image;
        public final TextView name;
        public final ImageView check;
        public final ImageView badge;

        public ProductHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout_item_product_list_item);
            image = (ImageView) itemView.findViewById(R.id.iv_item_product_list_item_image);
            name = (TextView) itemView.findViewById(R.id.tv_item_product_list_item_name);
            check = (ImageView) itemView.findViewById(R.id.iv_item_product_list_item_check);
            badge = (ImageView) itemView.findViewById(R.id.iv_item_product_list_item_badge);
        }

        public void setProduct(Product product, boolean b){
            name.setText(product.getName());
            Glide.with(image.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_product_gray_50dp)
                    .into(image);
            if(System.currentTimeMillis() - product.getCheckInDate()
                    < DefaultPreferenceUtil.getNewProductPeriod(name.getContext())){
                badge.setImageResource(R.drawable.ic_new_red_20dp);
            }else{
                badge.setImageBitmap(null);
            }

            check.setVisibility(b ? View.VISIBLE : View.GONE);

            layout.animate().scaleX(b ? 0.9f : 1f)
                    .scaleY(b ? 0.9f : 1f)
                    .setDuration(100L)
                    .start();
        }
    }
}

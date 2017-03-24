package com.fivetrue.market.memo.ui.adapter.product;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fivetrue.market.memo.LL;
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

    public static final int PRODUCT = 0x01;
    public static final int FOOTER = 0x02;

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
        if(viewType == FOOTER){
            View view = inflater.inflate(R.layout.item_product_list_footer, null);
            RecyclerView.ViewHolder holder = new ProductFooter(view);
            return holder;
        }else{
            View view = inflater.inflate(R.layout.item_product_list_item, null);
            RecyclerView.ViewHolder holder = new ProductHolder(view);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(getItemViewType(position) == FOOTER){

        }else{
            onBindProductHolder((ProductHolder) holder, position);
        }
    }

    private void onBindFooterHolder(ProductFooter holder, int position){
        if(LL.D)
            Log.d(TAG, "onBindFooterHolder() called with: holder = [" + holder + "], position = [" + position + "]");
    }

    private void onBindProductHolder(final ProductHolder holder, int position){
        if(LL.D)
            Log.d(TAG, "onBindProductHolder() called with: holder = [" + holder + "], position = [" + position + "]");
        final Product item = getItem(position);
        if(holder != null && item != null){
            holder.setProduct(item, isSelect(position));
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mProductItemListener != null){
                        mProductItemListener.onClickItem(holder, item);
                    }
                }
            });

            holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(mProductItemListener != null){
                        return mProductItemListener.onLongCLickItem(holder, item);
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(mData.size() == position){
            return FOOTER;
        }
        return PRODUCT;
    }

    @Override
    public Product getItem(int pos) {
        if(mData.size() > pos){
            return mData.get(pos);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }

        if (mData.size() == 0) {
            //Return 1 here to show nothing
            return 1;
        }

        // Add extra view to show the footer view
        return mData.size() + 1;
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
        if(getItemViewType(pos) == FOOTER){
            return;
        }
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
        if(getItemViewType(pos) == FOOTER){
            return;
        }

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

    public static final class ProductFooter extends RecyclerView.ViewHolder{


        public ProductFooter(View itemView) {
            super(itemView);
        }
    }
}

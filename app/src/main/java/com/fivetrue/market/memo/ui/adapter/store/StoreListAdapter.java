package com.fivetrue.market.memo.ui.adapter.store;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.database.RealmDB;
import com.fivetrue.market.memo.model.vo.Product;
import com.fivetrue.market.memo.model.vo.Store;
import com.fivetrue.market.memo.ui.adapter.BaseAdapterImpl;

import java.util.List;

/**
 * Created by kwonojin on 2017. 1. 26..
 */

public class StoreListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements BaseAdapterImpl<Store> {

    private static final String TAG = "StoreListAdapter";

    public interface OnStoreItemClickListener{
        void onClickItem(StoreHolder holder, Store item);
        boolean onLongCLickItem(StoreHolder holder, Store item);
    }

    private SparseBooleanArray mSelectedItems;
    private List<Store> mData;
    private OnStoreItemClickListener mOnStoreItemClickListener;

    public StoreListAdapter(List<Store> data, OnStoreItemClickListener ll){
        this.mData = data;
        mOnStoreItemClickListener = ll;
        mSelectedItems = new SparseBooleanArray();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
         View view = inflater.inflate(R.layout.item_store_list_item, null);
        RecyclerView.ViewHolder holder = new StoreHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Store item = getItem(position);
        final StoreHolder storeHolder = (StoreHolder) holder;
        if(holder != null && item != null){
            storeHolder.setStore(item);
            storeHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mOnStoreItemClickListener != null){
                        mOnStoreItemClickListener.onClickItem(storeHolder, item);
                    }
                }
            });

            storeHolder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(mOnStoreItemClickListener != null){
                        return mOnStoreItemClickListener.onLongCLickItem(storeHolder, item);
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public Store getItem(int pos) {
        return mData.get(pos);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public List<Store> getData() {
        return mData;
    }

    public void setData(List<Store> data){
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public void add(Store data) {

    }

    @Override
    public void toggle(int pos) {

    }

    @Override
    public boolean isSelect(int pos) {
        return false;
    }

    @Override
    public void selection(int pos, boolean b) {

    }

    @Override
    public void clearSelection() {

    }

    @Override
    public void clear() {

    }

    @Override
    public List<Store> getSelections() {
        return null;
    }

    public void selectPosition(int pos){
        mSelectedItems.clear();
        mSelectedItems.put(pos, true);
        for(int i = 1 ; i < getItemCount() ; i ++){
            notifyItemChanged(i);
        }
    }

    public int selectedPosition(){
        for(int i = 1 ; i < getItemCount() ; i++){
            if(mSelectedItems.get(i)){
                return i;
            }
        }
        return -1;
    }

    public boolean isSelected(int pos){
        return mSelectedItems.get(pos);
    }

    public static final class StoreHolder extends RecyclerView.ViewHolder{

        public final View layout;
        public final ImageView image;
        public final TextView name;
        public final TextView count;

        public StoreHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout_item_store_list_item);
            image = (ImageView) itemView.findViewById(R.id.iv_item_store_list_item_image);
            name = (TextView) itemView.findViewById(R.id.tv_item_store_list_item_name);
            count = (TextView) itemView.findViewById(R.id.tv_item_store_list_item_count);
        }

        public void setStore(Store store){
            name.setText(store.getName());
            long c = RealmDB.get().where(Product.class).equalTo("storeName", store.getName()).count();
            count.setText(String.format(count.getResources().getString(R.string.store_product_count), c));
            Glide.with(image.getContext()).load(store.getImageUrl())
                    .placeholder(R.drawable.ic_product_gray_50dp)
                    .dontTransform().into(image);
        }
    }
}

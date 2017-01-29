package com.fivetrue.market.memo.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.model.StoreWrapper;

import java.util.List;

/**
 * Created by kwonojin on 2017. 1. 26..
 */

public class StoreListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements BaseAdapterImpl<StoreWrapper>{

    private static final String TAG = "StoreListAdapter";

    public interface OnStoreItemClickListener{
        void onClickHeader(StoreHolder holder, StoreWrapper item);
        void onClickItem(StoreHolder holder, StoreWrapper item);
        boolean onLongCLickItem(StoreHolder holder, StoreWrapper item);
    }

    private SparseBooleanArray mSelectedItems;
    private List<StoreWrapper> mData;
    private OnStoreItemClickListener mOnStoreItemClickListener;

    public StoreListAdapter(List<StoreWrapper> data, OnStoreItemClickListener ll){
        this.mData = data;
        mOnStoreItemClickListener = ll;
        mSelectedItems = new SparseBooleanArray();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
         View view = inflater.inflate(R.layout.item_store_list_item, null);
        RecyclerView.ViewHolder holder = new StoreHolder(view);

        if(viewType == StoreWrapper.TYPE_ADD_HEADER){

        }else if(viewType == StoreWrapper.TYPE_STORE_ITEM){

        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        StoreWrapper item = getItem(position);
        if(item != null && holder != null){
            if(getItemViewType(position) == StoreWrapper.TYPE_ADD_HEADER){
                onBindStoreHeader((StoreHolder) holder, item, position);
            }else if(getItemViewType(position) == StoreWrapper.TYPE_STORE_ITEM){
                onBindStoreItem((StoreHolder) holder, item, position);
            }
        }
    }

    private void onBindStoreHeader(final StoreHolder holder, final StoreWrapper item, int pos){
        if(holder != null && item != null){
            holder.icon.setImageResource(R.drawable.ic_add_white_20dp);
            holder.name.setText(R.string.add_store);
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mOnStoreItemClickListener != null){
                        mOnStoreItemClickListener.onClickHeader(holder, item);
                    }
                }
            });
        }
    }

    private void onBindStoreItem(final StoreHolder holder, final StoreWrapper item, int pos) {
        if(holder != null && item != null){
            holder.icon.setImageResource(R.drawable.ic_shop_white_20dp);
            holder.name.setText(item.store.getName());
            holder.container.setBackgroundColor(mSelectedItems.get(pos)
                    ? holder.container.getResources().getColor(R.color.lightBlue)
                    : holder.container.getResources().getColor(R.color.dimGray));

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mOnStoreItemClickListener != null){
                        mOnStoreItemClickListener.onClickItem(holder, item);
                    }
                }
            });

            holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(mOnStoreItemClickListener != null){
                        return mOnStoreItemClickListener.onLongCLickItem(holder, item);
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).type;
    }

    @Override
    public StoreWrapper getItem(int pos) {
        return mData.get(pos);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public List<StoreWrapper> getData() {
        return mData;
    }

    public static final class StoreHolder extends RecyclerView.ViewHolder{

        public final View container;
        public final View layout;
        public final ImageView icon;
        public final TextView name;

        public StoreHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.layout_item_store_list_container);
            layout = itemView.findViewById(R.id.layout_item_store_list_item);
            icon = (ImageView) itemView.findViewById(R.id.iv_item_store_list_item);
            name = (TextView) itemView.findViewById(R.id.tv_item_store_list_item);
        }
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
}

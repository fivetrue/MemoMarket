package com.fivetrue.market.memo.ui.adapter.store;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.model.dto.StoreData;
import com.fivetrue.market.memo.ui.adapter.BaseAdapterImpl;

import java.util.List;

/**
 * Created by kwonojin on 2017. 2. 26..
 */

public class StoreNameListAdapter extends BaseAdapter implements BaseAdapterImpl<StoreData> {

    private static final String TAG = "StoreNameListAdapter";

    private Context mContext;
    private List<StoreData> mData;

    public StoreNameListAdapter(Context context, List<StoreData> list){
        this.mContext = context;
        this.mData = list;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder holder = new Holder();
        StoreData store = getItem(i);
        if(view == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.item_store_list_popup, null);
            holder.name = (TextView) view.findViewById(R.id.tv_item_store_list_name);
            view.setTag(holder);
        }else{
            holder = (Holder) view.getTag();
            if(holder == null){
                LayoutInflater inflater = LayoutInflater.from(mContext);
                view = inflater.inflate(R.layout.item_store_list_popup, null);
                holder.name = (TextView) view.findViewById(R.id.tv_item_store_list_name);
                view.setTag(holder);
            }
        }

        holder.name.setText(store.name);
        return view;
    }

    @Override
    public StoreData getItem(int pos) {
        return mData.get(pos);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public List<StoreData> getData() {
        return mData;
    }

    public void setData(List<StoreData> data){
        this.mData = data;
        notifyDataSetChanged();
    }

    @Override
    public void add(StoreData data) {

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
    public List<StoreData> getSelections() {
        return null;
    }

    private static class Holder{
        public TextView name;
    }
}

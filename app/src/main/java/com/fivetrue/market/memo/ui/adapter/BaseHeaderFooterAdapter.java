package com.fivetrue.market.memo.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ViewGroup;

import com.fivetrue.market.memo.LL;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kwonojin on 2017. 4. 20..
 */

public abstract class BaseHeaderFooterAdapter<V> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements BaseAdapterImpl<V>{

    private static final String TAG = "BaseHeaderFooterAdapter";

    public interface OnItemClickListener<V>{
        void onItemClick(RecyclerView.ViewHolder holder, int pos, V item);
        boolean onItemLongClick(RecyclerView.ViewHolder holder, int pos, V item);
    }

    public static final int HEADER = 0x98;
    public static final int FOOTER = 0x99;
    public static final int ITEM = 0x9A;

    private SparseBooleanArray mSelectedItems;
    private List<V> mData;

    private OnItemClickListener mOnItemClickListener;

    private boolean mEditMode = true;


    public BaseHeaderFooterAdapter(List<V> data){
        this.mData = data;
        mSelectedItems = new SparseBooleanArray();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == FOOTER){
            return onCreateFooterHolder(parent.getContext(), viewType);
        }else if(viewType == HEADER){
            return onCreateHeaderHolder(parent.getContext(), viewType);
        }
        return onCreateHolder(parent.getContext(), viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(LL.D)
            Log.d(TAG, "onBindViewHolder() called with: holder = [" + holder + "], position = [" + position + "]");
        if(getItemViewType(position) == HEADER){
            onBindHeaderHolder(holder, position);
        }else if(getItemViewType(position) == FOOTER){
            onBindFooterHolder(holder, position);
        }else{
            onBindHolder(holder, position - (isShowingHeader() ? 1 : 0));
        }
    }


    protected void onBindHeaderHolder(RecyclerView.ViewHolder holder, int position){

    }

    protected void onBindFooterHolder(RecyclerView.ViewHolder holder, int position){

    }

    protected abstract RecyclerView.ViewHolder onCreateFooterHolder(Context context, int viewType);

    protected abstract RecyclerView.ViewHolder onCreateHeaderHolder(Context context, int viewType);

    protected abstract RecyclerView.ViewHolder onCreateHolder(Context context, int viewType);

    protected abstract void onBindHolder(final RecyclerView.ViewHolder holder, final int position);

    protected abstract boolean isShowingFooter();

    protected abstract boolean isShowingHeader();

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && isShowingHeader()) {
            return HEADER;
        }else if (position == (getData().size() + (isShowingHeader() ? 1 : 0))
                && isShowingFooter()) {
            return FOOTER;
        }else{
            return ITEM;
        }
    }

    @Override
    public V getItem(int pos) {
        if(mData.size() > pos){
            return mData.get(pos);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        int itemCount = getData().size();
        if(isShowingFooter()){
            itemCount++;
        }

        if(isShowingHeader()){
            itemCount++;
        }

        return itemCount;
    }

    @Override
    public List<V> getData() {
        return mData;
    }

    @Override
    public void setData(List<V> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public void add(V data) {
        mData.add(data);
        notifyItemChanged(mData.size());
    }

    @Override
    public void toggle(int pos) {
        if(getItemViewType(pos) == FOOTER || getItemViewType(pos) == HEADER
                || !mEditMode){
            return;
        }
        int p = pos - (isShowingHeader() ? 1 : 0);
        mSelectedItems.put(p, !mSelectedItems.get(p));
        notifyItemChanged(pos);
    }

    @Override
    public boolean isSelect(int pos) {
        return mSelectedItems.get(pos);
    }

    @Override
    public void selection(int pos, boolean b) {
        if(getItemViewType(pos) == FOOTER
                || getItemViewType(pos) == HEADER
                || !mEditMode){
            return;
        }

        mSelectedItems.put(pos - (isShowingHeader() ? 1 : 0) , b);
        notifyItemChanged(pos);
    }

    @Override
    public void clearSelection() {
        mSelectedItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public List<V> getSelections() {
        ArrayList<V> list = new ArrayList<>();

        int itemCount = getItemCount();

        if(isShowingHeader()){
            itemCount --;
        }

        if(isShowingFooter()){
            itemCount --;
        }

        for(int i = 0 ; i < itemCount ; i++){
            if(mSelectedItems.get(i)){
                list.add(getItem(i));
            }
        }
        return list;
    }

    @Override
    public void clear(){
        mData.clear();
        clearSelection();
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener<V> ll){
        mOnItemClickListener = ll;
    }

    public void setEditMode(boolean b){
        mEditMode = b;
        notifyDataSetChanged();
    }

    public boolean isEditMode(){
        return mEditMode;
    }

    protected void onClickItem(RecyclerView.ViewHolder holder, int pos, V item){
        if(mOnItemClickListener != null){
            mOnItemClickListener.onItemClick(holder, pos + (isShowingHeader() ? 1 : 0), item);
        }
    }

    protected boolean onLongClickItem(RecyclerView.ViewHolder holder, int pos, V item){
        if(mOnItemClickListener != null){
            return mOnItemClickListener.onItemLongClick(holder, pos + (isShowingHeader() ? 1 : 0), item);
        }
        return false;
    }
}

package com.fivetrue.market.memo.ui.adapter.list;

import android.content.Context;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.model.Product;
import com.fivetrue.market.memo.ui.ProductAddActivity;
import com.fivetrue.market.memo.ui.adapter.BaseAdapterImpl;
import com.fivetrue.market.memo.ui.adapter.ItemTouchHelperAdapter;
import com.fivetrue.market.memo.ui.adapter.holder.HolderClickEvent;
import com.fivetrue.market.memo.ui.adapter.holder.HolderMoreEvent;
import com.fivetrue.market.memo.ui.adapter.holder.ProductAddHolder;
import com.fivetrue.market.memo.ui.adapter.holder.ProductFooterHolder;
import com.fivetrue.market.memo.ui.adapter.holder.ProductHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;


/**
 * Created by kwonojin on 2017. 1. 26..
 */

public class ProductListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements BaseAdapterImpl<Product>, HolderClickEvent, HolderMoreEvent {

    private static final String TAG = "ProductListAdapter";

    public static final int PRODUCT_ADD = 0x00;
    public static final int PRODUCT = 0x01;
    public static final int FOOTER = 0x02;

    private PublishSubject<ClickEvent> clickEventSubject = PublishSubject.create();
    private PublishSubject<MoreEvent> moreEventSubject = PublishSubject.create();

    private SparseBooleanArray mSelectedItems;
    private List<Product> mData;

    private boolean mShowAddButton = true;

    public ProductListAdapter(List<Product> data){
        this.mData = data;
        mSelectedItems = new SparseBooleanArray();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == FOOTER){
            return ProductFooterHolder.makeHolder(parent.getContext());
        }else if(viewType == PRODUCT_ADD){
            return ProductAddHolder.makeHolder(parent.getContext());
        }else{
            return ProductHolder.makeHolder(parent.getContext());
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(getItemViewType(position) == FOOTER){
            onBindFooterHolder((ProductFooterHolder) holder, position);
        }else if(getItemViewType(position) == PRODUCT_ADD){

        }else if(getItemViewType(position) == PRODUCT){
            onBindProductHolder((ProductHolder) holder, position);
        }
    }

    protected void onBindFooterHolder(ProductFooterHolder holder, int position){

    }

    protected void onBindProductAddHolder(ProductAddHolder holder, int pos){
    }


    protected void onBindProductHolder(final ProductHolder holder, final int position){
        final Product item = getItem(position);
        if(holder != null && item != null){
            holder.setProduct(item, isSelect(position));
            holder.mBinding.layout.setOnClickListener(view -> {
                clickEventSubject.onNext(new ClickEvent(ClickType.Click, holder));
            });

            holder.mBinding.more.setOnClickListener(view -> {
                showPopup(view.getContext(), view, item, position);
            });
        }
    }

    protected void showPopup(Context context, View view, Product item, int position){
        ListPopupWindow popupWindow = makePopup(context, item, position);
        popupWindow.setContentWidth((int) (((View) view.getParent()).getWidth() * 0.7));
        popupWindow.setAnchorView(view);
        popupWindow.setDropDownGravity(Gravity.LEFT);
        popupWindow.show();
    }

    protected ListPopupWindow makePopup(Context context, Product item, int position){
        final ListPopupWindow popupWindow = new ListPopupWindow(context);
        String [] listItems = {
                context.getString(R.string.buy),
                context.getString(R.string.duplicate)
                , context.getString(R.string.delete)};
        popupWindow.setAdapter(new ArrayAdapter(context,  android.R.layout.simple_list_item_1, listItems));
        popupWindow.setOnItemClickListener((adapterView, view1, i, l) -> {
            popupWindow.dismiss();
            switch (i){
                case 0 :
                    publishMoreEvent(new MoreEvent(MoreType.Buy, position, item));
                    break;
                case 1 :
                    publishMoreEvent(new MoreEvent(MoreType.Duplicate, position, item));
                    break;
                case 2 :
                    publishMoreEvent(new MoreEvent(MoreType.Delete, position, item));
                    break;
            }
            clearSelection();
        });
        return popupWindow;
    }

    public void showAddButton(boolean b){
        mShowAddButton = b;
        notifyDataSetChanged();
    }

    protected void publishMoreEvent(MoreEvent event){
        moreEventSubject.onNext(event);
    }


    @Override
    public int getItemViewType(int position) {
        if(mShowAddButton){
            if(mData.size() == position){
                return PRODUCT_ADD;
            }else if(mData.size() < position){
                return FOOTER;
            }
        }else{
            if(mData.size() == position){
                return FOOTER;
            }
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

        int extraCount = mShowAddButton ? 2 : 1;

        if (mData.size() == 0) {
            //Return 1 here to show nothing
            return extraCount;
        }

        // Add extra view to show the footer view
        return mData.size() + extraCount;
    }

    @Override
    public List<Product> getData() {
        return mData;
    }

    @Override
    public void setData(List<Product> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public void add(Product data) {
        mData.add(data);
        notifyItemChanged(mData.size());
    }

    public void remove(Product p){
        int index = mData.indexOf(p);
        if(index >= 0){
            mData.remove(index);
            notifyItemRemoved(index);
        }
    }

    @Override
    public void toggle(int pos) {
        if(getItemViewType(pos) == FOOTER || getItemViewType(pos) == PRODUCT_ADD){
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
        if(getItemViewType(pos) == FOOTER || getItemViewType(pos) == PRODUCT_ADD){
            return;
        }

        mSelectedItems.put(pos, b);
        notifyItemChanged(pos);
    }

    @Override
    public void clearSelection() {
        mSelectedItems.clear();
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

    @Override
    public void clear(){
        mData.clear();
        clearSelection();
    }

    @Override
    public Observable<ClickEvent> getClickObservable() {
        return clickEventSubject;
    }

    @Override
    public Observable<MoreEvent> getMoreObservable(){
        return moreEventSubject;
    }
}

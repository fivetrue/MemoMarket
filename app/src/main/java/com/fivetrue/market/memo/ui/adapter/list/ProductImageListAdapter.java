package com.fivetrue.market.memo.ui.adapter.list;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.model.Product;
import com.fivetrue.market.memo.ui.adapter.BaseAdapterImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kwonojin on 2017. 2. 22..
 */

public class ProductImageListAdapter extends BaseAdapter implements BaseAdapterImpl<Product> {

    private static final String TAG = "ProductImageListAdapter";

    private Context mContext;
    private List<Product> mData;
    private SparseBooleanArray mSelection;

    public ProductImageListAdapter(Context context, List<Product> images){
        mContext = context;
        mData = images;
        mSelection = new SparseBooleanArray();
    }

    @Override
    public int getCount() {
        return getItemCount();
    }

    @Override
    public Product getItem(int pos) {
        return mData.get(pos);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder holder = new Holder();
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_image_list_item, null);
            holder.layout = view.findViewById(R.id.layout_item_image_list_item);
            holder.image = (ImageView) view.findViewById(R.id.iv_item_image_list_item);
            holder.check = view.findViewById(R.id.iv_item_image_list_item_check);
            view.setTag(holder);
        }else{
            holder = (Holder) view.getTag();
            if(holder == null){
                view = LayoutInflater.from(mContext).inflate(R.layout.item_image_list_item, null);
                holder.layout = view.findViewById(R.id.layout_item_image_list_item);
                holder.image = (ImageView) view.findViewById(R.id.iv_item_image_list_item);
                holder.check = view.findViewById(R.id.iv_item_image_list_item_check);
                view.setTag(holder);
            }
        }

        Product product = getItem(i);
        if(product != null){
            Glide.with(holder.image.getContext()).load(product.getImageUrl())
                    .dontTransform()
                    .into(holder.image);
        }
        return view;
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
        notifyDataSetChanged();
    }

    @Override
    public void add(Product data) {
        mData.add(data);
        notifyDataSetChanged();
    }

    @Override
    public void toggle(int pos) {
        mSelection.put(pos, !mSelection.get(pos));
        notifyDataSetChanged();
    }

    @Override
    public boolean isSelect(int pos) {
        return mSelection.get(pos);
    }

    @Override
    public void selection(int pos, boolean b) {
        mSelection.put(pos, b);
        notifyDataSetChanged();
    }

    @Override
    public void clearSelection() {
        mSelection.clear();
        notifyDataSetChanged();
    }

    @Override
    public void clear() {
        mData.clear();
        clearSelection();
    }

    @Override
    public List<Product> getSelections() {
        ArrayList<Product> images = new ArrayList<>();
        for(int i  = 0 ; i < getItemCount() ; i ++){
            if(mSelection.get(i)){
                images.add(getItem(i));
            }
        }
        return images;
    }

    private static final class Holder{
        public View layout;
        public ImageView image;
        public View check;
    }
}

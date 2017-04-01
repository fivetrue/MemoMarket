package com.fivetrue.market.memo.ui.adapter.image;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.model.image.Image;
import com.fivetrue.market.memo.ui.adapter.BaseAdapterImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kwonojin on 2017. 2. 22..
 */

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ImageViewHolder> implements BaseAdapterImpl<Image> {

    private static final String TAG = "ImageListAdapter";

    private List<Image> mData;
    private SparseBooleanArray mSelection;

    public ImageListAdapter(List<Image> images){
        mData = images;
        mSelection = new SparseBooleanArray();
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageViewHolder holder = new ImageViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image_list_item, null));
        return holder;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, final int position) {
        Image image = getItem(position);
        if(image != null){
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for(Image i : getSelections()){
                        toggle(getData().indexOf(i));
                    }
                    toggle(position);
                }
            });
            Glide.with(holder.image.getContext()).load(image.thumbnailUrl)
                    .dontTransform()
                    .into(holder.image);
            holder.check.setVisibility(isSelect(position) ? View.VISIBLE : View.GONE);
        }

    }

    @Override
    public Image getItem(int pos) {
        return mData.get(pos);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public List<Image> getData() {
        return mData;
    }

    @Override
    public void setData(List<Image> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public void add(Image data) {
        mData.add(data);
        notifyItemChanged(mData.size());
    }

    @Override
    public void toggle(int pos) {
        mSelection.put(pos, !mSelection.get(pos));
        notifyItemChanged(pos);
    }

    @Override
    public boolean isSelect(int pos) {
        return mSelection.get(pos);
    }

    @Override
    public void selection(int pos, boolean b) {
        mSelection.put(pos, b);
        notifyItemChanged(pos);
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
    public List<Image> getSelections() {
        ArrayList<Image> images = new ArrayList<>();
        for(int i  = 0 ; i < getItemCount() ; i ++){
            if(mSelection.get(i)){
                images.add(getItem(i));
            }
        }
        return images;
    }

    public static final class ImageViewHolder extends RecyclerView.ViewHolder{

        public View layout;
        public ImageView image;
        public View check;
        public ImageViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout_item_image_list_item);
            image = (ImageView) itemView.findViewById(R.id.iv_item_image_list_item);
            check = itemView.findViewById(R.id.iv_item_image_list_item_check);
        }
    }
}

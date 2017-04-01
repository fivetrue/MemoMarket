package com.fivetrue.market.memo.ui.adapter.product;

import android.content.Context;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.database.RealmDB;
import com.fivetrue.market.memo.model.vo.Product;
import com.fivetrue.market.memo.preference.DefaultPreferenceUtil;
import com.fivetrue.market.memo.ui.adapter.BaseAdapterImpl;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by kwonojin on 2017. 1. 26..
 */

public class TimelineListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements BaseAdapterImpl<Product> {

    private static final String TAG = "TimelineListAdapter";

    public static final int PRODUCT = 0x01;
    public static final int FOOTER = 0x02;

    private SparseBooleanArray mSelectedItems;
    private List<Product> mData;

    public TimelineListAdapter(List<Product> data){
        this.mData = data;
        mSelectedItems = new SparseBooleanArray();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == FOOTER){
            View view = inflater.inflate(R.layout.item_product_list_footer, null);
            RecyclerView.ViewHolder holder = new TimelineListAdapter.ProductFooter(view);
            return holder;
        }else{
            View view = inflater.inflate(R.layout.item_product_list_item, null);
            RecyclerView.ViewHolder holder = new TimelineListAdapter.ProductHolder(view);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(getItemViewType(position) == FOOTER){

        }else{
            onBindProductHolder((TimelineListAdapter.ProductHolder) holder, position);
        }
    }

    private void onBindFooterHolder(TimelineListAdapter.ProductFooter holder, int position){

    }

    private void onBindProductHolder(final TimelineListAdapter.ProductHolder holder, final int position){
        final Product item = getItem(position);
        if(holder != null && item != null){
            holder.setProduct(item, isSelect(position));
            holder.layout.setOnClickListener(view -> {
            });

            holder.layout.setOnLongClickListener(view -> {
                return false;
            });

            holder.more.setOnClickListener(view -> {
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
        String [] listItems = {context.getString(R.string.delete)
                , context.getString(R.string.checkout)};
        popupWindow.setAdapter(new ArrayAdapter(context,  android.R.layout.simple_list_item_1, listItems));
        popupWindow.setOnItemClickListener((adapterView, view1, i, l) -> {
            popupWindow.dismiss();
            switch (i){
                case 0 :
                    RealmDB.get().executeTransaction(realm -> {
                        item.deleteFromRealm();
                        notifyItemRemoved(position);
                    });
                    break;
                case 1 :
                    RealmDB.get().executeTransaction(realm -> {
                        item.setCheckOut(true);
                        notifyItemRemoved(position);

                    });
                    break;
            }
            Toast.makeText(view1.getContext()
                    , String.format("%s %s", listItems[i], item.getName())
                    , Toast.LENGTH_SHORT).show();
        });
        return popupWindow;
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
        notifyDataSetChanged();
    }

    @Override
    public void add(Product data) {
        mData.add(data);
        notifyItemChanged(mData.size());
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
    public void clear() {
        mData.clear();
        clearSelection();
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
        public final ImageView more;

        public ProductHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout_item_product_list_item);
            image = (ImageView) itemView.findViewById(R.id.iv_item_product_list_item_image);
            name = (TextView) itemView.findViewById(R.id.tv_item_product_list_item_name);
            check = (ImageView) itemView.findViewById(R.id.iv_item_product_list_item_check);
            badge = (ImageView) itemView.findViewById(R.id.iv_item_product_list_item_badge);
            more = (ImageView) itemView.findViewById(R.id.iv_item_product_list_item_more);
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
            more.setVisibility(b ? View.INVISIBLE : View.VISIBLE);
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
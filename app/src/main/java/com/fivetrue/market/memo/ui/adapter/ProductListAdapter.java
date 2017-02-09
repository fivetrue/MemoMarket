package com.fivetrue.market.memo.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.database.RealmDB;
import com.fivetrue.market.memo.model.Product;

import java.util.List;

/**
 * Created by kwonojin on 2017. 1. 26..
 */

public class ProductListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements BaseAdapterImpl<Product>{

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
         View view = inflater.inflate(R.layout.item_store_list_item, null);
        RecyclerView.ViewHolder holder = new ProductHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Product item = getItem(position);
        final ProductHolder productHolder = (ProductHolder) holder;
        if(holder != null && item != null){
            productHolder.name.setText(item.getName());
            long count = RealmDB.getInstance().get().where(Product.class).equalTo("storeName", item.getName()).count();
            if(LL.D) Log.d(TAG, "onBindViewHolder: store name = " + item.getName() + ", product count : " + count);
            productHolder.price.setText(item.getPrice() + "");
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

    public static final class ProductHolder extends RecyclerView.ViewHolder{

        public final View layout;
        public final TextView name;
        public final TextView price;
        public final ImageView check;

        public ProductHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout_item_product_list_item);
            name = (TextView) itemView.findViewById(R.id.tv_item_product_list_item_name);
            price = (TextView) itemView.findViewById(R.id.tv_item_product_list_item_price);
            check = (ImageView) itemView.findViewById(R.id.iv_item_product_list_item_check);
        }
    }

    public boolean togglePosition(int pos){
        boolean b = !mSelectedItems.get(pos);
        mSelectedItems.put(pos, b);
        notifyItemChanged(pos);
        return b;
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

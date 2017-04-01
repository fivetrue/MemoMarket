package com.fivetrue.market.memo.ui.adapter.product;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.model.vo.Product;
import com.fivetrue.market.memo.ui.adapter.BaseAdapterImpl;

import java.util.List;

/**
 * Created by kwonojin on 2017. 3. 30..
 */

public class ProductCheckOutListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements BaseAdapterImpl<Product> {

    private static final String TAG = "ProductCheckOutListAdap";

    private List<Product> mProducts;

    public ProductCheckOutListAdapter(List<Product> products){
        mProducts = products;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_check_out_item, null);
        return new ProductCheckoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public Product getItem(int pos) {
        return mProducts.get(pos);
    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    @Override
    public List<Product> getData() {
        return mProducts;
    }

    @Override
    public void setData(List<Product> data) {
        mProducts = data;
        notifyDataSetChanged();
    }

    @Override
    public void add(Product data) {

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
    public List<Product> getSelections() {
        return mProducts;
    }

    public static final class ProductCheckoutViewHolder extends RecyclerView.ViewHolder{

        public ProductCheckoutViewHolder(View itemView) {
            super(itemView);
        }
    }
}

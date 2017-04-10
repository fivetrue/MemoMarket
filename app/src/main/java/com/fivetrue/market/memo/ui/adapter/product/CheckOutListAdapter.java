package com.fivetrue.market.memo.ui.adapter.product;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.database.product.ProductDB;
import com.fivetrue.market.memo.model.vo.Product;
import com.fivetrue.market.memo.ui.adapter.BaseAdapterImpl;
import com.fivetrue.market.memo.ui.adapter.store.StoreNameListAdapter;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by kwonojin on 2017. 3. 30..
 */

public class CheckOutListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements BaseAdapterImpl<Product> {

    private static final String TAG = "CheckOutListAdapter";

    public interface OnClickCheckoutProductListener{
        void onAcceptProduct(CheckOutViewHolder holder, Product product);
        void onDeleteProduct(CheckOutViewHolder holder, Product product);
    }

    private List<Product> mProducts;

    private OnClickCheckoutProductListener mOnClickCheckoutProductListener;

    public CheckOutListAdapter(List<Product> products, OnClickCheckoutProductListener ll){
        mProducts = products;
        mOnClickCheckoutProductListener = ll;
    }

    @Override
    public Product getItem(int pos) {
        return mProducts.get(pos);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CheckOutViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_check_out_item, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CheckOutViewHolder viewHolder = (CheckOutViewHolder) holder;
        Product p = getItem(position);
        viewHolder.setData(p);
        viewHolder.revert.setOnClickListener(v ->
                mOnClickCheckoutProductListener.onDeleteProduct((CheckOutViewHolder) holder, p)
        );

        viewHolder.accept.setOnClickListener(view ->
                mOnClickCheckoutProductListener.onAcceptProduct((CheckOutViewHolder) holder, p)
        );

        viewHolder.storeInput.setOnFocusChangeListener((view, b) -> {
            if(b){
                ((EditText)view).selectAll();
                List<String> data = Observable.fromIterable(ProductDB.getInstance().getProducts())
                        .filter(product -> !TextUtils.isEmpty(product.getStoreName()))
                        .map(product -> product.getStoreName())
                        .distinct()
                        .toList().blockingGet();

                StoreNameListAdapter adapter = new StoreNameListAdapter(view.getContext(), data);
                final ListPopupWindow popupWindow = new ListPopupWindow(view.getContext());
                popupWindow.setAdapter(adapter);
                popupWindow.setOnItemClickListener((adapterView, view1, i, l) -> {
                    popupWindow.dismiss();
                    if(adapter != null && adapter.getItemCount() > i){
                        viewHolder.storeInput.setText(adapter.getItem(i));
                    }
                });
                popupWindow.setAnchorView(view);
                popupWindow.show();
                viewHolder.storeInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if(popupWindow != null){
                            popupWindow.dismiss();
                        }
                    }
                });
            }
        });
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
    }

    @Override
    public void add(Product data) {
        mProducts.add(data);
        notifyItemInserted(mProducts.size());
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
        mProducts.clear();
        notifyDataSetChanged();
    }

    @Override
    public List<Product> getSelections() {
        return null;
    }

    public static final class CheckOutViewHolder extends RecyclerView.ViewHolder{

        private View view;
        public ImageView mainImage;
        public TextView productName;
        public TextView barcode;
        public EditText priceInput;
        public EditText storeInput;
        private FloatingActionButton revert;
        private FloatingActionButton accept;

        public CheckOutViewHolder(View view){
            super(view);
            this.view = view;
            mainImage = (ImageView) view.findViewById(R.id.iv_item_checkout_main);
            productName = (TextView) view.findViewById(R.id.tv_item_checkout_name);
            barcode = (TextView) view.findViewById(R.id.tv_item_checkout_barcode);
            priceInput = (EditText) view.findViewById(R.id.et_item_checkout_price);
            storeInput = (EditText) view.findViewById(R.id.et_item_checkout_store);
            revert = (FloatingActionButton) view.findViewById(R.id.fab_item_checkout_revert);
            accept = (FloatingActionButton) view.findViewById(R.id.fab_item_checkout_accept);
        }

        public void setData(Product product){
            if(product != null){
                Glide.with(view.getContext()).load(product.getImageUrl())
                        .dontTransform().into(mainImage);
                productName.setText(product.getName());
                barcode.setText(product.getBarcode());
                priceInput.setText(product.getPrice() > 0 ? product.getPrice() + "" : "");
                storeInput.setText(product.getStoreName());
            }
        }
    }
}

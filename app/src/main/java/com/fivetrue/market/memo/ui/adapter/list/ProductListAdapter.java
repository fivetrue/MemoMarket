package com.fivetrue.market.memo.ui.adapter.list;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.database.FirebaseDB;
import com.fivetrue.market.memo.database.RealmDB;
import com.fivetrue.market.memo.database.product.ProductDB;
import com.fivetrue.market.memo.model.vo.Product;
import com.fivetrue.market.memo.preference.DefaultPreferenceUtil;
import com.fivetrue.market.memo.ui.ProductAddActivity;
import com.fivetrue.market.memo.ui.ProductCheckOutActivity;
import com.fivetrue.market.memo.ui.adapter.BaseAdapterImpl;
import com.fivetrue.market.memo.utils.AdUtil;
import com.fivetrue.market.memo.utils.SimpleViewUtils;
import com.fivetrue.market.memo.utils.TrackingUtil;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by kwonojin on 2017. 1. 26..
 */

public class ProductListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements BaseAdapterImpl<Product> {

    private static final String TAG = "ProductListAdapter";

    public static final int PRODUCT_ADD = 0x00;
    public static final int PRODUCT = 0x01;
    public static final int FOOTER = 0x02;

    public interface OnProductItemListener {
        void onClickItem(ProductListAdapter.ProductHolder holder, Product item);
        boolean onLongClickItem(ProductListAdapter.ProductHolder holder, Product item);
    }

    private SparseBooleanArray mSelectedItems;
    private List<Product> mData;
    private ProductListAdapter.OnProductItemListener mProductItemListener;

    private boolean mShowAddButton = true;

    public ProductListAdapter(List<Product> data, ProductListAdapter.OnProductItemListener ll){
        this.mData = data;
        mProductItemListener = ll;
        mSelectedItems = new SparseBooleanArray();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == FOOTER){
            View view = inflater.inflate(R.layout.item_product_list_footer, null);
            RecyclerView.ViewHolder holder = new ProductFooterHolder(view);
            return holder;
        }else if(viewType == PRODUCT_ADD){
            View view = inflater.inflate(R.layout.item_product_list_add, null);
            RecyclerView.ViewHolder holder = new ProductListAdapter.ProductAddHolder(view);
            return holder;
        }else{
            View view = inflater.inflate(R.layout.item_product_list_item, null);
            RecyclerView.ViewHolder holder = new ProductListAdapter.ProductHolder(view);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(getItemViewType(position) == FOOTER){
            onBindFooterHolder((ProductFooterHolder) holder, position);
        }else if(getItemViewType(position) == PRODUCT_ADD){
            onBindProductAddHolder((ProductAddHolder) holder, position);
        }else if(getItemViewType(position) == PRODUCT){
            onBindProductHolder((ProductListAdapter.ProductHolder) holder, position);
        }
    }

    private void onBindFooterHolder(ProductFooterHolder holder, int position){
    }

    private void onBindProductAddHolder(ProductListAdapter.ProductAddHolder holder, int position){
        holder.layout.setOnClickListener(view -> {
            view.getContext().startActivity(ProductAddActivity.makeIntent(view.getContext(), TAG));
        });

    }

    private void checkoutProduct(Context context, Product product){
        long mills = System.currentTimeMillis();
        ProductDB.get().executeTransaction(realm -> {
            product.setCheckOutDate(mills);
            TrackingUtil.getInstance().checkoutProduct(product.getName()
                    , product.getBarcode()
                    , product.getPrice()
                    , product.getStoreName());

            FirebaseDB.getInstance(context)
                    .addProduct(product).addOnCompleteListener(task -> {
//                notifyDataSetChanged();
            });
        });
    }

    protected void onBindProductHolder(final ProductListAdapter.ProductHolder holder, final int position){
        final Product item = getItem(position);
        if(holder != null && item != null){
            holder.setProduct(item, isSelect(position));
            holder.layout.setOnClickListener(view -> {
                if(mProductItemListener != null){
                    mProductItemListener.onClickItem(holder, item);
                }
            });

            holder.layout.setOnLongClickListener(view -> {
                if(mProductItemListener != null){
                    return mProductItemListener.onLongClickItem(holder, item);
                }
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
        String [] listItems = {
                context.getString(R.string.buy),
                context.getString(R.string.duplicate)
                , context.getString(R.string.delete)};
        popupWindow.setAdapter(new ArrayAdapter(context,  android.R.layout.simple_list_item_1, listItems));
        popupWindow.setOnItemClickListener((adapterView, view1, i, l) -> {
            popupWindow.dismiss();
            switch (i){
                case 0 :
//                    Intent intent = ProductCheckOutActivity.makeIntent(context, TAG, item);
//                    context.startActivity(intent);
                    checkoutProduct(context, item);

                    break;
                case 1 :
                    Product p = new Product();
                    p.setName(item.getName());
                    p.setStoreName(item.getStoreName());
                    p.setBarcode(item.getBarcode());
                    p.setPrice(item.getPrice());
                    p.setImageUrl(item.getImageUrl());
                    p.setCheckInDate(System.currentTimeMillis());
                    ProductDB.getInstance().add(p);

                    break;
                case 2 :
                    new AlertDialog.Builder(context)
                            .setTitle(R.string.delete)
                            .setMessage(R.string.delete_product_message)
                            .setPositiveButton(android.R.string.ok, (dialogInterface, i1) -> {
                                dialogInterface.dismiss();
                                RealmDB.get().executeTransaction(realm -> {
                                    Toast.makeText(view1.getContext()
                                            , String.format("%s \"%s\"", listItems[i], item.getName())
                                            , Toast.LENGTH_SHORT).show();
                                    TrackingUtil.getInstance().deleteProduct(item.getName(), TAG);
                                    item.deleteFromRealm();
                                    notifyItemRemoved(position);
                                });
                            }).setNegativeButton(android.R.string.cancel, (dialogInterface, i1) -> dialogInterface.dismiss())
                            .show();

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
        notifyDataSetChanged();
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
                    .setDuration(200L)
                    .start();
        }
    }

    public static final class ProductAddHolder extends RecyclerView.ViewHolder{

        public View layout;

        public ProductAddHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout_item_product_list_add);
        }
    }

    public static final class ProductFooterHolder extends RecyclerView.ViewHolder{

        public FrameLayout layout;

        public ProductFooterHolder(View itemView) {
            super(itemView);
            layout = (FrameLayout) itemView.findViewById(R.id.layout_item_product_list_footer);
        }
    }
}

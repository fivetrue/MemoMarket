package com.fivetrue.market.memo.ui.adapter.product;

import android.content.Context;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.model.vo.Product;
import com.fivetrue.market.memo.ui.adapter.BaseAdapterImpl;
import com.fivetrue.market.memo.utils.CommonUtils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.observables.GroupedObservable;


/**
 * Created by kwonojin on 2017. 1. 26..
 */

public class TimelineListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements BaseAdapterImpl<GroupedObservable<Long, Product>> {

    private static final String TAG = "TimelineListAdapter";

    public static final int PRODUCT = 0x01;
    public static final int FOOTER = 0x02;

    private SparseBooleanArray mSelectedItems;
    private List<GroupedObservable<Long, Product>> mData;
    private Map<GroupedObservable<Long, Product> , List<Product>> mDataMap = new HashMap<>();

    public TimelineListAdapter(List<GroupedObservable<Long, Product>> data){
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
            View view = inflater.inflate(R.layout.item_timeline_list_item, null);
            RecyclerView.ViewHolder holder = new TimelineListAdapter.TimelineHolder(view);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(getItemViewType(position) == FOOTER){

        }else{
            onBindProductHolder((TimelineListAdapter.TimelineHolder) holder, position);
        }
    }

    private void onBindFooterHolder(TimelineListAdapter.ProductFooter holder, int position){

    }

    private void onBindProductHolder(final TimelineListAdapter.TimelineHolder holder, final int position){
        final GroupedObservable<Long, Product> item = getItem(position);
        if(holder != null && item != null){
            List<Product> products = mDataMap.get(item);
            if(products == null){
                products = item.toList().blockingGet();
                mDataMap.put(item, products);

            }
            holder.setData(item, products);
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

    protected void showPopup(Context context, View view, GroupedObservable<Long, Product> item, int position){
        ListPopupWindow popupWindow = makePopup(context, item, position);
        popupWindow.setContentWidth((int) (((View) view.getParent()).getWidth() * 0.7));
        popupWindow.setAnchorView(view);
        popupWindow.setDropDownGravity(Gravity.LEFT);
        popupWindow.show();
    }

    protected ListPopupWindow makePopup(Context context, GroupedObservable<Long, Product> item, int position){
        final ListPopupWindow popupWindow = new ListPopupWindow(context);
        String [] listItems = {context.getString(R.string.delete)
                , context.getString(R.string.checkout)};
        popupWindow.setAdapter(new ArrayAdapter(context,  android.R.layout.simple_list_item_1, listItems));
        popupWindow.setOnItemClickListener((adapterView, view1, i, l) -> {
            popupWindow.dismiss();
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
    public GroupedObservable<Long, Product> getItem(int pos) {
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
    public List<GroupedObservable<Long, Product>> getData() {
        return mData;
    }

    @Override
    public void setData(List<GroupedObservable<Long, Product>> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public void add(GroupedObservable<Long, Product> data) {
        mData.add(data);
        notifyItemInserted(mData.size());
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
    public List<GroupedObservable<Long, Product>> getSelections() {
        return null;
    }

    public static final class TimelineHolder extends RecyclerView.ViewHolder{

        public final View layout;
        public final GridView images;
        public final TextView date;
        public final TextView count;
        public final TextView price;
        public final ImageView more;

        public TimelineHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout_item_timeline_list_layout);
            images = (GridView) itemView.findViewById(R.id.gv_item_timeline_list_images);
            date = (TextView) itemView.findViewById(R.id.tv_item_timeline_list_date);
            count = (TextView) itemView.findViewById(R.id.tv_item_timeline_list_count);
            price = (TextView) itemView.findViewById(R.id.tv_item_timeline_list_price);
            more = (ImageView) itemView.findViewById(R.id.iv_item_timeline_list_more);
        }

        public void setData(GroupedObservable<Long, Product> data, List<Product> products){
            Locale locale = date.getContext().getResources().getConfiguration().locale;
            String formatted = DateFormat.getBestDateTimePattern(locale, "MM/dd/yyyy");
            SimpleDateFormat sdf = new SimpleDateFormat(formatted);
            date.setText(sdf.format(new Date(data.getKey())));
            count.setText("( " + products.size() + " )");
            long value = 0;
            for(Product p : products){
                value += p.getPrice();
            }
            price.setText(CommonUtils.convertToCurrency(value));
            if(images.getAdapter() == null){
                images.setAdapter(new ProductImageListAdapter(images.getContext()
                        , products));
            }else{
                ((ProductImageListAdapter)images.getAdapter()).setData(products);
            }
        }
    }

    public static final class ProductFooter extends RecyclerView.ViewHolder{
        public ProductFooter(View itemView) {
            super(itemView);
        }
    }
}

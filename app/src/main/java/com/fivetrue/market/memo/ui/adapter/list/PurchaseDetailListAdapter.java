package com.fivetrue.market.memo.ui.adapter.list;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.model.vo.Product;
import com.fivetrue.market.memo.ui.adapter.BaseAdapterImpl;
import com.fivetrue.market.memo.utils.CommonUtils;
import com.fivetrue.market.memo.utils.DataManager;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.akarnokd.rxjava2.math.MathFlowable;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by kwonojin on 2017. 1. 26..
 */

public class PurchaseDetailListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements BaseAdapterImpl<GroupedObservable<String, Product>> {

    private static final String TAG = "PurchaseListAdapter";

    public static final int PRODUCT = 0x01;
    public static final int FOOTER = 0x02;

    public interface OnPurchaseItemListener {
        void onClickItem(PurchaseDetailHolder holder, List<Product> items);
        boolean onLongClickItem(PurchaseDetailHolder holder, List<Product> item);
    }

    private static final SimpleDateFormat SDF = new SimpleDateFormat("M/d");

    private SparseBooleanArray mSelectedItems;
    private List<GroupedObservable<String, Product>> mData;
    private Map<GroupedObservable<String, Product> , List<Product>> mDataMap = new HashMap<>();

    private OnPurchaseItemListener mOnPurchaseItemListener;

    public PurchaseDetailListAdapter(List<GroupedObservable<String, Product>> data, OnPurchaseItemListener ll){
        this.mData = data;
        mSelectedItems = new SparseBooleanArray();
        mOnPurchaseItemListener = ll;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == FOOTER){
            View view = inflater.inflate(R.layout.item_product_list_footer, null);
            RecyclerView.ViewHolder holder = new FooterHolder(view);
            return holder;
        }else{
            View view = inflater.inflate(R.layout.item_purchase_detail_list_item, null);
            RecyclerView.ViewHolder holder = new PurchaseDetailHolder(view);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(getItemViewType(position) == FOOTER){

        }else{
            onBindProductHolder((PurchaseDetailHolder) holder, position);
        }
    }

    private void onBindFooterHolder(FooterHolder holder, int position){

    }

    private void onBindProductHolder(final PurchaseDetailHolder holder, final int position){
        final GroupedObservable<String, Product> item = getItem(position);
        if(holder != null && item != null){
            List<Product> products = mDataMap.get(item);
            if(products == null){
                products = item.toList().blockingGet();
                mDataMap.put(item, products);

            }
            holder.setData(item, products);
            holder.layout.setOnClickListener(view -> {
                if(mOnPurchaseItemListener != null){
                    mOnPurchaseItemListener.onClickItem(holder, mDataMap.get(item));
                }
            });

            holder.layout.setOnLongClickListener(view -> {
                if(mOnPurchaseItemListener != null){
                    return mOnPurchaseItemListener.onLongClickItem(holder, mDataMap.get(item));
                }
                return false;
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(mData.size() == position){
            return FOOTER;
        }
        return PRODUCT;
    }

    @Override
    public GroupedObservable<String, Product> getItem(int pos) {
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
    public List<GroupedObservable<String, Product>> getData() {
        return mData;
    }

    @Override
    public void setData(List<GroupedObservable<String, Product>> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public void add(GroupedObservable<String, Product> data) {
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
        if(getItemViewType(pos) == FOOTER){
            return false;
        }
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
    public List<GroupedObservable<String, Product>> getSelections() {
        return null;
    }

    public static final class PurchaseDetailHolder extends RecyclerView.ViewHolder{

        public final View layout;
        public final ImageView image;
        public final GraphView graph;
        public final TextView avg;

        public PurchaseDetailHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout_purchase_detail_list_item);
            image = (ImageView) itemView.findViewById(R.id.iv_item_purchase_detail_list_item);
            graph = (GraphView) itemView.findViewById(R.id.graph_item_purchase_detail_list_item);
            avg = (TextView) itemView.findViewById(R.id.tv_item_purchase_detail_list_item);
        }

        public void setData(GroupedObservable<String, Product> data, List<Product> products){
            DataManager.getInstance(layout.getContext())
                    .findImages(data.getKey(), 0)
                    .subscribe(img -> {

                        String imageUrl = null;
                        if(img != null && img.size() > 0){
                            imageUrl = img.get(0).getThumbnailUrl();
                            Glide.with(layout.getContext())
                                    .load(imageUrl)
                                    .placeholder(R.drawable.ic_product_gray_50dp)
                                    .dontTransform().into(image);
                        }

                    }, throwable -> {
                        Log.e(TAG, "setData: ", throwable);
                    });
            DataPoint[] dataPoints = new DataPoint[products.size()];
            for(int i = 0 ; i < products.size() ; i ++){
                Product p = products.get(i);
                dataPoints[i] = new DataPoint(p.getCheckOutDate(), p.getPrice());
            }
            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
            series.setBackgroundColor(image.getResources().getColor(android.R.color.white));
            series.setDrawBackground(false);
            series.setThickness(2);

            MathFlowable.averageDouble(Flowable.fromIterable(products).map(product -> product.getPrice()))
                    .subscribe(aDouble -> avg.setText(avg.getContext().getString(R.string.average, CommonUtils.convertToCurrency(aDouble.longValue()))));

            graph.setTitleColor(image.getResources().getColor(R.color.darkBlue));
            graph.setTitle(data.getKey() + " (" + products.size() + ")");
            graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if (isValueX) {
                        return SDF.format(new Date((long) value));
                    } else {
                        // show currency for y values
                        return CommonUtils.convertToCurrency((long) value);
                    }
                }
            });
            graph.addSeries(series);
        }
    }

    public static final class FooterHolder extends RecyclerView.ViewHolder{
        public FooterHolder(View itemView) {
            super(itemView);
        }
    }
}

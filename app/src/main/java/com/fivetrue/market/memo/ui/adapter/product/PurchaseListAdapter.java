package com.fivetrue.market.memo.ui.adapter.product;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.model.vo.Product;
import com.fivetrue.market.memo.ui.adapter.BaseAdapterImpl;
import com.fivetrue.market.memo.utils.ExportUtil;
import com.fivetrue.market.memo.utils.CommonUtils;
import com.fivetrue.market.memo.utils.MediaScannerUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.observables.GroupedObservable;


/**
 * Created by kwonojin on 2017. 1. 26..
 */

public class PurchaseListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements BaseAdapterImpl<GroupedObservable<String, Product>> {

    private static final String TAG = "PurchaseListAdapter";

    public static final int PRODUCT = 0x01;
    public static final int FOOTER = 0x02;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    private SparseBooleanArray mSelectedItems;
    private List<GroupedObservable<String, Product>> mData;
    private Map<GroupedObservable<String, Product> , List<Product>> mDataMap = new HashMap<>();

    public PurchaseListAdapter(List<GroupedObservable<String, Product>> data){
        this.mData = data;
        mSelectedItems = new SparseBooleanArray();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == FOOTER){
            View view = inflater.inflate(R.layout.item_product_list_footer, null);
            RecyclerView.ViewHolder holder = new TimelineFooterHolder(view);
            return holder;
        }else{
            View view = inflater.inflate(R.layout.item_timeline_list_item, null);
            RecyclerView.ViewHolder holder = new PurchaseListAdapter.TimelineHolder(view);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(getItemViewType(position) == FOOTER){

        }else{
            onBindProductHolder((PurchaseListAdapter.TimelineHolder) holder, position);
        }
    }

    private void onBindFooterHolder(TimelineFooterHolder holder, int position){

    }

    private void onBindProductHolder(final PurchaseListAdapter.TimelineHolder holder, final int position){
        final GroupedObservable<String, Product> item = getItem(position);
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

    protected void showPopup(Context context, View view, GroupedObservable<String, Product> item, int position){
        ListPopupWindow popupWindow = makePopup(context, item, position);
        popupWindow.setContentWidth((int) (((View) view.getParent()).getWidth() * 0.7));
        popupWindow.setAnchorView(view);
        popupWindow.setDropDownGravity(Gravity.LEFT);
        popupWindow.show();
    }

    protected ListPopupWindow makePopup(Context context, GroupedObservable<String, Product> item, int position){
        final ListPopupWindow popupWindow = new ListPopupWindow(context);
        String [] listItems = {context.getString(R.string.delete)
                , context.getString(R.string.export)};
        popupWindow.setAdapter(new ArrayAdapter(context,  android.R.layout.simple_list_item_1, listItems));
        popupWindow.setOnItemClickListener((adapterView, view1, i, l) -> {
            popupWindow.dismiss();
            switch (i){
                case 0 :
                    //TODO : delete
                    break;

                case 1 :
                    //TODO : export
                    new AlertDialog.Builder(context)
                            .setTitle(R.string.export)
                            .setMessage(R.string.export)
                            .setPositiveButton("Excel", (dialogInterface, i1) -> {
                                try {
                                    String filepath = ExportUtil.writeProductToExcelInExternalStorage(context
                                            , SDF.format(new Date(System.currentTimeMillis()))
                                            , mDataMap.get(item));
                                    shareFile(context, filepath, item.getKey());
                                } catch (ExportUtil.ExportException e) {
                                    Toast.makeText(context, "Export failure", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "makePopup: ", e);
                                }
                                dialogInterface.dismiss();

                            }).setNegativeButton("CSV", (dialogInterface, i1) -> {
                                 try {
                                   String filepath = ExportUtil.writeProductsToCVSInExternalStorage(context
                                    , SDF.format(new Date(System.currentTimeMillis()))
                                    , mDataMap.get(item));
                                    shareFile(context, filepath, item.getKey());
                                 } catch (ExportUtil.ExportException e) {
                                  Toast.makeText(context, "Export failure", Toast.LENGTH_SHORT).show();
                                  Log.e(TAG, "makePopup: ", e);
                                 }
                                 dialogInterface.dismiss();
                            }).setNeutralButton(android.R.string.cancel, (dialogInterface, i1) -> dialogInterface.dismiss())
                            .show();
                    break;
            }
        });
        return popupWindow;
    }

    private void shareFile(Context context, String subject, String filePath){
        MediaScannerUtil.getInstance(context).mediaScanning(filePath)
                .subscribe(scanData -> {
                    Log.d(TAG, "makePopup: scan data" + scanData);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(scanData.path)));
                    intent.setType("text/*");
                    context.startActivity(Intent.createChooser(intent, context.getString(R.string.export)));
                });
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

        public void setData(GroupedObservable<String, Product> data, List<Product> products){
            date.setText(data.getKey());
            count.setText("( " + products.size() + " )");
            long value = 0;
            for(Product p : products){
                value += p.getPrice();
            }
            price.setText(CommonUtils.convertToCurrency(value));
            images.setAdapter(new ProductImageListAdapter(images.getContext()
                    , products));
        }
    }

    public static final class TimelineFooterHolder extends RecyclerView.ViewHolder{
        public TimelineFooterHolder(View itemView) {
            super(itemView);
        }
    }
}

package com.fivetrue.market.memo.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.database.product.ProductDB;
import com.fivetrue.market.memo.model.vo.Product;
import com.fivetrue.market.memo.ui.adapter.list.PurchaseDetailListAdapter;
import com.fivetrue.market.memo.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import hu.akarnokd.rxjava2.math.MathFlowable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.observables.GroupedObservable;

/**
 * Created by kwonojin on 2017. 2. 7..
 */

public class PurchaseDetailListFragment extends BaseFragment{

    private static final String TAG = "PurchaseListFragment";

    private static final String KEY_PURCHASE_ITEMS = "purchase_items";

    private RecyclerView mRecyclerView;
    private Spinner mSpinner;
    private TextView mNoItem;
    private TextView mTotalPrice;
    private TextView mTotalCount;

    private PurchaseDetailListAdapter mAdapter;
    private ArrayAdapter<Filter> mSpinnerAdapter;

    private LinearLayoutManager mLayoutManager;

    private long[] mPurchaseItemCheckouts;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void loadData(){
        List<GroupedObservable<String,Product>> list = Observable.fromIterable(getProducts())
                .sorted((product, t1) -> sort(product, t1))
                .groupBy(product -> groupBy(product)).toList().blockingGet();
        setData(list);
    }

    private List<Product> getProducts(){
        mPurchaseItemCheckouts = getArguments().getLongArray(KEY_PURCHASE_ITEMS);
        List<Product> list = Flowable.fromIterable(ProductDB.getInstance().getProducts())
                .filter(product -> product.getCheckOutDate() > 0)
                .filter(product -> {
                    for(long l : mPurchaseItemCheckouts){
                        if(product.getCheckOutDate() == l){
                            return true;
                        }
                    }
                    return false;
                }).toList().blockingGet();
        Log.d(TAG, "getProducts() returned: " + list);
        return list;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_purchase_list, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_fragment_purchase_list);
        mNoItem = (TextView) view.findViewById(R.id.tv_fragment_purchase_list);
        mTotalPrice = (TextView) view.findViewById(R.id.tv_fragment_purchase_list_total_price);
        mTotalCount = (TextView) view.findViewById(R.id.tv_fragment_purchase_list_total_count);
        mSpinner = (Spinner) view.findViewById(R.id.sp_fragment_purchase_list);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        List<Filter> filters = new ArrayList<>();
        filters.add(new Filter(getString(R.string.product), Filter.Value.PRODUCT));
        filters.add(new Filter(getString(R.string.store), Filter.Value.STORE));

        mSpinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, filters);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        mSpinner.setAdapter(mSpinnerAdapter);
        mSpinner.setSelection(0);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        loadData();
    }

    public void setData(List<GroupedObservable<String, Product>> data){
        if(data != null){
            if(mAdapter == null){
                mAdapter = new PurchaseDetailListAdapter(data, new PurchaseDetailListAdapter.OnPurchaseItemListener() {
                    @Override
                    public void onClickItem(PurchaseDetailListAdapter.PurchaseDetailHolder holder, List<Product> items) {

                    }

                    @Override
                    public boolean onLongClickItem(PurchaseDetailListAdapter.PurchaseDetailHolder holder, List<Product> item) {
                        return false;
                    }
                });
                mRecyclerView.setAdapter(mAdapter);
            }else{
                mAdapter.setData(data);
            }
        }

        MathFlowable.sumLong(Flowable.fromIterable(getProducts())
                .map(product -> product.getPrice()))
                .subscribe(aLong -> mTotalPrice.setText(CommonUtils.convertToCurrency(aLong)));

        Flowable.fromIterable(getProducts())
                .count()
                .subscribe(aLong
                        -> mTotalCount.setText(String.format(getString(R.string.total_product_count), aLong)));


        validation();
    }


    private int sort(Product t1, Product t2){
        return t1.getCheckOutDate() < t2.getCheckOutDate() ? -1 : 1;
    }

    private String groupBy(Product product){
        if(getActivity() != null){
            Filter filter = mSpinnerAdapter.getItem(mSpinner.getSelectedItemPosition());
            switch (filter.value){
                case PRODUCT:{
                    return product.getName();
                }
                case STORE:{
                    return product.getStoreName();
                }
            }
        }
        return null;
    }

    private void validation(){
        if(mNoItem != null){
            mNoItem.setVisibility(mAdapter != null && mAdapter.getItemCount() -1 > 0
                    ? View.GONE : View.VISIBLE);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.purchase_list);
    }

    @Override
    public int getImageResource() {
        return R.drawable.selector_feed;
    }

    public static Bundle makeArgument(Context context, List<Product> products){
        List<Long> list = Observable.fromIterable(products)
                .map(product -> product.getCheckOutDate())
                .toList().blockingGet();

        long[] array = new long[list.size()];
        for(int i = 0 ; i < list.size() ; i ++){
            array[i] = list.get(i);
        }
        Bundle arg = new Bundle();
        arg.putLongArray(KEY_PURCHASE_ITEMS, array);
        return arg;
    }

    private static class Filter{
        enum Value {PRODUCT, STORE}
        public final String name;
        public final Value value;
        public Filter(String name, Value value){
            this.name = name;
            this.value = value;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}

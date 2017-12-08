package com.fivetrue.market.memo.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.databinding.FragmentPurchaseListBinding;
import com.fivetrue.market.memo.model.Product;
import com.fivetrue.market.memo.ui.adapter.list.PurchaseDetailListAdapter;
import com.fivetrue.market.memo.utils.CommonUtils;
import com.fivetrue.market.memo.viewmodel.ProductListViewModel;

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

    private PurchaseDetailListAdapter mAdapter;
    private ArrayAdapter<Filter> mSpinnerAdapter;

    private LinearLayoutManager mLayoutManager;

    private long[] mPurchaseItemCheckouts;

    private FragmentPurchaseListBinding mBinding;
    private ProductListViewModel mViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ProductListViewModel.class);
    }

    private void loadData(){
        mPurchaseItemCheckouts = getArguments().getLongArray(KEY_PURCHASE_ITEMS);
        mViewModel.getProductList().observe(this, products -> {
            Observable.fromIterable(products)
                    .filter(product -> product.getCheckOutDate() > 0)
                    .filter(product -> filter(product))
                    .sorted((product, t1) -> sort(product, t1))
                    .groupBy(product -> groupBy(product))
                    .toList().subscribe((groupedObservables, throwable) -> setData(groupedObservables));

            MathFlowable.sumLong(Flowable.fromIterable(products)
                    .filter(product -> filter(product))
                    .map(product -> product.getPrice()))
                    .subscribe(aLong -> mBinding.totalPrice.setText(CommonUtils.convertToCurrency(aLong)));

            Flowable.fromIterable(products)
                    .filter(product -> filter(product))
                    .count()
                    .subscribe(aLong
                            -> mBinding.totalCount.setText(String.format(getString(R.string.total_product_count), aLong)));
        });

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
        mBinding = DataBindingUtil.bind(view);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mBinding.list.setLayoutManager(mLayoutManager);


        List<Filter> filters = new ArrayList<>();
        filters.add(new Filter(getString(R.string.product), Filter.Value.PRODUCT));
        filters.add(new Filter(getString(R.string.store), Filter.Value.STORE));

        mSpinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, filters);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.spinner.setAdapter(mSpinnerAdapter);
        mBinding.spinner.setSelection(0);
        mBinding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        if(data != null && data.size() > 0){
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
                mBinding.list.setAdapter(mAdapter);
            }else{
                mAdapter.setData(data);
            }
            mBinding.setShowEmpty(data.isEmpty());
        }else{
            getFragmentManager().popBackStackImmediate();
        }
    }


    private boolean filter(Product product){
        for(long l : mPurchaseItemCheckouts){
            if(product.getCheckOutDate() == l){
                return true;
            }
        }
        return false;
    }

    private int sort(Product t1, Product t2){
        return t1.getCheckOutDate() < t2.getCheckOutDate() ? -1 : 1;
    }

    private String groupBy(Product product){
        if(getActivity() != null){
            Filter filter = mSpinnerAdapter.getItem(mBinding.spinner.getSelectedItemPosition());
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

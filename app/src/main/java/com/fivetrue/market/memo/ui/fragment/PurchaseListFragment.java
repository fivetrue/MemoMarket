package com.fivetrue.market.memo.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.databinding.FragmentPurchaseListBinding;
import com.fivetrue.market.memo.model.Product;
import com.fivetrue.market.memo.ui.MainActivity;
import com.fivetrue.market.memo.ui.ProductAddActivity;
import com.fivetrue.market.memo.ui.adapter.list.PurchaseListAdapter;
import com.fivetrue.market.memo.utils.CommonUtils;
import com.fivetrue.market.memo.view.PagerTabContent;
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

public class PurchaseListFragment extends BaseFragment implements PagerTabContent{

    private static final String TAG = "PurchaseListFragment";

    private PurchaseListAdapter mAdapter;
    private ArrayAdapter<Filter> mSpinnerAdapter;

    private FragmentPurchaseListBinding mBinding;

    private ProductListViewModel mViewModel;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ProductListViewModel.class);
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mBinding.list.setLayoutManager(layoutManager);
        mBinding.emptyText.setOnClickListener(v -> {
            if(getActivity() != null){
                v.getContext().startActivity(ProductAddActivity.makeIntent(view.getContext(), TAG));
                if(getActivity() instanceof MainActivity){
                    ((MainActivity) getActivity()).movePageToPosition(0);
                }
            }
        });

        List<Filter> filters = new ArrayList<>();
        filters.add(new Filter(getString(R.string.day), Filter.Value.DAY));
        filters.add(new Filter(getString(R.string.week), Filter.Value.WEEK));
        filters.add(new Filter(getString(R.string.month), Filter.Value.MONTH));
        filters.add(new Filter(getString(R.string.year), Filter.Value.YEAR));

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
        mBinding.layoutBottom.setOnClickListener(null);
        loadData();
    }

    private void loadData(){
        mViewModel.getProductList().observe(this, productList -> {
            Observable.fromIterable(productList)
                    .filter(product -> filter(product))
                    .sorted((product, t1) -> sort(product, t1))
                    .groupBy(product -> groupBy(product))
                    .toList().subscribe((groupedObservables, throwable) -> setData(groupedObservables));

            MathFlowable.sumLong(Flowable.fromIterable(productList)
                    .filter(product -> filter(product))
                    .map(product -> product.getPrice()))
                    .subscribe(aLong -> mBinding.totalPrice.setText(CommonUtils.convertToCurrency(aLong)));

            Flowable.fromIterable(productList)
                    .filter(product -> filter(product))
                    .count()
                    .subscribe(aLong
                            -> mBinding.totalCount.setText(String.format(getString(R.string.total_product_count), aLong)));
        });
    }

    public void setData(List<GroupedObservable<String, Product>> data){
        if(data != null){
            if(mAdapter == null){
                mAdapter = new PurchaseListAdapter(data, new PurchaseListAdapter.OnPurchaseItemListener() {
                    @Override
                    public void onClickItem(PurchaseListAdapter.PurchaseHolder holder, List<Product> items) {
                        if(getActivity() != null && items != null){
                            getChildFragmentManager().popBackStackImmediate();
                            Bundle arg = PurchaseDetailListFragment.makeArgument(getActivity(), items);
                            addFragment(PurchaseDetailListFragment.class, arg
                                    , R.id.fragment_anchor, true
                                    , android.R.anim.slide_in_left, android.R.anim.slide_out_right, null, null);
                        }
                    }

                    @Override
                    public boolean onLongClickItem(PurchaseListAdapter.PurchaseHolder holder, List<Product> item) {
                        return false;
                    }
                });
                mBinding.list.setAdapter(mAdapter);
            }else{
                mAdapter.setData(data);
            }
        }

        mBinding.setShowEmpty(data.isEmpty());
    }

    protected boolean filter(Product product){
        return product.getCheckOutDate() > 0;
    }

    protected int sort(Product t1, Product t2){
        return t1.getCheckOutDate() < t2.getCheckOutDate() ? 1 : -1;
    }

    protected String groupBy(Product product){
        if(getActivity() != null){
            Filter filter = mSpinnerAdapter.getItem(mBinding.spinner.getSelectedItemPosition());
            switch (filter.value){
                case DAY:{
                    return CommonUtils.getDate(getActivity(), "MM dd yyyy", product.getCheckOutDate());
                }
                case WEEK:{
                    String week = CommonUtils.getDate(getActivity(), "W", product.getCheckOutDate());
                    return String.format(getActivity().getString(R.string.date_week)
                            , CommonUtils.getDate(getActivity(), "MM yyyy", product.getCheckOutDate()), week);
                }
                case MONTH:{
                    return CommonUtils.getDate(getActivity(), "MM yyyy", product.getCheckOutDate());
                }

                case YEAR:{
                    return CommonUtils.getDate(getActivity(), "yyyy", product.getCheckOutDate());
                }
            }
        }
        return null;
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

    @Override
    public String getTabTitle(Context context) {
        return getTitle(context);
    }

    @Override
    public Drawable getTabDrawable(Context context) {
        return context.getDrawable(getImageResource());
    }

    @Override
    public TabIcon getIconState() {
        return TabIcon.TextWithIcon;
    }

    public static Bundle makeArgument(Context context){
        Bundle b = new Bundle();
        return b;
    }

    private static class Filter{
        enum Value {DAY, WEEK, MONTH, YEAR}
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

    @Override
    public boolean onBackPressed() {
        if(getChildFragmentManager().getBackStackEntryCount() > 0){
            return getChildFragmentManager().popBackStackImmediate();
        }
        return super.onBackPressed();
    }

}

package com.fivetrue.market.memo.ui.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
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
import com.fivetrue.market.memo.ui.adapter.product.PurchaseListAdapter;
import com.fivetrue.market.memo.utils.CommonUtils;
import com.fivetrue.market.memo.view.PagerTabContent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import hu.akarnokd.rxjava2.math.MathFlowable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observables.GroupedObservable;

/**
 * Created by kwonojin on 2017. 2. 7..
 */

public class PurchaseListFragment extends BaseFragment implements PagerTabContent{

    private static final String TAG = "PurchaseListFragment";

    private RecyclerView mRecyclerView;
    private Spinner mSpinner;
    private TextView mNoItem;
    private TextView mTotalPrice;
    private TextView mTotalCount;

    private PurchaseListAdapter mAdapter;
    private ArrayAdapter<Filter> mSpinnerAdapter;

    private LinearLayoutManager mLayoutManager;

    private Disposable mDisposable;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDisposable = ProductDB.getInstance()
                .getObservable()
                .map(products ->
                    Observable.fromIterable(products)
                            .filter(product -> filter(product))
                            .sorted((product, t1) -> sort(product, t1))
                            .groupBy(product -> groupBy(product)).toList().blockingGet()

                )
                .subscribe(product -> {
                    setData(product);
                });
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timeline_list, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_fragment_timeline);
        mNoItem = (TextView) view.findViewById(R.id.tv_fragment_timeline);
        mTotalPrice = (TextView) view.findViewById(R.id.tv_fragment_timeline_total_price);
        mTotalCount = (TextView) view.findViewById(R.id.tv_fragment_timeline_total_count);
        mSpinner = (Spinner) view.findViewById(R.id.sp_fragment_timeline);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        List<Filter> filters = new ArrayList<>();
        filters.add(new Filter(getString(android.R.string.selectAll), Filter.Value.ALL));
        filters.add(new Filter(getString(R.string.day), Filter.Value.DAY));
        filters.add(new Filter(getString(R.string.week), Filter.Value.WEEK));
        filters.add(new Filter(getString(R.string.month), Filter.Value.MONTH));
        filters.add(new Filter(getString(R.string.year), Filter.Value.YEAR));

        mSpinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, filters);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        mSpinner.setAdapter(mSpinnerAdapter);
        mSpinner.setSelection(0);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ProductDB.getInstance().updatePublish();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ProductDB.getInstance().updatePublish();
    }

    public void setData(List<GroupedObservable<String, Product>> data){
        if(data != null){
            if(mAdapter == null){
                mAdapter = new PurchaseListAdapter(data);
                mRecyclerView.setAdapter(mAdapter);
            }else{
                mAdapter.setData(data);
            }
        }

        MathFlowable.sumLong(Flowable.fromIterable(ProductDB.getInstance().getProducts())
                .filter(product -> filter(product))
                .map(product -> product.getPrice()))
                .subscribe(aLong -> mTotalPrice.setText(CommonUtils.convertToCurrency(aLong)));

        Flowable.fromIterable(ProductDB.getInstance().getProducts())
                .filter(product -> filter(product))
                .count()
                .subscribe(aLong
                        -> mTotalCount.setText(String.format(getString(R.string.total_product_count), aLong)));


        validation();
    }

    private boolean filter(Product product){
        return product.isCheckOut() && product.getCheckOutDate() > 0;
    }

    private int sort(Product t1, Product t2){
        return t1.getCheckOutDate() < t2.getCheckOutDate() ? 1 : -1;
    }

    private String groupBy(Product product){
        if(getActivity() != null){
            Filter filter = mSpinnerAdapter.getItem(mSpinner.getSelectedItemPosition());
            Locale locale = getActivity().getResources().getConfiguration().locale;
            switch (filter.value){
                case ALL:{
                    String formatted = DateFormat.getBestDateTimePattern(locale, "MM dd yyyy HH");
                    SimpleDateFormat sdf = new SimpleDateFormat(formatted);
                    return sdf.format(new Date(product.getCheckOutDate()));
                }
                case DAY:{
                    String formatted = DateFormat.getBestDateTimePattern(locale, "MM dd yyyy");
                    SimpleDateFormat sdf = new SimpleDateFormat(formatted);
                    return sdf.format(new Date(product.getCheckOutDate()));
                }
                case WEEK:{
                    String formatted = DateFormat.getBestDateTimePattern(locale, "W");
                    SimpleDateFormat sdf = new SimpleDateFormat(formatted);
                    String week = sdf.format(new Date(product.getCheckOutDate()));
                    formatted = DateFormat.getBestDateTimePattern(locale, "MM yyyy");
                    sdf = new SimpleDateFormat(formatted);
                    return String.format(getActivity().getString(R.string.date_week), sdf.format(new Date(product.getCheckOutDate())), week);
                }
                case MONTH:{
                    String formatted = DateFormat.getBestDateTimePattern(locale, "MM yyyy");
                    SimpleDateFormat sdf = new SimpleDateFormat(formatted);
                    return sdf.format(new Date(product.getCheckOutDate()));
                }

                case YEAR:{
                    String formatted = DateFormat.getBestDateTimePattern(locale, "yyyy");
                    SimpleDateFormat sdf = new SimpleDateFormat(formatted);
                    return sdf.format(new Date(product.getCheckOutDate()));
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
        if(mDisposable != null && !mDisposable.isDisposed()){
            mDisposable.dispose();
        }
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
        enum Value { ALL, DAY, WEEK, MONTH, YEAR }
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

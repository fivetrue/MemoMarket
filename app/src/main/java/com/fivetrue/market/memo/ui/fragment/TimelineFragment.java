package com.fivetrue.market.memo.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.database.RealmDB;
import com.fivetrue.market.memo.database.product.ProductDB;
import com.fivetrue.market.memo.model.vo.Product;
import com.fivetrue.market.memo.ui.BaseActivity;
import com.fivetrue.market.memo.ui.ProductAddActivity;
import com.fivetrue.market.memo.ui.adapter.product.CheckOutProductListAdapter;
import com.fivetrue.market.memo.ui.adapter.product.ProductListAdapter;
import com.fivetrue.market.memo.ui.adapter.product.TimelineListAdapter;
import com.fivetrue.market.memo.utils.SimpleViewUtils;
import com.fivetrue.market.memo.view.PagerTabContent;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observables.GroupedObservable;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;

/**
 * Created by kwonojin on 2017. 2. 7..
 */

public class TimelineFragment extends BaseFragment implements PagerTabContent{

    private static final String TAG = "TimelineFragment";

    private RecyclerView mRecyclerView;
    private TimelineListAdapter mAdapter;

    private LinearLayoutManager mLayoutManager;

    private Disposable mDisposable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDisposable = ProductDB.getInstance()
                .getObservable()
                .map(products ->
                    Observable.fromIterable(products)
                            .filter(product -> product.isCheckOut() && product.getCheckOutDate() > 0)
                            .groupBy(Product::getCheckOutDate).toList().blockingGet()
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
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        ProductDB.getInstance().updatePublish();
    }

    public void setData(List<GroupedObservable<Long, Product>> data){
        if(data != null){
            if(mAdapter == null){
                mAdapter = new TimelineListAdapter(data.get(0).toList().blockingGet());
                mRecyclerView.setAdapter(mAdapter);
            }else{
                mAdapter.setData(data.get(0).toList().blockingGet());
            }
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
        return context.getString(R.string.timeline);
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


}

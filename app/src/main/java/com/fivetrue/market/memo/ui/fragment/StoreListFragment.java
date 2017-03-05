package com.fivetrue.market.memo.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.Pair;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.database.RealmDB;
import com.fivetrue.market.memo.model.vo.Store;
import com.fivetrue.market.memo.ui.BaseActivity;
import com.fivetrue.market.memo.ui.adapter.store.StoreListAdapter;
import com.fivetrue.market.memo.ui.fragment.transition.MoveTransition;


import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;

/**
 * Created by kwonojin on 2017. 2. 7..
 */

public class StoreListFragment extends BaseFragment{

    private static final String TAG = "StoreListFragment";

    private static final String KEY_SCROLL_POSITION = "scroll_position";

    private NestedScrollView mScrollView;
    private View mContainer;
    private RecyclerView mRecyclerStore;
    private StoreListAdapter mStoreListAdapter;

    private TextView mTextMessage;

    private FloatingActionButton mFabAddStore;

    private RecyclerView.LayoutManager mLayoutManager;
    private int mScrollPos = 0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStoreList(RealmDB.get().where(Store.class).findAll());
        RealmDB.get().addChangeListener(new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm element) {
                setStoreList(RealmDB.get().where(Store.class).findAll());
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mScrollView != null){
            mScrollPos = mScrollView.getScrollY();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_list, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mScrollView = (NestedScrollView) view.findViewById(R.id.sv_fragment_product_list);
        mContainer = view.findViewById(R.id.layout_fragment_product_list);
        mRecyclerStore = (RecyclerView) view.findViewById(R.id.rv_fragment_product_list);
        mTextMessage = (TextView) view.findViewById(R.id.tv_fragment_product_list);

        mFabAddStore = (FloatingActionButton) view.findViewById(R.id.fab_fragment_product_list_add);
        mLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        mRecyclerStore.setLayoutManager(mLayoutManager);
        mRecyclerStore.setItemAnimator(new FadeInAnimator());
        mRecyclerStore.setAdapter(mStoreListAdapter);
        mFabAddStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity() != null && getActivity() instanceof BaseActivity){
                    ((BaseActivity) getActivity()).addFragment(StoreAddFragment.class, null
                            , ((BaseActivity) getActivity()).getDefaultFragmentAnchor(), true
                            , 0, 0
                            , new MoveTransition()
                            , new Pair<View, String>(mFabAddStore, getString(R.string.transition_floating_action_button)));
                }
            }
        });

        mContainer.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right
                    , int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(view != null){
                    view.removeOnLayoutChangeListener(this);
                    ViewAnimationUtils.createCircularReveal(view, left, top
                            , 0, Math.max(right, bottom)).setDuration(250L).start();
                }
            }
        });

        if(LL.D) Log.d(TAG, "onViewCreated: mScrollPos = " + mScrollPos);
        mTextMessage.setVisibility(mStoreListAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);

        mScrollView.setScrollY(mScrollPos);
    }

    private void setStoreList(List<Store> storeList){
        if(LL.D) Log.d(TAG, "setStoreList() called with: storeList = [" + storeList + "]");
        if(storeList != null){
            if(mStoreListAdapter == null){
                mStoreListAdapter = new StoreListAdapter(storeList, new StoreListAdapter.OnStoreItemClickListener() {
                    @Override
                    public void onClickItem(StoreListAdapter.StoreHolder holder, Store item) {
                        if(getActivity() != null && getActivity() instanceof BaseActivity){

                        }
                    }

                    @Override
                    public boolean onLongCLickItem(StoreListAdapter.StoreHolder holder, Store item) {
                        return false;
                    }
                });
            }else{
                mStoreListAdapter.setData(storeList);
            }
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
        return null;
    }

    @Override
    public int getImageResource() {
        return 0;
    }

}

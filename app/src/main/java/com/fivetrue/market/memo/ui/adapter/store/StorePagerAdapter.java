package com.fivetrue.market.memo.ui.adapter.store;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fivetrue.market.memo.model.vo.Store;
import com.fivetrue.market.memo.ui.adapter.BaseFragmentPagerAdapter;
import com.fivetrue.market.memo.ui.fragment.BaseFragment;

import java.util.List;

/**
 * Created by ojin.kwon on 2016-11-18.
 */

public class StorePagerAdapter extends BaseFragmentPagerAdapter {

    private static final String TAG = "StorePagerAdapter";

    private List<FragmentSet> mFragments;

    public StorePagerAdapter(FragmentManager fm, List<FragmentSet> list) {
        super(fm);
        mFragments = list;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = getFragmentManager().findFragmentByTag(makeFragmentName(0, getItemId(position)));
        if(f == null){
            f = mFragments.get(position).getFragment();
        }
        return f;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public int getRealCount() {
        return mFragments.size();
    }

    @Override
    public int getVirtualPosition(int position) {
        return position;
    }

    @Override
    protected String makeFragmentName(int viewId, long id) {
        return super.makeFragmentName(viewId, id) + ":" + mFragments.get((int)id).store.getName();
    }

    public static class FragmentSet{

        private static final String TAG = "FragmentSet";

        public FragmentSet(Class<? extends BaseFragment> cls, Bundle arg, Store store){
            this.cls = cls;
            this.arg = arg;
            this.store = store;
        }
        public final Class<? extends BaseFragment> cls;
        public final Bundle arg;
        public final Store store;
        private BaseFragment fragment;

        private BaseFragment getFragment(){
            if(fragment == null){
                try {
                    fragment = cls.newInstance();
                    fragment.setArguments(arg);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return  fragment;
        }
    }

    public List<FragmentSet> getData(){
        return mFragments;
    }



}

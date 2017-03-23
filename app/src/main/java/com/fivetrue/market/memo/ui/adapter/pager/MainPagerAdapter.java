package com.fivetrue.market.memo.ui.adapter.pager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;


import com.fivetrue.market.memo.ui.adapter.BaseFragmentPagerAdapter;
import com.fivetrue.market.memo.ui.fragment.BaseFragment;

import java.util.List;

/**
 * Created by ojin.kwon on 2016-11-18.
 */

public class MainPagerAdapter extends BaseFragmentPagerAdapter {

    private static final String TAG = "MainPagerAdapter";

    private List<FragmentSet> mFragments;

    public MainPagerAdapter(FragmentManager fm, List<FragmentSet> list) {
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
        return "android:switcher:" + id;
    }

    @Override
    protected boolean ignoreDestroyObject(int position, Object object) {
        return true;
    }

    public static class FragmentSet{

        public FragmentSet(Class<? extends BaseFragment> cls, Bundle arg){
            this.cls = cls;
            this.arg = arg;
        }
        public final Class<? extends BaseFragment> cls;
        public final Bundle arg;
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
}

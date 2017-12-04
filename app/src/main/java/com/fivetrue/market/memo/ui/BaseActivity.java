package com.fivetrue.market.memo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fivetrue.market.memo.MemoApplication;
import com.fivetrue.market.memo.ui.fragment.BaseFragment;
import com.fivetrue.market.memo.utils.TrackingUtil;

/**
 * Created by kwonojin on 2017. 1. 23..
 */

public class BaseActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    protected boolean popFragment(FragmentManager fm){
        boolean b = fm.popBackStackImmediate();
        return b;
    }

    public Fragment addFragment(Class< ? extends BaseFragment> cls, Bundle arguments, int anchorLayout, boolean addBackstack
            , int enterAnim, int exitAnim, @Nullable Object sharedTransition, @Nullable Pair<View, String>... pair){
        BaseFragment f = null;
        try {
            f = cls.newInstance();
            if(sharedTransition != null){
                f.setSharedElementEnterTransition(sharedTransition);
                f.setSharedElementReturnTransition(sharedTransition);
            }
        } catch (InstantiationException e) {
            Log.e(TAG, "addFragment: ", e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "addFragment: ", e);
        }
        if(f != null){
            if(arguments != null){
                f.setArguments(arguments);
            }
            FragmentTransaction ft = getCurrentFragmentManager().beginTransaction();
            ft.setCustomAnimations(enterAnim, exitAnim, enterAnim, exitAnim);
            ft.replace(anchorLayout, f, f.getTitle(this));
            if(pair != null && pair.length > 0){
                for(Pair<View, String> p : pair){
                    ft.addSharedElement(p.first, p.second);
                }
            }
            if(addBackstack){
                ft.addToBackStack(f.getTitle(this));
                ft.setBreadCrumbTitle(f.getTitle(this));
                ft.setBreadCrumbShortTitle(f.getSubTitle(this));
            }
            ft.commitAllowingStateLoss();
        }
        return f;
    }

    protected FragmentManager getCurrentFragmentManager(){
        return getSupportFragmentManager();
    }

    @Override
    public void onBackPressed() {
        if(getCurrentFragmentManager() != null){
            if(getCurrentFragmentManager().getFragments() != null){
                for(Fragment f : getCurrentFragmentManager().getFragments()){
                    if(f != null && f instanceof BaseFragment){
                        if(((BaseFragment) f).onBackPressed()){
                            return;
                        }
                    }
                }
            }

            if(popFragment(getCurrentFragmentManager())){
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    public void onBackStackChanged() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                onClickHome();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onClickHome(){
        if(getCurrentFragmentManager().getBackStackEntryCount() > 0){
            popFragment(getCurrentFragmentManager());
        }else{
            onBackPressed();
        }
    }

    public MemoApplication getApp(){
        return (MemoApplication) getApplicationContext();
    }

}

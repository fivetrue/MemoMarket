package com.fivetrue.market.memo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.view.View;

import com.fivetrue.market.memo.ui.dialog.LoadingDialog;
import com.fivetrue.market.memo.ui.fragment.BaseFragment;
import com.fivetrue.market.memo.utils.SimpleViewUtils;

/**
 * Created by kwonojin on 2017. 1. 23..
 */

public class BaseActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    private static final String TAG = "BaseActivity";

    private LoadingDialog mLoadingDialog;

    private int mStartX;
    private int mStartY;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        mStartX = getIntent().getIntExtra("startX", getWindow().getDecorView().getWidth() / 2);
        mStartY = getIntent().getIntExtra("startY", getWindow().getDecorView().getHeight() / 2);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportFragmentManager().removeOnBackStackChangedListener(this);
    }

    protected boolean popFragment(FragmentManager fm){
        boolean b = fm.popBackStackImmediate();
        return b;
    }

    public Fragment addFragment(Class< ? extends BaseFragment> cls, Bundle arguments, int anchorLayout, boolean addBackstack){
        return addFragment(cls, arguments, anchorLayout, addBackstack , 0, 0, null, null);
    }

    public Fragment addFragment(Class< ? extends BaseFragment> cls, Bundle arguments, boolean addBackstack){
        return addFragment(cls, arguments, getFragmentAnchorLayoutID(), addBackstack, 0, 0, null, null);
    }

    public Fragment addFragment(Class< ? extends BaseFragment> cls, Bundle arguments, int anchorLayout, boolean addBackstack, int enterAnim, int exitAnim){
        return addFragment(cls, arguments, getFragmentAnchorLayoutID(), addBackstack, enterAnim, exitAnim, null, null);
    }

    public Fragment addFragment(Class< ? extends BaseFragment> cls, Bundle arguments, int anchorLayout, boolean addBackstack
            , int enterAnim, int exitAnim, Object sharedTransition, Pair<View, String> pair){
        BaseFragment f = null;
        try {
            f = (BaseFragment) cls.newInstance();
            if(sharedTransition != null){
                f.setSharedElementEnterTransition(sharedTransition);
                f.setSharedElementReturnTransition(sharedTransition);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if(f != null){
            if(arguments != null){
                f.setArguments(arguments);
            }
            FragmentTransaction ft = getCurrentFragmentManager().beginTransaction();
            ft.setCustomAnimations(enterAnim, exitAnim, enterAnim, exitAnim);
            ft.replace(anchorLayout, f, f.getTitle(this));
            if(pair != null){
                ft.addSharedElement(pair.first, pair.second);
            }
            if(addBackstack){
                ft.addToBackStack(f.getTitle(this));
                ft.setBreadCrumbTitle(f.getTitle(this));
                ft.setBreadCrumbShortTitle(f.getSubTitle(this));
                onAddFragmentBackStack(f);
            }
            ft.commitAllowingStateLoss();
        }
        return f;
    }

    protected void onAddFragmentBackStack(BaseFragment f){

    }

    protected FragmentManager getCurrentFragmentManager(){
        return getSupportFragmentManager();
    }

    protected int getFragmentAnchorLayoutID(){
        return android.R.id.content;
    }

    protected boolean transitionModeWhenFinish(){
        return false;
    }

    protected View getDecorView(){
        return null;
    }

    @Override
    public void onBackPressed() {
        if(popFragment(getCurrentFragmentManager())){
            return;
        }
        if(transitionModeWhenFinish()){
            SimpleViewUtils.hideView(getDecorView(), View.INVISIBLE, mStartX, mStartY, new SimpleViewUtils.SimpleAnimationStatusListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd() {
                    supportFinishAfterTransition();
                }
            });
        }else{
            super.onBackPressed();
        }
    }

    protected void startActivityWithClipRevealAnimation(Intent intent, View view){

        int[] location = new int[2];
        view.getLocationInWindow(location);

        intent.putExtra("startX", location[0] + view.getWidth() / 2);
        intent.putExtra("startY", location[1] + view.getHeight() / 2);
        startActivity(intent, ActivityOptionsCompat.makeClipRevealAnimation(view,
                (int) view.getX(), (int) view.getY()
                , view.getWidth(), view.getHeight() ).toBundle());
        overridePendingTransition(0, 0);
    }

    protected void startActivityForResultWithClipRevealAnimation(Intent intent, int requestCode, View view){

        int[] location = new int[2];
        view.getLocationInWindow(location);

        intent.putExtra("startX", location[0] + view.getWidth() / 2);
        intent.putExtra("startY", location[1] + view.getHeight() / 2);
        startActivityForResult(intent, requestCode, ActivityOptionsCompat.makeClipRevealAnimation(view,
                (int) view.getX(), (int) view.getY()
                , view.getWidth(), view.getHeight() ).toBundle());
        overridePendingTransition(0, 0);
    }



    @Override
    public void finish() {
        super.finish();
        if(transitionModeWhenFinish()){
            overridePendingTransition(0, 0);
        }
    }

    public void showLoadingDialog(){
        if(mLoadingDialog == null){
            mLoadingDialog = new LoadingDialog(this);
        }
        if(!mLoadingDialog.isShowing()){
            mLoadingDialog.show();
        }
    }

    public void dismissLoadingDialog(){
        if(mLoadingDialog != null && mLoadingDialog.isShowing()){
            mLoadingDialog.dismiss();
        }
    }

    public int getDefaultFragmentAnchor(){
        return android.R.id.content;
    }

    @Override
    public void onBackStackChanged() {

    }
}

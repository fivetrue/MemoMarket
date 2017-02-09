package com.fivetrue.market.memo.ui;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.ui.fragment.StoreListFragment;


public class MainActivity extends BaseActivity{

    private static final String TAG = "MainActivity";

    private MaterialMenuDrawable mMaterialMenuDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportFragmentManager().removeOnBackStackChangedListener(this);
    }

    private void initData(){
        getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mMaterialMenuDrawable = new MaterialMenuDrawable(this, getResources().getColor(R.color.colorAccent), MaterialMenuDrawable.Stroke.THIN);
        toolbar.setNavigationIcon(mMaterialMenuDrawable);
        mMaterialMenuDrawable.setIconState(MaterialMenuDrawable.IconState.X);
        getSupportActionBar().setTitle(R.string.registered_store);
        addFragment(StoreListFragment.class, null, getDefaultFragmentAnchor(), false);
    }

    @Override
    public int getDefaultFragmentAnchor() {
        return R.id.layout_main_anchor;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                if(getCurrentFragmentManager().getBackStackEntryCount() > 0){
                    popFragment(getCurrentFragmentManager());
                }else{
                    //TODO : Something
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackStackChanged() {
        FragmentManager fm = getCurrentFragmentManager();
        if(fm.getBackStackEntryCount() > 0){
            FragmentManager.BackStackEntry backStackEntry = fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1);
            if(backStackEntry != null && backStackEntry.getBreadCrumbTitle() != null){
                getSupportActionBar().setTitle(backStackEntry.getBreadCrumbTitle());
                getSupportActionBar().setSubtitle(backStackEntry.getBreadCrumbShortTitle());
            }else{
                getSupportActionBar().setTitle(R.string.registered_store);
                getSupportActionBar().setSubtitle(null);
            }
            animateActionBarMenu(MaterialMenuDrawable.IconState.ARROW);
        }else{
            getSupportActionBar().setTitle(R.string.registered_store);
            getSupportActionBar().setSubtitle(null);
            animateActionBarMenu(MaterialMenuDrawable.IconState.X);
        }
    }

    private void animateActionBarMenu(MaterialMenuDrawable.IconState state){
        ValueAnimator animator = null;
        switch (state){
            case X:{
                animator = ValueAnimator.ofFloat(0, 1);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        float value = (Float)valueAnimator.getAnimatedValue();
                        mMaterialMenuDrawable.setTransformationOffset(
                                MaterialMenuDrawable.AnimationState.ARROW_X,
                                value);
                    }
                });
            }
            break;

            case ARROW: {
                animator = ValueAnimator.ofFloat(1, 0);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        float value = (Float)valueAnimator.getAnimatedValue();
                        mMaterialMenuDrawable.setTransformationOffset(
                                MaterialMenuDrawable.AnimationState.ARROW_X,
                                value);
                    }
                });
            }
        }
        animator.setDuration(250).start();
    }
}

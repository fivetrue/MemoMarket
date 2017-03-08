package com.fivetrue.market.memo.ui;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.ui.fragment.BaseFragment;
import com.fivetrue.market.memo.ui.fragment.CartFragment;
import com.fivetrue.market.memo.ui.fragment.ProductListFragment;


public class MainActivity extends BaseActivity{

    private static final String TAG = "MainActivity";

    private MaterialMenuDrawable mMaterialMenuDrawable;

    private MenuItem mCartMenu;

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
        mMaterialMenuDrawable = new MaterialMenuDrawable(this, getResources().getColor(R.color.colorPrimary), MaterialMenuDrawable.Stroke.THIN);
        toolbar.setNavigationIcon(mMaterialMenuDrawable);
        mMaterialMenuDrawable.setIconState(MaterialMenuDrawable.IconState.X);
        getSupportActionBar().setTitle(R.string.product);
        addFragment(ProductListFragment.class, null, getDefaultFragmentAnchor(), false);
    }

    @Override
    public int getDefaultFragmentAnchor() {
        return R.id.layout_main_anchor;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mCartMenu = menu.findItem(R.id.action_cart);
        return super.onPrepareOptionsMenu(menu);
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

            default:
                if(getSupportFragmentManager().getFragments() != null){
                    for(Fragment f : getSupportFragmentManager().getFragments()){
                        if(f != null){
                            f.onOptionsItemSelected(item);
                        }
                    }
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackStackChanged() {
        FragmentManager fm = getCurrentFragmentManager();
        if(fm.getBackStackEntryCount() > 0){
            mCartMenu.setVisible(false);
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
            mCartMenu.setVisible(true);
            getSupportActionBar().setTitle(R.string.product);
            getSupportActionBar().setSubtitle(null);
            animateActionBarMenu(MaterialMenuDrawable.IconState.X);
        }
    }

    private void animateActionBarMenu(MaterialMenuDrawable.IconState state){
        if(state != null && mMaterialMenuDrawable != null){
            ValueAnimator animator = null;
            switch (state){
                case X:{
                    if (mMaterialMenuDrawable.getIconState() != MaterialMenuDrawable.IconState.X){
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
                }
                break;

                case ARROW: {
                    if(mMaterialMenuDrawable.getIconState() != MaterialMenuDrawable.IconState.ARROW){
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
            }
            if(animator != null){
                animator.setDuration(250).start();
            }
        }
    }


}

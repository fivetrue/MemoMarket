package com.fivetrue.market.memo.ui;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.ui.adapter.pager.MainPagerAdapter;
import com.fivetrue.market.memo.ui.fragment.BaseFragment;
import com.fivetrue.market.memo.ui.fragment.CheckOutProductFragment;
import com.fivetrue.market.memo.ui.fragment.ProductListFragment;
import com.fivetrue.market.memo.ui.fragment.PurchaseListFragment;
import com.fivetrue.market.memo.utils.CommonUtils;
import com.fivetrue.market.memo.view.BottomNavigationBehavior;
import com.fivetrue.market.memo.view.PagerSlidingTabStrip;

import java.util.ArrayList;


public class MainActivity extends BaseActivity{

    private static final String TAG = "MainActivity";

    private MaterialMenuDrawable mMaterialMenuDrawable;

    private TextView mTitle;

    private PagerSlidingTabStrip mTab;
    private ViewPager mViewPager;

    private MainPagerAdapter mAdapter;

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

        ArrayList<MainPagerAdapter.FragmentSet> fragmentSets = new ArrayList<>();
        fragmentSets.add(new MainPagerAdapter.FragmentSet(ProductListFragment.class, ProductListFragment.makeArgument(this)));
//        fragmentSets.add(new MainPagerAdapter.FragmentSet(CheckOutProductFragment.class, CheckOutProductFragment.makeArgument(this)));
        fragmentSets.add(new MainPagerAdapter.FragmentSet(PurchaseListFragment.class, PurchaseListFragment.makeArgument(this)));
        mAdapter = new MainPagerAdapter(getSupportFragmentManager(), fragmentSets);
    }

    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        mTitle = (TextView) findViewById(R.id.tv_main_title);
        mTitle.setTypeface(CommonUtils.getFont(this,  "font/DroidSansMono.ttf"));
        mTitle.setText(R.string.app_name);
        mTab = (PagerSlidingTabStrip) findViewById(R.id.tab_main);
        mViewPager = (ViewPager) findViewById(R.id.vp_main);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

//        mFabProduct.setOnClickListener(view -> {
//            if(mAdapter != null && mViewPager != null){
//                if(mAdapter.getRealCount() > mViewPager.getCurrentItem()){
//                    Fragment f = mAdapter.getItem(mViewPager.getCurrentItem());
//                    if(f != null
//                            && f instanceof ProductListFragment
//                            && ((ProductListFragment) f).getAdapter().getSelections().size() > 0){
//                        ((ProductListFragment) f).doProducts(findViewById(R.id.layout_main));
//                        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
//                        return;
//                    }
//                }
//            }
//            startActivity(new Intent(MainActivity.this, ProductAddActivity.class));
//        });
//
//        mFabCheckout.setOnClickListener(view -> {
//            if(view.isShown()){
//                if(mAdapter != null && mViewPager != null){
//                    if(mAdapter.getRealCount() > mViewPager.getCurrentItem()){
//                        Fragment f = mAdapter.getItem(mViewPager.getCurrentItem());
//                        if(f != null
//                                && f instanceof CheckOutProductFragment
//                                && ((CheckOutProductFragment) f).getAdapter().getSelections().size() > 0){
//                            ((CheckOutProductFragment) f).doProducts(findViewById(R.id.layout_main));
//                            return;
//                        }
//                    }
//                }
//            }
//        });

        mViewPager.setAdapter(mAdapter);
        mTab.setViewPager(mViewPager);

        mMaterialMenuDrawable = new MaterialMenuDrawable(this, getResources().getColor(android.R.color.white), MaterialMenuDrawable.Stroke.THIN);
        toolbar.setNavigationIcon(mMaterialMenuDrawable);
        mMaterialMenuDrawable.setIconState(MaterialMenuDrawable.IconState.X);

        if(mTab.getLayoutParams() instanceof CoordinatorLayout.LayoutParams){
            CoordinatorLayout.Behavior behavior =
                    ((CoordinatorLayout.LayoutParams) mTab.getLayoutParams()).getBehavior();
            if(behavior != null && behavior instanceof BottomNavigationBehavior){
                ((BottomNavigationBehavior) behavior).setLayoutDependsOn(false);
            }
        }
    }

    public void movePageToRight(){
        if(mViewPager != null && mViewPager.getChildCount() - 1 >  mViewPager.getCurrentItem()){
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
        }
    }

    public void movePageToLeft(){
        if(mViewPager != null && mViewPager.getCurrentItem() > 0){
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
        }
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
            FragmentManager.BackStackEntry backStackEntry = fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1);
            if(backStackEntry != null && backStackEntry.getBreadCrumbTitle() != null){
                mTitle.setText(backStackEntry.getBreadCrumbTitle());
            }else{
                mTitle.setText(R.string.app_name);
            }
            animateActionBarMenu(MaterialMenuDrawable.IconState.ARROW);
        }else{
            mTitle.setText(R.string.app_name);
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
                        animator.addUpdateListener(valueAnimator -> {
                            float value = (Float)valueAnimator.getAnimatedValue();
                            mMaterialMenuDrawable.setTransformationOffset(
                                    MaterialMenuDrawable.AnimationState.ARROW_X,
                                    value);
                        });
                    }
                }
                break;

                case ARROW: {
                    if(mMaterialMenuDrawable.getIconState() != MaterialMenuDrawable.IconState.ARROW){
                        animator = ValueAnimator.ofFloat(1, 0);
                        animator.addUpdateListener(valueAnimator -> {
                            float value = (Float)valueAnimator.getAnimatedValue();
                            mMaterialMenuDrawable.setTransformationOffset(
                                    MaterialMenuDrawable.AnimationState.ARROW_X,
                                    value);
                        });
                    }
                }
            }
            if(animator != null){
                animator.setDuration(250).start();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(mAdapter != null && mViewPager != null){
            if(mAdapter.getRealCount() > mViewPager.getCurrentItem()){
                Fragment f = mAdapter.getItem(mViewPager.getCurrentItem());
                if(f != null && f instanceof BaseFragment){
                    if(((BaseFragment) f).onBackPressed()){
                        return;
                    }
                }
            }
        }
        super.onBackPressed();
    }
}

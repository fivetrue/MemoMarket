package com.fivetrue.market.memo.ui;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.ui.adapter.pager.MainPagerAdapter;
import com.fivetrue.market.memo.ui.fragment.CheckOutProductFragment;
import com.fivetrue.market.memo.ui.fragment.ProductListFragment;
import com.fivetrue.market.memo.ui.fragment.TimelineFragment;
import com.fivetrue.market.memo.utils.CommonUtils;
import com.fivetrue.market.memo.utils.SimpleViewUtils;
import com.fivetrue.market.memo.view.BottomNavigationBehavior;
import com.fivetrue.market.memo.view.PagerSlidingTabStrip;
import com.fivetrue.market.memo.view.PagerTabContent;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;


public class MainActivity extends BaseActivity{

    private static final String TAG = "MainActivity";

    private MaterialMenuDrawable mMaterialMenuDrawable;

    private MenuItem mCartMenu;

    private TextView mTitle;

    private PagerSlidingTabStrip mTab;
    private ViewPager mViewPager;

    private FloatingActionButton mFabAdd;

    private MainPagerAdapter mAdapter;

    private Disposable mProductDisposable;

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
        if(mProductDisposable.isDisposed()){
            mProductDisposable.dispose();
        }
        getSupportFragmentManager().removeOnBackStackChangedListener(this);
    }

    private void initData(){
        getSupportFragmentManager().addOnBackStackChangedListener(this);

        ArrayList<MainPagerAdapter.FragmentSet> fragmentSets = new ArrayList<>();
        fragmentSets.add(new MainPagerAdapter.FragmentSet(TimelineFragment.class, TimelineFragment.makeArgument(this)));
        fragmentSets.add(new MainPagerAdapter.FragmentSet(ProductListFragment.class, ProductListFragment.makeArgument(this)));
        fragmentSets.add(new MainPagerAdapter.FragmentSet(CheckOutProductFragment.class, CheckOutProductFragment.makeArgument(this)));
        mAdapter = new MainPagerAdapter(getSupportFragmentManager(), fragmentSets);
        for(int i = 0 ; i < mAdapter.getRealCount() ; i ++){
            Fragment f = mAdapter.getItem(i);
            if(f != null && f instanceof ProductListFragment){
                mProductDisposable = ((ProductListFragment) f).getObservable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(products -> products.size() > 0)
                        .subscribe(hasSelection -> updateUI(hasSelection));
            }
        }
    }

    private void updateUI(final boolean hasSelection){
        if(mFabAdd.getTag() == null
                || mFabAdd.getTag() instanceof Boolean && ((Boolean)mFabAdd.getTag()) != hasSelection){
            AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(this,
                    hasSelection ? R.animator.card_flip_left : R.animator.card_flip_right);
            set.setTarget(mFabAdd);
            set.start();
            mFabAdd.postDelayed(() -> {
                if(hasSelection){
                    mFabAdd.setImageResource(R.drawable.ic_cart_checkout_white_50dp);
                    mFabAdd.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                }else{
                    mFabAdd.setImageResource(R.drawable.ic_add_white_20dp);
                    mFabAdd.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                }
            }, getResources().getInteger(R.integer.card_flip_time_half));
        }
        mFabAdd.setTag(hasSelection);
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
        mFabAdd = (FloatingActionButton) findViewById(R.id.fab_main_add);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(mAdapter != null && mAdapter.getRealCount() > position){
                    if(mAdapter.getItem(position) instanceof PagerTabContent){
                        PagerTabContent content = (PagerTabContent) mAdapter.getItem(position);
                        if(content.getTabTitle(MainActivity.this).equals(getString(R.string.product))){
                            SimpleViewUtils.showView(mFabAdd, View.VISIBLE);
                        }else{
                            SimpleViewUtils.hideView(mFabAdd, View.GONE);
                        }

                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(mTab != null && mTab.getLayoutParams() instanceof CoordinatorLayout.LayoutParams){
                    CoordinatorLayout.Behavior behavior =
                            ((CoordinatorLayout.LayoutParams) mTab.getLayoutParams()).getBehavior();
                    if(behavior != null && behavior instanceof BottomNavigationBehavior){
                        switch (state){
                            case ViewPager.SCROLL_STATE_DRAGGING :
                            case ViewPager.SCROLL_STATE_SETTLING:
                                ((BottomNavigationBehavior) behavior).setScrollingEnabled(false);
                                return;

                            case ViewPager.SCROLL_STATE_IDLE:
                                ((BottomNavigationBehavior) behavior).setScrollingEnabled(true);
                                return;
                        }
                    }
                }
            }
        });

        mFabAdd.setOnClickListener( view -> {
            if(mAdapter != null && mViewPager != null){
                if(mAdapter.getRealCount() > mViewPager.getCurrentItem()){
                    Fragment f = mAdapter.getItem(mViewPager.getCurrentItem());
                    if(f != null
                            && f instanceof ProductListFragment
                            && ((ProductListFragment) f).getAdapter().getSelections().size() > 0){
                        ((ProductListFragment) f).doProducts(findViewById(R.id.layout_main));
                        return;
                    }
                }
            }
            startActivity(new Intent(MainActivity.this, ProductAddActivity.class));
        });

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
                mTitle.setText(backStackEntry.getBreadCrumbTitle());
            }else{
                mTitle.setText(R.string.app_name);
            }
            animateActionBarMenu(MaterialMenuDrawable.IconState.ARROW);
        }else{
            mCartMenu.setVisible(true);
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

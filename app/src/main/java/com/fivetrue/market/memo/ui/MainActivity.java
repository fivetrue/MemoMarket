package com.fivetrue.market.memo.ui;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.ui.adapter.pager.MainPagerAdapter;
import com.fivetrue.market.memo.ui.fragment.BaseFragment;
import com.fivetrue.market.memo.ui.fragment.ProductListFragment;
import com.fivetrue.market.memo.ui.fragment.PurchaseListFragment;
import com.fivetrue.market.memo.utils.CommonUtils;
import com.fivetrue.market.memo.view.BottomNavigationBehavior;
import com.fivetrue.market.memo.view.PagerSlidingTabStrip;

import java.util.ArrayList;


public class MainActivity extends BaseActivity{

    private static final String TAG = "MainActivity";

    private DrawerLayout mDrawerLayout;
    private TextView mTitle;
    private PagerSlidingTabStrip mTab;
    private ViewPager mViewPager;

    private ActionBarDrawerToggle mToggle;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_cart_return_white_20dp);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_main);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(mToggle);

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

        mViewPager.setAdapter(mAdapter);
        mTab.setViewPager(mViewPager);

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

    public void closeDrawer(){
        if(mDrawerLayout != null){
            if(LL.D) Log.d(TAG, "closeDrawer() try to close drawer");
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void openDrawer(){
        if(mDrawerLayout != null){
            if(LL.D) Log.d(TAG, "openDrawer() try to open drawer");
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    public boolean isOpenDrawer(){
        if(mDrawerLayout != null){
            boolean b = mDrawerLayout.isDrawerOpen(GravityCompat.START);
            if(LL.D) Log.d(TAG, "isOpenDrawer() returned: " + b);
            return b;
        }
        return false;
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
            case R.id.action_setting :
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.action_info :
                CommonUtils.goStore(this);
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
        if(mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        FragmentManager fm = getCurrentFragmentManager();
        if(fm.getBackStackEntryCount() > 0){
            FragmentManager.BackStackEntry backStackEntry = fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1);
            if(backStackEntry != null && backStackEntry.getBreadCrumbTitle() != null){
                mTitle.setText(backStackEntry.getBreadCrumbTitle());
            }else{
                mTitle.setText(R.string.app_name);
            }
        }else{
            mTitle.setText(R.string.app_name);
        }
    }

    @Override
    protected void onClickHome() {
        if(isOpenDrawer()){
            closeDrawer();
        }else{
            openDrawer();
        }
//        super.onClickHome();
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

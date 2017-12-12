package com.fivetrue.market.memo.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
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
import android.view.View;
import android.widget.TextView;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.model.Product;
import com.fivetrue.market.memo.ui.adapter.pager.MainPagerAdapter;
import com.fivetrue.market.memo.ui.dialog.LoadingDialog;
import com.fivetrue.market.memo.ui.fragment.BaseFragment;
import com.fivetrue.market.memo.ui.fragment.CheckOutProductFragment;
import com.fivetrue.market.memo.ui.fragment.ProductListFragment;
import com.fivetrue.market.memo.ui.fragment.PurchaseListFragment;
import com.fivetrue.market.memo.utils.CommonUtils;
import com.fivetrue.market.memo.view.BottomNavigationBehavior;
import com.fivetrue.market.memo.view.PagerSlidingTabStrip;
import com.fivetrue.market.memo.viewmodel.ProductViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private TextView mTitle;
    private PagerSlidingTabStrip mTab;
    private ViewPager mViewPager;

    private MainPagerAdapter mAdapter;

    private ProductViewModel mProductViewModel;

    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        checkIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportFragmentManager().removeOnBackStackChangedListener(this);
    }

    private void initData(){
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        mProductViewModel = ViewModelProviders.of(this).get(ProductViewModel.class);
        ArrayList<MainPagerAdapter.FragmentSet> fragmentSets = new ArrayList<>();
        fragmentSets.add(new MainPagerAdapter.FragmentSet(ProductListFragment.class, ProductListFragment.makeArgument(this)));
        fragmentSets.add(new MainPagerAdapter.FragmentSet(CheckOutProductFragment.class, CheckOutProductFragment.makeArgument(this)));
        fragmentSets.add(new MainPagerAdapter.FragmentSet(PurchaseListFragment.class, PurchaseListFragment.makeArgument(this)));
        mAdapter = new MainPagerAdapter(getSupportFragmentManager(), fragmentSets);
    }

    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        mLoadingDialog = new LoadingDialog(this);
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

    public void movePageToPosition(int pos){
        if(mViewPager != null && mViewPager.getCurrentItem() > pos){
            mViewPager.setCurrentItem(pos);
        }
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

    private void checkIntent(Intent intent){
        if(intent != null){
            if(mProductViewModel.hasProduct(intent)){
                if(mLoadingDialog != null){
                    mLoadingDialog.show();
                }
                mLoadingDialog.show();
                mProductViewModel.insertProducts(intent).subscribe(() -> onInsertCompletedFromShared(), throwable -> onInsertCompletedFromShared());
            }
        }
    }

    private void onInsertCompletedFromShared(){
        if(mLoadingDialog != null){
            mLoadingDialog.dismiss();
        }
    }
}

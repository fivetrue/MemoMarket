package com.fivetrue.market.memo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.database.RealmDB;
import com.fivetrue.market.memo.model.Store;
import com.fivetrue.market.memo.model.StoreWrapper;
import com.fivetrue.market.memo.ui.adapter.StoreListAdapter;
import com.fivetrue.market.memo.ui.adapter.StorePagerAdapter;
import com.fivetrue.market.memo.ui.fragment.StoreProductFragment;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import jp.wasabeef.recyclerview.animators.ScaleInAnimator;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_ADD_STORE = 0x88;

    private DrawerLayout mDrawerLayout;

    private ViewPager mViewPager;
    private RecyclerView mRecyclerViewTab;

    private StorePagerAdapter mStorePagerAdapter;
    private List<StorePagerAdapter.FragmentSet> mFragmentSet;

    private StoreListAdapter mStoreListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private void initData(){
        RealmResults<Store> storeRealmResults = RealmDB.getInstance().get().where(Store.class).findAll();
        mFragmentSet = new ArrayList<>();
        ArrayList<StoreWrapper> storeList = new ArrayList<>();
        storeList.add(new StoreWrapper(StoreWrapper.TYPE_ADD_HEADER, null));
        for(Store store : storeRealmResults){
            mFragmentSet
                    .add(new StorePagerAdapter.FragmentSet(StoreProductFragment.class
                            , StoreProductFragment.makeArgument(store)
                            , store));
            storeList.add(new StoreWrapper(StoreWrapper.TYPE_STORE_ITEM, store));
        }
        mStorePagerAdapter = new StorePagerAdapter(getSupportFragmentManager(), mFragmentSet);
        mStoreListAdapter = new StoreListAdapter(storeList, new StoreListAdapter.OnStoreItemClickListener() {
            @Override
            public void onClickHeader(StoreListAdapter.StoreHolder holder, StoreWrapper item) {
                addStore(holder.icon);
            }

            @Override
            public void onClickItem(StoreListAdapter.StoreHolder holder, StoreWrapper item) {
                if(!mStoreListAdapter.isSelected(holder.getAdapterPosition())){
                    mStoreListAdapter.selectPosition(holder.getAdapterPosition());
                    mViewPager.setCurrentItem(holder.getAdapterPosition() - 1, true);
                }
            }

            @Override
            public boolean onLongCLickItem(StoreListAdapter.StoreHolder holder, StoreWrapper item) {
                return false;
            }
        });
        mStoreListAdapter.selectPosition(1);
    }

    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_main_layout);
        mRecyclerViewTab = (RecyclerView) findViewById(R.id.rv_main_tab);
        mViewPager = (ViewPager) findViewById(R.id.vp_main);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_main);
        initNavigation(navigationView);

        mViewPager.addOnPageChangeListener(this);
        mViewPager.setAdapter(mStorePagerAdapter);
        mRecyclerViewTab.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerViewTab.setItemAnimator(new TabListAnimator());
        mRecyclerViewTab.setAdapter(mStoreListAdapter);
    }

    private void addStore(View view){
        startActivityForResultWithClipRevealAnimation(new Intent(this, StoreAddActivity.class), REQUEST_ADD_STORE, view);
    }

    private void removeStore(){
        if(mFragmentSet.size() > mViewPager.getCurrentItem()){
            final Store store = mFragmentSet.get(mViewPager.getCurrentItem()).store;
            final RealmResults<Store> results = RealmDB.getInstance().get().where(Store.class).equalTo("name", store.getName()).findAll();
            RealmDB.getInstance().get().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if(LL.D) Log.d(TAG, "removeStore: store = " + store.getName());
                    boolean b = results.deleteFirstFromRealm();
                    if(LL.D) Log.d(TAG, "removeStore: " + b);
                    mFragmentSet.remove(mViewPager.getCurrentItem());
                    mStorePagerAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void initNavigation(NavigationView view){
        view.setNavigationItemSelectedListener(this);
        View headerView = view.getHeaderView(0);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.action_add_store :
//                addStore();
//                break;
//            case R.id.action_remove_store :
//                removeStore();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(LL.D) Log.d(TAG, "onPageSelected() called with: position = [" + position + "]");
        if(mStoreListAdapter != null && mRecyclerViewTab != null){
            mStoreListAdapter.selectPosition(position + 1);
            mRecyclerViewTab.scrollToPosition(position + 1);
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private static final class TabListAnimator extends ScaleInAnimator{

        @Override
        public boolean getSupportsChangeAnimations() {
            return false;
        }

        @Override
        public long getChangeDuration() {
            return 0;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_ADD_STORE :
                if(resultCode == RESULT_OK){
                    String name = data.getStringExtra("name");
                    if(name != null){
                        Store store = new Store();
                        store.setName(name);
                        RealmDB.getInstance().get().beginTransaction();
                        RealmDB.getInstance().get().insert(store);
                        RealmDB.getInstance().get().commitTransaction();
                        mStoreListAdapter.getData().add(new StoreWrapper(StoreWrapper.TYPE_STORE_ITEM, store));
                        mStoreListAdapter.notifyItemChanged(mStoreListAdapter.getItemCount());
                    }
                }
                break;
        }
    }
}

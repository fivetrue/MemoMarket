package com.fivetrue.market.memo.ui.fragment;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.database.RealmDB;
import com.fivetrue.market.memo.database.product.ProductDB;
import com.fivetrue.market.memo.model.vo.Product;
import com.fivetrue.market.memo.ui.MainActivity;
import com.fivetrue.market.memo.ui.ProductAddActivity;
import com.fivetrue.market.memo.ui.adapter.BaseAdapterImpl;
import com.fivetrue.market.memo.ui.adapter.list.ProductListAdapter;
import com.fivetrue.market.memo.utils.AdUtil;
import com.fivetrue.market.memo.utils.CommonUtils;
import com.fivetrue.market.memo.utils.SimpleViewUtils;
import com.fivetrue.market.memo.view.PagerTabContent;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

/**
 * Created by kwonojin on 2017. 2. 7..
 */

public class ProductListFragment extends BaseFragment implements PagerTabContent {

    private static final String TAG = "ProductListFragment";

    private RecyclerView mRecyclerProduct;
    private ProductListAdapter mProductListAdapter;
    private TextView mTextMessage;
    private FloatingActionButton mFabAction;

    private AdView mAdView;

    private GridLayoutManager mLayoutManager;

    private Disposable mProductDisposable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProductDisposable = ProductDB.getInstance().getObservable()
                .map(products ->
                        Observable.fromIterable(products)
                                .filter(product -> makeFilter(product)).toList().blockingGet())
                .subscribe(product -> setProductList(product), Throwable::printStackTrace);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_list, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerProduct = (RecyclerView) view.findViewById(R.id.rv_fragment_product_list);
        mTextMessage = (TextView) view.findViewById(R.id.tv_fragment_product_list);
        mFabAction = (FloatingActionButton) view.findViewById(R.id.fab_fragment_product_list);
        mAdView = (AdView) view.findViewById(R.id.adView);

        mLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mProductListAdapter.getItemViewType(position)){
                    case ProductListAdapter.FOOTER :
                        return 3;

                    case ProductListAdapter.PRODUCT:
                    case ProductListAdapter.PRODUCT_ADD:
                        return 1;

                    default:
                        return -1;
                }
            }
        });
        mRecyclerProduct.setLayoutManager(mLayoutManager);
        mRecyclerProduct.setItemAnimator(new ProductItemAnimator());

        mTextMessage.setOnClickListener(view1 -> {
            if(getActivity() != null){
                view1.getContext().startActivity(ProductAddActivity.makeIntent(view.getContext(), getClass().getSimpleName()));
                if(getActivity() instanceof MainActivity){
                    ((MainActivity) getActivity()).movePageToLeft();
                }
            }
        });
        mFabAction.setImageResource(getFabIconResource());
        mFabAction.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(getFabTintColor())));
        mFabAction.setOnClickListener(v -> {
            onClickActionButton();
        });

        ProductDB.getInstance().updatePublish();

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                if(LL.D) Log.d(TAG, "onAdFailedToLoad() called with: i = [" + i + "]");
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }
        });
        if(AdUtil.getInstance() == null){
            AdUtil.init(getContext().getApplicationContext());
        }
        AdUtil.getInstance().getAdRequestBuilder().subscribe(builder -> {
            mAdView.loadAd(builder.build());
        });

    }

    protected boolean makeFilter(Product p){
        boolean b = false;
        if(p != null){
            b = p.getCheckOutDate() == 0;
        }
        return b;
    }

    protected void setProductList(List<Product> productList){
        if(LL.D) Log.d(TAG, "setProductList() called with: storeList = [" + productList + "]");
        if(productList != null){
            if(mProductListAdapter == null){
                mProductListAdapter = makeAdapter(productList);
                AlphaInAnimationAdapter adapter = new AlphaInAnimationAdapter(mProductListAdapter);
                mRecyclerProduct.setAdapter(adapter);
            }else{
                mProductListAdapter.setData(productList);
            }
        }
        validationList();
    }

    protected void validationList(){
        if(mTextMessage != null){
            mTextMessage.setVisibility(mProductListAdapter != null
                    && mProductListAdapter.getItemCount() - 1 > 0
                    ? View.GONE : View.VISIBLE);
        }
    }

    protected ProductListAdapter makeAdapter(List<Product> productList){
        return new ProductListAdapter(productList, new ProductListAdapter.OnProductItemListener(){

            @Override
            public void onClickItem(ProductListAdapter.ProductHolder holder, Product item) {
                mProductListAdapter.toggle(holder.getAdapterPosition());
                updateFab();
            }

            @Override
            public boolean onLongClickItem(ProductListAdapter.ProductHolder holder, Product item) {
                if(item != null && getActivity() != null){
                    Toast.makeText(getActivity()
                            , String.format(getString(R.string.product_registered_date)
                                    , CommonUtils.getDate(getActivity(), "MM dd yyyy HH"
                                            , item.getCheckInDate())), Toast.LENGTH_LONG).show();
                    return true;
                }
                return false;
            }
        });
    }

    public BaseAdapterImpl<Product> getAdapter(){
        return mProductListAdapter;
    }

    public void onClickActionButton(){
//        Intent intent = ProductCheckOutActivity.makeIntent(getActivity(), TAG, getAdapter().getSelections());
//        getActivity().startActivity(intent);
        long millis = System.currentTimeMillis();
        RealmDB.get().executeTransaction(realm -> {
            for(Product p : getAdapter().getSelections()){
                p.setCheckOutDate(millis);
            }
            getAdapter().clearSelection();
            updateFab();
            if(getActivity() != null && getActivity() instanceof MainActivity){
                ((MainActivity) getActivity()).movePageToRight();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mProductDisposable != null && !mProductDisposable.isDisposed()){
            mProductDisposable.dispose();
        }
    }

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.product);
    }

    @Override
    public int getImageResource() {
        return R.drawable.selector_cart;
    }

    @Override
    public String getTabTitle(Context context) {
        return getTitle(context);
    }

    @Override
    public Drawable getTabDrawable(Context context) {
        return context.getDrawable(getImageResource());
    }

    @Override
    public TabIcon getIconState() {
        return TabIcon.TextWithIcon;
    }

    @Override
    public boolean onBackPressed() {
        if(mProductListAdapter.getSelections().size() > 0){
            mProductListAdapter.clearSelection();
            updateFab();
            return true;
        }
        return super.onBackPressed();
    }

    protected void updateFab(){
        if(mProductListAdapter.getSelections().size() > 0){
            if(!mFabAction.isShown()){
                SimpleViewUtils.showView(mFabAction, View.VISIBLE);
            }
        }else{
            if(mFabAction.isShown()){
                SimpleViewUtils.hideView(mFabAction, View.GONE);
            }
        }
    }

    private static class ProductItemAnimator extends DefaultItemAnimator{

        @Override
        public boolean getSupportsChangeAnimations() {
            return false;
        }

        @Override
        public long getChangeDuration() {
            return 0;
        }
    }

    public static Bundle makeArgument(Context context){
        Bundle b = new Bundle();
        return b;
    }

    protected int getFabIconResource(){
        return R.drawable.ic_cart_checkout_white_50dp;
    }

    protected int getFabTintColor(){
        return R.color.colorPrimary;
    }

}

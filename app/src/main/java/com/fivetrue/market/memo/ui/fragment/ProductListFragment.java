package com.fivetrue.market.memo.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.model.entity.ProductEntity;
import com.fivetrue.market.memo.databinding.FragmentProductListBinding;
import com.fivetrue.market.memo.model.Product;
import com.fivetrue.market.memo.ui.MainActivity;
import com.fivetrue.market.memo.ui.ProductAddActivity;
import com.fivetrue.market.memo.ui.adapter.BaseAdapterImpl;
import com.fivetrue.market.memo.ui.adapter.holder.HolderClickEvent;
import com.fivetrue.market.memo.ui.adapter.list.ProductListAdapter;
import com.fivetrue.market.memo.ui.touch.ProductListItemTouch;
import com.fivetrue.market.memo.ui.touch.SimpleItemTouchCallback;
import com.fivetrue.market.memo.utils.AdUtil;
import com.fivetrue.market.memo.utils.TrackingUtil;
import com.fivetrue.market.memo.view.PagerTabContent;
import com.fivetrue.market.memo.viewmodel.ProductListViewModel;
import com.google.android.gms.ads.AdListener;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

/**
 * Created by kwonojin on 2017. 2. 7..
 */

public class ProductListFragment extends BaseFragment implements PagerTabContent{

    private static final String TAG = "ProductListFragment";
    private FragmentProductListBinding mBinding;

    private ProductListAdapter mProductListAdapter;
    private ProductListViewModel mProductListViewModel;

    private CompositeDisposable mCompositeDisposable;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCompositeDisposable.clear();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_list, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
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
        mBinding.productList.setLayoutManager(layoutManager);
        mBinding.productList.setItemAnimator(new ProductItemAnimator());
        mBinding.emptyText.setOnClickListener(view1 -> {
            if(getActivity() != null){
                view1.getContext().startActivity(ProductAddActivity.makeIntent(view.getContext(), getClass().getSimpleName()));
                if(getActivity() instanceof MainActivity){
                    ((MainActivity) getActivity()).movePageToLeft();
                }
            }
        });
        mBinding.fabButton.setImageResource(getFabIconResource());
        mBinding.fabButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(getFabTintColor())));
        mBinding.fabButton.setOnClickListener(v -> {
            onClickActionButton();
        });

        mBinding.adView.setAdListener(new AdListener() {
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
            mBinding.adView.loadAd(builder.build());
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mProductListViewModel = ViewModelProviders.of(this).get(ProductListViewModel.class);
        mProductListViewModel.getProductList().observe(this, products -> {
            setProductList(arrangeProducts(products));
        });
        mBinding.setShowFab(false);
    }

    protected void setProductList(List<Product> productList){
        if(LL.D) Log.d(TAG, "setProductList() called with: storeList = [" + productList + "]");
        mBinding.setEmpty(productList.isEmpty());
        if(productList != null){
            if(mProductListAdapter == null){
                mProductListAdapter = makeAdapter(productList);
                mCompositeDisposable.add(mProductListAdapter.getClickObservable().subscribe(clickEvent -> onItemClickEvent(clickEvent)));
                mCompositeDisposable.add(mProductListAdapter.getMoreObservable().subscribe(moreEvent -> onItemMoreEvent(moreEvent)));
                AlphaInAnimationAdapter adapter = new AlphaInAnimationAdapter(mProductListAdapter);
                mBinding.productList.setAdapter(adapter);
                ItemTouchHelper touchHelper = new ItemTouchHelper(new SimpleItemTouchCallback(new ProductListItemTouch(mProductListViewModel, mProductListAdapter)));
                touchHelper.attachToRecyclerView(mBinding.productList);
            }else{
                mProductListAdapter.setData(productList);
            }
        }

    }
    protected ProductListAdapter makeAdapter(List<Product> productList){
        return new ProductListAdapter(productList);
    }

    public ProductListAdapter getAdapter(){
        return mProductListAdapter;
    }

    public void onClickActionButton(){
        long millis = System.currentTimeMillis();
        Observable.fromIterable(getAdapter().getSelections())
                .map(product -> {
                    ProductEntity productEntity = (ProductEntity) product;
                    productEntity.setCheckOutDate(millis);
                    return productEntity;
                })
                .toList()
                .subscribe(productEntities -> {
                    mProductListViewModel.updateProducts(productEntities).subscribe(() -> {
                        getAdapter().clearSelection();
                        getAdapter().notifyDataSetChanged();
                        updateFab();
                        if(getActivity() != null && getActivity() instanceof MainActivity){
                            ((MainActivity) getActivity()).movePageToRight();
                        }
                    });
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        mBinding.setShowFab(mProductListAdapter.getSelections().size() > 0);
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


    protected List<Product> arrangeProducts(List<Product> products){
        return Observable.fromIterable(products)
                .filter(product -> product.getCheckOutDate() == 0)
                .sorted((product, t1) -> product.getCheckInDate() > t1.getCheckInDate() ? 1 : -1).toList().blockingGet();
    }

    protected void onItemClickEvent(HolderClickEvent.ClickEvent event){
        switch (event.type){
            case Click:
                mProductListAdapter.toggle(event.getAdapterPosition());
                updateFab();
                return;
        }
    }

    protected void onItemMoreEvent(ProductListAdapter.MoreEvent event){
        switch (event.type){
            case Buy:
                event.product.setCheckOutDate(System.currentTimeMillis());
                mProductListViewModel.updateProduct(event.product).subscribe();
                break;
            case Duplicate:
                mProductListViewModel.duplicateProduct(event.product).subscribe();
                break;
            case Delete:
                if(getActivity() != null){
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.delete)
                            .setMessage(R.string.delete_product_message)
                            .setPositiveButton(android.R.string.ok, (dialogInterface, i1) -> {
                                dialogInterface.dismiss();
                                mProductListViewModel.deleteProduct(event.product).subscribe();
                                TrackingUtil.getInstance().deleteProduct(event.product.getName(), TAG);
                            }).setNegativeButton(android.R.string.cancel, (dialogInterface, i1) -> dialogInterface.dismiss())
                            .show();
                }
                break;
            case Revert:
                event.product.setCheckOutDate(0);
                mProductListViewModel.updateProduct(event.product).subscribe();
                break;
        }
    }
}
//public class ProductListFragment extends BaseFragment implements PagerTabContent{
//
//    private static final String TAG = "ProductListFragment";
//
//    private RecyclerView mRecyclerProduct;
//    private ProductListAdapter mProductListAdapter;
//    private TextView mTextMessage;
//    private FloatingActionButton mFabAction;
//
//    private AdView mAdView;
//
//    private GridLayoutManager mLayoutManager;
//
//    private Disposable mProductDisposable;
//    private Disposable mProductClickDisposable;
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mProductDisposable = ProductDB.getInstance().getObservable()
//                .map(products -> arrangeProducts(products))
//                .subscribe(product -> setProductList(product), Throwable::printStackTrace);
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_product_list, null);
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        mRecyclerProduct = (RecyclerView) view.findViewById(R.id.rv_fragment_product_list);
//        mTextMessage = (TextView) view.findViewById(R.id.tv_fragment_product_list);
//        mFabAction = (FloatingActionButton) view.findViewById(R.id.fab_fragment_product_list);
//        mAdView = (AdView) view.findViewById(R.id.adView);
//
//        mLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
//        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                switch (mProductListAdapter.getItemViewType(position)){
//                    case ProductListAdapter.FOOTER :
//                        return 3;
//
//                    case ProductListAdapter.PRODUCT:
//                    case ProductListAdapter.PRODUCT_ADD:
//                        return 1;
//
//                    default:
//                        return -1;
//                }
//            }
//        });
//        mRecyclerProduct.setLayoutManager(mLayoutManager);
//        mRecyclerProduct.setItemAnimator(new ProductItemAnimator());
//        mTextMessage.setOnClickListener(view1 -> {
//            if(getActivity() != null){
//                view1.getContext().startActivity(ProductAddActivity.makeIntent(view.getContext(), getClass().getSimpleName()));
//                if(getActivity() instanceof MainActivity){
//                    ((MainActivity) getActivity()).movePageToLeft();
//                }
//            }
//        });
//        mFabAction.setImageResource(getFabIconResource());
//        mFabAction.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(getFabTintColor())));
//        mFabAction.setOnClickListener(v -> {
//            onClickActionButton();
//        });
//
//        mAdView.setAdListener(new AdListener() {
//            @Override
//            public void onAdClosed() {
//                super.onAdClosed();
//            }
//
//            @Override
//            public void onAdFailedToLoad(int i) {
//                super.onAdFailedToLoad(i);
//                if(LL.D) Log.d(TAG, "onAdFailedToLoad() called with: i = [" + i + "]");
//            }
//
//            @Override
//            public void onAdLeftApplication() {
//                super.onAdLeftApplication();
//            }
//
//            @Override
//            public void onAdOpened() {
//                super.onAdOpened();
//            }
//
//            @Override
//            public void onAdLoaded() {
//                super.onAdLoaded();
//            }
//        });
//        if(AdUtil.getInstance() == null){
//            AdUtil.init(getContext().getApplicationContext());
//        }
//        AdUtil.getInstance().getAdRequestBuilder().subscribe(builder -> {
//            mAdView.loadAd(builder.build());
//        });
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        ProductDB.getInstance().updatePublish();
//    }
//
//    protected void setProductList(List<Product> productList){
//        if(LL.D) Log.d(TAG, "setProductList() called with: storeList = [" + productList + "]");
//        if(productList != null && mRecyclerProduct != null){
//            if(mProductListAdapter == null){
//                mProductListAdapter = makeAdapter(productList);
//                mProductClickDisposable = mProductListAdapter.getObservable().subscribe(clickEvent -> onItemClickEvent(clickEvent));
//                AlphaInAnimationAdapter adapter = new AlphaInAnimationAdapter(mProductListAdapter);
//                mRecyclerProduct.setAdapter(adapter);
//                ItemTouchHelper touchHelper = new ItemTouchHelper(new SimpleItemTouchCallback(mProductListAdapter));
//                touchHelper.attachToRecyclerView(mRecyclerProduct);
//            }else{
//                mProductListAdapter.setData(productList);
//            }
//        }
//        validationList();
//    }
//
//    protected void validationList(){
//        if(mTextMessage != null){
//            mTextMessage.setVisibility(mProductListAdapter != null
//                    && mProductListAdapter.getItemCount() - 1 > 0
//                    ? View.GONE : View.VISIBLE);
//        }
//    }
//
//    protected ProductListAdapter makeAdapter(List<Product> productList){
//        return new ProductListAdapter(productList);
//    }
//
//    public BaseAdapterImpl<Product> getAdapter(){
//        return mProductListAdapter;
//    }
//
//    public void onClickActionButton(){
////        Intent intent = ProductCheckOutActivity.makeIntent(getActivity(), TAG, getAdapter().getSelections());
////        getActivity().startActivity(intent);
//        long millis = System.currentTimeMillis();
//        ProductDB.get().executeTransaction(realm -> {
//            for(Product p : getAdapter().getSelections()){
//                p.setCheckOutDate(millis);
//            }
//            getAdapter().clearSelection();
//            updateFab();
//            if(getActivity() != null && getActivity() instanceof MainActivity){
//                ((MainActivity) getActivity()).movePageToRight();
//            }
//            ProductDB.getInstance().updatePublish();
//        });
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if(mProductDisposable != null && !mProductDisposable.isDisposed()){
//            mProductDisposable.dispose();
//        }
//        if(mProductClickDisposable != null && !mProductClickDisposable.isDisposed()){
//            mProductClickDisposable.dispose();
//        }
//    }
//
//    @Override
//    public String getTitle(Context context) {
//        return context.getString(R.string.product);
//    }
//
//    @Override
//    public int getImageResource() {
//        return R.drawable.selector_cart;
//    }
//
//    @Override
//    public String getTabTitle(Context context) {
//        return getTitle(context);
//    }
//
//    @Override
//    public Drawable getTabDrawable(Context context) {
//        return context.getDrawable(getImageResource());
//    }
//
//    @Override
//    public TabIcon getIconState() {
//        return TabIcon.TextWithIcon;
//    }
//
//    @Override
//    public boolean onBackPressed() {
//        if(mProductListAdapter.getSelections().size() > 0){
//            mProductListAdapter.clearSelection();
//            updateFab();
//            return true;
//        }
//        return super.onBackPressed();
//    }
//
//    protected void updateFab(){
//        if(mProductListAdapter.getSelections().size() > 0){
//            if(!mFabAction.isShown()){
//                SimpleViewUtils.showView(mFabAction, View.VISIBLE);
//            }
//        }else{
//            if(mFabAction.isShown()){
//                SimpleViewUtils.hideView(mFabAction, View.GONE);
//            }
//        }
//    }
//
//    private static class ProductItemAnimator extends DefaultItemAnimator{
//
//        @Override
//        public boolean getSupportsChangeAnimations() {
//            return false;
//        }
//
//        @Override
//        public long getChangeDuration() {
//            return 0;
//        }
//    }
//
//    public static Bundle makeArgument(Context context){
//        Bundle b = new Bundle();
//        return b;
//    }
//
//    protected int getFabIconResource(){
//        return R.drawable.ic_cart_checkout_white_50dp;
//    }
//
//    protected int getFabTintColor(){
//        return R.color.colorPrimary;
//    }
//
//
//    protected List<Product> arrangeProducts(List<Product> products){
//        return Observable.fromIterable(products)
//                .filter(product -> product.getCheckOutDate() == 0).toList().blockingGet();
//    }
//
//    protected void onItemClickEvent(HolderClickEvent.ClickEvent event){
//        switch (event.type){
//            case Click:
//                mProductListAdapter.toggle(event.getAdapterPosition());
//                updateFab();
//                return;
//        }
//    }
//}

package com.fivetrue.market.memo.ui.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.database.RealmDB;
import com.fivetrue.market.memo.database.product.ProductDB;
import com.fivetrue.market.memo.model.vo.Product;
import com.fivetrue.market.memo.ui.adapter.BaseAdapterImpl;
import com.fivetrue.market.memo.ui.adapter.product.ProductListAdapter;
import com.fivetrue.market.memo.view.PagerTabContent;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

/**
 * Created by kwonojin on 2017. 2. 7..
 */

public class ProductListFragment extends BaseFragment implements PagerTabContent {

    private static final String TAG = "ProductListFragment";

    private RecyclerView mRecyclerProduct;
    private ProductListAdapter mProductListAdapter;

    private TextView mTextMessage;

    private GridLayoutManager mLayoutManager;

    private Disposable mProductDisposable;

    private PublishSubject<List<Product>> mProductSelectedPublishSubject = PublishSubject.create();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProductDisposable = ProductDB.getInstance().getObservable()
                .map(products ->
                    Observable.fromIterable(products)
                            .filter(product -> makeFilter(product)).toList().blockingGet())
                .subscribe(product -> setProductList(product));
    }


    @Override
    public void onStop() {
        super.onStop();
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

        mLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mProductListAdapter.getItemViewType(position)){
                    case ProductListAdapter.FOOTER :
                        return 2;

                    case ProductListAdapter.PRODUCT:
                        return 1;

                    default:
                        return -1;
                }
            }
        });
        mRecyclerProduct.setLayoutManager(mLayoutManager);
        mRecyclerProduct.setItemAnimator(new ProductItemAnimator());
        ProductDB.getInstance().updatePublish();
    }

    protected boolean makeFilter(Product p){
        boolean b = false;
        if(p != null){
            b = !p.isCheckOut();
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
                    && mProductListAdapter.getItemCount() > 0
                    ? View.GONE : View.VISIBLE);
        }
    }

    protected ProductListAdapter makeAdapter(List<Product> productList){
        return new ProductListAdapter(productList, new ProductListAdapter.OnProductItemListener(){

            @Override
            public void onClickItem(ProductListAdapter.ProductHolder holder, Product item) {
                mProductListAdapter.toggle(holder.getAdapterPosition());
                publish();
            }

            @Override
            public boolean onLongClickItem(ProductListAdapter.ProductHolder holder, Product item) {
                return false;
            }
        });
    }

    public BaseAdapterImpl<Product> getAdapter(){
        return mProductListAdapter;
    }

    public void doProducts(final View snackbarAnchor){
        RealmDB.get().executeTransaction(realm -> {
            final List<Product> products = mProductListAdapter.getSelections();
            for(Product p : products){
                p.setCheckOut(true);
            }
            mProductListAdapter.clearSelection();
            publish();
            if(snackbarAnchor != null){
                Snackbar.make(snackbarAnchor, R.string.product_moved_completed_message
                        , Snackbar.LENGTH_LONG)
                        .setAction(R.string.revert, view -> {
                            RealmDB.get().executeTransaction(realm1 -> {
                                for(Product p : products){
                                    p.setCheckOut(false);
                                }
                                publish();
                            });
                        }).show();
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
        return R.drawable.selector_product;
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
            publish();
            return true;
        }
        return super.onBackPressed();
    }

    public void publish(){
        if(mProductListAdapter != null){
            mProductSelectedPublishSubject.onNext(mProductListAdapter.getSelections());
        }
    }

    public Observable<List<Product>> getObservable(){
        return mProductSelectedPublishSubject;
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

}

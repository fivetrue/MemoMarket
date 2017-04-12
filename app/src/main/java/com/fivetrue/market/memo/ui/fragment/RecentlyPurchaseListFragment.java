package com.fivetrue.market.memo.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.fivetrue.market.memo.database.product.ProductDB;
import com.fivetrue.market.memo.model.vo.Product;
import com.fivetrue.market.memo.ui.MainActivity;
import com.fivetrue.market.memo.ui.ProductAddActivity;
import com.fivetrue.market.memo.ui.adapter.BaseAdapterImpl;
import com.fivetrue.market.memo.ui.adapter.list.ProductListAdapter;
import com.fivetrue.market.memo.ui.adapter.list.RecentlyPurchaseListAdapter;
import com.fivetrue.market.memo.utils.CommonUtils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

/**
 * Created by kwonojin on 2017. 2. 7..
 */

public class RecentlyPurchaseListFragment extends BaseFragment{

    private static final String TAG = "RPurchaseListFragment";

    private RecyclerView mRecyclerProduct;
    private RecentlyPurchaseListAdapter mProductListAdapter;

    private TextView mTopMessage;
    private TextView mTextMessage;

    private GridLayoutManager mLayoutManager;

    private Disposable mProductDisposable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProductDisposable = ProductDB.getInstance().getObservable()
                .map(products ->
                        Observable.fromIterable(products)
                                .filter(product -> makeFilter(product))
                                .sorted((product, t1)
                                        -> product.getCheckOutDate() < t1.getCheckOutDate() ? 1 : -1)
                                .toList().blockingGet())
                .subscribe(product -> setProductList(product));
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recently_purchase_list, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerProduct = (RecyclerView) view.findViewById(R.id.rv_fragment_recently_product_list);
        mTopMessage = (TextView) view.findViewById(R.id.tv_fragment_recently_product_list_top);
        mTextMessage = (TextView) view.findViewById(R.id.tv_fragment_recently_product_list);

        mLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mProductListAdapter.getItemViewType(position)){
                    case ProductListAdapter.FOOTER :
                        return 2;

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
        mTextMessage.setOnClickListener(v -> {
            if(getActivity() != null) {
                Intent intent = new Intent(getActivity(), ProductAddActivity.class);
                startActivity(intent);
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).closeDrawer();
                    ((MainActivity) getActivity()).movePageToLeft();
                }
            }
        });
        ProductDB.getInstance().updatePublish();
    }

    protected boolean makeFilter(Product p){
        boolean b = false;
        if(p != null){
            b = p.getCheckOutDate() > 0;
        }
        return b;
    }

    protected void setProductList(List<Product> productList){
        if(LL.D) Log.d(TAG, "setProductList() called with: storeList = [" + productList + "]");
        if(productList != null){
            if(mProductListAdapter == null){
                mProductListAdapter = new RecentlyPurchaseListAdapter(productList, new ProductListAdapter.OnProductItemListener() {
                    @Override
                    public void onClickItem(ProductListAdapter.ProductHolder holder, Product item) {

                    }

                    @Override
                    public boolean onLongClickItem(ProductListAdapter.ProductHolder holder, Product item) {
                        if(item != null && getActivity() != null){
                            Toast.makeText(getActivity()
                                    , String.format(getString(R.string.product_purchased_date)
                                            , CommonUtils.getDate(getActivity(), "MM dd yyyy HH"
                                                    , item.getCheckInDate())), Toast.LENGTH_LONG).show();
                            return true;
                        }
                        return false;
                    }
                });

                mProductListAdapter.showAddButton(false);
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

        if(mTopMessage != null && getActivity() != null){
            mTopMessage.setVisibility(mProductListAdapter != null
                    && mProductListAdapter.getItemCount() - 1 > 0
                    ? View.VISIBLE : View.GONE);
            if(mProductListAdapter.getData().size() > 0) {
                mTopMessage.setText(getString(R.string.recently_purchase_top_message
                        , CommonUtils.getDate(getActivity(), "yyyy/MM/dd hh:mm", mProductListAdapter.getItem(0).getCheckOutDate())
                        , mProductListAdapter.getData().size()));
            }
        }
    }

    public BaseAdapterImpl<Product> getAdapter(){
        return mProductListAdapter;
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
        return R.drawable.selector_cart_loaded;
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
}

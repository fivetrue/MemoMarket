package com.fivetrue.market.memo.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.database.RealmDB;
import com.fivetrue.market.memo.model.Product;
import com.fivetrue.market.memo.model.Store;
import com.fivetrue.market.memo.ui.adapter.ProductListAdapter;

import java.util.ArrayList;

import io.realm.RealmResults;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;

public class StoreProductFragment extends BaseFragment{

    private static final String TAG = "StoreProductFragment";

    private static final String KEY_STORE_NAME = "store_name";

    private RecyclerView mRecyclerProducts;
    private ProductListAdapter mProductListAdapter;

    private FloatingActionButton mAddProduct;

    private String mName;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mName = getArguments().getString(KEY_STORE_NAME);
        RealmResults<Product> productRealmResults = RealmDB.getInstance().get()
                .where(Product.class).equalTo("storeName", mName).findAllAsync();

        mProductListAdapter = new ProductListAdapter(new ArrayList<>(productRealmResults), new ProductListAdapter.OnProductItemListener() {
            @Override
            public void onClickItem(ProductListAdapter.ProductHolder holder, Product item) {
                if(holder != null){
                    boolean b = mProductListAdapter.togglePosition(holder.getAdapterPosition());
                    RealmDB.getInstance().get().beginTransaction();
                    item.setChecked(b);
                    RealmDB.getInstance().get().commitTransaction();
                }
            }

            @Override
            public boolean onLongCLickItem(ProductListAdapter.ProductHolder holder, Product item) {
                return false;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_store_product, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerProducts = (RecyclerView) view.findViewById(R.id.rv_fragment_store_product);
        mAddProduct = (FloatingActionButton) view.findViewById(R.id.fab_fragment_store_product);

        mRecyclerProducts.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false));
        mRecyclerProducts.setItemAnimator(new ProductItemAnimator());
        mRecyclerProducts.setAdapter(mProductListAdapter);

        mAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public String getTitle(Context context) {
        return getArguments().getString(KEY_STORE_NAME);
    }

    @Override
    public String getSubTitle(Context context) {
        long count = RealmDB.getInstance().get().where(Product.class).equalTo("storeName", mName).count();
        return String.format(context.getString(R.string.store_product_count), count);
    }

    @Override
    public int getImageResource() {
        return 0;
    }

    public static Bundle makeArgument(Store store){
        Bundle b = new Bundle();
        b.putString(KEY_STORE_NAME, store.getName());
        return b;
    }


    private static final class ProductItemAnimator extends FadeInAnimator{

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

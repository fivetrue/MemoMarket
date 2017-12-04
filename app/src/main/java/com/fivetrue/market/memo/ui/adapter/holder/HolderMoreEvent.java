package com.fivetrue.market.memo.ui.adapter.holder;

import android.support.v7.widget.RecyclerView;

import com.fivetrue.market.memo.model.Product;

import io.reactivex.Observable;

/**
 * Created by kwonojin on 2017. 11. 14..
 */

public interface HolderMoreEvent {

    enum MoreType{Buy, Duplicate, Delete, Revert};

    Observable<MoreEvent> getMoreObservable();

    class MoreEvent{
        public final MoreType type;
        public final int position;
        public final Product product;
        public MoreEvent(MoreType type, int position, Product product){
            this.type = type;
            this.position = position;
            this.product = product;
        }
    }
}

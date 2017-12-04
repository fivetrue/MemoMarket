package com.fivetrue.market.memo.ui.adapter.holder;

import android.support.v7.widget.RecyclerView;

import io.reactivex.Observable;

/**
 * Created by kwonojin on 2017. 11. 14..
 */

public interface HolderClickEvent{

    enum ClickType{Click, LongClick}

    Observable<ClickEvent> getClickObservable();

    class ClickEvent{

        public final ClickType type;
        public final RecyclerView.ViewHolder holder;

        public ClickEvent(ClickType type, RecyclerView.ViewHolder holder) {
            this.type = type;
            this.holder = holder;
        }

        public int getAdapterPosition(){
           return this.holder.getAdapterPosition();
        }
    }
}

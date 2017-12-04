package com.fivetrue.market.memo.ui.touch;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.ui.adapter.ItemTouchHelperAdapter;

/**
 * Created by kwonojin on 2017. 11. 13..
 */

public class SimpleItemTouchCallback extends ItemTouchHelper.Callback {

    private static final String TAG = "SimpleItemTouchCallback";

    private ItemTouchHelperAdapter adapter;

    private int fromPos = -1;
    private int toPos = -1;

    public SimpleItemTouchCallback(ItemTouchHelperAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(adapter.getDragFlag(), adapter.getSwipeFlag());
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return adapter.onItemMove(viewHolder.getAdapterPosition(),
                target.getAdapterPosition());
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return adapter.isEnableSwipe();
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return adapter.isEnableDrag();
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if(LL.D)
            Log.d(TAG, "onSelectedChanged() called with: viewHolder = [" + viewHolder + "], actionState = [" + actionState + "]");
    }

    @Override
    public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
        if(LL.D)
            Log.d(TAG, "onMoved() called with: recyclerView = [" + recyclerView + "], viewHolder = [" + viewHolder + "], fromPos = [" + fromPos + "], target = [" + target + "], toPos = [" + toPos + "], x = [" + x + "], y = [" + y + "]");
        int fromPosition = target.getAdapterPosition();
        int toPosition = viewHolder.getAdapterPosition();


        if(this.fromPos == -1) {
            this.fromPos =  fromPosition;
        }
        this.toPos = toPosition;
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        adapter.onMovedFinish(fromPos, toPos);
        fromPos = -1;
        toPos = -1;
    }
}
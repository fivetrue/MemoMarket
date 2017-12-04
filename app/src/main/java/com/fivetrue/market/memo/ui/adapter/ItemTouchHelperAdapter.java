package com.fivetrue.market.memo.ui.adapter;

/**
 * Created by kwonojin on 2017. 11. 13..
 */

public interface ItemTouchHelperAdapter {



    boolean onItemMove(int fromPosition, int toPosition);

    void onMovedFinish(int fromPos, int toPos);

    void onItemDismiss(int position);

    boolean isEnableDrag();

    boolean isEnableSwipe();

    int getSwipeFlag();

    int getDragFlag();

}

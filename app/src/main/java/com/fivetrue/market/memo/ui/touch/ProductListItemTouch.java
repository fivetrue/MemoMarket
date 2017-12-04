package com.fivetrue.market.memo.ui.touch;

import android.support.v7.widget.helper.ItemTouchHelper;

import com.fivetrue.market.memo.model.Product;
import com.fivetrue.market.memo.model.entity.ProductEntity;
import com.fivetrue.market.memo.ui.adapter.ItemTouchHelperAdapter;
import com.fivetrue.market.memo.ui.adapter.list.ProductListAdapter;
import com.fivetrue.market.memo.viewmodel.ProductListViewModel;

import java.util.Collections;


/**
 * Created by kwonojin on 2017. 11. 22..
 */

public class ProductListItemTouch implements ItemTouchHelperAdapter {

    private ProductListViewModel viewModel;
    private ProductListAdapter adapter;

    public ProductListItemTouch(ProductListViewModel viewModel, ProductListAdapter adapter){
        this.viewModel = viewModel;
        this.adapter = adapter;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if(fromPosition >= 0 && toPosition < adapter.getData().size()){
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(adapter.getData(), i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(adapter.getData(), i, i - 1);
                }
            }
            adapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }
        return false;
    }

    @Override
    public void onMovedFinish(int fromPos, int toPos) {
        if(fromPos != toPos){
            movedData(toPos, adapter.getItem(toPos));
            adapter.clearSelection();
        }
    }

    protected void movedData(int pos, Product movedProduct){
        if(pos >=0 && adapter.getData().size() > pos){
            long checkInDate = 0;
            if(pos == 0){
                Product p = adapter.getItem(pos + 1);
                checkInDate = p.getCheckInDate() - 1000;
            }else if(pos == adapter.getData().size() - 1){
                Product p = adapter.getItem(pos - 1);
                checkInDate = p.getCheckInDate() + 1000;
            }else {
                Product left = adapter.getItem(pos - 1);
                Product right = adapter.getItem(pos + 1);
                checkInDate = (left.getCheckInDate() + right.getCheckInDate()) /  2;
            }
            movedProduct.setCheckInDate(checkInDate);
            viewModel.updateProduct((ProductEntity) movedProduct).subscribe();
        }
    }

    @Override
    public void onItemDismiss(int position) {
        if(adapter.getData().size() > position){
            adapter.getData().remove(position);
            adapter.notifyItemRemoved(position);
        }
    }

    @Override
    public boolean isEnableDrag() {
        return true;
    }

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getSwipeFlag() {
        return 0;
    }

    @Override
    public int getDragFlag() {
        return ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
    }

}

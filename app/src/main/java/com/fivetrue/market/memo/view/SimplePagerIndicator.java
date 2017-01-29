package com.fivetrue.market.memo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.adapter.BasePagerAdapter;


/**
 * Created by ojin.kwon on 2016-12-15.
 */

public class SimplePagerIndicator extends LinearLayout implements ViewPager.OnPageChangeListener, ViewPager.OnAdapterChangeListener{

    private static final String TAG = "SimplePagerIndicator";

    private static final float MOVE_SPEED = 2f;

    private int mIconSize = 10;
    private int mIconMargin = 2;
    private int mIconResource = R.drawable.selector_dot_indicator;
    private boolean mAnimation = false;


    private ViewPager mViewPager;
    private BasePagerAdapter mAdapter;

    private int mPosition;
    private float mPositionOffset;
    private float mPositionOffsetPixels;

    private Paint mPaint;
    private Path mDotPath;

    public SimplePagerIndicator(Context context) {
        super(context);
    }

    public SimplePagerIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public SimplePagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SimplePagerIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        mIconSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mIconSize, dm);
        mIconMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mIconMargin, dm);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SimplePagerIndicator);
        mIconResource = a.getResourceId(R.styleable.SimplePagerIndicator_indicatorIcon, mIconResource);
        mIconSize = (int) a.getDimension(R.styleable.SimplePagerIndicator_indicatorIconSize, mIconSize);
        mIconMargin = (int) a.getDimension(R.styleable.SimplePagerIndicator_indicatorIconMargin, mIconMargin);
        mAnimation = a.getBoolean(R.styleable.SimplePagerIndicator_moveAnimation, mAnimation);
        a.recycle();


        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mDotPath = new Path();
        setWillNotDraw(false);
    }

    public void setViewPager(ViewPager viewPager){
        if(viewPager != null){
            mViewPager = viewPager;
            mViewPager.addOnPageChangeListener(this);
            mViewPager.addOnAdapterChangeListener(this);
        }
    }

    public void notifyDataChanged(){
        removeAllViews();
        for(int i = 0 ; i < mAdapter.getRealCount() ; i ++){
            addIcon(i);
        }
    }

    private void changedPos(int pos){
        int childCount = getChildCount();
        if(LL.D) Log.d(TAG, "changedPos() called with: pos, childCount = [" + pos + ", " + childCount  + "]");
        for(int i = 0 ; i < childCount ; i ++){
            getChildAt(i).setSelected(false);
        }
        getChildAt(pos).setSelected(true);
    }

    private void addIcon(int pos){
        if(LL.D) Log.d(TAG, "addIcon() called with: pos = [" + pos + "]");
        View view = new View(getContext());
        view.setBackgroundResource(mIconResource);
        LayoutParams params = new LayoutParams(mIconSize, mIconSize);
        params.leftMargin = mIconMargin;
        params.rightMargin = mIconMargin;
        params.topMargin = mIconMargin;
        params.bottomMargin = mIconMargin;
        view.setSelected(pos == 0);
        addView(view, pos, params);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mAnimation){
            int childCount = getChildCount();
            if(!isInEditMode() && childCount > 0 && mPosition >= 0 && childCount > mPosition){
                int height = getHeight();

                View currentTab = getChildAt(mPosition);
                float currentViewLeft = currentTab.getLeft();
                float currentViewRight = currentTab.getRight();
                float currentViewHeight = currentTab.getHeight();
                float currentViewWidth = currentTab.getWidth();
                float top = (height / 2) - (currentViewHeight / 2);
                float bottom = (height / 2) + (currentViewHeight / 2);

                if (mPositionOffset > 0f && mPosition < childCount - 1) {

                    View nextTab = getChildAt(mPosition + 1);
                    float nextViewLeft = nextTab.getLeft();
                    float nextViewRight = nextTab.getRight();

                    float toLeft = (mPositionOffset * nextViewLeft + (1f - mPositionOffset) * currentViewLeft);
                    float toRight = (mPositionOffset * nextViewRight + (1f - mPositionOffset) * currentViewRight);

                    drawStream(canvas, currentTab, nextTab, mPositionOffset);
                }else{
                    canvas.drawOval(currentViewLeft, top, currentViewRight, bottom, mPaint);
                }
            }
        }
    }

    private void drawStream(Canvas canvas, View leftView, View rightView, float offset){
        if(canvas != null && leftView != null && rightView != null){
            float distance = rightView.getLeft() - leftView.getRight();
            float moved = offset * MOVE_SPEED;
            float cy = leftView.getTop() + (leftView.getHeight() / 2);
            Log.d(TAG, "drawStream: distnace = " + distance + " / moved = " + moved);
            mDotPath.reset();

            float streamLeft = leftView.getLeft() + ((distance + rightView.getWidth()) * offset);
            float streamRight = leftView.getRight() + ((distance + rightView.getWidth()) * (moved > 1f ? 1f : moved));
            float streamTop = cy - ((leftView.getHeight() / 3) * (offset > 0.5f ? offset : 1f - offset));
            float streamBottom = cy + ((leftView.getHeight() / 3) * (offset > 0.5f ? offset : 1f - offset));

            if(moved < 1f){
                mDotPath.addCircle(leftView.getRight() - (leftView.getWidth() / 2),
                        leftView.getBottom() - (leftView.getHeight() / 2),
                        (leftView.getWidth() / 2) * (1f - moved), Path.Direction.CCW);
                streamLeft = leftView.getRight() - (leftView.getWidth() / 2);
            }

            mDotPath.addRoundRect(streamLeft + 1
                    , streamTop
                    , streamRight - 1
                    , streamBottom
                    , leftView.getRight() + (distance / 2)
                    ,cy, Path.Direction.CCW);

            if(streamRight > rightView.getRight() - (rightView.getWidth() / 2)){
                mDotPath.addCircle(rightView.getRight() - (rightView.getWidth() / 2),
                        rightView.getBottom() - (rightView.getHeight() / 2),
                        (rightView.getWidth() / 2) * offset, Path.Direction.CCW);
            }
            mDotPath.close();
            canvas.drawPath(mDotPath, mPaint);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mPosition = position;
        mPositionOffset = positionOffset;
        mPositionOffsetPixels = positionOffsetPixels;
        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
        mPosition = position;
        if(mAdapter != null){
            int pos = mAdapter.getVirtualPosition(position);
            changedPos(pos);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {

        }
    }

    @Override
    public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {
        if(newAdapter != null && newAdapter instanceof BasePagerAdapter){
            mAdapter = (BasePagerAdapter) newAdapter;
            notifyDataChanged();
            int pos = mAdapter.getVirtualPosition(mViewPager.getCurrentItem());
            if(getChildCount() > pos){
                View view = getChildAt(pos);
                if(view != null){
                    view.setSelected(true);
                }
            }
        }
    }
}

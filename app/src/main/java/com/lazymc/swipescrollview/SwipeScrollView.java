package com.lazymc.swipescrollview;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * Created by youyunkeji on 16/11/2.
 */

public class SwipeScrollView extends FrameLayout {
    private static final String TAG = "SwipeScrollView";
    private View mContentView;
    private Scroller mScroller;
    private int mTouchSlop;
    private float mX;//移动手指时的x坐标
    private float mLastX;//最后也是第一次按下手指时的坐标
    private int leftBord;//左边界限
    private int rightBord;//右边界限
    private float mFastY;//第一次按下手指y坐标
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private VelocityTracker mVelocityTracker;

    public SwipeScrollView(Context context) {
        super(context);
    }

    public SwipeScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SwipeScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if (child == null || getResources() == null || child.getTag() == null) return;
        if (child.getTag().toString().equals(getResources().getString(R.string.swipe_content_tag))) {
            mContentView = child;
            mScroller = new Scroller(getContext());
            mTouchSlop = ViewConfiguration.get(getContext()).getScaledPagingTouchSlop();
            mMinimumVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();

            mMaximumVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mContentView == null) {
            return;
        }
        mContentView.layout(left, top, right, bottom);
        leftBord = left;
        left = right;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child == mContentView) continue;
            right = child.getMeasuredWidth() + left;
            child.layout(left, top, right, bottom);
            left = right;
        }
        rightBord = getChildAt(getChildCount() - 1).getRight();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = ev.getRawX();
                mX = 0;
                mFastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                mX = ev.getRawX();
                mLastX = mX;
                float diff = Math.abs(mX - mLastX);
                if (diff > mTouchSlop && (diff > Math.abs(ev.getRawY() - mFastY))) {
                    return true;
                }
                break;
        }
        if (super.onInterceptTouchEvent(ev)) {
            return false;
        }

        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        obtainVelocityTracker(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mScroller.abortAnimation();
                break;
            case MotionEvent.ACTION_MOVE:
                mX = event.getRawX();
                int scrollX = (int) (mLastX - mX);
                if (getScrollX() + scrollX < leftBord) {
                    scrollTo(leftBord, 0);
                    return true;
                }
                scrollBy(scrollX, 0);
                mLastX = mX;
                break;
            case MotionEvent.ACTION_UP:
                if (getScrollX() > 0&&getScrollX()>rightBord-getWidth()) {
                    VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int hX = (int) velocityTracker.getXVelocity();
                    if ((Math.abs(hX) > mMinimumVelocity)) {
                        mScroller.fling(getScrollX(), getScrollY(), hX, 0, getWidth(), rightBord, 0, 0);
                        invalidate();
                        Log.d(TAG, "onTouchEvent: 1");
                    }else{
                        mScroller.startScroll(getScrollX(),getScrollY(),-(rightBord-getWidth()),0);
                        invalidate();
                        Log.d(TAG, "onTouchEvent: 2");
                    }
                }
                releaseVelocityTracker();
                break;
        }
        return true;
    }

    private void obtainVelocityTracker(MotionEvent event) {

        if (mVelocityTracker == null) {

            mVelocityTracker = VelocityTracker.obtain();

        }

        mVelocityTracker.addMovement(event);

    }


    private void releaseVelocityTracker() {

        if (mVelocityTracker != null) {

            mVelocityTracker.recycle();

            mVelocityTracker = null;

        }

    }
}

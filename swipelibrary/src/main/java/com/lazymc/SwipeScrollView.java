package com.lazymc.swipelibrary;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewCompat;
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
    private float mFastX;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private VelocityTracker mVelocityTracker;
    private boolean isScroller = false;//是否滑动中
    private boolean isVerticScroll;//是否垂直方向滚动
    private boolean isDebug=false;

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
            mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
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
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if ((visibility == INVISIBLE || visibility == GONE) && getScrollX() != 0) {
            close();
        }
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
                if (Math.abs(ev.getRawY() - mFastY) > mTouchSlop) {
                    isVerticScroll = true;
                    mFastY = ev.getRawY();
                    return super.onInterceptTouchEvent(ev);
                }
                mFastY = ev.getRawY();
                isVerticScroll = false;
                mX = ev.getRawX();
                float diff = Math.abs(mX - mLastX);
                mLastX = mX;
                if (diff > mTouchSlop || ev.getEventTime() - ev.getDownTime() > 115) {
                    return isScroller = true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            default:
                isScroller = false;
                break;
        }

        return super.onInterceptTouchEvent(ev);
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
                mFastY=event.getRawY();
                mFastX=event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:

                mX = event.getRawX();
                int scrollX = (int) (mLastX - mX);
                mLastX = mX;
                if (Math.abs(event.getRawY() - mFastY) > Math.abs(scrollX)) {
                    mFastY=event.getRawY();
                    return false;
                }
                mFastY=event.getRawY();
                if (getScrollX() + scrollX < leftBord) {
                    close();
                    isScroller = true;
                    return true;
                }
                requestDisallowInterceptTouchEvent(true);
                scrollBy(scrollX, 0);
                isScroller = true;
                break;
            case MotionEvent.ACTION_UP:
                if (!isVerticScroll)
                    up(event);
                return false;
        }
        return true;
    }

    /**
     * 滚动后点击内容区域时恢复原始位置操作
     *
     * @param event
     * @return
     */
    public boolean up(MotionEvent event) {
        if (!isScroller && getScrollX() != 0) {
            close();
            return true;
        }

        int diffX= (int) (event.getRawX()-mLastX);

        if (getScrollX()==0){
            if (event.getX()-mFastX>=mTouchSlop){
                isScroller = false;
                return false;
            }
        }

        //点击要操作的item项功能
        if (!isScroller && getScrollX() == 0&&event.getEventTime()-event.getDownTime()<=500) {
            if (Math.abs(diffX)>=mTouchSlop)
                return true;
            Object v = getParent();
            if (v != null) {
                ViewGroup vg = (ViewGroup) v;
                vg.performClick();
            }
            return true;
        }

        if (getScrollX()==0&&getScrollY()==0&&event.getEventTime()-event.getDownTime()<=500){
            if (Math.abs(diffX)>=mTouchSlop)
                return true;
            Object v = getParent();
            if (v != null) {
                ViewGroup vg = (ViewGroup) v;
                vg.performClick();
            }
            return true;
        }

        VelocityTracker velocityTracker = mVelocityTracker;
        velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
        int hX = (int) velocityTracker.getXVelocity();
        if (isDebug)
        Log.d(TAG, "up: " + hX);
        if (getScrollX() > 0) {
            mScroller.extendDuration(100);
            int diff = 0;
            if (getScrollX() >= (rightBord - getWidth()) / 2) {
                diff = -(getScrollX() - (rightBord - getWidth()));
                mScroller.startScroll(getScrollX(), getScrollY(), diff, 0);
                invalidate();
                if (isDebug)
                Log.d(TAG, "up: __");
            } else {
                if (hX>=-1000){
                    close();
                }else {
                    mScroller.fling(getScrollX(), getScrollY(), -hX, 0, 0, rightBord - getWidth(), 0, 0);
                    invalidate();
                }
                if (isDebug)
                Log.d(TAG, "up: _|");
            }
        } else {
            if (hX > mMinimumVelocity) {
                mScroller.fling(getScrollX(), getScrollY(), -hX, 0, 0, rightBord - getWidth(), 0, 0);
                invalidate();
                if (isDebug)
                Log.d(TAG, "up: __||");
            }
        }
        releaseVelocityTracker();
        isScroller = false;
        return false;
    }

    public void close() {
        if (getScrollX() != 0) {
            mScroller.startScroll(getScrollX(), getScrollY(), -(getScrollX()), 0);
            invalidate();
        } else {
            scrollTo(leftBord, 0);
        }
    }

    public View findChildViewUnder(float x, float y) {
        final int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            final float translationX = ViewCompat.getTranslationX(child);
            final float translationY = ViewCompat.getTranslationY(child);
            if (x >= child.getLeft() + translationX &&
                    x <= child.getRight() + translationX &&
                    y >= child.getTop() + translationY &&
                    y <= child.getBottom() + translationY) {
                return child;
            }
        }
        return null;
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

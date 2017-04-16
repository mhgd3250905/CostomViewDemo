package com.skkk.ww.costomviewdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by admin on 2017/4/11.
 */
/*
* 
* 描    述：常见的可以拖动删除的Item
* 作    者：ksheng
* 时    间：2017/4/11$ 22:06$.
*/
public class DragItemView extends ViewGroup {

    private ViewDragHelper dragHelper;
    private LinearLayout llShow;
    private LinearLayout llHide;
    private int maxWidth;//可以拖拽的最大距离
    private int leftBorder;
    private boolean dragToRight;//是否向右拖动
    private boolean mIsMoving;//是否正在拖动

    private RecyclerView rv;

    public DragItemView(Context context) {
        super(context);
        mInit();
    }

    public DragItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInit();

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DragItemView);

    }

    public DragItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mInit();
    }

    private void mInit() {
        dragHelper = ViewDragHelper.create(this, callback);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            getChildAt(0).layout(l, t, r, b);
            getChildAt(1).layout(l, t, r, b);
            maxWidth = llShow.getMeasuredWidth() / 2;
            leftBorder = llShow.getLeft();
            if (getParent().getParent() instanceof RecyclerView) {
                rv = (RecyclerView) getParent().getParent();
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        llHide = (LinearLayout) getChildAt(0);
        llShow = (LinearLayout) getChildAt(1);
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == llShow;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return 0;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (left >= leftBorder && left < (leftBorder + maxWidth)) {
                return left;
            } else if (left >= (leftBorder + maxWidth)) {
                return leftBorder + maxWidth;
            }
            return 0;
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
            mIsMoving = true;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (releasedChild.getLeft() < (leftBorder + maxWidth / 3)) {
                dragHelper.smoothSlideViewTo(llShow, leftBorder, 0);
            } else if (releasedChild.getLeft() >= (leftBorder + maxWidth * 1 / 3)
                    && releasedChild.getLeft() <= (leftBorder + maxWidth)) {
                if (dragToRight) {
                    dragHelper.smoothSlideViewTo(llShow, leftBorder + maxWidth, 0);
                } else {
                    dragHelper.smoothSlideViewTo(llShow, leftBorder, 0);
                }
            }
            ViewCompat.postInvalidateOnAnimation(DragItemView.this);
            mIsMoving = false;

        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (dx > 0) {
                dragToRight = true;
            } else if (dx < 0) {
                dragToRight = false;
            }
            if (mIsMoving && rv != null) {
                rv.setLayoutFrozen(true);
            } else if(!mIsMoving && rv != null){
                rv.setLayoutFrozen(false);
            }
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return 1;
        }

    };

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return dragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
}

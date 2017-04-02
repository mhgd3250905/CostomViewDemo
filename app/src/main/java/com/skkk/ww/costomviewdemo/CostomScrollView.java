package com.skkk.ww.costomviewdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by admin on 2017/3/30.
 */
/*
* 
* 描    述：自定义ScrollView
* 作    者：ksheng
* 时    间：2017/3/30$ 22:55$.
*/
public class CostomScrollView extends ViewGroup {
    private Scroller scroller;
    private int childCount;
    private int lastY;
    private int downY;
    private int moveY;
    private int topBorder;
    private int bottomBorder;
    private int touchGap;
    private int scrolledY;
    private int childHeight;

    public CostomScrollView(Context context) {
        super(context);
        mInit(context);
    }


    public CostomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInit(context);
    }

    public CostomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mInit(context);
    }

    private void mInit(Context context) {
        scroller = new Scroller(context);
        touchGap = ViewConfiguration.get(context).getScaledTouchSlop();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                childView.layout(0, i * childView.getMeasuredHeight(),
                        childView.getMeasuredWidth(), (i + 1) * childView.getMeasuredHeight());
            }
            topBorder = getChildAt(1).getTop();
            bottomBorder = getChildAt(childCount - 1).getTop();
            childHeight = getChildAt(0).getHeight();
            scrollTo(0,topBorder);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = (int) ev.getRawY();
                lastY = downY;
                break;
            case MotionEvent.ACTION_MOVE:
                moveY = (int) ev.getRawY();
                int diff = Math.abs(moveY - downY);
                lastY = downY;
                if (diff > touchGap) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_MOVE:
                moveY = (int) event.getRawY();
                scrolledY = lastY - moveY;
                scrollBy(0, scrolledY);
                lastY = moveY;
                break;
            case MotionEvent.ACTION_UP:
                int index = (getScrollY() + childHeight / 2) / childHeight;
                int dy = index * childHeight - getScrollY();
                if (getScrollY() < topBorder) {
                    dy=topBorder-getScrollY();
                }
                if (getScrollY() > bottomBorder) {
                    dy=bottomBorder-getScrollY();
                }
                scroller.startScroll(0, getScrollY(), 0, dy);
                invalidate();
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }
    }
}

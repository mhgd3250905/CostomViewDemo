package com.skkk.ww.costomviewdemo;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by admin on 2017/4/2.
 */
/*
* 
* 描    述：可以为RecyclerView设置下拉刷新栏位的自定义布局
* 作    者：ksheng
* 时    间：2017/4/2$ 21:37$.
*/
public class HeaderLayout extends ViewGroup {
    private Scroller scroller;
    private int childCount;
    private int lastY;
    private int downY;
    private int moveY;
    private int topBorder;
    private int bottomBorder;
    private int touchGap;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private int scrolledY;
    private int dy;

    public HeaderLayout(Context context) {
        super(context);
        mInit(context);
    }

    public HeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInit(context);
    }

    public HeaderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mInit(context);
    }

    private void mInit(Context context) {
        scroller=new Scroller(context);
        touchGap = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            childCount = getChildCount();
            getChildAt(0).layout(0,0, getChildAt(0).getMeasuredWidth(), getChildAt(0).getMeasuredHeight());
            getChildAt(1).layout(0, getChildAt(0).getMeasuredHeight(),
                    getChildAt(0).getMeasuredWidth(),
                    getChildAt(0).getMeasuredHeight() + getChildAt(1).getMeasuredHeight());

//            layout(0,0,
//                    getChildAt(0).getMeasuredWidth(),
//                    getChildAt(0).getMeasuredHeight() + getChildAt(1).getMeasuredHeight());
            topBorder = getChildAt(1).getTop();
            bottomBorder=getChildAt(1).getTop();
            scrollTo(0, topBorder);
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                downY= (int) ev.getRawY();
                lastY=downY;
                break;

            case MotionEvent.ACTION_MOVE:
                moveY= (int) ev.getRawY();
                lastY=downY;
                int diffY=moveY-downY;
//                Logger.i("moveY-downY="+diffY);选择
                recyclerView= (RecyclerView) getChildAt(1);
                layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (diffY>touchGap&&diffY>0) {
                    if (layoutManager.findFirstCompletelyVisibleItemPosition()==0) {
//                        Logger.w("传递到下一层");
                        return true;
                    }
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downY= (int) event.getRawY();
                lastY=downY;
                if (getScrollY()!=topBorder){
                    return false;
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                moveY= (int) event.getRawY();
                if (getScrollY()>bottomBorder){
                    scrollTo(0,bottomBorder);
                }
                scrolledY = lastY-moveY;
                scrollBy(0, scrolledY);
                lastY=moveY;
                break;
            case MotionEvent.ACTION_UP:
                dy = 0;
                if (getScrollY()<topBorder){
                    dy =topBorder-getScrollY();
                }
                Log.d("HeaderLayout", "getScrollY= " + getScrollY() + " dy= " + dy);
                scroller.startScroll(0,getScrollY(),0, dy);
                invalidate();
                break;

        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()){
            scrollTo(scroller.getCurrX(),scroller.getCurrY());
        }
        super.computeScroll();
    }
}

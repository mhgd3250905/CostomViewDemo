package com.skkk.ww.costomviewdemo;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by admin on 2017/4/2.
 */
/*
* 
* 描    述：可以为RecyclerView设置下拉刷新栏位的自定义布局
* 作    者：ksheng
* 时    间：2017/4/2$ 21:37$.
*/
public class RefreshLayout extends ViewGroup implements interfacePullToRefresh {
    private ViewDragHelper viewDragHelper;
    private HeaderView headerView;
    private RecyclerView recyclerView;
    private int headerHeight;
    private boolean isRelease = true;//Flag：是否松开手指

    public interface OnHeaderRefreshListener {
        void onRefreshListener();
    }

    private OnHeaderRefreshListener onHeaderRefreshListener;

    public void setOnHeaderRefreshListener(OnHeaderRefreshListener onHeaderRefreshListener) {
        this.onHeaderRefreshListener = onHeaderRefreshListener;
    }

    public RefreshLayout(Context context) {
        super(context);
        mInit();
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInit();
    }

    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mInit();
    }

    /**
     * 设置LinearLayout布局方向
     * 初始化viewDragHelper
     */
    private void mInit() {
        viewDragHelper = ViewDragHelper.create(this, callback);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        recyclerView.layout(l, t, r, b);
        headerView.layout(l, t - getChildAt(0).getMeasuredHeight(), r, t);
        headerHeight = getChildAt(0).getMeasuredHeight();
    }

    //完成绘制后调用
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        headerView = (HeaderView) getChildAt(0);
        recyclerView = (RecyclerView) getChildAt(1);
    }


    //将事件拦截交由viewDragHelper处理
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    //将事件交由viewDragHelper处理
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }


    private ViewDragHelper.Callback callback =
            new ViewDragHelper.Callback() {

                /**
                 * 指定哪个View需要移动
                 * @param child 需要移动的View
                 * @param pointerId
                 * @return
                 */
                @Override
                public boolean tryCaptureView(View child, int pointerId) {
                    return recyclerView == child;
                }

                /**
                 * 指定在纵坐标方向响应拖动
                 * @param child
                 * @param top
                 * @param dy
                 * @return 返回被拖动的纵向距离
                 */
                @Override
                public int clampViewPositionVertical(View child, int top, int dy) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0
                            && top > 0) {
                        return top;
                    }
                    return 0;
                }

                /**
                 * 指定在横坐标方向响应拖动
                 * @param child 被拖动的View
                 * @param left 拖动之后的left
                 * @param dx 拖动的距离
                 * @return 返回响应拖动的横向距离
                 */
                @Override
                public int clampViewPositionHorizontal(View child, int left, int dx) {
                    return 0;
                }

                /**
                 * 拖动结束ACTION_UP触发的事件
                 * @param releasedChild 移动的View
                 * @param xvel X轴方向拖动速度
                 * @param yvel Y轴方向拖动熟读
                 */
                @Override
                public void onViewReleased(View releasedChild, float xvel, float yvel) {
                    super.onViewReleased(releasedChild, xvel, yvel);
                    isRelease = true;//在拖拽结束的时候设置为true
                    if (recyclerView.getTop() < headerHeight) {
                        viewDragHelper.smoothSlideViewTo(releasedChild, 0, 0);
                    } else {
                        viewDragHelper.smoothSlideViewTo(releasedChild, 0, headerHeight);
                    }
                    ViewCompat.postInvalidateOnAnimation(RefreshLayout.this);
                }

                /**
                 * 监听拖动的状态变化：1.开始 2.放手 3.动画结束
                 * @param state
                 */
                @Override
                public void onViewDragStateChanged(int state) {
                    super.onViewDragStateChanged(state);
                }

                /**
                 * 拖拽开始
                 * @param capturedChild
                 * @param activePointerId
                 */
                @Override
                public void onViewCaptured(View capturedChild, int activePointerId) {
                    super.onViewCaptured(capturedChild, activePointerId);
                    isRelease = false;//在拖拽开始的时候设置为false
                }

                /**
                 * 当拖动View位置改变时候触发的事件
                 * @param changedView 拖动的View
                 * @param left 水平方向位置
                 * @param top 垂直方向位置
                 * @param dx X轴方向拖动距离
                 * @param dy Y轴方向拖动距离
                 */
                @Override
                public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                    super.onViewPositionChanged(changedView, left, top, dx, dy);
                    headerView.layout(left, top - headerView.getMeasuredHeight()
                            , left + headerView.getMeasuredWidth(), top);

                    //这里我们对下拉的位置进行监听，然后设置对应的headerView的状态
                    if (top == 0f) {

                        headerView.setRefreshStatus(HeaderView.NORMAL);

                    } else if (top < headerHeight && dy > 0 && !isRelease) {//当前为下拉的第一阶段pull
                        headerView.setRefreshStatus(HeaderView.PULL);

                    } else if (top > headerHeight && dy > 0 && !isRelease) {//当前为下拉的第二阶段ready
                        headerView.setRefreshStatus(HeaderView.READY);

                    } else if (top > headerHeight && dy < 0 && isRelease) {//当前为松开手之后第三阶段release
                        headerView.setRefreshStatus(HeaderView.RELEASE);

                    } else if (top == headerHeight && isRelease) {//当前为最后一个阶段refresh
                        headerView.setRefreshStatus(HeaderView.REFRESHING);
                        doInRefreshing();
                    }
                }

                /**
                 * 当被拖动的View可以消费触摸事件的时候，指定屏蔽的方向
                 * 这个这里指当view从view.getTop()位置向下1dp的距离拖动的时候拦截事件不作分发
                 * @param child 拖动的View
                 * @return
                 */
                @Override
                public int getViewVerticalDragRange(View child) {
                    return child.getTop() + 1;
                }
            };


    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void startRefreshing() {
        if (isRefreshing()) {
            return;
        }
        headerView.setRefreshStatus(HeaderView.REFRESHING);
        viewDragHelper.smoothSlideViewTo(headerView, 0, 300);
        viewDragHelper.smoothSlideViewTo(recyclerView, 0, 300);
        ViewCompat.postInvalidateOnAnimation(RefreshLayout.this);
    }

    @Override
    public void doInRefreshing() {
        if (onHeaderRefreshListener != null) {
            onHeaderRefreshListener.onRefreshListener();
        }
    }

    @Override
    public boolean isRefreshing() {
        return headerView.getAnimState() == HeaderView.REFRESHING ? true : false;
    }

    @Override
    public void cancelRefresh() {
        if (isRefreshing()) {
            headerView.setRefreshStatus(HeaderView.NORMAL);
            viewDragHelper.smoothSlideViewTo(headerView, 0, 0);
            viewDragHelper.smoothSlideViewTo(recyclerView, 0, 0);
            ViewCompat.postInvalidateOnAnimation(RefreshLayout.this);
        }
    }


}

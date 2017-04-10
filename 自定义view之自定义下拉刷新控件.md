## 自定义view之自定义下拉刷新控件

>经过了之前的学习，这一次撸一个能用的：在很多app中都有下拉刷新的功能，所以这次就做这个，ok，先看一下效果吧！

![效果](https://github.com/mhgd3250905/CostomViewDemo/blob/master/img/refreshshow.gif?raw=true)

### 1 如何实现下拉刷新

>俗话说站在巨人的肩膀上才可以看得更远，现在已有的下拉刷新框架其实都已经相当成熟了，比如官方的swipeRefreshLayout，又或者BGARefreshLayout，都是相当优秀的框架，使用起来也很容易上手，不过自己亲手去实现一个还是可以得到很多锻炼的

下面我们分析一下如何去实现下拉刷新，罗列出关键词：
- 自定义RefreshView
- recyclerView(自从解锁了recyclerView之后就再也没有用过ListView)
- HeaderView(刷新头)
- View的拖动
- 动画（属性动画）

接下来我们就一步一步去实现吧！

### 2 自定义RefreshView

先看一下我的构思图（布局）吧！
![效果](https://github.com/mhgd3250905/CostomViewDemo/blob/master/img/refresh_layout.png?raw=true)

一目了然，我们首先将刷新头布局（后面都使用HeaderView替代)隐藏在屏幕的上方，当我们拖动列表的时候，它顺势被脱下来...拖下来，然后进行刷新的一些操作；

好吧，我们先把需要自定义的两个View给定义出来，HeaderView+RefreshView：

#### 1.HeaderView
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="100dp"
    android:orientation="horizontal">

    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_weight="3">

        <ProgressBar
            android:id="@+id/pb_header"
            android:layout_width="50dp"
            android:layout_height="50dp"
            android:layout_gravity="center" />

        <ImageView
            android:src="@drawable/arrow_down"
            android:id="@+id/iv_header"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />
    </FrameLayout>

    <TextView
        android:id="@+id/tv_header"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_weight="1"
        android:gravity="center"
        android:text="下拉刷新!"
        android:textSize="30dp" />

</LinearLayout>
```
**这就是演示中下拉头的布局样式**   
自定义属性：
```xml
<declare-styleable name="HeaderView">
    <attr name="pullText" format="string"/>
    <attr name="readyText" format="string"/>
    <attr name="releaseText" format="string"/>
    <attr name="refreshText" format="string"/>
    <attr name="status" format="enum">
        <enum name="normal" value="0"/>
        <enum name="pull" value="1"/>
        <enum name="ready" value="2"/>
        <enum name="release" value="3"/>
        <enum name="refreshing" value="4"/>
    </attr>
</declare-styleable>
```
其实我们的需求很简单，就是通过给HeaderView设置不同的状态，然后显示出对应的动画，所以我们的如下定义：
```java
public class HeaderView extends LinearLayout{
    private TextView tvHeader;
    private ProgressBar pbHeader;
    private ImageView ivHeader;

    private String textPull,textReady,textRelease,textRefresh;
    private int statusRefresh;

    public final static int NORMAL=0;
    public final static int PULL=1;
    public final static int READY=2;
    public final static int RELEASE=3;
    public final static int REFRESHING=4;
    private int animState=0;

    public HeaderView(Context context) {
        super(context);
        mInit();
    }
    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInit();
        //获取自定义属性
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.HeaderView);
        textPull=ta.getString(R.styleable.HeaderView_pullText);
        textReady=ta.getString(R.styleable.HeaderView_readyText);
        textRelease=ta.getString(R.styleable.HeaderView_releaseText);
        textRefresh=ta.getString(R.styleable.HeaderView_refreshText);
        statusRefresh=ta.getInt(R.styleable.HeaderView_status,0);
        ta.recycle();
        setRefreshStatus(statusRefresh);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mInit();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    /**
     * 初始化布局
     */
    private void mInit() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_header,this,true);
        tvHeader= (TextView) findViewById(R.id.tv_header);
        ivHeader= (ImageView) findViewById(R.id.iv_header);
        pbHeader= (ProgressBar) findViewById(R.id.pb_header);
    }
    /**
     * 设置刷新状态
     * @param statusRefresh
     */
    public void setRefreshStatus(int statusRefresh) {
        switch (statusRefresh){
            case NORMAL://当整个header全部收起来
                break;
            case PULL://当我们向下开始拖动但header还没有全部露出来
                tvHeader.setText(textPull);
                ivHeader.setVisibility(VISIBLE);
                pbHeader.setVisibility(INVISIBLE);
                break;
            case READY://header全部露出来以后，继续向下拖动
                if (statusRefresh!=animState) {
                    ivHeader.animate().rotation(180).setDuration(500);
                }
                tvHeader.setText(textReady);
                ivHeader.setVisibility(VISIBLE);
                pbHeader.setVisibility(INVISIBLE);
                break;
            case RELEASE://当view开始回弹，但还没有到刷新位置
                tvHeader.setText(textRelease);
                ivHeader.setVisibility(VISIBLE);
                pbHeader.setVisibility(INVISIBLE);
                break;
            case REFRESHING://当view回弹到刚好显示header的时候
                tvHeader.setText(textRefresh);
                ivHeader.setVisibility(INVISIBLE);
                pbHeader.setVisibility(VISIBLE);
                break;
        }
        animState=statusRefresh;
    }

    public int getAnimState() {
        return animState;
    }
}
```
从上面的代码可以看到，我们将下拉刷新设置为了几个不同的阶段：

- NORMAL：当整个header全部隐藏起来；
- PULL：当我们向下开始拖动但header还没有全部露出来；
- READY：header全部露出来以后，继续向下拖动；
- RELEASE：当view开始回弹，但还没有到刷新位置；
- REFRESHING：当view回弹到刚好显示header的时候

并且根据阶段的不同设置布局文件中元素的隐藏或者变化，其中包含了一个动画，也就是当下拉刷新进入到Ready状态的时候我们对```ivHeader```进行了属性动画操作：

```java
ivHeader.animate().rotation(180).setDuration(500);
```

对于属性动画的介绍就不多说了，主要实现了箭头的旋转,这里强烈推荐郭霖大神的属性动画系列[Android属性动画完全解析(上)，初识属性动画的基本用法](http://blog.csdn.net/guolin_blog/article/details/43536355)；

**我们还需要注意到，在自定义HeaderView中我们暴露给外部的方法有只有两个```void setRefreshStatus(int statusRefresh)```和```int getAnimState()```,主要用于从外界对HeaderView设置状态以及获取到HeaderView当前所处的状态值**

完成效果如下：
![效果](https://github.com/mhgd3250905/CostomViewDemo/blob/master/img/headerView.png?raw=true)

#### 2.RefreshLayout

我们的布局文件是这样：
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    xmlns:header="http://schemas.android.com/apk/res-auto">

    <com.skkk.ww.costomviewdemo.RefreshLayout
        android:id="@+id/hl"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        <com.skkk.ww.costomviewdemo.HeaderView
            android:id="@+id/hv_header"
            header:status="pull"
            header:pullText="下拉刷新..."
            header:readyText="松开刷新..."
            header:releaseText="即将刷新..."
            header:refreshText="正在刷新..."
            android:layout_width="match_parent"
            android:layout_height="100dp"
            ></com.skkk.ww.costomviewdemo.HeaderView>

        <android.support.v7.widget.RecyclerView
            android:id="@+id/rv_header"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>
    </com.skkk.ww.costomviewdemo.RefreshLayout>
</LinearLayout>
```

这里其实是下拉刷新的核心控件，而核心中的核心就是下拉这个操作了，在上一篇自定义学习[自定义ScrollView](自定义view之自定义scrollView.md)中，我们使用Scroller来完成对View的拖动，这一次我选择了```ViewDragHelper```,```ViewDragHelper```的使用很简单，但大多数情况下我们需要做一些模板化的操作：
```java
//初始化
viewDragHelper = ViewDragHelper.create(this, callback);

//callback实现
private ViewDragHelper.Callback callback =
            new ViewDragHelper.Callback() {
              ···
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

//处理computeScroll方法
@Override
public void computeScroll() {
    if (viewDragHelper.continueSettling(true)) {
        ViewCompat.postInvalidateOnAnimation(this);
    }
}
```
也就是说我们之前需要根据不同的情况对事件进行拦截或怎样巴拉巴拉...现在都交给```viewDragHelper```来处理，然后我们只需要实现它的```callback```回调就可以了，```ViewDragHelper.Callback```中包含很多的回调方法，这里我挑出我需要的进行说明：

```java
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
}
/**
 * 当被拖动的View可以消费触摸事件的时候，指定屏蔽的方向
 * 这个这里指当view从view.getTop()位置向下1dp的距离拖动的时候拦截事件不作分发
 * @param child 拖动的View
 * @return
 */
@Override
public int getViewVerticalDragRange(View child) {
    return 0;
}
```

这就是我们需要使用到的方法，在使用之前，我们需要拿到我们操作的对象：

```java
//完成绘制后调用
@Override
protected void onFinishInflate() {
    super.onFinishInflate();
    headerView = (HeaderView) getChildAt(0);
    recyclerView = (RecyclerView) getChildAt(1);
}
```

OK,下面来进行我们```callback```的完成：

```java
@Override
public boolean tryCaptureView(View child, int pointerId) {
    return recyclerView == child;
}

@Override
public int clampViewPositionVertical(View child, int top, int dy) {
    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
    if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0
            && top > 0) {
        return top;
    }
    return 0;
}

@Override
public int clampViewPositionHorizontal(View child, int left, int dx) {
    return 0;
}
```
-  ```boolean tryCaptureView```：通过这个方法来确定可以被拖动的View,```return recyclerView == child```表示recyclerView可以被拖动，```return recyclerView == child||headerView==child```则表示两个View都可以被拖动；
- ```int clampViewPositionVertical```：返回值就是我们纵向拖动View随之被拖动的距离，这里我们进行了一点简单的处理，首先我们通过```recyclerView.getLayoutManager()```方法来获取其```LinearLayoutManager```（前提是我们确实给recyclerView设置了LinearLayoutManager），然后通过```layoutManager.findFirstCompletelyVisibleItemPosition()```方法获取当前可以看到的第一个Item的位置，判断是否为0，而且方法中的top，也就是View的下拉距离大于0，那么我们可以判断当前状况为已经下拉到了列表的顶端，而且继续下拉，那么我们就可以进行下拉操作；
- ```int clampViewPositionHorizontal```：同上，水平方向的拖动距离，这里设置为0，无法水平拖动；

这三个方法是实现拖动最简单的方法，不过我们这样设置之后，会发现下拉操作还是无效，这是因为当我们拖动的view本身会消费触摸事件的时候（很显然，recyclerView会消耗）我们需要重写```int getViewVerticalDragRange(View child)```方法，来进行事件的拦截：

```java
@Override
public int getViewVerticalDragRange(View child) {
    return child.getTop() + 1;
}
```

这个方法返回值为正的时候表示拦截给ViewGroup自己使用，返回为负的时候表示不拦截传递到子View，所以我们只需要返回正值就可以了；

这样我们就可以实现RecyclerView滑动到顶部的时候的下拉操作了，接下来我们处理下拉中的HeaderView变化，在上文中我们实现了HeaderView的5种状态，分别对应下拉过程中的5个阶段：
```java
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
    } else if (top == headerHeight && dy==0 && isRelease) {//当前为最后一个阶段refresh
        headerView.setRefreshStatus(HeaderView.REFRESHING);
    }
}
```

判断其实很简单，注意我们在其中添加了一个flag：isRelease（手指是否离开）：

```java
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
 * 拖动结束ACTION_UP触发的事件
 * @param releasedChild 移动的View
 * @param xvel X轴方向拖动速度
 * @param yvel Y轴方向拖动熟读
 */
@Override
public void onViewReleased(View releasedChild, float xvel, float yvel) {
    super.onViewReleased(releasedChild, xvel, yvel);
    isRelease = true;//在拖拽结束的时候设置为true
}
```
我们通过上面两个方法在手指触摸的时候设置isRelease为false，然后在手指离开的时候设置为true，所以我们在拖动的时候进行的判断就可以如下描述：

- 当拖动距离为0的时候，状态为NORMAL;
- 当拖动距离小于HeaderView高度，且下拉距离大于0（下拉方向为向下）且手指没有离开屏幕的时候，状态为PULL；
- 当拖动距离大于HeaderView高度，且下拉距离大于0（下拉方向为向下）且手指没有离开屏幕的时候，状态为READY；
- 当拖动距离大于HeaderView高度，且下拉距离小于0（下拉方向为向上，即回弹）且手指离开屏幕的时候，状态为RELEASE；
- 当拖动距离等于HeaderView高度，且下拉距离等于0（RecyclerView高度无移动）且手指离开屏幕的时候，状态为REFRESHING;

这样我们就完成了对HeaderView状态的变化设置，但是上方的REFRESHING状态我们该如何实现呢？我们的需求就是当我们达到了RELEASE状态之后，松开手指，回弹显示HeaderView然后显示为刷新，完成刷新后回弹收起，如果下拉仅仅触发PULL状态，然后松开手指，那么直接回弹收起，所以我们的处理需要在松开手指的方法中实现：

```java
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
```
好了，这样我们就完成了```RefreshLayout```的定义:全部代码如下：

```java
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
}
```

### 3.下拉刷新的任务

通过上面的代码我们完了我们需要的简单动画效果，但是我们还需要实现下拉刷新应有的一些基本方法，接口设计如下：
```java
public interface interfacePullToRefresh {
    void startRefreshing();//开始刷新
    void doInRefreshing();//刷新过程中做...
    boolean isRefreshing();//获取是否刷新
    void cancelRefresh();//取消刷新
}
```

使用接口

```java
public class RefreshLayout extends ViewGroup implements interfacePullToRefresh {
  ...

  public interface OnHeaderRefreshListener {
      void onRefreshListener();
  }
  private OnHeaderRefreshListener onHeaderRefreshListener;
  public void setOnHeaderRefreshListener(OnHeaderRefreshListener onHeaderRefreshListener) {
      this.onHeaderRefreshListener = onHeaderRefreshListener;
  }

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
```
接口方法实现说明一下：

- ```OnHeaderRefreshListener```:我们设计一个监听接口，包含了监听方法，然后加入到```doInRefreshing```方法中，作为REFRESHING状态下处理事件所用；
- ```void startRefreshing()```:开启刷新，手动进入REFRESHING状态，并且呼出HeaderView;
- ```boolean isRefreshing()```:获取当前是否处于刷新状态;
- ```void cancelRefresh()```:取消刷新；

### 4.完事具备，就差使用了

```java
public class MainActivity extends AppCompatActivity {
    private String TAG=this.getClass().getSimpleName();
    private RecyclerView rvHeader;
    private List<String> mDataList;
    private HeaderAdapter adapter;
    private RefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_header_main);

        refreshLayout = (RefreshLayout) findViewById(R.id.hl);
        rvHeader= (RecyclerView) findViewById(R.id.rv_header);
        rvHeader.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mDataList=new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mDataList.add("here is item "+i);
        }
        adapter = new HeaderAdapter(MainActivity.this,mDataList);
        rvHeader.setAdapter(adapter);

        adapter.setOnItemClickListener(new HeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int pos) {
                if (refreshLayout.isRefreshing()){
                    refreshLayout.cancelRefresh();
                }else {
                    refreshLayout.startRefreshing();
                }
            }
        });

        refreshLayout.setOnHeaderRefreshListener(new RefreshLayout.OnHeaderRefreshListener() {
            @Override
            public void onRefreshListener() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        refreshLayout.cancelRefresh();
                    }
                }).start();
            }
        });
    }


    /**
     * RecyclerView数据适配器
     */
    static class HeaderAdapter extends RecyclerView.Adapter<HeaderAdapter.MyViewHolder>{
        private Context context;
        private List<String> dataList;
        public OnItemClickListener onItemClickListener;
        public interface OnItemClickListener{
            void onItemClickListener(View view,int pos);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        public HeaderAdapter(Context context, List<String> dataList) {
            this.context = context;
            this.dataList = dataList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder viewHolder=
                    new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recycler,parent,false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            holder.tvItem.setText(dataList.get(position));
            if (onItemClickListener!=null){
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos=position;
                        onItemClickListener.onItemClickListener(v,pos);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{
            private TextView tvItem;

            public MyViewHolder(View itemView) {
                super(itemView);
                tvItem= (TextView) itemView.findViewById(R.id.tv_item);
            }
        }
    }
}
```
这样就可以获得一开始演示效果啦~

### 5.源码传送门

  [我是一只咸鱼，不想承认，也不能否认，不要同情我笨，又夸我天真，还梦想着翻身...](https://github.com/mhgd3250905/CostomViewDemo)

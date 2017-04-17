## 自定义view之自定义拖拽删除item

>再很多APP中我们都可以看见列表中的条目通过横向滑动来进行删除，或者露出下面的其他选项，这次我们就来实现一下这个效果：

![效果](https://github.com/mhgd3250905/CostomViewDemo/blob/master/img/dragItemShow.gif?raw=true)

### 1.样式设计

>我们在上一次的自定义下拉刷新使用了可以实现拖动效果的```ViewDragHelper```，这次我们依然使用这个辅助工具

思考一下实现的思路：

![效果](https://github.com/mhgd3250905/CostomViewDemo/blob/master/img/drapItem_1.png?raw=true)

上面就是具体实现的样式：
其实很简单，在我们的自定义ViewGroup中我们实现类似FrameLayout的布局，一个覆盖另一个，然后拖动上面的View就露出下里面的View，So easy
!
先写出布局文件：
```xml
<com.skkk.ww.costomviewdemo.DragItemView
    android:background="#55000000"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <LinearLayout
        android:id="@+id/ll_hide"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        <ImageView
            android:id="@+id/iv_delete"
            android:src="@drawable/vector_delete"
            android:text="DELETE"
            android:layout_width="60dp"
            android:layout_height="match_parent" />
    </LinearLayout>
    <LinearLayout
        android:background="#fff"
        android:id="@+id/ll_show"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        <TextView
            android:id="@+id/tv_item"
            android:textSize="30dp"
            android:gravity="center"
            android:text="This is a Item!"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>
    </LinearLayout>
</com.skkk.ww.costomviewdemo.DragItemView>
```
可以看见我们设置了两个LinearLayout作为子Viwe，对应着我们的内层item和外层item；

### 2.将滑动交给ViewDragHelper
下面我们自定义布局，并且委托ViewDragHelper负责所有的滑动事件
```java
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
        ...
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
```
这里需要注意一下我们重写的```onLayout```方法：
```java
@Override
protected void onLayout(boolean changed, int l, int t, int r, int b) {
    if (changed) {
        getChildAt(0).layout(l, t, r, b);
        getChildAt(1).layout(l, t, r, b);
        maxWidth = llShow.getMeasuredWidth() / 2;
        leftBorder = llShow.getLeft();
        rv = (RecyclerView) getParent().getParent();
    }
}
```
信息量很大：首先我们对两个子LinearView进行布局，也就是重叠，后一个压在前一个的身上...
然后我们设置item宽度的一般作为我们可以拖拽的最大宽度，然后item的左侧作为拖拽的左边界，然后我们获取了item的父布局的父布局，也就是我们的RecyclerView，到底用作什么，一会儿再解释；
**这算是一个使用ViewDragHelper来实现滑动托转的模板设置了,下一步是设置ViewDragHelper.Callback了**

### 3.ViewDragHelper.Callback
- 首先设置可以滑动的View：

```java
@Override
public boolean tryCaptureView(View child, int pointerId) {
    return child == llShow;
}
```
- 然后设置滑动方向反馈

```java
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
```
解释一下：垂直方向滑动返回0，也就是无法再垂直方向上拖拽；水平方向上拖拽，如果拖拽范围在左边界和最大拖拽距离之间，那么返回实际拖拽，如果拖拽到超过了最大拖拽距离，那么返回为最大拖拽距离，俗称，拖不动了；

- 设置水平拖拽拦截

```java
@Override
public int getViewHorizontalDragRange(View child) {
    return 1;
}
```
因为我们的列表item很有可能设置点击事件，所以我们需要保证我们的拖拽事件不会被item消耗掉，所以返回1：返回0或者负值，那么当item设置为可点击的时候，就无法触发拖拽；

- 当手指按下开始拖拽的时候:

```java
@Override
public void onViewCaptured(View capturedChild, int activePointerId) {
    super.onViewCaptured(capturedChild, activePointerId);
    mIsMoving=true;
}
```
这里我们设置一个flag，通过这个flag来判断是否处于正在拖拽状态：这里当我们开始拖拽的时候，设置为```mIsMoving=true```

- 当手指离开拖拽结束的时候：

```java
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
    mIsMoving=false;
}
```
这里很明显可以看到：我们将```mIsMoving=false```;
然后我们判断在拖拽结束的时候：如果view拖拽到最大拖拽距离的1/3以内，那么直接返回初始状态，俗称弹回去；如果view拖拽到超过1/3最大拖拽距离的时候，我们需要判断是否处于向右拖拽状态：这里解释一下,我们设置一个标记dragToRight，表示当前是否进行向右拖拽，我们设计为我们如果是向右拖拽，那么超过1/3最大拖拽距离的时候就让view移动到最大拖拽距离，如果不是向右拖拽（向左拖拽）的时候，那么直接弹回到初始位置，也就是还原关闭；

- 判断拖拽位置
```java
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
```
这个方法在被拖拽对象位置发生的时候回调，首先我们根据拖拽的dx正负判断是拖拽方向，然后设置dragToRight，然后我们判断当前是或否处于拖拽状态，如果处于拖拽状态，那么设置调用```setLayoutFrozen()```方法设置RecyclerView冻结，无法进行拖拽：这样是为了避免我们再拖拽item的时候触摸事件向下引起RecyclerView的滑动事件触发，印象拖拽体验；当并非拖拽状态的时候就解除RecyclerView冻结；

### 4.设置点击事件
为了给拖拽后的图像设置点击事件我们需要设计一下ViewHolder：
```java
public OnItemClickListener onItemClickListener;
public interface OnItemClickListener{
    void onItemClickListener(View view,int pos);
    void onDragButtonClickListener(View view,int pos);
}
public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
}

@Override
public void onBindViewHolder(MyViewHolder holder, final int position) {
    holder.tvItem.setText(dataList.get(position));
    if (onItemClickListener!=null){
        holder.llShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos=position;
                onItemClickListener.onItemClickListener(v,pos);
            }
        });
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos=position;
                onItemClickListener.onDragButtonClickListener(v,pos);
            }
        });
    }

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
```
这样，演示中的可以拖拽的item就实现了

### 5.源码传送门

  [我是一只咸鱼，不想承认，也不能否认，不要同情我笨，又夸我天真，还梦想着翻身...](https://github.com/mhgd3250905/CostomViewDemo)

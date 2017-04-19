## 自定义view之自定义弹簧特效

>在很多APP中我们都会看到类似弹簧的特效，之前觉得特别有意思，现在得自己动手实现一下，下面展示一下效果，动图看起来有点模糊...

![效果](https://github.com/mhgd3250905/CostomViewDemo/blob/master/img/bounceShow_1.gif?raw=true)

![效果](https://github.com/mhgd3250905/CostomViewDemo/blob/master/img/bounceShow_2.gif?raw=true)

### 1.使用到的知识列表

- 简单的自定义View
- 绘图
- Path的使用

### 2.自定义View
最基本的自定义View，不多说：
```java
/*
*
* 描    述：有弹簧特效的View
* 作    者：ksheng
* 时    间：2017/4/18$ 20:40$.
*/
public class DragBounceItem extends View {

  private PointF centerP;//圆心点
  private Paint paintCircle;//画笔
  private PointF lastP;
  private float width, height, offsetX, offsetY;


    public DragBounceItem(Context context) {
        super(context);
        mInit();
    }

    public DragBounceItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInit();
    }

    public DragBounceItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mInit();
    }

    private void mInit() {
        //初始化画笔
        paintCircle = new Paint();
        paintCircle.setStyle(Paint.Style.FILL);
        paintCircle.setColor(Color.GREEN);
        paintCircle.setDither(true);
        paintCircle.setAntiAlias(true);

        centerP = new PointF(0,0);
        lastP = new PointF(0, 0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //初始化中心点
        centerP = new PointF(200, h / 2);
        width = w;
        height = h;
        offsetX = 0f;
        offsetY = 0f;
        super.onSizeChanged(w, h, oldw, oldh);
    }
}
```
这里可以看到我们初始化了画笔，以及我们后面会用到的两个点；

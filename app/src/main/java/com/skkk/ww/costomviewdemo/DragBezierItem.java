package com.skkk.ww.costomviewdemo;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by admin on 2017/4/18.
 */
/*
* 
* 描    述：有贝塞尔球特效的View
* 作    者：ksheng
* 时    间：2017/4/18$ 20:40$.
*/
public class DragBezierItem extends View {
    private HorizontalLine horizontalLineTop,horizontalLineBottom;
    private VerticalLine verticalLineLeft,verticalLineRight;
    private float radius;//半径
    private PointF centerP;//圆心点
    private Paint paintCircle;//画笔
    private PointF lastP;
    private PointF currentP;


    public DragBezierItem(Context context) {
        super(context);
        mInit();
    }

    public DragBezierItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInit();
    }

    public DragBezierItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mInit();
    }

    private void mInit() {
        //初始化画笔
        paintCircle=new Paint();
        paintCircle.setStyle(Paint.Style.FILL);
        paintCircle.setColor(Color.GREEN);
        paintCircle.setDither(true);
        paintCircle.setAntiAlias(true);

        radius=200f;

        lastP=new PointF(0,0);
        currentP=new PointF(0,0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //初始化中心点
        centerP=new PointF(0,h/2);
        //初始化贝塞尔三阶曲线需要的四条表
        horizontalLineTop=new HorizontalLine(new PointF(centerP.x,centerP.y-radius),radius);
        horizontalLineBottom=new HorizontalLine(new PointF(centerP.x,centerP.y+radius),radius);
        verticalLineLeft=new VerticalLine(new PointF(centerP.x-radius,centerP.y),radius);
        verticalLineRight=new VerticalLine(new PointF(centerP.x+radius,centerP.y),radius);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Path ballPath=new Path();

        ballPath.moveTo(verticalLineLeft.middleP.x,verticalLineLeft.middleP.y);
        ballPath.cubicTo(verticalLineLeft.startP.x,verticalLineLeft.startP.y,
                horizontalLineTop.startP.x,horizontalLineTop.startP.y,
                horizontalLineTop.middleP.x,horizontalLineTop.middleP.y);

        ballPath.cubicTo(horizontalLineTop.endP.x,horizontalLineTop.endP.y,
                verticalLineRight.startP.x,verticalLineRight.startP.y,
                verticalLineRight.middleP.x,verticalLineRight.middleP.y);

        ballPath.cubicTo(verticalLineRight.endP.x,verticalLineRight.endP.y,
                horizontalLineBottom.endP.x,horizontalLineBottom.endP.y,
                horizontalLineBottom.middleP.x,horizontalLineBottom.middleP.y);

        ballPath.cubicTo(horizontalLineBottom.startP.x,horizontalLineBottom.startP.y,
                verticalLineLeft.endP.x,verticalLineLeft.endP.y,
                verticalLineLeft.middleP.x,verticalLineLeft.middleP.y);
        canvas.drawPath(ballPath,paintCircle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float dx=0;
        float dy=0;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastP.x=event.getX();
                break;

            case MotionEvent.ACTION_MOVE:

                currentP.x=event.getX();
                dx=currentP.x-lastP.x;
                lastP.x=currentP.x;

                if ((verticalLineRight.middleP.x-(centerP.x+radius))<radius){
                    verticalLineRight.setMiddleP(new PointF(verticalLineRight.middleP.x+dx,verticalLineRight.middleP.y));

                }else if ((verticalLineRight.middleP.x-(centerP.x+radius))>=radius
                        &&(verticalLineRight.middleP.x-(centerP.x+radius))<=2*radius){
                    verticalLineRight.setMiddleP(new PointF(verticalLineRight.middleP.x+dx,verticalLineRight.middleP.y));

                    ValueAnimator animRight=ValueAnimator.ofFloat(verticalLineRight.middleP.x,centerP.x+3*radius);
                    animRight.setInterpolator(new LinearInterpolator());
                    animRight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float updateX= (float) animation.getAnimatedValue();
                            verticalLineRight=new VerticalLine(new PointF(updateX,centerP.y),radius);
                            postInvalidate();
                        }
                    });

                    ValueAnimator animTopAndBottom=ValueAnimator.ofFloat(horizontalLineTop.middleP.x,centerP.x+2*radius);
                    animTopAndBottom.setInterpolator(new LinearInterpolator());
                    animTopAndBottom.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float updateX= (float) animation.getAnimatedValue();
                            horizontalLineTop=new HorizontalLine(new PointF(updateX,centerP.y-radius),radius);
                            horizontalLineBottom=new HorizontalLine(new PointF(updateX,centerP.y+radius),radius);
                            postInvalidate();
                        }
                    });

                    ValueAnimator animLeft=ValueAnimator.ofFloat(verticalLineLeft.middleP.x,centerP.x+radius);
                    animLeft.setInterpolator(new LinearInterpolator());
                    animLeft.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float updateX= (float) animation.getAnimatedValue();
                            verticalLineLeft=new VerticalLine(new PointF(updateX,centerP.y),radius);
                            postInvalidate();
                        }
                    });

                    AnimatorSet set=new AnimatorSet();
                    set.play(animRight).with(animTopAndBottom).with(animLeft);
                    set.setDuration(400);
                    set.start();

                }

                break;

            case MotionEvent.ACTION_UP:

                break;
        }
        invalidate();
        return true;
    }
}

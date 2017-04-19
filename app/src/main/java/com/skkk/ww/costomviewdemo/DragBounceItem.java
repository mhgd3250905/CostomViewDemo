package com.skkk.ww.costomviewdemo;

import android.animation.Animator;
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
import android.view.animation.DecelerateInterpolator;

import Utils.BounceUtils;

/**
 * Created by admin on 2017/4/18.
 */
/*
* 
* 描    述：有贝塞尔球特效的View
* 作    者：ksheng
* 时    间：2017/4/18$ 20:40$.
*/
public class DragBounceItem extends View {

    private float radius;//半径
    private PointF centerP;//圆心点
    private Paint paintCircle;//画笔
    private PointF lastP;
    private PointF currentP;
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

        radius = 200f;

        lastP = new PointF(0, 0);
        currentP = new PointF(0, 0);
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

    @Override
    protected void onDraw(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(centerP.x, 0);
        path.quadTo(centerP.x + offsetX, centerP.y + offsetY, centerP.x, height);
        path.lineTo(0, height);
        path.close();
        canvas.drawPath(path, paintCircle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastP.x = event.getX();
                lastP.y = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = event.getX() - lastP.x;
                float dy = event.getY() - lastP.y;
                lastP.x = event.getX();
                lastP.y = event.getY();
                offsetX += dx;
                offsetY += dy;
                break;
            case MotionEvent.ACTION_UP:
                default:
                float[] bounceNumX = BounceUtils.getBounceNum(offsetX, 20);
                float[] bounceNumY = BounceUtils.getBounceNum(offsetY, 20);
                ValueAnimator animX = ValueAnimator.ofFloat(bounceNumX);
                animX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        offsetX= (float) animation.getAnimatedValue();
                        postInvalidate();
                    }
                });
                ValueAnimator animY = ValueAnimator.ofFloat(bounceNumY);
                animY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        offsetY= (float) animation.getAnimatedValue();
                        postInvalidate();
                    }
                });
                AnimatorSet set = new AnimatorSet();
                set.play(animX).with(animY);
                set.setDuration(1000);
                set.setInterpolator(new DecelerateInterpolator());
                set.start();
                set.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        offsetX = 0f;
                        offsetY = 0f;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
        }
        invalidate();
        return true;
    }
}

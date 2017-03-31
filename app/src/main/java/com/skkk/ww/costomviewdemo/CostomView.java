package com.skkk.ww.costomviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by admin on 2017/3/27.
 */
/*
* 
* 描    述：自定义View学习
* 作    者：ksheng
* 时    间：2017/3/27$ 21:38$.
*/
public class CostomView extends View {
    private Paint paint;
    private int[] colors = new int[]{
            Color.GREEN, Color.YELLOW, Color.RED, Color.BLUE, Color.WHITE, Color.GRAY
    };
    private Paint textPaint;

    public CostomView(Context context) {
        super(context);
        initPaint();
    }

    public CostomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public CostomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        paint = new Paint();

        paint.setStrokeWidth(10f);
        paint.setDither(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //设置半径
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        float radius = Math.min(getMeasuredWidth() / 2, getMeasuredHeight() / 2) * 6 / 7;
        //画圆
        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, radius, paint);
        //画12个锯齿
        for (int i = 0; i < 6; i++) {
            paint.setColor(colors[i]);
            paint.setStyle(Paint.Style.FILL);
            RectF rectF=new RectF(getMeasuredWidth()/2-radius,getMeasuredHeight()/2-radius,
                    getMeasuredWidth()/2+radius,getMeasuredHeight()/2+radius);
            canvas.drawArc(rectF,(60f)*i,(60f),true,paint);

            textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setTextSize(70);
            textPaint.setColor(Color.BLACK);

            canvas.drawText(String.valueOf(i),
                    (float)(getMeasuredWidth()/2+(radius/2)*Math.cos(i*Math.PI/3+Math.PI /6)-textPaint.measureText(String.valueOf(i)) / 2),
                    (float)(getMeasuredHeight()/2+(radius/2)*Math.sin(i*Math.PI/3+Math.PI /6)-(textPaint.ascent()+textPaint.descent())/2),
                    textPaint);
        }
        paint.setColor(Color.LTGRAY);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, 50, paint);
    }

}

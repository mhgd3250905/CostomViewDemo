package com.skkk.ww.costomviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
        paint=new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
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
        float radius=Math.min(getMeasuredWidth()/2,getMeasuredHeight()/2)*2/3;
        canvas.drawCircle(getMeasuredWidth()/2,getMeasuredHeight()/2, radius,paint);
        for (int i = 0; i<12; i++) {
            float startX= (float) (getMeasuredWidth()/2+(radius)*Math.cos((Math.PI/6)*i));
            float startY= (float) (getMeasuredHeight()/2-(radius)*Math.sin((Math.PI/6)*i));
            float endX= (float) (getMeasuredWidth()/2+(radius+50)*Math.cos((Math.PI/6)*i));
            float endY= (float) (getMeasuredHeight()/2-(radius+50)*Math.sin((Math.PI/6)*i));
            canvas.drawLine(startX,startY,endX,endY,paint );
            for (int j = 0; j < 6; j++) {
                float startX2= (float) (getMeasuredWidth()/2+(radius)*Math.cos((i*Math.PI/6+j*Math.PI/30)));
                float startY2= (float) (getMeasuredHeight()/2-(radius)*Math.sin((i*Math.PI/6+j*Math.PI/30)));
                float endX2= (float) (getMeasuredWidth()/2+(radius+20)*Math.cos((i*Math.PI/6+j*Math.PI/30)));
                float endY2= (float) (getMeasuredHeight()/2-(radius+20)*Math.sin((i*Math.PI/6+j*Math.PI/30)));
                canvas.drawLine(startX2,startY2,endX2,endY2,paint );
            }
        }
        canvas.drawCircle(getMeasuredWidth()/2,getMeasuredHeight()/2, radius+60,paint);
    }
}

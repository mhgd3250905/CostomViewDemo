package com.skkk.ww.costomviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by admin on 2017/3/29.
 */
/*
* 
* 描    述：颜色变幻的文本样式
* 作    者：ksheng
* 时    间：2017/3/29$ 21:33$.
*/
public class ColorTextView extends TextView {
    private Paint colorPaint;
    private float mViewWidth = 0;
    private LinearGradient linearGradient;
    private Matrix matrix;
    private float mTranslate;

    public ColorTextView(Context context) {
        super(context, null);
    }

    public ColorTextView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public ColorTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mViewWidth == 0) {
            mViewWidth = getMeasuredWidth();
            if (mViewWidth > 0) {
                colorPaint = getPaint();
                linearGradient = new LinearGradient(0, 0, mViewWidth, 0,
                        new int[]{Color.BLUE, 0xffffffff}, null, Shader.TileMode.CLAMP);
                colorPaint.setShader(linearGradient);
                matrix = new Matrix();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (matrix!=null){
            mTranslate+=mViewWidth/5;
            if (mTranslate>2*mViewWidth){
                mTranslate=-mViewWidth;
            }
            matrix.setTranslate(mTranslate,0);
            linearGradient.setLocalMatrix(matrix);
            postInvalidateDelayed(100);
        }
    }
}

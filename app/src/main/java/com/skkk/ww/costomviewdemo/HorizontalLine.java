package com.skkk.ww.costomviewdemo;

import android.graphics.PointF;

/**
 * Created by admin on 2017/4/18.
 */
/*
* 
* 描    述：贝塞尔球的水平线
* 作    者：ksheng
* 时    间：2017/4/18$ 21:31$.
*/
public class HorizontalLine {
    public PointF startP;
    public PointF middleP;
    public PointF endP;
    private float c=0.551915024494f;
    private float r;

    public HorizontalLine(PointF middleP,float r) {
        this.middleP = middleP;
        this.r=r;
        startP=new PointF(middleP.x-r*c,middleP.y);
        endP=new PointF(middleP.x+r*c,middleP.y);
    }

    public void setMiddleP(PointF middleP) {
        this.middleP = middleP;
        startP=new PointF(middleP.x-r*c,middleP.y);
        endP=new PointF(middleP.x+r*c,middleP.y);
    }
}

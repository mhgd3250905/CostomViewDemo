package com.skkk.ww.costomviewdemo;

import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by admin on 2017/4/6.
 */
/*
* 
* 描    述：下拉刷新中的头部布局
* 作    者：ksheng
* 时    间：2017/4/6$ 0:44$.
*/
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

    private AnimatorSet animSet;

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
        animSet=new AnimatorSet();
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

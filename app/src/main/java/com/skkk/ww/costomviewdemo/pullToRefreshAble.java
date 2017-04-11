package com.skkk.ww.costomviewdemo;

import android.content.Context;

/**
 * Created by admin on 2017/4/9.
 */

public interface pullToRefreshAble {
    void addHeaderView(Context context);//添加头布局
    void startRefreshing();//开始刷新
    void doInRefreshing();//刷新过程中做...
    boolean isRefreshing();//获取是否刷新
    void cancelRefresh();//取消刷新
}

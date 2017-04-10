package com.skkk.ww.costomviewdemo;

/**
 * Created by admin on 2017/4/9.
 */

public interface interfacePullToRefresh {
    void startRefreshing();//开始刷新
    void doInRefreshing();//刷新过程中做...
    boolean isRefreshing();//获取是否刷新
    void cancelRefresh();//取消刷新
}

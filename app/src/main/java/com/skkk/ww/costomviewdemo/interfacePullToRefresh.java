package com.skkk.ww.costomviewdemo;

/**
 * Created by admin on 2017/4/9.
 */

public interface interfacePullToRefresh {
    void startRefreshing();
    void doInRefreshing();
    boolean isRefreshing();
    void cancelRefresh();
}

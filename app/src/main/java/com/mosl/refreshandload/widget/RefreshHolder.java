package com.mosl.refreshandload.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.ButterKnife;

/**
 * @Description 上下拉视图基类
 * @Author MoseLin
 * @Date 2016/7/21.
 */
abstract class RefreshHolder extends LinearLayout
{
    protected View view;
    private int height;
    private int width;
    RefreshHolder(Context context){
        super(context);
        view = LayoutInflater.from(context).inflate(getLayout(),this);
        int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        view.measure(w,h);
        height = view.getMeasuredHeight();
        width = view.getMeasuredWidth();
        ButterKnife.bind(this,view);
    }

    /**
     *
     * @return mxl布局的ID
     */
    protected abstract int getLayout();

    /**
     * 下拉刷新开始或上拉加载开始时可重写这个方法做一些自定义的动画
     */
    abstract void start();
    /**
     * 下拉刷新开始或上拉加载完成时可重写这个方法做一些完成后的动作
     */
    abstract void complete();



    /**
     *
     * @return 视图的高度
     */
    int getViewHeight()
    {
        return height;
    }

    /**
     * 上下拉过程中重写些方法做一些自定义的动画之类的操作
     * @param top View滚动后的Y位置
     * @param dy 竖直方向的距离   正为向下滚动，负为向上滚动
     */
    public abstract void onViewPositionChanged(int top,int dy);

    /**
     *
     * @return 视图的大小
     */
    public int getViewWidth()
    {
        return width;
    }


}

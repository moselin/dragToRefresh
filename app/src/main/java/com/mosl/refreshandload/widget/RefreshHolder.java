package com.mosl.refreshandload.widget;

import android.content.Context;
import android.util.Log;
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
    private boolean isStart;
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
     void start(){
         isStart = true;
         beginRefresh();
     }
    /**
     * 下拉刷新开始或上拉加载完成时可重写这个方法做一些完成后的动作
     */
     void complete(){
         isStart = false;
         refreshComplete();
     }



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
     * @param firstIn 是否第一次进入自动刷新
     */
    public void onViewPositionChanged(int top, int dy, boolean firstIn){
        if (!firstIn && !isStart)
            if (Math.abs(top) > getHeight() / 2)
            {
                canRefresh();
            }
            else
            {
                normal();
            }
        viewChangedY(top,dy);
    }

    /**
     *
     * @return 视图的大小
     */
    public int getViewWidth()
    {
        return width;
    }

    /**
     * 正常状态，如提示下拉可刷新或加载
     */
    public abstract void normal();

    /**
     * 下拉或上拉到松开手指可刷新的状态或加载
     */
    public abstract void canRefresh();

    /**
     * 开始刷新或加载的状态
     */
    public abstract void beginRefresh();

    /**
     * 刷新或加载完成的状态
     */
    public abstract void refreshComplete();

    /**
     * 当视图移动时可以自己根据移动的top来做相应的动画
     * @param top y移动的距离
     * @param dy 不为0时表示正在移动中
     */
    public abstract void viewChangedY(int top,int dy);

}

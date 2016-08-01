package com.mosl.refreshandload.widget;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;

/**
 * @Description 自定义上拉加载 下拉刷新控件   基于ViewDragHelper来实现
 * @Author MoseLin
 * @Date 2016/7/21.
 */

public class RefreshLayout extends FrameLayout
{
    private ViewDragHelper helper;//拖动view工具类
    private boolean isCanRefresh = false;//是否可以下拉刷新
    private boolean isCanLoadMore = false;//是否可以上拉加载更多
    private RefreshHolder headerView;//下拉刷新时显示的View
    private RefreshHolder footerView;//上拉加载时显示的View
    private int mTop;//标记位移了的Y坐标
    private View refreshView;//可下拉或上拉的View
    private int initX;//refreshView初始X坐标
    private int initY;//refreshView初始Y坐标
    private int maxPullDown = 240;//默认下拉的高度
    private int maxPullUp = -240;//默认上拉的高度
    private RefreshListener listener;//上拉下拉的回调接口
    private boolean isRefreshing = false;//是否在刷新中
    private boolean isLoadMoring = false;//是否在加载中
    private boolean firstIn;//是否是刚进来就自动打开刷新

    public RefreshLayout(Context context)
    {
        super(context);
        init();
    }

    /**
     * 初始化ViewDragHelper
     */
    private void init()
    {
        helper = ViewDragHelper.create(this,new RefreshCallback());
    }
    /**
     * 初始化可上下拉的控件，并记录其坐标
     */
    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        refreshView = getChildAt(0);
        initX = refreshView.getLeft();
        initY = refreshView.getTop();
    }

    public RefreshLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        final int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP)
        {
            helper.cancel();
            return false;
        }
        if (!isCanRefresh())
            return super.onInterceptTouchEvent(ev);
        if (isRefreshing){
            helper.processTouchEvent(ev);
            return true;
        }

        return helper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        helper.processTouchEvent(event);
        return true;
    }

    /**
     * 是否允许下拉刷新
     * @return true 允许 ||　false 不允许
     */
    private boolean isCanRefresh()
    {
        return isCanRefresh;
    }

    /**
     * 是否允许上拉加载更多
     * @return true 允许 ||　false 不允许
     */
    private boolean isCanLoadMore()
    {
        return isCanLoadMore;
    }

    public void setListener(RefreshListener listener)
    {
        this.listener = listener;
    }

    /**
     * 设置自定义的下拉刷新头部的View视图
     * @param headerView 自定义刷新的view视图
     */
    public void addHeaderView(RefreshHolder headerView)
    {
        this.headerView = headerView;
        maxPullDown = headerView.getViewHeight();
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(headerView, params);
        refreshView.bringToFront();
        isCanRefresh = true;
    }

    /**
     * 设置自定义的上拉刷新头部的View视图
     * @param footerView 自定义加载的view视图
     */
    public void addFooterView(RefreshHolder footerView)
    {
        this.footerView = footerView;
        maxPullUp = -footerView.getViewHeight();
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        addView(footerView, params);
        refreshView.bringToFront();
        isCanLoadMore = true;
    }
    public void refresh(boolean firstIn){

        this.firstIn = firstIn;
        if (helper.smoothSlideViewTo(refreshView,0,maxPullDown))
        {
            ViewCompat.postInvalidateOnAnimation(this);
            if (listener != null)
            {
                isRefreshing = true;
                headerView.start();
                listener.onRefresh(refreshView);

            }
        }
    }

    public boolean isFirstIn()
    {
        return firstIn;
    }

    public void setFirstIn(boolean firstIn)
    {
        this.firstIn = firstIn;
    }

    private class RefreshCallback extends ViewDragHelper.Callback
    {
        @Override
        public boolean tryCaptureView(View child, int pointerId)
        {
            return refreshView == child;
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId)
        {
            super.onViewCaptured(capturedChild, activePointerId);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel)
        {
            if (releasedChild == refreshView)
                if (mTop > 0)
                {
                    if (mTop < maxPullDown / 2)
                    {
                        //下拉距离太小，自动回到顶部，不执行下拉刷新动作
                        onComplete(releasedChild);
                    } else
                    {
                        helper.settleCapturedViewAt(initX, maxPullDown);
                        if (listener != null)
                        {
                            isRefreshing = true;
                            headerView.start();
                            listener.onRefresh(releasedChild);

                        }
                    }
                } else if (mTop < 0)
                {
                    if (mTop > maxPullUp / 2)
                    {
                        //上拉距离太小，自动回到顶部，不执行上拉加载更多动作
                        onComplete(releasedChild);
                    } else
                    {
                        helper.settleCapturedViewAt(initX, maxPullUp);
                        invalidate();
                        if (listener != null)
                        {
                            isLoadMoring = true;
                            footerView.start();
                            listener.onLoadMore(releasedChild);
                        }
                    }
                }
            invalidate();
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, final int top, int dx, final int dy)
        {
            mTop = top;

            if (changedView == refreshView)
            {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
                if (mTop > 0 && mTop < maxPullDown)
                {
                    headerView.onViewPositionChanged(top, dy,isFirstIn());
                }
                if (mTop < 0 && mTop < maxPullDown)
                {
                    footerView.onViewPositionChanged(top, dy, isFirstIn());
                }
            }

        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy)
        {

            if (top > 0 && !canChildScrollUp() && isCanRefresh())
            {
                if (top > maxPullDown)
                    top = maxPullDown;
                return top;
            }
            if (top < 0 && !canChildScrollDown() && isCanLoadMore())
            {
                if (top < maxPullUp)
                    top = maxPullUp;
                return top;
            }
            return super.clampViewPositionVertical(child, top, dy);

        }

        @Override
        public int getViewVerticalDragRange(View child)
        {
            return maxPullDown;
        }

    }

    /**
     * 完成上拉或下拉操作
     * @param releasedChild 可下拉或上拉的视图
     */
    public void onComplete(View releasedChild)
    {
        if (releasedChild == refreshView)
        {
            ObjectAnimator animator = ObjectAnimator.ofFloat(refreshView, "translationY", mTop, initY);
            animator.setDuration(500).setInterpolator(new AccelerateInterpolator(1.2f));
            animator.start();
            if (isLoadMoring)
            {
                footerView.complete();
                isLoadMoring = false;
            }
            if (isRefreshing)
            {
                headerView.complete();
                isRefreshing = false;
            }



        }
    }

    @Override
    public void computeScroll()
    {
        if (helper.continueSettling(true))
        {
            ViewCompat.postInvalidateOnAnimation(this);
        }

    }

    /**
     * @return 子视图是否可以下拉
     */
    private boolean canChildScrollUp()
    {
        if (refreshView == null)
        {
            return false;
        }
        if (Build.VERSION.SDK_INT < 14)
        {
            if (refreshView instanceof AbsListView)
            {
                final AbsListView absListView = (AbsListView) refreshView;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else
            {
                return ViewCompat.canScrollVertically(refreshView, -1) || refreshView.getScrollY() > 0;
            }
        } else
        {
            return ViewCompat.canScrollVertically(refreshView, -1);
        }
    }

    /**
     * @return 子视图是否可以上划
     */
    private boolean canChildScrollDown()
    {
        if (refreshView == null)
        {
            return false;
        }
        if (Build.VERSION.SDK_INT < 14)
        {
            if (refreshView instanceof AbsListView)
            {
                final AbsListView absListView = (AbsListView) refreshView;
                if (absListView.getChildCount() > 0)
                {
                    int lastChildBottom = absListView.getChildAt(absListView.getChildCount() - 1)
                            .getBottom();
                    return absListView.getLastVisiblePosition() == absListView.getAdapter().getCount() - 1
                            && lastChildBottom <= absListView.getMeasuredHeight();
                } else
                {
                    return false;
                }

            } else
            {
                return ViewCompat.canScrollVertically(refreshView, 1) || refreshView.getScrollY() > 0;
            }
        } else
        {
            return ViewCompat.canScrollVertically(refreshView, 1);
        }
    }

    public interface RefreshListener
    {
        void onRefresh(View releasedChild);

        void onLoadMore(View releasedChild);

    }
}

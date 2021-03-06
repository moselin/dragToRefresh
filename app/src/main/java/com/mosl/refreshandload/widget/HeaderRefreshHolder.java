package com.mosl.refreshandload.widget;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mosl.refreshandload.R;
import com.nineoldandroids.view.ViewHelper;

import butterknife.Bind;


/**
 * @Description 下拉刷新视图
 * @Author MoseLin
 * @Date 2016/7/26.
 */

public class HeaderRefreshHolder extends RefreshHolder
{


    @Bind(R.id.refreshing_icon)
    ProgressBar refreshingIcon;
    @Bind(R.id.state_tv)
    TextView stateTv;
    @Bind(R.id.llRefresh)
    LinearLayout llRefresh;
    @Bind(R.id.head_view)
    RelativeLayout headView;


    public HeaderRefreshHolder(Context context)
    {
        super(context);
    }


    @Override
    protected int getLayout()
    {
        return R.layout.header_refresh;
    }

    @Override
    public void normal()
    {
        stateTv.setText("下拉刷新");
    }

    @Override
    public void canRefresh()
    {
        stateTv.setText("释放立即刷新");
    }

    @Override
    public void beginRefresh()
    {
        stateTv.setText("刷新中...");
    }

    @Override
    public void refreshComplete()
    {
        stateTv.setText("刷新成功");
        init();
    }

    @Override
    public void viewChangedY(int top,int dy)
    {
        int t = top / 4;
        if (dy != 0)
        {
            ViewHelper.setTranslationY(llRefresh, t);
        }
    }

    private void init()
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(llRefresh, "translationY", llRefresh.getY(), 0);
        animator.setDuration(500).setInterpolator(new AccelerateInterpolator(1.2f));
        animator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                stateTv.setText("下拉刷新");
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });
        animator.start();
    }
}

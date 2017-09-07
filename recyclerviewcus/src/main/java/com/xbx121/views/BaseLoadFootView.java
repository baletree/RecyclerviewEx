package com.xbx121.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by peng on 2017/9/5.
 */

public abstract class BaseLoadFootView extends RelativeLayout {
    public static final int STATE_NO_LOAD = -1, STATE_LOADING = 1;
    private int stateFlag = STATE_NO_LOAD;
    private RefreshLoadListener onRefreshListener;

    public BaseLoadFootView(Context context) {
        super(context);
    }

    public BaseLoadFootView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseLoadFootView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
    }

    public void setOnLoadMoreListener(RefreshLoadListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    /**
     * @param height 头部的高度设置
     */
    public void setFootViewHeight(int height) {
        RecyclerView.LayoutParams rvLp = (RecyclerView.LayoutParams) getLayoutParams();
        if (height > 0) {
            rvLp.height = height;
            setLayoutParams(rvLp);
        } else {
            rvLp.height = 0;
            stateFlag = STATE_NO_LOAD;
            setLayoutParams(rvLp);
            reset();
        }
    }

    /**
     * @param height 上拉时的状态
     */
    public void pullUpFoot(int height) {
        if (height < 0)
            return;
        if (stateFlag == STATE_LOADING) {
            setFootViewHeight(loadHeight() + height);
            move(loadHeight() + height);
        } else {
            setFootViewHeight(height);
            move(height);
        }
        if (stateFlag != STATE_LOADING) {
            if (height > loadHeight())
                loadMore(true);
            else
                loadMore(false);
        }
    }

    /**
     * 松手时的状态
     */
    public void loosenFootView() {
        if (getHeight() >= loadHeight()) {
            animMove(100, getHeight(), loadHeight());
            if (stateFlag != STATE_LOADING) {
                stateFlag = STATE_LOADING;
                if (onRefreshListener == null)
                    return;
                onRefreshListener.onLoadMoreListener();
                loosenAndLoad();
            }
        } else {
            animMove(50, getHeight(), 0);
        }
    }


    /**
     * @param duratuon 头部松手时的动画
     * @param values
     */
    private void animMove(long duratuon, int... values) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(values);
        valueAnimator.setDuration(duratuon);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int dy = (int) animation.getAnimatedValue();
                setFootViewHeight(dy);
            }
        });
        valueAnimator.start();
    }

    public void onCompleteLoad() {
        stateFlag = STATE_NO_LOAD;
        animMove(200, getHeight(), 0);
    }

    public int getStateFlag() {
        return stateFlag;
    }

    /**
     * 拉伸过程
     */
    protected abstract void move(int height);

    /**
     * 可刷新的高度
     */
    protected abstract int loadHeight();

    /**
     * 是否已经达到可刷新状态
     */
    protected abstract void loadMore(boolean canRefresh);

    /**
     * 松开并加载
     */
    protected abstract void loosenAndLoad();

    /**
     * 复位
     */
    protected abstract void reset();
}

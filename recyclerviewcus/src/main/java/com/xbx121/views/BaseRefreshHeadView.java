package com.xbx121.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by Eric on 2017/9/4.
 */

public abstract class BaseRefreshHeadView extends RelativeLayout {
    public static final int STATE_NO_REFRESH = -1, STATE_REFRESHING = 1;
    private int stateFlag = STATE_NO_REFRESH;
    private RefreshLoadListener onRefreshListener;

    public BaseRefreshHeadView(Context context) {
        this(context, null);
    }

    public BaseRefreshHeadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseRefreshHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
    }

    public void setOnRefreshListener(RefreshLoadListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    /**
     * @param height 头部的高度设置
     */
    public void setHeadViewHeight(int height) {
        RecyclerView.LayoutParams rvLp = (RecyclerView.LayoutParams) getLayoutParams();
        if (height > 0) {
            rvLp.height = height;
            setLayoutParams(rvLp);
        } else {
            rvLp.height = 0;
            stateFlag = STATE_NO_REFRESH;
            setLayoutParams(rvLp);
            reset();
        }
    }

    /**
     * @param height 下拉时的状态
     */
    public void pullDownHead(int height) {
        if (height < 0)
            return;
        if (stateFlag == STATE_REFRESHING) {
            setHeadViewHeight(refreshHeight() + height);
            move(refreshHeight() + height);
        } else {
            setHeadViewHeight(height);
            move(height);
        }
        if (stateFlag != STATE_REFRESHING) {
            if (height > refreshHeight())
                refresh(true);
            else
                refresh(false);
        }
    }

    /**
     * 松手时的状态
     */
    public void loosenHeadView() {
        if (getHeight() >= refreshHeight()) {
            animMove(100, getHeight(), refreshHeight());
            if (stateFlag != STATE_REFRESHING) {
                stateFlag = STATE_REFRESHING;
                if (onRefreshListener == null)
                    return;
                onRefreshListener.onRefreshListener();
                loosenAndRefresh();
            }
        } else {
            animMove(50, getHeight(), 0);
        }
    }

    /**
     * 完成刷新操作
     */
    public void onCompleteRefresh() {
        stateFlag = STATE_NO_REFRESH;
        animMove(200, getHeight(), 0);
    }

    public int getStateFlag() {
        return stateFlag;
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
                setHeadViewHeight(dy);
            }
        });
        valueAnimator.start();
    }

    /**
     * 拉伸过程
     */
    protected abstract void move(int height);

    /**
     * 可刷新的高度
     */
    protected abstract int refreshHeight();

    /**
     * 是否已经达到可刷新状态
     */
    protected abstract void refresh(boolean canRefresh);

    /**
     * 松开并刷新
     */
    protected abstract void loosenAndRefresh();

    /**
     * 复位
     */
    protected abstract void reset();
}

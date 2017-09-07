package com.xbx121.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Eric on 2017/9/4.
 */

public class RefreshRecyclerView extends RecyclerView {
    private Context context;
    private RefreshLoadListener rLoadListener;
    private RefreshAdapter refreshAdapter = null;
    private float rDownY = -1, lDownY = -1;
    private View refreshView, loadMoreView;
    private boolean isSupportRefresh = false, isSupportLoad = false;

    public RefreshRecyclerView(Context context) {
        this(context, null);
    }

    public RefreshRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public void setOnRefreshLoadListener(RefreshLoadListener rLoadListener) {
        this.rLoadListener = rLoadListener;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void setSupportRefresh(boolean isSupportRefresh) {
        this.isSupportRefresh = isSupportRefresh;
    }

    public void setSupportLoad(boolean isSupportLoad) {
        this.isSupportLoad = isSupportLoad;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter == null)
            super.setAdapter(adapter);
        else {
            adapter.registerAdapterDataObserver(dataObserver);
            refreshAdapter = new RefreshAdapter(adapter);
            BaseRefreshHeadView headView = new DefaultRefreshHead(context);
            headView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSixD));
            refreshAdapter.addHeadView(headView);
            refreshAdapter.addFootView(new DefaultLoadFoot(context));
            super.setAdapter(refreshAdapter);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (rDownY == -1)
            rDownY = e.getRawY();
        if (lDownY == -1)
            lDownY = e.getRawY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float rdy = e.getRawY() - rDownY;
                float ldy = lDownY - e.getRawY();
                if (refreshView == null) {
                    refreshView = getLayoutManager().findViewByPosition(0);
                    if (refreshView instanceof BaseRefreshHeadView) {
                        ((BaseRefreshHeadView) refreshView).setOnRefreshListener(rLoadListener);
                    }
                }
                if (loadMoreView == null) {
                    loadMoreView = getLayoutManager().findViewByPosition(getLayoutManager().getItemCount() - 1);
                    if (loadMoreView instanceof BaseLoadFootView) {
                        ((BaseLoadFootView) loadMoreView).setOnLoadMoreListener(rLoadListener);
                    }
                }
                if (allowPulldown() && !isLoading() && isSupportRefresh) {
                    ((BaseRefreshHeadView) refreshView).pullDownHead((int) (rdy / 2));
                    if (refreshView.getHeight() >= 0 && rdy > 0) {
                        return false;
                    }
                } else {
                    rDownY = e.getRawY();
                }
                if (allowPullup() && !isRefreshing() && isSupportLoad) {
                    ((BaseLoadFootView) loadMoreView).pullUpFoot((int) (ldy / 2));
                    offsetChildrenVertical(1);
                    if (loadMoreView.getHeight() > 0 && ldy > 0) {
                        scrollToPosition(getLayoutManager().getItemCount() - 1);
                        return false;
                    }
                } else {
                    lDownY = e.getRawY();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (refreshView instanceof BaseRefreshHeadView && refreshView.getParent() != null && isSupportRefresh) {
                    ((BaseRefreshHeadView) refreshView).loosenHeadView();
                } else if (loadMoreView instanceof BaseLoadFootView && loadMoreView.getParent() != null && isSupportLoad) {
                    ((BaseLoadFootView) loadMoreView).loosenFootView();
                }
                rDownY = -1;
                lDownY = -1;
                break;
        }
        return super.onTouchEvent(e);
    }

    /**
     * 是否正在刷新
     */
    private boolean isRefreshing() {
        if (refreshView instanceof BaseRefreshHeadView)
            return ((BaseRefreshHeadView) refreshView).getStateFlag() == BaseRefreshHeadView.STATE_REFRESHING;
        return false;
    }

    /**
     * 是否正在加载
     */
    private boolean isLoading() {
        if (loadMoreView instanceof BaseLoadFootView)
            return ((BaseLoadFootView) loadMoreView).getStateFlag() == BaseLoadFootView.STATE_LOADING;
        return false;
    }

    /**
     * 允许下拉操作
     */
    private boolean allowPulldown() {
        return refreshView instanceof BaseRefreshHeadView && refreshView.getParent() != null
                && refreshView.getTop() == 0;
    }

    /**
     * 允许上拉操作
     */
    private boolean allowPullup() {
        return loadMoreView instanceof BaseLoadFootView && loadMoreView.getParent() != null
                && loadMoreView.getBottom() == getLayoutManager().getHeight();
    }

    /**
     * 下拉刷新完成
     */
    public void onRefreshComplete() {
        if (refreshView instanceof BaseRefreshHeadView) {
            if (refreshView.getParent() != null) {
                scrollToPosition(0);
                ((BaseRefreshHeadView) refreshView).onCompleteRefresh();
            } else {
                ((BaseRefreshHeadView) refreshView).setHeadViewHeight(0);
            }
            rDownY = -1;
        }
    }

    /**
     * 上拉加载完成
     */
    public void onLoadMoreComplete() {
        if (loadMoreView instanceof BaseLoadFootView) {
            if (loadMoreView.getParent() != null) {
                scrollToPosition(getLayoutManager().getItemCount() - 1);
                ((BaseLoadFootView) loadMoreView).onCompleteLoad();
            } else {
                ((BaseLoadFootView) loadMoreView).setFootViewHeight(0);
            }
            lDownY = -1;
        }
    }

    private final RecyclerView.AdapterDataObserver dataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            if (getChildCount() > 1) {
                removeViews(1, getChildCount() - 1);
            }
            refreshAdapter.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            refreshAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            refreshAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            refreshAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            refreshAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            refreshAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    };
}

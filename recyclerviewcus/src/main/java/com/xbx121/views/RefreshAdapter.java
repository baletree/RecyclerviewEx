package com.xbx121.views;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric on 2017/9/4.
 */

public class RefreshAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //用于区分不同的View的标识
    private static final int TYPE_FLAG_STRAT = 10000;
    //数据的适配器
    private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;
    //头部和脚部
    private List<View> headViews = null, footViews = null;

    public RefreshAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        this.adapter = adapter;
        headViews = new ArrayList<>();
        footViews = new ArrayList<>();
    }

    /**
     * @param view 添加头部
     */
    public void addHeadView(View view) {
        if (headViews == null)
            return;
        if (view instanceof BaseRefreshHeadView) {
            view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
            addRefreshHeadView((BaseRefreshHeadView) view);
        } else {
            view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            headViews.add(view);
        }
    }

    /**
     * @param basehHeadView 设置刷新的头部
     */
    public void addRefreshHeadView(BaseRefreshHeadView basehHeadView) {
        if (headViews != null && basehHeadView != null) {
            if (headViews.size() > 0 && headViews.get(0) instanceof BaseRefreshHeadView) {
                headViews.remove(0);
                headViews.add(0, basehHeadView);
            } else {
                headViews.add(0, basehHeadView);
            }
        }
    }

    /**
     * @param view 添加尾部
     */
    public void addFootView(View view) {
        if (footViews == null)
            return;
        if (view instanceof BaseLoadFootView) {
            view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
            addLoadFootView((BaseLoadFootView) view);
        } else {
            view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            if (footViews.size() > 0 && footViews.get(footViews.size() - 1) instanceof BaseLoadFootView) {
                footViews.add(footViews.size() - 1, view);
            } else {
                footViews.add(view);
            }
        }
    }

    /**
     * @param view 设置加载的尾部
     */
    public void addLoadFootView(BaseLoadFootView view) {
        if (footViews == null)
            return;
        if (footViews.size() > 0 && footViews.get(footViews.size() - 1) instanceof BaseLoadFootView) {
            footViews.remove(footViews.size() - 1);
            footViews.add(view);
        } else {
            footViews.add(view);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if ((viewType - TYPE_FLAG_STRAT) >= 0 && (viewType - TYPE_FLAG_STRAT) < headViews.size())
            return new HeadViewHolder(headViews.get(viewType - TYPE_FLAG_STRAT));
        if ((viewType - TYPE_FLAG_STRAT) >= (getItemCount() - footViews.size()) && (viewType - viewType - TYPE_FLAG_STRAT) < getItemCount())
            return new FootViewHolder(footViews.get(viewType - TYPE_FLAG_STRAT - adapter.getItemCount() - headViews.size()));
        return adapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeadViewHolder || holder instanceof FootViewHolder)
            return;
        adapter.onBindViewHolder(holder, position - (headViews.size()));
    }

    @Override
    public int getItemCount() {
        return adapter.getItemCount() + headViews.size() + footViews.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (headViews.size() > 0 && position < headViews.size())
            return TYPE_FLAG_STRAT + position;
        if (footViews.size() > 0 && position >= getItemCount() - footViews.size())
            return TYPE_FLAG_STRAT + position;
        return adapter.getItemViewType(position);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        adapter.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        if (position >= 0 && position < headViews.size() || (position < getItemCount() && position >= adapter.getItemCount() - footViews.size())) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp == null)
                return;
            if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            ((GridLayoutManager) recyclerView.getLayoutManager()).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

                @Override
                public int getSpanSize(int position) {
                    if (position >= 0 && position < headViews.size()) {
                        return ((GridLayoutManager) recyclerView.getLayoutManager()).getSpanCount();
                    } else if (position >= getItemCount() - footViews.size() && position < getItemCount()) {
                        return ((GridLayoutManager) recyclerView.getLayoutManager()).getSpanCount();
                    } else {
                        return 1;
                    }
                }
            });
        }
    }

    class HeadViewHolder extends RecyclerView.ViewHolder {

        public HeadViewHolder(View itemView) {
            super(itemView);
        }
    }

    class FootViewHolder extends RecyclerView.ViewHolder {

        public FootViewHolder(View itemView) {
            super(itemView);
        }
    }
}

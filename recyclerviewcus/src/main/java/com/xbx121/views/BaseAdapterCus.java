package com.xbx121.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Eric on 2017/9/6.
 */

public abstract class BaseAdapterCus<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public Context context;
    public LayoutInflater mInflater;
    //数据源
    public List<T> dataList;

    public BaseAdapterCus(Context context, List<T> dataList) {
        this.context = context;
        this.dataList = dataList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(getItemLayoutId(), parent, false);
        return new BaseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindViewData((BaseViewHolder) holder, position);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    /**
     * 封装ViewHolder ,子类可以直接使用
     */
    public class BaseViewHolder extends RecyclerView.ViewHolder {
        private Map<Integer, View> mViewMap;

        public BaseViewHolder(View itemView) {
            super(itemView);
            mViewMap = new HashMap<>();
        }

        public View getView(int id) {
            View view = mViewMap.get(id);
            if (view == null) {
                view = itemView.findViewById(id);
                mViewMap.put(id, view);
            }
            return view;
        }
    }

    public abstract int getItemLayoutId();

    public abstract void bindViewData(BaseViewHolder holder, int position);
}

package com.xbx121.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Eric on 2017/9/5.
 */

public class DefaultLoadFoot extends BaseLoadFootView {
    private Context context;
    private ProgressBar loadMorePb;
    private TextView loadMoreTv;

    public DefaultLoadFoot(Context context) {
        this(context, null);
    }

    public DefaultLoadFoot(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultLoadFoot(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.default_refresh_header, this);
        loadMorePb = (ProgressBar) findViewById(R.id.defaultRefreshPb);
        loadMoreTv = (TextView) findViewById(R.id.defaultRefreshTv);
        loadMorePb.setVisibility(GONE);
    }

    @Override
    protected void move(int height) {

    }

    @Override
    protected int loadHeight() {
        return ScreenUtil.dip2px(context, 50);
    }

    @Override
    protected void loadMore(boolean canRefresh) {
        if (canRefresh) {
            loadMoreTv.setText("松开加载");
        } else {
            loadMoreTv.setText("继续上拉");
        }
    }

    @Override
    protected void loosenAndLoad() {
        loadMorePb.setVisibility(VISIBLE);
        loadMoreTv.setText("正在加载中...");
    }

    @Override
    protected void reset() {
        loadMorePb.setVisibility(GONE);
        loadMoreTv.setText("");
    }
}

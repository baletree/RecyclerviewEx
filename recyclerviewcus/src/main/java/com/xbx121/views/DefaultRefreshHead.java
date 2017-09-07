package com.xbx121.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Eric on 2017/9/5.
 * 默认的头部刷新
 */

public class DefaultRefreshHead extends BaseRefreshHeadView {
    private Context context;
    private ProgressBar refreshPb;
    private TextView refreshTv;

    public DefaultRefreshHead(Context context) {
        this(context, null);
    }

    public DefaultRefreshHead(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultRefreshHead(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.default_refresh_header, this);
        refreshPb = (ProgressBar) findViewById(R.id.defaultRefreshPb);
        refreshTv = (TextView) findViewById(R.id.defaultRefreshTv);
        refreshPb.setVisibility(GONE);
    }

    @Override
    protected void move(int height) {

    }

    @Override
    protected int refreshHeight() {
        return ScreenUtil.dip2px(context, 60);
    }

    @Override
    protected void refresh(boolean canRefresh) {
        if (canRefresh)
            refreshTv.setText("松开即可刷新~");
        else
            refreshTv.setText("继续下拉");
    }

    @Override
    protected void loosenAndRefresh() {
        refreshPb.setVisibility(VISIBLE);
        refreshTv.setText("正在刷新~");
    }

    @Override
    protected void reset() {
        refreshTv.setText("");
        refreshPb.setVisibility(GONE);
    }
}

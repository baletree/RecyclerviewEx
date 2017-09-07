package com.xbx121.views;

import android.content.Context;

/**
 * Created by peng on 2017/9/5.
 */

public class ScreenUtil {
    /**
     * @param pxValue （DisplayMetrics类中属性density）将px值转换为dip或dp值，保证尺寸大小不变
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * @param dipValue scale  （DisplayMetrics类中属性density） 将dip或dp值转换为px值，保证尺寸大小不变
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}

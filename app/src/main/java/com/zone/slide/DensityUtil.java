package com.zone.slide;

import android.view.View;

import com.zone.slide.MyApplication;

/**
 * DIP PX 互相转换工具
 */
public class DensityUtil {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {
        if (dpValue <= 0) return 0;
        final float scale = MyApplication.appContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue) {
        if (pxValue <= 0) return 0;
        final float scale = MyApplication.appContext.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static float sp2px(float spValue) {
        if (spValue <= 0) return 0;
        final float scale = MyApplication.appContext.getResources().getDisplayMetrics().scaledDensity;
        return spValue * scale;
    }

    /**
     * 测量View的宽高
     *
     * @param view View
     */
    public static void measureWidthAndHeight(View view) {
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
    }
}

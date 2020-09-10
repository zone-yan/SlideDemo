package com.zone.slide;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;


/**
 * Describe: 防止多点触控时造成 pointerIndex out of range异常崩溃
 * 禁止手势滑动的ViewPager 通过noScroll控制
 */
public class MyScrollViewPager extends ViewPager {
    private boolean noScroll = true;

    public MyScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollViewPager(Context context) {
        super(context);
    }

    /**
     * 控制ViewPager是否可以手势滑动
     *
     * @param noScroll true-不可以，false-可以  默认为 true
     */
    public void setNoScroll(boolean noScroll) {
        this.noScroll = noScroll;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {

        if (noScroll) {

            return false;
        } else {
            try {
                //这里需要try catch 为了防止多点触控时造成 pointerIndex out of range异常崩溃
                return super.onTouchEvent(arg0);

            } catch (Exception e) {
                Log.e("NoScrollViewPager", e.getMessage());
                return false;
            }

        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (noScroll) {
            return false;

        } else {
            try {
                //这里需要try catch 为了防止多点触控时造成 pointerIndex out of range异常崩溃
                return super.onInterceptTouchEvent(arg0);

            } catch (Exception e) {
                Log.e("NoScrollViewPager", e.getMessage());
                return false;
            }

        }
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }
}

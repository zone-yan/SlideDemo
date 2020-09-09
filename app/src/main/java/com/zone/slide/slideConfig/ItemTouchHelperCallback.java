package com.zone.slide.slideConfig;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.zone.slide.MyAdapter;
import com.zone.slide.MyApplication;

import static androidx.recyclerview.widget.ItemTouchHelper.ANIMATION_TYPE_DRAG;


public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private static final String TAG = ItemTouchHelperCallback.class.getSimpleName();
    private OnSlideListener mListener;
    public boolean isEnableSlided = true;//是否可以滑动


    public ItemTouchHelperCallback() {
    }


    public void setOnSlideListener(OnSlideListener mListener) {
        this.mListener = mListener;
    }

    /**
     * 这个方法用于让RecyclerView拦截向上滑动，向下滑动，保留左右滑动
     * makeMovementFlags(dragFlags, swipeFlags);dragFlags是上下方向的滑动 swipeFlags是左右方向的滑动
     *
     * @param recyclerView
     * @param viewHolder
     * @return
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = 0;
        int slideFlags = 0;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof SlideLayoutManager) {
            slideFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        }
        return makeMovementFlags(dragFlags, slideFlags);
    }

    /**
     * drag状态下，在canDropOver()返回true时，会调用该方法让我们拖动换位置的逻辑(需要自己处理变换位置的逻辑)
     *
     * @param recyclerView
     * @param viewHolder
     * @param target
     * @return
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    /**
     * 针对swipe状态，swipe 到达滑动消失的距离回调函数,一般在这个函数里面处理删除item的逻辑
     * 确切的来讲是swipe item滑出屏幕动画结束的时候调用
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        Log.d(TAG, "onSwiped");
        viewHolder.itemView.setOnTouchListener(null);
        //可以在这里直接删除item，但需要把adapter传进来
//        int layoutPosition = viewHolder.getLayoutPosition();
//        String remove =  adapter.getData().remove(layoutPosition);
//        adapter.notifyDataSetChanged();
        //可以在回调中删除item，也可以处理一些业务逻辑
        if (mListener != null) {
            mListener.onSlided(viewHolder, direction == ItemTouchHelper.LEFT ? ItemConfig.SLIDED_LEFT : ItemConfig.SLIDED_RIGHT, false);
        }
    }

    /**
     * 开始滑动功能，默认为true
     * 针对swipe状态，是否允许swipe(滑动)操作
     * 如果设置为false，手动开启，调用startSwipe()
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    /**
     * 针对swipe和drag状态，整个过程中一直会调用这个函数,随手指移动的view就是在super里面做到的(和ItemDecoration里面的onDraw()函数对应)
     *
     * @param c
     * @param recyclerView
     * @param viewHolder
     * @param dX                item 移动的距离
     * @param dY
     * @param actionState
     * @param isCurrentlyActive
     */
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            int offset = 3;
            float ratio = dX / getThreshold(recyclerView, viewHolder);
            Log.d(TAG, " onChildDraw ratio: " + ratio);
            float ratioWithOffset = ratio / offset;
            float ratioOver = 1f;//临界点  滑动到临界点之后的过程，小于1表示过了临界点了（松手就会自动滑出屏幕，即喜欢/不喜欢标识移动到屏幕中间）
            if (ratioWithOffset > 1) {
                ratioWithOffset = 1;
            } else if (ratioWithOffset < -1) {
                ratioWithOffset = -1;
            }

            if (Math.abs(ratio) >= 1) {
                ratioOver = (3f - Math.abs(ratio)) / 2;
                if (ratioOver > 1) {
                    ratioOver = 1;
                } else if (ratioOver < 0) {
                    ratioOver = 0;
                }

            }

            if (ratio > 1) {
                ratio = 1;
            } else if (ratio < -1) {
                ratio = -1;
            }

            itemView.setRotation(ratioWithOffset * ItemConfig.DEFAULT_ROTATE_DEGREE);
            int childCount = recyclerView.getChildCount();
            double angle = Math.toRadians(ItemConfig.DEFAULT_ROTATE_DEGREE);
            int itemWidth = itemView.getMeasuredWidth();
            //计算旋转后的卡片需要移动的距离与item宽度的比例
            double distanceRatio = ((itemWidth * Math.cos(angle) + itemView.getMeasuredHeight() * Math.sin(angle) - itemWidth) / 2 + itemWidth) / itemWidth;

            itemView.setTranslationX((float) (dX * distanceRatio));

            for (int position = 0; position < childCount - 1; position++) {
                int index = childCount - position - 1;
                View view = recyclerView.getChildAt(position);
                float scaleX = 1 - index * ItemConfig.DEFAULT_SCALE + Math.abs(ratioWithOffset) * ItemConfig.DEFAULT_SCALE;
                if (scaleX < 0) {
                    scaleX = 0;
                }
                Log.d(TAG, "onChildDraw scaleX==" + scaleX);
                view.setScaleX(scaleX);
                view.setScaleY(scaleX);
            }

            if (mListener != null) {

                if (ratio != 0) {
                    mListener.onSliding(viewHolder, ratio, ratioOver, ratio < 0 ? ItemConfig.SLIDING_LEFT : ItemConfig.SLIDING_RIGHT);
                } else {
                    isEnableSlided = true;
                    mListener.onSliding(viewHolder, ratio, ratioOver, ItemConfig.SLIDING_NONE);
                }
            }
        }
    }

    /**
     * 针对swipe和drag状态，当一个item view在swipe、drag状态结束的时候调用
     * drag状态：当手指释放的时候会调用
     * swipe状态：当item从RecyclerView中删除的时候调用，一般我们会在onSwiped()函数里面删除掉指定的item view
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setRotation(0f);
        isEnableSlided = true;
        Log.d(TAG, "clearView");
    }

    /**
     * 针对swipe和drag状态，当手指离开之后，view回到指定位置动画的持续时间(swipe可能是回到原位，也有可能是swipe掉)
     */
    @Override
    public long getAnimationDuration(RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
        Log.d(TAG, "animationType==" + animationType + "==animateDx==" + animateDx + "==animateDy==" + animateDy);
        isEnableSlided = false;

        return animationType == ANIMATION_TYPE_DRAG ? 350
                : DEFAULT_DRAG_ANIMATION_DURATION;
//                super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);//
    }

    /**
     * 长按选中Item的时候开始调用
     *
     * @param viewHolder
     * @param actionState
     */
    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        Log.d(TAG, "onSelectedChanged");
    }

    /**
     * 针对drag状态，当itemView滑动到RecyclerView边界的时候(比如下面边界的时候),RecyclerView会scroll，
     * 同时会调用该函数去获取scroller距离(不用我们处理 直接super)
     */
    public int interpolateOutOfBoundsScroll(RecyclerView recyclerView,
                                            int viewSize,
                                            int viewSizeOutOfBounds,
                                            int totalSize,
                                            long msSinceStartScroll) {
        return super.interpolateOutOfBoundsScroll(recyclerView, viewSize, viewSizeOutOfBounds, totalSize, msSinceStartScroll);
    }

    private float getThreshold(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return recyclerView.getWidth() * getSwipeThreshold(viewHolder);
    }

    /**
     * 针对swipe状态，swipe滑动的位置超过了百分之多少就消失
     */
    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        return .3f;
    }

    /**
     * 针对swipe状态，swipe的逃逸速度，换句话说就算没达到getSwipeThreshold设置的距离，达到了这个逃逸速度item也会被swipe消失掉
     */
    public float getSwipeEscapeVelocity(float defaultValue) {
        return (float) (defaultValue / 0.1);
//        return defaultValue / 2;
    }

    /**
     * 针对swipe状态，swipe滑动的阻尼系数,设置最大滑动速度
     */
    public float getSwipeVelocityThreshold(float defaultValue) {
        return defaultValue / 2;
    }

    /**
     * 触发完整的左右滑动动画效果（比如点击喜欢按钮）
     *
     * @param recyclerView
     * @param isLeft
     * @param duration
     */
    public void swipeTopViewToLeftOrRight(final RecyclerView recyclerView, final boolean isLeft, final long duration) {
        if (!isEnableSlided) {
            return;
        }

        if (recyclerView == null) {
            return;
        }

//        int itemRealCount = adapter.getItemCount();
        int childCount = recyclerView.getChildCount();
//        Log.d(TAG,"itemRealCount=="+itemRealCount);
        Log.d(TAG, "childCount==" + childCount);
//        if (itemRealCount == 0) {
//            return;
//        }
        if (childCount == 0) {
            return;
        }

        final RecyclerView.ViewHolder viewHolderCurrent = recyclerView.findViewHolderForAdapterPosition(0);
        if (viewHolderCurrent == null) {
            return;
        }

//        int layoutPosition = viewHolderCurrent.getLayoutPosition();
        for (int position = 0; position < childCount - 1; position++) {
            int index = childCount - position - 1;
            View view = recyclerView.getChildAt(position);
            view.animate()
                    .scaleX(1 - index * ItemConfig.DEFAULT_SCALE + 1 * ItemConfig.DEFAULT_SCALE)
                    .scaleY(1 - index * ItemConfig.DEFAULT_SCALE + 1 * ItemConfig.DEFAULT_SCALE)
                    .setDuration(duration);
        }

        final View mCurrentView = viewHolderCurrent.itemView;
        double angle = Math.toRadians(ItemConfig.DEFAULT_ROTATE_DEGREE);
        //计算旋转后的卡片需要移动的距离
        int itemWidth = mCurrentView.getMeasuredWidth();
        int distanceX = (int) ((itemWidth * Math.cos(angle) + mCurrentView.getMeasuredHeight() * Math.sin(angle) - itemWidth) / 2 + itemWidth);

        mCurrentView.animate().cancel();
        mCurrentView.animate()
                .x(isLeft ? -distanceX : distanceX)
                .rotation(isLeft ? -8f : 8f)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mCurrentView.setOnTouchListener(null);
                        recyclerView.removeView(mCurrentView);


                        if (mListener != null) {
                            mListener.onSlided(viewHolderCurrent, isLeft ? ItemConfig.SLIDED_LEFT : ItemConfig.SLIDED_RIGHT, true);
                        }
                        isEnableSlided = true;
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        isEnableSlided = false;
                        if (mListener != null) {
                            mListener.onAnimationStart(duration, isLeft);
                        }
                    }
                });
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(float dpValue) {
        if (dpValue <= 0) return 0;
        final float scale = MyApplication.appContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}

package com.zone.slide.slideConfig;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;


import com.zone.slide.MyAdapter;

import java.util.List;

public class SlideLayoutManager extends RecyclerView.LayoutManager {

    private static final String TAG = SlideLayoutManager.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private ItemTouchHelper mItemTouchHelper;
    private ItemTouchHelperCallback mCallback;
    private Context mContext;

    public SlideLayoutManager(@NonNull Context context, @NonNull RecyclerView recyclerView, @NonNull ItemTouchHelper itemTouchHelper, @NonNull ItemTouchHelperCallback callback) {
        this.mContext = context;
        this.mRecyclerView = checkIsNull(recyclerView);
        this.mItemTouchHelper = checkIsNull(itemTouchHelper);
        this.mCallback = checkIsNull(callback);
    }

    private <T> T checkIsNull(T t) {
        if (t == null) {
            throw new NullPointerException();
        }
        return t;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(final RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        int itemCount = getItemCount();
        Log.d(TAG, "onLayoutChildren   " + itemCount);
        if (itemCount > 0) {
            for (int position = Math.min((itemCount - 1), 3); position >= 0; position--) {
                final View view = recycler.getViewForPosition(position);

                float scaleX = 1 - position * ItemConfig.DEFAULT_SCALE;
                if (scaleX < 0) {
                    scaleX = 0;
                }
                view.setScaleX(scaleX);
                view.setScaleY(scaleX);

                addView(view);
                measureChildWithMargins(view, 0, 0);
                layoutDecoratedWithMargins(view, 0, 0,
                        getDecoratedMeasuredWidth(view), getDecoratedMeasuredHeight(view));

                if (position == 0) {
                    view.setOnTouchListener(mOnTouchListener);
                }
            }
        }

    }

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            RecyclerView.ViewHolder childViewHolder = mRecyclerView.getChildViewHolder(v);
            if (event.getAction() == MotionEvent.ACTION_DOWN && mCallback.isEnableSlided) {//上一个滑动动画执行完成才能执行新的滑动
                Log.d(TAG, "MotionEvent.ACTION_DOWN");
                MyAdapter adapter = (MyAdapter) mRecyclerView.getAdapter();
                List list = adapter.getData();
                /***这里负责滑动拦截**/
                if (list == null || list.isEmpty()) {
                    return true;
                }
                mItemTouchHelper.startSwipe(childViewHolder);
            }
            return false;
        }
    };

}

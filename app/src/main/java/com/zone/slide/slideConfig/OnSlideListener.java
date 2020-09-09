package com.zone.slide.slideConfig;


import androidx.recyclerview.widget.RecyclerView;

public interface OnSlideListener {

    void onSliding(RecyclerView.ViewHolder viewHolder, float ratio, float ratioOver, int direction);

    void onSlided(RecyclerView.ViewHolder viewHolder, int direction, boolean isButtonClick);


    void onAnimationStart(long DURATION, boolean isLeft);

}

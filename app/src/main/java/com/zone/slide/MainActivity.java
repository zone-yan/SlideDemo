package com.zone.slide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.zone.slide.slideConfig.ItemConfig;
import com.zone.slide.slideConfig.ItemTouchHelperCallback;
import com.zone.slide.slideConfig.OnSlideListener;
import com.zone.slide.slideConfig.SlideLayoutManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnSlideListener {
    RecyclerView rv_card;
    ImageView iv_dislike;
    ImageView iv_like;
    ImageView iv_dislike_btn;
    ImageView iv_like_btn;

    private int screenWidth;
    private ArrayList<String> mCardList = new ArrayList<>();
    private ItemTouchHelperCallback mItemTouchHelperCallback;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv_card = findViewById(R.id.rv_card);
        iv_dislike = findViewById(R.id.iv_dislike);
        iv_like = findViewById(R.id.iv_like);
        iv_dislike_btn = findViewById(R.id.iv_dislike_btn);
        iv_like_btn = findViewById(R.id.iv_like_btn);

        screenWidth = getScreenPixWidth();

        mCardList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1599481177301&di=ecb464379c9ad3ba850b73c556aa0dff&imgtype=0&src=http%3A%2F%2Fimg3.cache.netease.com%2Fphoto%2F0031%2F2012-05-03%2F80JMJ5T043UD0031.jpg");
        mCardList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1599481012354&di=15b404d95d6cff7276c320554d6d91c9&imgtype=0&src=http%3A%2F%2Fimages.ali213.net%2Fpicfile%2Fpic%2F2013%2F04%2F01%2F927_dmhj%2520%25287%2529.jpg");
        mCardList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1599481225241&di=8110aa8fb7edad744c602b32ccd77f1c&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fzhidao%2Fpic%2Fitem%2F810a19d8bc3eb1358fde739da71ea8d3fd1f44ff.jpg");
        mCardList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1599481385113&di=3990fc234836e59fd37a3e56bb3a9028&imgtype=0&src=http%3A%2F%2Fclubimg.club.vmall.com%2Fdata%2Fattachment%2Fforum%2F202004%2F28%2F232538icfogvlsgjywjw36.jpg");
        mCardList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1599481639090&di=2ee361c8fa00c65d3e0f22045af7fa29&imgtype=0&src=http%3A%2F%2Fgss0.baidu.com%2F94o3dSag_xI4khGko9WTAnF6hhy%2Fzhidao%2Fpic%2Fitem%2F14ce36d3d539b6002ac5706de850352ac75cb7e4.jpg");

        myAdapter = new MyAdapter(rv_card, mCardList);
        mItemTouchHelperCallback = new ItemTouchHelperCallback();
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(mItemTouchHelperCallback);
        SlideLayoutManager mSlideLayoutManager = new SlideLayoutManager(this, rv_card, mItemTouchHelper, mItemTouchHelperCallback);
        rv_card.setLayoutManager(mSlideLayoutManager);
        mItemTouchHelper.attachToRecyclerView(rv_card);
        rv_card.setAdapter(myAdapter);

        mItemTouchHelperCallback.setOnSlideListener(this);
        iv_dislike_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemTouchHelperCallback.swipeTopViewToLeftOrRight(rv_card, true, 500);
            }
        });
        iv_like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemTouchHelperCallback.swipeTopViewToLeftOrRight(rv_card, false, 500);
            }
        });

    }

    /**
     * 左右滑动中
     *
     * @param viewHolder
     * @param ratio
     * @param ratioOver  滑动到临界点，小于1表示过了临界点了（松手就会自动滑出屏幕，即喜欢/不喜欢标识移动到屏幕中间）
     * @param direction
     */
    @Override
    public void onSliding(RecyclerView.ViewHolder viewHolder, float ratio, float ratioOver, int direction) {
        if (direction == ItemConfig.SLIDING_LEFT) {
            ratio = 1 - (1 + ratio);
            if (ratio < 1 && ratio > 0) {
                iv_dislike.setVisibility(View.VISIBLE);
            }
            iv_like.setVisibility(View.GONE);
            iv_dislike.setScaleX(ratio);
            iv_dislike.setScaleY(ratio);
            if (ratioOver < 1) {//渐变右1到0
                iv_dislike.setAlpha(ratioOver);
            } else {
                iv_dislike.setAlpha(ratio);
            }
            iv_dislike.setTranslationX((screenWidth + DensityUtil.dip2px(80)) * ratio / 2);

        } else if (direction == ItemConfig.SLIDING_RIGHT) {

            if (ratio < 1 && ratio > 0) {
                iv_like.setVisibility(View.VISIBLE);
            }
            iv_dislike.setVisibility(View.GONE);
            iv_like.setScaleX(ratio);
            iv_like.setScaleY(ratio);
            iv_like.setTranslationX(-(screenWidth + DensityUtil.dip2px(80)) * ratio / 2);
            if (ratioOver < 1) {//渐变右1到0
                iv_like.setAlpha(ratioOver);
            } else {
                iv_like.setAlpha(ratio);
            }

        } else {
            iv_dislike.setVisibility(View.GONE);
            iv_like.setVisibility(View.GONE);
        }
    }

    /**
     * 左右滑动结束的时候
     *
     * @param viewHolder
     * @param direction
     * @param isButtonClick 是否由点击（其他事件）触发左右滑动效果（不是由手势左右滑动触发）
     */
    @Override
    public void onSlided(RecyclerView.ViewHolder viewHolder, int direction, boolean isButtonClick) {
        if (!isButtonClick) {
            iv_dislike.setVisibility(View.GONE);
            iv_like.setVisibility(View.GONE);
        }
        if (mCardList.size() > 0) {
            myAdapter.remove(0);
        }

        if(mCardList.size() < 2){
            ArrayList<String> temp = new ArrayList<>();
            temp.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1599481177301&di=ecb464379c9ad3ba850b73c556aa0dff&imgtype=0&src=http%3A%2F%2Fimg3.cache.netease.com%2Fphoto%2F0031%2F2012-05-03%2F80JMJ5T043UD0031.jpg");
            temp.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1599481012354&di=15b404d95d6cff7276c320554d6d91c9&imgtype=0&src=http%3A%2F%2Fimages.ali213.net%2Fpicfile%2Fpic%2F2013%2F04%2F01%2F927_dmhj%2520%25287%2529.jpg");
            temp.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1599481225241&di=8110aa8fb7edad744c602b32ccd77f1c&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fzhidao%2Fpic%2Fitem%2F810a19d8bc3eb1358fde739da71ea8d3fd1f44ff.jpg");
            temp.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1599481385113&di=3990fc234836e59fd37a3e56bb3a9028&imgtype=0&src=http%3A%2F%2Fclubimg.club.vmall.com%2Fdata%2Fattachment%2Fforum%2F202004%2F28%2F232538icfogvlsgjywjw36.jpg");
            temp.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1599481639090&di=2ee361c8fa00c65d3e0f22045af7fa29&imgtype=0&src=http%3A%2F%2Fgss0.baidu.com%2F94o3dSag_xI4khGko9WTAnF6hhy%2Fzhidao%2Fpic%2Fitem%2F14ce36d3d539b6002ac5706de850352ac75cb7e4.jpg");

            myAdapter.addData(temp);
        }
    }

    /**
     * 左右滑动动画自动触发的动画开始（不是根据手势左右滑动触发的）
     *
     * @param DURATION
     * @param isLeft
     */
    @Override
    public void onAnimationStart(long DURATION, boolean isLeft) {
        final ImageView view = isLeft ? iv_dislike : iv_like;
        int distance = (screenWidth + DensityUtil.dip2px(80)) / 2;
        ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(view, "translationX", 0.0f,
                isLeft ? distance : -distance);
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", 0.0f, 1.0f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", 0.0f, 1.0f);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 0.0f, 1.0f);

        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0.0f);
        alpha.setDuration(DURATION / 2);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(DURATION / 2);
        animatorSet.play(translationAnimator).with(scaleXAnimator).with(scaleYAnimator).with(alphaAnimator).before(alpha);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                view.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                iv_dislike.setVisibility(View.GONE);
                iv_like.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                iv_dislike.setVisibility(View.GONE);
                iv_like.setVisibility(View.GONE);
            }
        });
        animatorSet.start();
    }

    private int getScreenPixWidth(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }
}

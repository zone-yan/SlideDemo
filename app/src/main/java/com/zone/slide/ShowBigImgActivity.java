package com.zone.slide;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.zone.slide.fresco.FrescoUtils;
import com.zone.slide.fresco.photodraweeview.OnPhotoTapListener;
import com.zone.slide.fresco.photodraweeview.PhotoDraweeView;

import java.util.ArrayList;


/**
 * 显示大图展示页
 */
public class ShowBigImgActivity extends AppCompatActivity {

    MyScrollViewPager vp_big_pics;
    TextView tv_pics_num;

    private ArrayList<String> mPics = new ArrayList<>();
    private BigPicsPageAdapter mBigPicsPageAdapter;
    private int mCurrentIndex = 0;

    /**
     * 显示大图
     *
     * @param context
     * @param pics
     * @param index   选中下标
     */
    public static void start(Context context, ArrayList<String> pics, int index) {

        Intent intent = new Intent(context, ShowBigImgActivity.class);
        intent.putStringArrayListExtra("BigPics", pics);
        intent.putExtra("CurrentIndex", index);
        context.startActivity(intent);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_big_img);
        vp_big_pics = findViewById(R.id.vp_big_pics);
        tv_pics_num = findViewById(R.id.tv_pics_num);

        initView();
    }


    protected void initView() {

        mPics = getIntent().getStringArrayListExtra("BigPics");
        mCurrentIndex = getIntent().getIntExtra("CurrentIndex", 0);
        vp_big_pics.setNoScroll(false);

        mBigPicsPageAdapter = new BigPicsPageAdapter();
        vp_big_pics.setAdapter(mBigPicsPageAdapter);

        vp_big_pics.setCurrentItem(mCurrentIndex);


    }

    private class BigPicsPageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mPics.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final PhotoDraweeView photoDraweeView = new PhotoDraweeView(ShowBigImgActivity.this);
            ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            photoDraweeView.setLayoutParams(layoutParams);

            String url = mPics.get(position);
            if (!TextUtils.isEmpty(url)) {
                String temp = "?x-image-process=image/resize";
                if (url.contains(temp)) {
                    url = url.substring(0, url.indexOf(temp));
                }
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "file://" + url;
                }
            }

            FrescoUtils.loadNetImage(photoDraweeView, url, new BaseControllerListener<ImageInfo>() {
                @Override
                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                    super.onFinalImageSet(id, imageInfo, animatable);
                    if (imageInfo == null) {
                        return;
                    }
                    photoDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
                }
            }, 0, 0);
            container.addView(photoDraweeView);
            photoDraweeView.setOnPhotoTapListener(new OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    finish();
                }
            });

            return photoDraweeView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}

package com.zone.slide.fresco;

import android.content.Context;
import android.graphics.PointF;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.postprocessors.IterativeBoxBlurPostProcessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.zone.slide.fresco.config.FrescoConfig;

/**
 * Created by yz on 2018/12/11 3:20 PM
 * Describe: fresco工具类
 * 原文：https://blog.csdn.net/ljy_programmer/article/details/78273267
 */
public class FrescoUtils {

    /**
     * 初始化
     *
     * @param context
     */
    public static void init(Context context) {
        ImagePipelineConfig imagePipelineConfig = new FrescoConfig.Builder(context).build();
        Fresco.initialize(context, imagePipelineConfig);
    }


    /**
     * 构建、获取ImageRequest
     *
     * @param uri              加载路径
     * @param simpleDraweeView 加载的图片控件
     * @return ImageRequest
     */
    public static ImageRequest getImageRequest(Uri uri, SimpleDraweeView simpleDraweeView, int width, int height) {

        if (width <= 0) {
            width = simpleDraweeView.getWidth();
        }
        if (height <= 0) {
            height = simpleDraweeView.getHeight();
        }
        //根据请求路径生成ImageRequest的构造者
        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(uri);
        //调整解码图片的大小
        if (width > 0 && height > 0) {
            builder.setResizeOptions(new ResizeOptions(width, height));
        }
        //设置是否开启渐进式加载，仅支持JPEG图片，这里不开启 理由：https://github.com/facebook/fresco/issues/1204
        builder.setProgressiveRenderingEnabled(false);
        //自动旋转
        builder.setRotationOptions(RotationOptions.autoRotate());

//        //图片变换处理
//        CombinePostProcessors.Builder processorBuilder = new CombinePostProcessors.Builder();
//        //加入模糊变换
//        processorBuilder.add(new BlurPostprocessor(context, radius));
//        //加入灰白变换
//        processorBuilder.add(new GrayscalePostprocessor());
//        //应用加入的变换
//        builder.setPostprocessor(processorBuilder.build());
        //更多图片变换请查看https://github.com/wasabeef/fresco-processors
        return builder.build();
    }

    /**
     * 构建、获取Controller
     *
     * @param request
     * @param oldController
     * @param listener
     * @return
     */
    public static DraweeController getController(ImageRequest request, @Nullable DraweeController oldController, ControllerListener listener) {

        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder();
        builder.setImageRequest(request);//设置图片请求
        builder.setTapToRetryEnabled(false);//设置是否允许加载失败时点击再次加载
        builder.setAutoPlayAnimations(true);//设置是否允许动画图自动播放
        builder.setOldController(oldController);
        if (listener != null) {
            builder.setControllerListener(listener);
        }
        return builder.build();
    }

    /**
     * 加载图片核心方法
     *
     * @param simpleDraweeView 图片加载控件
     * @param uri              图片加载地址
     * @param listener         图片加载监听
     */
    public static void loadImage(SimpleDraweeView simpleDraweeView, Uri uri, ControllerListener listener, int width, int height) {

        //加载失败显示的图片
//        simpleDraweeView.getHierarchy().setFailureImage(ResourcesCompat.getDrawable(MainApplication.appContext.getResources(), R.drawable.pic_default_avatar, null), ScalingUtils.ScaleType.CENTER_CROP);

        //构建并获取ImageRequest
        ImageRequest imageRequest = getImageRequest(uri, simpleDraweeView, width, height);
        //构建并获取Controller
        DraweeController draweeController = getController(imageRequest, simpleDraweeView.getController(), listener);
        //开始加载
        simpleDraweeView.setController(draweeController);
    }

    /**
     * 加载网络图片，一般用于单张或者大图展示
     * @see #loadNetImage(SimpleDraweeView simpleDraweeView, String url, int width, int height)
     */
    @Deprecated
    public static void loadNetImage(SimpleDraweeView simpleDraweeView, String url) {
        Uri uri = Uri.parse(url == null ? "" : url);
        loadImage(simpleDraweeView, uri, null, 0, 0);
    }

    /**
     * 加载网络图片, 重置宽高，一般用于小图和列表中的图片
     */
    public static void loadNetImage(SimpleDraweeView simpleDraweeView, String url, int width, int height) {
        Uri uri = Uri.parse(url == null ? "" : url);
        loadImage(simpleDraweeView, uri, null, width, height);
    }


    /**
     * 加载圆形网络图片配置
     *
     * @param simpleDraweeView
     * @param borderColor      描边颜色
     * @param borderWidth      描边宽度
     */
    public static void loadCircleNetImageConfig(SimpleDraweeView simpleDraweeView, int borderColor, int borderWidth) {
        RoundingParams params = new RoundingParams();
        if (borderWidth > 0) {
            params.setBorder(borderColor, borderWidth);
        }
        params.setRoundAsCircle(true);
        simpleDraweeView.getHierarchy().setRoundingParams(params);
    }

    /**
     * 设置图片缩放中心点
     *
     * @param simpleDraweeView
     * @param focusPoint
     */
    public static void loadPointImageConfig(SimpleDraweeView simpleDraweeView, PointF focusPoint) {
        simpleDraweeView.getHierarchy().setActualImageFocusPoint(focusPoint);
    }

    /**
     * 加载圆角网络图片配置
     *
     * @param simpleDraweeView
     * @param borderWidth      描边宽度
     * @param radius           圆角角度
     * @param borderColor      描边颜色
     */
    public static void loadRoundNetImageConfig(SimpleDraweeView simpleDraweeView, int borderWidth, int radius, int borderColor) {
        RoundingParams params = new RoundingParams();
        if (borderWidth > 0) {
            params.setBorder(borderColor, borderWidth);
        }
        params.setCornersRadius(radius);
        simpleDraweeView.getHierarchy().setRoundingParams(params);
    }


    /**
     * 加载网络图片
     *
     * @param simpleDraweeView
     * @param url
     * @param listener         图片加载监听器
     * @see   #loadNetImage(SimpleDraweeView simpleDraweeView, String url, ControllerListener listener, int width, int height)
     */
    @Deprecated
    public static void loadNetImage(SimpleDraweeView simpleDraweeView, String url, ControllerListener listener) {
        Uri uri = Uri.parse(url);
        loadImage(simpleDraweeView, uri, listener, 0, 0);
    }

    /**
     * 加载网络图片
     *
     * @param simpleDraweeView
     * @param url
     * @param listener         图片加载监听器
     */
    public static void loadNetImage(SimpleDraweeView simpleDraweeView, String url, ControllerListener listener, int width, int height) {
        Uri uri = Uri.parse(url);
        loadImage(simpleDraweeView, uri, listener, width, height);
    }

//    /**
//     * 加载网络图片(小图)，(目前不包括webp格式的图）
//     */
//    public static void loadNetSmallImage(SimpleDraweeView simpleDraweeView, String url, int width) {
//        Uri uri = Uri.parse(url + "?x-oss-process=image/resize,w_" + width);
//        loadImage(simpleDraweeView, uri, null);
//    }

    /**
     * 加载本地文件图片
     */
    public static void loadLocalImage(SimpleDraweeView simpleDraweeView, String fileName) {
        Uri uri = Uri.parse("file://" + fileName);
        loadImage(simpleDraweeView, uri, null, 0, 0);
    }

    /**
     * 加载本地文件图片
     */
    public static void loadLocalImage(SimpleDraweeView simpleDraweeView, String fileName, int width, int height) {
        Uri uri = Uri.parse("file://" + fileName);
        loadImage(simpleDraweeView, uri, null, width, height);
    }

    /**
     * 加载res下资源图片
     */
    public static void loadResourceImage(SimpleDraweeView simpleDraweeView, @DrawableRes int resId) {
        Uri uri = Uri.parse("res:///" + resId);
        loadImage(simpleDraweeView, uri, null, 0, 0);
    }

    /**
     * 加载res下资源图片
     */
    public static void loadResourceImage(SimpleDraweeView simpleDraweeView, @DrawableRes int resId, int width, int height) {
        Uri uri = Uri.parse("res:///" + resId);
        loadImage(simpleDraweeView, uri, null, width, height);
    }

    /**
     * 加载ContentProvider下的图片
     */
    public static void loadContentProviderImage(SimpleDraweeView simpleDraweeView, int resId) {
        Uri uri = Uri.parse("content:///" + resId);
        loadImage(simpleDraweeView, uri, null, 0, 0);
    }

    /**
     * 加载ContentProvider下的图片
     */
    public static void loadContentProviderImage(SimpleDraweeView simpleDraweeView, int resId, int width, int height) {
        Uri uri = Uri.parse("content:///" + resId);
        loadImage(simpleDraweeView, uri, null, width, height);
    }

    /**
     * 加载asset下的图片
     */
    public static void loadAssetImage(SimpleDraweeView simpleDraweeView, int resId) {
        Uri uri = Uri.parse("asset:///" + resId);
        loadImage(simpleDraweeView, uri, null, 0, 0);
    }

    /**
     * 加载asset下的图片
     */
    public static void loadAssetImage(SimpleDraweeView simpleDraweeView, int resId, int width, int height) {
        Uri uri = Uri.parse("asset:///" + resId);
        loadImage(simpleDraweeView, uri, null, width, height);
    }

    /**
     * 这个需要修改一下DraweeController的构建，通过setLowResImageRequest来添加小图请求
     */
    public static DraweeController getSmallToBigController(ImageRequest smallRequest, ImageRequest bigRequest, @Nullable DraweeController oldController) {

        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder();
        builder.setLowResImageRequest(smallRequest);//小图的图片请求
        builder.setImageRequest(bigRequest);//大图的图片请求
        builder.setTapToRetryEnabled(false);//设置是否允许加载失败时点击再次加载
        builder.setAutoPlayAnimations(true);//设置是否允许动画图自动播放
        builder.setOldController(oldController);
        return builder.build();
    }


    public static void loadImageSmallToBig(SimpleDraweeView simpleDraweeView, Uri smallUri, Uri bigUri) {
        //构建小图的图片请求
        ImageRequest smallRequest = getImageRequest(smallUri, simpleDraweeView, 0, 0);
        //构建大图的图片请求
        ImageRequest bigRequest = getImageRequest(bigUri, simpleDraweeView, 0, 0);
        //构建Controller
        DraweeController draweeController = getSmallToBigController(smallRequest, bigRequest, simpleDraweeView.getController());
        //开始加载
        simpleDraweeView.setController(draweeController);
    }

    /**
     * 加载网络图片，先加载小图，待大图加载完成后替换
     */
    public static void loadNetImageSmallToBig(SimpleDraweeView simpleDraweeView, String smallUrl, String bigUrl) {
        Uri smallUri = Uri.parse(smallUrl);
        Uri bigUri = Uri.parse(bigUrl);
        loadImageSmallToBig(simpleDraweeView, smallUri, bigUri);
    }

    /**
     * 以高斯模糊显示。
     *
     * @param draweeView View。
     * @param url        url.
     * @param iterations 迭代次数，越大越模糊。
     * @param blurRadius 模糊图半径，必须大于0，越大越模糊。
     */
    public static void showUrlBlur(SimpleDraweeView draweeView, String url, int iterations, int blurRadius) {
        try {
            Uri uri = Uri.parse(url);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setPostprocessor(new IterativeBoxBlurPostProcessor(iterations, blurRadius))
                    .build();
            AbstractDraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(draweeView.getController())
                    .setImageRequest(request)
                    .build();
            draweeView.setController(controller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取宽高比
     *
     * @return
     */
    public static float getImageWidthHeightScale(String imageUrl) {
        float scale = 1f;
        if (imageUrl.contains("?")) {
            String[] split = imageUrl.split("[?]");
            imageUrl = split[0];
        }
        if (!TextUtils.isEmpty(imageUrl) && imageUrl.contains("_") && imageUrl.contains(".")) {

            String s;
            int start = imageUrl.lastIndexOf("_") + 1;
            int end = imageUrl.lastIndexOf(".");

            s = imageUrl.substring(start, end);
            if (!TextUtils.isEmpty(s) && s.contains("x") && start < end) {
                String[] strings = s.split("x");
                scale = Float.parseFloat(strings[0]) / Float.parseFloat(strings[1]);
            }

        }
        return scale;
    }
}

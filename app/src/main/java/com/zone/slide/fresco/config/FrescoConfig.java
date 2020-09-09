package com.zone.slide.fresco.config;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.disk.NoOpDiskTrimmableRegistry;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Supplier;
import com.facebook.common.memory.MemoryTrimType;
import com.facebook.common.memory.MemoryTrimmable;
import com.facebook.common.memory.MemoryTrimmableRegistry;
import com.facebook.common.memory.NoOpMemoryTrimmableRegistry;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpNetworkFetcher;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import com.facebook.imagepipeline.producers.NetworkFetcher;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import okhttp3.OkHttpClient;

/**
 * Created by android_ls on 16/9/8.
 */
public class FrescoConfig {

    private FrescoConfig() {
        // FLog.setMinimumLoggingLevel(FLog.VERBOSE);
    }

    public static class Builder {

        private static final String IMAGE_PIPELINE_CACHE_DIR = "image_cache";
        private static final String IMAGE_PIPELINE_SMALL_CACHE_DIR = "image_small_cache";
        private static final int MAX_DISK_SMALL_CACHE_SIZE = 20 * ByteConstants.MB;
        private static final int MAX_DISK_SMALL_ONLOWDISKSPACE_CACHE_SIZE = 10 * ByteConstants.MB;
        //默认图磁盘缓存的最大值
        private static final int MAX_DISK_CACHE_SIZE = 100 * ByteConstants.MB;
        //默认图低磁盘空间缓存的最大值
        private static final int MAX_DISK_CACHE_LOW_SIZE = 60 * ByteConstants.MB;
        //默认图极低磁盘空间缓存的最大值
        private static final int MAX_DISK_CACHE_VERYLOW_SIZE = 20 * ByteConstants.MB;

        private final Context mContext;
        private Set<RequestListener> mRequestListeners;
        private MemoryTrimmableRegistry mMemoryTrimmableRegistry;
        private Supplier<MemoryCacheParams> mBitmapMemoryCacheParamsSupplier;
        private DiskCacheConfig mMainDiskCacheConfig;
        private DiskCacheConfig mSmallImageDiskCacheConfig;
        private NetworkFetcher mNetworkFetcher;

        public Builder(Context context) {
            mContext = Preconditions.checkNotNull(context);
        }

        public Builder setNetworkFetcher(NetworkFetcher networkFetcher) {
            mNetworkFetcher = networkFetcher;
            return this;
        }

        public Builder setRequestListeners(Set<RequestListener> requestListeners) {
            mRequestListeners = requestListeners;
            return this;
        }

        public Builder setMemoryTrimmableRegistry(MemoryTrimmableRegistry memoryTrimmableRegistry) {
            mMemoryTrimmableRegistry = memoryTrimmableRegistry;
            return this;
        }

        public Builder setBitmapMemoryCacheParamsSupplier(
                Supplier<MemoryCacheParams> bitmapMemoryCacheParamsSupplier) {
            mBitmapMemoryCacheParamsSupplier = bitmapMemoryCacheParamsSupplier;
            return this;
        }

        public Builder setMainDiskCacheConfig(DiskCacheConfig mainDiskCacheConfig) {
            mMainDiskCacheConfig = mainDiskCacheConfig;
            return this;
        }

        public Builder setSmallImageDiskCacheConfig(DiskCacheConfig smallImageDiskCacheConfig) {
            mSmallImageDiskCacheConfig = smallImageDiskCacheConfig;
            return this;
        }

        public ImagePipelineConfig build() {
            if (mNetworkFetcher == null) {
                OkHttpClient okHttpClient = new OkHttpClient();
//                mNetworkFetcher = new HttpUrlConnectionNetworkFetcher();
                mNetworkFetcher = new OkHttpNetworkFetcher(okHttpClient);

            }

            if (mRequestListeners == null) {
                mRequestListeners = new HashSet<>();
                mRequestListeners.add(new RequestLoggingListener());
            }

            if (mBitmapMemoryCacheParamsSupplier == null) {
                mBitmapMemoryCacheParamsSupplier = new BitmapMemoryCacheParamsSupplier(
                        (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE));
            }

            if (mMemoryTrimmableRegistry == null) {
                // 当内存紧张时采取的措施
                mMemoryTrimmableRegistry = NoOpMemoryTrimmableRegistry.getInstance();
                mMemoryTrimmableRegistry.registerMemoryTrimmable(new MemoryTrimmable() {
                    @Override
                    public void trim(MemoryTrimType trimType) {
                        final double suggestedTrimRatio = trimType.getSuggestedTrimRatio();

                        if (MemoryTrimType.OnCloseToDalvikHeapLimit.getSuggestedTrimRatio() == suggestedTrimRatio
                                || MemoryTrimType.OnSystemLowMemoryWhileAppInBackground.getSuggestedTrimRatio() == suggestedTrimRatio
                                || MemoryTrimType.OnSystemLowMemoryWhileAppInForeground.getSuggestedTrimRatio() == suggestedTrimRatio
                                ) {
                            // 清除内存缓存
                            Fresco.getImagePipeline().clearMemoryCaches();
                        }
                    }
                });
            }

            /*
             * 推荐缓存到应用本身的缓存文件夹，这么做的好处是:
             * 1、当应用被用户卸载后能自动清除缓存
             * 2、一些内存清理软件可以扫描出来，进行内存的清理
             */
            if (mMainDiskCacheConfig == null) {
                File fileCacheDir = mContext.getCacheDir();
                mMainDiskCacheConfig = DiskCacheConfig.newBuilder(mContext)
                        .setBaseDirectoryName(IMAGE_PIPELINE_CACHE_DIR)
                        .setBaseDirectoryPath(fileCacheDir)
                        .setMaxCacheSize(MAX_DISK_CACHE_SIZE)//默认缓存的最大大小。
                        .setMaxCacheSizeOnLowDiskSpace(MAX_DISK_CACHE_LOW_SIZE)//缓存的最大大小,使用设备时低磁盘空间。
                        .setMaxCacheSizeOnVeryLowDiskSpace(MAX_DISK_CACHE_VERYLOW_SIZE)//缓存的最大大小,当设备极低磁盘空间
                        .setDiskTrimmableRegistry(NoOpDiskTrimmableRegistry.getInstance())
                        .build();
            }

            if (mSmallImageDiskCacheConfig == null) {
                File fileCacheDir = mContext.getCacheDir();
                mSmallImageDiskCacheConfig = DiskCacheConfig.newBuilder(mContext)
                        .setBaseDirectoryPath(fileCacheDir)
                        .setBaseDirectoryName(IMAGE_PIPELINE_SMALL_CACHE_DIR)
                        .setMaxCacheSize(MAX_DISK_SMALL_CACHE_SIZE)
                        .setMaxCacheSizeOnLowDiskSpace(MAX_DISK_SMALL_ONLOWDISKSPACE_CACHE_SIZE)
                        .setDiskTrimmableRegistry(NoOpDiskTrimmableRegistry.getInstance())
                        .build();
            }

            return ImagePipelineConfig.newBuilder(mContext)
                    .setBitmapsConfig(Bitmap.Config.RGB_565) // 若不是要求忒高清显示应用，就用使用RGB_565吧（默认是ARGB_8888)
                    .setDownsampleEnabled(true) // 在解码时改变图片的大小，支持PNG、JPG以及WEBP格式的图片，与ResizeOptions配合使用
//                    .setProgressiveJpegConfig(new ProgressiveJpegConfig() { // 设置Jpeg格式的图片支持渐进式显示
//                        @Override
//                        public int getNextScanNumberToDecode(int scanNumber) {
//                            return scanNumber + 2;
//                        }
//
//                        public QualityInfo getQualityInfo(int scanNumber) {
//                            boolean isGoodEnough = (scanNumber >= 5);
//                            return ImmutableQualityInfo.of(scanNumber, isGoodEnough, false);
//                        }
//                    })
                    .setNetworkFetcher(mNetworkFetcher)
                    .setRequestListeners(mRequestListeners)
                    .setMemoryTrimmableRegistry(mMemoryTrimmableRegistry) // 报内存警告时的监听
                    .setBitmapMemoryCacheParamsSupplier(mBitmapMemoryCacheParamsSupplier) // 设置内存配置
                    .setMainDiskCacheConfig(mMainDiskCacheConfig) // 设置主磁盘配置
                    .setSmallImageDiskCacheConfig(mSmallImageDiskCacheConfig) // 设置小图的磁盘配置
                    .build();
        }
    }

}

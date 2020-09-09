package com.zone.slide;

import android.app.Application;
import android.content.Context;

import com.zone.slide.fresco.FrescoUtils;

/**
 * Created by yz on 2020/5/27 5:28 PM
 * Describe:
 */
public class MyApplication extends Application {
    public static volatile Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();

        //初始化Fresco
        FrescoUtils.init(this);
    }
}

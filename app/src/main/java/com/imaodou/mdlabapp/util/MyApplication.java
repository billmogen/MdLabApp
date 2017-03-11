package com.imaodou.mdlabapp.util;

import android.app.Application;
import android.content.Context;

/**
 * Created by billmogen on 2017/2/7.
 */

public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        //获取Context
        context = getApplicationContext();
    }

    //返回
    public static Context getContextObject(){
        return context;
    }

}

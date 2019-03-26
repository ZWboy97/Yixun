package com.jackchance.yixun;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.fengmap.android.FMMapSDK;
import com.jackchance.yixun.Bean.AMapPoint;
import com.jackchance.yixun.Util.OrderContext;
import com.jackchance.yixun.ZAssets.ARCode.GLView;

import cn.bmob.v3.Bmob;


/**
 * Created by 蚍蜉 on 2017/7/28.
 */

public class YiXunAPP extends Application {

    private String Bmob_ID = "e6791873b8175b3975936cb5c4ce5322";    //Bmob后端云ID
    public static SharedPreferences mSharedPreferences;             //本地配置存储对象
    public static YiXunAPP mInstance = null;                        //应用对象实例
    public static AMapPoint lastPoint = null;// 上一次定位到的经纬度

    public static final String WEIGHT_VALUE = "weight_value";

    public static final String STEP_LENGTH_VALUE = "step_length_value";// ????

    public static final String SENSITIVITY_VALUE = "sensitivity_value";// ?????

    public static final String SETP_SHARED_PREFERENCES = "setp_shared_preferences";// ????

    public static GLView glView;



    @Override
    public void onCreate() {
        super.onCreate();

        FMMapSDK.init(this);            //初始化地图引擎，传入 Application
        Bmob.initialize(this,Bmob_ID);  //初始化Bmob云数据库

        mInstance = this;               //初始化应用实例
        mSharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);  //初始化本地存储对象

        if (!OrderContext.isInitialized()) {
            OrderContext.init(getApplicationContext());
        }


    }


}

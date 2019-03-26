package com.jackchance.yixun.Service;

import android.util.Log;

import com.jackchance.yixun.Bean.AMapPoint;


/**
 * Created by Administrator on 2015/7/31.
 */
public class StepLocation {
    public AMapPoint lastAMapPoint;
    private static class LazyHolder {
        private static StepLocation INSTANCE = new StepLocation();
    }
    public static  StepLocation getInstance() {
        return LazyHolder.INSTANCE;
    }
    private StepLocation(){
        Log.i("zjx","StepLocation 初始化");
        int CurrentFloorDefault=1;//当前楼层 飞机场为1 农大体育馆为0
        lastAMapPoint=new AMapPoint(0.5,0.5, CurrentFloorDefault,30,0);
    }
    public static void refresh(){
        LazyHolder.INSTANCE=null;
        LazyHolder.INSTANCE = new StepLocation();
    }
}

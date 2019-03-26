package com.jackchance.yixun.Util;

import android.util.Log;

import com.fengmap.android.map.geometry.FMMapCoord;
import com.fengmap.android.utils.FMMath;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Description
 */
public class FMLocationAPI {
    private OnFMLocationListener mListener;     //监听
    private ArrayList<FMMapCoord> mCoordList;   //插值后的点和角度集合
    private double mTotalDistance;              //总距离
    private volatile int mIndex;                //索引
    private double mWalkStep;                   //步长
    private Timer mTimer;
    private TimerTask mTask;
    private long mFrameTime = 80;
    private long mDelayTime = 50;
    private int mGroupId = -1;                  //默认楼层-1
    static public boolean continueWalk = true;
    double[] angles;                            //相邻导航坐标点之间的角度
    static public double angleRange = 60;       //允许更新的角度区间
    public static double currentAngle;

    public ArrayList<FMMapCoord> getmCoordList() {
        return mCoordList;
    }

    public void initStepCounter(){
        mIndex = 0;
        if (mListener != null) {
            mListener.onAnimationStart();
        }
        //获取并计算行走点之间的角度
        angles = FMMath.getAnglesBetweenCoords(mCoordList);
    }

    public void updateToNextPoint(int step){

        if (mIndex > mCoordList.size() - 1) {
            if (mListener != null) {
                mListener.onAnimationEnd();
            }
            return;
        }
        double distance = mWalkStep * mIndex;
        if (mIndex == mCoordList.size() - 1) {
            distance = mTotalDistance;
        }
        //index小于行走点数据，更新画面
        if (mListener != null) {
            mListener.onAnimationUpdate(mCoordList.get(mIndex), mWalkStep, angles[mIndex]);
        }
        //行走点index增加
        mIndex= mIndex+step;
    }

    /**
     * 构造函数
     */
    public FMLocationAPI() {
        this.mTimer = new Timer();
        this.mCoordList = new ArrayList<FMMapCoord>();
    }

    /**
     * 复制路线坐标点，并返回
     * @param points
     * @return 返回复制的坐标点集合
     */
    public static ArrayList<FMMapCoord> cloneCollections(ArrayList<FMMapCoord> points) {
        ArrayList<FMMapCoord> list = new ArrayList<FMMapCoord>();
        for (FMMapCoord mapCoord : points) {
            list.add(mapCoord.clone());
        }
        return list;
    }

    /**
     * 安装目标线路
     * @param points 点集合
     * @param groupId 楼层
     */
    public void setupTargetLine(ArrayList<FMMapCoord> points, int groupId) {
        float verticalDist = 0;
        setupTargetLine(points, groupId, verticalDist);
    }

    /**
     * 安装目标线路
     * @param points 行走点集合
     * @param groupId   楼层id
     * @param verticalDist 垂直距离
     */
    public void setupTargetLine(ArrayList<FMMapCoord> points, int groupId, float verticalDist) {
        this.mIndex = 0;
        this.mGroupId = groupId;
        this.mCoordList.clear();
        this.mTotalDistance = ConvertUtils.getDistance(points);
        //添加模拟坐标点
        ArrayList<FMMapCoord> clones = cloneCollections(points);//从points复制点坐标集合到clones中
        FMMath.makeBezierSmooth(clones, 3);     //线拐角处平滑插值
        mCoordList.addAll(makeSimulatePoints(clones, verticalDist));
        //平均行走距离
        mWalkStep = mTotalDistance / mCoordList.size();
    }

    /**
     * 添加模拟行走坐标点
     * @param point 坐标点
     */
    public void addSimulatePoint(FMMapCoord point) {
        int index = compareSimulatePoint(point);
        int count = 20;
        for (int i = 0; i < count; i++) {
            FMMapCoord coord = new FMMapCoord(point.x + 1 * i, point.y);
            mCoordList.add(index + i, coord);
        }
    }

    /**
     * 比对模拟点
     * @param origin 要插入的模拟点坐标
     * @return
     */
    private int compareSimulatePoint(FMMapCoord origin) {
        for (int i = 0; i < mCoordList.size(); i++) {
            FMMapCoord coord = mCoordList.get(i);
            double offset = origin.x - coord.x;
            if (offset >= 0 && offset <= 2) {
                return i;
            }
        }
        return 0;
    }

    /**
     * 设置监听
     * @param pListener
     */
    public void setFMLocationListener(OnFMLocationListener pListener) {
        this.mListener = pListener;
    }

    public void setFrameTime(long time) {
        this.mFrameTime = time;
    }

    public int getGroupId() {
        return mGroupId;
    }

    /**
     * 开始执行动画
     */
    public void start() {
        this.mIndex = 0;
        if (mListener != null) {
            mListener.onAnimationStart();
        }
        //获取并计算行走点之间的角度
        angles = FMMath.getAnglesBetweenCoords(mCoordList);

        //开始执行刷新动画
        mTask = new TimerTask() {
            @Override
            public void run() {
                //行走点走完了
                if (mIndex > mCoordList.size() - 1) {
                    mTask.cancel();
                    mTimer.purge();

                    if (mListener != null) {
                        mListener.onAnimationEnd();
                    }
                    return;
                }
                //更新行走距离
                if(continueWalk){
                    double distance = mWalkStep * mIndex;
                    if (mIndex == mCoordList.size() - 1) {
                        distance = mTotalDistance;
                    }
                    //index小于行走点数据，更新画面
                    if (mListener != null) {
                        mListener.onAnimationUpdate(mCoordList.get(mIndex), mWalkStep, angles[mIndex]);
                    }
                    //行走点index增加
                    mIndex++;
                }
            }
        };

        mTimer.schedule(mTask, mDelayTime, mFrameTime);
    }

    /**
     * 基于手机方向传感器的方向，判断方向是否正确，以决定是否更新图片位置
     * @param currentAngle  来自传感器的角度
     * @return
     */
    public boolean DirectionCheck(float currentAngle){
        if(mIndex >= mCoordList.size())
            return false;

        Double max = angles[mIndex]+30+360;
        Double min = angles[mIndex]-30+360;
        currentAngle = currentAngle +360;
        if(currentAngle < max && currentAngle > min){
            continueWalk = true;
        }else{
            continueWalk = false;
        }
//                Log.e("", "max= "+Double.toString(max)+"\n" +
//                        "min="+Double.toString(min)+"\n" +
//                "currentangle= "+Float.toString(currentAngle));
        return continueWalk;

    }

    /**
     * 生成模拟数据
     * @param points 点集合
     * @return
     */
    private ArrayList<FMMapCoord> makeSimulatePoints(ArrayList<FMMapCoord> points) {
        float verticalDist = 2.0f;
        return makeSimulatePoints(points, verticalDist);
    }

    /**
     * 生成模拟数据
     *
     * @param points       点集合
     * @param verticalDist 垂直偏移
     * @return
     */
    private ArrayList<FMMapCoord> makeSimulatePoints(ArrayList<FMMapCoord> points, float verticalDist) {
        float speed = 0.25f;
        return CalcSimulate.calcSimulateLocationPoints(points, speed, verticalDist);
    }

    public ArrayList<FMMapCoord> getSimulateCoords() {
        return mCoordList;
    }

    /**
     * 暂停
     */

    public void stop() {
        if (mTask != null) {
            mTask.cancel();
            mTimer.purge();
        }
    }

    public void setDelayTime(long delayTime) {
        this.mDelayTime = delayTime;
    }


    /**
     * 销毁
     */
    public void destroy() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mCoordList != null) {
            mCoordList.clear();
        }
        mListener = null;
        mTask = null;
    }


    public double getTotalDistance() {
        return mTotalDistance;
    }

    /**
     * 动画回调监听
     */
    public interface OnFMLocationListener {
        /**
         * 模拟动画开始
         */
        void onAnimationStart();

        /**
         * 模拟动画更新
         *
         * @param mapCoord 当前点坐标
         * @param distance 行走距离
         * @param angle    当前点角度
         */
        void onAnimationUpdate(FMMapCoord mapCoord, double distance, double angle);

        /**
         * 模拟动画结束
         */
        void onAnimationEnd();
    }

}

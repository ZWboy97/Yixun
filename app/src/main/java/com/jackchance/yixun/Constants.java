package com.jackchance.yixun;

import java.security.PublicKey;

/**
 * 本类提供应用使用的全局常量
 * Created by 蚍蜉 on 2017/7/30.
 */

public class Constants {
    /***************************本地存储对象访问键***************************/
    public static final String SHARED_PREFERENCE_NAME = "YiXun";    //存储名称
    public static final String String_MapId = "Cur_MapId";          //地图id
    public static final String String_MapName = "Cur_MapName";      //地图名字
    public static final String String_X = "Cur_X";                  //x坐标
    public static final String String_Y = "Cur_Y";                  //y坐标
    public static final String String_GroupId = "Cur_GroupId";      //当前楼层id
    public static final String String_ThemeId = "Map_Theme";        //地图主题
    public static final String String_Direction = "Direction";      //方向
    public static final String String_Detail = "Detail";            //详情
    public static final String String_ModelName = "Model_Name";     //模型名称
    public static final String String_ModelSite = "Model_Site";     //商铺网站
    public static final String String_ArId = "Ar_id";               //AR的id
    public static final String String_QrType = "Qr_Type";           //QR的类型（商铺/商铺）
    public static final String String_ModelImage = "ModelImage";    //模型图片

    //反向寻车
    public static final String Car_MapId = "Car_MapId";          //地图id
    public static final String Car_MapName = "Car_MapName";      //地图名字
    public static final String Car_X = "Car_X";                  //x坐标
    public static final String Car_Y = "Car_Y";                  //y坐标
    public static final String Car_GroupId = "Car_GroupId";      //当前楼层id
    public static final String Car_ThemeId = "Car_Theme";        //地图主题
    public static final String Car_ModelName = "Car_Name";     //模型名称
    public static final String Car_isHaving = "Car_ishaving";   //是否有车位记录



    /*********************************应用使用的全局引擎id***********************************/
    public static final String Bmob_ID =
            "e6791873b8175b3975936cb5c4ce5322";                     //Bmob云数据库id
    //EasyAR Key
    public static final String EasyAR_KEY =                         //EasyAR引擎ID
            "qkv5wFLURMedy1EymFZQUpwrvoBsgsfHEDktxuk5RdIeiApZ6jilHEKNDmbBKeYHAbqCHSYH8YYqYSedzPVHLH08MQVQTENq0Wt1qW4J17kk7eYQwz6VmVMUn14kqx6AN63gcRrmOZgmwTtOmw5IU0NWenK3zAKllrj15KneqwgBElln9FR185mW6ZDWCw9ymaI24sn2";

}

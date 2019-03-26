package com.jackchance.yixun.Bean;

import cn.bmob.v3.BmobObject;

/**
 * @author 蚍蜉
 * 二维码数据信息的数据结构，用于和bmob后端进行通信
 * 对应bmob平台的二维码数据表设计
 * created on 2017/9/10
 */

public class QRInfo extends BmobObject {

    //地图级别信息
    private String mapid;                   //二维码位置对应的fengmapid
    private String mapname;                 //二维码对应的地图名称
    //模型定位级别信息
    private Integer groupid;                //二维码所在的楼层
    private Float x;                        //位置x
    private Float y;                        //位置y
    private Integer direction;              //位置方向
    private String detail;                  //位置备注描述
    //商务业务级别信息
    private String modelname;               //模型名称
    private String modelsite;               //模型业务网址
    private String arid;                    //模型业务对应的ar展示id
    private Integer qrtype;                 //二维码的类型，是商铺还是商品
    private String imageURL;                //商铺图片的位置
    //构造函数
    public QRInfo() {
        mapid = "0000";
        mapname = "未知";
        groupid = 1;
        direction = 1;
        detail = "未知";
        modelname = "未知";
        modelsite = "未知";
        imageURL = "未知";
        qrtype = 1;
        arid = "0000";
    }
    /******************************getter and setter*****************************/
    public String getMapid() {
        return mapid;
    }

    public String getArid() {
        return arid;
    }

    public void setQrtype(int qrtype) {
        this.qrtype = qrtype;
    }

    public void setModelsite(String modelsite) {

        this.modelsite = modelsite;
    }

    public void setModelname(String modelname) {

        this.modelname = modelname;
    }

    public void setMapname(String mapname) {

        this.mapname = mapname;
    }

    public void setX(Float x) {

        this.x = x;
    }

    public void setY(Float y) {

        this.y = y;
    }

    public void setMapid(String mapid) {

        this.mapid = mapid;
    }

    public void setImageURL(String imageURL) {

        this.imageURL = imageURL;
    }

    public void setGroupid(Integer groupid) {

        this.groupid = groupid;
    }

    public void setDirection(Integer direction) {

        this.direction = direction;
    }

    public void setDetail(String detail) {

        this.detail = detail;
    }

    public void setArid(String arid) {

        this.arid = arid;
    }

    public Float getY() {

        return y;
    }

    public Float getX() {

        return x;
    }

    public Integer getQrtype() {

        return qrtype;
    }

    public String getModelsite() {

        return modelsite;
    }

    public String getModelname() {

        return modelname;
    }

    public String getMapname() {

        return mapname;
    }

    public String getImageURL() {

        return imageURL;
    }

    public Integer getGroupid() {

        return groupid;
    }

    public Integer getDirection() {

        return direction;
    }

    public String getDetail() {

        return detail;
    }
}

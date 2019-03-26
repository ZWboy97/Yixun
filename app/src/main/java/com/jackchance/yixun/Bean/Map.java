package com.jackchance.yixun.Bean;


import java.io.Serializable;

/**
 * 地图自定义数据结构
 * 进行了序列化，以便于活动间传递
 * Created by 蚍蜉 on 2017/8/27.
 */

public class Map implements Serializable{
    private String Name;        //地图名称
    private String MapId;       //地图ID
    private String Detail;      //地图简介
    private String Loction;     //地图位置
    private int ImageID;        //地图对于的图片id
    private String Themeid;     //地图主题id
    private float init_X;       //初始X
    private float init_y;       //初始Y
    private int init_group;     //初始楼层

    /**
     * 构造函数
     * @param name      地图名称
     * @param MapId     地图id
     * @param imageID   图片id
     * @param loction   地图所在的物理地址信息
     * @param detail    地图详细信息
     */
    public Map(String name,String MapId,int imageID,String loction,String detail){
        this.Name = name;
        this.MapId = MapId;
        this.Detail = detail;
        this.Loction=loction;
        this.Themeid = "3007";
        this.ImageID = imageID;
    }
    public Map(String name,String MapId,int imageID,String loction,String theme,String detail){
        this.Name = name;
        this.MapId = MapId;
        this.Detail = detail;
        this.Loction=loction;
        this.Themeid = theme;
        this.ImageID = imageID;
    }

    /**
     * 无参数的构造函数
     */
    public Map(){

    }

    /*********************************getter and setter********************************/
    public String getName() {
        return Name;
    }

    public String getDetail() {
        return Detail;
    }
    public void setImageID(int imageID) {
        ImageID = imageID;
    }

    public void setLoction(String loction) {

        Loction = loction;
    }

    public void setDetail(String detail) {

        Detail = detail;
    }

    public void setMapId(String mapId) {

        MapId = mapId;
    }

    public void setName(String name) {

        Name = name;
    }

    public float getInit_X() {
        return init_X;
    }

    public void setInit_X(float init_X) {
        this.init_X = init_X;
    }

    public float getInit_y() {
        return init_y;
    }

    public void setInit_y(float init_y) {
        this.init_y = init_y;
    }

    public int getInit_group() {
        return init_group;
    }

    public void setInit_group(int init_group) {
        this.init_group = init_group;
    }

    public int getImageID() {

        return ImageID;

    }

    public String getLoction() {

        return Loction;
    }

    public String getMapId() {

        return MapId;
    }
    public String getThemeid(){
        return Themeid;
    }
    public void setThemeid(String themeid){
        Themeid = themeid;
    }


}

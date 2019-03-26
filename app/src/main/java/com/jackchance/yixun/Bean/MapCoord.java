package com.jackchance.yixun.Bean;

import com.fengmap.android.map.geometry.FMMapCoord;

import java.io.Serializable;

/**
 * 对地图坐标数据进行自定义扩展的类
 * @author 蚍蜉
 * @Description 将坐标与楼层封装在一起，便于传输
 */
public class MapCoord implements Serializable{  //进行了序列化

    private int groupId;                //位置所在的楼层id
    private FMMapCoord mapCoord;        //位置的地图内部坐标

    /**
     * 无参数构造函数
     */
    public MapCoord(){
        this.groupId=0;
    }

    /**
     * 构造函数
     * @param groupId   楼层id
     * @param mapCoord  坐标id
     */
    public MapCoord(int groupId, FMMapCoord mapCoord) {
        this.groupId = groupId;
        this.mapCoord = mapCoord;
    }

    /**
     * 构造函数
     * @param mapCoord  坐标
     * @param groupId   楼层id
     */
    public MapCoord(FMMapCoord mapCoord,int groupId) {
        this.groupId = groupId;
        this.mapCoord = mapCoord;
    }

    /******************************getter and setter***********************************/
    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public FMMapCoord getMapCoord() {
        return mapCoord;
    }

    public void setMapCoord(FMMapCoord mapCoord) {
        this.mapCoord = mapCoord;
    }

    public void copy(MapCoord source){
        this.groupId = source.getGroupId();
        this.mapCoord = source.getMapCoord();
    }
}

package com.jackchance.yixun.Bean;

import com.fengmap.android.map.geometry.FMMapCoord;

/**
 * Created by 蚍蜉 on 2018/4/9.
 */

public class POI {


    private String Name;
    private FMMapCoord CenterMapCoord;
    private String FID;
    private int GroupID;

    public POI(String name, FMMapCoord centerMapCoord, String FID, int groupID) {
        Name = name;
        CenterMapCoord = centerMapCoord;
        this.FID = FID;
        GroupID = groupID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public FMMapCoord getCenterMapCoord() {
        return CenterMapCoord;
    }

    public void setCenterMapCoord(FMMapCoord centerMapCoord) {
        CenterMapCoord = centerMapCoord;
    }

    public String getFID() {
        return FID;
    }

    public void setFID(String FID) {
        this.FID = FID;
    }

    public int getGroupID() {
        return GroupID;
    }

    public void setGroupID(int groupID) {
        GroupID = groupID;
    }
}

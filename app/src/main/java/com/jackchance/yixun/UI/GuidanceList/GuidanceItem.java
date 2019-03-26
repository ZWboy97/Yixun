package com.jackchance.yixun.UI.GuidanceList;

import cn.bmob.v3.BmobObject;

/**
 * Created by 蚍蜉 on 2018/5/21.
 */

public class GuidanceItem extends BmobObject{

    public String guidanceTitle;
    public String guidanceContent;
    public String details;
    public String mapName;
    public String url;
    public String time;

    public GuidanceItem() {
        this.guidanceTitle = "第十届大创展参展指南";
        this.guidanceContent = "北邮第十届大创展";
        this.details = "欢迎来到第十届大创展.........";
        this.mapName = "北邮第十届大创展";
        this.time = "2018/5/21";
        this.url = "http://mp.weixin.qq.com/s?__biz=MzAxOTc0ODk4Ng==&mid=2650439225&idx=1&sn=6fa06af5bf16c6870cf37432850d0c39&chksm=83cc9f2cb4bb163aa8c8268a99ee28953b9dce520302a539d206218667178cf529c3f9ff2d6f&mpshare=1&scene=23&srcid=0521cqY5dFKEZIS4AypQwdsc#rd";
    }

    public GuidanceItem(String title, String content, String details,
                     String mapName, String url,String time) {
        this.guidanceTitle = title;
        this.guidanceContent = content;
        this.details = details;
        this.mapName = mapName;
        this.url = url;
        this.time = time;
    }

    public String getGuidanceTitle() {
        return guidanceTitle;
    }

    public void setGuidanceTitle(String guidanceTitle) {
        this.guidanceTitle = guidanceTitle;
    }

    public String getGuidanceContent() {
        return guidanceContent;
    }

    public void setGuidanceContent(String guidanceContent) {
        this.guidanceContent = guidanceContent;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

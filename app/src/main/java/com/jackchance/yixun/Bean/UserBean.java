/*****************************************************************************
 **                                                                         **
 **               Copyright (C) 2017 XMLIU diyangxia@163.com.               **
 **                                                                         **
 **                          All rights reserved.                           **
 **                                                                         **
 *****************************************************************************/
package com.jackchance.yixun.Bean;

import java.util.ArrayList;
import cn.bmob.v3.BmobUser;

/**
 * 用户信息的数据结构，在bmobuser基础上进行扩充
 * @author 蚍蜉
 * created on 2017/10/9
 */
public class UserBean extends BmobUser {

    /**
     * 验证码，只作记录用
     */
    Integer code;
    /**
     * 头像地址
     */
    String avatar;
    /**
     * 用户背景图片
     */
    String bgurl;
    /**
     * 昵称
     */
    String nickname;
    /**
     * 性别：男 女
     */
    Integer gender;
    /**
     * 所在地区
     */
    String area;
    /**
     * 所在地区ids
     */
    String areaIds;
    /**
     * 个性签名
     */
    String signature;
    /**
     * 极光推送id
     */
    Integer rid;
    /**
     * 出生年月
     */
    String birth;
    /**
     * 收藏地点
     */
    ArrayList<String> favourList;

    public UserBean() {
        favourList = new ArrayList<String>();
    }

    public ArrayList<String> getFavour() {
        return favourList;
    }

    public void setFavour(ArrayList<String> favour) {
        this.favourList.addAll(0,favour);
    }

    public void addToFavourList(String favour){
        this.favourList.add(favour);


    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getAreaIds() {
        return areaIds;
    }

    public void setAreaIds(String areaIds) {
        this.areaIds = areaIds;
    }

    public Integer getRid() {
        return rid;
    }

    public void setRid(Integer rid) {
        this.rid = rid;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBgurl() {
        return bgurl;
    }

    public void setBgurl(String bgurl) {
        this.bgurl = bgurl;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


}
package com.jackchance.yixun.Bean;

import com.jackchance.yixun.R;

/**
 * Created by 蚍蜉 on 2018/5/18.
 */

public class Theme {

    private String themeName;
    private String themeID;
    private int imageID;

    public Theme() {
        themeName = "缤纷";
        themeID = "3010";
        imageID = R.drawable.theme_colorful;
    }

    public Theme(String themeName, String themeID,int imageID) {
        this.themeName = themeName;
        this.themeID = themeID;
        this.imageID = imageID;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public String getThemeID() {
        return themeID;
    }

    public void setThemeID(String themeID) {
        this.themeID = themeID;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }
}

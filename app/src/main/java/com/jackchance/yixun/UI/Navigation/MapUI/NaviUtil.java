package com.jackchance.yixun.UI.Navigation.MapUI;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.fengmap.android.analysis.navi.FMNaviAnalyser;
import com.fengmap.android.analysis.navi.FMNaviDescriptionData;
import com.fengmap.android.analysis.search.FMSearchAnalyser;
import com.fengmap.android.analysis.search.FMSearchResult;
import com.fengmap.android.analysis.search.model.FMSearchModelByKeywordRequest;
import com.fengmap.android.map.FMMap;
import com.fengmap.android.map.geometry.FMMapCoord;
import com.fengmap.android.map.marker.FMImageMarker;
import com.fengmap.android.map.marker.FMLocationMarker;
import com.fengmap.android.map.marker.FMModel;
import com.jackchance.yixun.Bean.MapCoord;

import java.util.ArrayList;

/**
 * Created by 蚍蜉 on 2017/12/27.
 */

public class NaviUtil {

    /**获得导航信息当前地址
     *
     * @param position 导航步骤
     * @param fmNaviAnalyser 导航分析对象
     * @return 坐标
     */
    public static MapCoord getCurrentCoord(int position, FMNaviAnalyser fmNaviAnalyser) {
        ArrayList<FMNaviDescriptionData> datas = fmNaviAnalyser.getNaviDescriptionData();
        if(position == datas.size())
            return makeMapCoord(datas.get(datas.size()-1).getEndCoord(),
                    datas.get(datas.size()-1).getEndGroupId());
        FMNaviDescriptionData data = datas.get(position);
        return makeMapCoord(data.getStartCoord(), data.getStartGroupId());
    }

    /**获取导航信息下一站点地址
     *
     * @param position 导航步骤
     * @param fmNaviAnalyser 导航分析器
     * @return 坐标
     */
    public static MapCoord getNextCoord(int position,FMNaviAnalyser fmNaviAnalyser) {
        ArrayList<FMNaviDescriptionData> datas = fmNaviAnalyser.getNaviDescriptionData();
        if(position == datas.size())
            return makeMapCoord(datas.get(datas.size()-1).getEndCoord(),
                    datas.get(datas.size()-1).getEndGroupId());
        FMNaviDescriptionData data = datas.get(position);
        return makeMapCoord(data.getEndCoord(), data.getEndGroupId());

    }
    public static MapCoord makeMapCoord(FMMapCoord coord, int groupId) {
        return new MapCoord(coord, groupId);
    }

    /**
     * 获取第position位置的导航信息
     * @param fmNaviAnalyser
     * @param position
     * @return
     */
    public static FMNaviDescriptionData getDescription(int position,FMNaviAnalyser fmNaviAnalyser){
        ArrayList<FMNaviDescriptionData> datas = fmNaviAnalyser.getNaviDescriptionData();
        if(position == datas.size()){
            return datas.get(position-1);
        }
        else
            return datas.get(position);
    }

    /**
     * 行走时间
     * @param distance 距离
     * @return
     */
    public static int getTimeByWalk(double distance) {
        if (distance == 0) {
            return 0;
        }
        int time = (int) Math.ceil(distance / 80);
        if (time < 1) {
            time = 1;
        }
        return time;
    }

    /**
     * 通过关键字查询模型
     *
     * @param searchAnalyser 搜素控制
     * @param map            地图
     * @param keyword        关键字
     */
    public static ArrayList<FMModel> queryModelByKeyword(FMMap map, FMSearchAnalyser searchAnalyser,
                                                         String keyword) {
        int[] groupIds = map.getMapGroupIds();
        return queryModelByKeyword(map, groupIds, searchAnalyser, keyword);
    }

    /**
     * 通过关键字查询模型
     *
     * @param searchAnalyser 搜素控制
     * @param map            地图
     * @param keyword        关键字
     * @param groupIds       楼层集合
     */
    public static ArrayList<FMModel> queryModelByKeyword(FMMap map, int[] groupIds, FMSearchAnalyser searchAnalyser,
                                                         String keyword) {
        ArrayList<FMModel> list = new ArrayList<FMModel>();
        FMSearchModelByKeywordRequest request = new FMSearchModelByKeywordRequest(groupIds, keyword);
        ArrayList<FMSearchResult> result = searchAnalyser.executeFMSearchRequest(request);
        for (FMSearchResult r : result) {
            String fid = (String) r.get("FID");
            FMModel model = map.getFMLayerProxy().queryFMModelByFid(fid);
            list.add(model);
        }
        return list;
    }


    /**
     * 添加图片标注
     * @param resources 资源
     * @param mapCoord  坐标
     * @param resId     资源id
     */
    public static FMImageMarker buildImageMarker(Resources resources, FMMapCoord mapCoord, int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(resources, resId);
        FMImageMarker imageMarker = new FMImageMarker(mapCoord, bitmap);
        //设置图片宽高
        imageMarker.setMarkerWidth(90);
        imageMarker.setMarkerHeight(90);
        //设置图片在模型之上
        imageMarker.setCustomOffsetHeight(1.5f);
        imageMarker.setFMImageMarkerOffsetMode(FMImageMarker.FMImageMarkerOffsetMode.FMNODE_CUSTOM_HEIGHT);
        return imageMarker;
    }

    /**
     * 创建定位标注
     *
     * @param groupId  楼层id
     * @param mapCoord 坐标点
     * @param angle    方向
     * @return
     */
    public static FMLocationMarker buildLocationMarker(int groupId, FMMapCoord mapCoord, float angle) {
        FMLocationMarker locationMarker = new FMLocationMarker(groupId, mapCoord);
        //设置定位点图片
        locationMarker.setActiveImageFromAssets("active.png");
        //设置定位图片宽高
        locationMarker.setMarkerWidth(90);
        locationMarker.setMarkerHeight(90);
        locationMarker.setAngle(angle);
        return locationMarker;
    }

    /**
     * 创建定位标注
     *
     * @param groupId  楼层id
     * @param mapCoord 坐标点
     * @return
     */
    public static FMLocationMarker buildLocationMarker(int groupId, FMMapCoord mapCoord) {
        return buildLocationMarker(groupId, mapCoord, 0f);
    }



}

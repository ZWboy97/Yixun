package com.jackchance.yixun.UI.Navigation.MapUI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.fengmap.android.FMDevice;
import com.fengmap.android.analysis.navi.FMNaviAnalyser;
import com.fengmap.android.analysis.navi.FMNaviDescriptionData;
import com.fengmap.android.analysis.navi.FMNaviOption;
import com.fengmap.android.analysis.navi.FMNaviResult;
import com.fengmap.android.analysis.navi.FMSimulateNavigation;
import com.fengmap.android.analysis.search.FMSearchAnalyser;
import com.fengmap.android.analysis.search.FMSearchResult;
import com.fengmap.android.analysis.search.facility.FMSearchFacilityByTypeRequest;
import com.fengmap.android.analysis.search.model.FMSearchModelByCircleRequest;
import com.fengmap.android.data.OnFMDownloadProgressListener;
import com.fengmap.android.exception.FMObjectException;
import com.fengmap.android.map.FMMap;
import com.fengmap.android.map.FMMapUpgradeInfo;
import com.fengmap.android.map.FMMapView;
import com.fengmap.android.map.FMViewMode;
import com.fengmap.android.map.animator.FMLinearInterpolator;
import com.fengmap.android.map.event.OnFMMapInitListener;
import com.fengmap.android.map.event.OnFMSwitchGroupListener;
import com.fengmap.android.map.geometry.FMMapCoord;
import com.fengmap.android.map.layer.FMImageLayer;
import com.fengmap.android.map.layer.FMLineLayer;
import com.fengmap.android.map.layer.FMLocationLayer;
import com.fengmap.android.map.layer.FMModelLayer;
import com.fengmap.android.map.marker.FMImageMarker;
import com.fengmap.android.map.marker.FMLineMarker;
import com.fengmap.android.map.marker.FMLocationMarker;
import com.fengmap.android.map.marker.FMModel;
import com.fengmap.android.map.marker.FMSegment;
import com.fengmap.android.utils.FMMath;
import com.fengmap.android.widget.FM3DControllerButton;
import com.fengmap.android.widget.FMFloorControllerComponent;
import com.fengmap.android.widget.FMInfoWindow;
import com.fengmap.android.widget.FMNodeInfoWindow;
import com.fengmap.android.widget.FMZoomComponent;
import com.jackchance.yixun.Adapter.SearchBarAdapter;
import com.jackchance.yixun.Bean.Map;
import com.jackchance.yixun.Bean.MapCoord;
import com.jackchance.yixun.Bean.UserBean;
import com.jackchance.yixun.R;
import com.jackchance.yixun.UI.ChooseMap.MapListActivity;
import com.jackchance.yixun.UI.UserManager.LandActivity;
import com.jackchance.yixun.UI.UserManager.UserInfomation;
import com.jackchance.yixun.Util.AppManager;
import com.jackchance.yixun.Util.CommonUtils;
import com.jackchance.yixun.Util.FMLocationAPI;
import com.jackchance.yixun.Util.KeyBoardUtils;
import com.jackchance.yixun.ZAssets.Widget.SearchBar;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import cn.bmob.v3.BmobUser;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author 蚍蜉
 * @Version 2.0
 * @Description 地图导航界面的基础类，用于分解代码模块
 * created on 2017/12/26
 */
public abstract class BaseMapActivity extends AppCompatActivity implements View.OnClickListener,
        OnFMSwitchGroupListener,OnFMMapInitListener
, AdapterView.OnItemClickListener,SearchBar.OnSearchResultCallback{


    /*******地图显示控件********/
    protected ImageView navInfoImage;                     //导航路线信息指示图标
    protected FMNodeInfoWindow mInfoWindow;               //长按模型弹出窗口
    protected FMInfoWindow nextStepInfoWindow;            //导航下一步窗口
    protected View vPopWindow;                            //PopupWindow控件
    protected Button qrScanCorrectButton;                 //二维码矫正按钮
    /**********模式控制***********/
    public enum Model{
        NAVIGATIONMODEL,    //导航模式
        SEARCHMODEL,        //搜索模式
        NORMALMODEL,         //常规模式
        ARNAVIMODEL
    }
    public Model currentModel = Model.NORMALMODEL;//默认常规模式
    /*********方向控制************/
    enum Dirction{
        LEFT,       //向左
        RIGHT,      //向右
        LIFT,       //电梯
        STRAIGHT,   //直行
        END,        //目的地

    }
    /*********导航传感器优化***********/
    protected boolean isWalking = false;                    //设置行走动画是否正在进行
    protected boolean SensorAssist = true;                 //是否开启传感器辅助导航，角度正确才刷新位置
    protected boolean WalkAnimation = true;                //是否开启行走动画，否则跳跃进行
    protected boolean AutoNextStep = true;                 //是否自动进入下一步导航
    protected double MAX_BETWEEN_LENGTH = 3;  //两个点相差最大距离20米
    protected int MAP_NORMAL_LEVEL = 20;       //进入地图显示级别
    /************基础控件**********/
    protected DrawerLayout drawerLayout;                    //主界面侧滑菜单
    protected NavigationView navigationView;                //侧滑菜单栏
    protected TextView userName;                            //用户名字

    /******************************地图展示控制*********************************/
    protected FMMapView mMapView;                               //地图View
    public static FMMap mFMMap;                                 //地图
    protected String mapID;                                     //从上一个活动获得的地图id
    protected FMFloorControllerComponent mFloorComponent;       //楼层切换控件
    protected FMLocationMarker mLocationMarker;                 //定位图标
    protected Map currentMap;                                   //当前地图信息
    protected float mapRotate = 60;                             //地图旋转角度
    protected boolean isAnimateEnd = true;                      //楼层切换动画是否结束
    protected FMModelLayer modelLayer;                          //商铺模型图层
    protected FMModel mClickedModel;                            //所选择的模型
    protected FMZoomComponent mZoomComponent;                   //地图缩进控件
    protected FM3DControllerButton m3DTextButton;               //2D3D转换控件
    /*****************************导航相关数据**********************************/
    protected FMNaviAnalyser mNaviAnalyser;                     //导航分析器
    protected FMSimulateNavigation mSimulationNavigation;       //模拟导航
    protected FMNaviOption mNaviOption;                         //导航配置
    protected FMLineLayer mLineLayer;                           //线图层
    protected FMImageLayer stImageLayer;                        //起点图层
    protected FMImageLayer endImageLayer;                       //终点图层
    protected FMLocationLayer mLocationLayer;                   //定位图层
    protected FMLocationAPI mLocationAPI;                       //差值动画
    protected MapCoord currentLoc;                              //当前位置
    protected MapCoord navStartLoc;                             //导航起点位置
    protected MapCoord navEndLoc;                               //导航终点位置
    protected MapCoord navNextLoc;                              //导航下一点
    protected FMModel navStartModel;                            //起点点击模型
    protected FMModel navEndModel;                              //终点点击模型
    protected RelativeLayout bottomLayout;                      //底部商铺详细信息栏
    protected ArrayList<ArrayList<FMMapCoord>>
            mNaviPoints = new ArrayList<>();                    //导航行走点集合
    protected ArrayList<Integer>
            mNaviGroupIds = new ArrayList<>();                  //导航行走的楼层集合
    protected int mCurrentIndex = 0;                            //导航行走索引
    protected int simulateStep =0;
    /*****************************传感器相关***********************************/
    protected SensorManager sensormaneger;                      //传感器manager
    protected SensorEventListener sensorEventListener;          //传感器监听实例
    protected Sensor msenser;                                   //获取传感器实例
    protected boolean sensorState = false;                      //方向同步控制
    /****************************导航变量**************************************/
    protected ArrayList<String> mDescriptions;                  //记录导航描述message列表
    protected int navCurrentStep = 0;                           //当前导航步骤数
    protected ArrayList<FMNaviDescriptionData> datas;           //分段导航信息
    protected int fullDistance;                                 //总距离
    protected int leftDistance;                                 //剩余距离
    protected int time;                                         //剩余行走时间估算
    /****************************搜索相关**************************************/
    public static FMSearchAnalyser mSearchAnalyser;                 //搜索分析器
    protected HashMap<Integer, FMImageLayer>
            resultImageLayers = new HashMap<>();                //显示搜索目标结果
    protected SearchBar mSearchBar;                             //搜索栏实例
    protected SearchBarAdapter mSearchAdapter;                  //搜索栏提示适配器
    protected int[] mClickedIds = {R.id.btn_type_food,
            R.id.btn_type_facility, R.id.btn_type_shopping};
    protected int[] mClickedTypes = {170000, 160000, 150000};
    protected Handler mHandler = new Handler();                 //更新界面

    protected float lineWith = 1f;
    protected float offsetHigh = 2f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_map);
        //侧滑菜单的一些逻辑
        userName = (TextView)findViewById(R.id.username);
        if(BmobUser.getCurrentUser() == null){
            userName.setText("点击登陆");
        }else{
            userName.setText(BmobUser.getCurrentUser(UserBean.class).getNickname());
        }
        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BmobUser.getCurrentUser() == null){
                    Intent intent = new Intent(BaseMapActivity.this, LandActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawers();
                }else{
                    Intent intent1 = new Intent(BaseMapActivity.this, UserInfomation.class);
                    startActivity(intent1);
                    drawerLayout.closeDrawers();
                }
            }
        });
        //点击头像进入用户设置
        CircleImageView circleImageView = (CircleImageView)findViewById(R.id.icon_image);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BmobUser.getCurrentUser() == null){
                    Intent intent = new Intent(BaseMapActivity.this, LandActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawers();
                }else{
                    Intent intent1 = new Intent(BaseMapActivity.this, UserInfomation.class);
                    startActivity(intent1);
                    drawerLayout.closeDrawers();
                }
            }
        });
    }

    /**
     * 载入子类布局。
     * @param layoutId 资源id
     */
    public void setContentView(int layoutId) {
        View view = View.inflate(getBaseContext(), layoutId, null);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //将MainMapActivity布局载入进来
        RelativeLayout viewGroup = (RelativeLayout) findViewById(R.id.layout_root);
        viewGroup.addView(view, lp);
    }
    /**
     * 地图加载失败处理
     */
    @Override
    public void onMapInitFailure(String s, int i) {
        CommonUtils.showToast(BaseMapActivity.this,"地图加载失败，请检查您的网络");
        Log.e("base",s);
    }
    /**
     * 地图更新处理，加载过程中，自动检查更新
     */
    @Override
    public boolean onUpgrade(FMMapUpgradeInfo fmMapUpgradeInfo) {
        boolean isUpgrade = fmMapUpgradeInfo.isNeedUpgrade();
        if (isUpgrade) { // 有新版本更新
            // 调用更新接口，返回true，SDK内部会去加载新地图并显示
            mFMMap.upgrade(fmMapUpgradeInfo, new OnFMDownloadProgressListener() {
                @Override
                public void onCompleted(String mapPath) {
                    CommonUtils.showToast(BaseMapActivity.this,"地图更新完成!");
                }
                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    CommonUtils.showToast(BaseMapActivity.this,"正在更新地图数据！");
                }
                @Override
                public void onFailure(String mapPath, int errorCode) {
                    CommonUtils.showToast(BaseMapActivity.this,"地图更新失败，请检查网络！");
                }
            });
        }
        return false;
    }
    /**
     * 地图销毁调用
     */
    @Override
    public void onBackPressed() {
        //停止模拟轨迹动画
        if (mLocationAPI != null) {
            mLocationAPI.destroy();
        }
        //地图销毁
        if (mFMMap != null) {
            mFMMap.onDestroy();
        }
        super.onBackPressed();

    }



    /***************地图展示基础方法，为上层子类提供基础支持*****************/
    /**
     * 自定义方法，添加放大缩小组件
     */
    public void setZoomComponent(){
        //创建放大/缩小控件
        if(mZoomComponent != null)
            return;
        mZoomComponent = new FMZoomComponent(this);
        mZoomComponent.measure(0, 0);
        int width = mZoomComponent.getMeasuredWidth();
        int height = mZoomComponent.getMeasuredHeight();
        //缩放控件位置
        int offsetX = FMDevice.getDeviceWidth() - width - 10;
        int offsetY = FMDevice.getDeviceHeight() - 1200 - height;
        mMapView.addComponent(mZoomComponent, offsetX, offsetY);
        //设置放大/缩小控件
        mZoomComponent.setOnFMZoomComponentListener(new FMZoomComponent.OnFMZoomComponentListener() {
            @Override
            public void onZoomIn(View view) {
                mFMMap.setZoomLevel(mFMMap.getZoomLevel()+1,true);
            }

            @Override
            public void onZoomOut(View view) {
                mFMMap.setZoomLevel(mFMMap.getZoomLevel()-1,true);

            }
        });
    }
    /**
     * 自定义方法，添加2D，3D切换组件
     */
    public void set2D3DSwitchComponent(){
        //2D/3D控件初始化
        if(m3DTextButton != null)
            return;
        m3DTextButton = new FM3DControllerButton(this);
        //设置初始状态为3D
        m3DTextButton.initState(true);
        m3DTextButton.measure(0, 0);

        int width = m3DTextButton.getMeasuredWidth();
        //设置3D控件位置
        mMapView.addComponent(m3DTextButton, FMDevice.getDeviceWidth() - 10 - width, 330);
        //2D/3D点击监听
        m3DTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m3DTextButton.isSelected()) {
                    m3DTextButton.setSelected(false);
                    mFMMap.setFMViewMode(FMViewMode.FMVIEW_MODE_2D);
                } else {
                    m3DTextButton.setSelected(true);
                    mFMMap.setFMViewMode(FMViewMode.FMVIEW_MODE_3D);
                }
            }
        });
    }
    /**
     * 自定义方法，设置多楼层方式显示
     */
    public void setMultidisplay(){
        //多楼层显示地图
        int[] gids = mFMMap.getMapGroupIds();    //获取地图所有的group
        int focus = 0;
        mFMMap.setMultiDisplay(gids, focus, this);
    }
    /**
     * 自定义方法，设置单楼层方式显示
     */
    public void setSingledisplay(){
        //单楼层显示地图
        int[] gids = {mFMMap.getFocusGroupId()};       //获取当前地图焦点层id
        mFMMap.setMultiDisplay(gids, 0, this);
    }
    /**
     * 自定义方法,添加起点图标
     */
    public void createStartImageMarker() {
        clearStartImageLayer();
        // 添加起点图层
        stImageLayer = new FMImageLayer(mFMMap, navStartLoc.getGroupId());
        mFMMap.addLayer(stImageLayer);
        // 标注物样式
        FMImageMarker imageMarker = NaviUtil.buildImageMarker(getResources(), navStartLoc.getMapCoord(), R.drawable.start);
        imageMarker.setCustomOffsetHeight(offsetHigh);
        stImageLayer.addMarker(imageMarker);

    }
    /**
     * 自定义方法,创建终点图标
     */
    public void createEndImageMarker() {
        clearEndImageLayer();
        // 添加起点图层
        endImageLayer = new FMImageLayer(mFMMap, navEndLoc.getGroupId());
        mFMMap.addLayer(endImageLayer);
        // 标注物样式
        FMImageMarker imageMarker = NaviUtil.buildImageMarker(getResources(), navEndLoc.getMapCoord(), R.drawable.end);
        imageMarker.setCustomOffsetHeight(offsetHigh);
        endImageLayer.addMarker(imageMarker);
    }
    /**
     * 自定义方法,清除线图层
     */
    public void clearLineLayer() {
        if (mLineLayer != null) {
            mLineLayer.removeAll();
            mFMMap.removeLayer(modelLayer);
        }
    }
    /**
     * 清除起点图层
     */
    void clearStartImageLayer() {
        if (stImageLayer != null) {
            stImageLayer.removeAll();
            mFMMap.removeLayer(stImageLayer); // 移除图层
            stImageLayer = null;
        }
    }
    /**
     * 清除终点图层
     */
    void clearEndImageLayer() {
        if (endImageLayer != null) {
            endImageLayer.removeAll();
            mFMMap.removeLayer(endImageLayer); // 移除图层
            endImageLayer = null;
        }
    }
    /**
     * 清除导航下一步图层
     */
    void clearNextStepLayer(){
        if(nextStepInfoWindow == null)
            return;
        if(nextStepInfoWindow.isOpened()){
            nextStepInfoWindow.close();
        }
    }
    /**
     * 清除定位图层
     */
    void clearLocationLayer(){
        if(mLocationLayer != null){
            mLocationLayer.removeAll();
            mFMMap.removeLayer(mLocationLayer);
            mLocationLayer = null;
        }
    }
    /**
     * 自定义方法--消除当前啊导航记忆
     */
    public void clearBeforeFinish(){
        clearLineLayer();
        clearNextStepLayer();
        clearResultMarker();
        clearEndImageLayer();
        clearResultMarker();
        clearStartImageLayer();
        clearWalkPoints();
    }

    /***************地图导航基础方法，为上层子类提供导航基础支持*****************/
    /**自定义方法，初始化导航
     */
    public void initNavigation(String path){
        //添加导航线图层
        mLineLayer = mFMMap.getFMLayerProxy().getFMLineLayer();
        //获取导航分析
        try {
            mNaviAnalyser = FMNaviAnalyser.getFMNaviAnalyserByPath(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (FMObjectException e) {
            e.printStackTrace();
        }
    }
    /**自定义方法，获取导航描述
     */
    public void getRouteDescription() {
        if(mDescriptions == null){
            mDescriptions = new ArrayList<>();
        }else{
            mDescriptions.clear();
        }
        mDescriptions.addAll(mNaviAnalyser.getNaviDescription());
        //获取分段导航数据
        if(datas == null){
            datas = new ArrayList<>();
        }else
            datas.clear();
        datas.addAll(mNaviAnalyser.getNaviDescriptionData());
        //行走总距离
        fullDistance = (int)mNaviAnalyser.getSceneRouteLength();
        leftDistance = fullDistance;
        fillWalkPoints();


    }

    /**自定义方法，填充导航路线点
     */
    public void fillWalkPoints() {
        clearWalkPoints();
        //获取路径导航分析结果
        ArrayList<FMNaviResult> results = mNaviAnalyser.getNaviResults();
        int focusGroupId = Integer.MIN_VALUE;
        for (FMNaviResult r : results) {
            //获取某一结果的楼层id
            int groupId = r.getGroupId();
            //获取导航路径点集合
            ArrayList<FMMapCoord> points = r.getPointList();
            //点数据小于2，则为单个数据集合
            if (points.size() < 2) {
                continue;
            }
            //判断是否为同层导航数据，非同层数据即其他层数据
            if (focusGroupId == Integer.MIN_VALUE || focusGroupId != groupId) {
                focusGroupId = groupId;
                //添加即将行走的楼层与点集合
                mNaviGroupIds.add(groupId);
                mNaviPoints.add(points);
            } else {
                mNaviPoints.get(mNaviPoints.size() - 1).addAll(points);
            }
        }
    }

    /**自定义方法，清空导航点
     */
    void clearWalkPoints() {
        navCurrentStep = 0;
        mNaviPoints.clear();
        mNaviGroupIds.clear();
    }

    /**自定义方法---导航前在起点终点间添加路线
     */
    public void addLineMarker() {
        ArrayList<FMNaviResult> results = mNaviAnalyser.getNaviResults();
        // 填充导航数据
        ArrayList<FMSegment> segments = new ArrayList<>();
        for (FMNaviResult r : results) {
            int groupId = r.getGroupId();
            FMSegment s = new FMSegment(groupId, r.getPointList());
            segments.add(s);
        }
        //添加LineMarker
        FMLineMarker lineMarker = new FMLineMarker(segments);
        lineMarker.setLineWidth(lineWith);

        //lineMarker.setLineMode(FMLineMarker.FMLineMode.FMLINE_CIRCLE);
        mLineLayer.addMarker(lineMarker);
        mFMMap.addLayer(mLineLayer);
    }

    /**自定义方法，导航过程中显示下一步标记
     */
    public void addNextStepMaker(){
        if(nextStepInfoWindow == null){
            nextStepInfoWindow = new FMInfoWindow(mMapView, R.layout.layout_next_step_window);
        }
        //如果已经有打开的窗口，那么先关闭它
        clearNextStepLayer();
        nextStepInfoWindow.setPosition(navNextLoc.getGroupId(),navNextLoc.getMapCoord());
        nextStepInfoWindow.computeWindowSize();
        nextStepInfoWindow.open();


    }

    /**自定义方法，更新方向标记
     * @param data 导航描述数据
     */
    public void updateDirctionPic(FMNaviDescriptionData data){
        Dirction endDirction = Dirction.LEFT;
        String dirction = data.getEndDirection();
        if(dirction==null){
            navInfoImage.setImageResource(R.drawable.straignt);
            return;
        }else if(dirction.indexOf("右") >= 0){
            endDirction = Dirction.RIGHT;
        }else if(dirction.indexOf("左") >= 0){
            endDirction = Dirction.LEFT;
        }else if(dirction.indexOf("电梯") >= 0){
            endDirction = Dirction.LIFT;
        }else if(dirction.indexOf("目的地") >= 0){
            endDirction = Dirction.END;
        }else if(dirction.indexOf("直") >=0){
            endDirction = Dirction.STRAIGHT;
        }
        switch (endDirction){
            case LEFT:
                navInfoImage.setImageResource(R.drawable.turn_left);
                break;
            case LIFT:
                navInfoImage.setImageResource(R.drawable.lift);
                break;
            case RIGHT:
                navInfoImage.setImageResource(R.drawable.turn_right);
                break;
            case END:
                navInfoImage.setImageResource(R.drawable.arrive);
                break;
            case STRAIGHT:
                navInfoImage.setImageResource(R.drawable.straignt);
                break;
            default:
                navInfoImage.setImageResource(R.drawable.straignt);
                break;
        }
    }

    /**自定义方法，基于当前位置坐标点，显示定位图标
     */
    public void UpdateLocateInMap(){
        if(mLocationMarker == null){
            mLocationMarker = NaviUtil.buildLocationMarker(currentLoc.getGroupId(), currentLoc.getMapCoord());
            mLocationLayer = mFMMap.getFMLayerProxy().getFMLocationLayer();
            mLocationLayer.addMarker(mLocationMarker);
            mFMMap.addLayer(mLocationLayer);
        }

        mLocationMarker.setVisible(true);
        clearResultMarker();
        //添加图片
//        FMImageMarker imageMarker = NaviUtil.buildImageMarker(getResources(), currentLoc.getMapCoord(), R.drawable.search_result);
//        imageMarker.setFMImageMarkerOffsetMode(FMImageMarker.FMImageMarkerOffsetMode.FMNODE_CUSTOM_HEIGHT);
//        imageMarker.setCustomOffsetHeight(1);
//        resultImageLayers.get(currentLoc.getGroupId()).addMarker(imageMarker);
        switchFloor(currentLoc.getGroupId(), new Runnable() {
            @Override
            public void run() {
                mFMMap.setZoomLevel(MAP_NORMAL_LEVEL,true);
                //更新定位点位置和方向
                float angle = 0;
                mLocationMarker.updateAngleAndPosition(currentLoc.getGroupId(),angle, currentLoc.getMapCoord());
                mFMMap.moveToCenter(currentLoc.getMapCoord(), true);
            }
        });
    }
    /**自定义方法，基于坐标和角度，更新定位点显示
     * @param mapCoord
     * @param angle
     */
    public void updateLocationMarker(FMMapCoord mapCoord, double angle){
        if(mLocationLayer == null){
            mLocationLayer = mFMMap.getFMLayerProxy().getFMLocationLayer();
            mFMMap.addLayer(mLocationLayer);
        }
        if (mLocationMarker == null) {
            mLocationMarker = NaviUtil.buildLocationMarker(mFMMap.getFocusGroupId(), mapCoord);
            mLocationLayer.addMarker(mLocationMarker);
        }
        //更新定位点位置和方向
        mLocationMarker.updateAngleAndPosition((float) angle, mapCoord);

//        clearResultMarker();
//        FMImageMarker imageMarker = NaviUtil.buildImageMarker(getResources(), mapCoord, R.drawable.search_result);
//        imageMarker.setFMImageMarkerOffsetMode(FMImageMarker.FMImageMarkerOffsetMode.FMNODE_CUSTOM_HEIGHT);
//        imageMarker.setCustomOffsetHeight(2);
//        resultImageLayers.get(mFMMap.getFocusGroupId()).addMarker(imageMarker);
        if (angle != 0 && sensorState==false) {
            animateRotate((float) -angle);
        }
        moveToCenter(mapCoord);
    }
    /**切换楼层
     * @param groupId 楼层id
     */
    void switchFloor(final int groupId, final Runnable runnable) {
        if (groupId != mFMMap.getFocusGroupId()) {
            mFMMap.setFocusByGroupIdAnimated(groupId, new FMLinearInterpolator(), new OnFMSwitchGroupListener() {
                @Override
                public void beforeGroupChanged() {
                    //改变控件楼层文字
                    int groupSize = mFMMap.getFMMapInfo().getGroupSize();
                    int position = groupSize - groupId;
                }
                @Override
                public void afterGroupChanged() {
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            });
        } else {
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    /**自定义方法，获取即将行走的下一层groupId
     * @return 即将行走的层id
     */
    int getWillWalkingGroupId() {
        if (navCurrentStep > mNaviGroupIds.size() - 1) {
            return mFMMap.getFocusGroupId();
        } else {
            return mNaviGroupIds.get(navCurrentStep);
        }
    }

    /**获取即将行走的下一层点集合
     * @return 层集合
     */
    ArrayList<FMMapCoord> getWillWalkingPoints() {
        if (navCurrentStep > mNaviGroupIds.size() - 1) {
            return null;
        }
        return mNaviPoints.get(navCurrentStep++);
    }

    /**切换楼层行走
     * @param groupId 层id
     */
    void setFocusGroupId(int groupId) {
        if (groupId != mFMMap.getFocusGroupId()) {
            mFMMap.setFocusByGroupId(groupId, null);
            //更新楼层控
        }
        setupTargetLine(groupId);
    }

    /**安装行走路径线
     * @param groupId 层id
     */
    void setupTargetLine(int groupId) {
        ArrayList<FMMapCoord> points = getWillWalkingPoints();
        mLocationAPI.setupTargetLine(points, groupId);
        mLocationAPI.start();
    }

    /**动画旋转
     * @param angle 角度
     */
    void animateRotate(final float angle) {
        if (Math.abs(mFMMap.getRotateAngle() - angle) > 2) {
            mFMMap.setRotateAngle(angle);
        }
    }
    /**移动至中心点,如果中心与屏幕中心点距离大于20米，将移动
     * @param mapCoord
     */
    void moveToCenter(final FMMapCoord mapCoord) {
        FMMapCoord centerCoord = mFMMap.getMapCenter();
        double length = FMMath.length(centerCoord, mapCoord);
        if (length > MAX_BETWEEN_LENGTH) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mFMMap.moveToCenter(mapCoord, true);
                }
            });
        }
    }
    /**自定义方法，判断是否到达终点
     * @return 是否结束
     */
    public boolean isWalkComplete() {
        if (navCurrentStep > mNaviGroupIds.size() - 1) {
            return true;
        }
        return false;
    }

    /**自定义方法，清楚搜索结果标注
     */
    public void clearResultMarker() {
        if(resultImageLayers.isEmpty())
            return;
        for (FMImageLayer imageLayer : resultImageLayers.values()) {
            imageLayer.removeAll();
        }
    }


    /************************地图检索***********************/
    /**基于位置坐标，就近转化为对应商铺
     * @param mapCoord 地图坐标
     * @return
     */
    protected FMModel getModelFromMapCoord(MapCoord mapCoord){
        FMSearchModelByCircleRequest request = new FMSearchModelByCircleRequest(
                mapCoord.getGroupId(),mapCoord.getMapCoord() , 10);
        ArrayList<FMSearchResult> result = mSearchAnalyser.executeFMSearchRequest(request);
        //对返回集合遍历获取相对应模型信息
        ArrayList<FMModel> models = new ArrayList<FMModel>();
        for (FMSearchResult r : result) {
            String fid = (String) r.get("FID"); //获取fid，在通过fid获取模型对象
            FMModel model = mFMMap.getFMLayerProxy().queryFMModelByFid(fid);
            models.add(model);
        }
        if(!models.isEmpty())
            return models.get(0);
        else
            return null;
    }
    /**
     * 初始化搜索分析器
     */
    public void initSearch(){
        //搜索分析
        try {
            mSearchAnalyser = FMSearchAnalyser.getFMSearchAnalyserById(mapID);
        } catch (FMObjectException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    //自定义方法，通过关键字检索，返回模型集合
    ArrayList<FMModel> queryModelByKeyword(String keyword) {
        //查询楼层集合
        int[] groupIds = {mFMMap.getFocusGroupId()};
        //搜索请求
        return NaviUtil.queryModelByKeyword(mFMMap, groupIds, mSearchAnalyser, keyword);
    }
    //搜索输入提示item点击逻辑
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //关闭软键盘
        KeyBoardUtils.closeKeybord(mSearchBar.getCompleteText(), BaseMapActivity.this);
        //获得model
        FMModel model = (FMModel) parent.getItemAtPosition(position);
        //切换楼层
        int groupId = model.getGroupId();
        if (groupId != mFMMap.getFocusGroupId()) {
            mFMMap.setFocusByGroupId(groupId, this);
            mFloorComponent.setSelected(groupId);
        }
        //移动至中心点
        FMMapCoord mapCoord = model.getCenterMapCoord();
        mFMMap.setZoomLevel(20,true);
        mFMMap.moveToCenter(mapCoord, true);
        //清除原有显示
        clearResultMarker();
        //添加图片
        FMImageMarker imageMarker = NaviUtil.buildImageMarker(getResources(), mapCoord, R.drawable.search_result);
        imageMarker.setFMImageMarkerOffsetMode(FMImageMarker.FMImageMarkerOffsetMode.FMNODE_CUSTOM_HEIGHT);
        imageMarker.setCustomOffsetHeight(5f);
        resultImageLayers.get(model.getGroupId()).addMarker(imageMarker);
        clearFocus(model);
    }
    //执行搜索操作
    @Override
    public void onSearchCallback(String keyword) {
        //地图未显示前，不执行搜索事件
        boolean isCompleted = mFMMap.getMapFirstRenderCompleted();
        if (!isCompleted) {
            return;
        }
        ArrayList<FMModel> models = NaviUtil.queryModelByKeyword(mFMMap, mSearchAnalyser, keyword);
        if (mSearchAdapter == null) {
            mSearchAdapter = new SearchBarAdapter(BaseMapActivity.this, models);
            mSearchBar.setAdapter(mSearchAdapter);
        } else {
            mSearchAdapter.setDatas(models);
            mSearchAdapter.notifyDataSetChanged();
        }
    }
    //自定义方法，清楚模型聚焦
    void clearFocus(FMModel model) {
        if (!model.equals(mClickedModel)) {
            if (mClickedModel != null) {
                mClickedModel.setSelected(false);
            }
            this.mClickedModel = model;
            this.mClickedModel.setSelected(true);
        }
    }
    /**
     * 展示图片标注
     *
     * @param coords 图片标注点集合
     */
    protected void displayImageMarkers(ArrayList<FMMapCoord> coords) {
        clearResultMarker();
        int groupId = mFMMap.getFocusGroupId();
        //展示图片标注
        for (FMMapCoord mapCoord : coords) {
            //添加图片
            FMImageMarker imageMarker = NaviUtil.buildImageMarker(getResources(),
                    mapCoord, R.drawable.ic_marker_blue);
            imageMarker.setFMImageMarkerOffsetMode(FMImageMarker.FMImageMarkerOffsetMode.FMNODE_CUSTOM_HEIGHT);
            imageMarker.setCustomOffsetHeight(5f);
            resultImageLayers.get(groupId).addMarker(imageMarker);
        }
    }
    /**
     * 公共设施搜索
     *
     * @param type 类型值
     * @return
     */
    protected ArrayList<FMMapCoord> queryFacilityByType(int type) {
        ArrayList<FMMapCoord> list = new ArrayList<FMMapCoord>();
        int[] groupIds = {mFMMap.getFocusGroupId()};
        //对公共设施类型执行搜索
        FMSearchFacilityByTypeRequest request = new FMSearchFacilityByTypeRequest(groupIds, type);
        ArrayList<FMSearchResult> result = mSearchAnalyser.executeFMSearchRequest(request);
        //返回值获取坐标
        for (FMSearchResult r : result) {
            int eid = (int) r.get("eid");           //获取公共设施的eid
            int groupId = (int) r.get("groupId");   //获取公共设施的groupId
            FMMapCoord mapCoord = mSearchAnalyser.getFacilityCoord(groupId, eid);
            if (mapCoord != null) {
                list.add(mapCoord);
            }
        }
        return list;
    }

    /**
     * 点击分类图标
     * @param v
     */
    @Override
    public void onClick(View v) {
        //地图未显示前，不执行搜索事件
        boolean isCompleted = mFMMap.getMapFirstRenderCompleted();
        if (!isCompleted) {
            return;
        }
        int type = (int) v.getTag();
        ArrayList<FMMapCoord> coords = queryFacilityByType(type);
        displayImageMarkers(coords);
    }

    /**
     * 退出APP
     * @param context 上下文
     */
    public void QuitOutAPP(final Context context){
        MaterialDialog.Builder quitoutBuilder = new MaterialDialog.Builder(context)
                .title("应用提示")
                .theme(Theme.LIGHT)
                .content("确定退出易寻导航系统?")
                .positiveText("确定").onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                        AppManager.getInstance().AppExit(context);
                        finish();
                    }
                })
                .negativeText("取消")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                    }
                });
        MaterialDialog quitoutDialog = quitoutBuilder.build();
        quitoutDialog.show();
    }

    public static FMMap getFMMap(){
        return mFMMap;
    }

    public static FMSearchAnalyser getSearchAnalyser(){
        return mSearchAnalyser;
    }

}

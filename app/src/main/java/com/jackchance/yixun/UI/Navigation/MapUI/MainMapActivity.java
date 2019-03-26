package com.jackchance.yixun.UI.Navigation.MapUI;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.fengmap.android.analysis.navi.FMNaviAnalyser;
import com.fengmap.android.analysis.navi.FMNaviDescriptionData;
import com.fengmap.android.analysis.navi.FMNaviOption;
import com.fengmap.android.analysis.navi.FMNaviResult;
import com.fengmap.android.analysis.navi.FMNavigationInfo;
import com.fengmap.android.analysis.navi.FMPointOption;
import com.fengmap.android.analysis.navi.FMSimulateNavigation;
import com.fengmap.android.analysis.navi.OnFMNavigationListener;
import com.fengmap.android.analysis.search.FMSearchResult;
import com.fengmap.android.analysis.search.model.FMSearchModelByCircleRequest;
import com.fengmap.android.map.FMMapView;
import com.fengmap.android.map.event.OnFMCompassListener;
import com.fengmap.android.map.event.OnFMMapInitListener;
import com.fengmap.android.map.event.OnFMNodeListener;
import com.fengmap.android.map.event.OnFMSwitchGroupListener;
import com.fengmap.android.map.geometry.FMGeoCoord;
import com.fengmap.android.map.geometry.FMMapCoord;
import com.fengmap.android.map.layer.FMLocationLayer;
import com.fengmap.android.map.marker.FMLocationMarker;
import com.fengmap.android.map.marker.FMModel;
import com.fengmap.android.map.marker.FMNode;
import com.fengmap.android.utils.FMMath;
import com.fengmap.android.widget.FMFloorControllerComponent;
import com.fengmap.android.widget.FMNodeInfoWindow;
import com.jackchance.yixun.Bean.Map;
import com.jackchance.yixun.Bean.MapCoord;
import com.jackchance.yixun.Bean.QRInfo;
import com.jackchance.yixun.Bean.UserBean;
import com.jackchance.yixun.Constants;
import com.jackchance.yixun.R;
import com.jackchance.yixun.UI.ARNavi.ARNaviActivity;
import com.jackchance.yixun.UI.BroadcastMessage.BroadcastMessageListActivity;
import com.jackchance.yixun.UI.FavouriteLoction.LoctionListActivity;
import com.jackchance.yixun.UI.UserManager.LandActivity;
import com.jackchance.yixun.Util.QRResultUtil;

import com.jackchance.yixun.UI.ChooseMap.MapListActivity;
import com.jackchance.yixun.UI.QRScan.QRCodeActivity;
import com.jackchance.yixun.UI.UserManager.UserInfomation;
import com.jackchance.yixun.Util.AppManager;
import com.jackchance.yixun.Util.CommonUtils;
import com.jackchance.yixun.Util.FMLocationAPI;
import com.jackchance.yixun.ZAssets.ARCode.GLView;
import com.jackchance.yixun.ZAssets.Widget.ImageViewCheckBox;
import com.jackchance.yixun.ZAssets.Widget.SearchBar;
import com.jackchance.yixun.YiXunAPP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.easyar.Engine;
import de.hdodenhof.circleimageview.CircleImageView;


public class MainMapActivity extends BaseMapActivity implements OnFMMapInitListener
        ,OnFMSwitchGroupListener{

    public final String SELECT_Modle = "select_Loc";
    /*******************************界面控件*****************************/
    private TextView messageView;                       //地图文字消息message
    private TextView navMessage;                        //地图导航提示信息
    private RelativeLayout navFromToLayout;             //导航表单布局
    private EditText navStartPointEditText;             //导航起点文本
    private EditText navEndPointEditText;               //导航终点文本
    private Button navConfirmButton;                    //导航确认按钮
    private Button navNextStepButton;                   //导航下一步按钮
    private Button navPreStepButton;                    //导航上一步按钮
    private ImageViewCheckBox navLayoutButton;          //导航表单显示触发开关
    private ImageViewCheckBox locateButton;             //点击后地图聚焦当前定位
    private ImageViewCheckBox followButton;             //设置导航是否视野跟随
    private TextView nextStoreFront;                    //导航前方的商店名称
    private TextView leftDistanceText;                  //导航剩余距离
    private RelativeLayout normalModelLayout;            //常规模式界面
    private RelativeLayout navModelLayout;              //导航控制界面布局
    private RelativeLayout searchModelLayout;           //搜索界面布局
    private LinearLayout moreInfoButton;                //进入业务详情按钮
    private LinearLayout starCurModelButton;            //收藏当前商铺按钮
    private LinearLayout naviToHereButton;              //导航到当前商铺按钮
    private RelativeLayout nearbyLayout;                //周边选择布局展示
    private ImageButton quitNavButton;                  //退出导航模式
    private ImageView starImageView;                    //收藏图标
    private CircleImageView userImageToolBar;           //标题栏上的用户按钮
    private ImageButton naviSettingButton;              //导航设置界面按钮
    private ImageButton startArNaviButton;              //Ar导航按钮
    private FloatingActionButton qrScanButton;          //启动二维码扫描
    private Toolbar mtoolBar;                           //标题栏
    private LinearLayout broadcastButton;               //点击查看消息通知的按钮
    /********************************搜索********************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main_map);
        mtoolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mtoolBar);
        /******************获取地图和定位信息******************/
        currentMap = new Map();
        currentLoc = new MapCoord();
        SharedPreferences pref = YiXunAPP.mSharedPreferences;
        mapID = pref.getString(Constants.String_MapId,"0000");
        currentMap.setMapId(mapID);
        currentMap.setName(pref.getString(Constants.String_MapName,"未知"));
        currentMap.setThemeid(pref.getString(Constants.String_ThemeId,"3007"));
        int groupid = pref.getInt(Constants.String_GroupId,1);
        currentLoc.setGroupId(groupid);
        FMMapCoord mapCoord = new FMMapCoord();
        mapCoord.x = pref.getFloat(Constants.String_X,0);
        mapCoord.y = pref.getFloat(Constants.String_Y,0);
        currentLoc.setMapCoord(mapCoord);

        /****************加载地图，并进行相应配置******************/
        mMapView = (FMMapView)findViewById(R.id.map_view);
        mFMMap = mMapView.getFMMap();
        mFMMap.openMapById(mapID,true);
        mFMMap.setOnFMMapInitListener(this);        //设置Map初始化监听
        //设置差值动画监听
        mLocationAPI = new FMLocationAPI();         //差值动画
        initialize();                               //完成activity的初始化操作
        setWidgetClickListener();                   //控件事件监听逻辑

        /*****************EASYAR引擎初始化********************/
        //Engine.initialize(this, Constants.EasyAR_KEY);

        /****************初始化对象****************************/
        mFloorComponent = new FMFloorControllerComponent(this);

    }
    @Override
    public void onBackPressed() {
        //几种模式下的退出方式
        if(currentModel == Model.NAVIGATIONMODEL){
            quitNaviModel();
        }else {
            clearBeforeFinish();
            if(sensormaneger != null){
                sensormaneger.unregisterListener(sensorEventListener);
            }
            super.onBackPressed();
        }
    }

    /**
     *实现OnMapInitlistrner的接口
     */
    @Override
    public void onMapInitSuccess(String s) {
        mFMMap.loadThemeById(currentMap.getThemeid());
        //设置多楼层显示
        setMultidisplay();
        //模型点击逻辑--自定义方法
        setMapModelClickListener();
        //添加楼层切换组件
        //setFloorControlComponent();
        //添加缩放控件
        setZoomComponent();
        //添加2D3D切换控制部件
        set2D3DSwitchComponent();
        //初始化导航
        initNavigation(s);
        //初始化搜索分析
        initSearch();
        //初始化UI模式
        updateCurrentUIModel();
        if(currentLoc.getGroupId() != -1){
            UpdateLocateInMap();
        }
        //设置点击事件
        for (int i = 0; i < mClickedIds.length; i++) {
            View view = (View)findViewById(mClickedIds[i]);
            view.setTag(mClickedTypes[i]);
            view.setOnClickListener(this);
        }
        mFMMap.setOnFMCompassListener(new OnFMCompassListener() {
            @Override       //是否同步更新
            public void onCompassClick() {
                if(sensorState == false){
                    sensorState = true;
                    Toast.makeText(MainMapActivity.this,"方向同步已开启",Toast.LENGTH_SHORT).show();
                }else {
                    sensorState = false;
                    //恢复为初始状态
                    mFMMap.resetCompassToNorth();
                    Toast.makeText(MainMapActivity.this,"方向同步已经关闭",Toast.LENGTH_SHORT).show();
                }
            }
        });
        // 创建模拟导航对象
        mSimulationNavigation = new FMSimulateNavigation(mFMMap);
        // 创建导航配置对象
        mNaviOption = new FMNaviOption();
        // 设置跟随模式，默认跟随
        mNaviOption.setFollowPosition(true);
        // 设置跟随角度（第一人视角），默认跟随
        mNaviOption.setFollowAngle(true);
        // 点移距离视图中心点超过最大距离5米，就会触发移动动画；若设为0，则实时居中
        mNaviOption.setNeedMoveToCenterMaxDistance(5);
        // 设置导航开始时的缩放级别，true: 导航结束时恢复开始前的缩放级别，false：保持现状
        mNaviOption.setZoomLevel(20, false);
        // 设置配置
        mSimulationNavigation.setNaviOption(mNaviOption);
    }

    /**
     * initialize，完成一些初始化
     */
    void initialize(){
        /*********************获取控件实例**********************/
        messageView = (TextView)findViewById(R.id.mainmap_message);
        bottomLayout = (RelativeLayout)findViewById(R.id.mainmap_bottom_layout);
        navFromToLayout = (RelativeLayout)findViewById(R.id.navigate_buttom_layout);
        navStartPointEditText = (EditText) findViewById(R.id.nav_startpoint_edittext);
        navEndPointEditText = (EditText) findViewById(R.id.nav_endpoint_edittext);
        navConfirmButton = (Button)findViewById(R.id.nav_confirm_button);
        locateButton = (ImageViewCheckBox) findViewById(R.id.btn_locate);
        followButton = (ImageViewCheckBox)findViewById(R.id.btn_follow);
        navNextStepButton = (Button) findViewById(R.id.nav_next_step_btn);
        navPreStepButton = (Button)findViewById(R.id.nav_pre_step_btn);
        navModelLayout = (RelativeLayout)findViewById(R.id.mainmap_nav_control_layout);
        normalModelLayout = (RelativeLayout)findViewById(R.id.mainmap_normal_layout);
        navMessage = (TextView)findViewById(R.id.nav_message);
        nextStoreFront = (TextView)findViewById(R.id.nav_store_front);
        searchModelLayout = (RelativeLayout)findViewById(R.id.mainmap_search_layout);
        leftDistanceText = (TextView)findViewById(R.id.nav_left_message);
        navInfoImage = (ImageView)findViewById(R.id.navi_image);
        quitNavButton = (ImageButton)findViewById(R.id.quit_nav_btn);
        moreInfoButton = (LinearLayout)findViewById(R.id.select_model_moreinfo);
        //starCurModelButton = (LinearLayout)findViewById(R.id.select_model_star_here);
        naviToHereButton = (LinearLayout)findViewById(R.id.select_model_navihere);
        nearbyLayout = (RelativeLayout)findViewById(R.id.nearby_layout);
        //starImageView = (ImageView)findViewById(R.id.star_imageview);
        userImageToolBar = (CircleImageView)findViewById(R.id.user_image);
        naviSettingButton = (ImageButton)findViewById(R.id.navi_setting_btn);
        startArNaviButton = (ImageButton)findViewById(R.id.ar_nav_btn);
        qrScanCorrectButton = (Button)findViewById(R.id.nav_qr_scan);
        mSearchBar = (SearchBar) findViewById(R.id.mainmap_search_bar);
        qrScanButton = (FloatingActionButton)findViewById(R.id.btn_qr_start);
        mtoolBar = (Toolbar)findViewById(R.id.toolbar);
        broadcastButton = (LinearLayout)findViewById(R.id.layout_broadcast);
        //搜索框
        mSearchBar = (SearchBar)findViewById(R.id.mainmap_search_bar);
        mSearchBar.setOnSearchResultCallback(this);
        mSearchBar.setOnItemClickListener(this);
        /*********************初始化一些变量***********************/
        if(currentLoc == null){
            currentLoc = new MapCoord(-1,new FMMapCoord());     //初始化定位点
            navStartLoc = new MapCoord();                       //初始化起点
            navEndLoc = new MapCoord();                         //初始化终点
            navNextLoc = new MapCoord();                        //初始化导航下一步位置
        }else {
            navStartLoc = new MapCoord(currentLoc.getGroupId(),currentLoc.getMapCoord());
            navEndLoc = new MapCoord(currentLoc.getGroupId(),currentLoc.getMapCoord());
            navNextLoc = new MapCoord(currentLoc.getGroupId(),currentLoc.getMapCoord());
        }
        messageView.setText("欢迎光临"+currentMap.getName());
        /**************************其他的初始化********************/
        //初始化方向传感器
        InitSenser();
        //初始化AR
        //initARNavigation();
    }
    /**
     * 自定义方法，添加控件点击逻辑
     */
    void setWidgetClickListener(){
        /********************侧滑菜单按钮逻辑****************************/
        //初始化关闭手势开启模式
        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        navigationView = (NavigationView) findViewById(R.id.menu_view_left);
        navigationView.setCheckedItem(R.id.nav_normal_model);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_normal_model: //常规模式
                        currentModel = Model.NORMALMODEL;
                        updateCurrentUIModel();
                        break;
                    case R.id.nav_search:       //搜索模式
                        currentModel = Model.SEARCHMODEL;
                        updateCurrentUIModel();
                        break;
                    case R.id.nav_mapswitch:    //切换地图
                        //弹窗显示
                        MaterialDialog.Builder quitoutBuilder = new MaterialDialog.Builder(MainMapActivity.this)
                                .title("应用提示")
                                .theme(Theme.LIGHT)
                                .content("确定切换地图吗?")
                                .positiveText("确定").onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        dialog.dismiss();
                                        onBackPressed();
                                        Intent intent = new Intent(MainMapActivity.this,
                                                MapListActivity.class);
                                        startActivity(intent);
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
                        break;
                    case R.id.nav_settings:     //进入设置界面
                        break;
                    case R.id.nav_landout:      //注销登陆
                        AppManager.LandOut(MainMapActivity.this);
                        break;
                    case R.id.nav_quitout:      //退出应用
                        QuitOutAPP(MainMapActivity.this);
                        break;
                    case R.id.nav_userinfo:
                        if(BmobUser.getCurrentUser() == null){
                            Intent intent = new Intent(MainMapActivity.this, LandActivity.class);
                            startActivity(intent);
                            drawerLayout.closeDrawers();
                        }else{
                            Intent intent1 = new Intent(MainMapActivity.this, UserInfomation.class);
                            startActivity(intent1);
                            drawerLayout.closeDrawers();
                        }
                        break;
                    default:
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });

        /**********************导航表单层开关**************************/
        navLayoutButton = (ImageViewCheckBox) findViewById(R.id.btn_navigate);
        navLayoutButton.setOnCheckStateChangedListener(new ImageViewCheckBox.OnCheckStateChangedListener() {
            @Override
            public void onCheckStateChanged(View view, boolean isChecked) {
                if(isChecked){
                    navFromToLayout.setVisibility(View.VISIBLE);
                    bottomLayout.setVisibility(View.GONE);
                    if(navStartModel != null){
                        navStartPointEditText.setText(navStartModel.getName().toString());
                    }else{
                        navStartPointEditText.setFocusable(true);
                        navStartPointEditText.requestFocus();
                    }
                    if(navEndModel != null){
                        navEndPointEditText.setText(navEndModel.getName().toString());
                    }
                }else{
                    navFromToLayout.setVisibility(View.GONE);
                    navStartPointEditText.setText("");
                    navEndPointEditText.setText("");
                }
            }
        });
        /******************确认导航按钮逻辑***************************/
        navConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(navEndPointEditText.getText().toString().isEmpty()){
                    CommonUtils.showToast(MainMapActivity.this,"请输入导航终点");
                    return;
                }
                if(navStartPointEditText.getText().toString().isEmpty()){
                    CommonUtils.showToast(MainMapActivity.this,"请输入导航起点");
                    return;
                }
                //进行导航分析
                int type = mNaviAnalyser.analyzeNavi(navStartLoc.getGroupId(), navStartLoc.getMapCoord(), navEndLoc.getGroupId(), navEndLoc.getMapCoord(),
                        FMNaviAnalyser.FMNaviModule.MODULE_SHORTEST);//基于最短路径原则
                if (type == FMNaviAnalyser.FMRouteCalcuResult.ROUTE_SUCCESS) {
                    addLineMarker();            //添加线图层
                    getRouteDescription();      //获取路径描述
                    navCurrentStep = 0;         //导航步骤初始化
                    updateNavigationInfo();
                    currentModel = Model.NAVIGATIONMODEL;
                    updateCurrentUIModel();     //界面切换为导航模式
                    navLayoutButton.setStateChanged();//导航表单按钮恢复

                }
            }
        });
        /************************点击起点选择弹窗***************************/
        navStartPointEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseStartLoc();
            }
        });
        /************************点击起点选择弹窗***************************/
        navEndPointEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseEndLoc();
            }
        });
        /*******************定位显示到当前坐标按钮*************************/
        locateButton.setOnCheckStateChangedListener(new ImageViewCheckBox.OnCheckStateChangedListener() {
            @Override
            public void onCheckStateChanged(View view, boolean isChecked) {
                UpdateLocateInMap();  //定位聚焦显示当前位置。
                //填写导航表单时候，点击聚焦选择当前位置为起点
                if(navStartPointEditText.isFocused()){
                    navStartLoc = currentLoc;
                    navStartPointEditText.setText("当前位置");
                }
            }
        });
        /***********************启动二维码扫描定位**********************/
        qrScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMapActivity.this,QRCodeActivity.class);
                QRCodeActivity.setStartModel(QRCodeActivity.StartModel.QRForMoreInfo);
                startActivityForResult(intent,2);    //request码为2，表示扫描的二维码有多用途
            }
        });
        /************************导航下一步按钮************************/
        navNextStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检查是否到达终点
                if(navCurrentStep == mDescriptions.size()-1){
                    currentLoc = NaviUtil.getNextCoord(navCurrentStep,mNaviAnalyser);
                    UpdateLocateInMap();
                    CommonUtils.showToast(MainMapActivity.this,"您已经到达终点");
                    return;
                }
                navCurrentStep++;
                if(NaviUtil.getCurrentCoord(navCurrentStep,mNaviAnalyser).getGroupId() !=
                        NaviUtil.getNextCoord(navCurrentStep,mNaviAnalyser).getGroupId()){
                    navCurrentStep++;
                }
                updateNavigationInfo();     //更新当前导航位置
            }
        });
        /************************导航上一步按钮************************/
        navPreStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检查是否到达终点
                if(navCurrentStep == 0){
                    currentLoc = NaviUtil.getCurrentCoord(navCurrentStep,mNaviAnalyser);
                    UpdateLocateInMap();
                    CommonUtils.showToast(MainMapActivity.this,"您已经到达起点");
                    return;
                }
                navCurrentStep--;
                if(NaviUtil.getCurrentCoord(navCurrentStep,mNaviAnalyser).getGroupId() !=
                        NaviUtil.getNextCoord(navCurrentStep,mNaviAnalyser).getGroupId()){
                    navCurrentStep--;
                }
                updateNavigationInfo();     //更新当前导航位置
            }
        });
        /****************是否同步方向传感器**************************/
        followButton.setOnCheckStateChangedListener(new ImageViewCheckBox.OnCheckStateChangedListener() {
            @Override
            public void onCheckStateChanged(View view, boolean isChecked) {
                if(isChecked){
                    sensorState = true;
                }else {
                    sensorState = false;
                    //恢复为初始状态
                    mFMMap.resetCompassToNorth();
                }
            }
        });
        /**************************退出导航模式**************************/
        quitNavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quitNaviModel();//退出导航模式
            }
        });
        /***********************查看商铺详细信息按钮**********************/
        moreInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMapActivity.this,InfoActivity.class);
                startActivity(intent);
            }
        });
        /************************导航到这按钮***************************/
        naviToHereButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navLayoutButton.setStateChanged();
                navFromToLayout.setVisibility(View.VISIBLE);
                navEndLoc.setGroupId(mClickedModel.getGroupId());
                navEndLoc.setMapCoord(mClickedModel.getCenterMapCoord());
                navEndModel = mClickedModel;
                createEndImageMarker();
                navEndPointEditText.setText(navEndModel.getName());
            }
        });
        /************************查看广播通知按钮**************************/
        broadcastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMapActivity.this,BroadcastMessageListActivity.class);
                startActivity(intent);
            }
        });
        /*******************ToolBar头像点击打开抽屉***********************/
        userImageToolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.START);
                if(BmobUser.getCurrentUser() == null){
                    userName.setText("点击登陆");
                }else
                    userName.setText(BmobUser.getCurrentUser(UserBean.class).getNickname());
            }
        });
        /*****************打开导航Setting界面****************************/
        naviSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                naviSetting();
            }
        });
        /**********************打开AR导航模式**************************/
        startArNaviButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMapActivity.this, ARNaviActivity.class);
                startActivity(intent);
                //startARNavigation();//开启导航模式
            }
        });
        /***********************打开二维码矫正导航*************************/
        qrScanCorrectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRCodeActivity.setStartModel(QRCodeActivity.StartModel.QRToCorrectNavi);
                Intent intent = new Intent(MainMapActivity.this, QRCodeActivity.class);
                startActivityForResult(intent,5);
            }
        });
        Button button = (Button)findViewById(R.id.btn_type_food);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<FMMapCoord> coords = queryFacilityByType(170000);
                coords.add(currentLoc.getMapCoord());
                displayImageMarkers(coords);
            }
        });
    }

    /*******************************与导航相关的方法******************************/
    /**
     * 展示导航位置，引导信息等
     */
    void updateNavigationInfo(){
        //计算时间
        time = NaviUtil.getTimeByWalk(leftDistance);
        leftDistanceText.setText("路程："+Integer.toString(leftDistance)+"米 "
                +"预计"+Integer.toString(time) +"分钟");
        navMessage.setText(mDescriptions.get(navCurrentStep));//更新路径提示信息
        //更新位置
        currentLoc = NaviUtil.getCurrentCoord(navCurrentStep,mNaviAnalyser);
        navNextLoc = NaviUtil.getNextCoord(navCurrentStep,mNaviAnalyser);
        addNextStepMaker();         //添加导航下一步标签
        updateStoreFront();         //更新下一步附近商铺
        updateDirctionPic(NaviUtil.getDescription(navCurrentStep,mNaviAnalyser));

        UpdateLocateInMap();        //更新位置

        if(WalkAnimation){
            //载入目标路线点集合
            mLocationAPI.stop();
            ArrayList<FMMapCoord> points = new ArrayList<>();
            points.add(currentLoc.getMapCoord());
            points.add(navNextLoc.getMapCoord());
            mLocationAPI.setupTargetLine(points,currentLoc.getGroupId());
            mLocationAPI.start();
            mLocationAPI.setFMLocationListener(new FMLocationAPI.OnFMLocationListener() {
                @Override
                public void onAnimationStart() {
                    isWalking = true;
                }

                @Override
                public void onAnimationUpdate(FMMapCoord mapCoord, double distance, double angle) {
                    updateLocationMarker(mapCoord, angle);
                }

                @Override
                public void onAnimationEnd() {
                    // 已经行走过终点
                    if (isWalkComplete()) {
                        isWalking = false;
                        return;
                    }
                }
            });
        }
    }
    /**
     * 添加下一步周边商铺提示
     */
    void updateStoreFront(){
        int groupId = navNextLoc.getGroupId();
        FMSearchModelByCircleRequest request = new FMSearchModelByCircleRequest(groupId,navNextLoc.getMapCoord() , 10);
        ArrayList<FMSearchResult> result = mSearchAnalyser.executeFMSearchRequest(request);
        //对返回集合遍历获取相对应模型信息
        ArrayList<FMModel> models = new ArrayList<FMModel>();
        for (FMSearchResult r : result) {
            String fid = (String) r.get("FID"); //获取fid，在通过fid获取模型对象
            FMModel model = mFMMap.getFMLayerProxy().queryFMModelByFid(fid);
            models.add(model);
        }
        if(!models.isEmpty()){
            nextStoreFront.setText(models.get(0).getName());
        }else {
            nextStoreFront.setText("未知");
        }
    }
    /**
     * 退出导航模式，返回常规模式
     */
    void quitNaviModel(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainMapActivity.this);
        dialog.setTitle("结束导航");
        String message = "结束本次导航？";
        dialog.setMessage(message);
        dialog.setCancelable(true);
        dialog.setPositiveButton("结束", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentModel = Model.NORMALMODEL;
                updateCurrentUIModel();
                clearBeforeFinish();
                //Intent intent = new Intent(MainMapActivity.this,MainMapActivity.class);
                //startActivity(intent);
                //AppManager.getInstance().killActivity(MainMapActivity.this);
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.show();
    }
    /**
     * 导航Setting弹窗
     */
    void naviSetting(){

        LayoutInflater inflater = (LayoutInflater)getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        vPopWindow =inflater.inflate(R.layout.navi_setting_layout, null, false);
        PopupWindow popupWindow = new PopupWindow(vPopWindow);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.BottomPopWindowAnimation);
        popupWindow.showAtLocation(MainMapActivity.this.drawerLayout,Gravity.BOTTOM, 0, 0);
        //设置是否自动下一步按钮
        ImageViewCheckBox autoNextStepCheckBox = (ImageViewCheckBox)vPopWindow.findViewById(R.id.auto_next_step_checkbox);
        autoNextStepCheckBox.setOnCheckStateChangedListener(new ImageViewCheckBox.OnCheckStateChangedListener() {
            @Override
            public void onCheckStateChanged(View view, boolean isChecked) {
                if(isChecked){
                    AutoNextStep = true;
                    Toast.makeText(MainMapActivity.this, Boolean.toString(AutoNextStep),Toast.LENGTH_SHORT).show();
                }else{
                    AutoNextStep = false;
                    Toast.makeText(MainMapActivity.this,Boolean.toString(AutoNextStep),Toast.LENGTH_SHORT).show();
                }
            }
        });
        //设置按钮显示状态
//        if(!AutoNextStep)
//            autoNextStepCheckBox.setStateChanged();
//        if(!SensorAssist)
//            sensorAssistCheckBox.setStateChanged();
//        if(!WalkAnimation)
//            walkAnimationCheckBox.setStateChanged();
    }
    /**
     * 导航起点输入
     */
    void chooseStartLoc(){
        final String[] items = { "选择当前位置","从收藏中选择","名称搜索选择","地图选址" };
        AlertDialog.Builder listDialog = new AlertDialog.Builder(MainMapActivity.this);
        listDialog.setTitle("选择方式：");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                //下标从0开始
                switch (index){
                    case 0:
                        if(currentLoc.getGroupId() == -1){
                            Toast.makeText(MainMapActivity.this,"未获取当前位置",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(navStartModel != null)
                            navStartModel.setSelected(false);
                        navStartLoc.copy(currentLoc);
                        createStartImageMarker();
                        navStartModel = getModelFromMapCoord(navStartLoc);
                        navStartModel.setSelected(true);
                        navStartPointEditText.setText(navStartModel.getName());
                        break;
                    case 1:
                        //从收藏中选择
                        startActivity(new Intent(MainMapActivity.this,LoctionListActivity.class));
                        break;
                    case 2:
                        CommonUtils.showToast(MainMapActivity.this,"即将上线！");
                        break;
                    case 3:
                        Toast.makeText(MainMapActivity.this,"您可以点击地图模型选择",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        listDialog.show();
    }
    /**
     * 导航终点选择
     */
    void chooseEndLoc(){
        final String[] items = { "选择当前位置","从收藏中选择","名称搜索选择","地图选址" };
        AlertDialog.Builder listDialog = new AlertDialog.Builder(MainMapActivity.this);
        listDialog.setTitle("选择方式：");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                //下标从0开始
                switch (index){
                    case 0:
                        if(currentLoc.getGroupId() == -1){
                            Toast.makeText(MainMapActivity.this,"未获取当前位置",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(navEndModel != null)
                            navEndModel.setSelected(false);
                        navEndLoc.copy(currentLoc);
                        createEndImageMarker();
                        navEndModel = getModelFromMapCoord(navEndLoc);
                        navEndModel.setSelected(true);
                        navEndPointEditText.setText(navEndModel.getName());
                        break;
                    case 1:
                        //从收藏中选择
                        CommonUtils.showToast(MainMapActivity.this,"即将上线！");
                        break;
                    case 2:
                        CommonUtils.showToast(MainMapActivity.this,"即将上线！");
                        break;
                    case 3:
                        Toast.makeText(MainMapActivity.this,"您可以点击地图模型选择",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        listDialog.show();
    }


    /******************************与控件显示相关的方法********************************/
    //更新当前UI模式显示---导航、搜索、常规
    void updateCurrentUIModel(){
        switch (currentModel){
            case NORMALMODEL:   //常规UI模式
                mtoolBar.setVisibility(View.VISIBLE);
                normalModelLayout.setVisibility(View.VISIBLE);
                navModelLayout.setVisibility(View.GONE);
                searchModelLayout.setVisibility(View.GONE);
                mFloorComponent.setVisibility(View.VISIBLE);
                m3DTextButton.setVisibility(View.VISIBLE);
                mZoomComponent.setVisibility(View.VISIBLE);
                bottomLayout.setVisibility(View.GONE);
                navFromToLayout.setVisibility(View.GONE);
                mSearchBar.clearFocus();
                clearResultMarker();
                break;
            case NAVIGATIONMODEL://导航UI模式
                normalModelLayout.setVisibility(View.GONE);
                navModelLayout.setVisibility(View.VISIBLE);
                searchModelLayout.setVisibility(View.GONE);
                mFloorComponent.setVisibility(View.GONE);
                m3DTextButton.setVisibility(View.VISIBLE);
                mZoomComponent.setVisibility(View.GONE);
                clearResultMarker();
                mFMMap.setZoomLevel(21,true);
                mFMMap.setRotateAngle(90);
                mFMMap.moveToCenter(currentLoc.getMapCoord(),true);
                mtoolBar.setVisibility(View.GONE);
                break;
            case SEARCHMODEL:   //搜索UI模式
                mtoolBar.setVisibility(View.VISIBLE);
                normalModelLayout.setVisibility(View.GONE);
                navModelLayout.setVisibility(View.GONE);
                searchModelLayout.setVisibility(View.GONE);
                mFloorComponent.setVisibility(View.VISIBLE);
                m3DTextButton.setVisibility(View.VISIBLE);
                mZoomComponent.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }
    //自定义方法，添加模型点击逻辑
    void setMapModelClickListener(){
        //创建模型图层并添加
        modelLayer = mFMMap.getFMLayerProxy().getFMModelLayer(mFMMap.getFocusGroupId());
        mFMMap.addLayer(modelLayer);
        //设置模型点击事件
        modelLayer.setOnFMNodeListener(new OnFMNodeListener() {
            @Override
            public boolean onClick(FMNode node) {
                if(mClickedModel!=null){
                    mClickedModel.setSelected(false);
                    bottomLayout.setVisibility(View.GONE);
                }
                FMModel model = (FMModel) node;
                if(mClickedModel == model)
                    return true;
                mClickedModel = model;
                if(bottomLayout.getVisibility() == View.GONE){
                    bottomLayout.setVisibility(View.VISIBLE);
                    model.setSelected(true);
                    mFMMap.updateMap();
                    TextView nameView = (TextView)findViewById(R.id.mapselect_bottom_name_textview);
                    if(model.getName().isEmpty())
                        nameView.setText("闲置");
                    else
                        nameView.setText(model.getName());//店铺名称
                    TextView floorView = (TextView)findViewById(R.id.mapselect_bottom_floor_textview);
                    floorView.setText(Integer.toString(model.getGroupId())+"楼");
                    TextView infoView = (TextView)findViewById(R.id.mapselect_bottom_intro_textview);
                    infoView.setText("主要研发，设计，生产，销售休闲类服饰");
                }
                else{
                    bottomLayout.setVisibility(View.GONE);
                    model.setSelected(false);
                    mFMMap.updateMap();
                }

                if(navStartPointEditText.isFocused()){
                    navStartPointEditText.setText(model.getName());
                    navStartLoc.setGroupId(model.getGroupId());//记录起点
                    navStartLoc.setMapCoord(model.getCenterMapCoord());
                    navStartModel = mClickedModel;  //记录起点模型
                    createStartImageMarker();   //显示起点图标
                    if(navEndPointEditText.getText().toString().isEmpty()){//终点为空，则自动聚焦终点
                        navEndPointEditText.setFocusable(true);
                        navEndPointEditText.requestFocus();
                    }
                }else if(navEndPointEditText.isFocused()){
                    navEndPointEditText.setText(model.getName());
                    navEndLoc.setGroupId(model.getGroupId());//记录候选终点
                    navEndLoc.setMapCoord(model.getCenterMapCoord());
                    navEndModel = mClickedModel;
                    createEndImageMarker();     //显示终点位置坐标
                    navEndPointEditText.clearFocus();
                }
                return true;
            }

            @Override
            public boolean onLongPress(FMNode node) {
                if(mClickedModel!=null){
                    mClickedModel.setSelected(false);
                    bottomLayout.setVisibility(View.GONE);
                }
                final FMModel model = (FMModel) node;
                mClickedModel = model;
                model.setSelected(true);
                mFMMap.updateMap();
                //无窗口就创建一个，并注册关闭监听
                if (mInfoWindow == null) {
                    mInfoWindow = new FMNodeInfoWindow(mMapView, R.layout.layout_info_window);
                    //点击关闭
                    mInfoWindow.getView().findViewById(R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mInfoWindow.close();
                        }
                    });
                    //查看详情按钮逻辑
                    mInfoWindow.getView().findViewById(R.id.set_info_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainMapActivity.this,InfoActivity.class);
                            intent.putExtra(SELECT_Modle,mClickedModel.getName());
                            startActivity(intent);
                            mInfoWindow.close();
                        }
                    });
                    //设为定位点按钮逻辑
                    mInfoWindow.getView().findViewById(R.id.set_star_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            currentLoc.setGroupId(mClickedModel.getGroupId());
                            currentLoc.setMapCoord(mClickedModel.getCenterMapCoord());
                            //显示悬浮图标
                            clearResultMarker();
                            mInfoWindow.close();
                        }
                    });
                    //设为导航起点按钮逻辑
                    mInfoWindow.getView().findViewById(R.id.set_start_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            navStartLoc.setMapCoord(mClickedModel.getCenterMapCoord());
                            navStartLoc.setGroupId(mClickedModel.getGroupId());
                            navStartModel = mClickedModel;
                            createStartImageMarker();//显示起点位置
                            //如果导航表单可见，难么更新表单
                            if(navFromToLayout.getVisibility() == View.VISIBLE){
                                navStartPointEditText.setText(mClickedModel.getName().toString());
                            }
                            mInfoWindow.close();
                        }
                    });
                    //设为导航终点按钮逻辑
                    mInfoWindow.getView().findViewById(R.id.set_end_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            navEndLoc.setMapCoord(mClickedModel.getCenterMapCoord());
                            navEndLoc.setGroupId(mClickedModel.getGroupId());
                            navEndModel = mClickedModel;
                            createEndImageMarker();//显示终点位置
                            if(navFromToLayout.getVisibility() == View.VISIBLE){
                                navEndPointEditText.setText(mClickedModel.getName().toString());
                            }
                            mInfoWindow.close();
                        }
                    });
                }

                //如果已经有打开的窗口，那么先关闭它
                if (mInfoWindow.isOpened()) {
                    mInfoWindow.close();
                } else{
                    mInfoWindow.setTarget(node);
                    mInfoWindow.open();
                }
                return true;
            }
        });

        //
    }


    /****************************手机方向传感器监听逻辑********************************/
    void InitSenser() {
        final TextView textView = (TextView) findViewById(R.id.testmessage);

        sensormaneger = (SensorManager) getSystemService(SENSOR_SERVICE);//获取manager
        msenser = sensormaneger.getDefaultSensor(Sensor.TYPE_ORIENTATION);//获取传感器实例
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                mapRotate = event.values[0];    //传感器角度
                /*******设置地图旋转是否同步传感器********/
                if(sensorState == true){    //方向同步模式
                    if(m3DTextButton.isSelected())
                        mFMMap.setTiltAngle(event.values[1]+90);
                    float angle = mapRotate;
                    if(angle > 360)
                        angle = 360 - angle;
                    mFMMap.setRotateAngle(angle);    //方向传感器的数据需要较准
                    mFMMap.updateMap();
                }else {//方向非同步模式
                    //更新定位图标的角度
                    if (mLocationMarker != null){
                        mLocationMarker.updateAngle(360-mapRotate);
                    }
                }
                /************jiachang******************/
                if(SensorAssist){
                    if(isWalking){
                        float angle = 360 - mapRotate;
                        if(angle < 0){
                            angle = 360 + angle;
                        }
                        mLocationAPI.DirectionCheck(angle);
                    }
                }
                if(AutoNextStep && currentModel==Model.NAVIGATIONMODEL){
                    if(!isWalking){
                        if(NextStepAngleCheck(360-mapRotate)){
                            //检查是否到达终点
                            if(navCurrentStep == mDescriptions.size()-1){
                                currentLoc = NaviUtil.getNextCoord(navCurrentStep,mNaviAnalyser);
                                UpdateLocateInMap();
                                CommonUtils.showToast(MainMapActivity.this,"您已经到达终点");
                                return;
                            }
                            navCurrentStep++;
                            if(NaviUtil.getCurrentCoord(navCurrentStep,mNaviAnalyser).getGroupId() !=
                                    NaviUtil.getNextCoord(navCurrentStep,mNaviAnalyser).getGroupId()){
                                navCurrentStep++;
                            }
                            updateNavigationInfo();     //更新当前导航位置
                        }
                    }
                }


            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensormaneger.registerListener(sensorEventListener,msenser,SensorManager.SENSOR_DELAY_GAME);
    }

    //自定义动画，判断传感器角度，能都自动进入下一步导航
    boolean NextStepAngleCheck(double currentAngle){
        if(navCurrentStep == mDescriptions.size()-1){
            //最后一步，禁止自动进入下一步
            return false;
        }
        FMNaviDescriptionData data = datas.get(navCurrentStep+1);
        ArrayList<FMMapCoord> points = new ArrayList<>();
        points.add(data.getStartCoord());
        points.add(data.getEndCoord());
        mLocationAPI.setupTargetLine(points,data.getStartGroupId());
        points.clear();
        points.addAll(mLocationAPI.getSimulateCoords());

        double[] angles  = FMMath.getAnglesBetweenCoords(points);
        double angle1 = angles[0] - currentAngle;
        double angle2 = currentAngle - angles[0];
        //判断夹角是否在允许更新路线的范围内
        if(angle1 <10 && angle1 >=0 || angle2 <10 &&angle2 >= 0){
            return true;
        }else
            return  false;
    }








    /****************************处理启动QR二维码扫描返回的结果**************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        BmobQuery<QRInfo> bmobQuery = new BmobQuery<>();
        if(data == null)
            return;
        String qrId = data.getStringExtra(QRCodeActivity.QRSCANRESULT).toString();
        /*************服务器查询二维码活码内容，结果存于本地***************/
        bmobQuery.getObject(qrId, new QueryListener<QRInfo>() {
            @Override
            public void done(QRInfo qrInfo, BmobException e) {
                if(e == null){
                    if(!qrInfo.getMapid().equals(mapID)){
                        Toast.makeText(MainMapActivity.this,"该二维码不再您所在场景中！",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    SharedPreferences.Editor editor = YiXunAPP.mSharedPreferences.edit();
                    editor.putString(Constants.String_MapId,qrInfo.getMapid());
                    editor.putString(Constants.String_MapName,qrInfo.getMapname());
                    editor.putInt(Constants.String_GroupId,qrInfo.getGroupid());
                    editor.putFloat(Constants.String_X,qrInfo.getX());
                    editor.putFloat(Constants.String_Y,qrInfo.getY());
                    editor.apply();
                }else{
                    Toast.makeText(MainMapActivity.this,"定位失败",Toast.LENGTH_SHORT).show();
                }
            }
        });

        FMMapCoord fmMapCoord = new FMMapCoord();
        fmMapCoord.x = YiXunAPP.mSharedPreferences.getFloat(Constants.String_X,0);
        fmMapCoord.y = YiXunAPP.mSharedPreferences.getFloat(Constants.String_Y,0);
        switch (resultCode){
            case 1:
                currentLoc.setGroupId(YiXunAPP.mSharedPreferences.getInt(Constants.String_GroupId,0));
                currentLoc.setMapCoord(fmMapCoord);
                UpdateLocateInMap();       //更新当前位置
                break;
            case 2:     //扫码结果作为收藏点
                Toast.makeText(MainMapActivity.this,"收藏成功",Toast.LENGTH_SHORT).show();
                break;
            case 3:     //扫码结果作为起点
                navStartLoc.setGroupId(YiXunAPP.mSharedPreferences.getInt(Constants.String_GroupId,1));
                navStartLoc.setMapCoord(fmMapCoord);
                createStartImageMarker();
                navStartModel = getModelFromMapCoord(navStartLoc);
                break;
            case 4:     //扫码结果作为终点
                navEndLoc.setGroupId(YiXunAPP.mSharedPreferences.getInt(Constants.String_GroupId,1));
                navEndLoc.setMapCoord(fmMapCoord);
                createEndImageMarker();
                navEndModel = getModelFromMapCoord(navEndLoc);
                break;
            case 5:     //矫正导航
                navStartLoc.setGroupId(YiXunAPP.mSharedPreferences.getInt(Constants.String_GroupId,1));
                navStartLoc.setMapCoord(fmMapCoord);
                createStartImageMarker();
                navStartModel = getModelFromMapCoord(navStartLoc);
                //重新进行导航分析
                int type = mNaviAnalyser.analyzeNavi(navStartLoc.getGroupId(), navStartLoc.getMapCoord(), navEndLoc.getGroupId(), navEndLoc.getMapCoord(),
                        FMNaviAnalyser.FMNaviModule.MODULE_SHORTEST);//基于最短路径原则
                if (type == FMNaviAnalyser.FMRouteCalcuResult.ROUTE_SUCCESS) {
                    clearLineLayer();
                    addLineMarker();            //添加线图层
                    getRouteDescription();      //获取路径描述
                    navCurrentStep = 0;         //导航步骤初始化
                    updateNavigationInfo();
                    currentModel = Model.NAVIGATIONMODEL;
                    updateCurrentUIModel();     //界面切换为导航模式
                }
                break;
            default:
                break;
        }
    }

    /**
     * OnFMSwitchGroupListener类的抽象接口
     */
    @Override
    public void beforeGroupChanged() {
        isAnimateEnd = false;
    }
    @Override
    public void afterGroupChanged() {
        isAnimateEnd = true;
        setMapModelClickListener();
    }


    /**
     * 实现FMLocationAPI接口函数
     */


    /**********************ToolBar菜单按钮*************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()){
            case R.id.action_search:
                currentModel = Model.SEARCHMODEL;
                updateCurrentUIModel();
                break;
            case R.id.action_star:
                Intent intent2 = new Intent(MainMapActivity.this, LoctionListActivity.class);
                startActivity(intent2);
                break;
            case R.id.action_nearby:
                if(navFromToLayout.getVisibility() == View.VISIBLE){
                    navFromToLayout.setVisibility(View.GONE);
                    navLayoutButton.setStateChanged();
                }
                if(nearbyLayout.getVisibility() == View.GONE)
                    nearbyLayout.setVisibility(View.VISIBLE);
                else
                    nearbyLayout.setVisibility(View.GONE);
                break;
        }


        return super.onOptionsItemSelected(item);
    }



    /************************AR导航模式逻辑*****************************/
    /**
     *
     */
    void initARNavigation(){
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //glView = new GLView(this);
    }
    /**
     * 开启导航模式
     */
    void startARNavigation(){
//        checkSupported();
//        try
//        {
//            if (supportsEs2) {
//
//                glSView=(GLSurfaceView)findViewById(R.id.ar_model);
//                glRenderer = new GLRenderer(this);
//                glSView.setRenderer(glRenderer);
//            } else {
//
//                Toast.makeText(this, "当前设备不支持OpenGL ES 2.0!", Toast.LENGTH_SHORT).show();
//            }
//        }
//        catch (IOException e){
//            e.printStackTrace();
//        }
//
//
//        requestCameraPermission(new PermissionCallback() {
//            @Override
//            public void onSuccess() {
//                ((ViewGroup) findViewById(R.id.ar_navi_preview)).addView(glView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//            }
//
//            @Override
//            public void onFailure() {
//            }
//        });
    }


//    //直接移过来的类
//    private interface PermissionCallback
//    {
//        void onSuccess();
//        void onFailure();
//    }
//
//    private HashMap<Integer, PermissionCallback> permissionCallbacks = new HashMap<Integer, PermissionCallback>();
//    private int permissionRequestCodeSerial = 0;
//    @TargetApi(23)
//    private void requestCameraPermission(PermissionCallback callback)
//    {
//        if (Build.VERSION.SDK_INT >= 23) {
//            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                int requestCode = permissionRequestCodeSerial;
//                permissionRequestCodeSerial += 1;
//                permissionCallbacks.put(requestCode, callback);
//                requestPermissions(new String[]{Manifest.permission.CAMERA}, requestCode);
//            } else {
//                callback.onSuccess();
//            }
//        } else {
//            callback.onSuccess();
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
//    {
//        if (permissionCallbacks.containsKey(requestCode)) {PermissionCallback callback = permissionCallbacks.get(requestCode);
//            permissionCallbacks.remove(requestCode);
//            boolean executed = false;
//            for (int result : grantResults) {
//                if (result != PackageManager.PERMISSION_GRANTED) {
//                    executed = true;
//                    callback.onFailure();
//                }
//            }
//            if (!executed) {
//                callback.onSuccess();
//            }
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//
//    @Override
//    protected void onResume()
//    {
//        super.onResume();
//        if (glView != null) { glView.onResume(); }
//    }
//
//    @Override
//    protected void onPause()
//    {
//        if (glView != null) { glView.onPause(); }
//        super.onPause();
//    }
//

//
//
//    private void checkSupported() {
//        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
//        supportsEs2 = configurationInfo.reqGlEsVersion >= 0x2000;
//
//        boolean isEmulator = Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
//                && (Build.FINGERPRINT.startsWith("generic")
//                || Build.FINGERPRINT.startsWith("unknown")
//                || Build.MODEL.contains("google_sdk")
//                || Build.MODEL.contains("Emulator")
//                || Build.MODEL.contains("Android SDK built for x86"));
//
//        supportsEs2 = supportsEs2 || isEmulator;
//    }

}

package com.jackchance.yixun.UI;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.dou361.dialogui.DialogUIUtils;
import com.dou361.dialogui.bottomsheet.BottomSheetBean;
import com.dou361.dialogui.listener.DialogUIItemListener;
import com.dou361.dialogui.listener.DialogUIListener;
import com.fengmap.android.analysis.navi.FMNaviAnalyser;
import com.fengmap.android.analysis.navi.FMNaviDescriptionData;
import com.fengmap.android.analysis.search.FMSearchResult;
import com.fengmap.android.analysis.search.model.FMSearchModelByCircleRequest;
import com.fengmap.android.map.FMMapView;
import com.fengmap.android.map.animator.FMLinearInterpolator;
import com.fengmap.android.map.event.OnFMCompassListener;
import com.fengmap.android.map.event.OnFMMapInitListener;
import com.fengmap.android.map.event.OnFMMapThemeListener;
import com.fengmap.android.map.event.OnFMNodeListener;
import com.fengmap.android.map.event.OnFMSwitchGroupListener;
import com.fengmap.android.map.geometry.FMMapCoord;
import com.fengmap.android.map.marker.FMModel;
import com.fengmap.android.map.marker.FMNode;
import com.fengmap.android.utils.FMMath;
import com.fengmap.android.widget.FMFloorControllerComponent;
import com.fengmap.android.widget.FMNodeInfoWindow;
import com.jackchance.yixun.Bean.Map;
import com.jackchance.yixun.Bean.MapCoord;
import com.jackchance.yixun.Bean.QRInfo;
import com.jackchance.yixun.Constants;
import com.jackchance.yixun.R;
import com.jackchance.yixun.Service.StepCounterService.SettingsActivity;
import com.jackchance.yixun.Service.StepCounterService.StepCounterActivity;
import com.jackchance.yixun.Service.StepCounterService.StepCounterService;
import com.jackchance.yixun.Service.StepCounterService.StepDetector;
import com.jackchance.yixun.UI.ARNavi.ARNaviActivity;
import com.jackchance.yixun.UI.BroadcastMessage.BroadcastMessageListActivity;
import com.jackchance.yixun.UI.ChooseMap.MapListActivity;
import com.jackchance.yixun.UI.ChooseTheme.ThemeListActivity;
import com.jackchance.yixun.UI.FavouriteLoction.LoctionListActivity;
import com.jackchance.yixun.UI.GuidanceList.GuidanceListActivity;
import com.jackchance.yixun.UI.Navigation.MapUI.BaseMapActivity;
import com.jackchance.yixun.UI.Navigation.MapUI.InfoActivity;
import com.jackchance.yixun.UI.Navigation.MapUI.NaviUtil;
import com.jackchance.yixun.UI.QRScan.QRCodeActivity;
import com.jackchance.yixun.UI.SelectPOI.SelectPOIActivity;
import com.jackchance.yixun.UI.UserManager.LandActivity;
import com.jackchance.yixun.UI.UserManager.UserInfomation;
import com.jackchance.yixun.Util.AppManager;
import com.jackchance.yixun.Util.CommonUtils;
import com.jackchance.yixun.Util.FMLocationAPI;
import com.jackchance.yixun.Util.FileUtils;
import com.jackchance.yixun.Util.SQLOrderDao;
import com.jackchance.yixun.YiXunAPP;
import com.jackchance.yixun.ZAssets.ARCode.GLView;
import com.jackchance.yixun.ZAssets.Widget.ImageViewCheckBox;
import com.longsh.optionframelibrary.OptionBottomDialog;
import com.longsh.optionframelibrary.OptionMaterialDialog;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.easyar.Engine;
import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends BaseMapActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnFMMapInitListener
        ,OnFMSwitchGroupListener {
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("易寻");
        setSupportActionBar(toolbar);

        InitOnCreat();
        updateMap();
        //定位弹窗
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ChooseHowToLocate();
            }
        }, 4000);//3秒后执行Runnable中的run方法

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initARNavigation();
    }

    private long firstTime = 0;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //几种模式下的退出方式
            if(currentModel == Model.NAVIGATIONMODEL || currentModel == Model.ARNAVIMODEL){
                quitNaviModel();
            }else {
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {
                    Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    firstTime = secondTime;
                } else {
                    moveTaskToBack(true);
                }
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Intent intent = new Intent(MainActivity.this,SelectPOIActivity.class);
            startActivityForResult(intent,0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_guidance) {      //活动引导
            Intent intent = new Intent(MainActivity.this,GuidanceListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {    //地图切换
            Intent intent = new Intent(MainActivity.this,MapListActivity.class);
            startActivityForResult(intent,6);
        } else if (id == R.id.nav_slideshow) {  //周边搜索
            Intent intent = new Intent(MainActivity.this,SelectPOIActivity.class);
            startActivityForResult(intent,0);
        } else if (id == R.id.nav_theme) {     //主题设置
            Intent intent = new Intent(MainActivity.this, ThemeListActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_ar) {         //AR展示
            Intent intent = new Intent(MainActivity.this, com.jackchance.yixun.ZAssets.ARCode.ARNaviActivity.class);
            startActivityForResult(intent,3);

        } else if(id == R.id.nav_star){          //收藏位置
            Intent intent = new Intent(MainActivity.this,LoctionListActivity.class);
            startActivityForResult(intent,0);   //打开收藏列表，直接定位
        }else if(id == R.id.nav_seekcar){       //反向寻车
            CarSeekOrRecord();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


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
    private TextView modelNameInWin;                    //弹窗中的名字
    private LinearLayout stepCounterSettingButton;      //弹出框中的惯性导航设置
    //SQL操作
    public static SQLOrderDao orderDao;

    public double currentAngel = 0;
    public float init_x = 0;
    public float init_y = 0;


    void InitOnCreat(){
        initialize();                               //完成activity的初始化操作
        setWidgetClickListener();                   //控件事件监听逻辑
        currentMap = new Map();
        currentLoc = new MapCoord();
        /****************加载地图，并进行相应配置******************/
        mMapView = (FMMapView)findViewById(R.id.map_view);
        mFMMap = mMapView.getFMMap();
        mFMMap.setOnFMMapInitListener(this);        //设置Map初始化监听
        //设置差值动画监听
        mLocationAPI = new FMLocationAPI();         //差值动画

    }

    void updateMap(){
        /******************获取地图和定位信息******************/
        //从本地存储读取地图信息
        SharedPreferences pref = YiXunAPP.mSharedPreferences;
        mapID = pref.getString(Constants.String_MapId,"10347");
        currentMap.setMapId(mapID);
        currentMap.setName(pref.getString(Constants.String_MapName,"爱情海购物中心"));
        currentMap.setThemeid(pref.getString(Constants.String_ThemeId,"3010"));
        int groupid = pref.getInt(Constants.String_GroupId,1);
        currentLoc.setGroupId(groupid);
        FMMapCoord mapCoord = new FMMapCoord();
        mapCoord.x = pref.getFloat(Constants.String_X,0);
        mapCoord.y = pref.getFloat(Constants.String_Y,0);
        currentLoc.setMapCoord(mapCoord);

        String path = FileUtils.getMapPath(MainActivity.this,mapID);
        //mFMMap.openMapById("bupt-innovation",true);
        //mFMMap.openMapById(FileUtils.DEFAULT_MAP_ID,true);
        //String path = FileUtils.getDefaultMapPath(this);
        mFMMap.openMapByPath(path);


        /*********************初始化一些变量***********************/
        navStartLoc = new MapCoord(currentLoc.getGroupId(),currentLoc.getMapCoord());
        navEndLoc = new MapCoord(currentLoc.getGroupId(),currentLoc.getMapCoord());
        navNextLoc = new MapCoord(currentLoc.getGroupId(),currentLoc.getMapCoord());
        messageView.setText("欢迎光临"+currentMap.getName());

        if(mapID.equals("bupt-innovation")){
            MAP_NORMAL_LEVEL = 23;
            MAX_BETWEEN_LENGTH = 3;
            lineWith = 1f;
            offsetHigh = 2f;
        }else{
            MAP_NORMAL_LEVEL = 20;
            MAX_BETWEEN_LENGTH = 10;
            lineWith = 3f;
            offsetHigh = 1.5f;
        }


    }

    /**
     * 选择如何定位
     */
    void ChooseHowToLocate(){
        final String[] items = {"扫描附近的二维码(推荐)","搜索以确定您的位置","为您推荐热门位置"};
        AlertDialog.Builder listDialog = new AlertDialog.Builder(MainActivity.this);
        listDialog.setTitle("确定您的位置？");
        listDialog.setCancelable(true);
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                //下标从0开始
                switch (index){
                    case 0:
                        Intent intent = new Intent(MainActivity.this,QRCodeActivity.class);
                        QRCodeActivity.setStartModel(QRCodeActivity.StartModel.QRToLocateInMap);
                        startActivityForResult(intent,1);
                        break;
                    case 1:
                        Intent intent1 = new Intent(MainActivity.this,SelectPOIActivity.class);
                        startActivityForResult(intent1,0);
                        break;
                    case 2:
                        CommonUtils.showToast(MainActivity.this,"即将上线");
                        break;
                    default:
                        break;
                }
            }
        });
        listDialog.show();
    }

    /**
     *实现OnMapInitlistrner的接口
     */
    @Override
    public void onMapInitSuccess(String s) {

        mFMMap.loadThemeById("3010");
//        if(mFMMap.getCurrentMapId().equals("bupt-innovation")){
//            mFMMap.loadThemeByPath(FileUtils.getThemePath(this,"bupt-innovation"));
//            Log.e("MainActivity", "onMapInitSuccess: "+FileUtils.getThemePath(this,"bupt-innovation"));
//        }else{
//            String themeid = YiXunAPP.mSharedPreferences.getString(Constants.String_ThemeId,"3010");
//            mFMMap.loadThemeById(themeid);
//        }
        //设置多楼层显示
        setMultidisplay();
        //模型点击逻辑--自定义方法
        setMapModelClickListener();
        //添加楼层切换组件
        setFloorControlComponent();
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
                    Toast.makeText(MainActivity.this,"方向同步已开启",Toast.LENGTH_SHORT).show();
                }else {
                    sensorState = false;
                    //恢复为初始状态
                    mFMMap.resetCompassToNorth();
                    Toast.makeText(MainActivity.this,"方向同步已经关闭",Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        qrScanButton = (FloatingActionButton)findViewById(R.id.btn_qr_start);
        mtoolBar = (Toolbar)findViewById(R.id.toolbar);
        broadcastButton = (LinearLayout)findViewById(R.id.layout_broadcast);
        modelNameInWin = (TextView)findViewById(R.id.text_name);
        tv_show_step = (TextView) this.findViewById(R.id.text_step_number);
        tv_velocity = (TextView) this.findViewById(R.id.text_step_speed);


        //SQL操作
        orderDao = new SQLOrderDao(this);
        if (! orderDao.isDataExist()){
            orderDao.initTable();
        }

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
        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
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
                    CommonUtils.showToast(MainActivity.this,"请输入导航终点");
                    return;
                }
                if(navStartPointEditText.getText().toString().isEmpty()){
                    CommonUtils.showToast(MainActivity.this,"请输入导航起点");
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
                    addStepCounter();
                    CommonUtils.showToast(MainActivity.this,"点击下一步，开始导航");
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
                ChooseHowToLocate();
            }
        });
        /***********************启动二维码扫描定位**********************/
        qrScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,QRCodeActivity.class);
                QRCodeActivity.setStartModel(QRCodeActivity.StartModel.QRToLocateInMap);
                startActivityForResult(intent,0);
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
                    CommonUtils.showToast(MainActivity.this,"您已经到达终点");
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
                    CommonUtils.showToast(MainActivity.this,"您已经到达起点");
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
                Intent intent = new Intent(MainActivity.this,InfoActivity.class);
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
                Intent intent = new Intent(MainActivity.this,GuidanceListActivity.class);
                startActivity(intent);
            }
        });
        /*******************ToolBar头像点击打开抽屉***********************/

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
                if(currentModel == Model.NAVIGATIONMODEL){
                    //请求相机画面
                    requestCameraPermission(new PermissionCallback() {
                        @Override
                        public void onSuccess() {
                            ((ViewGroup) findViewById(R.id.ar_navi_preview)).addView(glView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        }

                        @Override
                        public void onFailure() {
                        }
                    });
                    group.setVisibility(View.VISIBLE);
                    ((ViewGroup) findViewById(R.id.ar_navi_preview)).setVisibility(View.VISIBLE);
                    currentModel = Model.ARNAVIMODEL;
                    int count =((ViewGroup) findViewById(R.id.ar_navi_preview)).getChildCount();
                    if(count == 0)
                        ((ViewGroup) findViewById(R.id.ar_navi_preview)).addView(glView, new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));//添加相机画面
                }else if(currentModel == Model.ARNAVIMODEL){
                    group.setVisibility(View.GONE);
                    currentModel=Model.NAVIGATIONMODEL;
                    ((ViewGroup) findViewById(R.id.ar_navi_preview)).setVisibility(View.GONE);
                    ((ViewGroup) findViewById(R.id.ar_navi_preview)).removeAllViews();  //移除相机画面
                }
            }
        });
        /***********************打开二维码矫正导航*************************/
        qrScanCorrectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRCodeActivity.setStartModel(QRCodeActivity.StartModel.QRToCorrectNavi);
                Intent intent = new Intent(MainActivity.this, QRCodeActivity.class);
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
            //mLocationAPI.stop();
            ArrayList<FMMapCoord> points = new ArrayList<>();
            points.add(currentLoc.getMapCoord());
            points.add(navNextLoc.getMapCoord());
            mLocationAPI.setupTargetLine(points,currentLoc.getGroupId());
            //mLocationAPI.start();
            mLocationAPI.initStepCounter();
            mLocationAPI.setFMLocationListener(new FMLocationAPI.OnFMLocationListener() {
                @Override
                public void onAnimationStart() {
                    isWalking = true;
                    startService(stepService);
                    startTimer = System.currentTimeMillis();
                    tempTime = timer;
                }

                @Override
                public void onAnimationUpdate(FMMapCoord mapCoord, double distance, double angle) {
                    updateLocationMarker(mapCoord, angle);
                    currentAngel = angle;
                }

                @Override
                public void onAnimationEnd() {
                    // 已经行走过终点
                    if (isWalkComplete()) {
                        isWalking = false;
                        Vibrator vibrator = (Vibrator)MainActivity.this.getSystemService(MainActivity.VIBRATOR_SERVICE);
                        vibrator.vibrate(500);
                        CommonUtils.showToast(MainActivity.this,"注意附近转向！");
                        stopService(stepService);
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
            String strlong = models.get(0).getName();
            if(strlong.length() > 6)
            {
                strlong=strlong.substring(0,10)+"..";
            }else{
                strlong = strlong;
            }
            nextStoreFront.setText(strlong);
            for(FMModel model:models){
                if(!model.getName().isEmpty()){
                    String substring = model.getName();
                    if(substring.length() > 6)
                    {
                        substring=substring.substring(0,6)+"..";
                    }else{
                        substring = substring;
                    }
                    nextStoreFront.setText(substring);
                    break;
                }
            }
        }else {
            nextStoreFront.setText("未知");
        }
    }
    /**
     * 退出导航模式，返回常规模式
     */
    void quitNaviModel(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
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
                stopService(stepService);
                ((ViewGroup) findViewById(R.id.ar_navi_preview)).removeAllViews();
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
        popupWindow.showAtLocation(MainActivity.this.drawerLayout,Gravity.BOTTOM, 0, 0);
        //设置是否自动下一步按钮
        ImageViewCheckBox autoNextStepCheckBox = (ImageViewCheckBox)vPopWindow.findViewById(R.id.auto_next_step_checkbox);
        autoNextStepCheckBox.setOnCheckStateChangedListener(new ImageViewCheckBox.OnCheckStateChangedListener() {
            @Override
            public void onCheckStateChanged(View view, boolean isChecked) {
                if(isChecked){
                    AutoNextStep = true;
                }else{
                    AutoNextStep = false;
                }
            }
        });

        //设置是否传感器优化
        ImageViewCheckBox walkAnimationCheckBox = (ImageViewCheckBox)vPopWindow.findViewById(R.id.walk_animation_checkbox);
        walkAnimationCheckBox.setOnCheckStateChangedListener(new ImageViewCheckBox.OnCheckStateChangedListener() {
            @Override
            public void onCheckStateChanged(View view, boolean isChecked) {
                if(isChecked){
                    WalkAnimation = true;
                    startService(stepService);
                    startTimer = System.currentTimeMillis();
                    tempTime = timer;
                }else{
                    WalkAnimation = false;
                    stopService(stepService);
                }
            }
        });
        LinearLayout stepCounterSettingButton = (LinearLayout)vPopWindow.findViewById(R.id.btn_step_counter_setting);
        stepCounterSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {       //启动setting
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        //设置按钮显示状态
        if(!AutoNextStep)
            autoNextStepCheckBox.setStateChanged();
        if(!WalkAnimation)
            walkAnimationCheckBox.setStateChanged();
        SeekBar sbStepLength = (SeekBar)vPopWindow.findViewById(R.id.step_length);
        sbStepLength.setProgress(step_count_length);
        sbStepLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                step_count_length = progress;
                TextView stepLengthText = (TextView)vPopWindow.findViewById(R.id.text_step_length);
                stepLengthText.setText(Float.toString(step_count_length*0.25f)+"m/步");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        TextView stepLengthText = (TextView)vPopWindow.findViewById(R.id.text_step_length);
        stepLengthText.setText(Float.toString(step_count_length*0.25f)+"m/步");
    }
    /**
     * 导航起点输入
     */
    void chooseStartLoc(){
        final String[] items = {"扫描身边二维码","名称搜索选择","地图选址" };
        AlertDialog.Builder listDialog = new AlertDialog.Builder(MainActivity.this);
        listDialog.setTitle("起点方式：");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                //下标从0开始
                switch (index){
                    case 0: //扫码作为起点
                        startActivityForResult(new Intent(MainActivity.this,QRCodeActivity.class),1);
                        break;
                    case 1:
                        Intent intent = new Intent(MainActivity.this,SelectPOIActivity.class);
                        startActivityForResult(intent,1);  //启动获得
                        break;
                    case 2:  //搜索选择
                        Toast.makeText(MainActivity.this,"请点击地图对应位置",Toast.LENGTH_SHORT).show();
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
        final String[] items = {"从收藏中选择","名称搜索选择","地图选址" };
        AlertDialog.Builder listDialog = new AlertDialog.Builder(MainActivity.this);
        listDialog.setTitle("选择方式：");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                //下标从0开始
                switch (index){
                    case 0:
                        startActivity(new Intent(MainActivity.this,LoctionListActivity.class));
                        break;
                    case 1:
                        Intent intent = new Intent(MainActivity.this,SelectPOIActivity.class);
                        startActivityForResult(intent,2);
                        break;
                    case 2:
                        Toast.makeText(MainActivity.this,"请点击地图对应位置",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        listDialog.show();
    }

    /******************************与控件显示相关的方法********************************/
    public void setFloorControlComponent(){
        if(mFloorComponent != null)
        {
            mMapView.removeView(mFloorComponent);
        }
        mFloorComponent = new FMFloorControllerComponent(this);
        // 创建楼层切换控件
        mFloorComponent.setMaxItemCount(3);//设置楼层最大显示数量
        //楼层切换事件监听
        mFloorComponent.setOnFMFloorControllerComponentListener
                (new FMFloorControllerComponent.OnFMFloorControllerComponentListener() {
                    @Override
                    public void onSwitchFloorMode(View view, FMFloorControllerComponent.FMFloorMode currentMode) {
                        if (currentMode == FMFloorControllerComponent.FMFloorMode.SINGLE) {
                            setSingledisplay();
                        } else {
                            setMultidisplay();
                        }
                    }
                    @Override
                    public boolean onItemSelected(final int groupId, String floorName) {
                        //楼层切换
                        mFloorComponent.setSelected(groupId-1);
                        if(groupId != currentLoc.getGroupId()){
                            if(mLocationMarker != null)
                                mLocationMarker.setVisible(false);
                        }else {
                            if(mLocationMarker!= null)
                                mLocationMarker.setVisible(true);
                        }
                        if (isAnimateEnd) {
                            mFMMap.setFocusByGroupIdAnimated(groupId, new FMLinearInterpolator(), new OnFMSwitchGroupListener() {
                                @Override
                                public void beforeGroupChanged() {
                                    isAnimateEnd = false;
                                }
                                @Override
                                public void afterGroupChanged() {
                                    isAnimateEnd = true;
                                    setMapModelClickListener();
                                }
                            });
                            return true;
                        }
                        return false;
                    }
                });
        //设置为单层模式
        mFloorComponent.setFloorMode(FMFloorControllerComponent.FMFloorMode.MULTIPLE);
        int groupId = 1; //设置默认显示楼层id
        mFloorComponent.setFloorDataFromFMMapInfo(mFMMap.getFMMapInfo(), groupId);
        mMapView.addComponent(mFloorComponent, 10, 300);//添加楼层控件并设置位置
    }
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
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                group.setVisibility(View.GONE);
                ((ViewGroup) findViewById(R.id.ar_navi_preview)).setVisibility(View.GONE);
                clearResultMarker();
                break;
            case NAVIGATIONMODEL://导航UI模式
                followButton.setVisibility(View.VISIBLE);
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
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                group.setVisibility(View.GONE);
                ((ViewGroup) findViewById(R.id.ar_navi_preview)).setVisibility(View.GONE);
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
            case ARNAVIMODEL:
                group.setVisibility(View.VISIBLE);
                followButton.setVisibility(View.GONE);
                ((ViewGroup) findViewById(R.id.ar_navi_preview)).setVisibility(View.VISIBLE);
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

                if(model.getName().isEmpty())
                    return true;
                if(mClickedModel == model)
                    return true;
                mClickedModel = model;
                Log.e("Main", "x="+Double.toString(model.getCenterMapCoord().x)+"," +
                        "y="+Double.toString(model.getCenterMapCoord().y)+"," +
                        "楼层="+Integer.toString(model.getGroupId()),null);
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
                    infoView.setText("欢迎前来我们展位参观！");
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
                FMModel model = (FMModel) node;
                mClickedModel = model;
                if(model.getName().isEmpty())
                    return true;
                model.setSelected(true);
                mFMMap.updateMap();
                //无窗口就创建一个，并注册关闭监听
                if (mInfoWindow == null) {
                    mInfoWindow = new FMNodeInfoWindow(mMapView, R.layout.layout_info_window);
                    //modelNameInWin.setText(model.getName());
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
                            Intent intent = new Intent(MainActivity.this,InfoActivity.class);
                            intent.putExtra(SELECT_Modle,mClickedModel.getName());
                            startActivity(intent);
                            mInfoWindow.close();
                        }
                    });
                    //添加收藏点按钮逻辑
                    mInfoWindow.getView().findViewById(R.id.set_star_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //添加备注
                            LayoutInflater factory = LayoutInflater.from(MainActivity.this);
                            final View textEntryView = factory.inflate(R.layout.edit_detail_window, null);
                            final EditText editTextName = (EditText) textEntryView.findViewById(R.id.editTextName);
                            final AlertDialog.Builder ad1 = new AlertDialog.Builder(MainActivity.this);
                            ad1.setTitle("备注：");
                            ad1.setMessage("填写备注");
                            ad1.setIcon(android.R.drawable.ic_dialog_info);
                            ad1.setView(textEntryView);
                            ad1.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int i) {
                                    QRInfo qrInfo = new QRInfo();
                                    qrInfo.setMapid(currentMap.getMapId());
                                    qrInfo.setMapname(currentMap.getName());
                                    qrInfo.setGroupid(mClickedModel.getGroupId());
                                    qrInfo.setModelname(mClickedModel.getName().isEmpty()?"闲置":mClickedModel.getName());
                                    qrInfo.setX((float)mClickedModel.getCenterMapCoord().x);
                                    qrInfo.setY((float)mClickedModel.getCenterMapCoord().y);
                                    qrInfo.setDetail(editTextName.getText().toString());

                                    Date day=new Date();
                                    SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                    qrInfo.setImageURL(df.format(day));
                                    orderDao.insertDate(qrInfo);
                                    CommonUtils.showToast(MainActivity.this,"收藏成功");
                                    mInfoWindow.close();
                                }
                            });
                            ad1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int i) {
                                }
                            });
                            ad1.show();// 显示对话框

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
                if(AutoNextStep && (currentModel==Model.NAVIGATIONMODEL||currentModel == Model.ARNAVIMODEL)){
                    if(!isWalking){
                        if(NextStepAngleCheck(360-mapRotate)){
                            //检查是否到达终点
                            if(navCurrentStep == mDescriptions.size()-1){
                                currentLoc = NaviUtil.getNextCoord(navCurrentStep,mNaviAnalyser);
                                UpdateLocateInMap();
                                CommonUtils.showToast(MainActivity.this,"您已经到达终点");
                                Vibrator vibrator = (Vibrator)MainActivity.this.getSystemService(MainActivity.VIBRATOR_SERVICE);
                                vibrator.vibrate(500);
                                return;
                            }
                            navCurrentStep++;
                            if(NaviUtil.getCurrentCoord(navCurrentStep,mNaviAnalyser).getGroupId() !=
                                    NaviUtil.getNextCoord(navCurrentStep,mNaviAnalyser).getGroupId()){
                                navCurrentStep++;
                            }
                            updateNavigationInfo();     //更新当前导航位置
                            Vibrator vibrator = (Vibrator)MainActivity.this.getSystemService(MainActivity.VIBRATOR_SERVICE);
                            vibrator.vibrate(500);
                            CommonUtils.showToast(MainActivity.this,"自动进入下一步");
                        }
                    }
                }

                if(currentModel == Model.ARNAVIMODEL){
                    ARSensorChanged(event);
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

    /****************************Activity返回的结果**************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case 1:
                if(requestCode == 0){   //扫描二维码结果返回定位
                    QRLocationQuery(data);
                }else if(requestCode == 1){ //扫码结果作为起点
                    boolean result = QRLocationQuery(data);
//                    if(!result)
//                        return;
                    navStartPointEditText.setText(YiXunAPP.mSharedPreferences.getString(Constants.String_ModelName,"起点"));
                    navStartLoc.setGroupId(YiXunAPP.mSharedPreferences.getInt(Constants.String_GroupId,0));//记录起点
                    Float x = YiXunAPP.mSharedPreferences.getFloat(Constants.String_X,0);
                    Float y = YiXunAPP.mSharedPreferences.getFloat(Constants.String_Y,0);
                    navStartLoc.setMapCoord(new FMMapCoord(x,y));
                    navEndPointEditText.setFocusable(true);
                    navEndPointEditText.requestFocus();
                    createStartImageMarker();   //显示起点图标
                }else if(requestCode == 2){     //扫码结果记录车位
                    BmobQuery<QRInfo> bmobQuery = new BmobQuery<>();
                    if(data == null)
                        return;
                    String qrId = data.getStringExtra(QRCodeActivity.QRSCANRESULT).toString();
                    bmobQuery.getObject(qrId, new QueryListener<QRInfo>() {
                        @Override
                        public void done(final QRInfo qrInfo, BmobException e) {
                            if(e == null){
                                SharedPreferences.Editor editor = YiXunAPP.mSharedPreferences.edit();
                                editor.putString(Constants.Car_MapId,qrInfo.getMapid());
                                editor.putString(Constants.Car_MapName,qrInfo.getMapname());
                                editor.putInt(Constants.Car_GroupId,qrInfo.getGroupid());
                                editor.putFloat(Constants.Car_X,qrInfo.getX());
                                editor.putFloat(Constants.Car_Y,qrInfo.getY());
                                editor.putString(Constants.Car_ModelName,qrInfo.getModelname());
                                editor.apply();
                                CommonUtils.showToast(MainActivity.this,"记录成功");
                            }else {
                                CommonUtils.showToast(MainActivity.this,"车位记录失败，请检查您的网络");
                            }
                        }
                    });
                }
                break;
            case 2:     //扫码结果作为收藏点,废弃
                Toast.makeText(MainActivity.this,"收藏成功",Toast.LENGTH_SHORT).show();
                break;
            case 3:     //更改主题
                String themeid = YiXunAPP.mSharedPreferences.getString(Constants.String_ThemeId,"3010");
                mFMMap.loadThemeById(themeid);
                mFMMap.updateMap();
                Toast.makeText(MainActivity.this,"success",Toast.LENGTH_SHORT).show();
                mFMMap.setOnFMMapThemeListener(new OnFMMapThemeListener() {
                    @Override
                    public void onSuccess(String s) {
                        Toast.makeText(MainActivity.this,"success",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String s, int i) {
                        Toast.makeText(MainActivity.this,"fail",Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case 4:     //扫码结果作为终点,废弃
                break;
            case 5:     //矫正导航
                boolean result = QRLocationQuery(data);
//                if(!result)
//                    return;
                navStartLoc.setGroupId(YiXunAPP.mSharedPreferences.getInt(Constants.String_GroupId,1));
                FMMapCoord fmMapCoord1 = new FMMapCoord();
                fmMapCoord1.x = YiXunAPP.mSharedPreferences.getFloat(Constants.String_X,0);
                fmMapCoord1.y = YiXunAPP.mSharedPreferences.getFloat(Constants.String_Y,0);
                navStartLoc.setMapCoord(fmMapCoord1);
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
                    CommonUtils.showToast(MainActivity.this,"已为您重新规划导航路线");
                }
                break;
            case 6:  //地图切换
                mFMMap.clearAllMapStatus();
                mFMMap.removeAll();
                mLocationMarker = null;//重新创建
                updateMap();
                break;
            case 7://地图搜索
                String name  = data.getStringExtra("name");
                String fid = data.getStringExtra("fid");
                int groupid = data.getIntExtra("groupid",0);
                double x = data.getDoubleExtra("x",0.0);
                double y = data.getDoubleExtra("y",0.0);
                if(requestCode == 0){   //搜索定位
                    currentLoc.setGroupId(groupid);
                    currentLoc.setMapCoord(new FMMapCoord(x,y));
                    UpdateLocateInMap();
                }else if(requestCode == 1){ //搜索作为起点
                    navStartPointEditText.setText(name);
                    navStartLoc.setGroupId(groupid);//记录起点
                    navStartLoc.setMapCoord(new FMMapCoord(x,y));
                    createStartImageMarker();   //显示起点图标
                    navEndPointEditText.setFocusable(true);
                    navEndPointEditText.requestFocus();
                } else if(requestCode == 2){    //搜索作为终点
                    navEndPointEditText.setText(name);
                    navEndLoc.setGroupId(groupid);//记录候选终点
                    navEndLoc.setMapCoord(new FMMapCoord(x,y));
                    createEndImageMarker();     //显示终点位置坐标
                    navEndPointEditText.clearFocus();
                }else if(requestCode == 3){     //搜索作为反向寻车的车位
                    SharedPreferences.Editor editor = YiXunAPP.mSharedPreferences.edit();
                    editor.putString(Constants.Car_MapId,currentMap.getMapId());
                    editor.putString(Constants.Car_MapName,currentMap.getName());
                    editor.putInt(Constants.Car_GroupId,groupid);
                    editor.putFloat(Constants.Car_X,(float)x);
                    editor.putFloat(Constants.Car_Y,(float)y);
                    editor.putString(Constants.Car_ModelName,name);
                    editor.apply();
                    CommonUtils.showToast(MainActivity.this,"记录成功");
                }
                break;
            case 8:
                if(requestCode == 0){   //点击收藏列表实现定位
                    String mapid = data.getStringExtra("mapid");
                    if(mapid.equals(currentMap.getMapId())){
                        currentLoc.setGroupId(data.getIntExtra("groupid",0));
                        currentLoc.setMapCoord(new FMMapCoord(data.getFloatExtra("x",0.0f),
                                data.getFloatExtra("y",0.0f)));
                        UpdateLocateInMap();
                    }else{
                        final OptionMaterialDialog mMaterialDialog = new OptionMaterialDialog(MainActivity.this);
                        mMaterialDialog.setTitle("切换地图")
                                .setMessage("该收藏位置不在当前地图中，确认切换？")
                                .setCanceledOnTouchOutside(false)
                                .setPositiveButton("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        SharedPreferences.Editor editor = YiXunAPP.mSharedPreferences.edit();
                                        editor.putString(Constants.String_MapId,data.getStringExtra("mapid"));
                                        editor.putString(Constants.String_MapName,data.getStringExtra("mapname"));
                                        editor.putInt(Constants.String_GroupId,data.getIntExtra("groupid",0));
                                        editor.putFloat(Constants.String_X,data.getFloatExtra("x",0.0f));
                                        editor.putFloat(Constants.String_Y,data.getFloatExtra("y",0.0f));
                                        editor.apply();
                                        mFMMap.clearAllMapStatus();
                                        mFMMap.removeAll();
                                        mLocationMarker = null;//重新创建
                                        updateMap();
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                UpdateLocateInMap();
                                            }
                                        }, 3000);//3秒后执行Runnable中的run方法
                                        mMaterialDialog.dismiss();
                                    }
                                })
                                .setNegativeButton("取消",
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mMaterialDialog.dismiss();
                                            }
                                        })
                                .setCanceledOnTouchOutside(true)
                                .show();
                    }
                }else if(requestCode == 1){     //从收藏中获取终点
                    String mapid = data.getStringExtra("mapid");
                    if(mapid.equals(currentMap.getMapId())) {
                        navEndLoc.setGroupId(data.getIntExtra("groupid", 0));
                        navEndLoc.setMapCoord(new FMMapCoord(data.getFloatExtra("x", 0.0f),
                                data.getFloatExtra("y", 0.0f)));
                        navEndPointEditText.setText(data.getStringExtra("modelname"));
                        createEndImageMarker();     //显示终点位置坐标
                        navEndPointEditText.clearFocus();
                    }else {
                        CommonUtils.showToast(MainActivity.this,"该收藏地点不在当前地图中");
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 基于返回的intent，解析二维码数据
     * @param data
     */
    boolean qrisSucess = false;
    boolean QRLocationQuery(Intent data){
        qrisSucess = false;
        BmobQuery<QRInfo> bmobQuery = new BmobQuery<>();
        if(data == null)
            return false;
        final String qrId = data.getStringExtra(QRCodeActivity.QRSCANRESULT).toString();
        /*************服务器查询二维码活码内容，结果存于本地***************/
        bmobQuery.getObject(qrId, new QueryListener<QRInfo>() {
            @Override
            public void done(final QRInfo qrInfo, BmobException e) {
                if(e == null){
                    qrisSucess = true;
                    SharedPreferences.Editor editor = YiXunAPP.mSharedPreferences.edit();
                    editor.putString(Constants.String_MapId,qrInfo.getMapid());
                    editor.putString(Constants.String_MapName,qrInfo.getMapname());
                    editor.putInt(Constants.String_GroupId,qrInfo.getGroupid());
                    editor.putFloat(Constants.String_X,qrInfo.getX());
                    editor.putFloat(Constants.String_Y,qrInfo.getY());
                    editor.putString(Constants.String_ModelName,qrInfo.getModelname());
                    editor.apply();
                    if(qrInfo.getMapid().equals(mapID)){
                        currentLoc.setGroupId(qrInfo.getGroupid());
                        currentLoc.setMapCoord(new FMMapCoord(qrInfo.getX(),qrInfo.getY()));
                        UpdateLocateInMap();
                    }else {
                        final OptionMaterialDialog mMaterialDialog = new OptionMaterialDialog(MainActivity.this);
                        mMaterialDialog.setTitle("切换地图")
                                .setMessage("该位置不在当前地图中，确认切换？")
                                .setCanceledOnTouchOutside(false)
                                .setPositiveButton("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mapID = qrInfo.getMapid();
                                        currentMap.setMapId(mapID);
                                        currentMap.setName(qrInfo.getMapname());
                                        currentLoc.setGroupId(qrInfo.getGroupid());
                                        currentLoc.setMapCoord(new FMMapCoord(qrInfo.getX(),qrInfo.getY()));

                                        mFMMap.clearAllMapStatus();
                                        mFMMap.removeAll();
                                        mLocationMarker = null;//重新创建
                                        updateMap();
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                UpdateLocateInMap();
                                            }
                                        }, 3000);//3秒后执行Runnable中的run方法
                                        mMaterialDialog.dismiss();
                                    }
                                })
                                .setNegativeButton("取消",
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mMaterialDialog.dismiss();
                                            }
                                        })
                                .setCanceledOnTouchOutside(true)
                                .show();
                    }
                }else{
                    Toast.makeText(MainActivity.this,"定位失败,请扫描指定二维码",Toast.LENGTH_SHORT).show();
                    qrisSucess=false;
                }
            }
        });
        return qrisSucess;
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


    /********************反向寻车功能***********************/
    boolean isHavingCar;    //记录当前是否有汽车
    //选择是记录车位位置还是开始反向寻车
    void CarSeekOrRecord(){
        List<String> strings = new ArrayList<>();
        strings.add("记录我的车位");
        strings.add("开始反向寻车");
        DialogUIUtils.showBottomSheetAndCancel(MainActivity.this, strings, "取消", new DialogUIItemListener() {
            @Override
            public void onItemClick(CharSequence text, int position) {
                if(position == 0){  //记录车位
                    final String[] items = {"扫码记录车位","搜索选择车位"};
                    AlertDialog.Builder listDialog = new AlertDialog.Builder(MainActivity.this);
                    listDialog.setTitle("记录车位：");
                    listDialog.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int index) {
                            //下标从0开始
                            switch (index){
                                case 0: //扫码记录车位
                                    startActivityForResult(new Intent(MainActivity.this,QRCodeActivity.class),2);
                                    break;
                                case 1: //搜索选择车位
                                    startActivityForResult(new Intent(MainActivity.this,SelectPOIActivity.class),3);
                                    break;
                            }
                        }
                    });
                    listDialog.show();
                }else if(position == 1){  //开始反向寻车
                    navFromToLayout.setVisibility(View.VISIBLE);
                    bottomLayout.setVisibility(View.GONE);
                    navStartPointEditText.requestFocus();
                    navEndPointEditText.setText(YiXunAPP.mSharedPreferences.getString(Constants.Car_ModelName,"我的车位"));
                    navEndLoc.setGroupId(YiXunAPP.mSharedPreferences.getInt(Constants.Car_GroupId,0));//记录起点
                    Float x = YiXunAPP.mSharedPreferences.getFloat(Constants.Car_X,0);
                    Float y = YiXunAPP.mSharedPreferences.getFloat(Constants.Car_Y,0);
                    navEndLoc.setMapCoord(new FMMapCoord(x,y));
                    createEndImageMarker();   //显示起点图标
                }

            }

            @Override
            public void onBottomBtnClick() {
            }
        }).show();
    }

    /***************************惯性导航******************/
    private TextView tv_velocity;// 速度
    private TextView tv_show_step;//步行计数
    private long timer = 0;// 运动时间
    private  long startTimer = 0;// 开始时间
    private  long tempTime = 0;
    private Double distance = 0.0;// 路程：米
    private Double calories = 0.0;// 热量：卡路里
    private Double velocity = 0.0;// 速度：米每秒
    private int step_length = 0;  //步长
    private int weight = 0;       //体重
    private int total_step = 0;   //走的总步数
    private Thread thread;  //定义线程对象
    private Intent stepService;
    private int step_count_length = 5;
    Handler handler = new Handler() {// Handler对象用于更新当前步数,定时发送消息，调用方法查询数据用于显示？？？？？？？？？？
        //主要接受子线程发送的数据, 并用此数据配合主线程更新UI
        //Handler运行在主线程中(UI线程中), 它与子线程可以通过Message对象来传递数据,
        //Handler就承担着接受子线程传过来的(子线程用sendMessage()方法传递Message对象，(里面包含数据)
        //把这些消息放入主线程队列中，配合主线程进行更新UI。

        @Override                  //这个方法是从父类/接口 继承过来的，需要重写一次
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);        // 此处可以更新UI

            countDistance();     //调用距离方法，看一下走了多远
            if (timer != 0 && distance != 0.0) {
                // 体重、距离
                // 跑步热量（kcal）＝体重（kg）×距离（公里）×1.036
                calories = weight * distance * 0.001;
                //速度velocity
                velocity = distance * 1000 / timer;
            } else {
                calories = 0.0;
                velocity = 0.0;
            }
            countStep();          //调用步数方法
            String step_text = tv_show_step.getText().toString();
            if(!(total_step+"").equals(step_text)){
                mLocationAPI.updateToNextPoint(step_count_length);
            }
            tv_show_step.setText(total_step + "");// 显示当前步数
            tv_velocity.setText(formatDouble(velocity));// 显示速度
        }
    };

    /**
     * 计算行走的距离
     */
    private void countDistance() {
        if (StepDetector.CURRENT_SETP % 2 == 0) {
            distance = (StepDetector.CURRENT_SETP / 2) * 3 * step_length * 0.01;
        } else {
            distance = ((StepDetector.CURRENT_SETP / 2) * 3 + 1) * step_length * 0.01;
        }
    }

    /**
     * 实际的步数
     */
    private void countStep() {
        if (StepDetector.CURRENT_SETP % 2 == 0) {
            total_step = StepDetector.CURRENT_SETP;
        } else {
            total_step = StepDetector.CURRENT_SETP +1;
        }

        total_step = StepDetector.CURRENT_SETP;
    }

    /**
     * 计算并格式化doubles数值，保留两位有效数字
     *
     * @param doubles
     * @return 返回当前路程
     */
    private String formatDouble(Double doubles) {
        DecimalFormat format = new DecimalFormat("####.##");
        String distanceStr = format.format(doubles);
        return distanceStr.equals(getString(R.string.zero)) ? getString(R.string.double_zero)
                : distanceStr;
    }

    private void addStepCounter(){
        if (SettingsActivity.sharedPreferences == null) {
            SettingsActivity.sharedPreferences = getSharedPreferences(
                    SettingsActivity.SETP_SHARED_PREFERENCES,
                    Context.MODE_PRIVATE);
        }

        //创建一个线程，监听步数变化
        if (thread == null) {
            thread = new Thread() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    super.run();
                    int temp = 0;
                    while (true) {
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        if (StepCounterService.FLAG) {
                            Message msg = new Message();
                            if (temp != StepDetector.CURRENT_SETP) {
                                temp = StepDetector.CURRENT_SETP;
                            }
                            if (startTimer != System.currentTimeMillis()) {
                                timer = tempTime + System.currentTimeMillis()
                                        - startTimer;
                            }
                            handler.sendMessage(msg);// 通知主线程
                        }
                    }
                }
            };
            thread.start();
        }

        stepService = new Intent(MainActivity.this, StepCounterService.class);
        stopService(stepService);
        StepDetector.CURRENT_SETP = 0;
        tempTime = timer = 0;
        tv_show_step.setText("0");
        tv_velocity.setText(formatDouble(0.0));
        handler.removeCallbacks(thread);

        //信息初始化
        step_length = SettingsActivity.sharedPreferences.getInt(
                SettingsActivity.STEP_LENGTH_VALUE, 70);
        weight = SettingsActivity.sharedPreferences.getInt(
                SettingsActivity.WEIGHT_VALUE, 50);
        countDistance();
        countStep();
        if ((timer += tempTime) != 0 && distance != 0.0) {  //tempTime记录运动的总时间，timer记录每次运动时间
            // 体重、距离
            // 跑步热量（kcal）＝体重（kg）×距离（公里）×1.036，换算一下
            calories = weight * distance * 0.001;
            velocity = distance * 1000 / timer;
        } else {
            calories = 0.0;
            velocity = 0.0;
        }
        tv_velocity.setText(formatDouble(velocity));
        tv_show_step.setText(total_step + "");
    }



    /*******************启动AR导航***********************/
    // 陀螺儀移動1度的圖片位移量
    private final float DISPLACEMENT = 22;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    //private SurfaceViewCallback surfaceViewCallback;
    private boolean previewing;
    private android.hardware.Camera mCamera;
    private SensorManager sensorManager;
    private int mCurrentCamIndex = 0;
    private Float tmp_x, tmp_y, tmp_z;
    private RelativeLayout group;
    private ImageView imgView;
    private GLView glView;


    private void initARNavigation()
    {

        if (!Engine.initialize(this, Constants.EasyAR_KEY)) {
            Log.e("HelloAR", "Initialization Failed.");
        }
        glView = new GLView(this);

        group = (RelativeLayout)findViewById(R.id.group);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        imgView = new ImageView(this);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pikachu);
        imgView.setImageBitmap(bitmap);
        group.addView(imgView);

        //imgView.setY(400);
        //imgView.setX(400);
    }

    private interface PermissionCallback
    {
        void onSuccess();
        void onFailure();
    }

    private HashMap<Integer, PermissionCallback> permissionCallbacks = new HashMap<Integer, PermissionCallback>();
    private int permissionRequestCodeSerial = 0;
    @TargetApi(23)
    private void requestCameraPermission(PermissionCallback callback)
    {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                int requestCode = permissionRequestCodeSerial;
                permissionRequestCodeSerial += 1;
                permissionCallbacks.put(requestCode, callback);
                requestPermissions(new String[]{Manifest.permission.CAMERA}, requestCode);
            } else {
                callback.onSuccess();
            }
        } else {
            callback.onSuccess();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (permissionCallbacks.containsKey(requestCode)) {
          PermissionCallback callback = permissionCallbacks.get(requestCode);
            permissionCallbacks.remove(requestCode);
            boolean executed = false;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    executed = true;
                    callback.onFailure();
                }
            }
            if (!executed) {
                callback.onSuccess();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (glView != null) { glView.onResume(); }
    }

    @Override
    protected void onPause()
    {
        if (glView != null) { glView.onPause(); }
        super.onPause();
    }

    public void ARSensorChanged(SensorEvent sensorEvent) {
        float[] values = sensorEvent.values;

        if( tmp_x != null && tmp_y != null && tmp_z != null )
        {
//            float difference = (float)currentAngel - (360-values[0]);
            float init_x = 400 - (float)currentAngel*DISPLACEMENT;//x目标坐标

            float x_position = 0;
            float y_position = 0;
//            float z_position = 0;

            x_position = init_x + (360-values[0])*DISPLACEMENT;


            //角度位移量
            float x_angle= tmp_x - values[0];       //角度差
            float y_angle = tmp_y - values[1];
//            float z_displacement = tmp_z - values[2];

            if( -180 > x_angle )
            {
                x_angle += 360;
            }
            else if( x_angle > 180 )
            {
                x_angle -= 360;
            }


//            x_position = imgView.getX() + x_angle * DISPLACEMENT;
            y_position = imgView.getY() + y_angle * DISPLACEMENT;

            // 旋轉超過360度的x座標位置調整
            if( x_position > 180 * DISPLACEMENT )
            {
                x_position = 180 * -DISPLACEMENT;
            }
            else if ( 180 * -DISPLACEMENT > x_position )
            {
                x_position = 180 * DISPLACEMENT;
            }

            imgView.setX(x_position);
            imgView.setY(y_position);
        }

        tmp_x = values[0];
        tmp_y = values[1];
        tmp_z = values[2];
    }

}

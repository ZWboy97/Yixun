package com.jackchance.yixun.UI.LocateInMap.LocateWays;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengmap.android.FMDevice;
import com.fengmap.android.data.OnFMDownloadProgressListener;
import com.fengmap.android.map.FMMap;
import com.fengmap.android.map.FMMapUpgradeInfo;
import com.fengmap.android.map.FMMapView;
import com.fengmap.android.map.FMViewMode;
import com.fengmap.android.map.animator.FMLinearInterpolator;
import com.fengmap.android.map.event.OnFMMapInitListener;
import com.fengmap.android.map.event.OnFMNodeListener;
import com.fengmap.android.map.event.OnFMSwitchGroupListener;
import com.fengmap.android.map.layer.FMModelLayer;
import com.fengmap.android.map.marker.FMModel;
import com.fengmap.android.map.marker.FMNode;
import com.fengmap.android.widget.FM3DControllerButton;
import com.fengmap.android.widget.FMFloorControllerComponent;
import com.fengmap.android.widget.FMZoomComponent;
import com.jackchance.yixun.Bean.Map;
import com.jackchance.yixun.Bean.MapCoord;
import com.jackchance.yixun.Constants;
import com.jackchance.yixun.R;
import com.jackchance.yixun.UI.LocateInMap.LocateActivity;
import com.jackchance.yixun.UI.Navigation.MapUI.InfoActivity;
import com.jackchance.yixun.UI.Navigation.MapUI.MainMapActivity;
import com.jackchance.yixun.Util.CommonUtils;
import com.jackchance.yixun.YiXunAPP;

/**
 * 提供基于文字搜索，地图点击选址，两种定位选址方式，用于在进入地图之前的定位
 * @author 蚍蜉
 * @version 2.0
 * Created on 2017/11/15
 */
public class ChooseToLocActivity extends AppCompatActivity implements OnFMMapInitListener,
        OnFMSwitchGroupListener,LocInMapBaseWay {

    /*定义字符串常量*/
    static public final String SELECT_LOC = "select_Loc";
    static public final String CUR_MAP = "CUR_MAP";

    private FMMap mFMMap;                               //地图引擎对象
    private FMMapView mMapView;                         //地图控件实例
    private String mapId;                               //地图id
    private Map currentMap;                             //当前地图数据信息
    private FMModelLayer modelLayer;                    //地图模型图层
    private FMModel mClickedModel;                      //所选择的模型

    private FMFloorControllerComponent mFloorComponent; //楼层切换控件实例
    private FM3DControllerButton m3DTextButton;         //三维显示控件
    private FMZoomComponent mZoomComponent;             //缩放控件

    boolean isAnimateEnd = true;                        //楼层切换动画是否结束
    private TextView messageView;                       //文字消息message
    private RelativeLayout bottomLayout;                //控制底部布局显示
    private TextView nameView;                          //底部布局--店铺名称
    private TextView floorView;                         //底部布局--楼层信息
    private TextView infoView;                          //底部布局--商铺信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_to_loc);
        messageView = (TextView)findViewById(R.id.select_message);
        bottomLayout = (RelativeLayout)findViewById(R.id.mapselect_bottom_layout);
        nameView = (TextView)findViewById(R.id.mapselect_bottom_name_textview);
        floorView = (TextView)findViewById(R.id.mapselect_bottom_floor_textview);
        infoView = (TextView)findViewById(R.id.mapselect_bottom_intro_textview);

        //读取intent意图,获得地图信息
        Intent intent = getIntent();
        currentMap = (Map)intent.getSerializableExtra(LocateActivity.CUR_MAP_LOCACTIVITY);
        mapId = currentMap.getMapId();

        //加载地图
        mMapView = (FMMapView)findViewById(R.id.select_mapview);
        mFMMap = mMapView.getFMMap();
        mFMMap.openMapById(mapId,true);
        mFMMap.setOnFMMapInitListener(this);//设置监听

        //控件事件监听逻辑
        setWidgetClickListener();

    }

    /**
     * 退出前销毁地图
     */
    @Override
    public void onBackPressed() {
        //完成之后，一定要销毁地图对象
        if (mFMMap != null) {
            mFMMap.onDestroy();
        }
        super.onBackPressed();
    }

    /**
     *OnMapInitlistrner必须实现的接口
     */
    @Override
    public void onMapInitSuccess(String s) {
        mFMMap.loadThemeById(currentMap.getThemeid());
        //设置多楼层显示
        setMultidisplay();
        //模型点击逻辑
        setMapModelClickListener();
        //添加楼层切换组件
        setFloorControlComponent();
        //添加缩放控件
        setZoomComponent();
        //添加2D3D切换控制部件
        set2D3DSwitchComponent();
    }

    /**
     * 地图加载失败处理
     */
    @Override
    public void onMapInitFailure(String s, int i) {
        CommonUtils.showToast(ChooseToLocActivity.this,s);
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
                    CommonUtils.showToast(ChooseToLocActivity.this,"地图更新完成！");
                }
                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    CommonUtils.showToast(ChooseToLocActivity.this,"正在更新地图数据！");
                }
                @Override
                public void onFailure(String mapPath, int errorCode) {
                    CommonUtils.showToast(ChooseToLocActivity.this,"地图更新失败，请检查网络！");
                }
            });
        }
        return false;
    }

    /**
     * 自定义方法，添加注册控件点击逻辑
     */
    void setWidgetClickListener(){
        /****************************确认定位按钮逻辑*******************************/
        LinearLayout setHereButton = (LinearLayout)findViewById(R.id.mapselect_bottom_sethere_button);
        setHereButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            locInMap();
            }
        });

        /************查看详情按钮逻辑**************/
        LinearLayout forMoreInfoButton = (LinearLayout)findViewById(R.id.mapselect_bottom_moreinfo_button);
        forMoreInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseToLocActivity.this,InfoActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 实现工厂模式接口，确认定位
     */
    @Override
    public void locInMap() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ChooseToLocActivity.this);
        dialog.setTitle("确认定位");
        String message = "您当前位置"+mClickedModel.getName().toString();
        dialog.setMessage(message);
        dialog.setCancelable(true);
        dialog.setPositiveButton("确认定位", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MapCoord selectLoc = new MapCoord(mClickedModel.getGroupId(),
                        mClickedModel.getCenterMapCoord());
                SharedPreferences.Editor editor = YiXunAPP.mSharedPreferences.edit();
                editor.putString(Constants.String_MapId,currentMap.getMapId());
                editor.putString(Constants.String_MapName,currentMap.getName());
                editor.putString(Constants.String_ThemeId,currentMap.getThemeid());
                editor.putInt(Constants.String_GroupId,selectLoc.getGroupId());
                editor.putFloat(Constants.String_X,(float) selectLoc.getMapCoord().x);
                editor.putFloat(Constants.String_Y,(float) selectLoc.getMapCoord().y);
                editor.apply();
                Intent intent = new Intent(ChooseToLocActivity.this,MainMapActivity.class);
                mFMMap.onDestroy();
                startActivity(intent);
                finish();
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
     * 自定义方法，添加模型点击逻辑
     */
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
                    nameView.setText(model.getName());//店铺名称
                    floorView.setText(String.valueOf(model.getGroupId()));
                    infoView.setText("主要研发，设计，生产，销售休闲类服饰");
                }
                else{
                    bottomLayout.setVisibility(View.GONE);
                    model.setSelected(false);
                    mFMMap.updateMap();
                }
                return true;
            }

            @Override
            public boolean onLongPress(FMNode node) {
                if(mClickedModel!=null){
                    mClickedModel.setSelected(false);
                    bottomLayout.setVisibility(View.GONE);
                }
                return false;
            }
        });
    }

    /**
     * 自定义方法，添加楼层切换组件
     */
    void setFloorControlComponent(){
        // 创建楼层切换控件
        mFloorComponent = new FMFloorControllerComponent(this);
        mFloorComponent.setMaxItemCount(4);//设置楼层最大显示数量
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
                    public boolean onItemSelected(int groupId, String floorName) {
                        //楼层切换
                        mFloorComponent.setSelected(groupId-1);
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
        mMapView.addComponent(mFloorComponent, 10, 400);//添加楼层控件并设置位置
    }

    /**
     * 自定义方法，添加放大缩小组件
     */
    void setZoomComponent(){
        //创建放大/缩小控件
        mZoomComponent = new FMZoomComponent(this);
        mZoomComponent.measure(0, 0);
        int width = mZoomComponent.getMeasuredWidth();
        int height = mZoomComponent.getMeasuredHeight();
        //缩放控件位置
        int offsetX = FMDevice.getDeviceWidth() - width - 10;
        int offsetY = FMDevice.getDeviceHeight() - 400 - height;
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
    void set2D3DSwitchComponent(){
        //2D/3D控件初始化
        m3DTextButton = new FM3DControllerButton(this);
        //设置初始状态为3D
        m3DTextButton.initState(true);
        m3DTextButton.measure(0, 0);
        int width = m3DTextButton.getMeasuredWidth();
        //设置3D控件位置
        mMapView.addComponent(m3DTextButton, FMDevice.getDeviceWidth() - 10 - width, 50);
        //2D/3D点击监听
        m3DTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m3DTextButton.isSelected()) {
                    m3DTextButton.setSelected(false);
                    mFMMap.setFMViewMode(FMViewMode.FMVIEW_MODE_2D);
                    mFloorComponent.setFloorMode(FMFloorControllerComponent.FMFloorMode.SINGLE);
                    setSingledisplay();
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
    void setMultidisplay(){
        //多楼层显示地图
        int[] gids = mFMMap.getMapGroupIds();    //获取地图所有的group
        int focus = 0;

        mFMMap.setMultiDisplay(gids, focus, this);
    }

    /**
     * 自定义方法，设置单楼层方式显示
     */
    void setSingledisplay(){
        //单楼层显示地图
        int[] gids = {mFMMap.getFocusGroupId()};       //获取当前地图焦点层id
        mFMMap.setMultiDisplay(gids, 0, this);
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


}

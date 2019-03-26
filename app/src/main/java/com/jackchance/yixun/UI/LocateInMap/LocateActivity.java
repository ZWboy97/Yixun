package com.jackchance.yixun.UI.LocateInMap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.jackchance.yixun.Bean.Map;
import com.jackchance.yixun.R;
import com.jackchance.yixun.UI.ChooseMap.MapDetailActivity;
import com.jackchance.yixun.UI.QRScan.QRCodeActivity;
import com.jackchance.yixun.Util.AppManager;
import com.jackchance.yixun.Util.CommonUtils;
import com.jackchance.yixun.Util.QRResultUtil;


/**
 * 地图内定位，提供二维码扫描，文字搜索，地图选择三种定位位置的方式
 * @author 蚍蜉
 * @version 2.0
 * Created on 2017/10/23
 */

public class LocateActivity extends AppCompatActivity {

    /*定义几个字符串常量，用于标识*/
    public static String CUR_MAP_LOCACTIVITY = "CUR_MAP_LOCACTIVITY";

    private Map currentMap;                     //当前地图信息
    private TextView currentMapLoction;         //界面显示当前地图名称
    private ImageButton qrLocButton;            //启动二维码定位按钮
    private ImageButton searchByNameButton;     //搜索按钮
    private ImageButton locByMapButton;         //地图选址按钮
    private ImageButton othersButton;           //其他方式按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate);
        //将当前活动加入到自定义活动管理
        AppManager.getInstance().addActivity(this);

        //从上一个活动获取当前地图信息
        Intent intent = getIntent();
        currentMap = (Map)intent.getSerializableExtra(MapDetailActivity.SELECT_MAP);
        /*************初始化实例*************/
        currentMapLoction = (TextView)findViewById(R.id.current_map_loction);
        qrLocButton = (ImageButton)findViewById(R.id.qr_button);
        searchByNameButton = (ImageButton)findViewById(R.id.search_by_name_button);
        locByMapButton = (ImageButton)findViewById(R.id.loc_by_map_button);
        othersButton = (ImageButton)findViewById(R.id.skip_loc_button);

        /***************************几种定位方式选择按钮逻辑***************************/
        currentMapLoction.setText(currentMap.getName());
        //扫描二维码定位
        qrLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent qrIntent = LocWaysFactory.startLocInMap(LocateActivity.this,
                        LocWaysFactory.QRSCAN, currentMap);
                startActivityForResult(qrIntent,1);
            }
        });
        //名称搜索定位
        searchByNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent = LocWaysFactory.startLocInMap(LocateActivity.this,
                        LocWaysFactory.SEARCH, currentMap);
                startActivity(searchIntent);
            }
        });
        //地图选址定位
        locByMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseIntent = LocWaysFactory.startLocInMap(LocateActivity.this,
                        LocWaysFactory.CHOOSE, currentMap);
                startActivity(chooseIntent);
            }
        });
        //其他定位方式
        othersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.showToast(LocateActivity.this,"更多定位方式正在接入(WiFi,惯导)");
            }
        });
    }

    /*******************处理启动QR二维码扫描返回的结果*************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null)
            return;
        String qrId = data.getStringExtra(QRCodeActivity.QRSCANRESULT).toString();
        /******服务器查询二维码活码内容，结果保存本地*****/
        QRResultUtil.analysisQRResult(qrId,LocateActivity.this);
    }
}

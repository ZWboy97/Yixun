package com.jackchance.yixun.UI.ChooseMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.jackchance.yixun.Adapter.MapListAdapter;
import com.jackchance.yixun.Bean.Map;
import com.jackchance.yixun.Constants;
import com.jackchance.yixun.R;
import com.jackchance.yixun.UI.LocateInMap.LocateActivity;
import com.jackchance.yixun.UI.MainActivity;
import com.jackchance.yixun.Util.AppManager;
import com.jackchance.yixun.YiXunAPP;
import com.longsh.optionframelibrary.OptionMaterialDialog;

/**
 * 点击选择地图数据之后，该模块展示地图基本信息，并提供进入地图的入口
 * @author 蚍蜉
 * @version 2.0
 * Created on 2017/10/3
 */

public class MapDetailActivity extends AppCompatActivity {

    static private Activity listActivity;
    static void setListContext(Activity activity){
        listActivity = activity;
    }
    public static String SELECT_MAP = "SELECT_MAP";     //字符串常量
    private Map selectMap;                              //选定的地图数据
    private Toolbar toolbar;                            //工具栏
    private CollapsingToolbarLayout collapsingToolbar;  //沉浸工具栏控件
    private ImageView mapImageView;                     //地图图片
    private TextView mapInfotextView;                   //地图详细信息
    private TextView mapLoctiontextView;                //地图地址信息
    private FloatingActionButton floatingActionButton;  //悬浮按钮，点击进入地图

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_detail);
        //将活动加入到自定义活动工具
        AppManager.getInstance().addActivity(this);

        /*****************从上一个活动获取数据******************/
        Intent intent = getIntent();
        final String MapName = intent.getStringExtra(MapListAdapter.Map_Name);
        final String MapLoction = intent.getStringExtra(MapListAdapter.Map_Loction);
        final String MapInfo = intent.getStringExtra(MapListAdapter.Map_Info);
        final String MapID = intent.getStringExtra(MapListAdapter.Map_ID);
        final int MapImageID = intent.getIntExtra(MapListAdapter.Map_Image_ID, 0);
        final String MapDetail = intent.getStringExtra(MapListAdapter.Map_Detail);
        final String MapThemeID = intent.getStringExtra(MapListAdapter.Map_Theme_ID);
        final float Init_X = intent.getFloatExtra(MapListAdapter.Map_Init_X,0);
        final float Init_Y = intent.getFloatExtra(MapListAdapter.Map_Theme_ID,0);
        final int Init_Group = intent.getIntExtra(MapListAdapter.Map_Init_Group,0);

        //控件实例化
        toolbar = (Toolbar) findViewById(R.id.toolbar_map_detail);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mapImageView = (ImageView) findViewById(R.id.map_image_detail);
        mapInfotextView = (TextView) findViewById(R.id.text_map_info);
        mapLoctiontextView = (TextView) findViewById(R.id.text_map_loction);
        //设置沉浸式标题栏
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false); //左上角返回禁用
        }
        collapsingToolbar.setTitle(MapName);
        //加载显示地图信息
        Glide.with(this).load(MapImageID).into(mapImageView);  //Glide加载地图图片
        mapInfotextView.setText(MapInfo);       //详细信息
        mapLoctiontextView.setText(MapLoction); //地址信息

        /***********悬浮按钮逻辑，点击进入商家地图*************/
        floatingActionButton = (FloatingActionButton)findViewById(R.id.float_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final OptionMaterialDialog materialDialog = new OptionMaterialDialog(MapDetailActivity.this);
                materialDialog.setTitle("地图切换")
                        .setMessage("确认切换地图吗？")
                        .setPositiveButton("确定",new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                SharedPreferences.Editor editor = YiXunAPP.mSharedPreferences.edit();
                                editor.putString(Constants.String_MapId,MapID);
                                editor.putString(Constants.String_MapName,MapName);
                                editor.putString(Constants.String_ThemeId,MapThemeID);
                                editor.putInt(Constants.String_GroupId,Init_Group);
                                editor.putFloat(Constants.String_X,Init_X);
                                editor.putFloat(Constants.String_Y,Init_Y);
                                editor.apply();
                                Intent i=new Intent();
                                i.putExtra("result", "success");
                                setResult(6,i);
                                finish();
                            }
                        })
                        .setNegativeButton("取消",new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                materialDialog.dismiss();
                            }
                        }).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

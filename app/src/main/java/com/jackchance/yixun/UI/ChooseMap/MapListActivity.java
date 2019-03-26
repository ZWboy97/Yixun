package com.jackchance.yixun.UI.ChooseMap;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jackchance.yixun.Adapter.MapListAdapter;
import com.jackchance.yixun.Bean.Map;
import com.jackchance.yixun.Bean.UserBean;
import com.jackchance.yixun.R;
import com.jackchance.yixun.UI.QRScan.QRCodeActivity;
import com.jackchance.yixun.UI.UserManager.LandActivity;
import com.jackchance.yixun.UI.UserManager.UserInfomation;
import com.jackchance.yixun.Util.AppManager;
import com.jackchance.yixun.Util.NetWorkUtils;
import com.jackchance.yixun.Util.QRResultUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 展示一系列地图资源，点击查看详情
 * @author 蚍蜉
 * @version 2.0
 * 2017/08/22
 */

public class MapListActivity extends AppCompatActivity {

    //基本交互组件
    private RecyclerView recyclerView;                  //滑动菜单
    private List<Map> mapsList;                         //List存列表数据
    private MapListAdapter adapter;                     //地图列表的适配器
    private DrawerLayout drawerLayout;                  //抽屉布局
    private SwipeRefreshLayout swipeRefreshLayout;      //下拉刷新控件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_list);

        //初始化控件实例
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        recyclerView = (RecyclerView)findViewById(R.id.map_recyclerview);

        //地图数据列表填充初始化数据
        initMaplist();                       //获取地图数据到list中
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);//设置布局管理器
        recyclerView.setLayoutManager(layoutManager);
        //设置列表适配器，填充到滑动布局
        adapter = new MapListAdapter(mapsList,this);
        recyclerView.setAdapter(adapter);

        /*******************************界面控件逻辑************************************/
        //下拉刷新逻辑
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });
        //检查网络是否可用
        NetWorkUtils.networkStateTips(MapListActivity.this);
    }
    /**
     * **刷新列表**
     */
    private void refreshList(){
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     *******初始化地图数据列表*******
     */
    private void initMaplist(){

        Map buptmap = new Map("北邮第十届大创展","bupt-innovation",R.drawable.idbupt_innovation,"北京邮电大学","3010","未知");
        Map map1 = new Map("双安商场","10322",R.drawable.id10322,"双安商场","未知");
        Map map2 = new Map("爱情海购物中心","10347",R.drawable.id10347,"爱情海购物中心","未知");
        Map map3 = new Map("停车场地图","90869",R.drawable.id90869,"停车场地图2","4001","未知");
        Map map4 = new Map("太原晋祠博物馆","90886",R.drawable.id90886,"景区地图","4003","未知");
        Map map5 = new Map("医院地图","90872",R.drawable.id90872,"医院地图2","4005","未知");
        Map map6 = new Map("图书馆地图","90877",R.drawable.id90877,"图书馆地图2","4003","未知");

        mapsList = new ArrayList<Map>();
        mapsList.add(buptmap);
        mapsList.add(map2);
        mapsList.add(map3);
        mapsList.add(map1);
        mapsList.add(map4);
        mapsList.add(map5);
        mapsList.add(map6);
    }
}



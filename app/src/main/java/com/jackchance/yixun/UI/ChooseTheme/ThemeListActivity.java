package com.jackchance.yixun.UI.ChooseTheme;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jackchance.yixun.Adapter.MapListAdapter;
import com.jackchance.yixun.Adapter.ThemeListAdapter;
import com.jackchance.yixun.Bean.Map;
import com.jackchance.yixun.Bean.Theme;
import com.jackchance.yixun.R;
import com.jackchance.yixun.Util.NetWorkUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.b.The;

/**
 * 展示一系列的主题资源，点击查看详情
 * @author 蚍蜉
 * @version 2.0
 * 2017/08/22
 */

public class ThemeListActivity extends AppCompatActivity {

    //基本交互组件
    private RecyclerView recyclerView;                  //滑动菜单
    private List<Theme> themesList;                         //List存列表数据
    private ThemeListAdapter adapter;                     //地图列表的适配器
    private SwipeRefreshLayout swipeRefreshLayout;      //下拉刷新控件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_list);

        //初始化控件实例
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        recyclerView = (RecyclerView)findViewById(R.id.map_recyclerview);

        //地图数据列表填充初始化数据
        initMaplist();                       //获取地图数据到list中
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);//设置布局管理器
        recyclerView.setLayoutManager(layoutManager);
        //设置列表适配器，填充到滑动布局
        adapter = new ThemeListAdapter(themesList,this);
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
        NetWorkUtils.networkStateTips(ThemeListActivity.this);
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

        Theme theme1 = new Theme("蓝色主题","3002",R.drawable.theme_blue);
        Theme theme2 = new Theme("蓝黑主题","3008",R.drawable.theme_bluedark);
        Theme theme3 = new Theme("春节主题","3001",R.drawable.theme_chunjie);
        Theme theme4 = new Theme("缤纷主题","3010",R.drawable.theme_colorful);
        Theme theme5 = new Theme("绿色主题","3004",R.drawable.theme_green);
        Theme theme6 = new Theme("海洋主题","1003",R.drawable.theme_haiyang);
        Theme theme7 = new Theme("景区主题","1001",R.drawable.theme_jingqu);
        Theme theme8 = new Theme("粉色主题","3007",R.drawable.theme_pink);
        themesList = new ArrayList<Theme>();
        themesList.add(theme1);
        themesList.add(theme2);
        themesList.add(theme3);
        themesList.add(theme4);
        themesList.add(theme5);
        themesList.add(theme6);
        themesList.add(theme7);
        themesList.add(theme8);
    }
}



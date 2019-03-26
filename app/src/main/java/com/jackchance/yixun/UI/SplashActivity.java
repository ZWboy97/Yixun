package com.jackchance.yixun.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import com.jackchance.yixun.Bean.UserBean;
import com.jackchance.yixun.R;
import com.jackchance.yixun.UI.ChooseMap.MapListActivity;
import com.jackchance.yixun.UI.Navigation.MapUI.MainMapActivity;
import com.jackchance.yixun.UI.UserManager.LandActivity;
import com.jackchance.yixun.YiXunAPP;


import cn.bmob.v3.BmobUser;

/**
 * 该模块为应用执行的首个活动，展示活动启动画面。判断是否是首次安装，以分别进入主界面或者登陆界面
 * @author 蚍蜉
 * @version 2.0
 * 2017/08/11
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //设置状态栏沉浸
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(option);

        //暂停1500ms进入地图界面
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                //判断是否是首次安装
                boolean isFirstTime = ((YiXunAPP)getApplication()).mSharedPreferences.getBoolean("isFirstTime", true);
                if(!isFirstTime){
                    //引导界面代码
                }
                else{
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                    //获取用户信息
//                    UserBean current_user = BmobUser.getCurrentUser(UserBean.class);
//                    if(current_user == null){
//                        //直接进入主界面
////                        startActivity(new Intent(SplashActivity.this, MapListActivity.class));
////
//
//
//                    }
//                    else{
//                        //进入登陆界面
////                        Intent intent = new Intent(SplashActivity.this,LandActivity.class);
////                        startActivity(intent);
////                        finish();
////                        startActivity(new Intent(SplashActivity.this, MapListActivity.class));
////                        finish();
//                    }
                }
            }
        }, 3000);

    }
}

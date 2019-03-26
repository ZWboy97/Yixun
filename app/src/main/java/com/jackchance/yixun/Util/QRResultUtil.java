package com.jackchance.yixun.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.jackchance.yixun.Bean.QRInfo;
import com.jackchance.yixun.Constants;
import com.jackchance.yixun.UI.Navigation.MapUI.MainMapActivity;
import com.jackchance.yixun.YiXunAPP;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

/**
 * 该方法提供二维码扫描结果的服务器查询解析
 * 基于id，将二维码定位结果保存到本地存储
 * Created by 蚍蜉 on 2017/11/18.
 */

public class QRResultUtil {

    static public ProgressDialog progressDialog;  //进度条
    static public Boolean success = false;
    static public String mapid = "";
    static public String mapname = "";
    static public float x = 0;
    static public float y = 0;
    static public int groupid = 0;
    /**
     * 基于二维码扫码结果，到服务器解析活码内容
     * 解析过程中，进度条展示
     * @param result
     * @param context
     */
    public static void analysisQRResult(final String result, final Context context){
        //进度栏显示
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("定位");
        progressDialog.setMessage("正在定位中，请稍等");
        progressDialog.show();
        //基于二维码扫码结果，到云数据库中进行检索查表
        BmobQuery<QRInfo> bmobQuery = new BmobQuery<>();
        bmobQuery.getObject(result, new QueryListener<QRInfo>() {
            @Override
            public void done(QRInfo qrInfo, BmobException e) {
                if(e == null){
                    success = true;
                    Toast.makeText(context,"定位成功",Toast.LENGTH_SHORT).show();
                    //将定位成功结果保存到本地sharepreference，以实现在本地活动共享
                    SharedPreferences.Editor editor = YiXunAPP.mSharedPreferences.edit();
                    editor.putString(Constants.String_MapId,qrInfo.getMapid());
                    editor.putString(Constants.String_MapName,qrInfo.getMapname());
                    editor.putInt(Constants.String_GroupId,qrInfo.getGroupid());
                    editor.putFloat(Constants.String_X,qrInfo.getX());
                    editor.putFloat(Constants.String_Y,qrInfo.getY());
                    editor.apply();
                    //启动地图主界面
                    Intent intent = new Intent(context,MainMapActivity.class);
                    context.startActivity(intent);
                }else{
                    //定位失败
                    Toast.makeText(context,"定位失败",Toast.LENGTH_SHORT).show();
                }
                progressDialog.cancel();    //关闭进度条
            }
        });
    }

    public static void analysisQRResult(final String result){
        BmobQuery<QRInfo> bmobQuery = new BmobQuery<>();
        bmobQuery.getObject(result, new QueryListener<QRInfo>() {
            @Override
            public void done(QRInfo qrInfo, BmobException e) {
                if(e == null){
                    success = true;
                    //将定位成功结果保存到本地sharepreference，以实现在本地活动共享
                    mapid = qrInfo.getMapid();
                    mapname = qrInfo.getMapname();
                    groupid = qrInfo.getGroupid();
                    x = qrInfo.getX();
                    y = qrInfo.getY();
                }else{
                    success = false;
                }
            }
        });
    }

}

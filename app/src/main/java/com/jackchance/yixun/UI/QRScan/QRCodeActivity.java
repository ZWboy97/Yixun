package com.jackchance.yixun.UI.QRScan;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import java.util.HashMap;
import cn.easyar.Engine;
import com.jackchance.yixun.Constants;
import com.jackchance.yixun.R;
import com.jackchance.yixun.Util.CommonUtils;
import com.jackchance.yixun.Util.QRResultUtil;
import com.jackchance.yixun.ZAssets.QRCodeScan.GLView;

/**
 * 启动二维码扫描活动
 */
public class QRCodeActivity extends AppCompatActivity {

    public static String QRSCANRESULT = "QRSCANRESULT";                 //键key
    public static StartModel startModel = StartModel.QRToLocateInMap;   //默认查看模式启动
    private static String key = Constants.EasyAR_KEY;                   //EasyArKey初始化
    private GLView glView;                                              //相机画面View

    //二维码扫描启动模式枚举
    public enum StartModel{
        QRToLocateInMap,            //扫码定位模式启动
        QRToCorrectNavi,            //导航纠正模式启动
        QRForMoreInfo,              //扫码获得更多信息
        QRWithAR,                   //QR+AR个性化展示启动
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);
        //设置屏幕常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //启动初始化AR引擎
        if (!Engine.initialize(this, key)) {
            Log.e("HelloAR", "Initialization Failed.");
        }

        //新建GLView,AR画面展示
        glView = new GLView(this);

        //实现GLView中的接口,获取扫描结果，进行处理
        GLView.QRScanResult qrScanResult = new GLView.QRScanResult(){
            @Override
            public void invoke(String result) {
                switch (startModel){
                    case QRToLocateInMap:   //扫描二维码定位
                        returnResultToPreActivity(result,1);    //将扫码结果返回到上一个activity
                        break;
                    case QRToCorrectNavi:   //扫描二维码纠正导航
                        returnResultToPreActivity(result,5);    //扫码结果，以回复码5回复
                        break;
                    case QRForMoreInfo:      //扫描二维码，获取多用途信息

                        break;
                    case QRWithAR:           //QR与AR协同
                        break;
                    default:
                        break;
                }
            }
        };
        glView.setQrScanResult(qrScanResult); //传入变量，设置监听

        //请求相机权限
        requestCameraPermission(new PermissionCallback() {
            @Override
            public void onSuccess() {
                //请求成果，将glView画面加载到preview预览界面中
                ((ViewGroup) findViewById(R.id.qrscan_preview)).addView(glView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }

            @Override
            public void onFailure() {
                //请求失败
                CommonUtils.showToast(QRCodeActivity.this,"请求相机权限失败，请允许相机权限");
            }
        });
    }

    /********************定义方法********************/
    /**设置启动模式
     *
     * @param startmodel 启动模式
     */
    static public void setStartModel(StartModel startmodel){
        startModel = startmodel;
    }

    /**将扫码结果result返回到上个活动
     *
     * @param result     二维码扫码结果字符串
     * @param resultCode 验证值
     */
    private void returnResultToPreActivity(String result,int resultCode){
        Intent intent = new Intent();
        intent.putExtra(QRSCANRESULT,result);
        setResult(resultCode,intent);           //将扫描的result返回上一个activity
        finish();
    }




    /***************************相机请求实现*************************/
    //定义一个接口
    private interface PermissionCallback
    {
        void onSuccess();       //请求成功接口
        void onFailure();       //请求失败接口
    }
    //自定义相机权限请求
    private HashMap<Integer, PermissionCallback> permissionCallbacks
            = new HashMap<Integer, PermissionCallback>();   //请求HashMap
    private int permissionRequestCodeSerial = 0;            //初始化请求码=0
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
            //版本低于23，不需要请求相机权限
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


    /**********************************将view画面与获得周期绑定***************************/
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

    //菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    //菜单选择
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.main_menu_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

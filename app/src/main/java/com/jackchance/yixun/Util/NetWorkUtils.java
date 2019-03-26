package com.jackchance.yixun.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * 用于网络可用性检查的工具类
 * @author 蚍蜉
 * created on 2017/3/2
 */
public class NetWorkUtils {
	/**
	 * 网络是否可用
	 * @param context 上下文，监测网络是否可用
	 * @return  返回结果
	 */
	public static boolean isNetworkAvailable(Context context) {

        //获取网络连接管理器
		ConnectivityManager connectivity = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);     //网络连接服务
		if (connectivity == null) {} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();  //读取所有的网络连接信息
			if (info != null) {
                //挨个检查网络，检索是否存在可用网络
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 如果没有网络，便给出toast提示
	 * @param context 上下文
	 * @return
	 */
	public static void networkStateTips(Context context) {
		if (!isNetworkAvailable(context)) {
			Toast.makeText(context, "网络故障，请先检查网络连接", Toast.LENGTH_SHORT).show();
		}
	}

}

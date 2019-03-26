package com.jackchance.yixun.Util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.jackchance.yixun.Bean.UserBean;
import com.jackchance.yixun.UI.ChooseMap.MapListActivity;
import com.jackchance.yixun.UI.UserManager.LandActivity;

import java.util.Stack;

/**
 * APP应用管理
 * @author 蚍蜉
 * Created on 2017/8/30
 */
public class AppManager {
	private static Stack<Activity> mActivityStack;
	private static AppManager mAppManager;

	private AppManager() {
	}

	/**
	 * 采用单例模式，保证一次项目中只有一个实例
	 */
	public static AppManager getInstance() {
		if (mAppManager == null) {
			mAppManager = new AppManager();
		}
		return mAppManager;
	}

	/**
	 * 添加Activity到堆栈
	 * @param activity 活动对象
	 */
	public void addActivity(Activity activity) {
		if (mActivityStack == null) {
			mActivityStack = new Stack<Activity>();
		}
		mActivityStack.add(activity);
	}

	/**
	 * 获取栈顶Activity
	 */
	public Activity getTopActivity() {
		Activity activity = mActivityStack.lastElement();
		return activity;
	}

	/**
	 * 结束栈顶Activity
	 */
	public void killTopActivity() {
		Activity activity = mActivityStack.lastElement();
		killActivity(activity);
	}

	/**
	 * 结束指定的Activity
     * @param activity 活动对象
	 */
	public void killActivity(Activity activity) {
		if (activity != null) {
			mActivityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 结束指定类名的Activity
     * @param cls 类名称.class
	 */
	public void killActivity(Class<?> cls) {
		for (Activity activity : mActivityStack) {
			if (activity.getClass().equals(cls)) {
				killActivity(activity);
			}
		}
	}

	/**
	 * 结束所有Activity
	 */
	public void killAllActivity() {
		for (int i = 0, size = mActivityStack.size(); i < size; i++) {
			if (null != mActivityStack.get(i)) {
				mActivityStack.get(i).finish();
			}
		}
		mActivityStack.clear();
	}

	/**
	 * 退出应用程序
	 */
	public void AppExit(Context context) {
		try {
			killAllActivity();
			ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			activityMgr.restartPackage(context.getPackageName());
			System.exit(0);
		} catch (Exception e) {}
	}

    /**
     * 用户退出登陆，返回登陆界面
     * @param context
     */

    public static void LandOut(final Context context){
		if(UserBean.getCurrentUser() == null){
            Toast.makeText(context,"您未登陆！",Toast.LENGTH_SHORT);
            return;
        }
        MaterialDialog.Builder landoutBuilder = new MaterialDialog.Builder(context)
                .title("应用提示")
                .theme(Theme.LIGHT)
                .content("确定退出当前登录账号" + UserBean.getCurrentUser().getUsername() + "吗？")
                .positiveText("确定").onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                        UserBean.logOut();
                        Toast.makeText(context,"注销成功！",Toast.LENGTH_SHORT).show();
                    }
                })
                .negativeText("取消")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                    }
                });

        MaterialDialog dialog = landoutBuilder.build();
        dialog.show();
    }

    /**
     * 退出APP
     * @param context  应用的上下文
     */
    public static void QuitOutAPP(final Context context){
        MaterialDialog.Builder quitoutBuilder = new MaterialDialog.Builder(context)
                .title("应用提示")
                .theme(Theme.LIGHT)
                .content("确定退出易寻导航系统?")
                .positiveText("确定").onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();

                        AppManager.getInstance().AppExit(context);
                    }
                })
                .negativeText("取消")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                    }
                });
        MaterialDialog quitoutDialog = quitoutBuilder.build();
        quitoutDialog.show();
    }

}

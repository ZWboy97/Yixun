package com.jackchance.yixun.Util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * @author 蚍蜉
 * @Version 2.0.0
 * @Description 打开或关闭软键盘
 * Created on 2017/2/9
 */
public class KeyBoardUtils {
    /**
     * 打开软键盘
     *
     * @param editText 输入框
     * @param context  上下文
     */
    public static void openKeybord(EditText editText, Context context) {
        //获取输入管理器
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);    //输入服务
        //展示软键盘
        imm.showSoftInput(editText, InputMethodManager.RESULT_SHOWN);
        //开关配置
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * 关闭软键盘
     *
     * @param editText 输入框
     * @param context  上下文
     */
    public static void closeKeybord(EditText editText, Context context) {
        //获取输入管理器
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        //隐藏软键盘
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * 关闭软键盘
     *
     * @param windowToken
     * @param context     上下文
     */
    public static void hideSoftInputMode(Context context, View windowToken) {
        InputMethodManager imm = ((InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE));
        imm.hideSoftInputFromWindow(windowToken.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 弹出软键盘
     *
     * @param context
     * @param windowToken
     */
    public static void showSoftInputMode(Context context, View windowToken) {
        final InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(windowToken, InputMethodManager.SHOW_FORCED);
    }

}

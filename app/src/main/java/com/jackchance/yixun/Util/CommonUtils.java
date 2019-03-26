package com.jackchance.yixun.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.jackchance.yixun.R;


public class CommonUtils {

    protected static Toast toast = null;            //toast消息

    public static MaterialDialog materialDialog;    //对话框

    /**
     * 退出确认，展示对话框
     * @param context
     */
    public static void showExitDialog(final Context context) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title("应用提示")
                .theme(Theme.LIGHT)
                .content("确定退出" + context.getResources().getString(R.string.app_name) + "吗?")
                .positiveText("确定").onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                        System.exit(0);
                    }
                })
                .negativeText("取消")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                    }
                });

        MaterialDialog dialog = builder.build();
        dialog.show();
    }

    /**
     * 展示进度条
     * @param context   上下文
     * @param content   展示的message文字
     */
    public static void showProgressDialog(Context context, String content) {
        //填充进度条到view
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_progress,null);
        //界面配置
        ImageView imageView = (ImageView) view.findViewById(R.id.close_dialog);
        TextView textView = (TextView) view.findViewById(R.id.dialog_meassage);
        textView.setText(content);
        materialDialog = new MaterialDialog.Builder(context)    //使用buider构建对话框
                .customView(view,true)
                .cancelable(false)
                .theme(Theme.LIGHT)
                .show();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
    }

    /**
     * 取消进度对话框
     */
    public static void hideProgressDialog() {
        materialDialog.dismiss();
    }


    /**
     * 显示Toast消息
     *
     * @param context
     * @param msg
     */
    public static void showToast(Context context, String msg) {
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }
}

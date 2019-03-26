package com.jackchance.yixun.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 *  ViewHolder
 */
public class ViewHolder {

    private final SparseArray<View> mViews;     //
    private int mPosition;                      //下标索引
    private View mConvertView;                  //适配的view
    private Context mContext;

    private ViewHolder(Context context, ViewGroup parent, int layoutId,
                       int position) {
        this.mContext = context;
        this.mPosition = position;
        this.mViews = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,   //填充view
                false);
        mConvertView.setTag(this);  // setTag
    }

    /**
     * 获得一个ViewHolder对象
     * @param context     上下文
     * @param convertView 视图
     * @param parent      父布局
     * @param layoutId    布局文件
     * @param position    下标索引
     * @return
     */
    public static ViewHolder get(Context context, View convertView,
                                 ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {
            return new ViewHolder(context, parent, layoutId, position);
        }
        return (ViewHolder) convertView.getTag();
    }

    //返回结果
    public View getConvertView() {
        return mConvertView;
    }

    /**
     * 通过控件的Id获取对于的控件，如果没有则加入views
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 为TextView设置字符串
     * @param viewId 资源文件
     * @param text   文字
     * @return
     */
    public ViewHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    /**
     * 为ImageView设置图片
     * @param viewId     资源文件
     * @param drawableId 图片
     * @return
     */
    public ViewHolder setImageResource(int viewId, int drawableId) {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);
        return this;
    }

    /**
     * 为ImageView设置图片
     * @param viewId 资源文件
     * @param bitmap 图片
     * @return
     */
    public ViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bitmap);
        return this;
    }

    public int getPosition() {
        return mPosition;
    }

    /**
     * 设置viewholder的点击
     * @param viewId
     * @param listener
     * @return
     */
    public ViewHolder setOnClickListener(int viewId,
                                         View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }


}

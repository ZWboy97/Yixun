package com.jackchance.yixun.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * 普通适配器，提供基本方法的基础抽象类
 * 继承自安卓API中的基础adapter
 */
public abstract class CommonAdapter<T> extends BaseAdapter {

    protected final int mItemLayoutId;      //需要载入的布局id
    protected LayoutInflater mInflater;     //布局填充器
    protected Context mContext;             //上下文
    protected List<T> mDatas;               //保存需要适配的数据

    /**
     * 构造函数
     * @param context       上下文
     * @param mDatas        适配数据
     * @param itemLayoutId  布局id
     */
    public CommonAdapter(Context context, List<T> mDatas, int itemLayoutId) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);     //上下文中获取布局填充器
        this.mDatas = mDatas;
        this.mItemLayoutId = itemLayoutId;
    }

    /******************************数据配置**********************************/
    public List<T> getDatas(){
        return mDatas;
    }
    public void setDatas(List<T> datas) {
        this.mDatas = datas;
    }

    /***********************实现基础适配器adapter的方法**********************/
    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 抽象方法适配转换过程,由子类实现
     * @param viewHolder    holder
     * @param item          数据
     * @param position      索引下标
     */
    public abstract void convert(ViewHolder viewHolder, T item, int position);

    /**
     * 获取getViewHolder实例
     */
    private ViewHolder getViewHolder(int position, View convertView, ViewGroup parent) {
        return ViewHolder.get(mContext, convertView, parent, mItemLayoutId, position);
    }
    /**
     * 传入数据id，以及目标适配的view，返回适配结果view
     * @param position      下标索引
     * @param convertView   目标适配的view
     * @param parent        父类view
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder = getViewHolder(position,   //获取一个viewholder
                convertView, parent);
        convert(viewHolder, getItem(position), position);       //适配转化
        return viewHolder.getConvertView();                     //适配结果view
    }


}  
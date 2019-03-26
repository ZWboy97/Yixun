package com.jackchance.yixun.Adapter;

import android.content.Context;
import android.widget.Filter;
import android.widget.Filterable;

import com.fengmap.android.map.marker.FMModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 在CommonAdapter基础上实现对数据的过滤展示
 * 实现Filterable接口
 */
public abstract class CommonFilterAdapter<T> extends CommonAdapter<T> implements Filterable {

    private ArrayFilter mArrayFilter;   //过滤器
    private ArrayList<T> mUnfiltered;   //未经过过滤的原始数据

    //构造函数
    public CommonFilterAdapter(Context context, List<T> datas, int itemLayoutId) {
        super(context, datas, itemLayoutId);
    }

    /**
     * 内部类，数据过滤器，继承实现基础filter类
     */
    private class ArrayFilter extends Filter {
        //过滤过程
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();    //过滤结果
            //输入数据为空
            if (mUnfiltered == null) {
                mUnfiltered = new ArrayList<T>(mDatas);
            }
            //将data数据给result并返回
            results.values = mDatas;
            results.count = mDatas.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            mDatas = (List<T>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            if(resultValue instanceof FMModel){
                FMModel model = (FMModel) resultValue;
                return model.getName();
            }
            return super.convertResultToString(resultValue);
        }
    }


    @Override
    public Filter getFilter() {
        if (mArrayFilter == null) {
            mArrayFilter = new CommonFilterAdapter.ArrayFilter();
        }
        return mArrayFilter;
    }


}

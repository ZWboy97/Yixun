package com.jackchance.yixun.Adapter;

import android.content.Context;

import com.jackchance.yixun.R;
import com.fengmap.android.map.marker.FMModel;

import java.util.ArrayList;

/**
 * 搜索栏搜索结果列表适配器，将匹配的地点名称展示到列表
 * @author 蚍蜉
 * created on 2017/11/22
 */
public class SearchBarAdapter extends CommonFilterAdapter<FMModel> {

    public SearchBarAdapter(Context context, ArrayList<FMModel> mapModels) {
        //上下文，数据，布局id
        super(context, mapModels, R.layout.layout_item_model_search);
    }

    /*************实现CommonAdapter的抽象方法***********/
    @Override
    public void convert(ViewHolder viewHolder, FMModel mapNode, int position) {
        viewHolder.setText(R.id.txt_model_name, mapNode.getName());
    }

}
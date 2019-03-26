package com.jackchance.yixun.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jackchance.yixun.Bean.Map;
import com.jackchance.yixun.Bean.Theme;
import com.jackchance.yixun.Constants;
import com.jackchance.yixun.R;
import com.jackchance.yixun.YiXunAPP;

import java.util.List;

/**
 * 地图列表的适配器，数据与布局容器之间的接口
 * @author 蚍蜉
 * @version 2.0
 * Created on 2017/8/27.
 */

public class ThemeListAdapter extends RecyclerView.Adapter<ThemeListAdapter.MyViewHolder>{

    private Activity listActivity;
    private List<Theme> themesList;                      //适配器数据存放
    private Context mContext;                       //父类提供的上下文

    /**
     * 内部类，viewholder，构建数据列表中的每个item布局
     */
    static class MyViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;          //item的卡片布局
        ImageView themeImage;         //item的图片
        TextView themeName;           //item的文字
        //构造函数中实例化
        public MyViewHolder(View view){
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardview);
            themeImage = (ImageView)view.findViewById(R.id.maplist_item_image);
            themeName = (TextView)view.findViewById(R.id.maplist_item_name);
        }
    }

    /**
     * 适配器Adapter的构造函数
     * @param themesList 需要展示的地图数据list
     */
    public ThemeListAdapter(List<Theme> themesList, Activity activity){
        this.themesList = themesList;
        this.listActivity = activity;
    }

    /*******************重写RecycleView.Adapter中的方法************************/
    /**
     * 覆盖重写onCreateViewHolder方法，构造viewholder
     * @param parent 父布局
     * @param viewType
     * @return MyViewHolder实例
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.mapid_list_content, parent, false);//填充布局

        /************设置holder的点击事件逻辑***************/
        final MyViewHolder holder = new MyViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //启动对于的地图详情
                int position = holder.getAdapterPosition();
                Theme theme = themesList.get(position);
                SharedPreferences.Editor editor = YiXunAPP.mSharedPreferences.edit();
                editor.putString(Constants.String_ThemeId,theme.getThemeID());
                editor.apply();
                Intent i=new Intent();
                i.putExtra("result","success");
                listActivity.setResult(3,i);
                listActivity.finish();
            }
        });
        return holder;
    }

    /**
     * 实现getItemCount()方法
     * @return 适配器maplist元素个数
     */
    @Override
    public int getItemCount() {
        return themesList.size();
    }

    /**
     * 重写onBindViewHolder，实现单项数据与布局的绑定
     * @param holder    布局实例
     * @param position  数据列表位置
     */
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Theme theme = themesList.get(position);
        holder.themeName.setText(theme.getThemeName());      //设置名字
        Glide.with(mContext).load(theme.getImageID()) //加载图片
                .into(holder.themeImage);
    }
}

package com.jackchance.yixun.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jackchance.yixun.R;
import com.jackchance.yixun.Bean.POI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/5/18.
 */
public class ListAdapter extends BaseAdapter {
    private List<POI> poiSearch = new ArrayList<POI>();
    Context ct;
    private LayoutInflater inflater;
    public ListAdapter(Context ct, List<POI> poiSearch) {
        // TODO Auto-generated constructor stub
        this.poiSearch = poiSearch;
        this.ct = ct;
        inflater = (LayoutInflater) ct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return poiSearch.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return poiSearch.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
    static class ViewHolder {

        TextView text;
        TextView subtitle;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;
        POI p = poiSearch.get(position);
        if(convertView==null){
            convertView = inflater.inflate(R.layout.simple_item,parent,false);
            holder = new ViewHolder();
            holder.text=(TextView)convertView.findViewById(R.id.title);
            holder.subtitle=(TextView)convertView.findViewById(R.id.subtitle);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.text.setText(p.getName());
        holder.subtitle.setText("所在楼层："+ Integer.toString(p.getGroupID()));
        return convertView;
    }



}
package com.jackchance.yixun.UI.GuidanceList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jackchance.yixun.R;
import com.jackchance.yixun.Util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


public class GuidanceListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private SwipeRefreshLayout swipeRefreshLayout;      //下拉刷新控件
    public static List<GuidanceItem> ITEMS = new ArrayList<GuidanceItem>();
    public SimpleItemRecyclerViewAdapter adapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guidance_list);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        View recyclerView = findViewById(R.id.guidance_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        //下拉刷新逻辑
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });

    }

    private void refreshList(){
        initlist();
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    void initlist(){
        BmobQuery<GuidanceItem> query = new BmobQuery<GuidanceItem>();
        query.setLimit(50);
        query.findObjects(new FindListener<GuidanceItem>() {
            @Override
            public void done(List<GuidanceItem> list, BmobException e) {
                if(e == null){
                    ITEMS.clear();
                    Log.e("enenenne", "list.size= "+Integer.toString(list.size()) );
                    for(GuidanceItem item:list){
                        ITEMS.add(new GuidanceItem(item.guidanceTitle,item.guidanceContent,
                                item.details,item.mapName,item.url,item.time));
                        Log.e("enenenne", "ITEMS.size = "+Integer.toString(ITEMS.size()) );

                    }
                    CommonUtils.showToast(GuidanceListActivity.this,"下拉刷新，更新成功");
                }else{
                    CommonUtils.showToast(GuidanceListActivity.this,"获取数据失败,请检查您的网络");
                }
            }
        });
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        initlist();
        adapter = new SimpleItemRecyclerViewAdapter(ITEMS);
        recyclerView.setAdapter(adapter);
        refreshList();

    }

    private void addItem(GuidanceItem item) {
        ITEMS.add(item);
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<GuidanceItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<GuidanceItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.guidance_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).guidanceTitle);
            holder.mContentView.setText(mValues.get(position).guidanceContent);
            holder.timetextView.setText(mValues.get(position).time);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, GuidanceDetailActivity.class);
                    intent.putExtra("guidance_title", holder.mItem.guidanceTitle);
                    intent.putExtra("guidence_url",holder.mItem.url);
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public final TextView timetextView;
            public GuidanceItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.guidance_title);
                mContentView = (TextView) view.findViewById(R.id.map_name);
                timetextView = (TextView) view.findViewById(R.id.time);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}

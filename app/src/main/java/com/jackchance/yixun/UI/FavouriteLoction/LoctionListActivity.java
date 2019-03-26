package com.jackchance.yixun.UI.FavouriteLoction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jackchance.yixun.Bean.QRInfo;
import com.jackchance.yixun.R;

import com.jackchance.yixun.UI.FavouriteLoction.dummy.DummyContent;
import com.jackchance.yixun.UI.LocateInMap.LocateActivity;
import com.jackchance.yixun.UI.MainActivity;
import com.jackchance.yixun.Util.CommonUtils;
import com.jackchance.yixun.Util.MyDBOpenHelper;
import com.jackchance.yixun.Util.SQLOrderDao;
import com.koushikdutta.async.Util;
import com.longsh.optionframelibrary.OptionBottomDialog;
import com.longsh.optionframelibrary.OptionCenterDialog;
import com.longsh.optionframelibrary.OptionMaterialDialog;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static com.jackchance.yixun.UI.FavouriteLoction.dummy.DummyContent.ITEMS;
import static com.jackchance.yixun.UI.MainActivity.orderDao;

public class LoctionListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private View recyclerView;
    public  List<QRInfo> orderResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loction_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        updateData();   //更新数据

        recyclerView = findViewById(R.id.loction_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);


        if (findViewById(R.id.loction_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(ITEMS));
    }

    //更新数据
    void updateData(){
        ITEMS.clear();
        orderResult = orderDao.getAllDate();
        if (orderResult != null){
            for(QRInfo qrInfo:orderResult){
                DummyContent.addItem(new DummyContent.DummyItem(qrInfo.getMapname(),
                        qrInfo.getModelname(),qrInfo.getDetail(),
                        Integer.toString(qrInfo.getGroupid())+"楼",qrInfo.getImageURL()));
            }
        }
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<DummyContent.DummyItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.loction_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final int select_position = position;
            holder.mItem = mValues.get(select_position);
            holder.mIdView.setText(mValues.get(select_position).id);
            holder.mContentView.setText(mValues.get(select_position).content);
            holder.mDetail.setText(mValues.get(select_position).details);
            holder.mTime.setText(mValues.get(select_position).time);
            holder.mGroupid.setText(mValues.get(select_position).groupid);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ArrayList<String> list = new ArrayList<>();
                    list.add("选择");
                    list.add("删除");
                    list.add("修改备注");
                    final OptionCenterDialog optionCenterDialog = new OptionCenterDialog();
                    optionCenterDialog.show(LoctionListActivity.this, list);
                    optionCenterDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            switch (position){
                                case 0:
                                    Intent intent = new Intent();
                                    intent.putExtra("mapid",orderResult.get(select_position).getMapid());
                                    intent.putExtra("mapname",orderResult.get(select_position).getMapname());
                                    intent.putExtra("modelname",orderResult.get(select_position).getModelname());
                                    intent.putExtra("x",orderResult.get(select_position).getX());
                                    intent.putExtra("y",orderResult.get(select_position).getY());
                                    intent.putExtra("groupid",orderResult.get(select_position).getGroupid());
                                    setResult(8,intent);
                                    finish();
                                    break;
                                case 1://删除
                                    final OptionMaterialDialog mMaterialDialog = new OptionMaterialDialog(LoctionListActivity.this);
                                    mMaterialDialog.setTitle("删除确认")
                                            .setMessage("确定要删除该收藏地点？")
                                            .setPositiveButton("确定", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    //注意selectposion和position的区别
                                                    String modelname = orderResult.get(select_position).getImageURL();
                                                    boolean result = MainActivity.orderDao.deleteOrder(modelname);
                                                    if(result){
                                                        CommonUtils.showToast(LoctionListActivity.this,"删除成功");
                                                    }
                                                    else {
                                                        CommonUtils.showToast(LoctionListActivity.this,"删除失败");
                                                    }
                                                    updateData();
                                                    setupRecyclerView((RecyclerView) recyclerView);
                                                    mMaterialDialog.dismiss();
                                                }
                                            })
                                            .setNegativeButton("取消",
                                                    new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            mMaterialDialog.dismiss();
                                                        }
                                                    })
                                            .setCanceledOnTouchOutside(true)
                                            .show();
                                    break;
                                case 2:
                                    changeDetail(select_position);
                                    break;
                                default:
                                    break;
                            }
                            optionCenterDialog.dismiss();
                        }
                    });

                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final CardView mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public final TextView mDetail;
            public final TextView mTime;
            public final TextView mGroupid;
            public DummyContent.DummyItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = (CardView) view.findViewById(R.id.cardview);
                mIdView = (TextView) view.findViewById(R.id.map_name);
                mContentView = (TextView) view.findViewById(R.id.loc_name);
                mDetail = (TextView) view.findViewById(R.id.detail);
                mTime = (TextView)view.findViewById(R.id.time);
                mGroupid = (TextView)view.findViewById(R.id.groupid);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }


        }

        void changeDetail(final int position) {
            LayoutInflater factory = LayoutInflater.from(LoctionListActivity.this);
            final View textEntryView = factory.inflate(R.layout.edit_detail_window, null);
            final EditText editTextName = (EditText) textEntryView.findViewById(R.id.editTextName);
            final AlertDialog.Builder ad1 = new AlertDialog.Builder(LoctionListActivity.this);
            ad1.setTitle("修改备注：");
            ad1.setMessage("填写备注");
            ad1.setIcon(android.R.drawable.ic_dialog_info);
            ad1.setView(textEntryView);
            ad1.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int i) {
                    QRInfo qrInfo = orderResult.get(position);
                    qrInfo.setDetail(editTextName.getText().toString());
                    boolean result = MainActivity.orderDao.updateOrder(qrInfo);
                    if(result){
                        CommonUtils.showToast(LoctionListActivity.this,"修改成功");
                    }else {
                        CommonUtils.showToast(LoctionListActivity.this,"修改失败");
                    }
                    updateData();
                    setupRecyclerView((RecyclerView) recyclerView);
                }
            });
            ad1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int i) {
                }
            });
            ad1.show();// 显示对话框

        }
    }
}

package com.jackchance.yixun.UI.SelectPOI;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.fengmap.android.analysis.search.FMSearchAnalyser;
import com.fengmap.android.analysis.search.FMSearchAnalysisTable;
import com.fengmap.android.analysis.search.FMSearchResult;
import com.fengmap.android.analysis.search.facility.FMSearchFacilityByTypeRequest;
import com.fengmap.android.analysis.search.model.FMSearchModelByTypeRequest;
import com.fengmap.android.map.FMGroupInfo;
import com.fengmap.android.map.FMMap;
import com.fengmap.android.map.geometry.FMMapCoord;
import com.fengmap.android.map.layer.FMGroup;
import com.fengmap.android.map.marker.FMFacility;
import com.fengmap.android.map.marker.FMModel;
import com.jackchance.yixun.Adapter.ListAdapter;
import com.jackchance.yixun.Bean.POI;
import com.jackchance.yixun.R;
import com.jackchance.yixun.UI.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectPOIActivity extends Activity {
    List<POI> poiSearch = new ArrayList<POI>() ;
    EditText editinput;
    ListView listview;
    ListAdapter adapter;
    Button search_btn;
    Button search_video;
    final String FILE_NAME = "history.txt";
    protected static final String TAG = "zjx";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("zjx","searchactivity create");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_poi);
        editinput = (EditText)findViewById(R.id.search_edit);
        listview = (ListView)findViewById(R.id.listView);
        search_btn = (Button) findViewById(R.id.button3);
        editinput.clearFocus();
        editinput.clearComposingText();

        FMSearchAnalyser mSearchAnalyser = MainActivity.getSearchAnalyser();
        FMMap map = MainActivity.getFMMap();


        for(int i=1;i<map.getMapGroupIds().length+1;i++){
            ArrayList<FMSearchResult> modelResults = mSearchAnalyser.getModels(i);
            Log.e(TAG, "modelresult.size="+Integer.toString(modelResults.size()),null);
            for (FMSearchResult result : modelResults) {
                String fid = (String) result.get("FID");
                FMModel model = map.getFMLayerProxy().queryFMModelByFid(fid);
                //添加
                String name = model.getName();
                FMMapCoord mapCoord = model.getCenterMapCoord();
                String FID = model.getFID();
                int groupid = model.getGroupId();
                if(!name.isEmpty())
                    poiSearch.add(new POI(name, mapCoord, FID, groupid));
            }
        }

        adapter = new ListAdapter(getApplicationContext(), poiSearch);
        listview.setAdapter(adapter);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Log.i("zjx", "你点击了ListView条目" + arg2);//在LogCat中输出信息
                Log.i("zjx", "" + ((POI) listview.getItemAtPosition(arg2)).getName());

                String name=((POI) listview.getItemAtPosition(arg2)).getName();
                int group = ((POI)listview.getItemAtPosition(arg2)).getGroupID();
                FMMapCoord mapCoord = ((POI)listview.getItemAtPosition(arg2)).getCenterMapCoord();
                String fid = ((POI)listview.getItemAtPosition(arg2)).getFID();
                Intent intent = new Intent();
                intent.putExtra("name",name);
                intent.putExtra("fid",fid);
                intent.putExtra("groupid",group);
                intent.putExtra("x",mapCoord.x);
                intent.putExtra("y",mapCoord.y);
                setResult(7, intent);
                finish();
            }
        });
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myText = editinput.getText().toString();
                //空字符提示、结束函数不做改动
                if (myText.length() == 0) {
                    return;
                }
//                writeHis();
                // 跳转到MainActivity
                String search_value=editinput.getText().toString();
                Bundle bundle=new Bundle();
                bundle.putSerializable("searchkey", search_value);
                Intent intent=getIntent();
                intent.putExtras(bundle);
                SelectPOIActivity.this.setResult(0,intent);
                SelectPOIActivity.this.finish();

            }
        });
        editinput.addTextChangedListener(new watcher());
        //监听回车键
        editinput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEND) {
                String myText = editinput.getText().toString();
                //空字符提示、结束函数不做改动
                if (myText.length() == 0) {
                    return false;
                }
                Bundle bundle=new Bundle();
                bundle.putSerializable("searchkey", myText);
                Intent intent=getIntent();
                intent.putExtras(bundle);
                SelectPOIActivity.this.setResult(0,intent);
                SelectPOIActivity.this.finish();
//                writeHis();
                //清空输入框
                return true;
            }
        });
    }



    class watcher implements TextWatcher{

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            if (s.length() == 0) {

                search_btn.setVisibility(View.GONE);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
            if (count == 0) {
                search_btn.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            // TODO Auto-generated method stub
            String aa = s.toString();
            Pattern p = Pattern.compile(aa);
            List<POI> we = new ArrayList<POI>();
            for(int i=0;i< poiSearch.size();i++){
                POI pp = poiSearch.get(i);
                Matcher matcher = p.matcher(pp.getName());
                if(matcher.find()){
                    we.add(pp);
                }
            }
            adapter = new ListAdapter(getApplicationContext(), we);
            listview.setAdapter(adapter);
        }

    }

}
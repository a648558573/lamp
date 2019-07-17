package com.example.administrator.light.other.collect;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.administrator.light.BaseActivity;
import com.example.administrator.light.R;

import java.util.ArrayList;
import java.util.Map;

public class GetList extends BaseActivity{
    private LinearLayout listTitle1, listTitle2;
    private ListView listView;
    private ArrayList<Map<String, Object>> list;
    private SimpleAdapter adapter;
    private String FromName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collect_get_list);
        init();
    }

    @Override
    protected void onResume() {
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onResume();
    }

    public void init(){
        listTitle1 = (LinearLayout)findViewById(R.id.listTitle1);
        listTitle2 = (LinearLayout)findViewById(R.id.listTitle2);
        listView = (ListView)findViewById(R.id.listView);
        Intent intent = getIntent();
        FromName = intent.getStringExtra("ClassName");
        list = (ArrayList<Map<String, Object>>)intent.getSerializableExtra("List");
        System.out.println(list);

        switch (FromName) {
            case "GetSingle":
                getToolbarTitle().setText("杆号核对");
                listTitle1.setVisibility(View.VISIBLE);
                listTitle2.setVisibility(View.GONE);
                adapter = new SimpleAdapter(getApplicationContext(), list,
                        R.layout.collect_get_list_item_single,
                        new String[]{"DevNo", "rod_num", "rod_real","rod_name",
                                "Area_name", "DevX", "DevY", "update_dtm"},
                        new int[]{R.id.DevNo, R.id.rod_num, R.id.rod_real, R.id.rod_name,
                                R.id.Area_name, R.id.DevX, R.id.DevY, R.id.update_dtm});
                listView.setAdapter(adapter);
                break;
            case "GetConcentrator":
                getToolbarTitle().setText("集中核对");
                listTitle1.setVisibility(View.GONE);
                listTitle2.setVisibility(View.VISIBLE);
                adapter = new SimpleAdapter(getApplicationContext(), list,
                        R.layout.collect_get_list_item_concentrator,
                        new String[]{"DevNo", "DevName", "temp_char1",
                                "Area_name", "DevX", "DevY", "update_dtm"},
                        new int[]{R.id.DevNo, R.id.DevName, R.id.temp_char1,
                                R.id.Area_name, R.id.DevX, R.id.DevY, R.id.update_dtm});
                listView.setAdapter(adapter);
                break;
            default:
                listTitle1.setVisibility(View.GONE);
                listTitle2.setVisibility(View.GONE);
                break;
        }
    }

}

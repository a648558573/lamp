package com.example.administrator.light.system;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.light.BaseActivity;
import com.example.administrator.light.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JO on 2016/5/20.
 */
public class ResultList extends BaseActivity {

    ListView resultListView;
    TextView resultTip;
    ArrayList<String> resultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_result_list);

        getToolbarTitle().setText("操作记录");

        resultListView = (ListView)findViewById(R.id.result_list);
        resultTip = (TextView)findViewById(R.id.result_tip);

        Intent intent = getIntent();
        resultList = intent.getStringArrayListExtra("resultList");
        if(resultList.size() > 0) {
            ArrayAdapter<String> resultAdapter;
            //初始化结果列表
            resultAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, resultList);
            resultListView.setAdapter(resultAdapter);
        } else {
            resultListView.setVisibility(View.GONE);
            resultTip.setVisibility(View.VISIBLE);
        }

    }
}

package com.example.administrator.light.system.ConcentratorAlarm;

import android.content.Intent;
import android.os.Bundle;

import com.example.administrator.light.BaseActivity;
import com.example.administrator.light.R;

import com.example.administrator.light.customComponent.ShowMsgLayout;

/**
 * Created by Lxr on 2016/4/28.
 */
public class ShowConcentratorAlarm extends BaseActivity {
    ShowMsgLayout number,name,time,info;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showconcentratoralarm);
        init();

        Intent intent = getIntent();
        String [] s = intent.getStringExtra("name").split("-");
        number.set_info(s[0]);
        name.set_info(s[1]);
        time.set_info(intent.getStringExtra("time"));
        info.set_info(intent.getStringExtra("info"));
    }

    public void init(){
        number = (ShowMsgLayout)findViewById(R.id.show_concentrator_num);
        name = (ShowMsgLayout)findViewById(R.id.show_concentrator_name);
        time = (ShowMsgLayout)findViewById(R.id.show_concentrator_time);
        info = (ShowMsgLayout)findViewById(R.id.show_concentrator_info);
        number.set_name("终端号");
        name.set_name("终端名");
        time.set_name("报警时间");
        info.set_name("报警信息");

        getToolbarTitle().setText("报警信息");
    }
}

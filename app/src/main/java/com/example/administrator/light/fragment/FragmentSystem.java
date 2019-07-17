package com.example.administrator.light.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.administrator.light.ExpandablelistView;
import com.example.administrator.light.R;
import com.example.administrator.light.BaseActivity;
import com.example.administrator.light.system.ConcentratorAlarm.ConcentratorAlarm;
import com.example.administrator.light.system.ConcentratorAlarm.ConcentratorAlarm1;
import com.example.administrator.light.system.SingleAlarm.SingleTerminal;

/**
 * Created by Lxr on 2016/4/21.
 */
public class FragmentSystem extends BaseActivity {

    LinearLayout fragment_system_map,fragment_system_alarm,fragment_system_parameter,
            fragment_system_timecontrol, fragment_system_switch,fragment_system_sldimming,
            fragment_system_slalarm,fragment_system_sltcsetting;

    @Override
    public void onCreate(
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_system);
        initview();
    }
    //获取界面id
    public void initview()
    {
        fragment_system_map = (LinearLayout) findViewById(R.id.fragment_system_map);
        fragment_system_alarm = (LinearLayout)findViewById(R.id.fragment_system_alarm);
        fragment_system_parameter = (LinearLayout)findViewById(R.id.fragment_system_parameter);
        fragment_system_timecontrol = (LinearLayout)findViewById(R.id.fragment_system_timecontrol);
        fragment_system_timecontrol.setVisibility(View.VISIBLE);
        fragment_system_switch = (LinearLayout)findViewById(R.id.fragment_system_switch);
        fragment_system_sldimming = (LinearLayout)findViewById(R.id.fragment_system_sldimming);
        fragment_system_slalarm = (LinearLayout)findViewById(R.id.fragment_system_slalarm);
        //fragment_system_sltimecontrol = (LinearLayout)rootview.findViewById(R.id.fragment_system_sltimecontrol);
        fragment_system_sltcsetting = (LinearLayout)findViewById(R.id.fragment_system_sltcsetting);
        listen();
    }
    //监听函数
    public void listen()
    {
        fragment_system_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //    Toast.makeText(getActivity(),"map",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("from","map");
                intent.setClass(FragmentSystem.this,ExpandablelistView.class);
                startActivity(intent);
            }
        });
        fragment_system_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(getActivity() , ConcentratorAlarm.class);
                Intent intent = new Intent(FragmentSystem.this , ConcentratorAlarm1.class);
                startActivity(intent);
            }
        });
        fragment_system_parameter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   Toast.makeText(getActivity(),"parameter",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("from","parameter");
                intent.setClass(FragmentSystem.this,ExpandablelistView.class);
                startActivity(intent);
            }
        });
        fragment_system_timecontrol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   Toast.makeText(getActivity(),"timecontrol",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("from","timecontrol");
                intent.setClass(FragmentSystem.this,ExpandablelistView.class);
                startActivity(intent);
            }
        });
        fragment_system_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(getActivity(),"switch",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("from","switch");
                intent.setClass(FragmentSystem.this,ExpandablelistView.class);
                startActivity(intent);
            }
        });
        fragment_system_sldimming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(getActivity(),"sldimming",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("from","sldimming");
                intent.setClass(FragmentSystem.this,ExpandablelistView.class);
                startActivity(intent);
            }
        });
        fragment_system_slalarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Toast.makeText(getActivity(),"slalarm",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(FragmentSystem.this,SingleTerminal.class);
                startActivity(intent);
            }
        });
//        fragment_system_sltimecontrol.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               // Toast.makeText(getActivity(),"sltimecontrol",Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent();
//                intent.putExtra("from","sltimecontrol");
//                intent.setClass(getActivity(),ExpandablelistView.class);
//                startActivity(intent);
//            }
//        });
        fragment_system_sltcsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(),"sltcsetting",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("from","sltcsetting");
                intent.setClass(FragmentSystem.this,ExpandablelistView.class);
                startActivity(intent);
            }
        });
    }
}
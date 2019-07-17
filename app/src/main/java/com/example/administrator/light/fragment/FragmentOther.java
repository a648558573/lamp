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
import com.example.administrator.light.BaseActivity;
import com.example.administrator.light.R;
import com.example.administrator.light.other.collect.Collection;

/**
 * Created by Lxr on 2016/4/21.
 */
public class FragmentOther  extends BaseActivity {

    LinearLayout fragment_other_energy_query, fragment_other_parameter_figure, fragment_other_asset_ratio,
            fragment_other_daily, fragment_other_single_parameter_history,
            fragment_other_centralized_fault_query, fragment_other_single_fault_query,
            fragment_other_video, fragment_other_collect;

    @Override
    public void onCreate(
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_other);
        initview();
    }

    //获取界面id
    public void initview() {
        fragment_other_energy_query = (LinearLayout)findViewById(R.id.fragment_other_energy_query);
        fragment_other_parameter_figure = (LinearLayout)findViewById(R.id.fragment_other_parameter_figure);
        fragment_other_asset_ratio = (LinearLayout)findViewById(R.id.fragment_other_single_rate);
        fragment_other_daily = (LinearLayout)findViewById(R.id.fragment_other_daily);
        fragment_other_single_parameter_history = (LinearLayout)findViewById(R.id.fragment_other_single_parameter);
        fragment_other_centralized_fault_query = (LinearLayout)findViewById(R.id.fragment_other_fault_query);
        fragment_other_single_fault_query = (LinearLayout)findViewById(R.id.fragment_other_single_fault_query);
        fragment_other_video = (LinearLayout)findViewById(R.id.fragment_other_video);
        fragment_other_collect = (LinearLayout)findViewById(R.id.fragment_other_collect);
        listen();
    }
    //监听函数
    public void listen() {
        fragment_other_energy_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Toast.makeText(getActivity(), "energy query", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("from","energy_query");
                intent.setClass(FragmentOther.this,ExpandablelistView.class);
                startActivity(intent);
            }
        });
        fragment_other_parameter_figure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   Toast.makeText(getActivity(),"parameter figure",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("from","three_phase");
                intent.setClass(FragmentOther.this,ExpandablelistView.class);
                startActivity(intent);
            }
        });
        fragment_other_asset_ratio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(getActivity(), "asset ratio", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("from","asset_ratio");
                intent.setClass(FragmentOther.this,ExpandablelistView.class);
                startActivity(intent);
            }
        });
        fragment_other_daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   Toast.makeText(getActivity(),"daily",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("from","daily");
                intent.setClass(FragmentOther.this,ExpandablelistView.class);
                startActivity(intent);
            }
        });
        fragment_other_single_parameter_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(getActivity(), "single light parameter history", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("from","single_parameter_history");
                intent.setClass(FragmentOther.this,ExpandablelistView.class);
                startActivity(intent);
            }
        });
        fragment_other_centralized_fault_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), "centralized fault query", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("from", "centralized_fault_query");
                intent.setClass(FragmentOther.this, ExpandablelistView.class);
                startActivity(intent);
            }
        });
        fragment_other_single_fault_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Toast.makeText(getActivity(),"single light fault query",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("from", "single_fault_query");
                intent.setClass(FragmentOther.this, ExpandablelistView.class);
                startActivity(intent);
            }
        });
        fragment_other_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FragmentOther.this,"该功能暂时还没有",Toast.LENGTH_SHORT).show();
            }
        });
        fragment_other_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(getActivity(),"collect",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(FragmentOther.this, Collection.class);
                startActivity(intent);
            }
        });
    }

}
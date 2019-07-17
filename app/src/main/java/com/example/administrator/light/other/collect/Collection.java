package com.example.administrator.light.other.collect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.administrator.light.BaseActivity;
import com.example.administrator.light.R;

/**
 * Created by JO on 2016/5/26.
 */
public class Collection extends BaseActivity implements View.OnClickListener{

    RelativeLayout layout1, layout2, layout3, layout4, layout5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection);
        init();
    }

    private void init() {
        getToolbarTitle().setText("采集");

        layout1 = (RelativeLayout)findViewById(R.id.collect_layout1);
        layout2 = (RelativeLayout)findViewById(R.id.collect_layout2);
        layout3 = (RelativeLayout)findViewById(R.id.collect_layout3);
        layout4 = (RelativeLayout)findViewById(R.id.collect_layout4);
        layout5 = (RelativeLayout)findViewById(R.id.collect_layout5);

        layout1.setOnClickListener(this);
        layout2.setOnClickListener(this);
        layout3.setOnClickListener(this);
        layout4.setOnClickListener(this);
        layout5.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.collect_layout1:
                Intent intent1 = new Intent();
                intent1.setClass(Collection.this, SetSingle.class);
                startActivity(intent1);
                break;
            case R.id.collect_layout2:
                Intent intent2 = new Intent();
                intent2.setClass(Collection.this, SetConcentrator.class);
                startActivity(intent2);
                break;
            case R.id.collect_layout3:
                Intent intent3 = new Intent();
                intent3.setClass(Collection.this, UploadPic.class);
                startActivity(intent3);
                break;
            case R.id.collect_layout4:
                Intent intent4 = new Intent();
                intent4.setClass(Collection.this, GetSingle.class);
                startActivity(intent4);
                break;
            case R.id.collect_layout5:
                Intent intent5 = new Intent();
                intent5.setClass(Collection.this, GetConcentrator.class);
                startActivity(intent5);
                break;
            default:
                break;
        }
    }

}

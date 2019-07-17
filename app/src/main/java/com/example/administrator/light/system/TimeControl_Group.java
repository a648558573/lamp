package com.example.administrator.light.system;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.administrator.light.BaseActivity;
import com.example.administrator.light.R;
import com.example.administrator.light.VollyQueue;
import com.example.administrator.light.util.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class TimeControl_Group extends BaseActivity {
    private String rootURL, userName, password, grade;
    private String groupname, DevNo;
    private ArrayList<String> childList;
    private int mHour, mMinute;
    private int list_nums = 0;
    private String week_str;

    private LinearLayout timeLayout;
    private Spinner spinner1, spinner2, spinner3, spinner4;
    private Button btOn1, btOff1, btOn2, btOff2, btOn3, btOff3, btOn4, btOff4;
    private TimePickerDialog TimePickerDialog0;
    private Button showTime;
    private CheckBox checkBox1, checkBox2, checkBox3, checkBox4,
            checkBox5, checkBox6, checkBox7, checkBox8;
    private Spinner spinner;
    private List<String> timeSpList = new ArrayList<String>();
    private ArrayAdapter<String> timeSpAdapter;
    private ListView resultListView;//执行结果listview
    private List<String> ruseltList = new ArrayList<String>();//执行结果列
    private ArrayAdapter<String> resultAdapter;
    private List<Boolean> booleanList = new ArrayList<Boolean>();//使能列
    private Button btSetGTime, btSetGWeek;

    @Override
    public void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.timecontrol_group);
        init();  //初始化标题栏和界面
        initListener();//监听事件
    }
    /*
    * 初始化标题栏和界面
    * */
    public void init() {
        rootURL = (String) SharedPreferencesUtils.getParam(this, "rootURL", "");
        userName = (String) SharedPreferencesUtils.getParam(this, "username", "");
        password = (String) SharedPreferencesUtils.getParam(this, "password", "");
        grade = (String) SharedPreferencesUtils.getParam(this, "grade", "2") ;//3
        Intent intent = getIntent();
        groupname = intent.getStringExtra("groupname");
        childList = intent.getStringArrayListExtra("childList");
        //DevNo = groupname.split("-")[0].trim();

        getToolbarTitle().setText(groupname);
        //组别
        timeLayout = (LinearLayout)findViewById(R.id.layout);
        spinner1 = (Spinner)findViewById(R.id.sp_1);
        spinner2 = (Spinner)findViewById(R.id.sp_2);
        spinner3 = (Spinner)findViewById(R.id.sp_3);
        spinner4 = (Spinner)findViewById(R.id.sp_4);
        btOn1 = (Button)findViewById(R.id.on1_txt);
        btOff1 = (Button)findViewById(R.id.off1_txt);
        btOn2 = (Button)findViewById(R.id.on2_txt);
        btOff2 = (Button)findViewById(R.id.off2_txt);
        btOn3 = (Button)findViewById(R.id.on3_txt);
        btOff3 = (Button)findViewById(R.id.off3_txt);
        btOn4 = (Button)findViewById(R.id.on4_txt);
        btOff4 = (Button)findViewById(R.id.off4_txt);
        //spinner样式
        spinner = (Spinner)findViewById(R.id.spinner);
        timeSpList.add("时间段1");
        timeSpList.add("时间段2");
        timeSpAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_display_style1, R.id.spinner_text, timeSpList);
        timeSpAdapter.setDropDownViewResource(R.layout.spinner_dropdown_style);
        spinner.setAdapter(timeSpAdapter);
        //单选框
        checkBox1 = (CheckBox)findViewById(R.id.checkBox1);
        checkBox2 = (CheckBox)findViewById(R.id.checkBox2);
        checkBox3 = (CheckBox)findViewById(R.id.checkBox3);
        checkBox4 = (CheckBox)findViewById(R.id.checkBox4);
        checkBox5 = (CheckBox)findViewById(R.id.checkBox5);
        checkBox6 = (CheckBox)findViewById(R.id.checkBox6);
        checkBox7 = (CheckBox)findViewById(R.id.checkBox7);
        checkBox8 = (CheckBox)findViewById(R.id.checkBox8);
        //ListView
        resultListView = (ListView)findViewById(R.id.resultRistView);
        resultAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ruseltList);
        resultListView.setAdapter(resultAdapter);

        btSetGTime = (Button)findViewById(R.id.btn_set_G_time);
        btSetGWeek = (Button)findViewById(R.id.btn_set_G_week);
    }
    /*
    * 监听事件
    * */
    public void initListener() {
         View.OnClickListener ocl= new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String URL = null;
                try {
                    switch (view.getId())  {
                        case R.id.btn_set_G_time:
                            URL = rootURL + "/wcf/set_group_onofftime?group_name="
                                    + URLEncoder.encode(groupname, "utf-8").trim()//selNode
                                    + "&group_typ="     + URLEncoder.encode(groupname, "utf-8").trim()//selgroup
                                    + "&term_str="
                                    + URLEncoder.encode(spinner.getSelectedItem().toString(), "utf-8").trim()
                                    + "&time1_on="      + btOn1.getText().toString().trim()
                                    + "&time1_off="     + btOff1.getText().toString().trim()
                                    + "&team_set1_int=" + spinner1.getSelectedItemPosition() + ""
                                    + "&time2_on="      + btOn2.getText().toString().trim()
                                    + "&time2_off="     + btOff2.getText().toString().trim()
                                    + "&team_set2_int=" + spinner2.getSelectedItemPosition() + ""
                                    + "&time3_on="      + btOn3.getText().toString().trim()
                                    + "&time3_off="     + btOff3.getText().toString().trim()
                                    + "&team_set3_int=" + spinner3.getSelectedItemPosition() + ""
                                    + "&time4_on="      + btOn4.getText().toString().trim()
                                    + "&time4_off="     + btOff4.getText().toString().trim()
                                    + "&team_set4_int=" + spinner4.getSelectedItemPosition() + ""
                                    + "&log_name="       + URLEncoder.encode(userName, "utf-8").trim()
                                    + "&log_pass="       + password + "&sn_node_mode=1";
                            break;
                        case R.id.btn_set_G_week:
                            URL = rootURL + "/wcf/set_group_week?group_name="
                                    + URLEncoder.encode(groupname, "utf-8").trim()//selNode
                                    + "&group_typ=" + URLEncoder.encode(groupname, "utf-8").trim()//selgroup
                                    + "&week1_int=" + week_str.trim().substring(0, 1).trim()
                                    + "&week2_int=" + week_str.trim().substring(1, 2).trim()
                                    + "&week3_int=" + week_str.trim().substring(2, 3).trim()
                                    + "&week4_int=" + week_str.trim().substring(3, 4).trim()
                                    + "&week5_int=" + week_str.trim().substring(4, 5).trim()
                                    + "&week6_int=" + week_str.trim().substring(5, 6).trim()
                                    + "&week7_int=" + week_str.trim().substring(6, 7).trim()
                                    + "&week8_int=" + week_str.trim().substring(7, 8).trim()
                                    + "&log_name="  + URLEncoder.encode(userName, "utf-8").trim()
                                    + "&log_pass="  + password + "&sn_node_mode=1";
                            break;
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if(URL == null) {
                    return;
                }
                set_group(URL);
            }
        };
        btSetGTime.setOnClickListener(ocl);
        btSetGWeek.setOnClickListener(ocl);
        View.OnClickListener ocl1= new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(TimeControl_Group.this,"time",Toast.LENGTH_SHORT).show();
                showTime=(Button) view;
                String temp_str = showTime.getText().toString();
                try {
                    mHour = Integer.parseInt(temp_str.split(":")[0]);
                    mMinute = Integer.parseInt(temp_str.split(":")[1]);
                } catch (Exception ex) {
                    mHour=0;
                    mMinute=0;
                }
                TimePickerDialog0= new TimePickerDialog(TimeControl_Group.this, mTimeSetListener, mHour, mMinute, true);
                TimePickerDialog0.show();
            }
        };
        btOn1.setOnClickListener(ocl1);
        btOff1.setOnClickListener(ocl1);
        btOn2.setOnClickListener(ocl1);
        btOff2.setOnClickListener(ocl1);
        btOn3.setOnClickListener(ocl1);
        btOff3.setOnClickListener(ocl1);
        btOn4.setOnClickListener(ocl1);
        btOff4.setOnClickListener(ocl1);
    }

    //时钟设置
    private TimePickerDialog.OnTimeSetListener mTimeSetListener
            = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mHour = hourOfDay;
            mMinute = minute;
            showTime.setText(new StringBuilder().append(mHour).append(":").append((mMinute < 10) ? "0" + mMinute : mMinute));
        }
    };

    //设置
    private void set_group(String URL) {
        if(grade.trim().equals("3")) {
            ruseltList.add(0, "(" + list_nums + ")失败!你是查询员,没有设置的权限! ");
            resultAdapter.notifyDataSetChanged();
            return;
        }
        week_str = "";
        if (checkBox1.isChecked()) {
            week_str = week_str.trim() + "1";
        } else {
            week_str = week_str.trim() + "0";
        }
        if (checkBox2.isChecked()) {
            week_str = week_str.trim() + "1";
        } else {
            week_str = week_str.trim() + "0";
        }
        if (checkBox3.isChecked()) {
            week_str = week_str.trim() + "1";
        } else {
            week_str = week_str.trim() + "0";
        }
        if (checkBox4.isChecked()) {
            week_str = week_str.trim() + "1";
        } else {
            week_str = week_str.trim() + "0";
        }
        if (checkBox5.isChecked()) {
            week_str = week_str.trim() + "1";
        } else {
            week_str = week_str.trim() + "0";
        }
        if (checkBox6.isChecked()) {
            week_str = week_str.trim() + "1";
        } else {
            week_str = week_str.trim() + "0";
        }
        if (checkBox7.isChecked()) {
            week_str = week_str.trim() + "1";
        } else {
            week_str = week_str.trim() + "0";
        }
        if (checkBox8.isChecked()) {
            week_str = week_str.trim() + "1";
        } else {
            week_str = week_str.trim() + "0";
        }

        final ProgressDialog dialog = ProgressDialog.show(TimeControl_Group.this, null, "设置中...", true);
        if(URL == null) {
            dialog.dismiss();
            return;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if(!s.isEmpty() && !s.trim().equals("")) {
                            dialog.dismiss();
                            System.out.println(s);
                            try {
                                JSONObject jsobObj = new JSONObject(s);
                                String  temp_str=jsobObj.getString("rslt");
                                list_nums++;
                                ruseltList.add(0, "(" + list_nums + ")" + temp_str.trim());
                                resultAdapter.notifyDataSetChanged();
                                saveLog(ruseltList.get(0).toString().trim());
                            } catch (JSONException e) {
                                Toast.makeText(TimeControl_Group.this,"加载失败",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(TimeControl_Group.this, "出错了，请检查一下网络", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        VollyQueue.getHttpQueues().add(stringRequest);
    }

    private void saveLog(String rslt_str) {
        try {
            String URL = rootURL
                    + "/wcf/savelog?logname=" + URLEncoder.encode(userName, "utf-8").trim()
                    + "&logtype=" + URLEncoder.encode("(手机用户)", "utf-8").trim()
                    + "&logstr=" + URLEncoder.encode(rslt_str, "utf-8").trim();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(TimeControl_Group.this, "网络异常", Toast.LENGTH_SHORT).show();
                }
            });
            stringRequest.setTag("");
            VollyQueue.getHttpQueues().add(stringRequest);
        } catch (Exception e) {
            Toast.makeText(TimeControl_Group.this, "网络故障!!!", Toast.LENGTH_SHORT).show();
        }
    }

}

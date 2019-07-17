package com.example.administrator.light.system;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.administrator.light.customComponent.Time;
import com.example.administrator.light.util.SharedPreferencesUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wujia on 2016/5/5.
 */
public class TimeControl extends BaseActivity {
    //时控
    private String rootURL, userName, grade;
    private String childname, DevNo;//单灯号
    private int mHour, mMinute;
    private int Uid_store = 0; //当前记录流水号  用于判断是否有新数椐
    private int list_nums = 0;
    private String week_str;

    private LinearLayout timeLayout;
    private Time time1,time2,time3,time4,time5,time6,time7,time8;
    private TimePickerDialog TimePickerDialog0;
    private Button showTime;
    private Button bu_ref,bu_get,bu_set;
    private CheckBox checkBox1,checkBox2,checkBox3,checkBox4,checkBox5,checkBox6,checkBox7,checkBox8;
    private Spinner spinner;
    private List<String> spinner_list = new ArrayList<String>();
    private ArrayAdapter<String> myAdapter;
    private ListView listView;//执行结果listview
    private List<String> rsltlist = new ArrayList<String>();//执行结果列
    private ArrayAdapter<String> listspadp;
    private List<Boolean> booleanList = new ArrayList<Boolean>();//使能列

    @Override
    public void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.timecontrol);
        init();  //初始化标题栏和界面
        initNum();   //初始化路灯数目
        initName();  //初始化路灯信息
        initListener();//监听事件
        refresh();   //刷新界面
    }
    /*
    * 初始化标题栏和界面
    * */
    public void init() {
        rootURL = (String) SharedPreferencesUtils.getParam(this, "rootURL", "");
        userName = (String) SharedPreferencesUtils.getParam(this, "username", "");
        grade = (String) SharedPreferencesUtils.getParam(this, "grade", "2") ;//3
        Intent intent = getIntent();
        childname = intent.getStringExtra("childname");
        DevNo = childname.split("-")[0].trim();

        getToolbarTitle().setText(childname);
        timeLayout = (LinearLayout)findViewById(R.id.layout);
        //button
        bu_ref = (Button)findViewById(R.id.toRefresh);
        bu_get = (Button)findViewById(R.id.toGet);
        bu_set = (Button)findViewById(R.id.toSet);
        //自定义界面
        time1 = (Time)findViewById(R.id.time1);
        time2 = (Time)findViewById(R.id.time2);
        time3 = (Time)findViewById(R.id.time3);
        time4 = (Time)findViewById(R.id.time4);
        time5 = (Time)findViewById(R.id.time5);
        time6 = (Time)findViewById(R.id.time6);
        time7 = (Time)findViewById(R.id.time7);
        time8 = (Time)findViewById(R.id.time8);
        //单选框
        checkBox1 = (CheckBox)findViewById(R.id.checkBox1);
        checkBox2 = (CheckBox)findViewById(R.id.checkBox2);
        checkBox3 = (CheckBox)findViewById(R.id.checkBox3);
        checkBox4 = (CheckBox)findViewById(R.id.checkBox4);
        checkBox5 = (CheckBox)findViewById(R.id.checkBox5);
        checkBox6 = (CheckBox)findViewById(R.id.checkBox6);
        checkBox7 = (CheckBox)findViewById(R.id.checkBox7);
        checkBox8 = (CheckBox)findViewById(R.id.checkBox8);
        //spinner样式
        spinner = (Spinner)findViewById(R.id.spinner);
        spinner_list.add("时间段1");
        spinner_list.add("时间段2");
        myAdapter = new ArrayAdapter<String>(this, R.layout.spinner_display_style1, R.id.spinner_text,spinner_list);
        myAdapter.setDropDownViewResource(R.layout.spinner_dropdown_style);
        spinner.setAdapter(myAdapter);
        //ListView
        listView = (ListView)findViewById(R.id.resultRistView);
        listspadp = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rsltlist );
        listView.setAdapter(listspadp);
    }
    /*
    * 监听事件
    * */
    public void initListener() {
         View.OnClickListener ocl= new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId())  {
                    case R.id.toRefresh:
                        refresh();
                        break;
                    case R.id.toGet:
                        get_onofftime();
                        handler.postDelayed(runnable, 4000);
                        break;
                    case R.id.toSet:
                        set();
                        handler.postDelayed(runnable, 4000);
                        break;
                }
            }
        };
        bu_ref.setOnClickListener(ocl);
        bu_get.setOnClickListener(ocl);
        bu_set.setOnClickListener(ocl);
        View.OnClickListener ocl1= new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(TimeControl.this,"time",Toast.LENGTH_SHORT).show();
                showTime=(Button) view;
                String temp_str= showTime.getText().toString();
                try {
                    mHour = Integer.parseInt(temp_str.split(":")[0]);
                    mMinute = Integer.parseInt(temp_str.split(":")[1]);
                } catch (Exception ex) {
                    mHour=0;
                    mMinute=0;
                }
                TimePickerDialog0= new TimePickerDialog(TimeControl.this, mTimeSetListener, mHour, mMinute, true); //点击时候显示的是当前的时间
                TimePickerDialog0.show();
            }
        };
        time1.time_bu_on_listen(ocl1);
        time2.time_bu_on_listen(ocl1);
        time3.time_bu_on_listen(ocl1);
        time4.time_bu_on_listen(ocl1);
        time5.time_bu_on_listen(ocl1);
        time6.time_bu_on_listen(ocl1);
        time7.time_bu_on_listen(ocl1);
        time8.time_bu_on_listen(ocl1);

        time1.time_bu_off_listen(ocl1);
        time2.time_bu_off_listen(ocl1);
        time3.time_bu_off_listen(ocl1);
        time4.time_bu_off_listen(ocl1);
        time5.time_bu_off_listen(ocl1);
        time6.time_bu_off_listen(ocl1);
        time7.time_bu_off_listen(ocl1);
        time8.time_bu_off_listen(ocl1);
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

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            refresh();
        }
    };

    /*
    * 初始化路灯个数*/
    public void initNum() {
        String validateURL = rootURL + "/Onoff/Gset_time_class?Dev_id=" + DevNo;
        StringRequest stringRequest3 = new StringRequest(Request.Method.GET, validateURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        System.out.println(s);
                        if(!s.isEmpty() && !s.trim().equals("")) {
                            try {
                                JSONObject jsobObj = new JSONObject(s);
                                JSONArray json_arr = jsobObj.getJSONArray("enable_time_class");
                                booleanList.clear();
                                for (int i = 0; i < 8; i++) {
                                    boolean btemp = json_arr.getBoolean(i);
                                    booleanList.add(btemp);
                                    if(btemp == true ) {
                                        timeLayout.getChildAt(i).setVisibility(View.VISIBLE);
                                    } else {
                                        timeLayout.getChildAt(i).setVisibility(View.GONE);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "用户名加载失败!!!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "用户名加载找不到接口!!!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(),
                        "出错了，请检查一下网络" + "\ninitNum:" + volleyError.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        VollyQueue.getHttpQueues().add(stringRequest3);
    }
    /*
    *初始化路灯信息
    * */
    public void initName() {
        String validateURL = rootURL + "/Onoff/Gdev_addr_light?Dev_id=" + DevNo;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, validateURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (s != null && !s.trim().equals("")) {
                            System.out.println(s);
                            try {
                                JSONObject jsobObj  = new JSONObject(s);
                                JSONObject jsobObj2= jsobObj.getJSONObject("dev_addr_light");
                                if(jsobObj2.getString("addr_jing1").trim().equals("")){
                                    time1.time_text_settext("第1路");
                                } else {
                                    time1.time_text_settext(jsobObj2.getString("addr_jing1").trim());
                                }
                                if(jsobObj2.getString("addr_jing2").trim().equals("")){
                                    time2.time_text_settext("第2路");
                                } else {
                                    time2.time_text_settext(jsobObj2.getString("addr_jing2").trim());
                                }
                                if(jsobObj2.getString("addr_jing3").trim().equals("")){
                                    time3.time_text_settext("第3路");
                                } else {
                                    time3.time_text_settext(jsobObj2.getString("addr_jing3").trim());
                                }
                                if(jsobObj2.getString("addr_jing4").trim().equals("")){
                                    time4.time_text_settext("第4路");
                                } else {
                                    time4.time_text_settext(jsobObj2.getString("addr_jing4").trim());
                                }
                                if(jsobObj2.getString("addr_jing5").trim().equals("")){
                                    time5.time_text_settext("第5路");
                                } else {
                                    time5.time_text_settext(jsobObj2.getString("addr_jing5").trim());}
                                if(jsobObj2.getString("addr_jing6").trim().equals("")){
                                    time6.time_text_settext("第6路");
                                } else {
                                    time6.time_text_settext(jsobObj2.getString("addr_jing6").trim());}
                                if(jsobObj2.getString("addr_jing7").trim().equals("")){
                                    time7.time_text_settext("第7路");
                                } else {
                                    time7.time_text_settext(jsobObj2.getString("addr_jing7").trim());}
                                if(jsobObj2.getString("addr_jing8").trim().equals("")){
                                    time8.time_text_settext("第8路");
                                } else {
                                    time8.time_text_settext(jsobObj2.getString("addr_jing8").trim());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "用户名加载失败!!!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "用户名加载找不到接口!!!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(TimeControl.this,
                        "出错了，请检查一下网络" + "\ninitName:" + volleyError.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        VollyQueue.getHttpQueues().add(stringRequest);
    }
    //刷新
    private void refresh() {
        final ProgressDialog dialog = ProgressDialog.show(TimeControl.this, null, "加载中...", true);
        String term_str = myAdapter.getItem(spinner.getSelectedItemPosition()).trim();
        String URL = null;
        try {
            URL = rootURL + "/Onoff/GOnoff_r_max_view?Dev_id=" + DevNo
                    + "&term_str=" + URLEncoder.encode(term_str, "utf-8").trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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
                                JSONObject jsobObj2 = jsobObj.getJSONObject("Onoff_r_max_view");
                                time1.bu_on_setText(jsobObj2.getString("switch1_timeOn"));
                                time2.bu_on_setText(jsobObj2.getString("switch2_timeOn"));
                                time3.bu_on_setText(jsobObj2.getString("switch3_timeOn"));
                                time4.bu_on_setText(jsobObj2.getString("switch4_timeOn"));
                                time5.bu_on_setText(jsobObj2.getString("switch5_timeOn"));
                                time6.bu_on_setText(jsobObj2.getString("switch6_timeOn"));
                                time7.bu_on_setText(jsobObj2.getString("switch7_timeOn"));
                                time8.bu_on_setText(jsobObj2.getString("switch8_timeOn"));
                                time1.bu_off_setText(jsobObj2.getString("switch1_timeOff"));
                                time2.bu_off_setText(jsobObj2.getString("switch2_timeOff"));
                                time3.bu_off_setText(jsobObj2.getString("switch3_timeOff"));
                                time4.bu_off_setText(jsobObj2.getString("switch4_timeOff"));
                                time5.bu_off_setText(jsobObj2.getString("switch5_timeOff"));
                                time6.bu_off_setText(jsobObj2.getString("switch6_timeOff"));
                                time7.bu_off_setText(jsobObj2.getString("switch7_timeOff"));
                                time8.bu_off_setText(jsobObj2.getString("switch8_timeOff"));
                                try {
                                    checkBox1.setChecked(jsobObj2.getString("versionId").substring(0, 1).trim().equals("1"));
                                    checkBox2.setChecked(jsobObj2.getString("versionId").substring(1, 2).trim().equals("1"));
                                    checkBox3.setChecked(jsobObj2.getString("versionId").substring(2, 3).trim().equals("1"));
                                    checkBox4.setChecked(jsobObj2.getString("versionId").substring(3, 4).trim().equals("1"));
                                    checkBox5.setChecked(jsobObj2.getString("versionId").substring(4, 5).trim().equals("1"));
                                    checkBox6.setChecked(jsobObj2.getString("versionId").substring(5, 6).trim().equals("1"));
                                    checkBox7.setChecked(jsobObj2.getString("versionId").substring(6, 7).trim().equals("1"));
                                    checkBox8.setChecked(jsobObj2.getString("versionId").substring(7, 8).trim().equals("1"));
                                } catch (Exception ex) {

                                }
                                int Uid_int = jsobObj2.getInt("onoff_id");
                                if(Uid_int > 0) {
                                    if(Uid_store < Uid_int) {
                                        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                        String date_str = sDateFormat.format(new java.util.Date());
                                        list_nums++;
                                        rsltlist.add(0,"("+list_nums+")刷新成功 "+date_str.trim());
                                        listspadp.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(TimeControl.this,"没有最新数据",Toast.LENGTH_SHORT).show();
                                    }
                                    Uid_store = Uid_int;
                                }
                            } catch (JSONException e) {
                                Toast.makeText(TimeControl.this, "加载失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(TimeControl.this,
                        "出错了，请检查一下网络" + "\nrefresh" + volleyError.toString(),
                        Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        VollyQueue.getHttpQueues().add(stringRequest);
    }
    //获取
    private void get_onofftime() {
        final ProgressDialog dialog = ProgressDialog.show(TimeControl.this, null, "获取中...", true);
        String term_str = myAdapter.getItem(spinner.getSelectedItemPosition()).trim();
        String URL2 = null;
        try {
            URL2 = rootURL + "/wcf/get_onofftime?Dev_id="+ DevNo
                    + "&term_str=" + URLEncoder.encode(term_str, "utf-8").trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(URL2 == null) {
            dialog.dismiss();
            return;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if(!s.isEmpty() && !s.trim().equals("")) {
                            dialog.dismiss();
                            System.out.println(s);
                            try {
                                JSONObject jsobObj = new JSONObject(s);
                                String temp_str = jsobObj.getString("rslt");
                                list_nums++;
                                rsltlist.add(0, "(" + list_nums + ")" + temp_str.trim());
                                listspadp.notifyDataSetChanged();
                            } catch (Exception e){
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(TimeControl.this, "出错了，请检查一下网络", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        VollyQueue.getHttpQueues().add(stringRequest);
    }
    //设置
    private void set_onofftime() {
        final ProgressDialog dialog = ProgressDialog.show(TimeControl.this, null, "设置中...", true);
        String term_str = myAdapter.getItem(spinner.getSelectedItemPosition()).trim();
        String validateURL = null;
        try {
            validateURL = rootURL + "/wcf/set_onofftime?Dev_id=" + DevNo
                    + "&term_str=" + URLEncoder.encode(term_str, "utf-8").trim()
                    + "&time1_on=" + time1.time_bu_on_gettext().trim()
                    + "&time1_off=" + time1.time_bu_off_gettext().trim()
                    + "&time2_on=" + time2.time_bu_on_gettext().trim()
                    + "&time2_off=" + time2.time_bu_off_gettext().trim()
                    + "&time3_on=" + time3.time_bu_on_gettext().trim()
                    + "&time3_off=" + time3.time_bu_off_gettext().trim()
                    + "&time4_on=" + time4.time_bu_on_gettext().trim()
                    + "&time4_off=" + time4.time_bu_off_gettext().trim()
                    + "&time5_on=" + time5.time_bu_on_gettext().trim()
                    + "&time5_off=" + time5.time_bu_off_gettext().trim()
                    + "&time6_on=" + time6.time_bu_on_gettext().trim()
                    + "&time6_off=" + time6.time_bu_off_gettext().trim()
                    + "&time7_on=" + time7.time_bu_on_gettext().trim()
                    + "&time7_off=" + time7.time_bu_off_gettext().trim()
                    + "&time8_on=" + time8.time_bu_on_gettext().trim()
                    + "&time8_off=" + time8.time_bu_off_gettext().trim()
                    + "&week_str=" + week_str.trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(validateURL == null) {
            dialog.dismiss();
            return;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET, validateURL,
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
                                rsltlist.add(0, "(" + list_nums + ")" + temp_str.trim());
                                listspadp.notifyDataSetChanged();
                                saveLog(rsltlist.get(0).toString().trim());
                            } catch (JSONException e) {
                                Toast.makeText(TimeControl.this,"加载失败",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(TimeControl.this, "出错了，请检查一下网络", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        VollyQueue.getHttpQueues().add(stringRequest);
    }
    private void set() {
        if(grade.trim().equals("3")) {
            rsltlist.add(0, "(" + list_nums + ")失败!你是查询员,没有设置的权限! " );
            listspadp.notifyDataSetChanged();
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
        set_onofftime();
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
                    Toast.makeText(TimeControl.this, "网络异常", Toast.LENGTH_SHORT).show();
                }
            });
            stringRequest.setTag("");
            VollyQueue.getHttpQueues().add(stringRequest);
        } catch (Exception e) {
            Toast.makeText(TimeControl.this, "网络故障!!!", Toast.LENGTH_SHORT).show();
        }
    }

}

package com.example.administrator.light.system;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.administrator.light.BaseActivity;
import com.example.administrator.light.R;
import com.example.administrator.light.VollyQueue;
import com.example.administrator.light.util.SharedPreferencesUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wujia on 2016/5/1.
 */
public class CentralizingSwitch extends BaseActivity {
    //集中开关
    private String rootURL, account, grade;
    private String childname, DevNo;
    private int Uid_store = 0; //流水号
    private int listnum = 0;
    private String N8_str = "00000000";//8路开关参数
    private String mode_str = "10000001";//模式参数

    private Button refresh, btGet, btSet, btAll;
    private ListView rsltListView;//执行结果listview
    private List<String> rsltList = new ArrayList<String>();//执行结果列
    private ArrayAdapter<String> listspadp;
    private List<Boolean> booleanList = new ArrayList<Boolean>();//使能列
    private Spinner spinner;//运行模式控件
    private LinearLayout SwitchLayout;//开关状态父容器
    private Switch switch1, switch2, switch3, switch4,
            switch5, switch6, switch7, switch8,
            switch9, switch10, Switch0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.centralizing_switch);
        initView();    //初始化界面ID
        initListener();//界面监听事件
        initSwitch();  //初始化switch个数
        initName();    //初始switch名字
        refresh();
    }
    /*
    * 初始化界面ID
    * */
    public void initView() {
        //从数据库获取网址及基本信息
        rootURL = (String) SharedPreferencesUtils.getParam(this, "rootURL", "");
        account = (String) SharedPreferencesUtils.getParam(this, "username", "");
        grade = (String) SharedPreferencesUtils.getParam(this, "grade", "2") ;//3
        Intent intent = getIntent();
        childname = intent.getStringExtra("childname");
        DevNo = childname.split("-")[0].trim();

        //设置标题栏
        getToolbarTitle().setText(childname);
        //switch控件
        switch1 = (Switch)findViewById(R.id.switch1);
        switch2 = (Switch)findViewById(R.id.switch2);
        switch3 = (Switch)findViewById(R.id.switch3);
        switch4 = (Switch)findViewById(R.id.switch4);
        switch5 = (Switch)findViewById(R.id.switch5);
        switch6 = (Switch)findViewById(R.id.switch6);
        switch7 = (Switch)findViewById(R.id.switch7);
        switch8 = (Switch)findViewById(R.id.switch8);
        switch9 = (Switch)findViewById(R.id.switch9);
        switch10 = (Switch)findViewById(R.id.switch10);
        SwitchLayout = (LinearLayout)findViewById(R.id.switch_layout);
        //spinner
        spinner = (Spinner)findViewById(R.id.centralizing_switch_spinner);
        List<String> spinnerList = new ArrayList<String>();
        spinnerList.add("正常模式");
        spinnerList.add("应急模式");
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, R.layout.spinner_display_style, R.id.spinner_text,spinnerList);
        myAdapter.setDropDownViewResource(R.layout.spinner_dropdown_style);
        spinner.setAdapter(myAdapter);
        //button按钮：获取，刷新，设置，全选
        refresh = (Button)findViewById(R.id.refresh);
        btGet = (Button)findViewById(R.id.btGet);
        btSet = (Button)findViewById(R.id.btSet);
        btAll = (Button)findViewById(R.id.btAll);
        //ListView
        rsltListView = (ListView)findViewById(R.id.resultListview);
        listspadp = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rsltList);
        rsltListView.setAdapter(listspadp);
    }
    /*
    * 界面的监听事件
    * */
    public void initListener() {
        //监听刷新switch按钮
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });
        //监听获取按钮
        btGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_onoff();
                handler.postDelayed(runnable, 4000);
            }
        });
        //监听设置按钮
        btSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                set();
                handler.postDelayed(runnable, 4000);
            }
        });
        //监听全选按钮
        btAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context dialogContext = new ContextThemeWrapper(CentralizingSwitch.this,
                        android.R.style.Theme_Light);
                String[] choices = {"全部关闭", "全部打开"};
                ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
                        android.R.layout.simple_list_item_1, choices);
                AlertDialog.Builder builder = new AlertDialog.Builder(dialogContext);
                builder.setSingleChoiceItems(adapter, -1,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                boolean switchState = false;
                                switch (which) {
                                    case 0:
                                        switchState = false;
                                        break;
                                    case 1:
                                        switchState = true;
                                        break;
                                }
                                for (int i = 0; i < 8; i++) {
                                    Switch0 = (Switch) findViewById(SwitchLayout.getChildAt(i).getId());
                                    Switch0.setChecked(switchState);
                                }
                            }
                        });
                builder.create().show();
            }
        });
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            refresh();
        }
    };

    /*
    * 初始switch个数
    * */
    public void initSwitch() {
        String URL5 = rootURL + "/Onoff/Gset_time_class?Dev_id=" + DevNo;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL5,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (s != null && !s.trim().equals("")) {
                            try {
                                JSONObject jsobObj = new JSONObject(s);
                                JSONArray json_arr = jsobObj.getJSONArray("enable_time_class");
                                booleanList.clear();
                                for (int i = 0; i < json_arr.length(); i++) {
                                    boolean btemp = json_arr.getBoolean(i);
                                    booleanList.add(btemp);
                                    if (btemp == true) {
                                        SwitchLayout.getChildAt(i).setVisibility(View.VISIBLE);
                                    } else {
                                        SwitchLayout.getChildAt(i).setVisibility(View.GONE);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "用户名加载失败!!!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "用户名加载找不到接口!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "出错了，请检查一下网络", Toast.LENGTH_SHORT).show();
            }
        });
        VollyQueue.getHttpQueues().add(stringRequest);
    }
    /*
    * 初始化路灯名字
    * */
    public void initName() {
        String URL4 = rootURL + "/Onoff/Gdev_addr_light?Dev_id=" + DevNo;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL4,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        System.out.println(s);
                        if (s != null && !s.trim().equals("")) {
                            try {
                                JSONObject jsobObj = new JSONObject(s);
                                JSONObject jsobObj2= jsobObj.getJSONObject("dev_addr_light");
                                if(jsobObj2.getString("addr_jing1").trim().equals("")){
                                    switch1.setText("第1路");
                                }else {
                                    switch1.setText(jsobObj2.getString("addr_jing1").trim());
                                }
                                if(jsobObj2.getString("addr_jing2").trim().equals("")){
                                    switch2.setText("第2路");
                                }else {
                                    switch2.setText(jsobObj2.getString("addr_jing2").trim());
                                }
                                if(jsobObj2.getString("addr_jing3").trim().equals("")){
                                    switch3.setText("第3路");
                                }else {
                                    switch3.setText(jsobObj2.getString("addr_jing3").trim());
                                }
                                if(jsobObj2.getString("addr_jing4").trim().equals("")){
                                    switch4.setText("第4路");
                                }else {
                                    switch4.setText(jsobObj2.getString("addr_jing4").trim());
                                }
                                if(jsobObj2.getString("addr_jing5").trim().equals("")){
                                    switch5.setText("第5路");}else {
                                    switch5.setText(jsobObj2.getString("addr_jing5").trim());
                                }
                                if(jsobObj2.getString("addr_jing6").trim().equals("")){
                                    switch6.setText("第6路");
                                }else {
                                    switch6.setText(jsobObj2.getString("addr_jing6").trim());
                                }
                                if(jsobObj2.getString("addr_jing7").trim().equals("")){
                                    switch7.setText("第7路");
                                }else {
                                    switch7.setText(jsobObj2.getString("addr_jing7").trim());
                                }
                                if(jsobObj2.getString("addr_jing8").trim().equals("")){
                                    switch8.setText("第8路");
                                }else {
                                    switch8.setText(jsobObj2.getString("addr_jing8").trim());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "用户名加载失败!!!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "用户名加载找不到接口!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(CentralizingSwitch.this, "出错了，请检查一下网络", Toast.LENGTH_SHORT).show();
            }
        });
        //将请求加入全局队列中
        VollyQueue.getHttpQueues().add(stringRequest);
    }
    /*
    * 从服务器获取参数，刷新页面
    * */
    public void refresh() {
        final ProgressDialog dialog = ProgressDialog.show(CentralizingSwitch.this, null, "加载中...", true);
        String URL = rootURL + "/Onoff/Gset_Onoff_read?Dev_id=" + DevNo;
        //Get的方式通过volley框架获取
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        dialog.dismiss();
                        if(!s.isEmpty()&&!s.trim().equals("")) {
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                JSONArray jsonArray = jsonObject.getJSONArray("onoff");
                                for(int i = 0 ; i < jsonArray.length() ; i++) {
                                    boolean switchState = jsonArray.getBoolean(i);
                                    if(i < 8) {
                                        Switch0 = (Switch)findViewById(SwitchLayout.getChildAt(i).getId());
                                        Switch0.setChecked(switchState);
                                    }
                                    if(i == 9) {
                                        if(switchState) {
                                            spinner.setSelection(1);
                                        } else {
                                            spinner.setSelection(0);
                                        }
                                    }
                                    if(i >= 10 && i <= 11) {
                                        Switch0 = (Switch)findViewById(SwitchLayout.getChildAt(i - 2).getId());
                                        Switch0.setChecked(switchState);
                                    }
                                }
                                int uid = jsonObject.getInt("Uid");
                                if(uid > 0) {
                                    if(Uid_store < uid) {
                                        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                        String date_str = sDateFormat.format(new java.util.Date());
                                        listnum++;
                                        rsltList.add(0, "(" + listnum + ")刷新成功 " + date_str.trim());
                                        listspadp.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(CentralizingSwitch.this,"没有最新数据",Toast.LENGTH_SHORT).show();
                                    }
                                    Uid_store = uid;
                                }
                            } catch (JSONException e) {
                                Toast.makeText(CentralizingSwitch.this,"加载失败",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CentralizingSwitch.this, "出错了，请检查一下网络", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        VollyQueue.getHttpQueues().add(stringRequest);
    }

    public void get_onoff() {
        final ProgressDialog dialog = ProgressDialog.show(CentralizingSwitch.this, null, "获取中...", true);
        String URL1 = rootURL + "/wcf/get_onoff?Dev_id="+ DevNo;
        StringRequest stringRequest1 = new StringRequest(Request.Method.GET, URL1,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        if(!response.isEmpty() && !response.trim().equals("")) {
                            try {
                                JSONObject jsobObj = new JSONObject(response);
                                String  temp_str=jsobObj.getString("rslt");
                                listnum++;
                                rsltList.add(0, "(" + listnum + ")" + temp_str.trim());
                                listspadp.notifyDataSetChanged();
                            } catch (JSONException e) {
                                Toast.makeText(CentralizingSwitch.this,"加载失败",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CentralizingSwitch.this, "出错了，请检查一下网络", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        VollyQueue.getHttpQueues().add(stringRequest1);
    }

    /*
    * 将设置传至服务器
    * */
    private void set_onoff() {
        final ProgressDialog dialog = ProgressDialog.show(CentralizingSwitch.this, null, "设置中...", true);
        String URL2 = rootURL + "/wcf/set_onoff?Dev_id=" + DevNo
                +"&N8_str=" + N8_str.trim()
                +"&mode_str=" + mode_str.trim();
        System.out.println("URL--->" +  URL2);
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, URL2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        dialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            String  temp_str=jsonObject.getString("rslt");
                            listnum++;
                            rsltList.add(0, "(" + listnum + ")" + temp_str.trim());
                            listspadp.notifyDataSetChanged();
                            saveLog(rsltList.get(0).toString().trim());
                        } catch (JSONException e) {
                            Toast.makeText(CentralizingSwitch.this,"加载失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(CentralizingSwitch.this, "出错了，请检查一下网络", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        VollyQueue.getHttpQueues().add(stringRequest2);
    }

    private void set() {
        if(grade.trim().equals("3")) {
            rsltList.add(0, "(" + listnum + ")失败!你是查询员,没有设置的权限! ");
            listspadp.notifyDataSetChanged();
            return;
        }
        N8_str = "";
        mode_str = "";
        for (int i = 0; i < 8; i++) {
            Switch0 = (Switch) findViewById(SwitchLayout.getChildAt(i).getId());
            if (Switch0.isChecked()) {
                N8_str = "1" + N8_str.trim();
            } else {
                N8_str = "0" + N8_str.trim();
            }
        }
        if ( spinner.getSelectedItemPosition() == 0) {
            mode_str = "101" + mode_str.trim();
        } else {
            mode_str = "110" + mode_str.trim();
        }
        if(switch9.isChecked()) {
            mode_str = "1" + mode_str.trim();
        } else {
            mode_str = "0" + mode_str.trim();
        }
        if(switch10.isChecked()) {
            mode_str = "1" + mode_str.trim();
        } else {
            mode_str = "0" + mode_str.trim();
        }
        mode_str ="101" + mode_str.trim();
        set_onoff();
    }

    private void saveLog(String rslt_str) {
        try {
            String URL = rootURL + "/wcf/savelog?logname=" + URLEncoder.encode(account, "utf-8").trim()
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
                    Toast.makeText(CentralizingSwitch.this, "网络异常", Toast.LENGTH_SHORT).show();
                }
            });
            stringRequest.setTag("");
            VollyQueue.getHttpQueues().add(stringRequest);
        } catch (Exception e) {
            Toast.makeText(CentralizingSwitch.this, "网络故障!!!", Toast.LENGTH_SHORT).show();
        }
    }
}

package com.example.administrator.light.system;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class CentralizingSwitch_Group extends BaseActivity {
    private String rootURL, account, grade;
    private String groupname, DevNo;
    private ArrayList<String> childList;
    private int listnum = 0;
    private String N8_str = "00000000";//8路开关参数
    private String mode_str = "10000001";//模式参数

    private int num = 0;
    private String str = null;

    private Button refresh, btGet, btSet, btAll;
    private ListView rsltListView;//执行结果listview
    private List<String> rsltList = new ArrayList<String>();//执行结果列
    private ArrayAdapter<String> listspadp;
    private Spinner spinner;//运行模式控件
    private LinearLayout SwitchLayout;//开关状态父容器
    private Switch switch1, switch2, switch3, switch4,
            switch5, switch6, switch7, switch8,
            switch9, switch10, Switch0;
    private ProgressDialog dialog = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.centralizing_switch);
        initView();    //初始化界面ID
        initListener();//界面监听事件
    }
    /*
    * 初始化界面ID
    * */
    public void initView() {
        //从数据库获取网址及基本信息
        rootURL = (String) SharedPreferencesUtils.getParam(this, "rootURL", "");
        account = (String) SharedPreferencesUtils.getParam(this, "username", "");
        grade = (String) SharedPreferencesUtils.getParam(this, "grade", "2");//3
        Intent intent = getIntent();
        groupname = intent.getStringExtra("groupname");
        childList = intent.getStringArrayListExtra("childList");
        System.out.println(childList);

        //设置标题栏
        getToolbarTitle().setText(groupname);
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
        refresh.setVisibility(View.GONE);
        btGet.setVisibility(View.GONE);
        //监听设置按钮
        btSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                set();
            }
        });
        //监听全选按钮
        btAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context dialogContext = new ContextThemeWrapper(CentralizingSwitch_Group.this,
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

    /*
    * 将设置传至服务器
    * */
    private void set_onoff(String DevNo) {
        String URL2 = rootURL + "/wcf/set_onoff?Dev_id=" + DevNo
                +"&N8_str=" + N8_str.trim()
                +"&mode_str=" + mode_str.trim();
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, URL2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            String  temp_str=jsonObject.getString("rslt");
                            num ++;
                            if(num == 1) {
                                str = temp_str.trim();
                            } else {
                                str += "\n" + temp_str.trim();
                            }
                            if(num >= childList.size()) {
                                dialog.dismiss();
                                listnum++;
                                str = "(" + listnum + ")\n" + str;
                                rsltList.add(0, str);
                                listspadp.notifyDataSetChanged();
                                saveLog(rsltList.get(0).toString().trim());
                            }
                        } catch (JSONException e) {
                            Toast.makeText(CentralizingSwitch_Group.this,"加载失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(CentralizingSwitch_Group.this, "出错了，请检查一下网络", Toast.LENGTH_SHORT).show();
            }
        });
        VollyQueue.getHttpQueues().add(stringRequest2);
    }

    private void set() {
        dialog = ProgressDialog.show(CentralizingSwitch_Group.this, null, "设置中...", true);
        if (grade.trim().equals("3")) {
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
        num = 0;
        str = null;
        for(int i = 0; i < childList.size(); i++) {
            DevNo = childList.get(i).split("-")[0].trim();;
            set_onoff(DevNo);
        }
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
                    Toast.makeText(CentralizingSwitch_Group.this, "网络异常", Toast.LENGTH_SHORT).show();
                }
            });
            stringRequest.setTag("");
            VollyQueue.getHttpQueues().add(stringRequest);
        } catch (Exception e) {
            Toast.makeText(CentralizingSwitch_Group.this, "网络故障!!!", Toast.LENGTH_SHORT).show();
        }
    }
}

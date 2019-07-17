package com.example.administrator.light.other;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SingleParaHistory extends BaseActivity{
    private String childname;
    private String DevNo;
    private int year, month, day, hour, minute;
    private String startDateStr, endDateStr, startTimeStr, endTimeStr;
    private String URL;

    private Button btStartDate, btEndDate, btStartTime, btEndTime,
            btFind, btDate, btTime;
    private CheckBox checkBox, checkBox1, checkBox2, checkBox3, checkBox4;
    private Spinner spinner;
    private ArrayList<String> RodNumList;
    private ArrayAdapter<String> RodNumAdapter;
    private ListView listView;
    ArrayList<HashMap<String, Object>> list;

    private String webURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_para_history);
        initview();
        listen();
    }

    @Override
    protected void onResume() {
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onResume();
    }

    public void initview(){
        //获取上一个activity传过来的名字
        Intent intent = getIntent();
        childname = intent.getStringExtra("childname");
        DevNo = childname.split("-")[0];
        getToolbarTitle().setText(childname);
        //获取系统时间
        Calendar now = Calendar.getInstance();
        year = now.get(Calendar.YEAR);
        month = now.get(Calendar.MONTH)+1;
        day = now.get(Calendar.DAY_OF_MONTH);
        hour = now.get(Calendar.HOUR);
        minute = now.get(Calendar.MINUTE);
        //从数据库获取网址及基本信息
        webURL = (String) SharedPreferencesUtils.getParam(this, "rootURL", "");

        startDateStr = (String) SharedPreferencesUtils.getParam(this, "single_para_history_start_date", year + "-" + month + "-" + (day - 1));
        endDateStr = (String) SharedPreferencesUtils.getParam(this, "single_para_history_end_date", year + "-" + month + "-" + (day));
        startTimeStr = (String) SharedPreferencesUtils.getParam(this, "single_para_history_start_time", hour + ":" + (minute-1));
        endTimeStr = (String) SharedPreferencesUtils.getParam(this, "single_para_history_end_time", hour + ":" + (minute - 1));

        btStartDate = (Button)findViewById(R.id.startDate);
        btEndDate = (Button)findViewById(R.id.endDate);
        btStartTime = (Button)findViewById(R.id.startTime);
        btEndTime = (Button)findViewById(R.id.endTime);
        btFind = (Button)findViewById(R.id.find);
        checkBox = (CheckBox)findViewById(R.id.checkbox);
        checkBox1 = (CheckBox)findViewById(R.id.checkbox1);
        checkBox2 = (CheckBox)findViewById(R.id.checkbox2);
        checkBox3 = (CheckBox)findViewById(R.id.checkbox3);
        checkBox4 = (CheckBox)findViewById(R.id.checkbox4);

        btStartDate.setText(startDateStr);
        btEndDate.setText(endDateStr);
        btStartTime.setText(startTimeStr);
        btEndTime.setText(endTimeStr);

        spinner = (Spinner)findViewById(R.id.spinner);
        RodNumList = new ArrayList<String>();
        listView = (ListView)findViewById(R.id.listview);
        list = new ArrayList<HashMap<String, Object>>();

        getRodNumList(DevNo);
    }

    public void listen(){
        View.OnClickListener btListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.startDate:
                    case R.id.endDate:
                        btDate = (Button)view;
                        String date = btDate.getText().toString();
                        year = Integer.parseInt(date.split("-")[0]);
                        month = Integer.parseInt(date.split("-")[1]);
                        day = Integer.parseInt(date.split("-")[2]);
                        DatePickerDialog datePickerDialog = new DatePickerDialog(SingleParaHistory.this,datesetlistener,year,month,day);
                        datePickerDialog.show();
                        break;
                    case R.id.startTime:
                    case R.id.endTime:
                        if(checkBox.isChecked()) {
                            btTime = (Button)view;
                            String time = btTime.getText().toString();
                            hour = Integer.parseInt(time.split(":")[0]);
                            minute = Integer.parseInt(time.split(":")[1]);
                            TimePickerDialog timePickerDialog = new TimePickerDialog(SingleParaHistory.this,timeSetListener,hour,minute,true);
                            timePickerDialog.show();
                        } else {
                            Toast.makeText(SingleParaHistory.this,"请先勾选选择按钮",Toast.LENGTH_SHORT).show();
                        }
                        break;
                }

            }
        };
        btStartDate.setOnClickListener(btListener);
        btEndDate.setOnClickListener(btListener);
        btStartTime.setOnClickListener(btListener);
        btEndTime.setOnClickListener(btListener);
        btFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                volleyrequest(DevNo, startDateStr, endDateStr, "",
                        checkBox.isChecked(), startTimeStr, endTimeStr,
                        checkBox1.isChecked(), checkBox2.isChecked(),
                        checkBox3.isChecked(), checkBox4.isChecked());
            }
        });
    }

    private void getRodNumList(String Dev_id) {
        int pageSize = 0;
        int CurrentPageIndex = 0;
        try {
            String URL = webURL + "/Single/Getsingle_volt_detail?Dev_id=" + Dev_id
                    + "&pageSize=" + pageSize + "&CurrentPageIndex=" + CurrentPageIndex;
            System.out.println(URL);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println(response);
                            if(!response.isEmpty() && !response.trim().equals("")) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONArray jsonArray = jsonObject.getJSONArray("single_volt_detail");
                                    RodNumList.clear();
                                    for(int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject itemObject = jsonArray.getJSONObject(i);
                                        String str = itemObject.getString("rod_num").trim();
                                        if(str.equals("0-0")) {
                                            continue;
                                        }
                                        RodNumList.add(str);
                                    }
                                    RodNumAdapter = new ArrayAdapter<String>(SingleParaHistory.this,
                                            android.R.layout.simple_spinner_dropdown_item, RodNumList);
                                    spinner.setAdapter(RodNumAdapter);

                                    volleyrequest(DevNo, startDateStr, endDateStr,
                                            spinner.getSelectedItem().toString(),
                                            false, startTimeStr, endTimeStr,
                                            false, false, false, false);

                                } catch (JSONException e) {
                                    Toast.makeText(SingleParaHistory.this, "用户名加载失败", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(SingleParaHistory.this, "用户加载找不到接口!!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(SingleParaHistory.this, "网络异常", Toast.LENGTH_SHORT).show();
                }
            });
            //设置请求的Tag标签，可以在全局请求队列中通过Tag标签进行请求的查找
            stringRequest.setTag("");
            //将请求加入全局队列中
            VollyQueue.getHttpQueues().add(stringRequest);
        } catch (Exception e) { //UnsupportedEncodingException
            e.printStackTrace();
        }
    }

    public void volleyrequest(String Dev_id, String begin_date_str, String end_date_str,
                              String rod_num, boolean chk_I_time, String BeginH, String endH,
                              boolean chk_I_zero, boolean chk_I_not_zero,
                              boolean chk_U_zero, boolean chk_U_not_zero) {
        URL = webURL + "/Single/Getsingle_volt_detail_I_his?Dev_id=" + Dev_id
                + "&begin_date_str=" + begin_date_str
                + "&end_date_str=" + end_date_str
                + "&CurrentPageIndex=" + 0
                + "&pageSize=" + 0
                + "&rod_num=" + rod_num
                + "&chk_I_time=" + chk_I_time
                + "&BeginH=" + BeginH
                + "&endH=" + endH
                + "&chk_I_zero=" + chk_I_zero
                + "&chk_I_not_zero=" + chk_I_not_zero
                + "&chk_U_zero=" + chk_U_zero
                + "&chk_U_not_zero=" + chk_U_not_zero;
        System.out.println(URL);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null && !response.trim().equals("")) {
                            System.out.println(response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("single_volt_detail_alarm_his");
                                list.clear();
                                for(int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject itemObject = jsonArray.getJSONObject(i);
//                                    if(itemObject.getString("rod_num").trim().equals("0-0")) {
//                                        continue;
//                                    }
                                    HashMap<String, Object> hashMap = new HashMap<String, Object>();
                                    String time_str = "";
                                    try {
                                        time_str = get_jason_date(itemObject.getString("update_dtm").trim()).trim();
                                    } catch (Exception e) {

                                    }
                                    hashMap.put("update_dtm", time_str.trim());
                                    hashMap.put("rod_num", itemObject.getString("rod_num"));
                                    hashMap.put("rod_real", itemObject.getString("rod_real"));
                                    hashMap.put("V_rod", itemObject.getString("V_rod"));
                                    hashMap.put("V_rod2", itemObject.getString("V_rod2"));
                                    hashMap.put("I1", itemObject.getString("I1"));
                                    hashMap.put("I2", itemObject.getString("I2"));
                                    hashMap.put("w1", itemObject.getString("w1"));
                                    hashMap.put("w2", itemObject.getString("w2"));
                                    list.add(hashMap);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            System.out.println(list);
                            SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), list,
                                    R.layout.single_para_history_item,
                                    new String[]{"update_dtm", "rod_num", "rod_real",
                                            "V_rod", "V_rod2", "I1", "I2", "w1", "w2"},
                                    new int[]{R.id.rod_time, R.id.rod_num, R.id.rod_real,
                                            R.id.rod_V1, R.id.rod_V2, R.id.rod_I1, R.id.rod_I2,
                                            R.id.rod_P1, R.id.rod_P2});
                            listView.setAdapter(adapter);

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println(volleyError.toString());
                Toast.makeText(SingleParaHistory.this,volleyError.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        VollyQueue.getHttpQueues().add(stringRequest);
    }

    private String get_jason_date(String Date_str) {
        try {
            Date_str = Date_str.replace("/Date(", "");
            Date_str = Date_str.replace(")/", "");
            Long long1 = Long.parseLong(Date_str);
            Date date_1 = new Date(long1);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            SimpleDateFormat format_date = new SimpleDateFormat("yyyy-MM-dd");

//            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
//            if(format_date.format(date_1).trim().equals(format_date.format(curDate).trim())){
//                return sdf.format(date_1);
//            }
//            return "";
            return sdf.format(date_1);
        } catch (Exception ex) {
            return "";
        }
    }

    public DatePickerDialog.OnDateSetListener datesetlistener
            = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            Toast.makeText(SingleParaHistory.this, year + "-" + month + "-" + day, Toast.LENGTH_SHORT).show();
            btDate.setText(year + "-" + month + "-" + day);
            startDateStr = btStartDate.getText().toString();
            endDateStr = btEndDate.getText().toString();

            SharedPreferencesUtils.setParam(getApplicationContext(), "single_para_history_start_date", startDateStr);
            SharedPreferencesUtils.setParam(getApplicationContext(), "single_para_history_end_date", endDateStr);
        }
    };
    public TimePickerDialog.OnTimeSetListener timeSetListener
            = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            if(hour < 10) {
                if(minute < 10) {
                    btTime.setText("0" + hour + ":" + "0" + minute);
                } else btTime.setText("0"+ hour + ":" + minute);
            } else {
                if(minute < 10) {
                    btTime.setText(hour + ":" + "0" + minute);
                } else btTime.setText(hour + ":" + minute);
            }

            startTimeStr = btStartTime.getText().toString();
            endTimeStr = btEndTime.getText().toString();

            SharedPreferencesUtils.setParam(getApplicationContext(), "single_para_history_start_time", startTimeStr);
            SharedPreferencesUtils.setParam(getApplicationContext(), "single_para_history_end_time", endTimeStr);
        }
    };

}

package com.example.administrator.light.other;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

public class SingleFaultQuery extends BaseActivity{
    private String childname;
    private String DevNo;
    private int year, month, day;
    private String startDateStr, endDateStr;
    private String URL;

    private Button btStartDate, btEndDate, btFind, btDate;
    private ListView listView;
    ArrayList<HashMap<String, Object>> list;

    private String webURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_fault_query);
        initview();
        listen();
        volleyrequest(DevNo, startDateStr, endDateStr);
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
        //从数据库获取网址及基本信息
        webURL = (String) SharedPreferencesUtils.getParam(this, "rootURL", "");

        startDateStr = (String) SharedPreferencesUtils.getParam(this, "single_fault_query_start_date", year + "-" + month + "-" + (day - 1));
        endDateStr = (String) SharedPreferencesUtils.getParam(this, "single_fault_query_end_date", year + "-" + month + "-" + (day));

        btStartDate = (Button)findViewById(R.id.startDate);
        btEndDate = (Button)findViewById(R.id.endDate);
        btFind = (Button)findViewById(R.id.find);

        btStartDate.setText(startDateStr);
        btEndDate.setText(endDateStr);

        listView = (ListView)findViewById(R.id.listview);
        list = new ArrayList<HashMap<String, Object>>();
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
                        DatePickerDialog datePickerDialog = new DatePickerDialog(SingleFaultQuery.this,datesetlistener,year,month,day);
                        datePickerDialog.show();
                        break;
                }
            }
        };
        btStartDate.setOnClickListener(btListener);
        btEndDate.setOnClickListener(btListener);
        btFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                volleyrequest(DevNo, startDateStr, endDateStr);
            }
        });
    }

    public void volleyrequest(String Dev_id, String begin_date_str, String end_date_str) {
        URL = webURL + "/Single/Getsingle_volt_detail_alarm_his?Dev_id=" + Dev_id
                + "&begin_date_str=" + begin_date_str
                + "&end_date_str=" + end_date_str
                + "&pageSize=" + 0
                + "&CurrentPageIndex=" + 0;
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
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject itemObject = jsonArray.getJSONObject(i);
//                                    if(itemObject.getString("rod_num").trim().equals("0-0")) {
//                                        continue;
//                                    }
                                    HashMap<String, Object> hashMap = new HashMap<String, Object>();
                                    hashMap.put("DevNo", itemObject.getString("DevNo"));
                                    String time_str = "";
                                    try {
                                        time_str = get_jason_date(itemObject.getString("update_dtm").trim()).trim();
                                    } catch (Exception e) {

                                    }
                                    hashMap.put("update_dtm", time_str.trim());
                                    hashMap.put("rod_num", itemObject.getString("rod_num"));
                                    hashMap.put("rod_real", itemObject.getString("rod_real"));
                                    hashMap.put("rod_name", "");
                                    hashMap.put("alarm_info", itemObject.getString("alarm_info"));
                                    list.add(hashMap);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            System.out.println(list);
                            SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), list,
                                    R.layout.single_fault_query_item,
                                    new String[]{"DevNo", "update_dtm", "rod_num", "rod_real",
                                            "rod_name", "alarm_info"},
                                    new int[]{R.id.no, R.id.time, R.id.rod_num, R.id.rod_real,
                                            R.id.rod_name, R.id.alarm_info});
                            listView.setAdapter(adapter);

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println(volleyError.toString());
                Toast.makeText(SingleFaultQuery.this,volleyError.toString(),Toast.LENGTH_SHORT).show();
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
            Toast.makeText(SingleFaultQuery.this, year + "-" + month + "-" + day, Toast.LENGTH_SHORT).show();
            btDate.setText(year + "-" + month + "-" + day);
            startDateStr = btStartDate.getText().toString();
            endDateStr = btEndDate.getText().toString();

            SharedPreferencesUtils.setParam(getApplicationContext(), "single_fault_query_start_date", startDateStr);
            SharedPreferencesUtils.setParam(getApplicationContext(), "single_fault_query_end_date", endDateStr);
        }
    };

}

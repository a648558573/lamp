package com.example.administrator.light.other;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
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

import java.util.Calendar;

/**
 * Created by wujia on 2016/5/21.
 */
public class Three_phase extends BaseActivity{
    private String childname;
    private String DevNo;
    private int year, month, day;
    private int hour, minute;
    private String startdate, enddate, starttime, endtime, show = "column";
    private int type = 0;
    private String[] shows ;
    private String[] types ;
    private String URL;
    private StringBuilder sb;
    private String js ;

    private WebView webView;
    private Spinner spinner1 , spinner2 ;
    private CheckBox checkBox ;
    private Button startdate_bu , enddate_bu , starttime_bu , endtime_bu ,
            find , button_date , button_time ;

    private String webURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.three_phase);
        initview();
        listen();
        volleyrequest(DevNo, startdate, enddate,
                checkBox.isChecked(), starttime, endtime, show, type);
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

        startdate = (String) SharedPreferencesUtils.getParam(this, "three_phase_start_date", year + "-" + month + "-" + (day-1));
        enddate = (String) SharedPreferencesUtils.getParam(this, "three_phase_end_date", year + "-" + month + "-" + (day));
        starttime = (String) SharedPreferencesUtils.getParam(this, "three_phase_start_time", hour +":" + (minute-1));
        endtime = (String) SharedPreferencesUtils.getParam(this, "three_phase_end_time", hour + ":" + (minute - 1));

        types = getResources().getStringArray(R.array.three_phase_type);
        shows = getResources().getStringArray(R.array.three_phase_show);

        checkBox = (CheckBox)findViewById(R.id.three_phase_checkbox);
        spinner1 = (Spinner)findViewById(R.id.three_phase_spinner1);
        spinner2 = (Spinner)findViewById(R.id.three_phase_spinner2);
        startdate_bu = (Button)findViewById(R.id.three_phase_startdate);
        enddate_bu = (Button)findViewById(R.id.three_phase_enddate);
        starttime_bu = (Button)findViewById(R.id.three_phase_starttime);
        endtime_bu = (Button)findViewById(R.id.three_phase_endtime);
        find = (Button)findViewById(R.id.three_phase_find);

        startdate_bu.setText(startdate);
        enddate_bu.setText(enddate);
        starttime_bu.setText(starttime);
        endtime_bu.setText(endtime);

        //网页加载，特性设置
        webView = (WebView) findViewById(R.id.three_phase_webview);
        webView.getSettings().setJavaScriptEnabled(true);//设置支持js
        webView.getSettings().setBuiltInZoomControls(true);//设置支持缩放
        webView.getSettings().setUseWideViewPort(true);//将图片调整到适合webview的大小
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);// 缩放至屏幕的大小
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setDomStorageEnabled(true);
        //html构造
        sb = new StringBuilder();
    }

    public void listen(){
        AdapterView.OnItemSelectedListener spinnerLostener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (adapterView.getId()) {
                    case R.id.three_phase_spinner1:
                        show = shows[i];
                        break;
                    case R.id.three_phase_spinner2:
                        type = i;
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        };
        View.OnClickListener btListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.three_phase_startdate:
                    case R.id.three_phase_enddate:
                        button_date = (Button)view;
                        String date = button_date.getText().toString();
                        year = Integer.parseInt(date.split("-")[0]);
                        month = Integer.parseInt(date.split("-")[1]);
                        day = Integer.parseInt(date.split("-")[2]);
                        DatePickerDialog datePickerDialog = new DatePickerDialog(Three_phase.this,datesetlistener,year,month,day);
                        datePickerDialog.show();
                        break;
                    case R.id.three_phase_starttime:
                    case R.id.three_phase_endtime:
                        if(checkBox.isChecked()) {
                            button_time = (Button)view;
                            String time = button_time.getText().toString();
                            hour = Integer.parseInt(time.split(":")[0]);
                            minute = Integer.parseInt(time.split(":")[1]);
                            TimePickerDialog timePickerDialog = new TimePickerDialog(Three_phase.this,timeSetListener,hour,minute,true);
                            timePickerDialog.show();
                        } else {
                            Toast.makeText(Three_phase.this,"请先勾选选择按钮",Toast.LENGTH_SHORT).show();
                        }
                        break;
                }

            }
        };
        spinner1.setOnItemSelectedListener(spinnerLostener);
        spinner2.setOnItemSelectedListener(spinnerLostener);
        startdate_bu.setOnClickListener(btListener);
        enddate_bu.setOnClickListener(btListener);
        starttime_bu.setOnClickListener(btListener);
        endtime_bu.setOnClickListener(btListener);
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                volleyrequest(DevNo, startdate, enddate,
                        checkBox.isChecked(), starttime, endtime, show, type);
            }
        });
    }

    /*
    * 构造html
    * */
    public void inithtml() {
        sb.delete(0, sb.length());
        sb.append("<!doctype html>");
        sb.append("<html lang=\"en\">");
        sb.append("<head>");
        sb.append("<script type=\"text/javascript\" src=\"file:///android_asset/js/jquery.js\"></script>");
        sb.append("<script type=\"text/javascript\" src=\"file:///android_asset/js/highcharts.js\"></script>");
        //sb.append("<script type=\"text/javascript\" src=\"file:///android_asset/js/data.js\"></script>");
        sb.append("<script>");
        sb.append(js);
        sb.append("</script>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<div id=\"container\" style=\"min-width:700px;height:400px\">");
        sb.append("</biv>");
        sb.append("</body>");
        sb.append("</html>");
        System.out.println("三相电参图：" + sb.toString());
        webView.loadDataWithBaseURL(null, sb.toString(), "text/html", "utf-8", null);
    }

    public void volleyrequest(String DevNo, String begin_str, String end_str,
                              boolean chk_U_time_term, String OnTime_str, String offTime_str,
                              String chart_type, int para_index) {
        URL = webURL + "/Home/Dev_elr?DevNo=" + DevNo
                + "&begin_str=" + begin_str
                + "&end_str=" + end_str
                + "&chk_U_time_term=" + chk_U_time_term
                + "&OnTime_str=" + OnTime_str
                + "&offTime_str=" + offTime_str
                + "&chart_type=" + chart_type
                + "&para_index=" + para_index;
        System.out.println(URL);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            s = s.replaceAll("\\\\r\\\\n","");
                            JSONObject jsonObject = new JSONObject(s);
                            js = jsonObject.getString("highcharts");
                            inithtml();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println(volleyError.toString());
                Toast.makeText(Three_phase.this,volleyError.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        VollyQueue.getHttpQueues().add(stringRequest);
    }

    public DatePickerDialog.OnDateSetListener datesetlistener
            = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            Toast.makeText(Three_phase.this, year + "-" + month + "-" + day, Toast.LENGTH_SHORT).show();
            button_date.setText(year + "-" + month + "-" + day);
            startdate = startdate_bu.getText().toString();
            enddate = enddate_bu.getText().toString();

            SharedPreferencesUtils.setParam(getApplicationContext(), "start_date", startdate);
            SharedPreferencesUtils.setParam(getApplicationContext(), "end_date", enddate);
        }
    };
    public TimePickerDialog.OnTimeSetListener timeSetListener
            = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            if(hour < 10) {
                if(minute < 10) {
                    button_time.setText("0"+ hour + ":" +"0"+ minute);
                } else button_time.setText("0"+ hour + ":" + minute);
            } else {
                if(minute < 10) {
                    button_time.setText(hour + ":" +"0"+ minute);
                } else button_time.setText(hour + ":" + minute);
            }

            starttime = starttime_bu.getText().toString();
            endtime = endtime_bu.getText().toString();

            SharedPreferencesUtils.setParam(getApplicationContext(), "three_phase_start_time", starttime);
            SharedPreferencesUtils.setParam(getApplicationContext(), "three_phase_end_time", endtime);
        }
    };

}

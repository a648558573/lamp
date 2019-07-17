package com.example.administrator.light.other;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
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
import java.util.Calendar;

/**
 * Created by wujia on 2016/5/17.
 */
public class Daily extends BaseActivity {
    private String childname;
    private String DevNo;
    private int year, month, day;
    private String starttime , endtime ;
    private String count = "按月显示" , show = "line" ;
    private int option = 0;
    private String[] counts ;
    private String[] shows ;
    private String[] options ;
    private String URL;
    private StringBuilder sb;
    private String js ;

    private String webURL;

    private WebView daily_webview;
    private Button daily_starttimebutton , daily_endtimebutton , daily_find , button ;
    private Spinner daily_spinner1 , daily_spinner2 , daily_spinner3 ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daily);
        initview();
        listen();
        volleyrequest();
    }

    @Override
    protected void onResume() {
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onResume();
    }

    /*
    * 初始化界面
    * */
    public void initview() {
        //获取上一个activity传过来的名字
        Intent intent = getIntent();
        childname = intent.getStringExtra("childname");
        DevNo = childname.split("-")[0];
        getToolbarTitle().setText(childname);
        //获取系统时间
        Calendar now = Calendar.getInstance();
        year = now.get(Calendar.YEAR);
        month = now.get(Calendar.MONTH) + 1;
        day = now.get(Calendar.DAY_OF_MONTH);
        //从数据库获取网址及基本信息
        webURL = (String) SharedPreferencesUtils.getParam(this, "rootURL", "");
        starttime = (String) SharedPreferencesUtils.getParam(this, "daily_start_time", year + "-" + month + "-" + (day - 1));
        endtime = (String) SharedPreferencesUtils.getParam(this, "daily_end_time", year + "-" + month + "-" + (day));

        counts = getResources().getStringArray(R.array.daily_count);
        shows = getResources().getStringArray(R.array.daily_show);
        options = getResources().getStringArray(R.array.daily_option);

        daily_spinner1 = (Spinner)findViewById(R.id.daily_spinner1);
        daily_spinner2 = (Spinner)findViewById(R.id.daily_spinner2);
        daily_spinner3 = (Spinner)findViewById(R.id.daily_spinner3);
        daily_starttimebutton = (Button)findViewById(R.id.daily_starttime);
        daily_endtimebutton = (Button)findViewById(R.id.daily_endtime);
        daily_find = (Button)findViewById(R.id.daily_find);

        daily_starttimebutton.setText(starttime);
        daily_endtimebutton.setText(endtime);

        //网页加载，特性设置
        daily_webview = (WebView) findViewById(R.id.daily_webview);
        daily_webview.getSettings().setJavaScriptEnabled(true);//设置支持js
        daily_webview.getSettings().setBuiltInZoomControls(true);//设置支持缩放
        daily_webview.getSettings().setUseWideViewPort(true);//将图片调整到适合webview的大小
        daily_webview.getSettings().setAllowContentAccess(true);
        daily_webview.getSettings().setAllowFileAccessFromFileURLs(true);
        daily_webview.getSettings().setAppCacheEnabled(true);
        daily_webview.getSettings().setLoadWithOverviewMode(true);// 缩放至屏幕的大小
        daily_webview.getSettings().setPluginState(WebSettings.PluginState.ON);
        daily_webview.getSettings().setDomStorageEnabled(true);
        //html构造
        sb = new StringBuilder();
    }

    /*
    * 监听事件
    * */
    public void listen() {
        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (adapterView.getId()) {
                    case R.id.daily_spinner1:
                        count = counts[i];
                        Toast.makeText(Daily.this, count, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.daily_spinner2:
                        show = shows[i];
                        Toast.makeText(Daily.this, show, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.daily_spinner3:
                        option = i;
                        Toast.makeText(Daily.this, option + "", Toast.LENGTH_SHORT).show();
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
                button = (Button)view;
                String date = button.getText().toString();
                year = Integer.parseInt(date.split("-")[0]);
                month = Integer.parseInt(date.split("-")[1]);
                day = Integer.parseInt(date.split("-")[2]);
                DatePickerDialog datePickerDialog = new DatePickerDialog(Daily.this,datesetlistener,year,month,day);
                datePickerDialog.show();
            }
        };
        daily_spinner1.setOnItemSelectedListener(spinnerListener);
        daily_spinner2.setOnItemSelectedListener(spinnerListener);
        daily_spinner3.setOnItemSelectedListener(spinnerListener);
        daily_starttimebutton.setOnClickListener(btListener);
        daily_endtimebutton.setOnClickListener(btListener);
        daily_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                volleyrequest();
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
        System.out.println("日用电量：" + sb.toString());
        daily_webview.loadDataWithBaseURL(null, sb.toString(), "text/html", "utf-8", null);
    }

    public void volleyrequest() {
        try {
            URL = webURL + "/Home/get_dev_kwh_per?DevNo_str=" + DevNo
                    + "&DevName_str=" + URLEncoder.encode("", "utf-8")
                    + "&date_type_str=" + URLEncoder.encode(count, "utf-8")
                    + "&dtpBeginDate_str=" + starttime
                    + "&dtpEndDate_str=" + endtime
                    + "&light_type_str=" + URLEncoder.encode("", "utf-8")
                    + "&chart_type=" + show
                    + "&para_index=" + option;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            s = s.replaceAll("\\\\r\\\\n", "");
                            //Toast.makeText(Daily.this,s,Toast.LENGTH_SHORT).show();
                            JSONObject jsonObject = new JSONObject(s);
                            js = jsonObject.getString("highcharts");
                            //Toast.makeText(Daily.this,js,Toast.LENGTH_SHORT).show();
                            inithtml();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println(volleyError.toString());
                Toast.makeText(Daily.this,volleyError.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        VollyQueue.getHttpQueues().add(stringRequest);
    }

    public DatePickerDialog.OnDateSetListener datesetlistener = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            Toast.makeText(Daily.this, year + "-" + month + "-" + day, Toast.LENGTH_SHORT).show();
            button.setText(year + "-" + month + "-" + day);
            starttime = daily_starttimebutton.getText().toString();
            endtime = daily_endtimebutton.getText().toString();

            SharedPreferencesUtils.setParam(getApplicationContext(), "daily_start_time", starttime);
            SharedPreferencesUtils.setParam(getApplicationContext(), "daily_end_time", endtime);
        }
    };

}

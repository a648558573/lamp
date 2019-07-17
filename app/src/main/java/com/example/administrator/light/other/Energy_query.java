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
 * Created by wujia on 2016/5/12.
 */
public class Energy_query extends BaseActivity {
    private String childname;
    private String DevNo;
    private int year, month, day;
    private String starttime, endtime, count = "按月能耗显示", show = "line" ;
    private int option = 0;
    private String[] counts ;
    private String[] shows ;
    private String[] options ;
    private StringBuilder sb;
    private String js ;

    private String webURL;

    private Spinner spinner1 , spinner2 , spinner3;
    private Button starttimebutton , endtimebutton , button , energy_query_find ;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.energy_query);
        initview();
        listen();
        volleyrequest(DevNo, count, starttime, endtime, show, option);
        // 加载网页文件
        //webView.loadUrl("file:///android_asset/index.html");
    }

    @Override
    protected void onResume() {
        if(getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onResume();
    }

    /*
    * 初始化界面信息
    * */
    public void initview() {
        //获取上一个activity传过来的名字
        Intent intent = getIntent();
        childname = intent.getStringExtra("childname");
        getToolbarTitle().setText(childname);
        DevNo = childname.split("-")[0];
        //获取系统时间
        Calendar now = Calendar.getInstance();
        year = now.get(Calendar.YEAR);
        month = now.get(Calendar.MONTH) + 1;
        day = now.get(Calendar.DAY_OF_MONTH);
        //从数据库获取网址及基本信息
        webURL = (String) SharedPreferencesUtils.getParam(this, "rootURL", "");
        starttime = (String) SharedPreferencesUtils.getParam(this, "energy_query_start_time", year + "-" + month + "-" + (day - 1));
        endtime = (String) SharedPreferencesUtils.getParam(this, "energy_query_end_time", year + "-" + month + "-" + (day));

        counts = getResources().getStringArray(R.array.count);
        shows = getResources().getStringArray(R.array.show);
        options = getResources().getStringArray(R.array.option);
        //count = counts[0] ;
        //show = shows[0] ;
        //option = options[0];

        spinner1 = (Spinner)findViewById(R.id.energy_query_spinner1);
        spinner2 = (Spinner)findViewById(R.id.energy_query_spinner2);
        spinner3 = (Spinner)findViewById(R.id.energy_query_spinner3);
        starttimebutton = (Button)findViewById(R.id.energy_query_starttime);
        endtimebutton = (Button)findViewById(R.id.energy_query_endtime);
        energy_query_find = (Button)findViewById(R.id.energy_query_find);

        starttimebutton.setText(starttime);
        endtimebutton.setText(endtime);

        //网页加载，特性设置
        webView = (WebView) findViewById(R.id.energy_query_webview);
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

    /*
    * 监听事件
    * */
    public void listen() {
        View.OnClickListener btListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button = (Button)view;
                String date = button.getText().toString();
                year = Integer.parseInt(date.split("-")[0]);
                month = Integer.parseInt(date.split("-")[1]);
                day = Integer.parseInt(date.split("-")[2]);
                DatePickerDialog datePickerDialog = new DatePickerDialog(Energy_query.this,
                        datesetlistener, year, month, day);
                datePickerDialog.show();
            }
        };
        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (adapterView.getId()) {
                    case R.id.energy_query_spinner1:
                        count = counts[i];
                        Toast.makeText(Energy_query.this, count, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.energy_query_spinner2:
                        show = shows[i];
                        Toast.makeText(Energy_query.this, show, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.energy_query_spinner3:
                        option = i;
                        Toast.makeText(Energy_query.this, option + "", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        };

        spinner1.setOnItemSelectedListener(spinnerListener);
        spinner2.setOnItemSelectedListener(spinnerListener);
        spinner3.setOnItemSelectedListener(spinnerListener);
        starttimebutton.setOnClickListener(btListener);
        endtimebutton.setOnClickListener(btListener);
        energy_query_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                volleyrequest(DevNo, count, starttime, endtime, show, option);
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
        System.out.println("能耗查询：" + sb.toString());
        webView.loadDataWithBaseURL(null, sb.toString(), "text/html", "utf-8", null);
    }

    public void volleyrequest(String DevNo, String count, String starttime, String endtime,
                              String show, int option) {
        //Toast.makeText(Energy_query.this,count,Toast.LENGTH_SHORT).show();
        String URL = null;
        try {
            URL = webURL + "/Home/get_elr1?DevNo_str=" + DevNo
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
                Toast.makeText(Energy_query.this,volleyError.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        VollyQueue.getHttpQueues().add(stringRequest);
    }

    public DatePickerDialog.OnDateSetListener datesetlistener = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            Toast.makeText(Energy_query.this,year+"-"+month+"-"+day,Toast.LENGTH_SHORT).show();
            button.setText(year + "-" + month + "-" + day);
            starttime = starttimebutton.getText().toString();
            endtime = endtimebutton.getText().toString();

            SharedPreferencesUtils.setParam(getApplicationContext(), "energy_query_start_time", starttime);
            SharedPreferencesUtils.setParam(getApplicationContext(), "energy_query_end_time", endtime);
        }
    };

}

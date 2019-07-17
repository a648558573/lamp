package com.example.administrator.light.other;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
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

/**
 * Created by wujia on 2016/5/17.
 */
public class AssetRatio extends BaseActivity {
    private String childname;
    private String DevNo;
    private String penType = "灯杆厂家占有比例图", queryType = "终端号查询",
            tableType = "灯具型号瓦数";
    private String[] penTypes, queryTypes, tableTypes;
    private String URL;
    private StringBuilder sb;
    private String js ;

    private String webURL;

    private WebView webview;
    private Button find;
    private Spinner spinner1, spinner2, spinner3 ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.asset_ratio);
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
        //从数据库获取网址及基本信息
        webURL = (String) SharedPreferencesUtils.getParam(this, "rootURL", "");

        penTypes = getResources().getStringArray(R.array.pen_type);
        queryTypes = getResources().getStringArray(R.array.query_type);
        tableTypes = getResources().getStringArray(R.array.table_type);

        spinner1 = (Spinner)findViewById(R.id.spinner1);
        spinner2 = (Spinner)findViewById(R.id.spinner2);
        spinner3 = (Spinner)findViewById(R.id.spinner3);
        find = (Button)findViewById(R.id.find);

        //网页加载，特性设置
        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);//设置支持js
        webview.getSettings().setBuiltInZoomControls(true);//设置支持缩放
        webview.getSettings().setUseWideViewPort(true);//将图片调整到适合webview的大小
        webview.getSettings().setAllowContentAccess(true);
        webview.getSettings().setAllowFileAccessFromFileURLs(true);
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setLoadWithOverviewMode(true);// 缩放至屏幕的大小
        webview.getSettings().setPluginState(WebSettings.PluginState.ON);
        webview.getSettings().setDomStorageEnabled(true);
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
                    case R.id.spinner1:
                        penType = penTypes[i];
                        Toast.makeText(AssetRatio.this, penType, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.spinner2:
                        queryType = queryTypes[i];
                        Toast.makeText(AssetRatio.this, queryType, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.spinner3:
                        tableType = tableTypes[i];
                        Toast.makeText(AssetRatio.this, tableType + "", Toast.LENGTH_SHORT).show();
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
        find.setOnClickListener(new View.OnClickListener() {
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
        System.out.println("资产比例图：" + sb.toString());
        webview.loadDataWithBaseURL(null, sb.toString(), "text/html", "utf-8", null);
    }

    public void volleyrequest() {
        try {
            URL = webURL + "/Home/get_pen_light_info?DevNo_str=" + DevNo
                    + "&Group_type_str=" + URLEncoder.encode("", "utf-8")
                    + "&pen_type_str=" + URLEncoder.encode(penType, "utf-8")
                    + "&Query_type_str=" + URLEncoder.encode(queryType, "utf-8")
                    + "&table_type=" + URLEncoder.encode(tableType, "utf-8");
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
                Toast.makeText(AssetRatio.this,volleyError.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        VollyQueue.getHttpQueues().add(stringRequest);
    }

}

package com.example.administrator.light.other.collect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JO on 2016/5/26.
 */
public class GetSingle extends BaseActivity {
    private EditText RodNumEdit, CityEdit;
    private Button bt;
    private String rootURL;
    private ArrayList<Map<String, Object>> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collect_get);
        init();
    }

    private void init() {
        getToolbarTitle().setText("杆号核对");
        rootURL = (String) SharedPreferencesUtils.getParam(this, "rootURL", "");
        list = new ArrayList<Map<String, Object>>();

        RodNumEdit = (EditText)findViewById(R.id.rodnum_edit);
        CityEdit = (EditText)findViewById(R.id.city_edit);
        bt = (Button)findViewById(R.id.set_bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String RodNumStr = RodNumEdit.getText().toString().trim();
                String CityStr = CityEdit.getText().toString().trim();
                if (RodNumStr.equals("")) {
                    Toast.makeText(GetSingle.this, "终端号不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    Getsingle_map_temp(RodNumStr, CityStr);
                }
            }
        });
    }

    private void Getsingle_map_temp(String DevNo_int, String Area_name) {
        try {
            String URL = rootURL + "/Tree/Getsingle_map_temp?DevNo_int=" + DevNo_int
                    + "&Area_name=" + Area_name
                    + "&rod_num_str=" + "";
            System.out.println(URL);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println(response);
                            if(!response.isEmpty() && !response.trim().equals("")) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONArray jsonArray = jsonObject.getJSONArray("single_map_temp");
                                    list.clear();
                                    for(int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject itemObject = jsonArray.getJSONObject(i);
                                        Map<String, Object> map = new HashMap<String, Object>();
                                        map.put("DevNo", itemObject.getString("DevNo").trim());
                                        map.put("rod_num", itemObject.getString("rod_num").trim());
                                        map.put("rod_real", itemObject.getString("rod_real").trim());
                                        map.put("rod_name", itemObject.getString("rod_name").trim());
                                        map.put("Area_name", itemObject.getString("Area_name").trim());
                                        map.put("DevX", itemObject.getString("DevX").trim());
                                        map.put("DevY", itemObject.getString("DevY").trim());
                                        String time_str = "";
                                        try {
                                            time_str = get_jason_date(itemObject.getString("update_dtm").trim()).trim();
                                        } catch (Exception e) {

                                        }
                                        map.put("update_dtm", time_str.trim());
                                        list.add(map);
                                    }
                                    if(list.size() > 0) {
                                        Toast.makeText(GetSingle.this, list.toString(), Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent();
                                        intent.setClass(GetSingle.this, GetList.class);
                                        intent.putExtra("ClassName", "GetSingle");
                                        intent.putExtra("List", (Serializable) list);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(GetSingle.this, "暂无相关信息", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(GetSingle.this, "用户名加载失败", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(GetSingle.this, "用户加载找不到接口!!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(GetSingle.this, "网络异常", Toast.LENGTH_SHORT).show();
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

}

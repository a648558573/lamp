package com.example.administrator.light.system.ConcentratorAlarm;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class ConcentratorAlarm1 extends BaseActivity {
    private String rootURL = null;
    String account = null;
    String password = null;
    ListView listView;
    ArrayList<HashMap<String, Object>> list;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.concentratoralarm);
        init();
    }

    public void onStart() {
        super.onStart();
        initlistView();
    }

    public void init() {
        account = (String) SharedPreferencesUtils.getParam(this, "username", "");
        password = (String) SharedPreferencesUtils.getParam(this, "password", "");
        rootURL = (String) SharedPreferencesUtils.getParam(this, "rootURL", "");
        listView = (ListView) findViewById(R.id.concentrator_alarm_list);
        list = new ArrayList<HashMap<String, Object>>();
        //设置标题栏文本
        getToolbarTitle().setText("终端报警");
    }

    public void initlistView() {
        final ProgressDialog dialog = ProgressDialog.show(ConcentratorAlarm1.this, null, "加载中...", true);
        try{
            String URL = rootURL
                    + "/Tree/DevInfoAlarm?log_name=" + URLEncoder.encode(account.trim(), "utf-8").trim()
                    + "&log_pass=" + password.trim() + "&sn_node_mode=1";
            System.out.println(URL);
            StringRequest stringRequest = new StringRequest(URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            dialog.dismiss();
                            try {
                                if (response != null && !response.trim().equals("")) {
                                    System.out.println(response);
                                    Log.d("response", response);
                                    JSONObject jsobObj = new JSONObject(response);

                                    JSONArray node_name_arr = jsobObj.getJSONArray("node_name");
                                    JSONArray node_info_arr = jsobObj.getJSONArray("alarm_info");
                                    JSONArray node_time_arr = jsobObj.getJSONArray("alarm_time");
                                    Log.d("node_name_arr", node_name_arr.toString());
                                    Log.d("alarm_info_arr", node_info_arr.toString());
                                    Log.d("alarm_time_arr", node_time_arr.toString());

                                    for (int i = 0,j =0; i < node_name_arr.length(); i++) {
                                        JSONArray node_name_arr1 = node_name_arr.getJSONArray(i);
                                        JSONArray node_info_arr1 = node_info_arr.getJSONArray(i);
                                        JSONArray node_time_arr1 = node_time_arr.getJSONArray(i);

                                        Log.d("node_name_arr1", node_name_arr1.toString());
                                        Log.d("alarm_info_arr1", node_info_arr1.toString());
                                        Log.d("alarm_time_arr1", node_time_arr1.toString());

                                        for(int k =0; k < node_name_arr1.length(); k++,j++) {
                                            HashMap<String, Object> hashMap = new HashMap<String, Object>();
                                            hashMap.put("node_name", node_name_arr1.getString(k).toString());
                                            hashMap.put("alarm_info", node_info_arr1.getString(k).toString());
                                            hashMap.put("alarm_time", node_time_arr1.getString(k).toString());
                                            list.add(hashMap);
                                        }
                                    }
                                    System.out.println(list);
                                    SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), list,
                                            R.layout.concentrator_alarm_item,
                                            new String[]{"node_name", "alarm_time", "alarm_info"},
                                            new int[]{R.id.alarmConcentrator, R.id.alarmTime, R.id.alarmInfo});
                                    //adapter.notifyDataSetChanged();
                                    listView.setAdapter(adapter);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("TAG", error.getMessage(), error);
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            });
            stringRequest.setTag("");
            VollyQueue.getHttpQueues().add(stringRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}

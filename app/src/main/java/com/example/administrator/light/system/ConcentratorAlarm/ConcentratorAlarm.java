package com.example.administrator.light.system.ConcentratorAlarm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

/**
 * Created by Lxr on 2016/4/28.
 */
public class ConcentratorAlarm extends BaseActivity {
    private String rootURL = null;
    String account = null;
    String password = null;
    ListView listView;
    String[] name_arr;
    String[] info_arr;
    String[] time_arr;

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
        //设置标题栏文本
        getToolbarTitle().setText("终端报警");
    }

    public void initlistView() {
        try{
            String URL = rootURL
                    + "/Tree/DevInfoAlarm?log_name=" + URLEncoder.encode(account.trim(), "utf-8").trim()
                    +"&log_pass=" + password.trim() + "&sn_node_mode=1";
            System.out.println(URL);
            StringRequest stringRequest = new StringRequest(URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
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

                                    int len = 0;
                                    for (int i = 0,j =0; i < node_name_arr.length(); i++) {
                                        JSONArray node_name_arr1 = node_name_arr.getJSONArray(i);
                                        len+=node_name_arr1.length();
                                    }
                                    name_arr = new String[len];
                                    info_arr = new String[len];
                                    time_arr = new String[len];

                                    for (int i = 0,j =0; i < node_name_arr.length(); i++) {
                                        JSONArray node_name_arr1 = node_name_arr.getJSONArray(i);
                                        JSONArray node_info_arr1 = node_info_arr.getJSONArray(i);
                                        JSONArray node_time_arr1 = node_time_arr.getJSONArray(i);
                                        Log.d("node_name_arr1", node_name_arr1.toString());
                                        Log.d("alarm_info_arr1", node_info_arr1.toString());
                                        Log.d("alarm_time_arr1", node_time_arr1.toString());

                                        for(int k=0;k<node_name_arr1.length();k++,j++) {
                                            name_arr[j] = node_name_arr1.getString(k).toString();
                                            info_arr[j] = node_info_arr1.getString(k).toString();
                                            time_arr[j] = node_time_arr1.getString(k).toString();
                                            Log.d("name_arr", name_arr[j]+" "+j);
                                        }
                                    }
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>
                                            (getApplicationContext() , R.layout.array_item , name_arr);
                                    adapter.notifyDataSetChanged();
                                    listView.setAdapter(adapter);
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                            Bundle data = new Bundle();
                                            data.putString("name", name_arr[position]);
                                            data.putString("info", info_arr[position]);
                                            data.putString("time", time_arr[position]);

                                            Intent intent = new Intent(ConcentratorAlarm.this, ShowConcentratorAlarm.class);
                                            intent.putExtras(data);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("TAG", error.getMessage(), error);
                }
            });
            stringRequest.setTag("");
            VollyQueue.getHttpQueues().add(stringRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}

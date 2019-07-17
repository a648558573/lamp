package com.example.administrator.light.system.SingleAlarm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lxr on 2016/5/18.
 */
public class SingleTerminal extends BaseActivity {
    private String rootURL = null;
    private ListView listView;
    private List<Map<String,String>> data ;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singletermina);
        init();
        initListView();
    }

    private void init() {
        rootURL = (String) SharedPreferencesUtils.getParam(this, "rootURL", "");
        listView = (ListView) findViewById(R.id.single_terminal_list);
        data = new ArrayList<Map<String,String>>();
        //设置标题栏文本
        getToolbarTitle().setText("终端选择");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent();
                intent.putExtra("id", data.get(position).get("DevNo"));
                intent.setClass(SingleTerminal.this, SingleAlarm.class);
                startActivity(intent);
            }
        });
    }

    private void initListView(){
        final ProgressDialog dialog = ProgressDialog.show(SingleTerminal.this, null, "加载中...", true);
        String URL = rootURL + "/Single/GetAlarm_single_dev_count";
        StringRequest stringRequest = new StringRequest(URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        System.out.println(response);
                        try {
                            if (response != null && !response.trim().equals("")) {
                                JSONObject jsobObj = new JSONObject(response);
                                JSONArray DevNo_arr = jsobObj.getJSONArray("DevNo");
                                JSONArray DevName_arr = jsobObj.getJSONArray("DevName");
                                JSONArray Alarm_count_arr = jsobObj.getJSONArray("alarm_count");

                                for(int i =0 ; i<DevNo_arr.length();i++){
                                    Map map = new HashMap<String,String>();
                                    map.put("DevNo", DevNo_arr.get(i));
                                    map.put("DevName", DevName_arr.get(i));
                                    map.put("alarm_count", Alarm_count_arr.get(i));
                                    data.add(map);
                                }
                                MyAdapter adapter = new MyAdapter(SingleTerminal.this);
                                listView.setAdapter(adapter);
                            }
                        }catch (JSONException e) {
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
    }

    //ViewHolder静态类
    static class ViewHolder {
        public TextView title;
        public TextView info;
    }
    private class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater = null;
        private MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.singleterminal_item, null);
                holder.title = (TextView)convertView.findViewById(R.id.single_terminal_title);
                holder.info = (TextView)convertView.findViewById(R.id.single_terminal_info);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.title.setText(data.get(position).get("DevNo")+"-"+data.get(position).get("DevName"));
            holder.info.setText(data.get(position).get("alarm_count"));
            return convertView;
        }
    }

}




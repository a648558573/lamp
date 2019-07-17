package com.example.administrator.light.system.SingleAlarm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class SingleAlarm extends BaseActivity {
    private String rootURL = null;
    private String Dev_id = null;
    private ListView listView;
    private List<Map<String,String>> data ;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singlealarm);
        init();
        initListView();
    }

    private void init() {
        rootURL = (String) SharedPreferencesUtils.getParam(this, "rootURL", "");
        Intent intent = getIntent();
        Dev_id = intent.getStringExtra("id");
        System.out.println("id " + Dev_id);
        listView = (ListView)findViewById(R.id.single_alarm_list);
        data = new ArrayList<Map<String,String>>();
        //设置标题栏文本
        getToolbarTitle().setText("终端报警");
    }

    private void initListView(){
        final ProgressDialog dialog = ProgressDialog.show(SingleAlarm.this, null, "加载中...", true);
        String URL = rootURL + "/Single/Getsingle_volt_detail?Dev_Id=" + Dev_id;
        StringRequest stringRequest = new StringRequest(URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        System.out.println(response);
                        try {
                            if (response != null && !response.trim().equals("")) {
                                JSONObject jsobObj = new JSONObject(response);
                                JSONArray jsonArray = jsobObj.getJSONArray("single_volt_detail");
                                System.out.println(jsonArray);
                                for(int i =0 ; i<jsonArray.length();i++){
                                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                                    Map map = new HashMap<String,String>();
                                    map.put("rod_num", jsonObject.getString("rod_num").trim());
                                    map.put("alarm_info", jsonObject.getString("alarm_info").trim());
                                    if(!jsonObject.getString("alarm_info").trim().equals("")){
                                        data.add(map);
                                    }
                                }
                                System.out.println("size is " + data.size());
                                if(data.size()!=0) {
                                    MyAdapter adapter = new MyAdapter(SingleAlarm.this);
                                    listView.setAdapter(adapter);
                                } else {
                                    TextView tv = (TextView)findViewById(R.id.single_alarm_tv);
                                    tv.setVisibility(View.VISIBLE);
                                }
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
                convertView = mInflater.inflate(R.layout.showmsg, null);
                holder.title = (TextView)convertView.findViewById(R.id.show_name);
                holder.info = (TextView)convertView.findViewById(R.id.show_info);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.title.setText("杆号："+data.get(position).get("rod_num"));
            holder.info.setText(data.get(position).get("alarm_info"));
            return convertView;
        }
    }

}

package com.example.administrator.light.other.collect;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JO on 2016/5/27.
 */
public class PicList extends BaseActivity {
    private String rootURL = null;
    private ListView PicListView;
    private TextView PicTip;
    private Button btRefresh;
    private ProgressDialog progressDialog = null;
    private List<Map<String, Object>> PicList = new ArrayList<Map<String, Object>>();
    private BaseAdapter PicAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collect_pic_list);
        init();
        initList();
        GetDev_pic();
    }

    private void init() {
        getToolbarTitle().setText("列表");
        rootURL = (String) SharedPreferencesUtils.getParam(this, "rootURL", "");

        PicListView = (ListView)findViewById(R.id.pic_list);
        PicTip = (TextView)findViewById(R.id.pic_tip);
        btRefresh = (Button)findViewById(R.id.pic_list_bt_refresh);
        btRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetDev_pic();
            }
        });
    }

    public class ViewHolder {
        public TextView picName;
        public TextView picTime;
    }
    private void initList() {
        //初始化单灯列表
        PicAdapter = new BaseAdapter() {
            private LayoutInflater mInflater;

            @Override
            public int getCount() {
                return PicList.size();
            }

            @Override
            public Object getItem(int position) {
                return PicList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                ViewHolder holder = null;
                if (convertView == null) {
                    holder = new ViewHolder();
                    mInflater = LayoutInflater.from(getApplicationContext());//获得上下文
                    convertView = mInflater.inflate(
                            R.layout.collect_pic_list_item, null);

                    holder.picName = (TextView) convertView.findViewById(R.id.pic_list_name);
                    holder.picTime = (TextView) convertView.findViewById(R.id.pic_list_upload_time);

                    convertView.setTag(holder);

                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                holder.picName.setText(PicList.get(position).get("picName").toString());
                holder.picTime.setText(PicList.get(position).get("picTime").toString());

                return convertView;
            }
        };
        PicListView.setAdapter(PicAdapter);
    }

    private void GetDev_pic() {
        try {
            if(rootURL.toString().equals("")) {
                Toast.makeText(getApplicationContext(), "ip不能为空", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog = ProgressDialog.show(PicList.this, null, "加载中...", true);
                String URL = rootURL + "/Tree/GetDev_pic";
                System.out.println(URL);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                System.out.println(response);
                                progressDialog.dismiss();
                                if(!response.isEmpty() && !response.trim().equals("")) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        JSONArray jsonArray = jsonObject.getJSONArray("pic_name");
                                        PicList.clear();
                                        for(int i = 0; i < jsonArray.length(); i++) {
                                            System.out.println(jsonArray.getString(i));
                                            String[] temp_strs = jsonArray.getString(i).split(",");
                                            if(temp_strs.length > 0) {
                                                Map<String, Object> map = new HashMap<String, Object>();
                                                map.put("picName", temp_strs[0].trim());
                                                map.put("picTime", temp_strs[1].trim());
                                                PicList.add(map);
                                            }
                                        }
                                        PicAdapter.notifyDataSetChanged();
                                    } catch (JSONException e) {
                                        Toast.makeText(getApplicationContext(), "用户名加载失败", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "用户加载找不到接口!!!", Toast.LENGTH_SHORT).show();
                                }

                                if(PicList.size() > 0) {
                                    PicListView.setVisibility(View.VISIBLE);
                                    PicTip.setVisibility(View.GONE);
                                } else {
                                    PicListView.setVisibility(View.GONE);
                                    PicTip.setVisibility(View.VISIBLE);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplicationContext(), "网络异常", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        if(PicList.size() > 0) {
                            PicListView.setVisibility(View.VISIBLE);
                            PicTip.setVisibility(View.GONE);
                        } else {
                            PicListView.setVisibility(View.GONE);
                            PicTip.setVisibility(View.VISIBLE);
                        }
                    }
                });
                //设置请求的Tag标签，可以在全局请求队列中通过Tag标签进行请求的查找
                stringRequest.setTag("");
                //将请求加入全局队列中
                VollyQueue.getHttpQueues().add(stringRequest);
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "网络故障!!!", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            if(PicList.size() > 0) {
                PicListView.setVisibility(View.VISIBLE);
                PicTip.setVisibility(View.GONE);
            } else {
                PicListView.setVisibility(View.GONE);
                PicTip.setVisibility(View.VISIBLE);
            }
        }

    }

}

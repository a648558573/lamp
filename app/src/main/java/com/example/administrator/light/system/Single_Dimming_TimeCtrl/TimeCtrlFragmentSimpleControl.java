package com.example.administrator.light.system.Single_Dimming_TimeCtrl;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.administrator.light.R;
import com.example.administrator.light.VollyQueue;
import com.example.administrator.light.util.ClickUtils;
import com.example.administrator.light.util.ConvertUtils;
import com.example.administrator.light.util.SharedPreferencesUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JO on 2016/4/29.
 */
public class TimeCtrlFragmentSimpleControl extends Fragment {
    private View view;
    private int DevNo;
    private String rootURL, userName;

    private Spinner spinner1, spinner2;
    private CheckBox checkBox1, checkBox2, checkBox3, checkBox4;
    private Button btStart1, btStart2, btStart3, btStart4,
            btEnd1, btEnd2, btEnd3, btEnd4,
            btValue1, btValue2, btValue3, btValue4,
            btRefresh, btSet;

    private LinearLayout mutilLayout, listTitle1, listTitle2;
    private TextView noList;
    private RadioGroup radioGroup;

    private ListView listView1, listView2;
    private BaseAdapter adapter1, adapter2;
    private List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();

    //spinner2
    private ArrayList<String> list;
    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.single_timectrl_frag_simplecontrol, container, false);
        init();
        initSpinnerListener();
        initRBListener();
        initBtListener();
        return view;
    }

    private void init() {
        rootURL = (String) SharedPreferencesUtils.getParam(getActivity(), "rootURL", "");
        userName = (String) SharedPreferencesUtils.getParam(getActivity(), "username", "");
        DevNo = ((SingleActivity)getActivity()).getDevNo();

        spinner1 = (Spinner)view.findViewById(R.id.set_spinner1);
        spinner2 = (Spinner)view.findViewById(R.id.set_spinner2);
        checkBox1 = (CheckBox)view.findViewById(R.id.set_checkbox1);
        checkBox2 = (CheckBox)view.findViewById(R.id.set_checkbox2);
        checkBox3 = (CheckBox)view.findViewById(R.id.set_checkbox3);
        checkBox4 = (CheckBox)view.findViewById(R.id.set_checkbox4);

        btStart1 = (Button)view.findViewById(R.id.set_bt_start1);
        btStart2 = (Button)view.findViewById(R.id.set_bt_start2);
        btStart3 = (Button)view.findViewById(R.id.set_bt_start3);
        btStart4 = (Button)view.findViewById(R.id.set_bt_start4);
        btEnd1 = (Button)view.findViewById(R.id.set_bt_end1);
        btEnd2 = (Button)view.findViewById(R.id.set_bt_end2);
        btEnd3 = (Button)view.findViewById(R.id.set_bt_end3);
        btEnd4 = (Button)view.findViewById(R.id.set_bt_end4);
        btRefresh = (Button)view.findViewById(R.id.bt_refresh);

        btValue1 = (Button)view.findViewById(R.id.set_bt_value1);
        btValue2 = (Button)view.findViewById(R.id.set_bt_value2);
        btValue3 = (Button)view.findViewById(R.id.set_bt_value3);
        btValue4 = (Button)view.findViewById(R.id.set_bt_value4);

        btSet = (Button)view.findViewById(R.id.btSet);
        mutilLayout = (LinearLayout)view.findViewById(R.id.simple_layout2);
        radioGroup = (RadioGroup)view.findViewById(R.id.simple_rg);
        listTitle1 = (LinearLayout)view.findViewById(R.id.simple_list_title1);
        listTitle2 = (LinearLayout)view.findViewById(R.id.simple_list_title2);
        listView1 = (ListView)view.findViewById(R.id.simple_list1);
        listView2 = (ListView)view.findViewById(R.id.simple_list2);
        noList = (TextView)view.findViewById(R.id.simple_nolist);
    }

    private void initSpinnerListener() {
        list = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, list);
        spinner2.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] strArray;
                switch (spinner1.getSelectedItem().toString().trim()) {
                    case "LED灯调光（0-100%）":
                        btValue1.setText("亮度0%");
                        btValue2.setText("亮度0%");
                        btValue3.setText("亮度0%");
                        btValue4.setText("亮度0%");
                        btValue1.setOnClickListener(ClickUtils.getLuxSetting(getActivity()));
                        btValue2.setOnClickListener(ClickUtils.getLuxSetting(getActivity()));
                        btValue3.setOnClickListener(ClickUtils.getLuxSetting(getActivity()));
                        btValue4.setOnClickListener(ClickUtils.getLuxSetting(getActivity()));
                        strArray = getResources().getStringArray(R.array.simple_spinner5);
                        list.clear();
                        for (int n = 0; n < strArray.length; n++) {
                            list.add(strArray[n]);
                        }
                        adapter.notifyDataSetChanged();
                        break;
                    case "钠灯调档（0-4档）":
                        btValue1.setText("档位0");
                        btValue2.setText("档位0");
                        btValue3.setText("档位0");
                        btValue4.setText("档位0");
                        btValue1.setOnClickListener(ClickUtils.getTapSetting(getActivity()));
                        btValue2.setOnClickListener(ClickUtils.getTapSetting(getActivity()));
                        btValue3.setOnClickListener(ClickUtils.getTapSetting(getActivity()));
                        btValue4.setOnClickListener(ClickUtils.getTapSetting(getActivity()));
                        strArray = getResources().getStringArray(R.array.simple_spinner6);
                        list.clear();
                        for (int n = 0; n < strArray.length; n++) {
                            list.add(strArray[n]);
                        }
                        adapter.notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinner2.getSelectedItem().toString().equals("组控")) {
                    btSet.setText("组控设置");
                    mutilLayout.setVisibility(View.VISIBLE);
                    radioGroup.check(R.id.simple_rb1);
                } else {
                    btSet.setText("单控设置");
                    mutilLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void initRBListener() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.simple_rb1:
                        initList1();
                        Getsingle_volt_detail_group(DevNo);
                        listTitle1.setVisibility(View.VISIBLE);
                        listView1.setVisibility(View.VISIBLE);
                        listTitle2.setVisibility(View.GONE);
                        listView2.setVisibility(View.GONE);
                        break;
                    case R.id.simple_rb2:
                        initList2();
                        Getsingle_G_control_set(DevNo);
                        listTitle1.setVisibility(View.GONE);
                        listView1.setVisibility(View.GONE);
                        listTitle2.setVisibility(View.VISIBLE);
                        listView2.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void initBtListener() {
        btStart1.setOnClickListener(ClickUtils.getTimeSetting(getContext()));
        btStart2.setOnClickListener(ClickUtils.getTimeSetting(getContext()));
        btStart3.setOnClickListener(ClickUtils.getTimeSetting(getContext()));
        btStart4.setOnClickListener(ClickUtils.getTimeSetting(getContext()));
        btEnd1.setOnClickListener(ClickUtils.getTimeSetting(getContext()));
        btEnd2.setOnClickListener(ClickUtils.getTimeSetting(getContext()));
        btEnd3.setOnClickListener(ClickUtils.getTimeSetting(getContext()));
        btEnd4.setOnClickListener(ClickUtils.getTimeSetting(getContext()));
        btRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(radioGroup.getCheckedRadioButtonId() == R.id.simple_rb1) {
                    listTitle1.setVisibility(View.VISIBLE);
                    listTitle2.setVisibility(View.GONE);
                    initList1();
                    Getsingle_volt_detail_group(DevNo);
                } else {
                    listTitle1.setVisibility(View.GONE);
                    listTitle2.setVisibility(View.VISIBLE);
                    initList2();
                    Getsingle_G_control_set(DevNo);
                }
            }
        });
        btSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> rodNumList =
                        ((SingleActivity) getActivity()).getmForceOnOffFg().getSelectedSingleRodNum();
                String cmd_type = "";
                int Lux_1 = 255, Lux_2 = 255, Lux_3 = 255, Lux_4 = 255;
                String L1_time1 = "", L1_time2 = "", L2_time1 = "", L2_time2 = "",
                        L3_time1 = "", L3_time2 = "", L4_time1 = "", L4_time2 = "";
                String tmp_int2_str = "0";
                String log_name_str = userName;
                String obj_strs = "";
                if (rodNumList.size() < 1) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("提示")
                            .setMessage("请勾选想要设置的单灯")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                } else {
                    L1_time1 = btStart1.getText().toString();
                    L1_time2 = btEnd1.getText().toString();
                    L2_time1 = btStart2.getText().toString();
                    L2_time2 = btEnd2.getText().toString();
                    L3_time1 = btStart3.getText().toString();
                    L3_time2 = btEnd3.getText().toString();
                    L4_time1 = btStart4.getText().toString();
                    L4_time2 = btEnd4.getText().toString();
                    if (checkBox1.isChecked()) {
                        switch (spinner1.getSelectedItem().toString().trim()) {
                            case "LED灯调光（0-100%）":
                                Lux_1 = Integer.parseInt(btValue1.getText().toString().split("亮度")[1].split("%")[0].trim());
                                break;
                            case "钠灯调档（0-4档）":
                                Lux_1 = Integer.parseInt(btValue1.getText().toString().split("档位")[1].trim());
                                break;
                        }
                    }
                    if (checkBox2.isChecked()) {
                        switch (spinner1.getSelectedItem().toString().trim()) {
                            case "LED灯调光（0-100%）":
                                Lux_2 = Integer.parseInt(btValue2.getText().toString().split("亮度")[1].split("%")[0].trim());
                                break;
                            case "钠灯调档（0-4档）":
                                Lux_2 = Integer.parseInt(btValue2.getText().toString().split("档位")[1].trim());
                                break;
                        }
                    }
                    if (checkBox3.isChecked()) {
                        switch (spinner1.getSelectedItem().toString().trim()) {
                            case "LED灯调光（0-100%）":
                                Lux_3 = Integer.parseInt(btValue3.getText().toString().split("亮度")[1].split("%")[0].trim());
                                break;
                            case "钠灯调档（0-4档）":
                                Lux_3 = Integer.parseInt(btValue3.getText().toString().split("档位")[1].trim());
                                break;
                        }
                    }
                    if (checkBox4.isChecked()) {
                        switch (spinner1.getSelectedItem().toString().trim()) {
                            case "LED灯调光（0-100%）":
                                Lux_4 = Integer.parseInt(btValue4.getText().toString().split("亮度")[1].split("%")[0].trim());
                                break;
                            case "钠灯调档（0-4档）":
                                Lux_4 = Integer.parseInt(btValue4.getText().toString().split("档位")[1].trim());
                                break;
                        }
                    }
                    if (!spinner2.getSelectedItem().toString().equals("组控")) {//单控
                        switch (spinner2.getSelectedItem().toString()) {
                            case "时控灯1":
                            case "档位时段1-4":
                                cmd_type = "设置时控灯1";
                                break;
                            case "时控灯2":
                            case "档位时段5-8":
                                cmd_type = "设置时控灯2";
                                break;
                        }
                        tmp_int2_str = "";
                        obj_strs = "";
                        Setweb_single_time_onoff(DevNo, rodNumList, cmd_type,
                                L1_time1, L1_time2, Lux_1, L2_time1, L2_time2, Lux_2,
                                L3_time1, L3_time2, Lux_3, L4_time1, L4_time2, Lux_4,
                                tmp_int2_str, log_name_str, obj_strs);
                    } else if (listTitle1.getVisibility() == View.VISIBLE) {//支路X-N
                        cmd_type = "广播时控灯1支路";
                        //"0,0#0,0#0,0#0,0#0,0#0,0#0,0#0,0#0,0#0,0#0,0#0,0#0,0#0,0#0,0#0,0"
                        String obj_array[] = {"0,0", "0,0", "0,0", "0,0", "0,0", "0,0", "0,0",
                                "0,0", "0,0", "0,0", "0,0", "0,0", "0,0", "0,0", "0,0", "0,0"};
                        for (int i = 0; i < list1.size(); i++) {
                            obj_array[i] = ConvertUtils.boolToInt((boolean) list1.get(i).get("even"))
                                    + "," + ConvertUtils.boolToInt((boolean) list1.get(i).get("odd"));
                        }
                        for (int j = 0; j < obj_array.length; j++) {
                            if (j > 0) {
                                obj_strs += "#";
                            }
                            obj_strs += obj_array[j].trim();
                        }
                        tmp_int2_str = "";
                        Setweb_single_time_onoff(DevNo, rodNumList, cmd_type,
                                L1_time1, L1_time2, Lux_1, L2_time1, L2_time2, Lux_2,
                                L3_time1, L3_time2, Lux_3, L4_time1, L4_time2, Lux_4,
                                tmp_int2_str, log_name_str, obj_strs);
                    } else if (listTitle2.getVisibility() == View.VISIBLE) {//自定义组
                        cmd_type = "广播时控灯1";
                        obj_strs = "";
                        for (int i = 0; i < list2.size(); i++) {
                            if ((boolean) list2.get(i).get("op")) {
                                tmp_int2_str = list2.get(i).get("CG_NO").toString().trim();
                                Setweb_single_time_onoff(DevNo, rodNumList, cmd_type,
                                        L1_time1, L1_time2, Lux_1, L2_time1, L2_time2, Lux_2,
                                        L3_time1, L3_time2, Lux_3, L4_time1, L4_time2, Lux_4,
                                        tmp_int2_str, log_name_str, obj_strs);
                            }
                        }
                    }
                }
            }
        });
    }

    public class ViewHolder {
        public CheckBox checkbox1;
        public CheckBox checkbox2;
        public TextView tv1;
        public TextView tv2;
    }
    private void initList1() {
        //初始化单灯列表
        adapter1 = new BaseAdapter() {
            private LayoutInflater mInflater;

            @Override
            public int getCount() {
                return list1.size();
            }

            @Override
            public Object getItem(int position) {
                return list1.get(position);
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
                    mInflater = LayoutInflater.from(getContext().getApplicationContext());//获得上下文
                    convertView = mInflater.inflate(
                            R.layout.single_frag_simplecontrol_list_item1, null);
                    holder.checkbox1 = (CheckBox) convertView.findViewById(R.id.checkbox1);
                    holder.checkbox2 = (CheckBox) convertView.findViewById(R.id.checkbox2);
                    holder.tv1 = (TextView) convertView.findViewById(R.id.tv1);
                    holder.tv2 = (TextView) convertView.findViewById(R.id.tv2);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.checkbox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        list1.get(position).put("even", compoundButton.isChecked());
                    }
                });
                holder.checkbox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        list1.get(position).put("odd", compoundButton.isChecked());
                    }
                });
                holder.checkbox1.setChecked((boolean) list1.get(position).get("even"));
                holder.checkbox2.setChecked((boolean) list1.get(position).get("odd"));
                holder.tv1.setText((String) list1.get(position).get("DevNo"));
                holder.tv2.setText((String) list1.get(position).get("post"));
                return convertView;
            }
        };
        listView1.setAdapter(adapter1);
    }

    private void initList2() {
        //初始化单灯列表
        adapter2 = new BaseAdapter() {
            private LayoutInflater mInflater;

            @Override
            public int getCount() {
                return list2.size();
            }

            @Override
            public Object getItem(int position) {
                return list2.get(position);
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
                    mInflater = LayoutInflater.from(getContext().getApplicationContext());//获得上下文
                    convertView = mInflater.inflate(
                            R.layout.single_frag_simplecontrol_list_item2, null);
                    holder.checkbox1 = (CheckBox) convertView.findViewById(R.id.list2_check);
                    holder.tv1 = (TextView) convertView.findViewById(R.id.list2_tv1);
                    holder.tv2 = (TextView) convertView.findViewById(R.id.list2_tv2);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.checkbox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        list2.get(position).put("op", compoundButton.isChecked());
                    }
                });
                holder.checkbox1.setChecked((boolean) list2.get(position).get("op"));
                holder.tv1.setText((String) list2.get(position).get("CG_NO"));
                holder.tv2.setText((String) list2.get(position).get("CG_NAME"));
                return convertView;
            }
        };
        listView2.setAdapter(adapter2);
    }

    private void Setweb_single_time_onoff(int Dev_id, List<String> rod_num, String cmd_type,
                                          String L1_time1, String L1_time2, int L1_Lux12,
                                          String L2_time1, String L2_time2, int L2_Lux12,
                                          String L3_time1, String L3_time2, int L3_Lux12,
                                          String L4_time1, String L4_time2, int L4_Lux12,
                                          String tmp_int2_str, String log_name_str, String obj_strs) {
        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), null, "设置中...", true);
        try {
            String URL = rootURL + "/wcf/Setweb_single_time_onoff?Dev_id=" + Dev_id;
            for(int i = 0; i < rod_num.size(); i++) {
                URL +=  "&rod_num[" + i + "]=" + rod_num.get(i);
            }
            URL += "&cmd_type=" + URLEncoder.encode(cmd_type, "utf-8").trim()
                    + "&L1_time1=" + L1_time1
                    + "&L1_time2=" + L1_time2
                    + "&L1_Lux12=" + L1_Lux12
                    + "&L2_time1=" + L2_time1
                    + "&L2_time2=" + L2_time2
                    + "&L2_Lux12=" + L2_Lux12
                    + "&L3_time1=" + L3_time1
                    + "&L3_time2=" + L3_time2
                    + "&L3_Lux12=" + L3_Lux12
                    + "&L4_time1=" + L4_time1
                    + "&L4_time2=" + L4_time2
                    + "&L4_Lux12=" + L4_Lux12;
            if(!tmp_int2_str.trim().equals("")) {
                URL += "&tmp_int2_str=" + tmp_int2_str;
            }
            URL += "&log_name_str=" + URLEncoder.encode(log_name_str, "utf-8").trim();
            if(!tmp_int2_str.trim().equals("")) {
                URL += "&obj_strs=" + obj_strs;
            }
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
                                    String temp_str = jsonObject.getString("rslt");
                                    if(temp_str != null) {
                                        ((SingleActivity)getActivity()).updateResultList(temp_str.trim());
                                    }
                                    ((SingleActivity) getActivity()).saveLog(rootURL, userName);
                                } catch (JSONException e) {
                                    Toast.makeText(getActivity(), "用户名加载失败", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "用户加载找不到接口!!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
            //设置请求的Tag标签，可以在全局请求队列中通过Tag标签进行请求的查找
            stringRequest.setTag("");
            //将请求加入全局队列中
            VollyQueue.getHttpQueues().add(stringRequest);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "网络故障!!!", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    //获取支路组X-N表
    private void Getsingle_volt_detail_group(int Dev_id) {
        final ProgressDialog dialog = ProgressDialog.show(getActivity(), null, "刷新中...", true);
        String URL = rootURL + "/Single/Getsingle_volt_detail_group?Dev_id="+ Dev_id;
        System.out.println(URL);
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            dialog.dismiss();
                            System.out.println(response);
                            if(!response.isEmpty() && !response.trim().equals("")) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONArray jsonArrayDevNo = jsonObject.getJSONArray("DevNo");
                                    JSONArray jsonArrayPost = jsonObject.getJSONArray("post");
                                    list1.clear();
                                    for(int i = 0; i < jsonArrayDevNo.length(); i++) {
                                        Map<String, Object> map = new HashMap<String, Object>();
                                        map.put("even", false);
                                        map.put("odd", false);
                                        map.put("DevNo", jsonArrayDevNo.get(i));
                                        map.put("post", jsonArrayPost.get(i));
                                        list1.add(map);
                                    }
                                    adapter1.notifyDataSetChanged();
                                    if(list1.size() > 0) {
                                        listView1.setVisibility(View.VISIBLE);
                                        noList.setVisibility(View.GONE);
                                    } else {
                                        listView1.setVisibility(View.GONE);
                                        noList.setVisibility(View.VISIBLE);
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(getActivity(), "用户名加载失败", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "用户加载找不到接口!!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
            //设置请求的Tag标签，可以在全局请求队列中通过Tag标签进行请求的查找
            stringRequest.setTag("");
            //将请求加入全局队列中
            VollyQueue.getHttpQueues().add(stringRequest);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "网络故障!!!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    }

    //获取自定义组表
    private void Getsingle_G_control_set(int Dev_id) {
        final ProgressDialog dialog = ProgressDialog.show(getActivity(), null, "刷新中...", true);
        String URL = rootURL + "/Single/Getsingle_G_control_set?Dev_id="+ Dev_id;
        System.out.println(URL);
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            dialog.dismiss();
                            System.out.println(response);
                            if(!response.isEmpty() && !response.trim().equals("")) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONArray jsonArrayDevNo = jsonObject.getJSONArray("DevNo");
                                    JSONArray jsonArrayCG_NO = jsonObject.getJSONArray("CG_NO");
                                    JSONArray jsonArrayCG_NAME = jsonObject.getJSONArray("CG_NAME");
                                    list2.clear();
                                    for(int i = 0; i < jsonArrayDevNo.length(); i++) {
                                        Map<String, Object> map = new HashMap<String, Object>();
                                        map.put("DevNo", jsonArrayDevNo.get(i));
                                        map.put("CG_NO", jsonArrayCG_NO.get(i));
                                        map.put("CG_NAME", jsonArrayCG_NAME.get(i));
                                        map.put("op", false);
                                        list2.add(map);
                                    }
                                    adapter2.notifyDataSetChanged();
                                    if(list2.size() > 0) {
                                        listView2.setVisibility(View.VISIBLE);
                                        noList.setVisibility(View.GONE);
                                    } else {
                                        listView2.setVisibility(View.GONE);
                                        noList.setVisibility(View.VISIBLE);
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(getActivity(), "用户名加载失败", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "用户加载找不到接口!!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
            //设置请求的Tag标签，可以在全局请求队列中通过Tag标签进行请求的查找
            stringRequest.setTag("");
            //将请求加入全局队列中
            VollyQueue.getHttpQueues().add(stringRequest);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "网络故障!!!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    }

}
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.administrator.light.R;
import com.example.administrator.light.VollyQueue;
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
public class DimmingFragmentSimpleControl extends Fragment {
    private View view;
    private int DevNo;
    private String rootURL, userName;

    private Spinner spinner1, spinner2, spinner3;
    private Button btSet, btRecovery, btRefresh;
    private CheckBox checkExpand, checkLight1, checkLight2, checkMutilControl;
    private SeekBar seekBar;
    private TextView tv_seekBar, noList;
    private LinearLayout expandLayout, mutilLayout, listTitle1, listTitle2;
    private RadioGroup radioGroup;
    private ListView listView1, listView2;
    private BaseAdapter adapter1, adapter2;
    private List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view=inflater.inflate(R.layout.single_dimming_frag_simplecontrol, container, false);
        init();
        initBtListener();
        initSeekBarListener();
        initRBListener();
        initCheckListener();
        initSpinnerListener();
        JudgesVisibility();
        return view;
    }

    public void init() {
        rootURL = (String) SharedPreferencesUtils.getParam(getActivity(), "rootURL", "");
        userName = (String) SharedPreferencesUtils.getParam(getActivity(), "username", "");
        DevNo = ((SingleActivity)getActivity()).getDevNo();

        spinner1 = (Spinner)view.findViewById(R.id.spinner1);
        spinner2 = (Spinner)view.findViewById(R.id.spinner2);
        spinner3 = (Spinner)view.findViewById(R.id.simple_spinner3);

        btSet = (Button)view.findViewById(R.id.btSet);
        btRecovery = (Button)view.findViewById(R.id.btRecovery);
        btRefresh = (Button)view.findViewById(R.id.simple_bt_refresh);

        checkExpand = (CheckBox)view.findViewById(R.id.checkExpand);
        checkMutilControl = (CheckBox)view.findViewById(R.id.checkMutilControl);
        checkLight1 = (CheckBox)view.findViewById(R.id.checkLight1);
        checkLight2 = (CheckBox)view.findViewById(R.id.checkLight2);

        seekBar = (SeekBar)view.findViewById(R.id.simple_seekbar);
        tv_seekBar = (TextView)view.findViewById(R.id.simple_seekbar_value);

        expandLayout = (LinearLayout)view.findViewById(R.id.simple_layout1);
        mutilLayout = (LinearLayout)view.findViewById(R.id.simple_layout2);
        listTitle1 = (LinearLayout)view.findViewById(R.id.simple_list_title1);
        listTitle2 = (LinearLayout)view.findViewById(R.id.simple_list_title2);
        radioGroup = (RadioGroup)view.findViewById(R.id.simple_rg);

        listView1 = (ListView)view.findViewById(R.id.simple_list1);
        listView2 = (ListView)view.findViewById(R.id.simple_list2);
        noList = (TextView)view.findViewById(R.id.simple_nolist);
    }

    private void JudgesVisibility() {
        if (checkExpand.isChecked() == false) {
            expandLayout.setVisibility(View.GONE);
            checkMutilControl.setVisibility(View.INVISIBLE);
            mutilLayout.setVisibility(View.GONE);
            spinner3.setEnabled(true);
        } else if (!spinner1.getSelectedItem().toString().equals("组号")) {
            expandLayout.setVisibility(View.VISIBLE);
            checkMutilControl.setVisibility(View.INVISIBLE);
            mutilLayout.setVisibility(View.GONE);
            spinner3.setEnabled(true);
        } else if(checkMutilControl.isChecked() == false) {
            expandLayout.setVisibility(View.VISIBLE);
            checkMutilControl.setVisibility(View.VISIBLE);
            mutilLayout.setVisibility(View.GONE);
            spinner3.setEnabled(true);
        } else {
            expandLayout.setVisibility(View.VISIBLE);
            checkMutilControl.setVisibility(View.VISIBLE);
            mutilLayout.setVisibility(View.VISIBLE);
            spinner3.setSelection(0);
            spinner3.setEnabled(false);
            radioGroup.check(R.id.simple_rb1);
        }
    }

    private void initBtListener() {
        Button.OnClickListener btListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.simple_bt_refresh:
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
                        break;
                    default:
                        break;
                }
            }
        };
        btRefresh.setOnClickListener(btListener);

        Button.OnClickListener setListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> rod_num =
                        ((SingleActivity)getActivity()).getmForceOnOffFg().getSelectedSingleRodNum();
                String cmd_type = "";
                String chk_flag_str = "";
                String log_name_str = userName;
                String obj_strs = "";
                if (rod_num.size() < 1) {
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
                    switch (view.getId()) {
                        case R.id.btSet:
                            cmd_type = spinner1.getSelectedItem().toString().trim()
                                    + spinner2.getSelectedItem().toString().trim();
                            break;
                        case R.id.btRecovery:
                            cmd_type = "恢复时控";
                            break;
                    }
                    if(!checkExpand.isChecked()) {
                        chk_flag_str = "1100";
                        obj_strs = "";
                        Setweb_single_BO_onoff(DevNo, rod_num, cmd_type, chk_flag_str, log_name_str, obj_strs);
                    } else if(mutilLayout.getVisibility() != View.VISIBLE){
                        chk_flag_str = ConvertUtils.boolToInt(checkLight1.isChecked())
                                + ConvertUtils.boolToInt(checkLight2.isChecked())
                                + "00," + tv_seekBar.getText().toString().trim()
                                + "," + tv_seekBar.getText().toString().trim()
                                + "," + spinner3.getSelectedItem().toString().trim()
                                + "," + spinner3.getSelectedItem().toString().trim();
                        obj_strs = "";
                        Setweb_single_BO_onoff(DevNo, rod_num, cmd_type, chk_flag_str, log_name_str, obj_strs);
                    } else if(listTitle1.getVisibility() == View.VISIBLE) {//支路X-N
                        cmd_type += "支路";
                        chk_flag_str = ConvertUtils.boolToInt(checkLight1.isChecked())
                                + ConvertUtils.boolToInt(checkLight2.isChecked())
                                + "00," + tv_seekBar.getText().toString().trim()
                                + "," + tv_seekBar.getText().toString().trim()
                                + "," + spinner3.getSelectedItem().toString().trim()
                                + "," + spinner3.getSelectedItem().toString().trim();
                        //"0,0#0,0#0,0#0,0#0,0#0,0#0,0#0,0#0,0#0,0#0,0#0,0#0,0#0,0#0,0#0,0"
                        String obj_array[] = {"0,0", "0,0", "0,0", "0,0", "0,0", "0,0", "0,0",
                                "0,0", "0,0", "0,0", "0,0", "0,0", "0,0", "0,0", "0,0", "0,0"};
                        for(int i = 0; i< list1.size(); i++) {
                            obj_array[i] = ConvertUtils.boolToInt((boolean) list1.get(i).get("even"))
                                    + "," +  ConvertUtils.boolToInt((boolean) list1.get(i).get("odd"));
                        }
                        for(int j = 0; j < obj_array.length; j++) {
                            if(j > 0) {
                                obj_strs += "#";
                            }
                            obj_strs += obj_array[j].trim();
                        }
                        Setweb_single_BO_onoff(DevNo, rod_num, cmd_type, chk_flag_str, log_name_str, obj_strs);
                    } else if(listTitle2.getVisibility() == View.VISIBLE) {//自定义组
                        for(int i = 0; i< list2.size(); i++) {
                            if((boolean)list2.get(i).get("op")) {
                                chk_flag_str = ConvertUtils.boolToInt(checkLight1.isChecked())
                                        + ConvertUtils.boolToInt(checkLight2.isChecked())
                                        + "00," + tv_seekBar.getText().toString().trim()
                                        + "," + tv_seekBar.getText().toString().trim()
                                        + "," + list2.get(i).get("CG_NO")
                                        + "," + list2.get(i).get("CG_NO");
                                obj_strs = "";
                                Setweb_single_BO_onoff(DevNo, rod_num, cmd_type, chk_flag_str, log_name_str, obj_strs);
                            }
                        }
                    }
                }
            }
        };
        btSet.setOnClickListener(setListener);
        btRecovery.setOnClickListener(setListener);
    }

    private void initSeekBarListener() {
        SeekBar.OnSeekBarChangeListener seekbarListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String Progress_str = Integer.toString(seekBar.getProgress());
                switch (seekBar.getId()) {
                    case  R.id.simple_seekbar:
                        tv_seekBar.setText(Progress_str);
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };
        seekBar.setOnSeekBarChangeListener(seekbarListener);
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
                        noList.setVisibility(View.GONE);
                        break;
                    case R.id.simple_rb2:
                        initList2();
                        Getsingle_G_control_set(DevNo);
                        listTitle1.setVisibility(View.GONE);
                        listView1.setVisibility(View.GONE);
                        listTitle2.setVisibility(View.VISIBLE);
                        listView2.setVisibility(View.VISIBLE);
                        noList.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void initCheckListener() {
        checkExpand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                JudgesVisibility();
            }
        });
        checkMutilControl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                JudgesVisibility();
            }
        });
    }

    private void initSpinnerListener() {
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                JudgesVisibility();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (adapterView.getSelectedItem().toString()) {
                    case "开灯":
                        seekBar.setProgress(100);
                        seekBar.setEnabled(true);
                        break;
                    case "关灯":
                        seekBar.setProgress(0);
                        seekBar.setEnabled(false);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
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

    //按广播开关灯
    private void Setweb_single_BO_onoff(int Dev_id, List<String> rod_num, String cmd_type,
                                        String chk_flag_str, String log_name_str, String obj_strs) {
        final ProgressDialog dialog = ProgressDialog.show(getActivity(), null, "设置中...", true);
        System.out.println(rod_num.size());
        try {
            String URL = rootURL + "/wcf/Setweb_single_BO_onoff?Dev_id="+ Dev_id;
            for(int i = 0; i < rod_num.size(); i++) {
                URL +=  "&rod_num[" + i + "]=" + rod_num.get(i);
            }
            URL += "&cmd_type=" + URLEncoder.encode(cmd_type, "utf-8").trim()
                    + "&chk_flag_str=" + chk_flag_str
                    + "&log_name_str=" + URLEncoder.encode(log_name_str, "utf-8").trim()
                    + "&obj_strs=" + obj_strs;
            System.out.println(URL);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println(response);
                            dialog.dismiss();
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
                    dialog.dismiss();
                }
            });
            stringRequest.setTag("");
            VollyQueue.getHttpQueues().add(stringRequest);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "网络故障!!!", Toast.LENGTH_SHORT).show();
        }
    }

    //获取支路组X-N表
    private void Getsingle_volt_detail_group(int Dev_id) {
        final ProgressDialog dialog = ProgressDialog.show(getActivity(), null, "刷新中...", true);
        String URL = rootURL + "/Single/Getsingle_volt_detail_group?Dev_id=" + Dev_id;
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
            stringRequest.setTag("");
            VollyQueue.getHttpQueues().add(stringRequest);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "网络故障!!!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    }

    //获取自定义组表
    private void Getsingle_G_control_set(int Dev_id) {
        final ProgressDialog dialog = ProgressDialog.show(getActivity(), null, "刷新中...", true);
        String URL = rootURL + "/Single/Getsingle_G_control_set?Dev_id=" + Dev_id;
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
            stringRequest.setTag("");
            VollyQueue.getHttpQueues().add(stringRequest);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "网络故障!!!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    }

}
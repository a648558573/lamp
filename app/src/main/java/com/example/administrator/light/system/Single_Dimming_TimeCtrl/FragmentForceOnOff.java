package com.example.administrator.light.system.Single_Dimming_TimeCtrl;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.administrator.light.R;
import com.example.administrator.light.VollyQueue;
import com.example.administrator.light.system.SingleDetailParameter;
import com.example.administrator.light.util.SerializableMap;
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
 * Created by JO on 2016/4/29.
 */
public class FragmentForceOnOff extends Fragment {
    private View view;
    private int DevNo;
    private String rootURL, userName;
    private String myTAG;

    private Button btAll, btGet, btSet, btRefresh,btSet1;
    private Spinner spinner;
    private ListView singleListView;
    private List<Map<String, Object>> singleList = new ArrayList<Map<String, Object>>();
    private BaseAdapter singleAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.single_frag_force_onoff, container, false);
        init();
        initListener();
        initListView();
        Getsingle_volt_detail(DevNo);
        return view;
    }

    public void init() {
        rootURL = (String) SharedPreferencesUtils.getParam(getActivity(), "rootURL", "");
        userName = (String) SharedPreferencesUtils.getParam(getActivity(), "userName", "");

        btAll = (Button)view.findViewById(R.id.btAll);
        btGet = (Button)view.findViewById(R.id.btGet);
        btSet = (Button)view.findViewById(R.id.btSet);
        btSet1 = (Button)view.findViewById(R.id.btSet1);
        btRefresh = (Button)view.findViewById(R.id.btRefresh);
        spinner = (Spinner)view.findViewById(R.id.spinner);
        singleListView = (ListView)view.findViewById(R.id.single_list);

        DevNo = ((SingleActivity) getActivity()).getDevNo();
        myTAG = ((SingleActivity)getActivity()).getMyTAG();
    }

    private void initListener() {
        btAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context dialogContext = new ContextThemeWrapper(getActivity(),
                    android.R.style.Theme_Light);
                String[] choices = {"全部取消", "全部选择"};
                ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
                        android.R.layout.simple_list_item_1, choices);
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(dialogContext);
                builder.setSingleChoiceItems(adapter, -1,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                boolean switchState = false;
                                switch (which) {
                                    case 0:
                                        switchState = false;
                                        break;
                                    case 1:
                                        switchState = true;
                                        break;
                                }
                                for (int i = 0; i < singleList.size(); i++) {
                                    singleList.get(i).put("is_checked", switchState);
                                }
                                singleAdapter.notifyDataSetChanged();
                            }
                        });
                builder.create().show();
            }
        });
        //刷新
        btRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Getsingle_volt_detail(DevNo);
            }
        });
        //获取
        btGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSelectedSingleRodNum().size() < 1) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("提示")
                            .setMessage("请勾选想要获取的单灯")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                } else {
                    Addweb_single_query(DevNo, getSelectedSingleRodNum(), spinner.getSelectedItem().toString(), userName);
                    handler.postDelayed(runnable, 4000);
                }
            }
        });
        //设置
//        switch (myTAG) {
//            case "Dimming":
//                btSet.setText("手动设置亮度");
//                break;
//            case "TimeControl":
//                btSet.setText("设置时段开关灯");
//                break;
//        }
        btSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSelectedSingleRodNum().size() < 1) {
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
                            final DimmingDialogForceSet.Builder builder = new DimmingDialogForceSet.Builder(getActivity());
                            builder.setTitle("设置亮度")
                                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            int Lux_1 = 255, Lux_2 = 255, Lux_3 = 255, Lux_4 = 255;
                                            if (builder.getCheckBox1().isChecked()) {
                                                Lux_1 = Integer.parseInt(builder.getTv_value1().getText().toString());
                                            }
                                            if (builder.getCheckBox1().isChecked()) {
                                                Lux_2 = Integer.parseInt(builder.getTv_value2().getText().toString());
                                            }
                                            dialogInterface.dismiss();
                                            Setweb_single_onoff(DevNo, getSelectedSingleRodNum(), "设置亮度",
                                                    Lux_1, Lux_2, Lux_3, Lux_4, userName, "");
                                            handler.postDelayed(runnable, 4000);
                                        }
                                    }).create().show();
                }
            }
        });
        btSet1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSelectedSingleRodNum().size() < 1) {
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
                            final TimeCtrlDialogForceSet.Builder builderT = new TimeCtrlDialogForceSet.Builder(getActivity(), getActivity());
                            builderT.setTitle("设置时段开关灯")
                                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            List<String> rodNumList = getSelectedSingleRodNum();
                                            String cmd_type = "";
                                            int Lux_1 = 255, Lux_2 = 255, Lux_3 = 255, Lux_4 = 255;
                                            String L1_time1 = "", L1_time2 = "", L2_time1 = "", L2_time2 = "",
                                                    L3_time1 = "", L3_time2 = "", L4_time1 = "", L4_time2 = "";
                                            L1_time1 = builderT.getBtStart1().getText().toString();
                                            L1_time2 = builderT.getBtEnd1().getText().toString();
                                            L2_time1 = builderT.getBtStart2().getText().toString();
                                            L2_time2 = builderT.getBtEnd2().getText().toString();
                                            L3_time1 = builderT.getBtStart3().getText().toString();
                                            L3_time2 = builderT.getBtEnd3().getText().toString();
                                            L4_time1 = builderT.getBtStart4().getText().toString();
                                            L4_time2 = builderT.getBtEnd4().getText().toString();

                                            switch (builderT.getSpinner2().getSelectedItem().toString()) {
                                                case "时控灯1":
                                                case "档位时段1-4":
                                                    cmd_type = "设置时控灯1";
                                                    break;
                                                case "时控灯2":
                                                case "档位时段5-8":
                                                    cmd_type = "设置时控灯2";
                                                    break;
                                            }
                                            if (builderT.getCheckBox1().isChecked()) {
                                                switch (builderT.getSpinner1().getSelectedItem().toString().trim()) {
                                                    case "LED灯调光（0-100%）":
                                                        Lux_1 = Integer.parseInt(builderT.getBtValue1().getText().toString().split("亮度")[1].split("%")[0].trim());
                                                        break;
                                                    case "钠灯调档（0-4档）":
                                                        Lux_1 = Integer.parseInt(builderT.getBtValue1().getText().toString().split("档位")[1].trim());
                                                        break;
                                                }
                                            }
                                            if (builderT.getCheckBox2().isChecked()) {
                                                switch (builderT.getSpinner1().getSelectedItem().toString().trim()) {
                                                    case "LED灯调光（0-100%）":
                                                        Lux_2 = Integer.parseInt(builderT.getBtValue2().getText().toString().split("亮度")[1].split("%")[0].trim());
                                                        break;
                                                    case "钠灯调档（0-4档）":
                                                        Lux_2 = Integer.parseInt(builderT.getBtValue2().getText().toString().split("档位")[1].trim());
                                                        break;
                                                }
                                            }
                                            if (builderT.getCheckBox3().isChecked()) {
                                                switch (builderT.getSpinner1().getSelectedItem().toString().trim()) {
                                                    case "LED灯调光（0-100%）":
                                                        Lux_3 = Integer.parseInt(builderT.getBtValue3().getText().toString().split("亮度")[1].split("%")[0].trim());
                                                        break;
                                                    case "钠灯调档（0-4档）":
                                                        Lux_3 = Integer.parseInt(builderT.getBtValue3().getText().toString().split("档位")[1].trim());
                                                        break;
                                                }
                                            }
                                            if (builderT.getCheckBox4().isChecked()) {
                                                switch (builderT.getSpinner1().getSelectedItem().toString().trim()) {
                                                    case "LED灯调光（0-100%）":
                                                        Lux_4 = Integer.parseInt(builderT.getBtValue4().getText().toString().split("亮度")[1].split("%")[0].trim());
                                                        break;
                                                    case "钠灯调档（0-4档）":
                                                        Lux_4 = Integer.parseInt(builderT.getBtValue4().getText().toString().split("档位")[1].trim());
                                                        break;
                                                }
                                            }
                                            dialogInterface.dismiss();
                                            Setweb_single_time_onoff(DevNo, rodNumList, cmd_type,
                                                    L1_time1, L1_time2, Lux_1, L2_time1, L2_time2, Lux_2,
                                                    L3_time1, L3_time2, Lux_3, L4_time1, L4_time2, Lux_4,
                                                    "", userName, "");
                                            handler.postDelayed(runnable, 4000);
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    }).create().show();
                }
            }
        });
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Getsingle_volt_detail(DevNo);
        }
    };

    public final class ViewHolder {
        public CheckBox checkBox;
        public TextView tv_rod_real;
        public Button btn_rod_num;
        public ImageView light1;
        public ImageView light2;
        public TextView time;
        public TextView result;
    }

    private void initListView() {
        //初始化单灯列表
        singleAdapter = new BaseAdapter() {
            private LayoutInflater mInflater;

            @Override
            public int getCount() {
                return singleList.size();
            }

            @Override
            public Object getItem(int position) {
                return singleList.get(position);
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
                    convertView = mInflater.inflate(R.layout.single_frag_forceonoff_list_item, null);
                    holder.checkBox = (CheckBox) convertView.findViewById(R.id.force_checkbox);
                    holder.tv_rod_real = (TextView)convertView.findViewById(R.id.force_rod_real);
                    holder.btn_rod_num = (Button) convertView.findViewById(R.id.force_rod_num);
                    holder.light1 = (ImageView) convertView.findViewById(R.id.force_light1);
                    holder.light2 = (ImageView) convertView.findViewById(R.id.force_light2);
                    holder.time = (TextView) convertView.findViewById(R.id.force_time);
                    holder.result = (TextView) convertView.findViewById(R.id.force_result);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                if(singleList.get(position).get("is_checked") != null) {
                    holder.checkBox.setChecked((Boolean) singleList.get(position).get("is_checked"));
                } else {
                    holder.checkBox.setChecked(false);
                }
                holder.tv_rod_real.setText((String) singleList.get(position).get("rod_real"));
                holder.btn_rod_num.setText((String) singleList.get(position).get("rod_num"));
                holder.light1.setBackgroundResource((Integer) singleList.get(position).get("light1"));
                holder.light2.setBackgroundResource((Integer) singleList.get(position).get("light2"));
                holder.time.setText((String) singleList.get(position).get("update_dtm"));
                holder.result.setText((String) singleList.get(position).get("single_state2"));

                holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (compoundButton.isChecked()) {
                            // Integer pos_int=(Integer) ((CheckBox)v).getTag();
                            //   bool_chk_arr[position]=((CheckBox)v).isChecked();
                            String temp = (String) singleList.get(position).get("rod_num");
                            Toast.makeText(getActivity(), "选择" + temp, Toast.LENGTH_SHORT).show();
                            singleList.get(position).put("is_checked", true);
                        } else {
                            singleList.get(position).put("is_checked", false);
                        }
                    }
                });
                holder.btn_rod_num.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        SerializableMap tmpMap = new SerializableMap();
                        tmpMap.setMap(singleList.get(position));
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("singleMap", tmpMap);
                        intent.putExtras(bundle);
                        intent.setClass(getActivity(), SingleDetailParameter.class);
                        startActivity(intent);
                    }
                });
                return convertView;
            }
        };
        singleListView.setAdapter(singleAdapter);
        singleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView adapterView, View view, int arg2, long arg3) {
//                String devno_str = "";
//                try {
//                    devno_str = alarmlist.get_onofftime(arg2).trim();
//                    // String[] temp_strs=  devno_str.split(" . ");
//                    //  devno_str=temp_strs[0].trim();
//                    if (!devno_str.equals("")) {
//                        set_setting_info(devno_str.trim(), "selNode");
//                        Thread thread0 = new Thread(new baidumapRunHandler());
//                        thread0.start();
//                    }
//
//                } catch (Exception ex) {
//                }
            }
        });
    }

    /**
     * 获取单灯信息列表
     * @param Dev_id 终端号
     */
    private void Getsingle_volt_detail(int Dev_id) {
        int pageSize = 0;
        int CurrentPageIndex = 0;
        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), null, "刷新中...", true);
        try {
            String URL = rootURL + "/Single/Getsingle_volt_detail?Dev_id=" + Dev_id
                    + "&pageSize=" + pageSize
                    + "&CurrentPageIndex=" + CurrentPageIndex;
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
                                    JSONArray jsonArray = jsonObject.getJSONArray("single_volt_detail");
                                    singleList.clear();
                                    for(int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject itemObject = jsonArray.getJSONObject(i);
                                        if(itemObject.getString("rod_num").trim().equals("0-0")) {
                                            continue;
                                        }
                                        Map<String, Object> map = new HashMap<String, Object>();
                                        map.put("is_checked", false);
                                        map.put("rod_real", itemObject.getString("rod_real").trim());
                                        map.put("rod_num", itemObject.getString("rod_num").trim());
                                        map.put("light1", getOnoffStateImg(itemObject.getString("single_state3").trim(),
                                                1, itemObject.getString("L_num").trim())); //设置灯显示的图标
                                        map.put("light2", getOnoffStateImg(itemObject.getString("single_state3").trim(),
                                                2, itemObject.getString("L_num").trim()));

                                        String time_str = "";
                                        try {
                                            time_str = get_jason_date(itemObject.getString("update_dtm").trim()).trim().split(" ")[1];  //时间
                                            System.out.println("time_str-->" + time_str);
                                        } catch (Exception e) {

                                        }
                                        map.put("update_dtm", time_str.trim());

                                        if(!time_str.trim().equals("")) {
                                            map.put("single_state2", itemObject.getString("single_state2").trim());
                                        } else {
                                            map.put("single_state2", "今天暂无执行记录");
                                        }

                                        map.put("I1", itemObject.getString("I1").trim());
                                        map.put("V_rod", itemObject.getString("V_rod").trim());
                                        map.put("Lux_1", itemObject.getString("Lux_1").trim());
//                                        map.put("V_rod", itemObject.getString("V_rod").trim());

                                        map.put("I2", itemObject.getString("I2").trim());
                                        map.put("V_rod2", itemObject.getString("V_rod2").trim());
                                        map.put("Lux_2", itemObject.getString("Lux_2").trim());
//                                        map.put("V_rod", itemObject.getString("V_rod").trim());

                                        map.put("rod_alarm", itemObject.getString("rod_alarm").trim());   //灯杆报警
                                        map.put("alarm_info", itemObject.getString("alarm_info").trim()); //总报警内容
                                        map.put("alarm_1", itemObject.getString("alarm_1").trim());       //1灯警报
                                        map.put("alarm_2", itemObject.getString("alarm_2").trim());       //2灯警报
                                        map.put("alarm_3", itemObject.getString("alarm_3").trim());       //1灯扩警报
                                        map.put("alarm_4", itemObject.getString("alarm_4").trim());       //2灯扩警报
                                        map.put("rod_V_up", itemObject.getString("rod_V_up").trim());     //电压上限
                                        map.put("rod_V_down", itemObject.getString("rod_V_down").trim()); //电压下限
                                        map.put("I1_up", itemObject.getString("I1_up").trim());            //电流1上限
                                        map.put("I2_up", itemObject.getString("I2_up").trim());            //电流2上限
                                        map.put("I3_up", itemObject.getString("I3_up").trim());            //1灯阀值
                                        map.put("I4_up", itemObject.getString("I4_up").trim());            //2灯阀值

                                        singleList.add(map);
                                    }
                                    singleAdapter.notifyDataSetChanged();

                                    if(jsonArray.length() > 0) {
                                        ((SingleActivity)getActivity()).updateResultList(">>刷新成功");
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
                    progressDialog.dismiss();
                }
            });
            //设置请求的Tag标签，可以在全局请求队列中通过Tag标签进行请求的查找
            stringRequest.setTag("");
            //将请求加入全局队列中
            VollyQueue.getHttpQueues().add(stringRequest);
        } catch (Exception e) { //UnsupportedEncodingException
            e.printStackTrace();
            progressDialog.dismiss();
        }
    }

    //解析日期
    private String get_jason_date(String Date_str) {
        try {
            Date_str = Date_str.replace("/Date(", "");
            Date_str = Date_str.replace(")/", "");
            Long long1 = Long.parseLong(Date_str);
            Date date_1 = new Date(long1);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            SimpleDateFormat format_date = new SimpleDateFormat("yyyy-MM-dd");

            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            System.out.println("currentTimeMillis--->" + System.currentTimeMillis());
            System.out.println("format_date.format(date_1).trim()" + format_date.format(date_1).trim());
            System.out.println("format_date.format(curDate).trim()" + format_date.format(curDate).trim());
            if(format_date.format(date_1).trim().equals(format_date.format(curDate).trim())){
                return sdf.format(date_1);
            }
            return "";
            //return sdf.format(date_1);
        } catch (Exception ex) {
            return "";
        }
    }

    //解析开关灯状态
    private int getOnoffStateImg(final String single_state3, final int rod_index, final String L_num) {
        int return_int = R.drawable.offlight;
        try {
            String onoff_str = "0000";
            if (single_state3.split("#").length >= 2
                    && single_state3.trim().split("#")[1].trim().length() >= 4) {
                onoff_str = single_state3.trim().split("#")[1];
            }
            String pic_temp = onoff_str.trim().substring(rod_index - 1, rod_index).trim();
            if (pic_temp.equals("1")) {
                return_int = R.drawable.on_hand;// Properties.Resources.hand_ON;
            } else if (pic_temp.equals("2")) {
                return_int = R.drawable.on_time;// Properties.Resources.time_ON;
            } else if (pic_temp.equals("3")) {
                return_int = R.drawable.on_bo;// Properties.Resources.bo_ON;
            } else if (pic_temp.equals("4")) {
                return_int = R.drawable.off_time;// Properties.Resources.time_OFF;
            } else if (pic_temp.equals("5")) {
                return_int = R.drawable.off_bo;// Properties.Resources.bo_OFF;
            } else if (pic_temp.equals("6")) {
                return_int = R.drawable.off_hand;// Properties.Resources.hand_OFF;
            }

            if (rod_index == 2 && !L_num.trim().equals("2")) //若灯数不为2，且灯位是2灯则  显示没有2灯
            {
                return R.drawable.lightoff;//nothingpic
            }
        } catch (Exception ex) {
        }
        return return_int;
    }

    //获取
    private void Addweb_single_query(int Dev_id, List<String> rod_num, String cmd_type,
                                     String log_name_str) {
        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), null, "获取中...", true);
        try {
            String URL = rootURL + "/wcf/Addweb_single_query?Dev_id="+ Dev_id;
            for(int i = 0; i < rod_num.size(); i++) {
                URL +=  "&rod_num[" + i + "]=" + rod_num.get(i);
            }
            URL += "&cmd_type=" + URLEncoder.encode(cmd_type, "utf-8").trim()
                    + "&log_name_str=" + URLEncoder.encode(log_name_str, "utf-8").trim();

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
                                    System.out.println(temp_str);
                                    if(temp_str != null) {
                                        ((SingleActivity)getActivity()).updateResultList(temp_str.trim());
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

    //SingleDimming设置
    private void Setweb_single_onoff(int Dev_id, List<String> rod_num, String cmd_type,
                                     int Lux_1, int Lux_2, int Lux_3, int Lux_4,
                                     String log_name_str, String object_str) {
        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), null, "设置中...", true);
        try {
            String URL = rootURL + "/wcf/Setweb_single_onoff?Dev_id="+ Dev_id;
            for(int i = 0; i < rod_num.size(); i++) {
                URL +=  "&rod_num[" + i + "]=" + rod_num.get(i);
            }
            URL += "&cmd_type=" + URLEncoder.encode(cmd_type, "utf-8").trim()
                    + "&Lux_1=" + Lux_1
                    + "&Lux_2=" + Lux_2
                    + "&Lux_3=" + Lux_3
                    + "&Lux_4=" + Lux_4
                    + "&log_name_str=" + URLEncoder.encode(log_name_str, "utf-8").trim()
                    + "&object_str=" + object_str;
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

    //SingleTimeControl设置
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

    public List<String> getSelectedSingleRodNum() {
        List<String> rodNumList = new ArrayList<String>();
        for(int i = 0; i < singleList.size(); i++) {
            if((boolean)singleList.get(i).get("is_checked")) {
                rodNumList.add(singleList.get(i).get("rod_num").toString());
            }
        }
        return rodNumList;
    }

}
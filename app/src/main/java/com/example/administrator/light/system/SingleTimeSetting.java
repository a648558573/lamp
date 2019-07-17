package com.example.administrator.light.system;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.administrator.light.BaseActivity;
import com.example.administrator.light.R;
import com.example.administrator.light.VollyQueue;
import com.example.administrator.light.customComponent.SingleSettingItem;
import com.example.administrator.light.util.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lxr on 2016/5/25.
 */
public class SingleTimeSetting extends BaseActivity {
    //单灯时控组设
    private String account = null, rootURL = null;
    private String childname;
    private int DevNo=0;
    private int Uid_store = 0;
    private int mHour, mMinute;
    private List<SingleSettingItem> list_item = new ArrayList<SingleSettingItem>();
    private List<String> list_info = new ArrayList<String>();
    private ListView rsltlistView;
    private MyAdapter myAdapter;
    private TimePickerDialog TimePickerDialog0;
    private Button showTime, btRefresh, btGet, btSet;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_time_setting);
        init();
        initListener();
        list_item.get(0).setSpinner1(17);
        refRequest();
    }

    private void init(){
        account = (String) SharedPreferencesUtils.getParam(this, "username", "");
        rootURL = (String) SharedPreferencesUtils.getParam(this, "rootURL", "");
        Intent intent = getIntent();
        childname = intent.getStringExtra("childname");
        String[] temp_strs =childname.split("-");
        if(temp_strs.length > 0) {
            try {
                DevNo = Integer.parseInt(temp_strs[0].trim());
                System.out.println("截取的值是： "+DevNo);
            }catch (Exception ex){}
        }

        //设置标题栏文本
        getToolbarTitle().setText(childname);

        SingleSettingItem singleSettingItem = (SingleSettingItem)findViewById(R.id.setting_group1);
        list_item.add(singleSettingItem);
        singleSettingItem = (SingleSettingItem)findViewById(R.id.setting_group2);
        list_item.add(singleSettingItem);
        singleSettingItem = (SingleSettingItem)findViewById(R.id.setting_group3);
        list_item.add(singleSettingItem);
        singleSettingItem = (SingleSettingItem)findViewById(R.id.setting_group4);
        list_item.add(singleSettingItem);
        singleSettingItem = (SingleSettingItem)findViewById(R.id.setting_group5);
        list_item.add(singleSettingItem);
        singleSettingItem = (SingleSettingItem)findViewById(R.id.setting_group6);
        list_item.add(singleSettingItem);
        singleSettingItem = (SingleSettingItem)findViewById(R.id.setting_group7);
        list_item.add(singleSettingItem);
        singleSettingItem = (SingleSettingItem)findViewById(R.id.setting_group8);
        list_item.add(singleSettingItem);

        btRefresh = (Button)findViewById(R.id.btRefresh);
        btSet = (Button)findViewById(R.id.btSet);
        btGet = (Button)findViewById(R.id.btGet);
        rsltlistView = (ListView)findViewById(R.id.resultListView);
        myAdapter = new MyAdapter(SingleTimeSetting.this);
        rsltlistView.setAdapter(myAdapter);
    }

    private void initListener(){
        View.OnClickListener ocl= new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btRefresh:
                        refRequest();
                        break;
                    case R.id.btGet:
                        getRequest();
                        handler.postDelayed(runnable, 4000);
                        break;
                    case R.id.btSet:
                        setRequest();
                        handler.postDelayed(runnable, 4000);
                        break;
                }
            }
        };
        btGet.setOnClickListener(ocl);
        btSet.setOnClickListener(ocl);
        btRefresh.setOnClickListener(ocl);

        View.OnClickListener ocl1=  new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTime=(Button) view;
                String temp_str= showTime.getText().toString();
                try {
                    mHour = Integer.parseInt(temp_str.split(":")[0]);
                    mMinute = Integer.parseInt(temp_str.split(":")[1]);
                }catch (Exception ex){mHour=0;mMinute=0;}
                TimePickerDialog0= new TimePickerDialog(SingleTimeSetting.this, mTimeSetListener, mHour, mMinute, true);
                TimePickerDialog0.show();
            }
        };

        View.OnClickListener ocl2= new View.OnClickListener() {
            LinearLayout seekbar_layout;
            SeekBar seekBar;
            TextView textView ;
            int progress;
            Button button;
            @Override
            public void onClick(final View view) {
                seekbar_layout = (LinearLayout)getLayoutInflater().inflate(R.layout.seekbardialog, null);
                seekBar = (SeekBar)seekbar_layout.findViewById(R.id.seekbar);
                textView = (TextView)seekbar_layout.findViewById(R.id.seekbar_tv);
                button = (Button)view;
                textView.setText("亮度："+button.getText().toString());
                String[] temp_strs =button.getText().toString().split("%");
                progress = Integer.parseInt(temp_strs[0]);
                System.out.println(progress);
                seekBar.setProgress(progress);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // 开始拖动 SeekBar 时的行为
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int p, boolean fromTouch) {
                        textView.setText("亮度："+String.valueOf(p)+"%");
                        progress=p;
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // 停止拖动 SeekBar 时的行为

                    }
                });
                new AlertDialog.Builder(SingleTimeSetting.this)
                        .setTitle("选择亮度") .setView(seekbar_layout)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                button.setText(String.valueOf(progress) + "%");
                            }
                        }).setNegativeButton("取消", null).create().show();
            }
        };

        AdapterView.OnItemSelectedListener sp1Listener = new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                // An item was selected. You can retrieve the selected item using
                for(int i = 0; i<8; i++){
                    //当有禁用时隐藏下面的item
                    if(list_item.get(i).getSpinner1().toString().trim().equals("禁用")) {
                        int j = i+1;
                        while(j < 8) {
                            list_item.get(j).setVisibility(View.GONE);
                            list_item.get(j).setSpinner1(17);
                            j++;
                        }
                        break;
                    } else {
                        if(i+1 < 8) {
                            list_item.get(i+1).setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        };
        for(int i=0; i<8; i++){
            list_item.get(i).setOnButtonLinstener(ocl1);
            list_item.get(i).setOffButtonLinstener(ocl1);
            list_item.get(i).setLuxButtonLinstener(ocl2);
            list_item.get(i).setSp1Listener(sp1Listener);
        }
    }

    /*
    * 时钟设置*/
    private TimePickerDialog.OnTimeSetListener mTimeSetListener
            = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mHour = hourOfDay;
            mMinute = minute;
            showTime.setText(new StringBuilder().append(mHour).append(":")
                            .append((mMinute < 10) ? "0" + mMinute : mMinute));
        }
    };

    Handler handler=new Handler();
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            refRequest();
        }
    };

    private String setTime(String t){
        if(t.equals("0")||t.equals("0:0"))
            return "00:00";
        else
            return t;
    }
    private void refRequest(){
        final ProgressDialog dialog = ProgressDialog.show(SingleTimeSetting.this, null, "加载中...", true);
        String URL = rootURL +"/Onoff/GetDev_single_time_gbo?Dev_id=" + DevNo;
        StringRequest stringRequest1 = new StringRequest(URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            if(response != null) {
                                System.out.println(response);
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject  jsonObject1 = jsonObject.getJSONObject("Dev_single_time_gbo");
                                //设置时间段的开灯时间
                                list_item.get(0).setBtOn(setTime(jsonObject1.getString("sg1_timeon1").trim())); //开灯时段1
                                list_item.get(2).setBtOn(setTime(jsonObject1.getString("sg2_timeon1").trim()));// 开灯时段3
                                list_item.get(4).setBtOn(setTime(jsonObject1.getString("sg3_timeon1").trim())); //开灯时段5
                                list_item.get(6).setBtOn(setTime(jsonObject1.getString("sg4_timeon1").trim())); //开灯时段7
                                list_item.get(1).setBtOn(setTime(jsonObject1.getString("sg1_timeon2").trim()));  //依次为 2 4 6 8
                                list_item.get(3).setBtOn(setTime(jsonObject1.getString("sg2_timeon2").trim()));
                                list_item.get(5).setBtOn(setTime(jsonObject1.getString("sg3_timeon2").trim()));
                                list_item.get(7).setBtOn(setTime(jsonObject1.getString("sg4_timeon2").trim()));
                                //设置时间段的关灯时间
                                list_item.get(0).setBtOff(setTime(jsonObject1.getString("sg1_timeoff1").trim()));
                                list_item.get(2).setBtOff(setTime(jsonObject1.getString("sg2_timeoff1").trim()));
                                list_item.get(4).setBtOff(setTime(jsonObject1.getString("sg3_timeoff1").trim()));
                                list_item.get(6).setBtOff(setTime(jsonObject1.getString("sg4_timeoff1").trim()));
                                list_item.get(1).setBtOff(setTime(jsonObject1.getString("sg1_timeoff2").trim()));
                                list_item.get(3).setBtOff(setTime(jsonObject1.getString("sg2_timeoff2").trim()));
                                list_item.get(5).setBtOff(setTime(jsonObject1.getString("sg3_timeoff2").trim()));
                                list_item.get(7).setBtOff(setTime(jsonObject1.getString("sg4_timeoff2").trim()));
                                //各个时间段的亮度
                                list_item.get(0).setBtLux(String.valueOf(jsonObject1.getInt("sg1_lux1")).trim());
                                list_item.get(2).setBtLux(String.valueOf(jsonObject1.getInt("sg2_lux1")).trim());
                                list_item.get(4).setBtLux(String.valueOf(jsonObject1.getInt("sg3_lux1")).trim());
                                list_item.get(6).setBtLux(String.valueOf(jsonObject1.getInt("sg4_lux1")).trim());
                                list_item.get(1).setBtLux(String.valueOf(jsonObject1.getInt("sg1_lux2")).trim());
                                list_item.get(3).setBtLux(String.valueOf(jsonObject1.getInt("sg2_lux2")).trim());
                                list_item.get(5).setBtLux(String.valueOf(jsonObject1.getInt("sg3_lux2")).trim());
                                list_item.get(7).setBtLux(String.valueOf(jsonObject1.getInt("sg4_lux2")).trim());
                                //时控分组
                                list_item.get(0).setSpinner1(Integer.parseInt(jsonObject1.getString("sg1").trim())); //1 3 5 7
                                list_item.get(2).setSpinner1(Integer.parseInt(jsonObject1.getString("sg2").trim()));
                                list_item.get(4).setSpinner1(Integer.parseInt(jsonObject1.getString("sg3").trim()));
                                list_item.get(6).setSpinner1(Integer.parseInt(jsonObject1.getString("sg4").trim()));
                                list_item.get(1).setSpinner1(Integer.parseInt(jsonObject1.getString("sg1b").trim())); //2 4 6 8
                                list_item.get(3).setSpinner1(Integer.parseInt(jsonObject1.getString("sg2b").trim()));
                                list_item.get(5).setSpinner1(Integer.parseInt(jsonObject1.getString("sg3b").trim()));
                                list_item.get(7).setSpinner1(Integer.parseInt(jsonObject1.getString("sg4b").trim()));
                                //时控分组1-8 对应的开关灯时间段
                                list_item.get(0).setSpinner2(jsonObject1.getInt("time_term1"));
                                list_item.get(1).setSpinner2(jsonObject1.getInt("time_term2"));
                                list_item.get(2).setSpinner2(jsonObject1.getInt("time_term3"));
                                list_item.get(3).setSpinner2(jsonObject1.getInt("time_term4"));
                                list_item.get(4).setSpinner2(jsonObject1.getInt("time_term5"));
                                list_item.get(5).setSpinner2(jsonObject1.getInt("time_term6"));
                                list_item.get(6).setSpinner2(jsonObject1.getInt("time_term7"));
                                list_item.get(7).setSpinner2(jsonObject1.getInt("time_term8"));

                                int uid = jsonObject1.getInt("Uid");
                                if(uid > 0) {
                                    if(Uid_store < uid) {
                                        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                        String date_str = sDateFormat.format(new java.util.Date());
                                        list_info.add(0, date_str.trim() + ">>刷新成功");
                                        myAdapter.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(SingleTimeSetting.this,"没有最新数据",Toast.LENGTH_LONG).show();
                                    }
                                    Uid_store = uid;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                Toast.makeText(SingleTimeSetting.this, error.getMessage() ,Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        VollyQueue.getHttpQueues().add(stringRequest1);
    }

    private void getRequest(){
        final ProgressDialog dialog = ProgressDialog.show(SingleTimeSetting.this, null, "获取中...", true);
        String URL = rootURL +"/wcf/get_single_time_group?Dev_id=" + DevNo;
        System.out.println(URL);
        StringRequest stringRequest1 = new StringRequest(URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            if(response != null){
                                System.out.println(response);
                                JSONObject jsonObject = new JSONObject(response);
                                String s = jsonObject.getString("rslt");
                                list_info.add(0, s);
                                Toast.makeText(SingleTimeSetting.this, s, Toast.LENGTH_SHORT).show();
                                myAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                Toast.makeText(SingleTimeSetting.this, error.getMessage() ,Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        VollyQueue.getHttpQueues().add(stringRequest1);
    }

    private void setRequest(){
        final ProgressDialog dialog = ProgressDialog.show(SingleTimeSetting.this, null, "设置中...", true);
        String validateURL = rootURL + "/wcf/set_single_time_group?Dev_id=" + DevNo
                + "&timeonN_arrstr[0]="  + list_item.get(0).getBtOn()
                + "&timeonN_arrstr[1]="  + list_item.get(1).getBtOn()
                + "&timeonN_arrstr[2]="  + list_item.get(2).getBtOn()
                + "&timeonN_arrstr[3]="  + list_item.get(3).getBtOn()
                + "&timeonN_arrstr[4]="  + list_item.get(4).getBtOn()
                + "&timeonN_arrstr[5]="  + list_item.get(5).getBtOn()
                + "&timeonN_arrstr[6]="  + list_item.get(6).getBtOn()
                + "&timeonN_arrstr[7]="  + list_item.get(7).getBtOn()
                + "&timeoffN_arrstr[0]=" + list_item.get(0).getBtOff()
                + "&timeoffN_arrstr[1]=" + list_item.get(1).getBtOff()
                + "&timeoffN_arrstr[2]=" + list_item.get(2).getBtOff()
                + "&timeoffN_arrstr[3]=" + list_item.get(3).getBtOff()
                + "&timeoffN_arrstr[4]=" + list_item.get(4).getBtOff()
                + "&timeoffN_arrstr[5]=" + list_item.get(5).getBtOff()
                + "&timeoffN_arrstr[6]=" + list_item.get(6).getBtOff()
                + "&timeoffN_arrstr[7]=" + list_item.get(7).getBtOff()
                + "&sgN_byte[0]="         + list_item.get(0).getNumSpinner1()
                + "&sgN_byte[1]="         + list_item.get(1).getNumSpinner1()
                + "&sgN_byte[2]="         + list_item.get(2).getNumSpinner1()
                + "&sgN_byte[3]="         + list_item.get(3).getNumSpinner1()
                + "&sgN_byte[4]="         + list_item.get(4).getNumSpinner1()
                + "&sgN_byte[5]="         + list_item.get(5).getNumSpinner1()
                + "&sgN_byte[6]="         + list_item.get(6).getNumSpinner1()
                + "&sgN_byte[7]="         + list_item.get(7).getNumSpinner1()
                + "&time_termN[0]="       + list_item.get(0).getSpinner2()
                + "&time_termN[1]="       + list_item.get(1).getSpinner2()
                + "&time_termN[2]="       + list_item.get(2).getSpinner2()
                + "&time_termN[3]="       + list_item.get(3).getSpinner2()
                + "&time_termN[4]="       + list_item.get(4).getSpinner2()
                + "&time_termN[5]="       + list_item.get(5).getSpinner2()
                + "&time_termN[6]="       + list_item.get(6).getSpinner2()
                + "&time_termN[7]="       + list_item.get(7).getSpinner2()
                + "&sgN_lux1_byte[0]="    + list_item.get(0).getBtLux()
                + "&sgN_lux1_byte[1]="    + list_item.get(1).getBtLux()
                + "&sgN_lux1_byte[2]="    + list_item.get(2).getBtLux()
                + "&sgN_lux1_byte[3]="    + list_item.get(3).getBtLux()
                + "&sgN_lux1_byte[4]="    + list_item.get(4).getBtLux()
                + "&sgN_lux1_byte[5]="    + list_item.get(5).getBtLux()
                + "&sgN_lux1_byte[6]="    + list_item.get(6).getBtLux()
                + "&sgN_lux1_byte[7]="    + list_item.get(7).getBtLux();
        System.out.println(validateURL);
        StringRequest stringRequest1 = new StringRequest(validateURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            if(response != null){
                                System.out.println(response);
                                JSONObject jsonObject = new JSONObject(response);
                                String s = jsonObject.getString("rslt");
                                list_info.add(0, s);
                                Toast.makeText(SingleTimeSetting.this, s, Toast.LENGTH_SHORT).show();
                                myAdapter.notifyDataSetChanged();
                                saveLog(s);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                Toast.makeText(SingleTimeSetting.this, error.getMessage() ,Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        VollyQueue.getHttpQueues().add(stringRequest1);
    }

    private void saveLog(String rslt_str) {
        try {
            String URL = rootURL
                    + "/wcf/savelog?logname=" + URLEncoder.encode(account, "utf-8").trim()
                    + "&logtype=" + URLEncoder.encode("(手机用户)", "utf-8").trim()
                    + "&logstr=" + URLEncoder.encode(rslt_str, "utf-8").trim();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(SingleTimeSetting.this, "网络异常", Toast.LENGTH_SHORT).show();
                }
            });
            stringRequest.setTag("");
            VollyQueue.getHttpQueues().add(stringRequest);
        } catch (Exception e) {
            Toast.makeText(SingleTimeSetting.this, "网络故障!!!", Toast.LENGTH_SHORT).show();
        }
    }

    private class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater = null;
        private MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return list_info.size();
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
            convertView = mInflater.inflate(R.layout.array_item, null);
            TextView textView ;
            textView = (TextView)convertView.findViewById(R.id.array_item_tv) ;
            textView.setText(list_info.get(position)) ;
            return convertView;
        }
    }

}

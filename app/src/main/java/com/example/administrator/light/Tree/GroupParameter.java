package com.example.administrator.light.Tree;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.administrator.light.BaseActivity;
import com.example.administrator.light.R;
import com.example.administrator.light.VollyQueue;
import com.example.administrator.light.pullToRefresh.XListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by binyejiang on 2019/7/9.
 */
public class GroupParameter extends BaseActivity implements XListView.IXListViewListener{

    private String childname;
    private SharedPreferences preferences;
    private String account = null;
    private String password = null;
    private String rootURL = null;
    private int DevNo=0;
    private boolean isNetError;
    private String onOffMode="正常模式";//当前默认运行模式
    private TextView title_vtxt;//未用
    private String selNode;//当前选定终端信息
    private int Uid_store =0;//当前记录流水号  用于判断是否有新数椐
    private List<Map<String, Object>> list_detail = new ArrayList<Map<String, Object>>();
    private ListView rsltlistView;//执行结果listview
    private XListView listView_detail;
    private Button btn_getDetailvis ,btn_refDetail;
    private ArrayAdapter<String> listspadp;
    private SimpleAdapter detail_adapter;
    private int list_nums=0;
    private List<String> rsltlist = new ArrayList<String>();//执行结果列
    private String title1_str="",title2_str="",title3_str="",title4_str="",title5_str="",title6_str="",title7_str="",title8_str="";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.electricalparameter);
        init();
        initListener();
        initLoad();
    }

    private void init(){
        preferences = getSharedPreferences("light", MODE_ENABLE_WRITE_AHEAD_LOGGING);
        rootURL = preferences.getString("rootURL", "http://183.233.174.11:11178");

        Intent intent = getIntent();
        childname = intent.getStringExtra("childname");
        getSearchView().setVisibility(View.GONE);
        getMune().setVisibility(View.GONE);
        String[] temp_strs =childname.split("-");
        if(temp_strs.length>0) {
            try {
                DevNo = Integer.parseInt(temp_strs[0].trim());
                System.out.println("截取的值是： "+DevNo);
            }catch (Exception ex){}
        }

        //设置标题栏文本
        getToolbarTitle().setText(childname);

        rsltlistView = (ListView)findViewById(R.id.rsltListView);
        listspadp = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rsltlist );
        rsltlistView.setAdapter(listspadp);
        list_nums = 0;
        btn_refDetail = (Button) findViewById(R.id.btn_refDetail);
        btn_getDetailvis = (Button) findViewById(R.id.btn_getDetailvis);
        listView_detail = (XListView)findViewById(R.id.listView_detail);
        listView_detail.setPullLoadEnable(false);
        listView_detail.setXListViewListener(this);
    }

    private void initLoad() {
        // 没有用上 所以注释掉了 CSH  2016 9 9
//        String validateURL = rootURL + "/Onoff/Gdev_addr_light?Dev_id="+ DevNo;
//        System.out.println("validateURL:" + validateURL);
//        titlerequest(validateURL);
        String  validateURL = rootURL + "/Onoff/Get_cs_table?Dev_id="+ DevNo;
        request(validateURL) ;
        init_list();
    }

    private void init_list() {
        detail_adapter = new SimpleAdapter(this,list_detail, R.layout.eleparam_list_item,
                new String[]{"title_list2","values_list2"},
                new int[]{R.id.title_list2, R.id.values_list2 });
        listView_detail.setAdapter(detail_adapter);
    }

    private void  titlerequest(String URL){
        StringRequest stringRequestTitle = new StringRequest(URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println(response);
                            if (response != null && !response.trim().equals("")) {
                                JSONObject jsobObj = new JSONObject(response);
                                //    JSONObject json_arr = jsobObj.getJSONObject("rslt") ;
                                JSONObject jsobObj2= jsobObj.getJSONObject("dev_addr_light");
                                title1_str=jsobObj2.getString("addr_jing1").trim();
                                title2_str=jsobObj2.getString("addr_jing2").trim();
                                title3_str=jsobObj2.getString("addr_jing3").trim();
                                title4_str=jsobObj2.getString("addr_jing4").trim();
                                title5_str=jsobObj2.getString("addr_jing5").trim();
                                title6_str=jsobObj2.getString("addr_jing6").trim();
                                title7_str=jsobObj2.getString("addr_jing7").trim();
                                title8_str=jsobObj2.getString("addr_jing8").trim();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        VollyQueue.getHttpQueues().add(stringRequestTitle);
    }

    private void initListener(){
        //刷新
        btn_refDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String validateURL = rootURL + "/Onoff/Get_cs_table?Dev_id="+ DevNo;
                request(validateURL) ;
            }
        });
        //获取
        btn_getDetailvis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get();
            }
        });
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String validateURL = rootURL + "/Onoff/Get_cs_table?Dev_id=" + DevNo;
            request(validateURL);
            onLoad();
        }
    };
    private void get() {
        String validateURL = rootURL + "/wcf/get_DetailElecMsg?Dev_id="+ DevNo;
        StringRequest stringRequest = new StringRequest(validateURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println(response);
                            if (response != null && !response.trim().equals("")) {
                                JSONObject jsobObj = new JSONObject(response);
                                //    JSONObject json_arr = jsobObj.getJSONObject("rslt") ;
                                String temp_str = jsobObj.getString("rslt");
                                list_nums++;
                                rsltlist.add(0, "(" + list_nums + ")" + temp_str.trim());
                                listspadp.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        VollyQueue.getHttpQueues().add(stringRequest);
        handler.postDelayed(runnable, 3000);

    }
    private void request(String URL){
        StringRequest stringRequest1 = new StringRequest(URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println(response);
                            if (response != null && !response.trim().equals("")) {
                                JSONObject jsobObj = new JSONObject(response);
                                // JSONObject json_arr = jsobObj.getJSONObject("bus_stop_detail") ;
                                String temp_str = jsobObj.getString("cs_table");
                                JSONObject jsobObj_child = new JSONObject(temp_str);
//                    {"Dev_id":1,"DevName":"","Ua":224.9,"Ub":232.6,"Uc":233.1,"Ua_out":0,"Ub_out":0,"Uc_out":0,"Ia":19.6,"Ib":19.6,"Ic":19.6,
//                   "kWa":2.1,"kWb":2.3,"kWc":4.4,"kVARa":3.6,"kVARb":3.7,"kVARc":0.1,"kVAa":4.2,"kVAb":4.4,"kVAc":4.4,"PF_a":0.503,"PF_b":0.529,
//                    "PF_c":0.999,"Kwha":1.6,"Kwhb":0,"Kwhc":0,"con1":"正常. ","con2":"正常. ","con3":"正常. ","con4":"正常. ","con5":"正常. ",
//                    "con6":"正常. ","con7":"正常. ","con8":"正常. ","door1":"关闭. ","door2":"关闭. ","saving":"停机. ","I01":19.4,"I02":19.4,"I03":19.4,
//                      "I04":19.6,"I05":19.6,"I06":19.7,"I07":19.5,"I08":19.5,"I09":19.6,"I10":0,"I11":0,"I12":0,"Pf01":0,"Pf02":0,"Pf03":0,"Pf04":0,
//                     "Pf05":0,"Pf06":0,"Pf07":0,"Pf08":0,"Pf09":0,"Pf10":0,"Pf11":0,"Pf12":0,"Kw01":5,"Kw02":5,"Kw03":5,"Kw04":5,"Kw05":5,"Kw06":5,"" +
//                    "Kw07":5,"Kw08":5,"Kw09":5,"Kw10":5,"Kw11":5,"Kw12":5,"Kvar01":1,"Kvar02":9,"Kvar03":17,"Kvar04":2,"Kvar05":10,"Kvar06":18,"Kvar07" +
//                "":3,"Kvar08":11,"Kvar09":19,"Kvar10":4,"Kvar11":12,"Kvar12":20,"No1":"正常. ","No2":"正常. ","No3":"正常. ","No4":"正常. ","No5":"正常. ",
//                            "No6":"正常. ","No7":"正常. ","No8":"正常. ","AddTime":"2016/3/19 12:00:24","cs_id":1952013711}}
                                list_detail.clear();
                                String date1_str=jsobObj_child.getString("AddTime");
                                getData_list("数椐时间", date1_str);
                                getData_list("A相电压", jsobObj_child.getString("Ua").trim());
                                getData_list("B相电压", jsobObj_child.getString("Ub").trim());
                                getData_list("C相电压", jsobObj_child.getString("Uc").trim());
                                getData_list("输出电压A", jsobObj_child.getString("Ua_out").trim());
                                getData_list("输出电压B", jsobObj_child.getString("Ub_out").trim());
                                getData_list("输出电压C", jsobObj_child.getString("Uc_out").trim());
                                getData_list("A相电流", jsobObj_child.getString("Ia").trim());
                                getData_list("B相电流", jsobObj_child.getString("Ib").trim());
                                getData_list("C相电流", jsobObj_child.getString("Ic").trim());
//                    getData_list("有功", jsobObj_child.getString("kWa").trim());
//                    getData_list("无功", jsobObj_child.getString("kVARa").trim());
//                    getData_list("视在", jsobObj_child.getString("kVAa").trim());
//                    getData_list("功率因数", jsobObj_child.getString("PF_a").trim());
                                getData_list("总有功电度", jsobObj_child.getString("Kwha").trim());
                                getData_list("总无功电度", jsobObj_child.getString("Kwhb").trim());
                                getData_list("总功率", jsobObj_child.getString("Kwhc").trim());
                                getData_list("I1", jsobObj_child.getString("I01").trim());
                                getData_list("I2", jsobObj_child.getString("I02").trim());
                                getData_list("I3", jsobObj_child.getString("I03").trim());
                                getData_list("I4", jsobObj_child.getString("I04").trim());
                                getData_list("I5", jsobObj_child.getString("I05").trim());
                                getData_list("I6", jsobObj_child.getString("I06").trim());
                                getData_list("I7", jsobObj_child.getString("I07").trim());
                                getData_list("I8", jsobObj_child.getString("I08").trim());
                                getData_list("I9", jsobObj_child.getString("I09").trim());
                                getData_list("I10", jsobObj_child.getString("I10").trim());
                                getData_list("I11", jsobObj_child.getString("I11").trim());
                                getData_list("I12", jsobObj_child.getString("I12").trim());

                                getData_list("接触器1", jsobObj_child.getString("con1").trim());
                                getData_list("接触器2", jsobObj_child.getString("con2").trim());
                                getData_list("接触器3", jsobObj_child.getString("con3").trim());
                                getData_list("接触器4", jsobObj_child.getString("con4").trim());
                                getData_list("接触器5", jsobObj_child.getString("con5").trim());
                                getData_list("接触器6", jsobObj_child.getString("con6").trim());
                                getData_list("接触器7", jsobObj_child.getString("con7").trim());
                                getData_list("接触器8", jsobObj_child.getString("con8").trim());
                                getData_list("门禁1", jsobObj_child.getString("door1").trim());
                                getData_list("门禁2", jsobObj_child.getString("door2").trim());
                                getData_list("输出空开1", jsobObj_child.getString("No1").trim());
                                getData_list("输出空开2", jsobObj_child.getString("No2").trim());
                                getData_list("输出空开3", jsobObj_child.getString("No3").trim());
                                getData_list("输出空开4", jsobObj_child.getString("No4").trim());
                                getData_list("回路/时钟", jsobObj_child.getString("No5").trim());
                                getData_list("主空开", jsobObj_child.getString("No6").trim());
                                getData_list("是否漏电", jsobObj_child.getString("No7").trim());
                                getData_list("监控开关", jsobObj_child.getString("No8").trim());
                                int Uid_int = jsobObj_child.getInt("cs_id");
                                if (Uid_int > 0) {
                                    if (Uid_store != Uid_int) {
                                        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                        String date_str = sDateFormat.format(new java.util.Date());
                                        //rsltlistView.get
                                        list_nums++;
                                        rsltlist.add(0, "(" + list_nums + ")刷新成功 " + date_str.trim());
                                    }else  if(Uid_store==Uid_int){
                                        Toast.makeText(GroupParameter.this,"没有新数据", Toast.LENGTH_LONG).show();
                                    }
                                    //  else if(Uid_store==Uid_int)  { rsltlist.add(0,"没有找到新数椐"); }
                                    Uid_store = Uid_int;
                                }else if(Uid_int < 0) {
                                    Toast.makeText(GroupParameter.this,"终端掉线", Toast.LENGTH_LONG).show();
                                }
                                listspadp.notifyDataSetChanged();
                                detail_adapter.notifyDataSetChanged();
                            }else {
                                Toast.makeText(GroupParameter.this,"返回数据为空", Toast.LENGTH_LONG).show();
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
        VollyQueue.getHttpQueues().add(stringRequest1);
    }

    private void getData_list(final String title_str,final String values_str) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title_list2", title_str.trim());//URLEncoder.encode(title_str , "utf-8").trim()
        map.put("values_list2", values_str.trim());
        list_detail.add(map);
    }

    @Override
    public void onRefresh() {
        get();

    }

    @Override
    public void onLoadMore() {

    }

    /** 停止刷新， */
    private void onLoad() {
        listView_detail.stopRefresh();
        listView_detail.stopLoadMore();
        listView_detail.setRefreshTime("刚刚");
    }
}

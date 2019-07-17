package com.example.administrator.light;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.administrator.light.conctrol.conctrolMenu;
import com.example.administrator.light.map.MapActivity;
import com.example.administrator.light.other.AssetRatio;
import com.example.administrator.light.other.CentralizedFaultQuery;
import com.example.administrator.light.other.Daily;
import com.example.administrator.light.other.Energy_query;
import com.example.administrator.light.other.SingleFaultQuery;
import com.example.administrator.light.other.SingleParaHistory;
import com.example.administrator.light.other.Three_phase;
import com.example.administrator.light.system.CentralizingSwitch;
import com.example.administrator.light.system.CentralizingSwitch_Group;
import com.example.administrator.light.system.ElectricalParameter;
import com.example.administrator.light.system.Single_Dimming_TimeCtrl.SingleActivity;
import com.example.administrator.light.system.SingleTimeSetting;
import com.example.administrator.light.system.TimeControl;
import com.example.administrator.light.system.TimeControl_Group;
import com.example.administrator.light.util.SharedPreferencesUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import static android.graphics.Color.*;
import static com.example.administrator.light.R.color.greebblue;
import static java.lang.Integer.parseInt;

//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.RequestBody;

/**
 * Created by binyej 2019/6/04.
 */
public class ExpandablelistViewput extends BaseActivity {
    Spinner expand_spinner;
    List<String> grouplist = new ArrayList<String>();
    List<String> grouplist1 = new ArrayList<String>();
    List<Integer> idlist = new ArrayList<Integer>();

    List<List<String>> childLeftList = new ArrayList<List<String>>();
    List<List<String>> currentNodeTypelist = new ArrayList<List<String>>();
    List<List<String>> androidUrllist = new ArrayList<List<String>>();
    List<List<List<String>>> simcurrentNodeTypelist = new ArrayList<List<List<String>>>();
    List<List<List<String>>> simandroidUrllist = new ArrayList<List<List<String>>>();
    List<List<Integer>> childRightList = new ArrayList<>();
    List<List<String>> childcoordinate = new ArrayList<List<String>>();
    String account = null;
    String password = null;
    String From = null;
    String token=null;
    ExpandableListView listView;
    RelativeLayout group_relative;
    private String rootURL = null;
    String groupname=null;
    String outtagIds=null;
    String sintagIds=null;
    int resid,outtagId,sintagId;
    String HttpPath="http://192.168.0.157:45002/api/resTag";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tree_shuchusin);
        init();
        listView();
    }

    public void init() {
        rootURL = (String) SharedPreferencesUtils.getParam(this, "rootURL", "");
        account = (String) SharedPreferencesUtils.getParam(this, "username", "");
        password = (String) SharedPreferencesUtils.getParam(this, "password", "");
        outtagId = (int) SharedPreferencesUtils.getParam(this, "outtagId", -1);
        outtagIds = (String) SharedPreferencesUtils.getParam(this, "outtagIds", "");
        sintagId = (int) SharedPreferencesUtils.getParam(this, "sintagId", -1);
        sintagIds = (String) SharedPreferencesUtils.getParam(this, "sintagIds", "");
        token= (String) SharedPreferencesUtils.getParam(this, "token", "");
        Intent intent = getIntent();
        From = intent.getStringExtra("from");
        listView = (ExpandableListView) findViewById(R.id.list);

        //设置标题栏
     //   setTitle("智能路灯监控");
        getSearchView().setVisibility(View.GONE);
        groupname = intent.getStringExtra("groupname");
        getToolbarTitle().setText(groupname);
        resid = intent.getIntExtra("resid",-1);
    }
    public void listView() {
        String mode="default";
        String[] a={"-1"};
        String[] b={"-1"};
        String[] c={"-1"};
        int al=-1,bl=-1,cl=-1;
        if(outtagId>0){
            mode="custom";
            bl=outtagId;
            b=outtagIds.split("-");
        }
        int inb[] = new int[b.length];
// 对String数组进行遍历循环，并转换成int类型的数组
        for (int i = 0; i < b.length; i++) {
            inb[i] = Integer.parseInt(b[i]);
        }
        JSONArray jsonArray2= new JSONArray();
        for(int i = 0 ; i < inb.length ;i++) {  //依次将数组元素添加进JSONArray对象中
            jsonArray2.put(inb[i]);
        }
        JSONObject jsonStr=new JSONObject();
        JSONObject par=new JSONObject();
        try {
            jsonStr.put("jsonrpc","2.0");
            jsonStr.put("method","loadTreeNode");
            par.put("token",token);
            par.put("mode",mode);
            par.put("currentDeviceType",0);
            par.put("currentNodeType","terminalName");
            par.put("resId",17);
            par.put("terminalTagGroupId",-1);
            par.put("terminalTagId",-1);
            par.put("terminalTagIds",-1);
            par.put("outputTagGroupId",bl);
            par.put("outputTagId",-1);
            par.put("outputTagIds",jsonArray2);
            par.put("singleLampTagGroupId",-1);
            par.put("singleLampTagId",-1);
            par.put("singleLampTagIds",-1);

            jsonStr.put("params",par);
            jsonStr.put("id",111);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST,HttpPath
                ,jsonStr,new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(ExpandablelistViewput.this, "设置成功", Toast.LENGTH_SHORT).show();
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String ss=error.toString();
                String str=null;
                int a=ss.length();
                if(a>31) {
                    str = ss.substring(ss.indexOf("["),ss.lastIndexOf("]") + 1);
                    JSONArray jsonObject = null;
                    try {
                        jsonObject = new JSONArray(str);
                        if(str.length()>50) {
                                     JSONObject obj1=new JSONObject(jsonObject.getString(0));
                                     JSONArray obj2= obj1.getJSONArray("result");
                                    grouplist.clear();
                                    childLeftList.clear();
                                    for(int i=0;i<obj2.length();i++){
                                        List<String> list=new ArrayList<String>();
                                        List<String> left = new ArrayList<String>();
                                        List<String> list1=new ArrayList<String>();
                                        List<List<String>> currentNodeTypelist1 = new ArrayList<List<String>>();
                                        List<List<String>> androidUrllist1 = new ArrayList<List<String>>();
                                        JSONObject  obj3=new JSONObject(obj2.getString(i));
                                        String resname=obj3.getString("resName");
                                        grouplist.add(resname);
                                        int resid=obj3.getInt("resId");
                                        getResid(resid,list,currentNodeTypelist1,androidUrllist1);
                                        childLeftList.add(list);
                                        simcurrentNodeTypelist.add(currentNodeTypelist1);
                                        simandroidUrllist.add(androidUrllist1);
                                        String nodeType=obj3.getString("currentNodeType");
                                        idlist.add(resid);
                                        getNodeType(nodeType,left,list1);
                                        currentNodeTypelist.add(left);
                                        androidUrllist.add(list1);
                            };
                            Adapter();   }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(ExpandablelistViewput.this, "网络连接错误", Toast.LENGTH_SHORT).show();
                }
            }
        }){
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {

                try {
                    String jsonString = new String(response.data, "UTF-8");
                    return Response.success(new JSONObject(jsonString),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }
        };
        jsonObjectRequest.setTag("jsonPost");
        VollyQueue.getHttpQueues().add(jsonObjectRequest);
    }
    /*
    * expandablelistview的适配函数
    * */
    public void Adapter() {
        ExpandableListAdapter adapter = new BaseExpandableListAdapter() {
            @Override
            public int getGroupCount() {
                return grouplist.size();
            }

            @Override
            public int getChildrenCount(int groupPosition) {
                return childLeftList.get(groupPosition).size();
            }

            @Override
            public Object getGroup(int groupPosition) {
                return grouplist.get(groupPosition);
            }

            @Override
            public Object getChild(int groupPosition, int childPosition) {
                System.out.println(groupPosition);
                System.out.println(childLeftList.get(groupPosition).get(childPosition));
                return childLeftList.get(groupPosition).get(childPosition);
            }

//            public Object getChildright(int groupPosition, int childPosition) {
//                return childRightList.get(groupPosition).get(childPosition);
//            }

            @Override
            public long getGroupId(int groupPosition) {
                return groupPosition;
            }

            @Override
            public long getChildId(int groupPosition, int childPosition) {
                return childPosition;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            @Override
            public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                View ll = inflater.inflate(R.layout.grouplayout,null);
                LinearLayout group = (LinearLayout)ll.findViewById(R.id.group);
                final TextView group_text = (TextView)ll.findViewById(R.id.group_text);
                TextView group_text1 = (TextView)ll.findViewById(R.id.group_text1);
                ImageView group_img = (ImageView)ll.findViewById(R.id.group_img);
                group_relative = (RelativeLayout)ll.findViewById(R.id.group_relative);
            //    group_img.setImageResource(R.drawable.xlistview_arrow);
                group_text.setText(getGroup(groupPosition).toString());
                group_text1.setVisibility(View.GONE);
//                if(isExpanded) {
//                    group_img.setImageResource(R.drawable.xlistview_arrow1);
//                }
                group_relative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //  Toast.makeText(ExpandablelistView.this,"niam",Toast.LENGTH_SHORT).show();
                        String groupname = grouplist.get(groupPosition);
                        //expand_textview.setText(groupname);
                      //  System.out.println((ArrayList<String>) childLeftList.get(groupPosition));
                        //根据不同功能需求，让点击子列表后转向不同的activity
                        List tNodeType = currentNodeTypelist.get(groupPosition);
                        final String[] array= (String[]) tNodeType.toArray(new String[tNodeType.size()]);
                        android.support.v7.app.AlertDialog.Builder builder1=new android.support.v7.app.AlertDialog.Builder(ExpandablelistViewput.this);
                        builder1.setTitle("选择你要的操作");
                        builder1.setItems(array, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String groupname = grouplist.get(groupPosition);
                                int resid=idlist.get(groupPosition);
                                String  nameclass =androidUrllist.get(groupPosition).get(which);
                                Toast.makeText(ExpandablelistViewput.this,nameclass, Toast.LENGTH_SHORT).show();
                                Class clazz= null;
                                try {
                                    clazz = Class.forName(nameclass);
                                    Intent intent=new Intent(ExpandablelistViewput.this,clazz);
                                    intent.putExtra("groupname", groupname);
                                    intent.putExtra("resid", resid);
                                    startActivity(intent);
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        builder1.show();
                    }
                });
                return ll;
            }

            @Override
            public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                View ll1 = inflater.inflate(R.layout.childlayout,null);
                TextView left = (TextView)ll1.findViewById(R.id.left);
                TextView right = (TextView)ll1.findViewById(R.id.right);
                ImageView img = (ImageView)ll1.findViewById(R.id.img);
                left.setText(getChild(groupPosition, childPosition).toString());
                    right.setVisibility(View.GONE);
                return ll1;
            }

            @Override
            public boolean isChildSelectable(int i, int i1) {
                return true;
            }
        };
        ExpandableListView expandablelistView = (ExpandableListView)findViewById(R.id.list);
      //  adapter.notifyDataSetChanged();

        expandablelistView.setAdapter(adapter);
        //子列表点击事件
        expandablelistView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View convertView, final int groupPosition, final int childPosition, long l) {
                final String childname = childLeftList.get(groupPosition).get(childPosition).trim();
                //根据不同功能需求，让点击子列表后转向不同的activity
                if(true){
                    List tNodeType = simcurrentNodeTypelist.get(groupPosition).get(childPosition);
                    final String[] array= (String[]) tNodeType.toArray(new String[tNodeType.size()]);
                    android.support.v7.app.AlertDialog.Builder builder1=new android.support.v7.app.AlertDialog.Builder(ExpandablelistViewput.this);
                    builder1.setTitle("选择你要的操作");
                    builder1.setItems(array, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String  nameclass =simandroidUrllist.get(groupPosition).get(childPosition).get(which);
                            Toast.makeText(ExpandablelistViewput.this,nameclass, Toast.LENGTH_SHORT).show();
                            Class clazz= null;
                            try {
                                clazz = Class.forName(nameclass);
                                Intent intent=new Intent(ExpandablelistViewput.this,clazz);
                                intent.putExtra("groupname", childname);
                                startActivity(intent);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    builder1.show();
                }
//                switch(From) {
//                    case "map":
//                        String childxy = childcoordinate.get(groupPosition).get(childPosition).trim();
//                        Intent intent1 = new Intent();
//                        intent1.putExtra("childxy", childxy);
//                        intent1.putExtra("childname", childname);
//                        intent1.setClass(ExpandablelistViewput.this, MapActivity.class);
//                        startActivity(intent1);
//                        break;
//                    case "parameter":
//                        Intent intent_param = new Intent();
//                        intent_param.putExtra("childname", childname);
//                        intent_param.setClass(ExpandablelistViewput.this, ElectricalParameter.class);
//                        startActivity(intent_param);
//                        break;
//                    case "timecontrol":
//                        Intent intent_timecontrol = new Intent();
//                        intent_timecontrol.putExtra("childname",childname);
//                        intent_timecontrol.setClass(ExpandablelistViewput.this, TimeControl.class);
//                        startActivity(intent_timecontrol);
//                        break;
//                    case "switch":
//                        Intent intent = new Intent();
//                        intent.putExtra("childname", childname);
//                        intent.setClass(ExpandablelistViewput.this, CentralizingSwitch.class);
//                        startActivity(intent);
//                        break;
//                    case "sldimming":
//                        Intent intent_sldimming = new Intent();
//                        intent_sldimming.putExtra("childname", childname);
//                        intent_sldimming.putExtra("myTAG", "Dimming");
//                        intent_sldimming.setClass(ExpandablelistViewput.this, SingleActivity.class);
//                        startActivity(intent_sldimming);
//                        break;
//                    case "sltimecontrol":
//                        Intent intent_sltimecontrol = new Intent();
//                        intent_sltimecontrol.putExtra("childname", childname);
//                        intent_sltimecontrol.putExtra("myTAG", "TimeControl");
//                        intent_sltimecontrol.setClass(ExpandablelistViewput.this, SingleActivity.class);
//                        startActivity(intent_sltimecontrol);
//                        break;
//                    case "sltcsetting":
//                        Intent intent_sltcsetting = new Intent();
//                        intent_sltcsetting.putExtra("childname", childname);
//                        intent_sltcsetting.setClass(ExpandablelistViewput.this, SingleTimeSetting.class);
//                        startActivity(intent_sltcsetting);
//                        break;
//                    case "energy_query":
//                        Intent intent_query = new Intent();
//                        intent_query.putExtra("childname", childname);
//                        intent_query.setClass(ExpandablelistViewput.this, Energy_query.class);
//                        startActivity(intent_query);
//                        break;
//                    case "daily":
//                        Intent intent_daily = new Intent();
//                        intent_daily.putExtra("childname", childname);
//                        intent_daily.setClass(ExpandablelistViewput.this, Daily.class);
//                        startActivity(intent_daily);
//                        break;
//                    case "three_phase":
//                        Intent intent_parameters = new Intent();
//                        intent_parameters.putExtra("childname", childname);
//                        intent_parameters.setClass(ExpandablelistViewput.this, Three_phase.class);
//                        startActivity(intent_parameters);
//                        break;
//                    case "asset_ratio":
//                        Intent intent_asset_ratio = new Intent();
//                        intent_asset_ratio.putExtra("childname", childname);
//                        intent_asset_ratio.setClass(ExpandablelistViewput.this, AssetRatio.class);
//                        startActivity(intent_asset_ratio);
//                        break;
//                    case "single_parameter_history":
//                        Intent intent_single_parameter_history = new Intent();
//                        intent_single_parameter_history.putExtra("childname", childname);
//                        intent_single_parameter_history.setClass(ExpandablelistViewput.this, SingleParaHistory.class);
//                        startActivity(intent_single_parameter_history);
//                        break;
//                    case "centralized_fault_query":
//                        Intent intent_centralized_fault_query = new Intent();
//                        intent_centralized_fault_query.putExtra("childname", childname);
//                        intent_centralized_fault_query.setClass(ExpandablelistViewput.this, CentralizedFaultQuery.class);
//                        startActivity(intent_centralized_fault_query);
//                        break;
//                    case "single_fault_query":
//                        Intent intent_single_fault_query = new Intent();
//                        intent_single_fault_query.putExtra("childname", childname);
//                        intent_single_fault_query.setClass(ExpandablelistViewput.this, SingleFaultQuery.class);
//                        startActivity(intent_single_fault_query);
//                        break;
//                }
                return false;
            }
        });
    }

    public void getResid(int id, final List<String> list,final List<List<String>> currentNodeTypelist1,
                         final List<List<String>> androidUrllist1) {
        String mode="default";
        String[] a={"-1"};
        String[] b={"-1"};
        String[] c={"-1"};
        int al=-1,bl=-1,cl=-1;
        if(sintagId>0){
            mode="custom";
            cl=sintagId;
            c=sintagIds.split("-");
        }
        int inc[] = new int[c.length];
// 对String数组进行遍历循环，并转换成int类型的数组
        for (int i = 0; i < c.length; i++) {
            inc[i] = Integer.parseInt(c[i]);
        }
        JSONArray jsonArray3 = new JSONArray();
        for(int i = 0 ; i < inc.length ;i++) {  //依次将数组元素添加进JSONArray对象中
            jsonArray3.put(inc[i]);
        }
        JSONObject jsonStr1=new JSONObject();
        JSONObject par1=new JSONObject();
        try {
            jsonStr1.put("jsonrpc","2.0");
            jsonStr1.put("method","loadTreeNode");
            par1.put("token",token);
            par1.put("mode",mode);
            par1.put("currentDeviceType",1);
            par1.put("currentNodeType","outPutName");
            par1.put("resId",23);
            par1.put("terminalTagGroupId",-1);
            par1.put("terminalTagId",-1);
            par1.put("terminalTagIds",-1);
            par1.put("outputTagGroupId",-1);
            par1.put("outputTagId",-1);
            par1.put("outputTagIds",-1);
            par1.put("singleLampTagGroupId",cl);
            par1.put("singleLampTagId",-1);
            par1.put("singleLampTagIds",jsonArray3);

            jsonStr1.put("params",par1);
            jsonStr1.put("id",111);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST,HttpPath
                ,jsonStr1,new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(ExpandablelistViewput.this, "设置成功", Toast.LENGTH_SHORT).show();
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String ss=error.toString();
                String str=null;
                int a=ss.length();
                if(a>31) {
                    str = ss.substring(ss.indexOf("["),ss.lastIndexOf("]") + 1);
                    JSONArray jsonObject = null;
                    try {
                        jsonObject = new JSONArray(str);
                        if(str.length()>50) {
                             JSONObject obj1=new JSONObject(jsonObject.getString(0));
                             JSONArray obj2= obj1.getJSONArray("result");
                            for(int i=0;i<obj2.length();i++){
                                List<String> list2=new ArrayList<String>();
                                List<String> left2 = new ArrayList<String>();
                                JSONObject  obj3=new JSONObject(obj2.getString(i));
                                String nodeType=obj3.getString("currentNodeType");
                                getNodeType(nodeType,left2,list2);
                                currentNodeTypelist1.add(left2);
                                androidUrllist1.add(list2);
                                String resname=obj3.getString("resName");
                                list.add(resname);
                                int resid=obj3.getInt("resId");
                            };
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(ExpandablelistViewput.this, "网络连接错误", Toast.LENGTH_SHORT).show();
                }
            }
        }){
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {

                try {
                    String jsonString = new String(response.data, "UTF-8");
                    return Response.success(new JSONObject(jsonString),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }
        };
        jsonObjectRequest.setTag("jsonPost");
        VollyQueue.getHttpQueues().add(jsonObjectRequest);
    }
    private void getNodeType(String tNodeType, final List<String> left, final List<String> list1) {
        JSONObject jsonStr=new JSONObject();
        JSONObject par=new JSONObject();
        try {
            jsonStr.put("jsonrpc","2.0");
            jsonStr.put("method","loadMenu");
            par.put("token",token);
            par.put("parentResId",0);
            par.put("resType",1);
            par.put("funModType",0);
            par.put("menuClass",tNodeType);
            jsonStr.put("params",par);
            jsonStr.put("id",111);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，
        RequestBody body = RequestBody.create(JSON, jsonStr.toString());
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(HttpPath)
                .post(body)
                .build();
        client.newCall(request).enqueue(
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Looper.prepare();
                        Toast.makeText(ExpandablelistViewput.this, "网络连接错误", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                    @Override
                    public void onResponse(Call call, okhttp3.Response response) throws IOException {
                        if(response.isSuccessful()){//回调的方法执行在子线程。
                            //     Log.d("kwwl","response.body().string()=="+response.body().string());
                            String str=response.body().string();
                            JSONArray obj = null;
                            try {
                                obj = new JSONArray(str);
                                int aa= str.length();
                                if(str.length()>50) {
                                    JSONObject obj1=new JSONObject(obj.getString(0));
                                    JSONArray obj2= obj1.getJSONArray("result");
                                    //  menu.clear();
                                    for(int i=0;i<obj2.length();i++){
                                        JSONObject  obj3=new JSONObject(obj2.getString(i));
                                        String resname=obj3.getString("resName");
                                        left.add(resname);
                                        String urlclass=obj3.getString("resId");
                                        list1.add(urlclass);
                                    };
                                }else {
                                    //rolename.setText("");
                                }
                            } catch (JSONException e) {
                                Looper.prepare();
                                Toast.makeText(ExpandablelistViewput.this,"网络连接错误", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                                e.printStackTrace();
                            }
                        }
                    }
                });//此处省略回调方法。
    }
}

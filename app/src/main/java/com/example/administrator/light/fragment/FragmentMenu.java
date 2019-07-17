package com.example.administrator.light.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.administrator.light.ExpandablelistView;
import com.example.administrator.light.LoginActivity;
import com.example.administrator.light.MainActivity;
import com.example.administrator.light.R;
import com.example.administrator.light.Tree.NewGroup;
import com.example.administrator.light.map.MapActivity;
import com.example.administrator.light.other.AssetRatio;
import com.example.administrator.light.other.CentralizedFaultQuery;
import com.example.administrator.light.other.Daily;
import com.example.administrator.light.other.Energy_query;
import com.example.administrator.light.other.SingleFaultQuery;
import com.example.administrator.light.other.SingleParaHistory;
import com.example.administrator.light.other.Three_phase;
import com.example.administrator.light.other.collect.Collection;
import com.example.administrator.light.system.CentralizingSwitch;
import com.example.administrator.light.system.ConcentratorAlarm.ConcentratorAlarm;
import com.example.administrator.light.system.ElectricalParameter;
import com.example.administrator.light.system.SingleAlarm.SingleTerminal;
import com.example.administrator.light.system.SingleTimeSetting;
import com.example.administrator.light.system.Single_Dimming_TimeCtrl.SingleActivity;
import com.example.administrator.light.system.TimeControl;
import com.example.administrator.light.util.SharedPreferencesUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.LogRecord;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by binyej 2019/6/12.
 */
public class FragmentMenu extends Fragment {
    ListView parentMenuList, childMenuList;
    ArrayAdapter<String> parentAdapter, childAdapter;
    String token=null;
    List<String> menu=new ArrayList<>();
    List<String> menuna=new ArrayList<>();
    List<String> menunaclass=new ArrayList<>();
    String HttpPath="http://192.168.0.157:45002/api/resTag";
    String[] mmm={};
    String[] mmmm={"监控","报警","发现","地图","我的"};
    int P = 0;//记录是在系统功能还是其他功能的子列表下
    String childname,menuname,allmenuname,classname;
    boolean dss=false;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what==1){
                teeMunu();
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_menu, container, false);
        initView(view);
        seeMenu();
        return view;
    }

    private void teeMunu() {
        childMenuList.setVisibility(View.GONE);
        menu.clear();
        menuname = (String) SharedPreferencesUtils.getParam(getActivity(), "menulist", "");
        mmm= menuname.split(",");
        parentAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, mmm);
        parentAdapter.notifyDataSetChanged();
        parentMenuList.setAdapter(parentAdapter);
        parentMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String strItem = (String) parentMenuList.getItemAtPosition(i);

                allmenuname = (String) SharedPreferencesUtils.getParam(getActivity(), "menulistall", "");
                classname = (String) SharedPreferencesUtils.getParam(getActivity(), "funModClass", "");
                menuna.clear();
                menunaclass.clear();
                try {
                    JSONArray jsonObject = new JSONArray(allmenuname);
                    JSONArray jsobjclass = new JSONArray(classname);
                    for(int j=0;j<jsonObject.length();j++){
                        if(i==j){
                            JSONArray jsonObject1 = new JSONArray(jsonObject.getString(j));
                            JSONArray jjsobjclass1= new JSONArray(jsobjclass.getString(j));
                            for(int k=0;k<jsonObject1.length();k++){
                                menuna.add(jsonObject1.getString(k));
                                menunaclass.add(jjsobjclass1.getString(k));
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                P=i;
                childMenuList.setVisibility(View.VISIBLE);
                childAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, menuna);
                childAdapter.notifyDataSetChanged();
                childMenuList.setAdapter(childAdapter);
            }
        });
        childMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String strChildItem = (String) childMenuList.getItemAtPosition(i);
                String classname=menunaclass.get(i).toString();
                String activityName="com.example.administrator.light.MainActivity";
                Intent intent = new Intent();
                intent.putExtra("classname", classname);
                intent.setClass(getActivity(), ActivityTree.class);
                startActivity(intent);
            }
        });
    }

    public void initView(View view) {
        childname = getArguments().getString("childname");
        parentMenuList = (ListView)view.findViewById(R.id.parentMenuList);
        childMenuList = (ListView)view.findViewById(R.id.childMenuList);
        token=(String) SharedPreferencesUtils.getParam(getContext(), "token", "");
    }
    public void seeMenu() {
        JSONObject jsonStr=new JSONObject();
        JSONObject par=new JSONObject();
        try {
            jsonStr.put("jsonrpc","2.0");
            jsonStr.put("method","loadMenu");
            par.put("token",token);
            par.put("parentResId",0);
            par.put("resType",1);
            par.put("funModType",0);
            par.put("menuClass","home");
            jsonStr.put("params",par);
            jsonStr.put("id",111);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，
        RequestBody body = RequestBody.create(JSON, jsonStr.toString());
        Request request = new Request.Builder()
                .url(HttpPath)
                .post(body)
                .build();
        client.newCall(request).enqueue(
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Looper.prepare();
                        Toast.makeText(getActivity(), "网络连接错误", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(response.isSuccessful()){//回调的方法执行在子线程。
                            Log.d("kwwl","获取数据成功了");
                            Log.d("kwwl","response.code()=="+response.code());
                            //     Log.d("kwwl","response.body().string()=="+response.body().string());
                            String str=response.body().string();
                            JSONArray obj = null;
                            try {
                                obj = new JSONArray(str);
                                int aa= str.length();
                                if(str.length()>50) {
                                    JSONObject obj1=new JSONObject(obj.getString(0));
                                    JSONArray obj2= obj1.getJSONArray("result");
                                    String treNamelist="";
                                    String allname="";
                                    List<List<String> > alllist = new ArrayList<List<String> >();
                                    List<List<String> > classlist = new ArrayList<List<String> >();
                                    for(int i=0;i<obj2.length();i++){
                                        List<String> left = new ArrayList<String>();
                                        List<String> cllist = new ArrayList<String>();
                                        JSONObject  obj3=new JSONObject(obj2.getString(i));
                                        String resname=obj3.getString("resName");
                                        treNamelist+=resname+",";
                                        int resId=obj3.getInt("resId");
                                        List<String> list = new ArrayList<String>();
                                        getResid(resId,list,cllist);
                                        alllist.add(list);
                                        classlist.add(cllist);
                                    };
                                    SharedPreferencesUtils.setParam(getActivity(), "menulistall",alllist.toString());
                                    SharedPreferencesUtils.setParam(getActivity(), "funModClass",classlist.toString());
                                    SharedPreferencesUtils.setParam(getActivity(), "menulist",treNamelist);
                                }else {
                                    SharedPreferencesUtils.setParam(getActivity(), "menulist","");
                                }
                                        Message msg = new Message();
                                        msg.what = 1;
                                        handler.sendMessage(msg);
                            } catch (JSONException e) {
                                Looper.prepare();
                                Toast.makeText(getActivity(), "网络连接错误", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                                e.printStackTrace();
                            }
                        }
                    }
                });//此处省略回调方法。
    }
    public void toNextActivity(String fromStr, Class fromClass) {
        Intent intent = new Intent();
        if(childname.equals("")) {
            intent.putExtra("from", fromStr);
            intent.setClass(getActivity(), ExpandablelistView.class);
        } else {
            intent.putExtra("childname", childname);
            intent.setClass(getActivity(), fromClass);
        }
        startActivity(intent);
    }
    public void getResid(int id, final List<String> list,final List<String> cllist) {
        JSONObject jsonStr=new JSONObject();
        JSONObject par=new JSONObject();
        try {
            jsonStr.put("jsonrpc","2.0");
            jsonStr.put("method","loadMenu");
            par.put("token",token);
            par.put("parentResId",id);
            par.put("resType",1);
            par.put("funModType",0);
            par.put("menuClass","home");
            jsonStr.put("params",par);
            jsonStr.put("id",111);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，
        RequestBody body = RequestBody.create(JSON, jsonStr.toString());
        Request request = new Request.Builder()
                .url(HttpPath)
                .post(body)
                .build();
        client.newCall(request).enqueue(
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Looper.prepare();
                        Toast.makeText(getActivity(), "网络连接错误", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(response.isSuccessful()){//回调的方法执行在子线程。
                            Log.d("kwwl","获取数据成功了");
                            Log.d("kwwl","response.code()=="+response.code());
                            //     Log.d("kwwl","response.body().string()=="+response.body().string());
                            String str=response.body().string();
                            JSONArray obj = null;
                            try {
                                obj = new JSONArray(str);
                                int aa= str.length();
                                if(str.length()>50) {
                                    JSONObject obj1=new JSONObject(obj.getString(0));
                                    JSONArray obj2= obj1.getJSONArray("result");
                                    list.clear();
                                    cllist.clear();
                                        for(int i=0;i<obj2.length();i++){
                                            List<String> left = new ArrayList<String>();
                                            JSONObject  obj3=new JSONObject(obj2.getString(i));
                                            String resname=obj3.getString("resName");
                                            list.add(resname);
                                            if(obj3.has("androidUrl")){
                                                String funModClass=obj3.getString("androidUrl");
                                                cllist.add(funModClass);
                                            }else {
                                                cllist.add("");
                                            }
                                        };
                                }
                            } catch (JSONException e) {
                                Looper.prepare();
                                Toast.makeText(getActivity(), "网络连接错误", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                                e.printStackTrace();
                            }
                        }
                    }
                });//此处省略回调方法。
    }
}

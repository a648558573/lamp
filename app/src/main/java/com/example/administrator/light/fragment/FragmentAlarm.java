package com.example.administrator.light.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.administrator.light.BaseActivity;
import com.example.administrator.light.R;
import com.example.administrator.light.util.SharedPreferencesUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by binyejiang on 2019/6/18.
 */

public class FragmentAlarm extends Fragment {
    ArrayAdapter<String> parentAdapter, childAdapter;
    List<String> menu=new ArrayList<>();
    List<String> menuna=new ArrayList<>();
    private ListView listView;
    String token,menuClass,resid,groupname;
    String HttpPath="http://192.168.0.157:45002/api/resTag";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.conctrol_menu, container, false);
        init(view);
        listMenu();
        parentAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, menu);
        parentAdapter.notifyDataSetChanged();
        listView.setAdapter(parentAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String strItem = (String) listView.getItemAtPosition(i);
                String activityName=menuna.get(i);
              // String activityName="com.example.administrator.light.Tree.testExpandableList";
                Class clazz= null;
                try {
                    clazz = Class.forName(activityName);
                    Intent intent=new Intent(getActivity(),clazz);
                    startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),"sha",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
    public void init(View view){
        listView = (ListView)view.findViewById(R.id.listmune);
        token= (String) SharedPreferencesUtils.getParam(getContext(), "token", "");
//        Intent intent = getIntent();
//        menuClass = intent.getStringExtra("menuClass");
//        getToolbarTitle().setText("设备功能");
//        getSearchView().setVisibility(View.GONE);
    }
    public void listMenu(){
        JSONObject jsonStr=new JSONObject();
        JSONObject par=new JSONObject();
        try {
            jsonStr.put("jsonrpc","2.0");
            jsonStr.put("method","loadMenu");
            par.put("token",token);
            par.put("parentResId",0);
            par.put("resType",1);
            par.put("funModType",0);
            par.put("menuClass","alarm");

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
                            //     Log.d("kwwl","response.body().string()=="+response.body().string());
                            String str=response.body().string();
                            JSONArray obj = null;
                            try {
                                obj = new JSONArray(str);
                                int aa= str.length();
                                if(str.length()>50) {
                                    JSONObject obj1=new JSONObject(obj.getString(0));
                                    JSONArray obj2= obj1.getJSONArray("result");
                                    menu.clear();
                                    menuna.clear();
                                    for(int i=0;i<obj2.length();i++){
                                        List<String> left = new ArrayList<String>();
                                        JSONObject  obj3=new JSONObject(obj2.getString(i));
                                        String resname=obj3.getString("resName");
                                        menu.add(resname);
                                        if(obj3.has("androidUrl")){
                                            String funModClass=obj3.getString("androidUrl");
                                            menuna.add(funModClass);
                                        }else {

                                            menuna.add("");
                                        }
                                    };
                                }else {
                                    //rolename.setText("");
                                }
                            } catch (Exception e) {
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

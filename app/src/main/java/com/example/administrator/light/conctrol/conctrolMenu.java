package com.example.administrator.light.conctrol;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
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

public class conctrolMenu extends BaseActivity {
    ListView parentMenuList, childMenuList;
    ArrayAdapter<String> parentAdapter, childAdapter;
    List<String> menu=new ArrayList<>();
    List<String> menuna=new ArrayList<>();
    private ListView listView;
    String token,menuClass,menuClass1,resid,groupname;
    String HttpPath="http://192.168.0.157:45002/api/resTag";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conctrol_menu);
        init();
        menu.clear();
        listMenu(menuClass);
        listMenu(menuClass1);
        parentAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menu);
        parentAdapter.notifyDataSetChanged();
        listView.setAdapter(parentAdapter);
    }
    public void init(){
        listView = (ListView) findViewById(R.id.listmune);
        token= (String) SharedPreferencesUtils.getParam(this, "token", "");
        Intent intent = getIntent();
        menuClass = intent.getStringExtra("menuClass");
        menuClass1 = intent.getStringExtra("menuClass1");
        getToolbarTitle().setText("设备功能");
        getSearchView().setVisibility(View.GONE);
    }
    public void listMenu(String menuClass){
        JSONObject jsonStr=new JSONObject();
        JSONObject par=new JSONObject();
        try {
            jsonStr.put("jsonrpc","2.0");
            jsonStr.put("method","loadMenu");
            par.put("token",token);
            par.put("parentResId",0);
            par.put("resType",1);
            par.put("funModType",0);
            par.put("menuClass",menuClass);
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
                        Toast.makeText(conctrolMenu.this, "网络连接错误", Toast.LENGTH_SHORT).show();
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
                                  //  menu.clear();
                                    for(int i=0;i<obj2.length();i++){
                                        List<String> left = new ArrayList<String>();
                                        JSONObject  obj3=new JSONObject(obj2.getString(i));
                                        String resname=obj3.getString("resName");
                                        menu.add(resname);
                                        int resid=obj3.getInt("resId");
                                    };
                                }else {
                                    //rolename.setText("");
                                }
                            } catch (JSONException e) {
                                Looper.prepare();
                                Toast.makeText(conctrolMenu.this, "网络连接错误", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                                e.printStackTrace();
                            }
                        }
                    }
                });//此处省略回调方法。
    }
}

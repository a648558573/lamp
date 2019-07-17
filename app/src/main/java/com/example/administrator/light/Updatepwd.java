package com.example.administrator.light;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.administrator.light.util.SharedPreferencesUtils;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2019/5/30.
 */

public class Updatepwd extends AppCompatActivity {
    private EditText ip_port , ip_port1,ip_port11;
    private Button toSet,toolbarLeftBt;
    private String user,phnum,email,token;
    String HttpPath="http://192.168.0.157:45002/api/userAuth";
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pwd);
        init();
        token=(String) SharedPreferencesUtils.getParam(this, "token", "");
        toSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user=ip_port.getText().toString();
                phnum=ip_port1.getText().toString();
                email=ip_port11.getText().toString();
                if(user.length()==0){
                    Toast.makeText(Updatepwd.this,"请输入账号",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(phnum.length()==0){
                    Toast.makeText(Updatepwd.this,"请输入手机号",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(email.length()==0){
                    Toast.makeText(Updatepwd.this,"请输入邮箱",Toast.LENGTH_SHORT).show();
                    return;
                }
                gotoupdatepwd();
            }
        });
    }
    protected void init(){
        ip_port = (EditText)findViewById(R.id.ip_port);
        ip_port1 = (EditText)findViewById(R.id.ip_port1);
        ip_port11 = (EditText)findViewById(R.id.ip_port11);
        toSet = (Button)findViewById(R.id.toSet);
        toolbarLeftBt= (Button)findViewById(R.id.toolbarLeftBt);
        toolbarLeftBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(Updatepwd.this, LoginActivity.class);
                Updatepwd.this.startActivity(intent);
            }
        });
    }
    protected void gotoupdatepwd(){
        JSONObject jsonStr=new JSONObject();
        JSONObject par=new JSONObject();
        try {
            jsonStr.put("jsonrpc","2.0");
            jsonStr.put("method","getSysUserByUserName");
            par.put("token",token);
            par.put("userName",user);
            jsonStr.put("params",par);
            jsonStr.put("id",111);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonArrayRequest aareq=new JsonArrayRequest(HttpPath,
                new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray jsonObject) {

                    }
                },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){

            }
        });
        JsonObjectRequest req=new JsonObjectRequest(Request.Method.POST, HttpPath, jsonStr,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                    }
                },new Response.ErrorListener(){
                   @Override
                   public void onErrorResponse(VolleyError error){
                       String ss=error.toString();
                       if(ss.length()<100){
                           Toast.makeText(Updatepwd.this, "网络繁忙，请等待",Toast.LENGTH_SHORT).show();
                           return;
                       }
                       String str = ss.substring(ss.indexOf("["), ss.indexOf("]") + 1);
                       JSONArray jsonObject = null;
                       if(str.length()<50){
                           Toast.makeText(Updatepwd.this, "账号不存在", Toast.LENGTH_SHORT).show();
                           return;
                       }
                       try {
                           jsonObject = new JSONArray(str);
                           JSONObject obj = new JSONObject(jsonObject.getString(0));
                           if(obj.getString("result").length()>0) {
                               JSONObject obj1 = new JSONObject(obj.getString("result"));
                               String num= obj1.getString("phoneNumber");
                               String eee=obj1.getString("email");
                               if(num.equals(phnum)){
                                   if(eee.equals(email)){
                                       Intent intent = new Intent();
                                       intent.setClass(Updatepwd.this, Setpwd.class);
                                       Updatepwd.this.startActivity(intent);
                                       finish();
                                   }else {
                                       Toast.makeText(Updatepwd.this,"邮箱与账号不比配",Toast.LENGTH_SHORT).show();
                                   }
                               }else{
                                   Toast.makeText(Updatepwd.this,"手机号码与账号不比配",Toast.LENGTH_SHORT).show();
                               }

                           }else{
                               Toast.makeText(Updatepwd.this,"账号不存在",Toast.LENGTH_SHORT).show();
                           }
                       } catch (JSONException e) {
                           e.printStackTrace();
                           Toast.makeText(Updatepwd.this, "网络繁忙，请等待",Toast.LENGTH_SHORT).show();
                       }

                   }
        });
         req.setTag("jsonPost");
         VollyQueue.getHttpQueues().add(req);
    }
}

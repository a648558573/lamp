package com.example.administrator.light;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.administrator.light.util.SharedPreferencesUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2019/5/23.
 */

public class Setpwd extends AppCompatActivity {
    private EditText ip_port , ip_port1;
    private Button toSet;
    private String account = null , password = null, token = null;
    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pwd);
        account = (String) SharedPreferencesUtils.getParam(this, "username", "");
        password = (String) SharedPreferencesUtils.getParam(this, "password", "");
        token=(String) SharedPreferencesUtils.getParam(this, "token", "");
        init();
        toSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pwd1=ip_port.getText().toString();
                String pwd2=ip_port1.getText().toString();
                if(pwd1.length()==0||pwd2.length()==0){
                    Toast.makeText(Setpwd.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }
              if(ip_port.getText().toString().equals(ip_port1.getText().toString())){
                  String HttpPath="http://192.168.0.157:45002/api/userAuth";
                  JSONObject jsonStr=new JSONObject();
                  JSONObject par=new JSONObject();
                  try {
                      jsonStr.put("jsonrpc","2.0");
                      jsonStr.put("method","updatePasswordSysUser");
                      par.put("userName","wqq");
                      par.put("newPassword",pwd1);
                      par.put("token",token);
                      jsonStr.put("params",par);
                      jsonStr.put("id",111);
                  } catch (JSONException e) {
                      e.printStackTrace();
                  }
                  JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST,HttpPath
                          ,jsonStr,new Response.Listener<JSONObject>(){
                      @Override
                      public void onResponse(JSONObject response) {
                          Toast.makeText(Setpwd.this, "设置成功", Toast.LENGTH_SHORT).show();
                      }
                  },new Response.ErrorListener() {
                      @Override
                      public void onErrorResponse(VolleyError error) {
                          String ss=error.toString();
                          String str = ss.substring(ss.indexOf("["), ss.indexOf("]") + 1);

                          JSONArray jsonObject = null;
                          try {
                              jsonObject = new JSONArray(str);
                              JSONObject obj = new JSONObject(jsonObject.getString(0));
                              if(obj.getString("result").equals("true")){
                                  Toast.makeText(Setpwd.this, "设置成功", Toast.LENGTH_SHORT).show();
                                  Intent intent = new Intent();
                                  intent.setClass(Setpwd.this, LoginActivity.class);
                                  Setpwd.this.startActivity(intent);
                                  finish();
                              }
                          } catch (JSONException e) {
                              e.printStackTrace();
                          }

                      }
                  });
                  jsonObjectRequest.setTag("jsonPost");
                  VollyQueue.getHttpQueues().add(jsonObjectRequest);

              }else {
                  Toast.makeText(Setpwd.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
              }
            }
        });
    }
   public void init(){
       ip_port = (EditText)findViewById(R.id.ip_port);
       ip_port1 = (EditText)findViewById(R.id.ip_port1);
       toSet = (Button)findViewById(R.id.toSet);
   }
}

package com.example.administrator.light;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.administrator.light.util.SharedPreferencesUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by byjon 2019/5/10.
 */
public class LoginActivity extends AppCompatActivity {
    private TextView login_set, rootURL;
    private EditText login_accountnum , login_password;
    private Button login_login,button;
    private ProgressDialog progressDialog = null;
    private String account = null , password = null, rootURL_str = null,token=null;
    String HttpPath="http://192.168.0.157:45002/login";

    @Override
    protected void onResume() {
        String preferences_ip = (String) SharedPreferencesUtils.getParam(this, "rootURL", "");
        rootURL.setText(preferences_ip);
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        account = (String) SharedPreferencesUtils.getParam(this, "username", "");
        password = (String) SharedPreferencesUtils.getParam(this, "password", "");
        token=(String) SharedPreferencesUtils.getParam(this, "token", "");
         if(account.length()>0&&password.length()>0&&token.length()>0){
         Intent intent=new Intent(LoginActivity.this,MainActivity.class);
         startActivity(intent);
         LoginActivity.this.finish();
         }
        setContentView(R.layout.activity_login);
        init();
        login_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootURL_str = rootURL.getText().toString();
                account = login_accountnum.getText().toString();
                password = login_password.getText().toString();
                if (rootURL_str.length() == 0) {
                    Toast.makeText(LoginActivity.this, "ip不能为空", Toast.LENGTH_SHORT).show();
                }
                else if (account.length() == 0 ||password.length()==0) {
                    Toast.makeText(LoginActivity.this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog = ProgressDialog.show(LoginActivity.this, null, "登陆中...", true);
                  //Asynlogin(URL);
                   jsonRequest_post();
                }
            }
        });
        //忘记密码
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferencesUtils.setParam(LoginActivity.this, "token",
                        "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJ3cXEiLCJpYXQiOjE1NTkxMTk1NDMsInN1YiI6ImNoYW5naGVk" +
                                "aWFucWkiLCJpc3MiOiJzdG9uZXdhbmciLCJleHAiOjE1NTk0Nzk1NDN9.0P96_GPpk_RP50b-PQe7v_gJd2dCCLKKhRGo1GX3hdE");
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, Updatepwd.class);
                LoginActivity.this.startActivity(intent);
                finish();
            }
        });

    }
    public void init() {
        login_set = (TextView)findViewById(R.id.login_set);
        login_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, SetIPActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });
        rootURL = (TextView)findViewById(R.id.ip_port);
        login_accountnum = (EditText)findViewById(R.id.login_accountnum);
        login_password = (EditText)findViewById(R.id.login_password);
        login_login = (Button)findViewById(R.id.login_login);
        button = (Button)findViewById(R.id.button);
        String preferences_username = (String) SharedPreferencesUtils.getParam(this, "username", "");
        String preferences_password = (String) SharedPreferencesUtils.getParam(this, "password", "");
        String preferences_token = (String) SharedPreferencesUtils.getParam(this, "token", "");
        String time = (String) SharedPreferencesUtils.getParam(this, "time","0");
        login_accountnum.setText(preferences_username);
        login_password.setText(preferences_password);

        /*if(time.equals("1")) {
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, MainActivity.class);
            LoginActivity.this.startActivity(intent);
            finish();
        } else {
            if(preferences_username.length()!=0&&preferences_password.length()!=0) {
                login_accountnum.setText(preferences_username);
                login_password.setText(preferences_password);
            }
        }*/

    }
    private void jsonRequest_post(){
//	  <span style="color:#ff0000;"> //封装请求參数 </span>span
              JSONObject jsonStr=new JSONObject();
               JSONObject par=new JSONObject();
        try {
            jsonStr.put("jsonrpc","2.0");
            jsonStr.put("method","signIn");
            par.put("userName",account);
            par.put("password",password);
            par.put("client","androidOnline");
            jsonStr.put("params",par);
            jsonStr.put("id",111);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST,HttpPath
                ,jsonStr,new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                // TODO Auto-generated method stub
                JSONArray jsonObject;
                try {
                      jsonObject = new JSONArray(response.toString());
                    Toast.makeText(LoginActivity.this, jsonObject.getString(0), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                //由于服务端返回的是JSONArray则在这里取返回的数据；
                String ss = error.toString();
                // String st=ss.substring(2, ss.indexOf("["));
                if(ss.length()<50){
                    Toast.makeText(LoginActivity.this, "出错了，请检查一下网络", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }
                String str = ss.substring(ss.indexOf("["), ss.indexOf("]") + 1);
                try {
                    JSONArray jsonObject = new JSONArray(str);
                    JSONObject obj = new JSONObject(jsonObject.getString(0));
                    if (ss.length()!=236){
                        JSONObject obj1 = new JSONObject(obj.getString("result"));
                            if (obj1.getString("promptMessage").equals("loginFailed")) {
                                Toast.makeText(LoginActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                return;
                            }
                            if (obj1.getString("promptMessage").equals("loginSuccessfully")) {
                                Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                                SharedPreferencesUtils.setParam(LoginActivity.this, "username", "admin");
                                SharedPreferencesUtils.setParam(LoginActivity.this, "password", "admin123");
                                SharedPreferencesUtils.setParam(LoginActivity.this, "token", obj1.getString("token"));
                               if (Integer.parseInt(obj1.getString("forcePasswordChange")) == 1){
                                    Toast.makeText(LoginActivity.this, "首次登陆要修改密码", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent();
                                    intent.setClass(LoginActivity.this, Setpwd.class);
                                    LoginActivity.this.startActivity(intent);
                                    finish();
                                } else {
                                Intent intent = new Intent();
                                intent.setClass(LoginActivity.this, MainActivity.class);
                                LoginActivity.this.startActivity(intent);
                                finish();
                            }
                        }
                    } else {
                       Toast.makeText(LoginActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                     }
                }catch(JSONException e){
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                }
        });
        jsonObjectRequest.setTag("jsonPost");
        VollyQueue.getHttpQueues().add(jsonObjectRequest);
    }
    public void Asynlogin(String Url) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("1")) {
                            SharedPreferencesUtils.setParam(LoginActivity.this, "username", account);
                            SharedPreferencesUtils.setParam(LoginActivity.this, "password", password);
                            SharedPreferencesUtils.setParam(LoginActivity.this, "time", "1");
                            progressDialog.dismiss();
                            Intent intent = new Intent();
                            intent.setClass(LoginActivity.this, MainActivity.class);
                            LoginActivity.this.startActivity(intent);
                            finish();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this,"出错了，请检查一下网络", Toast.LENGTH_SHORT).show();
            }
        });
        //设置请求的Tag标签，可以在全局请求队列中通过Tag标签进行请求的查找
        stringRequest.setTag("");
        //将请求加入全局队列中
        VollyQueue.getHttpQueues().add(stringRequest);
    }
}
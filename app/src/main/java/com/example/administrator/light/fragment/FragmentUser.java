package com.example.administrator.light.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.administrator.light.BaseActivity;
import com.example.administrator.light.R;
import com.example.administrator.light.Setpwd;
import com.example.administrator.light.VollyQueue;
import com.example.administrator.light.util.SharedPreferencesUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.R.attr.data;
import static java.lang.Integer.parseInt;

/**
 * Created by Administrator on 2019/5/27.
 */

public class FragmentUser extends BaseActivity {
    private EditText orgname,name,phnumb,email,crtime,uptime,lasttime,user,remark,rolename;
    Spinner orgclassname;
    private Button toSet111;
    String HttpPath="http://192.168.0.157:45002/api/userAuth";
    private int userId;//用户id
    private int orgId;//所属组织Id
   // private String orgName;//组织名称
    private int orgClassId;//类别代码（1厂家、2代理商、3销售商、4工程商、5工程实施地）
    private int parentOrgId;//父组织id
    //private String userName;//登录用户名
    //private String name;//实名姓名
    private String iniPassword;//初始密码
    private String password;//登陆密码
  //  private String phoneNumber;//手机号码
   // private String EMail;//邮件地址
   // private String remark;//备注
    private String createTime;//创建时间
    private String updateTime;//更新时间
    private String lastLoginTime;//最近登录时间
  //  private String token;//令牌
    private int pcOnline;//pc client在线状态（1在线；0离线）
    private int wpOnline;//wp client网页在线状态（1在线；0离线）
    private int iosOnline;//ios client在线状态（1在线；0离线）
    private int androidOnline;//android client在线状态（1在线；0离线）
    private int usable;//是否可用（1、可用；0禁用）
    private String token,token1;
    String DevGroup=null;
    private String view_time;
    private int roleId;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user);
       initView();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        token=(String) SharedPreferencesUtils.getParam(this, "token", "");
        final String username=(String) SharedPreferencesUtils.getParam(this, "username", "");
                    JSONObject jsonStr=new JSONObject();
                    JSONObject par=new JSONObject();
                    try {
                        jsonStr.put("jsonrpc","2.0");
                        jsonStr.put("method","getSysUserByUserName");
                        par.put("token",token);
                        par.put("userName","wqq");
                        jsonStr.put("params",par);
                        jsonStr.put("id",111);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST,HttpPath
                            ,jsonStr,new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(FragmentUser.this, "设置成功", Toast.LENGTH_SHORT).show();
                        }
                    },new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            String ss=error.toString();
                            String str=null;
                            int a=ss.length();
                            if(a>31) {
                                str = ss.substring(ss.indexOf("["), ss.indexOf("]") + 1);
                            JSONArray jsonObject = null;
                            try {
                                jsonObject = new JSONArray(str);
                                JSONObject obj = new JSONObject(jsonObject.getString(0));
                                if(obj.getString("result").length()>0) {
                                    JSONObject obj1 = new JSONObject(obj.getString("result"));
                                    orgname.setText(obj1.getString("orgName"));
                                    user.setText(obj1.getString("userName"));
                                  //  类别代码 1厂家、2代理商、3销售商、4工程商、5工程实施地
                                    if(obj1.getString("orgClassId").equals("1")){
                                        orgclassname.setSelection(0,true);
                                    }
                                    else if(obj1.getString("orgClassId").equals("2")){
                                        orgclassname.setSelection(1,true);
                                    }
                                    else if(obj1.getString("orgClassId").equals("3")){
                                        orgclassname.setSelection(2,true);
                                    }
                                    else if(obj1.getString("orgClassId").equals("4")){
                                        orgclassname.setSelection(3,true);
                                    }
                                    else if(obj1.getString("orgClassId").equals("5")){
                                        orgclassname.setSelection(4,true);
                                    }
                                    remark.setText(obj1.getString("remark"));
                                    name.setText(obj1.getString("name"));
                                    phnumb.setText(obj1.getString("phoneNumber"));
                                    email.setText(obj1.getString("email"));
                                    userId=parseInt(obj1.getString("userId"));
                                    crtime.setText(obj1.getString("createTime"));
                                    uptime.setText(obj1.getString("updateTime"));
                                    lasttime.setText(obj1.getString("lastLoginTime"));
                                  //  orgId=parseInt(obj1.getString("userId"));//所属组织Id
                                    // private String orgName;//组织名称
                                   // orgClassId=parseInt(obj1.getString("orgClassId"));//类别代码（1厂家、2代理商、3销售商、4工程商、5工程实施地）
                                    parentOrgId=parseInt(obj1.getString("parentOrgId"));//父组织id
                                    //private String userName;//登录用户名
                                    //private String name;//实名姓名
                                    iniPassword=obj1.getString("iniPassword");//初始密码
                                    password=obj1.getString("password");//登陆密码
                                    //  private String phoneNumber;//手机号码
                                    // private String EMail;//邮件地址
                                    // private String remark;//备注
                                    createTime=obj1.getString("createTime");//创建时间
                                    updateTime=obj1.getString("updateTime");//更新时间
                                    lastLoginTime=obj1.getString("lastLoginTime");//最近登录时间
                                    token1=obj1.getString("token");//令牌
                                    pcOnline=parseInt(obj1.getString("pcOnline"));//pc client在线状态（1在线；0离线）
                                    wpOnline=parseInt(obj1.getString("wpOnline"));//wp client网页在线状态（1在线；0离线）
                                    iosOnline=parseInt(obj1.getString("iosOnline"));//ios client在线状态（1在线；0离线）
                                    androidOnline=parseInt(obj1.getString("androidOnline"));//android client在线状态（1在线；0离线）
                                    usable=parseInt(obj1.getString("usable"));//是否可用（1、可用；0禁用）
                                    seeroleid();
                                };
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            }else {
                                Toast.makeText(FragmentUser.this, "网络连接错误", Toast.LENGTH_SHORT).show();
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
        toSet111.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userId>0){
                    setuser();
                }
            }
        });
    }

    private void seeroleid() {
        JSONObject jsonStr=new JSONObject();
        JSONObject par=new JSONObject();
        try {
            jsonStr.put("jsonrpc","2.0");
            jsonStr.put("method","getSysUserRoleByUserId");
            par.put("token",token);
            par.put("userId",userId);
            jsonStr.put("params",par);
            jsonStr.put("id",111);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST,HttpPath
                ,jsonStr,new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(FragmentUser.this, "设置成功", Toast.LENGTH_SHORT).show();
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String ss=error.toString();
                String str = ss.substring(ss.indexOf("["), ss.lastIndexOf("]") + 1);

                JSONArray jsonObject = null;
                try {
                    jsonObject = new JSONArray(str);
                    JSONObject obj = new JSONObject(jsonObject.getString(0));
                    if(str.length()>50) {
                        JSONArray obj1 = new JSONArray(obj.getString("result"));
                        JSONObject obj2 = new JSONObject(obj1.getString(0));
                        String roid=obj2.get("roleId").toString();
                         roleId=  Integer.parseInt(roid);
                        seeroleName();
                    }else {
                        rolename.setText("");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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

    private void seeroleName() {
        JSONObject jsonStr=new JSONObject();
        JSONObject par=new JSONObject();
        try {
            jsonStr.put("jsonrpc","2.0");
            jsonStr.put("method","getSysRoleByRoleId");
            par.put("token",token);
            par.put("roleId",roleId);
            jsonStr.put("params",par);
            jsonStr.put("id",111);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST,HttpPath
                ,jsonStr,new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(FragmentUser.this, "设置成功", Toast.LENGTH_SHORT).show();
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String ss=error.toString();
                String str = ss.substring(ss.indexOf("["), ss.lastIndexOf("]") + 1);

                JSONArray jsonObject = null;
                try {
                    jsonObject = new JSONArray(str);
                    JSONObject obj = new JSONObject(jsonObject.getString(0));
                    if(obj.getString("result").length()>0) {
                        JSONObject obj1 = new JSONObject(obj.getString("result"));
                        rolename.setText(obj1.getString("roleCname"));
                    }else {
                        rolename.setText("");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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

    private void setuser() {
        JSONObject jsonStr=new JSONObject();
        JSONObject par=new JSONObject();
        JSONObject sysOrg=new JSONObject();
        if(user.getText().toString().length()==0){
            Toast.makeText(FragmentUser.this, "请输入正确的用户名", Toast.LENGTH_SHORT).show();
            return;
        };
        if(name.getText().toString().length()==0){
            Toast.makeText(FragmentUser.this, "请输入正确的姓名", Toast.LENGTH_SHORT).show();
            return;
        };
        if(orgname.getText().toString().length()==0){
            Toast.makeText(FragmentUser.this, "请输入正确的组织名称", Toast.LENGTH_SHORT).show();
            return;
        };
        if(!isChinaPhoneLegal(phnumb.getText().toString())){
            Toast.makeText(FragmentUser.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        };

        if(DevGroup.equals("厂家")){
            orgClassId=1;
        };
        if(DevGroup.equals("代理商")){
            orgClassId=2;
        };
        if(DevGroup.equals("销售商")){
            orgClassId=3;
        };
        if(DevGroup.equals("工程商")){
            orgClassId=4;
        };
        if(DevGroup.equals("工程实施地")){
            orgClassId=5;
        };
        SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd");
        view_time=sd.format(new Date());
        updateTime=view_time;
        lastLoginTime=view_time;
        if(!isEmail(email.getText().toString())){
            Toast.makeText(FragmentUser.this, "请输入正确的邮箱地址", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            jsonStr.put("jsonrpc","2.0");
            jsonStr.put("method","updateSysUserByUserId");
            par.put("token",token);
                                sysOrg.put("orgName",uncoden(orgname.getText().toString()));
                                sysOrg.put( "androidOnline",androidOnline);
                                sysOrg.put("remark", uncoden(remark.getText().toString()));
                                sysOrg.put("updateTime",updateTime);
                                sysOrg.put("orgClassId",orgClassId);//父组织id
                                sysOrg.put( "userName",uncoden(user.getText().toString()));
                                sysOrg.put("userId",userId);
                                sysOrg.put("orgId",orgId);
                                sysOrg.put("iniPassword",iniPassword);
                                sysOrg.put("token",token1);
                                sysOrg.put("iosOnline",iosOnline);
                                sysOrg.put("lastLoginTime",lastLoginTime);
                                sysOrg.put("usable",usable);
                                sysOrg.put("password",password);
                                sysOrg.put("phoneNumber",phnumb.getText());
                                sysOrg.put("parentOrgId",parentOrgId);
                                sysOrg.put("createTime",createTime);
                                sysOrg.put("name",uncoden(name.getText().toString()));
                                sysOrg.put("wpOnline",wpOnline);
                                sysOrg.put( "pcOnline",pcOnline);
                                sysOrg.put("email",email.getText());
                        par.put("sysUser",sysOrg);
            jsonStr.put("params",par);
            jsonStr.put("id",111);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST,HttpPath
                ,jsonStr,new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(FragmentUser.this, "设置成功", Toast.LENGTH_SHORT).show();
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
                    if(obj.getString("result").equals("true")) {
                        Toast.makeText(FragmentUser.this, "设置成功", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        jsonObjectRequest.setTag("jsonPost");
        VollyQueue.getHttpQueues().add(jsonObjectRequest);
    }
    public void initView() {
        orgname = (EditText)findViewById(R.id.orgname);
        orgclassname = (Spinner)findViewById(R.id.orgclassname);
        name = (EditText)findViewById(R.id.name);
        phnumb = (EditText)findViewById(R.id.phnumb);
        email = (EditText)findViewById(R.id.email);
        crtime = (EditText)findViewById(R.id.crtime);
        uptime = (EditText)findViewById(R.id.uptime);
        lasttime = (EditText)findViewById(R.id.lasttime);
        user = (EditText)findViewById(R.id.user);
        remark = (EditText)findViewById(R.id.remark);
        toSet111= (Button)findViewById(R.id.toSet111);
        rolename= (EditText)findViewById(R.id.rolename);
        crtime.setEnabled(false);
        uptime.setEnabled(false);
        lasttime.setEnabled(false);
        email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        phnumb.setInputType(InputType.TYPE_CLASS_NUMBER);
        getSearchView().setVisibility(View.GONE);
        getToolbarTitle().setText("个人信息");
        orgclassname.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] groups = getResources().getStringArray(R.array.orgclass);
                DevGroup = groups[i];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
    public String uncoden(String str){
        String rr=null;
        try {
            rr=URLEncoder.encode(str, "utf-8").trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return rr;
    }
    /**
     * 是否大陆手机
     * @param str
     * @return
     */
    public static boolean isChinaPhoneLegal(String str) {
        String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }
    //邮箱验证
    public static boolean isEmail(String strEmail) {
        String strPattern = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        if (TextUtils.isEmpty(strPattern)) {
            return false;
        } else {
            return strEmail.matches(strPattern);
        }
    }
}

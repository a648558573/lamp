package com.example.administrator.light.Tree;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.administrator.light.BaseActivity;
import com.example.administrator.light.R;
import com.example.administrator.light.util.SharedPreferencesUtils;
import com.example.administrator.light.other.collect.SetLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import static java.util.Arrays.asList;

/**
 * Created by binyejiang on 2019/7/2.
 */
public class NewGroup extends BaseActivity implements View.OnClickListener {
    private EditText CityEdit, RodNameEdit,groupname_edit,cnname_edit,groupfaid_edit,groupid_edit,orga_edit,project_edit,
            remarks_edit,xieyi_edit,bianhao_edit,numb_edit;
    private CheckBox RodNameCheckBox, LocationCheckBox;
    private LinearLayout LocationLayout;
    private TextView LocationText;
    private Button bt;
    private ProgressDialog progressDialog = null;
    private Spinner grouptypespinner, availabilityspinner;
    private ArrayList<String> bigTreeList, smallTreeList;
    private ArrayAdapter<String> bigTreeAdapter, smallTreeAdapter;
    private String rootURL,shebeiname;
    private String account = null;
    private String password = null;
    private String token = null;
    private String DevGroup = "区域分组";
    private List  leixname= asList("终端", "输出", "单灯");
    private List  keyname= asList("可用", "禁用");
    private  String HttpPath="http://192.168.0.157:45002/api/userAuth";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newgroup);
        init();
       //getTree(DevGroup, account, password);
    }

    private void init() {
        getToolbarTitle().setText("新增设备");
        rootURL = (String) SharedPreferencesUtils.getParam(this, "rootURL", "");
        account = (String) SharedPreferencesUtils.getParam(this, "username", "");
        password = (String) SharedPreferencesUtils.getParam(this, "password", "");
        token = (String) SharedPreferencesUtils.getParam(this, "token", "");
        bigTreeList = new ArrayList<String>();
        smallTreeList = new ArrayList<String>();
        shebeiname=getIntent().getStringExtra("newgroup");

        grouptypespinner = (Spinner)findViewById(R.id.grouptype_spinner);
        LinearLayout grouptype = (LinearLayout) findViewById(R.id.grouptype);
        LinearLayout groupfatype = (LinearLayout) findViewById(R.id.groupfatype);
        availabilityspinner = (Spinner)findViewById(R.id.availability_spinner);
        groupname_edit = (EditText)findViewById(R.id.groupname_edit);
        cnname_edit = (EditText)findViewById(R.id.cnname_edit);
        groupfaid_edit = (EditText)findViewById(R.id.groupfaid_edit);
        groupid_edit = (EditText)findViewById(R.id.groupid_edit);
        orga_edit = (EditText)findViewById(R.id.orga_edit);
        project_edit = (EditText)findViewById(R.id.project_edit);
        remarks_edit = (EditText)findViewById(R.id.remarks_edit);
        xieyi_edit = (EditText)findViewById(R.id.xieyi_edit);
        bianhao_edit = (EditText)findViewById(R.id.bianhao_edit);
        numb_edit = (EditText)findViewById(R.id.numb_edit);
//        RodNameCheckBox = (CheckBox)findViewById(R.id.rodname_checkbox);
        LocationCheckBox = (CheckBox)findViewById(R.id.location_checkbox);
        LocationLayout = (LinearLayout)findViewById(R.id.location_layout);
        LocationText = (TextView)findViewById(R.id.location_text);
        bt = (Button)findViewById(R.id.group_bt);
        bigTreeAdapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.custom_spinner, leixname);
        grouptypespinner.setAdapter(bigTreeAdapter);
        smallTreeAdapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.custom_spinner , keyname);
        availabilityspinner.setAdapter(smallTreeAdapter);
        LocationLayout.setOnClickListener(this);
        bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.location_layout:
                Intent intent = new Intent();
                intent.setClass(NewGroup.this, SetLocation.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.group_bt:
                String DevNo_int = grouptypespinner.getSelectedItem().toString().trim().split("-")[0];
                String groupname = groupname_edit.getText().toString().trim();
                String cnname = cnname_edit.getText().toString().trim();
                String groupfaid = groupfaid_edit.getText().toString().trim();
                String groupid = groupid_edit.getText().toString().trim();
                String orga = orga_edit.getText().toString().trim();
                String prohect = project_edit.getText().toString().trim();
                String remarks = remarks_edit.getText().toString().trim();
                String xieyi = xieyi_edit.getText().toString().trim();
                String bianhao = bianhao_edit.getText().toString().trim();
                String numb = numb_edit.getText().toString().trim();
                String temp_char1 = availabilityspinner.getSelectedItem().toString().trim().split("-")[0];
//                boolean DevName_bool = RodNameCheckBox.isChecked();
                boolean XY_bool = LocationCheckBox.isChecked();
                String DevName, DevX, DevY;
//                if(DevName_bool) {
//                    DevName = RodNameEdit.getText().toString().trim();
//                } else {
//                    DevName = "";
//                }
                if(XY_bool) {
                    DevX = LocationText.getText().toString().split(" ")[0];
                    DevY = LocationText.getText().toString().split(" ")[1];
                } else {
                    DevX = "";
                    DevY = "";
                }
                SetDev_temp(DevNo_int, groupname, temp_char1,groupid,prohect,remarks,
                        xieyi,bianhao,numb,groupfaid, cnname,
                        orga, DevX, DevY);
                break;
            default:
                break;
        }
    }

    private void SetDev_temp(String devNo_int, String groupname,
                             String temp_char1, String groupid, String prohect,
                             String remarks, String xieyi, String bianhao, String numb,
                             String groupfaid, String cnname, String orga, String devX, String devY) {
        JSONObject jsonStr=new JSONObject();
        JSONObject par=new JSONObject();
        JSONObject poll=new JSONObject();
        try {
            jsonStr.put("jsonrpc","2.0");
            jsonStr.put("method","addSysResBatch");
             par.put("token",token);
                poll.put("resName",groupname);
                poll.put("resCname",cnname);
                poll.put("parentResId",groupfaid);
                poll.put("resType",0);
                poll.put("funModType",0);
                poll.put("deviceType",devNo_int);
                poll.put("updateUserName",account);
                poll.put("apiurl","1");
                poll.put("funModClass","1");
                poll.put("apimethod","1");
                poll.put("devId",groupid);
                poll.put("remark",remarks);
                poll.put("createTime","0");
                poll.put("updateTime","0");
                poll.put("devIdDecimal",groupid);
                poll.put("userSortedId",bianhao);
                poll.put("protocol",xieyi);
                poll.put("longitude",devY);
                poll.put("latitude",devX);
                poll.put("usable",temp_char1);
                poll.put("sumOper",numb);

             par.put("sysRes",poll);

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
                        Toast.makeText(NewGroup.this, "网络连接错误", Toast.LENGTH_SHORT).show();
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
                                    JSONObject obj2 = new JSONObject(obj1.getString("result"));
                                    String seccnum=obj1.getString("sumSucceed");
                                    String failnum=obj1.getString("sumFail");
                                    Toast.makeText(NewGroup.this,"成功个数："+seccnum+"失败个数："+failnum, Toast.LENGTH_SHORT).show();

                                }else {
                                    //rolename.setText("");
                                }
                            } catch (JSONException e) {
                                Looper.prepare();
                                Toast.makeText(NewGroup.this,"网络连接错误", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                                e.printStackTrace();
                            }
                        }
                    }
                });//此处省略回调方法。
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String temp = data.getStringExtra("return");
        if(requestCode == 0 && !temp.equals("")){
            LocationText.setText(temp);
        }
    }

    private void getTree(String DevGroup, String account, String password) {
    }
}

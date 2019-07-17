package com.example.administrator.light.other.collect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.administrator.light.BaseActivity;
import com.example.administrator.light.R;
import com.example.administrator.light.VollyQueue;
import com.example.administrator.light.util.SharedPreferencesUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by JO on 2016/5/26.
 */
public class SetConcentrator extends BaseActivity implements View.OnClickListener {
    private EditText CityEdit, RodNameEdit;
    private CheckBox RodNameCheckBox, LocationCheckBox;
    private LinearLayout LocationLayout;
    private TextView LocationText;
    private Button bt;
    private ProgressDialog progressDialog = null;
    private Spinner spinner1, spinner2;
    private ArrayList<String> bigTreeList, smallTreeList;
    private ArrayAdapter<String> bigTreeAdapter, smallTreeAdapter;
    private String rootURL;
    private String account = null;
    private String password = null;
    private String DevGroup = "区域分组";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collect_set_concentrator);
        init();
        getTree(DevGroup, account, password);
    }

    private void init() {
        getToolbarTitle().setText("新增终端");
        rootURL = (String) SharedPreferencesUtils.getParam(this, "rootURL", "");
        account = (String) SharedPreferencesUtils.getParam(this, "username", "");
        password = (String) SharedPreferencesUtils.getParam(this, "password", "");
        bigTreeList = new ArrayList<String>();
        smallTreeList = new ArrayList<String>();

        spinner1 = (Spinner)findViewById(R.id.rodnum_spinner);
        spinner2 = (Spinner)findViewById(R.id.group_spinner);
        CityEdit = (EditText)findViewById(R.id.city_edit);
        RodNameEdit = (EditText)findViewById(R.id.rodname_edit);
        RodNameCheckBox = (CheckBox)findViewById(R.id.rodname_checkbox);
        LocationCheckBox = (CheckBox)findViewById(R.id.location_checkbox);
        LocationLayout = (LinearLayout)findViewById(R.id.location_layout);
        LocationText = (TextView)findViewById(R.id.location_text);
        bt = (Button)findViewById(R.id.getconcentrator_bt);

        LocationLayout.setOnClickListener(this);
        bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.location_layout:
                Intent intent = new Intent();
                intent.setClass(SetConcentrator.this, SetLocation.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.getconcentrator_bt:
                String DevNo_int = spinner1.getSelectedItem().toString().trim().split("-")[0];
                String Area_name = CityEdit.getText().toString().trim();
                String temp_char1 = spinner2.getSelectedItem().toString().trim().split("-")[0];
                boolean DevName_bool = RodNameCheckBox.isChecked();
                boolean XY_bool = LocationCheckBox.isChecked();
                String DevName, DevX, DevY;
                if(DevName_bool) {
                    DevName = RodNameEdit.getText().toString().trim();
                } else {
                    DevName = "";
                }
                if(XY_bool) {
                    DevX = LocationText.getText().toString().split(" ")[0];
                    DevY = LocationText.getText().toString().split(" ")[1];
                } else {
                    DevX = "";
                    DevY = "";
                }
                SetDev_temp(DevNo_int, Area_name, temp_char1,
                        DevName_bool, DevName,
                        XY_bool, DevX, DevY);
                break;
            default:
                break;
        }
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
        try {
            progressDialog = ProgressDialog.show(SetConcentrator.this, null, "加载中...", true);
            String URL = rootURL + "/Tree/DevInfoGroup?DevGroup="+ URLEncoder.encode(DevGroup, "utf-8").trim()
                    + "&log_name=" + account
                    + "&log_pass=" + password + "&sn_node_mode=1";
            System.out.println(URL);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println(response);
                            progressDialog.dismiss();
                            if(!response.isEmpty() && !response.trim().equals("")) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONArray bigTreeArray = jsonObject.getJSONArray("bigtree");
                                    JSONArray smallTreeArray = jsonObject.getJSONArray("smalltree");
                                    bigTreeList.clear();
                                    for(int i = 0; i < bigTreeArray.length(); i++) {
                                        bigTreeList.add(bigTreeArray.getString(i));
                                    }
                                    System.out.println(bigTreeList);
                                    bigTreeAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                            R.layout.custom_spinner, bigTreeList);
                                    spinner2.setAdapter(bigTreeAdapter);
                                    smallTreeList.clear();
                                    for(int i = 0; i < smallTreeArray.length(); i++) {
                                        JSONArray itemSmallTreeArray = smallTreeArray.getJSONArray(i);
                                        for(int j = 0; j < itemSmallTreeArray.length(); j++) {
                                            smallTreeList.add(itemSmallTreeArray.getString(j));
                                        }
                                    }
                                    System.out.println(smallTreeList);
                                    smallTreeAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                            R.layout.custom_spinner , smallTreeList);
                                    spinner1.setAdapter(smallTreeAdapter);
                                } catch (JSONException e) {
                                    Toast.makeText(getApplicationContext(), "用户名加载失败", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "用户加载找不到接口!!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(getApplicationContext(), "网络异常", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
            //设置请求的Tag标签，可以在全局请求队列中通过Tag标签进行请求的查找
            stringRequest.setTag("");
            //将请求加入全局队列中
            VollyQueue.getHttpQueues().add(stringRequest);
        } catch (Exception e) { //UnsupportedEncodingException
            e.printStackTrace();
            progressDialog.dismiss();
        }
    }

    private void SetDev_temp(String DevNo_int, String Area_name, String temp_char1,
                                    boolean DevName_bool, String DevName,
                                    boolean XY_bool, String DevX, String DevY) {
        try {
            progressDialog = ProgressDialog.show(SetConcentrator.this, null, "加载中...", true);
            String URL = rootURL + "/Tree/SetDev_temp?DevNo_int="+ DevNo_int
                    + "&Area_name=" + Area_name
                    + "&temp_char1=" + temp_char1
                    + "&DevName_bool=" + DevName_bool
                    + "&DevName=" + DevName
                    + "&XY_bool=" + XY_bool
                    + "&DevX=" + DevX
                    + "DevY=" + DevY;
            System.out.println(URL);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println(response);
                            progressDialog.dismiss();
                            if(!response.isEmpty() && !response.trim().equals("")) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String str = jsonObject.getString("rslt");
                                    Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    Toast.makeText(getApplicationContext(), "用户名加载失败", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "用户加载找不到接口!!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(getApplicationContext(), "网络异常", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
            //设置请求的Tag标签，可以在全局请求队列中通过Tag标签进行请求的查找
            stringRequest.setTag("");
            //将请求加入全局队列中
            VollyQueue.getHttpQueues().add(stringRequest);
        } catch (Exception e) { //UnsupportedEncodingException
            e.printStackTrace();
            progressDialog.dismiss();
        }
    }

}

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
import android.widget.NumberPicker;
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
public class SetSingle extends BaseActivity implements View.OnClickListener {
    private EditText CityEdit, RodNameEdit;
    private CheckBox RodRealCheckBox, RodNameCheckBox, LocationCheckBox;
    private NumberPicker RodNumNp1, RodNumNp2, RodRealNp1, RodRealNp2;
    private TextView LocationText;
    private LinearLayout LocationLayout;
    private Button bt;
    private ProgressDialog progressDialog = null;
    private Spinner spinner;
    private ArrayList<String> smallTreeList;
    private ArrayAdapter<String> smallTreeAdapter;
    private String rootURL;
    private String account = null;
    private String password = null;
    private String DevGroup = "区域分组";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collect_set_single);
        init();
        getSmallTree(DevGroup, account, password);
    }

    private void init() {
        getToolbarTitle().setText("单灯采集");
        rootURL = (String) SharedPreferencesUtils.getParam(this, "rootURL", "");
        account = (String) SharedPreferencesUtils.getParam(this, "username", "");
        password = (String) SharedPreferencesUtils.getParam(this, "password", "");
        smallTreeList = new ArrayList<String>();

        spinner = (Spinner)findViewById(R.id.rodnum_spinner);
        CityEdit = (EditText)findViewById(R.id.city_edit);
        RodNameEdit = (EditText)findViewById(R.id.rodname_edit);

        RodRealCheckBox = (CheckBox)findViewById(R.id.rodreal_checkbox);
        RodNameCheckBox = (CheckBox)findViewById(R.id.rodname_checkbox);
        LocationCheckBox = (CheckBox)findViewById(R.id.location_checkbox);

        RodNumNp1 = (NumberPicker)findViewById(R.id.rodnum_np1);
        RodNumNp2 = (NumberPicker)findViewById(R.id.rodnum_np2);
        RodRealNp1 = (NumberPicker)findViewById(R.id.rodreal_np1);
        RodRealNp2 = (NumberPicker)findViewById(R.id.rodreal_np2);
        initNumberPicker(RodNumNp1);
        initNumberPicker(RodNumNp2);
        initNumberPicker(RodRealNp1);
        initNumberPicker(RodRealNp2);

        LocationText = (TextView)findViewById(R.id.location_text);
        LocationLayout = (LinearLayout)findViewById(R.id.location_layout);
        LocationLayout.setOnClickListener(this);
        bt = (Button)findViewById(R.id.getsingle_bt);
        bt.setOnClickListener(this);
    }

    private void initNumberPicker(NumberPicker np) {
        np.setMaxValue(1000);
        np.setMinValue(0);
        np.setValue(1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.location_layout:
                Intent intent = new Intent();
                intent.setClass(SetSingle.this, SetLocation.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.getsingle_bt:
                String DevNo_int = spinner.getSelectedItem().toString().trim().split("-")[0];
                String Area_name = CityEdit.getText().toString().trim();
                String rod_num_str = RodNumNp1.getValue() + "-" + RodNumNp2.getValue();
                boolean rod_real_bool = RodRealCheckBox.isChecked();
                boolean rod_name_bool = RodNameCheckBox.isChecked();
                boolean XY_bool = LocationCheckBox.isChecked();
                String rod_real_str, rod_name, DevX, DevY;
                if(rod_real_bool) {
                    rod_real_str = RodRealNp1.getValue() + "-" + RodRealNp2.getValue();
                } else {
                    rod_real_str = "";
                }
                if(rod_name_bool) {
                    rod_name = RodNameEdit.getText().toString().trim();
                } else {
                    rod_name = "";
                }
                if(XY_bool) {
                    DevX = LocationText.getText().toString().split(" ")[0];
                    DevY = LocationText.getText().toString().split(" ")[1];
                } else {
                    DevX = "";
                    DevY = "";
                }
                Setsingle_map_temp(DevNo_int, Area_name, rod_num_str,
                        rod_real_bool, rod_real_str,
                        rod_name_bool, rod_name,
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

    private void getSmallTree(String DevGroup, String account, String password) {
        try {
            progressDialog = ProgressDialog.show(SetSingle.this, null, "加载中...", true);
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
                                    JSONArray jsonArray = jsonObject.getJSONArray("smalltree");
                                    smallTreeList.clear();
                                    for(int i = 0; i < jsonArray.length(); i++) {
                                        JSONArray itemArray = jsonArray.getJSONArray(i);
                                        for(int j = 0; j < itemArray.length(); j++) {
                                            smallTreeList.add(itemArray.getString(j));
                                        }
                                    }
                                    System.out.println(smallTreeList);
                                    smallTreeAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                            R.layout.custom_spinner, smallTreeList);
                                    spinner.setAdapter(smallTreeAdapter);
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

    private void Setsingle_map_temp(String DevNo_int, String Area_name, String rod_num_str,
                                    boolean rod_real_bool, String rod_real_str,
                                    boolean rod_name_bool, String rod_name,
                                    boolean XY_bool, String DevX, String DevY) {
        try {
            progressDialog = ProgressDialog.show(SetSingle.this, null, "加载中...", true);
            String URL = rootURL + "/Tree/Setsingle_map_temp?DevNo_int="+ DevNo_int
                    + "&Area_name=" + Area_name
                    + "&rod_num_str=" + rod_num_str
                    + "&rod_real_bool=" + rod_real_bool
                    + "&rod_real_str=" + rod_real_str
                    + "&rod_name_bool=" + rod_name_bool
                    + "&rod_name=" + rod_name
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

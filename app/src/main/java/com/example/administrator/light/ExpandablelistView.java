package com.example.administrator.light;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.administrator.light.map.MapActivity;
import com.example.administrator.light.other.AssetRatio;
import com.example.administrator.light.other.CentralizedFaultQuery;
import com.example.administrator.light.other.Daily;
import com.example.administrator.light.other.Energy_query;
import com.example.administrator.light.other.SingleFaultQuery;
import com.example.administrator.light.other.SingleParaHistory;
import com.example.administrator.light.other.Three_phase;
import com.example.administrator.light.system.CentralizingSwitch;
import com.example.administrator.light.system.CentralizingSwitch_Group;
import com.example.administrator.light.system.ElectricalParameter;
import com.example.administrator.light.system.Single_Dimming_TimeCtrl.SingleActivity;
import com.example.administrator.light.system.SingleTimeSetting;
import com.example.administrator.light.system.TimeControl;
import com.example.administrator.light.system.TimeControl_Group;
import com.example.administrator.light.util.SharedPreferencesUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wujia on 2016/3/26.
 */
public class ExpandablelistView extends BaseActivity {
    Spinner expand_spinner;
    List<String> grouplist = new ArrayList<String>();
    List<List<String>> childLeftList = new ArrayList<List<String>>();
    List<List<String>> childRightList = new ArrayList<List<String>>();
    List<List<String>> childcoordinate = new ArrayList<List<String>>();
    String account = null;
    String password = null;
    String DevGroup = "区域分组";
    String From = null;
    ExpandableListView listView;
    TextView expand_textview;
    RelativeLayout group_relative;
    private String rootURL = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expandablelistview);
        init();
        //listView();
    }

    public void init() {
        rootURL = (String) SharedPreferencesUtils.getParam(this, "rootURL", "");
        account = (String) SharedPreferencesUtils.getParam(this, "username", "");
        password = (String) SharedPreferencesUtils.getParam(this, "password", "");
        expand_spinner = (Spinner)findViewById(R.id.expand_spinner);
        expand_textview = (TextView)findViewById(R.id.expand_textview);
        Intent intent = getIntent();
        From = intent.getStringExtra("from");
        listView = (ExpandableListView) findViewById(R.id.list);

        //设置标题栏
        setTitle("智能路灯监控");

        //spinner列表点击选择的监听事件
        expand_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] groups = getResources().getStringArray(R.array.group);
                DevGroup = groups[i];
                //更新expandablelistview
                listView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    public void listView() {
        try {
            /*
            * 通过volley框架get方式获取列表信息
            * */
                    String URL = rootURL + "/Tree/DevInfoGroup?DevGroup=" + URLEncoder.encode(DevGroup, "utf-8").trim() + "&log_name=" + URLEncoder.encode(account, "utf-8").trim()
                            + "&log_pass=" + password + "&sn_node_mode=1";
            System.out.println(URL);
            StringRequest stringRequest = new StringRequest(Request.Method.GET,URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println(response);
                            if(!response.isEmpty() && !response.trim().equals("")) {
                                //解析JSON到list
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONArray groupArray = jsonObject.getJSONArray("bigtree");
                                    JSONArray childLeftArray = jsonObject.getJSONArray("smalltree");
                                    JSONArray childRightArray = jsonObject.getJSONArray("linktree");
                                    JSONArray coordinate = jsonObject.getJSONArray("DevXDevY");
                                    grouplist.clear();
                                    childLeftList.clear();
                                    childRightList.clear();
                                    childcoordinate.clear();

                                    for (int i = 0; i < groupArray.length(); i++) {
                                        List<String> left = new ArrayList<String>();
                                        List<String> right = new ArrayList<String>();
                                        List<String> xy = new ArrayList<String>();
                                        grouplist.add(groupArray.getString(i));
                                        for(int j = 0 ; j < childLeftArray.getJSONArray(i).length() ; j++) {
                                            left.add(childLeftArray.getJSONArray(i).getString(j));
                                            right.add(childRightArray.getJSONArray(i).getString(j));
                                            xy.add(coordinate.getJSONArray(i).getString(j));
                                        }
                                        childLeftList.add(left);
                                        childRightList.add(right);
                                        childcoordinate.add(xy);

                                    }
//                                    System.out.println(childLeftList.get(0).get(0).toString());
//                                    System.out.println(childLeftList.get(1).get(0).toString());
                                    Adapter();
                                } catch (JSONException e) {
                                    //e.printStackTrace();
                                    Toast.makeText(ExpandablelistView.this,"用户名加载失败",Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ExpandablelistView.this,"用户加载找不到接口!!!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ExpandablelistView.this,"网络异常", Toast.LENGTH_SHORT).show();
                }
            });
            //设置请求的Tag标签，可以在全局请求队列中通过Tag标签进行请求的查找
            stringRequest.setTag("");
            //将请求加入全局队列中
            VollyQueue.getHttpQueues().add(stringRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    /*
    * expandablelistview的适配函数
    * */
    public void Adapter() {
        ExpandableListAdapter adapter = new BaseExpandableListAdapter() {
            @Override
            public int getGroupCount() {
                return grouplist.size();
            }

            @Override
            public int getChildrenCount(int groupPosition) {
                return childLeftList.get(groupPosition).size();
            }

            @Override
            public Object getGroup(int groupPosition) {
                return grouplist.get(groupPosition);
            }

            @Override
            public Object getChild(int groupPosition, int childPosition) {
                System.out.println(groupPosition);
                System.out.println(childLeftList.get(groupPosition).get(childPosition));
                return childLeftList.get(groupPosition).get(childPosition);
            }

            public Object getChildright(int groupPosition, int childPosition) {
                return childRightList.get(groupPosition).get(childPosition);
            }

            @Override
            public long getGroupId(int groupPosition) {
                return groupPosition;
            }

            @Override
            public long getChildId(int groupPosition, int childPosition) {
                return childPosition;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            @Override
            public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                View ll = inflater.inflate(R.layout.grouplayout,null);
                final TextView group_text = (TextView)ll.findViewById(R.id.group_text);
                ImageView group_img = (ImageView)ll.findViewById(R.id.group_img);
                group_relative = (RelativeLayout)ll.findViewById(R.id.group_relative);
                group_img.setImageResource(R.drawable.devdown);
                group_text.setText(getGroup(groupPosition).toString());
                if(isExpanded) {
                    group_img.setImageResource(R.drawable.devup);
                }
                group_relative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      //  Toast.makeText(ExpandablelistView.this,"niam",Toast.LENGTH_SHORT).show();
                        String groupname = grouplist.get(groupPosition);
                        expand_textview.setText(groupname);
                        System.out.println((ArrayList<String>) childLeftList.get(groupPosition));
                        //根据不同功能需求，让点击子列表后转向不同的activity
                        Intent intent = new Intent();
                        intent.putExtra("groupname", groupname);
                        intent.putStringArrayListExtra("childList", (ArrayList<String>) childLeftList.get(groupPosition));
                        switch(From) {
                            case "timecontrol":
                                intent.setClass(ExpandablelistView.this, TimeControl_Group.class);
                                startActivity(intent);
                                break;
                            case "switch":
                                intent.setClass(ExpandablelistView.this, CentralizingSwitch_Group.class);
                                startActivity(intent);
                                break;
                        }
                    }
                });
                return ll;
            }

            @Override
            public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                View ll1 = inflater.inflate(R.layout.childlayout,null);
                TextView left = (TextView)ll1.findViewById(R.id.left);
                TextView right = (TextView)ll1.findViewById(R.id.right);
                ImageView img = (ImageView)ll1.findViewById(R.id.img);
                left.setText(getChild(groupPosition, childPosition).toString());
                if(getChildright(groupPosition, childPosition).toString().trim().equals("connet")) {
                    right.setText("连接成功");
                    img.setImageResource(R.drawable.connect_success);
                } else {
                    right.setText("掉线");
                    img.setImageResource(R.drawable.connect_fail);
                }
                return ll1;
            }

            @Override
            public boolean isChildSelectable(int i, int i1) {
                return true;
            }
        };
        ExpandableListView expandablelistView = (ExpandableListView)findViewById(R.id.list);
        expandablelistView.setAdapter(adapter);
        //子列表点击事件
        expandablelistView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View convertView, int groupPosition, int childPosition, long l) {
                String childname = childLeftList.get(groupPosition).get(childPosition).trim();
                //根据不同功能需求，让点击子列表后转向不同的activity
                switch(From) {
                    case "map":
                        String childxy = childcoordinate.get(groupPosition).get(childPosition).trim();
                        Intent intent1 = new Intent();
                        intent1.putExtra("childxy", childxy);
                        intent1.putExtra("childname", childname);
                        intent1.setClass(ExpandablelistView.this, MapActivity.class);
                        startActivity(intent1);
                        break;
                    case "parameter":
                        Intent intent_param = new Intent();
                        intent_param.putExtra("childname", childname);
                        intent_param.setClass(ExpandablelistView.this, ElectricalParameter.class);
                        startActivity(intent_param);
                        break;
                    case "timecontrol":
                        Intent intent_timecontrol = new Intent();
                        intent_timecontrol.putExtra("childname",childname);
                        intent_timecontrol.setClass(ExpandablelistView.this, TimeControl.class);
                        startActivity(intent_timecontrol);
                        break;
                    case "switch":
                        Intent intent = new Intent();
                        intent.putExtra("childname", childname);
                        intent.setClass(ExpandablelistView.this, CentralizingSwitch.class);
                        startActivity(intent);
                        break;
                    case "sldimming":
                        Intent intent_sldimming = new Intent();
                        intent_sldimming.putExtra("childname", childname);
                        intent_sldimming.putExtra("myTAG", "Dimming");
                        intent_sldimming.setClass(ExpandablelistView.this, SingleActivity.class);
                        startActivity(intent_sldimming);
                        break;
                    case "sltimecontrol":
                        Intent intent_sltimecontrol = new Intent();
                        intent_sltimecontrol.putExtra("childname", childname);
                        intent_sltimecontrol.putExtra("myTAG", "TimeControl");
                        intent_sltimecontrol.setClass(ExpandablelistView.this, SingleActivity.class);
                        startActivity(intent_sltimecontrol);
                        break;
                    case "sltcsetting":
                        Intent intent_sltcsetting = new Intent();
                        intent_sltcsetting.putExtra("childname", childname);
                        intent_sltcsetting.setClass(ExpandablelistView.this, SingleTimeSetting.class);
                        startActivity(intent_sltcsetting);
                        break;
                    case "energy_query":
                        Intent intent_query = new Intent();
                        intent_query.putExtra("childname", childname);
                        intent_query.setClass(ExpandablelistView.this, Energy_query.class);
                        startActivity(intent_query);
                        break;
                    case "daily":
                        Intent intent_daily = new Intent();
                        intent_daily.putExtra("childname", childname);
                        intent_daily.setClass(ExpandablelistView.this, Daily.class);
                        startActivity(intent_daily);
                        break;
                    case "three_phase":
                        Intent intent_parameters = new Intent();
                        intent_parameters.putExtra("childname", childname);
                        intent_parameters.setClass(ExpandablelistView.this, Three_phase.class);
                        startActivity(intent_parameters);
                        break;
                    case "asset_ratio":
                        Intent intent_asset_ratio = new Intent();
                        intent_asset_ratio.putExtra("childname", childname);
                        intent_asset_ratio.setClass(ExpandablelistView.this, AssetRatio.class);
                        startActivity(intent_asset_ratio);
                        break;
                    case "single_parameter_history":
                        Intent intent_single_parameter_history = new Intent();
                        intent_single_parameter_history.putExtra("childname", childname);
                        intent_single_parameter_history.setClass(ExpandablelistView.this, SingleParaHistory.class);
                        startActivity(intent_single_parameter_history);
                        break;
                    case "centralized_fault_query":
                        Intent intent_centralized_fault_query = new Intent();
                        intent_centralized_fault_query.putExtra("childname", childname);
                        intent_centralized_fault_query.setClass(ExpandablelistView.this, CentralizedFaultQuery.class);
                        startActivity(intent_centralized_fault_query);
                        break;
                    case "single_fault_query":
                        Intent intent_single_fault_query = new Intent();
                        intent_single_fault_query.putExtra("childname", childname);
                        intent_single_fault_query.setClass(ExpandablelistView.this, SingleFaultQuery.class);
                        startActivity(intent_single_fault_query);
                        break;
                }
                return false;
            }
        });
    }


}

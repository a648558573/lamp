package com.example.administrator.light.Tree;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.administrator.light.ExpandablelistViewput;
import com.example.administrator.light.R;
import com.example.administrator.light.VollyQueue;
import com.example.administrator.light.MainActivity;
import com.example.administrator.light.util.SharedPreferencesUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.media.CamcorderProfile.get;

/**
 * Created by byj on 2019/6/1.
 */

public class Tree_dandeng extends Fragment {
    private EditText orgname,name,phnumb,email,crtime,uptime,lasttime,user,remark,rolename;
    Spinner orgclassname;
    private Button btGdet,btSelect;
    ListView listView;
    String HttpPath="http://192.168.0.157:45002/api/tagGroup";
    private String token,token1;
    String DevGroup=null;
    private String view_time;
    private int roleId;
    private String resname=null;
    TextView select,tree_textview;
    String From = null;
    int  id=0;
    RelativeLayout group_relative;
    ExpandableListView expandablelistView;
    List<String> grouplist = new ArrayList<String>();
    List<Integer> grouplist1 = new ArrayList<Integer>();
    List<List<String>> childLeftList = new ArrayList<List<String>>();
    List<List<Integer>> childIdList = new ArrayList<List<Integer>>();
    List<List<Integer>> childId1List = new ArrayList<List<Integer>>();
    private List<List<Map<String, Object>>> singleList = new ArrayList<List<Map<String, Object>>>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.tree_group, container, false);
        initView(view);
        token=(String) SharedPreferencesUtils.getParam(getContext(), "token", "");
        final String username=(String) SharedPreferencesUtils.getParam(getContext(), "username", "");
        seelist();
        return view;
    }

    private void seelist() {
        JSONObject jsonStr=new JSONObject();
        JSONObject par=new JSONObject();
        try {
            jsonStr.put("jsonrpc","2.0");
            jsonStr.put("method","findAll");
            par.put("token",token);
            jsonStr.put("params",par);
            jsonStr.put("id",111);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(com.android.volley.Request.Method.POST,HttpPath
                ,jsonStr,new com.android.volley.Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getActivity(), "设置成功", Toast.LENGTH_SHORT).show();
            }
        },new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String ss=error.toString();
                String str=null;
                int a=ss.length();
                if(a>31) {
                    str = ss.substring(ss.indexOf("["),ss.lastIndexOf("]") + 1);
                    JSONArray jsonObject = null;
                    try {
                        jsonObject = new JSONArray(str);
                        if(str.length()>50) {
                            JSONObject obj1=new JSONObject(jsonObject.getString(0));
                            JSONArray obj2= obj1.getJSONArray("result");
                            grouplist.clear();
                            grouplist1.clear();
                            childLeftList.clear();
                            childIdList.clear();
                            childId1List.clear();
                            for(int i=0;i<obj2.length();i++){
                                JSONObject  obj3=new JSONObject(obj2.getString(i));
                                String resname=obj3.getString("tagGroupName");
                                int resid=obj3.getInt("tagGroupId");
                                int devType=obj3.getInt("devType");
                                if(devType==2) {
                                    grouplist.add(resname);
                                    grouplist1.add(resid);
                                    //     grouplist.add(resname);
                                    List<String> list = new ArrayList<String>();
                                    List<Integer> list1 = new ArrayList<Integer>();
                                    List<Integer> list2 = new ArrayList<Integer>();
                                    getResid(resid, list, list1,list2);
                                    childLeftList.add(list);
                                    childIdList.add(list1);
                                    childId1List.add(list2);

                                }
                            };
                            Adapter();   }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(getActivity(), "网络连接错误", Toast.LENGTH_SHORT).show();
                }
            }
        }){
            @Override
            protected com.android.volley.Response<JSONObject> parseNetworkResponse(NetworkResponse response) {

                try {
                    String jsonString = new String(response.data, "UTF-8");
                    return com.android.volley.Response.success(new JSONObject(jsonString),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return com.android.volley.Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return com.android.volley.Response.error(new ParseError(je));
                }
            }
        };
        jsonObjectRequest.setTag("jsonPost");
        VollyQueue.getHttpQueues().add(jsonObjectRequest);
    }
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

//            public Object getChildright(int groupPosition, int childPosition) {
//                return childRightList.get(groupPosition).get(childPosition);
//            }

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
                LayoutInflater inflater = LayoutInflater.from(getContext().getApplicationContext());
                View ll = inflater.inflate(R.layout.grouplayout,null);
                final TextView group_text = (TextView)ll.findViewById(R.id.group_text);
                TextView group_text1 = (TextView)ll.findViewById(R.id.group_text1);
                ImageView group_img = (ImageView)ll.findViewById(R.id.group_img);
                group_relative = (RelativeLayout)ll.findViewById(R.id.group_relative);
                group_img.setImageResource(R.drawable.devdown);
                group_text.setText(getGroup(groupPosition).toString());
                group_text1.setVisibility(View.GONE);
                if(isExpanded) {
                    group_img.setImageResource(R.drawable.devup);
                }
                group_relative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //  Toast.makeText(ExpandablelistView.this,"niam",Toast.LENGTH_SHORT).show();
                        String groupname = grouplist.get(groupPosition);
//                        tree_textview.setText(groupname);
                        //  System.out.println((ArrayList<String>) childLeftList.get(groupPosition));
                        //根据不同功能需求，让点击子列表后转向不同的activity
//                        Intent intent = new Intent();
//                        intent.putExtra("groupname", groupname);
//                        intent.putStringArrayListExtra("childList", (ArrayList<String>) childLeftList.get(groupPosition));
                    }
                });
                return ll;
            }

            @Override
            public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
                LayoutInflater inflater = LayoutInflater.from(getContext().getApplicationContext());
                View ll1 = inflater.inflate(R.layout.tree_list,null);
                CheckBox left = (CheckBox)ll1.findViewById(R.id.force_checkbox);
                TextView group_name = (TextView)ll1.findViewById(R.id.group_name);
                group_name.setText(getChild(groupPosition, childPosition).toString());
                final Map<String, Object> mapl = new HashMap<String, Object>();
                final List< Map<String, Object>> sss = new ArrayList< Map<String, Object>>();
                mapl.clear();
                sss.clear();
                singleList.clear();
                left.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean boool) {
                        if (boool) {
                            String temp = (String) childLeftList.get(groupPosition).get(childPosition);
                            int iid=(Integer) childIdList.get(groupPosition).get(childPosition);
                            int id=(Integer) childId1List.get(groupPosition).get(childPosition);
                            mapl.put("resId",iid);
                            mapl.put("rId",id);
                            mapl.put("is_checked", true);
                        } else {
                            mapl.put("is_checked", false);
                        }
                        sss.add(mapl);
                        singleList.add(sss);
                    }
                });
                return ll1;
            }

            @Override
            public boolean isChildSelectable(int i, int i1) {
                return true;
            }
        };
        expandablelistView.setAdapter(adapter);
        SharedPreferencesUtils.setParam(getActivity(), "pagerId", 0);
        //子列表点击事件
        expandablelistView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View convertView, int groupPosition, int childPosition, long l) {
                String childname = childLeftList.get(groupPosition).get(childPosition).trim();
                //根据不同功能需求，让点击子列表后转向不同的activity

                return false;
            }
        });
    }
    public void initView(View view) {
        listView = (ListView)view.findViewById(R.id.listtree);
        select=(TextView)view.findViewById(R.id.select);
      //  tree_textview=(TextView)view.findViewById(R.id.tree_textview);
        expandablelistView = (ExpandableListView)view.findViewById(R.id.list);
        btGdet=(Button)view.findViewById(R.id.btGdet);
        btSelect=(Button)view.findViewById(R.id.btSelect);
        btGdet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSelectedSingleRodNum();
            }
        });
        btSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int tertagId= (int)SharedPreferencesUtils.getParam(getActivity(), "tertagId", 0);
                String tertagIds= (String)SharedPreferencesUtils.getParam(getActivity(), "tertagIds", "");
                int outtagId= (int)SharedPreferencesUtils.getParam(getActivity(), "outtagId", 0);
                String outtagIds= (String)SharedPreferencesUtils.getParam(getActivity(), "outtagIds", "");
                int sintagId= (int)SharedPreferencesUtils.getParam(getActivity(), "sintagId", 0);
                String sintagIds= (String)SharedPreferencesUtils.getParam(getActivity(), "sintagIds", "");
                Intent intent = new Intent();
                intent.putExtra("tertagId", tertagId);
                intent.putExtra("tertagIds", tertagIds);
                intent.putExtra("outtagId", outtagId);
                intent.putExtra("outtagIds", outtagIds);
                intent.putExtra("sintagId", sintagId);
                intent.putExtra("sintagIds", sintagIds);
                intent.putExtra("fragid", 0);
                intent.setClass(getActivity(), MainActivity.class);
                startActivity(intent);
                //getSelectedSingleRodNum();
            }
        });
    };
    public String uncoden(String str){
        String rr=null;
        try {
            rr=URLEncoder.encode(str, "utf-8").trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return rr;
    }
    public void getResid(int id, final List<String> list,final List<Integer> list1,final List<Integer> list2) {
        JSONObject jsonStr=new JSONObject();
        JSONObject par=new JSONObject();
        try {
            jsonStr.put("jsonrpc","2.0");
            jsonStr.put("method","getTagByTagGroupId");
            par.put("token",token);
            par.put("tagGroupId",id);
            jsonStr.put("params",par);
            jsonStr.put("id",111);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(com.android.volley.Request.Method.POST,HttpPath
                ,jsonStr,new com.android.volley.Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getActivity(), "设置成功", Toast.LENGTH_SHORT).show();
            }
        },new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String ss=error.toString();
                String str=null;
                int a=ss.length();
                if(a>31) {
                    str = ss.substring(ss.indexOf("["),ss.lastIndexOf("]") + 1);
                    grouplist1.clear();
                    JSONArray jsonObject = null;
                    try {
                        jsonObject = new JSONArray(str);
                        if(str.length()>50) {
                            JSONObject obj1=new JSONObject(jsonObject.getString(0));
                            JSONArray obj2= obj1.getJSONArray("result");
                            for(int i=0;i<obj2.length();i++){
                                JSONObject  obj3=new JSONObject(obj2.getString(i));
                                String resname=obj3.getString("tagGroupName");
                                list.add(resname);
                                int resid=obj3.getInt("tagId");
                                list1.add(resid);
                                int resid1=obj3.getInt("tagGroupId");
                                list2.add(resid1);
                            };

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(getActivity(), "网络连接错误", Toast.LENGTH_SHORT).show();
                }
            }
        }){
            @Override
            protected com.android.volley.Response<JSONObject> parseNetworkResponse(NetworkResponse response) {

                try {
                    String jsonString = new String(response.data, "UTF-8");
                    return com.android.volley.Response.success(new JSONObject(jsonString),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return com.android.volley.Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return com.android.volley.Response.error(new ParseError(je));
                }
            }
        };
        jsonObjectRequest.setTag("jsonPost");
        VollyQueue.getHttpQueues().add(jsonObjectRequest);
    }
    public void getSelectedSingleRodNum() {
        List<String> rodNumList = new ArrayList<String>();
        List<String> rodNumList1 = new ArrayList<String>();
        rodNumList.clear();
        rodNumList1.clear();
        String treIds="";
        int rId=0;
        for(int i = 0; i < singleList.size(); i++) {
            for (int j = 0; j< singleList.get(i).size(); j++) {
                if ((boolean) singleList.get(i).get(j).get("is_checked")) {
                    treIds += singleList.get(i).get(j).get("resId").toString() + "-";
                    rodNumList.add(singleList.get(i).get(j).get("resId").toString());
                    rodNumList1.add(singleList.get(i).get(j).get("rId").toString());
                    rId=Integer.parseInt(singleList.get(i).get(j).get("rId").toString());
                }
            }
        }
        for(int i=0;i<rodNumList1.size();i++){
            for(int j=0;j<rodNumList1.size();j++){
                if(!rodNumList1.get(i).equals(rodNumList1.get(j))){
                    Toast.makeText(getActivity(),"只能选择一个标检",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
        if(rId>0&&treIds.length()>0) {
            SharedPreferencesUtils.setParam(getActivity(), "sintagIds", treIds);
            SharedPreferencesUtils.setParam(getActivity(), "sintagId", rId);
        }else{
            SharedPreferencesUtils.setParam(getActivity(), "sintagIds", "");
            SharedPreferencesUtils.setParam(getActivity(), "sintagId", -1);
        }
        Toast.makeText(getActivity(),"选择成功",Toast.LENGTH_SHORT).show();
    }
}

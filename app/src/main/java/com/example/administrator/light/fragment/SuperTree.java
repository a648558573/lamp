package com.example.administrator.light.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.light.R;
import com.example.administrator.light.fragment.TreeView.TreeNode;
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

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class SuperTree extends BaseExpandableListAdapter {
    static public class SuperTreeNode {
        Object parent;
        //二级树形菜单的结构体
        List<TreeView.TreeNode> childs = new ArrayList<>();
    }
    private List<SuperTreeNode> superTreeNodes = new ArrayList<SuperTreeNode>();
    private Context parentContext;
    private OnChildClickListener stvClickEvent;//外部回调函数
    public SuperTree(Context view, OnChildClickListener stvClickEvent) {
        parentContext = view;
        this.stvClickEvent=stvClickEvent;
    }
    public List<SuperTreeNode> GetTreeNode() {
        return superTreeNodes;
    }
    public void UpdateTreeNode(List<SuperTreeNode> node) {
        superTreeNodes = node;
    }
    public void RemoveAll()
    {
        superTreeNodes.clear();
    }
    public Object getChild(int groupPosition, int childPosition) {
        return superTreeNodes.get(groupPosition).childs.get(childPosition);
    }
    public int getChildrenCount(int groupPosition) {
        return superTreeNodes.get(groupPosition).childs.size();
    }
    public ExpandableListView getExpandableListView() {
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT, com.example.administrator.light.fragment.TreeView.ItemHeight);
        ExpandableListView superTreeView = new ExpandableListView(parentContext);
        superTreeView.setLayoutParams(lp);
        return superTreeView;
    }
    /**
     * 三层树结构中的第二层是一个ExpandableListView
     */
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final ExpandableListView treeView = getExpandableListView();
        final com.example.administrator.light.fragment.TreeView treeViewAdapter = new com.example.administrator.light.fragment.TreeView(this.parentContext,0);
        List<TreeNode> tmp = treeViewAdapter.GetTreeNode();//临时变量取得TreeViewAdapter的TreeNode集合，可为空
        final TreeNode treeNode=(TreeNode) getChild(groupPosition, childPosition);
        tmp.add(treeNode);
        treeViewAdapter.UpdateTreeNode(tmp);
        treeView.setAdapter(treeViewAdapter);
        treeView.setDivider(null);
        treeView.setChildDivider(null);
        //   Toast.makeText(parentContext,childPosition+"",Toast.LENGTH_SHORT).show();
        SharedPreferencesUtils.setParam(parentContext, "oldgroupPosition",groupPosition);
//关键点：取得选中的二级树形菜单的父子节点,结果返回给外部回调函数
        treeView.setOnChildClickListener(this.stvClickEvent);
/**
 * 关键点：第二级菜单展开时通过取得节点数来设置第三级菜单的大小
 */
        treeView.setOnGroupExpandListener(new OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT,
                        (treeNode.childs.size()+1)* com.example.administrator.light.fragment.TreeView.ItemHeight + 10);
                treeView.setLayoutParams(lp);
            }
        });
/**
 * 第二级菜单回收时设置为标准Item大小
 */
        treeView.setOnGroupCollapseListener(new OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                        com.example.administrator.light.fragment.TreeView.ItemHeight);
                treeView.setLayoutParams(lp);
            }
        });
        treeView.setPadding(TreeView.PaddingLeft, 0, 0, 0);
        return treeView;
    }
    /**
     * 三级树结构中的首层是TextView,用于作为title
     */
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) parentContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View ll = inflater.inflate(R.layout.grouplayout,null);
        final TextView group_text = (TextView)ll.findViewById(R.id.group_text);
        TextView group_text1 = (TextView)ll.findViewById(R.id.group_text1);
        ImageView group_img = (ImageView)ll.findViewById(R.id.group_img);
        RelativeLayout group_relative = (RelativeLayout)ll.findViewById(R.id.group_relative);
        group_img.setImageResource(R.drawable.devdown);
        group_text.setText(getGroup(groupPosition).toString());
        if(isExpanded){
            group_img.setImageResource(R.drawable.devup);
        }
        String  onlinelist=(String) SharedPreferencesUtils.getParam(parentContext, "onlinelist", "");
        try {
            JSONArray jsonarray = new JSONArray(onlinelist);
            group_text1.setText(jsonarray.getString(groupPosition));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String  classname=(String) SharedPreferencesUtils.getParam(parentContext, "classname", "");
        group_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                                        String va=superTreeNodes.get(groupPosition).parent.toString();
                                        Class clazz;
                                        try {
                                            clazz = Class.forName(classname);
                                            Intent intent=new Intent(parentContext,clazz);
                                            intent.putExtra("childname", va);
//                                          intent.putExtra("resid", resid);
                                            parentContext.startActivity(intent);
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        // return ll;
//        TextView textView = TreeViewAdapter.getTextView(this.parentContext);
//        textView.setText(getGroup(groupPosition).toString());
//        textView.setPadding(TreeViewAdapter.PaddingLeft, 0, 0, 0);

        return ll;
    }
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
    public Object getGroup(int groupPosition) {
        return superTreeNodes.get(groupPosition).parent;
    }
    public int getGroupCount() {
        return superTreeNodes.size();
    }
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    public boolean hasStableIds() {
        return true;
    }
    private synchronized void getNodeType(String tNodeType, final List<String> left, final List<String> list) {
        JSONObject jsonStr=new JSONObject();
        JSONObject par=new JSONObject();
        String token=(String) SharedPreferencesUtils.getParam(parentContext, "token", "");
        String HttpPath="http://192.168.0.157:45002/api/resTag";
        try {
            jsonStr.put("jsonrpc","2.0");
            jsonStr.put("method","loadMenu");
            par.put("token",token);
            par.put("parentResId",0);
            par.put("resType",1);
            par.put("funModType",0);
            par.put("menuClass",tNodeType);
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
                        Toast.makeText(parentContext, "网络连接错误", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(response.isSuccessful()){//回调的方法执行在子线程。
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
                                        JSONObject  obj3=new JSONObject(obj2.getString(i));
                                        String resname=obj3.getString("resName");
                                        left.add(resname);
                                        String urlclass=obj3.getString("androidUrl");
                                        list.add(urlclass);
                                    };
                                }else {
                                    //rolename.setText("");
                                }
                            } catch (JSONException e) {
                                Looper.prepare();
                                Toast.makeText(parentContext,"网络连接错误", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                                e.printStackTrace();
                            }
                        }
                    }
                });//此处省略回调方法。
    }
}

package com.example.administrator.light.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.light.R;
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

/**
 * Created by binyejiang on 2019/7/13.
 */
public class TreeView extends BaseExpandableListAdapter{
    public static final int ItemHeight=140;//每项的高度
    public static final int PaddingLeft=100;//每项的高度
    private int myPaddingLeft=150;//如果是由SuperTreeView调用，则作为子项需要往右移


    static public class TreeNode{
        Object parent;
        List<Object> childs=new ArrayList<Object>();
    }
    List<TreeNode> treeNodes = new ArrayList<TreeNode>();
    Context parentContext;
    public TreeView(Context view, int myPaddingLeft)
    {
        parentContext=view;
        this.myPaddingLeft=myPaddingLeft;
    }
    public List<TreeNode> GetTreeNode()
    {
        return treeNodes;
    }
    public void UpdateTreeNode(List<TreeNode> nodes)
    {
        treeNodes=nodes;
    }
    public void RemoveAll()
    {
        treeNodes.clear();
    }
    public Object getChild(int groupPosition, int childPosition) {
        return treeNodes.get(groupPosition).childs.get(childPosition);
    }
    public int getChildrenCount(int groupPosition) {
        return treeNodes.get(groupPosition).childs.size();
    }
    static public TextView getTextView(Context context) {
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT, ItemHeight);
        TextView textView = new TextView(context);
        textView.setLayoutParams(lp);
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        return textView;
    }
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) parentContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View ll1 = inflater.inflate(R.layout.childlayout,null);
        TextView left = (TextView)ll1.findViewById(R.id.left);
        TextView right = (TextView)ll1.findViewById(R.id.right);
        ImageView img = (ImageView)ll1.findViewById(R.id.img);
        img.setVisibility(View.GONE);
        left.setVisibility(View.GONE);
        right.setText(getChild(groupPosition, childPosition).toString());
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              int  oldgroupPosition=(int) SharedPreferencesUtils.getParam(parentContext, "oldgroupPosition", -1);
                final String  classname=(String) SharedPreferencesUtils.getParam(parentContext, "classname", "");
                try {
                         String va=getChild(groupPosition, childPosition).toString();
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
        ll1.setPadding(10, 0, 0, 0);
        return ll1;
    }
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) parentContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View ll = inflater.inflate(R.layout.grouplayout,null);
        LinearLayout group = (LinearLayout)ll.findViewById(R.id.group);
        final TextView group_text = (TextView)ll.findViewById(R.id.group_text);
        TextView group_text1 = (TextView)ll.findViewById(R.id.group_text1);
        ImageView group_img = (ImageView)ll.findViewById(R.id.group_img);
        RelativeLayout group_relative = (RelativeLayout)ll.findViewById(R.id.group_relative);
        //  group_img.setImageResource(R.drawable.marquee_add_blue);
        group_text1.setVisibility(View.GONE);
        //  group.setBackgroundResource(R.color.huabai);
        group_text.setText(getGroup(groupPosition).toString());
        //    Toast.makeText(parentContext,getGroup(0).toString(),Toast.LENGTH_SHORT).show();
        final int  oldgroupPosition=(int) SharedPreferencesUtils.getParam(parentContext, "oldgroupPosition", -1);
        final String  classname=(String) SharedPreferencesUtils.getParam(parentContext, "classname", "");
        if(isExpanded) {
            //  group_img.setImageResource(R.drawable.connect_fail);
        }
        group_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //      int  oldgroupPosition=0;
                try {
                    String va=getGroup(groupPosition).toString();
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
        return ll;
//        TextView textView = getTextView(this.parentContext);
//        textView.setText(getGroup(groupPosition).toString());
//        textView.setPadding(myPaddingLeft+(PaddingLeft>>1), 0, 0, 0);
//        return textView;
    }
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
    public Object getGroup(int groupPosition) {
        return treeNodes.get(groupPosition).parent;
    }
    public int getGroupCount() {
        return treeNodes.size();
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
                                        String urlclass=obj3.getString("resId");
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

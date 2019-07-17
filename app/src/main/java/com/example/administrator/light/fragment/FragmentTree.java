package com.example.administrator.light.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
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
import com.example.administrator.light.fragment.SuperTreeViewAdapter;
import com.example.administrator.light.fragment.TreeViewAdapter;
import com.example.administrator.light.Tree.testExpandableList;
import com.example.administrator.light.other.collect.Collection;
import com.example.administrator.light.other.collect.SetConcentrator;
import com.example.administrator.light.other.collect.SetSingle;
import com.example.administrator.light.other.collect.UploadPic;
import com.example.administrator.light.util.SharedPreferencesUtils;
import com.example.administrator.light.Tree.Select_Tree;
import com.example.administrator.light.Tree.NewGroup;
import com.example.administrator.light.conctrol.conctrolMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.example.administrator.light.fragment.FragmentAdapter;

/**
 * Created by byj on 2019/6/1.
 */

public class FragmentTree extends Fragment {
    public ExpandableListView listView,listvview;
    String HttpPath="http://192.168.0.157:45002/api/resTag";
    String HttpPath1="http://192.168.0.157:45002//api/devMon";
    private String token;
    TextView select,allgroup;
    ImageView imageV;
    List<String> grouplist = new ArrayList<String>();
    List<String> onlinelist = new ArrayList<String>();
    List<String> nodeTypelist = new ArrayList<String>();
    List<List<String>> outnodetypelist = new ArrayList<List<String>>();
    List<List<String>> outlist = new ArrayList<List<String>>();
    List<List<List<String>>> simnodetypelist = new ArrayList<List<List<String>>>();
    List<List<List<String>>> simlist = new ArrayList<List<List<String>>>();

    List<List<String>> groupNodeTypeList  = new ArrayList<List<String>>();
    List<List<String>> childLeftList = new ArrayList<List<String>>();
    List<List<String>> groupUrlList = new ArrayList<List<String>>();
    List<List<List<String>>> outNodeTypelist = new ArrayList<List<List<String>>>();
    List<List<List<String>>> outUrllist = new ArrayList<List<List<String>>>();
    List<List<List<List<String>>>> simNodeTypelist = new ArrayList<List<List<List<String>>>>();
    List<List<List<List<String>>>> simUrllist = new ArrayList<List<List<List<String>>>>();
    List<Integer> idlist = new ArrayList<Integer>();
    int tertagId=0;
    String tertagIds=null;
    int outtagId=0;
    String outtagIds=null;
    int sintagId=0;
    String sintagIds=null;
    Button select1;
    Boolean isShow=false;
    Boolean isShow1=false;
    private ListView addViewPager;
    String[] mmm={"新增终端","新增输出","新增单灯"};
    ArrayAdapter<String> parentAdapter, childAdapter;
    private FragmentSao addFg = null;
    ExpandableListAdapter singleAdapter;
    SuperTreeViewAdapter superAdapter;
    String grout,out, smil;
    //TreeViewAdapter adapter;
    // Sample data set. children[i] contains the children (String[]) for groups[i].
    ProgressDialog dialog = null;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what==1){
                selAdapter();
            }
        }
    };
    @Override
    public synchronized View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.fragment_tree, container, false);
        initView(view);

        new Thread(new Runnable() {
                    @Override
                     public void run() {
                        // tvMessage.setText("...");
                       // 以上操作会报错，无法再子线程中访问UI组件，UI组件的属性必须在UI线程中访问
                       // 使用post方式修改UI组件tvMessage的Text属性
                        seelist(view);
                    }
                }).start();
   //     dialog = ProgressDialog.show(getActivity(),null,"加载中，请稍后..");
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//               dialog.dismiss();
//         // Adapter();
//          selAdapter();
//            }
//        },7000);//3秒后执行Runnable中的run方法
        return view;
    }

    private synchronized void selAdapter() {
        superAdapter=new SuperTreeViewAdapter(getActivity(),stvClickEvent);
        superAdapter.notifyDataSetChanged();
        List<SuperTreeViewAdapter.SuperTreeNode> superTreeNode = superAdapter.GetTreeNode();
        for(int i=0;i<grouplist.size();i++)//第一层
        {
            SuperTreeViewAdapter.SuperTreeNode superNode=new SuperTreeViewAdapter.SuperTreeNode();
            superNode.parent=grouplist.get(i);
//第二层
            for(int ii=0;ii<outlist.get(i).size();ii++)
            {
                TreeViewAdapter.TreeNode node=new TreeViewAdapter.TreeNode();
                node.parent=outlist.get(i).get(ii);//第二级菜单的标题
                for(int iii=0;iii<simlist.get(i).get(ii).size();iii++)//第三级菜单
                {
                    node.childs.add(simlist.get(i).get(ii).get(iii));
                }
                superNode.childs.add(node);
            }
            superTreeNode.add(superNode);
        }
        superAdapter.UpdateTreeNode(superTreeNode);
        listvview.setChildDivider(null);
        listvview.setAdapter(superAdapter);
    }

    private void seelist(final View view) {
        String mode = "default";
        String[] a={"-1"};
        String[] b={"-1"};
        String[] c={"-1"};
        int al=-1,bl=-1,cl=-1;
        if(tertagId>0){
            mode="custom";
            al=tertagId;
            a=tertagIds.split("-");
        }
        int in[] = new int[a.length];
// 对String数组进行遍历循环，并转换成int类型的数组
        for (int i = 0; i < a.length; i++) {
            in[i] = Integer.parseInt(a[i]);
        }
        JSONObject jsonStr=new JSONObject();
        JSONObject par=new JSONObject();
        JSONArray jsonArray1 = new JSONArray();
        for(int i = 0 ; i < in.length ;i++) {  //依次将数组元素添加进JSONArray对象中
            jsonArray1.put(in[i]);
        }
        try {
            jsonStr.put("jsonrpc","2.0");
            jsonStr.put("method","loadTreeNode");
            par.put("token",token);
            par.put("mode",mode);
            par.put("currentDeviceType",-1);
            par.put("currentNodeType","root");
            par.put("resId",-1);
            par.put("terminalTagGroupId",tertagId);
            par.put("terminalTagId",-1);
            par.put("terminalTagIds",jsonArray1);
            par.put("outputTagGroupId",-1);
            par.put("outputTagId",-1);
            par.put("outputTagIds",-1);
            par.put("singleLampTagGroupId",-1);
            par.put("singleLampTagId",-1);
            par.put("singleLampTagIds",-1);

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
                        Toast.makeText(getActivity(), "网络连接错误", Toast.LENGTH_SHORT).show();
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
                                    grouplist.clear();
                                    idlist.clear();
                                    onlinelist.clear();
                                    groupNodeTypeList.clear();
                                    groupUrlList.clear();
                                    outlist.clear();
                                    simlist.clear();
                                        for(int i=0;i<obj2.length();i++){
                                            List<String> left = new ArrayList<String>();
                                            List<String> list = new ArrayList<String>();
                                            List<String> shuchulist = new ArrayList<String>();
                                            List<String> outtypelist = new ArrayList<String>();
                                            List<List<String>> simtypelist = new ArrayList<List<String>>();
                                            List<List<String>> dandenglist = new ArrayList<List<String>>();
                                            List<List<String>> groupmenulist = new ArrayList<List<String>>();
                                            List<List<String>> groupurllist = new ArrayList<List<String>>();
                                            List<List<List<String>>> outmenulist = new ArrayList<List<List<String>>>();
                                            List<List<List<String>>> outurllist = new ArrayList<List<List<String>>>();
                                            JSONObject  obj3=new JSONObject(obj2.getString(i));
                                            String resname=obj3.getString("resName");
                                            int resid=obj3.getInt("resId");
                                            String nodeType=obj3.getString("currentNodeType");
                                            grouplist.add(resname);
                                            idlist.add(resid);
                                            nodeTypelist.add(nodeType);
                                            outList(resid,shuchulist,dandenglist,outtypelist,simtypelist,outmenulist,outurllist);
                                            outlist.add(shuchulist);
                                            simlist.add(dandenglist);
                                            outnodetypelist.add(outtypelist);
                                            simnodetypelist.add(simtypelist);
                                            String devid=obj3.getString("devId");
                                            getOnline(devid);
//                                            outNodeTypelist.add(groupmenulist);
//                                            outUrllist.add(groupurllist);
//                                            simNodeTypelist.add(outmenulist);
//                                            simUrllist.add(outurllist);
                                            getNodeType(nodeType,left,list);
                                            groupNodeTypeList.add(left);
                                            groupUrlList.add(list);
                                        };
                                    TimerTask task = new TimerTask() {
                                        @Override
                                        public void run() {

                                            SharedPreferencesUtils.setParam(getActivity(), "nodeTypelist",nodeTypelist.toString() );
                                            SharedPreferencesUtils.setParam(getActivity(), "outnodetypelist",outnodetypelist.toString() );
                                            SharedPreferencesUtils.setParam(getActivity(), "simnodetypelist",simnodetypelist.toString() );
                                            Message msg = new Message();
                                            msg.what = 1;
                                            handler.sendMessage(msg);
                                        }
                                    };
                                    Timer timer = new Timer();
                                    timer.schedule(task, 300);
                                     //execute the task

                                }
                            } catch (JSONException e) {
                                Looper.prepare();
                                Toast.makeText(getActivity(), "网络连接错误", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                                e.printStackTrace();
                            }
                        }
                    }
                });//此处省略回调方法。
    }

    private void getOnline(String devid) {
        JSONObject jsonStr=new JSONObject();
        JSONObject par=new JSONObject();
        try {
            jsonStr.put("jsonrpc","2.0");
            jsonStr.put("method","getTrmlOnlineStatus");
            par.put("token",token);
            par.put("devId","0100000000000003eb");

            jsonStr.put("params",par);
            jsonStr.put("id",111);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，
        RequestBody body = RequestBody.create(JSON, jsonStr.toString());
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(HttpPath1)
                .post(body)
                .build();
        client.newCall(request).enqueue(
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Looper.prepare();
                        Toast.makeText(getActivity(), "网络连接错误", Toast.LENGTH_SHORT).show();
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
                                if(str.length()>20) {
                                    JSONObject obj1=new JSONObject(obj.getString(0));
                                    String obj2= obj1.getString("result");
                                    int i=Integer.parseInt(obj2);
                                    if(i==0){
                                        onlinelist.add("离线");
                                    }
                                    if(i==1){
                                        onlinelist.add("在线");
                                    }
                                    SharedPreferencesUtils.setParam(getActivity(), "onlinelist",onlinelist.toString() );
                                }else {
                                    //rolename.setText("");
                                }
                            } catch (JSONException e) {
                                Looper.prepare();
                                Toast.makeText(getActivity(),"网络连接错误", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                                e.printStackTrace();
                            }
                        }
                    }
                });//此处省略回调方法。
    }

    public void Adapter() {
         singleAdapter = new BaseExpandableListAdapter() {
            private  LayoutInflater mInflater;
            @Override
            public int getGroupCount() {
                return grouplist.size();
            }

            @Override
            public int getChildrenCount(int groupPosition) {
                return groupPosition;
            }

            @Override
            public Object getGroup(int groupPosition) {
                return grouplist.get(groupPosition);
            }

            @Override
            public Object getChild(int groupPosition, int childPosition) {
                System.out.println(groupPosition);
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
            public View getGroupView(final int groupPosition,boolean isExpanded,View view, ViewGroup viewGroup) {
                mInflater = LayoutInflater.from(getContext().getApplicationContext());
                view= mInflater.inflate(R.layout.grouplayout, null);
                TextView group_text = (TextView)view.findViewById(R.id.group_text);
                TextView group_text1 = (TextView)view.findViewById(R.id.group_text1);
                ImageView group_img = (ImageView)view.findViewById(R.id.group_img);
                RelativeLayout group_relative = (RelativeLayout)view.findViewById(R.id.group_relative);
                RelativeLayout group_muni = (RelativeLayout)view.findViewById(R.id.group_muni);
                group_img.setImageResource(R.drawable.devdown);
                group_text.setText(getGroup(groupPosition).toString());

                group_text1.setText(onlinelist.get(groupPosition).toString());

                group_muni.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //  Toast.makeText(ExpandablelistView.this,"niam",Toast.LENGTH_SHORT).show();
                        String groupname = grouplist.get(groupPosition);
                        int resid=idlist.get(groupPosition);
                        //根据不同功能需求，让点击子列表后转向不同的activity
                        Intent intent = new Intent();
                        intent.putExtra("groupname", groupname);
                        intent.putExtra("resid", resid);
                        intent.putExtra("outtagId", outtagId);
                        intent.putExtra("outtagId", outtagIds);
                        intent.putExtra("sintagId", sintagId);
                        intent.putExtra("sintagIds", sintagIds);
                        intent.setClass(getActivity(), ExpandablelistViewput.class);
                        startActivity(intent);
                    }
                });
                group_relative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        List tNodeType = groupNodeTypeList.get(groupPosition);
                        final String[] array= (String[]) tNodeType.toArray(new String[tNodeType.size()]);
                        android.support.v7.app.AlertDialog.Builder builder1=new android.support.v7.app.AlertDialog.Builder(getContext());
                        builder1.setTitle("选择你要的操作");
                        builder1.setItems(array, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String groupname = grouplist.get(groupPosition);
                                int resid=idlist.get(groupPosition);
                                String  nameclass =groupUrlList.get(groupPosition).get(which);
                                Toast.makeText(getActivity(),nameclass, Toast.LENGTH_SHORT).show();
                                Class clazz= null;
                                try {
                                    clazz = Class.forName(nameclass);
                                    Intent intent=new Intent(getActivity(),clazz);
                                    intent.putExtra("groupname", groupname);
                                    intent.putExtra("resid", resid);
                                    startActivity(intent);
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        builder1.show();
                    }
//获取控制功能
                });
                return view;
            }

            @Override
            public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
                return null;
            }

            @Override
            public boolean isChildSelectable(int i, int i1) {
                return false;
            }
             Handler handler = new Handler(){
                 @Override
                 public void handleMessage(Message msg) {
                     notifyDataSetChanged();//更新数据
                     super.handleMessage(msg);
                 }
             };
        };
        listView.setAdapter(singleAdapter);
    }
    public void initView(View view) {
        token=(String) SharedPreferencesUtils.getParam(getContext(), "token", "");
        if(token==null||token==""){
            Toast.makeText(getActivity(),"记录过期请重新登录",Toast.LENGTH_SHORT).show();
            return;
        }
        String username=(String) SharedPreferencesUtils.getParam(getContext(), "username", "");
        listView = (ExpandableListView)view.findViewById(R.id.listtree);
        listvview = (ExpandableListView)view.findViewById(R.id.listtree);
        select=(TextView)view.findViewById(R.id.select);
        select1=(Button) view.findViewById(R.id.select1);
        addViewPager=(ListView) view.findViewById(R.id.addViewPaper);
        allgroup=(TextView)view.findViewById(R.id.allgroup);
        imageV=(ImageView)view.findViewById(R.id.imageV);
        //获取搜索条件
        tertagId=this.getActivity().getIntent().getIntExtra("tertagId",-1);
        tertagIds=this.getActivity().getIntent().getStringExtra("tertagIds");
        outtagId=this.getActivity().getIntent().getIntExtra("outtagId",-1);
        outtagIds=this.getActivity().getIntent().getStringExtra("outtagIds");
        sintagId=this.getActivity().getIntent().getIntExtra("sintagId",-1);
        sintagIds=this.getActivity().getIntent().getStringExtra("sintagIds");
        select.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View view) {
               Intent intent = new Intent();
               intent.setClass(getActivity(), Select_Tree.class);
                startActivity(intent);
           }
        });
        allgroup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (isShow1) {
                    listView.setVisibility(View.VISIBLE);
                    imageV.setImageResource(R.drawable.ic_arrow1);
                    isShow1 = false;
                }else {
                    listView.setVisibility(View.GONE);
                    imageV.setImageResource(R.drawable.ic_arrow);
                    isShow1 = true;
                }
            }
        });
        //新增设备
        select1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShow) {
                    addViewPager.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    isShow = false;
                } else {
                        listView.setVisibility(View.GONE);
                        addViewPager.setVisibility(View.VISIBLE);
                        parentAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, mmm);
                        parentAdapter.notifyDataSetChanged();
                        addViewPager.setAdapter(parentAdapter);
                        addViewPager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            switch (i) {
                                case 0:
                                    Intent intent1 = new Intent();
                                    intent1.putExtra("newgroup", "group");
                                    intent1.setClass(getActivity(), NewGroup.class);
                                    startActivity(intent1);
                                    break;
                                case 1:
                                    Intent intent2 = new Intent();
                                    intent2.putExtra("newgroup", "output");
                                    intent2.setClass(getActivity(), NewGroup.class);
                                    startActivity(intent2);
                                    break;
                                case 2:
                                    Intent intent3 = new Intent();
                                    intent3.putExtra("newgroup", "sinlam");
                                    intent3.setClass(getActivity(), NewGroup.class);
                                    startActivity(intent3);
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                    isShow = true;
                }
            }
        });
    }
    public synchronized void outList(int id, final List<String> shuchulist, final List<List<String>> dandenglist, final List<String> outtypelist,
                        final List<List<String>> simtypelist,
                        final List<List<List<String>>> outmenulist, final List<List<List<String>>> outurllist) {
        String mode = "default";
        String[] a = {"-1"};
        String[] b = {"-1"};
        String[] c = {"-1"};
        int al = -1, bl = -1, cl = -1;
        if (outtagId > 0) {
            mode = "custom";
            bl = outtagId;
            b = outtagIds.split("-");
        }
        int inb[] = new int[b.length];
// 对String数组进行遍历循环，并转换成int类型的数组
        for (int i = 0; i < b.length; i++) {
            inb[i] = Integer.parseInt(b[i]);
        }
        JSONArray jsonArray2 = new JSONArray();
        for (int i = 0; i < inb.length; i++) {  //依次将数组元素添加进JSONArray对象中
            jsonArray2.put(inb[i]);
        }
        JSONObject jsonStr = new JSONObject();
        JSONObject par = new JSONObject();
        try {
            jsonStr.put("jsonrpc", "2.0");
            jsonStr.put("method", "loadTreeNode");
            par.put("token", token);
            par.put("mode", mode);
            par.put("currentDeviceType", 0);
            par.put("currentNodeType", "terminalName");
            par.put("resId",id);
            par.put("terminalTagGroupId", -1);
            par.put("terminalTagId", -1);
            par.put("terminalTagIds", -1);
            par.put("outputTagGroupId", bl);
            par.put("outputTagId", -1);
            par.put("outputTagIds", jsonArray2);
            par.put("singleLampTagGroupId", -1);
            par.put("singleLampTagId", -1);
            par.put("singleLampTagIds", -1);

            jsonStr.put("params", par);
            jsonStr.put("id", 111);
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
                        Toast.makeText(getActivity(), "网络连接错误", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {//回调的方法执行在子线程。
                            String ss = response.body().string();
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
                                        for(int i=0;i<obj2.length();i++){
                                            List<String> list=new ArrayList<String>();
                                            List<String> left = new ArrayList<String>();
                                            List<String> list1=new ArrayList<String>();
                                            List<String> simnodelist=new ArrayList<String>();
                                            List<List<String>> currentNodeTypelist1 = new ArrayList<List<String>>();
                                            List<List<String>> androidUrllist1 = new ArrayList<List<String>>();
                                            JSONObject  obj3=new JSONObject(obj2.getString(i));
                                            String resname=obj3.getString("resName");
                                            shuchulist.add(resname);
                                            int resid=obj3.getInt("resId");
                                            getResid(resid,list,simnodelist,androidUrllist1);
                                            dandenglist.add(list);
                                            String nodeType=obj3.getString("currentNodeType");
                                            idlist.add(resid);
                                        //    getNodeType(nodeType,left,list1);
                                            outtypelist.add(nodeType);
                                            simtypelist.add(simnodelist);
                                        };
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                Toast.makeText(getActivity(), "网络连接错误", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
    public synchronized void getResid(int id, final List<String> list,final List<String> currentNodeTypelist1,
                         final List<List<String>> androidUrllist1) {
        String mode = "default";
        String[] a = {"-1"};
        String[] b = {"-1"};
        String[] c = {"-1"};
        int al = -1, bl = -1, cl = -1;
        if (sintagId > 0) {
            mode = "custom";
            cl = sintagId;
            c = sintagIds.split("-");
        }
        int inc[] = new int[c.length];
// 对String数组进行遍历循环，并转换成int类型的数组
        for (int i = 0; i < c.length; i++) {
            inc[i] = Integer.parseInt(c[i]);
        }
        JSONArray jsonArray3 = new JSONArray();
        for (int i = 0; i < inc.length; i++) {  //依次将数组元素添加进JSONArray对象中
            jsonArray3.put(inc[i]);
        }
        JSONObject jsonStr1 = new JSONObject();
        JSONObject par1 = new JSONObject();
        try {
            jsonStr1.put("jsonrpc", "2.0");
            jsonStr1.put("method", "loadTreeNode");
            par1.put("token", token);
            par1.put("mode", mode);
            par1.put("currentDeviceType", 1);
            par1.put("currentNodeType", "outPutName");
            par1.put("resId", id);
            par1.put("terminalTagGroupId", -1);
            par1.put("terminalTagId", -1);
            par1.put("terminalTagIds", -1);
            par1.put("outputTagGroupId", -1);
            par1.put("outputTagId", -1);
            par1.put("outputTagIds", -1);
            par1.put("singleLampTagGroupId", cl);
            par1.put("singleLampTagId", -1);
            par1.put("singleLampTagIds", jsonArray3);

            jsonStr1.put("params", par1);
            jsonStr1.put("id", 111);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，
        RequestBody body = RequestBody.create(JSON, jsonStr1.toString());
        Request request = new Request.Builder()
                .url(HttpPath)
                .post(body)
                .build();
        client.newCall(request).enqueue(
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Looper.prepare();
                        Toast.makeText(getActivity(), "网络连接错误", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {//回调的方法执行在子线程。

                            String ss = response.body().string();
                            String str = null;
                            int a = ss.length();
                            if (a > 31) {
                                str = ss.substring(ss.indexOf("["), ss.lastIndexOf("]") + 1);
                                JSONArray jsonObject = null;
                                try {
                                    jsonObject = new JSONArray(str);
                                    if (str.length() > 50) {
                                        JSONObject obj1 = new JSONObject(jsonObject.getString(0));
                                        JSONArray obj2 = obj1.getJSONArray("result");
                                        for (int i = 0; i < obj2.length(); i++) {
                                            List<String> list2 = new ArrayList<String>();
                                            List<String> left2 = new ArrayList<String>();
                                            JSONObject obj3 = new JSONObject(obj2.getString(i));
                                            String nodeType = obj3.getString("currentNodeType");
                                            String resname = obj3.getString("resName");
                                            list.add(resname);
                                            int resid = obj3.getInt("resId");
                                          //  getNodeType(nodeType, left2, list2);
                                            currentNodeTypelist1.add(nodeType);
                                        };
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(getActivity(), "网络连接错误", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
    /**
     * 三级树形菜单的事件不再可用，本函数由三级树形菜单的子项（二级菜单）进行回调
     */
    ExpandableListView.OnChildClickListener stvClickEvent=new ExpandableListView.OnChildClickListener(){
        @Override
        public boolean onChildClick(ExpandableListView parent,
                                    View v, int group1Position, int childPosition,
                                    long id) {
         //   Toast.makeText(getActivity(),group1Position+":"+childPosition+":"+id,Toast.LENGTH_SHORT).show();
            return false;
        }
    };
    public synchronized void getNodeType(String tNodeType, final List<String> left, final List<String> list1) {
        JSONObject jsonStr=new JSONObject();
        JSONObject par=new JSONObject();
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
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(HttpPath)
                .post(body)
                .build();
        client.newCall(request).enqueue(
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Looper.prepare();
                        Toast.makeText(getActivity(), "网络连接错误", Toast.LENGTH_SHORT).show();
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
                                    JSONArray obj2= obj1.getJSONArray("result");
                                    //  menu.clear();
                                    for(int i=0;i<obj2.length();i++){
                                        JSONObject  obj3=new JSONObject(obj2.getString(i));
                                        String resname=obj3.getString("resName");
                                        left.add(resname);
                                        String urlclass=obj3.getString("resId");
                                        list1.add(urlclass);
                                    };
                                }else {
                                    //rolename.setText("");
                                }
                            } catch (JSONException e) {
                                Looper.prepare();
                                Toast.makeText(getActivity(),"网络连接错误", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                                e.printStackTrace();
                            }
                        }
                    }
                });//此处省略回调方法。
    }
}

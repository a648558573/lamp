package com.example.administrator.light.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.light.R;
import com.example.administrator.light.util.SharedPreferencesUtils;
import com.example.administrator.light.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

/**
 * Created by byj on 2019/6/1.
 */

public class ActivityTree extends BaseActivity {
    public ExpandableListView listView,listvview;
    String HttpPath="http://192.168.0.157:45002/api/resTag";
    String HttpPath1="http://192.168.0.157:45002//api/devMon";
    private String token;
    TextView select,allgroup;
    ImageView imageV;
    List<String> grouplist = new ArrayList<String>();
    List<String> onlinelist = new ArrayList<String>();
    List<String> nodeTypelist = new ArrayList<String>();
    List<List<String>> outlist = new ArrayList<List<String>>();
    List<List<List<String>>> simnodetypelist = new ArrayList<List<List<String>>>();
    List<List<List<String>>> simlist = new ArrayList<List<List<String>>>();

    List<List<String>> groupNodeTypeList  = new ArrayList<List<String>>();
    List<List<String>> childLeftList = new ArrayList<List<String>>();
    List<List<String>> groupUrlList = new ArrayList<List<String>>();
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
    String classname;
    Button select1;
    Boolean isShow=false;
    Boolean isShow1=false;
    private ListView addViewPager;
    private ImageView imageView2;
    String[] mmm={"新增终端","新增输出","新增单灯"};
    ArrayAdapter<String> parentAdapter, childAdapter;
    private FragmentSao addFg = null;
    ExpandableListAdapter singleAdapter;
    SuperTree superAdapter;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tree);
        initView();
        seelist();
        dialog = ProgressDialog.show(ActivityTree.this,null,"加载中，请稍后..");
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                dialog.dismiss();
//                // Adapter();
//                selAdapter();
//            }
//        },7000);//3秒后执行Runnable中的run方法
    }

    private synchronized void selAdapter() {
        superAdapter=new SuperTree(ActivityTree.this,stvClickEvent);
        listvview = (ExpandableListView)findViewById(R.id.listtree);
        List<SuperTree.SuperTreeNode> superTreeNode = superAdapter.GetTreeNode();
        for(int i=0;i<grouplist.size();i++)//第一层
        {
            SuperTree.SuperTreeNode superNode=new SuperTree.SuperTreeNode();
            superNode.parent=grouplist.get(i);
//第二层
            for(int ii=0;ii<outlist.get(i).size();ii++)
            {
                TreeView.TreeNode node=new TreeView.TreeNode();
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

    private void seelist() {
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
                        Toast.makeText(ActivityTree.this, "网络连接错误", Toast.LENGTH_SHORT).show();
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
                                        List<String> shuchulist = new ArrayList<String>();
                                        List<List<String>> dandenglist = new ArrayList<List<String>>();
                                        JSONObject  obj3=new JSONObject(obj2.getString(i));
                                        String resname=obj3.getString("resName");
                                        int resid=obj3.getInt("resId");
                                        grouplist.add(resname);
                                        idlist.add(resid);
                                        outList(resid,shuchulist,dandenglist);
                                        outlist.add(shuchulist);
                                        simlist.add(dandenglist);
                                        String devid=obj3.getString("devId");
                                        getOnline(devid);
                                    };
                                    TimerTask task = new TimerTask() {
                                        @Override
                                        public void run() {
                                            SharedPreferencesUtils.setParam(ActivityTree.this, "classname",classname.toString() );
                                            dialog.dismiss();
                                            Message msg = new Message();
                                            msg.what = 1;
                                            handler.sendMessage(msg);
                                        }
                                    };
                                    Timer timer = new Timer();
                                    timer.schedule(task,3000);
                                    //execute the task


                                }
                            } catch (JSONException e) {
                                Looper.prepare();
                                Toast.makeText(ActivityTree.this, "网络连接错误", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ActivityTree.this, "网络连接错误", Toast.LENGTH_SHORT).show();
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
                                    SharedPreferencesUtils.setParam(ActivityTree.this, "onlinelist",onlinelist.toString() );
                                }else {
                                    //rolename.setText("");
                                }
                            } catch (JSONException e) {
                                Looper.prepare();
                                Toast.makeText(ActivityTree.this,"网络连接错误", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                                e.printStackTrace();
                            }
                        }
                    }
                });//此处省略回调方法。
    }
    public void initView() {
        token=(String) SharedPreferencesUtils.getParam(ActivityTree.this, "token", "");
        String username=(String) SharedPreferencesUtils.getParam(ActivityTree.this, "username", "");
        listView = (ExpandableListView)findViewById(R.id.listtree);
        select=(TextView)findViewById(R.id.select);
        select1=(Button)findViewById(R.id.select1);
        addViewPager=(ListView)findViewById(R.id.addViewPaper);
        imageView2=(ImageView)findViewById(R.id.imageView2);
        allgroup=(TextView)findViewById(R.id.allgroup);
        imageV=(ImageView)findViewById(R.id.imageV);
        getSearchView().setVisibility(View.GONE);
        getToolbarTitle().setText("设备选择");
        getMune().setVisibility(View.GONE);
        //获取搜索条件
        classname=this.getIntent().getStringExtra("classname");
        select.setVisibility(View.GONE);
        select1.setVisibility(View.GONE);
        imageView2.setVisibility(View.GONE);
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
    }
    public synchronized void outList(int id, final List<String> shuchulist, final List<List<String>> dandenglist) {
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
            par.put("resId", 17);
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
                        Toast.makeText(ActivityTree.this, "网络连接错误", Toast.LENGTH_SHORT).show();
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
                                            List<List<String>> androidUrllist1 = new ArrayList<List<String>>();
                                            JSONObject  obj3=new JSONObject(obj2.getString(i));
                                            String resname=obj3.getString("resName");
                                            shuchulist.add(resname);
                                            int resid=obj3.getInt("resId");
                                            getResid(resid,list);
                                            dandenglist.add(list);
                                            idlist.add(resid);
                                        };
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                Toast.makeText(ActivityTree.this, "网络连接错误", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
    public synchronized void getResid(int id, final List<String> list) {
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
            par1.put("resId", 23);
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
                        Toast.makeText(ActivityTree.this, "网络连接错误", Toast.LENGTH_SHORT).show();
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
                                            JSONObject obj3 = new JSONObject(obj2.getString(i));
                                            String resname = obj3.getString("resName");
                                            list.add(resname);
                                            int resid = obj3.getInt("resId");
                                        }
                                        ;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(ActivityTree.this, "网络连接错误", Toast.LENGTH_SHORT).show();
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
          //  Toast.makeText(ActivityTree.this,group1Position+":"+childPosition+":"+id,Toast.LENGTH_SHORT).show();
            return false;
        }
    };
}

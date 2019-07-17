package com.example.administrator.light.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.example.administrator.light.Info.ConInfo;
import com.example.administrator.light.Info.SingleLightInfo;
import com.example.administrator.light.MainActivity;
import com.example.administrator.light.R;
import com.example.administrator.light.VollyQueue;
import com.example.administrator.light.system.CentralizingSwitch;
import com.example.administrator.light.system.ElectricalParameter;
import com.example.administrator.light.system.SingleTimeSetting;
import com.example.administrator.light.system.Single_Dimming_TimeCtrl.SingleActivity;
import com.example.administrator.light.system.TimeControl;
import com.example.administrator.light.util.SharedPreferencesUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lxr on 2016/4/21.
 */

/**
 * Created by csh on 2016/7/21.
 */
public class FragmentMap extends Fragment {

    private String rootURL = null, account = null, password = null;

    private LinearLayout bottom1, bottom2;
    private Button btSetRadius, btLocation, btGetCenter, btDetail, btCtrl, btNavigation;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LayoutInflater inflater ;

    private LocationClient mLocationClient = null;
    private MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;

    private Marker centerMarker;
    private LatLng current, mapCenter;

    private Marker remove_marker = null;  // 放大后的图标
    private Gson gson = new Gson();  //用GSON库解析json数据
    private ConInfo conInfo;
    private SingleLightInfo singleLightInfo;
    private List<String> DevName,DevNo,DevXDevY ;
    private int num = 4; // 显示附近集中器的数目 默认为4
    private Spinner chooseNum,chooseTimeInterval;
    View view;
    private Integer[] nums = {4,5,6,7,8,9,10};
    private String[] TimeInterval = {"停止自动定位","10s","15s","30s","60s","90s","120s"};
    private ArrayAdapter adapter1,adapter2 ;

    private  List<Map<String,Object>> DevList = new ArrayList<Map<String,Object>>(); //存放集中器和单灯

    private  int ConcentratorOrLight = 0;//判断地图点击的是集中器还是单灯 1为集中器 2为单灯 默认值为0
    private String title_Marker; //地图覆盖物的title
    LocationClientOption option;
    MyLocationListener  mylocationlistener;
    private String timeinterval;
    private boolean ifAutoLocaion = false;
    private boolean ifFirstLocation = true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SDKInitializer.initialize(getActivity().getApplicationContext());
        System.out.println("startMapFragment");
        view = inflater.inflate(R.layout.fragment_map, container, false);
        this.inflater =  inflater;
        //permisson for 6.0
        initView(view);
        initLocation();
        initListener();
        btLocation.callOnClick(); //一进入直接调用点击当前位置获取 避免了一进入就显示北京 而是显示当前位
        return view;
    }


    //初始化界面
    private void initView(View view) {
        rootURL = (String) SharedPreferencesUtils.getParam(getContext(), "rootURL", "");
        account = (String) SharedPreferencesUtils.getParam(getContext(), "username", "");
        password = (String) SharedPreferencesUtils.getParam(getContext(), "password", "");

        bottom1 = (LinearLayout)view.findViewById(R.id.bottom1);
        bottom2 = (LinearLayout)view.findViewById(R.id.bottom2);
        btSetRadius = (Button)view.findViewById(R.id.btSetRadius);
        btLocation = (Button)view.findViewById(R.id.btLocation);
        btGetCenter = (Button)view.findViewById(R.id.btGetCenter);
        btDetail = (Button)view.findViewById(R.id.map_bottom_bu1);
        btCtrl = (Button)view.findViewById(R.id.map_bottom_bu2);
        btNavigation = (Button)view.findViewById(R.id.map_bottom_bu3);

        chooseNum = (Spinner)view.findViewById(R.id.chooseNum);
        chooseTimeInterval = (Spinner)view.findViewById(R.id.chooseTimeInterval);
        adapter1 = new ArrayAdapter<Integer>(getContext(),android.R.layout.simple_spinner_item,nums);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseNum.setAdapter(adapter1);
        adapter2 = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,TimeInterval);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseTimeInterval.setAdapter(adapter2);

        mMapView = (MapView)view.findViewById(R.id.fragment_map_mapview);
        //TmMapView  = (TextureMapView)view.findViewById(R.id.fragment_map_mapview);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(13).build()));
        mLocationClient = new LocationClient(getContext());     //声明LocationClient类

    }

    //监听事件
    private void initListener() {
        btSetRadius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText et = new EditText(getContext());
                et.setText(btSetRadius.getText().toString().split("：")[1].trim());
                et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                et.setBackgroundResource(android.R.drawable.editbox_background_normal);
                final EditText et1 = new EditText(getContext());
                et1.setText(btSetRadius.getText().toString().split("：")[1].trim());
                et1.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                et1.setBackgroundResource(android.R.drawable.editbox_background_normal);
                new AlertDialog.Builder(getContext()).setTitle("设置半径")
                        .setIcon(R.drawable.setting)
                        .setView(et)
                        .setView(et1)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String input = et.getText().toString();
                                if (input.equals("")) {
                                    Toast.makeText(getContext(), "请输入半径！" + input, Toast.LENGTH_LONG).show();
                                } else {
                                    btSetRadius.setText("半径 km：" + input);
                                }
                                if(!mLocationClient.isStarted()) {
                                    mLocationClient.start();
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });

        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                mapCenter = mBaiduMap.getMapStatus().target;
            }
        });

        btLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //注释掉 避免重复获取 2016-8-10 chish改
                //mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().target(current).zoom(13).build()));
                //volley(current, btSetRadius.getText().toString().split("：")[1].trim());

                //2016-8-9注，梁工改
                if(mLocationClient.isStarted() == true) {
                    mLocationClient.stop();
                }
                if (mLocationClient != null) {
                    mLocationClient.start();
                }else {
                    Toast.makeText(getActivity(), "LocationClient is null !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btGetCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("center_latitude:" + mapCenter.latitude);
                System.out.println("center_longitude:" + mapCenter.longitude);
                volley(mapCenter, btSetRadius.getText().toString().split("：")[1].trim());
                LatLng centerPoint = new LatLng(mapCenter.latitude, mapCenter.longitude);
                AddCenterMarker(centerPoint);
            }
        });

        chooseNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                num = nums[pos];
                System.out.println("num -->" + num);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
        chooseTimeInterval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                mLocationClient.stop();
                if(!ifFirstLocation) {  //第一次定位时候这里不重复定位请求
                    timeinterval = TimeInterval[pos];
                    if(timeinterval.equals("停止自动定位")) {
                        option.setScanSpan(0);
                        mLocationClient.setLocOption(option);
                        ifAutoLocaion = false;
                    }else {
                        int timeTemp = Integer.parseInt(timeinterval.replace("s",""));
                        option.setScanSpan(timeTemp*1000);
                        System.out.println(timeTemp);
                        ifAutoLocaion = true; //是否自动定位
                        mLocationClient.setLocOption(option);
                        mLocationClient.start();
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        //Marker的监听事件，内部实现改变Marker的大小，以及显示最下方的操作选项  2016-8-10 chish改 用title来
        //判断点击事件 这样就不用像原先那样查找而且还要用很多list 现在只要一个list（Devlist）即可 最少情况可以不用
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng point = marker.getPosition();
                title_Marker = marker.getTitle();
                String lastMarker_Title = null;
                System.out.println("title_Marker" + title_Marker);
                //如果title中包含"-"则为集中器 如果包含"#"则为单灯
                if(title_Marker.contains("#")){ //单灯
                    ConcentratorOrLight = 2;
                    System.out.println("单灯");
                }else { //集中器
                    ConcentratorOrLight = 1;
                    System.out.println("集中器");
                }
                if(remove_marker != null) {  //如果存在放大图标 就清除它
                    lastMarker_Title = remove_marker.getTitle();
                    remove_marker.remove();
                }
                //是否还是点击前一次的marker
                if(title_Marker.equals(lastMarker_Title)) {
                    remove_marker = null;
                    bottom1.setVisibility(View.VISIBLE);
                    bottom2.setVisibility(View.GONE);
                } else { //如果不是，判断新点击的是集中器还是单灯
                    bottom1.setVisibility(View.GONE);
                    bottom2.setVisibility(View.VISIBLE);
                    switch (ConcentratorOrLight) {
                        case 1:
                            Marker conMarker_Big = Addmarker(point, R.drawable.control, title_Marker, 1, 0);
                            conMarker_Big.setTitle(title_Marker);
                            break;
                        case 2:
                            String roadNum = title_Marker.split("#")[1];
                            Marker singleLightMarker_Big = Addmarker(point, R.drawable.roadlight1, roadNum, 1, 1);
                            singleLightMarker_Big.setTitle(roadNum);
                            break;
                    }
                }
                return true;
            }
        });

        btCtrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context dialogContext = new ContextThemeWrapper(getActivity(),
                        android.R.style.Theme_Light);
                String[] choices;
                if(ConcentratorOrLight == 1) { //集中器
                    choices = getResources().getStringArray(R.array.map_concentrator);
                    ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
                            android.R.layout.simple_list_item_1, choices);
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(dialogContext);
                    builder.setSingleChoiceItems(adapter, -1,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent();
                                    intent.putExtra("childname", title_Marker);
                                    switch (which) {
                                        case 0:
                                            intent.setClass(getActivity(), TimeControl.class);
                                            startActivity(intent);
                                            break;
                                        case 1:
                                            intent.setClass(getActivity(), CentralizingSwitch.class);
                                            startActivity(intent);
                                            break;
                                        case 2:
                                            intent.setClass(getActivity(), SingleTimeSetting.class);
                                            startActivity(intent);
                                            break;
                                    }
                                }
                            });
                    builder.create().show();
                } else if(ConcentratorOrLight == 2){ //单灯
                    choices = getResources().getStringArray(R.array.map_single);
                    ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
                            android.R.layout.simple_list_item_1, choices);
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(dialogContext);
                    builder.setSingleChoiceItems(adapter, -1,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent();
                                    intent.putExtra("childname", title_Marker.split("#")[0]);
                                    intent.setClass(getActivity(), SingleActivity.class);
                                    switch (which) {
                                        case 0:
                                            intent.putExtra("myTAG", "Dimming");
                                            startActivity(intent);
                                            break;
                                        case 1:
                                            intent.putExtra("myTAG", "TimeControl");
                                            startActivity(intent);
                                            break;
                                    }
                                }
                            });
                    builder.create().show();
                }
            }
        });

        btDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                if(ConcentratorOrLight ==1) {
                    intent.putExtra("childname", title_Marker);
                }else if(ConcentratorOrLight == 2) {
                    intent.putExtra("childname", title_Marker.split("#")[0]);
                }
                intent.setClass(getActivity(), ElectricalParameter.class);
                startActivity(intent);
            }
        });

        btNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng point = remove_marker.getPosition();
                try {
                    String string = "intent://map/direction?"
                            + "origin=latlng:" + current.latitude + "," + current.longitude
                            + "|name=起点"
                            + "&destination=latlng:" + point.latitude + "," + point.longitude
                            + "|name=目的地"
                            + "&mode=driving"
                            + "&src=Light#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end";
                    System.out.println("string :" + string);
                    // String string2= "intent://map/direction?origin=latlng:34.264642,108.951085|name=起点&destination=latlng:34.226869,108.963226|name=目的地&mode=driving&region=西安&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end";
                    Intent intent = Intent.getIntent(string);
                    if (isInstallByread("com.baidu.BaiduMap")) {
                        startActivity(intent); //启动调用
                    } else {
                        Toast.makeText(getActivity(), "未安装百度地图", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /*
      判断是否安装目标应用
      @param packageName 目标应用安装后的包名
      @return 是否已安装目标应用
    */
    private boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

    private void initLocation(){
        mylocationlistener = new MyLocationListener();
        option = new LocationClientOption();
        option.setOpenGps(true);        //是否打开GPS
        option.setCoorType("bd09ll");       //设置返回值的坐标类型。
        option.setPriority(LocationClientOption.NetWorkFirst);  //设置定位优先级
        option.setProdName("LocationDemo"); //设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
        option.setScanSpan(0);  //2016-8-9注，梁工改  //设置定时定位的时间间隔。单位毫秒
        mLocationClient.setLocOption(option);
        //注册位置监听器
        mLocationClient.registerLocationListener(mylocationlistener);
    }

    private void removeAllMarkers () {
        mBaiduMap.clear();
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            System.out.println("test2");
            ifFirstLocation = false;
            if(location == null) {
                Toast.makeText(getContext(),"location null",Toast.LENGTH_SHORT).show();
                return;
            }
            else if(!ifAutoLocaion)//2016-8-9注，梁工改
            {
              //  Toast.makeText(getContext(),"location "+location.getLatitude()+","+location.getLongitude(), Toast.LENGTH_SHORT).show();
                mLocationClient.stop();
            }else {
               // Toast.makeText(getContext(),"location "+location.getLatitude()+","+location.getLongitude(), Toast.LENGTH_SHORT).show();
            }
            current = new LatLng(location.getLatitude(),location.getLongitude());
            volley(current, btSetRadius.getText().toString().split("：")[1].trim());
            // 开启定位图层
            mBaiduMap.setMyLocationEnabled(true);
            // 构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();

            // 设置定位数据
            mBaiduMap.setMyLocationData(locData);
            // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
//            BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
            MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, null);
            mBaiduMap.setMyLocationConfigeration(config);
            // 当不需要定位图层时关闭定位图层
            //mBaiduMap.setMyLocationEnabled(false);
        }

    }

    /*
    * 添加地图的marker函数
    * isSelected代表是否是被选中按钮，isSelected==1，代表是，将增加字体大小
    * type代表单灯还是集中器，type==1，代表单灯，将背景设置为黑色边框绿色底
    * */
    public Marker Addmarker(LatLng point, int drawable ,String childnum, int isSelected, int type) {
        View map_marker ;
        TextView marker_text;
        ImageView marker_img;
        //创建marker
        if(inflater == null) System.out.println("null!!");
        map_marker = inflater.inflate(R.layout.map_marker, null);
        marker_text = (TextView)map_marker.findViewById(R.id.map_marker_text);
        marker_img = (ImageView)map_marker.findViewById(R.id.map_marker_img);
        marker_text.setText(childnum);
        marker_img.setImageResource(drawable);
        if(type == 1) marker_text.setBackground(getResources().getDrawable(R.drawable.single_info));
        if(isSelected == 1) marker_text.setTextSize(20);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(map_marker);
        //bitmap1 = getViewBitmap(map_marker);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);

        //在地图上添加Marker，并显示
        Marker marker = (Marker)(mBaiduMap.addOverlay(option));
        //回收BitmapDescriptor防止内存泄漏（OOM）
        if(bitmap != null ){
            bitmap.recycle();
            bitmap = null;
        }
        if(isSelected == 1) remove_marker = marker;
        return marker;
    }

    //点击获取按钮绘制地图中心点坐标
    private void  AddCenterMarker(LatLng centerpoint){
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.maker);
        OverlayOptions option = new MarkerOptions()
                .position(centerpoint)
                .icon(bitmap)
                .zIndex(5);
        centerMarker = (Marker)(mBaiduMap.addOverlay(option));
        //回收BitmapDescriptor防止内存泄漏（OOM）
        if(bitmap != null ){
            bitmap.recycle();
            bitmap = null;
        }
    }

    //先进入这个fragment内再发起定位请求，防止滑动卡顿
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser == true ) {
            startRequestLocation();
        }
        if(isVisibleToUser == false) {
            stopRequestLocation();
        }
    }
    public void startRequestLocation() {
        if(mLocationClient!=null) {
            //mLocationClient.registerLocationListener(myListener);    //注册监听函数
            mLocationClient.start();
            mLocationClient.requestLocation(); //请求一次定位
            System.out.println("start");
        }
    }
    public void stopRequestLocation() {
        if(mLocationClient!=null) {
            mLocationClient.unRegisterLocationListener(mylocationlistener);
            mLocationClient.stop();
            System.out.println("stop");
        }
    }

    public void onPause() {
        mMapView.onPause();
        super.onPause();
        System.out.println("3-Pause");
    }
    public void onResume() {
        mMapView.onResume();
        super.onResume();
        System.out.println("3-Resume");
    }
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
        System.gc();
        System.out.println("3-Destroy");
    }

    /*
        网络请求
        如果是CENTER_LOCATION代表的是请求屏幕中点坐标附近范围的集中器
        如果是CURRENT_LOCATION代表的是请求当前坐标附近的范围的集中器
     */
    //private void volley(int CenterOrCurrent, String radius)
    private void volley(LatLng latlng, String radius) {
        if(latlng == null) {
            Toast.makeText(getContext(),"latlng null",Toast.LENGTH_SHORT).show();
            return;
        }
        removeAllMarkers();
        DevList.clear();
        Integer rd_int=  Integer.parseInt(radius.trim())*1000;//2016-8-19 梁工加
        OverlayOptions option = new CircleOptions().center(latlng).radius(rd_int).stroke(new Stroke(4, 0xAA00FF00)).fillColor(0x2111AAFF);//2016-8-19
       // .stroke(new Stroke(5, 0xAA00FF00));
//在地图上添加多边形Option，用于显示
        // optionsetFillOpacity(0);
        mBaiduMap.addOverlay(option);
        String URL = null;
        try {
            URL = rootURL//2016-8-9注，梁工改，经度和纬度我帮你调转了， //2016-8-10 CSH改回来了
                    + "/Tree/DevInfo_map_center?centerX=" +latlng.longitude
                    + "&centerY=" +  latlng.latitude
                    + "&rad_km=" + radius
                    + "&near_p=" + num
                    + "&sn_pass=\"\"&log_name=" + URLEncoder.encode(account, "utf-8").trim()
                    + "&log_pass=" + password + "&sn_node_mode=0";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("URL: " + URL);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("response :" + response);
                      //  Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                        try {
                            conInfo = gson.fromJson(response, ConInfo.class);
                            DevName = conInfo.getDevName();
                            DevNo =  conInfo.getDevNo();
                            DevXDevY = conInfo.getDevXDevY();
                            for(int i = 0 ; i < DevName.size() ; i++) {
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("DevNo", DevNo.get(i).trim());      //集中器编号
                                map.put("DevName", DevName.get(i).trim());  //集中器名字
                                //集中器经纬度
                                String devxy = DevXDevY.get(i).trim();
                                Double longitude = Double.parseDouble(devxy.split(",")[0]);
                                Double latitude = Double.parseDouble(devxy.split(",")[1]);
                                LatLng latLng = new LatLng(latitude, longitude);
                                Marker marker = Addmarker(latLng, R.drawable.control,
                                        DevNo.get(i).trim() + "-" + DevName.get(i).trim(), 0, 0);
                                marker.setTitle(DevNo.get(i).trim() + "-" + DevName.get(i).trim());
                                DevList.add(map);
                            }
                            for(int i = 0; i < DevList.size(); i++) {
                                String DevNo = (String)DevList.get(i).get("DevNo");
                                String DevName = (String)DevList.get(i).get("DevName");
                                getSingleLightMessage(DevNo,DevName);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println(volleyError.toString());
                Toast.makeText(getActivity(),"网络异常", Toast.LENGTH_SHORT).show();
            }
        });
        VollyQueue.getHttpQueues().add(stringRequest);
    }

    //使用volley框架从服务器获取单灯信息
    public void getSingleLightMessage(final String DevNo,final String DevName) {
        String URL = rootURL + "/Single/Getsingle_map_table_volt_view?Dev_Id=" + DevNo;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null && !response.trim().equals("")) {
                            System.out.println("response :" + response);
                            try {
                                singleLightInfo = gson.fromJson(response, SingleLightInfo.class);
                                List Lights = singleLightInfo.getSingle_map_table_volt_view();
                                String str = "";
                                for(int i = 0 ; i < Lights.size(); i++) {
                                    String roadNum = ((SingleLightInfo.SingleMapTableVoltViewBean)Lights.get(i)).getRod_num().trim();
                                    if(roadNum.trim().equals("")|| roadNum == null) {
                                        continue;
                                    }
                                    str += roadNum + " ";
                                    Map<String, Object> map = new HashMap<String, Object>();
                                    map.put("singleNum", roadNum);           //记录单灯编号
                                    // 记录经纬度
                                    Double longitude = ((SingleLightInfo.SingleMapTableVoltViewBean)Lights.get(i)).getDevX();
                                    Double latitude = ((SingleLightInfo.SingleMapTableVoltViewBean)Lights.get(i)).getDevY();
                                    LatLng point = new LatLng(latitude, longitude);
                                    Marker marker = Addmarker(point, R.drawable.roadlight1, roadNum, 0, 1);
                                    marker.setTitle(DevNo+"-"+ DevName + "#" +roadNum);
                                    DevList.add(map);
                                }
                            //    Toast.makeText(getContext(), DevNo + ":" + str, Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"网络异常", Toast.LENGTH_SHORT).show();
            }
        });
        stringRequest.setTag("");
        VollyQueue.getHttpQueues().add(stringRequest);
    }
}

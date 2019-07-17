package com.example.administrator.light.map;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
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
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.administrator.light.BaseActivity;
import com.example.administrator.light.R;
import com.example.administrator.light.VollyQueue;
import com.example.administrator.light.system.CentralizingSwitch;
import com.example.administrator.light.system.ElectricalParameter;
import com.example.administrator.light.system.Single_Dimming_TimeCtrl.SingleActivity;
import com.example.administrator.light.system.SingleTimeSetting;
import com.example.administrator.light.system.TimeControl;
import com.example.administrator.light.util.SharedPreferencesUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wujia on 2016/3/26.
 */
public class MapActivity extends BaseActivity {
    private final int LIGHT = 1, CONCENTRATOR = 0;
    private final int SELECTED = 1, NOTSELECTED = 0;
    private String rootURL = null, childname = null, childnum = null,
            childxy = null, childx = null, childy = null;

    private LinearLayout bottomBtLayout;
    private Button btDetail, btCtrl, btNavigation;
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    private double latitude_destination, longitude_destination,
            latitude_origin, longitude_origin;

    private LocationClient locationClient = null;
    private MyLocationListener mlocationListener;

    private List<String> singleNum = new ArrayList<String>(); //记录单灯编号
    //记录经纬度
    private List<String> Lat = new ArrayList<String>();
    private List<String> Lon = new ArrayList<String>();
    private List<LatLng> slatlng = new ArrayList<LatLng>();
    private List<Marker> markers = new ArrayList<Marker>();
    private List<String> name = new ArrayList<String>();

    private int SelectedIndex = -1;
    private Marker remove_marker;
    private View map_marker;
    //private TextView marker_text;
    //private ImageView marker_img;
    //private View infowindow;
    //private TextView infowindow_text;
    //private LatLng clatlng ;
    //private Bitmap bitmap ;
    //private Marker marker = null;
    //private String intentnumm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        mlocationListener = new MyLocationListener();
        setContentView(R.layout.map);
        initLocation();
        init();
        initMap();


    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation Location) {
            System.out.println("latitude_origin:" + Location.getLatitude());
            latitude_origin = Location.getLatitude();
            System.out.println("longitude_origin" + Location.getLongitude());
            longitude_origin = Location.getLongitude();
        }
    }

    public void initLocation(){
        locationClient = new LocationClient(this);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy); //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span= 0;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationClient.setLocOption(option);
        locationClient.registerLocationListener(mlocationListener);    //注册监听函数
        locationClient.start();
        locationClient.requestLocation();
    }

    //初始化界面ID
    public void init() {
        //获取子列表的名字以及坐标字符串
        Intent intent = getIntent();
        childxy = intent.getStringExtra("childxy");
        childname = intent.getStringExtra("childname");
        //设置标题栏
        getToolbarTitle().setText(childname);
        rootURL = (String) SharedPreferencesUtils.getParam(this, "rootURL", "");

        bottomBtLayout = (LinearLayout)findViewById(R.id.map_bottom);
        btDetail = (Button)findViewById(R.id.map_bottom_bu1);
        btCtrl = (Button)findViewById(R.id.map_bottom_bu2);
        btNavigation = (Button)findViewById(R.id.map_bottom_bu3);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        //创建InfoWindow展示的view
        /*
        inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        infowindow = inflater.inflate(R.layout.infowindow,null);
        infowindow_text = (TextView)infowindow.findViewById(R.id.infowindow_text);
        */
    }

    public void initMap() {
        singleNum.clear();
        slatlng.clear();
        name.clear();
        Lon.clear();
        Lat.clear();

        childnum = childname.split("-")[0];
        //从字符串构建Latlng坐标
        String[]s = childxy.split(",");
        childx = s[0];//经度
        childy = s[1];//纬度
        longitude_destination = Double.parseDouble(childx); //集中器的经度
        latitude_destination = Double.parseDouble(childy);//集中器的纬度
        System.out.println("longitude_destination:" + longitude_destination);
        System.out.println("latitude_destination:" + latitude_destination);
        LatLng point = new LatLng(latitude_destination, longitude_destination);
        slatlng.add(point);
        name.add(childname);

        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //移动地图至坐标位置
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(point);
        mBaiduMap.animateMapStatus(u);
        //设置地图的层级为20
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(20).build()));

        //添加地图marker
        Addmarker(point, R.drawable.control, childname, NOTSELECTED, CONCENTRATOR);

        //创建并添加InfoWindow
        //createInfowindow(point, childname);

        //注册并实现监听事件
        Listener();

        getSingleLightMessage();
    }

    /*
    * 注册实现监听事件
    * */
    public void Listener() {
        //Marker的监听事件，内部实现改变InfoWindow的大小，以及显示最下方的操作选项
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                LatLng latlng =  marker.getPosition();
                latitude_destination = latlng.latitude;
                longitude_destination = latlng.longitude;
                if(SelectedIndex != -1) {
                    remove_marker.remove();
                    if(SelectedIndex == 0) {
                        Addmarker(slatlng.get(SelectedIndex), R.drawable.control, name.get(SelectedIndex), NOTSELECTED, CONCENTRATOR);
                    } else {
                        Addmarker(slatlng.get(SelectedIndex), R.drawable.roadlight1, name.get(SelectedIndex), NOTSELECTED, LIGHT);
                    }
                }
                 /*
                for(SelectedIndex = 0 ; SelectedIndex < slatlng.size() ; SelectedIndex++) {
                    if(latlng.equals(slatlng.get(SelectedIndex))) {
                        Toast.makeText(MapActivity.this, SelectedIndex +"", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                //Toast.makeText(MapActivity.this,latlng+"",Toast.LENGTH_SHORT).show();
                //infowindow_text.setTextSize(25);
                marker.remove();
                if(SelectedIndex == 0) {
                    Addmarker(latlng, R.drawable.control, name.get(SelectedIndex), SELECTED, CONCENTRATOR);
                } else {
                    Addmarker(latlng, R.drawable.roadlight1, name.get(SelectedIndex), SELECTED, LIGHT);
                }
                */
                int tempIndex;
                for(tempIndex = 0 ; tempIndex < slatlng.size() ; tempIndex++) {
                    if(latlng.equals(slatlng.get(tempIndex))) {
                   //     Toast.makeText(MapActivity.this, tempIndex +"", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                if(SelectedIndex == tempIndex) {
                    SelectedIndex = -1;
                    bottomBtLayout.setVisibility(View.GONE);
                } else {
                    SelectedIndex = tempIndex;
                    marker.remove();
                    if(SelectedIndex == 0) {
                        Addmarker(latlng, R.drawable.control, name.get(SelectedIndex), SELECTED, CONCENTRATOR);
                    } else {
                        Addmarker(latlng, R.drawable.roadlight1, name.get(SelectedIndex), SELECTED, LIGHT);
                    }
                    bottomBtLayout.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
        btCtrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context dialogContext = new ContextThemeWrapper(MapActivity.this,
                        android.R.style.Theme_Light);
                String[] choices;
                if (SelectedIndex == 0) {
                    choices = getResources().getStringArray(R.array.map_concentrator);
                    ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
                            android.R.layout.simple_list_item_1, choices);
                    AlertDialog.Builder builder = new AlertDialog.Builder(dialogContext);
                    builder.setSingleChoiceItems(adapter, -1,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent();
                                    intent.putExtra("childname", childname);
                                    switch (which) {
                                        case 0:
                                            intent.setClass(MapActivity.this, TimeControl.class);
                                            startActivity(intent);
                                            break;
                                        case 1:
                                            intent.setClass(MapActivity.this, CentralizingSwitch.class);
                                            startActivity(intent);
                                            break;
                                        case 2:
                                            intent.setClass(MapActivity.this, SingleTimeSetting.class);
                                            startActivity(intent);
                                            break;
                                    }
                                }
                            });
                    builder.create().show();
                } else {
                    choices = getResources().getStringArray(R.array.map_single);
                    ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
                            android.R.layout.simple_list_item_1, choices);
                    AlertDialog.Builder builder = new AlertDialog.Builder(dialogContext);
                    builder.setSingleChoiceItems(adapter, -1,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent();
                                    intent.putExtra("childname", childname);
                                    intent.setClass(MapActivity.this, SingleActivity.class);
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
                intent.putExtra("childname", childname);
                intent.setClass(MapActivity.this, ElectricalParameter.class);
                startActivity(intent);
            }
        });
        btNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String string = "intent://map/direction?" +
                            "origin=latlng:" + latitude_origin + "," + longitude_origin + "|name=起点" +
                            "&destination=latlng:" + latitude_destination + "," + longitude_destination + "|name=目的地" +
                            "&mode=driving" +
                            "&src=Light#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end";
                    System.out.println("string :" + string);
                    // String string2= "intent://map/direction?origin=latlng:34.264642,108.951085|name=起点&destination=latlng:34.226869,108.963226|name=目的地&mode=driving&region=西安&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end";
                    Intent intent = Intent.getIntent(string);
                    if (isInstallByread("com.baidu.BaiduMap")) {
                        startActivity(intent); //启动调用
                    } else {
                        Toast.makeText(MapActivity.this, "未安装百度地图", Toast.LENGTH_SHORT).show();
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

    /*
    * 添加地图的marker函数
    * isSelected代表是否是被选中按钮，isSelected==SELECTED,即1，代表是，将增加字体大小
    * type代表单灯还是集中器，type==LIGHT，即1，代表单灯，将背景设置为黑色边框绿色底
    * */
    public void Addmarker(LatLng point, int drawable ,String childnum, int isSelected, int type) {
        LayoutInflater inflater = getLayoutInflater();
        TextView marker_text;
        ImageView marker_img;
        //创建marker
        map_marker = inflater.inflate(R.layout.map_marker, null);
        marker_text = (TextView)map_marker.findViewById(R.id.map_marker_text);
        marker_img = (ImageView)map_marker.findViewById(R.id.map_marker_img);
        marker_text.setText(childnum);
        marker_img.setImageResource(drawable);
        if(type == LIGHT) {
            marker_text.setBackground(getResources().getDrawable(R.drawable.single_info));
        }
        if(isSelected == 1) {
            marker_text.setTextSize(20);
        }
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(map_marker);
        //bitmap1 = getViewBitmap(map_marker);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
        //在地图上添加Marker，并显示
        Marker marker = (Marker)(mBaiduMap.addOverlay(option));
        if(isSelected==1) remove_marker = marker;
        markers.add(marker);
    }

    /*
    * 在地图Marker上创建并添加Info窗口
    *
    public void createInfowindow(LatLng point , String childnum) {
        infowindow_text.setText(childnum);
        //定义用于显示该InfoWindow的坐标点
        //创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
        InfoWindow mInfoWindow = new InfoWindow(infowindow, point, -95);
        //显示InfoWindow
        mBaiduMap.showInfoWindow(mInfoWindow);
    }*/

    /*
    * 使用volley框架从服务器获取单灯信息
    * */
    public void getSingleLightMessage() {
        String URL = rootURL + "/Single/Getsingle_map_table_volt_view?Dev_Id=" + childnum;
            StringRequest stringRequest = new StringRequest(Request.Method.GET,URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println("伍嘉文"+response);
                            //Toast.makeText(MapActivity.this,response,Toast.LENGTH_SHORT).show();
                            if (response != null && !response.trim().equals("")) {
                                try {
                                    Boolean b = true; //记录有无数据
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONArray jsonArray = jsonObject.getJSONArray("single_map_table_volt_view");
                                    String str = "";
                                    for(int i = 0 ; i < jsonArray.length() ; i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        String roadNum = object.getString("rod_num").trim();
                                        if(roadNum.trim().equals("")|| roadNum ==null) {
                                            b = false;
                                            continue;
                                        }
                                        String lon = object.getString("DevX");
                                        String lat = object.getString("DevY");
                                        singleNum.add(roadNum);
//                                        Lat.add(lat);
//                                        Lon.add(lon);
                                        Double latitude = Double.parseDouble(lat);
                                        Double longitude = Double.parseDouble(lon);
                                        LatLng point1 = new LatLng(latitude,longitude);
                                        slatlng.add(point1);
                                        name.add(singleNum.get(i));
                                        str += singleNum.get(i) + " ";
                                        Addmarker(point1, R.drawable.roadlight1, singleNum.get(i), 0, 1);
                                        //Toast.makeText(MapActivity.this,num+" "+lat+ " "+lon,Toast.LENGTH_SHORT).show();
                                    }
                                  //  Toast.makeText(MapActivity.this, str, Toast.LENGTH_SHORT).show();
                                    if(b) {
                                        for(int i = 0 ; i < singleNum.size() ; i++) {

                                            //createInfowindow(point1, roadNum);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MapActivity.this,"网络异常", Toast.LENGTH_SHORT).show();
                }
            });
            //设置请求的Tag标签，可以在全局请求队列中通过Tag标签进行请求的查找
            stringRequest.setTag("testGet");
            //将请求加入全局队列中
            VollyQueue.getHttpQueues().add(stringRequest);
    }

    /*
    * 对地图生命周期的管理
    * */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        locationClient.unRegisterLocationListener(mlocationListener);
        locationClient.stop();
        locationClient = null;
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

//    public static Bitmap getViewBitmap(View addViewContent) {
//        addViewContent.setDrawingCacheEnabled(true);
//        addViewContent.measure(
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        addViewContent.layout(0, 0,
//                addViewContent.getMeasuredWidth(),
//                addViewContent.getMeasuredHeight());
//
//        addViewContent.buildDrawingCache();
//        Bitmap cacheBitmap = addViewContent.getDrawingCache();
//        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
//        return bitmap;
//    }

}

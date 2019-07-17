package com.example.administrator.light.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.administrator.light.ExpandablelistView;
import com.example.administrator.light.LoginActivity;
import com.example.administrator.light.R;
import com.example.administrator.light.util.SharedPreferencesUtils;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by binyej 2019/6/12.
 */
public class FragmentSao extends Fragment {
    ListView parentMenuList, childMenuList,parentMenuList1;
    ArrayAdapter<String> parentAdapter, childAdapter;
    String token=null;
    List<String> menu=new ArrayList<>();
    List<String> menuna=new ArrayList<>();
    List<String> menunaclass=new ArrayList<>();
    String[] mmm={"扫一扫","其他"};
    int P = 0;//记录是在系统功能还是其他功能的子列表下
    String childname,menuname,allmenuname,classname;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_menu, container, false);
        initView(view);
        parentAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, mmm);
        parentAdapter.notifyDataSetChanged();
        parentMenuList1.setAdapter(parentAdapter);
        parentMenuList1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String strItem = (String) parentMenuList1.getItemAtPosition(i);
                if(i==0){
                    IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
                    intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                    intentIntegrator.setCaptureActivity(ScanActivity.class);
                    intentIntegrator.setPrompt("请扫描二维码");//底部的提示文字，设为""可以置空
                    intentIntegrator.setCameraId(0);//前置或者后置摄像头
                    intentIntegrator.setBeepEnabled(false); //扫描成功的「哔哔」声，默认开启
                    intentIntegrator.setBarcodeImageEnabled(true);//是否保留扫码成功时候的截图
                    intentIntegrator.initiateScan();
                }
                else{
                    SharedPreferencesUtils.setParam(getContext(), "username", "");
                    SharedPreferencesUtils.setParam(getContext(), "password", "");
                    SharedPreferencesUtils.setParam(getContext(),"token","");
                    Intent intent1 = new Intent();
                    intent1.setClass(getActivity(), LoginActivity.class);
                    startActivity(intent1);
                }
            }
        });
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(scanResult!=null){
            String result = scanResult.getContents();
            Log.e("HYN",result);
            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();

        }

         super.onActivityResult(requestCode, resultCode, data);
    }
    public void initView(View view) {
        childname = getArguments().getString("childname");
        parentMenuList = (ListView)view.findViewById(R.id.parentMenuList);
        childMenuList = (ListView)view.findViewById(R.id.childMenuList);
        parentMenuList1 = (ListView)view.findViewById(R.id.parentMenuList1);
    }
    public void toNextActivity(String fromStr, Class fromClass) {
        Intent intent = new Intent();
        if(childname.equals("")) {
            intent.putExtra("from", fromStr);
            intent.setClass(getActivity(), ExpandablelistView.class);
        } else {
            intent.putExtra("childname", childname);
            intent.setClass(getActivity(), fromClass);
        }
        startActivity(intent);
    }
}

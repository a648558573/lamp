package com.example.administrator.light.customComponent;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.administrator.light.R;

/**
 * Created by Lxr on 2016/5/25.
 */
public class SingleSettingItem extends LinearLayout {
    int sp1, sp2;//分别表示两个spinner的选择项
    Button btOn,btOff,btLux;
    Spinner spinner1,spinner2;
    public SingleSettingItem(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        LayoutInflater.from(context).inflate(R.layout.single_setting_item, this, true);
        btOn = (Button)findViewById(R.id.setting_bt_on);
        btOff = (Button)findViewById(R.id.setting_bt_off);
        btLux = (Button)findViewById(R.id.setting_bt_Lux);
        spinner1 = (Spinner)findViewById(R.id.spinner1);
        spinner2 = (Spinner)findViewById(R.id.spinner2);
    }

    public void setOnButtonLinstener(OnClickListener listener) {
        btOn.setOnClickListener(listener);
    }
    public void setOffButtonLinstener(OnClickListener listener) {
        btOff.setOnClickListener(listener);
    }
    public void setLuxButtonLinstener(OnClickListener listener) {
        btLux.setOnClickListener(listener);
    }
    public void setBtOn(String s){
        btOn.setText(s);
    }
    public void setBtOff(String s){
        btOff.setText(s);
    }
    public void setBtLux(String s ){
        btLux.setText(s + "%");
    }


    public String getBtOn(){
        return btOn.getText().toString();
    }
    public String getBtOff(){
        return btOff.getText().toString();
    }
    public String getBtLux( ){
        String[] temp = btLux.getText().toString().split("%");
        return temp[0];
    }

    public void setSpinner1(int i){
        spinner1.setSelection(i);
    }
    public void setSpinner2(int i){
        spinner2.setSelection(i);
    }
    public String getSpinner1(){
        return spinner1.getSelectedItem().toString();
    }
    public String getNumSpinner1(){
        if(spinner1.getSelectedItem().toString().equals("禁用"))
            return "17";
        else
            return spinner1.getSelectedItem().toString();
    }
    public String getSpinner2(){
        if(spinner2.getSelectedItem().toString().equals("无")){
            return "0";
        }
        else
            return spinner2.getSelectedItem().toString();
    }

    public void setSp1Listener( AdapterView.OnItemSelectedListener l){
        spinner1.setOnItemSelectedListener(l);
    }
    public void setSp2Listener( AdapterView.OnItemSelectedListener l){
        spinner1.setOnItemSelectedListener(l);
    }

}

package com.example.administrator.light.customComponent;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.light.R;


/**
 * Created by wujia on 2016/5/6.
 */
public class Time extends LinearLayout {
    private TextView time_text;
    private Button time_bu_on , time_bu_off;
    public Time(Context context, AttributeSet attributeSet) {
        super(context,attributeSet);
        LayoutInflater.from(context).inflate(R.layout.time, this, true);

        time_text = (TextView)findViewById(R.id.tvName);
        time_bu_on = (Button)findViewById(R.id.btOn);
        time_bu_off = (Button)findViewById(R.id.btOff);

    }
    public void time_text_settext(String s) {
        time_text.setText(s);
    }
    public void time_bu_on_listen(OnClickListener listener) {
        time_bu_on.setOnClickListener(listener);
    }
    public void time_bu_off_listen(OnClickListener listener) {
        time_bu_off.setOnClickListener(listener);
    }
    public String time_bu_on_gettext() {
        String s = time_bu_on.getText().toString();
        return s;
    }
    public String time_bu_off_gettext() {
        String s = time_bu_off.getText().toString();
        return s;
    }
    public void bu_on_setText(String s) {
        time_bu_on.setText(s);
    }
    public void bu_off_setText(String s) {
        time_bu_off.setText(s);
    }
}

package com.example.administrator.light.customComponent;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.administrator.light.R;

/**
 * Created by Lxr on 2016/4/28.
 */
public class ShowMsgLayout extends LinearLayout {
    private TextView nameText,infoText ;

    public ShowMsgLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        LayoutInflater.from(context).inflate(R.layout.showmsg, this, true);


        this.nameText = (TextView) findViewById(R.id.show_name);
        this.infoText = (TextView) findViewById(R.id.show_info);

        this.setClickable(true);
        this.setFocusable(true);
    }


    public void set_name(String text) {
        this.nameText.setText(text);
    }

    public void set_info(String text) {
        this.infoText.setText(text);
    }

}
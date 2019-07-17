package com.example.administrator.light.customComponent;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.administrator.light.R;

/**
 * Created by Lxr on 2016/4/28.
 */
public class TitleView extends RelativeLayout {
    private TextView text ;
    private Button leftButton;

    public TitleView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        LayoutInflater.from(context).inflate(R.layout.titleview, this, true);


        this.text = (TextView) findViewById(R.id.title_view_text);
        this.leftButton = (Button)findViewById(R.id.title_view_back);

    }


    public void set_text(String text) {
        this.text.setText(text);
    }

    public void setLeftButtonListener(OnClickListener l) {
        leftButton.setOnClickListener(l);
    }

}

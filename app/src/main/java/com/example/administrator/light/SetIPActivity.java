package com.example.administrator.light;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.light.util.SharedPreferencesUtils;

public class SetIPActivity extends AppCompatActivity {
    private EditText rootURL;
    private Button toSet;
    private String rootURL_str = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_ip);
        init();
    }
    public void init() {
        rootURL = (EditText)findViewById(R.id.ip_port);
        toSet = (Button)findViewById(R.id.toSet);
        String preferences_ip = (String) SharedPreferencesUtils.getParam(this, "rootURL", "");//http://183.233.174.11:10005
        if(preferences_ip.equals("") || preferences_ip.equals("http://")) {
            rootURL.setText("");
        } else {
            rootURL.setText(preferences_ip.split("//")[1].toString().trim());
        }

        toSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootURL_str = rootURL.getText().toString();
                if (rootURL_str.length() == 0) {
                    Toast.makeText(SetIPActivity.this, "ip不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferencesUtils.setParam(SetIPActivity.this, "rootURL", "http://" + rootURL_str);
                    Toast.makeText(SetIPActivity.this, "保存ip成功", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

}
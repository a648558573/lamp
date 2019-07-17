package com.example.administrator.light.system;

import android.os.Bundle;
import android.widget.TextView;

import com.example.administrator.light.BaseActivity;
import com.example.administrator.light.R;
import com.example.administrator.light.util.SerializableMap;

import java.util.Map;

/**
 * Created by JO on 2016/5/20.
 */
public class SingleDetailParameter extends BaseActivity {

    private Map<String, Object> map;

    private TextView tv_single_state2;
    private TextView tv_update_dtm;
    private TextView tv_rod_num;
    private TextView tv_rod_real;
    private TextView tv_I1;
    private TextView tv_V_rod;
    private TextView tv_Lux_1;

    private TextView tv_I2;
    private TextView tv_V_rod2;
    private TextView tv_Lux_2;

    private TextView tv_rod_alarm;
    private TextView tv_alarm_info;
    private TextView tv_alarm_1;
    private TextView tv_alarm_2;
    private TextView tv_alarm_3;
    private TextView tv_alarm_4;
    private TextView tv_rod_V_up;
    private TextView tv_rod_V_down;
    private TextView tv_I1_up;
    private TextView tv_I2_up;
    private TextView tv_I3_up;
    private TextView tv_I4_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_detail_para);
        initView();
    }

    private void initView() {
        Bundle bundle = getIntent().getExtras();
        SerializableMap serializableMap = (SerializableMap)bundle.get("singleMap");
        map = serializableMap.getMap();
        getToolbarTitle().setText(map.get("rod_num").toString());

        tv_single_state2 = (TextView)findViewById(R.id.tv_single_state2);
        tv_update_dtm  = (TextView)findViewById(R.id.tv_update_dtm);
        tv_rod_num = (TextView)findViewById(R.id.tv_rod_num);
        tv_rod_real = (TextView)findViewById(R.id.tv_rod_real);
        tv_I1 = (TextView)findViewById(R.id.tv_I1);
        tv_V_rod = (TextView)findViewById(R.id.tv_V_rod);
        tv_Lux_1 = (TextView)findViewById(R.id.tv_Lux_1);

        tv_I2 = (TextView)findViewById(R.id.tv_I2);
        tv_V_rod2 = (TextView)findViewById(R.id.tv_V_rod2);
        tv_Lux_2 = (TextView)findViewById(R.id.tv_Lux_2);

        tv_rod_alarm = (TextView)findViewById(R.id.tv_rod_alarm);
        tv_alarm_info = (TextView)findViewById(R.id.tv_alarm_info);
        tv_alarm_1 = (TextView)findViewById(R.id.tv_alarm_1);
        tv_alarm_2 = (TextView)findViewById(R.id.tv_alarm_2);
        tv_alarm_3 = (TextView)findViewById(R.id.tv_alarm_3);
        tv_alarm_4 = (TextView)findViewById(R.id.tv_alarm_4);
        tv_rod_V_up = (TextView)findViewById(R.id.tv_rod_V_up);
        tv_rod_V_down = (TextView)findViewById(R.id.tv_rod_V_down);
        tv_I1_up = (TextView)findViewById(R.id.tv_I1_up);
        tv_I2_up = (TextView)findViewById(R.id.tv_I2_up);
        tv_I3_up = (TextView)findViewById(R.id.tv_I3_up);
        tv_I4_up = (TextView)findViewById(R.id.tv_I4_up);

        tv_single_state2.setText(map.get("single_state2").toString().trim());
        tv_update_dtm.setText(map.get("update_dtm").toString().trim());
        tv_rod_num.setText(map.get("rod_num").toString().trim());
        tv_rod_real.setText(map.get("rod_real").toString().trim());
        tv_I1.setText(map.get("I1").toString().trim());
        tv_V_rod.setText(map.get("V_rod").toString().trim());
        tv_Lux_1.setText(map.get("Lux_1").toString().trim());

        tv_I2.setText(map.get("I2").toString().trim());
        tv_V_rod2.setText(map.get("V_rod2").toString().trim());
        tv_Lux_2.setText(map.get("Lux_2").toString().trim());

        tv_rod_alarm.setText(map.get("rod_alarm").toString().trim());
        tv_alarm_info.setText(map.get("alarm_info").toString().trim());
        tv_alarm_1.setText(map.get("alarm_1").toString().trim());
        tv_alarm_2.setText(map.get("alarm_2").toString().trim());
        tv_alarm_3.setText(map.get("alarm_3").toString().trim());
        tv_alarm_4.setText(map.get("alarm_4").toString().trim());
        tv_rod_V_up.setText(map.get("rod_V_up").toString().trim());
        tv_rod_V_down.setText(map.get("rod_V_down").toString().trim());
        tv_I1_up.setText(map.get("I1_up").toString().trim());
        tv_I2_up.setText(map.get("I2_up").toString().trim());
        tv_I3_up.setText(map.get("I3_up").toString().trim());
        tv_I4_up.setText(map.get("I4_up").toString().trim());
    }

}

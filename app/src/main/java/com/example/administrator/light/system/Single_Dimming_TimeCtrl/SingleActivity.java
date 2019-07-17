package com.example.administrator.light.system.Single_Dimming_TimeCtrl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.administrator.light.BaseActivity;
import com.example.administrator.light.VollyQueue;
import com.example.administrator.light.fragment.FragmentAdapter;
import com.example.administrator.light.R;
import com.example.administrator.light.system.ResultList;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by JO on 2016/4/29.
 */
public class SingleActivity extends BaseActivity {
    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private FragmentAdapter mFragmentAdapter;
    private ViewPager viewPager;

    //Tab显示内容TextView
    private TextView ForceOnOffTv, SimpleControlTv,ControlTv;
    //Tab的那个引导线
    private ImageView mTabLine;
    //屏幕的宽度
    private int screenWidth;
    private LinearLayout.LayoutParams lineParam;
    //ViewPager的当前选中页
    private int currentIndex;

    //Fragment
    private FragmentForceOnOff mForceOnOffFg;
    private DimmingFragmentSimpleControl mDSimpleControlFg;
    private TimeCtrlFragmentSimpleControl mTSimpleControlFg;

    public String childname = null;//当前选定终端信息selNode
    private int DevNo = 0;

    private List<String> resultList = new ArrayList<String>();
    private int NumOfOperation = 0;
    public String myTAG = "SingleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_activity);
        init();
        initViewPager();
    }

    public int getDevNo() {
        return DevNo;
    }

    public String getMyTAG() {
        return myTAG;
    }

    public FragmentForceOnOff getmForceOnOffFg() {
        return mForceOnOffFg;
    }

    public void updateResultList(String resultStr) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date_str = sDateFormat.format(new Date(System.currentTimeMillis()));
        NumOfOperation ++;
        String tempResultStr =  tempResultStr = "(" + NumOfOperation + ") "
                + date_str.trim() + resultStr;
        resultList.add(0, tempResultStr);
        Toast.makeText(getApplicationContext(), tempResultStr, Toast.LENGTH_SHORT).show();
    }

    public void saveLog(String rootURL, String userName) {
        String rslt_str = resultList.get(0).toString().trim();
        try {
            String URL = rootURL + "/wcf/savelog?logname=" + URLEncoder.encode(userName, "utf-8").trim()
                    + "&logtype=" + URLEncoder.encode("(手机用户)", "utf-8").trim()
                    + "&logstr=" + URLEncoder.encode(rslt_str, "utf-8").trim();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(getApplicationContext(), "网络异常", Toast.LENGTH_SHORT).show();
                }
            });
            stringRequest.setTag("");
            VollyQueue.getHttpQueues().add(stringRequest);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "网络故障!!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void init() {
        Intent intent = getIntent();
        childname = intent.getStringExtra("childname");
        myTAG = intent.getStringExtra("myTAG");
        String[] temp_strs = childname.split("-");
        if(temp_strs.length >0) {
            try {
                DevNo = Integer.parseInt(temp_strs[0].trim());
                System.out.println(DevNo);
            }catch (Exception ex) {

            }
        }
        getToolbarTitle().setText(childname);
        getToolbarRightTv().setVisibility(View.VISIBLE);
        getToolbarRightTv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putStringArrayListExtra("resultList", (ArrayList) resultList);
                intent.setClass(getApplicationContext(), ResultList.class);
                startActivity(intent);
            }
        });
        initTab();
        initTextView();
    }

    private void initTab() {
        mTabLine = (ImageView) findViewById(R.id.tab_line);
        viewPager = (ViewPager) findViewById(R.id.viewPaper);

        DisplayMetrics dpMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dpMetrics);
        screenWidth = dpMetrics.widthPixels;
        //设置滑动条的宽度为屏幕的1/2(根据Tab的个数而定)
        lineParam = (LinearLayout.LayoutParams) mTabLine.getLayoutParams();
        mTabLine.setLayoutParams(lineParam);
        lineParam.width = screenWidth / 3;
        mTabLine.setLayoutParams(lineParam);
    }

    public void initTextView() {
        ForceOnOffTv = (TextView) findViewById(R.id.tv_force_onoff);
        SimpleControlTv = (TextView) findViewById(R.id.tv_simple_control);
        ControlTv=(TextView) findViewById(R.id.tv_simple_control1);
        ForceOnOffTv.setOnClickListener(new MyOnClickListener(0));
        SimpleControlTv.setOnClickListener(new MyOnClickListener(1));
        ControlTv.setOnClickListener(new MyOnClickListener(2));
    }

    public void initViewPager() {
        mForceOnOffFg = new FragmentForceOnOff();
        mFragmentList.add(mForceOnOffFg);
        mDSimpleControlFg = new DimmingFragmentSimpleControl();
        mFragmentList.add(mDSimpleControlFg);
        mTSimpleControlFg = new TimeCtrlFragmentSimpleControl();
        mFragmentList.add(mTSimpleControlFg);
//        switch (myTAG) {
//            case "Dimming":
//                mDSimpleControlFg = new DimmingFragmentSimpleControl();
//                mFragmentList.add(mDSimpleControlFg);
//                break;
//            case "TimeControl":
//                mTSimpleControlFg = new TimeCtrlFragmentSimpleControl();
//                mFragmentList.add(mTSimpleControlFg);
//                break;
//        }

        mFragmentAdapter = new FragmentAdapter(this.getSupportFragmentManager(), mFragmentList);
        viewPager.setAdapter(mFragmentAdapter);
        currentIndex = 0;
        viewPager.setCurrentItem(0);
        ForceOnOffTv.setTextColor(getResources().getColor(R.color.gray_shen));
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
    }

    //页卡切换监听
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        //state滑动中的状态 有三种状态（0，1，2） 1：正在滑动 2：滑动完毕 0：什么都没做。
        @Override
        public void onPageScrollStateChanged(int state) { }
        /**
         * position :当前页面，及你点击滑动的页面 offset:当前页面偏移的百分比
         * offsetPixels:当前页面偏移的像素位置
         */
        @Override
        public void onPageScrolled(int position, float offset, int offsetPixels) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLine.getLayoutParams();
            Log.e("offset:", offset + "");
            /**
             * 利用currentIndex(当前所在页面)和position(下一个页面)以及offset来
             * 设置mTabLineIv的左边距 滑动场景：
             * 记3个页面,
             * 从左到右分别为0,1,2
             * 0->1; 1->2; 2->1; 1->0
             */
            if (currentIndex == 0 && position == 0) {         // 0->1
                lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 3)
                        + currentIndex  * (screenWidth / 3));
            } else if (currentIndex == 1 && position == 0) {  // 1->0
                lp.leftMargin = (int) (-(1 - offset) * (screenWidth * 1.0 / 3)
                        + currentIndex * (screenWidth / 3));
            } else if (currentIndex == 1 && position == 1) {  // 1->2
                lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 3)
                        + currentIndex * (screenWidth / 3));
            }else if (currentIndex == 2 && position == 0) {  // 1->0
                lp.leftMargin = (int) (-(2 - offset) * (screenWidth * 1.0 / 3)
                        + currentIndex * (screenWidth / 3));
            } else if (currentIndex == 2&& position == 1) {  // 1->2
                lp.leftMargin = (int) (-(1 - offset)  * (screenWidth * 1.0 / 3)
                        + currentIndex * (screenWidth / 3));
            }else if (currentIndex == 2&& position == 2) {  // 1->2
                lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 3)
                        + currentIndex * (screenWidth / 3));
            }
            mTabLine.setLayoutParams(lp);
        }

        @Override
        public void onPageSelected(int position) {
            resetTextView();
            switch (position) {
                case 0:
                    ForceOnOffTv.setTextColor(getResources().getColor(R.color.gray_shen));
                    break;
                case 1:
                    SimpleControlTv.setTextColor(getResources().getColor(R.color.gray_shen));
                    break;
            }
            currentIndex = position;
        }

    }

    //头标点击监听
    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;
        public MyOnClickListener(int i) {
            index = i;
        }
        @Override
        public void onClick(View v) {
            viewPager.setCurrentItem(index);
        }
    }

    //重置颜色
    private void resetTextView() {
        ForceOnOffTv.setTextColor(getResources().getColor(R.color.gray_qian));
        SimpleControlTv.setTextColor(getResources().getColor(R.color.gray_qian));
    }

}
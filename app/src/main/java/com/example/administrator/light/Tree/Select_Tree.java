package com.example.administrator.light.Tree;

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
import com.example.administrator.light.LoginActivity;
import com.example.administrator.light.VollyQueue;
import com.example.administrator.light.fragment.FragmentAdapter;
import com.example.administrator.light.R;
import com.example.administrator.light.system.ResultList;
import com.example.administrator.light.system.Single_Dimming_TimeCtrl.*;
import com.example.administrator.light.Tree.*;
import com.example.administrator.light.util.SharedPreferencesUtils;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by binyejiang 2019/6/10.
 */
public class Select_Tree extends BaseActivity {
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
    private Tree_group tree_group;
    private Tree_shuchu mDSimpleControlFg;
    private Tree_dandeng mTSimpleControlFg;
    public String childname = null;//当前选定终端信息selNode
    private int DevNo = 0;

    private List<String> resultList = new ArrayList<String>();
    private int NumOfOperation = 0;
    public String myTAG = "SingleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_tree);
        init();
        initViewPager();
    }

    public int getDevNo() {
        return DevNo;
    }

    public String getMyTAG() {
        return myTAG;
    }

//    public FragmentForceOnOff getmForceOnOffFg() {
//        return mForceOnOffFg;
//    }


    private void init() {
        Intent intent = getIntent();
        childname ="1-1";
        myTAG ="Dimming";
        String[] temp_strs = childname.split("-");
        if(temp_strs.length >0) {
            try {
                DevNo = Integer.parseInt(temp_strs[0].trim());
                System.out.println(DevNo);
            }catch (Exception ex) {
            }
        }
        initTab();
        initTextView();
    }

    private void initTab() {
        mTabLine = (ImageView) findViewById(R.id.tab_line);
        viewPager = (ViewPager) findViewById(R.id.viewPaper);
        getToolbarTitle().setText("搜索");
        getSearchView().setVisibility(View.GONE);
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
        tree_group = new Tree_group();
        mFragmentList.add(tree_group);
        mDSimpleControlFg = new Tree_shuchu();
        mFragmentList.add(mDSimpleControlFg);
        mTSimpleControlFg = new Tree_dandeng();
        mFragmentList.add(mTSimpleControlFg);
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
                case 2:
                    ControlTv.setTextColor(getResources().getColor(R.color.gray_shen));
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
            SharedPreferencesUtils.setParam(Select_Tree.this, "pagerId", index);
        }
    }

    //重置颜色
    private void resetTextView() {
        ForceOnOffTv.setTextColor(getResources().getColor(R.color.gray_qian));
        SimpleControlTv.setTextColor(getResources().getColor(R.color.gray_qian));
        ControlTv.setTextColor(getResources().getColor(R.color.gray_qian));
    }

}
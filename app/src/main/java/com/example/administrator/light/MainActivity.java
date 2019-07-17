package com.example.administrator.light;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.light.customComponent.TitleView;
import com.example.administrator.light.fragment.FragmentAdapter;
import com.example.administrator.light.fragment.FragmentMap;
import com.example.administrator.light.fragment.FragmentMenu;
import com.example.administrator.light.fragment.FragmentAlarm;
import com.example.administrator.light.fragment.FragmentFind;
import com.example.administrator.light.fragment.FragmentMy;
import com.example.administrator.light.fragment.FragmentTree;

import java.util.ArrayList;
import java.util.List;

import q.rorbin.badgeview.QBadgeView;

public class MainActivity extends BaseActivity {

    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private FragmentAdapter mFragmentAdapter;

    private ViewPager viewPager;

    /**
     * Tab显示内容TextView
     */
    private TextView SystemTv, MapTv,OtherTv,UserTv,TreeTv;
    private LinearLayout tab_system,tab_map,tab_other,tab_user,tab_tree;

    /**
     * Tab的那个引导线
     */
    private ImageView mTabLine;

    /**
     * Fragment
     */
    private FragmentAlarm malarmFg;
    private FragmentFind mfindFg;
    private FragmentMap mMapFg;
    private FragmentMy mUserFg;
    private FragmentTree mTreeFg;

    /**
     * ViewPager的当前选中页
     */
    private int currentIndex;
    /**
     * 屏幕的宽度
     */
    private int screenWidth;

    private LinearLayout.LayoutParams lineParam ;

    private WindowManager mWindowManager = null;
    private WindowManager.LayoutParams wmParams = null;

    private BroadcastReceiver receiver;
    int idos=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        initTextView();
        initViewPager();

        //permission for 6.0
        if(ContextCompat.checkSelfPermission(MainActivity.this , Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},0);
        }

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent1) {
                setMenuVisibility(View.GONE);
                viewPager.setCurrentItem(5);
                MapTv.setTextColor(getResources().getColor(R.color.green));
            }
        };

    }
    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter("com.example.administrator.light.fragment.BROADCAST");
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void init(){
        mTabLine = (ImageView)findViewById(R.id.tab_line);
        viewPager = (ViewPager)findViewById(R.id.viewPaper);
        //设置标题栏
      //  getToolbarTitle().setText("智能路灯");
        getToolbarTitle().setVisibility(View.GONE);
        getToolbarLeftBt().setVisibility(View.GONE);
        Intent intent = getIntent();
        idos = intent.getIntExtra("fragid",-1);
        DisplayMetrics dpMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay()
                .getMetrics(dpMetrics);
        screenWidth = dpMetrics.widthPixels;

        //设置滑动条的宽度为屏幕的1/5(根据Tab的个数而定)

        lineParam =  (LinearLayout.LayoutParams) mTabLine
                .getLayoutParams();
        mTabLine.setLayoutParams(lineParam);
        lineParam.width = screenWidth / 5;
        mTabLine.setLayoutParams(lineParam);
    }

    public void initTextView(){
        tab_system= (LinearLayout)findViewById(R.id.tab_system);
        tab_map= (LinearLayout)findViewById(R.id.tab_map);
        tab_other= (LinearLayout)findViewById(R.id.tab_other);
        tab_tree= (LinearLayout)findViewById(R.id.tab_tree);
        tab_user= (LinearLayout)findViewById(R.id.tab_User);
        SystemTv = (TextView)findViewById(R.id.tv_system);
        OtherTv = (TextView)findViewById(R.id.tv_other);
        MapTv = (TextView)findViewById(R.id.tv_map);
        UserTv = (TextView)findViewById(R.id.tv_User);
        TreeTv = (TextView)findViewById(R.id.tv_tree);
        QBadgeView ssa= (QBadgeView) new QBadgeView(this).bindTarget(tab_system);
        ssa.setBadgeNumber(1);
        tab_tree.setOnClickListener(new MyOnClickListener(0));
        tab_system.setOnClickListener(new MyOnClickListener(1));
        tab_other.setOnClickListener(new MyOnClickListener(2));
        tab_map.setOnClickListener(new MyOnClickListener(3));
        tab_user.setOnClickListener(new MyOnClickListener(4));

    }

    public void initViewPager(){
        malarmFg = new FragmentAlarm();
        mfindFg = new FragmentFind();
        mMapFg = new FragmentMap();
        mUserFg=new FragmentMy();
        mTreeFg=new FragmentTree();
        mFragmentList.add(mTreeFg);
        mFragmentList.add(malarmFg);
        mFragmentList.add(mfindFg);
        mFragmentList.add(mMapFg);
        mFragmentList.add(mUserFg);
        mFragmentAdapter = new FragmentAdapter(this.getSupportFragmentManager(), mFragmentList);
        mFragmentAdapter.notifyDataSetChanged();
        viewPager.setAdapter(mFragmentAdapter);
        viewPager.setOffscreenPageLimit(5);
        currentIndex = idos;
        viewPager.setCurrentItem(idos);
        TreeTv.setTextColor(getResources().getColor(R.color.green));
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
}


    /**
     * 页卡切换监听
     */
    public class MyOnPageChangeListener implements OnPageChangeListener {

        /**
         * state滑动中的状态 有三种状态（0，1，2） 1：正在滑动 2：滑动完毕 0：什么都没做。
         */
        @Override
        public void onPageScrollStateChanged(int state) {

        }

        /**
         * position :当前页面，及你点击滑动的页面 offset:当前页面偏移的百分比
         * offsetPixels:当前页面偏移的像素位置
         */
        @Override
        public void onPageScrolled(int position, float offset,
                                   int offsetPixels) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLine
                    .getLayoutParams();

            Log.e("offset:", offset + "");
            /**
             * 利用currentIndex(当前所在页面)和position(下一个页面)以及offset来
             * 设置mTabLineIv的左边距 滑动场景：
             * 记4个页面,
             * 从左到右分别为0,1,2,3
             * 0->1; 1->2;2->3;3-> ;2->1; 1->0
             */

            if (currentIndex == 0 && position == 0) {         // 0->1
                lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 4)
                        + currentIndex  * (screenWidth / 5));
            } else if (currentIndex == 1 && position == 0) {  // 1->0
                lp.leftMargin = (int) (-(1 - offset) * (screenWidth * 1.0 / 5)
                        + currentIndex * (screenWidth / 5));
            } else if (currentIndex == 1 && position == 1) {  // 1->2
                lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 5)
                        + currentIndex * (screenWidth / 5));
            }else if (currentIndex == 2 && position == 0) {  // 1->0
                lp.leftMargin = (int) (-(2 - offset) * (screenWidth * 1.0 / 5)
                        + currentIndex * (screenWidth /5));
            } else if (currentIndex == 2&& position == 1) {  // 1->2
                lp.leftMargin = (int) (-(1 - offset)  * (screenWidth * 1.0 / 5)
                        + currentIndex * (screenWidth / 5));
            }else if (currentIndex == 2&& position == 2) {  // 1->2
                lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 5)
                        + currentIndex * (screenWidth / 5));
            }else if (currentIndex == 3 && position == 0) {  // 1->0
                lp.leftMargin = (int) (-(3 - offset) * (screenWidth * 1.0 / 5)
                        + currentIndex * (screenWidth / 5));
            } else if (currentIndex == 3&& position == 1) {  // 1->2
                lp.leftMargin = (int) (-(2 - offset)  * (screenWidth * 1.0 / 5)
                        + currentIndex * (screenWidth / 5));
            }else if (currentIndex == 3&& position == 2) {  // 1->2
                lp.leftMargin = (int) (-(1 - offset) * (screenWidth * 1.0 / 5)
                        + currentIndex * (screenWidth / 5));
            }else if (currentIndex == 3&& position == 3) {  // 1->2
                lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 5)
                        + currentIndex * (screenWidth / 5));
            }
            else if (currentIndex == 4 && position == 0) {  // 1->0
                lp.leftMargin = (int) (-(4 - offset) * (screenWidth * 1.0 / 5)
                        + currentIndex * (screenWidth / 5));
            } else if (currentIndex == 4&& position == 1) {  // 1->2
                lp.leftMargin = (int) (-(3 - offset)  * (screenWidth * 1.0 / 5)
                        + currentIndex * (screenWidth / 5));
            }else if (currentIndex == 4&& position == 2) {  // 1->2
                lp.leftMargin = (int) (-(2 - offset) * (screenWidth * 1.0 / 5)
                        + currentIndex * (screenWidth / 5));
            }else if (currentIndex == 4&& position == 3) {  // 1->2
                lp.leftMargin = (int) (-(1 - offset) * (screenWidth * 1.0 / 5)
                        + currentIndex * (screenWidth / 5));
            }else if (currentIndex == 4&& position == 4) {  // 1->2
                lp.leftMargin = (int) ( offset * (screenWidth * 1.0 / 5)
                        + currentIndex * (screenWidth / 5));
            }

            mTabLine.setLayoutParams(lp);
        }

        @Override
        public void onPageSelected(int position) {
            resetTextView();

            switch (position) {
                case 0:
                    TreeTv.setTextColor(getResources().getColor(R.color.green));
                    break;
                case 1:
                    SystemTv.setTextColor(getResources().getColor(R.color.green));
                    break;
                case 2:
                    OtherTv.setTextColor(getResources().getColor(R.color.green));
                    break;
                case 3:
                    MapTv.setTextColor(getResources().getColor(R.color.green));
                    break;
                case 4:
                    UserTv.setTextColor(getResources().getColor(R.color.green));
                    break;
            }
            currentIndex = position;
        }

    }

    /**
     * 头标点击监听
     */
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
    /**
     * 重置颜色
     */
    private void resetTextView() {
        SystemTv.setTextColor(getResources().getColor(R.color.gray_qian));
        OtherTv.setTextColor(getResources().getColor(R.color.gray_qian));
        MapTv.setTextColor(getResources().getColor(R.color.gray_qian));
        UserTv.setTextColor(getResources().getColor(R.color.gray_qian));
        TreeTv.setTextColor(getResources().getColor(R.color.gray_qian));
    }

    /*public class MyReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context , Intent intent1)
        {
            Toast.makeText(context,"接收到的action为："+intent1.getAction(),Toast.LENGTH_SHORT).show();
            menuViewPager.setVisibility(View.GONE);
            img_Float.setImageResource(R.drawable.add);
            clickTime = 0;
            viewPager.setCurrentItem(3);
            MapTv.setTextColor(getResources().getColor(R.color.gray_shen));

            <receiver android:name=".MainActivity$MyReceiver">
            <intent-filter>
                <action android:name="com.example.administrator.light.fragment.BROADCAST"/>
            </intent-filter>
        </receiver>
        }
    }*/


}
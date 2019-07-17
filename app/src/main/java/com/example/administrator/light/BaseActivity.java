package com.example.administrator.light;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.light.fragment.FragmentAdapter;
import com.example.administrator.light.fragment.FragmentMenu;
import com.example.administrator.light.fragment.FragmentSao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by binyejiang on 2019/5/10.
 */
public class BaseActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    //ToolBar
    private Button toolbarLeftBt;
    private TextView toolbarTitle;
    private TextView toolbarRightTv;
    private Button toolbar,mune;
    private Button searchh;
    private ListView listsearchView;

    //Floating Button
    private FloatingActionButton floatBt;
    boolean isShow = false;
    private LinearLayout containterLayout;

    //Menu
    private List<Fragment> menuFragmentList = new ArrayList<Fragment>();
    private List<Fragment> saoFragmentList = new ArrayList<Fragment>();
    private FragmentAdapter menuFragmentAdapter,saoFragmentAdapter;
    private ViewPager menuViewPager;
    private ViewPager saoViewPager;
    private FragmentMenu mMenuFg = null;
    private FragmentSao SaoFg = null;
    private String[] mStrs = {"kk", "kk", "wskx", "wksx"};

    //获取ToolBar上的控件对象
    public Button getToolbarLeftBt() {
        return toolbarLeftBt;
    }

    public TextView getToolbarRightTv() {
        return toolbarRightTv;
    }

    public TextView getToolbarTitle() {
        return toolbarTitle;
    }
    public Button getToolbar() {
        return toolbar;
    }
    public Button getSearchView() {
        return searchh;
    }
    public Button getMune() {return mune;}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        init();
    }
    // 用户输入字符时激发的方法
    @Override
    public boolean onQueryTextChange(String newText) {
        // TODO 自动生成的方法存根
        if(TextUtils.isEmpty(newText)) {
            //清除listview的过滤
            listsearchView.clearTextFilter();
        }
        else {
            //用用户输入的内容对listview的列表项进行过滤
            listsearchView.setFilterText(newText);
        }
        return true;
    }

    //用户单击搜索按钮时激发的方法
    @Override
    public boolean onQueryTextSubmit(String qurery) {
        //这里仅仅是模拟，实际中应该在该方法中进行查询，然后将结果得到
        Toast.makeText(BaseActivity.this, "你搜索的是："+qurery, Toast.LENGTH_SHORT).show();
        return false;
    }
    // 初始化控件
    private void init() {
        //ToolBar
        toolbarLeftBt = (Button) findViewById(R.id.toolbarLeftBt);
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        toolbarRightTv = (TextView) findViewById(R.id.toolbarRightTv);
        toolbar = (Button) findViewById(R.id.toolbar);

        //Floating Button
      //  floatBt = (FloatingActionButton) findViewById(R.id.popuBt);
        mune=(Button)findViewById(R.id.mune);
        //Menu
        menuViewPager = (ViewPager) findViewById(R.id.menuViewPaper);
        saoViewPager = (ViewPager) findViewById(R.id.saoViewPaper);
        searchh = (Button) findViewById(R.id.searchh);
        listsearchView = (ListView) findViewById(R.id.listsearchView);
        listsearchView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mStrs));
        listsearchView.setTextFilterEnabled(true);
        // 设置监听
        toolbarLeftBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        searchh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent= new Intent(BaseActivity.this,BaseSearch.class);
                startActivity(intent);
            }
        });

        mune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShow) {
                    setMenuVisibility(View.GONE);
                } else {
                    setMenuVisibility(View.VISIBLE);
                }
            }
        });
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShow) {
                    saoViewPager.setVisibility(View.GONE);
                    isShow = false;
                } else {
                    if (SaoFg == null) {
                        SaoFg = new FragmentSao();
                        Bundle bundle = new Bundle();
                        String str = getToolbarTitle().getText().toString();
                        if(str.contains("-")) {
                            bundle.putString("childname", str);
                        } else {
                            bundle.putString("childname", "");
                        }
                        SaoFg.setArguments(bundle);
                        saoFragmentList.add(SaoFg);
                        saoFragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), saoFragmentList);
                        saoFragmentAdapter.notifyDataSetChanged();
                        saoViewPager.setAdapter(saoFragmentAdapter);
                        saoViewPager.setCurrentItem(0);
                    }
                    saoViewPager.setVisibility(View.VISIBLE);
                    isShow = true;
                }
            }
        });
    }
    //点击屏幕 关闭输入弹出框
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager im = (InputMethodManager)getSystemService(BaseActivity.this.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        return super.onTouchEvent(event);
    }
    //操作Floating Button
    public void setMenuVisibility(int visibility) {
        if(visibility == View.GONE) {
            mMenuFg = null;
            this.menuViewPager.setVisibility(View.GONE);
          //  floatBt.setImageResource(R.drawable.add);
            isShow = false;
        } else if(visibility == View.VISIBLE) {
            if (mMenuFg == null) {
                mMenuFg = new FragmentMenu();
                Bundle bundle = new Bundle();
                String str = getToolbarTitle().getText().toString();
                if(str.contains("-")) {
                    bundle.putString("childname", str);
                } else {
                    bundle.putString("childname", "");
                }
                mMenuFg.setArguments(bundle);
                menuFragmentList.add(mMenuFg);
                menuFragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), menuFragmentList);
                menuFragmentAdapter.notifyDataSetChanged();
                menuViewPager.setAdapter(menuFragmentAdapter);
                menuViewPager.setCurrentItem(0);
            }
            menuViewPager.setVisibility(View.VISIBLE);
            isShow = true;
        }

    }

    @Override
    public void setContentView(int layoutResID) {
        containterLayout = (LinearLayout)findViewById(R.id.containerLayout);
        containterLayout.removeAllViews();
        View childLayout = this.getLayoutInflater().inflate(layoutResID, null);
        ViewGroup.LayoutParams childParams =  new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        containterLayout.addView(childLayout, childParams);
    }

}

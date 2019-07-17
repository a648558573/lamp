package com.example.administrator.light.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by binyejiang on 2019/5/21.
 */
public class FragmentAdapter extends FragmentPagerAdapter {

    List<Fragment> mFragments = new ArrayList<Fragment>();
    //构造方法一 推荐
    public FragmentAdapter(FragmentManager fm) {
        super(fm);

//        this.mFragments.add(new HomeClassesFragment());
//        this.mFragments.add(new HomeAttendanceFragment());
//        this.mFragments.add(new HomeExamFragment());
//        this.mFragments.add(new HomeGradeFragment());
    }
    public FragmentAdapter(FragmentManager fm,List<Fragment> fragmentList) {
        super(fm);
        mFragments = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

}
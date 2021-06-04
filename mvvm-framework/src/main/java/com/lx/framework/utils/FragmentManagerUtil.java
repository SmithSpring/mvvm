package com.lx.framework.utils;

import android.widget.RadioGroup;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentManagerUtil implements RadioGroup.OnCheckedChangeListener {
    /**Fragment集合*/
    private List<Fragment> fragments; // 一个tab页面对应一个Fragment
    /**RadioGroup对象，用于切换tab*/
    private RadioGroup rgs; // 用于切换tab
    /**Fragment管理者对象*/
    private FragmentManager fragmentManager; // Fragment所属的Activity
    /**Fragment父容器*/
    private int fragmentContainerId; // Activity中所要被替换的区域的id
    /**当前Tab页面索引*/
    private int currentTab;

    /**
     * 构造方法 使用时调用此方法就可以
     *
     * @version 1.0
     *
     * @createTime 2015/12/10,11:01
     * @updateTime 2015/12/10,11:01
     * @createAuthor
     * @updateAuthor
     * @updateInfo (此处输入修改内容,若无修改可不写.)
     * @param fragmentManager FragmentManager管理器
     * @param fragments Fragment对象的List集合
     * @param fragmentContainerId  fragment父容器
     * @param rgs RadioGroup对象
     */
    public FragmentManagerUtil(FragmentManager fragmentManager, List<Fragment> fragments, int fragmentContainerId, RadioGroup rgs) {
        this.fragments = fragments;
        this.rgs = rgs;
        this.fragmentManager = fragmentManager;
        this.fragmentContainerId = fragmentContainerId;
        rgs.setOnCheckedChangeListener(this);
    }

    public void check(int i){
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment fragment = fragments.get(i);
        if (fragment.isAdded()) {
            fragment.onStart();
        } else {
            ft.add(fragmentContainerId, fragment);
            ft.commit();
        }
    }


    /**
     * 监听RadioGroup中RadioButton是否改变了。
     *
     * @version 1.0
     *
     * @createTime 2015/12/10,11:03
     * @updateTime 2015/12/10,11:03
     * @createAuthor
     * @updateAuthor
     * @updateInfo (此处输入修改内容,若无修改可不写.)
     * @param radioGroup
     * @param checkedId
     */
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        for (int i = 0; i < rgs.getChildCount(); i++) {
            if (rgs.getChildAt(i).getId() == checkedId) {
                Fragment fragment = fragments.get(i);
                FragmentTransaction ft = obtainFragmentTransaction(i);
                //getCurrentFragment().onPause(); // 暂停当前tab
                getCurrentFragment().onStop(); // 暂停当前tab
                if (fragment.isAdded()) {
                    fragment.onStart(); // 启动目标tab的fragment onStart()
                    // fragment.onResume(); // 启动目标tab的onResume()
                } else {
                    ft.add(fragmentContainerId, fragment);
                    ft.commit();
                }
                showTab(i); // 显示目标tab
                break;
            }
        }
    }


    /**
     * 切换tab
     *
     * @version 1.0
     *
     * @createTime 2015/12/10,11:21
     * @updateTime 2015/12/10,11:21
     * @createAuthor
     * @updateAuthor
     * @updateInfo (此处输入修改内容,若无修改可不写.)
     * @param idx 需要切换到的Fragment对象
     */
    private void showTab(int idx) {
        for (int i = 0; i < fragments.size(); i++) {
            Fragment fragment = fragments.get(i);
            FragmentTransaction ft = obtainFragmentTransaction(idx);
            if (idx == i) {
                ft.show(fragment);
            } else {
                ft.hide(fragment);
            }
            ft.commit();
        }
        currentTab = idx; // 更新目标tab为当前tab
    }


    /**
     * 获取一个带动画的FragmentTransaction
     *
     * @version 1.0
     *
     * @createTime 2015/12/11,11:19
     * @updateTime 2015/12/11,11:19
     * @createAuthor
     * @updateAuthor
     * @updateInfo (此处输入修改内容,若无修改可不写.)
     * @param index
     * @return  一个带动画的FragmentTransaction对象
     */
    private FragmentTransaction obtainFragmentTransaction(int index) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        return ft;
    }


    /**
     *
     * return当前显示的Fragment对象索引。
     * @version 1.0
     *
     * @createTime 2015/12/11,11:18
     * @updateTime 2015/12/11,11:18
     * @createAuthor
     * @updateAuthor
     * @updateInfo (此处输入修改内容,若无修改可不写.)
     * @return当前显示的Fragment对象。
     */
    public int getCurrentTab() {
        return currentTab;
    }


    /**
     * 获取当前的Fragment对象
     *
     * @version 1.0
     *
     * @createTime 2015/12/11,11:10
     * @updateTime 2015/12/11,11:10
     * @createAuthor
     * @updateAuthor
     * @updateInfo (此处输入修改内容,若无修改可不写.)
     * @return  获取当前的Fragment对象
     */
    public Fragment getCurrentFragment() {
        return fragments.get(currentTab);
    }
}

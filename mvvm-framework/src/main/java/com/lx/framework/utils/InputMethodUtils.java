package com.lx.framework.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class InputMethodUtils {
    /**
     * 判断输入法是否打开
     * @param activity 当前Activity
     * @return true 打开
     */
    public static boolean isOpenInputMethod(Activity activity){
        InputMethodManager imm =(InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive();
    }

    /**
     * 显示软键盘
     *
     * @param activity 当前Activity
     */
    public static void showInputMethod(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 隐藏软键盘
     *
     * @param activity 当前Activity
     */
    public static void hideInputMethod(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow( activity.getWindow().getDecorView().getWindowToken(), 0);
    }
}

package com.lx.framework.utils;

import android.text.TextUtils;

public class Configure {
    private static String url;
    private static int code;

    private Configure() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void setUrl(String url,int code){
        Configure.url = url;
        Configure.code = code;
    }

    public static String getUrl() {
        if (!TextUtils.isEmpty(url)) {
            return url;
        }
        throw new NullPointerException("should be set in net url");
    }

    public static int getCode() {
        return code;
    }
}

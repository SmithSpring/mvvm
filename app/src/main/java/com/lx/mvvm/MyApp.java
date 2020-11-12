package com.lx.mvvm;

import com.lx.framework.base.BaseApplication;

import java.io.File;

public class MyApp extends BaseApplication {
    public static String FILE_DIR;

    @Override
    public void onCreate() {
        super.onCreate();
        FILE_DIR = new File(getFilesDir(), "tbs").getAbsolutePath() + File.separator;
    }
}

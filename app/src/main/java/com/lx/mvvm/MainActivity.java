package com.lx.mvvm;

import android.os.Bundle;
import android.view.View;

import com.artifex.mupdflib.ui.AttachPreviewActivity;
import com.lx.framework.utils.FileUtils;
import com.lx.framework.utils.KLog;
import com.lx.framework.utils.Utils;

import java.io.File;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static com.lx.mvvm.MyApp.FILE_DIR;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
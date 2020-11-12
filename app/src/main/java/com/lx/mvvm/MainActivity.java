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

    @Override
    public void onResume() {
        super.onResume();
        try {
            FileUtils.copyAssetsDir(Utils.getContext(), "tbs", FILE_DIR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openfile(View view) {
        openFileWithTbs(getFilePath("abc.pdf"));
    }

    @NonNull
    private String getFilePath(String fileName) {
        return new File(FILE_DIR + fileName).getAbsolutePath();
    }

    private void openFileWithTbs(String filePath) {
        KLog.d("lixiong", "Open File: " + filePath);
//        startFragment(TBSFileViewFragment.getInstance(filePath));
        AttachPreviewActivity.startActivity(this,filePath);
    }
}
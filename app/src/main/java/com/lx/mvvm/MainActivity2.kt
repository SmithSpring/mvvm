package com.lx.mvvm

import android.os.Bundle
import com.lx.framework.base.BaseActivity
import com.lx.mvvm.databinding.ActivityMainBinding

class MainActivity2 : BaseActivity<ActivityMainBinding?, MainViewModel?>() {
    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_main
    }
}
package com.lx.mvvm

import android.app.Application
import androidx.databinding.ObservableField
import com.lx.framework.base.BaseModel
import com.lx.framework.base.BaseViewModel
import com.lx.framework.binding.command.BindingAction
import com.lx.framework.binding.command.BindingCommand

class MainViewModel(application: Application) : BaseViewModel<BaseModel?>(application) {
    var title = ObservableField<String>("我的第一个页面")
    var backCommand = BindingCommand<Void>(BindingAction { finish() })
}
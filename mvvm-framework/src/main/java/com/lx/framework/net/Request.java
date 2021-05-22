package com.lx.framework.net;

import android.annotation.SuppressLint;
import android.app.Activity;

import com.lx.framework.base.BaseViewModel;
import com.lx.framework.http.ResponseThrowable;
import com.lx.framework.utils.RxUtils;
import com.lx.framework.utils.ToastUtils;

import io.reactivex.rxjava3.functions.Consumer;

/**
 * 网络请求
 *
 * @param <T> service interface
 * @param <K> 返回的数据类型
 */
public class Request<T, K> {

    /**
     * 可自定义code 封装处理    继承 ApiDisposableObserver
     */
    @SuppressLint("CheckResult")
    public void request(Activity activity, BaseViewModel viewModel, Class<T> service, IMethod<T, K> method, IResponse<K> iResponse) {
        method.method(RetrofitClient.getInstance().create(service))
                .compose(RxUtils.bindToLifecycle(viewModel.getLifecycleProvider())) // 请求与View周期同步
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe((Consumer<K>) iResponse::onSuccess, (Consumer<ResponseThrowable>) throwable -> {
                    iResponse.onError(throwable.message);
                    if (throwable.getCause() instanceof ResultException){
                        ResultException resultException = (ResultException) throwable.getCause();
                        ToastUtils.showLong(resultException.getErrMsg());
                    }
                });
    }
}

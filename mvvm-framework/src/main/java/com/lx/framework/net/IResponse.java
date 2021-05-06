package com.lx.framework.net;

public interface IResponse<T> {
    public void onSuccess(T t);

    public void onError(String msg);
}

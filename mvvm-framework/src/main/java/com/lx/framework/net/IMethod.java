package com.lx.framework.net;

import io.reactivex.rxjava3.core.Observable;

public interface IMethod<T,K> {
    Observable<K> method(T t);
}

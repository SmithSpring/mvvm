package com.lx.framework.net;

import io.reactivex.rxjava3.core.Observable;

interface IMethod<T,K> {
    Observable<K> method(T t);
}

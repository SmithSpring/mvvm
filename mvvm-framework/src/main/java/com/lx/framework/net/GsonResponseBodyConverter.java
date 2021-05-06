package com.lx.framework.net;

import com.google.gson.Gson;
import com.lx.framework.model.EntityResponse;
import com.lx.framework.utils.KLog;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class GsonResponseBodyConverter < T > implements Converter<ResponseBody,
        T > {
    private final Gson gson;
    private final Type type;

    GsonResponseBodyConverter(Gson gson, Type type) {
        this.gson = gson;
        this.type = type;
    }

    /**
     * 针对数据返回成功、错误不同类型字段处理
     */
    @Override public T convert(ResponseBody value) throws IOException {
        String response = value.string();
        try {
            // 这里的type实际类型是 LoginUserEntity<User>  User就是user字段的对象。
            EntityResponse result = gson.fromJson(response, EntityResponse.class);
            int code = result.getCode();
            if (code == 10000) {
                return gson.fromJson(response, type);
            } else {
                KLog.d("HttpManager", "err==：" + response);
                EntityResponse<String> errResponse = gson.fromJson(response, EntityResponse.class);
                if (code == 10001) {
                    throw new ResultException(errResponse.getContent(), code);
                } else {
                    throw new ResultException(errResponse.getContent(), code);
                }
            }
        } finally {
            value.close();
        }
    }
}
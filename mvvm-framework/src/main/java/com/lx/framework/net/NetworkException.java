package com.lx.framework.net;

import java.io.IOException;

public class NetworkException extends IOException {

    private String errMsg;
    private int errCode;

    public NetworkException(String errMsg, int errCode){
        this.errMsg = errMsg;
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }
}

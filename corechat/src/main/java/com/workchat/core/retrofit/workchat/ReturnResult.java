package com.workchat.core.retrofit.workchat;

import java.io.Serializable;

public class ReturnResult implements Serializable {
    public static final int SUCCESS = 0;
    public static final int ACCOUNT_NOT_EXISTS = 102;


    public static final String ERROR_CODE_TAG = "errorCode";
    public static final String ERROR_MESSAGE_TAG = "error";
    public static final String DATA_TAG = "data";


    private int errorCode = -1;
    private String error = "";
    private Object data;//parse json return

    //////////////////////////////////////////////////////////////////////////////////////////////////
    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return error;
    }

    public void setErrorMessage(String errorMessage) {
        this.error = errorMessage;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }


}

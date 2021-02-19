package com.topceo.retrofit;

import java.io.Serializable;

public class ReturnResultWc implements Serializable {
    public static final int SUCCESS = 0;
    public static final int ACCOUNT_NOT_EXISTS = 100;


    public static final String ERROR_CODE_TAG = "ErrorCode";
    public static final String ERROR_MESSAGE_TAG = "Message";
    public static final String DATA_TAG = "Data";


    private int ErrorCode = -1;
    private String Message = "";
    private Object Data;//parse json return

    public int getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(int errorCode) {
        ErrorCode = errorCode;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public Object getData() {
        return Data;
    }

    public void setData(Object data) {
        Data = data;
    }
}

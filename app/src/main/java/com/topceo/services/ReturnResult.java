package com.topceo.services;

import java.io.Serializable;

public class ReturnResult implements Serializable {
    public static final int SUCCESS = 0;
    public static final int ERROR_CODE_UNAUTHORIZED = 401;
    public static final int ERROR_CODE_BANNED = 106;
    public static final int ERROR_CODE_SIGNUP = 107;
    public static final int ERROR_CODE_PARAMS = 100;
    public static final int ERROR_CODE_DATA = 104;

    public static final int ERROR_CODE_COMPLETE_SIGNUP = 1;


    private int errorCode = -1;
    private String errorMessage = "";
    private String error = ""; //api ben corechat tra ve ~errorMessage
    private Object data;//parse json return


    //////////////////////////////////////////////////////////////////////////////////////////////////
    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

package com.topceo.ads;

import java.io.Serializable;

public class ReturnResult implements Serializable {
	public static final int ERROR_CODE_THANH_CONG=0;
	public static final int ERROR_CODE_EXCEPTION=115;
	public static final int ERROR_CODE_100_KHONG_CO_QUYEN_TRUY_CAP_WEB_SERVICE=100;//10x: là các lỗi khác, tương ứng với ErrorMessage
	
	

	private int ErrorCode=-1;
	private String ErrorMessage="";
	private Object Data;//parse json return
	
	
	public int getErrorCode() {
		return ErrorCode;
	}
	public void setErrorCode(int errorCode) {
		ErrorCode = errorCode;
	}
	public String getErrorMessage() {
		return ErrorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		ErrorMessage = errorMessage;
	}
	public Object getData() {
		return Data;
	}
	public void setData(Object data) {
		Data = data;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
}

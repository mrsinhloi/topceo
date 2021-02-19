package com.workchat.core.retrofit.workchat;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.lang.reflect.Type;

public class Parser {
    /**
     * @param obj
     * @param type Type collectionType = new TypeToken<List<WorkspaceMemberModel>>(){}.getType();
     * @return
     */
    public static ReturnResult parseJson(JSONObject obj, Type type, boolean isArray) {
        ReturnResult result = null;

        try {
            result = new ReturnResult();

            if (!obj.isNull(ReturnResult.ERROR_CODE_TAG)) {
                int errorCode = obj.getInt(ReturnResult.ERROR_CODE_TAG);
                result.setErrorCode(errorCode);
            }

            if (!obj.isNull(ReturnResult.ERROR_MESSAGE_TAG)) {
                String errorMessage = obj.getString(ReturnResult.ERROR_MESSAGE_TAG);
                result.setErrorMessage(errorMessage);
            }

            if (type == String.class) {
                if (!obj.isNull(ReturnResult.DATA_TAG)) {
                    String data = obj.getString(ReturnResult.DATA_TAG);
                    result.setData(data);
                }
            } else if (type == Boolean.class) {
                if (!obj.isNull(ReturnResult.DATA_TAG)) {
                    String data = obj.getString(ReturnResult.DATA_TAG);
                    try {
                        result.setData(Boolean.parseBoolean(data));
                    } catch (Exception e) {
                        result.setData(null);
                    }
                }
            } else {
                if (!obj.isNull(ReturnResult.DATA_TAG) && type != null) {
                    if (isArray) {
                        String data = obj.getString(ReturnResult.DATA_TAG);
                        Object object = new Gson().fromJson(data, type);
                        result.setData(object);
                    } else {//object
                        String data = obj.getString(ReturnResult.DATA_TAG);
                        Object object = new Gson().fromJson(data, type);
                        result.setData(object);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

    public static ReturnResult parseJson(String response, Type type, boolean isArray) {
        ReturnResult result = null;

        try {
            result = new ReturnResult();
            JSONObject obj = new JSONObject(response);
            result = parseJson(obj, type, isArray);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }


}

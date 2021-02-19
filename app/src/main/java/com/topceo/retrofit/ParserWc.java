package com.topceo.retrofit;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.lang.reflect.Type;

public class ParserWc {
    /**
     * @param obj
     * @param type Type collectionType = new TypeToken<List<WorkspaceMemberModel>>(){}.getType();
     * @return
     */
    public static ReturnResultWc parseJson(JSONObject obj, Type type, boolean isArray) {
        ReturnResultWc result = null;

        try {
            result = new ReturnResultWc();

            if (!obj.isNull(ReturnResultWc.ERROR_CODE_TAG)) {
                int errorCode = obj.getInt(ReturnResultWc.ERROR_CODE_TAG);
                result.setErrorCode(errorCode);
            }

            if (!obj.isNull(ReturnResultWc.ERROR_MESSAGE_TAG)) {
                String errorMessage = obj.getString(ReturnResultWc.ERROR_MESSAGE_TAG);
                result.setMessage(errorMessage);
            }

            if (type == String.class) {
                if (!obj.isNull(ReturnResultWc.DATA_TAG)) {
                    String data = obj.getString(ReturnResultWc.DATA_TAG);
                    result.setData(data);
                }
            } else if (type == Boolean.class) {
                if (!obj.isNull(ReturnResultWc.DATA_TAG)) {
                    String data = obj.getString(ReturnResultWc.DATA_TAG);
                    try {
                        result.setData(Boolean.parseBoolean(data));
                    } catch (Exception e) {
                        result.setData(null);
                    }
                }
            } else {
                if (!obj.isNull(ReturnResultWc.DATA_TAG) && type != null) {
                    if (isArray) {
                        String data = obj.getString(ReturnResultWc.DATA_TAG);
                        Object object = new Gson().fromJson(data, type);
                        result.setData(object);
                    } else {//object
                        String data = obj.getString(ReturnResultWc.DATA_TAG);
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

    public static ReturnResultWc parseJson(String response, Type type, boolean isArray) {
        ReturnResultWc result = null;

        try {
            result = new ReturnResultWc();
            JSONObject obj = new JSONObject(response);
            result = parseJson(obj, type, isArray);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }


}

package com.workchat.core.utils;

import android.text.TextUtils;

import com.workchat.core.config.ChatApplication;
import com.workchat.core.models.realm.UserInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

public class PhoneUtils {

    public static PhoneNumberUtil phoneUtil = PhoneNumberUtil.createInstance(ChatApplication.instance.getApplicationContext());

    public static String getE164FormattedMobileNumber(String mobile, String locale) {
        if (!TextUtils.isEmpty(mobile)) {
            try {
                Phonenumber.PhoneNumber phoneProto = phoneUtil.parse(mobile, locale);
                if (phoneUtil.isValidNumber(phoneProto)
                        && phoneUtil.isPossibleNumberForType(phoneProto, PhoneNumberUtil.PhoneNumberType.MOBILE)) {
                    return phoneUtil.format(phoneProto, PhoneNumberUtil.PhoneNumberFormat.E164);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getNationalFormattedMobileNumber(String mobile, String locale) {
        if (!TextUtils.isEmpty(mobile)) {
            try {
                Phonenumber.PhoneNumber phoneProto = phoneUtil.parse(mobile, locale);
                if (phoneUtil.isValidNumber(phoneProto)
                        && phoneUtil.isPossibleNumberForType(phoneProto, PhoneNumberUtil.PhoneNumberType.MOBILE)) {
                    String nationalHaveSpace =  phoneUtil.format(phoneProto, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
                    nationalHaveSpace = MyUtils.getPhoneNumberOnly(nationalHaveSpace);
                    return nationalHaveSpace;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * @param phone
     * @param countryCode or "" or null
     * @return
     */
    public static boolean isValidNumber(String phone, String countryCode) {
        boolean isValid = false;
        if (!TextUtils.isEmpty(phone)) {
            PhoneNumberUtil phoneUtils = PhoneNumberUtil.createInstance(ChatApplication.instance.getApplicationContext());
            try {
                Phonenumber.PhoneNumber phoneNumber = phoneUtils.parse(phone, countryCode);
                isValid = phoneUtils.isValidNumber(phoneNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return isValid;
    }

    public static boolean isValidNumber(String phone) {
        boolean isValid = false;
        if (!TextUtils.isEmpty(phone)) {
            String countryCode = getDefaultCountryNameCode();
            PhoneNumberUtil phoneUtils = PhoneNumberUtil.createInstance(ChatApplication.instance.getApplicationContext());
            try {
                Phonenumber.PhoneNumber phoneNumber = phoneUtils.parse(phone, countryCode);
                isValid = phoneUtils.isValidNumber(phoneNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return isValid;
    }

//    private String expectedMobileNumber = "+84938936128";
//    private List<String> sucessMobileNumbers;
//    private List<String> failMobileNumbers;

    public static void testPhoneNumber() {
        List<String> sucessMobileNumbers =
                Arrays.asList(
                        "+84938936128",
                        "+84 938936128",
                        "84938936128",
                        "0938936128",
                        "938936128",
                        "938 936 128");
        List<String> failMobileNumbers = Arrays.asList("abcdsds3434", "abcdsds343?#4", "21448410", "9946739087");

        for (int i = 0; i < sucessMobileNumbers.size(); i++) {
            String phone = sucessMobileNumbers.get(i);
            String phoneValid = getE164FormattedMobileNumber(phone, DEFAULT_ISO_COUNTRY);
            MyUtils.log("phone_valid: " + phone + " -> " + phoneValid);

            boolean isValid = isValidNumber(phone, "");
            MyUtils.log("phone_valid: " + phone + " -> " + isValid);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static final String DEFAULT_COUNTRY = Locale.getDefault().getCountry();
    public static final String DEFAULT_ISO_COUNTRY = "VN";

    public static String getDefaultCountryNameCode() {
        String countryCode;
        if (DEFAULT_COUNTRY == null || DEFAULT_COUNTRY.isEmpty()) {
            countryCode = DEFAULT_ISO_COUNTRY;
        } else {
            countryCode = DEFAULT_COUNTRY;
        }
        return countryCode;
    }

    public static void setPhoneForList(ArrayList<UserInfo> list) {
        if (list != null && list.size() > 0) {
            for (UserInfo user : list) {
                setPhoneForUser(user);
            }
        }
    }

    public static void setPhoneForUser(UserInfo user) {
        if(user!=null){
            if (!TextUtils.isEmpty(user.getPhone())) {

                //phone = phone_national
                String phoneNational = getNationalFormattedMobileNumber(user.getPhone(), PhoneUtils.DEFAULT_ISO_COUNTRY);
                if(!TextUtils.isEmpty(phoneNational)){
                    user.setPhoneNational(phoneNational);
                    user.setPhone(phoneNational);
                }

                //E164
                String phoneInternaltionHavePlus = getE164FormattedMobileNumber(user.getPhone(), PhoneUtils.DEFAULT_ISO_COUNTRY);
                if(!TextUtils.isEmpty(phoneInternaltionHavePlus)){
                    user.setPhoneE164(phoneInternaltionHavePlus);
                }
            }
        }
    }
}

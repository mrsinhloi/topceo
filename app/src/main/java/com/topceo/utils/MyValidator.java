package com.topceo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by MrPhuong on 2016-07-28.
 */
public class MyValidator {

    private static Pattern pattern;
    private static Matcher matcher;

    /////////////////////////////////////////////////////////////////////
    //https://regex101.com/
    public static final String USERNAME_PATTERN = "^(?=.{5,30}$)[a-z0-9](?!.*[._]{2})(?:[\\w]*|[a-z\\d\\._]*)[a-z0-9]$";
    /**
     * Validate username with regular expression
     * @param username username for validation
     * @return true valid username, false invalid username
     */
    public static boolean validateUsername(final String username){
        pattern = Pattern.compile(USERNAME_PATTERN);
        matcher = pattern.matcher(username);
        return matcher.matches();
    }
    /////////////////////////////////////////////////////////////////////
}

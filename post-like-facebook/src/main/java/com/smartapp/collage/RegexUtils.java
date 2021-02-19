package com.smartapp.collage;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    public static final String URL_REGEX =
            "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)?"
                    + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                    + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)";
    private static final Pattern urlPattern = Pattern.compile(
            URL_REGEX,
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    /**
     * LẤY THÔNG TIN THEO REGEX
     *
     * @param message
     * @param regex   : EX: Regexs.MONEY
     * @return
     */
    public static List<String> getList(String message, String regex) {
        List<String> allMatches = new ArrayList<String>();
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = pattern.matcher(message);
        while (m.find()) {
            allMatches.add(m.group());
        }
        return allMatches;
    }
}

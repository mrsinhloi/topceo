package com.workchat.core.utils;

import android.text.TextUtils;
import android.util.Log;

import com.workchat.core.config.ChatApplication;

import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import org.springframework.web.util.HtmlUtils;

public class Utils {

    //////////////////////////////////////////////////////////////////////////////////////////////
    public static String textToHtmlConvertingURLsToLinks(String text) {
        if (text == null) {
            return text;
        } else {
            text = text.replace("\\", "/");
        }

//        String escapedText = HtmlUtils.htmlEscape(text);

        return text.replaceAll("(\\A|\\s)((http|https|ftp|mailto):\\S+)(\\s|\\z)", "$1<a href=\"$2\" target=\"_blank\">$2</a>$4");
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    public static final String REGEX_MENTION = "@\\[([\\+\\s\\S\\d _][^\\]]+)\\]\\(userid:([a-z0-9]+)\\)";//20 steps
//    public static final String REGEX_MENTION = "@\\[(.+)\\]\\(userid:(\\w+)\\)";//89 steps


    public static String replaceAtMentionsWithLinks(String text) {
        if (TextUtils.isEmpty(text)) return "";
        String replace = text;
        try {
            replace = text.replaceAll(REGEX_MENTION, "<a class='mention-user' href='javascript:;' data-userid='$2'>@$1</a>");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return replace;
    }

    //Disable click on link <a>
    public static String replaceAtMentionsWithLinksNewOld(String text) {
        if (text == null) return "";
        String replace = text;
        try {
            replace = text.replaceAll(REGEX_MENTION, "<a class='mention-user' href='/' onclick='return false;' data-userid='$2'>@$1</a>");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return replace;
    }


    public static String getMentionUserId(String text) {
        if (text == null) return "";
        String replace = text;
        try {
            replace = text.replaceAll(REGEX_MENTION, "$2");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return replace;
    }


    /**
     * @param text = @[Full Name] => Full Name
     * @return
     */
    public static String getMentionFullName(String text) {
        if (text == null) return "";
        String replace = text;
        try {
            replace = text.replaceAll("@\\[([\\s\\S\\d _][^\\]]+)\\]", "$1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return replace;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    public static String tagsToTwitterURLs(String text) {
        if (text == null) return "";
        return text.replaceAll("(\\s|\\A)#(\\w+)", "$1<a href=\"http://twitter.com/#!/search?q=%23$2\" target=\"_blank\">#$2</a>");
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    public static String mentionedToTwitterURLs(String text) {
        if (text == null) return "";
        return text.replaceAll("(\\s|\\A)@(\\w+)", "$1<a href=\"http://twitter.com/#!/$2\" target=\"_blank\">@$2</a>");
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    public static String setToString(Set<?> elements, String separator) {

        if (elements == null) {
            return null;
        }

        StringBuilder result = new StringBuilder();
        if (!elements.isEmpty()) {

            String[] elementArray = elements.toArray(new String[0]);

            result.append(elementArray[0]);

            for (int i = 1; i < elements.size(); i++) {
                result.append(separator);
                result.append(elementArray[i]);
            }
        }

        return result.toString();
    }

    //14-8-2015////////////////////////////////////////////////////////////////////////////////////////////
    public static final String PATTERN_PLUS = "\\+\\[([\\s\\S\\d -_][^\\]]+)\\]";
    public static final String PATTERN_AT_SIGN = "@\\[([\\s\\S\\d -_][^\\]]+)\\]";
    public static final String PATTERN_EXCLAMATION_MARK = "!\\[([\\s\\S\\d -_][^\\]]+)\\]";
    public static final String PATTERN_NUMBER_SIGN = "#\\[([\\S -_][^\\]]*)\\]";

    //////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @param patternString +:PATTERN_PLUS, @:PATTERN_AT_SIGN, !:PATTERN_EXCLAMATION_MARK, #:PATTERN_NUMBER_SIGN
     * @param text
     * @param group         example:(0:@[Phuong Pham])  (1:Phuong Pham)
     * @return
     */
    public static ArrayList<String> getTextFromPattern(String patternString, String text, int group) {
        ArrayList<String> list = new ArrayList<String>();
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String s = matcher.group(group);
            list.add(s);
            Log.d("DEBUG", "pattern: " + patternString + " : " + s);
        }

        return list;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////
    private static String replaceOwnerByYou(String text) {
        //thay the @owner -> you //@[Hà Phan](userid:2681)
        String ownerMention = ChatApplication.Companion.getOwnerMention();
        if (text.contains(ownerMention)) {
            String mentionYou = ChatApplication.Companion.getReplaceOwnerMention();
            //chi thay the phan name
            text = text.replace(ownerMention, mentionYou);
        }
        return text;
    }

    //@Phuong -> @Ban
    private static String replaceOwnerByYouNotify(String text) {
        //thay the @owner -> @you
        String ownerMention = ChatApplication.Companion.getOwnerMentionNotify();
        if (text.contains(ownerMention)) {
            String mentionYou = ChatApplication.Companion.getReplaceOwnerMentionNotify();
            //chi thay the phan name
            text = text.replace(ownerMention, mentionYou);
        }
        return text;
    }

    public static String replaceAtMentionsWithLinksNew(String text) {
        if (text == null) return "";
        String replace = text;
        try {

            text = replaceOwnerByYou(text);

            //van giu nguyen
            replace = text.replaceAll(REGEX_MENTION, "<a class='mention-user' href='/' onclick='return false;' data-userid='$2'>@$1</a>");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return replace;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //Hien thi tren thanh ntf
    public static String replaceAtMentionsWithLinksNewNotification(String text) {
        if (text == null) return "";
        String replace = text;
        try {
            text = replaceOwnerByYouNotify(text);
            replace = text.replaceAll(REGEX_MENTION, "@$1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return replace;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    public static String getMentionUserName(String text) {
        if (text == null) return "";
        String replace = text;
        try {
            text = replaceOwnerByYou(text);
            replace = text.replaceAll(REGEX_MENTION, "@$1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return replace;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    public static String copyMentionUserName(String text) {
        if (text == null) return "";
        String replace = text;
        try {
            replace = text.replaceAll(REGEX_MENTION, "[$1]");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return replace;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    final static String regex = "<a class='mention-user' href='\\/' onclick='return false;' data-userid='([a-z0-9]+)'>(@.*)<\\/a>";

    public static String getMentionUserIdFromTagA(String text) {
        String userId = "";
        if (!TextUtils.isEmpty(text)) {
            final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
            final Matcher matcher = pattern.matcher(text);

            while (matcher.find()) {
                /*System.out.println("Full match: " + matcher.group(0));
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    System.out.println("Group " + i + ": " + matcher.group(i));
                }*/
                try {
                    String id = matcher.group(1);
                    if(!TextUtils.isEmpty(id)){
                        userId = id;
                    }
                    break;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return userId;
    }
}
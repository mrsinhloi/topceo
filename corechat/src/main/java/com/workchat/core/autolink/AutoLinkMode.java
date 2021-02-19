package com.workchat.core.autolink;

import android.graphics.Color;
import android.util.Patterns;

import androidx.annotation.NonNull;

import com.workchat.core.utils.MyUtils;

import java.util.regex.Pattern;

/**
 * Đầu tiên các bạn cần tạo 1 Object để chứa các định nghĩa về đoạn link mà bạn muốn khởi tạo.
 */
public class AutoLinkMode {
    public static final int DEFAULT_COLOR = Color.BLUE;
    public static final int DEFAULT_COLOR_SELECT = Color.LTGRAY;

    public static final String MODE_URL = "Url";
    static String regexUrl = "((([A-Za-z]{3,9}:(?:\\/\\/)?)(?:[\\-;:&=\\+\\$,\\w]+@)?[A-Za-z0-9\\.\\-]+|(?:www\\.|[\\-;:&=\\+\\$,\\w]+@)[A-Za-z0-9\\.\\-]+)((?:\\/[\\+~%\\/\\.\\w\\-_]*)?\\??(?:[\\-\\+=&;%@\\.\\w_]*)#?(?:[\\.\\!\\/\\\\\\w]*))?)(:\\d+)?\\/?(\\w+)?\\/?";
    public static Pattern patternURL = Pattern.compile(regexUrl, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    public static AutoLinkMode URL = new AutoLinkMode(MODE_URL, patternURL
            , DEFAULT_COLOR, DEFAULT_COLOR_SELECT);

    public static final String MODE_PHONE = "Phone";
    public static AutoLinkMode PHONE =
            new AutoLinkMode(MODE_PHONE, Pattern.compile("\\d{9,}", Pattern.MULTILINE), DEFAULT_COLOR, DEFAULT_COLOR_SELECT);

    public static final String MODE_EMAIL = "Email";
    public static AutoLinkMode EMAIL =
            new AutoLinkMode(MODE_EMAIL, Patterns.EMAIL_ADDRESS, DEFAULT_COLOR, DEFAULT_COLOR_SELECT);

    //final String string = "@[Phạm Trung Phương]";
//    public static final Pattern PATTERN_MENTION = Pattern.compile("@\\[([\\s\\S\\d _][^\\]]+)\\]");
    //    public static final Pattern PATTERN_MENTION = Pattern.compile("<a.*class=.mention-user.*[\\\\n]+.*?<\\/a>");
//    public static final Pattern PATTERN_MENTION = Pattern.compile(Utils.REGEX_MENTION);
    //MENTION
    public static final String MODE_MENTION = "Mention";
    public static final Pattern PATTERN_MENTION = Pattern.compile("<\\s*a[^>]*>(.*?)<\\s*\\/\\s*a>", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    public static AutoLinkMode MENTION =
            new AutoLinkMode(MODE_MENTION, PATTERN_MENTION, DEFAULT_COLOR, DEFAULT_COLOR_SELECT);


    //TASK
    public static final String MODE_TASK = "TASK";
    public static final Pattern PATTERN_TASK = MyUtils.patternTask;
    public static AutoLinkMode TASK =
            new AutoLinkMode(MODE_TASK, PATTERN_TASK, DEFAULT_COLOR, DEFAULT_COLOR_SELECT);

    //PROJECT
    public static final String MODE_PROJECT = "PROJECT";
    public static final Pattern PATTERN_PROJECT = MyUtils.patternProject;
    public static AutoLinkMode PROJECT =
            new AutoLinkMode(MODE_PROJECT, PATTERN_PROJECT, DEFAULT_COLOR, DEFAULT_COLOR_SELECT);



    private String name;

    private Pattern pattern;

    private int color = DEFAULT_COLOR;

    private int colorSelect = DEFAULT_COLOR_SELECT;

    public AutoLinkMode(@NonNull String name, @NonNull Pattern pattern, int color, int colorSelect) {
        this.name = name;
        this.pattern = pattern;
        this.color = color;
        this.colorSelect = colorSelect;
    }

    public AutoLinkMode(String name, Pattern pattern) {
        this.name = name;
        this.pattern = pattern;
        this.color = DEFAULT_COLOR;
        this.colorSelect = DEFAULT_COLOR_SELECT;
    }

    // Write method setter, getter

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColorSelect() {
        return colorSelect;
    }

    public void setColorSelect(int colorSelect) {
        this.colorSelect = colorSelect;
    }
}

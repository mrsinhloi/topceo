package com.workchat.core.autolink;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.text.HtmlCompat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class CustomTextViewLink extends EmojiconTextView {
    private static final int MIN_PHONE_NUMBER_LENGTH = 9;

    private AutoLinkOnClickListener autoLinkOnClickListener;

    private AutoLinkMode[] autoLinkModes;

    private boolean isUnderLineEnabled = false;

    public CustomTextViewLink(Context context) {
        super(context);
    }

    public CustomTextViewLink(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setHighlightColor(int color) {
        super.setHighlightColor(Color.TRANSPARENT);
    }

    public void setAutoLinkText(String text) {
        SpannableString spannableString = makeSpannableString(text);
        setText(spannableString, BufferType.SPANNABLE);
        setMovementMethod(new LinkTouchMovementMethod());
    }

    private SpannableString makeSpannableString(String text) {

        final SpannableString spannableString = new SpannableString(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY));

        List<AutoLinkItem> autoLinkItems = matchedRanges(text);

        for (final AutoLinkItem autoLinkItem : autoLinkItems) {
            int currentColor = autoLinkItem.getAutoLinkMode().getColor();
            int currentColorSelect = autoLinkItem.getAutoLinkMode().getColorSelect();

            TouchableSpan clickableSpan = new TouchableSpan(currentColor, currentColorSelect, isUnderLineEnabled) {
                @Override
                public void onClick(View widget) {
                    if (autoLinkOnClickListener != null) {
                        autoLinkOnClickListener.onAutoLinkTextClick(autoLinkItem.getAutoLinkMode(),
                                autoLinkItem.getMatchedText());
                    }
                }
            };

            try {
                spannableString.setSpan(clickableSpan, autoLinkItem.getStartPoint(), autoLinkItem.getEndPoint(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return spannableString;
    }


    private List<AutoLinkItem> matchedRanges(String text) {

        //List quan ly vi tri nao da add roi thi ko add lai
        List<Integer> listStart = new ArrayList<>();


        List<AutoLinkItem> autoLinkItems = new LinkedList<>();

        if (autoLinkModes == null) {
            throw new NullPointerException("Please add at least one mode");
        }

//        CharSequence html = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY);
        final SpannableString html = new SpannableString(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY));

        for (AutoLinkMode anAutoLinkMode : autoLinkModes) {
            Pattern pattern = anAutoLinkMode.getPattern();


            if(anAutoLinkMode.getName().equalsIgnoreCase(AutoLinkMode.MODE_MENTION)){
                Matcher matcher = pattern.matcher(text);
                while (matcher.find()) {

                    try {
                        //<a class='mention-user' href='/' onclick='return false;' data-userid='139616)'>@Phan Hữu Hà</a>
                        String matchString = matcher.group(0);
                        //Lay string tu chuoi => Phan Hữu Hà
//                        String name = matchString.substring(matchString.indexOf("@")+1, matchString.length()-4);
                        String name = matcher.group(1);//@Phan Hữu Hà, @+0938931234
                        //thay the + -> \+
                        name = name.replace("+", "\\+");

                        //Một user có thể được lặp lại
                        Pattern word = Pattern.compile(name, Pattern.MULTILINE);
                        Matcher match = word.matcher(html);
                        while (match.find()) {
//                            System.out.println("Found love at index "+ match.start() +" - "+ (match.end()-1));
                            int start = match.start();
                            int end = match.end();

                            if(!listStart.contains(start)) {
                                AutoLinkItem item = new AutoLinkItem(start, end, match.group(), anAutoLinkMode);
                                autoLinkItems.add(item);
                                listStart.add(start);
                            }

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }else{
                Matcher matcher = pattern.matcher(html);
                while (matcher.find()) {
//                    autoLinkItems.add(new AutoLinkItem(matcher.start(), matcher.end(), matcher.group(), anAutoLinkMode));
                    int start = matcher.start();
                    int end = matcher.end();

                    if(!listStart.contains(start)) {
                        AutoLinkItem item = new AutoLinkItem(start, end, matcher.group(), anAutoLinkMode);
                        autoLinkItems.add(item);
                        listStart.add(start);
                    }

                }
            }


            /*if (anAutoLinkMode.getName().equalsIgnoreCase(AutoLinkMode.MODE_PHONE)) {
                while (matcher.find()) {
                    String phone = matcher.group().replace("-","");
                    if (phone.length() > MIN_PHONE_NUMBER_LENGTH) {
                        autoLinkItems.add(
                                new AutoLinkItem(matcher.start(), matcher.end(), matcher.group(), anAutoLinkMode));
                    }
                }
            } */
            /*else if (anAutoLinkMode.getName().equalsIgnoreCase(AutoLinkMode.MODE_URL)) {
                while (matcher.find()) {
                    autoLinkItems.add(
                            new AutoLinkItem(matcher.start(), matcher.end(), matcher.group(), anAutoLinkMode));
                }
            } else if (anAutoLinkMode.getName().equalsIgnoreCase(AutoLinkMode.MODE_EMAIL)) {
                while (matcher.find()) {
                    autoLinkItems.add(
                            new AutoLinkItem(matcher.start(), matcher.end(), matcher.group(), anAutoLinkMode));
                }
            } else if (anAutoLinkMode.getName().equalsIgnoreCase(AutoLinkMode.MODE_MENTION)) {
                while (matcher.find()) {
                    autoLinkItems.add(
                            new AutoLinkItem(matcher.start(), matcher.end(), matcher.group(), anAutoLinkMode));
                }
            } else if (anAutoLinkMode.getName().equalsIgnoreCase(AutoLinkMode.MODE_TASK)) {
                while (matcher.find()) {
                    autoLinkItems.add(
                            new AutoLinkItem(matcher.start(), matcher.end(), matcher.group(), anAutoLinkMode));
                }
            } else if (anAutoLinkMode.getName().equalsIgnoreCase(AutoLinkMode.MODE_PROJECT)) {
                while (matcher.find()) {
                    autoLinkItems.add(
                            new AutoLinkItem(matcher.start(), matcher.end(), matcher.group(), anAutoLinkMode));
                }
            } */

            /*else {
                ArrayList<Integer> listStart = new ArrayList<>();
                while (matcher.find()) {

                    try {
                        //<a class='mention-user' href='/' onclick='return false;' data-userid='139616)'>@Phan Hữu Hà</a>
                        String matchString = matcher.group(0);
                        //Lay string tu chuoi => Phan Hữu Hà
//                        String name = matchString.substring(matchString.indexOf("@")+1, matchString.length()-4);
                        String name = matcher.group(1);//@Phan Hữu Hà, @+0938931234
                        //thay the + -> \+
                        name = name.replace("+", "\\+");

                        //Một user có thể được lặp lại
                        Pattern word = Pattern.compile(name, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
                        Matcher match = word.matcher(html);
                        while (match.find()) {
//                            System.out.println("Found love at index "+ match.start() +" - "+ (match.end()-1));
                            int start = match.start();
                            int end = match.end();

                            if (!listStart.contains(start)) {
                                listStart.add(start);
                                AutoLinkItem item = new AutoLinkItem(start, end, matcher.group(), anAutoLinkMode);
                                autoLinkItems.add(item);
                            }

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }*/


            /*//neu co phone
            pattern = Patterns.PHONE;
            matcher = pattern.matcher(text);
            while (matcher.find()) {
                if (matcher.group().length() > MIN_PHONE_NUMBER_LENGTH) {
                    autoLinkItems.add(
                            new AutoLinkItem(matcher.start(), matcher.end(), matcher.group(), anAutoLinkMode));
                }
            }

            //neu co url
            pattern = Patterns.WEB_URL;
            matcher = pattern.matcher(text);
            while (matcher.find()) {
                autoLinkItems.add(
                        new AutoLinkItem(matcher.start(), matcher.end(), matcher.group(), anAutoLinkMode));
            }

            //neu co email
            pattern = Patterns.EMAIL_ADDRESS;
            matcher = pattern.matcher(text);
            while (matcher.find()) {
                autoLinkItems.add(
                        new AutoLinkItem(matcher.start(), matcher.end(), matcher.group(), anAutoLinkMode));
            }

            //neu co task
            pattern = AutoLinkMode.PATTERN_TASK;
            matcher = pattern.matcher(text);
            while (matcher.find()) {
                autoLinkItems.add(
                        new AutoLinkItem(matcher.start(), matcher.end(), matcher.group(), anAutoLinkMode));
            }

            //neu co project
            pattern = AutoLinkMode.PATTERN_PROJECT;
            matcher = pattern.matcher(text);
            while (matcher.find()) {
                autoLinkItems.add(
                        new AutoLinkItem(matcher.start(), matcher.end(), matcher.group(), anAutoLinkMode));
            }*/


        }

        return autoLinkItems;
    }

    public void addAutoLinkMode(AutoLinkMode... autoLinkModes) {
        this.autoLinkModes = autoLinkModes;
    }

    public void enableUnderLine() {
        isUnderLineEnabled = true;
    }

    public void disableUnderLine() {
        isUnderLineEnabled = false;
    }

    public void setAutoLinkOnClickListener(AutoLinkOnClickListener autoLinkOnClickListener) {
        this.autoLinkOnClickListener = autoLinkOnClickListener;
    }

    public interface AutoLinkOnClickListener {
        void onAutoLinkTextClick(AutoLinkMode autoLinkMode, String matchedText);
    }
}

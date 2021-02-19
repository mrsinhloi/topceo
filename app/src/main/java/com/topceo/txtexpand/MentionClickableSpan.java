package com.topceo.txtexpand;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;

import com.topceo.config.MyApplication;
import com.topceo.utils.MyUtils;

public class MentionClickableSpan extends ClickableSpan {
    String text;
    Context context;

    public MentionClickableSpan(String text, Context context){
        super();
        this.text = text;
        this.context = context;
    }
    @Override
    public void onClick(@NonNull View widget) {
        //text, id
//        Log.d("TAG", "onClick [" + text + "]");
        MyUtils.gotoProfile(text, context);
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(MyApplication.colorMention);
        ds.setUnderlineText(false);
    }
}

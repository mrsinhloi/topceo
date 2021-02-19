package com.topceo.txtexpand;

import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;

import com.topceo.utils.MyUtils;

public class UrlClickableSpan extends ClickableSpan {
    String text;
    Context context;

    public UrlClickableSpan(String text,Context context){
        super();
        this.text = text;
        this.context = context;
    }
    @Override
    public void onClick(@NonNull View widget) {
        //text, id
//        Log.d("TAG", "onClick [" + text + "]");
        MyUtils.openWebPage(text, context);
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(Color.BLUE);
        ds.setUnderlineText(false);
    }
}
